package net.arcadiusmc.delphiplugin.resource;

import static net.arcadiusmc.delphi.resource.DelphiException.ERR_EMPTY_MODULE_NAME;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_IO_ERROR;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_MODULE_DIRECTORY_NOT_FOUND;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_MODULE_UNKNOWN;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_MODULE_ZIP_ACCESS_DENIED;

import com.google.common.base.Strings;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import net.arcadiusmc.delphi.resource.DelphiException;
import net.arcadiusmc.delphi.resource.DelphiResources;
import net.arcadiusmc.delphi.resource.DirectoryModule;
import net.arcadiusmc.delphi.resource.ResourceModule;
import net.arcadiusmc.delphi.resource.ResourcePath;
import net.arcadiusmc.delphi.resource.ZipModule;
import net.arcadiusmc.delphi.util.Result;
import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.delphidom.parser.ErrorListener;
import net.arcadiusmc.delphidom.scss.Rule;
import net.arcadiusmc.delphidom.scss.ScssParser;
import net.arcadiusmc.delphidom.scss.Sheet;
import net.arcadiusmc.dom.ParserException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class Modules implements DelphiResources {

  static final String DEFAULT_STYLE = "default-style.scss";

  private static final Logger LOGGER = Loggers.getLogger("DelphiResources");

  static final String ZIP_EXT = ".zip";

  private final Map<String, RegisteredModule> registered = new HashMap<>();
  private final Map<Path, FileSystemModule> cachedFileModules = new HashMap<>();

  private final Path directory;
  private FileSystemProvider zipProvider;

  @Getter
  private Sheet defaultStyle;

  public Modules(Path directory) {
    this.directory = directory;
    ensureDirectoryExists();
  }

  private void ensureDirectoryExists() {
    if (Files.isDirectory(directory)) {
      return;
    }

    try {
      Files.createDirectories(directory);
    } catch (IOException exc) {
      LOGGER.error("Failed to create modules directory", exc);
    }
  }

  public void loadDefaultStyle() {
    URL url = getClass().getClassLoader().getResource(DEFAULT_STYLE);

    if (url == null) {
      LOGGER.error("Failed to load {}, resource not found", DEFAULT_STYLE);
      return;
    }

    StringBuffer buf;

    try (InputStream stream = url.openStream()) {
      StringWriter writer = new StringWriter();
      InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
      reader.transferTo(writer);
      buf = writer.getBuffer();
    } catch (IOException exc) {
      LOGGER.error("Error loading {}: IO Error", DEFAULT_STYLE, exc);
      defaultStyle = null;
      return;
    }

    ScssParser parser = new ScssParser(buf);
    parser.getErrors().setListener(ErrorListener.logging(LOGGER));

    Sheet sheet;

    try {
      sheet = parser.stylesheet();
    } catch (ParserException exc) {
      LOGGER.error("Error parsing {}", DEFAULT_STYLE, exc);
      defaultStyle = null;
      return;
    }

    defaultStyle = sheet;
    defaultStyle.addFlag(Sheet.FLAG_DEFAULT);

    for (int i = 0; i < defaultStyle.getLength(); i++) {
      Rule rule = defaultStyle.getRule(i);
      rule.getSelectorObj().getSpec().set(0);
    }
  }

  @Override
  public Path getModuleDirectory() {
    return directory;
  }

  @Override
  public boolean registerModule(@NotNull String moduleName, @NotNull ResourceModule module) {
    Objects.requireNonNull(moduleName, "Null module name");
    Objects.requireNonNull(module, "Null module");

    ResourcePath.validateQuery(moduleName);

    String key = moduleName.toLowerCase();

    if (registered.containsKey(key)) {
      return false;
    }

    RegisteredModule registeredModule = new RegisteredModule(moduleName, module);
    registered.put(key, registeredModule);

    return true;
  }

  @Override
  public Result<ResourceModule, DelphiException> findModule(String moduleName) {
    if (Strings.isNullOrEmpty(moduleName)) {
      return Result.err(new DelphiException(ERR_EMPTY_MODULE_NAME, "Empty/null module name"));
    }

    String lower = moduleName.toLowerCase();
    RegisteredModule found = registered.get(lower);

    if (found != null) {
      return Result.ok(found.module);
    }

    if (!Files.isDirectory(directory)) {
      return Result.err(
          new DelphiException(ERR_MODULE_DIRECTORY_NOT_FOUND, "Module directory doesn't exist")
      );
    }

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
      for (Path path : stream) {
        String fname = path.getFileName().toString();

        if (Files.isDirectory(path)) {
          if (fname.equalsIgnoreCase(moduleName)) {
            return Result.ok(createDirectoryModule(path));
          }

          continue;
        }

        if (fname.endsWith(ZIP_EXT)) {
          String extRemoved = fname.substring(0, fname.length() - ZIP_EXT.length());

          if (!extRemoved.equalsIgnoreCase(moduleName)) {
            continue;
          }

          try {
            return Result.ok(getZipModule(path));
          } catch (AccessDeniedException acc) {
            return Result.err(
                new DelphiException(
                    ERR_MODULE_ZIP_ACCESS_DENIED,
                    "Zip access denied: " + acc.getReason(),
                    acc
                )
            );
          } catch (IOException exc) {
            return Result.err(new DelphiException(ERR_IO_ERROR, exc));
          }
        }
      }
    } catch (IOException e) {
      LOGGER.error("Error attempting to iterate module directory", e);
    }

    return Result.err(new DelphiException(ERR_MODULE_UNKNOWN, moduleName));
  }

  ZipModule getZipModule(Path path) throws IOException {
    FileSystemModule cached = cachedFileModules.get(path);
    if (cached instanceof ZipModuleImpl zip) {
      return zip;
    }

    FileSystemProvider provider = getZipProvider();
    FileSystem system = provider.newFileSystem(path, Map.of());

    ZipModuleImpl module = new ZipModuleImpl(system.getPath(""), system, path);
    cachedFileModules.put(path, module);

    return module;
  }

  public DirectoryModule createDirectoryModule(@NotNull Path path) {
    Objects.requireNonNull(path, "Null directory path");

    return (DirectoryModule) cachedFileModules.computeIfAbsent(
        path,
        p -> new DirectoryModuleImpl(p, p.getFileSystem())
    );
  }

  @Override
  public Result<ZipModule, DelphiException> createZipModule(@NotNull Path zipPath) {
    Objects.requireNonNull(zipPath, "Null zip path");

    try {
      ZipModule mod = getZipModule(zipPath);
      return Result.ok(mod);
    } catch (IOException exc) {
      return Result.ioError(exc);
    }
  }

  private FileSystemProvider getZipProvider() {
    if (zipProvider != null) {
      return zipProvider;
    }

    return zipProvider = FileSystemProvider.installedProviders().stream()
        .filter(provider -> provider.getScheme().equals("jar")) // JAR ???????
        .findFirst()
        .orElse(null);
  }

  @Override
  public List<String> getModuleNames() {
    List<String> result = new ArrayList<>();

    for (RegisteredModule value : registered.values()) {
      result.add(value.name);
    }

    if (!Files.isDirectory(directory)) {
      return result;
    }

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
      for (Path path : stream) {
        String fname = path.getFileName().toString();

        if (Files.isDirectory(path)) {
          result.add(fname);
        } else if (fname.endsWith(ZIP_EXT)) {
          String extRemoved = fname.substring(0, fname.length() - ZIP_EXT.length());
          result.add(extRemoved);
        }
      }
    } catch (IOException e) {
      LOGGER.error("Error attempting to iterate module directory");
    }

    return result;
  }

  record RegisteredModule(String name, ResourceModule module) {

  }
}

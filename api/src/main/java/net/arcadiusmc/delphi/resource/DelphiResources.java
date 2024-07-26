package net.arcadiusmc.delphi.resource;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public interface DelphiResources {

  /**
   * Gets the module directory.
   * <p>
   * The returned path is the {@code modules} directory inside the Delphi plugin's data folder.
   *
   * @return Module directory
   */
  Path getModuleDirectory();

  /**
   * Registers a module.
   * <p>
   * This method will return {@code false} if another module has already been registered
   * with the specified {@code moduleName}.
   *
   * @param moduleName Name of the module.
   * @param module Module.
   *
   * @return {@code true}, if the module was registered,
   *         {@code false}, if the specified name is already in use.
   *
   * @throws NullPointerException If either {@code moduleName} or {@code module} is {@code null}.
   * @throws IllegalArgumentException If the module name doesn't pass the {@link PagePath#validateQuery(String)} check.
   */
  boolean registerModule(@NotNull String moduleName, @NotNull ResourceModule module);

  /**
   * Attempts to locate a module.
   * <p>
   * This will first attempt to find a module registered with {@link #registerModule(String, ResourceModule)}.
   * No registered module is found, it will attempt to find a directory or a zip archive with the
   * specified name.
   * <p>
   * This method assumes the input has no file extension.
   *
   * @param moduleName Name of the module
   * @return Found module, or an {@code empty} optional if no module was found.
   */
  Optional<ResourceModule> findModule(String moduleName);

  /**
   * Gets an array list of all module names. This list inclues all registered modules
   * as well as all directory and zip archive modules installed in {@link #getModuleDirectory()}
   *
   * @return Module name list.
   */
  List<String> getModuleNames();
}

package net.arcadiusmc.delphi.resource;

import com.google.common.base.Strings;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;

public class DelphiException extends RuntimeException {

  /** Unknown error */
  public static final int ERR_UNKNOWN = 0;

  /**
   * No such file error, error message will be the file's name.
   * <p>
   * {@link #getCause()} will return the thrown {@link NoSuchFileException}
   */
  public static final int ERR_NO_FILE = 1;

  /**
   * Access to file denied.
   * <p>
   * {@link #getCause()} will return the thrown {@link AccessDeniedException}
   */
  public static final int ERR_ACCESS_DENIED = 2;

  /**
   * IO Exception
   * <p>
   * {@link #getCause()} will return the thrown {@link IOException}
   */
  public static final int ERR_IO_ERROR = 3;

  /**
   * Failed to parse {@link ResourcePath}
   */
  public static final int ERR_INVALID_PATH = 4;

  /**
   * Request resource is not available on {@link ApiModule} modules.
   */
  public static final int ERR_API_MODULE = 5;

  /**
   * Input string/file content was invalid
   */
  public static final int ERR_SYNTAX = 6;

  /**
   * Data did not match a schema.
   * <p>
   * Used by {@link ViewResources#loadItemStack(String)} if the JSON it attempted to
   * load data from did not match minecraft's item schema.
   * <p>
   * Has no {@link #getCause()}, instead all detail about the error is in the
   * {@link #getBaseMessage()}
   */
  public static final int ERR_SCHEMA_ERROR = 7;

  /**
   * XML Parser failed to initialize
   */
  public static final int ERR_SAX_PARSER_INIT = 8;

  /**
   * Syntax error or other kind of error occurred when attempting to parse an XML document.
   */
  public static final int ERR_DOC_PARSE = 9;

  /**
   * Failed to find enabled required plugins during document loading
   */
  public static final int ERR_MISSING_PLUGINS = 10;

  /**
   * Failed to find a module, {@link #getBaseMessage()} will return the name of the module
   * it failed to find.
   */
  public static final int ERR_MODULE_UNKNOWN = 11;

  /**
   * The module directory does not exist
   */
  public static final int ERR_MODULE_DIRECTORY_NOT_FOUND = 12;

  /**
   * Failed to read module {@code .zip} file
   */
  public static final int ERR_MODULE_ZIP_ACCESS_DENIED = 13;

  /**
   * Resource module threw an error while accessing resource
   */
  public static final int ERR_MODULE_ERROR = 14;

  /**
   * Attempted to find module with a null/blank name.
   */
  public static final int ERR_EMPTY_MODULE_NAME = 15;

  private final int code;

  public DelphiException(int code) {
    this.code = code;
  }

  public DelphiException(int code, String message) {
    super(message);
    this.code = code;
  }

  public DelphiException(int code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }

  public DelphiException(int code, Throwable cause) {
    super(cause);
    this.code = code;
  }

  public int getCode() {
    return code;
  }

  public String getCodeString() {
    return codeToString(code);
  }


  public String getBaseMessage() {
    return super.getMessage();
  }

  @Override
  public String getMessage() {
    String base = super.getMessage();

    if (Strings.isNullOrEmpty(base)) {
      return String.format("Error %s (%s)", code, codeToString(code));
    }

    return String.format("Error %s (%s): %s", code, codeToString(code), base);
  }

  public static String codeToString(int code) {
    return switch (code) {
      case ERR_NO_FILE -> "NO_SUCH_FILE";
      case ERR_ACCESS_DENIED -> "ACCESS_DENIED";
      case ERR_IO_ERROR -> "IO_ERROR";
      case ERR_INVALID_PATH -> "INVALID_PATH";
      case ERR_API_MODULE -> "API_MODULE";
      case ERR_SYNTAX -> "SYNTAX";
      case ERR_SCHEMA_ERROR -> "SCHEMA_ERROR";
      case ERR_SAX_PARSER_INIT -> "SAX_PARSER_INIT_FAILED";
      case ERR_DOC_PARSE -> "DOC_PARSE";
      case ERR_MISSING_PLUGINS -> "MISSING_PLUGINS";
      case ERR_MODULE_UNKNOWN -> "MODULE_UNKNOWN";
      case ERR_MODULE_DIRECTORY_NOT_FOUND -> "MODULE_DIRECTORY_NOT_FOUND";
      case ERR_MODULE_ZIP_ACCESS_DENIED -> "MODULE_ZIP_ACCESS_DENIED";
      case ERR_MODULE_ERROR -> "MODULE_ERROR";
      case ERR_EMPTY_MODULE_NAME -> "EMPTY_MODULE_NAME";
      default -> "ERR_UNKNOWN";
    };
  }
}

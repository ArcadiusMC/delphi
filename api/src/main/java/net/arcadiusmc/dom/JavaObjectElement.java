package net.arcadiusmc.dom;

/**
 * The Java Object element in the header allows for java classes to be
 * triggered from the DOM by specifying their class name.
 *
 * <h2>Activation</h2>
 * <p>
 *   When the {@link Attributes#CLASS_NAME} attribute is set, a lookup is
 *   made via {@link Class#forName(String, boolean, ClassLoader)} to find
 *   the class.
 * </p>
 * <p>
 *   If found, then a {@code public static void onDomInitialize(Document)}
 *   method in the class is called. If the method cannot be found, then a
 *   public constructor with a single {@link Document} parameter will be
 *   called. If that doesn't exist, activation fails.
 * </p>
 */
public interface JavaObjectElement extends Element {

  String INIT_METHOD_NAME = "onDomInitialize";

  /**
   * Get the name of the linked class.
   * <p>
   * If the class name is set, and the class it was linked to was found,
   * then it can be accessed with {@link #getJavaClass()}.
   * <p>
   * Shortcut for accessing the {@link Attributes#CLASS_NAME} attribute.
   *
   * @return Java Class name
   */
  String getClassName();

  /**
   * Get the class named by {@link #getClassName()}.
   * @return Java class
   */
  Class<?> getJavaClass();

  /**
   * Get whether the {@link #getJavaClass()}'s entry point was called. If
   * this method returns {@code false}, then it means the entrypoint couldn't
   * be found or threw an error when called.
   *
   * @return {@code true}, if the class entry point was called successfully,
   *         {@code false} otherwise.
   */
  boolean wasEntrypointCalled();

  /**
   * Set the name of the linked class.
   * <p>
   * Shortcut for changing the {@link Attributes#CLASS_NAME} attribute.
   *
   * @param className New class name
   */
  void setClassName(String className);
}

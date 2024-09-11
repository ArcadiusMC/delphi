package net.arcadiusmc.delphi;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * The plane a {@link DocumentView} exists on.
 */
public interface Screen {

  int DEFAULT_WIDTH = 3;
  int DEFAULT_HEIGHT = 2;

  /**
   * Gets the width of the screen
   * @return Screen width
   */
  float getWidth();

  /**
   * Gets the height of the screen
   * @return Screen height
   */
  float getHeight();

  /**
   * Gets the normal of the screen's plane
   * @return Screen normal
   */
  Vector3f normal();

  /**
   * Gets the center point of the screen
   * @return Screen center
   */
  Vector3f center();

  /**
   * Gets the width and height of the screen
   * @return Screen dimensions, (width, height)
   */
  Vector2f getDimensions();

  /**
   * Gets the lower left corner of the screen from the viewer's perspective
   * @return Lower left corner
   */
  Vector3f getLowerLeft();

  /**
   * Gets the lower right corner of the screen from the viewer's perspective
   * @return Lower right corner
   */
  Vector3f getLowerRight();

  /**
   * Gets the upper left corner of the screen from the viewer's perspective
   * @return Upper left corner
   */
  Vector3f getUpperLeft();

  /**
   * Gets the upper right corner of the screen from the viewer's perspective
   * @return Upper right corner
   */
  Vector3f getUpperRight();

  /**
   * Maps screen coordinates in range [0..{@link #getDimensions()}] to world coordinates.
   *
   * @param screenPoint Screen point, in space [0..{@link #getDimensions()}]
   * @param out Result destination
   */
  void screenToWorld(Vector2f screenPoint, Vector3f out);

  /**
   * Maps screen coordinates in range [0..{@link #getDimensions()}] to [0..1] space.
   *
   * @param in Input in range [0..{@link #getDimensions()}]
   * @param out Result output, in range [0..1]
   */
  void screenToScreenspace(Vector2f in, Vector2f out);

  /**
   * Maps screen coordinates in range [0..1] to [0..{@link #getDimensions()}] space.
   *
   * @param in Input in range [0..1]
   * @param out Result output, in range [0..{@link #getDimensions()}]
   */
  void screenspaceToScreen(Vector2f in, Vector2f out);

  /**
   * Maps screen coordinates in range [0..1] to world coordinates.
   *
   * @param screenPoint Screen point, in space [0..1]
   * @param out Result destination
   */
  void screenspaceToWorld(Vector2f screenPoint, Vector3f out);
}

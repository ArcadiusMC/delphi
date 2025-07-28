package com.juliewoolie.delphidom;

import com.juliewoolie.delphi.DocumentView;
import com.juliewoolie.dom.RenderBounds;
import org.bukkit.entity.Entity;
import org.joml.Vector2f;
import org.joml.Vector3f;

public interface ExtendedView extends DocumentView {

  void contentChanged(DelphiNode text);

  void removeRenderElement(DelphiElement element);

  void tooltipChanged(DelphiElement element, DelphiNode old, DelphiNode titleNode);

  Vector2f getCursorScreen();

  Vector3f getCursorWorld();

  void handleEntityVisibility(Entity entity);

  RenderBounds renderBounds(DelphiNode delphiNode);

  void canvasSizeChanged(DelphiCanvasElement element);
}

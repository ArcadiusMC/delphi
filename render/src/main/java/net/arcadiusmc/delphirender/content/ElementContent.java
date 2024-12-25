package net.arcadiusmc.delphirender.content;

import net.arcadiusmc.delphirender.FullStyle;
import net.arcadiusmc.delphirender.Layer;
import net.arcadiusmc.delphirender.dom.RenderObject;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.joml.Vector2f;

public interface ElementContent {

  Display createEntity(World world, Location location);

  void applyContentTo(Display entity, FullStyle set);

  Class<? extends Display> getEntityClass();

  void measureContent(Vector2f out, FullStyle set);

  boolean isEmpty();

  void configureInitial(Layer layer, RenderObject element);
}

package net.arcadiusmc.delphiplugin.render;

import net.kyori.adventure.text.format.Style;

public interface FontMeasureCallback {

  boolean measureNextChar(String text, Style style, int start, FontMeasureOutput output);
}

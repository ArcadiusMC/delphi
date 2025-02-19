package net.arcadiusmc.delphidom;

import net.arcadiusmc.dom.Attributes;
import net.arcadiusmc.dom.OptionElement;
import net.arcadiusmc.dom.TagNames;
import org.jetbrains.annotations.Nullable;

public class DelphiOptionElement extends DelphiElement implements OptionElement {

  public boolean suppressingUpdates = false;

  public DelphiOptionElement(DelphiDocument document) {
    super(document, TagNames.OPTION);
  }

  @Override
  public boolean canHaveChildren() {
    return false;
  }

  @Override
  public String getName() {
    return getAttribute(Attributes.NAME);
  }

  @Override
  public void setName(@Nullable String key) {
    setAttribute(Attributes.NAME, key);
  }

  @Override
  public String getValue() {
    return getAttribute(Attributes.VALUE);
  }

  @Override
  public void setValue(String value) {
    setAttribute(Attributes.VALUE, value);
  }
}

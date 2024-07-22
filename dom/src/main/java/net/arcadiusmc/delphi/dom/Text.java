package net.arcadiusmc.delphi.dom;

import lombok.Getter;
import net.arcadiusmc.dom.TextNode;
import org.jetbrains.annotations.Nullable;

public class Text extends DelphiNode implements TextNode {

  @Getter
  private String textContent;

  public Text(DelphiDocument document) {
    super(document);
  }

  @Override
  public void setTextContent(@Nullable String textContent) {
    this.textContent = textContent;
    owningDocument.textChanged(this, textContent);
  }
}

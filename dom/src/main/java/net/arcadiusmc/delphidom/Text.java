package net.arcadiusmc.delphidom;

import lombok.Getter;
import net.arcadiusmc.dom.NodeFlag;
import net.arcadiusmc.dom.TextNode;
import net.arcadiusmc.dom.Visitor;
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

    if (hasFlag(NodeFlag.ADDED) && document.getView() != null) {
      document.getView().contentChanged(this);
    }
  }

  @Override
  public String toString() {
    return "#text";
  }

  @Override
  public void enterVisitor(Visitor visitor) {
    visitor.enterText(this);
  }

  @Override
  public void exitVisitor(Visitor visitor) {
    visitor.exitText(this);
  }
}

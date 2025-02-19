package net.arcadiusmc.delphidom.scss;

import static net.arcadiusmc.delphidom.TestUtil.createDoc;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.arcadiusmc.chimera.ChimeraStylesheet;
import net.arcadiusmc.chimera.Rule;
import net.arcadiusmc.chimera.system.StyleNode;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.dom.Attributes;
import net.arcadiusmc.dom.style.StyleProperties;
import net.arcadiusmc.dom.style.StylePropertiesReadonly;
import net.arcadiusmc.dom.style.Stylesheet;
import org.junit.jupiter.api.Test;

class DocumentStylesTest {

  @Test
  void testInline() {
    DelphiDocument doc = createDoc();
    DelphiElement body = doc.getBody();

    body.setAttribute(Attributes.STYLE, "padding-left: 4px;");
    StylePropertiesReadonly map = body.getCurrentStyle();
    StyleProperties inline = body.getInlineStyle();

    assertEquals("4px", map.getPaddingLeft());
    assertEquals("4px", inline.getPaddingLeft());
  }

  @Test
  void testInlineSyntaxError() {
    DelphiDocument doc = createDoc();
    DelphiElement body = doc.getBody();

    assertDoesNotThrow(() -> {
      body.setAttribute(Attributes.STYLE, "paddi: 4px;");
    });

    assertDoesNotThrow(() -> {
      body.setAttribute(Attributes.STYLE, "padding: $non-existent-variable;");
    });
  }

  @Test
  void testSheetBuilder() {
    DelphiDocument doc = createDoc();
    String v = "4px";

    Stylesheet sheet = doc.createStylesheet()
        .addRule(".test", prop -> prop.setPaddingLeft(v))
        .build();

    assertEquals(sheet.getLength(), 1);
    assertEquals(v, sheet.getRule(0).getProperties().getPaddingLeft());
    assertEquals(1, doc.getStylesheets().size());

    Stylesheet gotten = doc.getStylesheets().get(0);
    assertEquals(sheet, gotten);
    assertEquals(1, gotten.getLength());
    assertEquals(v, gotten.getRule(0).getProperties().getPaddingLeft());
  }

  @Test
  void testStylesheet() {
    DelphiDocument doc = createDoc();
    DelphiElement body = doc.getBody();
    body.setClassName("test");

    assertEquals("test", body.getAttribute(Attributes.CLASS));

    StylePropertiesReadonly map = body.getCurrentStyle();
    StyleProperties inline = body.getInlineStyle();

    String v = "4px";

    ChimeraStylesheet sheet = doc.createStylesheet()
        .addRule(".test", prop -> prop.setPaddingLeft(v))
        .build();

    assertEquals(v, sheet.getRule(0).getProperties().getPaddingLeft());

    Rule r = sheet.getRule(0);
    assertEquals(v, r.getProperties().getPaddingLeft());
    assertTrue(r.getSelectorObject().test(body));

    StyleNode styleNode = doc.getStyles().getStyleNode(body);

    assertTrue(body.matches(".test"), "Body doesn't match .test selector");

    assertEquals(v, map.getPaddingLeft());
    assertNull(inline.getPaddingLeft());
  }
}
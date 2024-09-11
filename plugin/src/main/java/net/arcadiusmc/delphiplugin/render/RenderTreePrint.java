package net.arcadiusmc.delphiplugin.render;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.arcadiusmc.chimera.ChimeraStylesheet;
import net.arcadiusmc.chimera.PropertySet.PropertyIterator;
import net.arcadiusmc.chimera.Rule;
import net.arcadiusmc.chimera.Value;
import net.arcadiusmc.chimera.system.StyleObjectModel;
import net.arcadiusmc.delphi.resource.ApiModule;
import net.arcadiusmc.delphi.resource.DirectoryModule;
import net.arcadiusmc.delphi.resource.IoModule;
import net.arcadiusmc.delphi.resource.JarResourceModule;
import net.arcadiusmc.delphi.resource.ResourceModule;
import net.arcadiusmc.delphi.resource.ZipModule;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.delphidom.DelphiNode;
import net.arcadiusmc.delphidom.Rect;
import net.arcadiusmc.delphidom.XmlPrintVisitor;
import net.arcadiusmc.delphidom.event.EventListenerList;
import net.arcadiusmc.delphiplugin.PageView;
import net.arcadiusmc.delphiplugin.math.Screen;
import net.arcadiusmc.dom.ComponentNode;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.TextNode;
import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.style.StyleProperties;
import net.arcadiusmc.dom.style.StylePropertiesReadonly;
import org.joml.Vector2f;

public class RenderTreePrint extends XmlPrintVisitor {

  static final String COMMENT_START = "<!--";
  static final String COMMENT_END = "-->";

  private final PageView view;

  public RenderTreePrint(PageView view) {
    this.view = view;
  }

  public void appendHeader() {
    DelphiDocument doc = view.getDocument();
    StyleObjectModel styles = doc.getStyles();

    nlIndent().append("<header>");
    indent++;

    for (String optionKey : doc.getOptionKeys()) {
      nlIndent().append("<option name=")
          .append('"')
          .append(optionKey)
          .append('"')
          .append(" value=")
          .append('"')
          .append(doc.getOption(optionKey))
          .append('"')
          .append(" />");
    }

    for (ChimeraStylesheet stylesheet : styles.getSheets()) {
      if ((stylesheet.getFlags() & ChimeraStylesheet.FLAG_DEFAULT_STYLE) != 0) {
        continue;
      }

      nlIndent().append("<style>");
      indent++;

      for (int i = 0; i < stylesheet.getLength(); i++) {
        Rule rule = stylesheet.getRule(i);

        nlIndent().append(rule.getSelector()).append(" {");
        indent++;

        PropertyIterator it = rule.getPropertySet().iterator();
        while (it.hasNext()) {
          it.next();

          nlIndent().append(it.property().getKey()).append(": ");

          Value<Object> val = it.value();
          builder.append(val.getTextValue());
        }

        indent--;
        nlIndent().append("}");
      }

      indent--;
      nlIndent().append("</style>");
    }

    nlIndent().append(COMMENT_START);
    indent++;

    nlIndent().append("player-name: ").append(view.getPlayer().getName());
    nlIndent().append("world: ").append(view.getWorld().getName());
    nlIndent().append("render-object-count: ").append(view.getRenderObjects().size());
    nlIndent().append("module-name: ").append(view.getResources().getModuleName());

    ResourceModule module = view.getResources().getModule();
    String moduleType = switch (module) {
      case ApiModule apiModule -> "api-module";
      case ZipModule zip -> "zip(" + zip.getZipFile() + ")";
      case DirectoryModule dir -> "directory(" + dir.getDirectory() + ")";
      case JarResourceModule jarRes -> "jar-resource";
      case IoModule io -> "io-module";
      case null -> "unknown";
    };

    nlIndent().append("module-type: ").append(moduleType);
    nlIndent().append("resource-path: ").append(view.getPath());

    Screen screen = view.getScreen();
    nlIndent().append("screen:");
    indent++;
    screen.appendInfo(builder, indent);
    indent--;

    appendListeners("document-", doc.getDocumentListeners());
    appendListeners("global-", doc.getGlobalTarget());

    indent--;
    nlIndent().append(COMMENT_END);

    indent--;
    nlIndent().append("</header>");
  }

  private void appendFullStyle(FullStyle style)  {
    nlIndent().append("padding:");
    appendRect(style.padding);

    nlIndent().append("border: ");
    appendRect(style.border);

    nlIndent().append("outline: ");
    appendRect(style.outline);

    nlIndent().append("margin: ");
    appendRect(style.margin);

    nlIndent().append("text-color: ").append(style.textColor);
    nlIndent().append("background-color: ").append(style.backgroundColor);
    nlIndent().append("border-color: ").append(style.borderColor);
    nlIndent().append("outline-color: ").append(style.outlineColor);

    nlIndent().append("text-shadowed: ").append(style.textShadowed);
    nlIndent().append("bold: ").append(style.bold);
    nlIndent().append("italic: ").append(style.italic);
    nlIndent().append("underlined: ").append(style.underlined);
    nlIndent().append("strikethrough: ").append(style.strikethrough);
    nlIndent().append("obfuscated: ").append(style.obfuscated);

    nlIndent().append("display: DisplayType.").append(style.display);

    nlIndent().append("scale: ").append(style.scale);
    nlIndent().append("set-size: ").append(style.setSize);
    nlIndent().append("min-size: ").append(style.minSize);
    nlIndent().append("max-size: ").append(style.maxSize);

    nlIndent().append("z-index: ").append(style.zindex);
    nlIndent().append("align-items: ").append(style.alignItems);
    nlIndent().append("flex-direction: ").append(style.flexDirection);
    nlIndent().append("flex-wrap: ").append(style.flexWrap);
    nlIndent().append("justify-content: ").append(style.justify);
    nlIndent().append("order: ").append(style.order);
  }

  private void appendRect(Rect rect) {
    builder.append(rect.toString());
  }

  private List<Rule> findApplicableRules(DelphiNode node) {
    if (!(node instanceof DelphiElement el)) {
      return List.of();
    }

    StyleObjectModel styles = node.getDocument().getStyles();
    List<Rule> rules = new ArrayList<>();

    for (Rule rule : styles.getRules()) {
      if (!rule.getSelectorObject().test(null, el)) {
        continue;
      }

      rules.add(rule);
    }

    return rules;
  }

  private void appendProperties(boolean nl, String title, StylePropertiesReadonly readonly) {
    Set<String> properties = readonly.getProperties();
    if (properties.isEmpty()) {
      return;
    }

    if (nl) {
      nlIndent();
    }

    nlIndent()
        .append(title)
        .append("(property-count=")
        .append(properties.size())
        .append("): ");

    indent++;

    for (String property : properties) {
      String value = readonly.getPropertyValue(property);

      if (Strings.isNullOrEmpty(property)) {
        continue;
      }

      nlIndent().append(property).append(": ").append(value).append(";");
    }

    indent--;
  }

  private void appendRenderObjectComment(DelphiNode node, RenderObject re) {
    nlIndent().append("render-element:");
    indent++;

    nlIndent().append("parent-set: ").append(re.parent != null);

    if (re instanceof ContentRenderObject co) {
      nlIndent().append("content: ").append(co.getContent());
      nlIndent().append("content-dirty: ").append(co.isContentDirty());
    } else if (re instanceof ElementRenderObject er) {
      nlIndent().append("element-object-size: ").append(er.contentSize);
      nlIndent().append("child-count: ").append(er.childObjects.size());
    }

    Vector2f vector = new Vector2f();

    re.getElementSize(vector);
    nlIndent().append("size: ").append(vector);

    re.getContentStart(vector);
    nlIndent().append("content-start: ").append(vector);

    nlIndent().append("position: ").append(re.getPosition());
    nlIndent().append("depth: ").append(re.getDepth());

    nlIndent().append("full-style:");
    indent++;
    appendFullStyle(re.getStyle());
    indent--;

    indent--;

    List<Rule> applicable = findApplicableRules(node);
    if (!applicable.isEmpty()) {
      nlIndent();

      nlIndent()
          .append("applicable-rules(")
          .append(applicable.size())
          .append(" / ")
          .append(node.getDocument().getStyles().getRules().size())
          .append("):");

      indent++;

      for (Rule rule : applicable) {
        appendProperties(false, "rule[" + rule.getSelector() + "]", rule.getProperties());
      }

      indent--;
    }

    if (node instanceof DelphiElement el) {
      StyleProperties inline = el.getInlineStyle();
      appendProperties(true, "inline-properties", inline);
    }

    builder.append("\n");
    nlIndent().append("layers:");

    indent++;

    for (Layer layer : re.getLayers()) {
      if (RenderObject.isNotSpawned(layer)) {
        continue;
      }

      nlIndent().append("layer[").append(layer.layer).append("]:");
      indent++;

      nlIndent().append("size: ").append(layer.size);
      nlIndent().append("border-size: ").append(layer.borderSize);
      nlIndent().append("depth: ").append(layer.depth);
      nlIndent().append("scale: ").append(layer.scale);
      nlIndent().append("translate: ").append(layer.translate);

      nlIndent().append("entity-position: ")
          .append('(')
          .append(layer.entity.getX())
          .append(' ')
          .append(layer.entity.getY())
          .append(' ')
          .append(layer.entity.getZ())
          .append(')');

      indent--;
    }

    indent--;

    StylePropertiesReadonly styleSet = node.getDocument().getCurrentStyle(node);
    appendProperties(true, "current-style-properties", styleSet);

    if (node instanceof DelphiElement el) {
      appendListeners("", el.getListenerList());
    }
  }

  private void appendListeners(String name, EventListenerList listenerList) {
    Map<String, List<EventListener>> listenerMap = listenerList.getListenerMap();

    if (listenerMap.isEmpty()) {
      return;
    }

    nlIndent().append(name).append("event-listeners:");
    indent++;

    listenerMap.forEach((eventType, eventListeners) -> {
      nlIndent().append("event-type[").append(eventType).append("]: ");
      indent++;

      for (int i = 0; i < eventListeners.size(); i++) {
        EventListener l = eventListeners.get(i);
        nlIndent().append("- ").append(i).append(": ").append(l);
      }

      indent--;
    });

    indent--;
  }

  private void appendInfo(DelphiNode node) {
    RenderObject obj = view.getRenderObject(node);

    if (obj == null) {
      return;
    }

    nlIndent().append(COMMENT_START);
    indent++;

    appendRenderObjectComment(node, obj);

    indent--;
    nlIndent().append(COMMENT_END);
  }

  @Override
  public void enterElement(Element element) {
    super.enterElement(element);
    appendInfo((DelphiNode) element);
  }

  @Override
  public void enterText(TextNode text) {
    super.enterText(text);
    appendInfo((DelphiNode) text);
  }

  @Override
  public void enterComponent(ComponentNode node) {
    super.enterComponent(node);
    appendInfo((DelphiNode) node);
  }
}

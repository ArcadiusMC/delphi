package net.arcadiusmc.chimera;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.arcadiusmc.chimera.parse.Chimera;
import net.arcadiusmc.chimera.selector.Selector;
import net.arcadiusmc.chimera.system.StyleSystem;
import net.arcadiusmc.dom.ParserException;
import net.arcadiusmc.dom.style.StyleProperties;
import net.arcadiusmc.dom.style.StylesheetBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChimeraSheetBuilder implements StylesheetBuilder {

  private final @Nullable StyleSystem system;
  private final List<Rule> rules = new ArrayList<>();

  public ChimeraSheetBuilder(@Nullable StyleSystem system) {
    this.system = system;
  }

  public ChimeraSheetBuilder() {
    this(null);
  }

  @Override
  public ChimeraSheetBuilder addRule(
      @NotNull String selector,
      @NotNull Consumer<StyleProperties> consumer
  ) throws ParserException {
    Objects.requireNonNull(selector, "Null selector");
    Objects.requireNonNull(consumer, "Null consumer");

    Selector selectorObj = Chimera.parseSelector(selector);
    PropertySet set = new PropertySet();
    PropertiesMap map = new PropertiesMap(set, system);

    consumer.accept(map);

    Rule rule = new Rule(selectorObj, set);
    rules.add(rule);

    return this;
  }

  @Override
  public ChimeraStylesheet build() {
    return new ChimeraStylesheet(rules.toArray(Rule[]::new));
  }
}

package net.arcadiusmc.chimera.selector;

import net.arcadiusmc.dom.Element;

public record PseudoFuncFunction<T>(PseudoFunc<T> func, T argument) implements SelectorFunction {

  @Override
  public boolean test(Element root, Element element) {
    return func.test(root, element, argument);
  }

  @Override
  public void append(StringBuilder builder) {
    builder.append(':');
    func.append(builder);

    builder.append('(');
    func.appendValue(builder, argument);
    builder.append(')');
  }

  @Override
  public void appendSpec(Spec spec) {
    spec.classColumn++;
  }
}

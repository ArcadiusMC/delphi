package net.arcadiusmc.chimera.parse.ast;

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.chimera.parse.Scope;
import net.arcadiusmc.dom.style.Primitive;
import net.arcadiusmc.dom.style.Primitive.Unit;

@Getter @Setter
public class NumberLiteral extends Expression {

  private Number value;
  private Unit unit = Unit.NONE;

  @Override
  public Primitive evaluate(ChimeraContext ctx, Scope scope) {
    Unit unit = Objects.requireNonNullElse(this.unit, Unit.NONE);
    return Primitive.create(value.floatValue(), unit);
  }

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.numberLiteral(this);
  }
}

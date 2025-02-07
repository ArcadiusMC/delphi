package net.arcadiusmc.chimera.function;

import net.arcadiusmc.chimera.Properties;
import net.arcadiusmc.chimera.Property;
import net.arcadiusmc.chimera.PropertySet;
import net.arcadiusmc.chimera.Value;
import net.arcadiusmc.chimera.parse.Chimera;
import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.chimera.parse.Scope;
import org.apache.commons.lang3.Range;

public class GetPropertyFunction implements ScssFunction {

  @Override
  public Range<Integer> argumentCount() {
    return Range.is(1);
  }

  @Override
  public Object invoke(ChimeraContext ctx, Scope scope, Argument[] arguments)
      throws ScssInvocationException
  {
    String propertyName = arguments[0].string();
    PropertySet set = scope.getPropertyOutput();

    if (set == null) {
      throw new ScssInvocationException("Cannot access style properties in this context");
    }

    Property<?> property = Properties.getByKey(propertyName);
    if (property == null) {
      return null;
    }

    Value<?> v = set.orNull(property);
    if (v == null) {
      return null;
    }

    return Chimera.valueToScript(v);
  }
}

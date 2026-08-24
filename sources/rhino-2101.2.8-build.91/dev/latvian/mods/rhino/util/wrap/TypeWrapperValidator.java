package dev.latvian.mods.rhino.util.wrap;

import dev.latvian.mods.rhino.type.TypeInfo;

@FunctionalInterface
public interface TypeWrapperValidator {
   TypeWrapperValidator ALWAYS_VALID = (from, target) -> true;

   boolean isValid(Object var1, TypeInfo var2);
}

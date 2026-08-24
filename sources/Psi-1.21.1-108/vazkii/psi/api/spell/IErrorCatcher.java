package vazkii.psi.api.spell;

import org.jetbrains.annotations.NotNull;

public interface IErrorCatcher {
   boolean catchException(SpellPiece var1, SpellContext var2, SpellRuntimeException var3);

   @NotNull
   Object supplyReplacementValue(SpellPiece var1, SpellContext var2, SpellRuntimeException var3);

   boolean catchParam(SpellParam<?> var1);
}

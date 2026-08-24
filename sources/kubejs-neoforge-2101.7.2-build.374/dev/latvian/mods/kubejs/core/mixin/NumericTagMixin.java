package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.SpecialEquality;
import net.minecraft.nbt.NumericTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({NumericTag.class})
public abstract class NumericTagMixin implements SpecialEquality {
   @Shadow
   public abstract byte getAsByte();

   @Shadow
   public abstract double getAsDouble();

   public boolean specialEquals(Context cx, Object o, boolean shallow) {
      return switch (o) {
         case Boolean b -> b == (this.getAsByte() != 0);
         case Number n1 -> this.getAsDouble() == n1.doubleValue();
         case NumericTag n1x when !shallow -> this.getAsDouble() == n1x.getAsDouble();
         case null, default -> this.equals(o);
      };
   }
}

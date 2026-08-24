package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.CustomIngredientKJS;
import java.util.List;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({CompoundIngredient.class})
public abstract class CompoundIngredientMixin implements CustomIngredientKJS {
   @Shadow
   @Final
   private List<Ingredient> children;

   @Override
   public boolean kjs$canBeUsedForMatching() {
      for (Ingredient child : this.children) {
         if (!child.kjs$canBeUsedForMatching()) {
            return false;
         }
      }

      return true;
   }
}

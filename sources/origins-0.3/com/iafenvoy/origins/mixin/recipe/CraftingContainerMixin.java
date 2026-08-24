package com.iafenvoy.origins.mixin.recipe;

import com.iafenvoy.origins.accessor.PowerCraftingInventory;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CraftingInput.Positioned;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({CraftingContainer.class})
public interface CraftingContainerMixin {
   @ModifyReturnValue(
      method = {"asPositionedCraftInput"},
      at = {@At("RETURN")}
   )
   private Positioned passCacheToPositionedInput(Positioned original) {
      CraftingContainer var4 = (CraftingContainer)this;
      if (var4 instanceof PowerCraftingInventory sourcePci && original.input() instanceof PowerCraftingInventory targetPci) {
         targetPci.origins$setPowerTypes(sourcePci.origins$getPowerTypes());
         sourcePci.origins$getPlayer().ifPresentOrElse(targetPci::origins$setPlayer, targetPci::origins$clearPlayer);
         targetPci.origins$setInventory(sourcePci.origins$getInventory());
      }

      return original;
   }
}

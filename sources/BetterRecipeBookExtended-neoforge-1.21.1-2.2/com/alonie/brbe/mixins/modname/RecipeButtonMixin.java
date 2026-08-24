package com.alonie.brbe.mixins.modname;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.util.ModNameUtil;
import java.util.List;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({RecipeButton.class})
public abstract class RecipeButtonMixin {
   @Inject(
      method = {"getTooltipText"},
      locals = LocalCapture.CAPTURE_FAILHARD,
      at = {@At("RETURN")}
   )
   private void brbe$appendModName(CallbackInfoReturnable<List<Component>> cir, ItemStack itemStack, List<Component> list) {
      if (BetterRecipeBook.ctx().config().showModName) {
         Component modName = ModNameUtil.getFormattedModName(itemStack);
         if (modName != null && !modName.getString().isEmpty()) {
            list.add(Component.empty());
            list.add(modName);
         }
      }
   }
}

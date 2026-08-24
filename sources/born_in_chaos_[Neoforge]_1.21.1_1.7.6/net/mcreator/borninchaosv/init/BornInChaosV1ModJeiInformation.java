package net.mcreator.borninchaosv.init;

import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

@JeiPlugin
public class BornInChaosV1ModJeiInformation implements IModPlugin {
   public ResourceLocation getPluginUid() {
      return ResourceLocation.parse("born_in_chaos_v1:information");
   }

   public void registerRecipes(IRecipeRegistration registration) {
      registration.addIngredientInfo(
         List.of(new ItemStack((ItemLike)BornInChaosV1ModItems.SPINY_SHELL.get())),
         VanillaTypes.ITEM_STACK,
         new Component[]{Component.translatable("jei.born_in_chaos_v1.spiny_shell_info")}
      );
   }
}

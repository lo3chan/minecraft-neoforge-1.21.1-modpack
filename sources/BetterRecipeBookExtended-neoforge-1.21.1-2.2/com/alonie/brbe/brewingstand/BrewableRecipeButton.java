package com.alonie.brbe.brewingstand;

import com.alonie.brbe.generic.GenericRecipeButton;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BrewableRecipeButton extends GenericRecipeButton<BrewingRecipeCollection, BrewableResult, BrewingStandMenu> {
   public BrewableRecipeButton(RegistryAccess registryAccess, Supplier<Boolean> rememberedFilteringSupplier) {
      super(registryAccess, rememberedFilteringSupplier);
   }

   @Override
   public void updateWidgetNarration(NarrationElementOutput builder) {
      ItemStack inputStack = this.collection.getFirst().inputAsItemStack(this.category);
      builder.add(NarratedElementType.TITLE, Component.translatable("narration.recipe", new Object[]{inputStack.getHoverName()}));
      builder.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"));
   }

   @Override
   public List<Component> getTooltipText() {
      List<Component> list = Lists.newArrayList();
      ItemStack resultStack = this.collection.getFirst().getResult(this.registryAccess, this.category);
      list.add(resultStack.getHoverName());
      ((PotionContents)resultStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY))
         .addPotionTooltip(list::add, 1.0F, Minecraft.getInstance().level.tickRateManager().tickrate());
      list.add(Component.literal(""));
      ChatFormatting colour = ChatFormatting.DARK_GRAY;
      if (this.collection.getFirst().hasIngredient(this.menu.slots)) {
         colour = ChatFormatting.WHITE;
      }

      list.add(
         Component.literal(PlatformPotionUtil.getIngredient(this.collection.getFirst().recipe).getItems()[0].getHoverName().getString()).withStyle(colour)
      );
      list.add(Component.literal("↓").withStyle(ChatFormatting.DARK_GRAY));
      ItemStack inputStack = this.collection.getFirst().inputAsItemStack(this.category);
      if (!this.collection.getFirst().hasInput(this.category, this.menu.slots)) {
         colour = ChatFormatting.DARK_GRAY;
      }

      list.add(Component.literal(inputStack.getHoverName().getString()).withStyle(colour));
      this.addPinTooltip(list);
      return list;
   }
}

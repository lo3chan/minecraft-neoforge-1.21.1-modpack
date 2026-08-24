package jeresources.jei.worldgen;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import jeresources.api.conditionals.Conditional;
import jeresources.api.drop.LootDrop;
import jeresources.config.Settings;
import jeresources.entry.WorldGenEntry;
import jeresources.util.RegistryHelper;
import jeresources.util.TranslationHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class WorldGenTooltip implements IRecipeSlotTooltipCallback {
   private final WorldGenEntry entry;

   public WorldGenTooltip(WorldGenEntry entry) {
      this.entry = entry;
   }

   public void onTooltip(IRecipeSlotView recipeSlotView, List<Component> tooltip) {
      tooltip.addAll(
         this.getItemStackTooltip(
            (String)recipeSlotView.getSlotName().orElse(null), (ItemStack)((ITypedIngredient)recipeSlotView.getDisplayedIngredient().get()).getIngredient()
         )
      );
   }

   private List<Component> getItemStackTooltip(String slotName, ItemStack itemStack) {
      List<String> tooltip = new LinkedList<>();
      if (itemStack != null && slotName != null && slotName.equals("oreSlot")) {
         if (this.entry.isSilkTouchNeeded()) {
            tooltip.add(Conditional.silkTouch.toString());
         }

         List<String> biomes = this.entry.getBiomeRestrictions();
         if (biomes.size() > 0) {
            tooltip.add(TranslationHelper.translateAndFormat("jer.worldgen.biomes") + ":");
            tooltip.addAll(biomes);
         }

         if (Settings.showDevData) {
            tooltip.add(TranslationHelper.translateAndFormat("jer.worldgen.averageChunk") + ":");
            tooltip.add(this.entry.getAverageBlockCountPerChunk() + "");
         }
      } else {
         tooltip.add(TranslationHelper.translateAndFormat("jer.worldgen.average"));

         for (LootDrop dropItem : this.entry.getLootDrops(itemStack)) {
            String line = " - ";
            if (dropItem.fortuneLevel > 0) {
               line = line + Enchantment.getFullname(RegistryHelper.getHolder(Registries.ENCHANTMENT, Enchantments.FORTUNE), dropItem.fortuneLevel).getString();
            } else {
               line = line + TranslationHelper.translateAndFormat("jer.worldgen.base");
            }

            if (dropItem.chance < 1.0F) {
               line = line + " " + TranslationHelper.translateAndFormat("jer.worldgen.chance", dropItem.formatChance() + "%");
            }

            if (dropItem.minDrop == dropItem.maxDrop) {
               line = line + ": " + dropItem.minDrop;
            } else {
               line = line + ": " + dropItem.minDrop + " - " + dropItem.maxDrop;
            }

            if (dropItem.isAffectedBy(Conditional.affectedByFortune)) {
               line = line + " " + TranslationHelper.translateAndFormat("jer.worldgen.affectedByFortune");
            }

            tooltip.add(line);
         }
      }

      return tooltip.stream().<Component>map(Component::literal).collect(Collectors.toList());
   }
}

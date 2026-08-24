package jeresources.jei.enchantment;

import java.util.LinkedList;
import java.util.List;
import jeresources.entry.EnchantmentEntry;
import jeresources.registry.EnchantmentRegistry;
import jeresources.util.Font;
import jeresources.util.TranslationHelper;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EnchantmentWrapper implements IRecipeCategoryExtension<EnchantmentWrapper> {
   private static final int ENTRIES_PER_PAGE = 11;
   private static final int ENCHANT_X = 35;
   private static final int FIRST_ENCHANT_Y = 7;
   private static final int SPACING_Y = 10;
   private static final int PAGE_X = 55;
   private static final int PAGE_Y = 120;
   private static final int CYCLE_TIME = 2;
   protected final ItemStack itemStack;
   private final List<EnchantmentEntry> enchantments;
   private final int lastSet;
   private int set;
   private int nextCycle;

   public static EnchantmentWrapper create(@NotNull ItemStack itemStack) {
      List<EnchantmentEntry> enchantments = new LinkedList<>(EnchantmentRegistry.getInstance().getEnchantments(itemStack));
      return enchantments.isEmpty() ? null : new EnchantmentWrapper(itemStack, enchantments);
   }

   private EnchantmentWrapper(@NotNull ItemStack itemStack, @NotNull List<EnchantmentEntry> enchantments) {
      this.itemStack = itemStack;
      this.enchantments = enchantments;
      this.set = 0;
      this.lastSet = this.enchantments.size() / 12;
      this.nextCycle = (int)System.currentTimeMillis() / 1000 + 2;
   }

   public List<EnchantmentEntry> getEnchantments() {
      this.doCycle();
      int last = this.set * 11 + 11;
      if (last >= this.enchantments.size()) {
         last = this.enchantments.size();
      }

      return this.enchantments.subList(this.set * 11, last);
   }

   private void doCycle() {
      if ((int)System.currentTimeMillis() / 1000 > this.nextCycle) {
         if (!Screen.hasShiftDown()) {
            this.set = this.set == this.lastSet ? 0 : this.set + 1;
         }

         this.nextCycle = (int)System.currentTimeMillis() / 1000 + 2;
      }
   }

   public void drawInfo(EnchantmentWrapper recipe, int recipeWidth, int recipeHeight, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
      int y = 7;

      for (EnchantmentEntry enchantment : this.getEnchantments()) {
         Font.normal.print(guiGraphics, enchantment.getTranslatedWithLevels(), 35, y);
         y += 10;
      }

      if (this.lastSet > 0) {
         String toPrint = TranslationHelper.getLocalPageInfo(this.set, this.lastSet);
         Font.normal.print(guiGraphics, toPrint, 55, 120);
      }
   }
}

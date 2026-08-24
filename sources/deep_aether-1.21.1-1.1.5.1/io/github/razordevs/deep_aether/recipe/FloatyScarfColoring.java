package io.github.razordevs.deep_aether.recipe;

import io.github.razordevs.deep_aether.init.DAItems;
import io.github.razordevs.deep_aether.item.component.DADataComponentTypes;
import io.github.razordevs.deep_aether.item.component.FloatyScarf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags.Items;
import org.jetbrains.annotations.NotNull;

public class FloatyScarfColoring extends CustomRecipe {
   public FloatyScarfColoring(CraftingBookCategory pCategory) {
      super(pCategory);
   }

   public boolean matches(CraftingInput input, @NotNull Level level) {
      boolean containsScarf = false;
      boolean containsBucket = false;
      int colorCount = 0;

      for (int k = 0; k < input.size(); k++) {
         ItemStack itemstack = input.getItem(k);
         if (itemstack.is(DAItems.FLOATY_SCARF)) {
            containsScarf = true;
         } else if (itemstack.getItem() instanceof DyeItem) {
            colorCount++;
         } else if (itemstack.is(Items.BUCKETS)) {
            if (containsBucket) {
               return false;
            }

            containsBucket = true;
         } else if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return containsBucket && colorCount > 0 ? false : containsScarf;
   }

   @NotNull
   public ItemStack assemble(CraftingInput input, @NotNull Provider provider) {
      List<Integer> colors = new ArrayList<>();
      ItemStack scarf = new ItemStack((ItemLike)DAItems.FLOATY_SCARF.get());
      boolean bucket = false;

      for (int i = 0; i < input.size(); i++) {
         ItemStack itemstack = input.getItem(i);
         if (itemstack.is(DAItems.FLOATY_SCARF)) {
            scarf = itemstack;
         } else if (itemstack.getItem() instanceof DyeItem item) {
            colors.add(item.getDyeColor().getTextureDiffuseColor());
         } else if (itemstack.is(net.minecraft.world.item.Items.WATER_BUCKET)) {
            bucket = true;
            break;
         }
      }

      scarf = scarf.copy();
      FloatyScarf floatyScarf = (FloatyScarf)scarf.get(DADataComponentTypes.FLOATY_SCARF);
      if (floatyScarf == null) {
         floatyScarf = FloatyScarf.withDefaultColor(0);
      }

      if (bucket) {
         List<Integer> colors1 = floatyScarf.colors();
         colors1.set(floatyScarf.currentModification(), -1);
         byte currentModification = (byte)(floatyScarf.currentModification() + 1);
         if (currentModification > 4) {
            currentModification = 0;
         }

         floatyScarf = new FloatyScarf(floatyScarf.uuid(), colors1, currentModification);
      } else if (colors.isEmpty()) {
         byte currentModification = (byte)(floatyScarf.currentModification() + 1);
         if (currentModification > 4) {
            currentModification = 0;
         }

         floatyScarf = new FloatyScarf(floatyScarf.uuid(), floatyScarf.colors(), currentModification);
      } else {
         int applyDyes = applyDyes(floatyScarf.colors().get(floatyScarf.currentModification()), colors);
         List<Integer> colors1 = floatyScarf.colors();
         colors1.set(floatyScarf.currentModification(), applyDyes);
         byte currentModification = (byte)(floatyScarf.currentModification() + 1);
         if (currentModification > 4) {
            currentModification = 0;
         }

         floatyScarf = new FloatyScarf(floatyScarf.uuid(), colors1, currentModification);
      }

      scarf.set(DADataComponentTypes.FLOATY_SCARF, floatyScarf);
      return scarf.copy();
   }

   public boolean canCraftInDimensions(int pWidth, int pHeight) {
      return pWidth * pHeight >= 2;
   }

   @NotNull
   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)DARecipeSerializers.FLOATY_SCARF_COLORING.get();
   }

   public static int applyDyes(int color, List<Integer> dyes) {
      int i = 0;
      int j = 0;
      int k = 0;
      int l = 0;
      int i1 = 0;
      int j1 = ARGB32.red(color);
      int k1 = ARGB32.green(color);
      int l1 = ARGB32.blue(color);
      l += Math.max(j1, Math.max(k1, l1));
      i += j1;
      j += k1;
      k += l1;
      i1++;

      for (int dye : dyes) {
         int i2 = ARGB32.red(dye);
         int j2 = ARGB32.green(dye);
         int k2 = ARGB32.blue(dye);
         l += Math.max(i2, Math.max(j2, k2));
         i += i2;
         j += j2;
         k += k2;
         i1++;
      }

      int l2 = i / i1;
      int i3 = j / i1;
      int k3 = k / i1;
      float f = (float)l / i1;
      float f1 = Math.max(l2, Math.max(i3, k3));
      l2 = (int)(l2 * f / f1);
      i3 = (int)(i3 * f / f1);
      k3 = (int)(k3 * f / f1);
      return ARGB32.color(0, l2, i3, k3);
   }
}

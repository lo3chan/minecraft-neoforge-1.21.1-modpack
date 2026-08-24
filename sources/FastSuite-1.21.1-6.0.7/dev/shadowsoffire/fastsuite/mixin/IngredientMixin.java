package dev.shadowsoffire.fastsuite.mixin;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(
   value = {Ingredient.class},
   priority = 500,
   remap = false
)
public abstract class IngredientMixin {
   @Shadow
   @Nullable
   private IntList stackingIds;

   @Overwrite
   public IntList getStackingIds() {
      if (this.stackingIds == null) {
         ItemStack[] var1 = this.getItems();
      }

      return this.__getStackingIds();
   }

   @Shadow
   public abstract ItemStack[] getItems();

   @Unique
   private IntList __getStackingIds() {
      synchronized (this) {
         if (this.stackingIds == null) {
            ItemStack[] aitemstack = this.getItems();
            this.stackingIds = new IntArrayList(aitemstack.length);

            for (ItemStack itemstack : aitemstack) {
               this.stackingIds.add(StackedContents.getStackingIndex(itemstack));
            }

            this.stackingIds.sort(IntComparators.NATURAL_COMPARATOR);
         }

         return this.stackingIds;
      }
   }
}

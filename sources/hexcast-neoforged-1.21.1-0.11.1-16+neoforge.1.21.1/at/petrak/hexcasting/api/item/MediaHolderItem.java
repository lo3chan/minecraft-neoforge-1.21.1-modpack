package at.petrak.hexcasting.api.item;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus.OverrideOnly;

@OverrideOnly
public interface MediaHolderItem {
   long getMedia(ItemStack var1);

   long getMaxMedia(ItemStack var1);

   void setMedia(ItemStack var1, long var2);

   boolean canProvideMedia(ItemStack var1);

   boolean canRecharge(ItemStack var1);

   default float getMediaFullness(ItemStack stack) {
      long max = this.getMaxMedia(stack);
      return max == 0L ? 0.0F : (float)this.getMedia(stack) / (float)max;
   }

   default long withdrawMedia(ItemStack stack, long cost, boolean simulate) {
      long mediaHere = this.getMedia(stack);
      if (cost < 0L) {
         cost = mediaHere;
      }

      if (!simulate) {
         long mediaLeft = mediaHere - cost;
         this.setMedia(stack, mediaLeft);
      }

      return Math.min(cost, mediaHere);
   }

   default long insertMedia(ItemStack stack, long amount, boolean simulate) {
      long mediaHere = this.getMedia(stack);
      long emptySpace = this.getMaxMedia(stack) - mediaHere;
      if (emptySpace <= 0L) {
         return 0L;
      } else {
         if (amount < 0L) {
            amount = emptySpace;
         }

         long inserting = Math.min(amount, emptySpace);
         if (!simulate) {
            long newMedia = mediaHere + inserting;
            this.setMedia(stack, newMedia);
         }

         return inserting;
      }
   }
}

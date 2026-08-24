package at.petrak.hexcasting.api.addldata;

import org.jetbrains.annotations.ApiStatus.OverrideOnly;

public interface ADMediaHolder {
   int QUENCHED_ALLAY_PRIORITY = 800;
   int QUENCHED_SHARD_PRIORITY = 900;
   int CHARGED_AMETHYST_PRIORITY = 1000;
   int AMETHYST_SHARD_PRIORITY = 2000;
   int AMETHYST_DUST_PRIORITY = 3000;
   int BATTERY_PRIORITY = 4000;

   @OverrideOnly
   long getMedia();

   @OverrideOnly
   long getMaxMedia();

   @OverrideOnly
   void setMedia(long var1);

   boolean canRecharge();

   boolean canProvide();

   int getConsumptionPriority();

   boolean canConstructBattery();

   default long withdrawMedia(long cost, boolean simulate) {
      long mediaHere = this.getMedia();
      if (cost < 0L) {
         cost = mediaHere;
      }

      if (!simulate) {
         long mediaLeft = mediaHere - cost;
         this.setMedia(mediaLeft);
      }

      return Math.min(cost, mediaHere);
   }

   default long insertMedia(long amount, boolean simulate) {
      long mediaHere = this.getMedia();
      long emptySpace = this.getMaxMedia() - mediaHere;
      if (emptySpace <= 0L) {
         return 0L;
      } else {
         if (amount < 0L) {
            amount = emptySpace;
         }

         long inserting = Math.min(amount, emptySpace);
         if (!simulate) {
            long newMedia = mediaHere + inserting;
            this.setMedia(newMedia);
         }

         return inserting;
      }
   }
}

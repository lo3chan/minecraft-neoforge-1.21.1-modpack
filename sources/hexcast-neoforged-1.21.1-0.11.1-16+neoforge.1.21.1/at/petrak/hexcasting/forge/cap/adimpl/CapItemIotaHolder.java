package at.petrak.hexcasting.forge.cap.adimpl;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record CapItemIotaHolder(IotaHolderItem holder, ItemStack stack) implements ADIotaHolder {
   @Nullable
   @Override
   public CompoundTag readIotaTag() {
      return this.holder.readIotaTag(this.stack);
   }

   @Nullable
   @Override
   public Iota readIota(ServerLevel world) {
      return this.holder.readIota(this.stack, world);
   }

   @Nullable
   @Override
   public Iota emptyIota() {
      return this.holder.emptyIota(this.stack);
   }

   @Override
   public boolean writeIota(@Nullable Iota iota, boolean simulate) {
      if (!this.holder.canWrite(this.stack, iota)) {
         return false;
      } else {
         if (!simulate) {
            this.holder.writeDatum(this.stack, iota);
         }

         return true;
      }
   }

   @Override
   public boolean writeable() {
      return this.holder.writeable(this.stack);
   }
}

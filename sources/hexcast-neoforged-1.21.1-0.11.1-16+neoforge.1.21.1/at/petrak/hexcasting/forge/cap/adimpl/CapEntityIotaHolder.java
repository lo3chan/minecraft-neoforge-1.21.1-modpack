package at.petrak.hexcasting.forge.cap.adimpl;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.addldata.ItemDelegatingEntityIotaHolder;
import at.petrak.hexcasting.api.casting.iota.Iota;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

public abstract class CapEntityIotaHolder implements ADIotaHolder {
   public static class Wrapper extends CapEntityIotaHolder {
      private final ItemDelegatingEntityIotaHolder inner;

      public Wrapper(ItemDelegatingEntityIotaHolder inner) {
         this.inner = inner;
      }

      @Nullable
      @Override
      public CompoundTag readIotaTag() {
         return this.inner.readIotaTag();
      }

      @Override
      public boolean writeIota(@Nullable Iota iota, boolean simulate) {
         return this.inner.writeIota(iota, simulate);
      }

      @Override
      public boolean writeable() {
         return this.inner.writeable();
      }

      @Nullable
      @Override
      public Iota readIota(ServerLevel world) {
         return this.inner.readIota(world);
      }

      @Nullable
      @Override
      public Iota emptyIota() {
         return this.inner.emptyIota();
      }
   }
}

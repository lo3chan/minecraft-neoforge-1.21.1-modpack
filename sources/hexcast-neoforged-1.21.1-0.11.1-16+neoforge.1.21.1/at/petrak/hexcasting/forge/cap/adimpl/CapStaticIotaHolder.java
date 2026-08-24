package at.petrak.hexcasting.forge.cap.adimpl;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import java.util.function.Function;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record CapStaticIotaHolder(Function<ItemStack, Iota> provider, ItemStack stack) implements ADIotaHolder {
   @Nullable
   @Override
   public CompoundTag readIotaTag() {
      Iota iota = this.provider.apply(this.stack);
      return iota == null ? null : IotaType.serialize(iota);
   }

   @Nullable
   @Override
   public Iota readIota(ServerLevel world) {
      return this.provider.apply(this.stack);
   }

   @Override
   public boolean writeIota(@Nullable Iota iota, boolean simulate) {
      return false;
   }

   @Override
   public boolean writeable() {
      return false;
   }
}

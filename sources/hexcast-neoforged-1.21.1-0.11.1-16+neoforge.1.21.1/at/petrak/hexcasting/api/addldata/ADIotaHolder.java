package at.petrak.hexcasting.api.addldata;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

public interface ADIotaHolder {
   @Nullable
   CompoundTag readIotaTag();

   @Nullable
   default Iota readIota(ServerLevel world) {
      CompoundTag tag = this.readIotaTag();
      return tag != null ? IotaType.deserialize(tag, world) : null;
   }

   @Nullable
   default Iota emptyIota() {
      return null;
   }

   boolean writeIota(@Nullable Iota var1, boolean var2);

   boolean writeable();
}

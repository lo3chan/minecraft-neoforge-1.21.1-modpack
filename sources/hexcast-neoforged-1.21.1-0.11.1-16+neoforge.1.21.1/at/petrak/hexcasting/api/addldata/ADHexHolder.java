package at.petrak.hexcasting.api.addldata;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

public interface ADHexHolder {
   boolean canDrawMediaFromInventory();

   boolean hasHex();

   @Nullable
   List<Iota> getHex(ServerLevel var1);

   void writeHex(List<Iota> var1, @Nullable FrozenPigment var2, long var3);

   void clearHex();

   @Nullable
   FrozenPigment getPigment();
}

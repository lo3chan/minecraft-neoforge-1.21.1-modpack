package at.petrak.hexcasting.api.item;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.OverrideOnly;

@OverrideOnly
public interface HexHolderItem extends MediaHolderItem {
   boolean canDrawMediaFromInventory(ItemStack var1);

   boolean hasHex(ItemStack var1);

   @Nullable
   List<Iota> getHex(ItemStack var1, ServerLevel var2);

   void writeHex(ItemStack var1, List<Iota> var2, @Nullable FrozenPigment var3, long var4);

   void clearHex(ItemStack var1);

   @Nullable
   FrozenPigment getPigment(ItemStack var1);
}

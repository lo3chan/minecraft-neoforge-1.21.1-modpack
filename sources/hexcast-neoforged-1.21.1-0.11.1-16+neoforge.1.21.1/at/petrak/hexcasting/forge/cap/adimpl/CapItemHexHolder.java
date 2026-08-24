package at.petrak.hexcasting.forge.cap.adimpl;

import at.petrak.hexcasting.api.addldata.ADHexHolder;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.item.HexHolderItem;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record CapItemHexHolder(HexHolderItem holder, ItemStack stack) implements ADHexHolder {
   @Override
   public boolean canDrawMediaFromInventory() {
      return this.holder.canDrawMediaFromInventory(this.stack);
   }

   @Override
   public boolean hasHex() {
      return this.holder.hasHex(this.stack);
   }

   @Nullable
   @Override
   public List<Iota> getHex(ServerLevel level) {
      return this.holder.getHex(this.stack, level);
   }

   @Override
   public void writeHex(List<Iota> patterns, @Nullable FrozenPigment pigment, long media) {
      this.holder.writeHex(this.stack, patterns, pigment, media);
   }

   @Override
   public void clearHex() {
      this.holder.clearHex(this.stack);
   }

   @Nullable
   @Override
   public FrozenPigment getPigment() {
      return this.holder.getPigment(this.stack);
   }
}

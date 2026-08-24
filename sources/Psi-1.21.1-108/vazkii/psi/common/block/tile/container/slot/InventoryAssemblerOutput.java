package vazkii.psi.common.block.tile.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.common.block.tile.TileCADAssembler;

public class InventoryAssemblerOutput implements Container {
   private final Player player;
   private final TileCADAssembler assembler;

   public InventoryAssemblerOutput(Player player, TileCADAssembler assembler) {
      this.player = player;
      this.assembler = assembler;
   }

   private ItemStack getStack() {
      return this.assembler.getCachedCAD(this.player);
   }

   public int getContainerSize() {
      return 1;
   }

   public boolean isEmpty() {
      return this.getStack().isEmpty();
   }

   @NotNull
   public ItemStack getItem(int index) {
      return this.getStack();
   }

   @NotNull
   public ItemStack removeItem(int index, int count) {
      return this.getStack();
   }

   @NotNull
   public ItemStack removeItemNoUpdate(int index) {
      return this.getStack();
   }

   public void setItem(int index, @NotNull ItemStack stack) {
   }

   public int getMaxStackSize() {
      return 1;
   }

   public void setChanged() {
   }

   public boolean stillValid(@NotNull Player player) {
      return true;
   }

   public void startOpen(@NotNull Player player) {
   }

   public void stopOpen(@NotNull Player player) {
   }

   public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
      return false;
   }

   public void clearContent() {
   }
}

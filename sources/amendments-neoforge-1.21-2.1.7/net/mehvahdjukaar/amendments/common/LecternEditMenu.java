package net.mehvahdjukaar.amendments.common;

import java.util.List;
import net.mehvahdjukaar.amendments.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.misc.TileOrEntityTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LecternBlockEntity;

public class LecternEditMenu extends LecternMenu {
   private final BlockPos pos;

   public static LecternEditMenu of(int id, Inventory playerInventory, FriendlyByteBuf packetBuffer) {
      TileOrEntityTarget tile = TileOrEntityTarget.read(packetBuffer);
      return new LecternEditMenu(
         id, (LecternBlockEntity)tile.getBlockEntityOrThrow(playerInventory.player.level(), BlockEntityType.LECTERN), new SimpleContainerData(1)
      );
   }

   public LecternEditMenu(int i, LecternBlockEntity container, ContainerData containerData) {
      super(i, (Container)container, containerData);
      this.pos = container.getBlockPos();
   }

   public MenuType<?> getType() {
      return ModRegistry.LECTERN_EDIT_MENU.get();
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public void initializeContents(int stateId, List<ItemStack> items, ItemStack carried) {
      super.initializeContents(stateId, items, carried);
   }
}

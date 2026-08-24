package tannyjung.tanshugetrees.procedures;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.LevelAccessor;
import tannyjung.tanshugetrees.world.inventory.TreeSummonerStaffGUIMenu;
import tannyjung.tanshugetrees_handcode.systems.tree_generator.TreeSummonerStaff;

public class TreeSummonerStaffClickProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (!entity.isShiftKeyDown()) {
            Player player = (Player)entity;
            TreeSummonerStaff.click(player);
         } else {
            if (entity instanceof ServerPlayer _ent) {
               final BlockPos _bpos = BlockPos.containing(x, y, z);
               _ent.openMenu(new MenuProvider() {
                  public Component getDisplayName() {
                     return Component.literal("TreeSummonerStaffGUI");
                  }

                  public boolean shouldTriggerClientSideContainerClosingOnOpen() {
                     return false;
                  }

                  public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                     return new TreeSummonerStaffGUIMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(_bpos));
                  }
               }, _bpos);
            }

            TreeSummonerStaffGUIWhenOpenProcedure.execute(entity);
         }
      }
   }
}

package net.mehvahdjukaar.moonlight.api.block;

import java.util.UUID;
import net.mehvahdjukaar.moonlight.api.client.IScreenProvider;
import net.mehvahdjukaar.moonlight.api.misc.TileOrEntityTarget;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

@Deprecated(
   forRemoval = true
)
public interface IOnePlayerInteractable {
   void setPlayerWhoMayEdit(@Nullable UUID var1);

   UUID getPlayerWhoMayEdit();

   default boolean isEditingPlayer(BlockPos myPos, Player player) {
      if (player.level().isClientSide) {
         return this.isCloseEnoughToEdit(player, myPos);
      } else {
         this.validateEditingPlayer(myPos, player.level());
         UUID uuid = this.getPlayerWhoMayEdit();
         return uuid != null && uuid.equals(player.getUUID());
      }
   }

   default boolean isOtherPlayerEditing(BlockPos myPos, Player otherThan) {
      this.validateEditingPlayer(myPos, otherThan.level());
      UUID uuid = this.getPlayerWhoMayEdit();
      return uuid != null && !uuid.equals(otherThan.getUUID());
   }

   private void validateEditingPlayer(BlockPos myPos, Level level) {
      if (level == null) {
         this.setPlayerWhoMayEdit(null);
      } else {
         UUID uuid = this.getPlayerWhoMayEdit();
         if (uuid != null) {
            Player player = level.getPlayerByUUID(uuid);
            if (player == null || !this.isCloseEnoughToEdit(player, myPos)) {
               this.setPlayerWhoMayEdit(null);
            }
         }
      }
   }

   private boolean isCloseEnoughToEdit(Player player, BlockPos myPos) {
      return player.canInteractWithBlock(myPos, 8.0);
   }

   @Deprecated(
      forRemoval = true
   )
   default boolean tryOpeningEditGui(ServerPlayer player, BlockPos pos, ItemStack stack, Direction hitFace) {
      return this.tryOpeningEditGui(player, pos, stack, hitFace, new Vec3(0.5, 0.5, 0.5));
   }

   default boolean tryOpeningEditGui(ServerPlayer player, BlockPos pos, ItemStack stack, Direction hitFace, Vec3 hitPos) {
      if (Utils.mayPerformBlockAction(player, pos, stack) && !this.isOtherPlayerEditing(pos, player)) {
         this.setPlayerWhoMayEdit(player.getUUID());
         if (this instanceof IScreenProvider sp) {
            sp.sendOpenGuiPacket(player, hitFace, hitPos);
            return false;
         }

         if (this instanceof MenuProvider mp && this instanceof BlockEntity be) {
            TileOrEntityTarget target = TileOrEntityTarget.of(be);
            PlatHelper.openCustomMenu(player, mp, target::write);
            return true;
         }
      }

      return false;
   }
}

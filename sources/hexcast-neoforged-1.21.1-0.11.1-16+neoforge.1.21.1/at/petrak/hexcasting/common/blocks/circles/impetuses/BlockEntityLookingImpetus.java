package at.petrak.hexcasting.common.blocks.circles.impetuses;

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.api.casting.circles.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.common.lib.HexBlockEntities;
import at.petrak.hexcasting.common.lib.HexSounds;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class BlockEntityLookingImpetus extends BlockEntityAbstractImpetus {
   public static final int MAX_LOOK_AMOUNT = 30;
   public static final String TAG_LOOK_AMOUNT = "look_amount";
   private int lookAmount = 0;

   public BlockEntityLookingImpetus(BlockPos pWorldPosition, BlockState pBlockState) {
      super(HexBlockEntities.IMPETUS_LOOK_TILE, pWorldPosition, pBlockState);
   }

   public static void serverTick(Level level, BlockPos pos, BlockState bs, BlockEntityLookingImpetus self) {
      if (!(Boolean)bs.getValue(BlockCircleComponent.ENERGIZED)) {
         int prevLookAmt = self.lookAmount;
         int range = 20;
         List<ServerPlayer> players = level.getEntitiesOfClass(
            ServerPlayer.class,
            new AABB(pos.getX() - range, pos.getY() - range, pos.getZ() - range, pos.getX() + range, pos.getY() + range, pos.getZ() + range)
         );
         ServerPlayer looker = null;

         for (ServerPlayer player : players) {
            ItemStack hat = player.getItemBySlot(EquipmentSlot.HEAD);
            if (hat.isEmpty() || !hat.is(Blocks.CARVED_PUMPKIN.asItem())) {
               Vec3 lookEnd = player.getLookAngle().scale(range / 1.5F);
               BlockHitResult hit = level.clip(new ClipContext(player.getEyePosition(), player.getEyePosition().add(lookEnd), Block.VISUAL, Fluid.NONE, player));
               if (hit.getType() == Type.BLOCK && hit.getBlockPos().equals(pos)) {
                  looker = player;
                  break;
               }
            }
         }

         int newLook = Mth.clamp(prevLookAmt + (looker == null ? -1 : 1), 0, 30);
         if (newLook != prevLookAmt) {
            if (newLook == 30) {
               self.lookAmount = 0;
               self.startExecution(looker);
            } else {
               if (newLook % 5 == 1) {
                  float t = newLook / 30.0F;
                  float pitch = Mth.lerp(t, 0.5F, 1.2F);
                  float volume = Mth.lerp(t, 0.2F, 1.2F);
                  level.playSound(null, pos, HexSounds.IMPETUS_LOOK_TICK, SoundSource.BLOCKS, volume, pitch);
               }

               self.lookAmount = newLook;
               self.setChanged();
            }
         }
      }
   }

   @Override
   protected void saveModData(CompoundTag tag) {
      super.saveModData(tag);
      tag.putInt("look_amount", this.lookAmount);
   }

   @Override
   protected void loadModData(CompoundTag tag) {
      super.loadModData(tag);
      this.lookAmount = tag.getInt("look_amount");
   }
}

package io.github.razordevs.deep_aether.block.misc;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.mixin.mixins.common.accessor.ServerGamePacketListenerImplAccessor;
import io.github.razordevs.deep_aether.block.natural.SterlingAercloudBlock;
import io.github.razordevs.deep_aether.item.gear.DAEquipmentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChromaticAercloudBlock extends HalfTransparentBlock {
   public ChromaticAercloudBlock(Properties properties) {
      super(properties);
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return context instanceof EntityCollisionContext collisionContext
            && collisionContext.getEntity() instanceof LivingEntity entity
            && DAEquipmentUtil.hasCloudNecklace(entity)
         ? SterlingAercloudBlock.FULL_COLLISION
         : SterlingAercloudBlock.NO_COLLISION;
   }

   public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
      return false;
   }

   public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return Shapes.empty();
   }

   public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
      return 0.25F;
   }

   public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
   }

   public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
      entity.resetFallDistance();
      if (entity instanceof Player player) {
         handleFlight(player);
      } else {
         entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0, 0.005, 1.0));
         entity.setOnGround(true);
      }
   }

   static void handleFlight(LivingEntity entity) {
      if (entity instanceof Player player && !player.getAbilities().flying) {
         AetherPlayerAttachment data = (AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER);
         Vec3 deltaMovement = player.getDeltaMovement();
         if (data.isJumping() && !player.onGround()) {
            if (data.getFlightModifier() >= data.getFlightModifierMax()) {
               data.setFlightModifier(data.getFlightModifierMax());
            }

            if (data.getFlightTimer() > 2) {
               if (data.getFlightTimer() < data.getFlightTimerMax()) {
                  data.setFlightModifier(data.getFlightModifier() + 0.25F);
                  data.setFlightTimer(data.getFlightTimer() + 1);
               }
            } else {
               data.setFlightTimer(data.getFlightTimer() + 1);
            }
         } else if (!data.isJumping()) {
            data.setFlightModifier(1.0F);
         }

         if (player.onGround()) {
            data.setFlightTimer(0);
            data.setFlightModifier(1.0F);
         }

         if (data.isJumping() && !player.onGround() && data.getFlightTimer() > 2 && data.getFlightModifier() > 1.0F) {
            player.setDeltaMovement(deltaMovement.x(), 0.025F * data.getFlightModifier(), deltaMovement.z());
         }

         if (player instanceof ServerPlayer serverPlayer) {
            ServerGamePacketListenerImplAccessor serverGamePacketListenerImplAccessor = (ServerGamePacketListenerImplAccessor)serverPlayer.connection;
            serverGamePacketListenerImplAccessor.aether$setAboveGroundTickCount(0);
         }
      }
   }
}

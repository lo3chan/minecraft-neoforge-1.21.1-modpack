package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.block.BlockSculkBoomer;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class TileEntitySculkBoomer extends BlockEntity implements GameEventListener {
   private final BlockPositionSource blockPosSource = new BlockPositionSource(this.worldPosition);
   private boolean prevOpen = false;
   private int screamTime = 0;

   public TileEntitySculkBoomer(BlockPos pos, BlockState state) {
      super(AMTileEntityRegistry.SCULK_BOOMER.get(), pos, state);
   }

   public static void commonTick(Level level, BlockPos pos, BlockState state, TileEntitySculkBoomer tileEntity) {
      boolean hasPower = false;
      if (state.getBlock() instanceof BlockSculkBoomer && !tileEntity.isRemoved()) {
         if (tileEntity.screamTime < 0 && !(Boolean)state.getValue(BlockSculkBoomer.POWERED)) {
            AABB screamBox = new AABB(pos.getX() - 4, pos.getY() - 0.25F, pos.getZ() - 4, pos.getX() + 4, pos.getY() + 0.25F, pos.getZ() + 4.0F);
            level.setBlockAndUpdate(pos, (BlockState)state.setValue(BlockSculkBoomer.OPEN, true));
            tileEntity.screamTime++;
            if (tileEntity.screamTime >= 0) {
               tileEntity.screamTime = 100;
               level.setBlockAndUpdate(pos, (BlockState)state.setValue(BlockSculkBoomer.OPEN, false));
            }

            float screamProgress = 1.0F - tileEntity.screamTime / -20.0F;
            Vec3 center = screamBox.getCenter();

            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, screamBox)) {
               double distance = 0.5 + entity.position().subtract(center).horizontalDistance();
               if (distance < 4.0F * screamProgress && distance > 3.5F * screamProgress && !isOccluded(level, Vec3.atCenterOf(pos), entity.position())) {
                  entity.hurt(entity.damageSources().magic(), 6 + entity.getRandom().nextInt(3));
                  AMCompat.knockback(entity, 0.4000000059604645, center.x - entity.getX(), center.z - entity.getZ());
               }
            }
         }

         if (tileEntity.screamTime > 0) {
            tileEntity.screamTime--;
         }

         boolean openNow = (Boolean)state.getValue(BlockSculkBoomer.OPEN);
         if (!tileEntity.prevOpen && openNow) {
            SoundEvent sound = AMSoundRegistry.SCULK_BOOMER.get();
            if (level.getRandom().nextInt(100) == 0) {
               sound = AMSoundRegistry.SCULK_BOOMER_FART.get();
            }

            level.playSound((Player)null, pos, sound, SoundSource.BLOCKS, 4.0F, level.getRandom().nextFloat() * 0.2F + 0.9F);
            level.addParticle((ParticleOptions)AMParticleRegistry.SKULK_BOOM.get(), pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0.0, 0.0, 0.0);
         }

         tileEntity.prevOpen = openNow;
      }
   }

   public void tick() {
   }

   protected void loadAdditional(CompoundTag tag, Provider provider) {
      super.loadAdditional(tag, provider);
      if (AMCompat.contains(tag, "ScreamCooldown", 99)) {
         this.screamTime = AMCompat.getInt(tag, "ScreamCooldown");
      }
   }

   protected void saveAdditional(CompoundTag tag, Provider provider) {
      super.saveAdditional(tag, provider);
      tag.putInt("ScreamCooldown", this.screamTime);
   }

   public PositionSource getListenerSource() {
      return this.blockPosSource;
   }

   public int getListenerRadius() {
      return 8;
   }

   public boolean handleGameEvent(ServerLevel serverLevel, Holder<GameEvent> event, Context message, Vec3 from) {
      if (event.value() == GameEvent.SCULK_SENSOR_TENDRILS_CLICKING.value() && !isOccluded(serverLevel, Vec3.atCenterOf(this.getBlockPos()), from)) {
         double distance = from.distanceTo(Vec3.atCenterOf(this.getBlockPos()));
         serverLevel.sendParticles(
            new VibrationParticleOption(new BlockPositionSource(this.getBlockPos()), Mth.floor(distance)), from.x, from.y, from.z, 1, 0.0, 0.0, 0.0, 0.0
         );
         if (this.screamTime == 0) {
            this.screamTime = -20;
         }
      }

      return false;
   }

   private static boolean isOccluded(Level level, Vec3 vec1, Vec3 vec2) {
      Vec3 vec3 = new Vec3(Mth.floor(vec1.x) + 0.5, Mth.floor(vec1.y) + 0.5, Mth.floor(vec1.z) + 0.5);
      Vec3 vec31 = new Vec3(Mth.floor(vec2.x) + 0.5, Mth.floor(vec2.y) + 0.5, Mth.floor(vec2.z) + 0.5);

      for (Direction direction : Direction.values()) {
         Vec3 vec32 = vec3.relative(direction, 9.999999747378752E-6);
         if (level.isBlockInLine(new ClipBlockStateContext(vec32, vec31, p_223780_ -> p_223780_.is(BlockTags.OCCLUDES_VIBRATION_SIGNALS))).getType()
            != Type.BLOCK) {
            return false;
         }
      }

      return true;
   }
}

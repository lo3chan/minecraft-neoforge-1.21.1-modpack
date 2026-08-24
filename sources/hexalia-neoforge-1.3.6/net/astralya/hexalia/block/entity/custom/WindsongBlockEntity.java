package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.HexaliaConfig;
import net.astralya.hexalia.block.custom.WindsongBlock;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class WindsongBlockEntity extends BlockEntity {
   private int activeTicks;
   private long activationTime = -1L;
   private int duration = 600;
   private int particleCooldown;

   public WindsongBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntityTypes.WINDSONG.get(), pos, state);
   }

   public void activate() {
      this.activate(HexaliaConfig.windsongDuration());
   }

   public void activate(int customDuration) {
      this.activeTicks = customDuration;
      this.duration = customDuration;
      if (this.level != null) {
         this.activationTime = this.level.getGameTime();
      }

      this.setChanged();
   }

   public boolean isActive() {
      return this.activeTicks > 0;
   }

   public float getProgress() {
      return this.duration <= 0 ? 0.0F : Math.min(1.0F, (float)(this.duration - this.activeTicks) / this.duration);
   }

   public static void tick(Level level, BlockPos pos, BlockState state, WindsongBlockEntity entity) {
      if (entity.activationTime != -1L) {
         long elapsed = level.getGameTime() - entity.activationTime;
         int expected = entity.duration - (int)elapsed;
         if (Math.abs(entity.activeTicks - expected) > 5) {
            entity.activeTicks = Math.max(0, expected);
         }
      }

      if (entity.isActive()) {
         entity.activeTicks--;
         if (level instanceof ServerLevel serverLevel) {
            int radius = HexaliaConfig.windsongEffectRadius();
            AABB area = new AABB(pos).inflate(radius);

            for (Entity projectile : serverLevel.getEntitiesOfClass(Entity.class, area, entityInArea -> entityInArea instanceof Projectile)) {
               if (!projectile.isRemoved()) {
                  discardProjectile(serverLevel, projectile);
               }
            }

            entity.emitParticles(serverLevel, pos);
         }

         if (entity.activeTicks <= 0) {
            level.playSound(null, pos, (SoundEvent)SoundEvents.WIND_CHARGE_BURST.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
            level.destroyBlock(pos, false);
         } else {
            if (state.hasProperty(WindsongBlock.ACTIVE) && !(Boolean)state.getValue(WindsongBlock.ACTIVE)) {
               level.setBlock(pos, (BlockState)state.setValue(WindsongBlock.ACTIVE, true), 3);
            }

            entity.setChanged();
         }
      }
   }

   private static void discardProjectile(ServerLevel level, Entity projectile) {
      level.playSound(null, projectile.getX(), projectile.getY(), projectile.getZ(), SoundEvents.BREEZE_DEFLECT, SoundSource.BLOCKS, 1.0F, 1.0F);
      Vec3 position = projectile.position();

      for (int i = 0; i < 5; i++) {
         double angle = level.random.nextDouble() * 2.0 * 3.141592653589793;
         double radius = level.random.nextDouble() * 0.5;
         double x = position.x + radius * Math.cos(angle);
         double z = position.z + radius * Math.sin(angle);
         double y = position.y + level.random.nextDouble() * 0.5;
         level.sendParticles(ParticleTypes.EFFECT, x, y, z, 1, 0.0, 0.0, 0.0, 0.1);
      }

      projectile.discard();
   }

   private void emitParticles(ServerLevel level, BlockPos pos) {
      if (this.particleCooldown > 0) {
         this.particleCooldown--;
      } else {
         Vec3 center = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
         float progress = this.getProgress();
         int particleCount = Math.max(1, (int)(3.0F * (1.0F - progress * 0.5F)));

         for (int i = 0; i < particleCount; i++) {
            double angle = level.random.nextDouble() * 2.0 * 3.141592653589793;
            double radius = level.random.nextDouble() * HexaliaConfig.windsongEffectRadius();
            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);
            double y = center.y + level.random.nextDouble() * 2.0;
            level.sendParticles(ParticleTypes.CLOUD, x, y, z, 1, 0.0, 0.0, 0.0, 0.1);
         }

         this.particleCooldown = 5;
      }
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putInt("ActiveTicks", this.activeTicks);
      tag.putLong("ActivationTime", this.activationTime);
      tag.putInt("Duration", this.duration);
      tag.putInt("ParticleCooldown", this.particleCooldown);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.activeTicks = tag.getInt("ActiveTicks");
      this.activationTime = tag.getLong("ActivationTime");
      this.duration = tag.contains("Duration") ? tag.getInt("Duration") : HexaliaConfig.windsongDuration();
      this.particleCooldown = tag.getInt("ParticleCooldown");
   }

   public CompoundTag getUpdateTag(Provider registries) {
      CompoundTag tag = super.getUpdateTag(registries);
      tag.putInt("ActiveTicks", this.activeTicks);
      tag.putLong("ActivationTime", this.activationTime);
      tag.putInt("Duration", this.duration);
      tag.putInt("ParticleCooldown", this.particleCooldown);
      return tag;
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }
}

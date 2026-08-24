package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.HexaliaConfig;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class GrimshadeBlockEntity extends BlockEntity {
   private int activeTicks;
   private long activationTime = -1L;

   public GrimshadeBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntityTypes.GRIMSHADE.get(), pos, state);
   }

   private static int duration() {
      return Math.max(1, HexaliaConfig.grimshadeDuration());
   }

   private static int effectRadius() {
      return Math.max(1, HexaliaConfig.grimshadeEffectRadius());
   }

   public void activate() {
      this.activeTicks = duration();
      if (this.level != null) {
         this.activationTime = this.level.getGameTime();
      }

      this.setChanged();
   }

   public boolean isActive() {
      return this.activeTicks > 0;
   }

   public static void tick(Level level, BlockPos pos, BlockState state, GrimshadeBlockEntity entity) {
      if (entity.activationTime != -1L) {
         long elapsed = level.getGameTime() - entity.activationTime;
         int expected = duration() - (int)elapsed;
         if (Math.abs(entity.activeTicks - expected) > 5) {
            entity.activeTicks = Math.max(0, expected);
         }
      }

      if (entity.isActive()) {
         entity.activeTicks--;
         if (level instanceof ServerLevel serverLevel) {
            if (serverLevel.getDifficulty() != Difficulty.PEACEFUL) {
               AABB area = new AABB(pos).inflate(effectRadius());

               for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class, area, targetx -> targetx.isAlive() && !(targetx instanceof Player))) {
                  target.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 0, true, true));
                  target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0, true, true));
               }
            }

            emitParticles(serverLevel, pos);
         }

         if (entity.activeTicks <= 0) {
            level.playSound(null, pos, SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.BLOCKS, 0.6F, 0.7F);
         }

         entity.setChanged();
      }
   }

   public void applyCollisionPing(LivingEntity entity) {
      if (this.level != null && !this.level.isClientSide && this.level.getDifficulty() != Difficulty.PEACEFUL && !(entity instanceof Player)) {
         entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 0, true, true));
         entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0, true, true));
      }
   }

   private static void emitParticles(ServerLevel level, BlockPos pos) {
      if (level.random.nextInt(3) == 0) {
         Vec3 center = new Vec3(pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5);
         RandomSource random = level.random;
         int count = 6 + random.nextInt(4);

         for (int i = 0; i < count; i++) {
            double xOffset = (random.nextDouble() - 0.5) * 0.6;
            double yOffset = random.nextDouble() * 0.5;
            double zOffset = (random.nextDouble() - 0.5) * 0.6;
            level.sendParticles(ParticleTypes.SMOKE, center.x + xOffset, center.y + yOffset, center.z + zOffset, 1, 0.0, 0.001, 0.0, 0.0);
         }
      }
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putInt("ActiveTicks", this.activeTicks);
      tag.putLong("ActivationTime", this.activationTime);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.activeTicks = tag.getInt("ActiveTicks");
      this.activationTime = tag.getLong("ActivationTime");
   }

   public CompoundTag getUpdateTag(Provider registries) {
      CompoundTag tag = super.getUpdateTag(registries);
      tag.putInt("ActiveTicks", this.activeTicks);
      tag.putLong("ActivationTime", this.activationTime);
      return tag;
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }
}

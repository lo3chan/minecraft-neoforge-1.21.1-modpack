package net.mehvahdjukaar.amendments.common.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.mehvahdjukaar.amendments.common.network.ClientBoundFireballExplodePacket;
import net.mehvahdjukaar.amendments.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.block.ILightable;
import net.mehvahdjukaar.moonlight.api.platform.ForgeHelper;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FireballExplosion extends Explosion {
   private final float maxDamage;
   private final boolean hasKnockback;
   private final int onFireSeconds;
   private final float soundVolume;
   private final Set<BlockPos> visitedBlock = new HashSet<>();

   public static FireballExplosion explodeServer(
      Level serverLevel,
      @Nullable Entity source,
      @Nullable DamageSource damageSource,
      @Nullable ExplosionDamageCalculator damageCalculator,
      double x,
      double y,
      double z,
      float radius,
      boolean fire,
      ExplosionInteraction explosionInteraction
   ) {
      return explodeServer(
         serverLevel, source, damageSource, damageCalculator, x, y, z, radius, fire, explosionInteraction, new FireballExplosion.ExtraSettings()
      );
   }

   public static FireballExplosion explodeServer(
      Level serverLevel,
      @Nullable Entity source,
      @Nullable DamageSource damageSource,
      @Nullable ExplosionDamageCalculator damageCalculator,
      double x,
      double y,
      double z,
      float radius,
      boolean fire,
      ExplosionInteraction explosionInteraction,
      FireballExplosion.ExtraSettings settings
   ) {
      FireballExplosion explosion = explode(
         serverLevel,
         source,
         damageSource,
         damageCalculator,
         x,
         y,
         z,
         radius,
         fire,
         explosionInteraction,
         false,
         ModRegistry.FIREBALL_EXPLOSION_SOUND,
         settings
      );
      if (serverLevel instanceof ServerLevel sl) {
         if (!explosion.interactsWithBlocks()) {
            explosion.clearToBlow();
         }

         for (ServerPlayer serverPlayer : sl.players()) {
            if (serverPlayer.distanceToSqr(x, y, z) < 4096.0) {
               NetworkHelper.sendToClientPlayer(
                  serverPlayer,
                  new ClientBoundFireballExplodePacket(
                     x,
                     y,
                     z,
                     radius,
                     explosion.getToBlow(),
                     (Vec3)explosion.getHitPlayers().get(serverPlayer),
                     explosion.getBlockInteraction(),
                     explosion.getSmallExplosionParticles(),
                     explosion.getLargeExplosionParticles(),
                     explosion.getExplosionSound(),
                     explosion.soundVolume
                  )
               );
            }
         }

         return explosion;
      } else {
         return explosion;
      }
   }

   public static FireballExplosion explode(
      Level level,
      @Nullable Entity source,
      @Nullable DamageSource damageSource,
      @Nullable ExplosionDamageCalculator damageCalculator,
      double x,
      double y,
      double z,
      float radius,
      boolean fire,
      ExplosionInteraction explosionInteraction,
      boolean spawnParticles,
      Holder<SoundEvent> explosionSound,
      FireballExplosion.ExtraSettings settings
   ) {
      FireballExplosion explosion = new FireballExplosion(
         level,
         source,
         damageSource,
         damageCalculator,
         x,
         y,
         z,
         radius,
         fire,
         switch (explosionInteraction) {
            case NONE -> BlockInteraction.KEEP;
            case BLOCK -> level.getDestroyType(GameRules.RULE_BLOCK_EXPLOSION_DROP_DECAY);
            case MOB -> level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
               ? level.getDestroyType(GameRules.RULE_MOB_EXPLOSION_DROP_DECAY)
               : BlockInteraction.KEEP;
            case TNT -> level.getDestroyType(GameRules.RULE_TNT_EXPLOSION_DROP_DECAY);
            case TRIGGER -> BlockInteraction.TRIGGER_BLOCK;
            default -> throw new MatchException(null, null);
         },
         ParticleTypes.EXPLOSION_EMITTER,
         ParticleTypes.EXPLOSION_EMITTER,
         explosionSound,
         settings
      );
      if (!ForgeHelper.onExplosionStart(level, explosion)) {
         explosion.explode();
         explosion.finalizeExplosion(spawnParticles);
      }

      return explosion;
   }

   public FireballExplosion(
      Level level,
      @Nullable Entity source,
      double toBlowX,
      double toBlowY,
      double toBlowZ,
      float radius,
      List<BlockPos> positions,
      BlockInteraction blockInteraction,
      ParticleOptions smallExplosionParticles,
      ParticleOptions largeExplosionParticles,
      Holder<SoundEvent> explosionSound,
      FireballExplosion.ExtraSettings settings
   ) {
      super(level, source, toBlowX, toBlowY, toBlowZ, radius, positions, blockInteraction, smallExplosionParticles, largeExplosionParticles, explosionSound);
      this.onFireSeconds = settings.onFireSeconds;
      this.maxDamage = settings.maxDamage;
      this.hasKnockback = settings.hasKnockback;
      this.soundVolume = settings.soundVolume;
   }

   public FireballExplosion(
      Level level,
      @Nullable Entity source,
      @Nullable DamageSource damageSource,
      @Nullable ExplosionDamageCalculator damageCalculator,
      double toBlowX,
      double toBlowY,
      double toBlowZ,
      float radius,
      boolean fire,
      BlockInteraction blockInteraction,
      ParticleOptions smallExplosionParticles,
      ParticleOptions largeExplosionParticles,
      Holder<SoundEvent> explosionSound,
      FireballExplosion.ExtraSettings settings
   ) {
      super(
         level,
         source,
         damageSource,
         damageCalculator,
         toBlowX,
         toBlowY,
         toBlowZ,
         radius,
         fire,
         blockInteraction,
         smallExplosionParticles,
         largeExplosionParticles,
         explosionSound
      );
      this.onFireSeconds = settings.onFireSeconds;
      this.maxDamage = settings.maxDamage;
      this.hasKnockback = settings.hasKnockback;
      this.soundVolume = settings.soundVolume;
   }

   public void explode() {
      super.explode();
      if (!this.hasKnockback) {
         this.getHitPlayers().clear();
      }
   }

   public void finalizeExplosion(boolean spawnParticles) {
      super.finalizeExplosion(false);
      if (spawnParticles) {
         this.level.addParticle((ParticleOptions)ModRegistry.FIREBALL_EMITTER_PARTICLE.get(), this.x, this.y, this.z, this.radius, 0.0, 0.0);
      }

      if (this.fire) {
         for (BlockPos pos : this.visitedBlock) {
            BlockState state = this.level.getBlockState(pos);
            if (state.getBlock() instanceof ILightable l) {
               l.lightableInteractWithEntity(this.level, state, this.getDirectSourceEntity(), pos);
            } else if (state.getBlock() == Blocks.AIR) {
               this.level.setBlockAndUpdate(pos, BaseFireBlock.getState(this.level, pos));
            }
         }
      }
   }

   public boolean hasKnockback() {
      return this.hasKnockback;
   }

   public boolean hurtHitEntity(Entity entity, DamageSource source, float amount) {
      if (this.maxDamage > 0.0F && amount > this.maxDamage) {
         amount = this.maxDamage;
      }

      int oldFire = entity.getRemainingFireTicks();
      entity.igniteForSeconds(this.onFireSeconds);
      if (!entity.hurt(source, amount)) {
         entity.setRemainingFireTicks(oldFire);
         return false;
      } else {
         return true;
      }
   }

   public void addVisitedBlock(BlockPos pos, BlockState state) {
      this.visitedBlock.add(pos);
   }

   public float getExplosionVolume() {
      return this.soundVolume;
   }

   public static class ExtraSettings {
      public float soundVolume = 4.0F;
      public float maxDamage = 3.4028235E38F;
      public int onFireSeconds = 0;
      public boolean hasKnockback = false;
   }
}

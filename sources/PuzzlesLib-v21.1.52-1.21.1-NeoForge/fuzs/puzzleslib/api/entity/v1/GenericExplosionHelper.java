package fuzs.puzzleslib.api.entity.v1;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.Key;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

@Deprecated
public final class GenericExplosionHelper {
   private GenericExplosionHelper() {
   }

   public static <T extends Explosion> T explode(
      GenericExplosionHelper.ExplosionFactory<T> factory,
      Level level,
      @Nullable Entity source,
      double x,
      double y,
      double z,
      float radius,
      ExplosionInteraction explosionInteraction
   ) {
      return explode(
         factory,
         level,
         source,
         Explosion.getDefaultDamageSource(level, source),
         null,
         x,
         y,
         z,
         radius,
         false,
         explosionInteraction,
         ParticleTypes.EXPLOSION,
         ParticleTypes.EXPLOSION_EMITTER,
         SoundEvents.GENERIC_EXPLODE
      );
   }

   public static <T extends Explosion> T explode(
      GenericExplosionHelper.ExplosionFactory<T> factory,
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
      ParticleOptions smallExplosionParticles,
      ParticleOptions largeExplosionParticles,
      Holder<SoundEvent> explosionSound
   ) {
      T explosion = explode(
         factory,
         level,
         source,
         damageSource,
         damageCalculator,
         x,
         y,
         z,
         radius,
         fire,
         explosionInteraction,
         level.isClientSide,
         smallExplosionParticles,
         largeExplosionParticles,
         explosionSound
      );
      if (!level.isClientSide) {
         if (!explosion.interactsWithBlocks()) {
            explosion.clearToBlow();
         }

         for (ServerPlayer serverplayer : ((ServerLevel)level).players()) {
            if (serverplayer.distanceToSqr(x, y, z) < 4096.0) {
               serverplayer.connection
                  .send(
                     new ClientboundExplodePacket(
                        x,
                        y,
                        z,
                        radius,
                        explosion.getToBlow(),
                        (Vec3)explosion.getHitPlayers().get(serverplayer),
                        explosion.getBlockInteraction(),
                        explosion.getSmallExplosionParticles(),
                        explosion.getLargeExplosionParticles(),
                        explosion.getExplosionSound()
                     )
                  );
            }
         }
      }

      return explosion;
   }

   private static <T extends Explosion> T explode(
      GenericExplosionHelper.ExplosionFactory<T> factory,
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
      ParticleOptions smallExplosionParticles,
      ParticleOptions largeExplosionParticles,
      Holder<SoundEvent> explosionSound
   ) {
      T explosion = factory.create(
         level,
         source,
         damageSource,
         damageCalculator,
         x,
         y,
         z,
         radius,
         fire,
         getBlockInteraction(level, source, explosionInteraction),
         smallExplosionParticles,
         largeExplosionParticles,
         explosionSound
      );
      if (CommonAbstractions.INSTANCE.onExplosionStart(level, explosion)) {
         return explosion;
      } else {
         explosion.explode();
         explosion.finalizeExplosion(spawnParticles);
         return explosion;
      }
   }

   private static BlockInteraction getBlockInteraction(Level level, @Nullable Entity source, ExplosionInteraction explosionInteraction) {
      return switch (explosionInteraction) {
         case NONE -> BlockInteraction.KEEP;
         case BLOCK -> getDestroyType(level.getGameRules(), GameRules.RULE_BLOCK_EXPLOSION_DROP_DECAY);
         case MOB -> CommonAbstractions.INSTANCE.getMobGriefingRule(level, source)
            ? getDestroyType(level.getGameRules(), GameRules.RULE_MOB_EXPLOSION_DROP_DECAY)
            : BlockInteraction.KEEP;
         case TNT -> getDestroyType(level.getGameRules(), GameRules.RULE_TNT_EXPLOSION_DROP_DECAY);
         case TRIGGER -> BlockInteraction.TRIGGER_BLOCK;
         default -> throw new MatchException(null, null);
      };
   }

   private static BlockInteraction getDestroyType(GameRules gameRules, Key<BooleanValue> gameRule) {
      return gameRules.getBoolean(gameRule) ? BlockInteraction.DESTROY_WITH_DECAY : BlockInteraction.DESTROY;
   }

   public static <T extends Explosion> T explode(
      GenericExplosionHelper.ExplosionFactory<T> factory,
      Level level,
      @Nullable Entity source,
      double x,
      double y,
      double z,
      float radius,
      boolean fire,
      ExplosionInteraction explosionInteraction
   ) {
      return explode(
         factory,
         level,
         source,
         Explosion.getDefaultDamageSource(level, source),
         null,
         x,
         y,
         z,
         radius,
         fire,
         explosionInteraction,
         ParticleTypes.EXPLOSION,
         ParticleTypes.EXPLOSION_EMITTER,
         SoundEvents.GENERIC_EXPLODE
      );
   }

   public static <T extends Explosion> T explode(
      GenericExplosionHelper.ExplosionFactory<T> factory,
      Level level,
      @Nullable Entity source,
      @Nullable DamageSource damageSource,
      @Nullable ExplosionDamageCalculator damageCalculator,
      Vec3 pos,
      float radius,
      boolean fire,
      ExplosionInteraction explosionInteraction
   ) {
      return explode(
         factory,
         level,
         source,
         damageSource,
         damageCalculator,
         pos.x(),
         pos.y(),
         pos.z(),
         radius,
         fire,
         explosionInteraction,
         ParticleTypes.EXPLOSION,
         ParticleTypes.EXPLOSION_EMITTER,
         SoundEvents.GENERIC_EXPLODE
      );
   }

   public static <T extends Explosion> T explode(
      GenericExplosionHelper.ExplosionFactory<T> factory,
      Level level,
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
      return explode(
         factory,
         level,
         source,
         damageSource,
         damageCalculator,
         x,
         y,
         z,
         radius,
         fire,
         explosionInteraction,
         ParticleTypes.EXPLOSION,
         ParticleTypes.EXPLOSION_EMITTER,
         SoundEvents.GENERIC_EXPLODE
      );
   }

   @FunctionalInterface
   public interface ExplosionFactory<T extends Explosion> {
      T create(
         Level var1,
         @Nullable Entity var2,
         @Nullable DamageSource var3,
         @Nullable ExplosionDamageCalculator var4,
         double var5,
         double var7,
         double var9,
         float var11,
         boolean var12,
         BlockInteraction var13,
         ParticleOptions var14,
         ParticleOptions var15,
         Holder<SoundEvent> var16
      );
   }
}

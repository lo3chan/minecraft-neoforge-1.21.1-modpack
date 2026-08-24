package com.aetherteam.aether.entity.monster;

import com.aetherteam.aether.client.particle.AetherParticleTypes;
import com.aetherteam.aether.loot.AetherLoot;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootTable;

public class EvilWhirlwind extends AbstractWhirlwind {
   public EvilWhirlwind(EntityType<? extends EvilWhirlwind> type, Level level) {
      super(type, level);
      this.setEvil(true);
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData) {
      this.setLifeLeft((this.getRandom().nextInt(512) + 512) / 2);
      return spawnData;
   }

   @Override
   public void spawnParticles() {
      for (int i = 0; i < 3; i++) {
         double d2 = this.getX() + this.getRandom().nextDouble() * 0.25;
         double d5 = this.getY() + this.getBbHeight() + 0.125;
         double d8 = this.getZ() + this.getRandom().nextDouble() * 0.25;
         float f1 = this.getRandom().nextFloat() * 360.0F;
         this.level()
            .addParticle(
               (ParticleOptions)AetherParticleTypes.EVIL_WHIRLWIND.get(),
               d2,
               d5 - 0.25,
               d8,
               -Math.sin(0.0175F * f1) * 0.75,
               0.125,
               Math.cos(0.0175F * f1) * 0.75
            );
      }
   }

   @Override
   public ResourceKey<LootTable> getLootLocation() {
      return AetherLoot.EVIL_WHIRLWIND_JUNK;
   }

   @Override
   public int getDefaultColor() {
      return 0;
   }
}

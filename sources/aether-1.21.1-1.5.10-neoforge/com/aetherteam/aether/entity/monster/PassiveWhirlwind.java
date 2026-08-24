package com.aetherteam.aether.entity.monster;

import com.aetherteam.aether.client.particle.AetherParticleTypes;
import com.aetherteam.aether.loot.AetherLoot;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootTable;

public class PassiveWhirlwind extends AbstractWhirlwind {
   public PassiveWhirlwind(EntityType<? extends PassiveWhirlwind> type, Level level) {
      super(type, level);
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData) {
      this.setLifeLeft(this.getRandom().nextInt(512) + 512);
      return spawnData;
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemStack = player.getItemInHand(hand);
      if (itemStack.getItem() instanceof DyeItem dyeItem && player.isCreative()) {
         this.setColorData(dyeItem.getDyeColor().getMapColor().col);
         return InteractionResult.SUCCESS;
      } else {
         return super.mobInteract(player, hand);
      }
   }

   @Override
   public void spawnParticles() {
      for (int i = 0; i < 2; i++) {
         double d1 = this.getX() + this.getRandom().nextDouble() * 0.25;
         double d4 = this.getY() + this.getBbHeight() + 0.125;
         double d7 = this.getZ() + this.getRandom().nextDouble() * 0.25;
         float f = this.getRandom().nextFloat() * 360.0F;
         this.level()
            .addParticle(
               (ParticleOptions)AetherParticleTypes.PASSIVE_WHIRLWIND.get(),
               d1,
               d4 - 0.25,
               d7,
               -Math.sin(0.0175F * f) * 0.75,
               0.125,
               Math.cos(0.0175F * f) * 0.75
            );
      }
   }

   @Override
   public ResourceKey<LootTable> getLootLocation() {
      return AetherLoot.WHIRLWIND_JUNK;
   }

   @Override
   public int getDefaultColor() {
      return 16777215;
   }
}

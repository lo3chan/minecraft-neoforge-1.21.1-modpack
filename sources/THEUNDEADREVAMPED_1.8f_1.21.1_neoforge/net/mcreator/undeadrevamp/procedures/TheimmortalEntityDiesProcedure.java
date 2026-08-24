package net.mcreator.undeadrevamp.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class TheimmortalEntityDiesProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (sourceentity instanceof IronGolem) {
            entity.getPersistentData().putDouble("nore", 1.0);
         } else if (entity.getPersistentData().getDouble("burned") == 1.0) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 100, 20, false, false));
            }

            if (world instanceof ServerLevel _level) {
               _level.sendParticles(ParticleTypes.ASH, x, y, z, 400, entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth(), 0.5);
            }

            if (world instanceof ServerLevel _level) {
               _level.sendParticles(ParticleTypes.LAVA, x, y, z, 15, entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth(), 0.5);
            }

            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.skeleton.death")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.skeleton.death")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof Level _levelx) {
               if (!_levelx.isClientSide()) {
                  _levelx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.fire.extinguish")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.fire.extinguish")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }
         }

         if (entity.getPersistentData().getDouble("decored") == 1.0) {
            world.levelEvent(2001, BlockPos.containing(x, y, z), Block.getId(Blocks.GOLD_BLOCK.defaultBlockState()));
            world.levelEvent(2001, BlockPos.containing(x, y, z), Block.getId(Blocks.GOLD_BLOCK.defaultBlockState()));
            world.levelEvent(2001, BlockPos.containing(x, y, z), Block.getId(Blocks.GOLD_BLOCK.defaultBlockState()));
            world.levelEvent(2001, BlockPos.containing(x, y + 1.0, z), Block.getId(Blocks.GOLD_BLOCK.defaultBlockState()));
            world.levelEvent(2001, BlockPos.containing(x, y + 1.0, z), Block.getId(Blocks.DIAMOND_BLOCK.defaultBlockState()));
            if (world instanceof ServerLevel _levelxx) {
               ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack(Items.DIAMOND));
               entityToSpawn.setPickUpDelay(10);
               _levelxx.addFreshEntity(entityToSpawn);
            }

            if (world instanceof ServerLevel _levelxx) {
               ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack(Items.DIAMOND));
               entityToSpawn.setPickUpDelay(10);
               _levelxx.addFreshEntity(entityToSpawn);
            }

            if (world instanceof ServerLevel _levelxx) {
               ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack(Items.GOLD_INGOT));
               entityToSpawn.setPickUpDelay(10);
               _levelxx.addFreshEntity(entityToSpawn);
            }

            if (world instanceof ServerLevel _levelxx) {
               ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack(Items.GOLD_INGOT));
               entityToSpawn.setPickUpDelay(10);
               _levelxx.addFreshEntity(entityToSpawn);
            }

            if (world instanceof ServerLevel _levelxx) {
               ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack(Items.GOLD_INGOT));
               entityToSpawn.setPickUpDelay(10);
               _levelxx.addFreshEntity(entityToSpawn);
            }

            if (world instanceof ServerLevel _levelxx) {
               ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack(Items.GOLD_INGOT));
               entityToSpawn.setPickUpDelay(10);
               _levelxx.addFreshEntity(entityToSpawn);
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 100, 20, false, false));
            }

            if (world instanceof Level _levelxx) {
               if (!_levelxx.isClientSide()) {
                  _levelxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.amethyst_block.break")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.amethyst_block.break")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }
         }
      }
   }
}

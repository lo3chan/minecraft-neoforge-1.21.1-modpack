package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModBlocks;
import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class TransmutingElixirPriShchielchkiePKMPoBlokuProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, ItemStack itemstack) {
      if (world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == BornInChaosV1ModBlocks.INFERNAL_EVIL_PUMPKIN.get()
         || world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == BornInChaosV1ModBlocks.INFERNAL_EVIL_PUMPKIN_S.get()) {
         itemstack.shrink(1);
         world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
         if (world instanceof ServerLevel _level) {
            Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.LORD_PUMPKINHEAD.get())
               .spawn(_level, BlockPos.containing(x + 0.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
            }
         }

         if (world instanceof Level _levelx) {
            if (!_levelx.isClientSide()) {
               _levelx.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:zombie_clown_attack")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _levelx.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:zombie_clown_attack")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }

         if (world instanceof Level _levelxx) {
            if (!_levelxx.isClientSide()) {
               _levelxx.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.wither.spawn")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  0.8F
               );
            } else {
               _levelxx.playLocalSound(
                  x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.wither.spawn")), SoundSource.NEUTRAL, 1.0F, 0.8F, false
               );
            }
         }

         if (world instanceof ServerLevel _levelxxx) {
            _levelxxx.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.INTOXICATIND_BOMB_PART.get(), x + 0.5, y + 0.5, z + 0.5, 8, 0.3, 0.3, 0.3, 0.1
            );
         }

         if (world instanceof ServerLevel _levelxxx) {
            _levelxxx.sendParticles(ParticleTypes.POOF, x + 0.5, y + 0.5, z + 0.5, 20, 0.4, 0.8, 0.4, 0.1);
         }
      } else if (world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == Blocks.MAGMA_BLOCK) {
         itemstack.shrink(1);
         BlockPos _bp = BlockPos.containing(x, y, z);
         BlockState _bs = ((Block)BornInChaosV1ModBlocks.FEL_SOIL.get()).defaultBlockState();
         BlockState _bso = world.getBlockState(_bp);

         for (Property<?> _propertyOld : _bso.getProperties()) {
            Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOld.getName());
            if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
               try {
                  _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOld));
               } catch (Exception var20) {
               }
            }
         }

         world.setBlock(_bp, _bs, 3);
         if (world instanceof Level _levelxxx) {
            if (!_levelxxx.isClientSide()) {
               _levelxxx.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:zombie_clown_attack")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _levelxxx.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:zombie_clown_attack")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }

         if (world instanceof ServerLevel _levelxxxx) {
            _levelxxxx.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.INTOXICATIND_BOMB_PART.get(), x + 0.5, y + 0.5, z + 0.5, 10, 0.3, 0.3, 0.3, 0.1
            );
         }
      } else if (world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == Blocks.RAW_GOLD_BLOCK) {
         itemstack.shrink(1);
         BlockPos _bp = BlockPos.containing(x, y, z);
         BlockState _bs = Blocks.ANCIENT_DEBRIS.defaultBlockState();
         BlockState _bso = world.getBlockState(_bp);

         for (Property<?> _propertyOldx : _bso.getProperties()) {
            Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOldx.getName());
            if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
               try {
                  _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOldx));
               } catch (Exception var19) {
               }
            }
         }

         world.setBlock(_bp, _bs, 3);
         if (world instanceof Level _levelxxxx) {
            if (!_levelxxxx.isClientSide()) {
               _levelxxxx.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:zombie_clown_attack")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _levelxxxx.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:zombie_clown_attack")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }

         if (world instanceof ServerLevel _levelxxxxx) {
            _levelxxxxx.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.INTOXICATIND_BOMB_PART.get(), x + 0.5, y + 0.5, z + 0.5, 10, 0.3, 0.3, 0.3, 0.1
            );
         }
      } else if (world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == Blocks.OBSIDIAN) {
         itemstack.shrink(1);
         BlockPos _bp = BlockPos.containing(x, y, z);
         BlockState _bs = Blocks.CRYING_OBSIDIAN.defaultBlockState();
         BlockState _bso = world.getBlockState(_bp);

         for (Property<?> _propertyOldxx : _bso.getProperties()) {
            Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOldxx.getName());
            if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
               try {
                  _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOldxx));
               } catch (Exception var18) {
               }
            }
         }

         world.setBlock(_bp, _bs, 3);
         if (world instanceof Level _levelxxxxx) {
            if (!_levelxxxxx.isClientSide()) {
               _levelxxxxx.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:zombie_clown_attack")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _levelxxxxx.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:zombie_clown_attack")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }

         if (world instanceof ServerLevel _levelxxxxxx) {
            _levelxxxxxx.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.INTOXICATIND_BOMB_PART.get(), x + 0.5, y + 0.5, z + 0.5, 10, 0.3, 0.3, 0.3, 0.1
            );
         }
      } else if (world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == Blocks.AMETHYST_BLOCK) {
         itemstack.shrink(1);
         BlockPos _bp = BlockPos.containing(x, y, z);
         BlockState _bs = Blocks.DIAMOND_BLOCK.defaultBlockState();
         BlockState _bso = world.getBlockState(_bp);

         for (Property<?> _propertyOldxxx : _bso.getProperties()) {
            Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOldxxx.getName());
            if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
               try {
                  _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOldxxx));
               } catch (Exception var17) {
               }
            }
         }

         world.setBlock(_bp, _bs, 3);
         if (world instanceof Level _levelxxxxxx) {
            if (!_levelxxxxxx.isClientSide()) {
               _levelxxxxxx.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:zombie_clown_attack")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _levelxxxxxx.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:zombie_clown_attack")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }

         if (world instanceof ServerLevel _levelxxxxxxx) {
            _levelxxxxxxx.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.INTOXICATIND_BOMB_PART.get(), x + 0.5, y + 0.5, z + 0.5, 10, 0.3, 0.3, 0.3, 0.1
            );
         }
      } else if (world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == Blocks.SLIME_BLOCK) {
         itemstack.shrink(1);
         BlockPos _bp = BlockPos.containing(x, y, z);
         BlockState _bs = Blocks.HONEY_BLOCK.defaultBlockState();
         BlockState _bso = world.getBlockState(_bp);

         for (Property<?> _propertyOldxxxx : _bso.getProperties()) {
            Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOldxxxx.getName());
            if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
               try {
                  _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOldxxxx));
               } catch (Exception var16) {
               }
            }
         }

         world.setBlock(_bp, _bs, 3);
         if (world instanceof Level _levelxxxxxxx) {
            if (!_levelxxxxxxx.isClientSide()) {
               _levelxxxxxxx.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:zombie_clown_attack")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _levelxxxxxxx.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:zombie_clown_attack")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }

         if (world instanceof ServerLevel _levelxxxxxxxx) {
            _levelxxxxxxxx.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.INTOXICATIND_BOMB_PART.get(), x + 0.5, y + 0.5, z + 0.5, 10, 0.3, 0.3, 0.3, 0.1
            );
         }
      } else if (world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == Blocks.IRON_BLOCK) {
         itemstack.shrink(1);
         BlockPos _bp = BlockPos.containing(x, y, z);
         BlockState _bs = ((Block)BornInChaosV1ModBlocks.DARK_METAL_BLOCK.get()).defaultBlockState();
         BlockState _bso = world.getBlockState(_bp);

         for (Property<?> _propertyOldxxxxx : _bso.getProperties()) {
            Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOldxxxxx.getName());
            if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
               try {
                  _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOldxxxxx));
               } catch (Exception var15) {
               }
            }
         }

         world.setBlock(_bp, _bs, 3);
         if (world instanceof Level _levelxxxxxxxx) {
            if (!_levelxxxxxxxx.isClientSide()) {
               _levelxxxxxxxx.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:zombie_clown_attack")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _levelxxxxxxxx.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:zombie_clown_attack")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }

         if (world instanceof ServerLevel _levelxxxxxxxxx) {
            _levelxxxxxxxxx.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.INTOXICATIND_BOMB_PART.get(), x + 0.5, y + 0.5, z + 0.5, 10, 0.3, 0.3, 0.3, 0.1
            );
         }
      }
   }
}

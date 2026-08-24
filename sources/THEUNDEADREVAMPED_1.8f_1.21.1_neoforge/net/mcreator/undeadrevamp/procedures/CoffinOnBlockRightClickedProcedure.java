package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModBlocks;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class CoffinOnBlockRightClickedProcedure {
   public static void execute(final LevelAccessor world, double x, double y, double z) {
      if ((new Object() {
         public double getValue(LevelAccessor world, BlockPos pos, String tag) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            return blockEntity != null ? blockEntity.getPersistentData().getDouble(tag) : -1.0;
         }
      }).getValue(world, BlockPos.containing(x, y, z), "open") == 0.0) {
         UndeadRevamp2Mod.queueServerWork(25, () -> {
            if (!world.isClientSide()) {
               BlockPos _bp = BlockPos.containing(x, y, z);
               BlockEntity _blockEntity = world.getBlockEntity(_bp);
               BlockState _bs = world.getBlockState(_bp);
               if (_blockEntity != null) {
                  _blockEntity.getPersistentData().putDouble("swicth", 1.0);
               }

               if (world instanceof Level _levelx) {
                  _levelx.sendBlockUpdated(_bp, _bs, _bs, 3);
               }
            }
         });
         if ((new Object() {
            public double getValue(LevelAccessor world, BlockPos pos, String tag) {
               BlockEntity blockEntity = world.getBlockEntity(pos);
               return blockEntity != null ? blockEntity.getPersistentData().getDouble(tag) : -1.0;
            }
         }).getValue(world, BlockPos.containing(x, y, z), "swicth") != 1.0 && world instanceof Level _level) {
            if (!_level.isClientSide()) {
               _level.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:slam_coffin")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _level.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:slam_coffin")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }

         if (!world.isClientSide()) {
            BlockPos _bp = BlockPos.containing(x, y, z);
            BlockEntity _blockEntity = world.getBlockEntity(_bp);
            BlockState _bs = world.getBlockState(_bp);
            if (_blockEntity != null) {
               _blockEntity.getPersistentData().putDouble("open", 1.0);
            }

            if (world instanceof Level _levelx) {
               _levelx.sendBlockUpdated(_bp, _bs, _bs, 3);
            }
         }

         int _value = 1;
         BlockPos _pos = BlockPos.containing(x, y, z);
         BlockState _bsx = world.getBlockState(_pos);
         if (_bsx.getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty _integerProp
            && _integerProp.getPossibleValues().contains(_value)) {
            world.setBlock(_pos, (BlockState)_bsx.setValue(_integerProp, _value), 3);
         }
      } else if (world instanceof Level _levelx) {
         if (!_levelx.isClientSide()) {
            _levelx.playSound(
               null,
               BlockPos.containing(x, y, z),
               (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.stone.hit")),
               SoundSource.NEUTRAL,
               1.0F,
               1.0F
            );
         } else {
            _levelx.playLocalSound(
               x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.stone.hit")), SoundSource.NEUTRAL, 1.0F, 1.0F, false
            );
         }
      }

      if ((new Object() {
         public double getValue(LevelAccessor world, BlockPos pos, String tag) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            return blockEntity != null ? blockEntity.getPersistentData().getDouble(tag) : -1.0;
         }
      }).getValue(world, BlockPos.containing(x, y, z), "swicth") == 1.0) {
         if (!world.isClientSide()) {
            BlockPos _bpx = BlockPos.containing(x, y, z);
            BlockEntity _blockEntityx = world.getBlockEntity(_bpx);
            BlockState _bsx = world.getBlockState(_bpx);
            if (_blockEntityx != null) {
               _blockEntityx.getPersistentData().putDouble("swicth", 3.0);
            }

            if (world instanceof Level _levelxx) {
               _levelxx.sendBlockUpdated(_bpx, _bsx, _bsx, 3);
            }
         }

         for (int index0 = 0; index0 < 12; index0++) {
            if (world instanceof Level _levelxx) {
               if (!_levelxx.isClientSide()) {
                  _levelxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.item.pickup")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.item.pickup")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            if ((new Object() {
                     public Direction getDirection(BlockPos pos) {
                        BlockState _bsxx = world.getBlockState(pos);
                        Property<?> property = _bsxx.getBlock().getStateDefinition().getProperty("facing");
                        if (property != null && _bsxx.getValue(property) instanceof Direction _dir) {
                           return _dir;
                        } else if (_bsxx.hasProperty(BlockStateProperties.AXIS)) {
                           return Direction.fromAxisAndDirection((Axis)_bsxx.getValue(BlockStateProperties.AXIS), AxisDirection.POSITIVE);
                        } else {
                           return _bsxx.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)
                              ? Direction.fromAxisAndDirection((Axis)_bsxx.getValue(BlockStateProperties.HORIZONTAL_AXIS), AxisDirection.POSITIVE)
                              : Direction.NORTH;
                        }
                     }
                  })
                  .getDirection(BlockPos.containing(x, y, z))
               == Direction.SOUTH) {
               if (world instanceof ServerLevel _levelxxx) {
                  _levelxxx.addFreshEntity(new ExperienceOrb(_levelxxx, x, y + 1.0, z + 1.0, 1));
               }

               if (Math.random() < 0.2) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z + 1.2, new ItemStack(Items.IRON_INGOT));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.19) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z + 1.2, new ItemStack(Items.GOLD_INGOT));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.18) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z + 1.2, new ItemStack(Items.DIAMOND));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.17) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z + 1.2, new ItemStack(Items.COPPER_INGOT));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.16) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z + 1.2, new ItemStack(Blocks.GOLD_BLOCK));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.18) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(
                        _levelxxx, x, y + 1.0, z + 1.2, new ItemStack((ItemLike)UndeadRevamp2ModBlocks.COFFINBROAD.get())
                     );
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.3) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z + 1.2, new ItemStack(Items.GLOW_BERRIES));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.35) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z + 1.2, new ItemStack(Blocks.SMOOTH_STONE));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.2) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(
                        _levelxxx, x, y + 1.0, z + 1.2, new ItemStack((ItemLike)UndeadRevamp2ModItems.BOSTROXINGOT.get())
                     );
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.2) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z + 1.2, new ItemStack(Blocks.SOUL_LANTERN));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.05) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z + 1.2, new ItemStack(Blocks.PIGLIN_HEAD));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.1) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(
                        _levelxxx, x, y + 1.0, z + 1.2, new ItemStack((ItemLike)UndeadRevamp2ModItems.CLOGGERUPGRADE.get())
                     );
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.1) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z + 1.2, new ItemStack(Items.SPIDER_EYE));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.1) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(
                        _levelxxx, x, y + 1.0, z + 1.2, new ItemStack((ItemLike)UndeadRevamp2ModBlocks.COFFINBROAD.get())
                     );
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.25) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(
                        _levelxxx, x, y + 1.0, z + 1.2, new ItemStack((ItemLike)UndeadRevamp2ModItems.THE_SOMNOLENCEEXTRACT.get())
                     );
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.005) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z + 1.2, new ItemStack(Blocks.BEDROCK));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.6) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z + 1.2, new ItemStack(Items.SKULL_POTTERY_SHERD));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.02) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z + 1.2, new ItemStack(Items.DRAGON_BREATH));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.03) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z + 1.2, new ItemStack(Blocks.COBWEB));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.4) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z + 1.2, new ItemStack((ItemLike)UndeadRevamp2ModItems.HEAVYTOOTH.get()));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.5) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z + 1.2, new ItemStack(Items.DIAMOND));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.25) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(
                        _levelxxx, x, y + 1.0, z + 1.2, new ItemStack((ItemLike)UndeadRevamp2ModItems.CLOGGERUPGRADE.get())
                     );
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (world instanceof ServerLevel _levelxxx) {
                  ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z + 1.2, new ItemStack(Items.AMETHYST_SHARD));
                  entityToSpawn.setPickUpDelay(10);
                  _levelxxx.addFreshEntity(entityToSpawn);
               }
            } else if ((new Object() {
                     public Direction getDirection(BlockPos pos) {
                        BlockState _bsxx = world.getBlockState(pos);
                        Property<?> property = _bsxx.getBlock().getStateDefinition().getProperty("facing");
                        if (property != null && _bsxx.getValue(property) instanceof Direction _dir) {
                           return _dir;
                        } else if (_bsxx.hasProperty(BlockStateProperties.AXIS)) {
                           return Direction.fromAxisAndDirection((Axis)_bsxx.getValue(BlockStateProperties.AXIS), AxisDirection.POSITIVE);
                        } else {
                           return _bsxx.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)
                              ? Direction.fromAxisAndDirection((Axis)_bsxx.getValue(BlockStateProperties.HORIZONTAL_AXIS), AxisDirection.POSITIVE)
                              : Direction.NORTH;
                        }
                     }
                  })
                  .getDirection(BlockPos.containing(x, y, z))
               == Direction.NORTH) {
               if (world instanceof ServerLevel _levelxxx) {
                  _levelxxx.addFreshEntity(new ExperienceOrb(_levelxxx, x, y + 1.0, z, 1));
               }

               if (Math.random() < 0.2) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z, new ItemStack(Items.IRON_INGOT));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.19) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z, new ItemStack(Items.GOLD_INGOT));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.18) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z, new ItemStack(Items.DIAMOND));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.17) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z, new ItemStack(Items.COPPER_INGOT));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.16) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z, new ItemStack(Blocks.GOLD_BLOCK));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.18) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z, new ItemStack((ItemLike)UndeadRevamp2ModBlocks.COFFINBROAD.get()));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.3) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z, new ItemStack(Items.GLOW_BERRIES));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.35) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z, new ItemStack(Blocks.SMOOTH_STONE));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.2) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z, new ItemStack((ItemLike)UndeadRevamp2ModItems.BOSTROXINGOT.get()));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.2) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z, new ItemStack(Blocks.SOUL_LANTERN));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.05) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z, new ItemStack(Blocks.PIGLIN_HEAD));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.1) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z, new ItemStack(Items.DIAMOND));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.1) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z, new ItemStack(Items.SPIDER_EYE));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.1) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z, new ItemStack((ItemLike)UndeadRevamp2ModBlocks.COFFINBROAD.get()));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.25) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(
                        _levelxxx, x, y + 1.0, z, new ItemStack((ItemLike)UndeadRevamp2ModItems.THE_SOMNOLENCEEXTRACT.get())
                     );
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.01) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z, new ItemStack(Blocks.BEDROCK));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.6) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z, new ItemStack(Items.SKULL_POTTERY_SHERD));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.03) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z, new ItemStack(Items.DRAGON_BREATH));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.03) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z, new ItemStack(Blocks.COBWEB));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.4) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z, new ItemStack((ItemLike)UndeadRevamp2ModItems.HEAVYTOOTH.get()));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.5) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z, new ItemStack(Items.DIAMOND));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (world instanceof ServerLevel _levelxxx) {
                  ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x, y + 1.0, z, new ItemStack(Items.AMETHYST_SHARD));
                  entityToSpawn.setPickUpDelay(10);
                  _levelxxx.addFreshEntity(entityToSpawn);
               }
            } else if ((new Object() {
                     public Direction getDirection(BlockPos pos) {
                        BlockState _bsxx = world.getBlockState(pos);
                        Property<?> property = _bsxx.getBlock().getStateDefinition().getProperty("facing");
                        if (property != null && _bsxx.getValue(property) instanceof Direction _dir) {
                           return _dir;
                        } else if (_bsxx.hasProperty(BlockStateProperties.AXIS)) {
                           return Direction.fromAxisAndDirection((Axis)_bsxx.getValue(BlockStateProperties.AXIS), AxisDirection.POSITIVE);
                        } else {
                           return _bsxx.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)
                              ? Direction.fromAxisAndDirection((Axis)_bsxx.getValue(BlockStateProperties.HORIZONTAL_AXIS), AxisDirection.POSITIVE)
                              : Direction.NORTH;
                        }
                     }
                  })
                  .getDirection(BlockPos.containing(x, y, z))
               == Direction.DOWN) {
               if (world instanceof ServerLevel _levelxxx) {
                  _levelxxx.addFreshEntity(new ExperienceOrb(_levelxxx, x + 1.2, y + 1.0, z, 1));
               }

               if (Math.random() < 0.2) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x + 1.2, y + 1.0, z, new ItemStack(Items.IRON_INGOT));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.19) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x + 1.2, y + 1.0, z, new ItemStack(Items.GOLD_INGOT));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.18) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x + 1.2, y + 1.0, z, new ItemStack(Items.DIAMOND));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.17) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x + 1.2, y + 1.0, z, new ItemStack(Items.COPPER_INGOT));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.16) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x + 1.2, y + 1.0, z, new ItemStack(Blocks.GOLD_BLOCK));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.18) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(
                        _levelxxx, x + 1.2, y + 1.0, z, new ItemStack((ItemLike)UndeadRevamp2ModBlocks.COFFINBROAD.get())
                     );
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.3) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x + 1.2, y + 1.0, z, new ItemStack(Items.GLOW_BERRIES));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.35) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x + 1.2, y + 1.0, z, new ItemStack(Blocks.SMOOTH_STONE));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.2) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(
                        _levelxxx, x + 1.2, y + 1.0, z, new ItemStack((ItemLike)UndeadRevamp2ModItems.BOSTROXINGOT.get())
                     );
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.2) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x + 1.2, y + 1.0, z, new ItemStack(Blocks.SOUL_LANTERN));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.05) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x + 1.2, y + 1.0, z, new ItemStack(Blocks.PIGLIN_HEAD));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.1) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x + 1.2, y + 1.0, z, new ItemStack(Items.DIAMOND));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.1) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x + 1.2, y + 1.0, z, new ItemStack(Items.SPIDER_EYE));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.1) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(
                        _levelxxx, x + 1.2, y + 1.0, z, new ItemStack((ItemLike)UndeadRevamp2ModBlocks.COFFINBROAD.get())
                     );
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.25) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(
                        _levelxxx, x + 1.2, y + 1.0, z, new ItemStack((ItemLike)UndeadRevamp2ModItems.THE_SOMNOLENCEEXTRACT.get())
                     );
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.01) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x + 1.2, y + 1.0, z, new ItemStack(Blocks.BEDROCK));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.6) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x + 1.2, y + 1.0, z, new ItemStack(Items.SKULL_POTTERY_SHERD));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.03) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x + 1.2, y + 1.0, z, new ItemStack(Items.DRAGON_BREATH));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.03) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x + 1.2, y + 1.0, z, new ItemStack(Blocks.COBWEB));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.4) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x + 1.2, y + 1.0, z, new ItemStack((ItemLike)UndeadRevamp2ModItems.HEAVYTOOTH.get()));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.5) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x + 1.2, y + 1.0, z, new ItemStack(Items.DIAMOND));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (world instanceof ServerLevel _levelxxx) {
                  ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x + 1.2, y + 1.0, z, new ItemStack(Items.AMETHYST_SHARD));
                  entityToSpawn.setPickUpDelay(10);
                  _levelxxx.addFreshEntity(entityToSpawn);
               }
            } else {
               if (world instanceof ServerLevel _levelxxx) {
                  _levelxxx.addFreshEntity(new ExperienceOrb(_levelxxx, x - 1.0, y + 1.0, z, 4));
               }

               if (Math.random() < 0.2) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x - 1.2, y + 1.0, z, new ItemStack(Items.IRON_INGOT));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.19) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x - 1.2, y + 1.0, z, new ItemStack(Items.GOLD_INGOT));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.18) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x - 1.2, y + 1.0, z, new ItemStack(Items.DIAMOND));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.17) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x - 1.2, y + 1.0, z, new ItemStack(Items.COPPER_INGOT));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.16) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x - 1.2, y + 1.0, z, new ItemStack(Blocks.GOLD_BLOCK));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.18) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(
                        _levelxxx, x - 1.2, y + 1.0, z, new ItemStack((ItemLike)UndeadRevamp2ModBlocks.COFFINBROAD.get())
                     );
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.3) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x - 1.2, y + 1.0, z, new ItemStack(Items.GLOW_BERRIES));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.35) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x - 1.2, y + 1.0, z, new ItemStack(Blocks.SMOOTH_STONE));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.2) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(
                        _levelxxx, x - 1.2, y + 1.0, z, new ItemStack((ItemLike)UndeadRevamp2ModItems.BOSTROXINGOT.get())
                     );
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.2) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x - 1.2, y + 1.0, z, new ItemStack(Blocks.SOUL_LANTERN));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.05) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x - 1.2, y + 1.0, z, new ItemStack(Blocks.PIGLIN_HEAD));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.1) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x - 1.2, y + 1.0, z, new ItemStack(Items.DIAMOND));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.1) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x - 1.2, y + 1.0, z, new ItemStack(Items.SPIDER_EYE));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.1) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(
                        _levelxxx, x - 1.2, y + 1.0, z, new ItemStack((ItemLike)UndeadRevamp2ModBlocks.COFFINBROAD.get())
                     );
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.25) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(
                        _levelxxx, x - 1.2, y + 1.0, z, new ItemStack((ItemLike)UndeadRevamp2ModItems.THE_SOMNOLENCEEXTRACT.get())
                     );
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.01) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x - 1.2, y + 1.0, z, new ItemStack(Blocks.BEDROCK));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.6) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x - 1.2, y + 1.0, z, new ItemStack(Items.SKULL_POTTERY_SHERD));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.03) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x - 1.2, y + 1.0, z, new ItemStack(Items.DRAGON_BREATH));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.03) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x - 1.2, y + 1.0, z, new ItemStack(Blocks.COBWEB));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.4) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x - 1.2, y + 1.0, z, new ItemStack((ItemLike)UndeadRevamp2ModItems.HEAVYTOOTH.get()));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (Math.random() < 0.5) {
                  if (world instanceof ServerLevel _levelxxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x - 1.2, y + 1.0, z, new ItemStack(Items.DIAMOND));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxxx.addFreshEntity(entityToSpawn);
                  }
               } else if (world instanceof ServerLevel _levelxxx) {
                  ItemEntity entityToSpawn = new ItemEntity(_levelxxx, x - 1.2, y + 1.0, z, new ItemStack(Items.AMETHYST_SHARD));
                  entityToSpawn.setPickUpDelay(10);
                  _levelxxx.addFreshEntity(entityToSpawn);
               }
            }
         }

         UndeadRevamp2Mod.queueServerWork(5, () -> world.destroyBlock(BlockPos.containing(x, y, z), false));
      } else if (world instanceof Level _levelxxx) {
         if (!_levelxxx.isClientSide()) {
            _levelxxx.playSound(
               null,
               BlockPos.containing(x, y, z),
               (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.stone.hit")),
               SoundSource.NEUTRAL,
               1.0F,
               1.0F
            );
         } else {
            _levelxxx.playLocalSound(
               x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.stone.hit")), SoundSource.NEUTRAL, 1.0F, 1.0F, false
            );
         }
      }
   }
}

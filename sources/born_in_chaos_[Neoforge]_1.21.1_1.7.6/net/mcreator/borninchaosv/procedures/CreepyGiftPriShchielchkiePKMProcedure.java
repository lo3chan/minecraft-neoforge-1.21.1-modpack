package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.mcreator.borninchaosv.network.BornInChaosV1ModVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class CreepyGiftPriShchielchkiePKMProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == BornInChaosV1ModItems.CREEPY_GIFT.get()) {
            if (entity instanceof LivingEntity _entity) {
               _entity.swing(InteractionHand.MAIN_HAND, true);
            }
         } else if ((entity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.CREEPY_GIFT.get()
            && entity instanceof LivingEntity _entity) {
            _entity.swing(InteractionHand.OFF_HAND, true);
         }

         itemstack.shrink(1);
         if (entity instanceof Player _player) {
            _player.getCooldowns().addCooldown(itemstack.getItem(), 10);
         }

         if (world instanceof Level _level) {
            if (!_level.isClientSide()) {
               _level.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.wool.break")),
                  SoundSource.NEUTRAL,
                  0.8F,
                  1.1F
               );
            } else {
               _level.playLocalSound(
                  x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.wool.break")), SoundSource.NEUTRAL, 0.8F, 1.1F, false
               );
            }
         }

         if (world instanceof ServerLevel _levelx) {
            _levelx.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.LITTLESNOWFLAKE.get(),
               entity.getX(),
               entity.getY() + 1.0,
               entity.getZ(),
               7,
               0.25,
               0.25,
               0.25,
               0.1
            );
         }

         if (((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness <= 80.0
            && Math.random() < 0.55) {
            if (Math.random() < 0.5) {
               for (int index0 = 0; index0 < Mth.nextInt(RandomSource.create(), 1, 3); index0++) {
                  if (world instanceof ServerLevel _levelx) {
                     ItemEntity entityToSpawn = new ItemEntity(
                        _levelx,
                        entity.getX(),
                        entity.getY() + 1.0,
                        entity.getZ(),
                        new ItemStack((ItemLike)BornInChaosV1ModItems.CREEPY_COOKIES_WITH_MILK.get())
                     );
                     entityToSpawn.setPickUpDelay(10);
                     _levelx.addFreshEntity(entityToSpawn);
                  }
               }
            } else if (Math.random() < 0.9) {
               for (int index1 = 0; index1 < Mth.nextInt(RandomSource.create(), 1, 3); index1++) {
                  if (world instanceof ServerLevel _levelx) {
                     ItemEntity entityToSpawn = new ItemEntity(
                        _levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack((ItemLike)BornInChaosV1ModItems.SPIRITUAL_GINGERBREAD.get())
                     );
                     entityToSpawn.setPickUpDelay(10);
                     _levelx.addFreshEntity(entityToSpawn);
                  }
               }
            }
         } else if (((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness < 80.0
            && Math.random() < 0.55) {
            if (Math.random() < 0.2) {
               for (int index2 = 0; index2 < Mth.nextInt(RandomSource.create(), 1, 2); index2++) {
                  if (world instanceof ServerLevel _levelx) {
                     ItemEntity entityToSpawn = new ItemEntity(
                        _levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack((ItemLike)BornInChaosV1ModItems.COFFEE_CANDY.get())
                     );
                     entityToSpawn.setPickUpDelay(10);
                     _levelx.addFreshEntity(entityToSpawn);
                  }
               }
            } else if (Math.random() < 0.2) {
               for (int index3 = 0; index3 < Mth.nextInt(RandomSource.create(), 1, 2); index3++) {
                  if (world instanceof ServerLevel _levelx) {
                     ItemEntity entityToSpawn = new ItemEntity(
                        _levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack((ItemLike)BornInChaosV1ModItems.CHOCOLATE_HEART.get())
                     );
                     entityToSpawn.setPickUpDelay(10);
                     _levelx.addFreshEntity(entityToSpawn);
                  }
               }
            } else if (Math.random() < 0.25) {
               for (int index4 = 0; index4 < Mth.nextInt(RandomSource.create(), 2, 5); index4++) {
                  if (world instanceof ServerLevel _levelx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack(Items.SWEET_BERRIES));
                     entityToSpawn.setPickUpDelay(10);
                     _levelx.addFreshEntity(entityToSpawn);
                  }
               }
            } else if (Math.random() < 0.25) {
               for (int index5 = 0; index5 < Mth.nextInt(RandomSource.create(), 2, 3); index5++) {
                  if (world instanceof ServerLevel _levelx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack(Items.GLOW_BERRIES));
                     entityToSpawn.setPickUpDelay(10);
                     _levelx.addFreshEntity(entityToSpawn);
                  }
               }
            } else if (Math.random() < 0.15) {
               if (world instanceof ServerLevel _levelx) {
                  ItemEntity entityToSpawn = new ItemEntity(_levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack(Items.GOLDEN_APPLE));
                  entityToSpawn.setPickUpDelay(10);
                  _levelx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.02) {
               if (world instanceof ServerLevel _levelx) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack(Items.ENCHANTED_GOLDEN_APPLE)
                  );
                  entityToSpawn.setPickUpDelay(10);
                  _levelx.addFreshEntity(entityToSpawn);
               }
            } else {
               for (int index6 = 0; index6 < Mth.nextInt(RandomSource.create(), 2, 5); index6++) {
                  if (world instanceof ServerLevel _levelx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack(Items.COOKIE));
                     entityToSpawn.setPickUpDelay(10);
                     _levelx.addFreshEntity(entityToSpawn);
                  }
               }
            }
         } else if (((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness <= 80.0) {
            for (int index7 = 0; index7 < Mth.nextInt(RandomSource.create(), 2, 5); index7++) {
               if (world instanceof ServerLevel _levelx) {
                  ItemEntity entityToSpawn = new ItemEntity(_levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack(Items.COOKIE));
                  entityToSpawn.setPickUpDelay(10);
                  _levelx.addFreshEntity(entityToSpawn);
               }
            }
         }

         if (((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness < 35.0 && Math.random() < 0.35
            )
          {
            if (Math.random() < 0.15) {
               if (world instanceof ServerLevel _levelx) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack((ItemLike)BornInChaosV1ModItems.ICY_SWEETNESS.get())
                  );
                  entityToSpawn.setPickUpDelay(10);
                  _levelx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.15) {
               if (world instanceof ServerLevel _levelx) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack((ItemLike)BornInChaosV1ModItems.NUT_HAMMER.get())
                  );
                  entityToSpawn.setPickUpDelay(10);
                  _levelx.addFreshEntity(entityToSpawn);
               }
            } else {
               for (int index8 = 0; index8 < Mth.nextInt(RandomSource.create(), 1, 4); index8++) {
                  if (world instanceof ServerLevel _levelx) {
                     ItemEntity entityToSpawn = new ItemEntity(
                        _levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack((ItemLike)BornInChaosV1ModItems.PERMAFROST_SHARD.get())
                     );
                     entityToSpawn.setPickUpDelay(10);
                     _levelx.addFreshEntity(entityToSpawn);
                  }
               }
            }
         } else if (((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness < 35.0
            && Math.random() < 0.35) {
            if (Math.random() < 0.15) {
               for (int index9 = 0; index9 < Mth.nextInt(RandomSource.create(), 1, 2); index9++) {
                  if (world instanceof ServerLevel _levelx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack(Items.ENDER_PEARL));
                     entityToSpawn.setPickUpDelay(10);
                     _levelx.addFreshEntity(entityToSpawn);
                  }
               }
            } else if (Math.random() < 0.2) {
               if (world instanceof ServerLevel _levelx) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack((ItemLike)BornInChaosV1ModItems.DARK_METAL_NUGGET.get())
                  );
                  entityToSpawn.setPickUpDelay(10);
                  _levelx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.05) {
               if (world instanceof ServerLevel _levelx) {
                  ItemEntity entityToSpawn = new ItemEntity(_levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack(Items.DIAMOND));
                  entityToSpawn.setPickUpDelay(10);
                  _levelx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.45) {
               for (int index10 = 0; index10 < Mth.nextInt(RandomSource.create(), 1, 3); index10++) {
                  if (world instanceof ServerLevel _levelx) {
                     ItemEntity entityToSpawn = new ItemEntity(
                        _levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack(Items.EXPERIENCE_BOTTLE)
                     );
                     entityToSpawn.setPickUpDelay(10);
                     _levelx.addFreshEntity(entityToSpawn);
                  }
               }
            }
         }

         if (((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness < 65.0 && Math.random() < 0.2) {
            if (Math.random() < 0.15) {
               if (world instanceof ServerLevel _levelx) {
                  ItemEntity entityToSpawn = new ItemEntity(_levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack(Items.POWDER_SNOW_BUCKET));
                  entityToSpawn.setPickUpDelay(10);
                  _levelx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.12) {
               if (world instanceof ServerLevel _levelx) {
                  ItemEntity entityToSpawn = new ItemEntity(_levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack(Blocks.SNIFFER_EGG));
                  entityToSpawn.setPickUpDelay(10);
                  _levelx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.12) {
               if (world instanceof ServerLevel _levelx) {
                  ItemEntity entityToSpawn = new ItemEntity(_levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack(Blocks.TURTLE_EGG));
                  entityToSpawn.setPickUpDelay(10);
                  _levelx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.1) {
               if (world instanceof ServerLevel _levelx) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE)
                  );
                  entityToSpawn.setPickUpDelay(10);
                  _levelx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.03 && world instanceof ServerLevel _levelx) {
               ItemEntity entityToSpawn = new ItemEntity(_levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack(Blocks.RED_SHULKER_BOX));
               entityToSpawn.setPickUpDelay(10);
               _levelx.addFreshEntity(entityToSpawn);
            }
         } else if (((BornInChaosV1ModVariables.PlayerVariables)entity.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).naughtiness < 85.0) {
            if (Math.random() < 0.15) {
               for (int index11 = 0; index11 < Mth.nextInt(RandomSource.create(), 4, 7); index11++) {
                  if (world instanceof ServerLevel _levelx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack(Blocks.BLUE_ICE));
                     entityToSpawn.setPickUpDelay(10);
                     _levelx.addFreshEntity(entityToSpawn);
                  }
               }
            } else if (Math.random() < 0.15) {
               for (int index12 = 0; index12 < Mth.nextInt(RandomSource.create(), 4, 7); index12++) {
                  if (world instanceof ServerLevel _levelx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack(Blocks.ICE));
                     entityToSpawn.setPickUpDelay(10);
                     _levelx.addFreshEntity(entityToSpawn);
                  }
               }
            } else if (Math.random() < 0.2) {
               for (int index13 = 0; index13 < Mth.nextInt(RandomSource.create(), 4, 7); index13++) {
                  if (world instanceof ServerLevel _levelx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack(Blocks.SNOW_BLOCK));
                     entityToSpawn.setPickUpDelay(10);
                     _levelx.addFreshEntity(entityToSpawn);
                  }
               }
            } else if (world instanceof ServerLevel _levelx) {
               ItemEntity entityToSpawn = new ItemEntity(_levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack(Blocks.SPRUCE_SAPLING));
               entityToSpawn.setPickUpDelay(10);
               _levelx.addFreshEntity(entityToSpawn);
            }
         } else if (world instanceof ServerLevel _levelx) {
            ItemEntity entityToSpawn = new ItemEntity(_levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack(Items.COAL));
            entityToSpawn.setPickUpDelay(10);
            _levelx.addFreshEntity(entityToSpawn);
         }
      }
   }
}

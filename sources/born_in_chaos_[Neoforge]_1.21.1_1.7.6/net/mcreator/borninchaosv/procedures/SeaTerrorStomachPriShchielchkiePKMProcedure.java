package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class SeaTerrorStomachPriShchielchkiePKMProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == BornInChaosV1ModItems.SEA_TERROR_STOMACH.get()
            )
          {
            if (entity instanceof Player _player) {
               _player.getCooldowns().addCooldown((Item)BornInChaosV1ModItems.SEA_TERROR_STOMACH.get(), 15);
            }

            if (entity instanceof ServerPlayer _player) {
               AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:disgusting_lootbox"));
               if (_adv != null) {
                  AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                  if (!_ap.isDone()) {
                     for (String criteria : _ap.getRemainingCriteria()) {
                        _player.getAdvancements().award(_adv, criteria);
                     }
                  }
               }
            }

            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:stomach_open")),
                     SoundSource.PLAYERS,
                     0.5F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:stomach_open")),
                     SoundSource.PLAYERS,
                     0.5F,
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
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.slime_block.break")),
                     SoundSource.NEUTRAL,
                     0.7F,
                     0.9F
                  );
               } else {
                  _levelx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.slime_block.break")),
                     SoundSource.NEUTRAL,
                     0.7F,
                     0.9F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelxx) {
               _levelxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SPLASHOFFLESH.get(), x, y + 1.0, z, 5, 0.3, 0.3, 0.3, 0.1);
            }

            if (world instanceof ServerLevel _levelxx) {
               _levelxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.FLESHSPLASH.get(), x, y + 1.0, z, 3, 0.3, 0.3, 0.3, 0.1);
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.swing(InteractionHand.MAIN_HAND, true);
            }

            (entity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).shrink(1);
            if (world instanceof ServerLevel _levelxx) {
               _levelxx.addFreshEntity(new ExperienceOrb(_levelxx, x, y, z, 3));
            }

            if (Math.random() < 0.8) {
               for (int index0 = 0; index0 < 2; index0++) {
                  if (world instanceof ServerLevel _levelxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack((ItemLike)BornInChaosV1ModItems.ROTTEN_FISH.get()));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxx.addFreshEntity(entityToSpawn);
                  }
               }
            }

            if (Math.random() < 0.3) {
               for (int index1 = 0; index1 < 3; index1++) {
                  if (world instanceof ServerLevel _levelxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack((ItemLike)BornInChaosV1ModItems.CORPSE_MAGGOT.get()));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxx.addFreshEntity(entityToSpawn);
                  }
               }
            } else if (Math.random() < 0.6) {
               for (int index2 = 0; index2 < 2; index2++) {
                  if (world instanceof ServerLevel _levelxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack(Items.SALMON));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxx.addFreshEntity(entityToSpawn);
                  }
               }
            } else if (Math.random() < 0.4) {
               for (int index3 = 0; index3 < 2; index3++) {
                  if (world instanceof ServerLevel _levelxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack(Items.COD));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxx.addFreshEntity(entityToSpawn);
                  }
               }
            } else if (Math.random() < 0.1 && world instanceof ServerLevel _levelxx) {
               ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack(Items.PUFFERFISH));
               entityToSpawn.setPickUpDelay(10);
               _levelxx.addFreshEntity(entityToSpawn);
            }

            if (Math.random() < 0.4) {
               if (world instanceof ServerLevel _levelxx) {
                  ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack((ItemLike)BornInChaosV1ModItems.SPINY_SHELL.get()));
                  entityToSpawn.setPickUpDelay(10);
                  _levelxx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.14 && world instanceof ServerLevel _levelxx) {
               ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack((ItemLike)BornInChaosV1ModItems.PILEOF_DARK_METAL.get()));
               entityToSpawn.setPickUpDelay(10);
               _levelxx.addFreshEntity(entityToSpawn);
            }

            if (Math.random() < 0.2) {
               if (world instanceof ServerLevel _levelxx) {
                  ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack(Items.BONE));
                  entityToSpawn.setPickUpDelay(10);
                  _levelxx.addFreshEntity(entityToSpawn);
               }
            } else {
               for (int index4 = 0; index4 < 2; index4++) {
                  if (world instanceof ServerLevel _levelxx) {
                     ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack(Items.SLIME_BALL));
                     entityToSpawn.setPickUpDelay(10);
                     _levelxx.addFreshEntity(entityToSpawn);
                  }
               }
            }

            if (Math.random() < 0.1) {
               if (world instanceof ServerLevel _levelxx) {
                  ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack(Items.GLOW_INK_SAC));
                  entityToSpawn.setPickUpDelay(10);
                  _levelxx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.25 && world instanceof ServerLevel _levelxx) {
               ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack(Items.INK_SAC));
               entityToSpawn.setPickUpDelay(10);
               _levelxx.addFreshEntity(entityToSpawn);
            }

            if (Math.random() < 0.15) {
               if (world instanceof ServerLevel _levelxx) {
                  ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack(Items.NAUTILUS_SHELL));
                  entityToSpawn.setPickUpDelay(10);
                  _levelxx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.1) {
               if (world instanceof ServerLevel _levelxx) {
                  ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack(Items.TURTLE_SCUTE));
                  entityToSpawn.setPickUpDelay(10);
                  _levelxx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.2) {
               if (world instanceof ServerLevel _levelxx) {
                  ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack(Items.DIAMOND));
                  entityToSpawn.setPickUpDelay(10);
                  _levelxx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.3) {
               if (world instanceof ServerLevel _levelxx) {
                  ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack(Items.EMERALD));
                  entityToSpawn.setPickUpDelay(10);
                  _levelxx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.3 && world instanceof ServerLevel _levelxx) {
               ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack(Items.RAW_GOLD));
               entityToSpawn.setPickUpDelay(10);
               _levelxx.addFreshEntity(entityToSpawn);
            }

            if (Math.random() < 0.1) {
               if (world instanceof ServerLevel _levelxx) {
                  ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack(Items.FISHING_ROD));
                  entityToSpawn.setPickUpDelay(10);
                  _levelxx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.02 && world instanceof ServerLevel _levelxx) {
               ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack(Items.ENCHANTED_GOLDEN_APPLE));
               entityToSpawn.setPickUpDelay(10);
               _levelxx.addFreshEntity(entityToSpawn);
            }

            if (Math.random() < 0.4 && world instanceof ServerLevel _levelxx) {
               ItemEntity entityToSpawn = new ItemEntity(_levelxx, x, y, z, new ItemStack(Items.ENDER_PEARL));
               entityToSpawn.setPickUpDelay(10);
               _levelxx.addFreshEntity(entityToSpawn);
            }
         }
      }
   }
}

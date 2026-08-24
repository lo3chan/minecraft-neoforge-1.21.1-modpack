package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
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
import net.neoforged.fml.ModList;

public class KrampussBagPriShchielchkiePKMProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == BornInChaosV1ModItems.KRAMPUSS_BAG.get()) {
            if (entity instanceof LivingEntity _entity) {
               _entity.swing(InteractionHand.MAIN_HAND, true);
            }
         } else if ((entity instanceof LivingEntity _livEntx ? _livEntx.getOffhandItem() : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.KRAMPUSS_BAG.get()
            && entity instanceof LivingEntity _entity) {
            _entity.swing(InteractionHand.OFF_HAND, true);
         }

         itemstack.shrink(1);
         if (entity instanceof Player _player) {
            _player.getCooldowns().addCooldown(itemstack.getItem(), 20);
         }

         if (world instanceof Level _level) {
            if (!_level.isClientSide()) {
               _level.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.armor.equip_leather")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _level.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.armor.equip_leather")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }

         if (world instanceof ServerLevel _levelx) {
            _levelx.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.LITTLESNOWFLAKE.get(),
               entity.getX(),
               entity.getY() + 1.0,
               entity.getZ(),
               8,
               0.4,
               0.3,
               0.4,
               0.2
            );
         }

         if (world instanceof ServerLevel _levelx) {
            _levelx.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.SNOWCLOUD.get(), entity.getX(), entity.getY() + 1.0, entity.getZ(), 4, 0.3, 0.2, 0.3, 0.1
            );
         }

         for (int index0 = 0; index0 < Mth.nextInt(RandomSource.create(), 7, 15); index0++) {
            if (world instanceof ServerLevel _levelx) {
               ItemEntity entityToSpawn = new ItemEntity(_levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack(Items.COAL));
               entityToSpawn.setPickUpDelay(25);
               _levelx.addFreshEntity(entityToSpawn);
            }
         }

         if (Math.random() < 0.45 && world instanceof ServerLevel _levelx) {
            ItemEntity entityToSpawn = new ItemEntity(
               _levelx,
               entity.getX(),
               entity.getY() + 1.0,
               entity.getZ(),
               new ItemStack(
                  (ItemLike)BuiltInRegistries.ITEM
                     .getOrCreateTag(ItemTags.create(ResourceLocation.parse("born_in_chaos_v1:rareloot")))
                     .getRandomElement(RandomSource.create())
                     .orElseGet(() -> BuiltInRegistries.ITEM.wrapAsHolder(Items.AIR))
                     .value()
               )
            );
            entityToSpawn.setPickUpDelay(25);
            _levelx.addFreshEntity(entityToSpawn);
         }

         if (Math.random() < 0.9) {
            for (int index1 = 0; index1 < Mth.nextInt(RandomSource.create(), 1, 3); index1++) {
               if (world instanceof ServerLevel _levelx) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _levelx,
                     entity.getX(),
                     entity.getY() + 1.0,
                     entity.getZ(),
                     new ItemStack(
                        (ItemLike)BuiltInRegistries.ITEM
                           .getOrCreateTag(ItemTags.create(ResourceLocation.parse("born_in_chaos_v1:unusualloot")))
                           .getRandomElement(RandomSource.create())
                           .orElseGet(() -> BuiltInRegistries.ITEM.wrapAsHolder(Items.AIR))
                           .value()
                     )
                  );
                  entityToSpawn.setPickUpDelay(25);
                  _levelx.addFreshEntity(entityToSpawn);
               }
            }
         }

         if (Math.random() < 0.65 && ModList.get().isLoaded("alexsmobs")) {
            if (Math.random() < 0.09) {
               if (world instanceof ServerLevel _levelx) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _levelx,
                     entity.getX(),
                     entity.getY() + 1.0,
                     entity.getZ(),
                     new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse("alexsmobs:unsettling_kimono")))
                  );
                  entityToSpawn.setPickUpDelay(25);
                  _levelx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.1) {
               if (world instanceof ServerLevel _levelx) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _levelx,
                     entity.getX(),
                     entity.getY() + 1.0,
                     entity.getZ(),
                     new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse("alexsmobs:warped_muscle")))
                  );
                  entityToSpawn.setPickUpDelay(25);
                  _levelx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.17) {
               for (int index2 = 0; index2 < Mth.nextInt(RandomSource.create(), 1, 2); index2++) {
                  if (world instanceof ServerLevel _levelx) {
                     ItemEntity entityToSpawn = new ItemEntity(
                        _levelx,
                        entity.getX(),
                        entity.getY() + 1.0,
                        entity.getZ(),
                        new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse("alexsmobs:hemolymph_sac")))
                     );
                     entityToSpawn.setPickUpDelay(25);
                     _levelx.addFreshEntity(entityToSpawn);
                  }
               }
            } else if (Math.random() < 0.14) {
               if (world instanceof ServerLevel _levelx) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _levelx,
                     entity.getX(),
                     entity.getY() + 1.0,
                     entity.getZ(),
                     new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse("alexsmobs:farseer_arm")))
                  );
                  entityToSpawn.setPickUpDelay(25);
                  _levelx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.25 && world instanceof ServerLevel _levelx) {
               ItemEntity entityToSpawn = new ItemEntity(
                  _levelx,
                  entity.getX(),
                  entity.getY() + 1.0,
                  entity.getZ(),
                  new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse("alexsmobs:lost_tentacle")))
               );
               entityToSpawn.setPickUpDelay(25);
               _levelx.addFreshEntity(entityToSpawn);
            }
         }

         if (Math.random() < 0.4 && ModList.get().isLoaded("aquamirae")) {
            if (Math.random() < 0.1) {
               if (world instanceof ServerLevel _levelx) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _levelx,
                     entity.getX(),
                     entity.getY() + 1.0,
                     entity.getZ(),
                     new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse("aquamirae:rune_of_the_storm")))
                  );
                  entityToSpawn.setPickUpDelay(25);
                  _levelx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.17) {
               if (world instanceof ServerLevel _levelx) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _levelx,
                     entity.getX(),
                     entity.getY() + 1.0,
                     entity.getZ(),
                     new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse("aquamirae:ship_graveyard_echo")))
                  );
                  entityToSpawn.setPickUpDelay(25);
                  _levelx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.2 && world instanceof ServerLevel _levelx) {
               ItemEntity entityToSpawn = new ItemEntity(
                  _levelx,
                  entity.getX(),
                  entity.getY() + 1.0,
                  entity.getZ(),
                  new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse("aquamirae:abyssal_amethyst")))
               );
               entityToSpawn.setPickUpDelay(25);
               _levelx.addFreshEntity(entityToSpawn);
            }
         } else if (Math.random() < 0.3 && ModList.get().isLoaded("cataclysm")) {
            if (Math.random() < 0.1) {
               if (world instanceof ServerLevel _levelx) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _levelx,
                     entity.getX(),
                     entity.getY() + 1.0,
                     entity.getZ(),
                     new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse("cataclysm:witherite_ingot")))
                  );
                  entityToSpawn.setPickUpDelay(25);
                  _levelx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.16) {
               if (world instanceof ServerLevel _levelx) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _levelx,
                     entity.getX(),
                     entity.getY() + 1.0,
                     entity.getZ(),
                     new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse("cataclysm:lava_power_cell")))
                  );
                  entityToSpawn.setPickUpDelay(25);
                  _levelx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.2) {
               if (world instanceof ServerLevel _levelx) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _levelx,
                     entity.getX(),
                     entity.getY() + 1.0,
                     entity.getZ(),
                     new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse("cataclysm:ancient_metal_ingot")))
                  );
                  entityToSpawn.setPickUpDelay(25);
                  _levelx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.25) {
               for (int index3 = 0; index3 < Mth.nextInt(RandomSource.create(), 1, 3); index3++) {
                  if (world instanceof ServerLevel _levelx) {
                     ItemEntity entityToSpawn = new ItemEntity(
                        _levelx,
                        entity.getX(),
                        entity.getY() + 1.0,
                        entity.getZ(),
                        new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse("cataclysm:amethyst_crab_meat")))
                     );
                     entityToSpawn.setPickUpDelay(25);
                     _levelx.addFreshEntity(entityToSpawn);
                  }
               }
            }
         }

         if (Math.random() < 0.65 && ModList.get().isLoaded("create") && Math.random() < 0.25 && world instanceof ServerLevel _levelx) {
            ItemEntity entityToSpawn = new ItemEntity(
               _levelx,
               entity.getX(),
               entity.getY() + 1.0,
               entity.getZ(),
               new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse("create:precision_mechanism")))
            );
            entityToSpawn.setPickUpDelay(25);
            _levelx.addFreshEntity(entityToSpawn);
         }

         if (Math.random() < 0.5 && ModList.get().isLoaded("gnumus")) {
            if (Math.random() < 0.35 && world instanceof ServerLevel _levelx) {
               ItemEntity entityToSpawn = new ItemEntity(
                  _levelx,
                  entity.getX(),
                  entity.getY() + 1.0,
                  entity.getZ(),
                  new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse("gnumus:vintage_parts")))
               );
               entityToSpawn.setPickUpDelay(25);
               _levelx.addFreshEntity(entityToSpawn);
            }
         } else if (Math.random() < 0.65 && ModList.get().isLoaded("galosphere") && Math.random() < 0.25 && world instanceof ServerLevel _levelx) {
            ItemEntity entityToSpawn = new ItemEntity(
               _levelx,
               entity.getX(),
               entity.getY() + 1.0,
               entity.getZ(),
               new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse("galosphere:saltbound_tablet")))
            );
            entityToSpawn.setPickUpDelay(25);
            _levelx.addFreshEntity(entityToSpawn);
         }

         if (Math.random() < 0.4 && ModList.get().isLoaded("netherexp")) {
            if (Math.random() < 0.3 && world instanceof ServerLevel _levelx) {
               ItemEntity entityToSpawn = new ItemEntity(
                  _levelx,
                  entity.getX(),
                  entity.getY() + 1.0,
                  entity.getZ(),
                  new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse("netherexp:shotgun_core")))
               );
               entityToSpawn.setPickUpDelay(25);
               _levelx.addFreshEntity(entityToSpawn);
            }
         } else if (Math.random() < 0.4 && ModList.get().isLoaded("irons_spellbooks") && Math.random() < 0.11 && world instanceof ServerLevel _levelx) {
            ItemEntity entityToSpawn = new ItemEntity(
               _levelx,
               entity.getX(),
               entity.getY() + 1.0,
               entity.getZ(),
               new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse("irons_spellbooks:arcane_salvage")))
            );
            entityToSpawn.setPickUpDelay(25);
            _levelx.addFreshEntity(entityToSpawn);
         }

         if (Math.random() < 0.5 && ModList.get().isLoaded("alexscaves")) {
            if (Math.random() < 0.14) {
               if (world instanceof ServerLevel _levelx) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _levelx,
                     entity.getX(),
                     entity.getY() + 1.0,
                     entity.getZ(),
                     new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse("alexscaves:heart_of_iron")))
                  );
                  entityToSpawn.setPickUpDelay(25);
                  _levelx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.13) {
               if (world instanceof ServerLevel _levelx) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _levelx,
                     entity.getX(),
                     entity.getY() + 1.0,
                     entity.getZ(),
                     new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse("alexscaves:pure_darkness")))
                  );
                  entityToSpawn.setPickUpDelay(25);
                  _levelx.addFreshEntity(entityToSpawn);
               }
            } else if (Math.random() < 0.17 && world instanceof ServerLevel _levelx) {
               ItemEntity entityToSpawn = new ItemEntity(
                  _levelx,
                  entity.getX(),
                  entity.getY() + 1.0,
                  entity.getZ(),
                  new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse("alexscaves:immortal_embryo")))
               );
               entityToSpawn.setPickUpDelay(25);
               _levelx.addFreshEntity(entityToSpawn);
            }
         }
      }
   }
}

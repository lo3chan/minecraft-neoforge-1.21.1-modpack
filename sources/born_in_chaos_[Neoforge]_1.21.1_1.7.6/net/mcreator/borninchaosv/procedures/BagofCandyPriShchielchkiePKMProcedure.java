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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class BagofCandyPriShchielchkiePKMProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == BornInChaosV1ModItems.BAGOF_CANDY.get()) {
            if (entity instanceof LivingEntity _entity) {
               _entity.swing(InteractionHand.MAIN_HAND, true);
            }
         } else if ((entity instanceof LivingEntity _livEntx ? _livEntx.getOffhandItem() : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.BAGOF_CANDY.get()
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
               (SimpleParticleType)BornInChaosV1ModParticleTypes.CANDY_ORANGE.get(), entity.getX(), entity.getY() + 1.5, entity.getZ(), 3, 0.3, 0.2, 0.3, 0.2
            );
         }

         if (world instanceof ServerLevel _levelx) {
            _levelx.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.CANDYGREN.get(), entity.getX(), entity.getY() + 1.5, entity.getZ(), 3, 0.3, 0.2, 0.3, 0.2
            );
         }

         if (world instanceof ServerLevel _levelx) {
            _levelx.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.CANDYPURPLE.get(), entity.getX(), entity.getY() + 1.5, entity.getZ(), 3, 0.3, 0.2, 0.3, 0.2
            );
         }

         for (int index0 = 0; index0 < 5; index0++) {
            if (world instanceof ServerLevel _levelx) {
               ItemEntity entityToSpawn = new ItemEntity(
                  _levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack((ItemLike)BornInChaosV1ModItems.HOLIDAY_CANDY.get())
               );
               entityToSpawn.setPickUpDelay(10);
               _levelx.addFreshEntity(entityToSpawn);
            }
         }

         for (int index1 = 0; index1 < Mth.nextInt(RandomSource.create(), 10, 20); index1++) {
            if (world instanceof ServerLevel _levelx) {
               ItemEntity entityToSpawn = new ItemEntity(
                  _levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack((ItemLike)BornInChaosV1ModItems.MINT_CANDY.get())
               );
               entityToSpawn.setPickUpDelay(10);
               _levelx.addFreshEntity(entityToSpawn);
            }
         }

         for (int index2 = 0; index2 < Mth.nextInt(RandomSource.create(), 5, 15); index2++) {
            if (world instanceof ServerLevel _levelx) {
               ItemEntity entityToSpawn = new ItemEntity(
                  _levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack((ItemLike)BornInChaosV1ModItems.MINT_ICE_CREAM.get())
               );
               entityToSpawn.setPickUpDelay(10);
               _levelx.addFreshEntity(entityToSpawn);
            }
         }

         for (int index3 = 0; index3 < Mth.nextInt(RandomSource.create(), 10, 20); index3++) {
            if (world instanceof ServerLevel _levelx) {
               ItemEntity entityToSpawn = new ItemEntity(
                  _levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack((ItemLike)BornInChaosV1ModItems.CARAMEL_PEPPER.get())
               );
               entityToSpawn.setPickUpDelay(10);
               _levelx.addFreshEntity(entityToSpawn);
            }
         }

         for (int index4 = 0; index4 < Mth.nextInt(RandomSource.create(), 10, 20); index4++) {
            if (world instanceof ServerLevel _levelx) {
               ItemEntity entityToSpawn = new ItemEntity(
                  _levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack((ItemLike)BornInChaosV1ModItems.GUMMY_VAMPIRE_TEETH.get())
               );
               entityToSpawn.setPickUpDelay(10);
               _levelx.addFreshEntity(entityToSpawn);
            }
         }

         for (int index5 = 0; index5 < Mth.nextInt(RandomSource.create(), 10, 20); index5++) {
            if (world instanceof ServerLevel _levelx) {
               ItemEntity entityToSpawn = new ItemEntity(
                  _levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack((ItemLike)BornInChaosV1ModItems.CHOCOLATE_HEART.get())
               );
               entityToSpawn.setPickUpDelay(10);
               _levelx.addFreshEntity(entityToSpawn);
            }
         }

         for (int index6 = 0; index6 < Mth.nextInt(RandomSource.create(), 10, 20); index6++) {
            if (world instanceof ServerLevel _levelx) {
               ItemEntity entityToSpawn = new ItemEntity(
                  _levelx, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack((ItemLike)BornInChaosV1ModItems.COFFEE_CANDY.get())
               );
               entityToSpawn.setPickUpDelay(10);
               _levelx.addFreshEntity(entityToSpawn);
            }
         }
      }
   }
}

package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class NutHammerPriUdariePoSushchnostiInstrumientomProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         if (entity instanceof Player) {
            if (Math.random() < 0.35) {
               if (world instanceof ServerLevel _level) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _level,
                     entity.getX(),
                     entity.getY() + 1.5,
                     entity.getZ(),
                     (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).copy()
                  );
                  entityToSpawn.setPickUpDelay(15);
                  _level.addFreshEntity(entityToSpawn);
               }

               if (entity instanceof Player _player) {
                  ItemStack _stktoremove;
                  ItemStack var20 = _stktoremove = entity instanceof LivingEntity _entGetArmor
                     ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD)
                     : ItemStack.EMPTY;
                  _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
               }

               if (world instanceof ServerLevel _level) {
                  _level.sendParticles(
                     (SimpleParticleType)BornInChaosV1ModParticleTypes.STUNSTARS.get(),
                     entity.getX(),
                     entity.getY() + 1.5,
                     entity.getZ(),
                     5,
                     0.25,
                     0.2,
                     0.25,
                     0.1
                  );
               }

               if (!world.isClientSide() && world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(entity.getX(), entity.getY() + 1.5, entity.getZ()),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:bonk_hit")),
                        SoundSource.NEUTRAL,
                        0.8F,
                        0.9F
                     );
                  } else {
                     _level.playLocalSound(
                        entity.getX(),
                        entity.getY() + 1.5,
                        entity.getZ(),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:bonk_hit")),
                        SoundSource.NEUTRAL,
                        0.8F,
                        0.9F,
                        false
                     );
                  }
               }
            }
         } else if (!(entity instanceof Player) && Math.random() < 0.6) {
            if (world instanceof ServerLevel _levelx) {
               ItemEntity entityToSpawn = new ItemEntity(
                  _levelx,
                  entity.getX(),
                  entity.getY() + 1.5,
                  entity.getZ(),
                  (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).copy()
               );
               entityToSpawn.setPickUpDelay(15);
               _levelx.addFreshEntity(entityToSpawn);
            }

            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles(
                  (SimpleParticleType)BornInChaosV1ModParticleTypes.STUNSTARS.get(), entity.getX(), entity.getY() + 1.5, entity.getZ(), 5, 0.25, 0.2, 0.25, 0.1
               );
            }

            if (entity.getCapability(ItemHandler.ENTITY, null) instanceof IItemHandlerModifiable _modHandlerIter) {
               for (int _idx = 0; _idx < _modHandlerIter.getSlots(); _idx++) {
                  ItemStack itemstackiterator = _modHandlerIter.getStackInSlot(_idx).copy();
                  (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).shrink(1);
               }
            }

            if (!world.isClientSide() && world instanceof Level _levelx) {
               if (!_levelx.isClientSide()) {
                  _levelx.playSound(
                     null,
                     BlockPos.containing(entity.getX(), entity.getY() + 1.5, entity.getZ()),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:bonk_hit")),
                     SoundSource.NEUTRAL,
                     0.6F,
                     0.9F
                  );
               } else {
                  _levelx.playLocalSound(
                     entity.getX(),
                     entity.getY() + 1.5,
                     entity.getZ(),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:bonk_hit")),
                     SoundSource.NEUTRAL,
                     0.6F,
                     0.9F,
                     false
                  );
               }
            }
         }
      }
   }
}

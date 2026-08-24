package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.mcreator.borninchaosv.item.BonescallerStaffItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class StaffoftheSummonerPriShchielchkiePravoiKnopkoiMyshiNaBlokieProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _entGetArmorxxx ? _entGetArmorxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_HELMET.get()
            && (entity instanceof LivingEntity _entGetArmorxx ? _entGetArmorxx.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_LEGGINGS.get()
            && (entity instanceof LivingEntity _entGetArmorx ? _entGetArmorx.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_CHESTPLATE.get()
            && (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_BOOTS.get()
            && (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.BONESCALLER_STAFF.get()) {
            if (world.getBlockState(BlockPos.containing(x, y, z)).canOcclude()
               && !world.getBlockState(BlockPos.containing(x, y + 1.0, z)).canOcclude()
               && !(entity instanceof LivingEntity _livEnt12 && _livEnt12.hasEffect(BornInChaosV1ModMobEffects.MAGIC_DEPLETION))) {
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.MAGIC_DEPLETION, 460, 0));
               }

               if (world instanceof ServerLevel _level) {
                  (entity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).hurtAndBreak(1, _level, null, _stkprov -> {});
               }

               if (world instanceof ServerLevel _level) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CONTROLLED_BABY_SKELETON.get())
                     .spawn(_level, BlockPos.containing(x + 0.2, y + 1.0, z + 0.2), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
                  }
               }

               if (world instanceof ServerLevel _levelx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CONTROLLED_BABY_SKELETON.get())
                     .spawn(_levelx, BlockPos.containing(x + 0.8, y + 1.0, z + 0.2), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
                  }
               }

               if (world instanceof ServerLevel _levelxx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CONTROLLED_BABY_SKELETON.get())
                     .spawn(_levelxx, BlockPos.containing(x + 0.5, y + 1.0, z + 0.8), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
                  }
               }

               if (world instanceof Level _levelxxx) {
                  if (!_levelxxx.isClientSide()) {
                     _levelxxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.skeleton.ambient")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _levelxxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.skeleton.ambient")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }

               if (world instanceof Level _levelxxxx) {
                  if (!_levelxxxx.isClientSide()) {
                     _levelxxxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.evoker.prepare_attack")),
                        SoundSource.NEUTRAL,
                        0.5F,
                        1.0F
                     );
                  } else {
                     _levelxxxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.evoker.prepare_attack")),
                        SoundSource.NEUTRAL,
                        0.5F,
                        1.0F,
                        false
                     );
                  }
               }

               if (world instanceof ServerLevel _levelxxxxx) {
                  _levelxxxxx.sendParticles(ParticleTypes.POOF, x + 0.5, y + 1.0, z + 0.5, 10, 0.5, 0.5, 0.5, 0.1);
               }

               if (world instanceof ServerLevel _levelxxxxx) {
                  _levelxxxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.RITUAL.get(), x + 0.5, y + 1.0, z + 0.5, 6, 0.5, 0.4, 0.5, 0.1);
               }

               if (itemstack.getItem() instanceof BonescallerStaffItem) {
                  CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckoAnim", "invocation_right"));
               }
            }
         } else if ((entity instanceof LivingEntity _entGetArmorxxxx ? _entGetArmorxxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.SPIRITUAL_GUIDE_SOMBRERO_HELMET.get()
            && (entity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.BONESCALLER_STAFF.get()) {
            if (world.getBlockState(BlockPos.containing(x, y, z)).canOcclude()
               && !world.getBlockState(BlockPos.containing(x, y + 1.0, z)).canOcclude()
               && !(entity instanceof LivingEntity _livEnt31 && _livEnt31.hasEffect(BornInChaosV1ModMobEffects.MAGIC_DEPLETION))) {
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.MAGIC_DEPLETION, 560, 0));
               }

               if (world instanceof ServerLevel _levelxxxxx) {
                  (entity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY).hurtAndBreak(1, _levelxxxxx, null, _stkprov -> {});
               }

               if (world instanceof ServerLevel _levelxxxxx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CONTROLLED_SPIRITUAL_ASSISTANT.get())
                     .spawn(_levelxxxxx, BlockPos.containing(x + 0.2, y + 1.0, z + 0.2), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
                  }
               }

               if (world instanceof ServerLevel _levelxxxxxx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CONTROLLED_SPIRITUAL_ASSISTANT.get())
                     .spawn(_levelxxxxxx, BlockPos.containing(x + 0.8, y + 1.0, z + 0.2), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
                  }
               }

               if (world instanceof Level _levelxxxxxxx) {
                  if (!_levelxxxxxxx.isClientSide()) {
                     _levelxxxxxxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.skeleton.ambient")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _levelxxxxxxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.skeleton.ambient")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }

               if (world instanceof Level _levelxxxxxxxx) {
                  if (!_levelxxxxxxxx.isClientSide()) {
                     _levelxxxxxxxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.evoker.prepare_attack")),
                        SoundSource.NEUTRAL,
                        0.5F,
                        1.0F
                     );
                  } else {
                     _levelxxxxxxxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.evoker.prepare_attack")),
                        SoundSource.NEUTRAL,
                        0.5F,
                        1.0F,
                        false
                     );
                  }
               }

               if (world instanceof ServerLevel _levelxxxxxxxxx) {
                  _levelxxxxxxxxx.sendParticles(
                     (SimpleParticleType)BornInChaosV1ModParticleTypes.STIMULATINGSMOKE.get(), x + 0.5, y + 1.0, z + 0.5, 10, 0.5, 0.5, 0.5, 0.1
                  );
               }

               if (world instanceof ServerLevel _levelxxxxxxxxx) {
                  _levelxxxxxxxxx.sendParticles(
                     (SimpleParticleType)BornInChaosV1ModParticleTypes.RITUAL.get(), x + 0.5, y + 1.0, z + 0.5, 6, 0.5, 0.4, 0.5, 0.1
                  );
               }

               if (itemstack.getItem() instanceof BonescallerStaffItem) {
                  CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckoAnim", "invocation_right"));
               }
            }
         } else if ((entity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.BONESCALLER_STAFF.get()
            && world.getBlockState(BlockPos.containing(x, y, z)).canOcclude()
            && !world.getBlockState(BlockPos.containing(x, y + 1.0, z)).canOcclude()
            && !(entity instanceof LivingEntity _livEnt47 && _livEnt47.hasEffect(BornInChaosV1ModMobEffects.MAGIC_DEPLETION))) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.MAGIC_DEPLETION, 300, 0));
            }

            if (world instanceof ServerLevel _levelxxxxxxxxx) {
               (entity instanceof LivingEntity _livEntxxx ? _livEntxxx.getMainHandItem() : ItemStack.EMPTY)
                  .hurtAndBreak(1, _levelxxxxxxxxx, null, _stkprov -> {});
            }

            if (world instanceof ServerLevel _levelxxxxxxxxx) {
               Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CONTROLLED_BABY_SKELETON.get())
                  .spawn(_levelxxxxxxxxx, BlockPos.containing(x + 0.2, y + 1.0, z + 0.5), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }

            if (world instanceof ServerLevel _levelxxxxxxxxxx) {
               Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CONTROLLED_BABY_SKELETON.get())
                  .spawn(_levelxxxxxxxxxx, BlockPos.containing(x + 0.8, y + 1.0, z + 0.5), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }

            if (world instanceof Level _levelxxxxxxxxxxx) {
               if (!_levelxxxxxxxxxxx.isClientSide()) {
                  _levelxxxxxxxxxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.skeleton.ambient")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelxxxxxxxxxxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.skeleton.ambient")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof Level _levelxxxxxxxxxxxx) {
               if (!_levelxxxxxxxxxxxx.isClientSide()) {
                  _levelxxxxxxxxxxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.evoker.prepare_attack")),
                     SoundSource.NEUTRAL,
                     0.5F,
                     1.0F
                  );
               } else {
                  _levelxxxxxxxxxxxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.evoker.prepare_attack")),
                     SoundSource.NEUTRAL,
                     0.5F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelxxxxxxxxxxxxx) {
               _levelxxxxxxxxxxxxx.sendParticles(ParticleTypes.POOF, x + 0.5, y + 1.0, z + 0.5, 10, 0.5, 0.5, 0.5, 0.1);
            }

            if (world instanceof ServerLevel _levelxxxxxxxxxxxxx) {
               _levelxxxxxxxxxxxxx.sendParticles(
                  (SimpleParticleType)BornInChaosV1ModParticleTypes.RITUAL.get(), x + 0.5, y + 1.0, z + 0.5, 6, 0.5, 0.4, 0.5, 0.1
               );
            }

            if (itemstack.getItem() instanceof BonescallerStaffItem) {
               CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckoAnim", "invocation_right"));
            }
         }

         if ((entity instanceof LivingEntity _entGetArmorxxxxxxxx ? _entGetArmorxxxxxxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_HELMET.get()
            && (entity instanceof LivingEntity _entGetArmorxxxxxxx ? _entGetArmorxxxxxxx.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_LEGGINGS.get()
            && (entity instanceof LivingEntity _entGetArmorxxxxxx ? _entGetArmorxxxxxx.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_CHESTPLATE.get()
            && (entity instanceof LivingEntity _entGetArmorxxxxx ? _entGetArmorxxxxx.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_BOOTS.get()
            && (entity instanceof LivingEntity _livEntxx ? _livEntxx.getOffhandItem() : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.BONESCALLER_STAFF.get()) {
            if (world.getBlockState(BlockPos.containing(x, y, z)).canOcclude()
               && !world.getBlockState(BlockPos.containing(x, y + 1.0, z)).canOcclude()
               && !(entity instanceof LivingEntity _livEnt71 && _livEnt71.hasEffect(BornInChaosV1ModMobEffects.MAGIC_DEPLETION))) {
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.MAGIC_DEPLETION, 460, 0));
               }

               if (world instanceof ServerLevel _levelxxxxxxxxxxxxx) {
                  (entity instanceof LivingEntity _livEntxxx ? _livEntxxx.getOffhandItem() : ItemStack.EMPTY)
                     .hurtAndBreak(1, _levelxxxxxxxxxxxxx, null, _stkprov -> {});
               }

               if (world instanceof ServerLevel _levelxxxxxxxxxxxxx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CONTROLLED_BABY_SKELETON.get())
                     .spawn(_levelxxxxxxxxxxxxx, BlockPos.containing(x + 0.2, y + 1.0, z + 0.5), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
                  }
               }

               if (world instanceof ServerLevel _levelxxxxxxxxxxxxxx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CONTROLLED_BABY_SKELETON.get())
                     .spawn(_levelxxxxxxxxxxxxxx, BlockPos.containing(x + 0.8, y + 1.0, z + 0.5), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
                  }
               }

               if (world instanceof ServerLevel _levelxxxxxxxxxxxxxxx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CONTROLLED_BABY_SKELETON.get())
                     .spawn(_levelxxxxxxxxxxxxxxx, BlockPos.containing(x + 0.5, y + 1.0, z + 0.8), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
                  }
               }

               if (world instanceof Level _levelxxxxxxxxxxxxxxxx) {
                  if (!_levelxxxxxxxxxxxxxxxx.isClientSide()) {
                     _levelxxxxxxxxxxxxxxxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.skeleton.ambient")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _levelxxxxxxxxxxxxxxxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.skeleton.ambient")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }

               if (world instanceof Level _levelxxxxxxxxxxxxxxxxx) {
                  if (!_levelxxxxxxxxxxxxxxxxx.isClientSide()) {
                     _levelxxxxxxxxxxxxxxxxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.evoker.prepare_attack")),
                        SoundSource.NEUTRAL,
                        0.5F,
                        1.0F
                     );
                  } else {
                     _levelxxxxxxxxxxxxxxxxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.evoker.prepare_attack")),
                        SoundSource.NEUTRAL,
                        0.5F,
                        1.0F,
                        false
                     );
                  }
               }

               if (world instanceof ServerLevel _levelxxxxxxxxxxxxxxxxxx) {
                  _levelxxxxxxxxxxxxxxxxxx.sendParticles(ParticleTypes.POOF, x + 0.5, y + 1.0, z + 0.5, 10, 0.5, 0.5, 0.5, 0.1);
               }

               if (world instanceof ServerLevel _levelxxxxxxxxxxxxxxxxxx) {
                  _levelxxxxxxxxxxxxxxxxxx.sendParticles(
                     (SimpleParticleType)BornInChaosV1ModParticleTypes.RITUAL.get(), x + 0.5, y + 1.0, z + 0.5, 6, 0.5, 0.4, 0.5, 0.1
                  );
               }

               if (itemstack.getItem() instanceof BonescallerStaffItem) {
                  CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckoAnim", "invocation_left"));
               }
            }
         } else if ((entity instanceof LivingEntity _entGetArmorxxxxxxxxx ? _entGetArmorxxxxxxxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY)
                  .getItem()
               == BornInChaosV1ModItems.SPIRITUAL_GUIDE_SOMBRERO_HELMET.get()
            && (entity instanceof LivingEntity _livEntxxx ? _livEntxxx.getOffhandItem() : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.BONESCALLER_STAFF.get()) {
            if (world.getBlockState(BlockPos.containing(x, y, z)).canOcclude()
               && !world.getBlockState(BlockPos.containing(x, y + 1.0, z)).canOcclude()
               && !(entity instanceof LivingEntity _livEnt90 && _livEnt90.hasEffect(BornInChaosV1ModMobEffects.MAGIC_DEPLETION))) {
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.MAGIC_DEPLETION, 560, 0));
               }

               if (world instanceof ServerLevel _levelxxxxxxxxxxxxxxxxxx) {
                  (entity instanceof LivingEntity _livEntxxxx ? _livEntxxxx.getOffhandItem() : ItemStack.EMPTY)
                     .hurtAndBreak(1, _levelxxxxxxxxxxxxxxxxxx, null, _stkprov -> {});
               }

               if (world instanceof ServerLevel _levelxxxxxxxxxxxxxxxxxx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CONTROLLED_SPIRITUAL_ASSISTANT.get())
                     .spawn(_levelxxxxxxxxxxxxxxxxxx, BlockPos.containing(x + 0.2, y + 1.0, z + 0.5), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
                  }
               }

               if (world instanceof ServerLevel _levelxxxxxxxxxxxxxxxxxxx) {
                  Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CONTROLLED_SPIRITUAL_ASSISTANT.get())
                     .spawn(_levelxxxxxxxxxxxxxxxxxxx, BlockPos.containing(x + 0.8, y + 1.0, z + 0.5), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
                  }
               }

               if (world instanceof Level _levelxxxxxxxxxxxxxxxxxxxx) {
                  if (!_levelxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
                     _levelxxxxxxxxxxxxxxxxxxxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.skeleton.ambient")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _levelxxxxxxxxxxxxxxxxxxxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.skeleton.ambient")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }

               if (world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxx) {
                  if (!_levelxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
                     _levelxxxxxxxxxxxxxxxxxxxxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.evoker.prepare_attack")),
                        SoundSource.NEUTRAL,
                        0.5F,
                        1.0F
                     );
                  } else {
                     _levelxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.evoker.prepare_attack")),
                        SoundSource.NEUTRAL,
                        0.5F,
                        1.0F,
                        false
                     );
                  }
               }

               if (world instanceof ServerLevel _levelxxxxxxxxxxxxxxxxxxxxxx) {
                  _levelxxxxxxxxxxxxxxxxxxxxxx.sendParticles(
                     (SimpleParticleType)BornInChaosV1ModParticleTypes.STIMULATINGSMOKE.get(), x + 0.5, y + 1.0, z + 0.5, 10, 0.5, 0.5, 0.5, 0.1
                  );
               }

               if (world instanceof ServerLevel _levelxxxxxxxxxxxxxxxxxxxxxx) {
                  _levelxxxxxxxxxxxxxxxxxxxxxx.sendParticles(
                     (SimpleParticleType)BornInChaosV1ModParticleTypes.RITUAL.get(), x + 0.5, y + 1.0, z + 0.5, 6, 0.5, 0.4, 0.5, 0.1
                  );
               }

               if (itemstack.getItem() instanceof BonescallerStaffItem) {
                  CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckoAnim", "invocation_left"));
               }
            }
         } else if ((entity instanceof LivingEntity _livEntxxxx ? _livEntxxxx.getOffhandItem() : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.BONESCALLER_STAFF.get()
            && world.getBlockState(BlockPos.containing(x, y, z)).canOcclude()
            && !world.getBlockState(BlockPos.containing(x, y + 1.0, z)).canOcclude()
            && !(entity instanceof LivingEntity _livEnt106 && _livEnt106.hasEffect(BornInChaosV1ModMobEffects.MAGIC_DEPLETION))) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.MAGIC_DEPLETION, 300, 0));
            }

            if (world instanceof ServerLevel _levelxxxxxxxxxxxxxxxxxxxxxx) {
               (entity instanceof LivingEntity _livEntxxxxx ? _livEntxxxxx.getOffhandItem() : ItemStack.EMPTY)
                  .hurtAndBreak(1, _levelxxxxxxxxxxxxxxxxxxxxxx, null, _stkprov -> {});
            }

            if (world instanceof ServerLevel _levelxxxxxxxxxxxxxxxxxxxxxx) {
               Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CONTROLLED_BABY_SKELETON.get())
                  .spawn(_levelxxxxxxxxxxxxxxxxxxxxxx, BlockPos.containing(x + 0.2, y + 1.0, z + 0.5), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }

            if (world instanceof ServerLevel _levelxxxxxxxxxxxxxxxxxxxxxxx) {
               Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.CONTROLLED_BABY_SKELETON.get())
                  .spawn(_levelxxxxxxxxxxxxxxxxxxxxxxx, BlockPos.containing(x + 0.8, y + 1.0, z + 0.5), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }

            if (world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxx) {
               if (!_levelxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
                  _levelxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.skeleton.ambient")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.skeleton.ambient")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof Level _levelxxxxxxxxxxxxxxxxxxxxxxxxx) {
               if (!_levelxxxxxxxxxxxxxxxxxxxxxxxxx.isClientSide()) {
                  _levelxxxxxxxxxxxxxxxxxxxxxxxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.evoker.prepare_attack")),
                     SoundSource.NEUTRAL,
                     0.5F,
                     1.0F
                  );
               } else {
                  _levelxxxxxxxxxxxxxxxxxxxxxxxxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.evoker.prepare_attack")),
                     SoundSource.NEUTRAL,
                     0.5F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelxxxxxxxxxxxxxxxxxxxxxxxxxx) {
               _levelxxxxxxxxxxxxxxxxxxxxxxxxxx.sendParticles(ParticleTypes.POOF, x + 0.5, y + 1.0, z + 0.5, 10, 0.5, 0.5, 0.5, 0.1);
            }

            if (world instanceof ServerLevel _levelxxxxxxxxxxxxxxxxxxxxxxxxxx) {
               _levelxxxxxxxxxxxxxxxxxxxxxxxxxx.sendParticles(
                  (SimpleParticleType)BornInChaosV1ModParticleTypes.RITUAL.get(), x + 0.5, y + 1.0, z + 0.5, 6, 0.5, 0.4, 0.5, 0.1
               );
            }

            if (itemstack.getItem() instanceof BonescallerStaffItem) {
               CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckoAnim", "invocation_left"));
            }
         }
      }
   }
}

package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.entity.StaffofMagicArrowsProjectileEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.mcreator.borninchaosv.item.StaffOfMagicArrowsAItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public class StaffofMagicArrowsPriIspolzovaniiStrielkovoghoPriedmietaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
      if (entity != null) {
         if (entity instanceof Player _playerHasItem
            && _playerHasItem.getInventory().contains(new ItemStack((ItemLike)BornInChaosV1ModItems.SPIRITUAL_DUST.get()))) {
            if (!(new Object() {
                     public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                           return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else {
                           return _ent.level().isClientSide() && _ent instanceof Player _playerx
                              ? Minecraft.getInstance().getConnection().getPlayerInfo(_playerx.getGameProfile().getId()) != null
                                 && Minecraft.getInstance().getConnection().getPlayerInfo(_playerx.getGameProfile().getId()).getGameMode() == GameType.CREATIVE
                              : false;
                        }
                     }
                  })
                  .checkGamemode(entity)
               && entity instanceof Player _player) {
               ItemStack _stktoremove = new ItemStack((ItemLike)BornInChaosV1ModItems.SPIRITUAL_DUST.get());
               _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
            }

            Level projectileLevel = entity.level();
            if (!projectileLevel.isClientSide()) {
               Projectile _entityToSpawn = (new Object() {
                     public Projectile getArrow(Level level, Entity shooter, float damage, final int knockback, final byte piercing) {
                        AbstractArrow entityToSpawn = new StaffofMagicArrowsProjectileEntity(
                           (EntityType)BornInChaosV1ModEntities.STAFFOF_MAGIC_ARROWS_PROJECTILE.get(), level
                        ) {
                           public byte getPierceLevel() {
                              return piercing;
                           }

                           @Override
                           protected void doKnockback(LivingEntity livingEntity, DamageSource damageSource) {
                              if (knockback > 0) {
                                 double d1 = Math.max(0.0, 1.0 - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                                 Vec3 vec3 = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(knockback * 0.6 * d1);
                                 if (vec3.lengthSqr() > 0.0) {
                                    livingEntity.push(vec3.x, 0.1, vec3.z);
                                 }
                              }
                           }
                        };
                        entityToSpawn.setOwner(shooter);
                        entityToSpawn.setBaseDamage(damage);
                        entityToSpawn.setSilent(true);
                        return entityToSpawn;
                     }
                  })
                  .getArrow(projectileLevel, entity, 3.0F, 1, (byte)5);
               _entityToSpawn.setPos(entity.getX(), entity.getEyeY() - 0.1, entity.getZ());
               _entityToSpawn.shoot(entity.getLookAngle().x, entity.getLookAngle().y, entity.getLookAngle().z, 5.0F, 0.0F);
               projectileLevel.addFreshEntity(_entityToSpawn);
            }

            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:magic_staff_shoot")),
                     SoundSource.NEUTRAL,
                     0.6F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:magic_staff_shoot")),
                     SoundSource.NEUTRAL,
                     0.6F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.RITUAL.get(), x, y + 1.0, z, 5, 0.3, 0.3, 0.3, 0.1);
            }

            if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.STAFF_OF_MAGIC_ARROWS.get()) {
               if (itemstack.getItem() instanceof StaffOfMagicArrowsAItem) {
                  CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckoAnim", "shot_right"));
               }

               if (entity instanceof LivingEntity _entity) {
                  _entity.swing(InteractionHand.MAIN_HAND, true);
               }

               if (world instanceof ServerLevel _levelx) {
                  (entity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).hurtAndBreak(1, _levelx, null, _stkprov -> {});
               }
            } else if ((entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.STAFF_OF_MAGIC_ARROWS.get()) {
               if (itemstack.getItem() instanceof StaffOfMagicArrowsAItem) {
                  CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckoAnim", "shot_left"));
               }

               if (entity instanceof LivingEntity _entity) {
                  _entity.swing(InteractionHand.OFF_HAND, true);
               }

               if (world instanceof ServerLevel _levelx) {
                  (entity instanceof LivingEntity _livEntx ? _livEntx.getOffhandItem() : ItemStack.EMPTY).hurtAndBreak(1, _levelx, null, _stkprov -> {});
               }
            }

            if ((entity instanceof LivingEntity _entGetArmorxxx ? _entGetArmorxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
                  == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_HELMET.get()
               && (entity instanceof LivingEntity _entGetArmorxx ? _entGetArmorxx.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).getItem()
                  == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_LEGGINGS.get()
               && (entity instanceof LivingEntity _entGetArmorx ? _entGetArmorx.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem()
                  == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_CHESTPLATE.get()
               && (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).getItem()
                  == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_BOOTS.get()) {
               if (entity instanceof Player _player) {
                  _player.getCooldowns().addCooldown((Item)BornInChaosV1ModItems.STAFF_OF_MAGIC_ARROWS.get(), 25);
               }
            } else if (entity instanceof Player _player) {
               _player.getCooldowns().addCooldown((Item)BornInChaosV1ModItems.STAFF_OF_MAGIC_ARROWS.get(), 40);
            }
         } else if (!(
            entity instanceof Player _playerHasItem
               && _playerHasItem.getInventory().contains(new ItemStack((ItemLike)BornInChaosV1ModItems.SPIRITUAL_DUST.get()))
         )) {
            Level projectileLevelx = entity.level();
            if (!projectileLevelx.isClientSide()) {
               Projectile _entityToSpawn = (new Object() {
                     public Projectile getArrow(Level level, Entity shooter, float damage, final int knockback, final byte piercing) {
                        AbstractArrow entityToSpawn = new StaffofMagicArrowsProjectileEntity(
                           (EntityType)BornInChaosV1ModEntities.STAFFOF_MAGIC_ARROWS_PROJECTILE.get(), level
                        ) {
                           public byte getPierceLevel() {
                              return piercing;
                           }

                           @Override
                           protected void doKnockback(LivingEntity livingEntity, DamageSource damageSource) {
                              if (knockback > 0) {
                                 double d1 = Math.max(0.0, 1.0 - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                                 Vec3 vec3 = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(knockback * 0.6 * d1);
                                 if (vec3.lengthSqr() > 0.0) {
                                    livingEntity.push(vec3.x, 0.1, vec3.z);
                                 }
                              }
                           }
                        };
                        entityToSpawn.setOwner(shooter);
                        entityToSpawn.setBaseDamage(damage);
                        entityToSpawn.setSilent(true);
                        return entityToSpawn;
                     }
                  })
                  .getArrow(projectileLevelx, entity, 1.8F, 1, (byte)5);
               _entityToSpawn.setPos(entity.getX(), entity.getEyeY() - 0.1, entity.getZ());
               _entityToSpawn.shoot(entity.getLookAngle().x, entity.getLookAngle().y, entity.getLookAngle().z, 5.0F, 0.0F);
               projectileLevelx.addFreshEntity(_entityToSpawn);
            }

            if (world instanceof Level _levelx) {
               if (!_levelx.isClientSide()) {
                  _levelx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:magic_staff_shoot")),
                     SoundSource.NEUTRAL,
                     0.6F,
                     1.0F
                  );
               } else {
                  _levelx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:magic_staff_shoot")),
                     SoundSource.NEUTRAL,
                     0.6F,
                     1.0F,
                     false
                  );
               }
            }

            if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.STAFF_OF_MAGIC_ARROWS.get()) {
               if (itemstack.getItem() instanceof StaffOfMagicArrowsAItem) {
                  CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckoAnim", "shot_right"));
               }

               if (entity instanceof LivingEntity _entity) {
                  _entity.swing(InteractionHand.MAIN_HAND, true);
               }

               if (world instanceof ServerLevel _levelxx) {
                  (entity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).hurtAndBreak(1, _levelxx, null, _stkprov -> {});
               }
            } else if ((entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.STAFF_OF_MAGIC_ARROWS.get()) {
               if (itemstack.getItem() instanceof StaffOfMagicArrowsAItem) {
                  CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckoAnim", "shot_left"));
               }

               if (entity instanceof LivingEntity _entity) {
                  _entity.swing(InteractionHand.OFF_HAND, true);
               }

               if (world instanceof ServerLevel _levelxx) {
                  (entity instanceof LivingEntity _livEntx ? _livEntx.getOffhandItem() : ItemStack.EMPTY).hurtAndBreak(1, _levelxx, null, _stkprov -> {});
               }
            }

            if ((entity instanceof LivingEntity _entGetArmorxxxxxxx ? _entGetArmorxxxxxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
                  == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_HELMET.get()
               && (entity instanceof LivingEntity _entGetArmorxxxxxx ? _entGetArmorxxxxxx.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).getItem()
                  == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_LEGGINGS.get()
               && (entity instanceof LivingEntity _entGetArmorxxxxx ? _entGetArmorxxxxx.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem()
                  == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_CHESTPLATE.get()
               && (entity instanceof LivingEntity _entGetArmorxxxx ? _entGetArmorxxxx.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).getItem()
                  == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_BOOTS.get()) {
               if (entity instanceof Player _player) {
                  _player.getCooldowns().addCooldown((Item)BornInChaosV1ModItems.STAFF_OF_MAGIC_ARROWS.get(), 25);
               }
            } else if (entity instanceof Player _player) {
               _player.getCooldowns().addCooldown((Item)BornInChaosV1ModItems.STAFF_OF_MAGIC_ARROWS.get(), 40);
            }
         }
      }
   }
}

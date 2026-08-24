package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.entity.PumpkinStaffProjectileEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.item.PumpkinstaffaItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public class PumpkinStaffAtacProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
      if (entity != null) {
         Level projectileLevel = entity.level();
         if (!projectileLevel.isClientSide()) {
            Projectile _entityToSpawn = (new Object() {
               public Projectile getArrow(Level level, Entity shooter, float damage, final int knockback, final byte piercing) {
                  AbstractArrow entityToSpawn = new PumpkinStaffProjectileEntity((EntityType)BornInChaosV1ModEntities.PUMPKIN_STAFF_PROJECTILE.get(), level) {
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
            }).getArrow(projectileLevel, entity, 2.5F, 1, (byte)0);
            _entityToSpawn.setPos(entity.getX(), entity.getEyeY() - 0.1, entity.getZ());
            _entityToSpawn.shoot(entity.getLookAngle().x, entity.getLookAngle().y, entity.getLookAngle().z, 4.0F, 0.0F);
            projectileLevel.addFreshEntity(_entityToSpawn);
         }

         if (world instanceof Level _level) {
            if (!_level.isClientSide()) {
               _level.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:pumpkin_staff_shoot")),
                  SoundSource.NEUTRAL,
                  0.8F,
                  1.0F
               );
            } else {
               _level.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:pumpkin_staff_shoot")),
                  SoundSource.NEUTRAL,
                  0.8F,
                  1.0F,
                  false
               );
            }
         }

         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == BornInChaosV1ModItems.PUMPKINSTAFFA.get()) {
            if (itemstack.getItem() instanceof PumpkinstaffaItem) {
               CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckoAnim", "shot_right"));
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.swing(InteractionHand.MAIN_HAND, true);
            }

            if (world instanceof ServerLevel _levelx) {
               (entity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).hurtAndBreak(1, _levelx, null, _stkprov -> {});
            }
         } else if ((entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem()
            == BornInChaosV1ModItems.PUMPKINSTAFFA.get()) {
            if (itemstack.getItem() instanceof PumpkinstaffaItem) {
               CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckoAnim", "shot_left"));
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.swing(InteractionHand.OFF_HAND, true);
            }

            if (world instanceof ServerLevel _levelx) {
               (entity instanceof LivingEntity _livEntx ? _livEntx.getOffhandItem() : ItemStack.EMPTY).hurtAndBreak(1, _levelx, null, _stkprov -> {});
            }
         }

         if ((entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
            == BornInChaosV1ModItems.LORD_PUMPKINHEADS_HAT_HELMET.get()) {
            if (entity instanceof Player _player) {
               _player.getCooldowns().addCooldown((Item)BornInChaosV1ModItems.PUMPKINSTAFFA.get(), 25);
            }
         } else if (entity instanceof Player _player) {
            _player.getCooldowns().addCooldown((Item)BornInChaosV1ModItems.PUMPKINSTAFFA.get(), 45);
         }
      }
   }
}

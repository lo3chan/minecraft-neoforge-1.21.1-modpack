package net.mcreator.borninchaosv.entity;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.procedures.StaffofMagicArrowsKoghdaSnariadLietitTaktProcedure;
import net.mcreator.borninchaosv.procedures.StaffofMagicArrowsProjectileKoghdaSnariadPopadaietVSushchnostProcedure;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(
   value = Dist.CLIENT,
   _interface = ItemSupplier.class
)
public class StaffofMagicArrows2ProjectileEntity extends AbstractArrow implements ItemSupplier {
   public static final ItemStack PROJECTILE_ITEM = new ItemStack((ItemLike)BornInChaosV1ModItems.SPIRITUAL_DUST.get());
   private int knockback = 0;

   public StaffofMagicArrows2ProjectileEntity(EntityType<? extends StaffofMagicArrows2ProjectileEntity> type, Level world) {
      super(type, world);
   }

   public StaffofMagicArrows2ProjectileEntity(
      EntityType<? extends StaffofMagicArrows2ProjectileEntity> type, double x, double y, double z, Level world, @Nullable ItemStack firedFromWeapon
   ) {
      super(type, x, y, z, world, PROJECTILE_ITEM, firedFromWeapon);
   }

   public StaffofMagicArrows2ProjectileEntity(
      EntityType<? extends StaffofMagicArrows2ProjectileEntity> type, LivingEntity entity, Level world, @Nullable ItemStack firedFromWeapon
   ) {
      super(type, entity, world, PROJECTILE_ITEM, firedFromWeapon);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getItem() {
      return PROJECTILE_ITEM;
   }

   protected ItemStack getDefaultPickupItem() {
      return new ItemStack((ItemLike)BornInChaosV1ModItems.SPIRITUAL_DUST.get());
   }

   protected void doPostHurtEffects(LivingEntity entity) {
      super.doPostHurtEffects(entity);
      entity.setArrowCount(entity.getArrowCount() - 1);
   }

   public void setKnockback(int knockback) {
      this.knockback = knockback;
   }

   protected void doKnockback(LivingEntity livingEntity, DamageSource damageSource) {
      if (this.knockback > 0.0) {
         double d1 = Math.max(0.0, 1.0 - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
         Vec3 vec3 = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(this.knockback * 0.6 * d1);
         if (vec3.lengthSqr() > 0.0) {
            livingEntity.push(vec3.x, 0.1, vec3.z);
         }
      }
   }

   public void onHitEntity(EntityHitResult entityHitResult) {
      super.onHitEntity(entityHitResult);
      StaffofMagicArrowsProjectileKoghdaSnariadPopadaietVSushchnostProcedure.execute(this.level(), entityHitResult.getEntity());
   }

   public void tick() {
      super.tick();
      StaffofMagicArrowsKoghdaSnariadLietitTaktProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ());
      if (this.inGround) {
         this.discard();
      }
   }

   public static StaffofMagicArrows2ProjectileEntity shoot(Level world, LivingEntity entity, RandomSource source) {
      return shoot(world, entity, source, 1.0F, 2.5, 0);
   }

   public static StaffofMagicArrows2ProjectileEntity shoot(Level world, LivingEntity entity, RandomSource source, float pullingPower) {
      return shoot(world, entity, source, pullingPower * 1.0F, 2.5, 0);
   }

   public static StaffofMagicArrows2ProjectileEntity shoot(Level world, LivingEntity entity, RandomSource random, float power, double damage, int knockback) {
      StaffofMagicArrows2ProjectileEntity entityarrow = new StaffofMagicArrows2ProjectileEntity(
         (EntityType<? extends StaffofMagicArrows2ProjectileEntity>)BornInChaosV1ModEntities.STAFFOF_MAGIC_ARROWS_2_PROJECTILE.get(), entity, world, null
      );
      entityarrow.shoot(entity.getViewVector(1.0F).x, entity.getViewVector(1.0F).y, entity.getViewVector(1.0F).z, power * 2.0F, 0.0F);
      entityarrow.setSilent(true);
      entityarrow.setCritArrow(false);
      entityarrow.setBaseDamage(damage);
      entityarrow.setKnockback(knockback);
      world.addFreshEntity(entityarrow);
      world.playSound(
         null,
         entity.getX(),
         entity.getY(),
         entity.getZ(),
         (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:magic_staff_shoot")),
         SoundSource.PLAYERS,
         1.0F,
         1.0F / (random.nextFloat() * 0.5F + 1.0F) + power / 2.0F
      );
      return entityarrow;
   }

   public static StaffofMagicArrows2ProjectileEntity shoot(LivingEntity entity, LivingEntity target) {
      StaffofMagicArrows2ProjectileEntity entityarrow = new StaffofMagicArrows2ProjectileEntity(
         (EntityType<? extends StaffofMagicArrows2ProjectileEntity>)BornInChaosV1ModEntities.STAFFOF_MAGIC_ARROWS_2_PROJECTILE.get(),
         entity,
         entity.level(),
         null
      );
      double dx = target.getX() - entity.getX();
      double dy = target.getY() + target.getEyeHeight() - 1.1;
      double dz = target.getZ() - entity.getZ();
      entityarrow.shoot(dx, dy - entityarrow.getY() + Math.hypot(dx, dz) * 0.20000000298023224, dz, 2.0F, 12.0F);
      entityarrow.setSilent(true);
      entityarrow.setBaseDamage(2.5);
      entityarrow.setKnockback(0);
      entityarrow.setCritArrow(false);
      entity.level().addFreshEntity(entityarrow);
      entity.level()
         .playSound(
            null,
            entity.getX(),
            entity.getY(),
            entity.getZ(),
            (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:magic_staff_shoot")),
            SoundSource.PLAYERS,
            1.0F,
            1.0F / (RandomSource.create().nextFloat() * 0.5F + 1.0F)
         );
      return entityarrow;
   }
}

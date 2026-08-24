package net.mcreator.borninchaosv.entity;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.procedures.MonstrousEasterEggHitProcedureProcedure;
import net.mcreator.borninchaosv.procedures.MonstrousEasterEggProjectileKazhdyiTikPriPoliotieSnariadaProcedure;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(
   value = Dist.CLIENT,
   _interface = ItemSupplier.class
)
public class MonstrousEasterEggProjectileEntity extends AbstractArrow implements ItemSupplier {
   public static final ItemStack PROJECTILE_ITEM = new ItemStack((ItemLike)BornInChaosV1ModItems.MONSTROUS_EASTER_EGG.get());
   private int knockback = 0;

   public MonstrousEasterEggProjectileEntity(EntityType<? extends MonstrousEasterEggProjectileEntity> type, Level world) {
      super(type, world);
   }

   public MonstrousEasterEggProjectileEntity(
      EntityType<? extends MonstrousEasterEggProjectileEntity> type, double x, double y, double z, Level world, @Nullable ItemStack firedFromWeapon
   ) {
      super(type, x, y, z, world, PROJECTILE_ITEM, firedFromWeapon);
   }

   public MonstrousEasterEggProjectileEntity(
      EntityType<? extends MonstrousEasterEggProjectileEntity> type, LivingEntity entity, Level world, @Nullable ItemStack firedFromWeapon
   ) {
      super(type, entity, world, PROJECTILE_ITEM, firedFromWeapon);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getItem() {
      return PROJECTILE_ITEM;
   }

   protected ItemStack getDefaultPickupItem() {
      return new ItemStack((ItemLike)BornInChaosV1ModItems.MONSTROUS_EASTER_EGG.get());
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
      MonstrousEasterEggHitProcedureProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ());
   }

   public void onHitBlock(BlockHitResult blockHitResult) {
      super.onHitBlock(blockHitResult);
      MonstrousEasterEggHitProcedureProcedure.execute(
         this.level(), blockHitResult.getBlockPos().getX(), blockHitResult.getBlockPos().getY(), blockHitResult.getBlockPos().getZ()
      );
   }

   public void tick() {
      super.tick();
      MonstrousEasterEggProjectileKazhdyiTikPriPoliotieSnariadaProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ());
      if (this.inGround) {
         this.discard();
      }
   }

   public static MonstrousEasterEggProjectileEntity shoot(Level world, LivingEntity entity, RandomSource source) {
      return shoot(world, entity, source, 1.0F, 0.1, 0);
   }

   public static MonstrousEasterEggProjectileEntity shoot(Level world, LivingEntity entity, RandomSource source, float pullingPower) {
      return shoot(world, entity, source, pullingPower * 1.0F, 0.1, 0);
   }

   public static MonstrousEasterEggProjectileEntity shoot(Level world, LivingEntity entity, RandomSource random, float power, double damage, int knockback) {
      MonstrousEasterEggProjectileEntity entityarrow = new MonstrousEasterEggProjectileEntity(
         (EntityType<? extends MonstrousEasterEggProjectileEntity>)BornInChaosV1ModEntities.MONSTROUS_EASTER_EGG_PROJECTILE.get(), entity, world, null
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
         (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.egg.throw")),
         SoundSource.PLAYERS,
         1.0F,
         1.0F / (random.nextFloat() * 0.5F + 1.0F) + power / 2.0F
      );
      return entityarrow;
   }

   public static MonstrousEasterEggProjectileEntity shoot(LivingEntity entity, LivingEntity target) {
      MonstrousEasterEggProjectileEntity entityarrow = new MonstrousEasterEggProjectileEntity(
         (EntityType<? extends MonstrousEasterEggProjectileEntity>)BornInChaosV1ModEntities.MONSTROUS_EASTER_EGG_PROJECTILE.get(), entity, entity.level(), null
      );
      double dx = target.getX() - entity.getX();
      double dy = target.getY() + target.getEyeHeight() - 1.1;
      double dz = target.getZ() - entity.getZ();
      entityarrow.shoot(dx, dy - entityarrow.getY() + Math.hypot(dx, dz) * 0.20000000298023224, dz, 2.0F, 12.0F);
      entityarrow.setSilent(true);
      entityarrow.setBaseDamage(0.1);
      entityarrow.setKnockback(0);
      entityarrow.setCritArrow(false);
      entity.level().addFreshEntity(entityarrow);
      entity.level()
         .playSound(
            null,
            entity.getX(),
            entity.getY(),
            entity.getZ(),
            (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.egg.throw")),
            SoundSource.PLAYERS,
            1.0F,
            1.0F / (RandomSource.create().nextFloat() * 0.5F + 1.0F)
         );
      return entityarrow;
   }
}

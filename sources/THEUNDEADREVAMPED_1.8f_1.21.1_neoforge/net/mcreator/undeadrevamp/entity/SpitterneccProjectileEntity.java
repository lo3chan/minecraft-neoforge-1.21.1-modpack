package net.mcreator.undeadrevamp.entity;

import javax.annotation.Nullable;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.mcreator.undeadrevamp.procedures.SpitterneccProjectileHitsBlockProcedure;
import net.mcreator.undeadrevamp.procedures.SpitterneccProjectileHitsPlayerProcedure;
import net.mcreator.undeadrevamp.procedures.SpitterneccWhileProjectileFlyingTickProcedure;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(
   value = Dist.CLIENT,
   _interface = ItemSupplier.class
)
public class SpitterneccProjectileEntity extends AbstractArrow implements ItemSupplier {
   public static final ItemStack PROJECTILE_ITEM = new ItemStack(Blocks.AIR);
   private int knockback = 0;

   public SpitterneccProjectileEntity(EntityType<? extends SpitterneccProjectileEntity> type, Level world) {
      super(type, world);
   }

   public SpitterneccProjectileEntity(
      EntityType<? extends SpitterneccProjectileEntity> type, double x, double y, double z, Level world, @Nullable ItemStack firedFromWeapon
   ) {
      super(type, x, y, z, world, PROJECTILE_ITEM, firedFromWeapon);
   }

   public SpitterneccProjectileEntity(
      EntityType<? extends SpitterneccProjectileEntity> type, LivingEntity entity, Level world, @Nullable ItemStack firedFromWeapon
   ) {
      super(type, entity, world, PROJECTILE_ITEM, firedFromWeapon);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getItem() {
      return PROJECTILE_ITEM;
   }

   protected ItemStack getDefaultPickupItem() {
      return new ItemStack(Blocks.AIR);
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

   public void playerTouch(Player entity) {
      super.playerTouch(entity);
      SpitterneccProjectileHitsPlayerProcedure.execute(entity);
   }

   public void onHitEntity(EntityHitResult entityHitResult) {
      super.onHitEntity(entityHitResult);
      SpitterneccProjectileHitsPlayerProcedure.execute(entityHitResult.getEntity());
   }

   public void onHitBlock(BlockHitResult blockHitResult) {
      super.onHitBlock(blockHitResult);
      SpitterneccProjectileHitsBlockProcedure.execute(
         this.level(), blockHitResult.getBlockPos().getX(), blockHitResult.getBlockPos().getY(), blockHitResult.getBlockPos().getZ()
      );
   }

   public void tick() {
      super.tick();
      SpitterneccWhileProjectileFlyingTickProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
      if (this.inGround) {
         this.discard();
      }
   }

   public static SpitterneccProjectileEntity shoot(Level world, LivingEntity entity, RandomSource source) {
      return shoot(world, entity, source, 0.3F, 1.0, 0);
   }

   public static SpitterneccProjectileEntity shoot(Level world, LivingEntity entity, RandomSource source, float pullingPower) {
      return shoot(world, entity, source, pullingPower * 0.3F, 1.0, 0);
   }

   public static SpitterneccProjectileEntity shoot(Level world, LivingEntity entity, RandomSource random, float power, double damage, int knockback) {
      SpitterneccProjectileEntity entityarrow = new SpitterneccProjectileEntity(
         (EntityType<? extends SpitterneccProjectileEntity>)UndeadRevamp2ModEntities.SPITTERNECC_PROJECTILE.get(), entity, world, null
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
         (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("ambient.underwater.exit")),
         SoundSource.PLAYERS,
         1.0F,
         1.0F / (random.nextFloat() * 0.5F + 1.0F) + power / 2.0F
      );
      return entityarrow;
   }

   public static SpitterneccProjectileEntity shoot(LivingEntity entity, LivingEntity target) {
      SpitterneccProjectileEntity entityarrow = new SpitterneccProjectileEntity(
         (EntityType<? extends SpitterneccProjectileEntity>)UndeadRevamp2ModEntities.SPITTERNECC_PROJECTILE.get(), entity, entity.level(), null
      );
      double dx = target.getX() - entity.getX();
      double dy = target.getY() + target.getEyeHeight() - 1.1;
      double dz = target.getZ() - entity.getZ();
      entityarrow.shoot(dx, dy - entityarrow.getY() + Math.hypot(dx, dz) * 0.20000000298023224, dz, 0.6F, 12.0F);
      entityarrow.setSilent(true);
      entityarrow.setBaseDamage(1.0);
      entityarrow.setKnockback(0);
      entityarrow.setCritArrow(false);
      entity.level().addFreshEntity(entityarrow);
      entity.level()
         .playSound(
            null,
            entity.getX(),
            entity.getY(),
            entity.getZ(),
            (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("ambient.underwater.exit")),
            SoundSource.PLAYERS,
            1.0F,
            1.0F / (RandomSource.create().nextFloat() * 0.5F + 1.0F)
         );
      return entityarrow;
   }
}

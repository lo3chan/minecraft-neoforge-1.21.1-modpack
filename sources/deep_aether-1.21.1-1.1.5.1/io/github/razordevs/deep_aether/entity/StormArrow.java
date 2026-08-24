package io.github.razordevs.deep_aether.entity;

import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.entity.monster.PassiveWhirlwind;
import io.github.razordevs.deep_aether.init.DAEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class StormArrow extends AbstractArrow {
   public StormArrow(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public StormArrow(LivingEntity pOwner, Level pLevel, ItemStack pPickupItemStack, ItemStack weaponStack) {
      super((EntityType)DAEntities.STORM_ARROW.get(), pOwner, pLevel, pPickupItemStack, weaponStack);
   }

   protected void onHitBlock(BlockHitResult hit) {
      super.onHitBlock(hit);
      PassiveWhirlwind whirlwind = new PassiveWhirlwind((EntityType)AetherEntityTypes.WHIRLWIND.get(), this.level());
      whirlwind.setPos(this.position());
      whirlwind.setLifeLeft(this.level().getRandom().nextInt(512) + 512);
      this.level().addFreshEntity(whirlwind);
      this.discard();
   }

   protected ItemStack getDefaultPickupItem() {
      return new ItemStack(Items.ARROW);
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide()) {
         this.level().addParticle(ParticleTypes.END_ROD, this.getX() + this.random.nextFloat(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
      }
   }

   protected void onHitEntity(EntityHitResult pResult) {
      super.onHitEntity(pResult);
      PassiveWhirlwind whirlwind = new PassiveWhirlwind((EntityType)AetherEntityTypes.WHIRLWIND.get(), this.level());
      whirlwind.setPos(this.position());
      whirlwind.setLifeLeft(this.level().getRandom().nextInt(512) + 512);
      this.level().addFreshEntity(whirlwind);
      this.discard();
   }
}

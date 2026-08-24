package net.cibernet.alchemancy.properties;

import java.util.List;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ShatteringProperty extends Property implements IDataHolder<Boolean> {
   private static final float RADIUS = 3.0F;

   @Override
   public int modifyDurabilityConsumed(
      ItemStack stack, ServerLevel level, @Nullable LivingEntity user, int originalAmount, int resultingAmount, RandomSource random
   ) {
      if (user != null && stack.getMaxDamage() <= stack.getDamageValue() + resultingAmount) {
         this.shatter(level, user, stack);
      }

      return resultingAmount;
   }

   @Override
   public void onEntityItemDestroyed(ItemStack stack, Entity itemEntity, DamageSource cause) {
      if (!itemEntity.isRemoved()) {
         Level level = itemEntity.level();
         this.shatter(level, itemEntity, stack);
         itemEntity.discard();
      }
   }

   @Override
   public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
      if (this.getData(stack)) {
         this.setData(stack, false);
      }
   }

   public void shatter(Level level, Entity source, ItemStack stack) {
      if (!this.getData(stack)) {
         this.setData(stack, true);
         RandomSource rand = level.getRandom();
         List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, source.getBoundingBox().inflate(3.0));
         DamageSource damageSource = activationDamageSource(level, source, source.position());
         if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 20; i++) {
               serverLevel.sendParticles(
                  ParticleTypes.CRIT, source.position().x, source.position().y, source.position().z, 1, 0.0, 0.0, 0.0, rand.nextDouble() * 0.5
               );
            }
         }

         InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> {
            activateByEntity(source, source, stack);

            for (LivingEntity target : entities) {
               if (target.distanceTo(source) <= 3.0F) {
                  ((Property)propertyHolder.value()).onAttack(source, stack, damageSource, target);
               }
            }
         });
         this.setData(stack, false);
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 5482165;
   }

   public Boolean readData(CompoundTag tag) {
      return tag.getBoolean("activated");
   }

   public CompoundTag writeData(final Boolean data) {
      return new CompoundTag() {
         {
            this.putBoolean("activated", data);
         }
      };
   }

   public Boolean getDefaultData() {
      return false;
   }
}

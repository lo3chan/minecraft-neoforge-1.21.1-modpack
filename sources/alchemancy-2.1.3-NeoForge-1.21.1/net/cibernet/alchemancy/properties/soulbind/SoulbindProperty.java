package net.cibernet.alchemancy.properties.soulbind;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.ITintModifier;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SoulbindProperty extends Property implements ITintModifier {
   static ResourceKey<DamageType> SOUL_DAMAGE_KEY = ResourceKey.create(
      Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("alchemancy", "soul_escaped")
   );

   @Override
   public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
      if (!user.level().isClientSide() && user.getRandom().nextFloat() < 0.001F && InfusedPropertiesHelper.hasInfusedProperty(stack, this.asHolder())) {
         Vec3 lookVec = user.getLookAngle().scale(0.5);
         ((ServerLevel)user.level())
            .sendParticles(
               ParticleTypes.SOUL, user.position().x + lookVec.x, user.getEyeY() + lookVec.y, user.position().z + lookVec.z, 1, 0.0, 0.05, 0.0, 0.05
            );
         user.playSound((SoundEvent)SoundEvents.SOUL_ESCAPE.value(), 1.0F, 1.0F);
         InfusedPropertiesHelper.removeProperty(stack, this.asHolder());
         if (user.getRandom().nextFloat() < 0.2F && user instanceof LivingEntity living) {
            living.hurt(new DamageSource(user.damageSources().damageTypes.getHolderOrThrow(SOUL_DAMAGE_KEY)), 3.0F);
         }
      }
   }

   @Override
   public int getTint(ItemStack stack, int tintIndex, int originalTint, int currentTint) {
      float partialSecond = (float)(System.currentTimeMillis() % 2000L) / 1000.0F;
      return ARGB32.lerp(Mth.sin(6.2831855F * (partialSecond / 2.0F)) * 0.5F + 0.5F, currentTint, ARGB32.color(ARGB32.alpha(currentTint), 12910079));
   }

   @Override
   public int getColor(ItemStack stack) {
      float partialSecond = (float)(System.currentTimeMillis() % 2000L) / 1000.0F;
      return ARGB32.lerp(Mth.sin(6.2831855F * (partialSecond / 2.0F)) * 0.5F + 0.5F, 2804175, 12910079);
   }
}

package net.cibernet.alchemancy.properties.special;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.cibernet.alchemancy.client.particle.options.SparkParticleOptions;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.mixin.accessors.LivingEntityAccessor;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.SparklingProperty;
import net.cibernet.alchemancy.registries.AlchemancyParticles;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Tick;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;

public class GustJetProperty extends Property {
   private static final float PARTICLE_SCALE = 1.2F;
   public static final ParticleOptions PARTICLES = new SparkParticleOptions(
      (ParticleType<SparkParticleOptions>)AlchemancyParticles.GUST_DUST.get(), Vec3.fromRGB24(14739199).toVector3f(), 1.2F, false
   );

   @Override
   public void onItemUseTick(LivingEntity user, ItemStack stack, Tick event) {
      Level level = user.level();
      Vec3 eyePos = user.getEyePosition();
      float maxDistance = this.getMaxDistance(stack);
      double distance = level.clip(new ClipContext(eyePos, eyePos.add(user.getLookAngle().scale(maxDistance)), Block.COLLIDER, Fluid.ANY, user))
         .getLocation()
         .distanceTo(eyePos);
      double pow = Mth.lerp(Mth.clamp(distance / maxDistance, 0.0, 1.0), 0.3499999940395355, 0.08500000089406967);
      this.playEffects(
         level,
         user,
         stack,
         eyePos.add(user.getLookAngle()),
         user.getLookAngle(),
         (1.0 - distance / maxDistance) * 1.5 + 0.75,
         (float)(distance / maxDistance),
         0.1F,
         0.1F
      );
      Vec3 movementVector = user.getLookAngle().scale(pow);
      this.pushEntities(level, user, maxDistance, eyePos, user.getLookAngle(), stack);
      if ((!user.isShiftKeyDown() || !user.onGround()) && !this.shouldPull(stack)) {
         user.setDeltaMovement(user.getDeltaMovement().subtract(movementVector));
         user.hasImpulse = true;
         if (movementVector.y() > 0.004999999888241291) {
            user.fallDistance = Math.max(0.0F, user.fallDistance - 10.0F);
         }

         EquipmentSlot slot = user.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
         if (event.getDuration() % 40 == 5) {
            this.damageOrConsumeItem(user, stack, slot, 1);
         }
      }
   }

   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      if (!user.isPassenger() && (slot == EquipmentSlot.FEET || slot == EquipmentSlot.BODY)) {
         float maxDistance = this.getMaxDistance(stack);
         if (((LivingEntityAccessor)user).isJumping()) {
            Level level = user.level();
            Vec3 pos = user.position();
            Vec3 down = new Vec3(0.0, -1.0, 0.0);
            if (!level.isClientSide() && user.tickCount % 20 == 0) {
               this.damageOrConsumeItem(user, stack, slot, 2);
            }

            double distance = level.clip(new ClipContext(pos, pos.add(down.scale(maxDistance)), Block.COLLIDER, Fluid.ANY, user)).getLocation().distanceTo(pos);
            double pow = Mth.lerp(Mth.clamp(distance / maxDistance, 0.0, 1.0), 0.25, 0.06499999761581421);
            Vec3 movementVector = down.scale(pow);
            this.playEffects(level, user, stack, pos, down, 1.0 - distance / maxDistance, (float)(distance / maxDistance), user.getBbWidth(), 0.0F);
            this.pushEntities(level, user, maxDistance, pos, down, stack);
            user.setDeltaMovement(user.getDeltaMovement().subtract(movementVector));
            user.hasImpulse = true;
            if (movementVector.y() > 0.004999999888241291) {
               user.fallDistance = Math.max(0.0F, user.fallDistance - 10.0F);
            }

            if (user.tickCount % 40 == 0) {
               this.damageOrConsumeItem(user, stack, slot, 1);
            }
         }
      }
   }

   private void pushEntities(Level level, LivingEntity user, float pushDistance, Vec3 startPos, Vec3 angle, ItemStack stack) {
      List<Entity> entities = level.getEntities(
         user,
         CommonUtils.boundingBoxAroundPoint(startPos, pushDistance),
         entityx -> entityx.position().distanceToSqr(startPos) <= pushDistance * pushDistance
            && entityx.position().vectorTo(user.position()).normalize().dot(angle) < -0.75
      );
      boolean pull = this.shouldPull(stack);

      for (Entity entity : entities) {
         Vec3 vec = pull
            ? entity.position()
               .subtract(startPos)
               .normalize()
               .scale(Mth.lerp(Mth.clamp(entity.position().distanceTo(startPos) / pushDistance, 0.0, 1.0), -0.15000000596046448, -0.25))
            : entity.position()
               .subtract(startPos)
               .normalize()
               .scale(Mth.lerp(Mth.clamp(entity.position().distanceTo(startPos) / pushDistance, 0.0, 1.0), 0.3499999940395355, 0.8500000238418579));
         entity.setDeltaMovement(user.getDeltaMovement().add(vec));
         entity.hasImpulse = true;
         if (vec.y() > 0.004999999888241291) {
            entity.fallDistance = Math.max(0.0F, user.fallDistance - 10.0F);
         }

         if (!level.isClientSide() && user.getRandom().nextFloat() < 0.2F && entity instanceof LivingEntity living) {
            InfusedPropertiesHelper.forEachProperty(
               stack, propertyHolder -> ((Property)propertyHolder.value()).onAttack(user, stack, user.damageSources().generic(), living)
            );
         }
      }
   }

   private boolean shouldPull(ItemStack stack) {
      return InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.GRAPPLING);
   }

   private float getMaxDistance(ItemStack stack) {
      return InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.EXTENDED) ? 10.0F : 6.0F;
   }

   private void playEffects(
      Level level, LivingEntity user, ItemStack stack, Vec3 effectPosition, Vec3 movementVector, double pow, float soundPitch, float hOff, float vOff
   ) {
      RandomSource random = user.getRandom();
      if (level.isClientSide()) {
         int particleSpeed = 1;
         Optional<ParticleOptions> sparklingParticles = SparklingProperty.getParticles(stack);
         ArrayList<ParticleOptions> propertyParticles = new ArrayList<>();
         InfusedPropertiesHelper.forEachProperty(
            stack,
            propertyHolder -> {
               if (propertyHolder.is(AlchemancyTags.Properties.CHANGES_GUST_JET_WIND_COLOR)) {
                  propertyParticles.add(
                     new SparkParticleOptions(
                        (ParticleType<SparkParticleOptions>)AlchemancyParticles.GUST_DUST.get(),
                        Vec3.fromRGB24(((Property)propertyHolder.value()).getColor(stack)).toVector3f(),
                        1.2F,
                        false
                     )
                  );
               }
            }
         );

         for (int i = 0; i < random.nextInt(3) + 1; i++) {
            ParticleOptions particles = sparklingParticles.orElse(
               propertyParticles.isEmpty() ? PARTICLES : propertyParticles.get(user.getRandom().nextInt(propertyParticles.size()))
            );
            level.addParticle(
               particles,
               effectPosition.x() + (random.nextFloat() - 0.5F) * hOff,
               effectPosition.y() + (random.nextFloat() - 0.5F) * vOff,
               effectPosition.z() + (random.nextFloat() - 0.5F) * hOff,
               movementVector.x() * particleSpeed + user.getDeltaMovement().x() + (random.nextFloat() - 0.5F) * pow,
               movementVector.y() * particleSpeed + user.getDeltaMovement().y() + (random.nextFloat() - 0.5F) * pow,
               movementVector.z() * particleSpeed + user.getDeltaMovement().z() + (random.nextFloat() - 0.5F) * pow
            );
         }
      }

      if (random.nextFloat() > 0.15F) {
         level.playSound(null, user, (SoundEvent)AlchemancySoundEvents.GUST_JET.value(), SoundSource.BLOCKS, 0.25F, soundPitch);
      }
   }

   @Override
   public void onRightClickItem(RightClickItem event) {
      event.getEntity().startUsingItem(event.getHand());
      event.setCancellationResult(InteractionResult.CONSUME);
      event.setCanceled(true);
   }

   @Override
   public Optional<UseAnim> modifyUseAnimation(ItemStack stack, UseAnim original, Optional<UseAnim> current) {
      return current.isEmpty() && original == UseAnim.NONE ? Optional.of(UseAnim.BOW) : current;
   }

   @Override
   public int modifyUseDuration(ItemStack stack, int original, int result) {
      return 72000;
   }

   @Override
   public int getColor(ItemStack stack) {
      return 14739199;
   }
}

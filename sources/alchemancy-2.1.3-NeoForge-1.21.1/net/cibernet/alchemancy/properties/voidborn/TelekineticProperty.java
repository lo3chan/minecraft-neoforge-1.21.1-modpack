package net.cibernet.alchemancy.properties.voidborn;

import java.util.Optional;
import java.util.UUID;
import net.cibernet.alchemancy.mixin.accessors.AbstractArrowAccessor;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.SparklingProperty;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.ColorUtils;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Stop;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Tick;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;

public class TelekineticProperty extends Property implements IDataHolder<UUID> {
   public static final ParticleOptions PARTICLES = new DustColorTransitionOptions(
      Vec3.fromRGB24(4975635).toVector3f(), Vec3.fromRGB24(6714111).toVector3f(), 1.0F
   );

   @Override
   public void onDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, DamageSource damageSource) {
      if (!slot.isArmor() && this.getData(weapon) != this.getDefaultData()) {
         if (user instanceof ServerPlayer player) {
            player.getCooldowns().addCooldown(weapon.getItem(), 20);
         }

         this.removeData(weapon);
         user.stopUsingItem();
      }
   }

   @Override
   public void onRightClickItem(RightClickItem event) {
      Player user = event.getEntity();
      double distance = user.blockInteractionRange() * 3.0;
      if (!user.level().isClientSide) {
         EntityHitResult hit = ProjectileUtil.getEntityHitResult(
            user,
            user.getEyePosition(),
            user.getEyePosition().add(user.getLookAngle().scale(distance)),
            CommonUtils.boundingBoxAroundPoint(user.getEyePosition(), (float)distance),
            EntitySelector.NO_SPECTATORS.and(e -> !e.getType().is(AlchemancyTags.EntityTypes.UNMOVABLE)),
            distance
         );
         if (hit != null) {
            this.setData(event.getItemStack(), hit.getEntity().getUUID());
            event.getEntity().startUsingItem(event.getHand());
            event.setCancellationResult(InteractionResult.CONSUME);
            event.setCanceled(true);
         }
      }
   }

   @Override
   public void onRightClickEntity(EntityInteractSpecific event) {
      if (!event.getTarget().getType().is(AlchemancyTags.EntityTypes.UNMOVABLE)) {
         this.setData(event.getItemStack(), event.getTarget().getUUID());
         event.getEntity().startUsingItem(event.getHand());
         event.setCancellationResult(InteractionResult.CONSUME);
         event.setCanceled(true);
      }
   }

   @Override
   public void onItemUseTick(LivingEntity user, ItemStack stack, Tick event) {
      UUID targetUuid = this.getData(stack);
      if (user.level() instanceof ServerLevel level && targetUuid != null) {
         Entity target = level.getEntity(targetUuid);
         if (target == null) {
            user.stopUsingItem();
         } else {
            Vec3 eyePos = user.getEyePosition();
            double distance = user instanceof Player player ? player.blockInteractionRange() : 5.0;
            Vec3 newDelta = eyePos.add(user.getLookAngle().scale(distance).subtract(0.0, target.getBbHeight() * 0.5F, 0.0))
               .subtract(target.position())
               .scale(0.30000001192092896);
            if (target instanceof AbstractArrow arrow
               && ((AbstractArrowAccessor)arrow).accessInGround()
               && level.noBlockCollision(arrow, arrow.getBoundingBox().move(newDelta))) {
               ((AbstractArrowAccessor)arrow).setInGround(false);
            }

            target.resetFallDistance();
            target.setDeltaMovement(newDelta);
            target.hasImpulse = true;
            target.hurtMarked = true;
            level.sendParticles(
               SparklingProperty.getParticles(stack).orElse(PARTICLES),
               target.getRandomX(1.0),
               target.getRandomY(),
               target.getRandomZ(1.0),
               3,
               0.1,
               0.1,
               0.1,
               0.0
            );
         }
      }
   }

   @Override
   public int modifyUseDuration(ItemStack stack, int original, int result) {
      return 72000;
   }

   @Override
   public void onStopUsingItem(ItemStack stack, LivingEntity user, Stop event) {
      this.removeData(stack);
   }

   @Override
   public int getColor(ItemStack stack) {
      return ColorUtils.sineColorsOverTime(5.0F, 4975635, 6714111);
   }

   @Override
   public Optional<UseAnim> modifyUseAnimation(ItemStack stack, UseAnim original, Optional<UseAnim> current) {
      return Optional.of(UseAnim.BOW);
   }

   public UUID readData(CompoundTag tag) {
      return tag.hasUUID("target") ? tag.getUUID("target") : null;
   }

   public CompoundTag writeData(final UUID data) {
      return new CompoundTag() {
         {
            if (data != null) {
               this.putUUID("target", data);
            }
         }
      };
   }

   public UUID getDefaultData() {
      return null;
   }
}

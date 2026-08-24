package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.client.particle.options.SparkParticleOptions;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.SparklingProperty;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BlinkingProperty extends Property implements IDataHolder<Tuple<Boolean, Integer>> {
   public static final ParticleOptions PARTICLES = new SparkParticleOptions(Vec3.fromRGB24(65535).toVector3f(), 1.0F);
   private static final int MAX_DASHES = 3;
   private static final Tuple<Boolean, Integer> DEFAULT = new Tuple(false, 0);

   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      super.onEquippedTick(user, slot, stack);
      if (!user.isSprinting() || this.getSprinting(stack) || !slot.isArmor() && InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.INTERACTABLE)) {
         if (user.onGround()) {
            this.setDashCount(stack, 0);
         }
      } else {
         this.blink(user, stack, slot);
      }
   }

   @Override
   public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
      if (source != null) {
         if (source instanceof LivingEntity user) {
            this.blink(user, stack, EquipmentSlot.MAINHAND);
         }
      }
   }

   @Override
   public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
      boolean sprinting = user.isSprinting();
      if (sprinting != this.getSprinting(stack)) {
         this.setSprinting(stack, sprinting);
      }
   }

   public void blink(LivingEntity user, ItemStack stack, EquipmentSlot slot) {
      int dashes = this.getDashCount(stack) + 1;
      this.setDashCount(stack, Math.min(dashes, 3));
      if (dashes < 3 + (user.level().isClientSide() ? 1 : 0)) {
         float range = 10 * (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.EXTENDED) ? 2 : 1);

         for (int i = 0; i < range; i++) {
            BlockHitResult hit = user.level()
               .clip(
                  new ClipContext(
                     user.getEyePosition(), user.getEyePosition().add(user.getLookAngle().normalize().scale(range - i)), Block.OUTLINE, Fluid.NONE, user
                  )
               );
            Vec3 hitPos = hit.getLocation().subtract(hit.getLocation().subtract(user.getEyePosition()).normalize().scale(user.getBbWidth() * 0.5F));
            BlockHitResult verticalHit = user.level()
               .clip(new ClipContext(hitPos, hitPos.subtract(0.0, user.getEyeHeight(), 0.0), Block.OUTLINE, Fluid.NONE, user));
            if (user.level()
               .noBlockCollision(
                  user,
                  user.getLocalBoundsForPose(Pose.SWIMMING).move(verticalHit.getLocation()).move(-user.getBbWidth() * 0.5F, 0.0, -user.getBbWidth() * 0.5F)
               )) {
               playParticles(user, verticalHit.getLocation(), stack, 20, 5);
               user.moveTo(verticalHit.getLocation());
               user.setDeltaMovement(user.getLookAngle().normalize().scale(user.getDeltaMovement().length()));
               this.damageOrConsumeItem(user, stack, slot, 5);
               break;
            }
         }
      }
   }

   public static void playParticles(Entity user, Vec3 destination, ItemStack stack, int segments, int layers) {
      if (user.level() instanceof ServerLevel serverLevel) {
         for (int i = 0; i < segments; i++) {
            int currentLayers = layers - Math.max(0, layers - Math.ceilDiv(i, 3));

            for (int y = 0; y < currentLayers; y++) {
               Vec3 vec = user.position().lerp(destination, (double)i / segments);
               double yy = (double)y / layers * user.getBbHeight() + user.getBbHeight() / (currentLayers + 1);
               serverLevel.sendParticles(SparklingProperty.getParticles(stack).orElse(PARTICLES), vec.x, vec.y + yy, vec.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
         }
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return ARGB32.lerp(this.getDashCount(stack) / 3.0F, ColorUtils.interpolateColorsOverTime(0.5F, 65535, 9240575), 32928);
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      return super.getDisplayText(stack).copy().withStyle(ChatFormatting.BOLD);
   }

   private void setDashCount(ItemStack stack, int count) {
      this.setData(stack, new Tuple((Boolean)this.getData(stack).getA(), count));
   }

   private int getDashCount(ItemStack stack) {
      return (Integer)this.getData(stack).getB();
   }

   private void setSprinting(ItemStack stack, boolean sprinting) {
      this.setData(stack, new Tuple(sprinting, (Integer)this.getData(stack).getB()));
   }

   private boolean getSprinting(ItemStack stack) {
      return (Boolean)this.getData(stack).getA();
   }

   public Tuple<Boolean, Integer> readData(CompoundTag tag) {
      return new Tuple(tag.getBoolean("sprinting"), Math.clamp(tag.getInt("dash_count"), 0, 3));
   }

   public CompoundTag writeData(final Tuple<Boolean, Integer> data) {
      return new CompoundTag() {
         {
            this.putBoolean("sprinting", (Boolean)data.getA());
            this.putInt("dash_count", (Integer)data.getB());
         }
      };
   }

   public Tuple<Boolean, Integer> getDefaultData() {
      return DEFAULT;
   }
}

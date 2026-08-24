package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.client.particle.options.SparkParticleOptions;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.SparklingProperty;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyParticles;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class DashingProperty extends Property implements IDataHolder<Tuple<Boolean, Integer>> {
   private final int maxDashes;
   private final int[] colors;
   private final float dashStrength;
   public static final ParticleOptions CRYSTAL_PARTICLES = new SparkParticleOptions(
      (ParticleType<SparkParticleOptions>)AlchemancyParticles.CLOUD_SMOKE.get(), Vec3.fromRGB24(14186495).toVector3f(), 2.0F, false
   );
   public static final ParticleOptions CLOUD_PARTICLES = new SparkParticleOptions(
      (ParticleType<SparkParticleOptions>)AlchemancyParticles.CLOUD_SMOKE.get(), Vec3.fromRGB24(5551359).toVector3f(), 2.0F, false
   );
   private static final Tuple<Boolean, Integer> DEFAULT = new Tuple(false, 0);

   public DashingProperty(float dashStrength, int... colors) {
      this.maxDashes = colors.length - 1;
      this.colors = colors;
      this.dashStrength = dashStrength;
   }

   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      super.onEquippedTick(user, slot, stack);
      if (user.isSprinting() && !this.getSprinting(stack)) {
         if (slot.isArmor() || !InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.INTERACTABLE)) {
            this.dash(user, stack, slot);
         }
      } else if (user.onGround()) {
         this.setDashCount(stack, 0);
      }
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

   @Override
   public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
      if (source != null) {
         if (source instanceof LivingEntity user) {
            this.dash(user, stack, EquipmentSlot.MAINHAND);
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

   public void dash(LivingEntity user, ItemStack stack, EquipmentSlot slot) {
      int dashes = this.getDashCount(stack);
      float dashStrength = this.dashStrength * user.getSpeed() * 5.0F;
      if (dashes < this.maxDashes) {
         user.setDeltaMovement(
            user.getLookAngle()
               .normalize()
               .scale(dashStrength)
               .add(user.getLookAngle().normalize().scale(Math.min(user.getDeltaMovement().length(), 7.5)).scale(0.4000000059604645))
         );
         this.playParticles(user, stack, dashes);
         this.damageItem(user, stack, slot, 2);
         this.setDashCount(stack, dashes + 1);
      }
   }

   public void playParticles(Entity user, ItemStack stack, int dashes) {
      Vec3 particleSpeed = user.getDeltaMovement().scale(0.20000000298023224);

      for (int i = 0; i < 15; i++) {
         user.level()
            .addParticle(
               SparklingProperty.getParticles(stack).orElse(dashes < this.maxDashes - 1 ? CRYSTAL_PARTICLES : CLOUD_PARTICLES),
               user.getRandomX(1.2000000476837158),
               user.getY(user.getRandom().nextFloat() * 0.6F),
               user.getRandomZ(1.2000000476837158),
               particleSpeed.x(),
               particleSpeed.y(),
               particleSpeed.z()
            );
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return this.colors[Math.min(this.colors.length - 1, this.getDashCount(stack))];
   }

   public Tuple<Boolean, Integer> readData(CompoundTag tag) {
      return new Tuple(tag.getBoolean("sprinting"), Math.clamp(tag.getInt("dash_count"), 0, this.maxDashes));
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

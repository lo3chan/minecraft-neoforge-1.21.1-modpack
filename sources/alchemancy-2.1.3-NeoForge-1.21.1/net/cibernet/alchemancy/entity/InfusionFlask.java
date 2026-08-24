package net.cibernet.alchemancy.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.mixin.accessors.ClientLevelAccessor;
import net.cibernet.alchemancy.mixin.accessors.LevelRendererAccessor;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyEntities;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class InfusionFlask extends ThrowableItemProjectile implements ItemSupplier {
   public static final double SPLASH_RANGE = 4.0;
   private static final int MAX_AFFECTED_ITEMS = 32;

   public InfusionFlask(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
      super(entityType, level);
   }

   public InfusionFlask(Level level, double x, double y, double z) {
      super((EntityType)AlchemancyEntities.INFUSION_FLASK.get(), x, y, z, level);
   }

   public InfusionFlask(Level level, LivingEntity shooter) {
      super((EntityType)AlchemancyEntities.INFUSION_FLASK.get(), shooter, level);
   }

   public Component getName() {
      Component component = this.getCustomName();
      return (Component)(component != null ? component : Component.translatable(this.getItem().getDescriptionId()));
   }

   protected void onHit(HitResult result) {
      super.onHit(result);
      if (!this.level().isClientSide) {
         this.playSound(SoundEvents.SPLASH_POTION_BREAK, 1.0F, this.getRandom().nextFloat() * 0.1F + 0.9F);
         this.level().broadcastEntityEvent(this, (byte)3);
         List<Holder<Property>> infusions = this.getInfusions();
         if (!infusions.isEmpty()) {
            this.applySplash(infusions);
            this.level().broadcastEntityEvent(this, (byte)4);
         }

         this.discard();
      }
   }

   private List<Holder<Property>> getInfusions() {
      ItemStack itemstack = this.getItem().copy();
      ArrayList<Holder<Property>> infusions = new ArrayList<>(
         itemstack.is(AlchemancyTags.Items.DISABLES_INFUSION_ABILITIES)
            ? InfusedPropertiesHelper.getInfusedProperties(itemstack)
            : InfusedPropertiesHelper.getStoredProperties(itemstack)
      );
      infusions.removeIf(propertyHolder -> propertyHolder.is(AlchemancyTags.Properties.IGNORED_BY_INFUSION_FLASK));
      return infusions;
   }

   private void playSplashEffects() {
      if (this.level().isClientSide()) {
         ItemStack itemstack = this.getItem().copy();

         for (Holder<Property> infusion : this.getInfusions()) {
            int color = ((Property)infusion.value()).getColor(itemstack);
            float r = (color >> 16 & 0xFF) / 255.0F;
            float g = (color >> 8 & 0xFF) / 255.0F;
            float b = (color & 0xFF) / 255.0F;
            ParticleOptions particleoptions = ParticleTypes.INSTANT_EFFECT;
            Vec3 vec3 = this.position();

            for (int i2 = 0; i2 < 100; i2++) {
               double power = this.getRandom().nextDouble() * 4.0;
               double angle = this.getRandom().nextDouble() * 3.141592653589793 * 2.0;
               double xSpeed = Math.cos(angle) * power;
               double ySpeed = 0.01 + this.getRandom().nextDouble() * 0.5;
               double zSpeed = Math.sin(angle) * power;
               Particle particle = ((LevelRendererAccessor)((ClientLevelAccessor)this.level()).getLevelRenderer())
                  .invokeAddParticleInternal(
                     particleoptions,
                     particleoptions.getType().getOverrideLimiter(),
                     vec3.x + xSpeed * 0.1,
                     vec3.y + 0.3,
                     vec3.z + zSpeed * 0.1,
                     xSpeed,
                     ySpeed,
                     zSpeed
                  );
               if (particle != null) {
                  float colorOff = 0.75F + this.getRandom().nextFloat() * 0.25F;
                  particle.setColor(r * colorOff, g * colorOff, b * colorOff);
                  particle.setPower((float)power);
               }
            }
         }
      }
   }

   private ParticleOptions getParticle() {
      ItemStack itemstack = this.getItem();
      return new ItemParticleOption(ParticleTypes.ITEM, itemstack.isEmpty() ? this.getDefaultItem().getDefaultInstance() : itemstack);
   }

   public void handleEntityEvent(byte id) {
      if (id == 3) {
         ParticleOptions particleoptions = this.getParticle();

         for (int j = 0; j < 8; j++) {
            this.level()
               .addParticle(
                  particleoptions,
                  this.position().x,
                  this.position().y,
                  this.position().z,
                  this.random.nextGaussian() * 0.15,
                  this.random.nextDouble() * 0.2,
                  this.random.nextGaussian() * 0.15
               );
         }
      } else if (id == 4) {
         this.playSplashEffects();
      }
   }

   protected double getDefaultGravity() {
      return 0.05;
   }

   private void applySplash(List<Holder<Property>> infusions) {
      AABB aabb = this.getBoundingBox().inflate(4.0, 2.0, 4.0);
      int items = 0;

      for (LivingEntity living : this.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
         for (EquipmentSlot slot : EquipmentSlot.values()) {
            AtomicBoolean success = new AtomicBoolean(false);
            ItemStack entityStack = living.getItemBySlot(slot);
            if (!entityStack.is(AlchemancyTags.Items.IGNORED_BY_INFUSION_FLASK)) {
               ItemStack splitStack = entityStack.split(32 - items);
               ItemStack itemStack = this.infuseItem(splitStack, infusions, success);
               if (!entityStack.isEmpty()) {
                  if (!itemStack.isEmpty()) {
                     this.level().addFreshEntity(new ItemEntity(this.level(), living.getX(), living.getEyeY(), living.getZ(), itemStack));
                  }
               } else {
                  living.setItemSlot(slot, itemStack);
               }

               if (success.get()) {
                  items += splitStack.getCount();
                  if (items >= 32) {
                     return;
                  }
               }
            }
         }
      }

      for (ItemEntity itemEntity : this.level().getEntitiesOfClass(ItemEntity.class, aabb)) {
         ItemStack entityStack = itemEntity.getItem();
         if (!entityStack.is(AlchemancyTags.Items.IGNORED_BY_INFUSION_FLASK)) {
            AtomicBoolean success = new AtomicBoolean(false);
            ItemStack splitStackx = entityStack.split(32 - items);
            ItemStack itemStackx = this.infuseItem(splitStackx, infusions, success);
            if (!entityStack.isEmpty()) {
               if (!itemStackx.isEmpty()) {
                  ItemEntity newItem = new ItemEntity(this.level(), itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), itemStackx);
                  newItem.setDefaultPickUpDelay();
                  this.level().addFreshEntity(newItem);
               }
            } else if (itemStackx.isEmpty()) {
               itemEntity.discard();
            } else {
               itemEntity.setItem(itemStackx);
            }

            if (success.get()) {
               items += splitStackx.getCount();
               if (items >= 32) {
                  return;
               }
            }
         }
      }
   }

   private ItemStack infuseItem(ItemStack itemStack, List<Holder<Property>> infusions, AtomicBoolean successful) {
      if (itemStack.is(AlchemancyTags.Items.IMMUNE_TO_INFUSIONS)) {
         return itemStack;
      } else {
         boolean perform = false;
         ForgeRecipeGrid grid = new ForgeRecipeGrid(itemStack);

         for (Holder<Property> property : List.copyOf(infusions)) {
            if (InfusedPropertiesHelper.canInfuseWithProperty(itemStack, property)
               && ((Property)property.value()).onInfusedByDormantProperty(itemStack, this.getItem(), grid, infusions, false)) {
               perform = true;
            }
         }

         if (perform) {
            infusions.removeIf(propertyHolder -> !InfusedPropertiesHelper.canInfuseWithProperty(itemStack, (Holder<Property>)propertyHolder));
            InfusedPropertiesHelper.addProperties(itemStack, infusions);
            InfusedPropertiesHelper.forEachProperty(itemStack, propertyHolder -> {
               if (!propertyHolder.is(AlchemancyTags.Properties.CANNOT_CLONE_DATA) && propertyHolder.value() instanceof IDataHolder<?> dataHolder) {
                  dataHolder.combineDataAndSet(itemStack, this.getItem());
               }
            }, false);
            itemStack = ForgeRecipeGrid.resolveInteractions(itemStack, this.level());
            successful.set(true);
         }

         return itemStack;
      }
   }

   protected Item getDefaultItem() {
      return AlchemancyItems.INFUSION_FLASK.asItem();
   }
}

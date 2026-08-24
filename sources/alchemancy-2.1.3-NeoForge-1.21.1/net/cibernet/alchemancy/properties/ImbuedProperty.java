package net.cibernet.alchemancy.properties;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;

public class ImbuedProperty extends Property implements IDataHolder<MobEffectInstance> {
   @Override
   public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target) {
      this.consumeAndApply(target, weapon, 200);
   }

   @Override
   public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
      if (target instanceof LivingEntity living) {
         this.consumeAndApply(living, stack, 200);
      }
   }

   @Override
   public boolean onInfusedByDormantProperty(
      ItemStack stack, ItemStack propertySource, ForgeRecipeGrid grid, List<Holder<Property>> propertiesToAdd, boolean consumeItem
   ) {
      MobEffectInstance effect = getPotionEffect(propertySource);
      if (effect == null) {
         effect = this.getData(propertySource);
         if (effect == null) {
            return false;
         }
      }

      if (consumeItem) {
         this.setData(stack, effect);
      }

      return true;
   }

   @Override
   public Collection<ItemStack> populateCreativeTab(DeferredItem<Item> capsuleItem, Holder<Property> holder) {
      ArrayList<ItemStack> capsules = new ArrayList<>();
      ArrayList<Holder<MobEffect>> alreadyAdded = new ArrayList<>();

      for (Potion potion : BuiltInRegistries.POTION) {
         if (potion.getEffects().size() == 1) {
            MobEffectInstance effectInstance = (MobEffectInstance)potion.getEffects().getFirst();
            if (!alreadyAdded.contains(effectInstance.getEffect())) {
               ItemStack stack = InfusedPropertiesHelper.createPropertyCapsule(this.asHolder());
               this.setData(stack, effectInstance);
               alreadyAdded.add(effectInstance.getEffect());
               capsules.add(stack);
            }
         }
      }

      return capsules;
   }

   @Override
   public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event) {
      if (rayTraceResult.getType() == Type.ENTITY
         && rayTraceResult instanceof EntityHitResult entityHitResult
         && entityHitResult.getEntity() instanceof LivingEntity target) {
         this.consumeAndApply(target, stack, -1);
      }
   }

   @Override
   public boolean cluelessCanReset() {
      return false;
   }

   public boolean consumeAndApply(LivingEntity target, ItemStack stack, int ticks) {
      MobEffectInstance data = this.getData(stack);
      if (data == null) {
         InfusedPropertiesHelper.removeProperty(stack, this.asHolder());
         return false;
      } else {
         if (ticks < 0) {
            ticks = data.getDuration();
         }

         int newDuration = data.getDuration() - ticks;
         int durationToApply = Math.min(ticks, data.getDuration());
         MobEffectInstance effect = new MobEffectInstance(data.getEffect(), durationToApply, data.getAmplifier(), data.isAmbient(), data.isVisible());
         target.addEffect(effect);
         if (target.level() instanceof ServerLevel level) {
            for (int i = 0; i < 8; i++) {
               level.sendParticles(
                  ((MobEffect)data.getEffect().value()).createParticleOptions(effect),
                  target.getRandomX(1.0),
                  target.getRandomY(),
                  target.getRandomZ(1.0),
                  1,
                  0.0,
                  0.0,
                  0.0,
                  0.0
               );
            }
         }

         if (newDuration <= 0) {
            InfusedPropertiesHelper.removeProperty(stack, this.asHolder());
         } else {
            this.setData(stack, new MobEffectInstance(data.getEffect(), data.getDuration() - ticks, data.getAmplifier(), data.isAmbient(), data.isVisible()));
         }

         return true;
      }
   }

   public static MobEffectInstance getPotionEffect(ItemStack stack) {
      PotionContents potionContent = (PotionContents)stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
      Iterator<MobEffectInstance> effects = potionContent.getAllEffects().iterator();
      return effects.hasNext() ? effects.next() : null;
   }

   @Override
   public Component getName(ItemStack stack) {
      Component name = super.getName(stack);
      MobEffectInstance effectInstance = getPotionEffect(stack);
      if (effectInstance == null) {
         effectInstance = this.getData(stack);
      }

      if (effectInstance != null) {
         int color = ((MobEffect)effectInstance.getEffect().value()).getColor();
         List<Component> effectComponent = new ArrayList<>();
         PotionContents.addPotionTooltip(List.of(effectInstance), effectComponent::add, 1.0F, 20.0F);
         return Component.translatable("property.detail", new Object[]{name.copy().withColor(color), effectComponent.getFirst()}).withColor(color);
      } else {
         return name;
      }
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      Component name = super.getDisplayText(stack);
      MobEffectInstance effectInstance = this.getData(stack);
      if (effectInstance != null) {
         List<Component> effectComponent = new ArrayList<>();
         PotionContents.addPotionTooltip(List.of(effectInstance), effectComponent::add, 1.0F, 20.0F);
         return Component.translatable("property.detail", new Object[]{name, effectComponent.getFirst()})
            .withColor(((MobEffect)effectInstance.getEffect().value()).getColor());
      } else {
         return name;
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      MobEffectInstance effect = this.getData(stack);
      return effect == null ? 22015 : ((MobEffect)effect.getEffect().value()).getColor();
   }

   public MobEffectInstance readData(CompoundTag tag) {
      if (!tag.contains("potion", 10)) {
         return null;
      } else {
         DataResult<Pair<MobEffectInstance, Tag>> effect = MobEffectInstance.CODEC.decode(NbtOps.INSTANCE, tag.getCompound("potion"));
         return effect.isSuccess() ? (MobEffectInstance)((Pair)effect.getOrThrow()).getFirst() : null;
      }
   }

   public CompoundTag writeData(MobEffectInstance data) {
      return new CompoundTag();
   }

   public MobEffectInstance getDefaultData() {
      return null;
   }
}

package net.cibernet.alchemancy.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public class RandomEffectProperty extends Property implements IDataHolder<Long> {
   @Override
   public void modifyAttackDamage(Entity user, ItemStack weapon, Pre event) {
      ((Property)this.getRandomEffect(weapon).value()).modifyAttackDamage(user, weapon, event);
   }

   @Override
   public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target) {
      ((Property)this.getRandomEffect(weapon).value()).onAttack(user, weapon, damageSource, target);
   }

   @Override
   public void onIncomingAttack(Entity user, ItemStack weapon, LivingEntity target, LivingIncomingDamageEvent event) {
      ((Property)this.getRandomEffect(weapon).value()).onIncomingAttack(user, weapon, target, event);
   }

   @Override
   public void modifyDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, Pre event) {
      ((Property)this.getRandomEffect(weapon).value()).modifyDamageReceived(user, weapon, slot, event);
   }

   @Override
   public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
      ((Property)this.getRandomEffect(stack).value()).onActivation(source, target, stack, damageSource);
   }

   @Override
   public int getColor(ItemStack stack) {
      return ((Property)this.getRandomEffect(stack, 20L).value()).getColor(stack);
   }

   public Holder<Property> getRandomEffect(ItemStack stack) {
      return this.getRandomEffect(stack, 2L);
   }

   public Holder<Property> getRandomEffect(ItemStack stack, long delay) {
      return getRandomEffect(new Random(this.getData(stack) + CommonUtils.getLevelData().getGameTime() / delay));
   }

   public static Holder<Property> getRandomEffect(Random random) {
      ArrayList<DeferredHolder<Property, ? extends Property>> properties = new ArrayList<>(AlchemancyProperties.REGISTRY.getEntries());
      properties.remove(AlchemancyProperties.RANDOM);
      properties.removeIf(property -> property.is(AlchemancyTags.Properties.DISABLED));
      Collections.shuffle(properties, random);
      return (Holder<Property>)properties.getFirst();
   }

   @Override
   public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
      if (this.getData(stack) == 0L) {
         this.setData(stack, level.random.nextLong());
      }
   }

   @Override
   public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity) {
      if (this.getData(stack) == 0L) {
         this.setData(stack, itemEntity.getRandom().nextLong());
      }
   }

   @Override
   public void onProjectileTick(ItemStack stack, Projectile projectile) {
      if (this.getData(stack) == 0L) {
         this.setData(stack, projectile.getRandom().nextLong());
      }
   }

   public Long readData(CompoundTag tag) {
      return tag.getLong("seed");
   }

   public CompoundTag writeData(final Long data) {
      return new CompoundTag() {
         {
            this.putLong("seed", data);
         }
      };
   }

   public Long getDefaultData() {
      return 0L;
   }

   @Override
   public boolean hasJournalEntry() {
      return false;
   }
}

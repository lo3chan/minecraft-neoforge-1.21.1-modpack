package net.cibernet.alchemancy.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class InfectedProperty extends SpreadsOnHitProperty implements ITintModifier {
   public static final MobEffectInstance[] EFFECTS = new MobEffectInstance[]{
      new MobEffectInstance(MobEffects.CONFUSION, 600, 1), new MobEffectInstance(MobEffects.HUNGER, 600, 1)
   };

   public InfectedProperty() {
      super(EquipmentSlotGroup.ANY);
   }

   @Override
   public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
      if (!level.isClientSide() && user instanceof Player player && user.getRandom().nextFloat() < 0.001F) {
         if (user.getRandom().nextBoolean() && InfusedPropertiesHelper.hasInfusedProperty(stack, AlchemancyProperties.INFECTED)) {
            InfusedPropertiesHelper.removeProperty(stack, AlchemancyProperties.INFECTED);
            InfusedPropertiesHelper.addProperty(stack, AlchemancyProperties.DEAD);
         } else {
            this.infect(player);
         }
      }
   }

   @Override
   public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target) {
      if (!target.level().isClientSide() && target instanceof Player player) {
         this.infect(player);
      } else {
         super.onAttack(user, weapon, damageSource, target);
      }
   }

   private void infect(Player target) {
      this.infect(target, target.getInventory().items);
   }

   private void infect(Entity target, Collection<ItemStack> possibleCandidates) {
      List<ItemStack> candidates = possibleCandidates.stream()
         .filter(
            s -> !s.isEmpty()
               && !InfusedPropertiesHelper.hasProperty(s, AlchemancyProperties.INFECTED)
               && !InfusedPropertiesHelper.hasProperty(s, AlchemancyProperties.DEAD)
               && !InfusedPropertiesHelper.hasProperty(s, AlchemancyProperties.SANITIZED)
         )
         .toList();
      if (!candidates.isEmpty()) {
         InfusedPropertiesHelper.addProperty(candidates.get(target.getRandom().nextInt(candidates.size())), AlchemancyProperties.INFECTED);
      }
   }

   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      if (user.getRandom().nextFloat() < 0.001F) {
         user.addEffect(new MobEffectInstance(EFFECTS[user.getRandom().nextInt(EFFECTS.length)]));
      }
   }

   @Override
   public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds) {
      for (LivingEntity target : entitiesInBounds) {
         if (root.getLevel().getRandom().nextFloat() < 0.005F) {
            ArrayList<ItemStack> possibleCandidates = new ArrayList<>();

            for (EquipmentSlot slot : EquipmentSlot.values()) {
               possibleCandidates.add(target.getItemBySlot(slot));
            }

            this.infect(target, possibleCandidates);
         }
      }
   }

   @Override
   public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource random) {
      playRootedParticles(root, random, new ItemParticleOption(ParticleTypes.ITEM, Items.ROTTEN_FLESH.getDefaultInstance()));
   }

   @Override
   public int getColor(ItemStack stack) {
      return -9806568;
   }

   @Override
   public int getTint(ItemStack stack, int tintIndex, int originalTint, int currentTint) {
      return this.getColor(stack);
   }
}

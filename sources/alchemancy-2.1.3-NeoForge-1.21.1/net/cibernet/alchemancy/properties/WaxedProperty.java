package net.cibernet.alchemancy.properties;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.Nullable;

public class WaxedProperty extends Property implements IDataHolder<Integer> {
   @Override
   public int getColor(ItemStack stack) {
      return 16432937;
   }

   @Override
   public boolean onInfusedByDormantProperty(
      ItemStack stack, ItemStack propertySource, ForgeRecipeGrid grid, List<Holder<Property>> propertiesToAdd, boolean consumeItem
   ) {
      if (!this.getData(stack).equals(this.getDefaultData())) {
         if (consumeItem) {
            this.removeData(stack);
         }

         return true;
      } else {
         return super.onInfusedByDormantProperty(stack, propertySource, grid, propertiesToAdd, consumeItem);
      }
   }

   @Override
   public void onIncomingDamageReceived(Entity user, ItemStack stack, EquipmentSlot slot, DamageSource source, LivingIncomingDamageEvent event) {
      if (event.getSource().is(DamageTypeTags.IS_FIRE) && !event.isCanceled() && slot.isArmor()) {
         if (user.invulnerableTime <= 0) {
            if (InfusedPropertiesHelper.hasInfusedProperty(stack, this.asHolder())) {
               int durability = this.getData(stack) - 1;
               if (durability <= 0) {
                  InfusedPropertiesHelper.removeProperty(stack, this.asHolder());
               } else {
                  this.setData(stack, durability);
               }
            }

            user.invulnerableTime = 10;
         }

         event.setCanceled(true);
      }
   }

   @Override
   public void onStackedOverMe(
      ItemStack carriedItem,
      ItemStack stackedOnItem,
      Player player,
      ClickAction clickAction,
      SlotAccess carriedSlot,
      Slot stackedOnSlot,
      AtomicBoolean isCancelled
   ) {
      if (clickAction == ClickAction.SECONDARY && this.getData(stackedOnItem) < this.getDefaultData() && carriedItem.is(AlchemancyTags.Items.RESTORES_WAXED)) {
         if (carriedItem.hasCraftingRemainingItem()) {
            ItemStack remainder = carriedItem.getCraftingRemainingItem();
            if (carriedItem.getCount() > 1) {
               player.drop(remainder, true);
               carriedItem.shrink(1);
            } else {
               carriedSlot.set(remainder.copy());
            }
         } else {
            carriedItem.shrink(1);
         }

         this.setData(stackedOnItem, this.getDefaultData());
         playRestoreSound(player);
         isCancelled.set(true);
      }
   }

   public static void playRestoreSound(LivingEntity player) {
      player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.HONEYCOMB_WAX_ON, SoundSource.PLAYERS, 1.0F, 1.0F);
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      return Component.translatable(
            "property.detail",
            new Object[]{
               super.getDisplayText(stack),
               Component.translatable("property.detail.percentage", new Object[]{this.getData(stack) * 100 / this.getDefaultData()})
            }
         )
         .withColor(this.getColor(stack));
   }

   public Integer readData(CompoundTag tag) {
      return tag.contains("durability", 3) ? tag.getInt("durability") : this.getDefaultData();
   }

   public CompoundTag writeData(final Integer data) {
      return new CompoundTag() {
         {
            this.putInt("durability", data);
         }
      };
   }

   public Integer combineData(@Nullable Integer currentData, Integer newData) {
      return currentData == null ? newData : Math.min(this.getDefaultData(), currentData + newData);
   }

   public Integer getDefaultData() {
      return 100;
   }
}

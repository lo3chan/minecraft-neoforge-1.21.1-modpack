package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;

public class ArmorPulseProperty extends Property implements IDataHolder<Boolean> {
   @Override
   public void modifyDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, Pre event) {
      if (!this.getData(weapon)
         && (slot.isArmor() || user.getUseItem() == weapon)
         && !event.getSource().is(DamageTypeTags.BYPASSES_ARMOR)
         && event.getSource().getEntity() != null
         && event.getSource().getEntity().distanceTo(user)
            <= (user.getAttributes().hasAttribute(Attributes.ENTITY_INTERACTION_RANGE) ? user.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) : 3.0)) {
         this.setData(weapon, true);
         event.setNewDamage(Math.max(0.0F, event.getNewDamage() - 1.0F));
         activateByEntity(user, user, weapon);
      }
   }

   @Override
   public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
      if (this.getData(stack)) {
         this.setData(stack, false);
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 16743264;
   }

   public Boolean readData(CompoundTag tag) {
      return tag.getBoolean("activated");
   }

   public CompoundTag writeData(final Boolean data) {
      return new CompoundTag() {
         {
            this.putBoolean("activated", data);
         }
      };
   }

   public Boolean getDefaultData() {
      return false;
   }
}

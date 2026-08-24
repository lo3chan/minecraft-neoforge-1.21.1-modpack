package net.cibernet.alchemancy.properties.entangled;

import java.util.concurrent.atomic.AtomicBoolean;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.properties.special.AirWalkingProperty;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractEntangledProperty extends Property implements IDataHolder<AbstractEntangledProperty.EntangledData> {
   @Override
   public int getColor(ItemStack stack) {
      return 15269632;
   }

   @Override
   public void onStackedOverMe(
      ItemStack carriedItem, ItemStack stack, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled
   ) {
      if (!isCancelled.get()) {
         AbstractEntangledProperty.EntangledData data = this.getData(stack);
         if (clickAction == ClickAction.SECONDARY
            && !carriedItem.isEmpty()
            && data.equals(this.getDefaultData())
            && InfusedPropertiesHelper.getRemainingInfusionSlots(carriedItem) > 0) {
            ItemStack entangleTarget = carriedItem.split(1);
            InfusedPropertiesHelper.addProperty(entangleTarget, this.asHolder());
            this.setStoredItem(stack, entangleTarget);
            isCancelled.set(true);
         }
      }
   }

   public boolean getToggle(ItemStack stack) {
      return this.getData(stack).toggled();
   }

   public ItemStack getStoredItem(ItemStack stack) {
      return this.getData(stack).stack;
   }

   public void setToggle(ItemStack stack, boolean value) {
      this.setData(stack, new AbstractEntangledProperty.EntangledData(this.getData(stack).stack, value));
   }

   public void setData(ItemStack stack, ItemStack storedStack, boolean toggled) {
      this.setData(stack, new AbstractEntangledProperty.EntangledData(storedStack, toggled));
   }

   public void setStoredItem(ItemStack stack, ItemStack value) {
      this.setData(stack, new AbstractEntangledProperty.EntangledData(value, this.getData(stack).toggled));
   }

   public ItemStack shift(ItemStack stack, @Nullable Entity user) {
      AbstractEntangledProperty.EntangledData data = this.getData(stack);
      ItemStack storedItem = data.stack;
      if (storedItem.equals(this.getDefaultData().stack)) {
         return stack;
      } else {
         this.setStoredItem(stack, this.getDefaultData().stack);
         this.setData(storedItem, stack, data.toggled);
         if (user != null && InfusedPropertiesHelper.hasProperty(storedItem, AlchemancyProperties.AIR_WALKER)) {
            ((AirWalkingProperty)AlchemancyProperties.AIR_WALKER.value()).setData(storedItem, user.getY());
         }

         return storedItem;
      }
   }

   public void afterShiftingProjectile(ItemStack oldStack, ItemStack newStack, Entity projectile) {
      if (!InfusedPropertiesHelper.hasProperty(newStack, AlchemancyProperties.PHASING)) {
         projectile.noPhysics = false;
      }

      if (!InfusedPropertiesHelper.hasProperty(newStack, AlchemancyProperties.GLOWING_AURA)) {
         projectile.setGlowingTag(false);
      }

      if (!InfusedPropertiesHelper.hasProperty(newStack, AlchemancyProperties.ANTIGRAV)) {
         projectile.setNoGravity(false);
      }
   }

   @Override
   public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data) {
      return dataType == DataComponents.MAX_STACK_SIZE ? 1 : data;
   }

   public AbstractEntangledProperty.EntangledData readData(CompoundTag tag) {
      return !tag.contains("item")
         ? this.getDefaultData()
         : new AbstractEntangledProperty.EntangledData(
            ItemStack.parse(CommonUtils.registryAccessStatic(), tag.getCompound("item")).orElse(this.getDefaultData().stack()), tag.getBoolean("toggled")
         );
   }

   public CompoundTag writeData(final AbstractEntangledProperty.EntangledData data) {
      return new CompoundTag() {
         {
            if (!data.stack.isEmpty()) {
               this.put("item", data.stack.save(CommonUtils.registryAccessStatic()));
            }

            this.putBoolean("toggled", data.toggled);
         }
      };
   }

   public AbstractEntangledProperty.EntangledData getDefaultData() {
      return AbstractEntangledProperty.EntangledData.DEFAULT;
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      Component name = super.getDisplayText(stack);
      ItemStack storedStack = this.getData(stack).stack;
      return (Component)(!storedStack.isEmpty()
         ? Component.translatable(
               "property.detail",
               new Object[]{name, Component.translatable("property.detail.item_count", new Object[]{storedStack.getHoverName(), storedStack.getCount()})}
            )
            .withColor(this.getColor(stack))
         : name);
   }

   @Override
   public int getPriority() {
      return 2147483647;
   }

   public record EntangledData(ItemStack stack, boolean toggled) {
      public static final AbstractEntangledProperty.EntangledData DEFAULT = new AbstractEntangledProperty.EntangledData(ItemStack.EMPTY, false);

      @Override
      public boolean equals(Object obj) {
         return obj instanceof AbstractEntangledProperty.EntangledData data ? this.stack.equals(data.stack) : false;
      }
   }
}

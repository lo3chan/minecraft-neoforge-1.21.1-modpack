package net.cibernet.alchemancy.properties.special;

import java.text.DecimalFormat;
import java.util.List;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class BatteryPoweredProperty extends Property {
   private static final int COST = 100;
   public static final int CAPACITY = 10000;

   @Override
   public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
      this.repairItem(stack);
   }

   @Override
   public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds) {
      this.repairItem(root.getItem());
   }

   private void repairItem(ItemStack stack) {
      IEnergyStorage energy = (IEnergyStorage)stack.getCapability(EnergyStorage.ITEM);
      if (energy != null && stack.isDamaged() && energy.extractEnergy(100, true) == 100) {
         energy.extractEnergy(100, false);
         repairItem(stack, 1);
      }
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      IEnergyStorage energy = (IEnergyStorage)stack.getCapability(EnergyStorage.ITEM);
      if (energy == null) {
         return super.getDisplayText(stack);
      } else {
         DecimalFormat df = new DecimalFormat("###,###,###");
         return Component.translatable(
               "property.detail",
               new Object[]{
                  super.getDisplayText(stack),
                  Component.translatable("property.detail.fe", new Object[]{df.format(energy.getEnergyStored()), df.format(energy.getMaxEnergyStored())})
               }
            )
            .withColor(this.getColor(stack));
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return ColorUtils.interpolateColorsOverTime(0.25F, 11874304, 11874304, 14187530);
   }
}

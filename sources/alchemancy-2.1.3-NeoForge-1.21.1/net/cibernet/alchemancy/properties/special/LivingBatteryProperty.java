package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class LivingBatteryProperty extends Property {
   public static final int CONVERSION = 50;

   @Override
   public int onItemRepaired(ItemStack stack, int amount, int original) {
      int toCharge = amount - stack.getDamageValue();
      if (toCharge <= 0) {
         return amount;
      } else {
         IEnergyStorage cap = (IEnergyStorage)stack.getCapability(EnergyStorage.ITEM);
         return cap != null && cap.canReceive() ? Math.max(0, amount - cap.receiveEnergy(toCharge * 50, false) / 50) : amount;
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return ColorUtils.interpolateColorsOverTime(0.25F, 11857716, 11857716, 14875769);
   }
}

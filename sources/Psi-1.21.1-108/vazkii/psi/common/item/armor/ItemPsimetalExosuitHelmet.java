package vazkii.psi.common.item.armor;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.exosuit.IExosuitSensor;
import vazkii.psi.api.exosuit.ISensorHoldable;
import vazkii.psi.common.item.base.ModDataComponents;

public class ItemPsimetalExosuitHelmet extends ItemPsimetalArmor implements ISensorHoldable {
   public ItemPsimetalExosuitHelmet(Type type, Properties properties) {
      super(type, properties);
   }

   @Override
   public String getEvent(ItemStack stack) {
      ItemStack sensor = this.getAttachedSensor(stack);
      return !sensor.isEmpty() && sensor.getItem() instanceof IExosuitSensor ? ((IExosuitSensor)sensor.getItem()).getEventType(sensor) : super.getEvent(stack);
   }

   @Override
   public int getCastCooldown(ItemStack stack) {
      return 40;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public int getColor(@NotNull ItemStack stack) {
      ItemStack sensor = this.getAttachedSensor(stack);
      return !sensor.isEmpty() && sensor.getItem() instanceof IExosuitSensor ? ((IExosuitSensor)sensor.getItem()).getColor(sensor) : super.getColor(stack);
   }

   @Override
   public ItemStack getAttachedSensor(ItemStack stack) {
      return new ItemStack((ItemLike)stack.getOrDefault(ModDataComponents.SENSOR, Items.AIR));
   }

   @Override
   public void attachSensor(ItemStack stack, ItemStack sensor) {
      stack.set(ModDataComponents.SENSOR, sensor.getItem());
   }
}

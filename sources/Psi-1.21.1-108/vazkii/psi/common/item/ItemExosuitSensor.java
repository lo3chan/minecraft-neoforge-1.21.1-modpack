package vazkii.psi.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import vazkii.psi.api.exosuit.IExosuitSensor;

public abstract class ItemExosuitSensor extends Item implements IExosuitSensor {
   public static final int defaultColor = -15481345;
   public static final int lightColor = -5101;
   public static final int underwaterColor = -15511297;
   public static final int fireColor = -57837;
   public static final int lowHealthColor = -29499;

   public ItemExosuitSensor(Properties properties) {
      super(properties.stacksTo(1));
   }

   @Override
   public String getEventType(ItemStack stack) {
      return "psi.event.none";
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public int getColor(ItemStack stack) {
      return -15481345;
   }
}

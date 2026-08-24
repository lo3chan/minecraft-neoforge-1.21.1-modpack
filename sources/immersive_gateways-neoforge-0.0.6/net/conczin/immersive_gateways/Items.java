package net.conczin.immersive_gateways;

import net.conczin.immersive_gateways.item.GatewayItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

public interface Items {
   Item GATEWAY = new GatewayItem(baseProps());

   static Properties baseProps() {
      return new Properties();
   }

   static void registerItems(Common.RegisterHelper<Item> helper) {
      helper.register(Common.locate("gateway"), GATEWAY);
   }
}

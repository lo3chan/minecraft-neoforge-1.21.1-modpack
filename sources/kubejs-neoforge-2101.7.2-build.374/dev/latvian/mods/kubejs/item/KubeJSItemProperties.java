package dev.latvian.mods.kubejs.item;

import net.minecraft.world.item.Item.Properties;

public class KubeJSItemProperties extends Properties {
   public final ItemBuilder itemBuilder;

   public KubeJSItemProperties(ItemBuilder itemBuilder) {
      this.itemBuilder = itemBuilder;
   }
}

package com.github.alexthe666.citadel.item;

import com.github.alexthe666.citadel.Citadel;
import java.util.function.Consumer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class ItemCustomRender extends Item {
   public ItemCustomRender(Properties props) {
      super(props);
   }

   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept((IClientItemExtensions)Citadel.PROXY.getISTERProperties());
   }
}

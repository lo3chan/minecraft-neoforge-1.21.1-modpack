package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import java.util.function.Consumer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class ItemFancyRender extends Item implements IClientExtensionItem {
   public ItemFancyRender(Properties props) {
      super(props);
   }

   @Override
   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept((IClientItemExtensions)AlexsMobs.PROXY.getISTERProperties());
   }
}

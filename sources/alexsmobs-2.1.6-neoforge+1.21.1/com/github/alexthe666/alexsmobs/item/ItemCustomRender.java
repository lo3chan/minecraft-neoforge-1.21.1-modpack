package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import java.util.function.Consumer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class ItemCustomRender extends Item implements CustomTabBehavior, IClientExtensionItem {
   public ItemCustomRender(Properties props) {
      super(props);
   }

   @Override
   public void fillItemCategory(Output contents) {
   }

   @Override
   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept((IClientItemExtensions)AlexsMobs.PROXY.getISTERProperties());
   }
}

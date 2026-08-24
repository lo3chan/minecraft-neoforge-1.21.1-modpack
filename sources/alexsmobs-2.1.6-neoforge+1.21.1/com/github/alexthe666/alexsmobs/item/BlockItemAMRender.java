package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class BlockItemAMRender extends AMBlockItem implements IClientExtensionItem {
   public BlockItemAMRender(Supplier<Block> blockSupplier, Properties props) {
      super(blockSupplier, props);
   }

   @Override
   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept((IClientItemExtensions)AlexsMobs.PROXY.getISTERProperties());
   }
}

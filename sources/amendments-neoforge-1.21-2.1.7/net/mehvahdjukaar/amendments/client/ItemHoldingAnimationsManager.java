package net.mehvahdjukaar.amendments.client;

import java.util.stream.Collectors;
import net.mehvahdjukaar.amendments.client.renderers.CandleHolderRendererExtension;
import net.mehvahdjukaar.amendments.client.renderers.LanternRendererExtension;
import net.mehvahdjukaar.amendments.client.renderers.TorchRendererExtension;
import net.mehvahdjukaar.amendments.common.LanternRegistry;
import net.mehvahdjukaar.amendments.configs.ClientConfigs;
import net.mehvahdjukaar.moonlight.api.item.IThirdPersonSpecialItemRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class ItemHoldingAnimationsManager {
   private static boolean animAdded = false;

   public static void addAnimations() {
      if (!animAdded) {
         animAdded = true;
         if (ClientConfigs.LANTERN_HOLDING.get()) {
            LanternRendererExtension anim = new LanternRendererExtension();
            LanternRegistry.INSTANCE
               .getValues()
               .stream()
               .map(t -> t.lantern.asItem())
               .filter(i -> i != Items.AIR)
               .collect(Collectors.toSet())
               .forEach(item -> IThirdPersonSpecialItemRenderer.attachToItem(item, anim));
         }

         if (ClientConfigs.TORCH_HOLDING.get()) {
            TorchRendererExtension anim = new TorchRendererExtension();
            BlockScanner.getInstance()
               .getTorches()
               .stream()
               .<Item>map(Block::asItem)
               .filter(i -> i != Items.AIR)
               .collect(Collectors.toSet())
               .forEach(item -> IThirdPersonSpecialItemRenderer.attachToItem(item, anim));
         }

         if (ClientConfigs.CANDLE_HOLDER_HOLDING.get()) {
            BlockScanner.getInstance()
               .getCandleHolders()
               .stream()
               .<Item>map(Block::asItem)
               .filter(i -> i instanceof BlockItem)
               .map(i -> (BlockItem)i)
               .collect(Collectors.toSet())
               .forEach(item -> IThirdPersonSpecialItemRenderer.attachToItem(item, new CandleHolderRendererExtension(item)));
         }
      }
   }
}

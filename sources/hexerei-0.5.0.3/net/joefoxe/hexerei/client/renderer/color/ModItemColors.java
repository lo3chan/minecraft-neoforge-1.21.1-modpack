package net.joefoxe.hexerei.client.renderer.color;

import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.ConnectingCarpetDyed;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.BroomSeatItem;
import net.joefoxe.hexerei.item.custom.CandleItem;
import net.joefoxe.hexerei.item.custom.CofferItem;
import net.joefoxe.hexerei.item.custom.HerbJarItem;
import net.joefoxe.hexerei.item.custom.MixingCauldronItem;
import net.joefoxe.hexerei.item.custom.SatchelItem;
import net.joefoxe.hexerei.item.custom.WitchArmorItem;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Item;

@EventBusSubscriber(
   value = {Dist.CLIENT},
   bus = Bus.MOD
)
public class ModItemColors {
   private ModItemColors() {
   }

   @SubscribeEvent
   public static void initItemColors(Item event) {
      event.register((stack, color) -> {
         if (color == 0) {
            return -1;
         } else {
            int col = 0xFF000000 | ((WitchArmorItem)stack.getItem()).getColor(stack);
            return col == -1 ? 0 : col;
         }
      }, new ItemLike[]{(ItemLike)ModItems.WITCH_HELMET.get(), (ItemLike)ModItems.WITCH_CHESTPLATE.get(), (ItemLike)ModItems.WITCH_BOOTS.get()});
      event.register((stack, color) -> {
         Block block = Block.byItem(stack.getItem());
         return block instanceof WaterlilyBlock ? GrassColor.get(0.0, 0.5) : 0;
      }, new ItemLike[]{(ItemLike)ModBlocks.LILY_PAD_BLOCK.get()});
      CofferItem.ItemHandlerConsumer items = event.getItemColors()::register;
      items.register(
         (s, t) -> t == 1 ? CofferItem.getColorValue(CofferItem.getDyeColorNamed(s), s) : -1,
         (ItemLike)ModItems.COFFER.get(),
         (ItemLike)ModItems.ENTANGLED_COFFER.get()
      );
      items.register(
         (s, t) -> t == 0 ? MixingCauldronItem.getColorValue(MixingCauldronItem.getDyeColorNamed(s), s) : -1, (ItemLike)ModItems.MIXING_CAULDRON.get()
      );
      items.register(
         (s, t) -> t == 0 ? HerbJarItem.getColorValue(HexereiUtil.getDyeColorNamed(s.getHoverName().getString()), s) : -1, (ItemLike)ModItems.HERB_JAR.get()
      );
      items.register((s, t) -> t == 0 ? BroomSeatItem.getColorValue(SatchelItem.getDyeColorNamed(s), s) : -1, (ItemLike)ModItems.BROOM_SEAT.get());
      items.register((s, t) -> t == 1 ? SatchelItem.getColorValue(SatchelItem.getDyeColorNamed(s), s) : -1, (ItemLike)ModItems.SMALL_SATCHEL.get());
      items.register((s, t) -> t == 1 ? SatchelItem.getColorValue(SatchelItem.getDyeColorNamed(s), s) : -1, (ItemLike)ModItems.MEDIUM_SATCHEL.get());
      items.register((s, t) -> t == 1 ? SatchelItem.getColorValue(SatchelItem.getDyeColorNamed(s), s) : -1, (ItemLike)ModItems.LARGE_SATCHEL.get());
      items.register((s, t) -> t == 1 ? CandleItem.getColorValue(CandleItem.getDyeColorNamed(s), s) : -1, (ItemLike)ModItems.CANDLE.get());
      items.register(
         (s, t) -> t == 0 ? ConnectingCarpetDyed.getColorValue(s) : -1,
         (ItemLike)ModItems.INFUSED_FABRIC_CARPET.get(),
         (ItemLike)ModItems.WAXED_INFUSED_FABRIC_CARPET.get(),
         (ItemLike)ModItems.INFUSED_FABRIC_BLOCK.get(),
         (ItemLike)ModItems.WAXED_INFUSED_FABRIC_BLOCK.get()
      );
   }
}

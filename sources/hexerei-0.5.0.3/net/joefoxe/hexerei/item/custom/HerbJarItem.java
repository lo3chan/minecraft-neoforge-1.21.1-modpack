package net.joefoxe.hexerei.item.custom;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.HerbJar;
import net.joefoxe.hexerei.items.JarHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;

public class HerbJarItem extends BlockItem {
   public HerbJarItem(Block block, Properties properties) {
      super(block, properties);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      CompoundTag inv = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getCompound("Inventory");
      ListTag tagList = inv.getList("Items", 10);
      if (Screen.hasShiftDown()) {
         if (tagList.size() >= 1) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.herb_jar_shift_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.herb_jar_shift_5").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.herb_jar_shift_6").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         }

         if (tagList.size() < 1) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.herb_jar_shift_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.herb_jar_shift_5").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.herb_jar_shift_6").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.herb_jar_shift_7").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         }
      }
   }

   public static int getColorValue(DyeColor color, ItemStack stack) {
      int dyeCol = HexereiUtil.getDyeColor(stack, HerbJar.DEFAULT_COLOR.rgb());
      return color == null && dyeCol != -1 ? dyeCol : color.getTextureDiffuseColor();
   }

   public InteractionResult place(BlockPlaceContext context) {
      return super.place(context);
   }

   public void registerBlocks(Map<Block, Item> pBlockToItemMap, Item pItem) {
      super.registerBlocks(pBlockToItemMap, pItem);
   }

   public Optional<TooltipComponent> getTooltipImage(ItemStack p_150775_) {
      JarHandler handler = this.createHandler();
      int count = getContentsExtra(p_150775_);
      ItemStack stack = getContents(p_150775_);
      stack.setCount(count);
      handler.setStackInSlot(0, stack);
      return Optional.of(new HerbJarItem.HerbJarToolTip(handler));
   }

   private JarHandler createHandler() {
      return new JarHandler(1, 1024);
   }

   private static ItemStack getContents(ItemStack p_150783_) {
      CustomData compoundtag = (CustomData)p_150783_.get(DataComponents.CUSTOM_DATA);
      return compoundtag == null
         ? ItemStack.EMPTY
         : ItemStack.parseOptional(Hexerei.DynamicRegistries.get(), compoundtag.copyTag().getCompound("Inventory").getList("Items", 10).getCompound(0));
   }

   private static int getContentsExtra(ItemStack p_150783_) {
      CustomData compoundtag = (CustomData)p_150783_.get(DataComponents.CUSTOM_DATA);
      return compoundtag == null ? 0 : compoundtag.copyTag().getCompound("Inventory").getList("Items", 10).getCompound(0).getInt("ExtendedCount");
   }

   public record HerbJarToolTip(JarHandler jarHandler) implements TooltipComponent {
   }
}

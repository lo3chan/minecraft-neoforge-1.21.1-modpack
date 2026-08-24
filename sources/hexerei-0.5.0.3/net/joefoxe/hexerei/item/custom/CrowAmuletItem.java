package net.joefoxe.hexerei.item.custom;

import java.util.List;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.AskForMapDataPacket;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class CrowAmuletItem extends Item {
   public CrowAmuletItem(Properties properties) {
      super(properties);
   }

   public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
      CompoundTag inv = ((CustomData)pStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      if (!inv.isEmpty()) {
         ListTag tagList = inv.getList("Items", 10);
         CompoundTag compoundtag = tagList.getCompound(0);
         ItemStack stack = ItemStack.parseOptional(pLevel.registryAccess(), compoundtag);
         if (stack.getItem() instanceof MapItem mapItem) {
            mapItem.inventoryTick(pStack, pLevel, pEntity, pSlotId, true);
            MapItemSavedData mapitemsaveddata = MapItem.getSavedData(stack, pLevel);
            if (mapitemsaveddata == null) {
               HexereiPacketHandler.sendToServer(new AskForMapDataPacket(stack));
            }
         }
      }

      super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
   }

   public Component getName(ItemStack pStack) {
      CompoundTag inv = ((CustomData)pStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      ListTag tagList = inv.getList("Items", 10);
      CompoundTag compoundtag = tagList.getCompound(0);
      ItemStack stack = ItemStack.parseOptional(Hexerei.DynamicRegistries.get(), compoundtag);
      if (!stack.isEmpty()) {
         return stack.has(DataComponents.CUSTOM_NAME)
            ? Component.translatable("").append(stack.getHoverName()).append(Component.translatable("item.hexerei.crow_filled_amulet"))
            : Component.translatable(ItemStack.parseOptional(Hexerei.DynamicRegistries.get(), compoundtag).getDescriptionId())
               .append(Component.translatable("item.hexerei.crow_filled_amulet"));
      } else {
         return super.getName(pStack);
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      CompoundTag inv = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      ListTag tagList = inv.getList("Items", 10);
      CompoundTag compoundtag = tagList.getCompound(0);
      CompoundTag itemTags = tagList.getCompound(0);
      MutableComponent itemText = Component.translatable(ItemStack.parseOptional(Hexerei.DynamicRegistries.get(), compoundtag).getDescriptionId())
         .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10061824)));
      if (Screen.hasShiftDown()) {
         tooltipComponents.add(
            Component.translatable(
                  "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         if (!ItemStack.parseOptional(Hexerei.DynamicRegistries.get(), itemTags).isEmpty()) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.keychain_with_item").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         } else {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.keychain_without_item").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         }

         tooltipComponents.add(Component.translatable("tooltip.hexerei.crow_blank_amulet").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         tooltipComponents.add(Component.translatable("tooltip.hexerei.cosmetic_only").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
      } else {
         tooltipComponents.add(
            Component.translatable(
                  "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
      }

      if (!ItemStack.parseOptional(Hexerei.DynamicRegistries.get(), itemTags).isEmpty()) {
         tooltipComponents.add(
            Component.translatable("tooltip.hexerei.keychain_contains", new Object[]{itemText}).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
      }

      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
   }
}

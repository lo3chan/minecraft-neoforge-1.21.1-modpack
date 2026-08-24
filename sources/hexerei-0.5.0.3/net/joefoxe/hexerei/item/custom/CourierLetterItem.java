package net.joefoxe.hexerei.item.custom;

import java.util.List;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.ClientboundOpenCourierLetterScreenPacket;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class CourierLetterItem extends BlockItem {
   public CourierLetterItem(Block block, Properties properties) {
      super(block, properties);
   }

   public InteractionResult place(BlockPlaceContext context) {
      return super.place(context);
   }

   public InteractionResult useOn(UseOnContext pContext) {
      if (!pContext.isSecondaryUseActive()
         && pContext.getPlayer() instanceof ServerPlayer player
         && !pContext.getLevel().isClientSide
         && !pContext.getPlayer().isSteppingCarefully()
         && pContext.getItemInHand().getCount() == 1) {
         int slotIndex = pContext.getHand() == InteractionHand.OFF_HAND ? -1 : pContext.getPlayer().getInventory().selected;
         HexereiPacketHandler.sendToPlayerClient(new ClientboundOpenCourierLetterScreenPacket(slotIndex, pContext.getHand()), player);
      }

      return pContext.isSecondaryUseActive() ? super.useOn(pContext) : InteractionResult.CONSUME;
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
      ItemStack itemstack = playerIn.getItemInHand(handIn);
      if (playerIn instanceof ServerPlayer player && !playerIn.isSteppingCarefully() && itemstack.getCount() == 1) {
         int slotIndex = handIn == InteractionHand.OFF_HAND ? -1 : playerIn.getInventory().selected;
         HexereiPacketHandler.sendToPlayerClient(new ClientboundOpenCourierLetterScreenPacket(slotIndex, handIn), player);
      }

      return itemstack.getCount() == 1
         ? (isSealed(itemstack) ? InteractionResultHolder.fail(itemstack) : InteractionResultHolder.consume(itemstack))
         : InteractionResultHolder.fail(itemstack);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      if (Screen.hasShiftDown()) {
         tooltipComponents.add(
            Component.translatable(
                  "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         CustomData data = (CustomData)stack.get(DataComponents.BLOCK_ENTITY_DATA);
         if (data == null || !data.copyTag().contains("Message")) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_letter_use").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_letter_menu").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_letter_send").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.courier_letter_must_be_sealed").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
         } else if (isSealed(stack)) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_letter_send").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_letter_open").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         } else {
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.courier_letter_must_be_sealed").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
         }
      } else {
         tooltipComponents.add(
            Component.translatable(
                  "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
      }
   }

   public static boolean isSealed(ItemStack stack) {
      CustomData data = (CustomData)stack.get(DataComponents.BLOCK_ENTITY_DATA);
      return data != null && data.copyTag().contains("Sealed") && data.copyTag().getBoolean("Sealed");
   }
}

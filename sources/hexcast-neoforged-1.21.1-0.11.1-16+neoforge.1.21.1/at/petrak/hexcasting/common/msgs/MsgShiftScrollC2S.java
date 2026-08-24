package at.petrak.hexcasting.common.msgs;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.storage.ItemSpellbook;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.lib.HexSounds;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public record MsgShiftScrollC2S(double mainHandDelta, double offHandDelta, boolean isCtrl, boolean invertSpellbook, boolean invertAbacus) implements IMessage {
   public static final ResourceLocation ID = HexAPI.modLoc("scroll");

   @Override
   public ResourceLocation id() {
      return ID;
   }

   public static MsgShiftScrollC2S deserialize(ByteBuf buffer) {
      FriendlyByteBuf buf = new FriendlyByteBuf(buffer);
      double mainHandDelta = buf.readDouble();
      double offHandDelta = buf.readDouble();
      boolean isCtrl = buf.readBoolean();
      boolean invertSpellbook = buf.readBoolean();
      boolean invertAbacus = buf.readBoolean();
      return new MsgShiftScrollC2S(mainHandDelta, offHandDelta, isCtrl, invertSpellbook, invertAbacus);
   }

   @Override
   public void serialize(FriendlyByteBuf buf) {
      buf.writeDouble(this.mainHandDelta);
      buf.writeDouble(this.offHandDelta);
      buf.writeBoolean(this.isCtrl);
      buf.writeBoolean(this.invertSpellbook);
      buf.writeBoolean(this.invertAbacus);
   }

   public void handle(MinecraftServer server, ServerPlayer sender) {
      server.execute(() -> {
         this.handleForHand(sender, InteractionHand.MAIN_HAND, this.mainHandDelta);
         this.handleForHand(sender, InteractionHand.OFF_HAND, this.offHandDelta);
      });
   }

   private void handleForHand(ServerPlayer sender, InteractionHand hand, double delta) {
      if (delta != 0.0) {
         ItemStack stack = sender.getItemInHand(hand);
         if (stack.getItem() == HexItems.SPELLBOOK) {
            this.spellbook(sender, hand, stack, delta);
         } else if (stack.getItem() == HexItems.ABACUS) {
            this.abacus(sender, hand, stack, delta);
         }
      }
   }

   private void spellbook(ServerPlayer sender, InteractionHand hand, ItemStack stack, double delta) {
      if (this.invertSpellbook) {
         delta = -delta;
      }

      int newIdx = ItemSpellbook.rotatePageIdx(stack, delta < 0.0);
      int len = ItemSpellbook.highestPage(stack);
      boolean sealed = ItemSpellbook.isSealed(stack);
      MutableComponent component;
      if (hand == InteractionHand.OFF_HAND && stack.has(DataComponents.CUSTOM_NAME)) {
         if (sealed) {
            component = Component.translatable(
               "hexcasting.tooltip.spellbook.page_with_name.sealed",
               new Object[]{
                  Component.literal(String.valueOf(newIdx)).withStyle(ChatFormatting.WHITE),
                  Component.literal(String.valueOf(len)).withStyle(ChatFormatting.WHITE),
                  Component.literal("").withStyle(new ChatFormatting[]{stack.getRarity().color(), ChatFormatting.ITALIC}).append(stack.getHoverName()),
                  Component.translatable("hexcasting.tooltip.spellbook.sealed").withStyle(ChatFormatting.GOLD)
               }
            );
         } else {
            component = Component.translatable(
               "hexcasting.tooltip.spellbook.page_with_name",
               new Object[]{
                  Component.literal(String.valueOf(newIdx)).withStyle(ChatFormatting.WHITE),
                  Component.literal(String.valueOf(len)).withStyle(ChatFormatting.WHITE),
                  Component.literal("").withStyle(new ChatFormatting[]{stack.getRarity().color(), ChatFormatting.ITALIC}).append(stack.getHoverName())
               }
            );
         }
      } else if (sealed) {
         component = Component.translatable(
            "hexcasting.tooltip.spellbook.page.sealed",
            new Object[]{
               Component.literal(String.valueOf(newIdx)).withStyle(ChatFormatting.WHITE),
               Component.literal(String.valueOf(len)).withStyle(ChatFormatting.WHITE),
               Component.translatable("hexcasting.tooltip.spellbook.sealed").withStyle(ChatFormatting.GOLD)
            }
         );
      } else {
         component = Component.translatable(
            "hexcasting.tooltip.spellbook.page",
            new Object[]{
               Component.literal(String.valueOf(newIdx)).withStyle(ChatFormatting.WHITE),
               Component.literal(String.valueOf(len)).withStyle(ChatFormatting.WHITE)
            }
         );
      }

      sender.displayClientMessage(component.withStyle(ChatFormatting.GRAY), true);
   }

   private void abacus(ServerPlayer sender, InteractionHand hand, ItemStack stack, double delta) {
      if (this.invertAbacus) {
         delta = -delta;
      }

      boolean increase = delta < 0.0;
      double num = NBTHelper.getDouble(stack, "value");
      double shiftDelta;
      float pitch;
      if (hand == InteractionHand.MAIN_HAND) {
         shiftDelta = this.isCtrl ? 10.0 : 1.0;
         pitch = this.isCtrl ? 0.7F : 0.9F;
      } else {
         shiftDelta = this.isCtrl ? 0.01 : 0.1;
         pitch = this.isCtrl ? 1.3F : 1.0F;
      }

      int scale = Math.max((int)Math.floor(Math.abs(delta)), 1);
      num += scale * shiftDelta * (increase ? 1 : -1);
      NBTHelper.putDouble(stack, "value", num);
      pitch *= increase ? 1.05F : 0.95F;
      pitch = (float)(pitch + (Math.random() - 0.5) * 0.1);
      sender.level().playSound(null, sender.getX(), sender.getY(), sender.getZ(), HexSounds.ABACUS, SoundSource.PLAYERS, 0.5F, pitch);
      CompoundTag datumTag = HexItems.ABACUS.readIotaTag(stack);
      if (datumTag != null) {
         Component popup = IotaType.getDisplay(datumTag);
         sender.displayClientMessage(Component.translatable("hexcasting.tooltip.abacus", new Object[]{popup}).withStyle(ChatFormatting.GREEN), true);
      }
   }
}

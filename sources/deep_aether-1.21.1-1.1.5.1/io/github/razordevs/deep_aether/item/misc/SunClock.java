package io.github.razordevs.deep_aether.item.misc;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherTimeAttachment;
import com.aetherteam.aether.command.SunAltarWhitelist;
import com.aetherteam.aether.network.packet.clientbound.OpenSunAltarPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class SunClock extends Item {
   public SunClock(Properties pProperties) {
      super(pProperties);
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pUsedHand) {
      if (!level.isClientSide()) {
         if ((Boolean)AetherConfig.SERVER.sun_altar_whitelist.get()
            && !player.hasPermissions(4)
            && !SunAltarWhitelist.INSTANCE.isWhiteListed(player.getGameProfile())) {
            player.displayClientMessage(Component.translatable("aether.sun_altar.no_permission"), true);
         } else if (level.dimension().location().toString().equals("aether:the_aether")) {
            if (level.hasData(AetherDataAttachments.AETHER_TIME)) {
               if (!((AetherTimeAttachment)level.getData(AetherDataAttachments.AETHER_TIME)).isEternalDay()) {
                  this.openScreen(level, player, AetherTimeAttachment.getTicksPerDay());
               } else {
                  player.displayClientMessage(Component.translatable("aether.sun_altar.in_control"), true);
               }
            } else {
               this.openScreen(level, player, 24000);
            }
         } else {
            player.displayClientMessage(Component.translatable("aether.sun_altar.no_power"), true);
         }
      }

      return InteractionResultHolder.success(player.getItemInHand(pUsedHand));
   }

   protected void openScreen(Level level, Player player, int timeScale) {
      if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
         PacketDistributor.sendToPlayer(
            serverPlayer, new OpenSunAltarPacket(Component.translatable("menu.deep_aether.sun_clock"), timeScale), new CustomPacketPayload[0]
         );
      }
   }

   public Component getDescription() {
      return Component.translatable("deep_aether.item.disabled_item")
         .withStyle(Style.EMPTY.withItalic(true).withColor((TextColor)TextColor.parseColor("#d1362b").result().get()));
   }
}

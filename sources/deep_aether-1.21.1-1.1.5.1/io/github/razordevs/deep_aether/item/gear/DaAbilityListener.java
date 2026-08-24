package io.github.razordevs.deep_aether.item.gear;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.item.EquipmentUtil;
import io.github.razordevs.deep_aether.DeepAetherConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;

@EventBusSubscriber(
   modid = "deep_aether"
)
public class DaAbilityListener {
   @SubscribeEvent
   public static void onEntityFall(LivingFallEvent event) {
      LivingEntity livingEntity = event.getEntity();
      if (!event.isCanceled()) {
         event.setCanceled(DAEquipmentUtil.hasFullStratusSet(livingEntity));
      }
   }

   @SubscribeEvent
   public static void onEntityJump(LivingJumpEvent event) {
      LivingEntity livingEntity = event.getEntity();
      if (EquipmentUtil.hasFullGravititeSet(livingEntity)
         && livingEntity instanceof Player player
         && ((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).isGravititeJumpActive()) {
         player.push(0.0, DAEquipmentUtil.handleStratusRingBoost(livingEntity) - 1.0, 0.0);
         if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
         }
      }
   }

   @SubscribeEvent
   public static void onMiningSpeed(BreakSpeed event) {
      if (!(Boolean)DeepAetherConfig.SERVER.enable_skyjade_rework.get()) {
         Player player = event.getEntity();
         if (!event.isCanceled()) {
            event.setNewSpeed(DAEquipmentUtil.handleSkyjadeRingAbility(player, event.getNewSpeed()));
         }
      }
   }

   @SubscribeEvent
   public static void onBlockBreak(BreakEvent event) {
      if (!(Boolean)DeepAetherConfig.SERVER.enable_skyjade_rework.get()) {
         Player player = event.getPlayer();
         LevelAccessor level = event.getLevel();
         BlockState state = event.getState();
         BlockPos pos = event.getPos();
         if (!event.isCanceled()) {
            DAEquipmentUtil.damageSkyjadeRing(player, level, state, pos);
         }
      }
   }
}

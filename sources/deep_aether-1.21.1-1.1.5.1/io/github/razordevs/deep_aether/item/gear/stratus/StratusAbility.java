package io.github.razordevs.deep_aether.item.gear.stratus;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import io.github.razordevs.deep_aether.DeepAetherConfig;
import io.github.razordevs.deep_aether.client.DeepAetherKeys;
import io.github.razordevs.deep_aether.item.gear.DAEquipmentUtil;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class StratusAbility extends ArmorItem {
   public static float coolDown = 0.0F;
   private static boolean hasBeenOnGround = true;

   public StratusAbility(Holder<ArmorMaterial> armorMaterial, Type type, Properties properties) {
      super(armorMaterial, type, properties);
   }

   private static boolean isStratusDashActive(Player player) {
      return DAEquipmentUtil.hasFullStratusSet(player) && coolDown <= 0.0F;
   }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
      if (entity instanceof Player player) {
         if (player.onGround()) {
            hasBeenOnGround = true;
         }

         if (coolDown >= 0.0F) {
            coolDown -= 0.02F;
         }

         if (level.isClientSide() && DAEquipmentUtil.hasFullStratusSet(player) && DeepAetherKeys.STRATUS_DASH_ABILITY.isDown()) {
            dash(player);
         }
      }
   }

   static void dash(LivingEntity entity) {
      if (DAEquipmentUtil.hasFullStratusSet(entity) && hasBeenOnGround && entity instanceof Player player) {
         double x = player.getLookAngle().x * 1.3 * 2.0;
         double y = player.getLookAngle().y * 1.3;
         double z = player.getLookAngle().z * 1.3 * 2.0;
         double a = Math.abs(y * 0.5);
         a = 1.0 - a;
         if (isStratusDashActive(player)) {
            hasBeenOnGround = false;
            coolDown = ((Integer)DeepAetherConfig.SERVER.stratus_dash_cooldown.get()).intValue();
            float dashMultiplier = (float)DAEquipmentUtil.handleStratusRingBoost(player);
            player.push(x * a * dashMultiplier, y * dashMultiplier, z * a * dashMultiplier);
            if (player instanceof ServerPlayer serverPlayer) {
               serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
            }
         }
      }
   }

   public static void moreBoostedJump(LivingEntity entity) {
      if (DAEquipmentUtil.hasFullStratusSet(entity)) {
         if (entity instanceof Player player) {
            if (player.hasData(AetherDataAttachments.AETHER_PLAYER)
               && ((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).isGravititeJumpActive()) {
               player.push(0.0, 1.3 * (float)DAEquipmentUtil.handleStratusRingBoost(player), 0.0);
               if (player instanceof ServerPlayer serverPlayer) {
                  serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
               }
            }
         } else {
            entity.push(0.0, 1.3, 0.0);
         }
      }
   }
}

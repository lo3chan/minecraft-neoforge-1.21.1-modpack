package io.github.razordevs.deep_aether.item.gear.skyjade;

import com.aetherteam.nitrogen.attachment.INBTSynchable.Direction;
import io.github.razordevs.deep_aether.DeepAetherConfig;
import io.github.razordevs.deep_aether.client.DeepAetherKeys;
import io.github.razordevs.deep_aether.item.gear.DAEquipmentUtil;
import io.github.razordevs.deep_aether.networking.attachment.DAAttachments;
import io.github.razordevs.deep_aether.networking.attachment.DAPlayerAttachment;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class SkyjadeHelmetItem extends SkyjadeArmorItem {
   public SkyjadeHelmetItem(Holder<ArmorMaterial> pMaterial, Type pType, Properties pProperties) {
      super(pMaterial, pType, pProperties);
   }

   public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
      if ((Boolean)DeepAetherConfig.SERVER.enable_skyjade_rework.get() && pSlotId == 39 && pEntity instanceof Player player) {
         DAPlayerAttachment attachment = (DAPlayerAttachment)player.getData(DAAttachments.PLAYER);
         if (pLevel.isClientSide() && attachment.isSkyjadeAbilityActivated() != DeepAetherKeys.TOGGLE_SKYJADE_TRANSPARENCY.isDown()) {
            attachment.setSynched(player.getId(), Direction.SERVER, "setSkyjadeAbilityActivated", DeepAetherKeys.TOGGLE_SKYJADE_TRANSPARENCY.isDown());
         }

         DAEquipmentUtil.updateSkyjadeBehavior(player, attachment.isSkyjadeAbilityActivated() && attachment.hasSkyjadeSet());
      }
   }
}

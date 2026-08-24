package io.github.razordevs.deep_aether.item.dungeon.brass;

import com.aetherteam.aether.item.accessories.AccessoryItem;
import com.aetherteam.aether.item.accessories.SlotIdentifierHolder;
import com.aetherteam.aether.item.accessories.miscellaneous.ShieldOfRepulsionItem;
import io.github.razordevs.deep_aether.networking.attachment.DAAttachments;
import io.github.razordevs.deep_aether.networking.attachment.DAPlayerAttachment;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class WindShieldItem extends AccessoryItem implements SlotIdentifierHolder {
   private static final ResourceLocation SHIELD_OF_REPULSION = ResourceLocation.fromNamespaceAndPath(
      "deep_aether", "textures/models/accessory/wind_shield/wind_shield_accessory.png"
   );

   public WindShieldItem(Properties properties) {
      super(properties);
   }

   public WindShieldItem(Holder<SoundEvent> soundEventSupplier, Properties properties) {
      super(soundEventSupplier, properties);
   }

   public ResourceLocation getWindShieldTexture() {
      return SHIELD_OF_REPULSION;
   }

   public void tick(ItemStack stack, SlotReference reference) {
      if (reference.entity() instanceof Player player) {
         DAPlayerAttachment attachment = (DAPlayerAttachment)player.getData(DAAttachments.PLAYER);
         if (attachment.getWindShieldCooldown() > 0) {
            ((DAPlayerAttachment)player.getData(DAAttachments.PLAYER)).setWindShieldCooldown(attachment.getWindShieldCooldown() - 1);
         }
      }

      super.tick(stack, reference);
   }

   public SlotTypeReference getIdentifier() {
      return ShieldOfRepulsionItem.getStaticIdentifier();
   }
}

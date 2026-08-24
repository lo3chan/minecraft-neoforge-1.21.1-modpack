package io.github.razordevs.deep_aether.item.dungeon.brass;

import com.aetherteam.aether.item.accessories.cape.CapeItem;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class CloudCapeItem extends CapeItem {
   boolean hasDoubleJumped = false;
   boolean canJump = false;

   public CloudCapeItem(ResourceLocation capeLocation, Properties properties) {
      super(capeLocation, properties);
   }

   public void tick(ItemStack stack, SlotReference reference) {
      if (reference.entity().level().isClientSide()) {
         if (reference.entity().onGround()) {
            this.hasDoubleJumped = false;
            this.canJump = false;
         } else if (!this.canJump) {
            this.canJump = !Minecraft.getInstance().options.keyJump.isDown();
         } else if (!this.hasDoubleJumped && Minecraft.getInstance().options.keyJump.isDown()) {
            this.hasDoubleJumped = true;
            reference.entity().setDeltaMovement(reference.entity().getDeltaMovement().x(), 0.42, reference.entity().getDeltaMovement().z());
            reference.entity().resetFallDistance();
            if (reference.entity() instanceof ServerPlayer serverPlayer) {
               serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
            }
         }
      }
   }
}

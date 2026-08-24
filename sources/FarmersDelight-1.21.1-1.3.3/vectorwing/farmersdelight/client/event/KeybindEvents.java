package vectorwing.farmersdelight.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent.Pre;
import net.neoforged.neoforge.network.PacketDistributor;
import vectorwing.farmersdelight.common.item.SkilletItem;
import vectorwing.farmersdelight.common.network.payload.FlipSkilletPayload;
import vectorwing.farmersdelight.common.registry.ModDataComponents;

@EventBusSubscriber(
   modid = "farmersdelight",
   value = {Dist.CLIENT}
)
public class KeybindEvents {
   @SubscribeEvent
   public static void preClientTick(Pre event) {
      Minecraft mc = Minecraft.getInstance();
      Player player = mc.player;
      if (player != null && player.isUsingItem()) {
         ItemStack useItem = player.getUseItem();
         if (useItem.getItem() instanceof SkilletItem && !useItem.has((DataComponentType)ModDataComponents.SKILLET_FLIP_TIMESTAMP.get())) {
            while (mc.options.keyAttack.consumeClick()) {
               PacketDistributor.sendToServer(FlipSkilletPayload.INSTANCE, new CustomPacketPayload[0]);
            }
         }
      }
   }
}

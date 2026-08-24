package net.cibernet.alchemancy.properties;

import java.util.Random;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent.Pre;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;

@EventBusSubscriber({Dist.CLIENT})
public class FleetingProperty extends Property {
   private static final Random random = new Random();
   private static float sparkColor = 0.0F;

   @Override
   public void onItemTossed(Player player, ItemStack stack, ItemEntity itemEntity, ItemTossEvent event) {
      itemEntity.getItem().setCount(0);
      event.setCanceled(true);
   }

   @Override
   public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
      if (!level.isClientSide() && user.getRandom().nextFloat() < 0.002F) {
         stack.shrink(1);
      }
   }

   @OnlyIn(Dist.CLIENT)
   @SubscribeEvent
   private static void onClientTick(Pre event) {
      if (random.nextFloat() < 0.05F) {
         sparkColor = 1.0F;
      } else {
         sparkColor = Math.max(0.0F, sparkColor - 0.033333335F);
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return ARGB32.lerp(sparkColor, 1772571, 13761279);
   }
}

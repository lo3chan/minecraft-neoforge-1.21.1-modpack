package net.nycto_team.overpacked.core;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Pre;
import net.neoforged.neoforge.event.village.WandererTradesEvent;
import net.nycto_team.overpacked.registry.ModItems;
import net.nycto_team.overpacked.util.ModLoc;

@EventBusSubscriber(
   modid = "overpacked"
)
public class Events {
   private static final ResourceLocation speed = ModLoc.get("speed");

   @SubscribeEvent
   public static void addCustomWanderingTrades(WandererTradesEvent event) {
      List<ItemListing> rare_trades = event.getRareTrades();
      rare_trades.add(
         (trader, random) -> new MerchantOffer(new ItemCost(Items.EMERALD, 45), new ItemStack((ItemLike)ModItems.giant_backpack.get()), 1, 12, 1.0F)
      );
   }

   @SubscribeEvent
   public static void onPlayerTick(Pre event) {
      Player player = event.getEntity();
      if (!player.level().isClientSide()) {
         double slowdown = 0.0;
         List<ItemStack> items = new ArrayList<>(player.getInventory().items);
         if (!player.getOffhandItem().isEmpty()) {
            items.add(player.getOffhandItem());
         }

         for (ItemStack stack : items) {
            CustomData data = (CustomData)stack.get(DataComponents.CUSTOM_DATA);
            if (data != null && data.copyTag().contains("Count")) {
               int count = data.copyTag().getInt("Count");
               if (count >= 27) {
                  slowdown += (1.0 - slowdown) * (count < 54 ? 0.1 : (count < 81 ? 0.2 : 0.3));
               }
            }
         }

         AttributeInstance attribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
         if (attribute.getModifier(speed) != null) {
            attribute.removeModifier(speed);
         }

         attribute.addTransientModifier(new AttributeModifier(speed, -slowdown, Operation.ADD_MULTIPLIED_TOTAL));
      }
   }
}

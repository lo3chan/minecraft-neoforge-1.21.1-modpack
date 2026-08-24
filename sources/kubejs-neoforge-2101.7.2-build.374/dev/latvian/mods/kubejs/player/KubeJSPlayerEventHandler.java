package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.net.KubeJSNet;
import dev.latvian.mods.kubejs.net.SyncServerDataPayload;
import dev.latvian.mods.kubejs.plugin.builtin.event.PlayerEvents;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent.AdvancementEarnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent.Close;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent.Open;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;

@EventBusSubscriber(
   modid = "kubejs"
)
public class KubeJSPlayerEventHandler {
   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void datapackSync(OnDatapackSyncEvent event) {
      SyncServerDataPayload payload = event.getPlayerList().getServer().getServerResources().managers().kjs$getServerScriptManager().serverData;
      if (payload != null) {
         event.getRelevantPlayers().forEach(player -> KubeJSNet.safeSendToPlayer(player, payload));
      }
   }

   @SubscribeEvent
   public static void loggedIn(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (PlayerEvents.LOGGED_IN.hasListeners()) {
            PlayerEvents.LOGGED_IN.post(ScriptType.SERVER, new SimplePlayerKubeEvent(player));
         }

         player.inventoryMenu.addSlotListener(player.kjs$getInventoryChangeListener());
         if (!ConsoleJS.SERVER.errors.isEmpty() && !CommonProperties.get().hideServerScriptErrors) {
            player.displayClientMessage(ConsoleJS.SERVER.errorsComponent("/kubejs errors server"), false);
         }

         player.kjs$getStages().sync();
      }
   }

   @SubscribeEvent
   public static void cloned(Clone event) {
      if (event.getOriginal() instanceof ServerPlayer oldPlayer && event.getEntity() instanceof ServerPlayer newPlayer) {
         newPlayer.kjs$setRawPersistentData(oldPlayer.kjs$getRawPersistentData());
         newPlayer.inventoryMenu.addSlotListener(newPlayer.kjs$getInventoryChangeListener());
         if (PlayerEvents.CLONED.hasListeners()) {
            PlayerEvents.CLONED.post(ScriptType.SERVER, new PlayerClonedKubeEvent(newPlayer, oldPlayer, !event.isWasDeath()));
         }
      }
   }

   @SubscribeEvent
   public static void respawn(PlayerRespawnEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (PlayerEvents.RESPAWNED.hasListeners()) {
            PlayerEvents.RESPAWNED.post(ScriptType.SERVER, new PlayerRespawnedKubeEvent(player, event.isEndConquered()));
         }

         player.kjs$getStages().sync();
      }
   }

   @SubscribeEvent
   public static void loggedOut(PlayerLoggedOutEvent event) {
      if (PlayerEvents.LOGGED_OUT.hasListeners() && event.getEntity() instanceof ServerPlayer player) {
         PlayerEvents.LOGGED_OUT.post(ScriptType.SERVER, new SimplePlayerKubeEvent(player));
      }
   }

   @SubscribeEvent
   public static void tick(Post event) {
      if (PlayerEvents.TICK.hasListeners() && event.getEntity() instanceof ServerPlayer player) {
         PlayerEvents.TICK.post(player, new SimplePlayerKubeEvent(player));
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void chatDecorate(ServerChatEvent event) {
      if (PlayerEvents.DECORATE_CHAT.hasListeners()) {
         PlayerEvents.DECORATE_CHAT.post(ScriptType.SERVER, new PlayerChatReceivedKubeEvent(event)).applyCancel(event);
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void chatReceived(ServerChatEvent event) {
      if (PlayerEvents.CHAT.hasListeners()) {
         PlayerEvents.CHAT.post(ScriptType.SERVER, new PlayerChatReceivedKubeEvent(event)).applyCancel(event);
      }
   }

   @SubscribeEvent
   public static void advancement(AdvancementEarnEvent event) {
      ResourceLocation id = event.getAdvancement().id();
      if (PlayerEvents.ADVANCEMENT.hasListeners(id) && event.getEntity() instanceof ServerPlayer player) {
         PlayerEvents.ADVANCEMENT.post(new PlayerAdvancementKubeEvent(player, player.server.kjs$getAdvancement(id)), id);
      }
   }

   @SubscribeEvent
   public static void inventoryOpened(Open event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         AbstractContainerMenu menu = event.getContainer();
         if (!(menu instanceof InventoryMenu)) {
            menu.addSlotListener(player.kjs$getInventoryChangeListener());
         }

         ResourceKey<MenuType<?>> key;
         try {
            key = menu.getType().kjs$getKey();
         } catch (Exception var5) {
            return;
         }

         if (key != null) {
            if (PlayerEvents.INVENTORY_OPENED.hasListeners(key)) {
               PlayerEvents.INVENTORY_OPENED.post(player, key, new InventoryKubeEvent(player, menu));
            }

            if (menu instanceof ChestMenu && PlayerEvents.CHEST_OPENED.hasListeners(key)) {
               PlayerEvents.CHEST_OPENED.post(player, key, new ChestKubeEvent(player, menu));
            }
         }
      }
   }

   @SubscribeEvent
   public static void inventoryClosed(Close event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         AbstractContainerMenu menu = event.getContainer();

         ResourceKey<MenuType<?>> key;
         try {
            key = menu.getType().kjs$getKey();
         } catch (Exception var5) {
            return;
         }

         if (key != null) {
            if (PlayerEvents.INVENTORY_CLOSED.hasListeners(key)) {
               PlayerEvents.INVENTORY_CLOSED.post(player, key, new InventoryKubeEvent(player, menu));
            }

            if (menu instanceof ChestMenu && PlayerEvents.CHEST_CLOSED.hasListeners(key)) {
               PlayerEvents.CHEST_CLOSED.post(player, key, new ChestKubeEvent(player, menu));
            }
         }
      }
   }

   @SubscribeEvent
   public static void dimensionChanged(PlayerChangedDimensionEvent event) {
      try {
         event.getEntity().kjs$getStages().sync();
      } catch (Exception var2) {
      }
   }
}

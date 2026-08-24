package dev.shadowsoffire.placebo;

import dev.shadowsoffire.placebo.events.ResourceReloadEvent;
import dev.shadowsoffire.placebo.patreon.TrailsManager;
import dev.shadowsoffire.placebo.patreon.WingsManager;
import dev.shadowsoffire.placebo.patreon.wings.Wing;
import dev.shadowsoffire.placebo.patreon.wings.WingLayer;
import dev.shadowsoffire.placebo.util.SpecialTooltipItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.PlayerSkin.Model;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.AddLayers;
import net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent;
import net.neoforged.neoforge.client.event.ScreenEvent.MouseScrolled.Pre;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(
   value = {Dist.CLIENT},
   modid = "placebo"
)
public class PlaceboClient {
   public static long ticks = 0L;
   private static int scrollIdx = 0;
   private static ItemStack currentTooltipItem = ItemStack.EMPTY;
   private static long tooltipTick = 0L;

   @SubscribeEvent
   public static void setup(FMLClientSetupEvent e) {
      TrailsManager.init();
      WingsManager.init(e);
      NeoForge.EVENT_BUS.addListener(PlaceboClient::tick);
      NeoForge.EVENT_BUS.addListener(PlaceboClient::tooltip);
      NeoForge.EVENT_BUS.addListener(PlaceboClient::scroll);
      NeoForge.EVENT_BUS.addListener(PlaceboClient::scroll2);
   }

   @SubscribeEvent
   public static void keys(RegisterKeyMappingsEvent e) {
      e.register(TrailsManager.TOGGLE);
      e.register(WingsManager.TOGGLE);
   }

   @SubscribeEvent
   public static void clientResource(RegisterClientReloadListenersEvent e) {
      e.registerReloadListener((ResourceManagerReloadListener)res -> NeoForge.EVENT_BUS.post(new ResourceReloadEvent(res, LogicalSide.CLIENT)));
   }

   @SubscribeEvent
   public static void addLayers(AddLayers e) {
      Wing.INSTANCE = new Wing(e.getEntityModels().bakeLayer(WingsManager.WING_LOC));

      for (Model s : e.getSkins()) {
         LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> skin = (LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>)e.getSkin(
            s
         );
         skin.addLayer(new WingLayer(skin));
      }
   }

   public static float getColorTicks() {
      return ((float)ticks + Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false)) / 0.5F;
   }

   @Nullable
   public static PotionBrewing getBrewingRegistry() {
      ClientLevel level = Minecraft.getInstance().level;
      return level == null ? null : level.potionBrewing();
   }

   public static int getTooltipScrollIndex() {
      return scrollIdx;
   }

   public static int getTooltipScrollIndex(int size) {
      return Math.floorMod(scrollIdx, size);
   }

   public static void tick(Post e) {
      ticks++;
   }

   public static void scroll(Pre e) {
      if (currentTooltipItem.getItem() instanceof SpecialTooltipItem && tooltipTick == ticks && Screen.hasShiftDown()) {
         scrollIdx = scrollIdx + (e.getScrollDeltaY() < 0.0 ? 1 : -1);
         e.setCanceled(true);
      }
   }

   public static void scroll2(MouseScrollingEvent e) {
      if (currentTooltipItem.getItem() instanceof SpecialTooltipItem && tooltipTick == ticks && Screen.hasShiftDown()) {
         scrollIdx = scrollIdx + (e.getScrollDeltaY() < 0.0 ? 1 : -1);
         e.setCanceled(true);
      }
   }

   public static void tooltip(ItemTooltipEvent e) {
      currentTooltipItem = e.getItemStack();
      tooltipTick = ticks;
   }
}

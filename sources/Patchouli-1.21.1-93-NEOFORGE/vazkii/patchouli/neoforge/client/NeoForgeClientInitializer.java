package vazkii.patchouli.neoforge.client;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.InteractionResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.ModelEvent.ModifyBakingResult;
import net.neoforged.neoforge.client.event.ModelEvent.RegisterAdditional;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.base.BookModel;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.BookContentResourceListenerLoader;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.handler.BookRightClickHandler;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.client.handler.TooltipHandler;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.item.PatchouliItems;

@EventBusSubscriber(
   modid = "patchouli",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class NeoForgeClientInitializer {
   private static final Lock BOOK_LOAD_LOCK = new ReentrantLock();
   private static final Condition BOOK_LOAD_CONDITION = BOOK_LOAD_LOCK.newCondition();
   private static boolean booksLoaded = false;

   public static void signalBooksLoaded() {
      BOOK_LOAD_LOCK.lock();
      booksLoaded = true;
      BOOK_LOAD_CONDITION.signalAll();
      BOOK_LOAD_LOCK.unlock();
   }

   private static List<ResourceLocation> getBookModels() {
      BOOK_LOAD_LOCK.lock();

      List var0;
      try {
         while (!booksLoaded) {
            BOOK_LOAD_CONDITION.awaitUninterruptibly();
         }

         var0 = BookRegistry.INSTANCE.books.values().stream().map(b -> b.model).toList();
      } finally {
         BOOK_LOAD_LOCK.unlock();
      }

      return var0;
   }

   @SubscribeEvent
   public static void modelRegistry(RegisterAdditional e) {
      getBookModels().stream().map(ModelResourceLocation::standalone).forEach(e::register);
      ItemPropertyFunction prop = (stack, world, entity, seed) -> ItemModBook.getCompletion(stack);
      ItemProperties.register(PatchouliItems.BOOK, ResourceLocation.fromNamespaceAndPath("patchouli", "completion"), prop);
   }

   @SubscribeEvent
   public static void registerReloadListeners(RegisterClientReloadListenersEvent e) {
      e.registerReloadListener(BookContentResourceListenerLoader.INSTANCE);
      e.registerReloadListener((ResourceManagerReloadListener)manager -> {
         if (Minecraft.getInstance().level != null) {
            PatchouliAPI.LOGGER.info("Reloading resource pack-based books");
            ClientBookRegistry.INSTANCE.reload();
         } else {
            PatchouliAPI.LOGGER.debug("Not reloading resource pack-based books as client world is missing");
         }
      });
   }

   @SubscribeEvent
   public static void registerOverlays(RegisterGuiLayersEvent evt) {
      evt.registerAbove(VanillaGuiLayers.CROSSHAIR, ResourceLocation.fromNamespaceAndPath("patchouli", "book_overlay"), BookRightClickHandler::onRenderHUD);
      evt.registerBelow(
         VanillaGuiLayers.BOSS_OVERLAY, ResourceLocation.fromNamespaceAndPath("patchouli", "multiblock_progress"), MultiblockVisualizationHandler::onRenderHUD
      );
   }

   @SubscribeEvent
   public static void onInitializeClient(FMLClientSetupEvent evt) {
      ClientBookRegistry.INSTANCE.init();
      PersistentData.setup();
      NeoForge.EVENT_BUS.addListener(e -> ClientTicker.endClientTick(Minecraft.getInstance()));
      NeoForge.EVENT_BUS.addListener(e -> BookRightClickHandler.onRightClick(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec()));
      NeoForge.EVENT_BUS.addListener(e -> {
         InteractionResult result = MultiblockVisualizationHandler.onPlayerInteract(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec());
         if (result.consumesAction()) {
            e.setCanceled(true);
            e.setCancellationResult(result);
         }
      });
      NeoForge.EVENT_BUS.addListener(e -> MultiblockVisualizationHandler.onClientTick(Minecraft.getInstance()));
      NeoForge.EVENT_BUS.addListener(e -> ClientTicker.renderTickStart(e.getPartialTick().getGameTimeDeltaPartialTick(false)));
      NeoForge.EVENT_BUS.addListener(e -> ClientTicker.renderTickEnd());
      NeoForge.EVENT_BUS.addListener(e -> ClientAdvancements.playerLogout());
      NeoForge.EVENT_BUS.addListener(e -> TooltipHandler.onTooltip(e.getGraphics(), e.getItemStack(), e.getX(), e.getY()));
   }

   @SubscribeEvent
   public static void replaceBookModel(ModifyBakingResult evt) {
      ModelResourceLocation key = ModelResourceLocation.inventory(PatchouliItems.BOOK_ID);
      evt.getModels()
         .computeIfPresent(
            key, (k, oldModel) -> new BookModel(oldModel, model -> Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(model)))
         );
   }
}

package vazkii.patchouli.neoforge.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.CreativeModeTabRegistry;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import vazkii.patchouli.common.advancement.PatchouliCriteriaTriggers;
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.command.OpenBookCommand;
import vazkii.patchouli.common.handler.LecternEventHandler;
import vazkii.patchouli.common.handler.ReloadContentsHandler;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.item.PatchouliDataComponents;
import vazkii.patchouli.common.item.PatchouliItems;
import vazkii.patchouli.neoforge.network.NeoForgeNetworkHandler;

@EventBusSubscriber(
   modid = "patchouli",
   bus = Bus.MOD
)
@Mod("patchouli")
public class NeoForgeModInitializer {
   public NeoForgeModInitializer(IEventBus eventBus, Dist dist, ModContainer container) {
      NeoForgePatchouliConfig.setup(container);
      eventBus.addListener(NeoForgeNetworkHandler::setupPackets);
   }

   @SubscribeEvent
   public static void register(RegisterEvent evt) {
      evt.register(Registries.SOUND_EVENT, rh -> PatchouliSounds.submitRegistrations(rh::register));
      evt.register(Registries.DATA_COMPONENT_TYPE, rh -> PatchouliDataComponents.submitDataComponentRegistrations(rh::register));
      evt.register(Registries.ITEM, rh -> PatchouliItems.submitItemRegistrations(rh::register));
      evt.register(Registries.TRIGGER_TYPE, rh -> PatchouliCriteriaTriggers.submitTriggerRegistrations(rh::register));
   }

   @SubscribeEvent
   public static void processCreativeTabs(BuildCreativeModeTabContentsEvent evt) {
      BookRegistry.INSTANCE.books.values().forEach(b -> {
         if (!b.noBook) {
            ItemStack book = ItemModBook.forBook(b);
            if (evt.getTabKey() == CreativeModeTabs.SEARCH) {
               if (!evt.getSearchEntries().contains(book)) {
                  evt.accept(book, TabVisibility.SEARCH_TAB_ONLY);
               }
            } else if (b.creativeTab != null && evt.getTab() == CreativeModeTabRegistry.getTab(b.creativeTab)) {
               evt.accept(book);
            }
         }
      });
   }

   @SubscribeEvent
   public static void onInitialize(FMLCommonSetupEvent evt) {
      NeoForge.EVENT_BUS.addListener(e -> OpenBookCommand.register(e.getDispatcher()));
      NeoForge.EVENT_BUS.addListener(e -> {
         InteractionResult result = LecternEventHandler.rightClick(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec());
         if (result.consumesAction()) {
            e.setCanceled(true);
            e.setCancellationResult(result);
         }
      });
      BookRegistry.INSTANCE.init();
      NeoForge.EVENT_BUS.addListener(e -> ReloadContentsHandler.dataReloaded(e.getServer()));
   }
}

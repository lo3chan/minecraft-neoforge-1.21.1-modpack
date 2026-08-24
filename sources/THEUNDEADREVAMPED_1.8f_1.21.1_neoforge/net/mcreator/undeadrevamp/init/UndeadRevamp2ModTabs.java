package net.mcreator.undeadrevamp.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(
   bus = Bus.MOD
)
public class UndeadRevamp2ModTabs {
   public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "undead_revamp2");
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> THEUNDEADCREATIVE = REGISTRY.register(
      "theundeadcreative",
      () -> CreativeModeTab.builder()
         .title(Component.translatable("item_group.undead_revamp2.theundeadcreative"))
         .icon(() -> new ItemStack((ItemLike)UndeadRevamp2ModItems.ACIDSACK.get()))
         .displayItems((parameters, tabData) -> {
            tabData.accept((ItemLike)UndeadRevamp2ModItems.THEHEAVY_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.BOMBER_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.THEBIDY_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.THEROD_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.DEADCLOGGER_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.CLOGGER_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.THESPITTER_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.THEPREGNANT_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.THEHORRORS_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.THEHORRORSDECOYS_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.THEIMMORTAL_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.THEWOLF_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.THEBEARTAMER_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.SLAVEMAN_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.INVISICLOGGER_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.INVISIIMMORTAL_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.INVISIBLEBIDY_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.NEOCRORINES_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.THEDUNGEON_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.THESMOKER_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.THESWARMER_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.THEGLITER_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.THE_MOONFLOWER_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.THEORDURE_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.SUCKER_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.THEHUNTER_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.THESOMNOLENCE_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.BIGSUCKER_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.THESKEEPER_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.INVISILEHCERY_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.LECHERY_SPAWN_EGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.THELURKER_SPAWN_EGG.get());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.BOSTROXORE.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.BOSTROKESTONE.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.BLOCKOFBOSTROX.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.BOSTROXWALL.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.BOSTROXSTAIRS.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.COBBLEDBOSTROXWALL.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.COBBLEDBOSTROXSTAIRS.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.BOSTROXSLABS.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.COBBLEDBOSTROXSLAB.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.BLACPETALBLOCK.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.RAREIMMORTALSUMMONBLOCK.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.WOODENNEST.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.SOMNOLENCESPAWNS.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.CHISELED_DRIPSTONEPILLAR.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.CHISELEDDRIPSTONEPILLARTOP.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.CHISELEDDRIPSTONEPILLARBOTTOM.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.CHISELEDDRIPSTONEBLOCK.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.BASALTECHESTE.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.INDUCERSTONEINACTIVE.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.DEEPSLATEINDUCERINACTIVE.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.OAKINDUCER.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.DARKOAKINDUCER.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.SPURCEINDUCER.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.CHERRYBLOSSOMINDUCER.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.JUNGLEINDUCER.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.BIRCHINDUCER.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.SAVANNAINDUCER.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.MANGROVEINDUCER.get()).asItem());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.FIZZYSNOWGOO.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.ACIDDICSACKBOWL.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.SPIITERFRIEDEGG.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.SPITTEA.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.RAWBOSTROX.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.BOSTROXINGOT.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.BOSTROXSWORD.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.WINCALLERFAN.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.BOSTROXSET_HELMET.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.BOSTROXSET_CHESTPLATE.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.BOSTROXSET_BOOTS.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.BEESPHEROMONES.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.HUNTEREYE.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.HUNTERSACKRED.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.LUCKYSACK.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.FLIGHTSACK.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.HUNTERSACKPINK.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.HEAVYTOOTH.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.HEAVYUPGRADE.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.BONEDAGGER.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.CHAINSWORD.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.TOOTHMACE.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.PRIMODIALARMOUR_HELMET.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.PRIMODIALARMOUR_CHESTPLATE.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.PRIMODIALARMOUR_BOOTS.get());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.ARAPHOLIA.get()).asItem());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.CLOOGERRIBS.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.CLOGGERUPGRADE.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.CLOGGERARMOR_HELMET.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.CLOGGERARMOR_CHESTPLATE.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.CLOGGERARMOR_LEGGINGS.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.CLOGGERARMOR_BOOTS.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.CLOGGGERRIBSWITHBUN.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.ACIDSACK.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.F_IRESACK.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.ARAPOHOLIASPRAY.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.QUEENBEEPERFUME.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.THE_SOMNOLENCEEXTRACT.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.SLEEPINGSMOKEBOMB.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.ANCIENTBREAD.get());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.COFFINBROAD.get()).asItem());
            tabData.accept(((Block)UndeadRevamp2ModBlocks.ALTARNOM.get()).asItem());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.IMBUEMASK_HELMET.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.NULLIFYMASK_HELMET.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.WITHERCHARGEMASK_HELMET.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.SHIELDMASK_HELMET.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.SUCKERMEMEBRANE.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.FRIEDSUCKER.get());
            tabData.accept((ItemLike)UndeadRevamp2ModItems.DEVILURCHIN.get());
         })
         .withSearchBar()
         .build()
   );

   @SubscribeEvent
   public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
      if (tabData.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
         tabData.accept((ItemLike)UndeadRevamp2ModItems.AXESTROM_SPAWN_EGG.get());
         tabData.accept((ItemLike)UndeadRevamp2ModItems.CRACKLEBALL_SPAWN_EGG.get());
         tabData.accept((ItemLike)UndeadRevamp2ModItems.THEBIDYUPSIDE_SPAWN_EGG.get());
         tabData.accept((ItemLike)UndeadRevamp2ModItems.PROPBALL_1_SPAWN_EGG.get());
      } else if (tabData.getTabKey() == CreativeModeTabs.COMBAT) {
         tabData.accept((ItemLike)UndeadRevamp2ModItems.WINDTEXT.get());
      }
   }
}

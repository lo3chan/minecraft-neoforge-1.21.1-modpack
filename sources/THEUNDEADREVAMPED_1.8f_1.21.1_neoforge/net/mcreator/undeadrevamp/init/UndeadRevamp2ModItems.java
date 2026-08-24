package net.mcreator.undeadrevamp.init;

import net.mcreator.undeadrevamp.block.display.BasaltechesteDisplayItem;
import net.mcreator.undeadrevamp.item.AciddicsackbowlItem;
import net.mcreator.undeadrevamp.item.AcidsackItem;
import net.mcreator.undeadrevamp.item.AncientbreadItem;
import net.mcreator.undeadrevamp.item.ArapoholiasprayItem;
import net.mcreator.undeadrevamp.item.BeespheromonesItem;
import net.mcreator.undeadrevamp.item.BonedaggerItem;
import net.mcreator.undeadrevamp.item.BostroxingotItem;
import net.mcreator.undeadrevamp.item.BostroxsetItem;
import net.mcreator.undeadrevamp.item.BostroxswordItem;
import net.mcreator.undeadrevamp.item.BoulderthrowItem;
import net.mcreator.undeadrevamp.item.ChainswordItem;
import net.mcreator.undeadrevamp.item.CloggerarmorItem;
import net.mcreator.undeadrevamp.item.CloggerupgradeItem;
import net.mcreator.undeadrevamp.item.ClogggerribswithbunItem;
import net.mcreator.undeadrevamp.item.CloogerribsItem;
import net.mcreator.undeadrevamp.item.CustomiconItem;
import net.mcreator.undeadrevamp.item.CustomicontwoItem;
import net.mcreator.undeadrevamp.item.DevilurchinItem;
import net.mcreator.undeadrevamp.item.FIresackItem;
import net.mcreator.undeadrevamp.item.FizzysnowgooItem;
import net.mcreator.undeadrevamp.item.FlightsackItem;
import net.mcreator.undeadrevamp.item.FriedsuckerItem;
import net.mcreator.undeadrevamp.item.HeavytoothItem;
import net.mcreator.undeadrevamp.item.HeavyupgradeItem;
import net.mcreator.undeadrevamp.item.HonniethrowItem;
import net.mcreator.undeadrevamp.item.HuntereyeItem;
import net.mcreator.undeadrevamp.item.HuntersackpinkItem;
import net.mcreator.undeadrevamp.item.HuntersackredItem;
import net.mcreator.undeadrevamp.item.ImbuemaskItem;
import net.mcreator.undeadrevamp.item.LuckysackItem;
import net.mcreator.undeadrevamp.item.NullifymaskItem;
import net.mcreator.undeadrevamp.item.PregnantneccItem;
import net.mcreator.undeadrevamp.item.PrimodialarmourItem;
import net.mcreator.undeadrevamp.item.QueenbeeperfumeItem;
import net.mcreator.undeadrevamp.item.RawbostroxItem;
import net.mcreator.undeadrevamp.item.ShieldmaskItem;
import net.mcreator.undeadrevamp.item.SleepingsmokebombItem;
import net.mcreator.undeadrevamp.item.SpiiterfriedeggItem;
import net.mcreator.undeadrevamp.item.SpitteaItem;
import net.mcreator.undeadrevamp.item.SpitterneccItem;
import net.mcreator.undeadrevamp.item.SuckermemebraneItem;
import net.mcreator.undeadrevamp.item.TheSomnolenceextractItem;
import net.mcreator.undeadrevamp.item.ToothmaceItem;
import net.mcreator.undeadrevamp.item.WINDTEXTItem;
import net.mcreator.undeadrevamp.item.WincallerfanItem;
import net.mcreator.undeadrevamp.item.WitherchargemaskItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

public class UndeadRevamp2ModItems {
   public static final Items REGISTRY = DeferredRegister.createItems("undead_revamp2");
   public static final DeferredItem<Item> BOMBER_SPAWN_EGG = REGISTRY.register(
      "bomber_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.BOMBER, -3355648, -16751104, new Properties())
   );
   public static final DeferredItem<Item> THESPECTRE_SPAWN_EGG = REGISTRY.register(
      "thespectre_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.THESPECTRE, -14191476, -5317676, new Properties())
   );
   public static final DeferredItem<Item> THESPITTER_SPAWN_EGG = REGISTRY.register(
      "thespitter_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.THESPITTER, -16738048, -16751053, new Properties())
   );
   public static final DeferredItem<Item> THEHORRORS_SPAWN_EGG = REGISTRY.register(
      "thehorrors_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.THEHORRORS, -10066330, -1, new Properties())
   );
   public static final DeferredItem<Item> THEHORRORSDECOYS_SPAWN_EGG = REGISTRY.register(
      "thehorrorsdecoys_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.THEHORRORSDECOYS, -10066330, -1, new Properties())
   );
   public static final DeferredItem<Item> THESMOKER_SPAWN_EGG = REGISTRY.register(
      "thesmoker_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.THESMOKER, -6750157, -12292028, new Properties())
   );
   public static final DeferredItem<Item> THE_MOONFLOWER_SPAWN_EGG = REGISTRY.register(
      "the_moonflower_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.THE_MOONFLOWER, -10987432, -14339284, new Properties())
   );
   public static final DeferredItem<Item> ARAPHOLIA = block(UndeadRevamp2ModBlocks.ARAPHOLIA);
   public static final DeferredItem<Item> FIZZYSNOWGOO = REGISTRY.register("fizzysnowgoo", FizzysnowgooItem::new);
   public static final DeferredItem<Item> SUCKER_SPAWN_EGG = REGISTRY.register(
      "sucker_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.SUCKER, -65536, -16738048, new Properties())
   );
   public static final DeferredItem<Item> ACIDDICSACKBOWL = REGISTRY.register("aciddicsackbowl", AciddicsackbowlItem::new);
   public static final DeferredItem<Item> SPIITERFRIEDEGG = REGISTRY.register("spiiterfriedegg", SpiiterfriedeggItem::new);
   public static final DeferredItem<Item> THEHUNTER_SPAWN_EGG = REGISTRY.register(
      "thehunter_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.THEHUNTER, -26215, -16751104, new Properties())
   );
   public static final DeferredItem<Item> THEBEARTAMER_SPAWN_EGG = REGISTRY.register(
      "thebeartamer_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.THEBEARTAMER, -10066330, -10092544, new Properties())
   );
   public static final DeferredItem<Item> CUSTOMICON = REGISTRY.register("customicon", CustomiconItem::new);
   public static final DeferredItem<Item> RAWBOSTROX = REGISTRY.register("rawbostrox", RawbostroxItem::new);
   public static final DeferredItem<Item> BOSTROXINGOT = REGISTRY.register("bostroxingot", BostroxingotItem::new);
   public static final DeferredItem<Item> BOSTROXORE = block(UndeadRevamp2ModBlocks.BOSTROXORE);
   public static final DeferredItem<Item> BOSTROKESTONE = block(UndeadRevamp2ModBlocks.BOSTROKESTONE);
   public static final DeferredItem<Item> BLOCKOFBOSTROX = block(UndeadRevamp2ModBlocks.BLOCKOFBOSTROX);
   public static final DeferredItem<Item> BOSTROXSET_HELMET = REGISTRY.register("bostroxset_helmet", BostroxsetItem.Helmet::new);
   public static final DeferredItem<Item> BOSTROXSET_CHESTPLATE = REGISTRY.register("bostroxset_chestplate", BostroxsetItem.Chestplate::new);
   public static final DeferredItem<Item> BOSTROXSET_BOOTS = REGISTRY.register("bostroxset_boots", BostroxsetItem.Boots::new);
   public static final DeferredItem<Item> THEWOLF_SPAWN_EGG = REGISTRY.register(
      "thewolf_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.THEWOLF, -10092544, -52429, new Properties())
   );
   public static final DeferredItem<Item> AXESTROM_SPAWN_EGG = REGISTRY.register(
      "axestrom_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.AXESTROM, -10066330, -1, new Properties())
   );
   public static final DeferredItem<Item> THEORDURE_SPAWN_EGG = REGISTRY.register(
      "theordure_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.THEORDURE, -10066330, -13421773, new Properties())
   );
   public static final DeferredItem<Item> CRACKLEBALL_SPAWN_EGG = REGISTRY.register(
      "crackleball_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.CRACKLEBALL, -10066330, -1, new Properties())
   );
   public static final DeferredItem<Item> THESWARMER_SPAWN_EGG = REGISTRY.register(
      "theswarmer_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.THESWARMER, -8816337, -2645222, new Properties())
   );
   public static final DeferredItem<Item> BEESPHEROMONES = REGISTRY.register("beespheromones", BeespheromonesItem::new);
   public static final DeferredItem<Item> HUNTEREYE = REGISTRY.register("huntereye", HuntereyeItem::new);
   public static final DeferredItem<Item> HUNTERSACKRED = REGISTRY.register("huntersackred", HuntersackredItem::new);
   public static final DeferredItem<Item> THEBIDY_SPAWN_EGG = REGISTRY.register(
      "thebidy_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.THEBIDY, -256, -6711040, new Properties())
   );
   public static final DeferredItem<Item> INVISIBLEBIDY_SPAWN_EGG = REGISTRY.register(
      "invisiblebidy_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.INVISIBLEBIDY, -256, -6711040, new Properties())
   );
   public static final DeferredItem<Item> THEBIDYUPSIDE_SPAWN_EGG = REGISTRY.register(
      "thebidyupside_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.THEBIDYUPSIDE, -256, -6711040, new Properties())
   );
   public static final DeferredItem<Item> THEPREGNANT_SPAWN_EGG = REGISTRY.register(
      "thepregnant_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.THEPREGNANT, -6711040, -16751053, new Properties())
   );
   public static final DeferredItem<Item> LUCKYSACK = REGISTRY.register("luckysack", LuckysackItem::new);
   public static final DeferredItem<Item> FLIGHTSACK = REGISTRY.register("flightsack", FlightsackItem::new);
   public static final DeferredItem<Item> FLAME_SPAWN_EGG = REGISTRY.register(
      "flame_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.FLAME, -1, -1, new Properties())
   );
   public static final DeferredItem<Item> SLAVEMAN_SPAWN_EGG = REGISTRY.register(
      "slaveman_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.SLAVEMAN, -10066330, -3342337, new Properties())
   );
   public static final DeferredItem<Item> BOSTROXSWORD = REGISTRY.register("bostroxsword", BostroxswordItem::new);
   public static final DeferredItem<Item> WINCALLERFAN = REGISTRY.register("wincallerfan", WincallerfanItem::new);
   public static final DeferredItem<Item> THEROD_SPAWN_EGG = REGISTRY.register(
      "therod_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.THEROD, -8367104, -16239821, new Properties())
   );
   public static final DeferredItem<Item> COPPERTAR_SPAWN_EGG = REGISTRY.register(
      "coppertar_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.COPPERTAR, -1, -1, new Properties())
   );
   public static final DeferredItem<Item> THEGLITER_SPAWN_EGG = REGISTRY.register(
      "thegliter_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.THEGLITER, -10987432, -26113, new Properties())
   );
   public static final DeferredItem<Item> THEHEAVY_SPAWN_EGG = REGISTRY.register(
      "theheavy_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.THEHEAVY, -16764109, -16751104, new Properties())
   );
   public static final DeferredItem<Item> HEAVYTOOTH = REGISTRY.register("heavytooth", HeavytoothItem::new);
   public static final DeferredItem<Item> BONEDAGGER = REGISTRY.register("bonedagger", BonedaggerItem::new);
   public static final DeferredItem<Item> TOOTHMACE = REGISTRY.register("toothmace", ToothmaceItem::new);
   public static final DeferredItem<Item> BLACPETALBLOCK = block(UndeadRevamp2ModBlocks.BLACPETALBLOCK);
   public static final DeferredItem<Item> PRIMODIALARMOUR_HELMET = REGISTRY.register("primodialarmour_helmet", PrimodialarmourItem.Helmet::new);
   public static final DeferredItem<Item> PRIMODIALARMOUR_CHESTPLATE = REGISTRY.register("primodialarmour_chestplate", PrimodialarmourItem.Chestplate::new);
   public static final DeferredItem<Item> PRIMODIALARMOUR_BOOTS = REGISTRY.register("primodialarmour_boots", PrimodialarmourItem.Boots::new);
   public static final DeferredItem<Item> CLOGGER_SPAWN_EGG = REGISTRY.register(
      "clogger_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.CLOGGER, -13434829, -16764109, new Properties())
   );
   public static final DeferredItem<Item> INVISICLOGGER_SPAWN_EGG = REGISTRY.register(
      "invisiclogger_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.INVISICLOGGER, -16764109, -256, new Properties())
   );
   public static final DeferredItem<Item> PROPBALL_1_SPAWN_EGG = REGISTRY.register(
      "propball_1_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.PROPBALL_1, -10066330, -1, new Properties())
   );
   public static final DeferredItem<Item> DEADCLOGGER_SPAWN_EGG = REGISTRY.register(
      "deadclogger_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.DEADCLOGGER, -13434829, -16764109, new Properties())
   );
   public static final DeferredItem<Item> CUSTOMICONTWO = REGISTRY.register("customicontwo", CustomicontwoItem::new);
   public static final DeferredItem<Item> SPITTEA = REGISTRY.register("spittea", SpitteaItem::new);
   public static final DeferredItem<Item> THEIMMORTAL_SPAWN_EGG = REGISTRY.register(
      "theimmortal_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.THEIMMORTAL, -10079488, -6710887, new Properties())
   );
   public static final DeferredItem<Item> INVISIIMMORTAL_SPAWN_EGG = REGISTRY.register(
      "invisiimmortal_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.INVISIIMMORTAL, -256, -6711040, new Properties())
   );
   public static final DeferredItem<Item> RAREIMMORTALSUMMONBLOCK = block(UndeadRevamp2ModBlocks.RAREIMMORTALSUMMONBLOCK);
   public static final DeferredItem<Item> CLOOGERRIBS = REGISTRY.register("cloogerribs", CloogerribsItem::new);
   public static final DeferredItem<Item> BOSTROXWALL = block(UndeadRevamp2ModBlocks.BOSTROXWALL);
   public static final DeferredItem<Item> BOSTROXSTAIRS = block(UndeadRevamp2ModBlocks.BOSTROXSTAIRS);
   public static final DeferredItem<Item> COBBLEDBOSTROXWALL = block(UndeadRevamp2ModBlocks.COBBLEDBOSTROXWALL);
   public static final DeferredItem<Item> COBBLEDBOSTROXSTAIRS = block(UndeadRevamp2ModBlocks.COBBLEDBOSTROXSTAIRS);
   public static final DeferredItem<Item> BOSTROXSLABS = block(UndeadRevamp2ModBlocks.BOSTROXSLABS);
   public static final DeferredItem<Item> COBBLEDBOSTROXSLAB = block(UndeadRevamp2ModBlocks.COBBLEDBOSTROXSLAB);
   public static final DeferredItem<Item> CLOGGGERRIBSWITHBUN = REGISTRY.register("clogggerribswithbun", ClogggerribswithbunItem::new);
   public static final DeferredItem<Item> SPITTERNECC = REGISTRY.register("spitternecc", SpitterneccItem::new);
   public static final DeferredItem<Item> ARAPOHOLIASPRAY = REGISTRY.register("arapoholiaspray", ArapoholiasprayItem::new);
   public static final DeferredItem<Item> ACIDSACK = REGISTRY.register("acidsack", AcidsackItem::new);
   public static final DeferredItem<Item> HONNIETHROW = REGISTRY.register("honniethrow", HonniethrowItem::new);
   public static final DeferredItem<Item> QUEENBEEPERFUME = REGISTRY.register("queenbeeperfume", QueenbeeperfumeItem::new);
   public static final DeferredItem<Item> PREGNANTNECC = REGISTRY.register("pregnantnecc", PregnantneccItem::new);
   public static final DeferredItem<Item> F_IRESACK = REGISTRY.register("f_iresack", FIresackItem::new);
   public static final DeferredItem<Item> WINDTEXT = REGISTRY.register("windtext", WINDTEXTItem::new);
   public static final DeferredItem<Item> BOULDERTHROW = REGISTRY.register("boulderthrow", BoulderthrowItem::new);
   public static final DeferredItem<Item> WOODENNEST = block(UndeadRevamp2ModBlocks.WOODENNEST);
   public static final DeferredItem<Item> THESKEEPER_SPAWN_EGG = REGISTRY.register(
      "theskeeper_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.THESKEEPER, -10079488, -16711681, new Properties())
   );
   public static final DeferredItem<Item> THE_SOMNOLENCEEXTRACT = REGISTRY.register("the_somnolenceextract", TheSomnolenceextractItem::new);
   public static final DeferredItem<Item> CHAINSWORD = REGISTRY.register("chainsword", ChainswordItem::new);
   public static final DeferredItem<Item> THESOMNOLENCE_SPAWN_EGG = REGISTRY.register(
      "thesomnolence_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.THESOMNOLENCE, -16724788, -16777216, new Properties())
   );
   public static final DeferredItem<Item> WOODENNESTHARVEST_1 = block(UndeadRevamp2ModBlocks.WOODENNESTHARVEST_1);
   public static final DeferredItem<Item> WOODENNESTHARVEST_2 = block(UndeadRevamp2ModBlocks.WOODENNESTHARVEST_2);
   public static final DeferredItem<Item> WOODENNESTHARVEST_3 = block(UndeadRevamp2ModBlocks.WOODENNESTHARVEST_3);
   public static final DeferredItem<Item> SLEEPINGSMOKEBOMB = REGISTRY.register("sleepingsmokebomb", SleepingsmokebombItem::new);
   public static final DeferredItem<Item> SMOKESMITTER_SPAWN_EGG = REGISTRY.register(
      "smokesmitter_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.SMOKESMITTER, -1, -1, new Properties())
   );
   public static final DeferredItem<Item> THELURKER_SPAWN_EGG = REGISTRY.register(
      "thelurker_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.THELURKER, -16751104, -39169, new Properties())
   );
   public static final DeferredItem<Item> NEOCRORINES_SPAWN_EGG = REGISTRY.register(
      "neocrorines_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.NEOCRORINES, -16777216, -16777216, new Properties())
   );
   public static final DeferredItem<Item> CHISELED_DRIPSTONEPILLAR = block(UndeadRevamp2ModBlocks.CHISELED_DRIPSTONEPILLAR);
   public static final DeferredItem<Item> CHISELEDDRIPSTONEPILLARTOP = block(UndeadRevamp2ModBlocks.CHISELEDDRIPSTONEPILLARTOP);
   public static final DeferredItem<Item> CHISELEDDRIPSTONEPILLARBOTTOM = block(UndeadRevamp2ModBlocks.CHISELEDDRIPSTONEPILLARBOTTOM);
   public static final DeferredItem<Item> CHISELEDDRIPSTONEBLOCK = block(UndeadRevamp2ModBlocks.CHISELEDDRIPSTONEBLOCK);
   public static final DeferredItem<Item> COFFINBREADSTAGE_2 = block(UndeadRevamp2ModBlocks.COFFINBREADSTAGE_2);
   public static final DeferredItem<Item> COFFINBREADSTAGE_3 = block(UndeadRevamp2ModBlocks.COFFINBREADSTAGE_3);
   public static final DeferredItem<Item> COFFINBREADSTAGE_4 = block(UndeadRevamp2ModBlocks.COFFINBREADSTAGE_4);
   public static final DeferredItem<Item> COFFINBROAD = block(UndeadRevamp2ModBlocks.COFFINBROAD);
   public static final DeferredItem<Item> THEDUNGEON_SPAWN_EGG = REGISTRY.register(
      "thedungeon_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.THEDUNGEON, -16777216, -1, new Properties())
   );
   public static final DeferredItem<Item> ALTARNOM = block(UndeadRevamp2ModBlocks.ALTARNOM);
   public static final DeferredItem<Item> ALTARACTIVE = block(UndeadRevamp2ModBlocks.ALTARACTIVE);
   public static final DeferredItem<Item> BASALTECHESTE = REGISTRY.register(
      UndeadRevamp2ModBlocks.BASALTECHESTE.getId().getPath(),
      () -> new BasaltechesteDisplayItem((Block)UndeadRevamp2ModBlocks.BASALTECHESTE.get(), new Properties())
   );
   public static final DeferredItem<Item> IMBUEMASK_HELMET = REGISTRY.register("imbuemask_helmet", ImbuemaskItem.Helmet::new);
   public static final DeferredItem<Item> NULLIFYMASK_HELMET = REGISTRY.register("nullifymask_helmet", NullifymaskItem.Helmet::new);
   public static final DeferredItem<Item> WITHERCHARGEMASK_HELMET = REGISTRY.register("witherchargemask_helmet", WitherchargemaskItem.Helmet::new);
   public static final DeferredItem<Item> SHIELDMASK_HELMET = REGISTRY.register("shieldmask_helmet", ShieldmaskItem.Helmet::new);
   public static final DeferredItem<Item> BIGSUCKER_SPAWN_EGG = REGISTRY.register(
      "bigsucker_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.BIGSUCKER, -65536, -16738048, new Properties())
   );
   public static final DeferredItem<Item> SUCKERMEMEBRANE = REGISTRY.register("suckermemebrane", SuckermemebraneItem::new);
   public static final DeferredItem<Item> FRIEDSUCKER = REGISTRY.register("friedsucker", FriedsuckerItem::new);
   public static final DeferredItem<Item> SOMNOLENCESPAWNS = block(UndeadRevamp2ModBlocks.SOMNOLENCESPAWNS);
   public static final DeferredItem<Item> ANCIENTBREAD = REGISTRY.register("ancientbread", AncientbreadItem::new);
   public static final DeferredItem<Item> CLOGGERARMOR_HELMET = REGISTRY.register("cloggerarmor_helmet", CloggerarmorItem.Helmet::new);
   public static final DeferredItem<Item> CLOGGERARMOR_CHESTPLATE = REGISTRY.register("cloggerarmor_chestplate", CloggerarmorItem.Chestplate::new);
   public static final DeferredItem<Item> CLOGGERARMOR_LEGGINGS = REGISTRY.register("cloggerarmor_leggings", CloggerarmorItem.Leggings::new);
   public static final DeferredItem<Item> CLOGGERARMOR_BOOTS = REGISTRY.register("cloggerarmor_boots", CloggerarmorItem.Boots::new);
   public static final DeferredItem<Item> CLOGGERUPGRADE = REGISTRY.register("cloggerupgrade", CloggerupgradeItem::new);
   public static final DeferredItem<Item> HEAVYUPGRADE = REGISTRY.register("heavyupgrade", HeavyupgradeItem::new);
   public static final DeferredItem<Item> LECHERY_SPAWN_EGG = REGISTRY.register(
      "lechery_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.LECHERY, -26113, -6710887, new Properties())
   );
   public static final DeferredItem<Item> WEAKSPOT_SPAWN_EGG = REGISTRY.register(
      "weakspot_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.WEAKSPOT, -1, -1, new Properties())
   );
   public static final DeferredItem<Item> INDUCERSTONE = block(UndeadRevamp2ModBlocks.INDUCERSTONE);
   public static final DeferredItem<Item> DEEPSLATEINDUCER = block(UndeadRevamp2ModBlocks.DEEPSLATEINDUCER);
   public static final DeferredItem<Item> OAKINDUCER = block(UndeadRevamp2ModBlocks.OAKINDUCER);
   public static final DeferredItem<Item> DARKOAKINDUCER = block(UndeadRevamp2ModBlocks.DARKOAKINDUCER);
   public static final DeferredItem<Item> DEVILURCHIN = REGISTRY.register("devilurchin", DevilurchinItem::new);
   public static final DeferredItem<Item> INDUCERSTONEINACTIVE = block(UndeadRevamp2ModBlocks.INDUCERSTONEINACTIVE);
   public static final DeferredItem<Item> DEEPSLATEINDUCERINACTIVE = block(UndeadRevamp2ModBlocks.DEEPSLATEINDUCERINACTIVE);
   public static final DeferredItem<Item> OAKINDUCERINACTIVE = block(UndeadRevamp2ModBlocks.OAKINDUCERINACTIVE);
   public static final DeferredItem<Item> DARKOAKINDUCERINACTIVE = block(UndeadRevamp2ModBlocks.DARKOAKINDUCERINACTIVE);
   public static final DeferredItem<Item> INVISILEHCERY_SPAWN_EGG = REGISTRY.register(
      "invisilehcery_spawn_egg", () -> new DeferredSpawnEggItem(UndeadRevamp2ModEntities.INVISILEHCERY, -6750055, -6750055, new Properties())
   );
   public static final DeferredItem<Item> SPURCEINDUCER = block(UndeadRevamp2ModBlocks.SPURCEINDUCER);
   public static final DeferredItem<Item> CHERRYBLOSSOMINDUCER = block(UndeadRevamp2ModBlocks.CHERRYBLOSSOMINDUCER);
   public static final DeferredItem<Item> JUNGLEINDUCER = block(UndeadRevamp2ModBlocks.JUNGLEINDUCER);
   public static final DeferredItem<Item> BIRCHINDUCER = block(UndeadRevamp2ModBlocks.BIRCHINDUCER);
   public static final DeferredItem<Item> SAVANNAINDUCER = block(UndeadRevamp2ModBlocks.SAVANNAINDUCER);
   public static final DeferredItem<Item> MANGROVEINDUCER = block(UndeadRevamp2ModBlocks.MANGROVEINDUCER);
   public static final DeferredItem<Item> HUNTERSACKPINK = REGISTRY.register("huntersackpink", HuntersackpinkItem::new);

   private static DeferredItem<Item> block(DeferredHolder<Block, Block> block) {
      return REGISTRY.register(block.getId().getPath(), () -> new BlockItem((Block)block.get(), new Properties()));
   }
}

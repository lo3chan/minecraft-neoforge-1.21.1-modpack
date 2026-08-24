package vazkii.psi.common.item.base;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import vazkii.psi.common.block.base.ModBlocks;
import vazkii.psi.common.item.ItemCAD;
import vazkii.psi.common.item.ItemChargeSpellBullet;
import vazkii.psi.common.item.ItemCircleSpellBullet;
import vazkii.psi.common.item.ItemDetonator;
import vazkii.psi.common.item.ItemExosuitController;
import vazkii.psi.common.item.ItemFlashRing;
import vazkii.psi.common.item.ItemGrenadeSpellBullet;
import vazkii.psi.common.item.ItemHeatExosuitSensor;
import vazkii.psi.common.item.ItemLightExosuitSensor;
import vazkii.psi.common.item.ItemLoopcastSpellBullet;
import vazkii.psi.common.item.ItemMineSpellBullet;
import vazkii.psi.common.item.ItemProjectileSpellBullet;
import vazkii.psi.common.item.ItemSpellBullet;
import vazkii.psi.common.item.ItemSpellDrive;
import vazkii.psi.common.item.ItemStressExosuitSensor;
import vazkii.psi.common.item.ItemTriggerExosuitSensor;
import vazkii.psi.common.item.ItemVectorRuler;
import vazkii.psi.common.item.ItemWaterExosuitSensor;
import vazkii.psi.common.item.armor.ItemPsimetalExosuitBoots;
import vazkii.psi.common.item.armor.ItemPsimetalExosuitChestplate;
import vazkii.psi.common.item.armor.ItemPsimetalExosuitHelmet;
import vazkii.psi.common.item.armor.ItemPsimetalExosuitLeggings;
import vazkii.psi.common.item.component.ItemCADAssembly;
import vazkii.psi.common.item.component.ItemCADBattery;
import vazkii.psi.common.item.component.ItemCADColorizer;
import vazkii.psi.common.item.component.ItemCADColorizerEmpty;
import vazkii.psi.common.item.component.ItemCADColorizerPsi;
import vazkii.psi.common.item.component.ItemCADColorizerRainbow;
import vazkii.psi.common.item.component.ItemCADCore;
import vazkii.psi.common.item.component.ItemCADSocket;
import vazkii.psi.common.item.tool.ItemPsimetalAxe;
import vazkii.psi.common.item.tool.ItemPsimetalPickaxe;
import vazkii.psi.common.item.tool.ItemPsimetalShovel;
import vazkii.psi.common.item.tool.ItemPsimetalSword;

public final class ModItems {
   public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, "psi");
   public static final DeferredHolder<Item, Item> cadAssemblerItem = ITEMS.register(
      "cad_assembler", () -> new BlockItem((Block)ModBlocks.cadAssembler.get(), defaultBuilder().rarity(Rarity.UNCOMMON))
   );
   public static final DeferredHolder<Item, Item> programmerItem = ITEMS.register(
      "programmer", () -> new BlockItem((Block)ModBlocks.programmer.get(), defaultBuilder().rarity(Rarity.UNCOMMON))
   );
   public static final DeferredHolder<Item, Item> psidustBlockItem = ITEMS.register(
      "psidust_block", () -> new BlockItem((Block)ModBlocks.psidustBlock.get(), defaultBuilder())
   );
   public static final DeferredHolder<Item, Item> psimetalBlockItem = ITEMS.register(
      "psimetal_block", () -> new BlockItem((Block)ModBlocks.psimetalBlock.get(), defaultBuilder())
   );
   public static final DeferredHolder<Item, Item> psigemBlockItem = ITEMS.register(
      "psigem_block", () -> new BlockItem((Block)ModBlocks.psigemBlock.get(), defaultBuilder())
   );
   public static final DeferredHolder<Item, Item> psimetalPlateBlackItem = ITEMS.register(
      "black_psimetal_plate", () -> new BlockItem((Block)ModBlocks.psimetalPlateBlack.get(), defaultBuilder())
   );
   public static final DeferredHolder<Item, Item> psimetalPlateBlackLightItem = ITEMS.register(
      "lit_black_psimetal_plate", () -> new BlockItem((Block)ModBlocks.psimetalPlateBlackLight.get(), defaultBuilder())
   );
   public static final DeferredHolder<Item, Item> psimetalPlateWhiteItem = ITEMS.register(
      "white_psimetal_plate", () -> new BlockItem((Block)ModBlocks.psimetalPlateWhite.get(), defaultBuilder())
   );
   public static final DeferredHolder<Item, Item> psimetalPlateWhiteLightItem = ITEMS.register(
      "lit_white_psimetal_plate", () -> new BlockItem((Block)ModBlocks.psimetalPlateWhiteLight.get(), defaultBuilder())
   );
   public static final DeferredHolder<Item, Item> psimetalEbonyItem = ITEMS.register(
      "ebony_psimetal_block", () -> new BlockItem((Block)ModBlocks.psimetalEbony.get(), defaultBuilder())
   );
   public static final DeferredHolder<Item, Item> psimetalIvoryItem = ITEMS.register(
      "ivory_psimetal_block", () -> new BlockItem((Block)ModBlocks.psimetalIvory.get(), defaultBuilder())
   );
   public static final DeferredHolder<Item, Item> psidust = ITEMS.register("psidust", () -> new Item(defaultBuilder()));
   public static final DeferredHolder<Item, Item> psimetal = ITEMS.register("psimetal", () -> new Item(defaultBuilder()));
   public static final DeferredHolder<Item, Item> psigem = ITEMS.register("psigem", () -> new Item(defaultBuilder()));
   public static final DeferredHolder<Item, Item> ebonyPsimetal = ITEMS.register("ebony_psimetal", () -> new Item(defaultBuilder()));
   public static final DeferredHolder<Item, Item> ivoryPsimetal = ITEMS.register("ivory_psimetal", () -> new Item(defaultBuilder()));
   public static final DeferredHolder<Item, Item> ebonySubstance = ITEMS.register("ebony_substance", () -> new Item(defaultBuilder()));
   public static final DeferredHolder<Item, Item> ivorySubstance = ITEMS.register("ivory_substance", () -> new Item(defaultBuilder()));
   public static final DeferredHolder<Item, ItemCADAssembly> cadAssemblyIron = ITEMS.register(
      "cad_assembly_iron", () -> new ItemCADAssembly(defaultBuilder(), "cad_iron")
   );
   public static final DeferredHolder<Item, ItemCADAssembly> cadAssemblyGold = ITEMS.register(
      "cad_assembly_gold", () -> new ItemCADAssembly(defaultBuilder(), "cad_gold")
   );
   public static final DeferredHolder<Item, ItemCADAssembly> cadAssemblyPsimetal = ITEMS.register(
      "cad_assembly_psimetal", () -> new ItemCADAssembly(defaultBuilder(), "cad_psimetal")
   );
   public static final DeferredHolder<Item, ItemCADAssembly> cadAssemblyIvory = ITEMS.register(
      "cad_assembly_ivory_psimetal", () -> new ItemCADAssembly(defaultBuilder(), "cad_ivory_psimetal")
   );
   public static final DeferredHolder<Item, ItemCADAssembly> cadAssemblyEbony = ITEMS.register(
      "cad_assembly_ebony_psimetal", () -> new ItemCADAssembly(defaultBuilder(), "cad_ebony_psimetal")
   );
   public static final DeferredHolder<Item, ItemCADAssembly> cadAssemblyCreative = ITEMS.register(
      "cad_assembly_creative", () -> new ItemCADAssembly(defaultBuilder(), "cad_creative")
   );
   public static final DeferredHolder<Item, ItemCADCore> cadCoreBasic = ITEMS.register("cad_core_basic", () -> new ItemCADCore(defaultBuilder()));
   public static final DeferredHolder<Item, ItemCADCore> cadCoreOverclocked = ITEMS.register("cad_core_overclocked", () -> new ItemCADCore(defaultBuilder()));
   public static final DeferredHolder<Item, ItemCADCore> cadCoreConductive = ITEMS.register("cad_core_conductive", () -> new ItemCADCore(defaultBuilder()));
   public static final DeferredHolder<Item, ItemCADCore> cadCoreHyperClocked = ITEMS.register("cad_core_hyperclocked", () -> new ItemCADCore(defaultBuilder()));
   public static final DeferredHolder<Item, ItemCADCore> cadCoreRadiative = ITEMS.register("cad_core_radiative", () -> new ItemCADCore(defaultBuilder()));
   public static final DeferredHolder<Item, ItemCADSocket> cadSocketBasic = ITEMS.register("cad_socket_basic", () -> new ItemCADSocket(defaultBuilder()));
   public static final DeferredHolder<Item, ItemCADSocket> cadSocketSignaling = ITEMS.register(
      "cad_socket_signaling", () -> new ItemCADSocket(defaultBuilder())
   );
   public static final DeferredHolder<Item, ItemCADSocket> cadSocketLarge = ITEMS.register("cad_socket_large", () -> new ItemCADSocket(defaultBuilder()));
   public static final DeferredHolder<Item, ItemCADSocket> cadSocketTransmissive = ITEMS.register(
      "cad_socket_transmissive", () -> new ItemCADSocket(defaultBuilder())
   );
   public static final DeferredHolder<Item, ItemCADSocket> cadSocketHuge = ITEMS.register("cad_socket_huge", () -> new ItemCADSocket(defaultBuilder()));
   public static final DeferredHolder<Item, ItemCADBattery> cadBatteryBasic = ITEMS.register("cad_battery_basic", () -> new ItemCADBattery(defaultBuilder()));
   public static final DeferredHolder<Item, ItemCADBattery> cadBatteryExtended = ITEMS.register(
      "cad_battery_extended", () -> new ItemCADBattery(defaultBuilder())
   );
   public static final DeferredHolder<Item, ItemCADBattery> cadBatteryUltradense = ITEMS.register(
      "cad_battery_ultradense", () -> new ItemCADBattery(defaultBuilder())
   );
   public static final DeferredHolder<Item, ItemCADColorizer> cadColorizerWhite = ITEMS.register(
      "cad_colorizer_white", () -> new ItemCADColorizer(defaultBuilder(), DyeColor.WHITE)
   );
   public static final DeferredHolder<Item, ItemCADColorizer> cadColorizerOrange = ITEMS.register(
      "cad_colorizer_orange", () -> new ItemCADColorizer(defaultBuilder(), DyeColor.ORANGE)
   );
   public static final DeferredHolder<Item, ItemCADColorizer> cadColorizerMagenta = ITEMS.register(
      "cad_colorizer_magenta", () -> new ItemCADColorizer(defaultBuilder(), DyeColor.MAGENTA)
   );
   public static final DeferredHolder<Item, ItemCADColorizer> cadColorizerLightBlue = ITEMS.register(
      "cad_colorizer_light_blue", () -> new ItemCADColorizer(defaultBuilder(), DyeColor.LIGHT_BLUE)
   );
   public static final DeferredHolder<Item, ItemCADColorizer> cadColorizerYellow = ITEMS.register(
      "cad_colorizer_yellow", () -> new ItemCADColorizer(defaultBuilder(), DyeColor.YELLOW)
   );
   public static final DeferredHolder<Item, ItemCADColorizer> cadColorizerLime = ITEMS.register(
      "cad_colorizer_lime", () -> new ItemCADColorizer(defaultBuilder(), DyeColor.LIME)
   );
   public static final DeferredHolder<Item, ItemCADColorizer> cadColorizerPink = ITEMS.register(
      "cad_colorizer_pink", () -> new ItemCADColorizer(defaultBuilder(), DyeColor.PINK)
   );
   public static final DeferredHolder<Item, ItemCADColorizer> cadColorizerGray = ITEMS.register(
      "cad_colorizer_gray", () -> new ItemCADColorizer(defaultBuilder(), DyeColor.GRAY)
   );
   public static final DeferredHolder<Item, ItemCADColorizer> cadColorizerLightGray = ITEMS.register(
      "cad_colorizer_light_gray", () -> new ItemCADColorizer(defaultBuilder(), DyeColor.LIGHT_GRAY)
   );
   public static final DeferredHolder<Item, ItemCADColorizer> cadColorizerCyan = ITEMS.register(
      "cad_colorizer_cyan", () -> new ItemCADColorizer(defaultBuilder(), DyeColor.CYAN)
   );
   public static final DeferredHolder<Item, ItemCADColorizer> cadColorizerPurple = ITEMS.register(
      "cad_colorizer_purple", () -> new ItemCADColorizer(defaultBuilder(), DyeColor.PURPLE)
   );
   public static final DeferredHolder<Item, ItemCADColorizer> cadColorizerBlue = ITEMS.register(
      "cad_colorizer_blue", () -> new ItemCADColorizer(defaultBuilder(), DyeColor.BLUE)
   );
   public static final DeferredHolder<Item, ItemCADColorizer> cadColorizerBrown = ITEMS.register(
      "cad_colorizer_brown", () -> new ItemCADColorizer(defaultBuilder(), DyeColor.BROWN)
   );
   public static final DeferredHolder<Item, ItemCADColorizer> cadColorizerGreen = ITEMS.register(
      "cad_colorizer_green", () -> new ItemCADColorizer(defaultBuilder(), DyeColor.GREEN)
   );
   public static final DeferredHolder<Item, ItemCADColorizer> cadColorizerRed = ITEMS.register(
      "cad_colorizer_red", () -> new ItemCADColorizer(defaultBuilder(), DyeColor.RED)
   );
   public static final DeferredHolder<Item, ItemCADColorizer> cadColorizerBlack = ITEMS.register(
      "cad_colorizer_black", () -> new ItemCADColorizer(defaultBuilder(), DyeColor.BLACK)
   );
   public static final DeferredHolder<Item, ItemCADColorizerRainbow> cadColorizerRainbow = ITEMS.register(
      "cad_colorizer_rainbow", () -> new ItemCADColorizerRainbow(defaultBuilder())
   );
   public static final DeferredHolder<Item, ItemCADColorizerPsi> cadColorizerPsi = ITEMS.register(
      "cad_colorizer_psi", () -> new ItemCADColorizerPsi(defaultBuilder())
   );
   public static final DeferredHolder<Item, ItemCADColorizerEmpty> cadColorizerEmpty = ITEMS.register(
      "cad_colorizer_empty", () -> new ItemCADColorizerEmpty(defaultBuilder())
   );
   public static final DeferredHolder<Item, ItemFlashRing> flashRing = ITEMS.register("flash_ring", () -> new ItemFlashRing(defaultBuilder()));
   public static final DeferredHolder<Item, ItemSpellBullet> spellBullet = ITEMS.register("spell_bullet", () -> new ItemSpellBullet(defaultBuilder()));
   public static final DeferredHolder<Item, ItemProjectileSpellBullet> projectileSpellBullet = ITEMS.register(
      "spell_bullet_projectile", () -> new ItemProjectileSpellBullet(defaultBuilder())
   );
   public static final DeferredHolder<Item, ItemLoopcastSpellBullet> loopSpellBullet = ITEMS.register(
      "spell_bullet_loop", () -> new ItemLoopcastSpellBullet(defaultBuilder())
   );
   public static final DeferredHolder<Item, ItemCircleSpellBullet> circleSpellBullet = ITEMS.register(
      "spell_bullet_circle", () -> new ItemCircleSpellBullet(defaultBuilder())
   );
   public static final DeferredHolder<Item, ItemGrenadeSpellBullet> grenadeSpellBullet = ITEMS.register(
      "spell_bullet_grenade", () -> new ItemGrenadeSpellBullet(defaultBuilder())
   );
   public static final DeferredHolder<Item, ItemChargeSpellBullet> chargeSpellBullet = ITEMS.register(
      "spell_bullet_charge", () -> new ItemChargeSpellBullet(defaultBuilder())
   );
   public static final DeferredHolder<Item, ItemMineSpellBullet> mineSpellBullet = ITEMS.register(
      "spell_bullet_mine", () -> new ItemMineSpellBullet(defaultBuilder())
   );
   public static final DeferredHolder<Item, ItemSpellDrive> spellDrive = ITEMS.register("spell_drive", () -> new ItemSpellDrive(defaultBuilder()));
   public static final DeferredHolder<Item, ItemDetonator> detonator = ITEMS.register("detonator", () -> new ItemDetonator(defaultBuilder()));
   public static final DeferredHolder<Item, ItemExosuitController> exosuitController = ITEMS.register(
      "exosuit_controller", () -> new ItemExosuitController(defaultBuilder())
   );
   public static final DeferredHolder<Item, ItemLightExosuitSensor> exosuitSensorLight = ITEMS.register(
      "exosuit_sensor_light", () -> new ItemLightExosuitSensor(defaultBuilder())
   );
   public static final DeferredHolder<Item, ItemHeatExosuitSensor> exosuitSensorHeat = ITEMS.register(
      "exosuit_sensor_heat", () -> new ItemHeatExosuitSensor(defaultBuilder())
   );
   public static final DeferredHolder<Item, ItemStressExosuitSensor> exosuitSensorStress = ITEMS.register(
      "exosuit_sensor_stress", () -> new ItemStressExosuitSensor(defaultBuilder())
   );
   public static final DeferredHolder<Item, ItemWaterExosuitSensor> exosuitSensorWater = ITEMS.register(
      "exosuit_sensor_water", () -> new ItemWaterExosuitSensor(defaultBuilder())
   );
   public static final DeferredHolder<Item, ItemTriggerExosuitSensor> exosuitSensorTrigger = ITEMS.register(
      "exosuit_sensor_trigger", () -> new ItemTriggerExosuitSensor(defaultBuilder())
   );
   public static final DeferredHolder<Item, ItemCAD> cad = ITEMS.register("cad", () -> new ItemCAD(defaultBuilder()));
   public static final DeferredHolder<Item, ItemVectorRuler> vectorRuler = ITEMS.register("vector_ruler", () -> new ItemVectorRuler(defaultBuilder()));
   public static final DeferredHolder<Item, ItemPsimetalShovel> psimetalShovel = ITEMS.register(
      "psimetal_shovel", () -> new ItemPsimetalShovel(defaultBuilder())
   );
   public static final DeferredHolder<Item, ItemPsimetalPickaxe> psimetalPickaxe = ITEMS.register(
      "psimetal_pickaxe", () -> new ItemPsimetalPickaxe(defaultBuilder())
   );
   public static final DeferredHolder<Item, ItemPsimetalAxe> psimetalAxe = ITEMS.register("psimetal_axe", () -> new ItemPsimetalAxe(defaultBuilder()));
   public static final DeferredHolder<Item, ItemPsimetalSword> psimetalSword = ITEMS.register("psimetal_sword", () -> new ItemPsimetalSword(defaultBuilder()));
   public static final DeferredHolder<Item, ItemPsimetalExosuitHelmet> psimetalExosuitHelmet = ITEMS.register(
      "psimetal_exosuit_helmet", () -> new ItemPsimetalExosuitHelmet(Type.HELMET, defaultBuilder().durability(Type.HELMET.getDurability(18)))
   );
   public static final DeferredHolder<Item, ItemPsimetalExosuitChestplate> psimetalExosuitChestplate = ITEMS.register(
      "psimetal_exosuit_chestplate", () -> new ItemPsimetalExosuitChestplate(Type.CHESTPLATE, defaultBuilder().durability(Type.CHESTPLATE.getDurability(18)))
   );
   public static final DeferredHolder<Item, ItemPsimetalExosuitLeggings> psimetalExosuitLeggings = ITEMS.register(
      "psimetal_exosuit_leggings", () -> new ItemPsimetalExosuitLeggings(Type.LEGGINGS, defaultBuilder().durability(Type.LEGGINGS.getDurability(18)))
   );
   public static final DeferredHolder<Item, ItemPsimetalExosuitBoots> psimetalExosuitBoots = ITEMS.register(
      "psimetal_exosuit_boots", () -> new ItemPsimetalExosuitBoots(Type.BOOTS, defaultBuilder().durability(Type.BOOTS.getDurability(18)))
   );

   public static Properties defaultBuilder() {
      return new Properties();
   }
}

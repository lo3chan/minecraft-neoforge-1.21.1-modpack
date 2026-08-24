package com.aetherteam.aether.item;

import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.data.resources.registries.AetherJukeboxSongs;
import com.aetherteam.aether.data.resources.registries.AetherMoaTypes;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.item.accessories.cape.AgilityCapeItem;
import com.aetherteam.aether.item.accessories.cape.CapeItem;
import com.aetherteam.aether.item.accessories.cape.InvisibilityCloakItem;
import com.aetherteam.aether.item.accessories.cape.ValkyrieCapeItem;
import com.aetherteam.aether.item.accessories.gloves.GlovesItem;
import com.aetherteam.aether.item.accessories.gloves.GoldGlovesItem;
import com.aetherteam.aether.item.accessories.gloves.LeatherGlovesItem;
import com.aetherteam.aether.item.accessories.gloves.ZaniteGlovesItem;
import com.aetherteam.aether.item.accessories.miscellaneous.GoldenFeatherItem;
import com.aetherteam.aether.item.accessories.miscellaneous.IronBubbleItem;
import com.aetherteam.aether.item.accessories.miscellaneous.RegenerationStoneItem;
import com.aetherteam.aether.item.accessories.miscellaneous.ShieldOfRepulsionItem;
import com.aetherteam.aether.item.accessories.pendant.IcePendantItem;
import com.aetherteam.aether.item.accessories.pendant.PendantItem;
import com.aetherteam.aether.item.accessories.pendant.ZanitePendantItem;
import com.aetherteam.aether.item.accessories.ring.IceRingItem;
import com.aetherteam.aether.item.accessories.ring.RingItem;
import com.aetherteam.aether.item.accessories.ring.ZaniteRingItem;
import com.aetherteam.aether.item.combat.AetherArmorMaterials;
import com.aetherteam.aether.item.combat.DartShooterItem;
import com.aetherteam.aether.item.combat.EnchantedDartItem;
import com.aetherteam.aether.item.combat.GoldenDartItem;
import com.aetherteam.aether.item.combat.GravititeSwordItem;
import com.aetherteam.aether.item.combat.HolystoneSwordItem;
import com.aetherteam.aether.item.combat.PoisonDartItem;
import com.aetherteam.aether.item.combat.SkyrootSwordItem;
import com.aetherteam.aether.item.combat.ZaniteSwordItem;
import com.aetherteam.aether.item.combat.loot.CandyCaneSwordItem;
import com.aetherteam.aether.item.combat.loot.CloudStaffItem;
import com.aetherteam.aether.item.combat.loot.FlamingSwordItem;
import com.aetherteam.aether.item.combat.loot.HammerOfKingbdogzItem;
import com.aetherteam.aether.item.combat.loot.HolySwordItem;
import com.aetherteam.aether.item.combat.loot.LightningKnifeItem;
import com.aetherteam.aether.item.combat.loot.LightningSwordItem;
import com.aetherteam.aether.item.combat.loot.PhoenixBowItem;
import com.aetherteam.aether.item.combat.loot.PigSlayerItem;
import com.aetherteam.aether.item.combat.loot.ValkyrieLanceItem;
import com.aetherteam.aether.item.combat.loot.VampireBladeItem;
import com.aetherteam.aether.item.components.AetherDataComponents;
import com.aetherteam.aether.item.components.DungeonKind;
import com.aetherteam.aether.item.food.AetherFoods;
import com.aetherteam.aether.item.food.GummySwetItem;
import com.aetherteam.aether.item.food.HealingStoneItem;
import com.aetherteam.aether.item.food.WhiteAppleItem;
import com.aetherteam.aether.item.materials.AmbrosiumShardItem;
import com.aetherteam.aether.item.materials.SwetBallItem;
import com.aetherteam.aether.item.miscellaneous.AetherPortalItem;
import com.aetherteam.aether.item.miscellaneous.LifeShardItem;
import com.aetherteam.aether.item.miscellaneous.LoreBookItem;
import com.aetherteam.aether.item.miscellaneous.MoaEggItem;
import com.aetherteam.aether.item.miscellaneous.ParachuteItem;
import com.aetherteam.aether.item.miscellaneous.SkyrootBoatItem;
import com.aetherteam.aether.item.miscellaneous.SliderSpawnEggItem;
import com.aetherteam.aether.item.miscellaneous.bucket.SkyrootBucketItem;
import com.aetherteam.aether.item.miscellaneous.bucket.SkyrootMilkBucketItem;
import com.aetherteam.aether.item.miscellaneous.bucket.SkyrootMobBucketItem;
import com.aetherteam.aether.item.miscellaneous.bucket.SkyrootPoisonBucketItem;
import com.aetherteam.aether.item.miscellaneous.bucket.SkyrootRemedyBucketItem;
import com.aetherteam.aether.item.miscellaneous.bucket.SkyrootSolidBucketItem;
import com.aetherteam.aether.item.tools.gravitite.GravititeAxeItem;
import com.aetherteam.aether.item.tools.gravitite.GravititeHoeItem;
import com.aetherteam.aether.item.tools.gravitite.GravititePickaxeItem;
import com.aetherteam.aether.item.tools.gravitite.GravititeShovelItem;
import com.aetherteam.aether.item.tools.holystone.HolystoneAxeItem;
import com.aetherteam.aether.item.tools.holystone.HolystoneHoeItem;
import com.aetherteam.aether.item.tools.holystone.HolystonePickaxeItem;
import com.aetherteam.aether.item.tools.holystone.HolystoneShovelItem;
import com.aetherteam.aether.item.tools.skyroot.SkyrootAxeItem;
import com.aetherteam.aether.item.tools.skyroot.SkyrootHoeItem;
import com.aetherteam.aether.item.tools.skyroot.SkyrootPickaxeItem;
import com.aetherteam.aether.item.tools.skyroot.SkyrootShovelItem;
import com.aetherteam.aether.item.tools.valkyrie.ValkyrieAxeItem;
import com.aetherteam.aether.item.tools.valkyrie.ValkyrieHoeItem;
import com.aetherteam.aether.item.tools.valkyrie.ValkyriePickaxeItem;
import com.aetherteam.aether.item.tools.valkyrie.ValkyrieShovelItem;
import com.aetherteam.aether.item.tools.zanite.ZaniteAxeItem;
import com.aetherteam.aether.item.tools.zanite.ZaniteHoeItem;
import com.aetherteam.aether.item.tools.zanite.ZanitePickaxeItem;
import com.aetherteam.aether.item.tools.zanite.ZaniteShovelItem;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.Accessory;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.minecraft.world.level.block.entity.BannerPatternLayers.Builder;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

public class AetherItems {
   public static final Items ITEMS = DeferredRegister.createItems("aether");
   public static final Rarity AETHER_LOOT = Rarity.valueOf("AETHER_LOOT");
   public static final Component BRONZE_DUNGEON_TOOLTIP = Component.translatable("aether.dungeon.bronze_dungeon")
      .withStyle(Style.EMPTY.withItalic(true).withColor((TextColor)TextColor.parseColor("#D9AB7E").result().get()));
   public static final Component SILVER_DUNGEON_TOOLTIP = Component.translatable("aether.dungeon.silver_dungeon")
      .withStyle(Style.EMPTY.withItalic(true).withColor((TextColor)TextColor.parseColor("#E0E0E0").result().get()));
   public static final Component GOLD_DUNGEON_TOOLTIP = Component.translatable("aether.dungeon.gold_dungeon")
      .withStyle(Style.EMPTY.withItalic(true).withColor((TextColor)TextColor.parseColor("#FDF55F").result().get()));
   public static final DeferredItem<PickaxeItem> SKYROOT_PICKAXE = ITEMS.register("skyroot_pickaxe", SkyrootPickaxeItem::new);
   public static final DeferredItem<AxeItem> SKYROOT_AXE = ITEMS.register("skyroot_axe", SkyrootAxeItem::new);
   public static final DeferredItem<ShovelItem> SKYROOT_SHOVEL = ITEMS.register("skyroot_shovel", SkyrootShovelItem::new);
   public static final DeferredItem<HoeItem> SKYROOT_HOE = ITEMS.register("skyroot_hoe", SkyrootHoeItem::new);
   public static final DeferredItem<PickaxeItem> HOLYSTONE_PICKAXE = ITEMS.register("holystone_pickaxe", HolystonePickaxeItem::new);
   public static final DeferredItem<AxeItem> HOLYSTONE_AXE = ITEMS.register("holystone_axe", HolystoneAxeItem::new);
   public static final DeferredItem<ShovelItem> HOLYSTONE_SHOVEL = ITEMS.register("holystone_shovel", HolystoneShovelItem::new);
   public static final DeferredItem<HoeItem> HOLYSTONE_HOE = ITEMS.register("holystone_hoe", HolystoneHoeItem::new);
   public static final DeferredItem<PickaxeItem> ZANITE_PICKAXE = ITEMS.register("zanite_pickaxe", ZanitePickaxeItem::new);
   public static final DeferredItem<AxeItem> ZANITE_AXE = ITEMS.register("zanite_axe", ZaniteAxeItem::new);
   public static final DeferredItem<ShovelItem> ZANITE_SHOVEL = ITEMS.register("zanite_shovel", ZaniteShovelItem::new);
   public static final DeferredItem<HoeItem> ZANITE_HOE = ITEMS.register("zanite_hoe", ZaniteHoeItem::new);
   public static final DeferredItem<PickaxeItem> GRAVITITE_PICKAXE = ITEMS.register("gravitite_pickaxe", GravititePickaxeItem::new);
   public static final DeferredItem<AxeItem> GRAVITITE_AXE = ITEMS.register("gravitite_axe", GravititeAxeItem::new);
   public static final DeferredItem<ShovelItem> GRAVITITE_SHOVEL = ITEMS.register("gravitite_shovel", GravititeShovelItem::new);
   public static final DeferredItem<HoeItem> GRAVITITE_HOE = ITEMS.register("gravitite_hoe", GravititeHoeItem::new);
   public static final DeferredItem<PickaxeItem> VALKYRIE_PICKAXE = ITEMS.register("valkyrie_pickaxe", ValkyriePickaxeItem::new);
   public static final DeferredItem<AxeItem> VALKYRIE_AXE = ITEMS.register("valkyrie_axe", ValkyrieAxeItem::new);
   public static final DeferredItem<ShovelItem> VALKYRIE_SHOVEL = ITEMS.register("valkyrie_shovel", ValkyrieShovelItem::new);
   public static final DeferredItem<HoeItem> VALKYRIE_HOE = ITEMS.register("valkyrie_hoe", ValkyrieHoeItem::new);
   public static final DeferredItem<SwordItem> SKYROOT_SWORD = ITEMS.register("skyroot_sword", SkyrootSwordItem::new);
   public static final DeferredItem<SwordItem> HOLYSTONE_SWORD = ITEMS.register("holystone_sword", HolystoneSwordItem::new);
   public static final DeferredItem<SwordItem> ZANITE_SWORD = ITEMS.register("zanite_sword", ZaniteSwordItem::new);
   public static final DeferredItem<SwordItem> GRAVITITE_SWORD = ITEMS.register("gravitite_sword", GravititeSwordItem::new);
   public static final DeferredItem<SwordItem> VALKYRIE_LANCE = ITEMS.register("valkyrie_lance", ValkyrieLanceItem::new);
   public static final DeferredItem<SwordItem> FLAMING_SWORD = ITEMS.register("flaming_sword", FlamingSwordItem::new);
   public static final DeferredItem<SwordItem> LIGHTNING_SWORD = ITEMS.register("lightning_sword", LightningSwordItem::new);
   public static final DeferredItem<SwordItem> HOLY_SWORD = ITEMS.register("holy_sword", HolySwordItem::new);
   public static final DeferredItem<SwordItem> VAMPIRE_BLADE = ITEMS.register("vampire_blade", VampireBladeItem::new);
   public static final DeferredItem<SwordItem> PIG_SLAYER = ITEMS.register("pig_slayer", PigSlayerItem::new);
   public static final DeferredItem<SwordItem> CANDY_CANE_SWORD = ITEMS.register("candy_cane_sword", CandyCaneSwordItem::new);
   public static final DeferredItem<SwordItem> HAMMER_OF_KINGBDOGZ = ITEMS.register("hammer_of_kingbdogz", HammerOfKingbdogzItem::new);
   public static final DeferredItem<Item> LIGHTNING_KNIFE = ITEMS.register("lightning_knife", LightningKnifeItem::new);
   public static final DeferredItem<Item> GOLDEN_DART = ITEMS.register("golden_dart", () -> new GoldenDartItem(new Properties()));
   public static final DeferredItem<Item> POISON_DART = ITEMS.register("poison_dart", () -> new PoisonDartItem(new Properties()));
   public static final DeferredItem<Item> ENCHANTED_DART = ITEMS.register("enchanted_dart", () -> new EnchantedDartItem(new Properties().rarity(Rarity.RARE)));
   public static final DeferredItem<Item> GOLDEN_DART_SHOOTER = ITEMS.register(
      "golden_dart_shooter", () -> new DartShooterItem(GOLDEN_DART, new Properties().stacksTo(1))
   );
   public static final DeferredItem<Item> POISON_DART_SHOOTER = ITEMS.register(
      "poison_dart_shooter", () -> new DartShooterItem(POISON_DART, new Properties().stacksTo(1))
   );
   public static final DeferredItem<Item> ENCHANTED_DART_SHOOTER = ITEMS.register(
      "enchanted_dart_shooter", () -> new DartShooterItem(ENCHANTED_DART, new Properties().stacksTo(1).rarity(Rarity.RARE))
   );
   public static final DeferredItem<BowItem> PHOENIX_BOW = ITEMS.register("phoenix_bow", PhoenixBowItem::new);
   public static final DeferredItem<Item> ZANITE_HELMET = ITEMS.register(
      "zanite_helmet", () -> new ArmorItem(AetherArmorMaterials.ZANITE, Type.HELMET, new Properties().durability(Type.HELMET.getDurability(15)))
   );
   public static final DeferredItem<Item> ZANITE_CHESTPLATE = ITEMS.register(
      "zanite_chestplate", () -> new ArmorItem(AetherArmorMaterials.ZANITE, Type.CHESTPLATE, new Properties().durability(Type.CHESTPLATE.getDurability(15)))
   );
   public static final DeferredItem<Item> ZANITE_LEGGINGS = ITEMS.register(
      "zanite_leggings", () -> new ArmorItem(AetherArmorMaterials.ZANITE, Type.LEGGINGS, new Properties().durability(Type.LEGGINGS.getDurability(15)))
   );
   public static final DeferredItem<Item> ZANITE_BOOTS = ITEMS.register(
      "zanite_boots", () -> new ArmorItem(AetherArmorMaterials.ZANITE, Type.BOOTS, new Properties().durability(Type.BOOTS.getDurability(15)))
   );
   public static final DeferredItem<Item> GRAVITITE_HELMET = ITEMS.register(
      "gravitite_helmet", () -> new ArmorItem(AetherArmorMaterials.GRAVITITE, Type.HELMET, new Properties().durability(Type.HELMET.getDurability(33)))
   );
   public static final DeferredItem<Item> GRAVITITE_CHESTPLATE = ITEMS.register(
      "gravitite_chestplate",
      () -> new ArmorItem(AetherArmorMaterials.GRAVITITE, Type.CHESTPLATE, new Properties().durability(Type.CHESTPLATE.getDurability(33)))
   );
   public static final DeferredItem<Item> GRAVITITE_LEGGINGS = ITEMS.register(
      "gravitite_leggings", () -> new ArmorItem(AetherArmorMaterials.GRAVITITE, Type.LEGGINGS, new Properties().durability(Type.LEGGINGS.getDurability(33)))
   );
   public static final DeferredItem<Item> GRAVITITE_BOOTS = ITEMS.register(
      "gravitite_boots", () -> new ArmorItem(AetherArmorMaterials.GRAVITITE, Type.BOOTS, new Properties().durability(Type.BOOTS.getDurability(33)))
   );
   public static final DeferredItem<Item> VALKYRIE_HELMET = ITEMS.register(
      "valkyrie_helmet",
      () -> new ArmorItem(AetherArmorMaterials.VALKYRIE, Type.HELMET, new Properties().durability(Type.HELMET.getDurability(33)).rarity(AETHER_LOOT))
   );
   public static final DeferredItem<Item> VALKYRIE_CHESTPLATE = ITEMS.register(
      "valkyrie_chestplate",
      () -> new ArmorItem(AetherArmorMaterials.VALKYRIE, Type.CHESTPLATE, new Properties().durability(Type.CHESTPLATE.getDurability(33)).rarity(AETHER_LOOT))
   );
   public static final DeferredItem<Item> VALKYRIE_LEGGINGS = ITEMS.register(
      "valkyrie_leggings",
      () -> new ArmorItem(AetherArmorMaterials.VALKYRIE, Type.LEGGINGS, new Properties().durability(Type.LEGGINGS.getDurability(33)).rarity(AETHER_LOOT))
   );
   public static final DeferredItem<Item> VALKYRIE_BOOTS = ITEMS.register(
      "valkyrie_boots",
      () -> new ArmorItem(AetherArmorMaterials.VALKYRIE, Type.BOOTS, new Properties().durability(Type.BOOTS.getDurability(33)).rarity(AETHER_LOOT))
   );
   public static final DeferredItem<Item> NEPTUNE_HELMET = ITEMS.register(
      "neptune_helmet",
      () -> new ArmorItem(AetherArmorMaterials.NEPTUNE, Type.HELMET, new Properties().durability(Type.HELMET.getDurability(15)).rarity(AETHER_LOOT))
   );
   public static final DeferredItem<Item> NEPTUNE_CHESTPLATE = ITEMS.register(
      "neptune_chestplate",
      () -> new ArmorItem(AetherArmorMaterials.NEPTUNE, Type.CHESTPLATE, new Properties().durability(Type.CHESTPLATE.getDurability(15)).rarity(AETHER_LOOT))
   );
   public static final DeferredItem<Item> NEPTUNE_LEGGINGS = ITEMS.register(
      "neptune_leggings",
      () -> new ArmorItem(AetherArmorMaterials.NEPTUNE, Type.LEGGINGS, new Properties().durability(Type.LEGGINGS.getDurability(15)).rarity(AETHER_LOOT))
   );
   public static final DeferredItem<Item> NEPTUNE_BOOTS = ITEMS.register(
      "neptune_boots",
      () -> new ArmorItem(AetherArmorMaterials.NEPTUNE, Type.BOOTS, new Properties().durability(Type.BOOTS.getDurability(15)).rarity(AETHER_LOOT))
   );
   public static final DeferredItem<Item> PHOENIX_HELMET = ITEMS.register(
      "phoenix_helmet",
      () -> new ArmorItem(
         AetherArmorMaterials.PHOENIX, Type.HELMET, new Properties().durability(Type.HELMET.getDurability(33)).rarity(AETHER_LOOT).fireResistant()
      )
   );
   public static final DeferredItem<Item> PHOENIX_CHESTPLATE = ITEMS.register(
      "phoenix_chestplate",
      () -> new ArmorItem(
         AetherArmorMaterials.PHOENIX, Type.CHESTPLATE, new Properties().durability(Type.CHESTPLATE.getDurability(33)).rarity(AETHER_LOOT).fireResistant()
      )
   );
   public static final DeferredItem<Item> PHOENIX_LEGGINGS = ITEMS.register(
      "phoenix_leggings",
      () -> new ArmorItem(
         AetherArmorMaterials.PHOENIX, Type.LEGGINGS, new Properties().durability(Type.LEGGINGS.getDurability(33)).rarity(AETHER_LOOT).fireResistant()
      )
   );
   public static final DeferredItem<Item> PHOENIX_BOOTS = ITEMS.register(
      "phoenix_boots",
      () -> new ArmorItem(
         AetherArmorMaterials.PHOENIX, Type.BOOTS, new Properties().durability(Type.BOOTS.getDurability(33)).rarity(AETHER_LOOT).fireResistant()
      )
   );
   public static final DeferredItem<Item> OBSIDIAN_HELMET = ITEMS.register(
      "obsidian_helmet",
      () -> new ArmorItem(AetherArmorMaterials.OBSIDIAN, Type.HELMET, new Properties().durability(Type.HELMET.getDurability(37)).rarity(AETHER_LOOT))
   );
   public static final DeferredItem<Item> OBSIDIAN_CHESTPLATE = ITEMS.register(
      "obsidian_chestplate",
      () -> new ArmorItem(AetherArmorMaterials.OBSIDIAN, Type.CHESTPLATE, new Properties().durability(Type.CHESTPLATE.getDurability(37)).rarity(AETHER_LOOT))
   );
   public static final DeferredItem<Item> OBSIDIAN_LEGGINGS = ITEMS.register(
      "obsidian_leggings",
      () -> new ArmorItem(AetherArmorMaterials.OBSIDIAN, Type.LEGGINGS, new Properties().durability(Type.LEGGINGS.getDurability(37)).rarity(AETHER_LOOT))
   );
   public static final DeferredItem<Item> OBSIDIAN_BOOTS = ITEMS.register(
      "obsidian_boots",
      () -> new ArmorItem(AetherArmorMaterials.OBSIDIAN, Type.BOOTS, new Properties().durability(Type.BOOTS.getDurability(37)).rarity(AETHER_LOOT))
   );
   public static final DeferredItem<Item> SENTRY_BOOTS = ITEMS.register(
      "sentry_boots",
      () -> new ArmorItem(AetherArmorMaterials.SENTRY, Type.BOOTS, new Properties().durability(Type.BOOTS.getDurability(15)).rarity(AETHER_LOOT))
   );
   public static final DeferredItem<Item> BLUE_BERRY = ITEMS.register("blue_berry", () -> new Item(new Properties().food(AetherFoods.BLUE_BERRY)));
   public static final DeferredItem<Item> ENCHANTED_BERRY = ITEMS.register(
      "enchanted_berry", () -> new Item(new Properties().rarity(Rarity.RARE).food(AetherFoods.ENCHANTED_BERRY))
   );
   public static final DeferredItem<Item> WHITE_APPLE = ITEMS.register("white_apple", WhiteAppleItem::new);
   public static final DeferredItem<Item> BLUE_GUMMY_SWET = ITEMS.register("blue_gummy_swet", GummySwetItem::new);
   public static final DeferredItem<Item> GOLDEN_GUMMY_SWET = ITEMS.register("golden_gummy_swet", GummySwetItem::new);
   public static final DeferredItem<Item> HEALING_STONE = ITEMS.register("healing_stone", HealingStoneItem::new);
   public static final DeferredItem<Item> CANDY_CANE = ITEMS.register("candy_cane", () -> new Item(new Properties().food(AetherFoods.CANDY_CANE)));
   public static final DeferredItem<Item> GINGERBREAD_MAN = ITEMS.register(
      "gingerbread_man", () -> new Item(new Properties().food(AetherFoods.GINGERBREAD_MAN))
   );
   public static final DeferredItem<Item> IRON_RING = ITEMS.register(
      "iron_ring", () -> new RingItem(AetherSoundEvents.ITEM_ACCESSORY_EQUIP_IRON_RING, new Properties().stacksTo(1))
   );
   public static final DeferredItem<Item> GOLDEN_RING = ITEMS.register(
      "golden_ring", () -> new RingItem(AetherSoundEvents.ITEM_ACCESSORY_EQUIP_GOLD_RING, new Properties().stacksTo(1))
   );
   public static final DeferredItem<Item> ZANITE_RING = ITEMS.register("zanite_ring", () -> new ZaniteRingItem(new Properties().durability(49)));
   public static final DeferredItem<Item> ICE_RING = ITEMS.register("ice_ring", () -> new IceRingItem(new Properties().durability(125)));
   public static final DeferredItem<Item> IRON_PENDANT = ITEMS.register(
      "iron_pendant", () -> new PendantItem("iron_pendant", AetherSoundEvents.ITEM_ACCESSORY_EQUIP_IRON_PENDANT, new Properties().stacksTo(1))
   );
   public static final DeferredItem<Item> GOLDEN_PENDANT = ITEMS.register(
      "golden_pendant", () -> new PendantItem("golden_pendant", AetherSoundEvents.ITEM_ACCESSORY_EQUIP_GOLD_PENDANT, new Properties().stacksTo(1))
   );
   public static final DeferredItem<Item> ZANITE_PENDANT = ITEMS.register("zanite_pendant", () -> new ZanitePendantItem(new Properties().durability(98)));
   public static final DeferredItem<Item> ICE_PENDANT = ITEMS.register("ice_pendant", () -> new IcePendantItem(new Properties().durability(250)));
   public static final DeferredItem<Item> LEATHER_GLOVES = ITEMS.register("leather_gloves", () -> new LeatherGlovesItem(0.25, new Properties().durability(59)));
   public static final DeferredItem<Item> CHAINMAIL_GLOVES = ITEMS.register(
      "chainmail_gloves", () -> new GlovesItem(ArmorMaterials.CHAIN, 0.35, "chainmail_gloves", SoundEvents.ARMOR_EQUIP_CHAIN, new Properties().durability(131))
   );
   public static final DeferredItem<Item> IRON_GLOVES = ITEMS.register(
      "iron_gloves", () -> new GlovesItem(ArmorMaterials.IRON, 0.5, "iron_gloves", SoundEvents.ARMOR_EQUIP_IRON, new Properties().durability(250))
   );
   public static final DeferredItem<Item> GOLDEN_GLOVES = ITEMS.register("golden_gloves", () -> new GoldGlovesItem(0.25, new Properties().durability(32)));
   public static final DeferredItem<Item> DIAMOND_GLOVES = ITEMS.register(
      "diamond_gloves",
      () -> new GlovesItem(ArmorMaterials.DIAMOND, 0.75, "diamond_gloves", SoundEvents.ARMOR_EQUIP_DIAMOND, new Properties().durability(1561))
   );
   public static final DeferredItem<Item> NETHERITE_GLOVES = ITEMS.register(
      "netherite_gloves",
      () -> new GlovesItem(
         ArmorMaterials.NETHERITE, 1.0, "netherite_gloves", SoundEvents.ARMOR_EQUIP_NETHERITE, new Properties().durability(2031).fireResistant()
      )
   );
   public static final DeferredItem<Item> ZANITE_GLOVES = ITEMS.register("zanite_gloves", () -> new ZaniteGlovesItem(0.5, new Properties().durability(250)));
   public static final DeferredItem<Item> GRAVITITE_GLOVES = ITEMS.register(
      "gravitite_gloves",
      () -> new GlovesItem(
         AetherArmorMaterials.GRAVITITE, 0.75, "gravitite_gloves", AetherSoundEvents.ITEM_ARMOR_EQUIP_GRAVITITE, new Properties().durability(1561)
      )
   );
   public static final DeferredItem<Item> VALKYRIE_GLOVES = ITEMS.register(
      "valkyrie_gloves",
      () -> new GlovesItem(
         AetherArmorMaterials.VALKYRIE,
         1.0,
         "valkyrie_gloves",
         AetherSoundEvents.ITEM_ARMOR_EQUIP_VALKYRIE,
         new Properties().stacksTo(1).rarity(AETHER_LOOT).durability(1561)
      )
   );
   public static final DeferredItem<Item> NEPTUNE_GLOVES = ITEMS.register(
      "neptune_gloves",
      () -> new GlovesItem(
         AetherArmorMaterials.NEPTUNE,
         0.5,
         "neptune_gloves",
         AetherSoundEvents.ITEM_ARMOR_EQUIP_NEPTUNE,
         new Properties().stacksTo(1).rarity(AETHER_LOOT).durability(250)
      )
   );
   public static final DeferredItem<Item> PHOENIX_GLOVES = ITEMS.register(
      "phoenix_gloves",
      () -> new GlovesItem(
         AetherArmorMaterials.PHOENIX,
         1.0,
         "phoenix_gloves",
         AetherSoundEvents.ITEM_ARMOR_EQUIP_PHOENIX,
         new Properties().stacksTo(1).rarity(AETHER_LOOT).fireResistant().durability(1561)
      )
   );
   public static final DeferredItem<Item> OBSIDIAN_GLOVES = ITEMS.register(
      "obsidian_gloves",
      () -> new GlovesItem(
         AetherArmorMaterials.OBSIDIAN,
         1.0,
         "obsidian_gloves",
         AetherSoundEvents.ITEM_ARMOR_EQUIP_OBSIDIAN,
         new Properties().stacksTo(1).rarity(AETHER_LOOT).durability(2031)
      )
   );
   public static final DeferredItem<Item> RED_CAPE = ITEMS.register("red_cape", () -> new CapeItem("red_cape", new Properties().stacksTo(1)));
   public static final DeferredItem<Item> BLUE_CAPE = ITEMS.register("blue_cape", () -> new CapeItem("blue_cape", new Properties().stacksTo(1)));
   public static final DeferredItem<Item> YELLOW_CAPE = ITEMS.register("yellow_cape", () -> new CapeItem("yellow_cape", new Properties().stacksTo(1)));
   public static final DeferredItem<Item> WHITE_CAPE = ITEMS.register("white_cape", () -> new CapeItem("white_cape", new Properties().stacksTo(1)));
   public static final DeferredItem<Item> AGILITY_CAPE = ITEMS.register(
      "agility_cape", () -> new AgilityCapeItem("agility_cape", new Properties().stacksTo(1).rarity(AETHER_LOOT))
   );
   public static final DeferredItem<Item> SWET_CAPE = ITEMS.register("swet_cape", () -> new CapeItem("swet_cape", new Properties().stacksTo(1)));
   public static final DeferredItem<Item> INVISIBILITY_CLOAK = ITEMS.register(
      "invisibility_cloak", () -> new InvisibilityCloakItem(new Properties().stacksTo(1).rarity(AETHER_LOOT))
   );
   public static final DeferredItem<Item> VALKYRIE_CAPE = ITEMS.register(
      "valkyrie_cape", () -> new ValkyrieCapeItem(new Properties().stacksTo(1).rarity(AETHER_LOOT))
   );
   public static final DeferredItem<Item> GOLDEN_FEATHER = ITEMS.register(
      "golden_feather", () -> new GoldenFeatherItem(new Properties().stacksTo(1).rarity(AETHER_LOOT))
   );
   public static final DeferredItem<Item> REGENERATION_STONE = ITEMS.register(
      "regeneration_stone", () -> new RegenerationStoneItem(new Properties().stacksTo(1).rarity(AETHER_LOOT))
   );
   public static final DeferredItem<Item> IRON_BUBBLE = ITEMS.register(
      "iron_bubble", () -> new IronBubbleItem(new Properties().stacksTo(1).rarity(AETHER_LOOT))
   );
   public static final DeferredItem<Item> SHIELD_OF_REPULSION = ITEMS.register(
      "shield_of_repulsion", () -> new ShieldOfRepulsionItem(new Properties().durability(512).rarity(AETHER_LOOT))
   );
   public static final DeferredItem<Item> SKYROOT_STICK = ITEMS.register("skyroot_stick", () -> new Item(new Properties()));
   public static final DeferredItem<Item> GOLDEN_AMBER = ITEMS.register("golden_amber", () -> new Item(new Properties()));
   public static final DeferredItem<Item> SWET_BALL = ITEMS.register("swet_ball", () -> new SwetBallItem(new Properties()));
   public static final DeferredItem<Item> AECHOR_PETAL = ITEMS.register("aechor_petal", () -> new Item(new Properties()));
   public static final DeferredItem<Item> AMBROSIUM_SHARD = ITEMS.register("ambrosium_shard", () -> new AmbrosiumShardItem(new Properties()));
   public static final DeferredItem<Item> ZANITE_GEMSTONE = ITEMS.register("zanite_gemstone", () -> new Item(new Properties()));
   public static final DeferredItem<Item> VICTORY_MEDAL = ITEMS.register("victory_medal", () -> new Item(new Properties().stacksTo(10).rarity(AETHER_LOOT)));
   public static final DeferredItem<Item> BRONZE_DUNGEON_KEY = ITEMS.register(
      "bronze_dungeon_key",
      () -> new Item(
         new Properties()
            .stacksTo(1)
            .rarity(AETHER_LOOT)
            .fireResistant()
            .component(AetherDataComponents.DUNGEON_KIND, new DungeonKind(ResourceLocation.fromNamespaceAndPath("aether", "bronze")))
      )
   );
   public static final DeferredItem<Item> SILVER_DUNGEON_KEY = ITEMS.register(
      "silver_dungeon_key",
      () -> new Item(
         new Properties()
            .stacksTo(1)
            .rarity(AETHER_LOOT)
            .fireResistant()
            .component(AetherDataComponents.DUNGEON_KIND, new DungeonKind(ResourceLocation.fromNamespaceAndPath("aether", "silver")))
      )
   );
   public static final DeferredItem<Item> GOLD_DUNGEON_KEY = ITEMS.register(
      "gold_dungeon_key",
      () -> new Item(
         new Properties()
            .stacksTo(1)
            .rarity(AETHER_LOOT)
            .fireResistant()
            .component(AetherDataComponents.DUNGEON_KIND, new DungeonKind(ResourceLocation.fromNamespaceAndPath("aether", "gold")))
      )
   );
   public static final DeferredItem<Item> MUSIC_DISC_AETHER_TUNE = ITEMS.register(
      "music_disc_aether_tune", () -> new Item(new Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(AetherJukeboxSongs.AETHER_TUNE))
   );
   public static final DeferredItem<Item> MUSIC_DISC_ASCENDING_DAWN = ITEMS.register(
      "music_disc_ascending_dawn", () -> new Item(new Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(AetherJukeboxSongs.ASCENDING_DAWN))
   );
   public static final DeferredItem<Item> MUSIC_DISC_CHINCHILLA = ITEMS.register(
      "music_disc_chinchilla", () -> new Item(new Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(AetherJukeboxSongs.CHINCHILLA))
   );
   public static final DeferredItem<Item> MUSIC_DISC_HIGH = ITEMS.register(
      "music_disc_high", () -> new Item(new Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(AetherJukeboxSongs.HIGH))
   );
   public static final DeferredItem<Item> MUSIC_DISC_KLEPTO = ITEMS.register(
      "music_disc_klepto", () -> new Item(new Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(AetherJukeboxSongs.KLEPTO))
   );
   public static final DeferredItem<Item> MUSIC_DISC_SLIDERS_WRATH = ITEMS.register(
      "music_disc_sliders_wrath", () -> new Item(new Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(AetherJukeboxSongs.SLIDERS_WRATH))
   );
   public static final DeferredItem<Item> SKYROOT_BUCKET = ITEMS.register(
      "skyroot_bucket", () -> new SkyrootBucketItem(Fluids.EMPTY, new Properties().stacksTo(16))
   );
   public static final DeferredItem<Item> SKYROOT_WATER_BUCKET = ITEMS.register(
      "skyroot_water_bucket", () -> new SkyrootBucketItem(Fluids.WATER, new Properties().craftRemainder((Item)SKYROOT_BUCKET.get()).stacksTo(1))
   );
   public static final DeferredItem<Item> SKYROOT_POISON_BUCKET = ITEMS.register(
      "skyroot_poison_bucket", () -> new SkyrootPoisonBucketItem(new Properties().craftRemainder((Item)SKYROOT_BUCKET.get()).stacksTo(1))
   );
   public static final DeferredItem<Item> SKYROOT_REMEDY_BUCKET = ITEMS.register(
      "skyroot_remedy_bucket", () -> new SkyrootRemedyBucketItem(new Properties().craftRemainder((Item)SKYROOT_BUCKET.get()).stacksTo(1).rarity(Rarity.RARE))
   );
   public static final DeferredItem<Item> SKYROOT_MILK_BUCKET = ITEMS.register(
      "skyroot_milk_bucket", () -> new SkyrootMilkBucketItem(new Properties().craftRemainder((Item)SKYROOT_BUCKET.get()).stacksTo(1))
   );
   public static final DeferredItem<Item> SKYROOT_POWDER_SNOW_BUCKET = ITEMS.register(
      "skyroot_powder_snow_bucket",
      () -> new SkyrootSolidBucketItem(
         Blocks.POWDER_SNOW, SoundEvents.BUCKET_EMPTY_POWDER_SNOW, new Properties().craftRemainder((Item)SKYROOT_BUCKET.get()).stacksTo(1)
      )
   );
   public static final DeferredItem<Item> SKYROOT_COD_BUCKET = ITEMS.register(
      "skyroot_cod_bucket",
      () -> new SkyrootMobBucketItem(
         EntityType.COD,
         Fluids.WATER,
         SoundEvents.BUCKET_EMPTY_FISH,
         new Properties().craftRemainder((Item)SKYROOT_BUCKET.get()).stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)
      )
   );
   public static final DeferredItem<Item> SKYROOT_SALMON_BUCKET = ITEMS.register(
      "skyroot_salmon_bucket",
      () -> new SkyrootMobBucketItem(
         EntityType.SALMON,
         Fluids.WATER,
         SoundEvents.BUCKET_EMPTY_FISH,
         new Properties().craftRemainder((Item)SKYROOT_BUCKET.get()).stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)
      )
   );
   public static final DeferredItem<Item> SKYROOT_PUFFERFISH_BUCKET = ITEMS.register(
      "skyroot_pufferfish_bucket",
      () -> new SkyrootMobBucketItem(
         EntityType.PUFFERFISH,
         Fluids.WATER,
         SoundEvents.BUCKET_EMPTY_FISH,
         new Properties().craftRemainder((Item)SKYROOT_BUCKET.get()).stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)
      )
   );
   public static final DeferredItem<Item> SKYROOT_TROPICAL_FISH_BUCKET = ITEMS.register(
      "skyroot_tropical_fish_bucket",
      () -> new SkyrootMobBucketItem(
         EntityType.TROPICAL_FISH,
         Fluids.WATER,
         SoundEvents.BUCKET_EMPTY_FISH,
         new Properties().craftRemainder((Item)SKYROOT_BUCKET.get()).stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)
      )
   );
   public static final DeferredItem<Item> SKYROOT_AXOLOTL_BUCKET = ITEMS.register(
      "skyroot_axolotl_bucket",
      () -> new SkyrootMobBucketItem(
         EntityType.AXOLOTL,
         Fluids.WATER,
         SoundEvents.BUCKET_EMPTY_AXOLOTL,
         new Properties().craftRemainder((Item)SKYROOT_BUCKET.get()).stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)
      )
   );
   public static final DeferredItem<Item> SKYROOT_TADPOLE_BUCKET = ITEMS.register(
      "skyroot_tadpole_bucket",
      () -> new SkyrootMobBucketItem(
         EntityType.TADPOLE,
         Fluids.WATER,
         SoundEvents.BUCKET_EMPTY_TADPOLE,
         new Properties().craftRemainder((Item)SKYROOT_BUCKET.get()).stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)
      )
   );
   public static final DeferredItem<Item> SKYROOT_BOAT = ITEMS.register("skyroot_boat", () -> new SkyrootBoatItem(false, new Properties().stacksTo(1)));
   public static final DeferredItem<Item> SKYROOT_CHEST_BOAT = ITEMS.register(
      "skyroot_chest_boat", () -> new SkyrootBoatItem(true, new Properties().stacksTo(1))
   );
   public static final DeferredItem<Item> COLD_PARACHUTE = ITEMS.register(
      "cold_parachute", () -> new ParachuteItem(AetherEntityTypes.COLD_PARACHUTE, new Properties().durability(1))
   );
   public static final DeferredItem<Item> GOLDEN_PARACHUTE = ITEMS.register(
      "golden_parachute", () -> new ParachuteItem(AetherEntityTypes.GOLDEN_PARACHUTE, new Properties().durability(20))
   );
   public static final DeferredItem<Item> BLUE_MOA_EGG = ITEMS.register("blue_moa_egg", () -> new MoaEggItem(AetherMoaTypes.BLUE, 7829503, new Properties()));
   public static final DeferredItem<Item> WHITE_MOA_EGG = ITEMS.register(
      "white_moa_egg", () -> new MoaEggItem(AetherMoaTypes.WHITE, 16777215, new Properties())
   );
   public static final DeferredItem<Item> BLACK_MOA_EGG = ITEMS.register("black_moa_egg", () -> new MoaEggItem(AetherMoaTypes.BLACK, 2236962, new Properties()));
   public static final DeferredItem<Item> NATURE_STAFF = ITEMS.register("nature_staff", () -> new Item(new Properties().durability(100)));
   public static final DeferredItem<Item> CLOUD_STAFF = ITEMS.register("cloud_staff", CloudStaffItem::new);
   public static final DeferredItem<Item> LIFE_SHARD = ITEMS.register("life_shard", () -> new LifeShardItem(new Properties().stacksTo(1).rarity(AETHER_LOOT)));
   public static final DeferredItem<Item> BOOK_OF_LORE = ITEMS.register(
      "book_of_lore", () -> new LoreBookItem(new Properties().stacksTo(1).rarity(AETHER_LOOT))
   );
   public static final DeferredItem<Item> AETHER_PORTAL_FRAME = ITEMS.register("aether_portal_frame", () -> new AetherPortalItem(new Properties().stacksTo(1)));
   public static final DeferredItem<SpawnEggItem> AECHOR_PLANT_SPAWN_EGG = ITEMS.register(
      "aechor_plant_spawn_egg", () -> new DeferredSpawnEggItem(AetherEntityTypes.AECHOR_PLANT, 483704, 4966046, new Properties())
   );
   public static final DeferredItem<SpawnEggItem> AERBUNNY_SPAWN_EGG = ITEMS.register(
      "aerbunny_spawn_egg", () -> new DeferredSpawnEggItem(AetherEntityTypes.AERBUNNY, 14875903, 16769017, new Properties())
   );
   public static final DeferredItem<SpawnEggItem> AERWHALE_SPAWN_EGG = ITEMS.register(
      "aerwhale_spawn_egg", () -> new DeferredSpawnEggItem(AetherEntityTypes.AERWHALE, 12642301, 8887978, new Properties())
   );
   public static final DeferredItem<SpawnEggItem> COCKATRICE_SPAWN_EGG = ITEMS.register(
      "cockatrice_spawn_egg", () -> new DeferredSpawnEggItem(AetherEntityTypes.COCKATRICE, 7123292, 7100317, new Properties())
   );
   public static final DeferredItem<SpawnEggItem> FIRE_MINION_SPAWN_EGG = ITEMS.register(
      "fire_minion_spawn_egg", () -> new DeferredSpawnEggItem(AetherEntityTypes.FIRE_MINION, 16739585, 16708864, new Properties())
   );
   public static final DeferredItem<SpawnEggItem> FLYING_COW_SPAWN_EGG = ITEMS.register(
      "flying_cow_spawn_egg", () -> new DeferredSpawnEggItem(AetherEntityTypes.FLYING_COW, 14211288, 16767289, new Properties())
   );
   public static final DeferredItem<SpawnEggItem> MIMIC_SPAWN_EGG = ITEMS.register(
      "mimic_spawn_egg", () -> new DeferredSpawnEggItem(AetherEntityTypes.MIMIC, 11632946, 6314574, new Properties())
   );
   public static final DeferredItem<SpawnEggItem> MOA_SPAWN_EGG = ITEMS.register(
      "moa_spawn_egg", () -> new DeferredSpawnEggItem(AetherEntityTypes.MOA, 8896495, 8026746, new Properties())
   );
   public static final DeferredItem<SpawnEggItem> PHYG_SPAWN_EGG = ITEMS.register(
      "phyg_spawn_egg", () -> new DeferredSpawnEggItem(AetherEntityTypes.PHYG, 16761296, 16767289, new Properties())
   );
   public static final DeferredItem<SpawnEggItem> SENTRY_SPAWN_EGG = ITEMS.register(
      "sentry_spawn_egg", () -> new DeferredSpawnEggItem(AetherEntityTypes.SENTRY, 8421504, 3836652, new Properties())
   );
   public static final DeferredItem<SpawnEggItem> SHEEPUFF_SPAWN_EGG = ITEMS.register(
      "sheepuff_spawn_egg", () -> new DeferredSpawnEggItem(AetherEntityTypes.SHEEPUFF, 14875903, 13340816, new Properties())
   );
   public static final DeferredItem<SpawnEggItem> BLUE_SWET_SPAWN_EGG = ITEMS.register(
      "blue_swet_spawn_egg", () -> new DeferredSpawnEggItem(AetherEntityTypes.BLUE_SWET, 5222874, 13490767, new Properties())
   );
   public static final DeferredItem<SpawnEggItem> GOLDEN_SWET_SPAWN_EGG = ITEMS.register(
      "golden_swet_spawn_egg", () -> new DeferredSpawnEggItem(AetherEntityTypes.GOLDEN_SWET, 13490767, 5222874, new Properties())
   );
   public static final DeferredItem<SpawnEggItem> WHIRLWIND_SPAWN_EGG = ITEMS.register(
      "whirlwind_spawn_egg", () -> new DeferredSpawnEggItem(AetherEntityTypes.WHIRLWIND, 10470391, 16777215, new Properties())
   );
   public static final DeferredItem<SpawnEggItem> EVIL_WHIRLWIND_SPAWN_EGG = ITEMS.register(
      "evil_whirlwind_spawn_egg", () -> new DeferredSpawnEggItem(AetherEntityTypes.EVIL_WHIRLWIND, 10470391, 1118481, new Properties())
   );
   public static final DeferredItem<SpawnEggItem> VALKYRIE_SPAWN_EGG = ITEMS.register(
      "valkyrie_spawn_egg", () -> new DeferredSpawnEggItem(AetherEntityTypes.VALKYRIE, 16381411, 15913472, new Properties())
   );
   public static final DeferredItem<SpawnEggItem> VALKYRIE_QUEEN_SPAWN_EGG = ITEMS.register(
      "valkyrie_queen_spawn_egg", () -> new DeferredSpawnEggItem(AetherEntityTypes.VALKYRIE_QUEEN, 15913472, 16381411, new Properties())
   );
   public static final DeferredItem<SpawnEggItem> SLIDER_SPAWN_EGG = ITEMS.register(
      "slider_spawn_egg", () -> new SliderSpawnEggItem(AetherEntityTypes.SLIDER, 10987431, 6070258, new Properties())
   );
   public static final DeferredItem<SpawnEggItem> SUN_SPIRIT_SPAWN_EGG = ITEMS.register(
      "sun_spirit_spawn_egg", () -> new DeferredSpawnEggItem(AetherEntityTypes.SUN_SPIRIT, 16708864, 16739585, new Properties())
   );
   public static final DeferredItem<SpawnEggItem> ZEPHYR_SPAWN_EGG = ITEMS.register(
      "zephyr_spawn_egg", () -> new DeferredSpawnEggItem(AetherEntityTypes.ZEPHYR, 14671839, 10080232, new Properties())
   );
   public static ItemStack SWET_BANNER = null;

   public static void registerAccessories() {
      AccessoriesAPI.registerAccessory((Item)IRON_RING.get(), (Accessory)IRON_RING.get());
      AccessoriesAPI.registerAccessory((Item)GOLDEN_RING.get(), (Accessory)GOLDEN_RING.get());
      AccessoriesAPI.registerAccessory((Item)ZANITE_RING.get(), (Accessory)ZANITE_RING.get());
      AccessoriesAPI.registerAccessory((Item)ICE_RING.get(), (Accessory)ICE_RING.get());
      AccessoriesAPI.registerAccessory((Item)IRON_PENDANT.get(), (Accessory)IRON_PENDANT.get());
      AccessoriesAPI.registerAccessory((Item)GOLDEN_PENDANT.get(), (Accessory)GOLDEN_PENDANT.get());
      AccessoriesAPI.registerAccessory((Item)ZANITE_PENDANT.get(), (Accessory)ZANITE_PENDANT.get());
      AccessoriesAPI.registerAccessory((Item)ICE_PENDANT.get(), (Accessory)ICE_PENDANT.get());
      AccessoriesAPI.registerAccessory((Item)LEATHER_GLOVES.get(), (Accessory)LEATHER_GLOVES.get());
      AccessoriesAPI.registerAccessory((Item)CHAINMAIL_GLOVES.get(), (Accessory)CHAINMAIL_GLOVES.get());
      AccessoriesAPI.registerAccessory((Item)IRON_GLOVES.get(), (Accessory)IRON_GLOVES.get());
      AccessoriesAPI.registerAccessory((Item)GOLDEN_GLOVES.get(), (Accessory)GOLDEN_GLOVES.get());
      AccessoriesAPI.registerAccessory((Item)DIAMOND_GLOVES.get(), (Accessory)DIAMOND_GLOVES.get());
      AccessoriesAPI.registerAccessory((Item)NETHERITE_GLOVES.get(), (Accessory)NETHERITE_GLOVES.get());
      AccessoriesAPI.registerAccessory((Item)ZANITE_GLOVES.get(), (Accessory)ZANITE_GLOVES.get());
      AccessoriesAPI.registerAccessory((Item)GRAVITITE_GLOVES.get(), (Accessory)GRAVITITE_GLOVES.get());
      AccessoriesAPI.registerAccessory((Item)VALKYRIE_GLOVES.get(), (Accessory)VALKYRIE_GLOVES.get());
      AccessoriesAPI.registerAccessory((Item)NEPTUNE_GLOVES.get(), (Accessory)NEPTUNE_GLOVES.get());
      AccessoriesAPI.registerAccessory((Item)PHOENIX_GLOVES.get(), (Accessory)PHOENIX_GLOVES.get());
      AccessoriesAPI.registerAccessory((Item)OBSIDIAN_GLOVES.get(), (Accessory)OBSIDIAN_GLOVES.get());
      AccessoriesAPI.registerAccessory((Item)RED_CAPE.get(), (Accessory)RED_CAPE.get());
      AccessoriesAPI.registerAccessory((Item)BLUE_CAPE.get(), (Accessory)BLUE_CAPE.get());
      AccessoriesAPI.registerAccessory((Item)YELLOW_CAPE.get(), (Accessory)YELLOW_CAPE.get());
      AccessoriesAPI.registerAccessory((Item)WHITE_CAPE.get(), (Accessory)WHITE_CAPE.get());
      AccessoriesAPI.registerAccessory((Item)AGILITY_CAPE.get(), (Accessory)AGILITY_CAPE.get());
      AccessoriesAPI.registerAccessory((Item)SWET_CAPE.get(), (Accessory)SWET_CAPE.get());
      AccessoriesAPI.registerAccessory((Item)INVISIBILITY_CLOAK.get(), (Accessory)INVISIBILITY_CLOAK.get());
      AccessoriesAPI.registerAccessory((Item)VALKYRIE_CAPE.get(), (Accessory)VALKYRIE_CAPE.get());
      AccessoriesAPI.registerAccessory((Item)GOLDEN_FEATHER.get(), (Accessory)GOLDEN_FEATHER.get());
      AccessoriesAPI.registerAccessory((Item)REGENERATION_STONE.get(), (Accessory)REGENERATION_STONE.get());
      AccessoriesAPI.registerAccessory((Item)IRON_BUBBLE.get(), (Accessory)IRON_BUBBLE.get());
      AccessoriesAPI.registerAccessory((Item)SHIELD_OF_REPULSION.get(), (Accessory)SHIELD_OF_REPULSION.get());
   }

   public static void setupBucketReplacements() {
      SkyrootBucketItem.REPLACEMENTS.put(() -> net.minecraft.world.item.Items.WATER_BUCKET, SKYROOT_WATER_BUCKET);
      SkyrootBucketItem.REPLACEMENTS.put(() -> net.minecraft.world.item.Items.POWDER_SNOW_BUCKET, SKYROOT_POWDER_SNOW_BUCKET);
      SkyrootBucketItem.REPLACEMENTS.put(() -> net.minecraft.world.item.Items.COD_BUCKET, SKYROOT_COD_BUCKET);
      SkyrootBucketItem.REPLACEMENTS.put(() -> net.minecraft.world.item.Items.SALMON_BUCKET, SKYROOT_SALMON_BUCKET);
      SkyrootBucketItem.REPLACEMENTS.put(() -> net.minecraft.world.item.Items.PUFFERFISH_BUCKET, SKYROOT_PUFFERFISH_BUCKET);
      SkyrootBucketItem.REPLACEMENTS.put(() -> net.minecraft.world.item.Items.TROPICAL_FISH_BUCKET, SKYROOT_TROPICAL_FISH_BUCKET);
      SkyrootBucketItem.REPLACEMENTS.put(() -> net.minecraft.world.item.Items.AXOLOTL_BUCKET, SKYROOT_AXOLOTL_BUCKET);
      SkyrootBucketItem.REPLACEMENTS.put(() -> net.minecraft.world.item.Items.TADPOLE_BUCKET, SKYROOT_TADPOLE_BUCKET);
      SkyrootBucketItem.REPLACEMENTS.put(() -> net.minecraft.world.item.Items.MILK_BUCKET, SKYROOT_MILK_BUCKET);
   }

   public static ItemStack createSwetBannerItemStack(HolderGetter<BannerPattern> patternRegistry) {
      if (SWET_BANNER == null) {
         ItemStack bannerStack = new ItemStack(net.minecraft.world.item.Items.BLACK_BANNER);
         BannerPatternLayers layers = new Builder()
            .add(patternRegistry.getOrThrow(BannerPatterns.STRIPE_DOWNLEFT), DyeColor.CYAN)
            .add(patternRegistry.getOrThrow(BannerPatterns.STRIPE_BOTTOM), DyeColor.CYAN)
            .add(patternRegistry.getOrThrow(BannerPatterns.STRIPE_LEFT), DyeColor.CYAN)
            .add(patternRegistry.getOrThrow(BannerPatterns.HALF_HORIZONTAL), DyeColor.BLACK)
            .add(patternRegistry.getOrThrow(BannerPatterns.STRAIGHT_CROSS), DyeColor.CYAN)
            .add(patternRegistry.getOrThrow(BannerPatterns.BORDER), DyeColor.WHITE)
            .add(patternRegistry.getOrThrow(BannerPatterns.GRADIENT_UP), DyeColor.LIGHT_BLUE)
            .add(patternRegistry.getOrThrow(BannerPatterns.GRADIENT), DyeColor.LIGHT_BLUE)
            .build();
         bannerStack.set(DataComponents.BANNER_PATTERNS, layers);
         bannerStack.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
         bannerStack.set(DataComponents.ITEM_NAME, Component.translatable("aether.block.aether.swet_banner").withStyle(ChatFormatting.GOLD));
         SWET_BANNER = bannerStack;
      }

      return SWET_BANNER;
   }
}

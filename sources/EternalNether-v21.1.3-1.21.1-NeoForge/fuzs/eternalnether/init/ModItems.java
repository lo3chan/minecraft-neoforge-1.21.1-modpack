package fuzs.eternalnether.init;

import fuzs.eternalnether.util.HSV;
import fuzs.eternalnether.world.item.CutlassItem;
import fuzs.eternalnether.world.item.UnrepairableShieldItem;
import fuzs.eternalnether.world.item.WarpedEnderpearlItem;
import fuzs.eternalnether.world.item.WitheredBoneMealItem;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Item.Properties;

public final class ModItems {
   public static final Reference<Item> COBBLED_BLACKSTONE = ModRegistry.REGISTRIES.registerBlockItem(ModBlocks.COBBLED_BLACKSTONE);
   public static final Reference<Item> WITHERED_BLACKSTONE = ModRegistry.REGISTRIES.registerBlockItem(ModBlocks.WITHERED_BLACKSTONE);
   public static final Reference<Item> WITHERED_BLACKSTONE_STAIRS = ModRegistry.REGISTRIES.registerBlockItem(ModBlocks.WITHERED_BLACKSTONE_STAIRS);
   public static final Reference<Item> WITHERED_BLACKSTONE_SLAB = ModRegistry.REGISTRIES.registerBlockItem(ModBlocks.WITHERED_BLACKSTONE_SLAB);
   public static final Reference<Item> WITHERED_BLACKSTONE_WALL = ModRegistry.REGISTRIES.registerBlockItem(ModBlocks.WITHERED_BLACKSTONE_WALL);
   public static final Reference<Item> CRACKED_WITHERED_BLACKSTONE = ModRegistry.REGISTRIES.registerBlockItem(ModBlocks.CRACKED_WITHERED_BLACKSTONE);
   public static final Reference<Item> CRACKED_WITHERED_BLACKSTONE_STAIRS = ModRegistry.REGISTRIES
      .registerBlockItem(ModBlocks.CRACKED_WITHERED_BLACKSTONE_STAIRS);
   public static final Reference<Item> CRACKED_WITHERED_BLACKSTONE_SLAB = ModRegistry.REGISTRIES.registerBlockItem(ModBlocks.CRACKED_WITHERED_BLACKSTONE_SLAB);
   public static final Reference<Item> CRACKED_WITHERED_BLACKSTONE_WALL = ModRegistry.REGISTRIES.registerBlockItem(ModBlocks.CRACKED_WITHERED_BLACKSTONE_WALL);
   public static final Reference<Item> CHISELED_WITHERED_BLACKSTONE = ModRegistry.REGISTRIES.registerBlockItem(ModBlocks.CHISELED_WITHERED_BLACKSTONE);
   public static final Reference<Item> WITHERED_BASALT = ModRegistry.REGISTRIES.registerBlockItem(ModBlocks.WITHERED_BASALT);
   public static final Reference<Item> WITHERED_COAL_BLACK = ModRegistry.REGISTRIES.registerBlockItem(ModBlocks.WITHERED_COAL_BLOCK);
   public static final Reference<Item> WITHERED_QUARTZ_BLOCK = ModRegistry.REGISTRIES.registerBlockItem(ModBlocks.WITHERED_QUARTZ_BLOCK);
   public static final Reference<Item> WITHERED_DEBRIS = ModRegistry.REGISTRIES.registerBlockItem(ModBlocks.WITHERED_DEBRIS);
   public static final Reference<Item> SOUL_STONE = ModRegistry.REGISTRIES.registerBlockItem(ModBlocks.SOUL_STONE);
   public static final Reference<Item> WITHERED_BONE_BLOCK = ModRegistry.REGISTRIES.registerBlockItem(ModBlocks.WITHERED_BONE_BLOCK);
   public static final Reference<Item> WARPED_NETHER_BRICKS = ModRegistry.REGISTRIES.registerBlockItem(ModBlocks.WARPED_NETHER_BRICKS);
   public static final Reference<Item> CHISELED_WARPED_NETHER_BRICKS = ModRegistry.REGISTRIES.registerBlockItem(ModBlocks.CHISELED_WARPED_NETHER_BRICKS);
   public static final Reference<Item> WARPED_NETHER_BRICK_STAIRS = ModRegistry.REGISTRIES.registerBlockItem(ModBlocks.WARPED_NETHER_BRICK_STAIRS);
   public static final Reference<Item> WARPED_NETHER_BRICK_SLAB = ModRegistry.REGISTRIES.registerBlockItem(ModBlocks.WARPED_NETHER_BRICK_SLAB);
   public static final Reference<Item> WARPED_NETHER_BRICK_WALL = ModRegistry.REGISTRIES.registerBlockItem(ModBlocks.WARPED_NETHER_BRICK_WALL);
   public static final ResourceKey<JukeboxSong> WITHER_WALTZ_JUKEBOX_SONG = ModRegistry.REGISTRIES.makeResourceKey(Registries.JUKEBOX_SONG, "wither_waltz");
   public static final Reference<Item> WITHER_WALTZ_MUSIC_DISC = ModRegistry.REGISTRIES
      .registerItem("wither_waltz_music_disc", () -> new Item(new Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(WITHER_WALTZ_JUKEBOX_SONG)));
   public static final Reference<Item> WARPED_ENDER_PEARL = ModRegistry.REGISTRIES
      .registerItem("warped_ender_pearl", () -> new WarpedEnderpearlItem(new Properties().stacksTo(16).rarity(Rarity.RARE)));
   public static final Reference<Item> WITHERED_BONE = ModRegistry.REGISTRIES.registerItem("withered_bone", () -> new Item(new Properties()));
   public static final Reference<Item> WITHERED_BONE_MEAL = ModRegistry.REGISTRIES
      .registerItem("withered_bone_meal", () -> new WitheredBoneMealItem(new Properties()));
   public static final Reference<Item> NETHERITE_BELL = ModRegistry.REGISTRIES
      .registerBlockItem(ModBlocks.NETHERITE_BELL, () -> new Properties().rarity(Rarity.EPIC).fireResistant());
   public static final Reference<Item> GILDED_NETHERITE_SHIELD = ModRegistry.REGISTRIES
      .registerItem("gilded_netherite_shield", () -> new UnrepairableShieldItem(new Properties().durability(1512).rarity(Rarity.RARE).fireResistant()));
   public static final Reference<Item> CUTLASS = ModRegistry.REGISTRIES
      .registerItem(
         "cutlass",
         () -> new CutlassItem(
            new Properties()
               .durability(312)
               .component(DataComponents.TOOL, CutlassItem.createToolProperties())
               .attributes(SwordItem.createAttributes(Tiers.IRON, 3, -1.6F))
         )
      );
   public static final Reference<Item> PIGLIN_PRISONER_SPAWN_EGG = ModRegistry.REGISTRIES
      .registerSpawnEggItem(ModEntityTypes.PIGLIN_PRISONER, 13082248, 16380836);
   public static final Reference<Item> PIGLIN_HUNTER_SPAWN_EGG = ModRegistry.REGISTRIES.registerSpawnEggItem(ModEntityTypes.PIGLIN_HUNTER, 12215877, 16380836);
   public static final Reference<Item> WEX_SPAWN_EGG = ModRegistry.REGISTRIES
      .registerSpawnEggItem(ModEntityTypes.WEX, 7444680, generateHighlightColor(7444680));
   public static final Reference<Item> WARPED_ENDERMAN_SPAWN_EGG = ModRegistry.REGISTRIES
      .registerSpawnEggItem(ModEntityTypes.WARPED_ENDERMAN, 950913, generateHighlightColor(950913));
   public static final Reference<Item> WRAITHER_SPAWN_EGG = ModRegistry.REGISTRIES
      .registerSpawnEggItem(ModEntityTypes.WRAITHER, 4672845, generateHighlightColor(4672845));
   public static final Reference<Item> CORPOR_SPAWN_EGG = ModRegistry.REGISTRIES
      .registerSpawnEggItem(ModEntityTypes.CORPOR, 4872023, generateHighlightColor(4872023));
   public static final Reference<Item> WITHER_SKELETON_KNIGHT_SPAWN_EGG = ModRegistry.REGISTRIES
      .registerSpawnEggItem(ModEntityTypes.WITHER_SKELETON_KNIGHT, 5132882, generateHighlightColor(5132882));
   public static final Reference<Item> WITHER_SKELETON_HORSE_SPAWN_EGG = ModRegistry.REGISTRIES
      .registerSpawnEggItem(ModEntityTypes.WITHER_SKELETON_HORSE, 5064519, generateHighlightColor(5064519));

   public static void boostrap() {
   }

   @Deprecated
   public static int generateHighlightColor(int backgroundColor) {
      int hsv = HSV.rgbToHsv(
         HSV.from8BitChannel(ARGB32.red(backgroundColor)),
         HSV.from8BitChannel(ARGB32.green(backgroundColor)),
         HSV.from8BitChannel(ARGB32.blue(backgroundColor))
      );
      float saturation = Math.min(1.0F, HSV.saturationFloat(hsv) * 1.2F);
      float value = Math.max(0.0F, HSV.valueFloat(hsv) * 0.75F);
      return Mth.hsvToRgb(HSV.hueFloat(hsv), saturation, value);
   }
}

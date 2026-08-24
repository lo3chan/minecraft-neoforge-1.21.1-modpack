package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.item.AMBlockItem;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.BlockItemAMRender;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AMBlockRegistry {
   public static final Properties PURPUR_PLANKS_PROPERTIES = Properties.of().mapColor(MapColor.COLOR_PINK).strength(0.5F, 1.0F).sound(SoundType.WOOD);
   public static final DeferredRegister<Block> DEF_REG = DeferredRegister.create(Registries.BLOCK, "alexsmobs");
   public static final Supplier<Block> BANANA_PEEL = registerBlockAndItem("banana_peel", () -> new BlockBananaPeel());
   public static final Supplier<Block> HUMMINGBIRD_FEEDER = registerBlockAndItem("hummingbird_feeder", () -> new BlockHummingbirdFeeder());
   public static final Supplier<Block> CROCODILE_EGG = registerBlockAndItem("crocodile_egg", () -> new BlockReptileEgg(AMEntityRegistry.CROCODILE));
   public static final Supplier<Block> GUSTMAKER = registerBlockAndItem("gustmaker", () -> new BlockGustmaker());
   public static final Supplier<Block> STRADDLITE_BLOCK = registerBlockAndItem(
      "straddlite_block",
      () -> new Block(Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(1.0F, 1200.0F).sound(SoundType.ANCIENT_DEBRIS)),
      new net.minecraft.world.item.Item.Properties().fireResistant(),
      false
   );
   public static final Supplier<Block> PLATYPUS_EGG = registerBlockAndItem("platypus_egg", () -> new BlockReptileEgg(AMEntityRegistry.PLATYPUS));
   public static final Supplier<Block> LEAFCUTTER_ANTHILL = registerBlockAndItem("leafcutter_anthill", () -> new BlockLeafcutterAnthill());
   public static final Supplier<Block> LEAFCUTTER_ANT_CHAMBER = registerBlockAndItem("leafcutter_ant_chamber", () -> new BlockLeafcutterAntChamber());
   public static final Supplier<Block> CAPSID = registerBlockAndItem("capsid", () -> new BlockCapsid());
   public static final Supplier<Block> VOID_WORM_BEAK = registerBlockAndItem("void_worm_beak", () -> new BlockVoidWormBeak());
   public static final Supplier<Block> VOID_WORM_EFFIGY = registerBlockAndItem("void_worm_effigy", () -> new BlockVoidWormEffigy());
   public static final Supplier<Block> TERRAPIN_EGG = registerBlockAndItem("terrapin_egg", () -> new BlockTerrapinEgg());
   public static final Supplier<Block> RAINBOW_GLASS = registerBlockAndItem("rainbow_glass", () -> new BlockRainbowGlass());
   public static final Supplier<Block> BISON_FUR_BLOCK = registerBlockAndItem(
      "bison_fur_block", () -> new Block(Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.6F, 1.0F).sound(SoundType.WOOL))
   );
   public static final Supplier<Block> BISON_CARPET = registerBlockAndItem("bison_carpet", () -> new BlockBisonCarpet());
   public static final Supplier<Block> SAND_CIRCLE = registerBlockAndItem(
      "sand_circle", () -> AMPlatform.coloredSand(14406560, AMPlatform.copyProperties(Blocks.SAND)), new net.minecraft.world.item.Item.Properties(), false
   );
   public static final Supplier<Block> RED_SAND_CIRCLE = registerBlockAndItem(
      "red_sand_circle",
      () -> AMPlatform.coloredSand(11098145, AMPlatform.copyProperties(Blocks.RED_SAND)),
      new net.minecraft.world.item.Item.Properties(),
      false
   );
   public static final Supplier<Block> ENDER_RESIDUE = registerBlockAndItem("ender_residue", () -> new BlockEnderResidue());
   public static final Supplier<Block> TRANSMUTATION_TABLE = registerBlockAndItem(
      "transmutation_table", () -> new BlockTransmutationTable(), new net.minecraft.world.item.Item.Properties().rarity(Rarity.EPIC).fireResistant(), true
   );
   public static final Supplier<Block> SCULK_BOOMER = registerBlockAndItem("sculk_boomer", () -> new BlockSculkBoomer());
   public static final Supplier<Block> SKUNK_SPRAY = regBlock("skunk_spray", () -> new BlockSkunkSpray());
   public static final Supplier<Block> BANANA_SLUG_SLIME_BLOCK = registerBlockAndItem("banana_slug_slime_block", () -> new BlockBananaSlugSlime());
   public static final Supplier<Block> CRYSTALIZED_BANANA_SLUG_MUCUS = registerBlockAndItem("crystalized_banana_slug_mucus", () -> new BlockCrystalizedMucus());
   public static final Supplier<Block> CAIMAN_EGG = registerBlockAndItem("caiman_egg", () -> new BlockReptileEgg(AMEntityRegistry.CAIMAN));
   public static final Supplier<Block> TRIOPS_EGGS = registerBlockAndItem("triops_eggs", () -> new BlockTriopsEggs());

   public static Supplier<Block> registerBlockAndItem(String name, Supplier<Block> block) {
      return registerBlockAndItem(name, block, new net.minecraft.world.item.Item.Properties(), false);
   }

   public static Supplier<Block> registerBlockAndItem(
      String name, Supplier<Block> block, net.minecraft.world.item.Item.Properties blockItemProps, boolean specialRender
   ) {
      Supplier<Block> blockObj = regBlock(name, block);
      net.minecraft.world.item.Item.Properties props = blockDescriptionId(blockItemProps);
      AMItemRegistry.regItem(name, () -> (AMBlockItem)(specialRender ? new BlockItemAMRender(blockObj, props) : new AMBlockItem(blockObj, props)));
      return blockObj;
   }

   private static net.minecraft.world.item.Item.Properties blockDescriptionId(net.minecraft.world.item.Item.Properties props) {
      return props;
   }

   public static <B extends Block> Supplier<B> regBlock(String name, Supplier<B> sup) {
      return DEF_REG.register(name, sup);
   }
}

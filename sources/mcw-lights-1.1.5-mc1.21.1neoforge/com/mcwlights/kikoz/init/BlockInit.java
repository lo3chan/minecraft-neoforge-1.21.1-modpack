package com.mcwlights.kikoz.init;

import com.mcwlights.kikoz.objects.CeilingLight;
import com.mcwlights.kikoz.objects.ClassicStreet;
import com.mcwlights.kikoz.objects.DoubleStreet;
import com.mcwlights.kikoz.objects.Lamp;
import com.mcwlights.kikoz.objects.LightBaseShort;
import com.mcwlights.kikoz.objects.PaperLamp;
import com.mcwlights.kikoz.objects.SmallWallLamp;
import com.mcwlights.kikoz.objects.TikiTorch;
import com.mcwlights.kikoz.objects.TorchObject;
import com.mcwlights.kikoz.objects.WallLamp;
import com.mcwlights.kikoz.objects.WallLantern;
import com.mcwlights.kikoz.objects.candles.CandleHolder;
import com.mcwlights.kikoz.objects.candles.Chandelier;
import com.mcwlights.kikoz.objects.candles.DoubleCandle;
import com.mcwlights.kikoz.objects.candles.LowCandleHolder;
import com.mcwlights.kikoz.objects.candles.SmallChandelier;
import com.mcwlights.kikoz.objects.candles.TripleCandle;
import com.mcwlights.kikoz.objects.candles.WallCandle;
import java.util.function.ToIntFunction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;

public class BlockInit {
   public static final Blocks BLOCKS = DeferredRegister.createBlocks("mcwlights");
   public static final DeferredBlock<Block> WHITE_LAMP = BLOCKS.register(
      "white_lamp",
      () -> new Lamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> ORANGE_LAMP = BLOCKS.register(
      "orange_lamp",
      () -> new Lamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> MAGENTA_LAMP = BLOCKS.register(
      "magenta_lamp",
      () -> new Lamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> LIGHT_BLUE_LAMP = BLOCKS.register(
      "light_blue_lamp",
      () -> new Lamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> YELLOW_LAMP = BLOCKS.register(
      "yellow_lamp",
      () -> new Lamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> LIME_LAMP = BLOCKS.register(
      "lime_lamp",
      () -> new Lamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> PINK_LAMP = BLOCKS.register(
      "pink_lamp",
      () -> new Lamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> GRAY_LAMP = BLOCKS.register(
      "gray_lamp",
      () -> new Lamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_LAMP = BLOCKS.register(
      "light_gray_lamp",
      () -> new Lamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> CYAN_LAMP = BLOCKS.register(
      "cyan_lamp",
      () -> new Lamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> PURPLE_LAMP = BLOCKS.register(
      "purple_lamp",
      () -> new Lamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> BLUE_LAMP = BLOCKS.register(
      "blue_lamp",
      () -> new Lamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> BROWN_LAMP = BLOCKS.register(
      "brown_lamp",
      () -> new Lamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> GREEN_LAMP = BLOCKS.register(
      "green_lamp",
      () -> new Lamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> RED_LAMP = BLOCKS.register(
      "red_lamp",
      () -> new Lamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> BLACK_LAMP = BLOCKS.register(
      "black_lamp",
      () -> new Lamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> OAK_TIKI_TORCH = BLOCKS.register(
      "oak_tiki_torch",
      () -> new TikiTorch(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion(),
         ParticleTypes.FLAME
      )
   );
   public static final DeferredBlock<Block> SPRUCE_TIKI_TORCH = BLOCKS.register(
      "spruce_tiki_torch",
      () -> new TikiTorch(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion(),
         ParticleTypes.FLAME
      )
   );
   public static final DeferredBlock<Block> BIRCH_TIKI_TORCH = BLOCKS.register(
      "birch_tiki_torch",
      () -> new TikiTorch(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion(),
         ParticleTypes.FLAME
      )
   );
   public static final DeferredBlock<Block> JUNGLE_TIKI_TORCH = BLOCKS.register(
      "jungle_tiki_torch",
      () -> new TikiTorch(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion(),
         ParticleTypes.FLAME
      )
   );
   public static final DeferredBlock<Block> ACACIA_TIKI_TORCH = BLOCKS.register(
      "acacia_tiki_torch",
      () -> new TikiTorch(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion(),
         ParticleTypes.FLAME
      )
   );
   public static final DeferredBlock<Block> DARK_OAK_TIKI_TORCH = BLOCKS.register(
      "dark_oak_tiki_torch",
      () -> new TikiTorch(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion(),
         ParticleTypes.FLAME
      )
   );
   public static final DeferredBlock<Block> CRIMSON_TIKI_TORCH = BLOCKS.register(
      "crimson_tiki_torch",
      () -> new TikiTorch(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion(),
         ParticleTypes.FLAME
      )
   );
   public static final DeferredBlock<Block> WARPED_TIKI_TORCH = BLOCKS.register(
      "warped_tiki_torch",
      () -> new TikiTorch(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion(),
         ParticleTypes.FLAME
      )
   );
   public static final DeferredBlock<Block> MANGROVE_TIKI_TORCH = BLOCKS.register(
      "mangrove_tiki_torch",
      () -> new TikiTorch(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion(),
         ParticleTypes.FLAME
      )
   );
   public static final DeferredBlock<Block> BAMBOO_TIKI_TORCH = BLOCKS.register(
      "bamboo_tiki_torch",
      () -> new TikiTorch(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.COLOR_GREEN).strength(0.5F, 1.0F).sound(SoundType.BAMBOO).noOcclusion(),
         ParticleTypes.FLAME
      )
   );
   public static final DeferredBlock<Block> SOUL_OAK_TIKI_TORCH = BLOCKS.register(
      "soul_oak_tiki_torch",
      () -> new TikiTorch(
         Properties.of().lightLevel(blockOffLightValue(10)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion(),
         ParticleTypes.SOUL_FIRE_FLAME
      )
   );
   public static final DeferredBlock<Block> SOUL_SPRUCE_TIKI_TORCH = BLOCKS.register(
      "soul_spruce_tiki_torch",
      () -> new TikiTorch(
         Properties.of().lightLevel(blockOffLightValue(10)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion(),
         ParticleTypes.SOUL_FIRE_FLAME
      )
   );
   public static final DeferredBlock<Block> SOUL_BIRCH_TIKI_TORCH = BLOCKS.register(
      "soul_birch_tiki_torch",
      () -> new TikiTorch(
         Properties.of().lightLevel(blockOffLightValue(10)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion(),
         ParticleTypes.SOUL_FIRE_FLAME
      )
   );
   public static final DeferredBlock<Block> SOUL_JUNGLE_TIKI_TORCH = BLOCKS.register(
      "soul_jungle_tiki_torch",
      () -> new TikiTorch(
         Properties.of().lightLevel(blockOffLightValue(10)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion(),
         ParticleTypes.SOUL_FIRE_FLAME
      )
   );
   public static final DeferredBlock<Block> SOUL_ACACIA_TIKI_TORCH = BLOCKS.register(
      "soul_acacia_tiki_torch",
      () -> new TikiTorch(
         Properties.of().lightLevel(blockOffLightValue(10)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion(),
         ParticleTypes.SOUL_FIRE_FLAME
      )
   );
   public static final DeferredBlock<Block> SOUL_DARK_OAK_TIKI_TORCH = BLOCKS.register(
      "soul_dark_oak_tiki_torch",
      () -> new TikiTorch(
         Properties.of().lightLevel(blockOffLightValue(10)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion(),
         ParticleTypes.SOUL_FIRE_FLAME
      )
   );
   public static final DeferredBlock<Block> SOUL_CRIMSON_TIKI_TORCH = BLOCKS.register(
      "soul_crimson_tiki_torch",
      () -> new TikiTorch(
         Properties.of().lightLevel(blockOffLightValue(10)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion(),
         ParticleTypes.SOUL_FIRE_FLAME
      )
   );
   public static final DeferredBlock<Block> SOUL_WARPED_TIKI_TORCH = BLOCKS.register(
      "soul_warped_tiki_torch",
      () -> new TikiTorch(
         Properties.of().lightLevel(blockOffLightValue(10)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion(),
         ParticleTypes.SOUL_FIRE_FLAME
      )
   );
   public static final DeferredBlock<Block> SOUL_MANGROVE_TIKI_TORCH = BLOCKS.register(
      "soul_mangrove_tiki_torch",
      () -> new TikiTorch(
         Properties.of().lightLevel(blockOffLightValue(10)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion(),
         ParticleTypes.SOUL_FIRE_FLAME
      )
   );
   public static final DeferredBlock<Block> SOUL_BAMBOO_TIKI_TORCH = BLOCKS.register(
      "soul_bamboo_tiki_torch",
      () -> new TikiTorch(
         Properties.of().lightLevel(blockOffLightValue(10)).mapColor(MapColor.COLOR_GREEN).strength(0.5F, 1.0F).sound(SoundType.BAMBOO).noOcclusion(),
         ParticleTypes.SOUL_FIRE_FLAME
      )
   );
   public static final DeferredBlock<Block> WHITE_CEILING_LIGHT = BLOCKS.register(
      "white_ceiling_light",
      () -> new CeilingLight(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> ORANGE_CEILING_LIGHT = BLOCKS.register(
      "orange_ceiling_light",
      () -> new CeilingLight(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> MAGENTA_CEILING_LIGHT = BLOCKS.register(
      "magenta_ceiling_light",
      () -> new CeilingLight(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> LIGHT_BLUE_CEILING_LIGHT = BLOCKS.register(
      "light_blue_ceiling_light",
      () -> new CeilingLight(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> YELLOW_CEILING_LIGHT = BLOCKS.register(
      "yellow_ceiling_light",
      () -> new CeilingLight(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> LIME_CEILING_LIGHT = BLOCKS.register(
      "lime_ceiling_light",
      () -> new CeilingLight(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> PINK_CEILING_LIGHT = BLOCKS.register(
      "pink_ceiling_light",
      () -> new CeilingLight(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> GRAY_CEILING_LIGHT = BLOCKS.register(
      "gray_ceiling_light",
      () -> new CeilingLight(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_CEILING_LIGHT = BLOCKS.register(
      "light_gray_ceiling_light",
      () -> new CeilingLight(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> CYAN_CEILING_LIGHT = BLOCKS.register(
      "cyan_ceiling_light",
      () -> new CeilingLight(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> PURPLE_CEILING_LIGHT = BLOCKS.register(
      "purple_ceiling_light",
      () -> new CeilingLight(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> BLUE_CEILING_LIGHT = BLOCKS.register(
      "blue_ceiling_light",
      () -> new CeilingLight(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> BROWN_CEILING_LIGHT = BLOCKS.register(
      "brown_ceiling_light",
      () -> new CeilingLight(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> GREEN_CEILING_LIGHT = BLOCKS.register(
      "green_ceiling_light",
      () -> new CeilingLight(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> RED_CEILING_LIGHT = BLOCKS.register(
      "red_ceiling_light",
      () -> new CeilingLight(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> BLACK_CEILING_LIGHT = BLOCKS.register(
      "black_ceiling_light",
      () -> new CeilingLight(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> CLASSIC_STREET_LAMP = BLOCKS.register(
      "classic_street_lamp",
      () -> new ClassicStreet(
         Properties.of().mapColor(MapColor.WOOD).lightLevel(blockOffLightValue(15)).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> DOUBLE_STREET_LAMP = BLOCKS.register(
      "double_street_lamp",
      () -> new DoubleStreet(
         Properties.of().mapColor(MapColor.WOOD).lightLevel(blockOffLightValue(15)).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> SOUL_CLASSIC_STREET_LAMP = BLOCKS.register(
      "soul_classic_street_lamp",
      () -> new ClassicStreet(
         Properties.of().lightLevel(blockOffLightValue(10)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> SOUL_DOUBLE_STREET_LAMP = BLOCKS.register(
      "soul_double_street_lamp",
      () -> new DoubleStreet(
         Properties.of().lightLevel(blockOffLightValue(10)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> LAVA_LAMP = BLOCKS.register(
      "lava_lamp",
      () -> new LightBaseShort(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(1.5F, 2.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> GARDEN_LIGHT = BLOCKS.register(
      "garden_light",
      () -> new LightBaseShort(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(1.5F, 2.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> WHITE_PAPER_LAMP = BLOCKS.register(
      "white_paper_lamp",
      () -> new PaperLamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_PAPER_LAMP = BLOCKS.register(
      "light_gray_paper_lamp",
      () -> new PaperLamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> GRAY_PAPER_LAMP = BLOCKS.register(
      "gray_paper_lamp",
      () -> new PaperLamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> BLACK_PAPER_LAMP = BLOCKS.register(
      "black_paper_lamp",
      () -> new PaperLamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> BROWN_PAPER_LAMP = BLOCKS.register(
      "brown_paper_lamp",
      () -> new PaperLamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> RED_PAPER_LAMP = BLOCKS.register(
      "red_paper_lamp",
      () -> new PaperLamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> ORANGE_PAPER_LAMP = BLOCKS.register(
      "orange_paper_lamp",
      () -> new PaperLamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> YELLOW_PAPER_LAMP = BLOCKS.register(
      "yellow_paper_lamp",
      () -> new PaperLamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> LIME_PAPER_LAMP = BLOCKS.register(
      "lime_paper_lamp",
      () -> new PaperLamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> GREEN_PAPER_LAMP = BLOCKS.register(
      "green_paper_lamp",
      () -> new PaperLamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> CYAN_PAPER_LAMP = BLOCKS.register(
      "cyan_paper_lamp",
      () -> new PaperLamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> LIGHT_BLUE_PAPER_LAMP = BLOCKS.register(
      "light_blue_paper_lamp",
      () -> new PaperLamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> BLUE_PAPER_LAMP = BLOCKS.register(
      "blue_paper_lamp",
      () -> new PaperLamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> PURPLE_PAPER_LAMP = BLOCKS.register(
      "purple_paper_lamp",
      () -> new PaperLamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> MAGENTA_PAPER_LAMP = BLOCKS.register(
      "magenta_paper_lamp",
      () -> new PaperLamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> PINK_PAPER_LAMP = BLOCKS.register(
      "pink_paper_lamp",
      () -> new PaperLamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> STRIPED_LANTERN = BLOCKS.register(
      "striped_lantern",
      () -> new LanternBlock(
         Properties.of().lightLevel(AnotherSpecialLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> COVERED_LANTERN = BLOCKS.register(
      "covered_lantern",
      () -> new LanternBlock(
         Properties.of().lightLevel(AnotherSpecialLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> CHAIN_LANTERN = BLOCKS.register(
      "chain_lantern",
      () -> new LanternBlock(
         Properties.of().lightLevel(AnotherSpecialLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> TAVERN_LANTERN = BLOCKS.register(
      "tavern_lantern",
      () -> new LanternBlock(
         Properties.of().lightLevel(AnotherSpecialLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> FESTIVE_LANTERN = BLOCKS.register(
      "festive_lantern",
      () -> new LanternBlock(
         Properties.of().lightLevel(AnotherSpecialLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> CROSS_LANTERN = BLOCKS.register(
      "cross_lantern",
      () -> new LanternBlock(
         Properties.of().lightLevel(AnotherSpecialLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> BELL_LANTERN = BLOCKS.register(
      "bell_lantern",
      () -> new LanternBlock(
         Properties.of().lightLevel(AnotherSpecialLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> WALL_LANTERN = BLOCKS.register(
      "wall_lantern",
      () -> new WallLantern(
         Properties.of().lightLevel(AnotherSpecialLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> STRIPED_WALL_LANTERN = BLOCKS.register(
      "striped_wall_lantern",
      () -> new WallLantern(
         Properties.of().lightLevel(AnotherSpecialLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> COVERED_WALL_LANTERN = BLOCKS.register(
      "covered_wall_lantern",
      () -> new WallLantern(
         Properties.of().lightLevel(AnotherSpecialLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> CHAIN_WALL_LANTERN = BLOCKS.register(
      "chain_wall_lantern",
      () -> new WallLantern(
         Properties.of().lightLevel(AnotherSpecialLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> TAVERN_WALL_LANTERN = BLOCKS.register(
      "tavern_wall_lantern",
      () -> new WallLantern(
         Properties.of().lightLevel(AnotherSpecialLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> FESTIVE_WALL_LANTERN = BLOCKS.register(
      "festive_wall_lantern",
      () -> new WallLantern(
         Properties.of().lightLevel(AnotherSpecialLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> CROSS_WALL_LANTERN = BLOCKS.register(
      "cross_wall_lantern",
      () -> new WallLantern(
         Properties.of().lightLevel(AnotherSpecialLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> BELL_WALL_LANTERN = BLOCKS.register(
      "bell_wall_lantern",
      () -> new WallLantern(
         Properties.of().lightLevel(AnotherSpecialLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> WALL_LAMP = BLOCKS.register(
      "wall_lamp",
      () -> new SmallWallLamp(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> SQUARE_WALL_LAMP = BLOCKS.register(
      "square_wall_lamp",
      () -> new WallLamp(Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion())
   );
   public static final DeferredBlock<Block> FRAMED_TORCH = BLOCKS.register(
      "framed_torch",
      () -> new TorchObject(
         Properties.of().lightLevel(AnotherSpecialLightValue(15)).mapColor(MapColor.WOOD).noCollission().instabreak().noOcclusion(), ParticleTypes.FLAME
      )
   );
   public static final DeferredBlock<Block> IRON_FRAMED_TORCH = BLOCKS.register(
      "iron_framed_torch",
      () -> new TorchObject(
         Properties.of().lightLevel(AnotherSpecialLightValue(15)).mapColor(MapColor.WOOD).noCollission().instabreak().noOcclusion(), ParticleTypes.FLAME
      )
   );
   public static final DeferredBlock<Block> REINFORCED_TORCH = BLOCKS.register(
      "reinforced_torch",
      () -> new TorchObject(
         Properties.of().lightLevel(AnotherSpecialLightValue(15)).mapColor(MapColor.WOOD).noCollission().instabreak().noOcclusion(), ParticleTypes.FLAME
      )
   );
   public static final DeferredBlock<Block> RUSTIC_TORCH = BLOCKS.register(
      "rustic_torch",
      () -> new TorchObject(
         Properties.of().lightLevel(AnotherSpecialLightValue(15)).mapColor(MapColor.WOOD).noCollission().instabreak().noOcclusion(), ParticleTypes.FLAME
      )
   );
   public static final DeferredBlock<Block> UPGRADED_TORCH = BLOCKS.register(
      "upgraded_torch",
      () -> new TorchObject(
         Properties.of().lightLevel(AnotherSpecialLightValue(15)).mapColor(MapColor.WOOD).noCollission().instabreak().noOcclusion(), ParticleTypes.FLAME
      )
   );
   public static final DeferredBlock<Block> REDSTONE_LAMP_SLAB = BLOCKS.register(
      "redstone_lamp_slab",
      () -> new SlabBlock(
         Properties.of().lightLevel(AnotherSpecialLightValue(15)).mapColor(MapColor.GOLD).strength(0.5F, 1.5F).sound(SoundType.GLASS).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> GLOWSTONE_SLAB = BLOCKS.register(
      "glowstone_slab",
      () -> new SlabBlock(
         Properties.of().lightLevel(AnotherSpecialLightValue(15)).mapColor(MapColor.GOLD).strength(0.5F, 1.5F).sound(SoundType.GLASS).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> SHROOMLIGHT_SLAB = BLOCKS.register(
      "shroomlight_slab",
      () -> new SlabBlock(
         Properties.of().lightLevel(AnotherSpecialLightValue(15)).mapColor(MapColor.GOLD).strength(0.5F, 1.5F).sound(SoundType.SHROOMLIGHT).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> SEA_LANTERN_SLAB = BLOCKS.register(
      "sea_lantern_slab",
      () -> new SlabBlock(
         Properties.of().lightLevel(AnotherSpecialLightValue(15)).mapColor(MapColor.COLOR_CYAN).strength(0.5F, 1.5F).sound(SoundType.GLASS).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> CHERRY_TIKI_TORCH = BLOCKS.register(
      "cherry_tiki_torch",
      () -> new TikiTorch(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.COLOR_GREEN).strength(0.5F, 1.0F).sound(SoundType.BAMBOO).noOcclusion(),
         ParticleTypes.FLAME
      )
   );
   public static final DeferredBlock<Block> SOUL_CHERRY_TIKI_TORCH = BLOCKS.register(
      "soul_cherry_tiki_torch",
      () -> new TikiTorch(
         Properties.of().lightLevel(blockOffLightValue(10)).mapColor(MapColor.COLOR_GREEN).strength(0.5F, 1.0F).sound(SoundType.BAMBOO).noOcclusion(),
         ParticleTypes.SOUL_FIRE_FLAME
      )
   );
   public static final DeferredBlock<Block> OAK_CEILING_FAN_LIGHT = BLOCKS.register(
      "oak_ceiling_fan_light",
      () -> new LightBaseShort(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> SPRUCE_CEILING_FAN_LIGHT = BLOCKS.register(
      "spruce_ceiling_fan_light",
      () -> new LightBaseShort(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> BIRCH_CEILING_FAN_LIGHT = BLOCKS.register(
      "birch_ceiling_fan_light",
      () -> new LightBaseShort(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> JUNGLE_CEILING_FAN_LIGHT = BLOCKS.register(
      "jungle_ceiling_fan_light",
      () -> new LightBaseShort(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> ACACIA_CEILING_FAN_LIGHT = BLOCKS.register(
      "acacia_ceiling_fan_light",
      () -> new LightBaseShort(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> DARK_OAK_CEILING_FAN_LIGHT = BLOCKS.register(
      "dark_oak_ceiling_fan_light",
      () -> new LightBaseShort(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> CRIMSON_CEILING_FAN_LIGHT = BLOCKS.register(
      "crimson_ceiling_fan_light",
      () -> new LightBaseShort(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> WARPED_CEILING_FAN_LIGHT = BLOCKS.register(
      "warped_ceiling_fan_light",
      () -> new LightBaseShort(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> MANGROVE_CEILING_FAN_LIGHT = BLOCKS.register(
      "mangrove_ceiling_fan_light",
      () -> new LightBaseShort(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> CHERRY_CEILING_FAN_LIGHT = BLOCKS.register(
      "cherry_ceiling_fan_light",
      () -> new LightBaseShort(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.WOOD).strength(1.5F, 2.5F).sound(SoundType.WOOD).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> GOLDEN_LOW_CANDLE_HOLDER = BLOCKS.register(
      "golden_low_candle_holder",
      () -> new LowCandleHolder(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(1.5F, 2.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> GOLDEN_CANDLE_HOLDER = BLOCKS.register(
      "golden_candle_holder",
      () -> new CandleHolder(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(1.5F, 2.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> GOLDEN_WALL_CANDLE_HOLDER = BLOCKS.register(
      "golden_wall_candle_holder",
      () -> new WallCandle(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> GOLDEN_DOUBLE_CANDLE_HOLDER = BLOCKS.register(
      "golden_double_candle_holder",
      () -> new DoubleCandle(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> GOLDEN_TRIPLE_CANDLE_HOLDER = BLOCKS.register(
      "golden_triple_candle_holder",
      () -> new TripleCandle(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> GOLDEN_SMALL_CHANDELIER = BLOCKS.register(
      "golden_small_chandelier",
      () -> new SmallChandelier(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(1.5F, 2.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> GOLDEN_CHANDELIER = BLOCKS.register(
      "golden_chandelier",
      () -> new Chandelier(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(1.5F, 2.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> COPPER_LOW_CANDLE_HOLDER = BLOCKS.register(
      "copper_low_candle_holder",
      () -> new LowCandleHolder(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(1.5F, 2.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> COPPER_CANDLE_HOLDER = BLOCKS.register(
      "copper_candle_holder",
      () -> new CandleHolder(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(1.5F, 2.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> COPPER_WALL_CANDLE_HOLDER = BLOCKS.register(
      "copper_wall_candle_holder",
      () -> new WallCandle(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> COPPER_DOUBLE_CANDLE_HOLDER = BLOCKS.register(
      "copper_double_candle_holder",
      () -> new DoubleCandle(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> COPPER_TRIPLE_CANDLE_HOLDER = BLOCKS.register(
      "copper_triple_candle_holder",
      () -> new TripleCandle(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> COPPER_SMALL_CHANDELIER = BLOCKS.register(
      "copper_small_chandelier",
      () -> new SmallChandelier(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(1.5F, 2.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> COPPER_CHANDELIER = BLOCKS.register(
      "copper_chandelier",
      () -> new Chandelier(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(1.5F, 2.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> IRON_LOW_CANDLE_HOLDER = BLOCKS.register(
      "iron_low_candle_holder",
      () -> new LowCandleHolder(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(1.5F, 2.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> IRON_CANDLE_HOLDER = BLOCKS.register(
      "iron_candle_holder",
      () -> new CandleHolder(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(1.5F, 2.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> IRON_WALL_CANDLE_HOLDER = BLOCKS.register(
      "iron_wall_candle_holder",
      () -> new WallCandle(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> IRON_DOUBLE_CANDLE_HOLDER = BLOCKS.register(
      "iron_double_candle_holder",
      () -> new DoubleCandle(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> IRON_TRIPLE_CANDLE_HOLDER = BLOCKS.register(
      "iron_triple_candle_holder",
      () -> new TripleCandle(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(0.5F, 1.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> IRON_SMALL_CHANDELIER = BLOCKS.register(
      "iron_small_chandelier",
      () -> new SmallChandelier(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(1.5F, 2.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> IRON_CHANDELIER = BLOCKS.register(
      "iron_chandelier",
      () -> new Chandelier(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(1.5F, 2.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> GOLDEN_CHAIN = BLOCKS.register(
      "golden_chain",
      () -> new ChainBlock(Properties.of().forceSolidOn().requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.CHAIN).noOcclusion())
   );
   public static final DeferredBlock<Block> COPPER_CHAIN = BLOCKS.register(
      "copper_chain",
      () -> new ChainBlock(Properties.of().forceSolidOn().requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.CHAIN).noOcclusion())
   );
   public static final DeferredBlock<Block> THIN_GARDEN_LIGHT = BLOCKS.register(
      "thin_garden_light",
      () -> new LightBaseShort(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(1.5F, 2.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> TOWER_GARDEN_LIGHT = BLOCKS.register(
      "tower_garden_light",
      () -> new LightBaseShort(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(1.5F, 2.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> COVERED_GARDEN_LIGHT = BLOCKS.register(
      "covered_garden_light",
      () -> new LightBaseShort(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(1.5F, 2.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> ROUND_GARDEN_LIGHT = BLOCKS.register(
      "round_garden_light",
      () -> new LightBaseShort(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(1.5F, 2.5F).sound(SoundType.METAL).noOcclusion()
      )
   );
   public static final DeferredBlock<Block> STRIPED_GARDEN_LIGHT = BLOCKS.register(
      "striped_garden_light",
      () -> new LightBaseShort(
         Properties.of().lightLevel(blockOffLightValue(15)).mapColor(MapColor.METAL).strength(1.5F, 2.5F).sound(SoundType.METAL).noOcclusion()
      )
   );

   private static ToIntFunction<BlockState> blockOffLightValue(int num) {
      return state -> state.getValue(BlockStateProperties.LIT) ? num : 0;
   }

   private static ToIntFunction<BlockState> AnotherSpecialLightValue(int num) {
      return state -> 15;
   }
}

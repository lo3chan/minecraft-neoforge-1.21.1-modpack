package cn.foggyhillside.ends_delight.registry;

import cn.foggyhillside.ends_delight.block.ChorusFruitPieBlock;
import cn.foggyhillside.ends_delight.block.ChorusSucculentBlock;
import cn.foggyhillside.ends_delight.block.DragonEggFeastBlock;
import cn.foggyhillside.ends_delight.block.DragonLegBlock;
import cn.foggyhillside.ends_delight.block.EndStoveBlock;
import cn.foggyhillside.ends_delight.block.GrilledShulkerBlock;
import java.util.function.ToIntFunction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;

public class ModBlocks {
   public static final Blocks BLOCKS = DeferredRegister.createBlocks("ends_delight");
   public static final DeferredBlock<Block> CHORUS_FRUIT_PIE = BLOCKS.register(
      "chorus_fruit_pie", () -> new ChorusFruitPieBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CAKE), ModItems.CHORUS_FRUIT_PIE_SLICE)
   );
   public static final DeferredBlock<Block> CHORUS_SUCCULENT = BLOCKS.register(
      "chorus_succulent",
      () -> new ChorusSucculentBlock(
         Properties.of()
            .strength(0.0F, 0.0F)
            .mapColor(MapColor.COLOR_PURPLE)
            .lightLevel(state -> 1 + 2 * (Integer)state.getValue(ChorusSucculentBlock.SUCCULENT))
            .sound(SoundType.FUNGUS)
            .noOcclusion()
            .pushReaction(PushReaction.DESTROY)
      )
   );
   public static final DeferredBlock<Block> STEAMED_DRAGON_EGG_BLOCK = BLOCKS.register(
      "steamed_dragon_egg_block",
      () -> new DragonEggFeastBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DRAGON_EGG), ModItems.STEAMED_DRAGON_EGG, true)
   );
   public static final DeferredBlock<Block> DRAGON_MEAT_STEW_BLOCK = BLOCKS.register(
      "dragon_meat_stew_block",
      () -> new DragonEggFeastBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DRAGON_EGG), ModItems.DRAGON_MEAT_STEW, true)
   );
   public static final DeferredBlock<Block> GRILLED_SHULKER_BLOCK = BLOCKS.register(
      "grilled_shulker_block",
      () -> new GrilledShulkerBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SHULKER_BOX), ModItems.GRILLED_SHULKER, true)
   );
   public static final DeferredBlock<Block> END_STOVE = BLOCKS.register(
      "end_stove", () -> new EndStoveBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BRICKS).lightLevel(litBlockEmission(13)))
   );
   public static final DeferredBlock<Block> DRAGON_LEG_BLOCK = BLOCKS.register(
      "dragon_leg_with_sauce_block", () -> new DragonLegBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CAKE))
   );
   public static final DeferredBlock<Block> CHORUS_FRUIT_CRATE = BLOCKS.register(
      "chorus_fruit_crate",
      () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD))
   );

   private static ToIntFunction<BlockState> litBlockEmission(int lightValue) {
      return state -> state.getValue(BlockStateProperties.LIT) ? lightValue : 0;
   }
}

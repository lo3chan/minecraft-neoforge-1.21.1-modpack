package vazkii.psi.common.block.base;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockBehaviour.StateArgumentPredicate;
import net.minecraft.world.level.block.state.BlockBehaviour.StatePredicate;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import vazkii.psi.common.block.BlockCADAssembler;
import vazkii.psi.common.block.BlockConjured;
import vazkii.psi.common.block.BlockProgrammer;
import vazkii.psi.common.block.tile.TileCADAssembler;
import vazkii.psi.common.block.tile.TileConjured;
import vazkii.psi.common.block.tile.TileProgrammer;
import vazkii.psi.common.block.tile.container.ContainerCADAssembler;

public class ModBlocks {
   public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, "psi");
   public static final DeferredRegister<BlockEntityType<?>> BLOCK_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "psi");
   public static final DeferredRegister<MenuType<?>> MENU = DeferredRegister.create(Registries.MENU, "psi");
   public static final DeferredHolder<Block, BlockCADAssembler> cadAssembler = BLOCKS.register(
      "cad_assembler",
      () -> new BlockCADAssembler(
         Properties.of()
            .mapColor(MapColor.METAL)
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .requiresCorrectToolForDrops()
            .strength(5.0F, 10.0F)
            .sound(SoundType.METAL)
            .noOcclusion()
      )
   );
   public static final DeferredHolder<Block, BlockProgrammer> programmer = BLOCKS.register(
      "programmer",
      () -> new BlockProgrammer(
         Properties.of()
            .mapColor(MapColor.METAL)
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .requiresCorrectToolForDrops()
            .strength(5.0F, 10.0F)
            .sound(SoundType.METAL)
            .noOcclusion()
      )
   );
   public static final DeferredHolder<Block, Block> psidustBlock = BLOCKS.register(
      "psidust_block",
      () -> new Block(
         Properties.of()
            .mapColor(MapColor.METAL)
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .requiresCorrectToolForDrops()
            .strength(5.0F, 10.0F)
            .sound(SoundType.METAL)
      )
   );
   public static final DeferredHolder<Block, Block> psimetalBlock = BLOCKS.register(
      "psimetal_block",
      () -> new Block(
         Properties.of()
            .mapColor(MapColor.METAL)
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .requiresCorrectToolForDrops()
            .strength(5.0F, 10.0F)
            .sound(SoundType.METAL)
      )
   );
   public static final DeferredHolder<Block, Block> psigemBlock = BLOCKS.register(
      "psigem_block",
      () -> new Block(
         Properties.of()
            .mapColor(MapColor.METAL)
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .requiresCorrectToolForDrops()
            .strength(5.0F, 10.0F)
            .sound(SoundType.METAL)
      )
   );
   public static final DeferredHolder<Block, Block> psimetalPlateBlack = BLOCKS.register(
      "black_psimetal_plate",
      () -> new Block(
         Properties.of()
            .mapColor(MapColor.METAL)
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .requiresCorrectToolForDrops()
            .strength(5.0F, 10.0F)
            .sound(SoundType.METAL)
      )
   );
   public static final DeferredHolder<Block, Block> psimetalPlateBlackLight = BLOCKS.register(
      "lit_black_psimetal_plate",
      () -> new Block(
         Properties.of()
            .mapColor(MapColor.METAL)
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .requiresCorrectToolForDrops()
            .strength(5.0F, 10.0F)
            .sound(SoundType.METAL)
            .lightLevel(blockState -> 15)
      )
   );
   public static final DeferredHolder<Block, Block> psimetalPlateWhite = BLOCKS.register(
      "white_psimetal_plate",
      () -> new Block(
         Properties.of()
            .mapColor(MapColor.METAL)
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .requiresCorrectToolForDrops()
            .strength(5.0F, 10.0F)
            .sound(SoundType.METAL)
      )
   );
   public static final DeferredHolder<Block, Block> psimetalPlateWhiteLight = BLOCKS.register(
      "lit_white_psimetal_plate",
      () -> new Block(
         Properties.of()
            .mapColor(MapColor.METAL)
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .requiresCorrectToolForDrops()
            .strength(5.0F, 10.0F)
            .sound(SoundType.METAL)
            .lightLevel(blockstate -> 15)
      )
   );
   public static final DeferredHolder<Block, Block> psimetalEbony = BLOCKS.register(
      "ebony_psimetal_block",
      () -> new Block(
         Properties.of()
            .mapColor(MapColor.METAL)
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .requiresCorrectToolForDrops()
            .strength(5.0F, 10.0F)
            .sound(SoundType.METAL)
      )
   );
   public static final DeferredHolder<Block, Block> psimetalIvory = BLOCKS.register(
      "ivory_psimetal_block",
      () -> new Block(
         Properties.of()
            .mapColor(MapColor.METAL)
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .requiresCorrectToolForDrops()
            .strength(5.0F, 10.0F)
            .sound(SoundType.METAL)
      )
   );
   public static final DeferredHolder<MenuType<?>, MenuType<ContainerCADAssembler>> containerCADAssembler = MENU.register(
      "cad_assembler", () -> IMenuTypeExtension.create(ContainerCADAssembler::fromNetwork)
   );
   private static final StateArgumentPredicate<EntityType<?>> NO_SPAWN = (state, world, pos, et) -> false;
   private static final StatePredicate NO_SUFFOCATION = (state, world, pos) -> false;
   public static final DeferredHolder<Block, BlockConjured> conjured = BLOCKS.register(
      "conjured",
      () -> new BlockConjured(
         Properties.of()
            .mapColor(MapColor.NONE)
            .instrument(NoteBlockInstrument.HAT)
            .strength(0.3F)
            .sound(SoundType.GLASS)
            .noOcclusion()
            .noLootTable()
            .lightLevel(state -> state.getValue(BlockConjured.LIGHT) ? 15 : 0)
            .noOcclusion()
            .isValidSpawn(NO_SPAWN)
            .isRedstoneConductor(NO_SUFFOCATION)
            .isSuffocating(NO_SUFFOCATION)
            .isViewBlocking(NO_SUFFOCATION)
      )
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileCADAssembler>> cadAssemblerType = BLOCK_TYPES.register(
      "cad_assembler", () -> Builder.of(TileCADAssembler::new, new Block[]{(Block)cadAssembler.get()}).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileProgrammer>> programmerType = BLOCK_TYPES.register(
      "programmer", () -> Builder.of(TileProgrammer::new, new Block[]{(Block)programmer.get()}).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileConjured>> conjuredType = BLOCK_TYPES.register(
      "conjured", () -> Builder.of(TileConjured::new, new Block[]{(Block)conjured.get()}).build(null)
   );
}

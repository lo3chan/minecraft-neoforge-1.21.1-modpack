package io.github.razordevs.deep_aether.init;

import io.github.razordevs.deep_aether.entity.block.CombinerBlockEntity;
import io.github.razordevs.deep_aether.entity.block.DAHangingSignBlockEntity;
import io.github.razordevs.deep_aether.entity.block.DASignBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DABlockEntityTypes {
   public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "deep_aether");
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DASignBlockEntity>> SIGN = BLOCK_ENTITY_TYPES.register(
      "sign",
      () -> Builder.of(
            DASignBlockEntity::new,
            new Block[]{
               (Block)DABlocks.YAGROOT_SIGN.get(),
               (Block)DABlocks.YAGROOT_WALL_SIGN.get(),
               (Block)DABlocks.CRUDEROOT_SIGN.get(),
               (Block)DABlocks.CRUDEROOT_WALL_SIGN.get(),
               (Block)DABlocks.ROSEROOT_SIGN.get(),
               (Block)DABlocks.ROSEROOT_WALL_SIGN.get(),
               (Block)DABlocks.CONBERRY_SIGN.get(),
               (Block)DABlocks.CONBERRY_WALL_SIGN.get(),
               (Block)DABlocks.SUNROOT_SIGN.get(),
               (Block)DABlocks.SUNROOT_WALL_SIGN.get()
            }
         )
         .build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DAHangingSignBlockEntity>> HANGING_SIGN = BLOCK_ENTITY_TYPES.register(
      "hanging_sign",
      () -> Builder.of(
            DAHangingSignBlockEntity::new,
            new Block[]{
               (Block)DABlocks.YAGROOT_WALL_HANGING_SIGN.get(),
               (Block)DABlocks.YAGROOT_HANGING_SIGN.get(),
               (Block)DABlocks.CRUDEROOT_WALL_HANGING_SIGN.get(),
               (Block)DABlocks.CRUDEROOT_HANGING_SIGN.get(),
               (Block)DABlocks.ROSEROOT_WALL_HANGING_SIGN.get(),
               (Block)DABlocks.ROSEROOT_HANGING_SIGN.get(),
               (Block)DABlocks.CONBERRY_WALL_HANGING_SIGN.get(),
               (Block)DABlocks.CONBERRY_HANGING_SIGN.get(),
               (Block)DABlocks.SUNROOT_WALL_HANGING_SIGN.get(),
               (Block)DABlocks.SUNROOT_HANGING_SIGN.get()
            }
         )
         .build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CombinerBlockEntity>> COMBINER = BLOCK_ENTITY_TYPES.register(
      "combiner", () -> Builder.of(CombinerBlockEntity::new, new Block[]{(Block)DABlocks.COMBINER.get()}).build(null)
   );
}

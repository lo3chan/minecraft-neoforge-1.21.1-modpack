package net.blay09.mods.balm.world.level.block.entity;

import java.util.Set;
import java.util.function.Supplier;
import net.blay09.mods.balm.world.level.block.BlockLike;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public interface BalmBlockEntityTypeRegistrar {
   <T extends BlockEntity> BalmBlockEntityTypeRegistration<T> register(String var1, BalmBlockEntityTypeRegistrar.BlockEntitySupplier<T> var2, BlockLike... var3);

   <T extends BlockEntity> BalmBlockEntityTypeRegistration<T> register(
      String var1, BalmBlockEntityTypeRegistrar.BlockEntitySupplier<T> var2, Iterable<? extends BlockLike> var3
   );

   <T extends BlockEntity> BalmBlockEntityTypeRegistration<T> register(
      String var1, BalmBlockEntityTypeRegistrar.BlockEntitySupplier<T> var2, Supplier<Set<Block>> var3
   );

   void addAlias(ResourceLocation var1, ResourceLocation var2);

   void addAlias(String var1, String var2);

   <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BalmBlockEntityTypeRegistrar.BlockEntitySupplier<T> var1, Set<Block> var2);

   @FunctionalInterface
   public interface BlockEntitySupplier<T extends BlockEntity> {
      T create(BlockPos var1, BlockState var2);
   }
}

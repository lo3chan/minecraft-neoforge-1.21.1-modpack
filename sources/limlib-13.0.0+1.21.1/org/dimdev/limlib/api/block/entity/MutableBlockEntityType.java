package org.dimdev.limlib.api.block.entity;

import com.mojang.datafixers.types.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import org.dimdev.limlib.mixin.accessor.BlockEntityTypeAccessor;

public class MutableBlockEntityType<T extends BlockEntity> extends BlockEntityType<T> {
   public MutableBlockEntityType(MutableBlockEntityType.BlockEntityFactory<? extends T> factory, Set<Block> blocks, Type<?> type) {
      super(factory, new HashSet<>(blocks), type);
   }

   public boolean addBlock(Block block) {
      return getBlocks(this).add(block);
   }

   public boolean removeBlock(Block block) {
      return getBlocks(this).remove(block);
   }

   public static Set<Block> getBlocks(BlockEntityType<?> type) {
      return ((BlockEntityTypeAccessor)type).getBlocks();
   }

   @FunctionalInterface
   public interface BlockEntityFactory<T extends BlockEntity> extends BlockEntitySupplier<T> {
   }

   public static final class Builder<T extends BlockEntity> {
      private final MutableBlockEntityType.BlockEntityFactory<? extends T> factory;
      private final Set<Block> blocks;

      private Builder(MutableBlockEntityType.BlockEntityFactory<? extends T> factory, Set<Block> blocks) {
         this.factory = factory;
         this.blocks = blocks;
      }

      public static <T extends BlockEntity> MutableBlockEntityType.Builder<T> create(
         MutableBlockEntityType.BlockEntityFactory<? extends T> factory, Block... blocks
      ) {
         return new MutableBlockEntityType.Builder<>(factory, new HashSet<>(Arrays.asList(blocks)));
      }

      public MutableBlockEntityType<T> build() {
         return this.build(null);
      }

      public MutableBlockEntityType<T> build(Type<?> type) {
         return new MutableBlockEntityType<>(this.factory, this.blocks, type);
      }
   }
}

package net.conczin.immersive_gateways;

import net.conczin.immersive_gateways.block.GatewayBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityTypes {
   public static BlockEntityType<GatewayBlockEntity> GATEWAY;

   public static void register(BlockEntityTypes.TriFunction register) {
      GATEWAY = register.apply(Common.locate("gateway"), GatewayBlockEntity::new, Blocks.GATEWAY);
   }

   public interface BlockEntitySupplier<T extends BlockEntity> {
      T create(BlockPos var1, BlockState var2);
   }

   public interface TriFunction<E extends BlockEntity> {
      BlockEntityType<E> apply(ResourceLocation var1, BlockEntityTypes.BlockEntitySupplier<E> var2, Block var3);
   }
}

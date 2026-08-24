package net.blay09.mods.balm.client.renderer.blockentity;

import java.util.function.Supplier;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface BalmBlockEntityRendererRegistrar {
   <TBlockEntity extends BlockEntity> void register(Holder<BlockEntityType<TBlockEntity>> var1, BlockEntityRendererProvider<? super TBlockEntity> var2);

   <TBlockEntity extends BlockEntity> void register(
      String var1, Supplier<BlockEntityType<TBlockEntity>> var2, BlockEntityRendererProvider<? super TBlockEntity> var3
   );
}

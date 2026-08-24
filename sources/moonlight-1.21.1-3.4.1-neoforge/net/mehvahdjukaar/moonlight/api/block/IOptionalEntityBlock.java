package net.mehvahdjukaar.moonlight.api.block;

import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;

public interface IOptionalEntityBlock {
   boolean shouldHaveBlockEntity(BlockStateBase var1);
}

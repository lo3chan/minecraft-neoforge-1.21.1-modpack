package net.blay09.mods.balm.world.level.block.entity;

import java.util.function.Supplier;
import net.blay09.mods.balm.core.BalmHolderRegistration;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface BalmBlockEntityTypeRegistration<T extends BlockEntity> extends BalmHolderRegistration<BlockEntityType<T>> {
   @Override
   Supplier<BlockEntityType<T>> asSupplier();
}

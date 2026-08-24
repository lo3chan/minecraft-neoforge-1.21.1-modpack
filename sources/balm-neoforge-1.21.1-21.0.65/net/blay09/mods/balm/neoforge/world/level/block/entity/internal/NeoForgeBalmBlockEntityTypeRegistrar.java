package net.blay09.mods.balm.neoforge.world.level.block.entity.internal;

import java.util.Set;
import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityTypeRegistrar;
import net.blay09.mods.balm.world.level.block.entity.internal.AbstractBalmBlockEntityTypeRegistrarImpl;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;

public class NeoForgeBalmBlockEntityTypeRegistrar extends AbstractBalmBlockEntityTypeRegistrarImpl {
   public NeoForgeBalmBlockEntityTypeRegistrar(BalmRegistrar registrar, String namespace) {
      super(registrar, namespace);
   }

   @Override
   public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BalmBlockEntityTypeRegistrar.BlockEntitySupplier<T> constructor, Set<Block> blocks) {
      return Builder.of(constructor::create, blocks.toArray(Block[]::new)).build(null);
   }
}

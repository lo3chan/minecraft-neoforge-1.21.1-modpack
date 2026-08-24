package net.mcreator.undeadrevamp.init;

import net.mcreator.undeadrevamp.block.entity.BasaltechesteTileEntity;
import net.mcreator.undeadrevamp.block.entity.BlacpetalblockBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(
   bus = Bus.MOD
)
public class UndeadRevamp2ModBlockEntities {
   public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "undead_revamp2");
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> BLACPETALBLOCK = register(
      "blacpetalblock", UndeadRevamp2ModBlocks.BLACPETALBLOCK, BlacpetalblockBlockEntity::new
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> BASALTECHESTE = register(
      "basaltecheste", UndeadRevamp2ModBlocks.BASALTECHESTE, BasaltechesteTileEntity::new
   );

   private static DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> register(
      String registryname, DeferredHolder<Block, Block> block, BlockEntitySupplier<?> supplier
   ) {
      return REGISTRY.register(registryname, () -> Builder.of(supplier, new Block[]{(Block)block.get()}).build(null));
   }

   @SubscribeEvent
   public static void registerCapabilities(RegisterCapabilitiesEvent event) {
      event.registerBlockEntity(
         ItemHandler.BLOCK, (BlockEntityType)BLACPETALBLOCK.get(), (blockEntity, side) -> ((BlacpetalblockBlockEntity)blockEntity).getItemHandler()
      );
   }
}

package net.mcreator.borninchaosv.init;

import net.mcreator.borninchaosv.block.entity.DarkIceBlockEntity;
import net.mcreator.borninchaosv.block.entity.FelSoilDestructibleBlockEntity;
import net.mcreator.borninchaosv.block.entity.PuddleofStimulationBlockEntity;
import net.mcreator.borninchaosv.block.entity.PuddleofintoxicationBlockEntity;
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
public class BornInChaosV1ModBlockEntities {
   public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "born_in_chaos_v1");
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> PUDDLEOFINTOXICATION = register(
      "puddleofintoxication", BornInChaosV1ModBlocks.PUDDLEOFINTOXICATION, PuddleofintoxicationBlockEntity::new
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> FEL_SOIL_DESTRUCTIBLE = register(
      "fel_soil_destructible", BornInChaosV1ModBlocks.FEL_SOIL_DESTRUCTIBLE, FelSoilDestructibleBlockEntity::new
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> PUDDLEOF_STIMULATION = register(
      "puddleof_stimulation", BornInChaosV1ModBlocks.PUDDLEOF_STIMULATION, PuddleofStimulationBlockEntity::new
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> DARK_ICE = register(
      "dark_ice", BornInChaosV1ModBlocks.DARK_ICE, DarkIceBlockEntity::new
   );

   private static DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> register(
      String registryname, DeferredHolder<Block, Block> block, BlockEntitySupplier<?> supplier
   ) {
      return REGISTRY.register(registryname, () -> Builder.of(supplier, new Block[]{(Block)block.get()}).build(null));
   }

   @SubscribeEvent
   public static void registerCapabilities(RegisterCapabilitiesEvent event) {
      event.registerBlockEntity(
         ItemHandler.BLOCK, (BlockEntityType)PUDDLEOFINTOXICATION.get(), (blockEntity, side) -> ((PuddleofintoxicationBlockEntity)blockEntity).getItemHandler()
      );
      event.registerBlockEntity(
         ItemHandler.BLOCK, (BlockEntityType)FEL_SOIL_DESTRUCTIBLE.get(), (blockEntity, side) -> ((FelSoilDestructibleBlockEntity)blockEntity).getItemHandler()
      );
      event.registerBlockEntity(
         ItemHandler.BLOCK, (BlockEntityType)PUDDLEOF_STIMULATION.get(), (blockEntity, side) -> ((PuddleofStimulationBlockEntity)blockEntity).getItemHandler()
      );
      event.registerBlockEntity(ItemHandler.BLOCK, (BlockEntityType)DARK_ICE.get(), (blockEntity, side) -> ((DarkIceBlockEntity)blockEntity).getItemHandler());
   }
}

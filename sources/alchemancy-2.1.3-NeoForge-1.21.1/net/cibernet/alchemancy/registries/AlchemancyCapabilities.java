package net.cibernet.alchemancy.registries;

import net.cibernet.alchemancy.blocks.blockentities.ItemStackHolderBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

@EventBusSubscriber(
   bus = Bus.MOD
)
public class AlchemancyCapabilities {
   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void registerCapabilities(RegisterCapabilitiesEvent event) {
      event.registerBlock(
         ItemHandler.BLOCK,
         (level, pos, state, blockEntity, side) -> blockEntity instanceof ItemStackHolderBlockEntity pedestal ? pedestal.wrapper : null,
         new Block[]{(Block)AlchemancyBlocks.INFUSION_PEDESTAL.get(), (Block)AlchemancyBlocks.ALCHEMANCY_FORGE.get()}
      );
      event.registerBlockEntity(
         EnergyStorage.BLOCK,
         (BlockEntityType)AlchemancyBlockEntities.ROOTED_ITEM.get(),
         (blockEntity, direction) -> (IEnergyStorage)blockEntity.getItem().getCapability(EnergyStorage.ITEM)
      );
      Item[] items = BuiltInRegistries.ITEM.stream().toArray(Item[]::new);
      event.registerItem(
         EnergyStorage.ITEM,
         (stack, context) -> InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.BATTERY_POWERED)
            ? new ComponentEnergyStorage(stack, (DataComponentType)AlchemancyItems.Components.FE_STORAGE.get(), 10000)
            : null,
         items
      );
   }
}

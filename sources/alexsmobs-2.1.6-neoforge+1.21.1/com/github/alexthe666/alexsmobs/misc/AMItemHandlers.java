package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

public class AMItemHandlers {
   @Nullable
   public static IItemHandler find(@Nullable BlockEntity blockEntity, @Nullable Direction side) {
      return blockEntity != null && blockEntity.getLevel() != null
         ? (IItemHandler)ItemHandler.BLOCK.getCapability(blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, side)
         : null;
   }

   public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
      event.registerBlockEntity(
         ItemHandler.BLOCK, AMTileEntityRegistry.CAPSID.get(), (capsid, side) -> new SidedInvWrapper(capsid, side == null ? Direction.UP : side)
      );
   }
}

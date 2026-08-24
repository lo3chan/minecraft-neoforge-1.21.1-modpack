package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.util.KubeResourceLocation;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jetbrains.annotations.Nullable;

public interface BlockEntityAttachmentHandler {
   @HideFromJS
   void attach(String id, BlockEntityAttachmentType type, Set<Direction> directions, BlockEntityAttachmentFactory factory);

   default void attach(Context cx, String id, KubeResourceLocation type, Set<Direction> directions, Object args) {
      BlockEntityAttachmentType att = BlockEntityAttachmentType.ALL.get().get(type.wrapped());
      if (att != null) {
         try {
            this.attach(id, att, directions, (BlockEntityAttachmentFactory)cx.jsToJava(args, att.typeInfo()));
         } catch (Exception var8) {
            ConsoleJS.STARTUP.error("Error while creating BlockEntity attachment '" + type + "'", var8);
         }
      } else {
         ConsoleJS.STARTUP.error("BlockEntity attachment '" + type + "' not found!");
      }
   }

   default void attachCustomCapability(String id, Set<Direction> directions, BlockCapability<?, ?> capability, Supplier<?> dataFactory) {
      this.attach(id, CustomCapabilityAttachment.TYPE, directions, new CustomCapabilityAttachment.Factory(capability, dataFactory));
   }

   default void inventory(String id, Set<Direction> directions, int width, int height, @Nullable ItemPredicate inputFilter) {
      this.attach(id, InventoryAttachment.TYPE, directions, new InventoryAttachment.Factory(width, height, Optional.ofNullable(inputFilter)));
   }

   default void inventory(String id, Set<Direction> directions, int width, int height) {
      this.inventory(id, directions, width, height, null);
   }

   default void fluidTank(String id, Set<Direction> directions, int capacity, @Nullable FluidIngredient inputFilter) {
      this.attach(id, FluidTankAttachment.TYPE, directions, new FluidTankAttachment.Factory(capacity, Optional.ofNullable(inputFilter)));
   }

   default void fluidTank(String id, Set<Direction> directions, int capacity) {
      this.fluidTank(id, directions, capacity, null);
   }

   default void energyStorage(String id, Set<Direction> directions, int capacity, int maxReceive, int maxExtract, int autoOutput) {
      this.attach(
         id,
         EnergyStorageAttachment.TYPE,
         directions,
         new EnergyStorageAttachment.Factory(
            capacity,
            maxReceive <= 0 ? Optional.empty() : Optional.of(maxReceive),
            maxExtract <= 0 ? Optional.empty() : Optional.of(maxExtract),
            autoOutput <= 0 ? Optional.empty() : Optional.of(autoOutput)
         )
      );
   }
}

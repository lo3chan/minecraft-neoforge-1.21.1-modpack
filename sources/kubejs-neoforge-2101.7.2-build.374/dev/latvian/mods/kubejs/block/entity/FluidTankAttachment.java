package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.KubeJS;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jetbrains.annotations.Nullable;

public class FluidTankAttachment implements BlockEntityAttachment {
   public static final BlockEntityAttachmentType TYPE = new BlockEntityAttachmentType(KubeJS.id("fluid_tank"), FluidTankAttachment.Factory.class);
   public final KubeBlockEntity entity;
   public final FluidTankAttachment.Wrapped fluidTank;

   public FluidTankAttachment(KubeBlockEntity entity, int capacity, Predicate<FluidStack> filter) {
      this.entity = entity;
      this.fluidTank = new FluidTankAttachment.Wrapped(this, capacity, filter);
   }

   @Override
   public Object getWrappedObject() {
      return this.fluidTank;
   }

   @Nullable
   @Override
   public <CAP, SRC> CAP getCapability(BlockCapability<CAP, SRC> capability) {
      return (CAP)(capability == FluidHandler.BLOCK ? this.fluidTank : null);
   }

   @Nullable
   @Override
   public Tag serialize(Provider registries) {
      return this.fluidTank.getFluid().isEmpty() ? null : this.fluidTank.getFluid().save(registries);
   }

   @Override
   public void deserialize(Provider registries, @Nullable Tag tag) {
      this.fluidTank.setFluid(tag == null ? FluidStack.EMPTY : FluidStack.parse(registries, tag).orElse(FluidStack.EMPTY));
   }

   public record Factory(int capacity, Optional<FluidIngredient> inputFilter) implements BlockEntityAttachmentFactory {
      private static final Predicate<FluidStack> ALWAYS_TRUE = stack -> true;

      @Override
      public BlockEntityAttachment create(BlockEntityAttachmentInfo info, KubeBlockEntity entity) {
         return new FluidTankAttachment(entity, this.capacity, this.inputFilter.isEmpty() ? ALWAYS_TRUE : (Predicate)this.inputFilter.get());
      }

      @Override
      public List<BlockCapability<?, ?>> getCapabilities() {
         return List.of(FluidHandler.BLOCK);
      }
   }

   public static class Wrapped extends FluidTank {
      private final FluidTankAttachment attachment;

      public Wrapped(FluidTankAttachment attachment, int capacity, Predicate<FluidStack> inputFilter) {
         super(capacity, inputFilter);
         this.attachment = attachment;
      }

      protected void onContentsChanged() {
         this.attachment.entity.save();
      }
   }
}

package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.DirectionWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class EnergyStorageAttachment implements BlockEntityAttachment {
   public static final BlockEntityAttachmentType TYPE = new BlockEntityAttachmentType(KubeJS.id("energy_storage"), EnergyStorageAttachment.Factory.class);
   private final KubeBlockEntity entity;
   public final EnergyStorageAttachment.Wrapped energyStorage;
   public final int autoOutput;
   public final Direction[] autoOutputDirections;

   public EnergyStorageAttachment(KubeBlockEntity entity, int capacity, int maxReceive, int maxExtract, int autoOutput, Direction[] autoOutputDirections) {
      this.entity = entity;
      this.energyStorage = new EnergyStorageAttachment.Wrapped(this, capacity, maxReceive, maxExtract);
      this.autoOutput = autoOutput;
      this.autoOutputDirections = autoOutputDirections;
   }

   @Override
   public Object getWrappedObject() {
      return this.energyStorage;
   }

   @Nullable
   @Override
   public <CAP, SRC> CAP getCapability(BlockCapability<CAP, SRC> capability) {
      return (CAP)(capability == EnergyStorage.BLOCK ? this.energyStorage : null);
   }

   @Override
   public void serverTick() {
      if (this.autoOutputDirections.length > 0 && this.autoOutput > 0) {
         ArrayList<IEnergyStorage> list = new ArrayList<>(1);

         for (Direction dir : this.autoOutputDirections) {
            IEnergyStorage c = (IEnergyStorage)this.entity
               .getLevel()
               .getCapability(EnergyStorage.BLOCK, this.entity.getBlockPos().relative(dir), dir.getOpposite());
            if (c != null && c != this.energyStorage) {
               list.add(c);
            }
         }

         if (!list.isEmpty()) {
            int draw = Math.min(this.autoOutput, this.energyStorage.getEnergyStored()) / list.size();
            if (draw > 0) {
               for (IEnergyStorage c : list) {
                  int e = this.energyStorage.extractEnergy(draw, true);
                  if (e <= 0) {
                     break;
                  }

                  this.energyStorage.extractEnergy(c.receiveEnergy(e, false), false);
               }
            }
         }
      }
   }

   public record Factory(int capacity, Optional<Integer> maxReceive, Optional<Integer> maxExtract, Optional<Integer> autoOutput)
      implements BlockEntityAttachmentFactory {
      @Override
      public BlockEntityAttachment create(BlockEntityAttachmentInfo info, KubeBlockEntity entity) {
         int rx = Math.max(0, this.maxReceive.orElse(0));
         int tx = Math.max(0, this.maxExtract.orElse(0));
         int auto = Math.max(0, this.autoOutput.orElse(0));
         return new EnergyStorageAttachment(
            entity,
            this.capacity,
            rx,
            tx,
            auto,
            auto > 0 ? (info.directions().isEmpty() ? DirectionWrapper.VALUES : info.directions().toArray(new Direction[0])) : DirectionWrapper.NONE
         );
      }

      @Override
      public boolean isTicking() {
         return this.autoOutput.isPresent() && this.autoOutput.get() > 0;
      }

      @Override
      public List<BlockCapability<?, ?>> getCapabilities() {
         return List.of(EnergyStorage.BLOCK);
      }
   }

   public static class Wrapped extends net.neoforged.neoforge.energy.EnergyStorage {
      private final EnergyStorageAttachment attachment;

      public Wrapped(EnergyStorageAttachment attachment, int capacity, int maxReceive, int maxExtract) {
         super(capacity, maxReceive, maxExtract);
         this.attachment = attachment;
      }

      public void setEnergyStored(int energy) {
         this.energy = Mth.clamp(energy, 0, this.capacity);
      }

      public int addEnergy(int add, boolean simulate) {
         int i = Mth.clamp(this.capacity - this.energy, 0, add);
         if (!simulate && i > 0) {
            this.energy += i;
            this.attachment.entity.save();
         }

         return i;
      }

      public int removeEnergy(int remove, boolean simulate) {
         int i = Math.max(this.energy, remove);
         if (!simulate && i > 0) {
            this.energy -= i;
            this.attachment.entity.save();
         }

         return i;
      }

      public boolean useEnergy(int use, boolean simulate) {
         if (this.energy >= use) {
            if (!simulate) {
               this.energy -= use;
               this.attachment.entity.save();
            }

            return true;
         } else {
            return false;
         }
      }

      public int extractEnergy(int toExtract, boolean simulate) {
         int s = super.extractEnergy(toExtract, simulate);
         if (s > 0 && !simulate && !this.attachment.entity.getLevel().isClientSide()) {
            this.attachment.entity.save();
         }

         return s;
      }

      public int receiveEnergy(int toReceive, boolean simulate) {
         int s = super.receiveEnergy(toReceive, simulate);
         if (s > 0 && !simulate && !this.attachment.entity.getLevel().isClientSide()) {
            this.attachment.entity.save();
         }

         return s;
      }
   }
}

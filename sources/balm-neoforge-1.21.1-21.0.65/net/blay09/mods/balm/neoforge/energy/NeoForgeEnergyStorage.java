package net.blay09.mods.balm.neoforge.energy;

import net.blay09.mods.balm.api.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class NeoForgeEnergyStorage implements IEnergyStorage {
   private final EnergyStorage energyStorage;

   public NeoForgeEnergyStorage(EnergyStorage energyStorage) {
      this.energyStorage = energyStorage;
   }

   public int receiveEnergy(int maxReceive, boolean simulate) {
      return this.energyStorage.fill(maxReceive, simulate);
   }

   public int extractEnergy(int maxExtract, boolean simulate) {
      return this.energyStorage.drain(maxExtract, simulate);
   }

   public int getEnergyStored() {
      return this.energyStorage.getEnergy();
   }

   public int getMaxEnergyStored() {
      return this.energyStorage.getCapacity();
   }

   public boolean canExtract() {
      return this.energyStorage.canDrain();
   }

   public boolean canReceive() {
      return this.energyStorage.canFill();
   }
}

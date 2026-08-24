package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.minecraft.nbt.CompoundTag;

public abstract class RotationDataProperty extends Property implements IDataHolder<WayfindingProperty.RotationData> {
   public WayfindingProperty.RotationData readData(CompoundTag tag) {
      return WayfindingProperty.RotationData.fromNbt(tag);
   }

   public CompoundTag writeData(WayfindingProperty.RotationData data) {
      return data.toNbt();
   }

   public WayfindingProperty.RotationData getDefaultData() {
      return WayfindingProperty.RotationData.DEFAULT;
   }
}

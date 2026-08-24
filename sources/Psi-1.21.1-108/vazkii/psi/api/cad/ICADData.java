package vazkii.psi.api.cad;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import vazkii.psi.api.internal.Vector3;

public interface ICADData extends INBTSerializable<CompoundTag> {
   int getTime();

   void setTime(int var1);

   int getBattery();

   void setBattery(int var1);

   Vector3 getSavedVector(int var1);

   void setSavedVector(int var1, Vector3 var2);

   CompoundTag serializeForSynchronization();
}

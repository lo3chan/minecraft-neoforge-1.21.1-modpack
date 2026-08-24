package com.github.alexthe666.alexsmobs.misc;

import net.minecraft.nbt.CompoundTag;

public interface IEntitySaveDataAccessor {
   void am_writeSaveData(CompoundTag var1);

   void am_readSaveData(CompoundTag var1);
}

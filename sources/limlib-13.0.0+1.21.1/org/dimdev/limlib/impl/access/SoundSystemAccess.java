package org.dimdev.limlib.impl.access;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Nullable;

public interface SoundSystemAccess {
   void stopSoundsAtPosition(double var1, double var3, double var5, @Nullable ResourceLocation var7, @Nullable SoundSource var8);

   static SoundSystemAccess get(Object obj) {
      return (SoundSystemAccess)obj;
   }
}

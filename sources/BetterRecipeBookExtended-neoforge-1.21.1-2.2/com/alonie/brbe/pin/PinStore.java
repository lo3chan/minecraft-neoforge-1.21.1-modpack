package com.alonie.brbe.pin;

import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public interface PinStore {
   Set<ResourceLocation> load();

   void save(Set<ResourceLocation> var1);
}

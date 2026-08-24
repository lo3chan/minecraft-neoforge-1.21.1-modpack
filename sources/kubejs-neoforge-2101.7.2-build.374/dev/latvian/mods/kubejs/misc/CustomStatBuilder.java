package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import net.minecraft.resources.ResourceLocation;

public class CustomStatBuilder extends BuilderBase<ResourceLocation> {
   public CustomStatBuilder(ResourceLocation i) {
      super(i);
   }

   public ResourceLocation createObject() {
      return this.id;
   }
}

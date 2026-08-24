package dev.latvian.mods.kubejs.registry;

import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public class CustomBuilderObject extends BuilderBase {
   private final Supplier<Object> object;

   public CustomBuilderObject(ResourceLocation i, Supplier<Object> object) {
      super(i);
      this.object = object;
   }

   @Override
   public Object createObject() {
      return this.object.get();
   }
}

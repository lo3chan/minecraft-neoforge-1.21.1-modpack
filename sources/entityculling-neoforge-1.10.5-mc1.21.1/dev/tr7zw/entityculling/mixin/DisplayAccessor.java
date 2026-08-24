package dev.tr7zw.entityculling.mixin;

import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({Display.class})
public interface DisplayAccessor {
   @Invoker("setWidth")
   void invokeSetWidth(float var1);

   @Invoker("setHeight")
   void invokeSetHeight(float var1);
}

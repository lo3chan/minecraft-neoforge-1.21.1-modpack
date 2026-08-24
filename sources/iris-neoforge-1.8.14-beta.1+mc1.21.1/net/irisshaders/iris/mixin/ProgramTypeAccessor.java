package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.shaders.Program.Type;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({Type.class})
public interface ProgramTypeAccessor {
   @Invoker("<init>")
   static Type createProgramType(String name, int ordinal, String typeName, String extension, int glId) {
      throw new AssertionError();
   }
}

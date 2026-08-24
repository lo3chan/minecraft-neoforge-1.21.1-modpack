package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.shaders.Program.Type;
import net.irisshaders.iris.gl.program.IrisProgramTypes;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({Type.class})
public class MixinProgramType {
   @Shadow
   @Final
   @Mutable
   private static Type[] $VALUES = (Type[])ArrayUtils.addAll(
      MixinProgramType.$VALUES, new Type[]{IrisProgramTypes.GEOMETRY, IrisProgramTypes.TESS_CONTROL, IrisProgramTypes.TESS_EVAL}
   );

   static {
      int baseOrdinal = $VALUES.length;
      IrisProgramTypes.GEOMETRY = ProgramTypeAccessor.createProgramType("GEOMETRY", baseOrdinal, "geometry", ".gsh", 36313);
      IrisProgramTypes.TESS_CONTROL = ProgramTypeAccessor.createProgramType("TESS_CONTROL", baseOrdinal + 1, "tess_control", ".tcs", 36488);
      IrisProgramTypes.TESS_EVAL = ProgramTypeAccessor.createProgramType("TESS_EVAL", baseOrdinal + 2, "tess_eval", ".tes", 36487);
   }
}

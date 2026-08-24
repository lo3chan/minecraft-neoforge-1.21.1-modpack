package dev.worldgen.lithostitched.mixin.common;

import java.util.List;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({StructureProcessorList.class})
public interface StructureProcessorListAccessor {
   @Accessor("list")
   @Mutable
   void setProcessors(List<StructureProcessor> var1);
}

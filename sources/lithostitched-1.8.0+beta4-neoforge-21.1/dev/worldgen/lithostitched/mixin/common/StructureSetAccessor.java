package dev.worldgen.lithostitched.mixin.common;

import java.util.List;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureSet.StructureSelectionEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({StructureSet.class})
public interface StructureSetAccessor {
   @Accessor("structures")
   @Mutable
   void setStructures(List<StructureSelectionEntry> var1);
}

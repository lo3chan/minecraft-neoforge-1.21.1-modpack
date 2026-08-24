package com.finndog.moogs_structures.mixins.structures;

import java.util.List;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.Palette;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({StructureTemplate.class})
public interface TemplateAccessor {
   @Accessor("palettes")
   List<Palette> moogs_structures_getPalettes();
}

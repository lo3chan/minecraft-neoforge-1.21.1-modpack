package dev.worldgen.lithostitched.mixin.common;

import com.mojang.datafixers.util.Pair;
import dev.worldgen.lithostitched.duck.StructurePoolAccess;
import dev.worldgen.lithostitched.worldgen.structure.LithostitchedTemplates;
import java.util.List;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin({StructureTemplatePool.class})
public class StructureTemplatePoolMixin implements StructurePoolAccess {
   @Shadow
   @Final
   private List<Pair<StructurePoolElement, Integer>> rawTemplates;
   @Unique
   private LithostitchedTemplates lithostitchedTemplates = new LithostitchedTemplates();

   @Override
   public LithostitchedTemplates getLithostitchedTemplates() {
      return this.lithostitchedTemplates;
   }

   @Override
   public void compileRawTemplates() {
      this.rawTemplates.forEach(pair -> this.lithostitchedTemplates.add((StructurePoolElement)pair.getFirst(), (Integer)pair.getSecond()));
   }
}

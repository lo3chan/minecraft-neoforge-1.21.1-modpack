package dev.worldgen.lithostitched.mixin.common;

import java.util.List;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({JigsawStructure.class})
public interface JigsawStructureAccessor {
   @Accessor("poolAliases")
   @Mutable
   void setPoolAliases(List<PoolAliasBinding> var1);

   @Accessor("poolAliases")
   List<PoolAliasBinding> getPoolAliases();
}

package dev.worldgen.lithostitched.mixin.common.mnbs;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.world.level.biome.Climate.ParameterList;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import net.minecraft.world.level.biome.Climate.RTree;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ParameterList.class})
public interface ParameterListAccessor<T> {
   @Accessor("values")
   @Mutable
   @Final
   void lithostitched$setValues(List<Pair<ParameterPoint, T>> var1);

   @Accessor("index")
   @Mutable
   @Final
   void lithostitched$index(RTree<T> var1);
}

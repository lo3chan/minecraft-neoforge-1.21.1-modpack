package net.mehvahdjukaar.moonlight.core.misc;

import java.util.List;
import java.util.Optional;
import net.mehvahdjukaar.moonlight.api.map.CustomMapData;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapDecoration;
import net.minecraft.resources.ResourceLocation;

public interface IMapDataPacketExtension {
   Optional<List<CustomMapData.DirtyDataPatch<?, ?>>> moonlight$getDirtyCustomData();

   Optional<List<MLMapDecoration>> moonlight$getCustomDecorations();

   ResourceLocation moonlight$getDimension();

   int moonlight$getMapCenterX();

   int moonlight$getMapCenterZ();

   void moonlight$setDimension(ResourceLocation var1);

   void moonlight$setMapCenter(int var1, int var2);

   void moonlight$setCustomDecorations(Optional<List<MLMapDecoration>> var1);

   void moonlight$setDirtyCustomData(Optional<List<CustomMapData.DirtyDataPatch<?, ?>>> var1);
}

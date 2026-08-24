package net.mehvahdjukaar.moonlight.api.map;

import java.util.Map;
import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapDecoration;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapMarker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface ExpandedMapData {
   @Internal
   Map<String, MLMapDecoration> ml$getCustomDecorations();

   @Internal
   Map<String, MLMapMarker<?>> ml$getCustomMarkers();

   @Internal
   Map<CustomMapData.Type<?, ?>, CustomMapData<?, ?>> ml$getCustomData();

   boolean ml$toggleCustomDecoration(LevelAccessor var1, BlockPos var2);

   void ml$resetCustomDecoration();

   int ml$getVanillaDecorationSize();

   <M extends MLMapMarker<?>> void ml$addCustomMarker(M var1);

   boolean ml$removeCustomMarker(String var1);

   MapItemSavedData ml$copy();

   void ml$setCustomDecorationsDirty();

   <H extends CustomMapData.DirtyCounter> void ml$setCustomDataDirty(CustomMapData.Type<?, ?> var1, Consumer<H> var2);
}

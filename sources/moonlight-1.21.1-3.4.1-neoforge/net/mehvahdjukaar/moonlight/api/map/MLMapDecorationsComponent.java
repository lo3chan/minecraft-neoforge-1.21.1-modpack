package net.mehvahdjukaar.moonlight.api.map;

import com.mojang.serialization.Codec;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapMarker;
import net.minecraft.Util;

public record MLMapDecorationsComponent(Map<String, MLMapMarker<?>> decorations) {
   public static final Codec<MLMapDecorationsComponent> CODEC = Codec.unboundedMap(Codec.STRING, MLMapMarker.CODEC)
      .xmap(MLMapDecorationsComponent::new, d -> d.decorations);
   public static final MLMapDecorationsComponent EMPTY = new MLMapDecorationsComponent(Map.of());

   public MLMapDecorationsComponent copyAndAdd(MLMapMarker<?> marker) {
      return new MLMapDecorationsComponent(Util.copyAndPut(this.decorations, marker.getMarkerUniqueId(), marker));
   }

   public void addToMapIfAbsent(Set<String> strings, ExpandedMapData mapDataMixin) {
      for (Entry<String, MLMapMarker<?>> d : this.decorations.entrySet()) {
         if (!strings.contains(d.getKey())) {
            mapDataMixin.ml$addCustomMarker(d.getValue());
         }
      }
   }
}

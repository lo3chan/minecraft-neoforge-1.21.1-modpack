package net.mehvahdjukaar.moonlight.core.worldgen;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.DataResult.Error;
import java.util.stream.Stream;
import net.mehvahdjukaar.moonlight.api.worldgen.ISpawnBoxStructure;
import net.mehvahdjukaar.moonlight.api.worldgen.SpawnBoxSettings;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

public class JigsawCodecWithExtra extends MapCodec<JigsawStructure> {
   private static final String BOX_KEY = "spawn_boxes";
   private static final MapCodec<SpawnBoxSettings> BOX_CODEC = SpawnBoxSettings.CODEC.optionalFieldOf("spawn_boxes", SpawnBoxSettings.EMPTY);
   private final MapCodec<JigsawStructure> original;

   public JigsawCodecWithExtra(MapCodec<JigsawStructure> original) {
      this.original = original;
   }

   public <T> Stream<T> keys(DynamicOps<T> dynamicOps) {
      return Stream.concat(this.original.keys(dynamicOps), Stream.of((T)dynamicOps.createString("spawn_boxes")));
   }

   public <T> DataResult<JigsawStructure> decode(DynamicOps<T> dynamicOps, MapLike<T> mapLike) {
      DataResult<JigsawStructure> result = this.original.decode(dynamicOps, mapLike);
      DataResult<SpawnBoxSettings> boxResult = BOX_CODEC.decode(dynamicOps, mapLike);
      if (boxResult.isError()) {
         return DataResult.error(() -> ((Error)boxResult.error().get()).message());
      } else {
         if (result.isSuccess()) {
            Structure value = (Structure)result.getOrThrow();
            if (value instanceof ISpawnBoxStructure sb) {
               sb.ml$setSpawnBoxSettings((SpawnBoxSettings)boxResult.getOrThrow());
            }
         }

         return result;
      }
   }

   public <T> RecordBuilder<T> encode(JigsawStructure jigsawStructure, DynamicOps<T> dynamicOps, RecordBuilder<T> recordBuilder) {
      RecordBuilder<T> rb = this.original.encode(jigsawStructure, dynamicOps, recordBuilder);
      if (jigsawStructure instanceof ISpawnBoxStructure sb) {
         SpawnBoxSettings s = sb.ml$getSpawnBoxSettings();
         if (!s.isEmpty()) {
            rb = BOX_CODEC.encode(s, dynamicOps, rb);
         }
      }

      return rb;
   }
}

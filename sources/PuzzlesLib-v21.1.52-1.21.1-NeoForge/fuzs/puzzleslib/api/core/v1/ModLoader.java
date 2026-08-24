package fuzs.puzzleslib.api.core.v1;

import java.util.Arrays;
import java.util.stream.Stream;

public enum ModLoader {
   FABRIC,
   NEOFORGE,
   FORGE,
   QUILT;

   private static final ModLoader[] VALUES = values();
   private static final ModLoader[] FABRIC_LIKE = Stream.of(VALUES).filter(ModLoader::isFabricLike).toArray(ModLoader[]::new);
   private static final ModLoader[] FORGE_LIKE = Stream.of(VALUES).filter(ModLoader::isForgeLike).toArray(ModLoader[]::new);

   public static ModLoader[] getFabricLike() {
      return Arrays.copyOf(FABRIC_LIKE, FABRIC_LIKE.length);
   }

   public static ModLoader[] getForgeLike() {
      return Arrays.copyOf(FORGE_LIKE, FORGE_LIKE.length);
   }

   public boolean isFabric() {
      return this == FABRIC;
   }

   public boolean isNeoForge() {
      return this == NEOFORGE;
   }

   public boolean isForge() {
      return this == FORGE;
   }

   public boolean isQuilt() {
      return this == QUILT;
   }

   public boolean isFabricLike() {
      return this.isFabric() || this.isQuilt();
   }

   public boolean isForgeLike() {
      return this.isNeoForge() || this.isForge();
   }
}

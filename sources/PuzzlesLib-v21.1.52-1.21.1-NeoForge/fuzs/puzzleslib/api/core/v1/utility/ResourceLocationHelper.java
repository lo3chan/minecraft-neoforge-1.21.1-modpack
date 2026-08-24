package fuzs.puzzleslib.api.core.v1.utility;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@Deprecated
public final class ResourceLocationHelper {
   private ResourceLocationHelper() {
   }

   public static ResourceLocation fromNamespaceAndPath(String namespace, String path) {
      return ResourceLocation.fromNamespaceAndPath(namespace, path);
   }

   public static ResourceLocation withDefaultNamespace(String path) {
      return ResourceLocation.withDefaultNamespace(path);
   }

   public static ResourceLocation parse(String location) {
      return ResourceLocation.parse(location);
   }

   @Nullable
   public static ResourceLocation tryParse(String location) {
      return ResourceLocation.tryParse(location);
   }
}

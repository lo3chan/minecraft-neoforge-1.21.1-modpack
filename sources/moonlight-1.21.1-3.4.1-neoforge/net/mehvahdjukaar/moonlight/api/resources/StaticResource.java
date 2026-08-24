package net.mehvahdjukaar.moonlight.api.resources;

import com.google.gson.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

public class StaticResource {
   public final byte[] data;
   public final ResourceLocation location;
   public final String sourceName;
   private String dataAsString = null;

   private StaticResource(byte[] data, ResourceLocation location, String sourceName) {
      this.data = data;
      this.location = location;
      this.sourceName = sourceName;
   }

   public static StaticResource of(Resource original, ResourceLocation location) {
      byte[] data1 = new byte[0];

      try (InputStream stream = original.open()) {
         try {
            data1 = stream.readAllBytes();
         } catch (IOException var7) {
            Moonlight.LOGGER.error("Could not parse resource: {}", location);
         }
      } catch (Exception var9) {
      }

      return new StaticResource(data1, location, original.sourcePackId());
   }

   public static StaticResource create(byte[] data, ResourceLocation location) {
      return new StaticResource(data, location, location.toString());
   }

   @Nullable
   public static StaticResource getOrLog(ResourceManager manager, ResourceLocation location) {
      try {
         return of((Resource)manager.getResource(location).get(), location);
      } catch (Exception var3) {
         Moonlight.LOGGER.error("Could not find resource {}", location);
         return null;
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public static StaticResource getOrFail(ResourceManager manager, ResourceLocation location) throws NoSuchElementException {
      return of((Resource)manager.getResource(location).orElseThrow(), location);
   }

   public static StaticResource getOrThrow(ResourceManager manager, ResourceLocation location) throws NoSuchElementException {
      return of((Resource)manager.getResource(location).orElseThrow(), location);
   }

   public JsonObject toJson() {
      try {
         JsonObject var2;
         try (ByteArrayInputStream s = new ByteArrayInputStream(this.data)) {
            var2 = RPUtils.deserializeJson(s);
         }

         return var2;
      } catch (Exception var6) {
         throw new RuntimeException(var6);
      }
   }

   public String asString() {
      if (this.dataAsString == null) {
         this.dataAsString = new String(this.data, StandardCharsets.UTF_8);
      }

      return this.dataAsString;
   }
}

package software.bernie.geckolib.loading;

import com.google.gson.JsonObject;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.Charset;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.loading.json.raw.Model;
import software.bernie.geckolib.loading.json.typeadapter.KeyFramesAdapter;
import software.bernie.geckolib.loading.object.BakedAnimations;

public final class FileLoader {
   public static BakedAnimations loadAnimationsFile(ResourceLocation location, ResourceManager manager) {
      if (location.getPath().endsWith(".geo.json")) {
         throw new IllegalArgumentException("Geo model file found in animations folder!");
      } else {
         if (!location.getPath().endsWith(".animation.json")) {
            GeckoLibConstants.LOGGER
               .warn("Found animation file with improper file name format; animation files should end in .animation.json: '" + location + "'");
         }

         return (BakedAnimations)KeyFramesAdapter.GEO_GSON
            .fromJson(GsonHelper.getAsJsonObject(loadFile(location, manager), "animations"), BakedAnimations.class);
      }
   }

   public static Model loadModelFile(ResourceLocation location, ResourceManager manager) {
      if (location.getPath().endsWith(".animation.json")) {
         throw new IllegalArgumentException("Animation file found in geo models folder!");
      } else {
         if (!location.getPath().endsWith(".geo.json")) {
            GeckoLibConstants.LOGGER.warn("Found geo model file with improper file name format; geo model files should end in .geo.json: '" + location + "'");
         }

         return (Model)KeyFramesAdapter.GEO_GSON.fromJson(loadFile(location, manager), Model.class);
      }
   }

   public static JsonObject loadFile(ResourceLocation location, ResourceManager manager) {
      return (JsonObject)GsonHelper.fromJson(KeyFramesAdapter.GEO_GSON, getFileContents(location, manager), JsonObject.class);
   }

   public static String getFileContents(ResourceLocation location, ResourceManager manager) {
      try {
         String var3;
         try (InputStream inputStream = manager.getResourceOrThrow(location).open()) {
            var3 = IOUtils.toString(inputStream, Charset.defaultCharset());
         }

         return var3;
      } catch (Exception var7) {
         GeckoLibConstants.LOGGER.error("Couldn't load " + location, var7);
         throw new RuntimeException(new FileNotFoundException(location.toString()));
      }
   }
}

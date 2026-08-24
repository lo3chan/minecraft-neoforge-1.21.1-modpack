package net.mehvahdjukaar.moonlight.core.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

public class SimpleSpecialModelsLoader extends SimplePreparableReloadListener<Void> {
   private final List<ResourceLocation> specialModels = new ArrayList<>();

   protected Void prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
      this.specialModels.clear();
      Gson gson = new Gson();
      String name = "models/special_models";
      FileToIdConverter fileToIdConverter = FileToIdConverter.json(name);
      Map<ResourceLocation, Resource> resourceLocationResourceMap = fileToIdConverter.listMatchingResources(resourceManager);
      List<ResourceLocation> models = new ArrayList<>();

      for (Entry<ResourceLocation, Resource> entry : resourceLocationResourceMap.entrySet()) {
         try {
            Reader reader = entry.getValue().openAsReader();
            JsonElement jsonelement = (JsonElement)GsonHelper.fromJson(gson, reader, JsonElement.class);
            if (jsonelement instanceof JsonObject jo) {
               String mod = GsonHelper.getAsString(jo, "required_mod", "");
               if (!mod.isEmpty() && !PlatHelper.isModLoaded(mod)) {
                  continue;
               }
            }
         } catch (Exception var14) {
            Moonlight.LOGGER.error("Couldn't parse special model file {}:", entry.getKey(), var14);
         }

         models.add(entry.getKey());
      }

      this.specialModels.addAll(models.stream().map(s -> s.withPath(s.getPath().substring(7, s.getPath().length() - 5))).toList());
      return null;
   }

   protected void apply(Void object, ResourceManager resourceManager, ProfilerFiller profiler) {
   }

   public Iterable<ResourceLocation> getSpecialModels() {
      return this.specialModels;
   }
}

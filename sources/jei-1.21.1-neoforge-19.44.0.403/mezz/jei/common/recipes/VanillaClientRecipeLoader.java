package mezz.jei.common.recipes;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class VanillaClientRecipeLoader {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = new Gson();

   private VanillaClientRecipeLoader() {
   }

   public static List<RecipeHolder<?>> getVanillaRecipes(RegistryAccess registryAccess) {
      Map<ResourceLocation, JsonElement> recipeJson = new HashMap<>();

      try {
         CloseableResourceManager resourceManager = createVanillaServerDataResourceManager();

         try {
            SimpleJsonResourceReloadListener.scanDirectory(resourceManager, "recipe", GSON, recipeJson);
         } catch (Throwable var6) {
            if (resourceManager != null) {
               try {
                  resourceManager.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (resourceManager != null) {
            resourceManager.close();
         }
      } catch (RuntimeException var7) {
         LOGGER.error("Failed to load vanilla recipes from client resources.", var7);
         return List.of();
      }

      List<RecipeHolder<?>> recipeHolders = new ArrayList<>(recipeJson.size());
      recipeJson.forEach(
         (id, json) -> Recipe.CODEC
            .parse(registryAccess.createSerializationContext(JsonOps.INSTANCE), json)
            .resultOrPartial(message -> LOGGER.error("Failed to parse vanilla recipe {} from client resources: {}", id, message))
            .ifPresent(recipe -> recipeHolders.add(new RecipeHolder(id, recipe)))
      );
      LOGGER.info("Loaded {} vanilla recipes from client resources.", recipeHolders.size());
      return recipeHolders;
   }

   private static CloseableResourceManager createVanillaServerDataResourceManager() {
      return new MultiPackResourceManager(PackType.SERVER_DATA, List.of(ServerPacksSource.createVanillaPackSource()));
   }
}

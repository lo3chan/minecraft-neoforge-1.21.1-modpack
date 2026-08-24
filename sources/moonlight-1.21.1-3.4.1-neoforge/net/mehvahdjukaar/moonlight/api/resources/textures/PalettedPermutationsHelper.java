package net.mehvahdjukaar.moonlight.api.resources.textures;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.IntUnaryOperator;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

public final class PalettedPermutationsHelper {
   private static final ResourceLocation PALETTED_PERMUTATIONS_TYPE = ResourceLocation.withDefaultNamespace("paletted_permutations");
   private static volatile ResourceManager cachedManager;
   private static volatile Map<ResourceLocation, PalettedPermutationsHelper.PalettedPermRecipe> cachedIndex;

   @Nullable
   public static TextureImage tryResolve(ResourceManager manager, ResourceLocation spriteId) {
      PalettedPermutationsHelper.PalettedPermRecipe recipe = getIndex(manager).get(spriteId);
      if (recipe == null) {
         return null;
      } else {
         try {
            return bake(manager, recipe);
         } catch (Exception var4) {
            Moonlight.LOGGER.warn("Failed to reconstruct paletted-permutation texture {}: {}", spriteId, var4.getMessage());
            return null;
         }
      }
   }

   private static TextureImage bake(ResourceManager manager, PalettedPermutationsHelper.PalettedPermRecipe recipe) throws IOException {
      int[] key = PalettedPermutations.loadPaletteEntryFromImage(manager, recipe.paletteKey());
      int[] perm = PalettedPermutations.loadPaletteEntryFromImage(manager, recipe.permutation());
      IntUnaryOperator mapping = PalettedPermutations.createPaletteMapping(key, perm);

      TextureImage var7;
      try (TextureImage base = TextureImage.open(manager, recipe.base())) {
         NativeImage mapped = base.getImage().mappedCopy(mapping);
         var7 = TextureImage.of(mapped, base.getMcMeta());
      }

      return var7;
   }

   public static void invalidate() {
      cachedManager = null;
      cachedIndex = null;
   }

   private static Map<ResourceLocation, PalettedPermutationsHelper.PalettedPermRecipe> getIndex(ResourceManager manager) {
      Map<ResourceLocation, PalettedPermutationsHelper.PalettedPermRecipe> index = cachedIndex;
      if (cachedManager == manager && index != null) {
         return index;
      } else {
         synchronized (PalettedPermutationsHelper.class) {
            if (cachedManager != manager || cachedIndex == null) {
               cachedIndex = buildIndex(manager);
               cachedManager = manager;
            }

            return cachedIndex;
         }
      }
   }

   private static Map<ResourceLocation, PalettedPermutationsHelper.PalettedPermRecipe> buildIndex(ResourceManager manager) {
      Map<ResourceLocation, PalettedPermutationsHelper.PalettedPermRecipe> map = new HashMap<>();
      Map<ResourceLocation, Resource> atlases = manager.listResources("atlases", rl -> rl.getPath().endsWith(".json"));

      for (Entry<ResourceLocation, Resource> entry : atlases.entrySet()) {
         try (BufferedReader reader = entry.getValue().openAsReader()) {
            JsonObject json = GsonHelper.parse(reader);
            JsonArray sources = GsonHelper.getAsJsonArray(json, "sources", null);
            if (sources != null) {
               for (JsonElement el : sources) {
                  if (el.isJsonObject()) {
                     JsonObject src = el.getAsJsonObject();
                     if (PALETTED_PERMUTATIONS_TYPE.equals(ResourceLocation.tryParse(GsonHelper.getAsString(src, "type", "")))) {
                        parseSource(src, map);
                     }
                  }
               }
            }
         } catch (Exception var13) {
            Moonlight.LOGGER.warn("Failed to scan atlas {} for paletted permutations: {}", entry.getKey(), var13.getMessage());
         }
      }

      if (!map.isEmpty()) {
         Moonlight.LOGGER.debug("Indexed {} virtual paletted-permutation sprites for recoloring", map.size());
      }

      return map;
   }

   private static void parseSource(JsonObject src, Map<ResourceLocation, PalettedPermutationsHelper.PalettedPermRecipe> map) {
      JsonArray textures = GsonHelper.getAsJsonArray(src, "textures");
      ResourceLocation paletteKey = ResourceLocation.parse(GsonHelper.getAsString(src, "palette_key"));
      JsonObject permutations = GsonHelper.getAsJsonObject(src, "permutations");

      for (Entry<String, JsonElement> perm : permutations.entrySet()) {
         String suffix = "_" + perm.getKey();
         ResourceLocation permPalette = ResourceLocation.parse(perm.getValue().getAsString());

         for (JsonElement t : textures) {
            ResourceLocation base = ResourceLocation.parse(t.getAsString());
            map.putIfAbsent(base.withSuffix(suffix), new PalettedPermutationsHelper.PalettedPermRecipe(base, paletteKey, permPalette));
         }
      }
   }

   private record PalettedPermRecipe(ResourceLocation base, ResourceLocation paletteKey, ResourceLocation permutation) {
   }
}

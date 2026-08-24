package fuzs.puzzleslib.api.client.data.v2;

import fuzs.puzzleslib.api.client.data.v2.models.MaterialMapper;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.PathProvider;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractAtlasProvider implements DataProvider {
   private final Map<ResourceLocation, List<SpriteSource>> values = new LinkedHashMap<>();
   private final PathProvider pathProvider;

   public AbstractAtlasProvider(DataProviderContext context) {
      this(context.getPackOutput());
   }

   public AbstractAtlasProvider(PackOutput packOutput) {
      this.pathProvider = packOutput.createPathProvider(Target.RESOURCE_PACK, "atlases");
   }

   public static SpriteSource forMaterial(Material material) {
      return new SingleFile(material.texture(), Optional.empty());
   }

   public static SpriteSource forMapper(MaterialMapper mapper) {
      return new DirectoryLister(mapper.prefix(), mapper.prefix() + "/");
   }

   public static List<SpriteSource> simpleMapper(MaterialMapper mapper) {
      return List.of(forMapper(mapper));
   }

   public static List<SpriteSource> noPrefixMapper(String path) {
      return List.of(new DirectoryLister(path, ""));
   }

   public final CompletableFuture<?> run(CachedOutput output) {
      this.addAtlases();
      return CompletableFuture.allOf(
         this.values.entrySet().stream().map(entry -> this.storeAtlas(output, entry.getKey(), entry.getValue())).toArray(CompletableFuture[]::new)
      );
   }

   public final CompletableFuture<?> storeAtlas(CachedOutput output, ResourceLocation atlasId, List<SpriteSource> sources) {
      return DataProvider.saveStable(output, RegistryAccess.EMPTY, SpriteSources.FILE_CODEC, sources, this.pathProvider.json(atlasId));
   }

   public abstract void addAtlases();

   protected void addMaterial(Material material) {
      this.add((ResourceLocation)ModelManager.VANILLA_ATLASES.get(material.atlasLocation()), forMaterial(material));
   }

   protected void add(ResourceLocation resourceLocation, SpriteSource... spriteSources) {
      this.add(resourceLocation, Arrays.asList(spriteSources));
   }

   protected void add(ResourceLocation resourceLocation, List<SpriteSource> spriteSources) {
      this.values.computeIfAbsent(resourceLocation, resourceLocationX -> new ArrayList<>()).addAll(spriteSources);
   }

   public String getName() {
      return "Atlas Definitions";
   }
}

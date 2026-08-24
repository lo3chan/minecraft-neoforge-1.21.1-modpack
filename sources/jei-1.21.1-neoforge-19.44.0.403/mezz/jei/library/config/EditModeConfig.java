package mezz.jei.library.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.runtime.IEditModeConfig;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.codecs.EnumCodec;
import mezz.jei.common.config.file.JsonArrayFileHelper;
import mezz.jei.library.ingredients.IngredientVisibility;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EditModeConfig implements IEditModeConfig {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int VERSION = 2;
   private final Map<Object, Pair<IEditModeConfig.HideMode, ITypedIngredient<?>>> blacklist = new LinkedHashMap<>();
   private final EditModeConfig.ISerializer serializer;
   private final IIngredientManager ingredientManager;
   private WeakReference<IngredientVisibility> ingredientVisibilityRef = new WeakReference<>(null);

   public EditModeConfig(EditModeConfig.ISerializer serializer, IIngredientManager ingredientManager) {
      this.ingredientManager = ingredientManager;
      this.serializer = serializer;
      this.serializer.initialize(this);
      this.serializer.load(this);
   }

   public <V> void addIngredientToConfigBlacklist(
      ITypedIngredient<V> typedIngredient, IEditModeConfig.HideMode blacklistType, IIngredientHelper<V> ingredientHelper
   ) {
      if (this.addIngredientToConfigBlacklistInternal(typedIngredient, blacklistType, ingredientHelper)) {
         this.serializer.save(this);
         this.notifyListenersOfVisibilityChange(typedIngredient, false);
      }
   }

   private <V> void addIngredientToConfigBlacklistInternal(ITypedIngredient<V> typedIngredient, IEditModeConfig.HideMode blacklistType) {
      IIngredientHelper<V> ingredientHelper = this.ingredientManager.getIngredientHelper(typedIngredient.getType());
      this.addIngredientToConfigBlacklistInternal(typedIngredient, blacklistType, ingredientHelper);
   }

   private <V> boolean addIngredientToConfigBlacklistInternal(
      ITypedIngredient<V> typedIngredient, IEditModeConfig.HideMode blacklistType, IIngredientHelper<V> ingredientHelper
   ) {
      Object wildcardUid = getIngredientUid(typedIngredient, IEditModeConfig.HideMode.WILDCARD, ingredientHelper);
      Object uid = getIngredientUid(typedIngredient, IEditModeConfig.HideMode.SINGLE, ingredientHelper);
      if (wildcardUid.equals(uid)) {
         blacklistType = IEditModeConfig.HideMode.WILDCARD;
      }

      if (blacklistType == IEditModeConfig.HideMode.SINGLE) {
         return this.blacklist.put(uid, new Pair(blacklistType, typedIngredient)) == null;
      } else {
         return blacklistType == IEditModeConfig.HideMode.WILDCARD ? this.blacklist.put(wildcardUid, new Pair(blacklistType, typedIngredient)) == null : false;
      }
   }

   public <V> boolean isIngredientOnConfigBlacklist(ITypedIngredient<V> typedIngredient, IIngredientHelper<V> ingredientHelper) {
      for (IEditModeConfig.HideMode hideMode : IEditModeConfig.HideMode.values()) {
         if (this.isIngredientOnConfigBlacklist(typedIngredient, hideMode, ingredientHelper)) {
            return true;
         }
      }

      return false;
   }

   private <V> Set<IEditModeConfig.HideMode> getIngredientOnConfigBlacklist(ITypedIngredient<V> ingredient, IIngredientHelper<V> ingredientHelper) {
      Object singleUid = getIngredientUid(ingredient, IEditModeConfig.HideMode.SINGLE, ingredientHelper);
      Object wildcardUid = getIngredientUid(ingredient, IEditModeConfig.HideMode.WILDCARD, ingredientHelper);
      if (singleUid.equals(wildcardUid)) {
         return this.blacklist.containsKey(singleUid) ? Set.of(IEditModeConfig.HideMode.SINGLE, IEditModeConfig.HideMode.WILDCARD) : Set.of();
      } else {
         Set<IEditModeConfig.HideMode> set = new HashSet<>();
         if (this.blacklist.containsKey(singleUid)) {
            set.add(IEditModeConfig.HideMode.SINGLE);
         }

         if (this.blacklist.containsKey(wildcardUid)) {
            set.add(IEditModeConfig.HideMode.WILDCARD);
         }

         return Collections.unmodifiableSet(set);
      }
   }

   public <V> boolean isIngredientOnConfigBlacklist(
      ITypedIngredient<V> typedIngredient, IEditModeConfig.HideMode blacklistType, IIngredientHelper<V> ingredientHelper
   ) {
      Object uid = getIngredientUid(typedIngredient, blacklistType, ingredientHelper);
      return this.blacklist.containsKey(uid);
   }

   private static <V> Object getIngredientUid(
      ITypedIngredient<V> typedIngredient, IEditModeConfig.HideMode blacklistType, IIngredientHelper<V> ingredientHelper
   ) {
      return switch (blacklistType) {
         case SINGLE -> ingredientHelper.getUid(typedIngredient, UidContext.Ingredient);
         case WILDCARD -> ingredientHelper.getGroupingUid(typedIngredient);
      };
   }

   @Override
   public <V> boolean isIngredientHiddenUsingConfigFile(ITypedIngredient<V> ingredient) {
      IIngredientType<V> type = ingredient.getType();
      IIngredientHelper<V> ingredientHelper = this.ingredientManager.getIngredientHelper(type);
      return this.isIngredientOnConfigBlacklist(ingredient, ingredientHelper);
   }

   @Override
   public <V> Set<IEditModeConfig.HideMode> getIngredientHiddenUsingConfigFile(ITypedIngredient<V> ingredient) {
      IIngredientType<V> type = ingredient.getType();
      IIngredientHelper<V> ingredientHelper = this.ingredientManager.getIngredientHelper(type);
      return this.getIngredientOnConfigBlacklist(ingredient, ingredientHelper);
   }

   @Override
   public <V> void hideIngredientUsingConfigFile(ITypedIngredient<V> ingredient, IEditModeConfig.HideMode hideMode) {
      IIngredientType<V> type = ingredient.getType();
      IIngredientHelper<V> ingredientHelper = this.ingredientManager.getIngredientHelper(type);
      this.addIngredientToConfigBlacklist(ingredient, hideMode, ingredientHelper);
   }

   @Override
   public <V> void showIngredientUsingConfigFile(ITypedIngredient<V> ingredient, IEditModeConfig.HideMode hideMode) {
      IIngredientType<V> type = ingredient.getType();
      IIngredientHelper<V> ingredientHelper = this.ingredientManager.getIngredientHelper(type);
      Object blacklistUid = getIngredientUid(ingredient, hideMode, ingredientHelper);
      if (this.blacklist.remove(blacklistUid) != null) {
         this.serializer.save(this);
         this.notifyListenersOfVisibilityChange(ingredient, true);
      }
   }

   public void registerListener(IngredientVisibility ingredientVisibility) {
      this.ingredientVisibilityRef = new WeakReference<>(ingredientVisibility);
   }

   private <T> void notifyListenersOfVisibilityChange(ITypedIngredient<T> ingredient, boolean visible) {
      IngredientVisibility ingredientVisibility = this.ingredientVisibilityRef.get();
      if (ingredientVisibility != null) {
         ingredientVisibility.notifyListeners(List.of(ingredient), visible);
      }
   }

   public static class FileSerializer implements EditModeConfig.ISerializer {
      private final Path path;
      private final Codec<Pair<IEditModeConfig.HideMode, ITypedIngredient<?>>> codec;
      private final RegistryOps<JsonElement> registryOps;

      public FileSerializer(Path path, RegistryAccess registryAccess, ICodecHelper codecHelper) {
         this.path = path;
         this.codec = RecordCodecBuilder.create(
            builder -> builder.group(
                  EnumCodec.create(IEditModeConfig.HideMode.class).fieldOf("hide_mode").forGetter(Pair::getFirst),
                  codecHelper.getTypedIngredientCodec().codec().fieldOf("ingredient").forGetter(Pair::getSecond)
               )
               .apply(builder, Pair::new)
         );
         this.registryOps = registryAccess.createSerializationContext(JsonOps.INSTANCE);
      }

      @Override
      public void initialize(EditModeConfig config) {
         if (!Files.exists(this.path)) {
            this.save(config);
         }
      }

      @Override
      public void save(EditModeConfig config) {
         try {
            JsonArrayFileHelper.write(
               this.path,
               2,
               config.blacklist.values(),
               this.codec,
               this.registryOps,
               error -> EditModeConfig.LOGGER.error("Encountered an error when saving the blacklist config to file {}\n{}", this.path, error),
               (element, exception) -> EditModeConfig.LOGGER
                  .error("Encountered an exception when saving the blacklist config to file {}\n{}", this.path, element, exception)
            );
            EditModeConfig.LOGGER.debug("Saved blacklist config to file: {}", this.path);
         } catch (IOException | RuntimeException var3) {
            EditModeConfig.LOGGER.error("Failed to save blacklist config to file {}", this.path, var3);
         }
      }

      @Override
      public void load(EditModeConfig config) {
         if (Files.exists(this.path)) {
            List<Pair<IEditModeConfig.HideMode, ITypedIngredient<?>>> results;
            try (BufferedReader reader = Files.newBufferedReader(this.path)) {
               results = JsonArrayFileHelper.read(
                  reader,
                  2,
                  this.codec,
                  this.registryOps,
                  (element, error) -> EditModeConfig.LOGGER
                     .error("Encountered an error when loading the blacklist config from file {}\n{}\n{}", this.path, element, error),
                  (element, exception) -> EditModeConfig.LOGGER
                     .error("Encountered an exception when loading the blacklist config from file {}\n{}", this.path, element, exception)
               );
            } catch (JsonSyntaxException | IOException | IllegalArgumentException | JsonIOException var8) {
               EditModeConfig.LOGGER.error("Failed to load blacklist from file {}", this.path, var8);
               results = List.of();
            }

            for (Pair<IEditModeConfig.HideMode, ITypedIngredient<?>> pair : results) {
               config.addIngredientToConfigBlacklistInternal((ITypedIngredient)pair.getSecond(), (IEditModeConfig.HideMode)pair.getFirst());
            }
         }
      }
   }

   public interface ISerializer {
      void initialize(EditModeConfig var1);

      void save(EditModeConfig var1);

      void load(EditModeConfig var1);
   }
}

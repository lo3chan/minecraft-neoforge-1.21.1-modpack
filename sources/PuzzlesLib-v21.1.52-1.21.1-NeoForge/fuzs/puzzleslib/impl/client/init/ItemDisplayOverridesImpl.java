package fuzs.puzzleslib.impl.client.init;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMap.Builder;
import fuzs.puzzleslib.api.client.init.v1.ItemModelDisplayOverrides;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

public abstract class ItemDisplayOverridesImpl<T> implements ItemModelDisplayOverrides {
   private final Map<ModelResourceLocation, Map<ItemDisplayContext, Function<ItemDisplayOverridesImpl.BakedModelResolver, BakedModel>>> overrideLocations = new HashMap<>();

   protected ItemDisplayOverridesImpl() {
      this.registerEventHandlers();
   }

   @Override
   public void register(ModelResourceLocation itemModel, ModelResourceLocation itemModelOverride) {
      this.register(itemModel, itemModelOverride, getVanillaItemModelDisplayOverrides());
   }

   @Override
   public void register(ModelResourceLocation itemModel, ResourceLocation itemModelOverride) {
      this.register(itemModel, itemModelOverride, getVanillaItemModelDisplayOverrides());
   }

   static ItemDisplayContext[] getVanillaItemModelDisplayOverrides() {
      return EnumSet.complementOf(
            Sets.newEnumSet(Arrays.asList(ItemDisplayContext.GUI, ItemDisplayContext.GROUND, ItemDisplayContext.FIXED), ItemDisplayContext.class)
         )
         .toArray(ItemDisplayContext[]::new);
   }

   protected void register(
      ModelResourceLocation modelResourceLocation,
      Function<ItemDisplayOverridesImpl.BakedModelResolver, BakedModel> bakedModelGetter,
      ItemDisplayContext[] itemDisplayContexts
   ) {
      Objects.requireNonNull(modelResourceLocation, "item model is null");
      Preconditions.checkState(itemDisplayContexts.length > 0, "item display contexts is empty");
      Map<ItemDisplayContext, Function<ItemDisplayOverridesImpl.BakedModelResolver, BakedModel>> overrides = this.overrideLocations
         .computeIfAbsent(modelResourceLocation, $ -> new EnumMap<>(ItemDisplayContext.class));

      for (ItemDisplayContext itemDisplayContext : itemDisplayContexts) {
         if (overrides.put(itemDisplayContext, bakedModelGetter) != null) {
            throw new IllegalStateException(
               "Attempting to register duplicate item model display override for model %s and display context %s"
                  .formatted(modelResourceLocation, itemDisplayContext)
            );
         }
      }
   }

   protected Map<T, Map<ItemDisplayContext, BakedModel>> computeOverrideModels(
      ItemDisplayOverridesImpl.BakedModelResolver modelResolver, BakedModel missingModel
   ) {
      Builder<T, Map<ItemDisplayContext, BakedModel>> builder = ImmutableMap.builder();

      for (Entry<ModelResourceLocation, Map<ItemDisplayContext, Function<ItemDisplayOverridesImpl.BakedModelResolver, BakedModel>>> overrideEntry : this.overrideLocations
         .entrySet()) {
         BakedModel itemModel = modelResolver.getModel(overrideEntry.getKey());
         Preconditions.checkState(itemModel != missingModel, "item model is missing");
         Builder<ItemDisplayContext, BakedModel> overrideBuilder = ImmutableMap.builder();

         for (Entry<ItemDisplayContext, Function<ItemDisplayOverridesImpl.BakedModelResolver, BakedModel>> entry : overrideEntry.getValue().entrySet()) {
            BakedModel overrideModel = (BakedModel)entry.getValue().apply(modelResolver);
            Preconditions.checkState(overrideModel != missingModel, "override model is missing");
            overrideBuilder.put(entry.getKey(), overrideModel);
         }

         T overrideModelKey = this.createOverrideModelKey(overrideEntry.getKey(), itemModel);
         builder.put(overrideModelKey, overrideBuilder.build());
      }

      return builder.build();
   }

   protected abstract T createOverrideModelKey(ModelResourceLocation var1, BakedModel var2);

   protected abstract void registerEventHandlers();

   protected interface BakedModelResolver {
      BakedModel getModel(ModelResourceLocation var1);

      BakedModel getModel(ResourceLocation var1);
   }
}

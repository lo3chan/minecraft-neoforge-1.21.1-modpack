package fuzs.puzzleslib.api.init.v3.registry;

import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.event.v1.CommonSetupCallback;
import fuzs.puzzleslib.impl.item.CustomTransmuteRecipe;
import java.util.function.Consumer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SkullBlock.Type;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet.Builder;

public final class ContentRegistrationHelper {
   private ContentRegistrationHelper() {
   }

   public static void registerTransmuteRecipeSerializers(RegistryManager registryManager) {
      CustomTransmuteRecipe.registerSerializers(
         (string, recipeSerializerSupplier) -> registryManager.register(Registries.RECIPE_SERIALIZER, string, recipeSerializerSupplier)
      );
   }

   public static Type registerSkullBlockType(ResourceLocation resourceLocation) {
      String string = resourceLocation.toString();
      Type skullBlockType = () -> string;
      CommonSetupCallback.EVENT.register(() -> Type.TYPES.put(skullBlockType.getSerializedName(), skullBlockType));
      return skullBlockType;
   }

   public static LootContextParamSet registerContextKeySet(ResourceLocation resourceLocation, Consumer<Builder> builderConsumer) {
      Builder builder = new Builder();
      builderConsumer.accept(builder);
      LootContextParamSet contextKeySet = builder.build();
      if (ModLoaderEnvironment.INSTANCE.isDataGeneration()) {
         registerContextKeySet(resourceLocation, contextKeySet);
      } else {
         CommonSetupCallback.EVENT.register(() -> registerContextKeySet(resourceLocation, contextKeySet));
      }

      return contextKeySet;
   }

   private static void registerContextKeySet(ResourceLocation resourceLocation, LootContextParamSet contextKeySet) {
      if (LootContextParamSets.REGISTRY.put(resourceLocation, contextKeySet) != null) {
         throw new IllegalStateException("Loot context key set " + resourceLocation + " is already registered");
      }
   }
}

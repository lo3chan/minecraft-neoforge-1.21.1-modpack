package fuzs.puzzleslib.api.client.core.v1.context;

import fuzs.puzzleslib.api.client.init.v1.ArmorModelSet;
import java.util.function.Supplier;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface LayerDefinitionsContext {
   void registerLayerDefinition(ModelLayerLocation var1, Supplier<LayerDefinition> var2);

   default void registerArmorDefinition(ArmorModelSet<ModelLayerLocation> modelLayerSet, Supplier<ArmorModelSet<LayerDefinition>> layerSetSupplier) {
      ArmorModelSet<MutableObject<LayerDefinition>> mutableLayerSet = new ArmorModelSet<>(
         new MutableObject(), new MutableObject(), new MutableObject(), new MutableObject()
      );
      this.registerArmorDefinition(
         modelLayerSet, mutableLayerSet, layerSetSupplier, ArmorModelSet::head, ArmorModelSet::chest, ArmorModelSet::legs, ArmorModelSet::feet
      );
      this.registerArmorDefinition(
         modelLayerSet, mutableLayerSet, layerSetSupplier, ArmorModelSet::chest, ArmorModelSet::head, ArmorModelSet::legs, ArmorModelSet::feet
      );
      this.registerArmorDefinition(
         modelLayerSet, mutableLayerSet, layerSetSupplier, ArmorModelSet::legs, ArmorModelSet::head, ArmorModelSet::chest, ArmorModelSet::feet
      );
      this.registerArmorDefinition(
         modelLayerSet, mutableLayerSet, layerSetSupplier, ArmorModelSet::feet, ArmorModelSet::head, ArmorModelSet::chest, ArmorModelSet::legs
      );
   }

   private void registerArmorDefinition(
      ArmorModelSet<ModelLayerLocation> modelLayerSet,
      ArmorModelSet<MutableObject<LayerDefinition>> mutableLayerSet,
      Supplier<ArmorModelSet<LayerDefinition>> layerSetSupplier,
      LayerDefinitionsContext.ArmorModelSetGetter primaryGetter,
      LayerDefinitionsContext.ArmorModelSetGetter secondaryGetter,
      LayerDefinitionsContext.ArmorModelSetGetter tertiaryGetter,
      LayerDefinitionsContext.ArmorModelSetGetter quaternaryGetter
   ) {
      this.registerLayerDefinition(
         primaryGetter.apply(modelLayerSet),
         () -> this.storeArmorModelLayers(mutableLayerSet, layerSetSupplier, primaryGetter, secondaryGetter, tertiaryGetter, quaternaryGetter)
      );
   }

   private LayerDefinition storeArmorModelLayers(
      ArmorModelSet<MutableObject<LayerDefinition>> mutableLayerSet,
      Supplier<ArmorModelSet<LayerDefinition>> layerSetSupplier,
      LayerDefinitionsContext.ArmorModelSetGetter primaryGetter,
      LayerDefinitionsContext.ArmorModelSetGetter secondaryGetter,
      LayerDefinitionsContext.ArmorModelSetGetter tertiaryGetter,
      LayerDefinitionsContext.ArmorModelSetGetter quaternaryGetter
   ) {
      LayerDefinition layerDefinition = (LayerDefinition)primaryGetter.apply(mutableLayerSet).getValue();
      if (layerDefinition != null) {
         primaryGetter.apply(mutableLayerSet).setValue(null);
         return layerDefinition;
      } else {
         ArmorModelSet<LayerDefinition> armorModelSet = layerSetSupplier.get();
         secondaryGetter.apply(mutableLayerSet).setValue(secondaryGetter.apply(armorModelSet));
         tertiaryGetter.apply(mutableLayerSet).setValue(tertiaryGetter.apply(armorModelSet));
         quaternaryGetter.apply(mutableLayerSet).setValue(quaternaryGetter.apply(armorModelSet));
         return primaryGetter.apply(armorModelSet);
      }
   }

   @FunctionalInterface
   @Internal
   public interface ArmorModelSetGetter {
      <T> T apply(ArmorModelSet<T> var1);
   }
}

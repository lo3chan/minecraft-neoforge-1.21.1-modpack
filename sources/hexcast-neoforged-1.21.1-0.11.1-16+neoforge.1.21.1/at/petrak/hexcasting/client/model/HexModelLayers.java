package at.petrak.hexcasting.client.model;

import at.petrak.hexcasting.api.HexAPI;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class HexModelLayers {
   public static final ModelLayerLocation ALTIORA = make("altiora");
   public static final ModelLayerLocation ROBES = make("robes");

   private static ModelLayerLocation make(String name) {
      return make(name, "main");
   }

   private static ModelLayerLocation make(String name, String layer) {
      return new ModelLayerLocation(HexAPI.modLoc(name), layer);
   }

   public static void init(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> consumer) {
      consumer.accept(ALTIORA, ElytraModel::createLayer);
      consumer.accept(ROBES, HexRobesModels::variant1);
   }
}

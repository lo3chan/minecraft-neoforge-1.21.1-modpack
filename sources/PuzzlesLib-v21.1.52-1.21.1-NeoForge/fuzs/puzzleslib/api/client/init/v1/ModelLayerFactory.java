package fuzs.puzzleslib.api.client.init.v1;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat.Type;
import net.minecraft.world.level.block.state.properties.WoodType;

@FunctionalInterface
public interface ModelLayerFactory {
   static ModelLayerFactory from(String modId) {
      return () -> modId;
   }

   String modId();

   default ModelLayerLocation registerModelLayer(String path, String layer) {
      ModelLayerLocation modelLayerLocation = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(this.modId(), path), layer);
      if (!ModelLayers.ALL_MODELS.add(modelLayerLocation)) {
         throw new IllegalStateException("Duplicate registration for " + modelLayerLocation);
      } else {
         return modelLayerLocation;
      }
   }

   default ModelLayerLocation registerModelLayer(String path) {
      return this.registerModelLayer(path, "main");
   }

   default ArmorModelSet<ModelLayerLocation> registerArmorSet(String path) {
      return new ArmorModelSet<>(
         this.registerModelLayer(path, "helmet"),
         this.registerModelLayer(path, "chestplate"),
         this.registerModelLayer(path, "leggings"),
         this.registerModelLayer(path, "boots")
      );
   }

   default ModelLayerLocation registerInnerArmorModelLayer(String path) {
      return this.registerModelLayer(path, "inner_armor");
   }

   default ModelLayerLocation registerOuterArmorModelLayer(String path) {
      return this.registerModelLayer(path, "outer_armor");
   }

   default ModelLayerLocation createRaftModelName(Type boatType) {
      return this.registerModelLayer("raft/" + boatType.getName());
   }

   default ModelLayerLocation createChestRaftModelName(Type boatType) {
      return this.registerModelLayer("chest_raft/" + boatType.getName());
   }

   default ModelLayerLocation createBoatModelName(Type boatType) {
      return this.registerModelLayer("boat/" + boatType.getName());
   }

   default ModelLayerLocation createChestBoatModelName(Type boatType) {
      return this.registerModelLayer("chest_boat/" + boatType.getName());
   }

   default ModelLayerLocation createSignModelName(WoodType woodType) {
      return this.registerModelLayer("sign/" + woodType.name());
   }

   default ModelLayerLocation createHangingSignModelName(WoodType woodType) {
      return this.registerModelLayer("hanging_sign/" + woodType.name());
   }

   @Deprecated
   default ModelLayerLocation register(String path, String layer) {
      return this.registerModelLayer(path, layer);
   }

   @Deprecated
   default ModelLayerLocation register(String path) {
      return this.registerModelLayer(path);
   }

   @Deprecated
   default ModelLayerLocation registerInnerArmor(String path) {
      return this.registerInnerArmorModelLayer(path);
   }

   @Deprecated
   default ModelLayerLocation registerOuterArmor(String path) {
      return this.registerOuterArmorModelLayer(path);
   }
}

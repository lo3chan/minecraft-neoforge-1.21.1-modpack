package net.joefoxe.hexerei.block.connected;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ModelEvent.ModifyBakingResult;

public class ModelSwapper {
   protected CustomBlockModels customBlockModels = new CustomBlockModels();

   public CustomBlockModels getCustomBlockModels() {
      return this.customBlockModels;
   }

   public void onModelBake(ModifyBakingResult event) {
      Map<ModelResourceLocation, BakedModel> modelRegistry = event.getModels();
      this.customBlockModels.forEach((block, modelFunc) -> swapModels(modelRegistry, getAllBlockStateModelLocations(block), modelFunc));
   }

   public void registerListeners(IEventBus modEventBus) {
      modEventBus.addListener(this::onModelBake);
   }

   public static <T extends BakedModel> void swapModels(
      Map<ModelResourceLocation, BakedModel> modelRegistry, List<ModelResourceLocation> locations, Function<BakedModel, T> factory
   ) {
      locations.forEach(location -> swapModels(modelRegistry, location, factory));
   }

   public static <T extends BakedModel> void swapModels(
      Map<ModelResourceLocation, BakedModel> modelRegistry, ModelResourceLocation location, Function<BakedModel, T> factory
   ) {
      modelRegistry.put(location, factory.apply(modelRegistry.get(location)));
   }

   public static List<ModelResourceLocation> getAllBlockStateModelLocations(Block block) {
      List<ModelResourceLocation> models = new ArrayList<>();
      ResourceLocation blockRl = HexereiUtil.getKeyOrThrow(block);
      block.getStateDefinition().getPossibleStates().forEach(state -> models.add(BlockModelShaper.stateToModelLocation(blockRl, state)));
      return models;
   }

   public static ModelResourceLocation getItemModelLocation(Item item) {
      return new ModelResourceLocation(HexereiUtil.getKeyOrThrow(item), "inventory");
   }
}

package org.dimdev.limlib.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.dimdev.limlib.api.client.ModelLoadingRegistry;

public record ModelLoadingOverride(ModelResourceLocation replacementModel, List<Block> blockStateTargets, List<ModelResourceLocation> modelTargets) {
   public static final String STANDALONE_VARIANT = "standalone";
   private static final String INVENTORY_MODEL_PREFIX = "item/";

   public ModelLoadingOverride(ModelResourceLocation replacementModel, List<Block> blockStateTargets, List<ModelResourceLocation> modelTargets) {
      blockStateTargets = List.copyOf(blockStateTargets);
      modelTargets = List.copyOf(modelTargets);
      this.replacementModel = replacementModel;
      this.blockStateTargets = blockStateTargets;
      this.modelTargets = modelTargets;
   }

   public static ModelResourceLocation standalone(ResourceLocation modelId) {
      return new ModelResourceLocation(modelId, "standalone");
   }

   public static ModelLoadingOverride create(ModelResourceLocation replacementModel, Consumer<ModelLoadingRegistry> registration) {
      ModelLoadingOverride.Builder builder = new ModelLoadingOverride.Builder(replacementModel);
      registration.accept(builder);
      return builder.build();
   }

   public Stream<ModelResourceLocation> resolvedTargets() {
      return Stream.concat(
         this.blockStateTargets.stream().flatMap(block -> block.getStateDefinition().getPossibleStates().stream()).map(BlockModelShaper::stateToModelLocation),
         this.modelTargets.stream()
      );
   }

   public boolean targetsModel(ResourceLocation modelId) {
      return this.modelTargets.stream().anyMatch(target -> target.id().equals(modelId));
   }

   public Stream<ResourceLocation> resolvedTargetResources() {
      return this.modelTargets.stream().map(ModelLoadingOverride::modelResource);
   }

   public boolean targetsResource(ResourceLocation modelId) {
      return this.modelTargets.stream().anyMatch(target -> modelResource(target).equals(modelId));
   }

   private static ResourceLocation modelResource(ModelResourceLocation model) {
      return "inventory".equals(model.variant()) ? model.id().withPrefix("item/") : model.id();
   }

   private static final class Builder implements ModelLoadingRegistry {
      private final ModelResourceLocation replacementModel;
      private final List<Block> blockStateTargets = new ArrayList<>();
      private final List<ModelResourceLocation> modelTargets = new ArrayList<>();

      private Builder(ModelResourceLocation replacementModel) {
         this.replacementModel = replacementModel;
      }

      @Override
      public void replaceBlockStates(Block block) {
         this.blockStateTargets.add(block);
      }

      @Override
      public void replaceModel(ModelResourceLocation model) {
         this.modelTargets.add(model);
      }

      private ModelLoadingOverride build() {
         return new ModelLoadingOverride(this.replacementModel, this.blockStateTargets, this.modelTargets);
      }
   }
}

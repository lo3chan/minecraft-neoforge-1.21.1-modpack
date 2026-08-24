package com.github.alexthe666.alexsmobs.client.model.layered;

import com.github.alexthe666.alexsmobs.client.model.ModelWanderingVillagerRider;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;

@OnlyIn(Dist.CLIENT)
public class AMModelLayers {
   public static final ModelLayerLocation AM_ELYTRA = createLocation("am_elytra", "main");
   public static final ModelLayerLocation SITTING_WANDERING_VILLAGER = createLocation("sitting_wandering_villager", "main");
   public static final ModelLayerLocation ROADRUNNER_BOOTS = createLocation("roadrunner_boots", "main");
   public static final ModelLayerLocation MOOSE_HEADGEAR = createLocation("moose_headgear", "main");
   public static final ModelLayerLocation FRONTIER_CAP = createLocation("frontier_cap", "main");
   public static final ModelLayerLocation SPIKED_TURTLE_SHELL = createLocation("spiked_turtle_shell", "main");
   public static final ModelLayerLocation FEDORA = createLocation("fedora", "main");
   public static final ModelLayerLocation SOMBRERO = createLocation("sombrero", "main");
   public static final ModelLayerLocation SOMBRERO_GOOFY_FASHION = createLocation("sombrero_goofy_fashion", "main");
   public static final ModelLayerLocation FROSTSTALKER_HELMET = createLocation("froststalker_helmet", "main");
   public static final ModelLayerLocation ROCKY_CHESTPLATE = createLocation("rocky_chestplate", "main");
   public static final ModelLayerLocation FLYING_FISH_BOOTS = createLocation("flying_fish_boots", "main");
   public static final ModelLayerLocation NOVELTY_HAT = createLocation("novelty_hat", "main");
   public static final ModelLayerLocation UNDERMINER = createLocation("underminer", "main");
   public static final ModelLayerLocation UNSETTLING_KIMONO = createLocation("unsettling_kimono", "main");

   public static void register(RegisterLayerDefinitions event) {
      registerAll((loc, def) -> event.registerLayerDefinition(loc, def::get));
   }

   private static void registerAll(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> sink) {
      sink.accept(SITTING_WANDERING_VILLAGER, () -> LayerDefinition.create(ModelWanderingVillagerRider.createBodyModel(), 64, 64));
      sink.accept(UNDERMINER, () -> LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0.05F), 64, 64));
      sink.accept(ROADRUNNER_BOOTS, () -> ModelRoadrunnerBoots.createArmorLayer(new CubeDeformation(0.5F)));
      sink.accept(MOOSE_HEADGEAR, () -> ModelMooseHeadgear.createArmorLayer(new CubeDeformation(0.5F)));
      sink.accept(FRONTIER_CAP, () -> ModelFrontierCap.createArmorLayer(new CubeDeformation(0.5F)));
      sink.accept(SPIKED_TURTLE_SHELL, () -> ModelSpikedTurtleShell.createArmorLayer(new CubeDeformation(0.5F)));
      sink.accept(FEDORA, () -> ModelFedora.createArmorLayer(new CubeDeformation(0.5F)));
      sink.accept(AM_ELYTRA, () -> ModelAMElytra.createLayer(new CubeDeformation(1.0F)));
      sink.accept(SOMBRERO, () -> ModelSombrero.createArmorLayer(new CubeDeformation(0.5F)));
      sink.accept(SOMBRERO_GOOFY_FASHION, () -> ModelSombrero.createArmorLayerAprilFools(new CubeDeformation(0.5F)));
      sink.accept(FROSTSTALKER_HELMET, () -> ModelFroststalkerHelmet.createArmorLayer(new CubeDeformation(0.5F)));
      sink.accept(ROCKY_CHESTPLATE, () -> ModelRockyChestplate.createArmorLayer(new CubeDeformation(0.7F)));
      sink.accept(FLYING_FISH_BOOTS, () -> ModelFlyingFishBoots.createArmorLayer(new CubeDeformation(0.5F)));
      sink.accept(NOVELTY_HAT, () -> ModelNoveltyHat.createArmorLayer(new CubeDeformation(0.5F)));
      sink.accept(UNSETTLING_KIMONO, () -> ModelUnsettlingKimono.createArmorLayer(new CubeDeformation(0.5F)));
   }

   private static ModelLayerLocation createLocation(String model, String layer) {
      return new ModelLayerLocation(AMCompat.rl("alexsmobs", model), layer);
   }
}

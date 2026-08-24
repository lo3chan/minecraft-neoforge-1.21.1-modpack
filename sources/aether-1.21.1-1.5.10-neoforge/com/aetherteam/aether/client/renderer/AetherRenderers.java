package com.aetherteam.aether.client.renderer;

import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.blockentity.AetherBlockEntityTypes;
import com.aetherteam.aether.client.renderer.accessory.GlovesRenderer;
import com.aetherteam.aether.client.renderer.accessory.PendantRenderer;
import com.aetherteam.aether.client.renderer.accessory.ShieldOfRepulsionRenderer;
import com.aetherteam.aether.client.renderer.accessory.layer.ArmorStandCapeLayer;
import com.aetherteam.aether.client.renderer.accessory.model.CapeModel;
import com.aetherteam.aether.client.renderer.accessory.model.GlovesModel;
import com.aetherteam.aether.client.renderer.accessory.model.PendantModel;
import com.aetherteam.aether.client.renderer.block.FastModel;
import com.aetherteam.aether.client.renderer.blockentity.ChestMimicRenderer;
import com.aetherteam.aether.client.renderer.blockentity.SkyrootBedRenderer;
import com.aetherteam.aether.client.renderer.blockentity.TreasureChestRenderer;
import com.aetherteam.aether.client.renderer.entity.AechorPlantRenderer;
import com.aetherteam.aether.client.renderer.entity.AerbunnyRenderer;
import com.aetherteam.aether.client.renderer.entity.AerwhaleRenderer;
import com.aetherteam.aether.client.renderer.entity.BlueSwetRenderer;
import com.aetherteam.aether.client.renderer.entity.CloudCrystalRenderer;
import com.aetherteam.aether.client.renderer.entity.CloudMinionRenderer;
import com.aetherteam.aether.client.renderer.entity.CockatriceRenderer;
import com.aetherteam.aether.client.renderer.entity.EnchantedDartRenderer;
import com.aetherteam.aether.client.renderer.entity.FireCrystalRenderer;
import com.aetherteam.aether.client.renderer.entity.FireMinionRenderer;
import com.aetherteam.aether.client.renderer.entity.FloatingBlockRenderer;
import com.aetherteam.aether.client.renderer.entity.FlyingCowRenderer;
import com.aetherteam.aether.client.renderer.entity.GoldenDartRenderer;
import com.aetherteam.aether.client.renderer.entity.GoldenSwetRenderer;
import com.aetherteam.aether.client.renderer.entity.HammerProjectileRenderer;
import com.aetherteam.aether.client.renderer.entity.IceCrystalRenderer;
import com.aetherteam.aether.client.renderer.entity.LightningKnifeRenderer;
import com.aetherteam.aether.client.renderer.entity.MimicRenderer;
import com.aetherteam.aether.client.renderer.entity.MoaRenderer;
import com.aetherteam.aether.client.renderer.entity.ParachuteRenderer;
import com.aetherteam.aether.client.renderer.entity.PhygRenderer;
import com.aetherteam.aether.client.renderer.entity.PoisonDartRenderer;
import com.aetherteam.aether.client.renderer.entity.PoisonNeedleRenderer;
import com.aetherteam.aether.client.renderer.entity.SentryRenderer;
import com.aetherteam.aether.client.renderer.entity.SheepuffRenderer;
import com.aetherteam.aether.client.renderer.entity.SkyrootBoatRenderer;
import com.aetherteam.aether.client.renderer.entity.SliderRenderer;
import com.aetherteam.aether.client.renderer.entity.SunSpiritRenderer;
import com.aetherteam.aether.client.renderer.entity.ThunderCrystalRenderer;
import com.aetherteam.aether.client.renderer.entity.TntPresentRenderer;
import com.aetherteam.aether.client.renderer.entity.ValkyrieQueenRenderer;
import com.aetherteam.aether.client.renderer.entity.ValkyrieRenderer;
import com.aetherteam.aether.client.renderer.entity.WhirlwindRenderer;
import com.aetherteam.aether.client.renderer.entity.ZephyrRenderer;
import com.aetherteam.aether.client.renderer.entity.model.AechorPlantModel;
import com.aetherteam.aether.client.renderer.entity.model.AerbunnyModel;
import com.aetherteam.aether.client.renderer.entity.model.AerwhaleModel;
import com.aetherteam.aether.client.renderer.entity.model.ClassicAerwhaleModel;
import com.aetherteam.aether.client.renderer.entity.model.ClassicZephyrModel;
import com.aetherteam.aether.client.renderer.entity.model.CloudMinionModel;
import com.aetherteam.aether.client.renderer.entity.model.CockatriceModel;
import com.aetherteam.aether.client.renderer.entity.model.CrystalModel;
import com.aetherteam.aether.client.renderer.entity.model.HaloModel;
import com.aetherteam.aether.client.renderer.entity.model.MimicModel;
import com.aetherteam.aether.client.renderer.entity.model.MoaModel;
import com.aetherteam.aether.client.renderer.entity.model.QuadrupedWingsModel;
import com.aetherteam.aether.client.renderer.entity.model.SheepuffModel;
import com.aetherteam.aether.client.renderer.entity.model.SheepuffWoolModel;
import com.aetherteam.aether.client.renderer.entity.model.SliderModel;
import com.aetherteam.aether.client.renderer.entity.model.SunSpiritModel;
import com.aetherteam.aether.client.renderer.entity.model.ValkyrieModel;
import com.aetherteam.aether.client.renderer.entity.model.ValkyrieWingsModel;
import com.aetherteam.aether.client.renderer.entity.model.ZephyrModel;
import com.aetherteam.aether.client.renderer.player.layer.DartLayer;
import com.aetherteam.aether.client.renderer.player.layer.DeveloperGlowLayer;
import com.aetherteam.aether.client.renderer.player.layer.PlayerHaloLayer;
import com.aetherteam.aether.client.renderer.player.layer.PlayerWingsLayer;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.entity.projectile.dart.EnchantedDart;
import com.aetherteam.aether.entity.projectile.dart.GoldenDart;
import com.aetherteam.aether.entity.projectile.dart.PoisonDart;
import com.aetherteam.aether.item.AetherItems;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmorStandModel;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin.Model;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.AddLayers;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.client.event.ModelEvent.ModifyBakingResult;

public class AetherRenderers {
   public static void registerEntityRenderers(RegisterRenderers event) {
      event.registerBlockEntityRenderer((BlockEntityType)AetherBlockEntityTypes.SKYROOT_BED.get(), SkyrootBedRenderer::new);
      event.registerBlockEntityRenderer((BlockEntityType)AetherBlockEntityTypes.SKYROOT_SIGN.get(), SignRenderer::new);
      event.registerBlockEntityRenderer((BlockEntityType)AetherBlockEntityTypes.SKYROOT_HANGING_SIGN.get(), HangingSignRenderer::new);
      event.registerBlockEntityRenderer((BlockEntityType)AetherBlockEntityTypes.CHEST_MIMIC.get(), ChestMimicRenderer::new);
      event.registerBlockEntityRenderer((BlockEntityType)AetherBlockEntityTypes.TREASURE_CHEST.get(), TreasureChestRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.PHYG.get(), PhygRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.FLYING_COW.get(), FlyingCowRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.SHEEPUFF.get(), SheepuffRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.AERBUNNY.get(), AerbunnyRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.MOA.get(), MoaRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.AERWHALE.get(), AerwhaleRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.BLUE_SWET.get(), BlueSwetRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.GOLDEN_SWET.get(), GoldenSwetRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.WHIRLWIND.get(), WhirlwindRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.EVIL_WHIRLWIND.get(), WhirlwindRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.AECHOR_PLANT.get(), AechorPlantRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.COCKATRICE.get(), CockatriceRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.ZEPHYR.get(), ZephyrRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.MIMIC.get(), MimicRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.SENTRY.get(), SentryRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.VALKYRIE.get(), ValkyrieRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.FIRE_MINION.get(), FireMinionRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.SLIDER.get(), SliderRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.VALKYRIE_QUEEN.get(), ValkyrieQueenRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.SUN_SPIRIT.get(), SunSpiritRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.SKYROOT_BOAT.get(), context -> new SkyrootBoatRenderer(context, false));
      event.registerEntityRenderer((EntityType)AetherEntityTypes.SKYROOT_CHEST_BOAT.get(), context -> new SkyrootBoatRenderer(context, true));
      event.registerEntityRenderer((EntityType)AetherEntityTypes.CLOUD_MINION.get(), CloudMinionRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.COLD_PARACHUTE.get(), context -> new ParachuteRenderer(context, AetherBlocks.COLD_AERCLOUD));
      event.registerEntityRenderer(
         (EntityType)AetherEntityTypes.GOLDEN_PARACHUTE.get(), context -> new ParachuteRenderer(context, AetherBlocks.GOLDEN_AERCLOUD)
      );
      event.registerEntityRenderer((EntityType)AetherEntityTypes.FLOATING_BLOCK.get(), FloatingBlockRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.TNT_PRESENT.get(), TntPresentRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.ZEPHYR_SNOWBALL.get(), context -> new ThrownItemRenderer(context, 3.0F, true));
      event.registerEntityRenderer((EntityType)AetherEntityTypes.CLOUD_CRYSTAL.get(), CloudCrystalRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.FIRE_CRYSTAL.get(), FireCrystalRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.ICE_CRYSTAL.get(), IceCrystalRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.THUNDER_CRYSTAL.get(), ThunderCrystalRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.GOLDEN_DART.get(), GoldenDartRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.POISON_DART.get(), PoisonDartRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.ENCHANTED_DART.get(), EnchantedDartRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.POISON_NEEDLE.get(), PoisonNeedleRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.LIGHTNING_KNIFE.get(), LightningKnifeRenderer::new);
      event.registerEntityRenderer((EntityType)AetherEntityTypes.HAMMER_PROJECTILE.get(), HammerProjectileRenderer::new);
   }

   public static void registerLayerDefinitions(RegisterLayerDefinitions event) {
      event.registerLayerDefinition(AetherModelLayers.SKYROOT_BED_FOOT, BedRenderer::createFootLayer);
      event.registerLayerDefinition(AetherModelLayers.SKYROOT_BED_HEAD, BedRenderer::createHeadLayer);
      event.registerLayerDefinition(AetherModelLayers.CHEST_MIMIC, ChestRenderer::createSingleBodyLayer);
      event.registerLayerDefinition(AetherModelLayers.PHYG, () -> PigModel.createBodyLayer(CubeDeformation.NONE));
      event.registerLayerDefinition(AetherModelLayers.PHYG_WINGS, () -> QuadrupedWingsModel.createMainLayer(10.0F));
      event.registerLayerDefinition(AetherModelLayers.PHYG_SADDLE, () -> PigModel.createBodyLayer(new CubeDeformation(0.5F)));
      event.registerLayerDefinition(AetherModelLayers.PHYG_HALO, () -> HaloModel.createLayer(3.0F, -4.0F, 12.0F, -6.0F));
      event.registerLayerDefinition(AetherModelLayers.FLYING_COW, CowModel::createBodyLayer);
      event.registerLayerDefinition(AetherModelLayers.FLYING_COW_WINGS, () -> QuadrupedWingsModel.createMainLayer(0.0F));
      event.registerLayerDefinition(AetherModelLayers.FLYING_COW_SADDLE, CowModel::createBodyLayer);
      event.registerLayerDefinition(AetherModelLayers.SHEEPUFF, SheepuffModel::createBodyLayer);
      event.registerLayerDefinition(AetherModelLayers.SHEEPUFF_WOOL, () -> SheepuffWoolModel.createFurLayer(new CubeDeformation(1.75F), 0.0F));
      event.registerLayerDefinition(AetherModelLayers.SHEEPUFF_WOOL_PUFFED, () -> SheepuffWoolModel.createFurLayer(new CubeDeformation(3.75F), 2.0F));
      event.registerLayerDefinition(AetherModelLayers.AERBUNNY, AerbunnyModel::createBodyLayer);
      event.registerLayerDefinition(AetherModelLayers.MOA, () -> MoaModel.createBodyLayer(CubeDeformation.NONE));
      event.registerLayerDefinition(AetherModelLayers.MOA_HAT, () -> MoaModel.createBodyLayer(new CubeDeformation(0.23F)));
      event.registerLayerDefinition(AetherModelLayers.MOA_SADDLE, () -> MoaModel.createBodyLayer(new CubeDeformation(0.27F)));
      event.registerLayerDefinition(AetherModelLayers.AERWHALE, AerwhaleModel::createBodyLayer);
      event.registerLayerDefinition(AetherModelLayers.AERWHALE_CLASSIC, ClassicAerwhaleModel::createBodyLayer);
      event.registerLayerDefinition(AetherModelLayers.SWET, SlimeModel::createInnerBodyLayer);
      event.registerLayerDefinition(AetherModelLayers.SWET_OUTER, SlimeModel::createOuterBodyLayer);
      event.registerLayerDefinition(AetherModelLayers.AECHOR_PLANT, AechorPlantModel::createBodyLayer);
      event.registerLayerDefinition(AetherModelLayers.COCKATRICE, () -> CockatriceModel.createBodyLayer(CubeDeformation.NONE));
      event.registerLayerDefinition(AetherModelLayers.ZEPHYR, ZephyrModel::createBodyLayer);
      event.registerLayerDefinition(AetherModelLayers.ZEPHYR_TRANSPARENCY, ZephyrModel::createBodyLayer);
      event.registerLayerDefinition(AetherModelLayers.ZEPHYR_CLASSIC, ClassicZephyrModel::createBodyLayer);
      event.registerLayerDefinition(AetherModelLayers.MIMIC, MimicModel::createBodyLayer);
      event.registerLayerDefinition(AetherModelLayers.SENTRY, SlimeModel::createOuterBodyLayer);
      event.registerLayerDefinition(AetherModelLayers.VALKYRIE, ValkyrieModel::createBodyLayer);
      event.registerLayerDefinition(AetherModelLayers.VALKYRIE_WINGS, () -> ValkyrieWingsModel.createMainLayer(4.5F, 2.5F));
      event.registerLayerDefinition(AetherModelLayers.FIRE_MINION, SunSpiritModel::createBodyLayer);
      event.registerLayerDefinition(AetherModelLayers.SLIDER, SliderModel::createBodyLayer);
      event.registerLayerDefinition(AetherModelLayers.VALKYRIE_QUEEN, ValkyrieModel::createBodyLayer);
      event.registerLayerDefinition(AetherModelLayers.VALKYRIE_QUEEN_WINGS, () -> ValkyrieWingsModel.createMainLayer(4.5F, 2.5F));
      event.registerLayerDefinition(AetherModelLayers.SUN_SPIRIT, SunSpiritModel::createBodyLayer);
      event.registerLayerDefinition(AetherModelLayers.SKYROOT_BOAT, BoatModel::createBodyModel);
      event.registerLayerDefinition(AetherModelLayers.SKYROOT_CHEST_BOAT, ChestBoatModel::createBodyModel);
      event.registerLayerDefinition(AetherModelLayers.CLOUD_MINION, CloudMinionModel::createBodyLayer);
      event.registerLayerDefinition(AetherModelLayers.CLOUD_CRYSTAL, CrystalModel::createBodyLayer);
      event.registerLayerDefinition(AetherModelLayers.THUNDER_CRYSTAL, CrystalModel::createBodyLayer);
      event.registerLayerDefinition(AetherModelLayers.VALKYRIE_ARMOR_WINGS, () -> ValkyrieWingsModel.createMainLayer(3.5F, 3.375F));
      event.registerLayerDefinition(AetherModelLayers.PENDANT, PendantModel::createLayer);
      event.registerLayerDefinition(AetherModelLayers.GLOVES, () -> GlovesModel.createLayer(new CubeDeformation(0.5F), false, false));
      event.registerLayerDefinition(AetherModelLayers.GLOVES_TRIM, () -> GlovesModel.createLayer(new CubeDeformation(0.5F), false, true));
      event.registerLayerDefinition(AetherModelLayers.GLOVES_SLIM, () -> GlovesModel.createLayer(new CubeDeformation(0.5F), true, false));
      event.registerLayerDefinition(AetherModelLayers.GLOVES_TRIM_SLIM, () -> GlovesModel.createLayer(new CubeDeformation(0.5F), true, true));
      event.registerLayerDefinition(AetherModelLayers.GLOVES_FIRST_PERSON, () -> GlovesModel.createLayer(new CubeDeformation(0.25F), false, false));
      event.registerLayerDefinition(AetherModelLayers.GLOVES_TRIM_FIRST_PERSON, () -> GlovesModel.createLayer(new CubeDeformation(0.25F), false, true));
      event.registerLayerDefinition(
         AetherModelLayers.SHIELD_OF_REPULSION, () -> LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(1.1F), false), 64, 64)
      );
      event.registerLayerDefinition(
         AetherModelLayers.SHIELD_OF_REPULSION_SLIM, () -> LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(1.15F), true), 64, 64)
      );
      event.registerLayerDefinition(
         AetherModelLayers.SHIELD_OF_REPULSION_ARM, () -> LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.4F), false), 64, 64)
      );
      event.registerLayerDefinition(AetherModelLayers.CAPE, CapeModel::createLayer);
      event.registerLayerDefinition(AetherModelLayers.PLAYER_HALO, () -> HaloModel.createLayer(0.0F, 0.0F, 0.0F, 0.0F));
   }

   public static void registerAccessoryRenderers() {
      AccessoriesRendererRegistry.registerNoRenderer((Item)AetherItems.IRON_RING.get());
      AccessoriesRendererRegistry.registerNoRenderer((Item)AetherItems.GOLDEN_RING.get());
      AccessoriesRendererRegistry.registerNoRenderer((Item)AetherItems.ZANITE_RING.get());
      AccessoriesRendererRegistry.registerNoRenderer((Item)AetherItems.ICE_RING.get());
      AccessoriesRendererRegistry.registerRenderer((Item)AetherItems.IRON_PENDANT.get(), PendantRenderer::new);
      AccessoriesRendererRegistry.registerRenderer((Item)AetherItems.GOLDEN_PENDANT.get(), PendantRenderer::new);
      AccessoriesRendererRegistry.registerRenderer((Item)AetherItems.ZANITE_PENDANT.get(), PendantRenderer::new);
      AccessoriesRendererRegistry.registerRenderer((Item)AetherItems.ICE_PENDANT.get(), PendantRenderer::new);
      AccessoriesRendererRegistry.registerRenderer((Item)AetherItems.LEATHER_GLOVES.get(), GlovesRenderer::new);
      AccessoriesRendererRegistry.registerRenderer((Item)AetherItems.CHAINMAIL_GLOVES.get(), GlovesRenderer::new);
      AccessoriesRendererRegistry.registerRenderer((Item)AetherItems.IRON_GLOVES.get(), GlovesRenderer::new);
      AccessoriesRendererRegistry.registerRenderer((Item)AetherItems.GOLDEN_GLOVES.get(), GlovesRenderer::new);
      AccessoriesRendererRegistry.registerRenderer((Item)AetherItems.DIAMOND_GLOVES.get(), GlovesRenderer::new);
      AccessoriesRendererRegistry.registerRenderer((Item)AetherItems.NETHERITE_GLOVES.get(), GlovesRenderer::new);
      AccessoriesRendererRegistry.registerRenderer((Item)AetherItems.ZANITE_GLOVES.get(), GlovesRenderer::new);
      AccessoriesRendererRegistry.registerRenderer((Item)AetherItems.GRAVITITE_GLOVES.get(), GlovesRenderer::new);
      AccessoriesRendererRegistry.registerRenderer((Item)AetherItems.NEPTUNE_GLOVES.get(), GlovesRenderer::new);
      AccessoriesRendererRegistry.registerRenderer((Item)AetherItems.PHOENIX_GLOVES.get(), GlovesRenderer::new);
      AccessoriesRendererRegistry.registerRenderer((Item)AetherItems.OBSIDIAN_GLOVES.get(), GlovesRenderer::new);
      AccessoriesRendererRegistry.registerRenderer((Item)AetherItems.VALKYRIE_GLOVES.get(), GlovesRenderer::new);
      AccessoriesRendererRegistry.registerNoRenderer((Item)AetherItems.RED_CAPE.get());
      AccessoriesRendererRegistry.registerNoRenderer((Item)AetherItems.BLUE_CAPE.get());
      AccessoriesRendererRegistry.registerNoRenderer((Item)AetherItems.YELLOW_CAPE.get());
      AccessoriesRendererRegistry.registerNoRenderer((Item)AetherItems.WHITE_CAPE.get());
      AccessoriesRendererRegistry.registerNoRenderer((Item)AetherItems.AGILITY_CAPE.get());
      AccessoriesRendererRegistry.registerNoRenderer((Item)AetherItems.SWET_CAPE.get());
      AccessoriesRendererRegistry.registerNoRenderer((Item)AetherItems.INVISIBILITY_CLOAK.get());
      AccessoriesRendererRegistry.registerNoRenderer((Item)AetherItems.VALKYRIE_CAPE.get());
      AccessoriesRendererRegistry.registerNoRenderer((Item)AetherItems.GOLDEN_FEATHER.get());
      AccessoriesRendererRegistry.registerNoRenderer((Item)AetherItems.REGENERATION_STONE.get());
      AccessoriesRendererRegistry.registerNoRenderer((Item)AetherItems.IRON_BUBBLE.get());
      AccessoriesRendererRegistry.registerRenderer((Item)AetherItems.SHIELD_OF_REPULSION.get(), ShieldOfRepulsionRenderer::new);
   }

   public static void addEntityLayers(AddLayers event) {
      EntityRenderDispatcher renderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();

      for (Model type : event.getSkins()) {
         PlayerRenderer playerRenderer = (PlayerRenderer)event.getSkin(type);
         if (playerRenderer != null) {
            playerRenderer.addLayer(new DeveloperGlowLayer(playerRenderer));
            playerRenderer.addLayer(
               new DartLayer(
                  renderDispatcher,
                  playerRenderer,
                  entity -> new GoldenDart((EntityType<? extends GoldenDart>)AetherEntityTypes.GOLDEN_DART.get(), entity.level()),
                  AetherPlayerAttachment::getGoldenDartCount,
                  1.0F
               )
            );
            playerRenderer.addLayer(
               new DartLayer(
                  renderDispatcher,
                  playerRenderer,
                  entity -> new PoisonDart((EntityType<? extends PoisonDart>)AetherEntityTypes.POISON_DART.get(), entity.level()),
                  AetherPlayerAttachment::getPoisonDartCount,
                  2.0F
               )
            );
            playerRenderer.addLayer(
               new DartLayer(
                  renderDispatcher,
                  playerRenderer,
                  entity -> new EnchantedDart((EntityType<? extends EnchantedDart>)AetherEntityTypes.ENCHANTED_DART.get(), entity.level()),
                  AetherPlayerAttachment::getEnchantedDartCount,
                  3.0F
               )
            );
            playerRenderer.addLayer(new PlayerHaloLayer(playerRenderer, Minecraft.getInstance().getEntityModels()));
            playerRenderer.addLayer(new PlayerWingsLayer(playerRenderer, Minecraft.getInstance().getEntityModels()));
         }
      }

      LivingEntityRenderer<ArmorStand, ArmorStandModel> renderer = (LivingEntityRenderer<ArmorStand, ArmorStandModel>)event.getRenderer(EntityType.ARMOR_STAND);
      if (renderer != null) {
         renderer.addLayer(new ArmorStandCapeLayer(renderer));
      }
   }

   public static void bakeModels(ModifyBakingResult event) {
      List<Entry<ModelResourceLocation, BakedModel>> models = new ArrayList<>();

      for (Entry<ModelResourceLocation, BakedModel> model : event.getModels().entrySet()) {
         if (model.getKey().id().getNamespace().equals("aether")) {
            String path = model.getKey().id().getPath();
            if (path.equals(AetherBlocks.BERRY_BUSH.getId().getPath())) {
               models.add(model);
            } else if (path.equals(AetherBlocks.POTTED_BERRY_BUSH.getId().getPath())) {
               models.add(model);
            }
         }
      }

      models.forEach(entry -> event.getModels().put(entry.getKey(), new FastModel(entry.getValue())));
   }
}

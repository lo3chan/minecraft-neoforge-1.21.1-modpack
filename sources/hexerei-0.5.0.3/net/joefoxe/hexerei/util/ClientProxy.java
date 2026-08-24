package net.joefoxe.hexerei.util;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.joefoxe.hexerei.block.connected.BlockConnectivity;
import net.joefoxe.hexerei.block.connected.ModelSwapper;
import net.joefoxe.hexerei.block.connected.StitchedSprite;
import net.joefoxe.hexerei.client.renderer.entity.ModEntityTypes;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomBrushBaseModel;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomKeychainChainModel;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomKeychainModel;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomLargeSatchelModel;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomMediumSatchelModel;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomModel;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomNetheriteTipModel;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomRingsModel;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomSeatModel;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomSmallSatchelModel;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomStickBaseModel;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomThrusterBrushModel;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomWaterproofTipModel;
import net.joefoxe.hexerei.client.renderer.entity.model.CandleHerbLayer;
import net.joefoxe.hexerei.client.renderer.entity.model.CandleModel;
import net.joefoxe.hexerei.client.renderer.entity.model.CrowModel;
import net.joefoxe.hexerei.client.renderer.entity.model.MoonDustBrushModel;
import net.joefoxe.hexerei.client.renderer.entity.model.MushroomWitchArmorModel;
import net.joefoxe.hexerei.client.renderer.entity.model.OwlModel;
import net.joefoxe.hexerei.client.renderer.entity.model.WitchArmorModel;
import net.joefoxe.hexerei.client.renderer.entity.model.WitchHazelBroomStickModel;
import net.joefoxe.hexerei.client.renderer.entity.render.BroomRenderer;
import net.joefoxe.hexerei.client.renderer.entity.render.CrowRenderer;
import net.joefoxe.hexerei.client.renderer.entity.render.HexereiPaintingRenderer;
import net.joefoxe.hexerei.client.renderer.entity.render.ModBoatRenderer;
import net.joefoxe.hexerei.client.renderer.entity.render.ModChestBoatRenderer;
import net.joefoxe.hexerei.client.renderer.entity.render.OwlRenderer;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.item.ModItemProperties;
import net.joefoxe.hexerei.item.custom.BroomItem;
import net.joefoxe.hexerei.item.custom.CofferItem;
import net.joefoxe.hexerei.item.custom.HerbJarItem;
import net.joefoxe.hexerei.screen.tooltip.ClientBroomToolTip;
import net.joefoxe.hexerei.screen.tooltip.ClientCofferToolTip;
import net.joefoxe.hexerei.screen.tooltip.ClientHerbJarToolTip;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.tileentity.renderer.BookOfShadowsAltarRenderer;
import net.joefoxe.hexerei.tileentity.renderer.BroomStandRenderer;
import net.joefoxe.hexerei.tileentity.renderer.CandleDipperRenderer;
import net.joefoxe.hexerei.tileentity.renderer.CandleRenderer;
import net.joefoxe.hexerei.tileentity.renderer.CofferRenderer;
import net.joefoxe.hexerei.tileentity.renderer.CrystalBallRenderer;
import net.joefoxe.hexerei.tileentity.renderer.DryingRackRenderer;
import net.joefoxe.hexerei.tileentity.renderer.HerbJarRenderer;
import net.joefoxe.hexerei.tileentity.renderer.MixingCauldronRenderer;
import net.joefoxe.hexerei.tileentity.renderer.ModChestRenderer;
import net.joefoxe.hexerei.tileentity.renderer.OwlCourierDepotRenderer;
import net.joefoxe.hexerei.tileentity.renderer.PestleAndMortarRenderer;
import net.joefoxe.hexerei.tileentity.renderer.SageBurningPlateRenderer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(
   value = {Dist.CLIENT},
   modid = "hexerei",
   bus = Bus.MOD
)
public class ClientProxy implements SidedProxy {
   public static KeyMapping[] keys = null;
   public static final ModelLayerLocation CANDLE_HERB_LAYER = new ModelLayerLocation(HexereiUtil.getResource("candle_herb_layer"), "main");
   public static final ModelLayerLocation WITCH_ARMOR_LAYER = new ModelLayerLocation(HexereiUtil.getResource("witch_armor"), "main");
   public static final ModelLayerLocation MUSHROOM_WITCH_ARMOR_LAYER = new ModelLayerLocation(HexereiUtil.getResource("mushroom_witch_armor"), "main");
   public static final ModelLayerLocation READING_GLASSES_LAYER = new ModelLayerLocation(HexereiUtil.getResource("reading_glasses"), "main");
   public static final BlockConnectivity BLOCK_CONNECTIVITY = new BlockConnectivity();
   public static final ModelSwapper MODEL_SWAPPER = new ModelSwapper();
   public static Map<String, Font> fontList = new HashMap<>();
   public static int fontIndex = 0;
   public static final Map<Character, ResourceLocation> TEXT = Maps.newHashMap();
   public static final Map<Character, Float> TEXT_WIDTH = Maps.newHashMap();

   public static Font font() {
      if (fontIndex == 0) {
         return Minecraft.getInstance().font;
      } else {
         int index = fontIndex % ((List)HexConfig.FONT_LIST.get()).size();
         Font toReturn = fontList.get(((List)HexConfig.FONT_LIST.get()).get(index));
         return toReturn == null ? Minecraft.getInstance().font : toReturn;
      }
   }

   public static ResourceLocation fontId() {
      if (fontIndex == 0) {
         return null;
      } else {
         int index = fontIndex % ((List)HexConfig.FONT_LIST.get()).size();
         return ResourceLocation.parse((String)((List)HexConfig.FONT_LIST.get()).get(index));
      }
   }

   @Override
   public Player getPlayer() {
      return Minecraft.getInstance().player;
   }

   @Override
   public Level getLevel() {
      return Minecraft.getInstance().level;
   }

   @Override
   public void init() {
   }

   @Override
   public void openCodexGui() {
   }

   @SubscribeEvent
   public static void registerClientTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
      event.register(HerbJarItem.HerbJarToolTip.class, ClientHerbJarToolTip::new);
      event.register(CofferItem.CofferItemToolTip.class, ClientCofferToolTip::new);
      event.register(BroomItem.BroomItemToolTip.class, ClientBroomToolTip::new);
   }

   @SubscribeEvent
   public static void setup(RegisterRenderers e) {
      e.registerBlockEntityRenderer((BlockEntityType)ModTileEntities.CHEST_TILE.get(), ModChestRenderer::new);
      e.registerBlockEntityRenderer((BlockEntityType)ModTileEntities.SIGN_TILE.get(), SignRenderer::new);
      e.registerBlockEntityRenderer((BlockEntityType)ModTileEntities.HANGING_SIGN_TILE.get(), HangingSignRenderer::new);
      e.registerBlockEntityRenderer((BlockEntityType)ModTileEntities.MIXING_CAULDRON_TILE.get(), context -> new MixingCauldronRenderer());
      e.registerBlockEntityRenderer((BlockEntityType)ModTileEntities.COFFER_TILE.get(), context -> new CofferRenderer());
      e.registerBlockEntityRenderer((BlockEntityType)ModTileEntities.HERB_JAR_TILE.get(), context -> new HerbJarRenderer());
      e.registerBlockEntityRenderer((BlockEntityType)ModTileEntities.CRYSTAL_BALL_TILE.get(), context -> new CrystalBallRenderer());
      e.registerBlockEntityRenderer((BlockEntityType)ModTileEntities.BOOK_OF_SHADOWS_ALTAR_TILE.get(), context -> new BookOfShadowsAltarRenderer());
      e.registerBlockEntityRenderer((BlockEntityType)ModTileEntities.BROOM_STAND_TILE.get(), context -> new BroomStandRenderer());
      e.registerBlockEntityRenderer((BlockEntityType)ModTileEntities.OWL_COURIER_DEPOT_TILE.get(), context -> new OwlCourierDepotRenderer());
      e.registerBlockEntityRenderer((BlockEntityType)ModTileEntities.CANDLE_TILE.get(), context -> new CandleRenderer());
      e.registerBlockEntityRenderer((BlockEntityType)ModTileEntities.CANDLE_DIPPER_TILE.get(), context -> new CandleDipperRenderer());
      e.registerBlockEntityRenderer((BlockEntityType)ModTileEntities.DRYING_RACK_TILE.get(), context -> new DryingRackRenderer());
      e.registerBlockEntityRenderer((BlockEntityType)ModTileEntities.PESTLE_AND_MORTAR_TILE.get(), context -> new PestleAndMortarRenderer());
      e.registerBlockEntityRenderer((BlockEntityType)ModTileEntities.SAGE_BURNING_PLATE_TILE.get(), context -> new SageBurningPlateRenderer());
      e.registerEntityRenderer((EntityType)ModEntityTypes.BROOM.get(), BroomRenderer::new);
      e.registerEntityRenderer((EntityType)ModEntityTypes.HEXEREI_BOAT.get(), ModBoatRenderer::new);
      e.registerEntityRenderer((EntityType)ModEntityTypes.HEXEREI_CHEST_BOAT.get(), ModChestBoatRenderer::new);
      e.registerEntityRenderer((EntityType)ModEntityTypes.CROW.get(), CrowRenderer::new);
      e.registerEntityRenderer((EntityType)ModEntityTypes.OWL.get(), OwlRenderer::new);
      e.registerEntityRenderer((EntityType)ModEntityTypes.BOOK_CANVAS.get(), HexereiPaintingRenderer::new);
      ModItemProperties.setup();
   }

   @SubscribeEvent
   public static void layerDefinitions(RegisterLayerDefinitions event) {
      event.registerLayerDefinition(BroomModel.LAYER_LOCATION, BroomModel::createBodyLayerNone);
      event.registerLayerDefinition(BroomModel.POWER_LAYER_LOCATION, BroomModel::createBodyLayerEnlarge);
      event.registerLayerDefinition(OwlModel.LAYER_LOCATION, OwlModel::createBodyLayerNone);
      event.registerLayerDefinition(CrowModel.LAYER_LOCATION, CrowModel::createBodyLayerNone);
      event.registerLayerDefinition(CrowModel.POWER_LAYER_LOCATION, CrowModel::createBodyLayerEnlarge);
      event.registerLayerDefinition(BroomBrushBaseModel.LAYER_LOCATION, BroomBrushBaseModel::createBodyLayerNone);
      event.registerLayerDefinition(BroomBrushBaseModel.POWER_LAYER_LOCATION, BroomBrushBaseModel::createBodyLayerEnlarge);
      event.registerLayerDefinition(BroomStickBaseModel.LAYER_LOCATION, BroomStickBaseModel::createBodyLayerNone);
      event.registerLayerDefinition(BroomStickBaseModel.POWER_LAYER_LOCATION, BroomStickBaseModel::createBodyLayerEnlarge);
      event.registerLayerDefinition(WitchHazelBroomStickModel.LAYER_LOCATION, WitchHazelBroomStickModel::createBodyLayerNone);
      event.registerLayerDefinition(WitchHazelBroomStickModel.POWER_LAYER_LOCATION, WitchHazelBroomStickModel::createBodyLayerEnlarge);
      event.registerLayerDefinition(BroomRingsModel.LAYER_LOCATION, BroomRingsModel::createBodyLayer);
      event.registerLayerDefinition(BroomRingsModel.LAYER_LOCATION, BroomRingsModel::createBodyLayer);
      event.registerLayerDefinition(BroomSmallSatchelModel.LAYER_LOCATION, BroomSmallSatchelModel::createBodyLayer);
      event.registerLayerDefinition(BroomMediumSatchelModel.LAYER_LOCATION, BroomMediumSatchelModel::createBodyLayer);
      event.registerLayerDefinition(BroomLargeSatchelModel.LAYER_LOCATION, BroomLargeSatchelModel::createBodyLayer);
      event.registerLayerDefinition(BroomSeatModel.LAYER_LOCATION, BroomSeatModel::createBodyLayer);
      event.registerLayerDefinition(BroomKeychainModel.LAYER_LOCATION, BroomKeychainModel::createBodyLayer);
      event.registerLayerDefinition(BroomKeychainChainModel.LAYER_LOCATION, BroomKeychainChainModel::createBodyLayer);
      event.registerLayerDefinition(BroomNetheriteTipModel.LAYER_LOCATION, BroomNetheriteTipModel::createBodyLayer);
      event.registerLayerDefinition(BroomWaterproofTipModel.LAYER_LOCATION, BroomWaterproofTipModel::createBodyLayer);
      event.registerLayerDefinition(BroomThrusterBrushModel.LAYER_LOCATION, BroomThrusterBrushModel::createBodyLayerNone);
      event.registerLayerDefinition(BroomThrusterBrushModel.POWER_LAYER_LOCATION, BroomThrusterBrushModel::createBodyLayerEnlarge);
      event.registerLayerDefinition(MoonDustBrushModel.LAYER_LOCATION, MoonDustBrushModel::createBodyLayerNone);
      event.registerLayerDefinition(MoonDustBrushModel.POWER_LAYER_LOCATION, MoonDustBrushModel::createBodyLayerEnlarge);
      event.registerLayerDefinition(CANDLE_HERB_LAYER, CandleHerbLayer::createBodyLayer);
      event.registerLayerDefinition(CandleModel.CANDLE_LAYER, CandleModel::createBodyLayer);
      event.registerLayerDefinition(CandleModel.CANDLE_BASE_LAYER, CandleModel::createBaseLayer);
      event.registerLayerDefinition(CandleModel.CANDLE_HERB_LAYER, CandleModel::createBodyLayerHerb);
      event.registerLayerDefinition(CandleModel.CANDLE_GLOW_LAYER, CandleModel::createBodyLayerGlow);
      event.registerLayerDefinition(CandleModel.CANDLE_SWIRL_LAYER, CandleModel::createBodyLayerSwirl);
      event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("boat/willow"), "main"), BoatModel::createBodyModel);
      event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("boat/polished_willow"), "main"), BoatModel::createBodyModel);
      event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("boat/witch_hazel"), "main"), BoatModel::createBodyModel);
      event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("boat/polished_witch_hazel"), "main"), BoatModel::createBodyModel);
      event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("boat/mahogany"), "main"), BoatModel::createBodyModel);
      event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("boat/polished_mahogany"), "main"), BoatModel::createBodyModel);
      event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("chest_boat/willow"), "main"), ChestBoatModel::createBodyModel);
      event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("chest_boat/polished_willow"), "main"), ChestBoatModel::createBodyModel);
      event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("chest_boat/witch_hazel"), "main"), ChestBoatModel::createBodyModel);
      event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("chest_boat/polished_witch_hazel"), "main"), ChestBoatModel::createBodyModel);
      event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("chest_boat/mahogany"), "main"), ChestBoatModel::createBodyModel);
      event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("chest_boat/polished_mahogany"), "main"), ChestBoatModel::createBodyModel);
      event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("chest/mahogany"), "main"), ModChestRenderer::createSingleBodyLayer);
      event.registerLayerDefinition(
         new ModelLayerLocation(HexereiUtil.getResource("chest/mahogany_right"), "main"), ModChestRenderer::createDoubleBodyRightLayer
      );
      event.registerLayerDefinition(new ModelLayerLocation(HexereiUtil.getResource("chest/mahogany_left"), "main"), ModChestRenderer::createDoubleBodyLeftLayer);
      initArmors(event::registerLayerDefinition);
   }

   public static void initArmors(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> consumer) {
      consumer.accept(WITCH_ARMOR_LAYER, () -> LayerDefinition.create(WitchArmorModel.createBodyLayer(CubeDeformation.NONE), 128, 128));
      consumer.accept(MUSHROOM_WITCH_ARMOR_LAYER, () -> LayerDefinition.create(MushroomWitchArmorModel.createBodyLayer(), 128, 128));
   }

   @SubscribeEvent
   public static void onTextureStitch(TextureAtlasStitchedEvent event) {
      TextureAtlas atlas = event.getAtlas();
      ResourceLocation atlasLocation = atlas.location();
      List<StitchedSprite> sprites = StitchedSprite.ALL.get(atlasLocation);
      if (sprites != null) {
         for (StitchedSprite sprite : sprites) {
            sprite.loadSprite(atlas);
         }
      }
   }

   public static void registerTextLocations() {
   }
}

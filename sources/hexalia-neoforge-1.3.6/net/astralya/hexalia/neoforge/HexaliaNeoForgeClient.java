package net.astralya.hexalia.neoforge;

import net.astralya.hexalia.Hexalia;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.client.model.PestleModel;
import net.astralya.hexalia.client.renderer.blockentity.CenserBlockEntityRenderer;
import net.astralya.hexalia.client.renderer.blockentity.MortarAndPestleBlockEntityRenderer;
import net.astralya.hexalia.client.renderer.blockentity.RitualBrazierBlockEntityRenderer;
import net.astralya.hexalia.client.renderer.blockentity.RitualTableBlockEntityRenderer;
import net.astralya.hexalia.client.renderer.blockentity.ShelfBlockEntityRenderer;
import net.astralya.hexalia.client.renderer.blockentity.SmallCauldronBlockEntityRenderer;
import net.astralya.hexalia.client.renderer.entity.CacofeyRenderer;
import net.astralya.hexalia.client.renderer.entity.ModBoatRenderer;
import net.astralya.hexalia.client.renderer.entity.SilkMothRenderer;
import net.astralya.hexalia.client.renderer.entity.ThornArrowRenderer;
import net.astralya.hexalia.client.screen.NestingBlockScreen;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.entity.boat.ModBoatEntity;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.menu.ModMenuTypes;
import net.astralya.hexalia.particle.ModParticleTypes;
import net.astralya.hexalia.particle.custom.CacofeyDustHeldParticle;
import net.astralya.hexalia.particle.custom.CacofeyDustParticle;
import net.astralya.hexalia.particle.custom.InfusedBubbleParticle;
import net.astralya.hexalia.particle.custom.LeavesParticle;
import net.astralya.hexalia.particle.custom.SparkleParticle;
import net.astralya.hexalia.particle.custom.SporeParticle;
import net.astralya.hexalia.util.ModItemProperties;
import net.astralya.hexalia.util.ModWoodTypes;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Block;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Item;

public final class HexaliaNeoForgeClient {
   private HexaliaNeoForgeClient() {
   }

   public static void init(IEventBus modEventBus) {
      Hexalia.initClient();
      modEventBus.addListener(HexaliaNeoForgeClient::registerRenderers);
      modEventBus.addListener(HexaliaNeoForgeClient::registerParticles);
      modEventBus.addListener(HexaliaNeoForgeClient::registerLayerDefinitions);
      modEventBus.addListener(HexaliaNeoForgeClient::registerScreens);
      modEventBus.addListener(HexaliaNeoForgeClient::registerBlockColors);
      modEventBus.addListener(HexaliaNeoForgeClient::registerItemColors);
      modEventBus.addListener(HexaliaNeoForgeClient::setupClient);
   }

   private static void registerRenderers(RegisterRenderers event) {
      event.registerBlockEntityRenderer((BlockEntityType)ModBlockEntityTypes.RITUAL_TABLE.get(), RitualTableBlockEntityRenderer::new);
      event.registerBlockEntityRenderer((BlockEntityType)ModBlockEntityTypes.RITUAL_BRAZIER.get(), RitualBrazierBlockEntityRenderer::new);
      event.registerBlockEntityRenderer((BlockEntityType)ModBlockEntityTypes.SMALL_CAULDRON.get(), SmallCauldronBlockEntityRenderer::new);
      event.registerBlockEntityRenderer((BlockEntityType)ModBlockEntityTypes.MORTAR_AND_PESTLE.get(), MortarAndPestleBlockEntityRenderer::new);
      event.registerBlockEntityRenderer((BlockEntityType)ModBlockEntityTypes.CENSER.get(), CenserBlockEntityRenderer::new);
      event.registerBlockEntityRenderer((BlockEntityType)ModBlockEntityTypes.SHELF.get(), ShelfBlockEntityRenderer::new);
      event.registerBlockEntityRenderer((BlockEntityType)ModBlockEntityTypes.MOD_SIGN.get(), SignRenderer::new);
      event.registerBlockEntityRenderer((BlockEntityType)ModBlockEntityTypes.MOD_HANGING_SIGN.get(), HangingSignRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntities.SILK_MOTH.get(), SilkMothRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntities.CACOFEY.get(), CacofeyRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntities.MOD_BOAT.get(), context -> new ModBoatRenderer(context, false));
      event.registerEntityRenderer((EntityType)ModEntities.MOD_CHEST_BOAT.get(), context -> new ModBoatRenderer(context, true));
      event.registerEntityRenderer((EntityType)ModEntities.RABBAGE.get(), ThrownItemRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntities.PURIFYING_SAC.get(), ThrownItemRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntities.FOUL_SAC.get(), ThrownItemRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntities.FROST_SAC.get(), ThrownItemRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntities.SEARING_SAC.get(), ThrownItemRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntities.THORN_ARROW.get(), ThornArrowRenderer::new);
   }

   private static void registerParticles(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet((ParticleType)ModParticleTypes.SPORE.get(), SporeParticle.Factory::new);
      event.registerSpriteSet((ParticleType)ModParticleTypes.SPARKLE.get(), SparkleParticle.Factory::new);
      event.registerSpriteSet((ParticleType)ModParticleTypes.LEAVES.get(), LeavesParticle.Factory::new);
      event.registerSpriteSet((ParticleType)ModParticleTypes.INFUSED_BUBBLES.get(), InfusedBubbleParticle.Factory::new);
      event.registerSpriteSet((ParticleType)ModParticleTypes.CACOFEY_DUST.get(), CacofeyDustParticle.Factory::new);
      event.registerSpriteSet((ParticleType)ModParticleTypes.CACOFEY_DUST_HELD.get(), CacofeyDustHeldParticle.Factory::new);
   }

   private static void registerLayerDefinitions(RegisterLayerDefinitions event) {
      event.registerLayerDefinition(PestleModel.LAYER_LOCATION, PestleModel::createLayer);

      for (ModBoatEntity.Type type : ModBoatEntity.Type.values()) {
         event.registerLayerDefinition(ModBoatRenderer.createBoatModelName(type), BoatModel::createBodyModel);
         event.registerLayerDefinition(ModBoatRenderer.createChestBoatModelName(type), ChestBoatModel::createBodyModel);
      }
   }

   private static void registerScreens(RegisterMenuScreensEvent event) {
      event.register((MenuType)ModMenuTypes.NESTING_BLOCK.get(), NestingBlockScreen::new);
   }

   private static void registerBlockColors(Block event) {
      event.register(
         (state, level, pos, tintIndex) -> level != null && pos != null ? BiomeColors.getAverageFoliageColor(level, pos) : FoliageColor.getDefaultColor(),
         new net.minecraft.world.level.block.Block[]{
            (net.minecraft.world.level.block.Block)ModBlocks.COTTONWOOD_LEAVES.get(), (net.minecraft.world.level.block.Block)ModBlocks.WILLOW_LEAVES.get()
         }
      );
   }

   private static void registerItemColors(Item event) {
      event.register(
         (stack, tintIndex) -> FoliageColor.getDefaultColor(),
         new ItemLike[]{(ItemLike)ModItems.COTTONWOOD_LEAVES.get(), (ItemLike)ModItems.WILLOW_LEAVES.get()}
      );
   }

   private static void setupClient(FMLClientSetupEvent event) {
      event.enqueueWork(() -> {
         Sheets.addWoodType(ModWoodTypes.COTTONWOOD);
         Sheets.addWoodType(ModWoodTypes.WILLOW);
         registerCutoutBlocks();
         ModItemProperties.register();
      });
   }

   private static void registerCutoutBlocks() {
      RenderType cutout = RenderType.cutout();
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.SILKWORM_COCOON.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.RITUAL_BRAZIER.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.SMALL_CAULDRON.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.CENSER.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.DREAMCATCHER.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.CANDLE_SKULL.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.WITHER_CANDLE_SKULL.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.MORPHORA.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.POTTED_MORPHORA.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.GRIMSHADE.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.POTTED_GRIMSHADE.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.NAUTILITE.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.WINDSONG.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.POTTED_WINDSONG.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.ASTRYLIS.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.POTTED_ASTRYLIS.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.LOURDES.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.POTTED_LOURDES.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.AEGIFLORA.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.POTTED_AEGIFLORA.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.WITHERED_AEGIFLORA.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.POTTED_WITHERED_AEGIFLORA.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.BEGONIA.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.POTTED_BEGONIA.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.LAVENDER.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.POTTED_LAVENDER.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.DAHLIA.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.POTTED_DAHLIA.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.NIGHTSHADE_BUSH.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.POTTED_NIGHTSHADE_BUSH.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.SPIRIT_BLOOM.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.POTTED_SPIRIT_BLOOM.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.DREAMSHROOM.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.POTTED_DREAMSHROOM.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.PALE_MUSHROOM.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.SIREN_KELP.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.GHOST_FERN.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.POTTED_GHOST_FERN.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.CELESTIAL_BLOOM.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.POTTED_CELESTIAL_BLOOM.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.WITHERED_CELESTIAL_BLOOM.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.POTTED_WITHERED_CELESTIAL_BLOOM.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.LOTUS_FLOWER.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.WITCHWEED.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.MANDRAKE_CROP.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.SUNFIRE_TOMATO_CROP.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.RABBAGE_CROP.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.WILD_MANDRAKE.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.WILD_SUNFIRE_TOMATO.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.CHILLBERRY_BUSH.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.SALTSPROUT.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.GALEBERRIES_VINE.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.GALEBERRIES_VINE_PLANT.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.SALT_LAMP.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.COTTONWOOD_CATKIN.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.COTTONWOOD_SAPLING.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.POTTED_COTTONWOOD_SAPLING.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.COTTONWOOD_TRAPDOOR.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.COTTONWOOD_DOOR.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.WILLOW_SAPLING.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.POTTED_WILLOW_SAPLING.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.WILLOW_TRAPDOOR.get(), cutout);
      ItemBlockRenderTypes.setRenderLayer((net.minecraft.world.level.block.Block)ModBlocks.WILLOW_DOOR.get(), cutout);
   }
}

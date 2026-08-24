package at.petrak.hexcasting.forge;

import at.petrak.hexcasting.client.ClientTickCounter;
import at.petrak.hexcasting.client.RegisterClientStuff;
import at.petrak.hexcasting.client.ShiftScrollListener;
import at.petrak.hexcasting.client.gui.PatternTooltipComponent;
import at.petrak.hexcasting.client.model.AltioraLayer;
import at.petrak.hexcasting.client.model.HexModelLayers;
import at.petrak.hexcasting.client.render.HexAdditionalRenderers;
import at.petrak.hexcasting.client.render.shader.HexShaders;
import at.petrak.hexcasting.common.casting.PatternRegistryManifest;
import at.petrak.hexcasting.common.lib.HexParticles;
import at.petrak.hexcasting.common.misc.PatternTooltip;
import at.petrak.hexcasting.forge.interop.curios.CuriosRenderers;
import at.petrak.hexcasting.forge.xplat.ForgeClientXplatImpl;
import at.petrak.hexcasting.interop.HexInterop;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.AddLayers;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.client.event.ModelEvent.BakingCompleted;
import net.neoforged.neoforge.client.event.ModelEvent.RegisterAdditional;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import net.neoforged.neoforge.common.NeoForge;

public class ForgeHexClientInitializer {
   public static ItemColors GLOBAL_ITEM_COLORS;
   public static BlockColors GLOBAL_BLOCK_COLORS;

   @SubscribeEvent
   public static void clientInit(FMLClientSetupEvent evt) {
      evt.enqueueWork(
         () -> {
            RegisterClientStuff.init();
            RegisterClientStuff.registerColorProviders(
               (colorizer, item) -> GLOBAL_ITEM_COLORS.register(colorizer, new ItemLike[]{item}),
               (colorizer, block) -> GLOBAL_BLOCK_COLORS.register(colorizer, new Block[]{block})
            );
            if (ModList.get().isLoaded("curios")) {
               CuriosRenderers.register();
            }
         }
      );
      IEventBus evBus = NeoForge.EVENT_BUS;
      evBus.addListener(e -> PatternRegistryManifest.processRegistry(null));
      evBus.addListener(e -> {
         if (e.getStage().equals(Stage.AFTER_PARTICLES)) {
            HexAdditionalRenderers.overlayLevel(e.getPoseStack(), e.getPartialTick().getGameTimeDeltaPartialTick(false));
         }
      });
      evBus.addListener(e -> HexAdditionalRenderers.overlayGui(e.getGuiGraphics(), e.getPartialTick().getGameTimeDeltaPartialTick(false)));
      evBus.addListener(e -> ClientTickCounter.renderTickStart(e.getPartialTick().getGameTimeDeltaPartialTick(false)));
      evBus.addListener(e -> {
         ClientTickCounter.clientTickEnd();
         ShiftScrollListener.clientTickEnd();
         ForgeClientXplatImpl.tickClientCastingStack();
      });
      evBus.addListener(e -> {
         boolean cancel = ShiftScrollListener.onScrollInGameplay(e.getScrollDeltaY());
         e.setCanceled(cancel);
      });
      HexInterop.clientInit();
   }

   @SubscribeEvent
   public static void registerShaders(RegisterShadersEvent evt) throws IOException {
      HexShaders.init(evt.getResourceProvider(), p -> evt.registerShader((ShaderInstance)p.getFirst(), (Consumer)p.getSecond()));
   }

   @SubscribeEvent
   public static void registerParticles(final RegisterParticleProvidersEvent evt) {
      HexParticles.FactoryHandler.registerFactories(new HexParticles.FactoryHandler.Consumer() {
         @Override
         public <T extends ParticleOptions> void register(ParticleType<T> type, Function<SpriteSet, ParticleProvider<T>> constructor) {
            evt.registerSpriteSet(type, constructor::apply);
         }
      });
   }

   @SubscribeEvent
   public static void registerRenderers(RegisterRenderers evt) {
      RegisterClientStuff.registerBlockEntityRenderers(evt::registerBlockEntityRenderer);
   }

   @SubscribeEvent
   public static void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent evt) {
      evt.register(PatternTooltip.class, PatternTooltipComponent::new);
   }

   @SubscribeEvent
   public static void onModelRegister(RegisterAdditional evt) {
      ResourceManager recMan = Minecraft.getInstance().getResourceManager();
      RegisterClientStuff.onModelRegister(recMan, rl -> evt.register(ModelResourceLocation.standalone(rl)));
   }

   @SubscribeEvent
   public static void onModelBake(BakingCompleted evt) {
      RegisterClientStuff.onModelBake(evt.getModelBakery(), evt.getModels());
   }

   @SubscribeEvent
   public static void registerEntityLayers(RegisterLayerDefinitions evt) {
      HexModelLayers.init(evt::registerLayerDefinition);
      if (ModList.get().isLoaded("curios")) {
         CuriosRenderers.onLayerRegister(evt);
      }
   }

   @SubscribeEvent
   public static void addPlayerLayers(AddLayers evt) {
      evt.getSkins()
         .forEach(
            skinName -> {
               LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> skin = (LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>)evt.getSkin(
                  skinName
               );
               skin.addLayer(new AltioraLayer(skin, evt.getEntityModels()));
            }
         );
   }
}

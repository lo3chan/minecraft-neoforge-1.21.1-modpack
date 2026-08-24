package fuzs.puzzleslib.neoforge.impl.client.event;

import com.google.common.base.Stopwatch;
import com.mojang.blaze3d.shaders.FogShape;
import fuzs.puzzleslib.api.client.event.v1.AddResourcePackReloadListenersCallback;
import fuzs.puzzleslib.api.client.event.v1.ClientLifecycleEvents;
import fuzs.puzzleslib.api.client.event.v1.ClientSetupCallback;
import fuzs.puzzleslib.api.client.event.v1.ClientTickEvents;
import fuzs.puzzleslib.api.client.event.v1.InputEvents;
import fuzs.puzzleslib.api.client.event.v1.ModelEvents;
import fuzs.puzzleslib.api.client.event.v1.entity.ClientEntityLevelEvents;
import fuzs.puzzleslib.api.client.event.v1.entity.player.ClientPlayerCopyCallback;
import fuzs.puzzleslib.api.client.event.v1.entity.player.ClientPlayerNetworkEvents;
import fuzs.puzzleslib.api.client.event.v1.entity.player.ComputeFovModifierCallback;
import fuzs.puzzleslib.api.client.event.v1.entity.player.InteractionInputEvents;
import fuzs.puzzleslib.api.client.event.v1.entity.player.MovementInputUpdateCallback;
import fuzs.puzzleslib.api.client.event.v1.gui.AddToastCallback;
import fuzs.puzzleslib.api.client.event.v1.gui.ChatMessageReceivedCallback;
import fuzs.puzzleslib.api.client.event.v1.gui.ContainerScreenEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.CustomizeChatPanelCallback;
import fuzs.puzzleslib.api.client.event.v1.gui.GatherDebugTextEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.GatherEffectScreenTooltipCallback;
import fuzs.puzzleslib.api.client.event.v1.gui.InventoryMobEffectsCallback;
import fuzs.puzzleslib.api.client.event.v1.gui.ItemTooltipCallback;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiCallback;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiLayerEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderTooltipCallback;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenKeyboardEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenMouseEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenOpeningCallback;
import fuzs.puzzleslib.api.client.event.v1.level.ClientChunkEvents;
import fuzs.puzzleslib.api.client.event.v1.level.ClientLevelEvents;
import fuzs.puzzleslib.api.client.event.v1.level.ClientLevelTickEvents;
import fuzs.puzzleslib.api.client.event.v1.renderer.AddLivingEntityRenderLayersCallback;
import fuzs.puzzleslib.api.client.event.v1.renderer.ComputeCameraAnglesCallback;
import fuzs.puzzleslib.api.client.event.v1.renderer.ComputeFieldOfViewCallback;
import fuzs.puzzleslib.api.client.event.v1.renderer.FogEvents;
import fuzs.puzzleslib.api.client.event.v1.renderer.GameRenderEvents;
import fuzs.puzzleslib.api.client.event.v1.renderer.RenderBlockOverlayCallback;
import fuzs.puzzleslib.api.client.event.v1.renderer.RenderHandEvents;
import fuzs.puzzleslib.api.client.event.v1.renderer.RenderHighlightCallback;
import fuzs.puzzleslib.api.client.event.v1.renderer.RenderLevelEvents;
import fuzs.puzzleslib.api.client.event.v1.renderer.RenderLivingEvents;
import fuzs.puzzleslib.api.client.event.v1.renderer.RenderNameTagCallback;
import fuzs.puzzleslib.api.client.event.v1.renderer.RenderPlayerEvents;
import fuzs.puzzleslib.api.core.v1.resources.ForwardingReloadListenerHelper;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import fuzs.puzzleslib.api.event.v1.data.MutableBoolean;
import fuzs.puzzleslib.api.event.v1.data.MutableDouble;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.client.event.ModelLoadingHelper;
import fuzs.puzzleslib.impl.client.event.ScreenButtonList;
import fuzs.puzzleslib.neoforge.api.event.v1.core.NeoForgeEventInvokerRegistry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin.Model;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.GatherEffectScreenTooltipsEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RenderBlockScreenEffectEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.ToastAddEvent;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent.System;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.Clone;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingIn;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.client.event.ClientTickEvent.Pre;
import net.neoforged.neoforge.client.event.ContainerScreenEvent.Render.Background;
import net.neoforged.neoforge.client.event.ContainerScreenEvent.Render.Foreground;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent.Chat;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent.DebugText;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.AddLayers;
import net.neoforged.neoforge.client.event.InputEvent.InteractionKeyMappingTriggered;
import net.neoforged.neoforge.client.event.InputEvent.Key;
import net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent;
import net.neoforged.neoforge.client.event.ModelEvent.BakingCompleted;
import net.neoforged.neoforge.client.event.ModelEvent.ModifyBakingResult;
import net.neoforged.neoforge.client.event.RenderHighlightEvent.Block;
import net.neoforged.neoforge.client.event.RenderHighlightEvent.Entity;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import net.neoforged.neoforge.client.event.ScreenEvent.Closing;
import net.neoforged.neoforge.client.event.ScreenEvent.Opening;
import net.neoforged.neoforge.client.event.ScreenEvent.RenderInventoryMobEffects;
import net.neoforged.neoforge.client.event.ViewportEvent.ComputeCameraAngles;
import net.neoforged.neoforge.client.event.ViewportEvent.ComputeFogColor;
import net.neoforged.neoforge.client.event.ViewportEvent.ComputeFov;
import net.neoforged.neoforge.client.event.ViewportEvent.RenderFog;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.level.ChunkEvent.Load;
import net.neoforged.neoforge.event.level.ChunkEvent.Unload;

public final class NeoForgeClientEventInvokers {
   public static void registerLoadingHandlers() {
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(ClientSetupCallback.class, FMLClientSetupEvent.class, (callback, event) -> event.enqueueWork(callback::onClientSetup));
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            AddResourcePackReloadListenersCallback.class,
            RegisterClientReloadListenersEvent.class,
            (callback, evt) -> callback.onAddResourcePackReloadListeners(
               (resourceLocation, reloadListener) -> evt.registerReloadListener(
                  ForwardingReloadListenerHelper.fromReloadListener(resourceLocation, reloadListener)
               )
            )
         );
      NeoForgeEventInvokerRegistry.INSTANCE.register(ScreenOpeningCallback.class, Opening.class, (callback, evt) -> {
         DefaultedValue<Screen> newScreen = DefaultedValue.fromEvent(evt::setNewScreen, evt::getNewScreen, evt::getScreen);
         EventResult result = callback.onScreenOpening(evt.getCurrentScreen(), newScreen);
         if (result.isInterrupt() || newScreen.getAsOptional().filter(screen -> screen == evt.getCurrentScreen()).isPresent()) {
            evt.setCanceled(true);
         }
      });
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            ModelEvents.ModifyUnbakedModel.class,
            ModifyBakingResult.class,
            (callback, evt) -> {
               Stopwatch stopwatch = Stopwatch.createStarted();
               Map<ModelResourceLocation, BakedModel> models = evt.getModels();
               BakedModel missingModel = models.get(ModelBakery.MISSING_MODEL_VARIANT);
               Objects.requireNonNull(missingModel, "missing model is null");
               Map<ModelResourceLocation, UnbakedModel> additionalModels = new HashMap<>();
               Function<ModelResourceLocation, UnbakedModel> modelGetter = modelResourceLocation -> additionalModels.containsKey(modelResourceLocation)
                  ? additionalModels.get(modelResourceLocation)
                  : ModelLoadingHelper.getUnbakedTopLevelModel(evt.getModelBakery()).apply(modelResourceLocation);
               Map<UnbakedModel, BakedModel> unbakedCache = new IdentityHashMap<>();

               for (ModelResourceLocation modelLocation : getTopLevelModelLocations()) {
                  try {
                     EventResultHolder<UnbakedModel> result = callback.onModifyUnbakedModel(
                        modelLocation,
                        () -> modelGetter.apply(modelLocation),
                        modelGetter,
                        (resourceLocation, unbakedModelx) -> additionalModels.put(ModelResourceLocation.standalone(resourceLocation), unbakedModelx)
                     );
                     if (result.isInterrupt()) {
                        UnbakedModel unbakedModel = result.getInterrupt().get();
                        additionalModels.put(modelLocation, unbakedModel);
                        BakedModel bakedModel = unbakedCache.computeIfAbsent(unbakedModel, $ -> {
                           NeoForgeModelBakerImpl modelBaker = NeoForgeModelBakerImpl.create(evt, missingModel, additionalModels);
                           return modelBaker.bake(unbakedModel, modelLocation.id());
                        });
                        models.put(modelLocation, bakedModel);
                     }
                  } catch (Exception var13) {
                     PuzzlesLib.LOGGER.error("Failed to modify unbaked model", var13);
                  }
               }

               PuzzlesLib.LOGGER.info("Modifying unbaked models took {}ms", stopwatch.stop().elapsed().toMillis());
            }
         );
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            ModelEvents.ModifyBakedModel.class,
            ModifyBakingResult.class,
            (callback, evt) -> {
               Stopwatch stopwatch = Stopwatch.createStarted();
               Map<ModelResourceLocation, BakedModel> models = evt.getModels();
               BakedModel missingModel = models.get(ModelBakery.MISSING_MODEL_VARIANT);
               Objects.requireNonNull(missingModel, "missing model is null");
               Function<ModelResourceLocation, BakedModel> modelGetter = resourceLocation -> {
                  if (models.containsKey(resourceLocation)) {
                     return models.get(resourceLocation);
                  } else {
                     ModelBaker modelBaker = NeoForgeModelBakerImpl.create(evt, missingModel);
                     return modelBaker.bake(resourceLocation.id(), BlockModelRotation.X0_Y0, evt.getTextureGetter());
                  }
               };

               for (ModelResourceLocation modelLocation : getTopLevelModelLocations()) {
                  try {
                     EventResultHolder<BakedModel> result = callback.onModifyBakedModel(
                        modelLocation,
                        () -> modelGetter.apply(modelLocation),
                        () -> NeoForgeModelBakerImpl.create(evt, missingModel),
                        modelGetter,
                        models::putIfAbsent
                     );
                     result.getInterrupt().ifPresent(bakedModel -> models.put(modelLocation, bakedModel));
                  } catch (Exception var9) {
                     PuzzlesLib.LOGGER.error("Failed to modify baked model", var9);
                  }
               }

               PuzzlesLib.LOGGER.info("Modifying baked models took {}ms", stopwatch.stop().elapsed().toMillis());
            }
         );
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            ModelEvents.AddAdditionalBakedModel.class,
            ModifyBakingResult.class,
            (callback, evt) -> {
               Stopwatch stopwatch = Stopwatch.createStarted();
               Map<ModelResourceLocation, BakedModel> models = evt.getModels();
               BakedModel missingModel = models.get(ModelBakery.MISSING_MODEL_VARIANT);
               Objects.requireNonNull(missingModel, "missing model is null");

               try {
                  callback.onAddAdditionalBakedModel(
                     models::putIfAbsent,
                     resourceLocation -> models.getOrDefault(resourceLocation, missingModel),
                     () -> NeoForgeModelBakerImpl.create(evt, missingModel)
                  );
               } catch (Exception var6) {
                  PuzzlesLib.LOGGER.error("Failed to add additional baked models", var6);
               }

               PuzzlesLib.LOGGER.info("Adding additional baked models took {}ms", stopwatch.stop().elapsed().toMillis());
            }
         );
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            ModelEvents.CompleteModelLoading.class,
            BakingCompleted.class,
            (callback, evt) -> callback.onCompleteModelLoading(evt::getModelManager, evt::getModelBakery)
         );
      NeoForgeEventInvokerRegistry.INSTANCE.register(ClientLifecycleEvents.Started.class, (callback, context) -> {});
      NeoForgeEventInvokerRegistry.INSTANCE.register(ClientLifecycleEvents.Stopping.class, (callback, context) -> {});
      NeoForgeEventInvokerRegistry.INSTANCE.register(AddLivingEntityRenderLayersCallback.class, AddLayers.class, (callback, event) -> {
         for (Model playerModelType : Model.values()) {
            PlayerRenderer playerRenderer = (PlayerRenderer)event.getSkin(playerModelType);
            if (playerRenderer != null) {
               callback.addLivingEntityRenderLayers(EntityType.PLAYER, playerRenderer, event.getContext());
            }
         }

         for (EntityType<?> entityType : event.getEntityTypes()) {
            if (event.getRenderer(entityType) instanceof LivingEntityRenderer<?, ?> entityRenderer) {
               callback.addLivingEntityRenderLayers(entityType, entityRenderer, event.getContext());
            }
         }
      });
   }

   public static void registerEventHandlers() {
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(ClientTickEvents.Start.class, Pre.class, (callback, evt) -> callback.onStartClientTick(Minecraft.getInstance()));
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(ClientTickEvents.End.class, Post.class, (callback, evt) -> callback.onEndClientTick(Minecraft.getInstance()));
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            RenderGuiCallback.class,
            net.neoforged.neoforge.client.event.RenderGuiEvent.Post.class,
            (callback, evt) -> callback.onRenderGui(Minecraft.getInstance(), evt.getGuiGraphics(), evt.getPartialTick())
         );
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            RenderGuiEvents.Before.class,
            net.neoforged.neoforge.client.event.RenderGuiEvent.Pre.class,
            (callback, evt) -> callback.onBeforeRenderGui(Minecraft.getInstance().gui, evt.getGuiGraphics(), evt.getPartialTick())
         );
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            RenderGuiEvents.After.class,
            net.neoforged.neoforge.client.event.RenderGuiEvent.Post.class,
            (callback, evt) -> callback.onAfterRenderGui(Minecraft.getInstance().gui, evt.getGuiGraphics(), evt.getPartialTick())
         );
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            ItemTooltipCallback.class,
            ItemTooltipEvent.class,
            (callback, evt) -> callback.onItemTooltip(evt.getItemStack(), evt.getToolTip(), evt.getContext(), evt.getEntity(), evt.getFlags())
         );
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            RenderNameTagCallback.class,
            RenderNameTagEvent.class,
            (callback, evt) -> {
               DefaultedValue<Component> content = DefaultedValue.fromEvent(evt::setContent, evt::getContent, evt::getOriginalContent);
               EventResult result = callback.onRenderNameTag(
                  evt.getEntity(), content, evt.getEntityRenderer(), evt.getPoseStack(), evt.getMultiBufferSource(), evt.getPackedLight(), evt.getPartialTick()
               );
               if (result.isInterrupt()) {
                  evt.setCanRender(result.getAsBoolean() ? TriState.TRUE : TriState.FALSE);
               }
            }
         );
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            ContainerScreenEvents.Background.class,
            Background.class,
            (callback, evt) -> callback.onDrawBackground(evt.getContainerScreen(), evt.getGuiGraphics(), evt.getMouseX(), evt.getMouseY())
         );
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            ContainerScreenEvents.Foreground.class,
            Foreground.class,
            (callback, evt) -> callback.onDrawForeground(evt.getContainerScreen(), evt.getGuiGraphics(), evt.getMouseX(), evt.getMouseY())
         );
      NeoForgeEventInvokerRegistry.INSTANCE.register(InventoryMobEffectsCallback.class, RenderInventoryMobEffects.class, (callback, evt) -> {
         MutableBoolean fullSizeRendering = MutableBoolean.fromEvent(evt::setCompact, evt::isCompact);
         MutableInt horizontalOffset = MutableInt.fromEvent(evt::setHorizontalOffset, evt::getHorizontalOffset);
         EventResult result = callback.onInventoryMobEffects(evt.getScreen(), evt.getAvailableSpace(), fullSizeRendering, horizontalOffset);
         if (result.isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      NeoForgeEventInvokerRegistry.INSTANCE.register(ComputeFovModifierCallback.class, ComputeFovModifierEvent.class, (callback, evt) -> {
         float fovEffectScale = ((Double)Minecraft.getInstance().options.fovEffectScale().get()).floatValue();
         if (fovEffectScale != 0.0F) {
            Consumer<Float> consumer = value -> evt.setNewFovModifier(Mth.lerp(fovEffectScale, 1.0F, value));
            Supplier<Float> supplier = () -> (evt.getNewFovModifier() - 1.0F) / fovEffectScale + 1.0F;
            callback.onComputeFovModifier(evt.getPlayer(), DefaultedFloat.fromEvent(consumer, supplier, evt::getFovModifier));
         }
      });
      registerScreenEvent(
         ScreenEvents.BeforeInit.class,
         net.neoforged.neoforge.client.event.ScreenEvent.Init.Pre.class,
         (callback, evt) -> callback.onBeforeInit(
            Minecraft.getInstance(), evt.getScreen(), evt.getScreen().width, evt.getScreen().height, new ScreenButtonList(evt.getScreen().renderables)
         )
      );
      registerScreenEvent(
         ScreenEvents.AfterInit.class,
         net.neoforged.neoforge.client.event.ScreenEvent.Init.Post.class,
         (callback, evt) -> callback.onAfterInit(
            Minecraft.getInstance(),
            evt.getScreen(),
            evt.getScreen().width,
            evt.getScreen().height,
            new ScreenButtonList(evt.getScreen().renderables),
            abstractWidget -> {
               evt.addListener(abstractWidget);
               return abstractWidget;
            },
            evt::removeListener
         )
      );
      registerScreenEvent(ScreenEvents.Remove.class, Closing.class, (callback, evt) -> callback.onRemove(evt.getScreen()));
      registerScreenEvent(
         ScreenEvents.BeforeRender.class,
         net.neoforged.neoforge.client.event.ScreenEvent.Render.Pre.class,
         (callback, evt) -> callback.onBeforeRender(evt.getScreen(), evt.getGuiGraphics(), evt.getMouseX(), evt.getMouseY(), evt.getPartialTick())
      );
      registerScreenEvent(
         ScreenEvents.AfterRender.class,
         net.neoforged.neoforge.client.event.ScreenEvent.Render.Post.class,
         (callback, evt) -> callback.onAfterRender(evt.getScreen(), evt.getGuiGraphics(), evt.getMouseX(), evt.getMouseY(), evt.getPartialTick())
      );
      registerScreenEvent(
         ScreenMouseEvents.BeforeMouseClick.class, net.neoforged.neoforge.client.event.ScreenEvent.MouseButtonPressed.Pre.class, (callback, evt) -> {
            EventResult result = callback.onBeforeMouseClick(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getButton());
            if (result.isInterrupt()) {
               evt.setCanceled(true);
            }
         }
      );
      registerScreenEvent(
         ScreenMouseEvents.AfterMouseClick.class,
         net.neoforged.neoforge.client.event.ScreenEvent.MouseButtonPressed.Post.class,
         (callback, evt) -> callback.onAfterMouseClick(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getButton())
      );
      registerScreenEvent(
         ScreenMouseEvents.BeforeMouseRelease.class, net.neoforged.neoforge.client.event.ScreenEvent.MouseButtonReleased.Pre.class, (callback, evt) -> {
            EventResult result = callback.onBeforeMouseRelease(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getButton());
            if (result.isInterrupt()) {
               evt.setCanceled(true);
            }
         }
      );
      registerScreenEvent(
         ScreenMouseEvents.AfterMouseRelease.class,
         net.neoforged.neoforge.client.event.ScreenEvent.MouseButtonReleased.Post.class,
         (callback, evt) -> callback.onAfterMouseRelease(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getButton())
      );
      registerScreenEvent(
         ScreenMouseEvents.BeforeMouseScroll.class, net.neoforged.neoforge.client.event.ScreenEvent.MouseScrolled.Pre.class, (callback, evt) -> {
            EventResult result = callback.onBeforeMouseScroll(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getScrollDeltaX(), evt.getScrollDeltaY());
            if (result.isInterrupt()) {
               evt.setCanceled(true);
            }
         }
      );
      registerScreenEvent(
         ScreenMouseEvents.AfterMouseScroll.class,
         net.neoforged.neoforge.client.event.ScreenEvent.MouseScrolled.Post.class,
         (callback, evt) -> callback.onAfterMouseScroll(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getScrollDeltaX(), evt.getScrollDeltaY())
      );
      registerScreenEvent(
         ScreenMouseEvents.BeforeMouseDrag.class,
         net.neoforged.neoforge.client.event.ScreenEvent.MouseDragged.Pre.class,
         (callback, evt) -> {
            EventResult result = callback.onBeforeMouseDrag(
               evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getMouseButton(), evt.getDragX(), evt.getDragY()
            );
            if (result.isInterrupt()) {
               evt.setCanceled(true);
            }
         }
      );
      registerScreenEvent(
         ScreenMouseEvents.AfterMouseDrag.class,
         net.neoforged.neoforge.client.event.ScreenEvent.MouseDragged.Post.class,
         (callback, evt) -> callback.onAfterMouseDrag(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getMouseButton(), evt.getDragX(), evt.getDragY())
      );
      registerScreenEvent(ScreenKeyboardEvents.BeforeKeyPress.class, net.neoforged.neoforge.client.event.ScreenEvent.KeyPressed.Pre.class, (callback, evt) -> {
         EventResult result = callback.onBeforeKeyPress(evt.getScreen(), evt.getKeyCode(), evt.getScanCode(), evt.getModifiers());
         if (result.isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      registerScreenEvent(
         ScreenKeyboardEvents.AfterKeyPress.class,
         net.neoforged.neoforge.client.event.ScreenEvent.KeyPressed.Post.class,
         (callback, evt) -> callback.onAfterKeyPress(evt.getScreen(), evt.getKeyCode(), evt.getScanCode(), evt.getModifiers())
      );
      registerScreenEvent(
         ScreenKeyboardEvents.BeforeKeyRelease.class, net.neoforged.neoforge.client.event.ScreenEvent.KeyReleased.Pre.class, (callback, evt) -> {
            EventResult result = callback.onBeforeKeyRelease(evt.getScreen(), evt.getKeyCode(), evt.getScanCode(), evt.getModifiers());
            if (result.isInterrupt()) {
               evt.setCanceled(true);
            }
         }
      );
      registerScreenEvent(
         ScreenKeyboardEvents.AfterKeyRelease.class,
         net.neoforged.neoforge.client.event.ScreenEvent.KeyReleased.Post.class,
         (callback, evt) -> callback.onAfterKeyRelease(evt.getScreen(), evt.getKeyCode(), evt.getScanCode(), evt.getModifiers())
      );
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(RenderGuiLayerEvents.Before.class, net.neoforged.neoforge.client.event.RenderGuiLayerEvent.Pre.class, (callback, evt, context) -> {
            Objects.requireNonNull(context, "context is null");
            ResourceLocation resourceLocation = (ResourceLocation)context;
            if (evt.getName().equals(resourceLocation) && !Minecraft.getInstance().options.hideGui) {
               EventResult result = callback.onBeforeRenderGuiLayer(Minecraft.getInstance(), evt.getGuiGraphics(), evt.getPartialTick());
               if (result.isInterrupt()) {
                  evt.setCanceled(true);
               }
            }
         });
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(RenderGuiLayerEvents.After.class, net.neoforged.neoforge.client.event.RenderGuiLayerEvent.Post.class, (callback, evt, context) -> {
            Objects.requireNonNull(context, "context is null");
            ResourceLocation resourceLocation = (ResourceLocation)context;
            if (evt.getName().equals(resourceLocation) && !Minecraft.getInstance().options.hideGui) {
               callback.onAfterRenderGuiLayer(Minecraft.getInstance(), evt.getGuiGraphics(), evt.getPartialTick());
            }
         });
      NeoForgeEventInvokerRegistry.INSTANCE.register(CustomizeChatPanelCallback.class, Chat.class, (callback, evt) -> {
         MutableInt posX = MutableInt.fromEvent(evt::setPosX, evt::getPosX);
         MutableInt posY = MutableInt.fromEvent(evt::setPosY, evt::getPosY);
         callback.onRenderChatPanel(evt.getGuiGraphics(), evt.getPartialTick(), posX, posY);
      });
      NeoForgeEventInvokerRegistry.INSTANCE.register(ClientEntityLevelEvents.Load.class, EntityJoinLevelEvent.class, (callback, evt) -> {
         if (evt.getLevel().isClientSide) {
            EventResult result = callback.onEntityLoad(evt.getEntity(), (ClientLevel)evt.getLevel());
            if (result.isInterrupt()) {
               if (evt.getEntity() instanceof Player) {
                  throw new UnsupportedOperationException("Cannot prevent player from spawning in!");
               }

               evt.setCanceled(true);
            }
         }
      });
      NeoForgeEventInvokerRegistry.INSTANCE.register(ClientEntityLevelEvents.Unload.class, EntityLeaveLevelEvent.class, (callback, evt) -> {
         if (evt.getLevel().isClientSide) {
            callback.onEntityUnload(evt.getEntity(), (ClientLevel)evt.getLevel());
         }
      });
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(InputEvents.MouseClick.class, net.neoforged.neoforge.client.event.InputEvent.MouseButton.Pre.class, (callback, evt) -> {
            EventResult result = callback.onMouseClick(evt.getButton(), evt.getAction(), evt.getModifiers());
            if (result.isInterrupt()) {
               evt.setCanceled(true);
            }
         });
      NeoForgeEventInvokerRegistry.INSTANCE.register(InputEvents.MouseScroll.class, MouseScrollingEvent.class, (callback, evt) -> {
         EventResult result = callback.onMouseScroll(evt.isLeftDown(), evt.isMiddleDown(), evt.isRightDown(), evt.getScrollDeltaX(), evt.getScrollDeltaY());
         if (result.isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            InputEvents.KeyPress.class, Key.class, (callback, evt) -> callback.onKeyPress(evt.getKey(), evt.getScanCode(), evt.getAction(), evt.getModifiers())
         );
      NeoForgeEventInvokerRegistry.INSTANCE.register(ComputeCameraAnglesCallback.class, ComputeCameraAngles.class, (callback, evt) -> {
         MutableFloat pitch = MutableFloat.fromEvent(evt::setPitch, evt::getPitch);
         MutableFloat yaw = MutableFloat.fromEvent(evt::setYaw, evt::getYaw);
         MutableFloat roll = MutableFloat.fromEvent(evt::setRoll, evt::getRoll);
         callback.onComputeCameraAngles(evt.getRenderer(), evt.getCamera(), (float)evt.getPartialTick(), pitch, yaw, roll);
      });
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            RenderLivingEvents.Before.class,
            net.neoforged.neoforge.client.event.RenderLivingEvent.Pre.class,
            (callback, evt) -> {
               EventResult result = callback.onBeforeRenderEntity(
                  evt.getEntity(), evt.getRenderer(), evt.getPartialTick(), evt.getPoseStack(), evt.getMultiBufferSource(), evt.getPackedLight()
               );
               if (result.isInterrupt()) {
                  evt.setCanceled(true);
               }
            }
         );
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            RenderLivingEvents.After.class,
            net.neoforged.neoforge.client.event.RenderLivingEvent.Post.class,
            (callback, evt) -> callback.onAfterRenderEntity(
               evt.getEntity(), evt.getRenderer(), evt.getPartialTick(), evt.getPoseStack(), evt.getMultiBufferSource(), evt.getPackedLight()
            )
         );
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            RenderPlayerEvents.Before.class,
            net.neoforged.neoforge.client.event.RenderPlayerEvent.Pre.class,
            (callback, evt) -> {
               EventResult result = callback.onBeforeRenderPlayer(
                  (AbstractClientPlayer)evt.getEntity(),
                  evt.getRenderer(),
                  evt.getPartialTick(),
                  evt.getPoseStack(),
                  evt.getMultiBufferSource(),
                  evt.getPackedLight()
               );
               if (result.isInterrupt()) {
                  evt.setCanceled(true);
               }
            }
         );
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            RenderPlayerEvents.After.class,
            net.neoforged.neoforge.client.event.RenderPlayerEvent.Post.class,
            (callback, evt) -> callback.onAfterRenderPlayer(
               (AbstractClientPlayer)evt.getEntity(),
               evt.getRenderer(),
               evt.getPartialTick(),
               evt.getPoseStack(),
               evt.getMultiBufferSource(),
               evt.getPackedLight()
            )
         );
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            RenderHandEvents.MainHand.class,
            RenderHandEvent.class,
            (callback, evt) -> {
               if (evt.getHand() == InteractionHand.MAIN_HAND) {
                  Minecraft minecraft = Minecraft.getInstance();
                  ItemInHandRenderer itemInHandRenderer = minecraft.getEntityRenderDispatcher().getItemInHandRenderer();
                  EventResult result = callback.onRenderMainHand(
                     itemInHandRenderer,
                     minecraft.player,
                     minecraft.player.getMainArm(),
                     evt.getItemStack(),
                     evt.getPoseStack(),
                     evt.getMultiBufferSource(),
                     evt.getPackedLight(),
                     evt.getPartialTick(),
                     evt.getInterpolatedPitch(),
                     evt.getSwingProgress(),
                     evt.getEquipProgress()
                  );
                  if (result.isInterrupt()) {
                     evt.setCanceled(true);
                  }
               }
            }
         );
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            RenderHandEvents.OffHand.class,
            RenderHandEvent.class,
            (callback, evt) -> {
               if (evt.getHand() == InteractionHand.OFF_HAND) {
                  Minecraft minecraft = Minecraft.getInstance();
                  ItemInHandRenderer itemInHandRenderer = minecraft.getEntityRenderDispatcher().getItemInHandRenderer();
                  EventResult result = callback.onRenderOffHand(
                     itemInHandRenderer,
                     minecraft.player,
                     minecraft.player.getMainArm().getOpposite(),
                     evt.getItemStack(),
                     evt.getPoseStack(),
                     evt.getMultiBufferSource(),
                     evt.getPackedLight(),
                     evt.getPartialTick(),
                     evt.getInterpolatedPitch(),
                     evt.getSwingProgress(),
                     evt.getEquipProgress()
                  );
                  if (result.isInterrupt()) {
                     evt.setCanceled(true);
                  }
               }
            }
         );
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(ClientLevelTickEvents.Start.class, net.neoforged.neoforge.event.tick.LevelTickEvent.Pre.class, (callback, evt) -> {
            if (evt.getLevel() instanceof ClientLevel level) {
               callback.onStartLevelTick(Minecraft.getInstance(), level);
            }
         });
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(ClientLevelTickEvents.End.class, net.neoforged.neoforge.event.tick.LevelTickEvent.Post.class, (callback, evt) -> {
            if (evt.getLevel() instanceof ClientLevel level) {
               callback.onEndLevelTick(Minecraft.getInstance(), level);
            }
         });
      NeoForgeEventInvokerRegistry.INSTANCE.register(ClientChunkEvents.Load.class, Load.class, (callback, evt) -> {
         if (evt.getLevel() instanceof ClientLevel level) {
            callback.onChunkLoad(level, (LevelChunk)evt.getChunk());
         }
      });
      NeoForgeEventInvokerRegistry.INSTANCE.register(ClientChunkEvents.Unload.class, Unload.class, (callback, evt) -> {
         if (evt.getLevel() instanceof ClientLevel level) {
            callback.onChunkUnload(level, (LevelChunk)evt.getChunk());
         }
      });
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            ClientPlayerNetworkEvents.LoggedIn.class,
            LoggingIn.class,
            (callback, evt) -> callback.onLoggedIn(evt.getPlayer(), evt.getMultiPlayerGameMode(), evt.getConnection())
         );
      NeoForgeEventInvokerRegistry.INSTANCE.register(ClientPlayerNetworkEvents.LoggedOut.class, LoggingOut.class, (callback, evt) -> {
         if (evt.getPlayer() != null && evt.getMultiPlayerGameMode() != null) {
            Objects.requireNonNull(evt.getConnection(), "connection is null");
            callback.onLoggedOut(evt.getPlayer(), evt.getMultiPlayerGameMode(), evt.getConnection());
         }
      });
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            ClientPlayerCopyCallback.class,
            Clone.class,
            (callback, evt) -> callback.onCopy(evt.getOldPlayer(), evt.getNewPlayer(), evt.getMultiPlayerGameMode(), evt.getConnection())
         );
      NeoForgeEventInvokerRegistry.INSTANCE.register(InteractionInputEvents.Attack.class, InteractionKeyMappingTriggered.class, (callback, evt) -> {
         if (evt.isAttack()) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.hitResult != null) {
               EventResult result = callback.onAttackInteraction(minecraft, minecraft.player, minecraft.hitResult);
               if (result.isInterrupt()) {
                  evt.setSwingHand(false);
                  evt.setCanceled(true);
               }
            }
         }
      });
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            InteractionInputEvents.Use.class,
            InteractionKeyMappingTriggered.class,
            (callback, evt) -> {
               if (evt.isUseItem()) {
                  Minecraft minecraft = Minecraft.getInstance();
                  if (minecraft.hitResult != null
                     && minecraft.player.getItemInHand(evt.getHand()).isItemEnabled(minecraft.level.enabledFeatures())
                     && (
                        minecraft.hitResult.getType() != Type.ENTITY
                           || minecraft.level.getWorldBorder().isWithinBounds(((EntityHitResult)minecraft.hitResult).getEntity().blockPosition())
                     )) {
                     EventResult result = callback.onUseInteraction(minecraft, minecraft.player, evt.getHand(), minecraft.hitResult);
                     if (result.isInterrupt()) {
                        evt.setSwingHand(false);
                        evt.setCanceled(true);
                     }
                  }
               }
            }
         );
      NeoForgeEventInvokerRegistry.INSTANCE.register(InteractionInputEvents.Pick.class, InteractionKeyMappingTriggered.class, (callback, evt) -> {
         if (evt.isPickBlock()) {
            Minecraft minecraft = Minecraft.getInstance();
            EventResult result = callback.onPickInteraction(minecraft, minecraft.player, minecraft.hitResult);
            if (result.isInterrupt()) {
               evt.setCanceled(true);
            }
         }
      });
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(ClientLevelEvents.Load.class, net.neoforged.neoforge.event.level.LevelEvent.Load.class, (callback, evt) -> {
            if (evt.getLevel() instanceof ClientLevel level) {
               callback.onLevelLoad(Minecraft.getInstance(), level);
            }
         });
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(ClientLevelEvents.Unload.class, net.neoforged.neoforge.event.level.LevelEvent.Unload.class, (callback, evt) -> {
            if (evt.getLevel() instanceof ClientLevel level) {
               callback.onLevelUnload(Minecraft.getInstance(), level);
            }
         });
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            MovementInputUpdateCallback.class,
            MovementInputUpdateEvent.class,
            (callback, evt) -> callback.onMovementInputUpdate((LocalPlayer)evt.getEntity(), evt.getInput())
         );
      NeoForgeEventInvokerRegistry.INSTANCE.register(RenderBlockOverlayCallback.class, RenderBlockScreenEffectEvent.class, (callback, evt) -> {
         EventResult result = callback.onRenderBlockOverlay((LocalPlayer)evt.getPlayer(), evt.getPoseStack(), evt.getBlockState());
         if (result.isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            FogEvents.Render.class,
            RenderFog.class,
            (callback, evt) -> {
               MutableFloat nearPlaneDistance = MutableFloat.fromEvent(t -> {
                  evt.setNearPlaneDistance(t);
                  evt.setCanceled(true);
               }, evt::getNearPlaneDistance);
               MutableFloat farPlaneDistance = MutableFloat.fromEvent(t -> {
                  evt.setFarPlaneDistance(t);
                  evt.setCanceled(true);
               }, evt::getFarPlaneDistance);
               MutableValue<FogShape> fogShape = MutableValue.fromEvent(t -> {
                  evt.setFogShape(t);
                  evt.setCanceled(true);
               }, evt::getFogShape);
               callback.onRenderFog(
                  evt.getRenderer(), evt.getCamera(), (float)evt.getPartialTick(), evt.getMode(), evt.getType(), nearPlaneDistance, farPlaneDistance, fogShape
               );
            }
         );
      NeoForgeEventInvokerRegistry.INSTANCE.register(FogEvents.ComputeColor.class, ComputeFogColor.class, (callback, evt) -> {
         MutableFloat red = MutableFloat.fromEvent(evt::setRed, evt::getRed);
         MutableFloat green = MutableFloat.fromEvent(evt::setGreen, evt::getGreen);
         MutableFloat blue = MutableFloat.fromEvent(evt::setBlue, evt::getBlue);
         callback.onComputeFogColor(evt.getRenderer(), evt.getCamera(), (float)evt.getPartialTick(), red, green, blue);
      });
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            RenderTooltipCallback.class,
            net.neoforged.neoforge.client.event.RenderTooltipEvent.Pre.class,
            (callback, evt) -> {
               EventResult result = callback.onRenderTooltip(
                  evt.getGraphics(), evt.getFont(), evt.getX(), evt.getY(), evt.getComponents(), evt.getTooltipPositioner()
               );
               if (result.isInterrupt()) {
                  evt.setCanceled(true);
               }
            }
         );
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            RenderHighlightCallback.class,
            Block.class,
            (callback, evt) -> {
               Minecraft minecraft = Minecraft.getInstance();
               if (minecraft.getCameraEntity() instanceof Player && !minecraft.options.hideGui) {
                  EventResult result = callback.onRenderHighlight(
                     evt.getLevelRenderer(),
                     evt.getCamera(),
                     minecraft.gameRenderer,
                     evt.getTarget(),
                     evt.getDeltaTracker(),
                     evt.getPoseStack(),
                     evt.getMultiBufferSource(),
                     minecraft.level
                  );
                  if (result.isInterrupt()) {
                     evt.setCanceled(true);
                  }
               }
            },
            true
         );
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            RenderHighlightCallback.class,
            Entity.class,
            (callback, evt) -> {
               Minecraft minecraft = Minecraft.getInstance();
               if (minecraft.getCameraEntity() instanceof Player && !minecraft.options.hideGui) {
                  callback.onRenderHighlight(
                     evt.getLevelRenderer(),
                     evt.getCamera(),
                     minecraft.gameRenderer,
                     evt.getTarget(),
                     evt.getDeltaTracker(),
                     evt.getPoseStack(),
                     evt.getMultiBufferSource(),
                     minecraft.level
                  );
               }
            },
            true
         );
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            RenderLevelEvents.AfterTerrain.class,
            RenderLevelStageEvent.class,
            (callback, evt) -> {
               if (evt.getStage() == Stage.AFTER_CUTOUT_BLOCKS) {
                  Minecraft minecraft = Minecraft.getInstance();
                  callback.onRenderLevelAfterTerrain(
                     evt.getLevelRenderer(),
                     evt.getCamera(),
                     minecraft.gameRenderer,
                     evt.getPartialTick(),
                     evt.getPoseStack(),
                     evt.getProjectionMatrix(),
                     evt.getFrustum(),
                     minecraft.level
                  );
               }
            }
         );
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            RenderLevelEvents.AfterEntities.class,
            RenderLevelStageEvent.class,
            (callback, evt) -> {
               if (evt.getStage() == Stage.AFTER_ENTITIES) {
                  Minecraft minecraft = Minecraft.getInstance();
                  callback.onRenderLevelAfterEntities(
                     evt.getLevelRenderer(),
                     evt.getCamera(),
                     minecraft.gameRenderer,
                     evt.getPartialTick(),
                     evt.getPoseStack(),
                     evt.getProjectionMatrix(),
                     evt.getFrustum(),
                     minecraft.level
                  );
               }
            }
         );
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            RenderLevelEvents.AfterTranslucent.class,
            RenderLevelStageEvent.class,
            (callback, evt) -> {
               if (evt.getStage() == Stage.AFTER_PARTICLES) {
                  Minecraft minecraft = Minecraft.getInstance();
                  callback.onRenderLevelAfterTranslucent(
                     evt.getLevelRenderer(),
                     evt.getCamera(),
                     minecraft.gameRenderer,
                     evt.getPartialTick(),
                     evt.getPoseStack(),
                     evt.getProjectionMatrix(),
                     evt.getFrustum(),
                     minecraft.level
                  );
               }
            }
         );
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            RenderLevelEvents.AfterLevel.class,
            RenderLevelStageEvent.class,
            (callback, evt) -> {
               if (evt.getStage() == Stage.AFTER_LEVEL) {
                  Minecraft minecraft = Minecraft.getInstance();
                  callback.onRenderLevelAfterLevel(
                     evt.getLevelRenderer(),
                     evt.getCamera(),
                     minecraft.gameRenderer,
                     evt.getPartialTick(),
                     evt.getPoseStack(),
                     evt.getProjectionMatrix(),
                     evt.getFrustum(),
                     minecraft.level
                  );
               }
            }
         );
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(GameRenderEvents.Before.class, net.neoforged.neoforge.client.event.RenderFrameEvent.Pre.class, (callback, evt) -> {
            Minecraft minecraft = Minecraft.getInstance();
            callback.onBeforeGameRender(minecraft, minecraft.gameRenderer, evt.getPartialTick());
         });
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(GameRenderEvents.After.class, net.neoforged.neoforge.client.event.RenderFrameEvent.Post.class, (callback, evt) -> {
            Minecraft minecraft = Minecraft.getInstance();
            callback.onAfterGameRender(minecraft, minecraft.gameRenderer, evt.getPartialTick());
         });
      NeoForgeEventInvokerRegistry.INSTANCE.register(AddToastCallback.class, ToastAddEvent.class, (callback, evt) -> {
         Minecraft minecraft = Minecraft.getInstance();
         EventResult result = callback.onAddToast(minecraft.getToasts(), evt.getToast());
         if (result.isInterrupt()) {
            evt.setCanceled(true);
         }
      });
      NeoForgeEventInvokerRegistry.INSTANCE.register(GatherDebugTextEvents.Left.class, DebugText.class, (callback, evt) -> {
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft.getDebugOverlay().showDebugScreen()) {
            callback.onGatherLeftDebugText(evt.getWindow(), evt.getGuiGraphics(), evt.getPartialTick(), evt.getLeft());
         }
      });
      NeoForgeEventInvokerRegistry.INSTANCE.register(GatherDebugTextEvents.Right.class, DebugText.class, (callback, evt) -> {
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft.getDebugOverlay().showDebugScreen()) {
            callback.onGatherRightDebugText(evt.getWindow(), evt.getGuiGraphics(), evt.getPartialTick(), evt.getRight());
         }
      });
      NeoForgeEventInvokerRegistry.INSTANCE.register(ComputeFieldOfViewCallback.class, ComputeFov.class, (callback, evt) -> {
         MutableDouble fieldOfView = MutableDouble.fromEvent(evt::setFOV, evt::getFOV);
         callback.onComputeFieldOfView(evt.getRenderer(), evt.getCamera(), (float)evt.getPartialTick(), fieldOfView);
      });
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            ChatMessageReceivedCallback.class,
            ClientChatReceivedEvent.class,
            (callback, evt) -> {
               MutableValue<Component> message = MutableValue.fromEvent(evt::setMessage, evt::getMessage);
               PlayerChatMessage playerChatMessage = evt instanceof net.neoforged.neoforge.client.event.ClientChatReceivedEvent.Player player
                  ? player.getPlayerChatMessage()
                  : null;
               boolean isOverlay = evt instanceof System system && system.isOverlay();
               EventResult result = callback.onChatMessageReceived(message, evt.getBoundChatType(), playerChatMessage, isOverlay);
               if (result.isInterrupt()) {
                  evt.setCanceled(true);
               }
            }
         );
      NeoForgeEventInvokerRegistry.INSTANCE
         .register(
            GatherEffectScreenTooltipCallback.class,
            GatherEffectScreenTooltipsEvent.class,
            (callback, evt) -> callback.onGatherEffectScreenTooltip(evt.getScreen(), evt.getEffectInstance(), evt.getTooltip())
         );
   }

   private static <T, E extends ScreenEvent> void registerScreenEvent(Class<T> clazz, Class<E> event, BiConsumer<T, E> converter) {
      NeoForgeEventInvokerRegistry.INSTANCE.register(clazz, event, (callback, evt, context) -> {
         Objects.requireNonNull(context, "context is null");
         if (((Class)context).isInstance(evt.getScreen())) {
            converter.accept(callback, evt);
         }
      });
   }

   private static Set<ModelResourceLocation> getTopLevelModelLocations() {
      Set<ModelResourceLocation> modelLocations = new HashSet<>();
      modelLocations.add(ModelBakery.MISSING_MODEL_VARIANT);

      for (net.minecraft.world.level.block.Block block : BuiltInRegistries.BLOCK) {
         block.getStateDefinition().getPossibleStates().forEach(blockState -> modelLocations.add(BlockModelShaper.stateToModelLocation(blockState)));
      }

      for (ResourceLocation resourcelocation : BuiltInRegistries.ITEM.keySet()) {
         modelLocations.add(ModelResourceLocation.inventory(resourcelocation));
      }

      modelLocations.add(ItemRenderer.TRIDENT_IN_HAND_MODEL);
      modelLocations.add(ItemRenderer.SPYGLASS_IN_HAND_MODEL);
      return modelLocations;
   }
}

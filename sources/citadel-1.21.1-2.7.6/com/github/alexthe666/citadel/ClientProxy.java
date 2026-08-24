package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.CitadelItemRenderProperties;
import com.github.alexthe666.citadel.client.event.EventRenderSplashText;
import com.github.alexthe666.citadel.client.game.Tetris;
import com.github.alexthe666.citadel.client.gui.GuiCitadelBook;
import com.github.alexthe666.citadel.client.gui.GuiCitadelCapesConfig;
import com.github.alexthe666.citadel.client.gui.GuiCitadelPatreonConfig;
import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.citadel.client.model.TabulaModelHandler;
import com.github.alexthe666.citadel.client.render.CitadelLecternRenderer;
import com.github.alexthe666.citadel.client.render.pathfinding.WorldEventContext;
import com.github.alexthe666.citadel.client.rewards.CitadelCapes;
import com.github.alexthe666.citadel.client.rewards.CitadelPatreonRenderer;
import com.github.alexthe666.citadel.client.rewards.SpaceStationPatreonRenderer;
import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import com.github.alexthe666.citadel.client.tick.ClientTickRateTracker;
import com.github.alexthe666.citadel.config.ServerConfig;
import com.github.alexthe666.citadel.item.ItemWithHoverAnimation;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.Pathfinding;
import com.github.alexthe666.citadel.server.event.EventChangeEntityTickRate;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.options.SkinCustomizationScreen;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent.Pre;
import net.neoforged.neoforge.client.event.ScreenEvent.Opening;
import net.neoforged.neoforge.client.event.ScreenEvent.Init.Post;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;

public class ClientProxy extends ServerProxy {
   public static TabulaModel CITADEL_MODEL;
   public static boolean hideFollower = false;
   private Map<ItemStack, Float> prevMouseOverProgresses = new HashMap<>();
   private Map<ItemStack, Float> mouseOverProgresses = new HashMap<>();
   private ItemStack lastHoveredItem = null;
   private Tetris aprilFoolsTetrisGame = null;
   public static final ResourceLocation RAINBOW_AURA_POST_SHADER = ResourceLocation.parse("citadel:shaders/post/rainbow_aura.json");

   @Override
   public void onClientInit() {
      try {
         CITADEL_MODEL = new TabulaModel(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/citadel/models/citadel_model"));
      } catch (IOException var2) {
         var2.printStackTrace();
      }

      BlockEntityRenderers.register(Citadel.LECTERN_BE.get(), CitadelLecternRenderer::new);
      CitadelPatreonRenderer.register("citadel", new SpaceStationPatreonRenderer(ResourceLocation.parse("citadel:patreon_space_station"), new int[0]));
      CitadelPatreonRenderer.register(
         "citadel_red",
         new SpaceStationPatreonRenderer(ResourceLocation.parse("citadel:patreon_space_station_red"), new int[]{11685960, 10306880, 8009265, 7417898})
      );
      CitadelPatreonRenderer.register(
         "citadel_gray",
         new SpaceStationPatreonRenderer(ResourceLocation.parse("citadel:patreon_space_station_gray"), new int[]{10526880, 8947848, 6579300, 5723991})
      );
      if (CitadelConstants.debugShaders()) {
         PostEffectRegistry.registerEffect(RAINBOW_AURA_POST_SHADER);
      }
   }

   @SubscribeEvent
   public void screenOpen(Post event) {
      if (event.getScreen() instanceof SkinCustomizationScreen && Minecraft.getInstance().player != null) {
         try {
            String username = Minecraft.getInstance().player.getName().getString();
            int height = -20;
            if (Citadel.PATREONS.contains(username)) {
               Button button1 = Button.builder(
                     Component.translatable("citadel.gui.patreon_rewards_option").withStyle(ChatFormatting.GREEN),
                     p_213080_2_ -> Minecraft.getInstance().setScreen(new GuiCitadelPatreonConfig(event.getScreen(), Minecraft.getInstance().options))
                  )
                  .size(200, 20)
                  .pos(event.getScreen().width / 2 - 100, event.getScreen().height / 6 + 150 + height)
                  .build();
               event.addListener(button1);
               height += 25;
            }

            if (!CitadelCapes.getCapesFor(Minecraft.getInstance().player.getUUID()).isEmpty()) {
               Button button2 = Button.builder(
                     Component.translatable("citadel.gui.capes_option").withStyle(ChatFormatting.GREEN),
                     p_213080_2_ -> Minecraft.getInstance().setScreen(new GuiCitadelCapesConfig(event.getScreen(), Minecraft.getInstance().options))
                  )
                  .size(200, 20)
                  .pos(event.getScreen().width / 2 - 100, event.getScreen().height / 6 + 150 + height)
                  .build();
               event.addListener(button2);
               height += 25;
            }
         } catch (Exception var5) {
            var5.printStackTrace();
         }
      }
   }

   @SubscribeEvent
   public void screenRender(net.neoforged.neoforge.client.event.ScreenEvent.Render.Post event) {
      if (event.getScreen() instanceof TitleScreen && CitadelConstants.isAprilFools()) {
         if (this.aprilFoolsTetrisGame == null) {
            this.aprilFoolsTetrisGame = new Tetris();
         } else {
            this.aprilFoolsTetrisGame.render((TitleScreen)event.getScreen(), event.getGuiGraphics(), event.getPartialTick());
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void playerRender(Pre event) {
      PoseStack matrixStackIn = event.getPoseStack();
      String username = event.getEntity().getName().getString();
      if (event.getEntity().isModelPartShown(PlayerModelPart.CAPE) && !event.isCanceled() && !event.getEntity().isSpectator()) {
         if (Citadel.PATREONS.contains(username)) {
            CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
            String rendererName = tag.contains("CitadelFollowerType") ? tag.getString("CitadelFollowerType") : "citadel";
            if (!rendererName.equals("none") && !hideFollower) {
               CitadelPatreonRenderer renderer = CitadelPatreonRenderer.get(rendererName);
               if (renderer != null) {
                  float distance = tag.contains("CitadelRotateDistance") ? tag.getFloat("CitadelRotateDistance") : 2.0F;
                  float speed = tag.contains("CitadelRotateSpeed") ? tag.getFloat("CitadelRotateSpeed") : 1.0F;
                  float height = tag.contains("CitadelRotateHeight") ? tag.getFloat("CitadelRotateHeight") : 1.0F;
                  renderer.render(
                     matrixStackIn, event.getMultiBufferSource(), event.getPackedLight(), event.getPartialTick(), event.getEntity(), distance, speed, height
                  );
               }
            }
         }
      }
   }

   @SubscribeEvent
   public void renderWorldLastEvent(RenderLevelStageEvent event) {
      if (Pathfinding.isDebug()) {
         WorldEventContext.INSTANCE.renderWorldLastEvent(event);
      }
   }

   @SubscribeEvent
   public void onOpenGui(Opening event) {
      if (ServerConfig.skipWarnings) {
         try {
            if (event.getScreen() instanceof BackupConfirmScreen confirmBackupScreen) {
               MutableComponent title = Component.translatable("selectWorld.backupQuestion.experimental");
               if (confirmBackupScreen.getTitle().equals(title)) {
                  confirmBackupScreen.onProceed.proceed(false, true);
               }
            }

            if (event.getScreen() instanceof ConfirmScreen confirmScreen) {
               MutableComponent title = Component.translatable("selectWorld.backupQuestion.experimental");
               if (confirmScreen.getTitle().equals(title)) {
                  confirmScreen.callback.accept(true);
               }
            }
         } catch (Exception var4) {
            Citadel.LOGGER.warn("Citadel couldn't skip world loadings");
            var4.printStackTrace();
         }
      }
   }

   @SubscribeEvent
   public void renderSplashTextBefore(EventRenderSplashText.Pre event) {
      if (CitadelConstants.isAprilFools() && this.aprilFoolsTetrisGame != null) {
         event.setResult(TriState.TRUE);
         float hue = (float)(System.currentTimeMillis() % 6000L) / 6000.0F;
         event.getGuiGraphics().pose().mulPose(Axis.ZP.rotationDegrees((float)Math.sin(hue * 3.141592653589793) * 360.0F));
         if (!this.aprilFoolsTetrisGame.isStarted()) {
            event.setSplashText("Psst... press 'T' ;)");
         } else {
            event.setSplashText("");
         }

         int rainbow = Color.HSBtoRGB(hue, 0.6F, 1.0F);
         event.setSplashTextColor(rainbow);
      }
   }

   @SubscribeEvent
   public void onKeyPressed(net.neoforged.neoforge.client.event.ScreenEvent.KeyPressed.Pre event) {
      if (Minecraft.getInstance().screen instanceof TitleScreen
         && this.aprilFoolsTetrisGame != null
         && this.aprilFoolsTetrisGame.isStarted()
         && (event.getKeyCode() == 263 || event.getKeyCode() == 262 || event.getKeyCode() == 264 || event.getKeyCode() == 265)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public void clientTick(net.neoforged.neoforge.client.event.ClientTickEvent.Pre event) {
      if (!this.isGamePaused() && Minecraft.getInstance().isRunning() && Minecraft.getInstance().level != null && Minecraft.getInstance().player != null) {
         ClientTickRateTracker.getForClient(Minecraft.getInstance()).masterTick();
         this.tickMouseOverAnimations();
      }

      if (!this.isGamePaused() && CitadelConstants.isAprilFools() && this.aprilFoolsTetrisGame != null) {
         if (Minecraft.getInstance().screen instanceof TitleScreen) {
            this.aprilFoolsTetrisGame.tick();
         } else {
            this.aprilFoolsTetrisGame.reset();
         }
      }
   }

   private void tickMouseOverAnimations() {
      this.prevMouseOverProgresses.putAll(this.mouseOverProgresses);
      if (this.lastHoveredItem != null) {
         float prev = this.mouseOverProgresses.getOrDefault(this.lastHoveredItem, 0.0F);
         float maxTime = 5.0F;
         if (this.lastHoveredItem.getItem() instanceof ItemWithHoverAnimation hoverOver) {
            maxTime = hoverOver.getMaxHoverOverTime(this.lastHoveredItem);
         }

         if (prev < maxTime) {
            this.mouseOverProgresses.put(this.lastHoveredItem, prev + 1.0F);
         }
      }

      if (!this.mouseOverProgresses.isEmpty()) {
         Iterator<Entry<ItemStack, Float>> it = this.mouseOverProgresses.entrySet().iterator();

         while (it.hasNext()) {
            Entry<ItemStack, Float> next = it.next();
            float progress = next.getValue();
            if (this.lastHoveredItem == null || next.getKey() != this.lastHoveredItem) {
               if (progress == 0.0F) {
                  it.remove();
               } else {
                  next.setValue(progress - 1.0F);
               }
            }
         }
      }

      this.lastHoveredItem = null;
   }

   @SubscribeEvent
   public void renderTooltipColor(net.neoforged.neoforge.client.event.RenderTooltipEvent.Color event) {
      if (event.getItemStack().getItem() instanceof ItemWithHoverAnimation hoverOver && hoverOver.canHoverOver(event.getItemStack())) {
         this.lastHoveredItem = event.getItemStack();
      } else {
         this.lastHoveredItem = null;
      }
   }

   @Override
   public float getMouseOverProgress(ItemStack itemStack) {
      float prev = this.prevMouseOverProgresses.getOrDefault(itemStack, 0.0F);
      float current = this.mouseOverProgresses.getOrDefault(itemStack, 0.0F);
      float lerped = prev + (current - prev) * (float)Minecraft.getInstance().getFrameTimeNs();
      float maxTime = 5.0F;
      if (itemStack.getItem() instanceof ItemWithHoverAnimation hoverOver) {
         maxTime = hoverOver.getMaxHoverOverTime(itemStack);
      }

      return lerped / maxTime;
   }

   @Override
   public void handleAnimationPacket(int entityId, int index) {
      if (Minecraft.getInstance().level != null) {
         IAnimatedEntity entity = (IAnimatedEntity)Minecraft.getInstance().level.getEntity(entityId);
         if (entity != null) {
            if (index == -1) {
               entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
            } else {
               entity.setAnimation(entity.getAnimations()[index]);
            }

            entity.setAnimationTick(0);
         }
      }
   }

   @Override
   public void handlePropertiesPacket(String propertyID, CompoundTag compound, int entityID) {
      if (compound != null && Minecraft.getInstance().level != null) {
         Entity entity = Minecraft.getInstance().level.getEntity(entityID);
         if ((propertyID.equals("CitadelPatreonConfig") || propertyID.equals("CitadelTagUpdate")) && entity instanceof LivingEntity) {
            CitadelEntityData.setCitadelTag((LivingEntity)entity, compound);
         }
      }
   }

   @Override
   public void handleClientTickRatePacket(CompoundTag compound) {
      ClientTickRateTracker.getForClient(Minecraft.getInstance()).syncFromServer(compound);
   }

   @Override
   public Object getISTERProperties() {
      return new CitadelItemRenderProperties();
   }

   @Override
   public void openBookGUI(ItemStack book) {
      Minecraft.getInstance().setScreen(new GuiCitadelBook(book));
   }

   @Override
   public boolean isGamePaused() {
      return Minecraft.getInstance().isPaused();
   }

   @Override
   public Player getClientSidePlayer() {
      return Minecraft.getInstance().player;
   }

   @Override
   public boolean canEntityTickClient(Level level, Entity entity) {
      ClientTickRateTracker tracker = ClientTickRateTracker.getForClient(Minecraft.getInstance());
      if (tracker.isTickingHandled(entity)) {
         return false;
      } else if (!tracker.hasNormalTickRate(entity)) {
         EventChangeEntityTickRate event = new EventChangeEntityTickRate(entity, tracker.getEntityTickLengthModifier(entity));
         NeoForge.EVENT_BUS.post(event);
         if (event.isCanceled()) {
            return true;
         } else {
            tracker.addTickBlockedEntity(entity);
            return false;
         }
      } else {
         return true;
      }
   }

   @SubscribeEvent
   public void postRenderStage(RenderLevelStageEvent event) {
   }
}

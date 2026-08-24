package io.wispforest.owo.ui.component;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Axis;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIModelParsingException;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.util.pond.OwoEntityRenderDispatcherExtension;
import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.telemetry.TelemetryEventSender;
import net.minecraft.client.telemetry.WorldSessionTelemetryManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerLinks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.w3c.dom.Element;

public class EntityComponent<E extends Entity> extends BaseComponent {
   protected final EntityRenderDispatcher dispatcher;
   protected final BufferSource entityBuffers;
   protected final E entity;
   protected float mouseRotation = 0.0F;
   protected float scale = 1.0F;
   protected boolean lookAtCursor = false;
   protected boolean allowMouseRotation = false;
   protected boolean scaleToFit = false;
   protected boolean showNametag = false;
   protected Consumer<PoseStack> transform = matrixStack -> {};

   protected EntityComponent(Sizing sizing, E entity) {
      Minecraft client = Minecraft.getInstance();
      this.dispatcher = client.getEntityRenderDispatcher();
      this.entityBuffers = client.renderBuffers().bufferSource();
      this.entity = entity;
      this.sizing(sizing);
   }

   protected EntityComponent(Sizing sizing, EntityType<E> type, @Nullable CompoundTag nbt) {
      Minecraft client = Minecraft.getInstance();
      this.dispatcher = client.getEntityRenderDispatcher();
      this.entityBuffers = client.renderBuffers().bufferSource();
      this.entity = (E)type.create(client.level);
      if (nbt != null) {
         this.entity.load(nbt);
      }

      this.entity.absMoveTo(client.player.getX(), client.player.getY(), client.player.getZ());
      this.sizing(sizing);
   }

   @Override
   public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
      PoseStack matrices = context.pose();
      matrices.pushPose();
      matrices.translate(this.x + this.width / 2.0F, this.y + this.height / 2.0F, 100.0F);
      matrices.scale(75.0F * this.scale * this.width / 64.0F, -75.0F * this.scale * this.height / 64.0F, 75.0F * this.scale);
      matrices.translate(0.0F, this.entity.getBbHeight() / -2.0F, 0.0F);
      this.transform.accept(matrices);
      if (this.lookAtCursor) {
         float xRotation = (float)Math.toDegrees(Math.atan((mouseY - this.y - this.height / 2.0F) / 40.0F));
         float yRotation = (float)Math.toDegrees(Math.atan((mouseX - this.x - this.width / 2.0F) / 40.0F));
         if (this.entity instanceof LivingEntity living) {
            living.yHeadRotO = -yRotation;
         }

         this.entity.yRotO = -yRotation;
         this.entity.xRotO = xRotation * 0.65F;
         if (xRotation == 0.0F) {
            xRotation = 0.1F;
         }

         matrices.mulPose(Axis.XP.rotationDegrees(xRotation * 0.15F));
         matrices.mulPose(Axis.YP.rotationDegrees(yRotation * 0.15F));
      } else {
         matrices.mulPose(Axis.XP.rotationDegrees(35.0F));
         matrices.mulPose(Axis.YP.rotationDegrees(-45.0F + this.mouseRotation));
      }

      OwoEntityRenderDispatcherExtension dispatcher = (OwoEntityRenderDispatcherExtension)this.dispatcher;
      dispatcher.owo$setCounterRotate(true);
      dispatcher.owo$setShowNametag(this.showNametag);
      RenderSystem.setShaderLights(new Vector3f(0.15F, 1.0F, 0.0F), new Vector3f(0.15F, -1.0F, 0.0F));
      this.dispatcher.setRenderShadow(false);
      this.dispatcher.render(this.entity, 0.0, 0.0, 0.0, 0.0F, 0.0F, matrices, this.entityBuffers, 15728880);
      this.dispatcher.setRenderShadow(true);
      this.entityBuffers.endBatch();
      Lighting.setupFor3DItems();
      matrices.popPose();
      dispatcher.owo$setCounterRotate(false);
      dispatcher.owo$setShowNametag(true);
   }

   @Override
   public boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
      if (this.allowMouseRotation && button == 0) {
         this.mouseRotation = (float)(this.mouseRotation + deltaX);
         super.onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
         return true;
      } else {
         return super.onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
      }
   }

   public E entity() {
      return this.entity;
   }

   public EntityComponent<E> allowMouseRotation(boolean allowMouseRotation) {
      this.allowMouseRotation = allowMouseRotation;
      return this;
   }

   public boolean allowMouseRotation() {
      return this.allowMouseRotation;
   }

   public EntityComponent<E> lookAtCursor(boolean lookAtCursor) {
      this.lookAtCursor = lookAtCursor;
      return this;
   }

   public boolean lookAtCursor() {
      return this.lookAtCursor;
   }

   public EntityComponent<E> scale(float scale) {
      this.scale = scale;
      return this;
   }

   public float scale() {
      return this.scale;
   }

   public EntityComponent<E> scaleToFit(boolean scaleToFit) {
      this.scaleToFit = scaleToFit;
      if (scaleToFit) {
         float xScale = 0.5F / this.entity.getBbWidth();
         float yScale = 0.5F / this.entity.getBbHeight();
         this.scale(Math.min(xScale, yScale));
      }

      return this;
   }

   public boolean scaleToFit() {
      return this.scaleToFit;
   }

   public EntityComponent<E> transform(Consumer<PoseStack> transform) {
      this.transform = transform;
      return this;
   }

   public Consumer<PoseStack> transform() {
      return this.transform;
   }

   public EntityComponent<E> showNametag(boolean showNametag) {
      this.showNametag = showNametag;
      return this;
   }

   public boolean showNametag() {
      return this.showNametag;
   }

   @Override
   public boolean canFocus(Component.FocusSource source) {
      return source == Component.FocusSource.MOUSE_CLICK;
   }

   public static EntityComponent.RenderablePlayerEntity createRenderablePlayer(GameProfile profile) {
      return new EntityComponent.RenderablePlayerEntity(profile);
   }

   @Override
   public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
      super.parseProperties(model, element, children);
      UIParsing.apply(children, "scale", UIParsing::parseFloat, this::scale);
      UIParsing.apply(children, "look-at-cursor", UIParsing::parseBool, this::lookAtCursor);
      UIParsing.apply(children, "mouse-rotation", UIParsing::parseBool, this::allowMouseRotation);
      UIParsing.apply(children, "scale-to-fit", UIParsing::parseBool, this::scaleToFit);
   }

   public static EntityComponent<?> parse(Element element) {
      UIParsing.expectAttributes(element, "type");
      ResourceLocation entityId = UIParsing.parseIdentifier(element.getAttributeNode("type"));
      EntityType<?> entityType = (EntityType<?>)BuiltInRegistries.ENTITY_TYPE
         .getOptional(entityId)
         .orElseThrow(() -> new UIModelParsingException("Unknown entity type " + entityId));
      CompoundTag nbt = null;
      if (element.hasAttribute("nbt")) {
         try {
            nbt = TagParser.parseTag(element.getAttribute("nbt"));
         } catch (CommandSyntaxException var5) {
            throw new UIModelParsingException("Invalid NBT compound", var5);
         }
      }

      return new EntityComponent(Sizing.content(), (EntityType<E>)entityType, nbt);
   }

   public static class RenderablePlayerEntity extends LocalPlayer {
      protected PlayerSkin skinTextures;

      protected RenderablePlayerEntity(GameProfile profile) {
         super(
            Minecraft.getInstance(),
            Minecraft.getInstance().level,
            new ClientPacketListener(
               Minecraft.getInstance(),
               new Connection(PacketFlow.CLIENTBOUND),
               new CommonListenerCookie(
                  profile,
                  new WorldSessionTelemetryManager(TelemetryEventSender.DISABLED, false, Duration.ZERO, ""),
                  Minecraft.getInstance().level.registryAccess().freeze(),
                  Minecraft.getInstance().level.enabledFeatures(),
                  "Wisp Forest Enterprises",
                  null,
                  null,
                  Map.of(),
                  null,
                  false,
                  Map.of(),
                  ServerLinks.EMPTY
               )
            ),
            null,
            null,
            false,
            false
         );
         this.skinTextures = DefaultPlayerSkin.get(profile.getId());
         Util.backgroundExecutor().execute(() -> {
            GameProfile completeProfile = Minecraft.getInstance().getMinecraftSessionService().fetchProfile(profile.getId(), false).profile();
            this.skinTextures = DefaultPlayerSkin.get(completeProfile);
            this.minecraft.getSkinManager().getOrLoad(completeProfile).thenAccept(textures -> this.skinTextures = textures);
         });
      }

      public PlayerSkin getSkin() {
         return this.skinTextures;
      }

      public boolean isModelPartShown(PlayerModelPart modelPart) {
         return true;
      }

      @Nullable
      protected PlayerInfo getPlayerInfo() {
         return null;
      }
   }
}

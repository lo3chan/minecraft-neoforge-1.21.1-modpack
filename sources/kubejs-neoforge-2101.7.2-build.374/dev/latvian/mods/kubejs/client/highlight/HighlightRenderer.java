package dev.latvian.mods.kubejs.client.highlight;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.color.SimpleColor;
import dev.latvian.mods.kubejs.net.RequestBlockKubedexPayload;
import dev.latvian.mods.kubejs.net.RequestEntityKubedexPayload;
import dev.latvian.mods.kubejs.net.RequestInventoryKubedexPayload;
import dev.latvian.mods.kubejs.plugin.builtin.event.ClientEvents;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap.Entry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class HighlightRenderer {
   public static HighlightRenderer INSTANCE = new HighlightRenderer();
   public static KeyMapping keyMapping;
   public KubeColor color = new SimpleColor(10092467);
   public HighlightRenderer.Mode mode = HighlightRenderer.Mode.NONE;
   public boolean actualKey;
   @Nullable
   public HighlightRenderer.ShaderChain worldChain;
   @Nullable
   public HighlightRenderer.ShaderChain guiChain;
   @Nullable
   public ShaderInstance highlightShader;
   public final Set<Slot> hoveredSlots = new LinkedHashSet<>();
   public final Reference2IntMap<Entity> highlightedEntities = new Reference2IntLinkedOpenHashMap(0);
   public final Long2IntMap highlightedBlocks = new Long2IntLinkedOpenHashMap(0);
   public final IntOpenHashSet uniqueColors = new IntOpenHashSet(0);
   public boolean cancelBlockHighlight;

   public void loadPostChains(Minecraft mc) {
      if (this.worldChain != null) {
         this.worldChain.close();
      }

      if (this.guiChain != null) {
         this.guiChain.close();
      }

      ResourceLocation id = ResourceLocation.withDefaultNamespace("shaders/post/kubejs/highlight.json");
      this.worldChain = HighlightRenderer.ShaderChain.load(mc, id);
      this.guiChain = HighlightRenderer.ShaderChain.load(mc, id);
   }

   public void tickPre(Minecraft mc) {
      boolean prevKeyDown = this.actualKey;
      this.actualKey = mc.level != null
         && mc.player != null
         && keyMapping != null
         && !mc.isPaused()
         && mc.player.hasPermissions(2)
         && mc.kjs$isKeyMappingDown(keyMapping);

      while (this.actualKey && this.mode != HighlightRenderer.Mode.NONE && mc.options.keyInventory.consumeClick()) {
         this.keyToggled(mc, HighlightRenderer.Mode.NONE, false);
      }

      if (prevKeyDown != this.actualKey) {
         if (!this.actualKey) {
            this.keyToggled(mc, HighlightRenderer.Mode.NONE, true);
         } else if (mc.screen != null) {
            this.keyToggled(mc, HighlightRenderer.Mode.SCREEN, true);
         } else {
            this.keyToggled(mc, HighlightRenderer.Mode.WORLD, true);
         }
      }

      this.highlightedEntities.clear();
      this.highlightedBlocks.clear();
      this.uniqueColors.clear();
      this.cancelBlockHighlight = false;
      if (mc.level != null && mc.player != null) {
         HighlightKubeEvent event = new HighlightKubeEvent(mc, this);
         ClientEvents.HIGHLIGHT.post(event);
         if (this.mode == HighlightRenderer.Mode.WORLD) {
            event.addTarget(this.color);
         }

         if (mc.hitResult instanceof BlockHitResult hit && hit.getType() == Type.BLOCK && this.highlightedBlocks.containsKey(hit.getBlockPos().asLong())) {
            this.cancelBlockHighlight = true;
         }
      }
   }

   private void playSound(Minecraft mc) {
      String sound = DevProperties.get().kubedexSound;
      if (!sound.isEmpty()) {
         mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(ResourceLocation.parse(sound)), 1.0F));
      }
   }

   private int getFlags() {
      int flags = 0;
      flags |= Screen.hasShiftDown() ? 1 : 0;
      flags |= Screen.hasControlDown() ? 2 : 0;
      return flags | (Screen.hasAltDown() ? 4 : 0);
   }

   private void requestBlock(BlockPos pos) {
      PacketDistributor.sendToServer(new RequestBlockKubedexPayload(pos, this.getFlags()), new CustomPacketPayload[0]);
   }

   private void requestEntity(Entity entity) {
      PacketDistributor.sendToServer(new RequestEntityKubedexPayload(entity.getId(), this.getFlags()), new CustomPacketPayload[0]);
   }

   private void requestInventory(Set<Slot> slots) {
      ArrayList<Integer> slotIds = new ArrayList<>();
      ArrayList<ItemStack> stacks = new ArrayList<>();

      for (Slot slot : slots) {
         if (slot.container instanceof Inventory) {
            slotIds.add(slot.getSlotIndex());
         } else {
            ItemStack stack = slot.getItem();
            if (!stack.isEmpty()) {
               stacks.add(stack);
            }
         }
      }

      PacketDistributor.sendToServer(new RequestInventoryKubedexPayload(slotIds, stacks, this.getFlags()), new CustomPacketPayload[0]);
   }

   private void keyToggled(Minecraft mc, HighlightRenderer.Mode newMode, boolean success) {
      if (newMode == HighlightRenderer.Mode.NONE) {
         if (this.mode == HighlightRenderer.Mode.SCREEN) {
            if (success && !this.hoveredSlots.isEmpty()) {
               this.playSound(mc);
               this.requestInventory(this.hoveredSlots);
            }

            this.hoveredSlots.clear();
         } else if (success) {
            if (mc.hitResult instanceof EntityHitResult hit) {
               this.playSound(mc);
               this.requestEntity(hit.getEntity());
            } else if (mc.hitResult instanceof BlockHitResult hit && hit.getType() == Type.BLOCK) {
               this.playSound(mc);
               this.requestBlock(hit.getBlockPos());
            }
         }
      }

      this.mode = newMode;
   }

   public void clearBuffers(Minecraft mc) {
      if (this.worldChain != null) {
         this.worldChain.clearInput(mc);
      }

      if (this.guiChain != null) {
         this.guiChain.clearInput(mc);
      }
   }

   public void renderAfterLevel(Minecraft mc, RenderLevelStageEvent event) {
      this.updateDepth(mc);
      if (this.worldChain != null) {
         this.worldChain.draw(mc, event.getPartialTick().getGameTimeDeltaPartialTick(false));
      }
   }

   public void updateDepth(Minecraft mc) {
      if (this.worldChain != null) {
         this.worldChain.clearDepth(mc, true);
      }

      if (this.guiChain != null) {
         this.guiChain.clearDepth(mc, false);
      }
   }

   public void resizePostChains(int width, int height) {
      if (this.worldChain != null) {
         this.worldChain.postChain.resize(width, height);
      }

      if (this.guiChain != null) {
         this.guiChain.postChain.resize(width, height);
      }
   }

   public void renderAfterEntities(Minecraft mc, RenderLevelStageEvent event) {
      if (mc.level != null
         && this.worldChain != null
         && this.highlightShader != null
         && (!this.highlightedBlocks.isEmpty() || !this.highlightedEntities.isEmpty())) {
         mc.renderBuffers().bufferSource().endBatch();
         this.worldChain.renderInput.bindWrite(false);
         PoseStack ms = event.getPoseStack();
         Vec3 cam = event.getCamera().getPosition();
         float delta = event.getPartialTick().getGameTimeDeltaPartialTick(false);
         ms.pushPose();
         ms.translate(-cam.x, -cam.y, -cam.z);
         Int2ObjectOpenHashMap<HighlightRenderer.WrappedMultiBufferSource> sources = new Int2ObjectOpenHashMap();
         IntIterator var7 = this.uniqueColors.iterator();

         while (var7.hasNext()) {
            int color = (Integer)var7.next();
            sources.put(color, new HighlightRenderer.WrappedMultiBufferSource(mc.renderBuffers().bufferSource(), color));
         }

         ObjectIterator var24 = this.highlightedBlocks.long2IntEntrySet().iterator();

         while (var24.hasNext()) {
            it.unimi.dsi.fastutil.longs.Long2IntMap.Entry entry = (it.unimi.dsi.fastutil.longs.Long2IntMap.Entry)var24.next();
            BlockPos pos = BlockPos.of(entry.getLongKey());
            BlockState state = mc.level.getBlockState(pos);
            if (!state.isAir()) {
               this.worldChain.renderAnything.setTrue();
               double x = pos.getX();
               double y = pos.getY();
               double z = pos.getZ();
               ms.pushPose();
               ms.translate(x, y, z);
               BakedModel model = mc.getBlockRenderer().getBlockModel(state);
               long seed = state.getSeed(pos);
               HighlightRenderer.WrappedMultiBufferSource bufferSource = (HighlightRenderer.WrappedMultiBufferSource)sources.get(entry.getIntValue());

               for (RenderType renderType : model.getRenderTypes(state, RandomSource.create(seed), ModelData.EMPTY)) {
                  mc.getBlockRenderer()
                     .getModelRenderer()
                     .tesselateBlock(
                        mc.level,
                        model,
                        state,
                        pos,
                        ms,
                        bufferSource.getBuffer(RenderTypeHelper.getMovingBlockRenderType(renderType)),
                        false,
                        RandomSource.create(),
                        seed,
                        OverlayTexture.NO_OVERLAY,
                        ModelData.EMPTY,
                        renderType
                     );
               }

               BlockEntity entity = mc.level.getBlockEntity(pos);
               if (entity != null) {
                  mc.getBlockEntityRenderDispatcher().render(entity, delta, ms, bufferSource);
               } else if (state.getRenderShape() == RenderShape.INVISIBLE) {
                  VertexConsumer buf = bufferSource.getBuffer(RenderType.debugQuads());
                  Matrix4f m = ms.last().pose();
                  this.box(buf, m, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
               }

               ms.popPose();
            }
         }

         ObjectIterator var25 = this.highlightedEntities.reference2IntEntrySet().iterator();

         while (var25.hasNext()) {
            Entry<Entity> entry = (Entry<Entity>)var25.next();
            Entity entity = (Entity)entry.getKey();
            this.worldChain.renderAnything.setTrue();
            Vec3 p = entity.getPosition(delta);
            float yaw = Mth.lerp(delta, entity.yRotO, entity.getYRot());
            EntityRenderer<Entity> renderer = mc.getEntityRenderDispatcher().getRenderer(entity);
            HighlightRenderer.WrappedMultiBufferSource bufferSource = (HighlightRenderer.WrappedMultiBufferSource)sources.get(entry.getIntValue());
            if (renderer != null) {
               Vec3 off = renderer.getRenderOffset(entity, delta);
               double x1 = p.x + off.x();
               double y1 = p.y + off.y();
               double z1 = p.z + off.z();
               ms.pushPose();
               ms.translate(x1, y1, z1);
               renderer.render(entity, yaw, delta, ms, bufferSource, 15728880);
               ms.popPose();
            } else {
               VertexConsumer buf = bufferSource.getBuffer(RenderType.debugQuads());
               ms.pushPose();
               ms.translate(p.x, p.y, p.z);
               Matrix4f m = ms.last().pose();
               float w = entity.getBbWidth() / 2.0F;
               this.box(buf, m, -w, 0.0F, -w, w, entity.getBbHeight(), w);
               ms.popPose();
            }
         }

         ms.popPose();
         mc.renderBuffers().bufferSource().endBatch();
         mc.getMainRenderTarget().bindWrite(false);
      }
   }

   private void box(VertexConsumer buf, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1) {
      buf.addVertex(m, x0, y0, z0).setColor(255, 255, 255, 255);
      buf.addVertex(m, x1, y0, z0).setColor(255, 255, 255, 255);
      buf.addVertex(m, x1, y0, z1).setColor(255, 255, 255, 255);
      buf.addVertex(m, x0, y0, z1).setColor(255, 255, 255, 255);
      buf.addVertex(m, x0, y1, z0).setColor(255, 255, 255, 255);
      buf.addVertex(m, x0, y1, z1).setColor(255, 255, 255, 255);
      buf.addVertex(m, x1, y1, z1).setColor(255, 255, 255, 255);
      buf.addVertex(m, x1, y1, z0).setColor(255, 255, 255, 255);
      buf.addVertex(m, x0, y0, z0).setColor(255, 255, 255, 255);
      buf.addVertex(m, x0, y1, z0).setColor(255, 255, 255, 255);
      buf.addVertex(m, x1, y1, z0).setColor(255, 255, 255, 255);
      buf.addVertex(m, x1, y0, z0).setColor(255, 255, 255, 255);
      buf.addVertex(m, x0, y0, z1).setColor(255, 255, 255, 255);
      buf.addVertex(m, x1, y0, z1).setColor(255, 255, 255, 255);
      buf.addVertex(m, x1, y1, z1).setColor(255, 255, 255, 255);
      buf.addVertex(m, x0, y1, z1).setColor(255, 255, 255, 255);
      buf.addVertex(m, x0, y0, z0).setColor(255, 255, 255, 255);
      buf.addVertex(m, x0, y0, z1).setColor(255, 255, 255, 255);
      buf.addVertex(m, x0, y1, z1).setColor(255, 255, 255, 255);
      buf.addVertex(m, x0, y1, z0).setColor(255, 255, 255, 255);
      buf.addVertex(m, x1, y0, z0).setColor(255, 255, 255, 255);
      buf.addVertex(m, x1, y1, z0).setColor(255, 255, 255, 255);
      buf.addVertex(m, x1, y1, z1).setColor(255, 255, 255, 255);
      buf.addVertex(m, x1, y0, z1).setColor(255, 255, 255, 255);
   }

   public void screen(Minecraft mc, GuiGraphics graphics, AbstractContainerScreen<?> screen, int mx, int my, float delta) {
      if (this.guiChain != null && this.highlightShader != null) {
         while (this.actualKey && this.mode != HighlightRenderer.Mode.NONE && mc.options.keyInventory.consumeClick()) {
            this.keyToggled(mc, HighlightRenderer.Mode.NONE, false);
         }

         if (this.mode == HighlightRenderer.Mode.SCREEN) {
            AbstractContainerMenu menu = screen.getMenu();

            for (Slot slot : menu.slots) {
               int sx = slot.x + screen.getGuiLeft();
               int sy = slot.y + screen.getGuiTop();
               if (mx >= sx && mx < sx + 16 && my >= sy && my < sy + 16 && slot.hasItem()) {
                  this.hoveredSlots.add(slot);
               }
            }

            if (!this.hoveredSlots.isEmpty()) {
               this.guiChain.renderAnything.setTrue();
               graphics.flush();
               this.guiChain.renderInput.bindWrite(false);
               HighlightRenderer.WrappedMultiBufferSource bufferSource = new HighlightRenderer.WrappedMultiBufferSource(
                  mc.renderBuffers().bufferSource(), this.color.kjs$getRGB()
               );

               for (Slot slotx : this.hoveredSlots) {
                  int x = slotx.x + screen.getGuiLeft();
                  int y = slotx.y + screen.getGuiTop();
                  ItemStack stack = slotx.getItem();
                  BakedModel model = mc.getItemRenderer().getModel(stack, mc.level, mc.player, 0);
                  graphics.pose().pushPose();
                  graphics.pose().translate(x + 8.0F, y + 8.0F, 0.0F);
                  graphics.pose().scale(16.0F, -16.0F, 16.0F);

                  try {
                     ItemStack renderStack = stack.copy();
                     renderStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
                     renderStack.setDamageValue(0);
                     renderStack.setCount(1);
                     mc.getItemRenderer()
                        .render(renderStack, ItemDisplayContext.GUI, false, graphics.pose(), bufferSource, 15728880, OverlayTexture.NO_OVERLAY, model);
                  } catch (Throwable var18) {
                     CrashReport crashreport = CrashReport.forThrowable(var18, "Rendering item");
                     CrashReportCategory crashreportcategory = crashreport.addCategory("Item being rendered");
                     crashreportcategory.setDetail("Item Type", () -> String.valueOf(stack.getItem()));
                     crashreportcategory.setDetail("Item Components", () -> String.valueOf(stack.getComponents()));
                     crashreportcategory.setDetail("Item Foil", () -> String.valueOf(stack.hasFoil()));
                     throw new ReportedException(crashreport);
                  }

                  graphics.pose().popPose();
               }

               graphics.flush();
               mc.getMainRenderTarget().bindWrite(false);
               this.guiChain.draw(mc, delta);
            }
         }
      }
   }

   public void hudPostDraw(Minecraft mc, GuiGraphics graphics, float delta) {
   }

   public static enum Mode {
      NONE,
      SCREEN,
      WORLD;
   }

   public record ShaderChain(PostChain postChain, RenderTarget renderInput, RenderTarget mcDepthInput, RenderTarget renderOutput, MutableBoolean renderAnything) {
      @Nullable
      public static HighlightRenderer.ShaderChain load(Minecraft mc, ResourceLocation id) {
         try {
            PostChain postChain = new PostChain(mc.getTextureManager(), mc.getResourceManager(), mc.getMainRenderTarget(), id);
            postChain.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
            RenderTarget renderInput = postChain.getTempTarget("input");
            RenderTarget mcDepthInput = postChain.getTempTarget("mcdepth");
            RenderTarget renderOutput = postChain.getTempTarget("output");
            return new HighlightRenderer.ShaderChain(postChain, renderInput, mcDepthInput, renderOutput, new MutableBoolean(false));
         } catch (IOException var6) {
            KubeJS.LOGGER.warn("Failed to load shader: {}", id, var6);
         } catch (JsonSyntaxException var7) {
            KubeJS.LOGGER.warn("Failed to parse shader: {}", id, var7);
         }

         return null;
      }

      public void close() {
         this.postChain.close();
      }

      public void clearInput(Minecraft mc) {
         this.renderInput.clear(Minecraft.ON_OSX);
         mc.getMainRenderTarget().bindWrite(false);
         this.renderAnything.setFalse();
      }

      public void clearDepth(Minecraft mc, boolean copy) {
         this.mcDepthInput.clear(Minecraft.ON_OSX);
         if (copy) {
            this.mcDepthInput.copyDepthFrom(mc.getMainRenderTarget());
         }

         mc.getMainRenderTarget().bindWrite(false);
      }

      public void draw(Minecraft mc, float delta) {
         if (!this.renderAnything.isFalse()) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            this.postChain.setUniform("OutlineSize", (float)mc.getWindow().getGuiScale());
            this.postChain.process(delta);
            mc.getMainRenderTarget().bindWrite(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
            this.renderOutput.blitToScreen(mc.getWindow().getWidth(), mc.getWindow().getHeight(), false);
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            mc.getMainRenderTarget().bindWrite(false);
         }
      }
   }

   private record WrappedMultiBufferSource(MultiBufferSource delegate, int red, int green, int blue) implements MultiBufferSource {
      private WrappedMultiBufferSource(MultiBufferSource parent, int color) {
         this(parent, color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF);
      }

      public VertexConsumer getBuffer(RenderType renderType) {
         return new HighlightRenderer.WrappedVertexConsumer(
            this.delegate.getBuffer(new HighlightRenderer.WrappedRenderType(renderType)), this.red, this.green, this.blue
         );
      }
   }

   private static final class WrappedRenderType extends RenderType {
      public final RenderType delegate;

      public WrappedRenderType(RenderType delegate) {
         super("kubejs:wrapped", delegate.format(), delegate.mode(), delegate.bufferSize(), delegate.affectsCrumbling(), delegate.sortOnUpload(), () -> {
            delegate.setupRenderState();
            RenderSystem.setShader(() -> HighlightRenderer.INSTANCE.highlightShader);
         }, delegate::clearRenderState);
         this.delegate = delegate;
      }

      public String toString() {
         return "kubejs:wrapped[" + this.delegate + "]";
      }
   }

   private record WrappedVertexConsumer(VertexConsumer delegate, int red, int green, int blue) implements VertexConsumer {
      public VertexConsumer addVertex(float f, float g, float h) {
         this.delegate.addVertex(f, g, h);
         return this;
      }

      public VertexConsumer setColor(int i, int j, int k, int l) {
         this.delegate.setColor(this.red, this.green, this.blue, 255);
         return this;
      }

      public VertexConsumer setUv(float f, float g) {
         this.delegate.setUv(f, g);
         return this;
      }

      public VertexConsumer setUv1(int i, int j) {
         this.delegate.setUv1(i, j);
         return this;
      }

      public VertexConsumer setUv2(int i, int j) {
         this.delegate.setUv2(i, j);
         return this;
      }

      public VertexConsumer setNormal(float f, float g, float h) {
         this.delegate.setNormal(f, g, h);
         return this;
      }
   }
}

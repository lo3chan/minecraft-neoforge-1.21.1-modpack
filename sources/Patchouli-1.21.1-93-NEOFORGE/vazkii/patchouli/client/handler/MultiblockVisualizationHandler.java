package vazkii.patchouli.client.handler;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.awt.Color;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.SequencedMap;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.joml.Matrix4f;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.common.multiblock.StateMatcher;
import vazkii.patchouli.common.util.RotationUtil;
import vazkii.patchouli.mixin.client.AccessorMultiBufferSource;

public class MultiblockVisualizationHandler {
   public static boolean hasMultiblock;
   public static PersistentData.Bookmark bookmark;
   private static IMultiblock multiblock;
   private static Component name;
   private static BlockPos pos;
   private static boolean isAnchored;
   private static Rotation facingRotation;
   private static Function<BlockPos, BlockPos> offsetApplier;
   private static int blocks;
   private static int blocksDone;
   private static int airFilled;
   private static int timeComplete;
   private static BlockState lookingState;
   private static BlockPos lookingPos;
   private static BufferSource buffers = null;

   public static void setMultiblock(IMultiblock multiblock, Component name, PersistentData.Bookmark bookmark, boolean flip) {
      setMultiblock(multiblock, name, bookmark, flip, pos -> pos);
   }

   public static void setMultiblock(
      IMultiblock multiblock, Component name, PersistentData.Bookmark bookmark, boolean flip, Function<BlockPos, BlockPos> offsetApplier
   ) {
      if (flip && hasMultiblock) {
         hasMultiblock = false;
      } else {
         MultiblockVisualizationHandler.multiblock = multiblock;
         MultiblockVisualizationHandler.name = name;
         MultiblockVisualizationHandler.bookmark = bookmark;
         MultiblockVisualizationHandler.offsetApplier = offsetApplier;
         pos = null;
         hasMultiblock = multiblock != null;
         isAnchored = false;
      }
   }

   public static void onRenderHUD(GuiGraphics graphics, DeltaTracker deltaTracker) {
      if (hasMultiblock) {
         int waitTime = 40;
         int fadeOutSpeed = 4;
         int fullAnimTime = waitTime + 10;
         float animTime = timeComplete + (timeComplete == 0 ? 0.0F : deltaTracker.getGameTimeDeltaPartialTick(false));
         if (animTime > fullAnimTime) {
            hasMultiblock = false;
            return;
         }

         graphics.pose().pushPose();
         graphics.pose().translate(0.0F, -Math.max(0.0F, animTime - waitTime) * fadeOutSpeed, 0.0F);
         Minecraft mc = Minecraft.getInstance();
         int x = mc.getWindow().getGuiScaledWidth() / 2;
         int y = 12;
         graphics.drawCenteredString(mc.font, name, x, y, 16777215);
         int width = 180;
         int height = 9;
         int left = x - width / 2;
         int top = y + 10;
         if (timeComplete > 0) {
            graphics.pose().pushPose();
            graphics.pose().translate(0.0F, Math.min((float)(height + 5), animTime), 0.0F);
            graphics.drawCenteredString(mc.font, Component.translatable("patchouli.gui.lexicon.structure_complete"), x, top + height - 10, 65280);
            graphics.pose().popPose();
         }

         graphics.fill(left - 1, top - 1, left + width + 1, top + height + 1, -16777216);
         drawGradientRect(graphics, left, top, left + width, top + height, -10066330, -11184811);
         float fract = (float)blocksDone / Math.max(1, blocks);
         int progressWidth = (int)(width * fract);
         int color = Mth.hsvToRgb(fract / 3.0F, 1.0F, 1.0F) | 0xFF000000;
         int color2 = new Color(color).darker().getRGB();
         drawGradientRect(graphics, left, top, left + progressWidth, top + height, color, color2);
         if (!isAnchored) {
            graphics.drawCenteredString(mc.font, Component.translatable("patchouli.gui.lexicon.not_anchored"), x, top + height + 8, 16777215);
         } else {
            if (lookingState != null) {
               try {
                  Block block = lookingState.getBlock();
                  ItemStack stack = block.getCloneItemStack(mc.level, lookingPos, lookingState);
                  if (!stack.isEmpty()) {
                     graphics.drawString(mc.font, stack.getHoverName(), left + 20, top + height + 8, 16777215, true);
                     graphics.renderItem(stack, left, top + height + 2);
                  }
               } catch (Exception var21) {
               }
            }

            if (timeComplete == 0) {
               color = 16777215;
               int posx = left + width;
               int posy = top + height + 2;
               int mult = 1;
               String progress = blocksDone + "/" + blocks;
               if (blocksDone == blocks && airFilled > 0) {
                  progress = I18n.get("patchouli.gui.lexicon.needs_air", new Object[0]);
                  color = 14306879;
                  mult *= 2;
                  posx -= width / 2;
                  posy += 2;
               }

               graphics.drawString(mc.font, progress, posx - mc.font.width(progress) / mult, posy, color, false);
            }
         }

         graphics.pose().popPose();
      }
   }

   public static void onWorldRenderLast(PoseStack ms, Matrix4f pose) {
      if (hasMultiblock && multiblock != null) {
         renderMultiblock(Minecraft.getInstance().level, ms, pose);
      }
   }

   public static void anchorTo(BlockPos target, Rotation rot) {
      pos = target;
      facingRotation = rot;
      isAnchored = true;
   }

   public static InteractionResult onPlayerInteract(Player player, Level world, InteractionHand hand, BlockHitResult hit) {
      if (hasMultiblock && !isAnchored && player == Minecraft.getInstance().player) {
         anchorTo(hit.getBlockPos(), getRotation(player));
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public static void onClientTick(Minecraft mc) {
      if (Minecraft.getInstance().level == null) {
         hasMultiblock = false;
      } else if (isAnchored && blocks == blocksDone && airFilled == 0) {
         timeComplete++;
         if (timeComplete == 14) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F));
         }
      } else {
         timeComplete = 0;
      }
   }

   public static void renderMultiblock(Level world, PoseStack ms, Matrix4f pose) {
      ms.mulPose(pose);
      Minecraft mc = Minecraft.getInstance();
      if (!isAnchored) {
         facingRotation = getRotation(mc.player);
         if (mc.hitResult instanceof BlockHitResult) {
            pos = ((BlockHitResult)mc.hitResult).getBlockPos();
         }
      } else if (pos.distToCenterSqr(mc.player.position()) > 4096.0) {
         return;
      }

      if (pos != null) {
         if (multiblock.isSymmetrical()) {
            facingRotation = Rotation.NONE;
         }

         EntityRenderDispatcher erd = mc.getEntityRenderDispatcher();
         double renderPosX = erd.camera.getPosition().x();
         double renderPosY = erd.camera.getPosition().y();
         double renderPosZ = erd.camera.getPosition().z();
         ms.pushPose();
         ms.translate(-renderPosX, -renderPosY, -renderPosZ);
         if (buffers == null) {
            buffers = initBuffers(mc.renderBuffers().bufferSource());
         }

         BlockPos checkPos = null;
         if (mc.hitResult instanceof BlockHitResult blockRes) {
            checkPos = blockRes.getBlockPos().relative(blockRes.getDirection());
         }

         airFilled = 0;
         blocksDone = 0;
         blocks = 0;
         lookingState = null;
         lookingPos = checkPos;
         Pair<BlockPos, Collection<IMultiblock.SimulateResult>> sim = multiblock.simulate(world, getStartPos(), getFacingRotation(), true);

         for (IMultiblock.SimulateResult r : (Collection)sim.getSecond()) {
            float alpha = 0.3F;
            if (r.getWorldPosition().equals(checkPos)) {
               lookingState = r.getStateMatcher().getDisplayedState(ClientTicker.ticksInGame);
               alpha = 0.6F + (float)(Math.sin(ClientTicker.total * 0.3F) + 1.0) * 0.1F;
            }

            if (r.getStateMatcher() != StateMatcher.ANY) {
               boolean air = r.getStateMatcher() == StateMatcher.AIR;
               if (!air) {
                  blocks++;
               }

               if (!r.test(world, facingRotation)) {
                  BlockState renderState = r.getStateMatcher().getDisplayedState(ClientTicker.ticksInGame).rotate(facingRotation);
                  renderBlock(world, renderState, r.getWorldPosition(), alpha, ms);
                  if (air) {
                     airFilled++;
                  }
               } else if (!air) {
                  blocksDone++;
               }
            }
         }

         buffers.endBatch();
         ms.popPose();
         if (!isAnchored) {
            blocksDone = 0;
            blocks = 0;
         }
      }
   }

   public static void renderBlock(Level world, BlockState state, BlockPos pos, float alpha, PoseStack ms) {
      if (pos != null) {
         ms.pushPose();
         ms.translate(pos.getX(), pos.getY(), pos.getZ());
         if (state.getBlock() == Blocks.AIR) {
            float scale = 0.3F;
            float off = (1.0F - scale) / 2.0F;
            ms.translate(off, off, -off);
            ms.scale(scale, scale, scale);
            state = Blocks.RED_CONCRETE.defaultBlockState();
         }

         Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, ms, buffers, 15728880, OverlayTexture.NO_OVERLAY);
         ms.popPose();
      }
   }

   public static IMultiblock getMultiblock() {
      return multiblock;
   }

   public static boolean isAnchored() {
      return isAnchored;
   }

   public static Rotation getFacingRotation() {
      return multiblock.isSymmetrical() ? Rotation.NONE : facingRotation;
   }

   public static BlockPos getStartPos() {
      return offsetApplier.apply(pos);
   }

   private static void drawGradientRect(GuiGraphics graphics, int left, int top, int right, int bottom, int startColor, int endColor) {
      float f = (startColor >> 24 & 0xFF) / 255.0F;
      float f1 = (startColor >> 16 & 0xFF) / 255.0F;
      float f2 = (startColor >> 8 & 0xFF) / 255.0F;
      float f3 = (startColor & 0xFF) / 255.0F;
      float f4 = (endColor >> 24 & 0xFF) / 255.0F;
      float f5 = (endColor >> 16 & 0xFF) / 255.0F;
      float f6 = (endColor >> 8 & 0xFF) / 255.0F;
      float f7 = (endColor & 0xFF) / 255.0F;
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      Tesselator tessellator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tessellator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      Matrix4f mat = graphics.pose().last().pose();
      bufferbuilder.addVertex(mat, right, top, 0.0F).setColor(f1, f2, f3, f);
      bufferbuilder.addVertex(mat, left, top, 0.0F).setColor(f1, f2, f3, f);
      bufferbuilder.addVertex(mat, left, bottom, 0.0F).setColor(f5, f6, f7, f4);
      bufferbuilder.addVertex(mat, right, bottom, 0.0F).setColor(f5, f6, f7, f4);
      BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
      RenderSystem.disableBlend();
   }

   private static Rotation getRotation(Entity entity) {
      return RotationUtil.rotationFromFacing(entity.getDirection());
   }

   private static BufferSource initBuffers(BufferSource original) {
      ByteBufferBuilder fallback = ((AccessorMultiBufferSource)original).getFallbackBuffer();
      SequencedMap<RenderType, ByteBufferBuilder> layerBuffers = ((AccessorMultiBufferSource)original).getFixedBuffers();
      SequencedMap<RenderType, ByteBufferBuilder> remapped = new Object2ObjectLinkedOpenHashMap();

      for (Entry<RenderType, ByteBufferBuilder> e : layerBuffers.entrySet()) {
         remapped.put(MultiblockVisualizationHandler.GhostRenderLayer.remap(e.getKey()), e.getValue());
      }

      return new MultiblockVisualizationHandler.GhostBuffers(fallback, remapped);
   }

   private static class GhostBuffers extends BufferSource {
      protected GhostBuffers(ByteBufferBuilder fallback, SequencedMap<RenderType, ByteBufferBuilder> layerBuffers) {
         super(fallback, layerBuffers);
      }

      public VertexConsumer getBuffer(RenderType type) {
         return super.getBuffer(MultiblockVisualizationHandler.GhostRenderLayer.remap(type));
      }
   }

   private static class GhostRenderLayer extends RenderType {
      private static final Map<RenderType, RenderType> remappedTypes = new IdentityHashMap<>();

      private GhostRenderLayer(RenderType original) {
         super(
            String.format("%s_%s_ghost", original.toString(), "patchouli"),
            original.format(),
            original.mode(),
            original.bufferSize(),
            original.affectsCrumbling(),
            true,
            () -> {
               original.setupRenderState();
               RenderSystem.disableDepthTest();
               RenderSystem.enableBlend();
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.4F);
            },
            () -> {
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               RenderSystem.disableBlend();
               RenderSystem.enableDepthTest();
               original.clearRenderState();
            }
         );
      }

      public static RenderType remap(RenderType in) {
         return in instanceof MultiblockVisualizationHandler.GhostRenderLayer
            ? in
            : remappedTypes.computeIfAbsent(in, MultiblockVisualizationHandler.GhostRenderLayer::new);
      }
   }
}

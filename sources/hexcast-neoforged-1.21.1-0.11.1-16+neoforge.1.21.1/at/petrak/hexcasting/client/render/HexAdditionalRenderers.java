package at.petrak.hexcasting.client.render;

import at.petrak.hexcasting.api.client.ScryingLensOverlayRegistry;
import at.petrak.hexcasting.api.pigment.ColorProvider;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.player.Sentinel;
import at.petrak.hexcasting.api.utils.QuaternionfUtils;
import at.petrak.hexcasting.client.ClientTickCounter;
import at.petrak.hexcasting.common.lib.HexAttributes;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class HexAdditionalRenderers {
   public static void overlayLevel(PoseStack ps, float partialTick) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         Sentinel sentinel = IXplatAbstractions.INSTANCE.getSentinel(player);
         if (sentinel != null && player.level().dimension().equals(sentinel.dimension())) {
            renderSentinel(sentinel, player, ps, partialTick);
         }
      }
   }

   public static void overlayGui(GuiGraphics graphics, float partialTicks) {
      tryRenderScryingLensOverlay(graphics, partialTicks);
   }

   private static void renderSentinel(Sentinel sentinel, LocalPlayer owner, PoseStack ps, float partialTicks) {
      ps.pushPose();
      Minecraft mc = Minecraft.getInstance();
      Camera camera = mc.gameRenderer.getMainCamera();
      Vec3 playerPos = camera.getPosition();
      ps.translate(sentinel.position().x - playerPos.x, sentinel.position().y - playerPos.y, sentinel.position().z - playerPos.z);
      float time = ClientTickCounter.getTotal() / 2.0F;
      float bobSpeed = 0.05F;
      float magnitude = 0.1F;
      ps.translate(0.0F, Mth.sin(bobSpeed * time) * magnitude, 0.0F);
      float spinSpeed = 0.033333335F;
      ps.mulPose(QuaternionfUtils.fromXYZ(new Vector3f(0.0F, spinSpeed * time, 0.0F)));
      if (sentinel.extendsRange()) {
         ps.mulPose(QuaternionfUtils.fromXYZ(new Vector3f(spinSpeed * time / 8.0F, 0.0F, 0.0F)));
      }

      float scale = 0.5F;
      ps.scale(scale, scale, scale);
      BufferBuilder buf = Tesselator.getInstance().begin(Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
      Matrix4f neo = ps.last().pose();
      RenderSystem.enableBlend();
      RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
      RenderSystem.disableDepthTest();
      RenderSystem.disableCull();
      RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.lineWidth(5.0F);
      FrozenPigment pigment = IXplatAbstractions.INSTANCE.getPigment(owner);
      ColorProvider colProvider = pigment.getColorProvider();
      BiConsumer<float[], float[]> v = (l, r) -> {
         int lcolor = colProvider.getColor(time, new Vec3(l[0], l[1], l[2]));
         int rcolor = colProvider.getColor(time, new Vec3(r[0], r[1], r[2]));
         Vector3f normal = new Vector3f(r[0] - l[0], r[1] - l[1], r[2] - l[2]);
         normal.normalize();
         buf.addVertex(neo, l[0], l[1], l[2]).setColor(lcolor).setNormal(normal.x(), normal.y(), normal.z());
         buf.addVertex(neo, r[0], r[1], r[2]).setColor(rcolor).setNormal(-normal.x(), -normal.y(), -normal.z());
      };

      for (int side = 0; side <= 1; side++) {
         float[][] ring = side == 0 ? HexAdditionalRenderers.Icos.BOTTOM_RING : HexAdditionalRenderers.Icos.TOP_RING;
         float[] apex = side == 0 ? HexAdditionalRenderers.Icos.BOTTOM : HexAdditionalRenderers.Icos.TOP;

         for (int i = 0; i < 5; i++) {
            v.accept(apex, ring[i]);
         }

         for (int i = 0; i < 5; i++) {
            v.accept(ring[i % 5], ring[(i + 1) % 5]);
         }
      }

      for (int i = 0; i < 5; i++) {
         float[] bottom = HexAdditionalRenderers.Icos.BOTTOM_RING[i];
         v.accept(HexAdditionalRenderers.Icos.TOP_RING[(i + 2) % 5], bottom);
         v.accept(bottom, HexAdditionalRenderers.Icos.TOP_RING[(i + 3) % 5]);
      }

      BufferUploader.drawWithShader(buf.buildOrThrow());
      RenderSystem.enableDepthTest();
      RenderSystem.enableCull();
      ps.popPose();
   }

   private static void tryRenderScryingLensOverlay(GuiGraphics graphics, float partialTicks) {
      Minecraft mc = Minecraft.getInstance();
      PoseStack ps = graphics.pose();
      LocalPlayer player = mc.player;
      ClientLevel level = mc.level;
      if (player != null && level != null) {
         if (!(player.getAttributeValue(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(HexAttributes.SCRY_SIGHT)) <= 0.0)) {
            HitResult hitRes = mc.hitResult;
            if (hitRes != null && hitRes.getType() == Type.BLOCK) {
               BlockHitResult bhr = (BlockHitResult)hitRes;
               BlockPos pos = bhr.getBlockPos();
               BlockState bs = level.getBlockState(pos);
               List<Pair<ItemStack, Component>> lines = ScryingLensOverlayRegistry.getLines(bs, pos, player, level, bhr.getDirection());
               int totalHeight = 8;
               List<Pair<ItemStack, List<FormattedText>>> actualLines = Lists.newArrayList();
               Window window = mc.getWindow();
               int maxWidth = (int)(window.getGuiScaledWidth() / 2.0F * 0.8F);

               for (Pair<ItemStack, Component> pair : lines) {
                  totalHeight += 9 + 6;
                  Component text = (Component)pair.getSecond();
                  List<FormattedText> textLines = mc.font.getSplitter().splitLines(text, maxWidth, Style.EMPTY);
                  actualLines.add(Pair.of((ItemStack)pair.getFirst(), textLines));
                  if (textLines.size() > 1) {
                     totalHeight += 9 * (textLines.size() - 1);
                  }
               }

               if (!lines.isEmpty()) {
                  float x = window.getGuiScaledWidth() / 2.0F + 8.0F;
                  float y = window.getGuiScaledHeight() / 2.0F - totalHeight;
                  ps.pushPose();
                  ps.translate(x, y, 0.0F);

                  for (Pair<ItemStack, List<FormattedText>> pairx : actualLines) {
                     ItemStack stack = (ItemStack)pairx.getFirst();
                     if (!stack.isEmpty()) {
                        graphics.renderItem((ItemStack)pairx.getFirst(), 0, 0);
                     }

                     int tx = stack.isEmpty() ? 0 : 18;
                     int ty = 5;
                     List<FormattedText> text = (List<FormattedText>)pairx.getSecond();

                     for (FormattedText line : text) {
                        FormattedCharSequence actualLine = Language.getInstance().getVisualOrder(line);
                        graphics.drawString(mc.font, actualLine, tx, ty, -1);
                        ps.translate(0.0F, 9.0F, 0.0F);
                     }

                     if (text.isEmpty()) {
                        ps.translate(0.0F, 9.0F, 0.0F);
                     }

                     ps.translate(0.0F, 6.0F, 0.0F);
                  }

                  ps.popPose();
               }
            }
         }
      }
   }

   private static class Icos {
      public static float[] TOP = new float[]{0.0F, 1.0F, 0.0F};
      public static float[] BOTTOM = new float[]{0.0F, -1.0F, 0.0F};
      public static float[][] TOP_RING = new float[5][];
      public static float[][] BOTTOM_RING = new float[5][];

      static {
         float theta = (float)Mth.atan2(0.5, 1.0);

         for (int i = 0; i < 5; i++) {
            float phi = i / 5.0F * 6.2831855F;
            float x = Mth.cos(theta) * Mth.cos(phi);
            float y = Mth.sin(theta);
            float z = Mth.cos(theta) * Mth.sin(phi);
            TOP_RING[i] = new float[]{x, y, z};
            BOTTOM_RING[i] = new float[]{-x, -y, -z};
         }
      }
   }
}

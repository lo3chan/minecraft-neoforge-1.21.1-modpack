package at.petrak.hexcasting.client.gui;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.client.render.RenderLib;
import at.petrak.hexcasting.common.misc.PatternTooltip;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.stream.Collectors;
import kotlin.Pair;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class PatternTooltipComponent implements ClientTooltipComponent {
   public static final ResourceLocation PRISTINE_BG = HexAPI.modLoc("textures/gui/scroll.png");
   public static final ResourceLocation ANCIENT_BG = HexAPI.modLoc("textures/gui/scroll_ancient.png");
   public static final ResourceLocation SLATE_BG = HexAPI.modLoc("textures/gui/slate.png");
   private static final float RENDER_SIZE = 128.0F;
   private static final int TEXTURE_SIZE = 48;
   private final HexPattern pattern;
   private final List<Vec2> zappyPoints;
   private final List<Vec2> pathfinderDots;
   private final float scale;
   private final ResourceLocation background;

   public PatternTooltipComponent(PatternTooltip tt) {
      this.pattern = tt.pattern();
      this.background = tt.background();
      Pair<Float, List<Vec2>> pair = RenderLib.getCenteredPattern(this.pattern, 128.0F, 128.0F, 16.0F);
      this.scale = (Float)pair.getFirst();
      List<Vec2> dots = (List<Vec2>)pair.getSecond();
      this.zappyPoints = RenderLib.makeZappy(dots, RenderLib.findDupIndices(this.pattern.positions()), 10, 0.8F, 0.0F, 0.0F, 0.2F, 0.8F, 0.0);
      this.pathfinderDots = dots.stream().distinct().collect(Collectors.toList());
   }

   @Nullable
   public static ClientTooltipComponent tryConvert(TooltipComponent cmp) {
      return cmp instanceof PatternTooltip ptt ? new PatternTooltipComponent(ptt) : null;
   }

   public void renderImage(Font font, int mouseX, int mouseY, GuiGraphics graphics) {
      int width = this.getWidth(font);
      int height = this.getHeight();
      PoseStack ps = graphics.pose();
      ps.pushPose();
      ps.translate(mouseX, mouseY, 500.0F);
      RenderSystem.enableBlend();
      renderBG(graphics, this.background);
      ps.translate(0.0F, 0.0F, 100.0F);
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      RenderSystem.disableCull();
      RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      ps.translate(width / 2.0F, height / 2.0F, 1.0F);
      Matrix4f mat = ps.last().pose();
      int outer = -2963256;
      int innerLight = -928275806;
      int innerDark = -936236237;
      RenderLib.drawLineSeq(mat, this.zappyPoints, 6.0F, 0.0F, outer, outer);
      RenderLib.drawLineSeq(mat, this.zappyPoints, 2.4F, 0.0F, innerDark, innerLight);
      RenderLib.drawSpot(mat, this.zappyPoints.get(0), 2.5F, 1.0F, 0.1F, 0.15F, 0.6F);

      for (Vec2 dot : this.pathfinderDots) {
         RenderLib.drawSpot(mat, dot, 1.5F, 0.82F, 0.8F, 0.8F, 0.5F);
      }

      ps.popPose();
   }

   private static void renderBG(GuiGraphics graphics, ResourceLocation background) {
      graphics.blit(background, 0, 0, 128, 128, 0.0F, 0.0F, 48, 48, 48, 48);
   }

   public int getWidth(Font pFont) {
      return 128;
   }

   public int getHeight() {
      return 128;
   }
}

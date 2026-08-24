package at.petrak.hexcasting.forge.interop.jei;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.math.HexCoord;
import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.interop.utils.PatternDrawingUtil;
import at.petrak.hexcasting.interop.utils.PatternEntry;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

public class PatternDrawable implements IDrawable {
   private final int width;
   private final int height;
   private boolean strokeOrder;
   private final List<PatternEntry> patterns;
   private final List<Vec2> pathfinderDots;

   public PatternDrawable(ResourceLocation pattern, int w, int h) {
      Registry<ActionRegistryEntry> regi = IXplatAbstractions.INSTANCE.getActionRegistry();
      ActionRegistryEntry entry = (ActionRegistryEntry)regi.get(pattern);
      this.strokeOrder = !HexUtils.isOfTag(regi, pattern, HexTags.Actions.PER_WORLD_PATTERN);
      PatternDrawingUtil.PatternRenderingData data = PatternDrawingUtil.loadPatterns(List.of(new Pair(entry.prototype(), HexCoord.getOrigin())), 0.0F, 1.0F);
      this.patterns = data.patterns();
      this.pathfinderDots = data.pathfinderDots();
      this.width = w;
      this.height = h;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public PatternDrawable strokeOrder(boolean order) {
      this.strokeOrder = order;
      return this;
   }

   public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
      PoseStack ps = guiGraphics.pose();
      ps.pushPose();
      ps.translate(xOffset - 0.5F + this.width / 2.0F, yOffset + this.height / 2.0F, 0.0F);
      ps.scale(this.width / 64.0F, this.height / 64.0F, 1.0F);
      PatternDrawingUtil.drawPattern(guiGraphics, 0, 0, this.patterns, this.pathfinderDots, this.strokeOrder, -13422544, -15132648, -938735092, -2140773533);
      ps.popPose();
   }
}

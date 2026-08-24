package snownee.jade.impl.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.Element;
import snownee.jade.api.ui.IDisplayHelper;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.ProgressStyle;
import snownee.jade.overlay.DisplayHelper;
import snownee.jade.overlay.WailaTickHandler;
import snownee.jade.track.ProgressTrackInfo;

public class ProgressElement extends Element implements StyledElement {
   private final float progress;
   @Nullable
   private final Component text;
   private final ProgressStyle style;
   private final BoxStyle boxStyle;
   private ProgressTrackInfo track;
   private boolean canDecrease;

   public ProgressElement(float progress, Component text, ProgressStyle style, BoxStyle boxStyle, boolean canDecrease) {
      this.progress = Mth.clamp(progress, 0.0F, 1.0F);
      this.text = text;
      this.style = style;
      this.boxStyle = boxStyle;
      this.canDecrease = canDecrease;
   }

   @Override
   public Vec2 getSize() {
      int height = this.text == null ? 8 : 14;
      float width = 0.0F;
      width += this.boxStyle.borderWidth() * 2.0F;
      if (this.text != null) {
         width += DisplayHelper.font().width(this.text) + 3;
      }

      float var5;
      float finalWidth = var5 = Math.max(20.0F, width);
      if (this.getTag() != null) {
         this.track = WailaTickHandler.instance()
            .progressTracker
            .getOrCreate(this.getTag(), ProgressTrackInfo.class, () -> new ProgressTrackInfo(this.canDecrease, this.progress, finalWidth));
         this.track.setExpectedWidth(var5);
         var5 = this.track.getWidth();
      }

      return new Vec2(var5, height);
   }

   @Override
   public void render(GuiGraphics guiGraphics, float x, float y, float maxX, float maxY) {
      float width = this.style.direction().isHorizontal() && this.style.fitContentX() ? maxX - x : this.getCachedSize().x;
      float height = this.style.direction().isVertical() && this.style.fitContentY() ? maxY - y : this.getCachedSize().y;
      x = this.style.direction().isHorizontal() ? x : x + (maxX - x - width) / 2.0F;
      y = this.style.direction().isVertical() ? y : y + (maxY - y - height) / 2.0F;
      this.boxStyle.render(guiGraphics, this, x, y, width, height, IDisplayHelper.get().opacity());
      float progress = this.progress;
      if (this.track == null && this.getTag() != null) {
         this.track = WailaTickHandler.instance()
            .progressTracker
            .getOrCreate(this.getTag(), ProgressTrackInfo.class, () -> new ProgressTrackInfo(this.canDecrease, this.progress, width));
      }

      if (this.track != null) {
         this.track.setProgress(progress);
         this.track.update(Minecraft.getInstance().getTimer().getRealtimeDeltaTicks());
         progress = this.track.getSmoothProgress();
      }

      float b = this.boxStyle.borderWidth();
      this.style.render(guiGraphics, x + b, y + b, width - b * 2.0F, height - b * 2.0F, progress, this.text);
   }

   @Nullable
   @Override
   public String getMessage() {
      return this.text == null ? null : this.text.getString();
   }

   @Override
   public IElement getIcon() {
      return null;
   }

   @Override
   public BoxStyle getStyle() {
      return this.boxStyle;
   }
}

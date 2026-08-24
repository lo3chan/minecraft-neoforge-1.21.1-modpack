package snownee.jade.api.ui;

import java.util.Objects;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public abstract class ProgressStyle {
   @Nullable
   protected IElement overlay;
   protected boolean fitContentX = true;
   protected boolean fitContentY = true;
   protected ScreenDirection direction = ScreenDirection.RIGHT;

   public ProgressStyle color(int color) {
      return this.color(color, color);
   }

   public abstract ProgressStyle color(int var1, int var2);

   public abstract ProgressStyle textColor(int var1);

   public ProgressStyle direction(ScreenDirection direction) {
      this.direction = Objects.requireNonNull(direction);
      return this;
   }

   public ScreenDirection direction() {
      return this.direction;
   }

   public ProgressStyle overlay(IElement overlay) {
      this.overlay = overlay;
      return this;
   }

   public ProgressStyle fitContentX(boolean fitContentX) {
      this.fitContentX = fitContentX;
      return this;
   }

   public boolean fitContentX() {
      return this.fitContentX;
   }

   public ProgressStyle fitContentY(boolean fitContentY) {
      this.fitContentY = fitContentY;
      return this;
   }

   public boolean fitContentY() {
      return this.fitContentY;
   }

   public abstract void render(GuiGraphics var1, float var2, float var3, float var4, float var5, float var6, Component var7);
}

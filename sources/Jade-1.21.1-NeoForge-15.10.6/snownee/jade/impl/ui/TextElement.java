package snownee.jade.impl.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.Element;
import snownee.jade.api.ui.ITextElement;
import snownee.jade.overlay.DisplayHelper;

public class TextElement extends Element implements ITextElement {
   public final FormattedText text;

   public TextElement(Component component) {
      this.text = component;
   }

   public TextElement(FormattedText text) {
      this.text = text;
   }

   @Override
   public Vec2 getSize() {
      return new Vec2(Math.max(DisplayHelper.font().width(this.text), 0), 9 - 1);
   }

   @Override
   public void render(GuiGraphics guiGraphics, float x, float y, float maxX, float maxY) {
      DisplayHelper.INSTANCE.drawText(guiGraphics, this.text, x, y, IThemeHelper.get().getNormalColor());
   }

   @Nullable
   @Override
   public String getMessage() {
      return this.text.getString();
   }

   public SpecialTextElement toSpecial() {
      return new SpecialTextElement(this.text);
   }

   @Override
   public ITextElement scale(float scale) {
      return this.toSpecial().scale(scale);
   }

   @Override
   public ITextElement zOffset(int zOffset) {
      return this.toSpecial().zOffset(zOffset);
   }
}

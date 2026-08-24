package mezz.jei.api.gui.widgets;

import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.IPlaceable;
import mezz.jei.api.gui.placement.VerticalAlignment;
import net.minecraft.client.gui.Font;

public interface ITextWidget extends IPlaceable<ITextWidget> {
   ITextWidget setFont(Font var1);

   ITextWidget setColor(int var1);

   ITextWidget setLineSpacing(int var1);

   ITextWidget setShadow(boolean var1);

   ITextWidget setTextAlignment(HorizontalAlignment var1);

   ITextWidget setTextAlignment(VerticalAlignment var1);

   @Deprecated(
      since = "19.19.0",
      forRemoval = true
   )
   default ITextWidget alignHorizontalLeft() {
      return this.setTextAlignment(HorizontalAlignment.LEFT);
   }

   @Deprecated(
      since = "19.19.0",
      forRemoval = true
   )
   default ITextWidget alignHorizontalCenter() {
      return this.setTextAlignment(HorizontalAlignment.CENTER);
   }

   @Deprecated(
      since = "19.19.0",
      forRemoval = true
   )
   default ITextWidget alignHorizontalRight() {
      return this.setTextAlignment(HorizontalAlignment.RIGHT);
   }

   @Deprecated(
      since = "19.19.0",
      forRemoval = true
   )
   default ITextWidget alignVerticalTop() {
      return this.setTextAlignment(VerticalAlignment.TOP);
   }

   @Deprecated(
      since = "19.19.0",
      forRemoval = true
   )
   default ITextWidget alignVerticalCenter() {
      return this.setTextAlignment(VerticalAlignment.CENTER);
   }

   @Deprecated(
      since = "19.19.0",
      forRemoval = true
   )
   default ITextWidget alignVerticalBottom() {
      return this.setTextAlignment(VerticalAlignment.BOTTOM);
   }
}

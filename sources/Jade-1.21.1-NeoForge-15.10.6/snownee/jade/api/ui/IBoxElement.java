package snownee.jade.api.ui;

import org.jetbrains.annotations.Nullable;
import snownee.jade.api.ITooltip;
import snownee.jade.impl.ui.StyledElement;

public interface IBoxElement extends IElement, StyledElement {
   ITooltip getTooltip();

   void setBoxProgress(MessageType var1, float var2);

   float getBoxProgress();

   void clearBoxProgress();

   void setIcon(@Nullable IElement var1);

   int padding(ScreenDirection var1);

   void setPadding(ScreenDirection var1, int var2);

   @Override
   BoxStyle getStyle();
}

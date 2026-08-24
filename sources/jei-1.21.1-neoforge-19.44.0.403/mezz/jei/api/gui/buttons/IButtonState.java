package mezz.jei.api.gui.buttons;

import mezz.jei.api.gui.drawable.IDrawable;

public interface IButtonState {
   void setIcon(IDrawable var1);

   void setActive(boolean var1);

   void setVisible(boolean var1);

   void setForcePressed(boolean var1);
}

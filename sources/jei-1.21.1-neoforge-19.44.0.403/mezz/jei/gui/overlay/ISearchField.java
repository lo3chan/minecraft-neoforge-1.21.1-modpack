package mezz.jei.gui.overlay;

import mezz.jei.common.util.ImmutableRect2i;

public interface ISearchField {
   void setValue(String var1);

   void setFocused(boolean var1);

   void updateBounds(ImmutableRect2i var1);
}

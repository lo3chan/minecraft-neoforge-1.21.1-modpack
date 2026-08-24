package mezz.jei.api.gui.widgets;

import java.util.List;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import net.minecraft.network.chat.FormattedText;

public interface IScrollBoxWidget extends IRecipeWidget, IJeiInputHandler {
   int getContentAreaWidth();

   int getContentAreaHeight();

   IScrollBoxWidget setContents(IDrawable var1);

   IScrollBoxWidget setContents(List<FormattedText> var1);
}

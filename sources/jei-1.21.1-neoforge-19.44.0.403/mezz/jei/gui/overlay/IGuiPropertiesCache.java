package mezz.jei.gui.overlay;

import java.util.Set;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.common.util.ImmutableRect2i;
import org.jetbrains.annotations.Nullable;

public interface IGuiPropertiesCache {
   IScreenPropertiesUpdater createUpdater(Runnable var1);

   @Nullable
   IGuiProperties getGuiProperties();

   Set<ImmutableRect2i> getGuiExclusionAreas();
}

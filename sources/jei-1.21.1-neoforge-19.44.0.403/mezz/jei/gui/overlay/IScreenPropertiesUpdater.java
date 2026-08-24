package mezz.jei.gui.overlay;

import java.util.Set;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.common.util.ImmutablePoint2i;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public interface IScreenPropertiesUpdater {
   IScreenPropertiesUpdater updateScreen(@Nullable Screen var1);

   IScreenPropertiesUpdater updateGuiProperties(@Nullable IGuiProperties var1);

   IScreenPropertiesUpdater updateExclusionAreas(Set<ImmutableRect2i> var1);

   IScreenPropertiesUpdater updateMouseExclusionArea(@Nullable ImmutablePoint2i var1);

   void update();

   void forceUpdate();
}

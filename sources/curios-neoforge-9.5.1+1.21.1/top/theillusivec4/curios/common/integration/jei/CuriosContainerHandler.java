package top.theillusivec4.curios.common.integration.jei;

import java.util.List;
import javax.annotation.Nonnull;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rect2i;
import top.theillusivec4.curios.client.gui.CuriosScreen;
import top.theillusivec4.curios.common.integration.CuriosExclusionAreas;

public class CuriosContainerHandler implements IGuiContainerHandler<CuriosScreen> {
   @Nonnull
   public List<Rect2i> getGuiExtraAreas(@Nonnull CuriosScreen screen) {
      return CuriosExclusionAreas.create(screen);
   }
}

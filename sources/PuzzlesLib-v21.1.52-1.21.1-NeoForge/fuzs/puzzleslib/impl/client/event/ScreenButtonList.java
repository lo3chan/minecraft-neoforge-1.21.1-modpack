package fuzs.puzzleslib.impl.client.event;

import java.util.AbstractList;
import java.util.List;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;

public final class ScreenButtonList extends AbstractList<AbstractWidget> {
   private final List<Renderable> renderables;

   public ScreenButtonList(List<Renderable> renderables) {
      this.renderables = renderables;
   }

   @Override
   public int size() {
      return (int)this.renderables.stream().filter(AbstractWidget.class::isInstance).count();
   }

   public AbstractWidget get(int index) {
      return this.renderables
         .stream()
         .filter(AbstractWidget.class::isInstance)
         .skip(index)
         .findFirst()
         .map(AbstractWidget.class::cast)
         .orElseThrow(() -> new IndexOutOfBoundsException(index));
   }
}

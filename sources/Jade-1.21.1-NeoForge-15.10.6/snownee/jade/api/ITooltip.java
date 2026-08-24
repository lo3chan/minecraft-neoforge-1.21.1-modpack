package snownee.jade.api;

import java.util.List;
import java.util.function.UnaryOperator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.ui.ScreenDirection;

@NonExtendable
public interface ITooltip {
   void clear();

   int size();

   default boolean isEmpty() {
      return this.size() == 0;
   }

   default void add(Component component) {
      this.add(component, null);
   }

   default void add(Component component, ResourceLocation tag) {
      this.add(this.size(), component, tag);
   }

   default void add(int index, Component component) {
      this.add(index, component, null);
   }

   default void add(int index, Component component, ResourceLocation tag) {
      this.add(index, IElementHelper.get().text(component).tag(tag));
   }

   default void addAll(List<Component> components) {
      components.forEach(this::add);
   }

   default void add(IElement element) {
      this.add(this.size(), element);
   }

   default void add(int index, List<IElement> elements) {
      boolean first = true;

      for (IElement element : elements) {
         if (first) {
            this.add(index, element);
         } else {
            this.append(index, element);
         }

         first = false;
      }
   }

   default void add(List<IElement> elements) {
      this.add(this.size(), elements);
   }

   void add(int var1, IElement var2);

   default void append(Component component) {
      this.append(component, null);
   }

   default void append(Component component, ResourceLocation tag) {
      this.append(IElementHelper.get().text(component).tag(tag));
   }

   default void append(IElement element) {
      this.append(this.size() - 1, element);
   }

   default void append(int index, List<IElement> elements) {
      for (IElement element : elements) {
         this.append(index, element);
      }
   }

   void append(int var1, IElement var2);

   boolean remove(ResourceLocation var1);

   boolean replace(ResourceLocation var1, UnaryOperator<List<List<IElement>>> var2);

   boolean replace(ResourceLocation var1, Component var2);

   List<IElement> get(ResourceLocation var1);

   List<IElement> get(int var1, IElement.Align var2);

   String getMessage();

   String getMessage(ResourceLocation var1);

   void setLineMargin(int var1, ScreenDirection var2, int var3);
}

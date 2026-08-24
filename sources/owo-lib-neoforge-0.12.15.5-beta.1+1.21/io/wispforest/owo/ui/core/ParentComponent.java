package io.wispforest.owo.ui.core;

import io.wispforest.owo.ui.parsing.IncompatibleUIModelException;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

public interface ParentComponent extends Component {
   void layout(Size var1);

   void onChildMutated(Component var1);

   void queue(Runnable var1);

   default ParentComponent alignment(HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment) {
      this.horizontalAlignment(horizontalAlignment);
      this.verticalAlignment(verticalAlignment);
      return this;
   }

   ParentComponent verticalAlignment(VerticalAlignment var1);

   VerticalAlignment verticalAlignment();

   ParentComponent horizontalAlignment(HorizontalAlignment var1);

   HorizontalAlignment horizontalAlignment();

   ParentComponent padding(Insets var1);

   AnimatableProperty<Insets> padding();

   ParentComponent allowOverflow(boolean var1);

   boolean allowOverflow();

   ParentComponent surface(Surface var1);

   Surface surface();

   List<Component> children();

   ParentComponent removeChild(Component var1);

   @Override
   default void drawTooltip(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
      if (this.hasParent()) {
         Component.super.drawTooltip(context, mouseX, mouseY, partialTicks, delta);
      } else {
         ArrayList<Component> hoveredDescendants = new ArrayList<>();
         this.forEachDescendantWhere(hoveredDescendants::add, component -> component.isInBoundingBox(mouseX, mouseY));
         hoveredDescendants.remove(this);

         for (int i = hoveredDescendants.size() - 1; i >= 0; i--) {
            ParentComponent nextParent = null;

            for (int parentIdx = i - 1; parentIdx >= 0; parentIdx--) {
               if (hoveredDescendants.get(parentIdx) instanceof ParentComponent parent) {
                  nextParent = parent;
                  break;
               }
            }

            Component current = hoveredDescendants.get(i);
            if (nextParent != null && current.parent() != nextParent) {
               break;
            }

            if (current.shouldDrawTooltip(mouseX, mouseY)) {
               context.push();

               while (i >= 0 && (i <= 0 || hoveredDescendants.get(i).parent() == hoveredDescendants.get(i - 1))) {
                  context.translate(0.0F, 0.0F, hoveredDescendants.get(i).zIndex());
                  i--;
               }

               current.drawTooltip(context, mouseX, mouseY, partialTicks, delta);
               context.flush();
               context.pop();
               break;
            }
         }
      }
   }

   @Override
   default boolean onMouseDown(double mouseX, double mouseY, int button) {
      ListIterator<Component> iter = this.children().listIterator(this.children().size());

      while (iter.hasPrevious()) {
         Component child = iter.previous();
         if (child.isInBoundingBox(this.x() + mouseX, this.y() + mouseY)
            && child.onMouseDown(this.x() + mouseX - child.x(), this.y() + mouseY - child.y(), button)) {
            return true;
         }
      }

      return false;
   }

   @Override
   default boolean onMouseScroll(double mouseX, double mouseY, double amount) {
      ListIterator<Component> iter = this.children().listIterator(this.children().size());

      while (iter.hasPrevious()) {
         Component child = iter.previous();
         if (child.isInBoundingBox(this.x() + mouseX, this.y() + mouseY)
            && child.onMouseScroll(this.x() + mouseX - child.x(), this.y() + mouseY - child.y(), amount)) {
            return true;
         }
      }

      return false;
   }

   @Override
   default void update(float delta, int mouseX, int mouseY) {
      this.padding().update(delta);

      for (int i = 0; i < this.children().size(); i++) {
         this.children().get(i).update(delta, mouseX, mouseY);
      }
   }

   @Override
   default void parseProperties(UIModel model, Element element, Map<String, Element> children) {
      Component.super.parseProperties(model, element, children);
      UIParsing.apply(children, "padding", Insets::parse, this::padding);
      UIParsing.apply(children, "surface", Surface::parse, this::surface);
      UIParsing.apply(children, "vertical-alignment", VerticalAlignment::parse, this::verticalAlignment);
      UIParsing.apply(children, "horizontal-alignment", HorizontalAlignment::parse, this::horizontalAlignment);
      UIParsing.apply(children, "allow-overflow", UIParsing::parseBool, this::allowOverflow);
   }

   default <T extends Component> T childById(@NotNull Class<T> expectedClass, @NotNull String id) {
      ListIterator<Component> iter = this.children().listIterator(this.children().size());

      while (iter.hasPrevious()) {
         Component child = iter.previous();
         if (Objects.equals(child.id(), id)) {
            if (!expectedClass.isAssignableFrom(child.getClass())) {
               throw new IncompatibleUIModelException(
                  "Expected child with id '" + id + "' to be a " + expectedClass.getSimpleName() + " but it is a " + child.getClass().getSimpleName()
               );
            }

            return (T)child;
         }

         if (child instanceof ParentComponent parent) {
            T candidate = parent.childById(expectedClass, id);
            if (candidate != null) {
               return candidate;
            }
         }
      }

      return null;
   }

   @Nullable
   default Component childAt(int x, int y) {
      ListIterator<Component> iter = this.children().listIterator(this.children().size());

      while (iter.hasPrevious()) {
         Component child = iter.previous();
         if (child.isInBoundingBox(x, y)) {
            if (child instanceof ParentComponent parent) {
               return parent.childAt(x, y);
            }

            return child;
         }
      }

      return this.isInBoundingBox(x, y) ? this : null;
   }

   default void collectDescendants(ArrayList<Component> into) {
      this.forEachDescendant(into::add);
   }

   default void forEachDescendant(Consumer<Component> action) {
      action.accept(this);

      for (Component child : this.children()) {
         if (child instanceof ParentComponent parent) {
            parent.forEachDescendant(action);
         } else {
            action.accept(child);
         }
      }
   }

   default void forEachDescendantWhere(Consumer<Component> action, Predicate<Component> condition) {
      action.accept(this);

      for (Component child : this.children()) {
         if (condition.test(child)) {
            if (child instanceof ParentComponent parent) {
               parent.forEachDescendantWhere(action, condition);
            } else {
               action.accept(child);
            }
         }
      }
   }
}

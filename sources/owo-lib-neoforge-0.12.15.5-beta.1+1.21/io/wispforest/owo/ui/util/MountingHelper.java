package io.wispforest.owo.ui.util;

import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

public class MountingHelper {
   protected final MountingHelper.ComponentSink sink;
   protected final List<Component> lateChildren;

   protected MountingHelper(MountingHelper.ComponentSink sink, List<Component> children) {
      this.sink = sink;
      this.lateChildren = children;
   }

   @Deprecated(
      forRemoval = true
   )
   public static void inflateWithExpand(List<Component> children, Size childSpace, boolean vertical) {
      inflateWithExpand(children, childSpace, vertical, 0);
   }

   public static void inflateWithExpand(List<Component> children, Size childSpace, boolean vertical, int gap) {
      ArrayList<Component> nonExpandChildren = new ArrayList<>();
      children.forEach(child -> {
         if (!child.verticalSizing().get().isExpand() && !child.horizontalSizing().get().isExpand()) {
            if (child.positioning().get().type == Positioning.Type.LAYOUT) {
               nonExpandChildren.add(child);
            }

            child.inflate(childSpace);
         }
      });
      Size remainingSpace;
      if (vertical) {
         int height = childSpace.height();

         for (Component nonExpandChild : nonExpandChildren) {
            height -= nonExpandChild.fullSize().height();
         }

         height -= gap * Math.max(children.size() - 1, 0);
         remainingSpace = Size.of(childSpace.width(), Math.max(0, height));
      } else {
         int width = childSpace.width();

         for (Component nonExpandChild : nonExpandChildren) {
            width -= nonExpandChild.fullSize().width();
         }

         width -= gap * Math.max(children.size() - 1, 0);
         remainingSpace = Size.of(Math.max(0, width), childSpace.height());
      }

      children.forEach(child -> {
         if (child.verticalSizing().get().isExpand() || child.horizontalSizing().get().isExpand()) {
            child.inflate(remainingSpace);
         }
      });
   }

   public static MountingHelper mountEarly(MountingHelper.ComponentSink sink, List<Component> children, Consumer<Component> layoutFunc) {
      ArrayList<Component> lateChildren = new ArrayList<>();

      for (Component child : children) {
         if (!child.positioning().get().isRelative()) {
            sink.accept(child, layoutFunc);
         } else {
            lateChildren.add(child);
         }
      }

      return new MountingHelper(sink, lateChildren);
   }

   public void mountLate() {
      for (Component child : this.lateChildren) {
         this.sink.accept(child, component -> {
            throw new IllegalStateException("A layout-positioned child was mounted late");
         });
      }

      this.lateChildren.clear();
   }

   public interface ComponentSink {
      void accept(@Nullable Component var1, Consumer<Component> var2);
   }
}

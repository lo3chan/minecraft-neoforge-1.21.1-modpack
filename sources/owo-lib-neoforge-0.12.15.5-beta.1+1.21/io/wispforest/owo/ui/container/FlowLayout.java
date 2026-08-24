package io.wispforest.owo.ui.container;

import io.wispforest.owo.ui.base.BaseParentComponent;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Size;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.ui.util.MountingHelper;
import io.wispforest.owo.util.Observable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.mutable.MutableInt;
import org.w3c.dom.Element;

public class FlowLayout extends BaseParentComponent {
   protected final List<Component> children = new ArrayList<>();
   protected final List<Component> childrenView = Collections.unmodifiableList(this.children);
   protected final FlowLayout.Algorithm algorithm;
   protected Size contentSize = Size.zero();
   protected Observable<Integer> gap = Observable.of(0);

   protected FlowLayout(Sizing horizontalSizing, Sizing verticalSizing, FlowLayout.Algorithm algorithm) {
      super(horizontalSizing, verticalSizing);
      this.algorithm = algorithm;
      this.gap.observe(integer -> this.updateLayout());
   }

   @Override
   protected int determineHorizontalContentSize(Sizing sizing) {
      return this.contentSize.width() + this.padding.get().horizontal();
   }

   @Override
   protected int determineVerticalContentSize(Sizing sizing) {
      return this.contentSize.height() + this.padding.get().vertical();
   }

   @Override
   public void layout(Size space) {
      this.algorithm.layout(this);
   }

   public FlowLayout child(Component child) {
      this.children.add(child);
      this.updateLayout();
      return this;
   }

   public FlowLayout children(Collection<? extends Component> children) {
      this.children.addAll(children);
      this.updateLayout();
      return this;
   }

   public FlowLayout child(int index, Component child) {
      this.children.add(index, child);
      this.updateLayout();
      return this;
   }

   public FlowLayout children(int index, Collection<? extends Component> children) {
      this.children.addAll(index, children);
      this.updateLayout();
      return this;
   }

   public FlowLayout removeChild(Component child) {
      if (this.children.remove(child)) {
         child.dismount(Component.DismountReason.REMOVED);
         this.updateLayout();
      }

      return this;
   }

   public FlowLayout clearChildren() {
      for (Component child : this.children) {
         child.dismount(Component.DismountReason.REMOVED);
      }

      this.children.clear();
      this.updateLayout();
      return this;
   }

   @Override
   public List<Component> children() {
      return this.childrenView;
   }

   public FlowLayout gap(int gap) {
      this.gap.set(gap);
      return this;
   }

   public int gap() {
      return this.gap.get();
   }

   @Override
   public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
      super.draw(context, mouseX, mouseY, partialTicks, delta);
      this.drawChildren(context, mouseX, mouseY, partialTicks, delta, this.children);
   }

   @Override
   public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
      super.parseProperties(model, element, children);
      UIParsing.apply(children, "gap", UIParsing::parseSignedInt, this::gap);

      for (Element child : UIParsing.get(children, "children", e -> UIParsing.allChildrenOfType(e, (short)1)).orElse(Collections.emptyList())) {
         this.child(model.parseComponent(Component.class, child));
      }
   }

   public static FlowLayout parse(Element element) {
      UIParsing.expectAttributes(element, "direction");
      String var1 = element.getAttribute("direction");

      return switch (var1) {
         case "horizontal" -> Containers.horizontalFlow(Sizing.content(), Sizing.content());
         case "ltr-text-flow" -> Containers.ltrTextFlow(Sizing.content(), Sizing.content());
         default -> Containers.verticalFlow(Sizing.content(), Sizing.content());
      };
   }

   @FunctionalInterface
   public interface Algorithm {
      FlowLayout.Algorithm HORIZONTAL = container -> {
         MutableInt layoutWidth = new MutableInt(0);
         MutableInt layoutHeight = new MutableInt(0);
         ArrayList<Component> layout = new ArrayList<>();
         Insets padding = container.padding.get();
         Size childSpace = container.calculateChildSpace(container.space);
         MountingHelper.inflateWithExpand(container.children, childSpace, false, container.gap());
         MountingHelper mountState = MountingHelper.mountEarly(
            (x$0, x$1) -> container.mountChild(x$0, x$1),
            container.children,
            child -> {
               layout.add(child);
               child.mount(
                  container,
                  container.x + padding.left() + child.margins().get().left() + layoutWidth.intValue(),
                  container.y + padding.top() + child.margins().get().top()
               );
               Size childSize = child.fullSize();
               layoutWidth.add(childSize.width() + container.gap());
               if (childSize.height() > layoutHeight.intValue()) {
                  layoutHeight.setValue(childSize.height());
               }
            }
         );
         layoutWidth.subtract(container.gap());
         container.contentSize = Size.of(layoutWidth.intValue(), layoutHeight.intValue());
         container.applySizing();
         if (container.verticalAlignment() != VerticalAlignment.TOP) {
            for (Component component : layout) {
               component.updateY(component.baseY() + container.verticalAlignment().align(component.fullSize().height(), container.height - padding.vertical()));
            }
         }

         if (container.horizontalAlignment() != HorizontalAlignment.LEFT) {
            for (Component component : layout) {
               if (container.horizontalAlignment() == HorizontalAlignment.CENTER) {
                  component.updateX(component.baseX() + (container.width - padding.horizontal() - layoutWidth.intValue()) / 2);
               } else {
                  component.updateX(component.baseX() + (container.width - padding.horizontal() - layoutWidth.intValue()));
               }
            }
         }

         mountState.mountLate();
      };
      FlowLayout.Algorithm VERTICAL = container -> {
         MutableInt layoutHeight = new MutableInt(0);
         MutableInt layoutWidth = new MutableInt(0);
         ArrayList<Component> layout = new ArrayList<>();
         Insets padding = container.padding.get();
         Size childSpace = container.calculateChildSpace(container.space);
         MountingHelper.inflateWithExpand(container.children, childSpace, true, container.gap());
         MountingHelper mountState = MountingHelper.mountEarly(
            (x$0, x$1) -> container.mountChild(x$0, x$1),
            container.children,
            child -> {
               layout.add(child);
               child.mount(
                  container,
                  container.x + padding.left() + child.margins().get().left(),
                  container.y + padding.top() + child.margins().get().top() + layoutHeight.intValue()
               );
               Size childSize = child.fullSize();
               layoutHeight.add(childSize.height() + container.gap());
               if (childSize.width() > layoutWidth.intValue()) {
                  layoutWidth.setValue(childSize.width());
               }
            }
         );
         layoutHeight.subtract(container.gap());
         container.contentSize = Size.of(layoutWidth.intValue(), layoutHeight.intValue());
         container.applySizing();
         if (container.horizontalAlignment() != HorizontalAlignment.LEFT) {
            for (Component component : layout) {
               component.updateX(
                  component.baseX() + container.horizontalAlignment().align(component.fullSize().width(), container.width - padding.horizontal())
               );
            }
         }

         if (container.verticalAlignment() != VerticalAlignment.TOP) {
            for (Component component : layout) {
               if (container.verticalAlignment() == VerticalAlignment.CENTER) {
                  component.updateY(component.baseY() + (container.height - padding.vertical() - layoutHeight.intValue()) / 2);
               } else {
                  component.updateY(component.baseY() + (container.height - padding.vertical() - layoutHeight.intValue()));
               }
            }
         }

         mountState.mountLate();
      };
      FlowLayout.Algorithm LTR_TEXT = container -> {
         if (container.horizontalSizing.get().isContent()) {
            throw new IllegalStateException("An LTR-text-flow layout must use content-independent horizontal sizing");
         } else {
            MutableInt layoutWidth = new MutableInt(0);
            MutableInt layoutHeight = new MutableInt(0);
            MutableInt rowWidth = new MutableInt(0);
            MutableInt rowOffset = new MutableInt(0);
            ArrayList<Component> layout = new ArrayList<>();
            Insets padding = container.padding.get();
            Size childSpace = container.calculateChildSpace(container.space);
            container.children.forEach(child -> child.inflate(childSpace));
            MountingHelper mountState = MountingHelper.mountEarly((x$0, x$1) -> container.mountChild(x$0, x$1), container.children, child -> {
               layout.add(child);
               int x = container.x + padding.left() + child.margins().get().left() + rowWidth.intValue();
               int y = container.y + padding.top() + child.margins().get().top() + rowOffset.intValue();
               Size childSize = child.fullSize();
               if (rowWidth.intValue() + childSize.width() > childSpace.width()) {
                  x -= rowWidth.intValue();
                  y = y - rowOffset.intValue() + layoutHeight.intValue();
                  rowOffset.setValue(layoutHeight);
                  rowWidth.setValue(0);
               }

               child.mount(container, x, y);
               rowWidth.add(childSize.width() + container.gap());
               if (rowOffset.intValue() + childSize.height() > layoutHeight.intValue()) {
                  layoutHeight.setValue(rowOffset.intValue() + childSize.height());
               }

               if (rowWidth.intValue() > layoutWidth.intValue()) {
                  layoutWidth.setValue(rowWidth.intValue());
               }
            });
            layoutWidth.subtract(container.gap());
            container.contentSize = Size.of(layoutWidth.intValue(), layoutHeight.intValue());
            container.applySizing();
            if (container.verticalAlignment() != VerticalAlignment.TOP) {
               for (Component component : layout) {
                  component.updateY(component.baseY() + container.verticalAlignment().align(layoutHeight.intValue(), container.height - padding.vertical()));
               }
            }

            if (container.horizontalAlignment() != HorizontalAlignment.LEFT) {
               for (Component component : layout) {
                  if (container.horizontalAlignment() == HorizontalAlignment.CENTER) {
                     component.updateX(component.baseX() + (container.width - padding.horizontal() - layoutWidth.intValue()) / 2);
                  } else {
                     component.updateX(component.baseX() + (container.width - padding.horizontal() - layoutWidth.intValue()));
                  }
               }
            }

            mountState.mountLate();
         }
      };

      void layout(FlowLayout var1);
   }
}

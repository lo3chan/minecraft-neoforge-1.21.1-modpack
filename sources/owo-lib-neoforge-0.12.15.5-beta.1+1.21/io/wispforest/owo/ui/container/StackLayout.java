package io.wispforest.owo.ui.container;

import io.wispforest.owo.ui.base.BaseParentComponent;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Size;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.ui.util.MountingHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.mutable.MutableInt;
import org.w3c.dom.Element;

public class StackLayout extends BaseParentComponent {
   protected final List<Component> children = new ArrayList<>();
   protected final List<Component> childrenView = Collections.unmodifiableList(this.children);
   protected Size contentSize = Size.zero();

   protected StackLayout(Sizing horizontalSizing, Sizing verticalSizing) {
      super(horizontalSizing, verticalSizing);
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
      Size childSpace = this.calculateChildSpace(space);
      this.children.forEach(childx -> childx.inflate(childSpace));
      MutableInt layoutWidth = new MutableInt();
      MutableInt layoutHeight = new MutableInt();
      ArrayList<Component> layout = new ArrayList<>();
      MountingHelper helper = MountingHelper.mountEarly(
         (x$0, x$1) -> this.mountChild(x$0, x$1),
         this.childrenView,
         childx -> {
            layout.add(childx);
            childx.mount(
               this, this.x + this.padding.get().left() + childx.margins().get().left(), this.y + this.padding.get().top() + childx.margins().get().top()
            );
            Size fullChildSize = childx.fullSize();
            layoutWidth.setValue(Math.max(layoutWidth.getValue(), fullChildSize.width()));
            layoutHeight.setValue(Math.max(layoutHeight.getValue(), fullChildSize.height()));
         }
      );
      this.contentSize = Size.of(layoutWidth.intValue(), layoutHeight.intValue());
      this.applySizing();
      HorizontalAlignment horizontalAlignment = this.horizontalAlignment();
      VerticalAlignment verticalAlignment = this.verticalAlignment();

      for (Component child : layout) {
         child.updateX(child.baseX() + horizontalAlignment.align(child.fullSize().width(), this.width - this.padding.get().horizontal()));
         child.updateY(child.baseY() + verticalAlignment.align(child.fullSize().height(), this.height - this.padding.get().vertical()));
      }

      helper.mountLate();
   }

   @Override
   public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
      super.draw(context, mouseX, mouseY, partialTicks, delta);
      this.drawChildren(context, mouseX, mouseY, partialTicks, delta, this.children);
   }

   public StackLayout child(Component child) {
      this.children.add(child);
      this.updateLayout();
      return this;
   }

   public StackLayout children(Collection<? extends Component> children) {
      this.children.addAll(children);
      this.updateLayout();
      return this;
   }

   public StackLayout child(int index, Component child) {
      this.children.add(index, child);
      this.updateLayout();
      return this;
   }

   public StackLayout children(int index, Collection<? extends Component> children) {
      this.children.addAll(index, children);
      this.updateLayout();
      return this;
   }

   public StackLayout removeChild(Component child) {
      if (this.children.remove(child)) {
         child.dismount(Component.DismountReason.REMOVED);
         this.updateLayout();
      }

      return this;
   }

   public StackLayout clearChildren() {
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

   @Override
   public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
      super.parseProperties(model, element, children);

      for (Element child : UIParsing.get(children, "children", e -> UIParsing.allChildrenOfType(e, (short)1)).orElse(Collections.emptyList())) {
         this.child(model.parseComponent(Component.class, child));
      }
   }
}

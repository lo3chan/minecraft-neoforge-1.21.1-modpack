package io.wispforest.owo.ui.container;

import io.wispforest.owo.ui.base.BaseParentComponent;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Size;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIModelParsingException;
import io.wispforest.owo.ui.parsing.UIParsing;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

public class GridLayout extends BaseParentComponent {
   protected final int rows;
   protected final int columns;
   protected final Component[] children;
   protected final List<Component> nonNullChildren = new ArrayList<>();
   protected final List<Component> nonNullChildrenView = Collections.unmodifiableList(this.nonNullChildren);
   protected Size contentSize = Size.zero();

   protected GridLayout(Sizing horizontalSizing, Sizing verticalSizing, int rows, int columns) {
      super(horizontalSizing, verticalSizing);
      this.rows = rows;
      this.columns = columns;
      this.children = new Component[rows * columns];
   }

   @Override
   protected int determineHorizontalContentSize(Sizing sizing) {
      return this.contentSize.width() + this.padding.get().right();
   }

   @Override
   protected int determineVerticalContentSize(Sizing sizing) {
      return this.contentSize.height() + this.padding.get().bottom();
   }

   @Override
   public void layout(Size space) {
      int[] columnSizes = new int[this.columns];
      int[] rowSizes = new int[this.rows];
      Size childSpace = this.calculateChildSpace(space);

      for (Component child : this.children) {
         if (child != null) {
            child.inflate(childSpace);
         }
      }

      this.determineSizes(columnSizes, false);
      this.determineSizes(rowSizes, true);
      Size mountingOffset = this.childMountingOffset();
      MutableInt layoutX = new MutableInt(this.x + mountingOffset.width());
      MutableInt layoutY = new MutableInt(this.y + mountingOffset.height());

      for (int row = 0; row < this.rows; row++) {
         layoutX.setValue(this.x + mountingOffset.width());

         for (int column = 0; column < this.columns; column++) {
            int columnSize = columnSizes[column];
            int rowSize = rowSizes[row];
            this.mountChild(
               this.getChild(row, column),
               childx -> childx.mount(
                  this,
                  layoutX.intValue() + childx.margins().get().left() + this.horizontalAlignment().align(childx.fullSize().width(), columnSize),
                  layoutY.intValue() + childx.margins().get().top() + this.verticalAlignment().align(childx.fullSize().height(), rowSize)
               )
            );
            layoutX.add(columnSizes[column]);
         }

         layoutY.add(rowSizes[row]);
      }

      this.contentSize = Size.of(layoutX.intValue() - this.x, layoutY.intValue() - this.y);
   }

   @Override
   public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
      super.draw(context, mouseX, mouseY, partialTicks, delta);
      this.drawChildren(context, mouseX, mouseY, partialTicks, delta, this.nonNullChildren);
   }

   @Nullable
   protected Component getChild(int row, int column) {
      return this.children[row * this.columns + column];
   }

   protected void determineSizes(int[] sizes, boolean rows) {
      if (!(rows ? this.verticalSizing : this.horizontalSizing).get().isContent()) {
         Arrays.fill(
            sizes, (rows ? this.height - this.padding().get().vertical() : this.width - this.padding().get().horizontal()) / (rows ? this.rows : this.columns)
         );
      } else {
         for (int row = 0; row < this.rows; row++) {
            for (int column = 0; column < this.columns; column++) {
               Component child = this.getChild(row, column);
               if (child != null) {
                  if (rows) {
                     sizes[row] = Math.max(sizes[row], child.fullSize().height());
                  } else {
                     sizes[column] = Math.max(sizes[column], child.fullSize().width());
                  }
               }
            }
         }
      }
   }

   public GridLayout child(Component child, int row, int column) {
      Component previousChild = this.getChild(row, column);
      this.children[row * this.columns + column] = child;
      if (previousChild != child) {
         if (previousChild != null) {
            this.nonNullChildren.remove(previousChild);
            previousChild.dismount(Component.DismountReason.REMOVED);
         }

         this.nonNullChildren.add(child);
         this.updateLayout();
      }

      return this;
   }

   public GridLayout removeChild(int row, int column) {
      Component currentChild = this.getChild(row, column);
      if (currentChild != null) {
         currentChild.dismount(Component.DismountReason.REMOVED);
         this.nonNullChildren.remove(currentChild);
         this.updateLayout();
      }

      return this;
   }

   public GridLayout removeChild(Component child) {
      for (int i = 0; i < this.children.length; i++) {
         if (Objects.equals(this.children[i], child)) {
            this.removeChild(i / this.columns, i % this.columns);
            break;
         }
      }

      return this;
   }

   @Override
   public List<Component> children() {
      return this.nonNullChildrenView;
   }

   @Override
   public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
      super.parseProperties(model, element, children);

      for (Element child : UIParsing.get(children, "children", e -> UIParsing.allChildrenOfType(e, (short)1)).orElse(Collections.emptyList())) {
         UIParsing.expectAttributes(child, "row", "column");
         int row = UIParsing.parseUnsignedInt(child.getAttributeNode("row"));
         int column = UIParsing.parseUnsignedInt(child.getAttributeNode("column"));
         Component existingChild = this.getChild(row, column);
         if (existingChild != null) {
            throw new UIModelParsingException(
               "Tried to populate cell "
                  + row
                  + ","
                  + column
                  + " in grid layout twice. Present component: "
                  + existingChild.getClass().getSimpleName()
                  + "\nNew element: "
                  + child.getNodeName()
            );
         }

         this.child(model.parseComponent(Component.class, child), row, column);
      }
   }

   public static GridLayout parse(Element element) {
      UIParsing.expectAttributes(element, "rows", "columns");
      int rows = UIParsing.parseUnsignedInt(element.getAttributeNode("rows"));
      int columns = UIParsing.parseUnsignedInt(element.getAttributeNode("columns"));
      return new GridLayout(Sizing.content(), Sizing.content(), rows, columns);
   }
}

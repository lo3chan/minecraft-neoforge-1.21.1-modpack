package cc.cosmetica.cosmetica.gui.widget;

import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.gui.Align;
import cc.cosmetica.kupe.api.gui.Border;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Margins;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Function;
import org.jetbrains.annotations.Nullable;

public final class EntryList {
   private static List<Component> retag(Component self, List<Component> components, @Nullable Function<Component, ? extends Component> acquireSelected) {
      if (acquireSelected != null) {
         Component theSelected = acquireSelected.apply(self);

         for (Component component : components) {
            if (component == theSelected) {
               component.tag(new String[]{"entrylist-selected"});
            } else {
               component.tag(new String[0]);
            }
         }
      }

      return components;
   }

   private static Stylesheet makeStylesheet(@Nullable Style style) {
      return new Stylesheet()
         .self(
            Style.create()
               .set(CommonProperties.BACKGROUND_COLOUR, OptionalInt.of(0))
               .set(CommonProperties.PADDING, CommonProperties.fixed(new Margins(1)))
               .set(CommonProperties.MIN_HEIGHT, CommonProperties.fixedSize(0))
               .set(EntryList.Div.ALIGN_ITEMS, Align.STRETCH_START)
         )
         .tag("entrylist-selected", (Style)(style != null ? style : Style.create().set(CommonProperties.BORDER, Border.create(1, 16777215))));
   }

   public static class Div extends cc.cosmetica.kupe.api.gui.Div {
      @Nullable
      final Function<Component, ? extends Component> selected;
      private Style selectedStyle;

      public Div(Component... entries) {
         super(entries);
         this.selected = null;
      }

      public Div(Component[] entries, Function<Component, ? extends Component> selected) {
         super(entries);
         this.selected = selected;
      }

      public EntryList.Div selected(Style style) {
         this.selectedStyle = style;
         return this;
      }

      public List<Component> build() {
         return EntryList.retag(this, super.build(), this.selected);
      }

      public Stylesheet getStylesheet() {
         return EntryList.makeStylesheet(this.selectedStyle);
      }
   }

   public static class DynamicDiv extends EntryList.Div {
      private final State<List<Component>> entries;

      public DynamicDiv(State<List<Component>> entries, Function<Component, ? extends Component> selected) {
         super(new Component[0], selected);
         this.entries = entries;
      }

      @Override
      public List<Component> build() {
         List<Component> contents = (List<Component>)this.entries.acquire(this);
         return EntryList.retag(this, contents, this.selected);
      }
   }

   public static class Grid extends cc.cosmetica.kupe.api.gui.Grid {
      @Nullable
      private final Function<Component, ? extends Component> selected;

      public Grid(Component... entries) {
         super(entries);
         this.selected = null;
      }

      public Grid(Component[] entries, Function<Component, ? extends Component> selected) {
         super(entries);
         this.selected = selected;
      }

      public List<Component> build() {
         return EntryList.retag(this, super.build(), this.selected);
      }

      public Stylesheet getStylesheet() {
         return EntryList.makeStylesheet(null);
      }
   }
}

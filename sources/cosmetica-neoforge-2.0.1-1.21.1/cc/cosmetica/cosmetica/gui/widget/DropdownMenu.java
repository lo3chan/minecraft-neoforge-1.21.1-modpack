package cc.cosmetica.cosmetica.gui.widget;

import cc.cosmetica.cosmetica.gui.GuiUtils;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Align;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.Element;
import cc.cosmetica.kupe.api.gui.Label;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import com.google.common.collect.Streams;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.AbstractMap.SimpleEntry;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DropdownMenu<T> extends Div {
   private final T[] options;
   private final State<T> state;
   private final Function<T, Text> textMap;

   public DropdownMenu(State<T> state, Function<T, Text> textMap, T... options) {
      super(new Component[0]);
      this.options = options;
      this.textMap = textMap;
      this.state = state;
   }

   public List<Component> build() {
      T selected = (T)this.state.acquire(this);
      return Streams.zip(Arrays.<T>stream(this.options).map(this.textMap), Arrays.stream(this.options), SimpleEntry::new)
         .map(option -> this.createOption((T)option.getValue(), (Text)option.getKey(), option.getValue() == selected))
         .collect(Collectors.toList());
   }

   protected Component createOption(T option, Text text, boolean selected) {
      Label result = new Label(text) {
         public void mouseClicked(Element target, double x, double y, int button) {
            GuiUtils.playClick();
            if (DropdownMenu.this.state.peek() != option) {
               DropdownMenu.this.state.set(option);
            }
         }
      };
      if (selected) {
         result.tag(new String[]{"dropdown-item", "dropdown-selected"});
      } else {
         result.tag(new String[]{"dropdown-item"});
      }

      return result;
   }

   public Stylesheet getStylesheet() {
      return getDropdownStylesheet();
   }

   public static Stylesheet getDropdownStylesheet() {
      return new Stylesheet()
         .self(
            Style.create()
               .set(Label.ALIGN_TEXT, Align.START)
               .set(ALIGN_ITEMS, Align.STRETCH_START)
               .set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(24, 0, 0, 0)))
               .set(CommonProperties.PADDING, CommonProperties.fixed(new Margins(1, 2)))
               .set(CommonProperties.BACKGROUND_COLOUR, OptionalInt.of(8750469))
               .set(CommonProperties.BORDER, GuiUtils.POPOUT_BORDER)
               .set(CommonProperties.ALIGN_SELF, Optional.of(Align.END))
               .set(CommonProperties.WIDTH, CommonProperties.percent(50.0F, 0.0F))
               .set(CommonProperties.MAXIMUM_SIZE, CommonProperties.screen(100.0F, 75.0F, Dimensions::new))
         )
         .tag("dropdown-item", Style.create().set(CommonProperties.PADDING, CommonProperties.fixed(new Margins(1, 0))))
         .tag("dropdown-selected", Style.create().set(CommonProperties.BACKGROUND_COLOUR, OptionalInt.of(10592673)));
   }
}

package cc.cosmetica.cosmetica.gui.widget;

import cc.cosmetica.cosmetica.gui.GuiUtils;
import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Align;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.Element;
import cc.cosmetica.kupe.api.gui.Image;
import cc.cosmetica.kupe.api.gui.Justify;
import cc.cosmetica.kupe.api.gui.Label;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Axis2D;
import com.google.common.collect.Streams;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DropdownToggles<T> extends Div {
   private final T[] options;
   private final State<Set<T>> state;
   private final Function<T, Text> textMap;

   public DropdownToggles(State<Set<T>> state, Function<T, Text> textMap, T... options) {
      super(new Component[0]);
      this.options = options;
      this.state = state;
      this.textMap = textMap;
   }

   public List<Component> build() {
      Set<T> selected = (Set<T>)this.state.acquire(this);
      return Streams.zip(Arrays.<T>stream(this.options).map(this.textMap), Arrays.stream(this.options), SimpleEntry::new)
         .map(option -> this.createOption((T)option.getValue(), (Text)option.getKey(), selected.contains(option.getValue())))
         .collect(Collectors.toList());
   }

   protected Component createOption(T option, Text text, boolean selected) {
      return (new Div(new Component[0]) {
            public List<Component> build() {
               return Arrays.asList(
                  new Label(text),
                  new Image(new ResourceKey("cosmetica", selected ? "textures/checkbox_check.png" : "textures/checkbox_empty.png"))
                     .withStyle(
                        Style.create().set(CommonProperties.HEIGHT, CommonProperties.fixedSize(12)).set(CommonProperties.WIDTH, CommonProperties.fixedSize(12))
                     )
               );
            }

            public void mouseClicked(Element target, double x, double y, int button) {
               GuiUtils.playClick();
               if (((Set)DropdownToggles.this.state.peek()).contains(option)) {
                  ((Set)DropdownToggles.this.state.peek()).remove(option);
               } else {
                  ((Set)DropdownToggles.this.state.peek()).add(option);
               }

               DropdownToggles.this.state.set((Set)DropdownToggles.this.state.peek());
            }
         })
         .tag(new String[]{"dropdown-item"});
   }

   public Stylesheet getStylesheet() {
      return DropdownMenu.getDropdownStylesheet()
         .self(Style.create().set(CommonProperties.WIDTH, CommonProperties.percent(50.0F, 0.0F)))
         .tag(
            "dropdown-item",
            Style.create().set(Div.JUSTIFY_CONTENT, Justify.SPACE_BETWEEN).set(Div.ALIGN_ITEMS, Align.CENTRE).set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X)
         );
   }
}

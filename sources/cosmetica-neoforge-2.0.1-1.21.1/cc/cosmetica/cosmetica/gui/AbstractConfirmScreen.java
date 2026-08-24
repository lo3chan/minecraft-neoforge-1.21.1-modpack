package cc.cosmetica.cosmetica.gui;

import cc.cosmetica.kupe.api.Screen;
import cc.cosmetica.kupe.api.Screens;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Align;
import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.Label;
import cc.cosmetica.kupe.api.gui.Tooltip;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Axis2D;
import cc.cosmetica.kupe.api.maths.Margins;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractConfirmScreen extends Screen {
   protected State<Boolean> setting = new State(false);

   protected AbstractConfirmScreen(Text title) {
      super(title);
   }

   protected final Component[] buildScreen() {
      boolean setting = (Boolean)this.setting.acquire(this);
      return new Component[]{
         this.createConfirmLabel(),
         new Div(
               new Component[]{
                  new Button(Text.GUI_PROCEED, this::onConfirm)
                     .setDisabled(setting)
                     .withStyle(Style.create().set(CommonProperties.TOOLTIP, !setting ? Optional.empty() : Optional.of(this.getUpdatingTooltip()))),
                  new Button(Text.GUI_CANCEL, Screens::closeCurrentScreen)
               }
            )
            .tag(new String[]{"horizontal"})
      };
   }

   protected abstract Label createConfirmLabel();

   protected abstract Tooltip getUpdatingTooltip();

   protected abstract void onConfirm();

   @NotNull
   public Stylesheet getStylesheet() {
      return super.getStylesheet()
         .self(Style.create().set(Label.ALIGN_TEXT, Align.CENTRE))
         .tag(
            "horizontal",
            Style.create().set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X).set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(12, 0, 0, 0)))
         )
         .component(Button.class, Style.create().set(CommonProperties.WIDTH, CommonProperties.fixedSize(150)));
   }
}

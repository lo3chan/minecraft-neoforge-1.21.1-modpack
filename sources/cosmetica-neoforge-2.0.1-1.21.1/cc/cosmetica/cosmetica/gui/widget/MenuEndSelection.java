package cc.cosmetica.cosmetica.gui.widget;

import cc.cosmetica.core.api.CosmeticaAPI;
import cc.cosmetica.cosmetica.Cosmetica;
import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Screens;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.Justify;
import cc.cosmetica.kupe.api.gui.PointerEvents;
import cc.cosmetica.kupe.api.gui.Tooltip;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Axis2D;
import cc.cosmetica.kupe.api.maths.Margins;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;

public class MenuEndSelection extends Div {
   private final State<Boolean> clicked = new State(false);
   protected boolean disabled = false;

   public MenuEndSelection() {
      super(new Component[0]);
   }

   public List<Component> build() {
      boolean clicked = (Boolean)this.clicked.acquire(this);
      Cosmetica.OWN_COSMETICS.acquire(this);
      boolean isLoggedIn = CosmeticaAPI.isAuthenticated();
      return ImmutableList.of(
         new Button(Text.GUI_DONE, Screens::closeCurrentScreen).setDisabled(this.disabled),
         new IconButton(new ResourceKey("cosmetica", "textures/internet.png"), () -> {
               Cosmetica.openWebPanel("home");
               this.clicked.set(true);
            }, (region, x, y) -> {
               if ((Boolean)this.clicked.peek() && !region.contains((int)x, (int)y)) {
                  this.clicked.set(false);
               }
            })
            .setDisabled(!isLoggedIn)
            .withStyle(
               Style.create()
                  .set(
                     CommonProperties.TOOLTIP,
                     Optional.of(
                        Cosmetica.authTooltip(isLoggedIn)
                           .orElse(
                              new Tooltip(
                                 clicked
                                    ? Text.translatable("tooltip.cosmetica.copiedURL", new String[0])
                                    : Text.translatable("tooltip.cosmetica.openWebPanel", new String[0])
                              )
                           )
                     )
                  )
                  .set(CommonProperties.POINTER_EVENTS, PointerEvents.ALL)
            )
      );
   }

   public Stylesheet getStylesheet() {
      return new Stylesheet()
         .self(
            Style.create()
               .set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(0, 0, 12, 0)))
               .set(CommonProperties.WIDTH, CommonProperties.percent(100.0F, 0.0F))
               .set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X)
               .set(Div.JUSTIFY_CONTENT, Justify.CENTRE)
         );
   }
}

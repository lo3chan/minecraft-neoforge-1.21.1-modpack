package cc.cosmetica.cosmetica.gui.widget;

import cc.cosmetica.core.api.NametagConfig;
import cc.cosmetica.cosmetica.Cosmetica;
import cc.cosmetica.cosmetica.gui.OutfitSelectScreen;
import cc.cosmetica.kupe.api.Screens;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Align;
import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.GUIPlayer;
import cc.cosmetica.kupe.api.gui.Justify;
import cc.cosmetica.kupe.api.gui.Label;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.gui.style.CommonProperties.DimensionsOperator;
import cc.cosmetica.kupe.api.maths.Dimensions;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.UnaryOperator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.options.SkinCustomizationScreen;

public class OutfitPlayer extends Component {
   private final UUID player;
   private final boolean authenticated;
   private final String outfitName;
   private final NametagConfig lore;
   private final NametagConfig nametag;
   public static final State<Boolean> showingElytra = new State(false);
   private boolean disable = false;
   private UnaryOperator<GUIPlayer> overrides = gp -> gp;
   private RotatableGUIPlayer guiPlayer;
   private boolean keepGuiPlayer;
   private int loreHandle;

   public OutfitPlayer(UUID player, boolean authenticated, String outfitName, NametagConfig lore, NametagConfig nametag) {
      this.player = player;
      this.outfitName = outfitName;
      this.lore = lore;
      this.nametag = nametag;
      this.authenticated = authenticated;
   }

   public OutfitPlayer setDisabled(boolean disabled) {
      this.disable = disabled;
      return this;
   }

   public OutfitPlayer configureOverrides(UnaryOperator<GUIPlayer> overrides) {
      this.overrides = overrides;
      return this;
   }

   public OutfitPlayer keepGuiPlayer() {
      this.keepGuiPlayer = true;
      return this;
   }

   public List<Component> build() {
      RotatableGUIPlayer guiPlayer;
      if (this.keepGuiPlayer && this.guiPlayer != null) {
         guiPlayer = this.guiPlayer;
         guiPlayer.updateNametag(this.loreHandle, Text.literal(this.lore.getPrefix()), 0.75F);
      } else {
         guiPlayer = new RotatableGUIPlayer(this.player, showingElytra);
         guiPlayer.showNametag(true);
         this.loreHandle = guiPlayer.createNametag(Text.literal(this.lore.getPrefix()), 0.75F);
      }

      guiPlayer.icon(this.nametag.getIcon().getImage().isLoaded() ? this.nametag.getIcon().getImage() : null, this.nametag.isTransparentIcon())
         .loreIcon(this.lore.getIcon().getImage().isLoaded() ? this.lore.getIcon().getImage() : null);
      return Arrays.asList(
         new Div(
               new Component[]{
                  this.overrides
                     .apply(guiPlayer)
                     .withStyle(
                        Style.create()
                           .set(CommonProperties.MIN_WIDTH, CommonProperties.fixedSize(50))
                           .set(CommonProperties.MIN_HEIGHT, CommonProperties.fixedSize(90))
                           .set(CommonProperties.MAXIMUM_SIZE, CommonProperties.fixed(new Dimensions(90, 180)))
                           .set(CommonProperties.HEIGHT, (DimensionsOperator)(vw, vh, pw, ph) -> OptionalInt.of(20 + (int)(2 * vw * 0.0625)))
                           .set(CommonProperties.WIDTH, (DimensionsOperator)(vw, vh, pw, ph) -> OptionalInt.of(10 + (int)(vw * 0.0625)))
                     ),
                  new Label(Text.literal(this.outfitName)),
                  new SlideToggle(
                     showingElytra,
                     Text.translatable("button.cosmetica.toggleCloak", new String[0]),
                     Text.translatable("button.cosmetica.toggleElytra", new String[0])
                  ),
                  new Button(Text.translatable("button.cosmetica.changeOutfit", new String[0]), () -> Screens.setScreen(OutfitSelectScreen.ID))
                     .setDisabled(!this.authenticated || this.disable)
                     .withStyle(Cosmetica.authTooltipStyle(this.disable || this.authenticated)),
                  new Button(
                        Text.translatable("options.skinCustomisation.title", new String[0]),
                        () -> Minecraft.getInstance().setScreen(new SkinCustomizationScreen(Minecraft.getInstance().screen, Minecraft.getInstance().options))
                     )
                     .setDisabled(this.disable)
               }
            )
            .withStyle(Style.create().set(Div.JUSTIFY_CONTENT, Justify.CENTRE).set(Div.ALIGN_ITEMS, Align.CENTRE))
      );
   }

   public Stylesheet getStylesheet() {
      return new Stylesheet().component(Button.class, Style.create().set(CommonProperties.WIDTH, CommonProperties.fixed(OptionalInt.of(150))));
   }
}

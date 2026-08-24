package cc.cosmetica.cosmetica.gui;

import cc.cosmetica.core.api.CosmeticaAPI;
import cc.cosmetica.core.impl.Logging;
import cc.cosmetica.cosmetica.Cosmetica;
import cc.cosmetica.cosmetica.gui.widget.ClickableImage;
import cc.cosmetica.cosmetica.gui.widget.EntryList;
import cc.cosmetica.cosmetica.gui.widget.OutfitCount;
import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Screen;
import cc.cosmetica.kupe.api.Screens;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.Element;
import cc.cosmetica.kupe.api.gui.Image;
import cc.cosmetica.kupe.api.gui.Label;
import cc.cosmetica.kupe.api.gui.LayeredSpace;
import cc.cosmetica.kupe.api.gui.Tooltip;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Region;
import com.google.common.collect.ImmutableList;
import gg.cloaks.javaclient.api.PremiumApi;
import gg.cloaks.javaclient.model.PlanRestrictions;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

public class OutfitSelectScreen extends Component implements AnimatedTextureScreen {
   private final Text title;
   private static final State<Integer> outfitLimit = new State(-1);
   public static final ResourceKey ID = new ResourceKey("cosmetica", "outfit_select");
   public static final ResourceKey NEW_OUTFIT_ICON = new ResourceKey("cosmetica", "textures/new_outfit.png");

   public OutfitSelectScreen() {
      this.title = ID.translationKey("screens");
      fetchOutfitLimit();
   }

   public static void fetchOutfitLimit() {
      CosmeticaAPI.premiumApi()
         .requestAsync(PremiumApi::getRestrictions)
         .<BigDecimal>thenApply(PlanRestrictions::getMaxOutfits)
         .thenApply(BigDecimal::intValue)
         .thenAcceptAsync(outfitLimit::set, Minecraft.getInstance())
         .exceptionally(e -> {
            Logging.getInstance().error("Error fetching max outfits", e);
            return null;
         });
   }

   public List<Component> build() {
      final List<OutfitWheelScreen.OutfitOption> options = (List<OutfitWheelScreen.OutfitOption>)Cosmetica.OWN_OUTFITS.acquire(this);
      OutfitSelectScreen.SelectableOutfit[] components = options.stream()
         .map(OutfitSelectScreen.SelectableOutfit::new)
         .toArray(OutfitSelectScreen.SelectableOutfit[]::new);
      return Arrays.asList(
         new Div(new Component[]{new Label(this.title), new OutfitCount(outfitLimit)}).tag(new String[]{"title"}),
         new Div(
               new Component[]{
                  new EntryList.Grid(components, grid -> (Component)Cosmetica.SELECTED_OUTFIT_ID.extract(grid, id -> find(components, id.orElse(""))))
                     .withStyle(
                        Style.create()
                           .set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(3, 0, 0, 0)))
                           .set(CommonProperties.WIDTH, CommonProperties.screen(75.0F, 0.0F))
                           .set(CommonProperties.MIN_WIDTH, CommonProperties.screen(75.0F, 0.0F))
                           .set(CommonProperties.MIN_HEIGHT, CommonProperties.screen(0.0F, 60.0F))
                           .set(EntryList.Grid.COLUMN_GAP, 2)
                           .set(EntryList.Grid.ROW_GAP, 2)
                           .set(CommonProperties.BACKGROUND_COLOUR, OptionalInt.empty())
                     ),
                  new Button(Text.translatable("label.cosmetica.newOutfit", new String[0]), () -> Screens.setScreen(CreateNewOutfitScreen.ID)) {
                     public List<Component> build() {
                        int limit = (Integer)OutfitSelectScreen.outfitLimit.acquire(this);
                        int count = options.size();
                        this.setDisabled(count >= limit);
                        return ImmutableList.of();
                     }
                  },
                  new Button(Text.translatable("label.cosmetica.clearOutfit", new String[0]), OutfitWheelScreen::clearOutfit) {
                     public List<Component> build() {
                        boolean equippedOutfit = (Boolean)Cosmetica.SELECTED_OUTFIT_ID.extract(this, id -> id.isPresent());
                        this.setDisabled(!equippedOutfit);
                        return ImmutableList.of();
                     }

                     public void mouseClicked(Element target, double x, double y, int button) {
                        super.mouseClicked(target, x, y, button);
                        if (target.getComponent() == this) {
                           this.setDisabled(true);
                        }
                     }
                  },
                  new Button(Text.GUI_DONE, Screens::closeCurrentScreen)
               }
            )
            .tag(new String[]{"body"})
      );
   }

   @NotNull
   public Stylesheet getStylesheet() {
      return new Stylesheet()
         .tag("body", Screen.BODY_DEFAULT_STYLE)
         .tag("title", Screen.TITLE_DEFAULT_STYLE)
         .tag("body", Style.create().set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(23, 0, 0, 0))))
         .component(
            OutfitSelectScreen.SelectableOutfit.class,
            Style.create()
               .set(CommonProperties.WIDTH, CommonProperties.fixed(OptionalInt.of(46)))
               .set(CommonProperties.HEIGHT, CommonProperties.fixed(OptionalInt.of(69)))
         );
   }

   private static OutfitSelectScreen.SelectableOutfit find(OutfitSelectScreen.SelectableOutfit[] components, String id) {
      if (id.isEmpty()) {
         return null;
      } else {
         for (OutfitSelectScreen.SelectableOutfit outfit : components) {
            if (outfit.option.id.equals(id)) {
               return outfit;
            }
         }

         return null;
      }
   }

   static class SelectableOutfit extends LayeredSpace {
      private final OutfitWheelScreen.OutfitOption option;
      private Image icon;

      SelectableOutfit(OutfitWheelScreen.OutfitOption option) {
         super(true, new Component[0]);
         this.option = option;
      }

      public List<Component> build() {
         int deleteButtonSize = 15;
         ResourceKey deleteTexture = new ResourceKey("cosmetica", "textures/remove.png");
         return Arrays.asList(
            new Image(new ResourceKey(this.option.thumbnail.location)).crop(0.0F, 0.1667F, 0.0F, 0.1667F).setTransparent(this.option.usable ? 1.0F : 0.5F),
            (this.icon = new ClickableImage(
                  deleteTexture,
                  () -> {
                     if (!this.option.id.equals(((Optional)Cosmetica.SELECTED_OUTFIT_ID.peek()).orElse(""))) {
                        GuiUtils.playClick();
                        Screens.setScreen(
                           new ConfirmRemoveOutfitScreen(this.option.id, this.option.name),
                           Text.translatable("screens.cosmetica.confirmDeletion", new String[0])
                        );
                     }
                  }
               ) {
                  @Override
                  protected boolean canDrawDelete() {
                     return !SelectableOutfit.this.option.id.equals(((Optional)Cosmetica.SELECTED_OUTFIT_ID.peek()).orElse(""));
                  }
               })
               .withStyle(Style.create().set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(0, 0, 54, 31))))
         );
      }

      public void mouseClicked(Element target, double x, double y, int button) {
         if (this.option.usable) {
            if (target.getParent().isPresent()
               && ((Element)target.getParent().get()).getComponent() == this
               && ((Element)target.getParent().get()).getChildren().get(0) == target) {
               if (!this.option.id.equals(((Optional)Cosmetica.SELECTED_OUTFIT_ID.peek()).orElse(""))) {
                  GuiUtils.playClick();
                  this.option.equipAsync();
               }
            }
         }
      }

      public void render(Canvas canvas, Region region, Margins padding, int mouseX, int mouseY) {
         if (region.contains(mouseX, mouseY)) {
            boolean selected = this.option.id.equals(((Optional)Cosmetica.SELECTED_OUTFIT_ID.peek()).orElse(""));
            if (selected || !region.shrinkMargins(new Margins(0, 0, 54, 31)).contains(mouseX, mouseY)) {
               canvas.setTransparency(0.5F);
               canvas.drawRect(region, 2013265919);
               canvas.disableTransparency();
               if (selected) {
                  this.icon.setTransparent(0.0F);
               } else {
                  this.icon.setTransparent(1.0F);
               }
            }
         } else {
            this.icon.setTransparent(0.0F);
         }

         super.render(canvas, region, padding, mouseX, mouseY);
      }

      public Stylesheet getStylesheet() {
         return new Stylesheet().self(Style.create().set(CommonProperties.TOOLTIP, Optional.of(new Tooltip(Text.literal(this.option.name)))));
      }
   }
}

package cc.cosmetica.cosmetica.gui;

import cc.cosmetica.core.api.CosmeticaAPI;
import cc.cosmetica.core.api.Cosmetics;
import cc.cosmetica.core.api.OutfitCosmetics;
import cc.cosmetica.core.api.CosmeticaAPI.SubscriptionEvent;
import cc.cosmetica.core.impl.Logging;
import cc.cosmetica.cosmetica.Cosmetica;
import cc.cosmetica.cosmetica.gui.widget.EntryList;
import cc.cosmetica.cosmetica.gui.widget.OutfitCount;
import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Screens;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Align;
import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.Element;
import cc.cosmetica.kupe.api.gui.GUIPlayer;
import cc.cosmetica.kupe.api.gui.Image;
import cc.cosmetica.kupe.api.gui.Justify;
import cc.cosmetica.kupe.api.gui.Label;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.gui.style.CommonProperties.DimensionsOperator;
import cc.cosmetica.kupe.api.maths.Axis2D;
import cc.cosmetica.kupe.api.maths.Margins;
import gg.cloaks.javaclient.api.PremiumApi;
import gg.cloaks.javaclient.model.CopyOutfitDto;
import gg.cloaks.javaclient.model.PlanRestrictions;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

public class ReplaceOutfitSlotScreen extends Component implements AnimatedTextureScreen {
   private final Text title;
   private final UUID newOutfit;
   private final State<Cosmetics> newOutfitCosmetics;
   private final State<Integer> outfitLimit;
   private final State<Boolean> setting = new State(false);
   private final State<ReplaceOutfitSlotScreen.ReplaceableOutfit> replacing = new State(null);
   public static final ResourceKey STEAL_THEIR_LOOK = new ResourceKey("cosmetica", "steal_their_look");
   private static final Style BODY_DEFAULT_STYLE = Style.create()
      .set(CommonProperties.WIDTH, CommonProperties.SCREEN_WIDTH)
      .set(CommonProperties.HEIGHT, CommonProperties.SCREEN_HEIGHT)
      .set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X)
      .set(Div.JUSTIFY_CONTENT, Justify.CENTRE)
      .set(Div.ALIGN_ITEMS, Align.CENTRE);
   private static final Style TITLE_DEFAULT_STYLE = Style.create()
      .set(CommonProperties.WIDTH, CommonProperties.SCREEN_WIDTH)
      .set(Label.ALIGN_TEXT, Align.CENTRE)
      .set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(15, 0, 0, 0)));

   protected ReplaceOutfitSlotScreen(Cosmetics initialCosmetics) {
      if (!initialCosmetics.getOutfitId().isPresent()) {
         throw new IllegalArgumentException("No outfit id for steal-their-look cosmetics?!");
      } else {
         this.title = STEAL_THEIR_LOOK.translationKey("screens");
         this.newOutfit = UUID.fromString((String)initialCosmetics.getOutfitId().get());
         this.newOutfitCosmetics = new State(initialCosmetics);
         this.outfitLimit = new State(-1);
         CosmeticaAPI.premiumApi()
            .requestAsync(PremiumApi::getRestrictions)
            .<BigDecimal>thenApply(PlanRestrictions::getMaxOutfits)
            .thenApply(BigDecimal::intValue)
            .thenAcceptAsync(this.outfitLimit::set, Minecraft.getInstance());
         Cosmetica.fetchOutfits();
         CosmeticaAPI.subscribe(
            SubscriptionEvent.OUTFIT,
            this.newOutfit,
            STEAL_THEIR_LOOK.toResourceLocation(),
            () -> CosmeticaAPI.outfits()
               .requestAsync(api -> api.get(this.newOutfit.toString()))
               .thenApply(OutfitCosmetics::new)
               .thenAcceptAsync(this.newOutfitCosmetics::set, Minecraft.getInstance())
               .exceptionally(err -> {
                  Logging.getInstance().error("Error updating outfit cosmetics", err);
                  return null;
               })
         );
      }
   }

   public List<Component> build() {
      Cosmetics newOutfit = (Cosmetics)this.newOutfitCosmetics.acquire(this);
      boolean setting = (Boolean)this.setting.acquire(this);
      ReplaceOutfitSlotScreen.ReplaceableOutfit replacing = (ReplaceOutfitSlotScreen.ReplaceableOutfit)this.replacing.acquire(this);
      List<OutfitWheelScreen.OutfitOption> options = setting ? (List)Cosmetica.OWN_OUTFITS.peek() : (List)Cosmetica.OWN_OUTFITS.acquire(this);
      List<Component> components = options.stream()
         .map(x$0 -> new ReplaceOutfitSlotScreen.ReplaceableOutfit(x$0))
         .map(o -> o.tag(new String[]{"outfit"}))
         .collect(Collectors.toCollection(ArrayList::new));
      int outfitLimit = (Integer)this.outfitLimit.acquire(this);
      int currentCount = options.size();
      if (currentCount < outfitLimit) {
         components.add(0, new ReplaceOutfitSlotScreen.ReplaceableOutfit());
      }

      UUID player = Minecraft.getInstance().getUser().getProfileId();
      return Arrays.asList(
         new Div(new Component[]{new Label(this.title), new OutfitCount(this.outfitLimit)}).tag(new String[]{"title"}),
         new Div(
               new Component[]{
                  new Div(
                        new Component[]{
                           new GUIPlayer(player, true).withStyle(Style.create().set(CommonProperties.MIN_WIDTH, CommonProperties.screen(12.0F, 0.0F))),
                           new Label(Text.literal(newOutfit.getOutfitName().orElse("(No name)")))
                        }
                     )
                     .tag(new String[]{"width-50%"})
                     .withStyle(Style.create().set(CommonProperties.PADDING, CommonProperties.screen(6.0F, 0.0F, (w, h) -> new Margins(0, w, 0, 0)))),
                  new EntryList.Grid(components.toArray(new Component[0]), k -> {
                     if (replacing != null) {
                        for (Component o : components) {
                           OutfitWheelScreen.OutfitOption op = ((ReplaceOutfitSlotScreen.ReplaceableOutfit)o).option;
                           if (replacing.option == null && op == null) {
                              return o;
                           }

                           if (replacing.option != null && op != null && op.id.equals(replacing.option.id)) {
                              return o;
                           }
                        }
                     }

                     return null;
                  }).tag(new String[]{"width-50%"}).withStyle(Style.create().set(CommonProperties.HEIGHT, CommonProperties.screen(0.0F, 75.0F)))
               }
            )
            .tag(new String[]{"body"}),
         new Div(
               new Component[]{
                  new Button(
                        Text.translatable("button.cosmetica.confirm", new String[0]),
                        () -> {
                           if (replacing != null && replacing.usable) {
                              this.setting.set(true);
                              CopyOutfitDto dto = new CopyOutfitDto();
                              dto.equip(true);
                              if (replacing.option == null) {
                                 CosmeticaAPI.outfits()
                                    .requestAsync(api -> api.copy(this.newOutfit.toString(), dto))
                                    .thenAcceptAsync(outfit1 -> Minecraft.getInstance().setScreen(null), Minecraft.getInstance())
                                    .exceptionally(Cosmetica.mainThreadExcept(err -> {
                                       Logging.getInstance().error("Error stealing look (new)", err);
                                       this.setting.set(false);
                                    }));
                              } else {
                                 CosmeticaAPI.outfits()
                                    .requestAsync(api -> {
                                       api.delete(replacing.option.id);
                                       return api;
                                    })
                                    .thenApply(api -> api.copy(this.newOutfit.toString(), dto))
                                    .thenAcceptAsync(outfit1 -> Minecraft.getInstance().setScreen(null), Minecraft.getInstance())
                                    .exceptionally(Cosmetica.mainThreadExcept(err -> {
                                       Logging.getInstance().error("Error stealing look (replace)", err);
                                       this.setting.set(false);
                                    }));
                              }
                           }
                        }
                     )
                     .setDisabled(replacing == null || setting),
                  new Button(Text.GUI_CANCEL, Screens::closeCurrentScreen)
               }
            )
            .tag(new String[]{"bottom-bar"})
      );
   }

   public void unmount() {
      CosmeticaAPI.unsubscribe(SubscriptionEvent.OUTFIT, this.newOutfit, STEAL_THEIR_LOOK.toResourceLocation());
   }

   @NotNull
   public Stylesheet getStylesheet() {
      return new Stylesheet()
         .tag("body", BODY_DEFAULT_STYLE)
         .tag("title", TITLE_DEFAULT_STYLE)
         .tag("bottom-bar", Style.create().set(CommonProperties.MARGINS, (DimensionsOperator)(vw, vh, pw, ph) -> new Margins(vh - 50, 0, 0, 0)))
         .tag("width-50%", Style.create().set(CommonProperties.WIDTH, CommonProperties.screen(50.0F, 0.0F)))
         .component(
            ReplaceOutfitSlotScreen.ReplaceableOutfit.class,
            Style.create().set(CommonProperties.WIDTH, CommonProperties.fixedSize(50)).set(CommonProperties.HEIGHT, CommonProperties.fixedSize(50))
         );
   }

   private class ReplaceableOutfit extends Image {
      private final boolean usable;
      final OutfitWheelScreen.OutfitOption option;

      ReplaceableOutfit(OutfitWheelScreen.OutfitOption option) {
         super(new ResourceKey(option.thumbnail.location));
         this.usable = option.usable;
         this.option = option;
         this.setTransparent(option.usable ? 1.0F : 0.5F);
      }

      ReplaceableOutfit() {
         super(OutfitSelectScreen.NEW_OUTFIT_ICON);
         this.option = null;
         this.usable = true;
      }

      public void mouseClicked(Element target, double x, double y, int button) {
         if (this.usable) {
            GuiUtils.playClick();
            ReplaceOutfitSlotScreen.this.replacing.set(this);
         }
      }
   }
}

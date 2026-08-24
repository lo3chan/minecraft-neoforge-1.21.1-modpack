package cc.cosmetica.cosmetica.gui.widget;

import cc.cosmetica.core.api.CachedImage;
import cc.cosmetica.core.api.texture.CosmeticaTexture.Builder;
import cc.cosmetica.cosmetica.Cosmetica;
import cc.cosmetica.cosmetica.util.Lore;
import cc.cosmetica.kupe.api.Canvas;
import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Align;
import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.Element;
import cc.cosmetica.kupe.api.gui.Image;
import cc.cosmetica.kupe.api.gui.Justify;
import cc.cosmetica.kupe.api.gui.Label;
import cc.cosmetica.kupe.api.gui.LayeredSpace;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Axis2D;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Region;
import com.google.common.collect.ImmutableList;
import gg.cloaks.javaclient.model.LoreOptions;
import gg.cloaks.javaclient.model.UserConnection;
import gg.cloaks.javaclient.model.UpdateLoreDto.ColorEnum;
import gg.cloaks.javaclient.model.UpdateLoreDto.TypeEnum;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class LoreSelector extends LayeredSpace {
   private final AtomicBoolean loreModified;
   private final State<LoreOptions> availableLores;
   private State<Integer> lorePage = new State(0);
   private State<Boolean> colourSelectorOpen = new State(false);
   private State<ColorEnum> colour = new State(((Lore)Cosmetica.SELECTED_LORE.peek()).colour);

   public LoreSelector(AtomicBoolean loreModified, State<LoreOptions> availableLores) {
      super(true, new Component[0]);
      this.loreModified = loreModified;
      this.availableLores = availableLores;
   }

   public List<Component> build() {
      final LoreOptions loreOptions = (LoreOptions)this.availableLores.acquire(this);
      int page = (Integer)this.lorePage.acquire(this);
      this.colour.set(((Lore)Cosmetica.SELECTED_LORE.peek()).colour);

      LoreSelector.SelectableLore[] loreValues = switch (page) {
         case 0 -> (LoreSelector.SelectableLore[])loreOptions.getTitles()
            .stream()
            .map(x$0 -> new LoreSelector.SelectableLore(x$0))
            .toArray(LoreSelector.SelectableLore[]::new);
         case 1 -> (LoreSelector.SelectableLore[])loreOptions.getPronouns()
            .stream()
            .map(x$0 -> new LoreSelector.SelectablePronoun(x$0))
            .toArray(LoreSelector.SelectableLore[]::new);
         default -> {
            List<UserConnection> connections = (List<UserConnection>)Cosmetica.OWN_CONNECTIONS.acquire(this);
            yield connections.stream().map(x$0 -> new LoreSelector.SelectableConnection(x$0)).toArray(LoreSelector.SelectableLore[]::new);
         }
      };
      Function<Component, LoreSelector.SelectableLore> selectedState = t -> (LoreSelector.SelectableLore)Cosmetica.SELECTED_LORE.extract(t, loreObj -> {
         LoreSelector.SelectableLore selected = null;

         for (LoreSelector.SelectableLore lore : loreValues) {
            if (lore.value.equals(loreObj.value)) {
               selected = lore;
               break;
            }
         }

         return selected;
      });
      return ImmutableList.of(
         (new Div(new Component[0]) {
               public List<Component> build() {
                  boolean open = (Boolean)LoreSelector.this.colourSelectorOpen.acquire(this);
                  return open
                     ? Arrays.asList(
                        new DropdownMenu<ColorEnum>(
                           LoreSelector.this.colour,
                           colour -> Text.literal(new Lore(colour.toString().toLowerCase(Locale.ROOT), colour, CachedImage.NO_TEXTURE, "").formatted()),
                           loreOptions.getColors().stream().map(ColorEnum::fromValue).toArray(ColorEnum[]::new)
                        ),
                        new Div() {
                           public List<Component> build() {
                              ColorEnum colour = (ColorEnum)LoreSelector.this.colour.acquire(this);
                              Lore lore = (Lore)Cosmetica.SELECTED_LORE.peek();
                              if (colour != lore.colour) {
                                 Cosmetica.SELECTED_LORE.set(new Lore(lore.value, lore.displayText, colour, lore.icon, lore.service));
                              }

                              return super.build();
                           }
                        }
                     )
                     : super.build();
               }
            })
            .withStyle(Style.create().set(CommonProperties.Z_INDEX, 10)),
         new Div(
               new Component[]{
                  new LoreSelector.LoreHeader(Cosmetica.SELECTED_LORE::acquire, loreOptions.getColors()).tag(new String[]{"horizontal", "header"}),
                  page == 2 && loreValues.length == 0
                     ? new Div(
                           new Component[]{
                              new Div(new Component[0]).tag(new String[]{"flex-1"}),
                              new Label(Text.translatable("label.lore.noConnections", new String[0])),
                              new Button(Text.translatable("button.lore.connectDiscord", new String[0]), LoreSelector::openConnectDiscord),
                              new Div(new Component[0]).withStyle(Style.create().set(CommonProperties.FLEX, 3))
                           }
                        )
                        .tag(new String[]{"flex-1", "no-connections"})
                     : new EntryList.Div(loreValues, selectedState)
                        .selected(Style.create().set(CommonProperties.BACKGROUND_COLOUR, OptionalInt.of(16777215)).set(Label.TEXT_COLOUR, 3355443))
                        .tag(new String[]{"flex-1"}),
                  new Div(
                        new Component[]{
                           new Button(Text.translatable("button.lore.titles", new String[0]), () -> this.lorePage.set(0))
                              .setDisabled(page == 0)
                              .tag(new String[]{"lore-type"}),
                           new Button(Text.translatable("button.lore.pronouns", new String[0]), () -> this.lorePage.set(1))
                              .setDisabled(page == 1)
                              .tag(new String[]{"lore-type"}),
                           new Button(Text.translatable("button.lore.connections", new String[0]), () -> this.lorePage.set(2))
                              .setDisabled(page == 2)
                              .tag(new String[]{"lore-type"})
                        }
                     )
                     .tag(new String[]{"horizontal", "lore-types"})
               }
            )
            .tag(new String[]{"lore-selector-body"})
      );
   }

   public Stylesheet getStylesheet() {
      return new Stylesheet()
         .self(Style.create().set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(30, 10, 12, 10))))
         .tag("lore-selector-body", Style.create().set(Div.ALIGN_ITEMS, Align.STRETCH_START))
         .tag("header", Style.create().set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(0, 0, 2, 0))))
         .tag(
            "no-connections",
            Style.create()
               .set(CommonProperties.BACKGROUND_COLOUR, OptionalInt.of(0))
               .set(CommonProperties.PADDING, CommonProperties.fixed(new Margins(2)))
               .set(Label.ALIGN_TEXT, Align.CENTRE)
         )
         .tag("lore-type", Style.create().set(CommonProperties.WIDTH, CommonProperties.percent(33.0F, 0.0F)))
         .tag("lore-types", Style.create().set(Div.JUSTIFY_CONTENT, Justify.SPACE_BETWEEN));
   }

   private static void openConnectDiscord() {
      Cosmetica.openWebPanel("discord-connect");
   }

   private class LoreHeader extends Div {
      private final Function<Component, Lore> icon;
      private final List<String> unlockedColours;

      public LoreHeader(Function<Component, Lore> icon, List<String> unlockedColours) {
         super(new Component[0]);
         this.icon = icon;
         this.unlockedColours = unlockedColours;
      }

      public List<Component> build() {
         Lore lore = this.icon.apply(this);
         Text displayLore = lore.isNoLore()
            ? Text.translatable("label.lore.noLore", new String[0])
            : Text.translatable("label.lore.lore", new String[]{lore.formatted()});
         List<Component> result = new ArrayList<>();
         result.add(new Label(displayLore).tag(new String[]{"flex-1"}));
         if (this.unlockedColours.size() > 1) {
            result.add(new IconButton(new ResourceKey("cosmetica", "textures/colour.png"), () -> {
               boolean open = (Boolean)LoreSelector.this.colourSelectorOpen.peek();
               LoreSelector.this.colourSelectorOpen.set(!open);
            }));
         }

         result.add(new IconButton(new ResourceKey("cosmetica", "textures/remove.png"), this::clearLore));
         return result;
      }

      private void clearLore() {
         Lore current = (Lore)Cosmetica.SELECTED_LORE.peek();
         Lore next = Lore.none(current.colour);
         next.old = current.old == null ? current : current.old;
         Cosmetica.SELECTED_LORE.set(next);
         LoreSelector.this.loreModified.set(true);
      }
   }

   private class SelectableConnection extends LoreSelector.SelectableLore {
      private final Text username;
      private final Text serviceName;
      private final CachedImage texture;

      public SelectableConnection(UserConnection connection) {
         super(connection.getServiceId());
         this.username = Text.literal(connection.getUsername());
         this.serviceName = Text.literal("§7" + connection.getServiceName());
         this.texture = ThumbnailCache.getOrCreateImage(
            new Builder(connection.getIconUrl(), Cosmetica.LOADING_TEXTURE).failToLoadTexture(Cosmetica.FALLBACK_TEXTURE), false
         );
      }

      @Override
      public List<Component> build() {
         return ImmutableList.of(
            new Image(new ResourceKey(this.texture.location)).setTransparent(1.0F),
            new Div(new Component[]{new Label(this.username), new Label(this.serviceName)}).tag(new String[]{"label-column"})
         );
      }

      @Override
      public Stylesheet getStylesheet() {
         return new Stylesheet()
            .component(
               Image.class,
               Style.create()
                  .set(CommonProperties.WIDTH, CommonProperties.fixedSize(24))
                  .set(CommonProperties.HEIGHT, CommonProperties.fixedSize(24))
                  .set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(3, 5, 3, 0)))
            )
            .tag("label-column", Style.create().set(ALIGN_ITEMS, Align.STRETCH_START))
            .self(
               Style.create()
                  .set(CommonProperties.PADDING, CommonProperties.fixed(new Margins(3)))
                  .set(Label.ALIGN_TEXT, Align.START)
                  .set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X)
            );
      }

      @Override
      Lore createLore(Lore current, Lore old) {
         return new Lore(this.value, this.username.getDisplayString(), current.colour, this.texture, this.value);
      }
   }

   private class SelectableLore extends Div {
      protected final String value;
      boolean allowDuplication = false;

      public SelectableLore(String lore) {
         super(new Component[0]);
         this.value = lore;
      }

      public void mouseClicked(Element target, double x, double y, int button) {
         if (button == 0) {
            Lore current = (Lore)Cosmetica.SELECTED_LORE.peek();
            if (this.allowDuplication || !Objects.equals(this.value, current.value)) {
               LoreSelector.this.loreModified.set(true);
               Lore old = current.old == null ? current : current.old;
               Cosmetica.SELECTED_LORE.set(this.createLore(current, old));
            }
         }
      }

      public List<Component> build() {
         return ImmutableList.of(new Label(Text.literal(this.value)));
      }

      Lore createLore(Lore current, Lore old) {
         Lore lore = new Lore(this.value, current.colour, CachedImage.NO_TEXTURE, "");
         lore.old = old;
         return lore;
      }

      public void render(Canvas canvas, Region region, Margins padding, int mouseX, int mouseY) {
         if (region.addMargins(padding).shrinkMargins(new Margins(0, 6, 0, 0)).contains(mouseX, mouseY)
            && !((Optional)this.getStyle().get(CommonProperties.BORDER)).isPresent()) {
            canvas.drawRect(region.addMargins(padding), 7368816);
         }

         super.render(canvas, region, padding, mouseX, mouseY);
      }

      public Stylesheet getStylesheet() {
         return new Stylesheet().self(Style.create().set(CommonProperties.PADDING, CommonProperties.fixed(new Margins(1))));
      }
   }

   private class SelectablePronoun extends LoreSelector.SelectableLore {
      public SelectablePronoun(String lore) {
         super(lore);
         this.allowDuplication = true;
      }

      @Override
      public void mouseClicked(Element target, double x, double y, int button) {
      }

      @Override
      public List<Component> build() {
         Lore current = (Lore)Cosmetica.SELECTED_LORE.acquire(this);
         int slashes = 0;

         for (char c : current.value.toCharArray()) {
            if (c == '/') {
               slashes++;
            }
         }

         return ImmutableList.of(
            new Div(
                  new Component[]{
                     new Label(Text.literal(this.value)).tag(new String[]{"flex-1"}),
                     new Button(Text.literal("+"), () -> super.mouseClicked(null, 0.0, 0.0, 0)).setDisabled(slashes == 3)
                  }
               )
               .tag(new String[]{"innerdiv"})
         );
      }

      @Override
      Lore createLore(Lore current, Lore old) {
         Lore lore;
         if (current.getType().equals(TypeEnum.PRONOUNS)) {
            int slashes = 0;
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < current.value.length(); i++) {
               char c = current.value.charAt(i);
               if (c == '/') {
                  slashes++;
               }

               if (slashes == 4) {
                  break;
               }

               sb.append(c);
            }

            lore = new Lore(sb + "/" + this.value, current.colour, CachedImage.NO_TEXTURE, "pronoun");
         } else {
            lore = new Lore(this.value, current.colour, CachedImage.NO_TEXTURE, "pronoun");
         }

         lore.old = old;
         return lore;
      }

      @Override
      public Stylesheet getStylesheet() {
         return super.getStylesheet()
            .component(Button.class, Style.create().set(CommonProperties.WIDTH, CommonProperties.fixedSize(20)))
            .tag(
               "innerdiv",
               Style.create()
                  .set(FLOW_DIRECTION, Axis2D.POSITIVE_X)
                  .set(Label.ALIGN_TEXT, Align.START)
                  .set(CommonProperties.WIDTH, CommonProperties.percent(67.0F, 0.0F))
            );
      }
   }
}

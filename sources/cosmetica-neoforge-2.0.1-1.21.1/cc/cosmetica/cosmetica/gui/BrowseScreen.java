package cc.cosmetica.cosmetica.gui;

import cc.cosmetica.core.api.Accessory;
import cc.cosmetica.core.api.Cosmetic;
import cc.cosmetica.core.api.CosmeticaAPI;
import cc.cosmetica.core.api.Cosmetics;
import cc.cosmetica.core.api.ImageCosmetic;
import cc.cosmetica.core.api.Accessory.Adjustable;
import cc.cosmetica.core.impl.Logging;
import cc.cosmetica.cosmetica.Cosmetica;
import cc.cosmetica.cosmetica.gui.cosmeticconfig.AccessoryOptions;
import cc.cosmetica.cosmetica.gui.cosmeticconfig.CapeOptions;
import cc.cosmetica.cosmetica.gui.cosmeticconfig.CosmeticOptions;
import cc.cosmetica.cosmetica.gui.player.AccessoriesAttachment;
import cc.cosmetica.cosmetica.gui.widget.CosmeticEntry;
import cc.cosmetica.cosmetica.gui.widget.DataForwarder;
import cc.cosmetica.cosmetica.gui.widget.DropdownMenu;
import cc.cosmetica.cosmetica.gui.widget.DropdownToggles;
import cc.cosmetica.cosmetica.gui.widget.EntryList;
import cc.cosmetica.cosmetica.gui.widget.IconButton;
import cc.cosmetica.cosmetica.gui.widget.MenuEndSelection;
import cc.cosmetica.cosmetica.gui.widget.OutfitPlayer;
import cc.cosmetica.cosmetica.gui.widget.RotatableGUIPlayer;
import cc.cosmetica.cosmetica.gui.widget.SlideToggle;
import cc.cosmetica.cosmetica.gui.widget.SliderWidget;
import cc.cosmetica.cosmetica.settings.CosmeticaSettings;
import cc.cosmetica.cosmetica.util.EquipUtil;
import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Screens;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Align;
import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.GUIPlayer;
import cc.cosmetica.kupe.api.gui.Image;
import cc.cosmetica.kupe.api.gui.Justify;
import cc.cosmetica.kupe.api.gui.Label;
import cc.cosmetica.kupe.api.gui.LayeredSpace;
import cc.cosmetica.kupe.api.gui.TextBox;
import cc.cosmetica.kupe.api.gui.Tooltip;
import cc.cosmetica.kupe.api.gui.GUIPlayer.CapeProperties;
import cc.cosmetica.kupe.api.gui.GUIPlayer.ElytraProperties;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.gui.style.CommonProperties.DimensionsOperator;
import cc.cosmetica.kupe.api.maths.Axis2D;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import cc.cosmetica.kupe.api.maths.Vec3;
import com.google.common.collect.ImmutableList;
import gg.cloaks.javaclient.api.PremiumApi;
import gg.cloaks.javaclient.model.CreateOutfitAccessoryDto;
import gg.cloaks.javaclient.model.CreateOutfitDto;
import gg.cloaks.javaclient.model.Outfit;
import gg.cloaks.javaclient.model.PlanRestrictions;
import gg.cloaks.javaclient.model.SearchCosmeticsDto;
import gg.cloaks.javaclient.model.SearchCosmeticsDto.AttachmentsEnum;
import gg.cloaks.javaclient.model.SearchCosmeticsDto.SortByEnum;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

public class BrowseScreen extends AbstractHomeScreen {
   private final State<Integer> accessoryLimit = new State(0);
   private final DebounceState<String> query = new DebounceState<>("", 600L);
   private final State<BrowseScreen.Menu> menu = new State(BrowseScreen.Menu.NONE);
   private final State<BrowseScreen.Sort> sort = new State(BrowseScreen.Sort.NEWEST);
   private final DebounceState<Integer> page = new DebounceState<>(1, 400L);
   private final State<Integer> pageCap = new State(1);
   private final State<Set<AttachmentsEnum>> filter = new State(new HashSet());
   private final State<Optional<BrowseScreen.SelectedCosmeticTriple>> configuring = new State(Optional.empty());
   private final State<Cosmetic> configuringDownloaded = new State(null);
   private final State<Triple<Vec3, Boolean, Boolean>> options = new State(Triple.of(Vec3.XP, false, false));
   private final State<CosmeticEntry> selected = new State(null);
   public static final ResourceKey ID = new ResourceKey("cosmetica", "browse");

   public BrowseScreen() {
      super(ID);
      CosmeticaAPI.premiumApi()
         .requestAsync(PremiumApi::getRestrictions)
         .<BigDecimal>thenApply(PlanRestrictions::getMaxAccessories)
         .thenApply(BigDecimal::intValue)
         .thenAcceptAsync(this.accessoryLimit::set, Minecraft.getInstance());
   }

   @Override
   protected Component createMenuEndSelection() {
      return new MenuEndSelection() {
         @Override
         public List<Component> build() {
            Optional<BrowseScreen.SelectedCosmeticTriple> configuring = (Optional<BrowseScreen.SelectedCosmeticTriple>)BrowseScreen.this.configuring
               .acquire(this);
            this.disabled = configuring.isPresent();
            return super.build();
         }
      };
   }

   @Override
   protected Component createOutfitPlayer(UUID self, boolean authenticated, Cosmetics cosmetics) {
      final OutfitPlayer player = (OutfitPlayer)access$001(this, self, authenticated, cosmetics);
      player.keepGuiPlayer();
      return new Component() {
         public List<Component> build() {
            Triple<Vec3, Boolean, Boolean> options = (Triple<Vec3, Boolean, Boolean>)BrowseScreen.this.options.acquire(this);
            Optional<BrowseScreen.SelectedCosmeticTriple> configuring = (Optional<BrowseScreen.SelectedCosmeticTriple>)BrowseScreen.this.configuring
               .acquire(this);
            Cosmetic downloaded = (Cosmetic)BrowseScreen.this.configuringDownloaded.acquire(this);
            return ImmutableList.of(
               player.configureOverrides(
                     guiPlayer -> {
                        guiPlayer.configureOverride(GUIPlayer.ELYTRA, null);
                        guiPlayer.configureOverride(GUIPlayer.CAPE, null);
                        guiPlayer.configureOverride(AccessoriesAttachment.INSTANCE, null);
                        if (downloaded != null && configuring.isPresent()) {
                           if (downloaded instanceof Accessory) {
                              List<Accessory> accessories = new ArrayList<>(cosmetics.getAccessories());
                              accessories.add((Accessory)downloaded);
                              if (downloaded instanceof Adjustable newAccessory) {
                                 AccessoryOptions ranges = (AccessoryOptions)configuring.get().getMiddle();
                                 newAccessory.setOffset(
                                    newAccessory.getBaseOffset()
                                       .add(
                                          ranges.getXRange().clampMap(((Vec3)options.getLeft()).getX()) / 16.0,
                                          ranges.getYRange().clampMap(((Vec3)options.getLeft()).getY()) / 16.0,
                                          ranges.getZRange().clampMap(((Vec3)options.getLeft()).getZ()) / 16.0
                                       )
                                 );
                                 newAccessory.setMirrored((Boolean)options.getMiddle());
                              } else {
                                 Logging.getInstance()
                                    .warnOnce(
                                       "browse-adjust", "Accessory in browse screen not adjustable. Adjustments will not appear in preview", new Object[0]
                                    );
                              }

                              guiPlayer.configureOverride(AccessoriesAttachment.INSTANCE, accessories);
                           } else if (downloaded instanceof ImageCosmetic) {
                              if ((Boolean)options.getMiddle()) {
                                 guiPlayer.configureOverride(GUIPlayer.CAPE, new CapeProperties(((ImageCosmetic)downloaded).getImage().location));
                              }

                              if ((Boolean)options.getRight()) {
                                 guiPlayer.configureOverride(
                                    GUIPlayer.ELYTRA, new ElytraProperties(((ImageCosmetic)downloaded).getImage().location, false, true)
                                 );
                              }

                              ((RotatableGUIPlayer)guiPlayer).setYaw(220.0F);
                           }
                        }

                        return guiPlayer;
                     }
                  )
                  .setDisabled(true)
            );
         }
      };
   }

   @NotNull
   @Override
   protected Component createRightMenu(Cosmetics cosmetics, boolean authenticated) {
      return new LayeredSpace(
            true,
            new Component[]{
               (new Div() {
                     public List<Component> build() {
                        BrowseScreen.Menu menu = (BrowseScreen.Menu)BrowseScreen.this.menu.acquire(this);
                        if (menu == BrowseScreen.Menu.SORT) {
                           return Arrays.asList(
                              new DropdownMenu<BrowseScreen.Sort>(BrowseScreen.this.sort, BrowseScreen.Sort::text, BrowseScreen.Sort.values())
                           );
                        } else {
                           return menu == BrowseScreen.Menu.FILTER
                              ? Arrays.asList(new DropdownToggles<AttachmentsEnum>(BrowseScreen.this.filter, value -> {
                                 switch (value) {
                                    case CLOAK:
                                       return Text.translatable("label.cosmetica.filter.cloaks", new String[0]);
                                    case ELYTRA:
                                       return Text.translatable("label.cosmetica.filter.elytras", new String[0]);
                                    case HEAD:
                                       return Text.translatable("label.cosmetica.filter.head", new String[0]);
                                    case BODY:
                                       return Text.translatable("label.cosmetica.filter.body", new String[0]);
                                    case ARM:
                                       return Text.translatable("label.cosmetica.filter.arm", new String[0]);
                                    case LEG:
                                       return Text.translatable("label.cosmetica.filter.leg", new String[0]);
                                    default:
                                       return Text.translatable("label.cosmetica.filter.unknown", new String[0]);
                                 }
                              }, AttachmentsEnum.CLOAK, AttachmentsEnum.ELYTRA, AttachmentsEnum.HEAD, AttachmentsEnum.BODY, AttachmentsEnum.ARM, AttachmentsEnum.LEG))
                              : super.build();
                        }
                     }
                  })
                  .tag(new String[]{"browse-width"})
                  .withStyle(Style.create().set(CommonProperties.Z_INDEX, 10)),
               new Div(
                     new Component[]{
                        new LayeredSpace(
                              true,
                              new Component[]{
                                 new Div(
                                    new Component[]{
                                       new Div(
                                             new Component[]{
                                                new TextBox(Text.translatable("label.browse.search", new String[0]), this.query, true, 32)
                                                   .onEnter(this.query::setNow)
                                                   .tag(new String[]{"searchbar"}),
                                                new IconButton(new ResourceKey("cosmetica", "textures/filter.png"), () -> this.open(BrowseScreen.Menu.FILTER))
                                                   .tag(new String[]{"btn-search-adjust"}),
                                                new IconButton(new ResourceKey("cosmetica", "textures/sort.png"), () -> this.open(BrowseScreen.Menu.SORT))
                                                   .tag(new String[]{"btn-search-adjust"})
                                             }
                                          )
                                          .withStyle(
                                             Style.create()
                                                .set(CommonProperties.WIDTH, CommonProperties.percent(100.0F, 0.0F))
                                                .set(CommonProperties.MIN_HEIGHT, CommonProperties.fixedSize(20))
                                                .set(CommonProperties.PADDING, CommonProperties.fixed(new Margins(1, 1, 0, 1)))
                                                .set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X)
                                                .set(Div.JUSTIFY_CONTENT, Justify.SPACE_BETWEEN)
                                          ),
                                       new BrowseScreen.Results().tag(new String[]{"results"})
                                    }
                                 ),
                                 new BrowseScreen.ConfigureCosmetic()
                              }
                           )
                           .tag(new String[]{"browse-width", "results-wrapper"})
                     }
                  )
                  .withStyle(Style.create().set(Div.ALIGN_ITEMS, Align.STRETCH_START))
            }
         )
         .withStyle(Style.create().set(CommonProperties.PADDING, CommonProperties.fixed(new Margins(30, 10, 12, 10))));
   }

   private void open(BrowseScreen.Menu menu) {
      if (this.menu.peek() == menu) {
         this.menu.set(BrowseScreen.Menu.NONE);
      } else {
         this.menu.set(menu);
      }
   }

   public void unmount() {
      this.configuring.set(Optional.empty());
      this.configuringDownloaded.set(null);
      this.menu.set(BrowseScreen.Menu.NONE);
   }

   @NotNull
   @Override
   public Stylesheet getStylesheet() {
      return super.getStylesheet()
         .tag("results-wrapper", Style.create().set(CommonProperties.HEIGHT, CommonProperties.percent(0.0F, 100.0F)))
         .tag(
            "results",
            Style.create()
               .set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(4, 0, 0, 0)))
               .set(CommonProperties.HEIGHT, (DimensionsOperator)(vw, vh, pw, ph) -> OptionalInt.of(ph - 22))
         )
         .tag(
            "btn-search-adjust",
            Style.create().set(CommonProperties.HEIGHT, CommonProperties.fixedSize(20)).set(CommonProperties.WIDTH, CommonProperties.fixedSize(20))
         )
         .tag(
            "browse-width",
            Style.create()
               .set(CommonProperties.WIDTH, CommonProperties.screen(45.0F, 0.0F))
               .set(CommonProperties.MAXIMUM_SIZE, CommonProperties.fixed(new Dimensions(396, 2147483647)))
         )
         .tag("searchbar", Style.create().set(CommonProperties.WIDTH, (DimensionsOperator)(vw, vh, pw, ph) -> OptionalInt.of(pw - 44)));
   }

   private class ConfigureCosmetic extends Div {
      private ConfigureCosmetic() {
         super(new Component[0]);
      }

      public List<Component> build() {
         Optional<BrowseScreen.SelectedCosmeticTriple> configuring = (Optional<BrowseScreen.SelectedCosmeticTriple>)BrowseScreen.this.configuring.acquire(this);
         if (configuring.isPresent()) {
            final BrowseScreen.SelectedCosmeticTriple triple = configuring.get();
            final State<Float> xOffset = new State(0.5F);
            final State<Float> yOffset = new State(0.5F);
            final State<Float> zOffset = new State(0.5F);
            final State<Boolean> mirroredOrCloak = new State(triple.getMiddle() instanceof CapeOptions);
            final State<Boolean> elytra = new State(true);
            final State<Boolean> settingLock = new State(false);
            final State<Boolean> customVisibilityOverrides = new State(false);
            return Arrays.asList(
               DataForwarder.merge(BrowseScreen.this.options, xOffset, yOffset, zOffset, mirroredOrCloak, elytra),
               (new Div(new Component[0]) {
                     public List<Component> build() {
                        Cosmetics outfit = (Cosmetics)Cosmetica.OWN_COSMETICS.acquire(this);
                        List<Component> children = new ArrayList<>();
                        children.add(new Image(new ResourceKey(((CosmeticEntry.CosmeticData)triple.getLeft()).getThumbnail().location)).setTransparent(1.0F));
                        if (((CosmeticEntry.CosmeticData)triple.getLeft()).getCreator().isPresent()) {
                           children.add(new Label(Text.literal(((CosmeticEntry.CosmeticData)triple.getLeft()).getName())));
                           children.add(
                              new Label(
                                    Text.literal(
                                       "§o"
                                          + Text.translatable(
                                                "label.cosmetica.creator", new String[]{((CosmeticEntry.CosmeticData)triple.getLeft()).getCreator().get()}
                                             )
                                             .getDisplayString()
                                    )
                                 )
                                 .withStyle(
                                    Style.create()
                                       .set(Label.TEXT_COLOUR, 14540253)
                                       .set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(0, 0, 6, 0)))
                                 )
                           );
                        } else {
                           children.add(
                              new Label(Text.literal(((CosmeticEntry.CosmeticData)triple.getLeft()).getName()))
                                 .withStyle(Style.create().set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(0, 0, 6, 0))))
                           );
                        }

                        CosmeticOptions options = (CosmeticOptions)triple.getMiddle();
                        boolean mirrored;
                        if (options instanceof AccessoryOptions ao) {
                           mirrored = (Boolean)mirroredOrCloak.acquire(this);
                           children.add(
                              new Button(
                                    Text.translatable(
                                       "button.cosmetica.equip.mirrored",
                                       new String[]{mirrored ? Text.GUI_YES.getDisplayString() : Text.GUI_NO.getDisplayString()}
                                    ),
                                    () -> mirroredOrCloak.set(!mirrored)
                                 )
                                 .withStyle(Style.create().set(CommonProperties.FLEX_SHRINK, 0))
                           );
                           if (ao.getXRange().getRange() > 0.0) {
                              float precision = 0.5F / (float)ao.getXRange().getRange();
                              children.add(
                                 new SliderWidget(
                                       xOffset,
                                       precision,
                                       f_ -> Text.translatable(
                                          "button.cosmetica.equip.x", new String[]{String.format("%.1f", ao.getXRange().clampMap(f_.floatValue()))}
                                       )
                                    )
                                    .withStyle(Style.create().set(CommonProperties.FLEX_SHRINK, 0))
                              );
                           }

                           if (ao.getYRange().getRange() > 0.0) {
                              float precision = 0.5F / (float)ao.getYRange().getRange();
                              children.add(
                                 new SliderWidget(
                                       yOffset,
                                       precision,
                                       f_ -> Text.translatable(
                                          "button.cosmetica.equip.y", new String[]{String.format("%.1f", ao.getYRange().clampMap(f_.floatValue()))}
                                       )
                                    )
                                    .withStyle(Style.create().set(CommonProperties.FLEX_SHRINK, 0))
                              );
                           }

                           if (ao.getZRange().getRange() > 0.0) {
                              float precision = 0.5F / (float)ao.getZRange().getRange();
                              children.add(
                                 new SliderWidget(
                                       zOffset,
                                       precision,
                                       f_ -> Text.translatable(
                                          "button.cosmetica.equip.z", new String[]{String.format("%.1f", ao.getZRange().clampMap(f_.floatValue()))}
                                       )
                                    )
                                    .withStyle(Style.create().set(CommonProperties.FLEX_SHRINK, 0))
                              );
                           }

                           if (CosmeticaSettings.VISIBILITY_OVERRIDES.get()) {
                              children.add(
                                 new Label(Text.translatable("label.configureCosmetic.visibilityOptions", new String[0]))
                                    .withStyle(
                                       Style.create()
                                          .set(CommonProperties.FLEX_SHRINK, 0)
                                          .set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(8, 0, 0, 0)))
                                    )
                              );
                              children.add(
                                 new SlideToggle(
                                       customVisibilityOverrides,
                                       Text.translatable("button.configureCosmetic.visibilityOverrides.false", new String[0]),
                                       Text.translatable("button.configureCosmetic.visibilityOverrides.true", new String[0])
                                    )
                                    .withStyle(Style.create().set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(1, 0, 2, 0))))
                              );
                              children.add(
                                 (new Div(new Component[0]) {
                                       public List<Component> build() {
                                          boolean custom = (Boolean)customVisibilityOverrides.acquire(this);
                                          List<Component> components = new ArrayList<>();
                                          if (custom) {
                                             ao.forAllVisibilityOptions(option -> components.add(option.createController()));
                                          } else {
                                             ao.forAllVisibilityOptions(
                                                option -> components.add(
                                                   new Button(
                                                         Text.translatable(
                                                            option.getTranslationKey(),
                                                            new String[]{
                                                               option.getDefaultValue() ? Text.GUI_YES.getDisplayString() : Text.GUI_NO.getDisplayString()
                                                            }
                                                         ),
                                                         () -> {}
                                                      )
                                                      .setDisabled(true)
                                                      .tag(new String[]{"visibility-option-default"})
                                                )
                                             );
                                          }

                                          return components;
                                       }
                                    })
                                    .withStyle(Style.create().set(CommonProperties.FLEX_SHRINK, 0))
                              );
                           }
                        } else {
                           mirrored = false;
                           if (options instanceof CapeOptions co) {
                              if (co.isCloak()) {
                                 boolean isCloak = (Boolean)mirroredOrCloak.acquire(this);
                                 children.add(
                                    new Button(
                                       Text.translatable(
                                          "button.cosmetica.equip.isCloak",
                                          new String[]{isCloak ? Text.GUI_YES.getDisplayString() : Text.GUI_NO.getDisplayString()}
                                       ),
                                       () -> mirroredOrCloak.set(!isCloak)
                                    )
                                 );
                              }

                              if (co.isElytra()) {
                                 boolean isElytra = (Boolean)elytra.acquire(this);
                                 children.add(
                                    new Button(
                                       Text.translatable(
                                          "button.cosmetica.equip.isElytra",
                                          new String[]{isElytra ? Text.GUI_YES.getDisplayString() : Text.GUI_NO.getDisplayString()}
                                       ),
                                       () -> elytra.set(!isElytra)
                                    )
                                 );
                              }
                           }
                        }

                        children.add(
                           new Div(new Component[0]).withStyle(Style.create().set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(2, 0))))
                        );
                        Button submitButton = new Button(
                           Text.translatable("button.cosmetica.equip", new String[0]),
                           () -> {
                              CreateOutfitDto dto = new CreateOutfitDto();
                              if (options instanceof AccessoryOptions) {
                                 if (outfit == null) {
                                    throw new IllegalStateException("Outfit is null but submit button was pressed?");
                                 }

                                 AccessoryOptions ao = (AccessoryOptions)options;
                                 List<CreateOutfitAccessoryDto> accessoryDtos = new ArrayList<>();

                                 for (Accessory accessory : outfit.getAccessories()) {
                                    accessoryDtos.add(EquipUtil.dtoFromAccessory(accessory));
                                 }

                                 int flags;
                                 if ((Boolean)customVisibilityOverrides.peek()) {
                                    AtomicInteger customFlags = new AtomicInteger();
                                    ao.forAllVisibilityOptions(option -> option.configureUserValue(customFlags));
                                    flags = customFlags.get();
                                 } else {
                                    flags = -1;
                                 }

                                 CreateOutfitAccessoryDto newAccessoryDto = new CreateOutfitAccessoryDto()
                                    .id(((CosmeticEntry.CosmeticData)triple.getLeft()).getId())
                                    .mirrored(mirrored)
                                    .offset(
                                       Arrays.asList(
                                          BigDecimal.valueOf(ao.getXRange().clampMap(((Float)xOffset.peek()).floatValue())),
                                          BigDecimal.valueOf(ao.getYRange().clampMap(((Float)yOffset.peek()).floatValue())),
                                          BigDecimal.valueOf(ao.getZRange().clampMap(((Float)zOffset.peek()).floatValue()))
                                       )
                                    )
                                    .flags(flags);
                                 accessoryDtos.add(newAccessoryDto);
                                 dto.setAccessories(accessoryDtos);
                              } else if (options instanceof CapeOptions co) {
                                 if (co.isCloak() && (Boolean)mirroredOrCloak.peek()) {
                                    dto.setCloak(((CosmeticEntry.CosmeticData)triple.getLeft()).getId());
                                 }

                                 if (co.isElytra() && (Boolean)elytra.peek()) {
                                    dto.setElytra(((CosmeticEntry.CosmeticData)triple.getLeft()).getId());
                                 }
                              }

                              settingLock.set(true);
                              ((CompletableFuture)((Function)triple.getRight()).apply(dto)).thenAcceptAsync(newOutfit -> {
                                 BrowseScreen.this.configuring.set(Optional.empty());
                                 BrowseScreen.this.configuringDownloaded.set(null);
                                 Cosmetica.updateOwnCosmetics(newOutfit);
                              }, Minecraft.getInstance()).exceptionally(Cosmetica.mainThreadExcept(ex -> {
                                 settingLock.set(false);
                                 Logging.getInstance().error("Error equipping cosmetics", ex);
                              }));
                           }
                        );
                        boolean isSetting = (Boolean)settingLock.acquire(this);
                        int accessoryLimit = (Integer)BrowseScreen.this.accessoryLimit.acquire(this);
                        if (outfit == null || !outfit.getOutfitId().isPresent()) {
                           submitButton.setDisabled(true);
                           submitButton.withStyle(
                              Style.create()
                                 .set(
                                    CommonProperties.TOOLTIP, Optional.of(new Tooltip(Text.translatable("tooltip.cosmetica.noOutfitDisabled", new String[0])))
                                 )
                           );
                        } else if (isSetting) {
                           submitButton.setDisabled(true);
                           submitButton.withStyle(
                              Style.create()
                                 .set(CommonProperties.TOOLTIP, Optional.of(new Tooltip(Text.translatable("tooltip.cosmetica.equipping", new String[0]))))
                           );
                        } else if (options instanceof AccessoryOptions) {
                           if (outfit.getAccessories().size() >= accessoryLimit) {
                              submitButton.setDisabled(true);
                              submitButton.withStyle(
                                 Style.create()
                                    .set(
                                       CommonProperties.TOOLTIP,
                                       Optional.of(
                                          new Tooltip(
                                             Text.translatable(
                                                "tooltip.cosmetica.accessoryLimitReached",
                                                new String[]{String.valueOf(outfit.getAccessories().size()), String.valueOf(accessoryLimit)}
                                             )
                                          )
                                       )
                                    )
                              );
                           } else {
                              for (Accessory existingAccessory : outfit.getAccessories()) {
                                 if (existingAccessory.getId().equals(((CosmeticEntry.CosmeticData)triple.getLeft()).getId())
                                    && existingAccessory.isMirrored() == mirrored) {
                                    submitButton.setDisabled(true);
                                    submitButton.withStyle(
                                       Style.create()
                                          .set(
                                             CommonProperties.TOOLTIP,
                                             Optional.of(
                                                new Tooltip(
                                                   mirrored
                                                      ? Text.translatable("tooltip.cosmetica.alreadyEquippedAccessoryMirrored", new String[0])
                                                      : Text.translatable("tooltip.cosmetica.alreadyEquippedAccessory", new String[0])
                                                )
                                             )
                                          )
                                    );
                                 }
                              }
                           }
                        }

                        children.add(
                           new Div(
                                 new Component[]{
                                    submitButton.tag(new String[]{"flex-1"}),
                                    new Div(new Component[0]).withStyle(Style.create().set(CommonProperties.WIDTH, CommonProperties.fixedSize(4))),
                                    new Button(Text.GUI_CANCEL, () -> {
                                       BrowseScreen.this.configuring.set(Optional.empty());
                                       BrowseScreen.this.configuringDownloaded.set(null);
                                    }).setDisabled(isSetting).tag(new String[]{"flex-1"})
                                 }
                              )
                              .withStyle(Style.create().set(CommonProperties.FLEX_SHRINK, 0).set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X))
                        );
                        children.add(new Div(new Component[0]).tag(new String[]{"flex-1"}));
                        return children;
                     }
                  })
                  .tag(new String[]{"flex-1", "configure-main"})
            );
         } else {
            return ImmutableList.of();
         }
      }

      public Stylesheet getStylesheet() {
         return new Stylesheet()
            .component(
               Image.class,
               Style.create().set(CommonProperties.WIDTH, CommonProperties.fixedSize(50)).set(CommonProperties.HEIGHT, CommonProperties.fixedSize(50))
            )
            .tag(
               "configure-main",
               Style.create()
                  .set(CommonProperties.BACKGROUND_COLOUR, OptionalInt.of(8750469))
                  .set(CommonProperties.BORDER, GuiUtils.POPOUT_BORDER)
                  .set(CommonProperties.PADDING, CommonProperties.fixed(new Margins(1)))
            )
            .tag(
               "visibility-option-default",
               Style.create()
                  .set(CommonProperties.TOOLTIP, Optional.of(new Tooltip(Text.translatable("tooltip.cosmetica.defaultVisibilityOptions", new String[0]))))
            )
            .tag("flex-1", Style.create().set(CommonProperties.FLEX, 1));
      }
   }

   private static enum Menu {
      NONE,
      SORT,
      FILTER;
   }

   private class Results extends Component {
      private final State<List<Component>> pageResults = new State(Collections.emptyList());
      private volatile int searchId = 0;

      public List<Component> build() {
         final String query = (String)BrowseScreen.this.query.acquire(this);
         final BrowseScreen.Sort sort = (BrowseScreen.Sort)BrowseScreen.this.sort.acquire(this);
         final Set<AttachmentsEnum> filter = (Set<AttachmentsEnum>)BrowseScreen.this.filter.acquire(this);
         BrowseScreen.this.page.setNow(1);
         return ImmutableList.of(
            (new Div(new Component[0]) {
                  public List<Component> build() {
                     int page = (Integer)BrowseScreen.this.page.acquire(this);
                     Cosmetics outfit = (Cosmetics)Cosmetica.OWN_COSMETICS.acquire(this);
                     if (outfit == null) {
                        Screens.closeCurrentScreen();
                     } else {
                        int nextId = Results.this.searchId + 1;
                        Results.this.searchId = nextId;
                        SearchCosmeticsDto dto = new SearchCosmeticsDto();
                        dto.setQuery(query);
                        dto.setAttachments(new ArrayList<>(filter));
                        dto.setSortBy(sort.dtoEnumValue);
                        dto.setPageSize(BigDecimal.valueOf(20L));
                        dto.setPage(BigDecimal.valueOf((long)page));
                        CosmeticaAPI.search()
                           .requestAsync(api -> api.searchCosmetics(dto))
                           .thenAcceptAsync(cosmetics -> {
                              ArrayList next = new ArrayList();
                              CosmeticEntry.populateBrowseList(next, cosmetics.getResults(), outfit, (data, options, envelope, submit) -> {
                                 BrowseScreen.this.configuring.set(Optional.of(new BrowseScreen.SelectedCosmeticTriple(data, options, submit)));
                                 BrowseScreen.this.menu.set(BrowseScreen.Menu.NONE);
                                 switch (envelope.getType()) {
                                    case COSMETIC:
                                    case TEXTURE_COSMETIC:
                                    case UNKNOWN_DEFAULT_OPEN_API:
                                    default:
                                       break;
                                    case ANIMATED_TEXTURE_COSMETIC:
                                       assert envelope.getAnimatedTextureCosmetic() != null;

                                       BrowseScreen.this.configuringDownloaded.set(ImageCosmetic.fromAPI(envelope.getAnimatedTextureCosmetic()));
                                       break;
                                    case ACCESSORY:
                                       assert envelope.getAccessory() != null;

                                       BrowseScreen.this.configuringDownloaded.set(Accessory.fromAccessory(envelope.getAccessory()));
                                 }
                              });
                              if (nextId == Results.this.searchId) {
                                 Results.this.pageResults.set(next);
                              }

                              BrowseScreen.this.pageCap.set(cosmetics.getEstimatedPages());
                              BrowseScreen.this.selected.set(null);
                           }, Minecraft.getInstance())
                           .exceptionally(
                              ex -> {
                                 Logging.getInstance().error("Error performing search for " + query, ex);
                                 if (ex instanceof CompletionException) {
                                    ex = ex.getCause();
                                 }

                                 Cosmetica.showToast(
                                    Text.translatable("toast.cosmetica.searchError", new String[0]),
                                    Text.literal(ex.getMessage().length() > 27 ? ex.getMessage().substring(0, 25) + "..." : ex.getMessage())
                                 );
                                 return null;
                              }
                           );
                     }

                     return ImmutableList.of(
                        new EntryList.DynamicDiv(Results.this.pageResults, BrowseScreen.this.selected::acquire).tag(new String[]{"browse-width"}),
                        (new Div() {
                              public List<Component> build() {
                                 int pagex = BrowseScreen.this.page.acquireInstant(this);
                                 int pageCap = (Integer)BrowseScreen.this.pageCap.acquire(this);
                                 return ImmutableList.of(
                                    new IconButton(
                                          new ResourceKey("cosmetica", pagex <= 1 ? "textures/page-left-disabled.png" : "textures/page-left.png"), () -> {
                                             int p = BrowseScreen.this.page.peek();
                                             if (p > 1) {
                                                BrowseScreen.this.page.set(BrowseScreen.this.page.peek() - 1);
                                             }
                                          }
                                       )
                                       .setDisabled(pagex <= 1)
                                       .tag(new String[]{"page-button"}),
                                    new Label(Text.literal(pagex + " / " + pageCap)),
                                    new IconButton(
                                          new ResourceKey("cosmetica", pagex >= pageCap ? "textures/page-right-disabled.png" : "textures/page-right.png"),
                                          () -> {
                                             int p = BrowseScreen.this.page.peek();
                                             if (p < (Integer)BrowseScreen.this.pageCap.peek()) {
                                                BrowseScreen.this.page.set(p + 1);
                                             }
                                          }
                                       )
                                       .setDisabled(pagex >= pageCap)
                                       .tag(new String[]{"page-button"})
                                 );
                              }
                           })
                           .tag(new String[]{"page-turner"})
                     );
                  }
               })
               .tag(new String[]{"results-container", "browse-width"})
         );
      }

      public Stylesheet getStylesheet() {
         return new Stylesheet()
            .tag("results-container", Style.create().set(Div.JUSTIFY_CONTENT, Justify.SPACE_BETWEEN))
            .tag(
               "page-turner",
               Style.create()
                  .set(CommonProperties.HEIGHT, CommonProperties.fixedSize(12))
                  .set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X)
                  .set(Div.JUSTIFY_CONTENT, Justify.SPACE_BETWEEN)
                  .set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(0, 6, 0, 0)))
                  .set(CommonProperties.WIDTH, CommonProperties.percent(100.0F, 0.0F))
            )
            .tag("page-button", Style.create().set(CommonProperties.WIDTH, CommonProperties.fixedSize(30)))
            .component(EntryList.DynamicDiv.class, Style.create().set(CommonProperties.HEIGHT, (DimensionsOperator)(vw, vh, rw, rh) -> OptionalInt.of(rh - 13)));
      }
   }

   private static class SelectedCosmeticTriple
      extends MutableTriple<CosmeticEntry.CosmeticData, CosmeticOptions, Function<CreateOutfitDto, CompletableFuture<Outfit>>> {
      public SelectedCosmeticTriple(CosmeticEntry.CosmeticData image, CosmeticOptions envelope, Function<CreateOutfitDto, CompletableFuture<Outfit>> submit) {
         super(image, envelope, submit);
      }

      public void setLeft(CosmeticEntry.CosmeticData left) {
         throw new UnsupportedOperationException("SelectedCosmeticTriple is immutable");
      }

      public void setMiddle(CosmeticOptions middle) {
         throw new UnsupportedOperationException("SelectedCosmeticTriple is immutable");
      }

      public void setRight(Function<CreateOutfitDto, CompletableFuture<Outfit>> right) {
         throw new UnsupportedOperationException("SelectedCosmeticTriple is immutable");
      }
   }

   private static enum Sort {
      NEWEST("label.cosmetica.sort.newest", SortByEnum.NEWEST),
      OLDEST("label.cosmetica.sort.oldest", SortByEnum.OLDEST),
      MOST_POPULAR("label.cosmetica.sort.most_popular", SortByEnum.MOST_POPULAR),
      LEAST_POPULAR("label.cosmetica.sort.least_popular", SortByEnum.LEAST_POPULAR);

      private final Text text;
      private final SortByEnum dtoEnumValue;

      private Sort(String translationKey, SortByEnum sortByEnum) {
         this.text = Text.translatable(translationKey, new String[0]);
         this.dtoEnumValue = sortByEnum;
      }

      Text text() {
         return this.text;
      }
   }
}

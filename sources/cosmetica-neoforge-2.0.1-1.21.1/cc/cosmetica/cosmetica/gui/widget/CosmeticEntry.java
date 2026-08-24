package cc.cosmetica.cosmetica.gui.widget;

import cc.cosmetica.core.api.CachedImage;
import cc.cosmetica.core.api.CosmeticaAPI;
import cc.cosmetica.core.api.Cosmetics;
import cc.cosmetica.core.api.ImageCosmetic;
import cc.cosmetica.core.api.Accessory.Flag;
import cc.cosmetica.core.api.texture.CosmeticaTexture.AutoAnimate;
import cc.cosmetica.core.api.texture.CosmeticaTexture.Builder;
import cc.cosmetica.cosmetica.Cosmetica;
import cc.cosmetica.cosmetica.gui.ConfirmRemoveCosmeticScreen;
import cc.cosmetica.cosmetica.gui.GuiUtils;
import cc.cosmetica.cosmetica.gui.cosmeticconfig.AccessoryOptions;
import cc.cosmetica.cosmetica.gui.cosmeticconfig.CapeOptions;
import cc.cosmetica.cosmetica.gui.cosmeticconfig.CosmeticOptions;
import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Screens;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Align;
import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.Image;
import cc.cosmetica.kupe.api.gui.Label;
import cc.cosmetica.kupe.api.gui.Tooltip;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.RootStylesheet;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.gui.style.CommonProperties.DimensionsOperator;
import cc.cosmetica.kupe.api.maths.Axis2D;
import cc.cosmetica.kupe.api.maths.Dimensions;
import cc.cosmetica.kupe.api.maths.Margins;
import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import gg.cloaks.javaclient.model.Accessory;
import gg.cloaks.javaclient.model.AnimatedTextureCosmetic;
import gg.cloaks.javaclient.model.Cosmetic;
import gg.cloaks.javaclient.model.CosmeticEnvelope;
import gg.cloaks.javaclient.model.CreateOutfitDto;
import gg.cloaks.javaclient.model.Outfit;
import gg.cloaks.javaclient.model.TextureCosmetic;
import gg.cloaks.javaclient.model.Accessory.AttachmentEnum;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CosmeticEntry extends Component {
   private final CachedImage image;
   private final Cosmetics parentOutfit;
   private final ResourceKey icon;
   private final String id;
   private final String name;
   private final String owner;
   private final boolean mirrored;
   private final CosmeticEntry.Type type;
   private final CosmeticEntry.Attachment attachment;
   private List<ResourceKey> infoIcons = new ArrayList<>();
   @Nullable
   private final CosmeticEnvelope cosmetic;
   @Nullable
   private final CosmeticEntry.EquipCallback onEquipButton;
   private static final Stylesheet STYLESHEET = new Stylesheet()
      .component(Button.class, Style.create().set(CommonProperties.MAXIMUM_SIZE, CommonProperties.fixed(new Dimensions(20, 20))))
      .tag(
         "centry_main_icon",
         Style.create()
            .set(CommonProperties.PADDING, CommonProperties.fixed(new Margins(2)))
            .set(CommonProperties.WIDTH, CommonProperties.fixedSize(38))
            .set(CommonProperties.HEIGHT, CommonProperties.fixedSize(38))
            .set(CommonProperties.MIN_WIDTH, CommonProperties.fixedSize(38))
            .set(CommonProperties.MIN_HEIGHT, CommonProperties.fixedSize(38))
      )
      .tag(
         "button_subtract",
         Style.create()
            .set(CommonProperties.ALIGN_SELF, Optional.of(Align.START))
            .set(CommonProperties.MAXIMUM_SIZE, CommonProperties.fixed(new Dimensions(20, 20)))
      )
      .tag("button_add", Style.create().set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(0, 10, 0, 0))))
      .tag("centry_root", Style.create().set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X).set(Div.ALIGN_ITEMS, Align.CENTRE))
      .tag(
         "centry_normal_colour",
         Style.create().set(CommonProperties.BACKGROUND_COLOUR, OptionalInt.of(8750469)).set(CommonProperties.BORDER, GuiUtils.POPOUT_BORDER)
      )
      .tag(
         "external_colour",
         Style.create().set(CommonProperties.BACKGROUND_COLOUR, OptionalInt.of(5855577)).set(CommonProperties.BORDER, GuiUtils.SHADE_POPOUT_BORDER)
      )
      .tag("centry_names", Style.create().set(Div.ALIGN_ITEMS, Align.STRETCH_START).set(CommonProperties.FLEX, 1))
      .tag(
         "centry_info_icon",
         Style.create().set(CommonProperties.WIDTH, CommonProperties.fixedSize(14)).set(CommonProperties.HEIGHT, CommonProperties.fixedSize(14))
      )
      .tag(
         "info_icons", Style.create().set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(2, 0, 0, 0))).set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X)
      );
   public static final CachedImage NO_THUMBNAIL = new CachedImage(Cosmetica.FALLBACK_TEXTURE, 0);

   public CosmeticEntry(
      Cosmetics cosmetics,
      @Nullable CosmeticEnvelope cosmetic,
      CachedImage image,
      String id,
      String name,
      String owner,
      CosmeticEntry.Type type,
      CosmeticEntry.Attachment attachment,
      @Nullable CosmeticEntry.EquipCallback onEquipButton,
      boolean mirrored
   ) {
      this.parentOutfit = cosmetics;
      this.image = image;
      this.icon = new ResourceKey(image.location);
      this.id = id;
      this.name = name;
      this.owner = owner;
      this.type = type;
      this.attachment = attachment;
      this.cosmetic = cosmetic;
      this.onEquipButton = onEquipButton;
      this.mirrored = mirrored;
      if (cosmetic == null && type.hasEquipButton()) {
         throw new IllegalArgumentException("Cannot have null cosmetic envelope for equippable item");
      } else if (onEquipButton == null && type.hasEquipButton()) {
         throw new IllegalArgumentException("Cannot have null on equip callback for equippable item");
      }
   }

   private CosmeticEntry setInfoIcons(List<ResourceKey> infoIcons) {
      this.infoIcons = infoIcons;
      return this;
   }

   public List<Component> build() {
      Component attachmentIcon = new Image(this.attachment.icon)
         .setTransparent(1.0F)
         .tag(new String[]{"centry_info_icon"})
         .withStyle(
            Style.create()
               .set(CommonProperties.TOOLTIP, Optional.of(new Tooltip(this.attachment.tooltip())))
               .set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(0, 4, 0, 0)))
         );
      List<Component> infoIcons = this.infoIcons
         .stream()
         .map(
            i -> new Image(i)
               .setTransparent(1.0F)
               .tag(new String[]{"centry_info_icon"})
               .withStyle(Style.create().set(CommonProperties.TOOLTIP, Optional.of(new Tooltip(iconTooltip(i)))))
         )
         .collect(Collectors.toList());
      List<Component> content = new ArrayList<>(
         Arrays.asList(
            new Image(this.icon)
               .crop(0.0F, this.type == CosmeticEntry.Type.EXTERNAL ? 0.5F : 0.0F, 0.0F, 0.0F)
               .setTransparent(1.0F)
               .tag(new String[]{"centry_main_icon"}),
            new Div(
                  new Component[]{
                     new Div(
                           new Component[]{
                              new Label(Text.literal(this.name))
                                 .withStyle(
                                    Style.create().set(CommonProperties.FLEX_SHRINK, 0).set(Label.TEXT_WRAP, CommonProperties.fixed(OptionalInt.empty()))
                                 )
                           }
                        )
                        .withStyle(
                           Style.create()
                              .set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X)
                              .set(CommonProperties.MAXIMUM_SIZE, (DimensionsOperator)(vw, vh, pw, ph) -> new Dimensions(pw, 2147483647))
                        ),
                     this.type == CosmeticEntry.Type.EXTERNAL
                        ? new Div(new Component[]{attachmentIcon, new Label(Text.literal("§7" + this.owner))}).tag(new String[]{"info_icons"})
                        : new Div(merge(attachmentIcon, infoIcons).toArray(new Component[0])).tag(new String[]{"info_icons"})
                  }
               )
               .tag(new String[]{"centry_names"})
         )
      );
      if (this.type.hasRemoveButton()) {
         content.add(
            new ClickableImage(
                  new ResourceKey("cosmetica", "textures/remove_cross.png"),
                  () -> Screens.setScreen(
                     new ConfirmRemoveCosmeticScreen(this.parentOutfit, this.id, this.name, this.mirrored),
                     Text.translatable("screens.cosmetica.confirmDeletion", new String[0])
                  )
               )
               .setDisabled(this.type == CosmeticEntry.Type.REMOVABLE_OFFLINE)
               .setTransparent(1.0F)
               .withStyle(Cosmetica.authTooltipStyle(this.type == CosmeticEntry.Type.REMOVABLE))
               .tag(new String[]{"button_subtract"})
         );
      } else if (this.type.hasEquipButton()) {
         Button b = (Button)new Button(
               Text.literal("+"),
               () -> {
                  assert this.cosmetic != null;

                  assert this.onEquipButton != null;

                  CosmeticOptions options;
                  switch (this.attachment.category()) {
                     case ACCESSORY:
                        Accessory accessory = Objects.requireNonNull(this.cosmetic.getAccessory());
                        List<BigDecimal> offset = accessory.getOffset();
                        options = new AccessoryOptions(
                           new double[]{offset.get(0).doubleValue(), offset.get(3).doubleValue()},
                           new double[]{offset.get(1).doubleValue(), offset.get(4).doubleValue()},
                           new double[]{offset.get(2).doubleValue(), offset.get(5).doubleValue()},
                           Flag.HIDE_WITH_HELMET.isSet(accessory.getFlags()),
                           Flag.HIDE_WITH_CHESTPLATE.isSet(accessory.getFlags()),
                           Flag.HIDE_WITH_LEGGINGS.isSet(accessory.getFlags()),
                           Flag.HIDE_WITH_BOOTS.isSet(accessory.getFlags()),
                           Flag.HIDE_WITH_CLOAK.isSet(accessory.getFlags()),
                           Flag.HIDE_WITH_ELYTRA.isSet(accessory.getFlags()),
                           Flag.HIDE_WITH_PARROT.isSet(accessory.getFlags())
                        );
                        break;
                     case CAPE:
                        int flags = Objects.requireNonNull(this.cosmetic.getAnimatedTextureCosmetic()).getFlags();
                        options = new CapeOptions(flags);
                        break;
                     case UNKNOWN:
                     default:
                        throw new IllegalArgumentException("Unknown type for " + this.name + " (" + this.id + "), cannot equip!");
                  }

                  this.onEquipButton
                     .accept(
                        new CosmeticEntry.CosmeticData(this.name, this.owner, this.id, this.image),
                        options,
                        this.cosmetic,
                        dto -> CosmeticaAPI.outfits()
                           .requestAsync(api -> api.modify((String)this.parentOutfit.getOutfitId().orElseThrow(IllegalStateException::new), dto))
                     );
               }
            )
            .tag(new String[]{"button_add"});
         if (this.type == CosmeticEntry.Type.EQUIPPABLE_UNSUPPORTED) {
            b.setDisabled(true);
            b.withStyle(Style.create().set(CommonProperties.TOOLTIP, Optional.of(new Tooltip(Text.translatable("tooltip.cosmetica.outdated", new String[0])))));
         }

         content.add(b);
      }

      return ImmutableList.of(
         new Div(content.toArray(new Component[content.size()]))
            .tag(new String[]{"centry_root", this.type == CosmeticEntry.Type.EXTERNAL ? "external_colour" : "centry_normal_colour"})
      );
   }

   public Stylesheet getStylesheet() {
      return STYLESHEET;
   }

   private static <T> List<T> merge(T a, List<T> b) {
      ArrayList<T> result = new ArrayList<>();
      result.add(a);
      result.addAll(b);
      return result;
   }

   private static Text iconTooltip(ResourceKey infoIcon) {
      String[] path = infoIcon.getPath().split("/");
      return Text.translatable("tooltip.cosmetica.icons." + path[path.length - 1].substring(0, path[path.length - 1].length() - 4), new String[0]);
   }

   public static void populateEntryList(List<CosmeticEntry> entryList, Cosmetics cosmetics, CosmeticEntry.Type type) {
      if (cosmetics != null) {
         boolean showSeparateElytra = true;
         if (cosmetics.getCloak().isPresent()) {
            ImageCosmetic cloak = (ImageCosmetic)cosmetics.getCloak().get();
            if (cloak.getId().equals(cosmetics.getElytra().map(ImageCosmetic::getId).orElse(null))) {
               showSeparateElytra = false;
            }

            entryList.add(
               new CosmeticEntry(
                  cosmetics,
                  null,
                  cloak.isExternal()
                     ? cloak.getImage()
                     : (
                        !cloak.getThumbnail().isPresent()
                           ? NO_THUMBNAIL
                           : getOrCreateThumb((String)cloak.getThumbnail().get(), cloak.getImage().getFramePeriod(), false)
                     ),
                  cloak.getId(),
                  cloak.getName(),
                  cloak.getCreator().isPresent() ? ((GameProfile)cloak.getCreator().get()).getName() : "Could not load creator",
                  cloak.isExternal() ? CosmeticEntry.Type.EXTERNAL : type,
                  showSeparateElytra ? CosmeticEntry.Attachment.CLOAK : CosmeticEntry.Attachment.CLOAK_ELYTRA,
                  null,
                  false
               )
            );
         }

         if (showSeparateElytra && cosmetics.getElytra().isPresent()) {
            ImageCosmetic elytra = (ImageCosmetic)cosmetics.getElytra().get();
            entryList.add(
               new CosmeticEntry(
                  cosmetics,
                  null,
                  elytra.isExternal()
                     ? elytra.getImage()
                     : (
                        !elytra.getThumbnail().isPresent()
                           ? NO_THUMBNAIL
                           : getOrCreateThumb((String)elytra.getThumbnail().get(), elytra.getImage().getFramePeriod(), false)
                     ),
                  elytra.getId(),
                  elytra.getName(),
                  elytra.getCreator().isPresent() ? ((GameProfile)elytra.getCreator().get()).getName() : "Could not load creator",
                  elytra.isExternal() ? CosmeticEntry.Type.EXTERNAL : type,
                  CosmeticEntry.Attachment.ELYTRA,
                  null,
                  false
               )
            );
         }

         for (cc.cosmetica.core.api.Accessory accessory : cosmetics.getAccessories()) {
            CachedImage thumbnail = getOrCreateThumb(
               (String)accessory.getThumbnail().orElseThrow(IllegalStateException::new), accessory.getJsonObject().getTicksPerFrame().intValue(), false
            );
            CosmeticEntry entry;
            entryList.add(
               entry = new CosmeticEntry(
                  cosmetics,
                  null,
                  thumbnail,
                  accessory.getId(),
                  accessory.getName(),
                  accessory.getCreator().isPresent() ? ((GameProfile)accessory.getCreator().get()).getName() : "Could not load creator",
                  type,
                  CosmeticEntry.Attachment.accessory(accessory.getAttachment(), accessory.isMirrored()),
                  null,
                  accessory.isMirrored()
               )
            );
            List<ResourceKey> infoIcons = new ArrayList<>();
            if (accessory.isMirrored()) {
               infoIcons.add(new ResourceKey("cosmetica", "textures/icon/mirrored.png"));
            }

            for (Flag flag : accessory.getFlags()) {
               infoIcons.add(new ResourceKey("cosmetica", "textures/icon/" + flag.toString().toLowerCase(Locale.ROOT) + ".png"));
            }

            entry.setInfoIcons(infoIcons);
         }
      }
   }

   public static void populateBrowseList(
      List<CosmeticEntry> entryList, List<CosmeticEnvelope> cosmetics, @NotNull Cosmetics equipOntoOutfit, CosmeticEntry.EquipCallback equipCallback
   ) {
      Objects.requireNonNull(equipOntoOutfit, "Must have outfit to browse.");
      Objects.requireNonNull(equipCallback, "Must have equip callback.");

      for (CosmeticEnvelope envelope : cosmetics) {
         if (envelope.getCosmetic() != null) {
            Cosmetic cosmetic = envelope.getCosmetic();
            entryList.add(
               new CosmeticEntry(
                  equipOntoOutfit,
                  envelope,
                  CachedImage.NO_TEXTURE,
                  cosmetic.getId(),
                  cosmetic.getName(),
                  cosmetic.getCreator() == null ? "Could not load creator" : cosmetic.getCreator().getUsername(),
                  CosmeticEntry.Type.EQUIPPABLE_UNSUPPORTED,
                  CosmeticEntry.Attachment.UNKNOWN,
                  equipCallback,
                  false
               )
            );
         } else if (envelope.getTextureCosmetic() != null) {
            TextureCosmetic cosmetic = envelope.getTextureCosmetic();
            entryList.add(
               new CosmeticEntry(
                  equipOntoOutfit,
                  envelope,
                  getOrCreateThumb(cosmetic.getThumbnail(), 1, true),
                  cosmetic.getId(),
                  cosmetic.getName(),
                  cosmetic.getCreator() == null ? "Could not load creator" : cosmetic.getCreator().getUsername(),
                  CosmeticEntry.Type.EQUIPPABLE_UNSUPPORTED,
                  CosmeticEntry.Attachment.UNKNOWN,
                  equipCallback,
                  false
               )
            );
         } else if (envelope.getAnimatedTextureCosmetic() != null) {
            AnimatedTextureCosmetic cosmetic = envelope.getAnimatedTextureCosmetic();
            CosmeticEntry.Attachment attachment = "cape".equals(cosmetic.getType())
               ? CosmeticEntry.Attachment.cape(new CapeOptions(cosmetic.getFlags()))
               : CosmeticEntry.Attachment.UNKNOWN;
            entryList.add(
               new CosmeticEntry(
                  equipOntoOutfit,
                  envelope,
                  getOrCreateThumb(cosmetic.getThumbnail(), cosmetic.getTicksPerFrame().intValue(), true),
                  cosmetic.getId(),
                  cosmetic.getName(),
                  cosmetic.getCreator() == null ? "Could not load creator" : cosmetic.getCreator().getUsername(),
                  attachment.category() == CosmeticEntry.Category.UNKNOWN ? CosmeticEntry.Type.EQUIPPABLE_UNSUPPORTED : CosmeticEntry.Type.EQUIPPABLE,
                  attachment,
                  equipCallback,
                  false
               )
            );
         } else if (envelope.getAccessory() != null) {
            Accessory cosmetic = envelope.getAccessory();
            CosmeticEntry.Attachment attachment = "accessory".equals(cosmetic.getType())
               ? CosmeticEntry.Attachment.accessory(cosmetic.getAttachment(), false)
               : CosmeticEntry.Attachment.UNKNOWN;
            CosmeticEntry entry;
            entryList.add(
               entry = new CosmeticEntry(
                  equipOntoOutfit,
                  envelope,
                  getOrCreateThumb(cosmetic.getThumbnail(), cosmetic.getTicksPerFrame().intValue(), true),
                  cosmetic.getId(),
                  cosmetic.getName(),
                  cosmetic.getCreator() == null ? "Could not load creator" : cosmetic.getCreator().getUsername(),
                  attachment.category() == CosmeticEntry.Category.UNKNOWN ? CosmeticEntry.Type.EQUIPPABLE_UNSUPPORTED : CosmeticEntry.Type.EQUIPPABLE,
                  attachment,
                  equipCallback,
                  false
               )
            );
            if (attachment.category() == CosmeticEntry.Category.ACCESSORY) {
               List<ResourceKey> infoIcons = new ArrayList<>();
               int flags = cosmetic.getFlags();

               for (Flag flag : Flag.values()) {
                  if (flag.isSet(flags)) {
                     infoIcons.add(new ResourceKey("cosmetica", "textures/icon/" + flag.toString().toLowerCase(Locale.ROOT) + ".png"));
                  }
               }

               entry.setInfoIcons(infoIcons);
            }
         }
      }
   }

   private static CachedImage getOrCreateThumb(@Nullable String thumbnail, int ticksPerFrame, boolean browseCache) {
      return thumbnail == null
         ? NO_THUMBNAIL
         : ThumbnailCache.getOrCreateImage(
            new Builder(thumbnail + "?width=136", Cosmetica.LOADING_TEXTURE)
               .frames(8, ticksPerFrame)
               .ignoreTilesheet(true)
               .failToLoadTexture(Cosmetica.FALLBACK_TEXTURE)
               .autoAnimate(AutoAnimate.NEVER_TILESHEETS),
            browseCache
         );
   }

   static {
      RootStylesheet.setDefaultOverrides(
         CosmeticEntry.class, Style.create().set(CommonProperties.MAXIMUM_SIZE, CommonProperties.fixed(new Dimensions(2147483647, 40)))
      );
   }

   public static enum Attachment {
      HEAD_ACCESSORY(CosmeticEntry.Category.ACCESSORY, new ResourceKey("cosmetica", "textures/icon/accessory_head.png")),
      TORSO_ACCESSORY(CosmeticEntry.Category.ACCESSORY, new ResourceKey("cosmetica", "textures/icon/accessory_body.png")),
      LEFT_ARM_ACCESSORY(CosmeticEntry.Category.ACCESSORY, new ResourceKey("cosmetica", "textures/icon/accessory_left_arm.png")),
      RIGHT_ARM_ACCESSORY(CosmeticEntry.Category.ACCESSORY, new ResourceKey("cosmetica", "textures/icon/accessory_right_arm.png")),
      LEFT_LEG_ACCESSORY(CosmeticEntry.Category.ACCESSORY, new ResourceKey("cosmetica", "textures/icon/accessory_left_leg.png")),
      RIGHT_LEG_ACCESSORY(CosmeticEntry.Category.ACCESSORY, new ResourceKey("cosmetica", "textures/icon/accessory_right_leg.png")),
      CLOAK(CosmeticEntry.Category.CAPE, new ResourceKey("cosmetica", "textures/icon/cape_cloak.png")),
      ELYTRA(CosmeticEntry.Category.CAPE, new ResourceKey("cosmetica", "textures/icon/cape_elytra.png")),
      CLOAK_ELYTRA(CosmeticEntry.Category.CAPE, new ResourceKey("cosmetica", "textures/icon/cape_cape.png")),
      UNKNOWN(CosmeticEntry.Category.UNKNOWN, new ResourceKey("cosmetica", "icon.png"));

      private final CosmeticEntry.Category category;
      private final ResourceKey icon;

      private Attachment(CosmeticEntry.Category metaCategory, ResourceKey icon) {
         this.category = metaCategory;
         this.icon = icon;
      }

      public CosmeticEntry.Category category() {
         return this.category;
      }

      public Text tooltip() {
         return CosmeticEntry.iconTooltip(this.icon);
      }

      public static CosmeticEntry.Attachment accessory(AttachmentEnum attachmentEnum, boolean mirrored) {
         switch (attachmentEnum) {
            case HEAD:
               return HEAD_ACCESSORY;
            case BODY:
               return TORSO_ACCESSORY;
            case LEFT_ARM:
               return mirrored ? RIGHT_ARM_ACCESSORY : LEFT_ARM_ACCESSORY;
            case RIGHT_ARM:
               return mirrored ? LEFT_ARM_ACCESSORY : RIGHT_ARM_ACCESSORY;
            case LEFT_LEG:
               return mirrored ? RIGHT_LEG_ACCESSORY : LEFT_LEG_ACCESSORY;
            case RIGHT_LEG:
               return mirrored ? LEFT_LEG_ACCESSORY : RIGHT_LEG_ACCESSORY;
            case UNKNOWN_DEFAULT_OPEN_API:
            default:
               return UNKNOWN;
         }
      }

      public static CosmeticEntry.Attachment cape(CapeOptions options) {
         if (options.isElytra()) {
            return options.isCloak() ? CLOAK_ELYTRA : ELYTRA;
         } else {
            return options.isCloak() ? CLOAK : UNKNOWN;
         }
      }
   }

   public static enum Category {
      ACCESSORY,
      CAPE,
      UNKNOWN;
   }

   public static class CosmeticData {
      private final String name;
      private final String creator;
      private final String id;
      private final CachedImage thumbnail;

      public CosmeticData(String name, @NotNull String creator, String id, CachedImage thumbnail) {
         this.name = name;
         this.creator = creator;
         this.id = id;
         this.thumbnail = thumbnail;
      }

      public String getName() {
         return this.name;
      }

      public Optional<String> getCreator() {
         return this.creator.isEmpty() ? Optional.empty() : Optional.of(this.creator);
      }

      public String getId() {
         return this.id;
      }

      public CachedImage getThumbnail() {
         return this.thumbnail;
      }
   }

   @FunctionalInterface
   public interface EquipCallback {
      void accept(CosmeticEntry.CosmeticData var1, CosmeticOptions var2, CosmeticEnvelope var3, Function<CreateOutfitDto, CompletableFuture<Outfit>> var4);
   }

   public static enum Type {
      LISTED,
      EXTERNAL,
      REMOVABLE,
      REMOVABLE_OFFLINE,
      EQUIPPABLE,
      EQUIPPABLE_UNSUPPORTED;

      boolean hasRemoveButton() {
         return this == REMOVABLE || this == REMOVABLE_OFFLINE;
      }

      boolean hasEquipButton() {
         return this == EQUIPPABLE || this == EQUIPPABLE_UNSUPPORTED;
      }

      public static CosmeticEntry.Type removable(boolean authenticated) {
         return authenticated ? REMOVABLE : REMOVABLE_OFFLINE;
      }
   }
}

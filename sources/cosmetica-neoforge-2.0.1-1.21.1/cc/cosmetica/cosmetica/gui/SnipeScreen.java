package cc.cosmetica.cosmetica.gui;

import cc.cosmetica.core.api.Accessory;
import cc.cosmetica.core.api.CachedImage;
import cc.cosmetica.core.api.CosmeticaAPI;
import cc.cosmetica.core.api.Cosmetics;
import cc.cosmetica.core.api.ImageCosmetic;
import cc.cosmetica.core.api.NametagConfig;
import cc.cosmetica.core.impl.Logging;
import cc.cosmetica.cosmetica.Cosmetica;
import cc.cosmetica.cosmetica.StateHolder;
import cc.cosmetica.cosmetica.gui.player.AccessoriesAttachment;
import cc.cosmetica.cosmetica.gui.widget.CosmeticEntry;
import cc.cosmetica.cosmetica.gui.widget.CosmeticsList;
import cc.cosmetica.cosmetica.gui.widget.RotatableGUIPlayer;
import cc.cosmetica.cosmetica.gui.widget.SlideToggle;
import cc.cosmetica.cosmetica.util.CosmeticaLogCategory;
import cc.cosmetica.cosmetica.util.NametagUtil;
import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Screen;
import cc.cosmetica.kupe.api.Screens;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Align;
import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.GUIPlayer;
import cc.cosmetica.kupe.api.gui.Justify;
import cc.cosmetica.kupe.api.gui.Tooltip;
import cc.cosmetica.kupe.api.gui.GUIPlayer.CapeProperties;
import cc.cosmetica.kupe.api.gui.GUIPlayer.ElytraProperties;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.gui.style.CommonProperties.DimensionsOperator;
import cc.cosmetica.kupe.api.maths.Axis2D;
import cc.cosmetica.kupe.api.maths.Margins;
import gg.cloaks.javaclient.api.OutfitsApi;
import gg.cloaks.javaclient.model.OutfitAccessory;
import gg.cloaks.javaclient.model.Accessory.AttachmentEnum;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SnipeScreen extends Screen implements AnimatedTextureScreen {
   public static final ResourceKey ID = new ResourceKey("cosmetica", "snipe");
   private final State<Cosmetics> cosmetics;
   private final State<Boolean> isSettingOrUnauthenticated;
   private final State<Boolean> showingElytra = new State(false);
   @Nullable
   private final UUID playerUUID;
   private final boolean showNametag;

   public SnipeScreen(LivingEntity entity) {
      super(
         entity instanceof Player
            ? Text.literal(entity.getDisplayName().getString())
            : Text.literal(Cosmetics.getCosmetics(entity).<String>flatMap(Cosmetics::getOutfitName).orElse("Outfit"))
      );
      this.cosmetics = ((StateHolder)entity).cosmetica$getCosmeticState();
      this.playerUUID = entity instanceof Player ? entity.getUUID() : null;
      this.isSettingOrUnauthenticated = new State(!CosmeticaAPI.isAuthenticated());
      this.showNametag = entity instanceof Player;
   }

   protected Component[] buildScreen() {
      NametagUtil.isSnipe = true;
      NametagUtil.extraSpaceTaken = 29;
      Cosmetics outfit = (Cosmetics)this.cosmetics.acquire(this);
      UUID player = this.playerUUID == null ? Minecraft.getInstance().getUser().getProfileId() : this.playerUUID;
      List<CosmeticEntry> entryList = new ArrayList<>();
      RotatableGUIPlayer guiPlayer = new RotatableGUIPlayer(player, this.showingElytra);
      if (outfit != null) {
         CosmeticEntry.populateEntryList(entryList, outfit, CosmeticEntry.Type.LISTED);
         if (this.playerUUID == null) {
            guiPlayer.configureOverride(AccessoriesAttachment.INSTANCE, outfit.getAccessories());
         }

         guiPlayer.configureOverride(
            GUIPlayer.CAPE,
            outfit.getCloak()
               .<CachedImage>map(ImageCosmetic::getImage)
               .map(ci -> ci.location)
               .<CapeProperties>map(CapeProperties::new)
               .orElse(new CapeProperties((ResourceKey)null))
         );
         guiPlayer.configureOverride(
            GUIPlayer.ELYTRA,
            outfit.getElytra()
               .<CachedImage>map(ImageCosmetic::getImage)
               .map(ci -> ci.location)
               .map(location -> new ElytraProperties(new ResourceKey(location), false, true))
               .orElse(ElytraProperties.DEFAULT)
         );
         if (this.showNametag) {
            guiPlayer.showNametag(true);
            Optional<NametagConfig> lore = outfit.getLore();
            NametagConfig icon = outfit.getNametag();
            if (lore.isPresent()) {
               guiPlayer.addNametag(Text.literal(lore.get().getPrefix()), 0.75F);
               if (lore.get().getIcon().getImage().location != CachedImage.NO_TEXTURE.location) {
                  guiPlayer.loreIcon(lore.get().getIcon().getImage());
               }
            }

            if (icon.getIcon().getImage().location != CachedImage.NO_TEXTURE.location) {
               guiPlayer.icon(icon.getIcon().getImage(), icon.isTransparentIcon());
            }
         }
      }

      return new Component[]{
         new Div(
               new Component[]{
                  new Div(
                        new Component[]{
                           new Div(new Component[0]).withStyle(Style.create().set(CommonProperties.HEIGHT, CommonProperties.fixedSize(10))),
                           guiPlayer.withStyle(Style.create().set(CommonProperties.WIDTH, CommonProperties.screen(12.0F, 0.0F))),
                           new SlideToggle(
                                 this.showingElytra,
                                 Text.translatable("button.cosmetica.toggleCloak", new String[0]),
                                 Text.translatable("button.cosmetica.toggleElytra", new String[0])
                              )
                              .withStyle(Style.create().set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(5, 0, 0, 0))))
                        }
                     )
                     .tag(new String[]{"main-section"})
                     .withStyle(Style.create().set(Div.JUSTIFY_CONTENT, Justify.CENTRE)),
                  new CosmeticsList(entryList, CosmeticsList.ListType.LIST_ONLY).tag(new String[]{"main-section"})
               }
            )
            .tag(new String[]{"main-content"}),
         new SnipeScreen.StealTheirLookButton(
            outfit, this.isSettingOrUnauthenticated, Text.translatable("button.cosmetica.stealHisLook", new String[0]), () -> {
               String outfitId = outfit == null ? "" : outfit.getOutfitId().orElse("");
               if (outfitId.isEmpty()) {
                  this.isSettingOrUnauthenticated.set(true);
                  CosmeticaAPI.outfits().requestAsync(OutfitsApi::unequip).thenAccept(__ -> {
                     Logging.getInstance().debug(CosmeticaLogCategory.GUI, "Cleared Cosmetics by Steal-their-look.", new Object[0]);
                     Minecraft.getInstance().tell(Screens::closeCurrentScreen);
                  }).exceptionally(err -> {
                     Logging.getInstance().error("Failed to unequip cosmetics!", err);
                     Minecraft.getInstance().tell(() -> this.isSettingOrUnauthenticated.set(false));
                     return null;
                  });
               } else {
                  String ownedOutfit = this.findIdenticalOwnedOutfit(outfit);
                  if (ownedOutfit != null) {
                     this.isSettingOrUnauthenticated.set(true);
                     CosmeticaAPI.outfits().requestAsync(api -> api.equip(ownedOutfit)).thenAccept(__ -> {
                        Logging.getInstance().debug(CosmeticaLogCategory.GUI, "Set Cosmetics by Steal-their-look.", new Object[0]);
                        Minecraft.getInstance().tell(Screens::closeCurrentScreen);
                     }).exceptionally(err -> {
                        Logging.getInstance().error("Failed to set cosmetics!", err);
                        Minecraft.getInstance().tell(() -> this.isSettingOrUnauthenticated.set(false));
                        return null;
                     });
                  } else {
                     Screens.setScreen(new ReplaceOutfitSlotScreen(outfit), ReplaceOutfitSlotScreen.STEAL_THEIR_LOOK);
                  }
               }
            }
         ),
         new Button(Text.GUI_DONE, Screens::closeCurrentScreen)
      };
   }

   @Nullable
   private String findIdenticalOwnedOutfit(Cosmetics toWear) {
      if (((List)Cosmetica.OWN_OUTFITS.peek()).stream().anyMatch(option -> option.id.equals(toWear.getOutfitId().orElse("")))) {
         return toWear.getOutfitId().orElse("");
      } else {
         for (OutfitWheelScreen.OutfitOption owned : (List)Cosmetica.OWN_OUTFITS.peek()) {
            if (this.compare(owned, toWear)) {
               return owned.id;
            }
         }

         return null;
      }
   }

   private boolean compare(OutfitWheelScreen.OutfitOption owned, Cosmetics toWear) {
      String toWearCloak = toWear.getCloak().<String>map(ImageCosmetic::getId).orElse("");
      String toWearElytra = toWear.getElytra().<String>map(ImageCosmetic::getId).orElse("");
      if (owned.capeId.equals(toWearCloak) && owned.elytraId.equals(toWearElytra)) {
         List<Accessory> accessories = new LinkedList<>(toWear.getAccessories());

         label28:
         for (OutfitAccessory accessory : owned.accessories) {
            Vec3 offset = attachmentTransform(
               accessory.getAccessory().getAttachment(),
               ((BigDecimal)accessory.getOffset().get(0)).doubleValue(),
               ((BigDecimal)accessory.getOffset().get(1)).doubleValue(),
               ((BigDecimal)accessory.getOffset().get(2)).doubleValue()
            );
            Iterator<Accessory> accessoriesIterator = accessories.iterator();

            while (accessoriesIterator.hasNext()) {
               Accessory accessory1 = accessoriesIterator.next();
               if (accessory1.getId().equals(accessory.getAccessory().getId())) {
                  Logging.getInstance().debug(CosmeticaLogCategory.GUI, "Matching ID found. Checking offsets..", new Object[0]);
                  Vec3 offset1 = accessory1.getOffset();
                  if (offset.equals(offset1)) {
                     accessoriesIterator.remove();
                     continue label28;
                  }
               }
            }

            return false;
         }

         return accessories.isEmpty();
      } else {
         return false;
      }
   }

   private static Vec3 attachmentTransform(AttachmentEnum attachment, double x, double y, double z) {
      double dy;
      double dx;
      switch (attachment) {
         case HEAD:
            dy = 8.0;
            dx = 8.0;
            break;
         case RIGHT_ARM:
            dy = 0.0;
            dx = 8.0;
            break;
         case LEFT_ARM:
            dy = 0.0;
            dx = 7.0;
            break;
         default:
            dy = -2.0;
            dx = 8.0;
      }

      return new Vec3((x + dx) / 16.0, (y + dy) / 16.0, (z + 8.0) / 16.0);
   }

   @NotNull
   public Stylesheet getStylesheet() {
      return super.getStylesheet()
         .tag(
            "main-content",
            Style.create()
               .set(CommonProperties.FLEX, 1)
               .set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X)
               .set(Div.JUSTIFY_CONTENT, Justify.CENTRE)
               .set(Div.ALIGN_ITEMS, Align.CENTRE)
         )
         .tag(
            "main-section",
            Style.create()
               .set(CommonProperties.WIDTH, CommonProperties.screen(50.0F, 0.0F))
               .set(CommonProperties.HEIGHT, (DimensionsOperator)(vw, vh, pw, ph) -> OptionalInt.of(ph * 50 / 100 + 100))
         )
         .tag("body", Style.create().set(CommonProperties.PADDING, CommonProperties.fixed(new Margins(0, 0, 10, 0))));
   }

   private static class StealTheirLookButton extends Button {
      @Nullable
      private final Cosmetics outfit;
      private final State<Boolean> disabledState;

      public StealTheirLookButton(@Nullable Cosmetics outfit, State<Boolean> disabled, Text text, Runnable onClicked) {
         super(text, onClicked);
         this.outfit = outfit;
         this.disabledState = disabled;
      }

      public List<Component> build() {
         Cosmetics cosmetics1 = (Cosmetics)Cosmetica.OWN_COSMETICS.acquire(this);
         boolean overrideDisabled = (Boolean)this.disabledState.acquire(this);
         boolean disabled = this.outfit == null
            ? overrideDisabled || cosmetics1 == null
            : overrideDisabled || cosmetics1 != null && cosmetics1.getOutfitId().equals(this.outfit.getOutfitId());
         this.setDisabled(disabled);
         this.withStyle(
            Style.create()
               .set(
                  CommonProperties.TOOLTIP,
                  disabled ? Optional.of(new Tooltip(Text.translatable("tooltip.cosmetica.outfitAlreadySelected", new String[0]))) : Optional.empty()
               )
         );
         return super.build();
      }
   }
}

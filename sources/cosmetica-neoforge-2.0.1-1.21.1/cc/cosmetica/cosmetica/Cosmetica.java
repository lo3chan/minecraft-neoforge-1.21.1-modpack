package cc.cosmetica.cosmetica;

import cc.cosmetica.core.api.CachedImage;
import cc.cosmetica.core.api.CosmeticManagers;
import cc.cosmetica.core.api.CosmeticaAPI;
import cc.cosmetica.core.api.CosmeticaModel;
import cc.cosmetica.core.api.Cosmetics;
import cc.cosmetica.core.api.ImageCosmetic;
import cc.cosmetica.core.api.NametagConfig;
import cc.cosmetica.core.api.CosmeticaAPI.AuthChangeReason;
import cc.cosmetica.core.api.CosmeticaAPI.SubscriptionEvent;
import cc.cosmetica.core.api.texture.CosmeticaTexture.Builder;
import cc.cosmetica.core.builtin.manager.SelfCosmeticManager;
import cc.cosmetica.core.impl.BlockModelManager;
import cc.cosmetica.core.impl.Logging;
import cc.cosmetica.cosmetica.gui.BrowseScreen;
import cc.cosmetica.cosmetica.gui.CosmeticaToast;
import cc.cosmetica.cosmetica.gui.CreateNewOutfitScreen;
import cc.cosmetica.cosmetica.gui.HomeScreen;
import cc.cosmetica.cosmetica.gui.OutfitSelectScreen;
import cc.cosmetica.cosmetica.gui.OutfitWheelScreen;
import cc.cosmetica.cosmetica.gui.StyleNametagScreen;
import cc.cosmetica.cosmetica.gui.player.AccessoriesAttachment;
import cc.cosmetica.cosmetica.gui.player.CosmeticaCapeProvider;
import cc.cosmetica.cosmetica.settings.CosmeticaSettings;
import cc.cosmetica.cosmetica.util.CosmeticaLogCategory;
import cc.cosmetica.cosmetica.util.Lore;
import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Screens;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.GUIPlayer;
import cc.cosmetica.kupe.api.gui.Tooltip;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Style.MutableStyle;
import com.google.common.collect.ImmutableList;
import gg.cloaks.javaclient.api.AuthApi;
import gg.cloaks.javaclient.api.OutfitsApi;
import gg.cloaks.javaclient.api.UsersApi;
import gg.cloaks.javaclient.model.Outfit;
import gg.cloaks.javaclient.model.PlayerResponse;
import gg.cloaks.javaclient.model.UserConnection;
import gg.cloaks.javaclient.model.Lore.TypeEnum;
import gg.cloaks.javaclient.model.UpdateLoreDto.ColorEnum;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class Cosmetica {
   public static final State<Cosmetics> OWN_COSMETICS = new State(null);
   public static final State<List<OutfitWheelScreen.OutfitOption>> OWN_OUTFITS = new State(ImmutableList.of());
   public static final State<List<UserConnection>> OWN_CONNECTIONS = new State(ImmutableList.of());
   public static final State<Optional<String>> SELECTED_OUTFIT_ID = new State(Optional.empty());
   public static final State<ImageCosmetic> SELECTED_ICON = new State(NametagConfig.NO_ICON);
   public static final State<Lore> SELECTED_LORE = new State(Lore.none(ColorEnum.WHITE));
   private static CacheCosmeticManager cacheCosmeticManager;
   public static final ResourceLocation FALLBACK_TEXTURE = new ResourceKey("cosmetica", "icon.png").toResourceLocation();
   public static final ResourceLocation FALLBACK_OUTFIT_TEXTURE = new ResourceKey("cosmetica", "textures/default_outfit.png").toResourceLocation();
   public static final ResourceLocation LOADING_TEXTURE = new ResourceKey("cosmetica", "textures/loading.png").toResourceLocation();

   public static CacheCosmeticManager getCacheCosmeticManager() {
      return cacheCosmeticManager;
   }

   public static void init(CacheCosmeticManager.UserIO userIO) {
      Screens.setAllowDebug(true);
      CosmeticaSettings.refreshLocalSettings();
      CosmeticaAPI.addAuthenticationChangeCallback(reason -> {
         if (reason == AuthChangeReason.AUTHENTICATED) {
            Minecraft.getInstance().execute(CosmeticaSettings::applyLocalSettings);
         }
      });
      if (System.getProperty("cosmetica.token") != null && CosmeticaAPI.isAuthenticated()) {
         CosmeticaSettings.applyLocalSettings();
      }

      GUIPlayer.addCapeProvider(new CosmeticaCapeProvider());
      Path cosmeticCacheDir = BlockModelManager.getCacheFile(new ResourceKey("cosmetica", "a").toResourceLocation(), null)
         .getParent()
         .getParent()
         .resolve("offlineCache");

      try {
         Files.createDirectories(cosmeticCacheDir);
      } catch (IOException var3) {
         Logging.getInstance().error("Unable to create Cosmetic cache directory", var3);
      }

      cacheCosmeticManager = new CacheCosmeticManager(cosmeticCacheDir, userIO);
      CosmeticManagers.registerCosmeticManager(10, cacheCosmeticManager);
      GUIPlayer.registerAttachment(AccessoriesAttachment.INSTANCE);
      Cosmetics.registerCosmeticsChangeCallback((either, cosmetics) -> {
         if (either.entity instanceof Player) {
            Minecraft.getInstance().tell(() -> ((StateHolder)either.entity).cosmetica$setCosmeticState(cosmetics));
         } else if (either.remotePlayerInfo != null) {
            Minecraft.getInstance().tell(() -> ((StateHolder)either.remotePlayerInfo).cosmetica$setCosmeticState(cosmetics));
         }
      });
      CosmeticaAPI.subscribe(
         SubscriptionEvent.PLAYER,
         Minecraft.getInstance().getUser().getProfileId(),
         new ResourceKey("cosmetica", "outfit_refresh").toResourceLocation(),
         () -> Minecraft.getInstance().execute(Cosmetica::fetchOutfits)
      );
      CosmeticaAPI.addAuthenticationChangeCallback(reason -> {
         if (reason == AuthChangeReason.AUTHENTICATED) {
            Minecraft.getInstance().execute(Cosmetica::fetchOutfits);
            Minecraft.getInstance().execute(OutfitSelectScreen::fetchOutfitLimit);
         }
      });
      if (CosmeticaSettings.VERSION_CHECKER.get()) {
         CosmeticaAPI.downloads().requestAsync(api -> api.getVersionStatus("2.0.1")).thenAcceptAsync(e -> {
            if (e.getMinecraftMessage() != null) {
               VersionChecker.INSTANCE.setMessage(Text.literal(e.getMinecraftMessage()));
            }
         }, Minecraft.getInstance());
      }

      if (System.getProperty("cosmetica.token") != null && CosmeticaAPI.isAuthenticated()) {
         Minecraft.getInstance().execute(Cosmetica::fetchOutfits);
         Minecraft.getInstance().execute(OutfitSelectScreen::fetchOutfitLimit);
      }

      Cosmetics.registerSelfDataFetchCallback(
         (data, cosmetics) -> {
            if (data == null) {
               if (!cosmetics.getOutfitId().isPresent()) {
                  Logging.getInstance().debug(CosmeticaLogCategory.EVENTS, "Own cosmetics cleared", new Object[0]);
                  Minecraft.getInstance().execute(() -> {
                     OWN_COSMETICS.set(null);
                     SELECTED_OUTFIT_ID.set(Optional.empty());
                     SELECTED_ICON.set(NametagConfig.NO_ICON);
                  });
               } else {
                  Logging.getInstance().debug(CosmeticaLogCategory.EVENTS, "Received own outfit update", new Object[0]);
                  Minecraft.getInstance().execute(() -> {
                     OWN_COSMETICS.set(cosmetics);
                     SELECTED_OUTFIT_ID.set(cosmetics.getOutfitId());
                     SELECTED_ICON.set(cosmetics.getNametag().getIcon());
                  });
               }
            } else {
               Logging.getInstance().debug(CosmeticaLogCategory.EVENTS, "Received own cosmetics", new Object[0]);
               List<UserConnection> connections;
               Lore userLore;
               if (data.isIsUser()) {
                  cacheCosmeticManager.save(data.getUser());
                  if (data.getUser().getActiveSettings() != null) {
                     CosmeticaSettings.updateSettings(data.getUser());
                  } else {
                     CosmeticaAPI.users().requestAsync(UsersApi::getSelf).thenAcceptAsync(user -> {
                        if (user.getActiveSettings() != null) {
                           CosmeticaSettings.updateSettings(user);
                        }
                     }, Minecraft.getInstance());
                  }

                  connections = data.getUser().getConnections();
                  gg.cloaks.javaclient.model.Lore lore = data.getUser().getLore();
                  userLore = lore == null
                     ? Lore.none(ColorEnum.WHITE)
                     : new Lore(
                        lore.getContent(),
                        ColorEnum.fromValue(lore.getColor().getValue()),
                        lore.getIconUrl() == null
                           ? CachedImage.NO_TEXTURE
                           : CosmeticaModel.getOrCreateCosmeticaImage(new Builder(lore.getIconUrl(), FALLBACK_TEXTURE).frames(1, 1)),
                        lore.getType() == TypeEnum.CONNECTION ? lore.getService() : (lore.getType() == TypeEnum.PRONOUNS ? "pronoun" : "")
                     );
               } else {
                  connections = ImmutableList.of();
                  userLore = Lore.none(ColorEnum.WHITE);
               }

               Minecraft.getInstance().execute(() -> {
                  OWN_COSMETICS.set(cosmetics);
                  OWN_CONNECTIONS.set(connections);
                  SELECTED_OUTFIT_ID.set(cosmetics.getOutfitId());
                  SELECTED_ICON.set(cosmetics.getNametag().getIcon());
                  SELECTED_LORE.set(userLore);
               });
            }
         }
      );
      Authentication.authenticate();
      registerScreens();
   }

   public static void fetchOutfits() {
      CosmeticaAPI.outfits()
         .requestAsync(OutfitsApi::getOwn)
         .thenAcceptAsync(list -> OWN_OUTFITS.set(list.stream().map(OutfitWheelScreen.OutfitOption::new).collect(Collectors.toList())), Minecraft.getInstance());
   }

   public static void openWebPanel(String targetPage) {
      CosmeticaAPI.auth()
         .requestAsync(AuthApi::generateExchangeToken)
         .thenAccept(
            token -> copyAndOpenURL(System.getProperty("cosmetica.website", "https://cosmetica.cc") + "/login?token=" + token + "&state=" + targetPage)
         )
         .exceptionally(ex -> {
            Logging.getInstance().error("Unable to open " + targetPage + " page", ex);
            return null;
         });
   }

   public static void copyAndOpenURL(String url) {
      try {
         Minecraft.getInstance().keyboardHandler.setClipboard(url);
         Util.getPlatform().openUri(url);
      } catch (Exception var2) {
         throw new RuntimeException("bruh", var2);
      }
   }

   public static void updateOwnCosmetics(Outfit o) {
      Logging.getInstance().debug(CosmeticaLogCategory.GUI, "Updating own cosmetics from Outfit", new Object[0]);
      if (SelfCosmeticManager.update(o)) {
         Logging.getInstance().debug(CosmeticaLogCategory.GUI, "Refreshing self to refresh external capes on outfit", new Object[0]);
         CosmeticaAPI.users()
            .requestAsync(UsersApi::getSelf)
            .thenAcceptAsync(u -> SelfCosmeticManager.update(new PlayerResponse().user(u).isUser(true)), Minecraft.getInstance())
            .exceptionally(ex -> {
               Logging.getInstance().error("Updating own cosmetics", ex);
               return null;
            });
      }
   }

   public static void showToast(Text title, @Nullable Text description) {
      Minecraft.getInstance().getToasts().addToast(new CosmeticaToast(title, description));
   }

   public static MutableStyle authTooltipStyle(boolean authenticated) {
      return Style.create()
         .set(
            CommonProperties.TOOLTIP,
            authenticated ? Optional.empty() : Optional.of(new Tooltip(Text.translatable("tooltip.cosmetica.offline", new String[0])))
         );
   }

   public static Optional<Tooltip> authTooltip(boolean authenticated) {
      return authenticated ? Optional.empty() : Optional.of(new Tooltip(Text.translatable("tooltip.cosmetica.offline", new String[0])));
   }

   private static void registerScreens() {
      Screens.registerScreen(HomeScreen.ID, HomeScreen::new);
      Screens.registerScreen(BrowseScreen.ID, BrowseScreen::new);
      Screens.registerScreen(StyleNametagScreen.ID, StyleNametagScreen::new);
      Screens.registerScreen(OutfitSelectScreen.ID, OutfitSelectScreen::new);
      Screens.registerScreen(CreateNewOutfitScreen.ID, CreateNewOutfitScreen::new);
   }

   public static <T> Function<T, Void> mainThreadExcept(Consumer<T> tConsumer) {
      return t -> {
         Minecraft.getInstance().execute(() -> tConsumer.accept(t));
         return null;
      };
   }
}

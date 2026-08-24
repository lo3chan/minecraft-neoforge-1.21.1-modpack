package cc.cosmetica.cosmetica.gui;

import cc.cosmetica.core.api.CachedImage;
import cc.cosmetica.core.api.CosmeticaAPI;
import cc.cosmetica.core.api.Cosmetics;
import cc.cosmetica.core.api.ImageCosmetic;
import cc.cosmetica.core.api.NametagConfig;
import cc.cosmetica.core.builtin.OutfitCosmeticsHolder;
import cc.cosmetica.core.builtin.manager.ApiCosmeticManager;
import cc.cosmetica.core.builtin.manager.SelfCosmeticManager;
import cc.cosmetica.core.impl.Logging;
import cc.cosmetica.cosmetica.Cosmetica;
import cc.cosmetica.cosmetica.gui.player.AccessoriesAttachment;
import cc.cosmetica.cosmetica.gui.widget.IconButton;
import cc.cosmetica.cosmetica.gui.widget.MenuEndSelection;
import cc.cosmetica.cosmetica.gui.widget.OutfitPlayer;
import cc.cosmetica.cosmetica.settings.CosmeticaSettings;
import cc.cosmetica.cosmetica.util.CosmeticaLogCategory;
import cc.cosmetica.cosmetica.util.NametagUtil;
import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.Screen;
import cc.cosmetica.kupe.api.Screens;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Align;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.GUIPlayer;
import cc.cosmetica.kupe.api.gui.Justify;
import cc.cosmetica.kupe.api.gui.LayeredSpace;
import cc.cosmetica.kupe.api.gui.Tooltip;
import cc.cosmetica.kupe.api.gui.GUIPlayer.CapeProperties;
import cc.cosmetica.kupe.api.gui.GUIPlayer.ElytraProperties;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Axis2D;
import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import gg.cloaks.javaclient.api.UsersApi;
import gg.cloaks.javaclient.model.PlayerResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractHomeScreen extends Screen implements AnimatedTextureScreen {
   private static State<Boolean> reloadDisabled = new State(false);
   private static final ScheduledExecutorService BUTTON_SCHEDULER = Executors.newScheduledThreadPool(1);

   protected AbstractHomeScreen(ResourceKey id) {
      super(id);
   }

   protected Component[] buildScreen() {
      NametagUtil.isSnipe = false;
      NametagUtil.extraSpaceTaken = 69;
      UUID self = Minecraft.getInstance().getUser().getProfileId();
      Cosmetics cosmetics = (Cosmetics)Cosmetica.OWN_COSMETICS.acquire(this);
      boolean authenticated = CosmeticaAPI.isAuthenticated();
      if (cosmetics == null && !authenticated) {
         cosmetics = Cosmetica.getCacheCosmeticManager().getCosmetics(null);
      }

      Logging.getInstance()
         .debug(
            CosmeticaLogCategory.GUI,
            "Loaded cosmetics for screen: " + cosmetics + " with " + (cosmetics == null ? 0 : cosmetics.getAccessories().size()) + " accessories",
            new Object[0]
         );
      return new Component[]{
         new LayeredSpace(
               true,
               new Component[]{
                  new Div(
                        new Component[]{
                           this.createOutfitPlayer(self, authenticated, cosmetics).tag(new String[]{"main-section"}),
                           this.createRightMenu(cosmetics, authenticated).tag(new String[]{"main-section"})
                        }
                     )
                     .tag(new String[]{"main-content"}),
                  new Div(
                        new Component[]{
                           new IconButton(
                              new ResourceKey("cosmetica", "textures/gear.png"),
                              () -> Screens.setScreen(
                                 new CosmeticaSettingsScreen(CosmeticaSettingsScreen.SETTINGS_SCREEN, CosmeticaSettings.DISPLAY_SETTINGS),
                                 CosmeticaSettingsScreen.SETTINGS_SCREEN
                              )
                           ),
                           new IconButton(
                                 new ResourceKey("cosmetica", "textures/cape.png"),
                                 () -> Screens.setScreen(new ExternalCapesScreen(CosmeticaSettings.externalCapeSettings), ExternalCapesScreen.ID)
                              )
                              .setDisabled(!authenticated)
                              .withStyle(Cosmetica.authTooltipStyle(authenticated)),
                           new IconButton(new ResourceKey("minecraft", "textures/item/name_tag.png"), () -> Screens.setScreen(StyleNametagScreen.ID))
                              .setDisabled(!authenticated)
                              .withStyle(Cosmetica.authTooltipStyle(authenticated)),
                           new Div(new Component[0]).withStyle(Style.create().set(CommonProperties.FLEX, 1)),
                           (new IconButton(
                                 new ResourceKey("cosmetica", "textures/reload.png"),
                                 () -> {
                                    Logging.getInstance().info("Reloading all cosmetics", new Object[0]);
                                    reloadDisabled.set(true);
                                    BUTTON_SCHEDULER.schedule(() -> Minecraft.getInstance().execute(() -> reloadDisabled.set(false)), 15L, TimeUnit.SECONDS);
                                    if (CosmeticaAPI.isAuthenticated()) {
                                       CosmeticaAPI.users()
                                          .requestAsync(UsersApi::getSelf)
                                          .thenAcceptAsync(
                                             user -> SelfCosmeticManager.update(new PlayerResponse().isUser(true).user(user)), Minecraft.getInstance()
                                          )
                                          .exceptionally(ex -> {
                                             Logging.getInstance().error("Failed to reload own cosmetics", ex);
                                             return null;
                                          });
                                    } else {
                                       SelfCosmeticManager.clear();
                                    }

                                    int players = 0;
                                    int outfits = 0;
                                    ClientLevel level = Minecraft.getInstance().level;
                                    if (level != null) {
                                       for (Entity entity : level.entitiesForRendering()) {
                                          if (entity instanceof RemotePlayer) {
                                             if (Cosmetics.getCosmetics((LivingEntity)entity).isPresent()) {
                                                GameProfile profile = ((AbstractClientPlayer)entity).getGameProfile();
                                                ApiCosmeticManager.lookUpGameProfile(profile);
                                                players++;
                                             }
                                          } else if (entity instanceof OutfitCosmeticsHolder) {
                                             ((OutfitCosmeticsHolder)entity).cosmeticacore$reloadCosmetics();
                                             outfits++;
                                          }
                                       }
                                    }

                                    Logging.getInstance()
                                       .debug(
                                          CosmeticaLogCategory.GUI,
                                          "Started reload for {} remote players and {} outfit holders",
                                          new Object[]{players, outfits}
                                       );
                                 }
                              ) {
                                 public List<Component> build() {
                                    boolean disabled = (Boolean)AbstractHomeScreen.reloadDisabled.acquire(this);
                                    this.setDisabled(disabled);
                                    return ImmutableList.of();
                                 }
                              })
                              .withStyle(
                                 Style.create()
                                    .set(
                                       CommonProperties.TOOLTIP,
                                       Optional.of(new Tooltip(Text.translatable("tooltip.cosmetica.reloadCosmetics", new String[0])))
                                    )
                              )
                        }
                     )
                     .withStyle(Style.create().set(Div.ALIGN_ITEMS, Align.START).set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X))
               }
            )
            .tag(new String[]{"main-content-wrapper"}),
         this.createMenuEndSelection()
      };
   }

   protected Component createMenuEndSelection() {
      return new MenuEndSelection();
   }

   protected Component createOutfitPlayer(UUID self, boolean authenticated, Cosmetics cosmetics) {
      OutfitPlayer player = new OutfitPlayer(
         self,
         authenticated,
         Optional.ofNullable(cosmetics).<String>flatMap(Cosmetics::getOutfitName).orElse("§7No Outfit"),
         Optional.ofNullable(cosmetics).<NametagConfig>flatMap(Cosmetics::getLore).orElse(NametagConfig.EMPTY),
         Optional.ofNullable(cosmetics).<NametagConfig>map(Cosmetics::getNametag).orElse(NametagConfig.EMPTY)
      );
      if (!authenticated && cosmetics != null) {
         Logging.getInstance().debug(CosmeticaLogCategory.GUI, "Not authenticated and no outfit. Configuring overrides...", new Object[0]);
         player.configureOverrides(
            p -> p.configureOverride(AccessoriesAttachment.INSTANCE, cosmetics.getAccessories())
               .configureOverride(
                  GUIPlayer.CAPE,
                  cosmetics.getCloak()
                     .<CachedImage>map(ImageCosmetic::getImage)
                     .map(c -> c.location)
                     .<CapeProperties>map(CapeProperties::new)
                     .orElse(new CapeProperties((ResourceKey)null))
               )
               .configureOverride(
                  GUIPlayer.ELYTRA,
                  cosmetics.getElytra()
                     .<CachedImage>map(ImageCosmetic::getImage)
                     .map(c -> new ElytraProperties(c.location, false, true))
                     .orElse(ElytraProperties.DEFAULT)
               )
         );
      }

      return player;
   }

   @NotNull
   protected abstract Component createRightMenu(Cosmetics var1, boolean var2);

   @NotNull
   public Stylesheet getStylesheet() {
      return super.getStylesheet()
         .tag("main-content-wrapper", Style.create().set(CommonProperties.FLEX, 1))
         .tag(
            "main-content",
            Style.create().set(Div.FLOW_DIRECTION, Axis2D.POSITIVE_X).set(Div.JUSTIFY_CONTENT, Justify.CENTRE).set(Div.ALIGN_ITEMS, Align.CENTRE)
         )
         .tag(
            "main-section",
            Style.create()
               .set(CommonProperties.WIDTH, CommonProperties.screen(50.0F, 0.0F))
               .set(CommonProperties.HEIGHT, CommonProperties.percent(0.0F, 100.0F))
         );
   }
}

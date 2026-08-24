package net.mehvahdjukaar.moonlight.api.client.gui.widget;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import net.mehvahdjukaar.moonlight.api.client.gui.MoonlightIcons;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.util.TextHelper;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.client.MoonlightHubInfo;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.components.SpriteIconButton.Builder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

public class MediaButton {
   private static final String OWN_PACKAGE = "net/mehvahdjukaar";
   private static final Map<String, Boolean> OWN_MODS = new HashMap<>();
   public static final ResourceLocation YOUTUBE = MediaButton.MediaIcon.YOUTUBE.sprite();
   public static final ResourceLocation TWITTER = MediaButton.MediaIcon.TWITTER.sprite();
   public static final ResourceLocation DISCORD = MediaButton.MediaIcon.DISCORD.sprite();
   public static final ResourceLocation PATREON = MediaButton.MediaIcon.PATREON.sprite();
   public static final ResourceLocation KO_FI = MediaButton.MediaIcon.KO_FI.sprite();
   public static final ResourceLocation CURSEFORGE = MediaButton.MediaIcon.CURSEFORGE.sprite();
   public static final ResourceLocation MODRINTH = MediaButton.MediaIcon.MODRINTH.sprite();
   public static final ResourceLocation MARKETPLACE = MediaButton.MediaIcon.MARKETPLACE.sprite();
   public static final ResourceLocation GITHUB = MediaButton.MediaIcon.GITHUB.sprite();
   public static final ResourceLocation AKLIZ = MediaButton.MediaIcon.AKLIZ.sprite();
   public static final ResourceLocation BISECT = MediaButton.MediaIcon.BISECT.sprite();
   public static final ResourceLocation LINK = MediaButton.MediaIcon.LINK.sprite();
   public static final ResourceLocation YES = MoonlightIcons.YES;
   public static final ResourceLocation NO = MoonlightIcons.NO;
   private static final boolean LOL;
   private static final List<MediaButton.MediaIcon> MOD_PAGE_ORDER = List.of(
      MediaButton.MediaIcon.CURSEFORGE,
      MediaButton.MediaIcon.MODRINTH,
      MediaButton.MediaIcon.GITHUB,
      MediaButton.MediaIcon.DISCORD,
      MediaButton.MediaIcon.YOUTUBE,
      MediaButton.MediaIcon.TWITTER
   );
   private static final List<MediaButton.MediaIcon> HUB_ICONS = List.of(
      MediaButton.MediaIcon.DISCORD, MediaButton.MediaIcon.YOUTUBE, MediaButton.MediaIcon.TWITTER
   );
   private static final int MAX_UNKNOWN_LINKS = 2;

   private static boolean enabled(MediaButton.ButtonType type) {
      return MoonlightHubInfo.INSTANCE.isButtonEnabled(type);
   }

   public static boolean isOwnMod(String modId) {
      return OWN_MODS.computeIfAbsent(modId, id -> PlatHelper.findModResource(id, "net/mehvahdjukaar") != null);
   }

   public static Button create(Screen parent, int x, int y, ResourceLocation texture, String url, String tooltip) {
      return create(parent, x, y, texture, url, Component.literal(tooltip));
   }

   public static Button create(Screen parent, int x, int y, ResourceLocation texture, String url, Component tooltip) {
      return create(14, 14, texture, parent, x, y, url, tooltip);
   }

   public static Button create(int iconW, int iconH, ResourceLocation texture, Screen parent, int x, int y, String url, String tooltip) {
      return create(iconW, iconH, texture, parent, x, y, url, Component.literal(tooltip));
   }

   public static Button create(int iconW, int iconH, ResourceLocation texture, Screen parent, int x, int y, String url, Component tooltip) {
      String finalUrl = getLink(url);
      OnPress onPress = op -> {
         Style style = Style.EMPTY.withClickEvent(new ClickEvent(Action.OPEN_URL, finalUrl));
         parent.handleComponentClicked(style);
      };
      SpriteIconButton button = new Builder(CommonComponents.EMPTY, onPress, true).sprite(texture, iconW, iconH).size(iconW + 6, iconH + 6).build();
      button.setTooltip(Tooltip.create(tooltip));
      button.setPosition(x, y);
      return button;
   }

   private static String getLink(String original) {
      return LOL ? "https://www.youtube.com/watch?v=dQw4w9WgXcQ" : original;
   }

   private static String swap(String url, String old, String fetched) {
      return old.equals(url) ? fetched : url;
   }

   public static Button youtube(Screen parent, int x, int y, String url) {
      if (!enabled(MediaButton.ButtonType.YOUTUBE)) {
         return placeholderButton(x, y);
      } else {
         String redirected = swap(url, MoonlightHubInfo.OLD_SIGNATURE.youtube(), MoonlightHubInfo.INSTANCE.youtube());
         return create(parent, x, y, YOUTUBE, redirected, Component.translatable("tooltip.moonlight.media.youtube"));
      }
   }

   public static Button twitter(Screen parent, int x, int y, String url) {
      if (!enabled(MediaButton.ButtonType.TWITTER)) {
         return placeholderButton(x, y);
      } else {
         String redirected = swap(url, MoonlightHubInfo.OLD_SIGNATURE.twitter(), MoonlightHubInfo.INSTANCE.twitter());
         return create(parent, x, y, TWITTER, redirected, Component.translatable("tooltip.moonlight.media.twitter"));
      }
   }

   public static Button discord(Screen parent, int x, int y, String url) {
      if (!enabled(MediaButton.ButtonType.DISCORD)) {
         return placeholderButton(x, y);
      } else {
         String redirected = swap(url, MoonlightHubInfo.OLD_SIGNATURE.discord(), MoonlightHubInfo.INSTANCE.discord());
         return create(parent, x, y, DISCORD, redirected, Component.translatable("tooltip.moonlight.media.discord"));
      }
   }

   public static Button patreon(Screen parent, int x, int y, String url) {
      if (!enabled(MediaButton.ButtonType.PATREON)) {
         return placeholderButton(x, y);
      } else {
         String redirected = swap(url, MoonlightHubInfo.OLD_SIGNATURE.patreon(), MoonlightHubInfo.INSTANCE.patreon());
         return create(parent, x, y, PATREON, redirected, Component.translatable("tooltip.moonlight.media.patreon"));
      }
   }

   public static Button koFi(Screen parent, int x, int y, String url) {
      if (!enabled(MediaButton.ButtonType.KO_FI)) {
         return placeholderButton(x, y);
      } else {
         String redirected = swap(url, MoonlightHubInfo.OLD_SIGNATURE.koFi(), MoonlightHubInfo.INSTANCE.koFi());
         return create(parent, x, y, KO_FI, redirected, Component.translatable("tooltip.moonlight.media.ko_fi"));
      }
   }

   public static Button curseForge(Screen parent, int x, int y, String url) {
      return !enabled(MediaButton.ButtonType.CURSEFORGE)
         ? placeholderButton(x, y)
         : create(parent, x, y, CURSEFORGE, url, Component.translatable("tooltip.moonlight.media.curseforge"));
   }

   public static Button modrinth(Screen parent, int x, int y, String url) {
      return !enabled(MediaButton.ButtonType.MODRINTH)
         ? placeholderButton(x, y)
         : create(parent, x, y, MODRINTH, url, Component.translatable("tooltip.moonlight.media.modrinth"));
   }

   public static Button github(Screen parent, int x, int y, String url) {
      return !enabled(MediaButton.ButtonType.GITHUB)
         ? placeholderButton(x, y)
         : create(parent, x, y, GITHUB, url, Component.translatable("tooltip.moonlight.media.github"));
   }

   public static Button marketplace(Screen parent, int x, int y, String url) {
      return !enabled(MediaButton.ButtonType.MARKETPLACE)
         ? placeholderButton(x, y)
         : create(parent, x, y, MARKETPLACE, url, Component.translatable("tooltip.moonlight.media.marketplace"));
   }

   @Deprecated(
      forRemoval = true
   )
   public static Button akliz(Screen parent, int x, int y, String url, String tooltip) {
      return akliz(parent, x, y, url);
   }

   public static Button akliz(Screen parent, int x, int y, String url) {
      MoonlightHubInfo.PartnerServerProvider oldInfo = MoonlightHubInfo.OLD_SIGNATURE.partnerServer();
      if (oldInfo != null && oldInfo.url().equals(url)) {
         Button sp = serverProvider(parent, x, y);
         return sp != null ? sp : placeholderButton(x, y);
      } else {
         return !enabled(MediaButton.ButtonType.SERVER)
            ? placeholderButton(x, y)
            : create(parent, x, y, AKLIZ, url, Component.translatable("tooltip.moonlight.media.akliz"));
      }
   }

   private static Button forIcon(Screen parent, int x, int y, MediaButton.MediaIcon icon, String url) {
      return switch (icon) {
         case YOUTUBE -> youtube(parent, x, y, url);
         case TWITTER -> twitter(parent, x, y, url);
         case DISCORD -> discord(parent, x, y, url);
         default -> link(parent, x, y, url);
         case CURSEFORGE -> curseForge(parent, x, y, url);
         case MODRINTH -> modrinth(parent, x, y, url);
         case GITHUB -> github(parent, x, y, url);
      };
   }

   private static boolean enabled(MediaButton.MediaIcon icon) {
      return switch (icon) {
         case YOUTUBE -> enabled(MediaButton.ButtonType.YOUTUBE);
         case TWITTER -> enabled(MediaButton.ButtonType.TWITTER);
         case DISCORD -> enabled(MediaButton.ButtonType.DISCORD);
         default -> true;
         case CURSEFORGE -> enabled(MediaButton.ButtonType.CURSEFORGE);
         case MODRINTH -> enabled(MediaButton.ButtonType.MODRINTH);
         case GITHUB -> enabled(MediaButton.ButtonType.GITHUB);
      };
   }

   public static Button link(Screen parent, int x, int y, String url) {
      return create(parent, x, y, LINK, url, Component.translatable("tooltip.moonlight.media.link"));
   }

   @Nullable
   public static MediaButton.MediaIcon iconForUrl(String url) {
      String host = TextHelper.urlHost(url);
      if (host == null) {
         return null;
      } else if (host.endsWith("curseforge.com")) {
         return MediaButton.MediaIcon.CURSEFORGE;
      } else if (host.endsWith("modrinth.com")) {
         return MediaButton.MediaIcon.MODRINTH;
      } else if (host.endsWith("github.com")) {
         return MediaButton.MediaIcon.GITHUB;
      } else if (host.endsWith("discord.gg") || host.endsWith("discord.com") || host.endsWith("discordapp.com")) {
         return MediaButton.MediaIcon.DISCORD;
      } else if (host.endsWith("patreon.com")) {
         return MediaButton.MediaIcon.PATREON;
      } else if (host.endsWith("ko-fi.com")) {
         return MediaButton.MediaIcon.KO_FI;
      } else if (host.endsWith("youtube.com") || host.equals("youtu.be")) {
         return MediaButton.MediaIcon.YOUTUBE;
      } else {
         return !host.endsWith("twitter.com") && !host.equals("x.com") ? null : MediaButton.MediaIcon.TWITTER;
      }
   }

   private static Button placeholderButton(int x, int y) {
      Button b = Button.builder(CommonComponents.EMPTY, op -> {}).bounds(x, y, 20, 20).build();
      b.visible = false;
      b.active = false;
      return b;
   }

   @Nullable
   public static Button serverProvider(Screen parent, int x, int y) {
      if (!enabled(MediaButton.ButtonType.SERVER)) {
         return null;
      } else {
         MoonlightHubInfo.PartnerServerProvider info = MoonlightHubInfo.INSTANCE.partnerServer();
         if (info == null) {
            return null;
         } else {
            Component tooltip = Component.translatable("tooltip.moonlight.media.partner_server", new Object[]{info.providerName()});
            return create(parent, x, y, info.icon().sprite(), info.url(), tooltip);
         }
      }
   }

   public static void addAuthorMediaButtons(
      Screen parent,
      Consumer<Button> adder,
      int centerX,
      int y,
      int spacing,
      String modId,
      @Nullable String curseforgeUrl,
      @Nullable String modrinthUrl,
      @Nullable String modSourceUrl,
      Runnable onBack
   ) {
      MoonlightHubInfo hub = MoonlightHubInfo.INSTANCE;
      boolean ours = isOwnMod(modId);
      Map<MediaButton.MediaIcon, String> byIcon = new LinkedHashMap<>();
      List<String> unknownHosts = new ArrayList<>();

      for (String url : PlatHelper.getModLinks(modId)) {
         MediaButton.MediaIcon icon = iconForUrl(url);
         if (icon == null) {
            if (!unknownHosts.contains(url)) {
               unknownHosts.add(url);
            }
         } else {
            byIcon.putIfAbsent(icon, url);
         }
      }

      if (curseforgeUrl != null) {
         byIcon.put(MediaButton.MediaIcon.CURSEFORGE, curseforgeUrl);
      }

      if (modrinthUrl != null) {
         byIcon.put(MediaButton.MediaIcon.MODRINTH, modrinthUrl);
      }

      if (modSourceUrl != null) {
         byIcon.put(MediaButton.MediaIcon.GITHUB, modSourceUrl);
      }

      adder.accept(Button.builder(CommonComponents.GUI_BACK, b -> onBack.run()).bounds(centerX - 45, y, 90, 20).build());
      List<IntFunction<Button>> support = new ArrayList<>();
      List<IntFunction<Button>> socials = new ArrayList<>();
      if (ours) {
         addIfEnabled(support, MediaButton.ButtonType.PATREON, x -> patreon(parent, x, y, hub.patreon()));
         addIfEnabled(support, MediaButton.ButtonType.KO_FI, x -> koFi(parent, x, y, hub.koFi()));
         addIfEnabled(socials, MediaButton.ButtonType.DISCORD, x -> discord(parent, x, y, hub.discord()));
         addIfEnabled(socials, MediaButton.ButtonType.YOUTUBE, x -> youtube(parent, x, y, hub.youtube()));
         addIfEnabled(socials, MediaButton.ButtonType.TWITTER, x -> twitter(parent, x, y, hub.twitter()));
         addIfEnabled(socials, MediaButton.ButtonType.MARKETPLACE, x -> marketplace(parent, x, y, hub.marketplace()));
         if (hub.partnerServer() != null) {
            addIfEnabled(socials, MediaButton.ButtonType.SERVER, x -> serverProvider(parent, x, y));
         }
      }

      List<MediaButton.ModLink> pages = new ArrayList<>();

      for (MediaButton.MediaIcon icon : MOD_PAGE_ORDER) {
         String urlx = byIcon.get(icon);
         if (urlx != null && (!ours || !HUB_ICONS.contains(icon)) && enabled(icon)) {
            pages.add(new MediaButton.ModLink(icon, urlx));
         }
      }

      for (String urlx : unknownHosts.stream().limit(2L).toList()) {
         pages.add(new MediaButton.ModLink(MediaButton.MediaIcon.LINK, urlx));
      }

      List<IntFunction<Button>> left = new ArrayList<>();
      List<IntFunction<Button>> right = new ArrayList<>();
      int leftCount = support.size();
      int rightCount = socials.size();

      for (MediaButton.ModLink page : pages) {
         if (leftCount <= rightCount) {
            left.add(x -> forIcon(parent, x, y, page.icon(), page.url()));
            leftCount++;
         } else {
            right.add(x -> forIcon(parent, x, y, page.icon(), page.url()));
            rightCount++;
         }
      }

      left.addAll(support);
      right.addAll(socials);
      placeRow(adder, left, centerX - 45 - spacing, -spacing);
      placeRow(adder, right, centerX + 45 + 2, spacing);
   }

   private static void addIfEnabled(List<IntFunction<Button>> out, MediaButton.ButtonType type, IntFunction<Button> factory) {
      if (enabled(type)) {
         out.add(factory);
      }
   }

   private static void placeRow(Consumer<Button> adder, List<IntFunction<Button>> buttons, int startX, int step) {
      int x = startX;

      for (IntFunction<Button> button : buttons) {
         adder.accept(button.apply(x));
         x += step;
      }
   }

   public static void addAuthorMediaButtons(Screen parent, Consumer<Button> adder, int centerX, int y, int spacing, String modId, Runnable onBack) {
      addAuthorMediaButtons(parent, adder, centerX, y, spacing, modId, null, null, null, onBack);
   }

   public static void addAuthorMediaButtons(
      Screen parent,
      Consumer<Button> adder,
      int centerX,
      int y,
      int spacing,
      String modId,
      @Nullable String curseforgeUrl,
      @Nullable String modrinthUrl,
      Runnable onBack
   ) {
      addAuthorMediaButtons(parent, adder, centerX, y, spacing, modId, curseforgeUrl, modrinthUrl, null, onBack);
   }

   static {
      Calendar calendar = Calendar.getInstance();
      LOL = calendar.get(2) == 3 && calendar.get(5) == 1;
   }

   public static enum ButtonType implements StringRepresentable {
      YOUTUBE,
      TWITTER,
      DISCORD,
      PATREON,
      KO_FI,
      CURSEFORGE,
      MODRINTH,
      MARKETPLACE,
      GITHUB,
      SERVER;

      public static final Codec<MediaButton.ButtonType> CODEC = StringRepresentable.fromValues(MediaButton.ButtonType::values);
      private final String name = this.toString().toLowerCase(Locale.ROOT);

      public String getSerializedName() {
         return this.name;
      }
   }

   public static enum MediaIcon implements StringRepresentable {
      YOUTUBE,
      TWITTER,
      DISCORD,
      PATREON,
      KO_FI,
      CURSEFORGE,
      MODRINTH,
      MARKETPLACE,
      GITHUB,
      AKLIZ,
      BISECT,
      GENERIC_SERVER,
      LINK;

      public static final Codec<MediaButton.MediaIcon> CODEC = StringRepresentable.fromValues(MediaButton.MediaIcon::values);
      private final String name = this.toString().toLowerCase(Locale.ROOT);
      private final ResourceLocation sprite = Moonlight.res("media/" + this.name);

      public ResourceLocation sprite() {
         return this.sprite;
      }

      public String getSerializedName() {
         return this.name;
      }
   }

   private record ModLink(MediaButton.MediaIcon icon, String url) {
   }
}

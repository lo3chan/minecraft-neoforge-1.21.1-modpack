package net.mehvahdjukaar.moonlight.core.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.mehvahdjukaar.moonlight.api.client.gui.widget.MediaButton;
import net.mehvahdjukaar.moonlight.api.util.FileDownloadUtils;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public record MoonlightHubInfo(
   @Nullable MoonlightHubInfo.PartnerServerProvider partnerServer,
   String patreon,
   String koFi,
   String youtube,
   String twitter,
   String discord,
   String marketplace,
   Set<MediaButton.ButtonType> buttons
) {
   public static final Set<MediaButton.ButtonType> ALL_BUTTONS = Set.of(MediaButton.ButtonType.values());
   private static final String MARKETPLACE_URL = "https://www.minecraft.net/en-us/marketplace/pdp/razzleberries/supplementaries/c18ca233-28af-416b-9618-c0c59b64569d";
   public static MoonlightHubInfo INSTANCE = new MoonlightHubInfo(
      null,
      "https://www.patreon.com/user?u=53696377",
      "https://ko-fi.com/mehvahdjukaar",
      "https://www.youtube.com/@MehVahdJukaar",
      "https://twitter.com/Supplementariez",
      "https://discord.com/invite/qdKRTDf8Cv",
      "https://www.minecraft.net/en-us/marketplace/pdp/razzleberries/supplementaries/c18ca233-28af-416b-9618-c0c59b64569d",
      ALL_BUTTONS
   );
   public static MoonlightHubInfo OLD_SIGNATURE = new MoonlightHubInfo(
      new MoonlightHubInfo.PartnerServerProvider(MediaButton.MediaIcon.AKLIZ, "Akliz", "https://www.akliz.net/supplementaries"),
      "https://www.patreon.com/user?u=53696377",
      "https://ko-fi.com/mehvahdjukaar",
      "https://www.youtube.com/watch?v=LSPNAtAEn28&t=1s",
      "https://twitter.com/Supplementariez?s=09",
      "https://discord.com/invite/qdKRTDf8Cv",
      "",
      ALL_BUTTONS
   );
   private static final String FETCH_URL = "https://raw.githubusercontent.com/MehVahdJukaar/Moonlight/1.21/supplementaries_team_info.json";
   private static final List<String> DEFAULT_BUTTON_NAMES = Arrays.stream(MediaButton.ButtonType.values())
      .map(MediaButton.ButtonType::getSerializedName)
      .toList();
   public static final Codec<MoonlightHubInfo> CODEC = RecordCodecBuilder.create(
      i -> i.group(
            MoonlightHubInfo.PartnerServerProvider.CODEC.optionalFieldOf("partner_server").forGetter(p -> Optional.ofNullable(p.partnerServer)),
            Codec.STRING.fieldOf("patreon").forGetter(p -> p.patreon),
            Codec.STRING.fieldOf("ko_fi").forGetter(p -> p.koFi),
            Codec.STRING.fieldOf("youtube").forGetter(p -> p.youtube),
            Codec.STRING.fieldOf("twitter").forGetter(p -> p.twitter),
            Codec.STRING.fieldOf("discord").forGetter(p -> p.discord),
            Codec.STRING
               .optionalFieldOf(
                  "marketplace", "https://www.minecraft.net/en-us/marketplace/pdp/razzleberries/supplementaries/c18ca233-28af-416b-9618-c0c59b64569d"
               )
               .forGetter(p -> p.marketplace),
            Codec.STRING
               .listOf()
               .optionalFieldOf("buttons", DEFAULT_BUTTON_NAMES)
               .forGetter(p -> p.buttons.stream().map(MediaButton.ButtonType::getSerializedName).toList())
         )
         .apply(
            i,
            (ps, pat, kf, yt, tw, dc, mk, btnNames) -> new MoonlightHubInfo(
               (MoonlightHubInfo.PartnerServerProvider)ps.orElse(null), pat, kf, yt, tw, dc, mk, toButtons(btnNames)
            )
         )
   );
   private static final Gson GSON = new GsonBuilder()
      .disableHtmlEscaping()
      .registerTypeAdapter(MoonlightHubInfo.class, (JsonDeserializer)(json, type, ctx) -> (MoonlightHubInfo)CODEC.parse(JsonOps.INSTANCE, json).getOrThrow())
      .create();

   public boolean isButtonEnabled(MediaButton.ButtonType type) {
      return this.buttons.contains(type);
   }

   private static Set<MediaButton.ButtonType> toButtons(List<String> names) {
      EnumSet<MediaButton.ButtonType> set = EnumSet.noneOf(MediaButton.ButtonType.class);

      for (String n : names) {
         for (MediaButton.ButtonType b : MediaButton.ButtonType.values()) {
            if (b.getSerializedName().equals(n)) {
               set.add(b);
               break;
            }
         }
      }

      return set;
   }

   public static void fetchFromServer() {
      Thread t = new Thread(
         () -> {
            try {
               INSTANCE = (MoonlightHubInfo)GSON.fromJson(
                  FileDownloadUtils.readString("https://raw.githubusercontent.com/MehVahdJukaar/Moonlight/1.21/supplementaries_team_info.json"),
                  MoonlightHubInfo.class
               );
            } catch (Exception var1) {
               Moonlight.LOGGER
                  .warn(
                     "Failed to fetch hub info from {}: {}",
                     "https://raw.githubusercontent.com/MehVahdJukaar/Moonlight/1.21/supplementaries_team_info.json",
                     var1.toString()
                  );
            }
         },
         "Moonlight Hub Fetcher"
      );
      t.setDaemon(true);
      t.start();
   }

   public record PartnerServerProvider(MediaButton.MediaIcon icon, String providerName, String url) {
      public static final Codec<MoonlightHubInfo.PartnerServerProvider> CODEC = RecordCodecBuilder.create(
         i -> i.group(
               MediaButton.MediaIcon.CODEC
                  .lenientOptionalFieldOf("icon", MediaButton.MediaIcon.GENERIC_SERVER)
                  .forGetter(MoonlightHubInfo.PartnerServerProvider::icon),
               Codec.STRING.fieldOf("provider_name").forGetter(MoonlightHubInfo.PartnerServerProvider::providerName),
               Codec.STRING.fieldOf("url").forGetter(MoonlightHubInfo.PartnerServerProvider::url)
            )
            .apply(i, MoonlightHubInfo.PartnerServerProvider::new)
      );
   }
}

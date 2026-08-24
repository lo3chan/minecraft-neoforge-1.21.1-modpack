package corgitaco.corgilib.config;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DataResult.Error;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.corgilib.CorgiLib;
import corgitaco.corgilib.platform.ModPlatform;
import corgitaco.corgilib.serialization.codec.CommentedCodec;
import corgitaco.corgilib.serialization.jankson.JanksonJsonOps;
import corgitaco.corgilib.shadow.blue.endless.jankson.Jankson;
import corgitaco.corgilib.shadow.blue.endless.jankson.JsonElement;
import corgitaco.corgilib.shadow.blue.endless.jankson.JsonGrammar;
import corgitaco.corgilib.shadow.blue.endless.jankson.JsonObject;
import corgitaco.corgilib.shadow.blue.endless.jankson.api.SyntaxError;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

public record AnnouncementConfig(AnnouncementConfig.AnnouncementDelivery announcementDelivery) {
   public static final Path PATH = ModPlatform.PLATFORM.modConfigDir().resolve("announcement_config.json5");
   public static final Codec<AnnouncementConfig.AnnouncementDelivery> DELIVERY_CODEC = Codec.STRING
      .xmap(s -> AnnouncementConfig.AnnouncementDelivery.valueOf(s.toUpperCase()), Enum::name);
   public static final String COMMENT = "This has 2 acceptable values: [CHAT, WIDGET]\n* CHAT - Will post a message within 5 minutes to a user's chat once they've been in game for 5 minutes. Will no longer show after the user presses dismiss.\n* WIDGET - Displays a widget on screen with the announcement on all screens until the widget is dismissed.\n";
   public static final Codec<AnnouncementConfig> CODEC = RecordCodecBuilder.create(
      announcementConfigInstance -> announcementConfigInstance.group(
            CommentedCodec.of(
                  DELIVERY_CODEC,
                  "announcement_delivery",
                  "This has 2 acceptable values: [CHAT, WIDGET]\n* CHAT - Will post a message within 5 minutes to a user's chat once they've been in game for 5 minutes. Will no longer show after the user presses dismiss.\n* WIDGET - Displays a widget on screen with the announcement on all screens until the widget is dismissed.\n"
               )
               .forGetter(AnnouncementConfig::announcementDelivery)
         )
         .apply(announcementConfigInstance, AnnouncementConfig::new)
   );
   public static final Supplier<AnnouncementConfig> INSTANCE = Suppliers.memoize(
      () -> {
         if (!PATH.toFile().exists()) {
            DataResult<JsonElement> jsonElementDataResult = CODEC.encodeStart(
               JanksonJsonOps.INSTANCE, new AnnouncementConfig(AnnouncementConfig.AnnouncementDelivery.WIDGET)
            );
            if (jsonElementDataResult.error().isPresent()) {
               CorgiLib.LOGGER
                  .error(
                     "Ignoring config %s due to errors: %s".formatted(PATH.toAbsolutePath(), ((Error)jsonElementDataResult.error().orElseThrow()).toString())
                  );
               return new AnnouncementConfig(AnnouncementConfig.AnnouncementDelivery.WIDGET);
            }

            try {
               Files.createDirectories(PATH.getParent());
               Files.writeString(PATH, ((JsonElement)jsonElementDataResult.result().get()).toJson(new JsonGrammar.Builder().withComments(true).build()));
            } catch (IOException var3) {
               var3.printStackTrace();
               CorgiLib.LOGGER.error("Ignoring config %s due to errors: %s".formatted(PATH.toAbsolutePath(), var3.getLocalizedMessage()));
               return new AnnouncementConfig(AnnouncementConfig.AnnouncementDelivery.WIDGET);
            }
         } else {
            try {
               JsonObject load = Jankson.builder().build().load(PATH.toFile());
               DataResult<Pair<AnnouncementConfig, JsonElement>> decode = CODEC.decode(JanksonJsonOps.INSTANCE, load);
               if (decode.error().isPresent()) {
                  CorgiLib.LOGGER
                     .error("Ignoring config %s due to errors: %s".formatted(PATH.toAbsolutePath(), ((Error)decode.error().orElseThrow()).toString()));
                  return new AnnouncementConfig(AnnouncementConfig.AnnouncementDelivery.WIDGET);
               }

               if (decode.result().isPresent()) {
                  return (AnnouncementConfig)((Pair)decode.result().orElseThrow()).getFirst();
               }
            } catch (SyntaxError | IOException var2) {
               var2.printStackTrace();
               CorgiLib.LOGGER.error("Ignoring config %s due to errors: %s".formatted(PATH.toAbsolutePath(), var2.getLocalizedMessage()));
               return new AnnouncementConfig(AnnouncementConfig.AnnouncementDelivery.WIDGET);
            }
         }

         return new AnnouncementConfig(AnnouncementConfig.AnnouncementDelivery.WIDGET);
      }
   );

   public static enum AnnouncementDelivery {
      CHAT,
      WIDGET;
   }
}

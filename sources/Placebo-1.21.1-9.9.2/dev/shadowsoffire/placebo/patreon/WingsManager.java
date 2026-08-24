package dev.shadowsoffire.placebo.patreon;

import dev.shadowsoffire.placebo.Placebo;
import dev.shadowsoffire.placebo.patreon.wings.Wing;
import dev.shadowsoffire.placebo.payloads.PatreonDisablePayload;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.InputEvent.Key;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

public class WingsManager {
   static Map<UUID, PatreonUtils.WingType> WINGS = new HashMap<>();
   public static final KeyMapping TOGGLE = new KeyMapping("placebo.toggleWings", 328, "key.categories.placebo");
   public static final Set<UUID> DISABLED = new HashSet<>();
   public static final ModelLayerLocation WING_LOC = new ModelLayerLocation(Placebo.loc("wings"), "main");

   public static void init(FMLClientSetupEvent e) {
      e.enqueueWork(() -> ClientHooks.registerLayerDefinition(WING_LOC, Wing::createLayer));
      new Thread(() -> {
         Placebo.LOGGER.info("Loading patreon wing data...");

         try {
            URL url = new URI("https://raw.githubusercontent.com/Shadows-of-Fire/Placebo/1.16/PatreonWings.txt").toURL();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
               String s;
               while ((s = reader.readLine()) != null) {
                  String[] split = s.split(" ", 2);
                  if (split.length != 2) {
                     Placebo.LOGGER.error("Invalid patreon wing entry {} will be ignored.", s);
                  } else {
                     WINGS.put(UUID.fromString(split[0]), PatreonUtils.WingType.valueOf(split[1]));
                  }
               }

               reader.close();
            } catch (IOException var6) {
               Placebo.LOGGER.error("Exception loading patreon wing data!");
               var6.printStackTrace();
            }
         } catch (Exception var7) {
         }

         Placebo.LOGGER.info("Loaded {} patreon wings.", WINGS.size());
         if (WINGS.size() > 0) {
            NeoForge.EVENT_BUS.register(WingsManager.class);
         }
      }, "Placebo Patreon Wing Loader").start();
   }

   @SubscribeEvent
   public static void keys(Key e) {
      if (e.getAction() == 1 && TOGGLE.matches(e.getKey(), e.getScanCode()) && Minecraft.getInstance().getConnection() != null) {
         PacketDistributor.sendToServer(
            new PatreonDisablePayload(PatreonDisablePayload.CosmeticType.WINGS, Minecraft.getInstance().player.getUUID()), new CustomPacketPayload[0]
         );
      }
   }

   public static PatreonUtils.WingType getType(UUID id) {
      return WINGS.get(id);
   }
}

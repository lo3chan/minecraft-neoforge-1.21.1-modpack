package dev.shadowsoffire.placebo.patreon;

import dev.shadowsoffire.placebo.Placebo;
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
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.client.event.InputEvent.Key;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

public class TrailsManager {
   static Map<UUID, PatreonUtils.PatreonParticleType> TRAILS = new HashMap<>();
   public static final KeyMapping TOGGLE = new KeyMapping("placebo.toggleTrails", 329, "key.categories.placebo");
   public static final Set<UUID> DISABLED = new HashSet<>();

   public static void init() {
      new Thread(() -> {
         Placebo.LOGGER.info("Loading patreon trails data...");

         try {
            URL url = new URI("https://raw.githubusercontent.com/Shadows-of-Fire/Placebo/1.16/PatreonTrails.txt").toURL();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
               String s;
               while ((s = reader.readLine()) != null) {
                  String[] split = s.split(" ", 2);
                  if (split.length != 2) {
                     Placebo.LOGGER.error("Invalid patreon trail entry {} will be ignored.", s);
                  } else {
                     TRAILS.put(UUID.fromString(split[0]), PatreonUtils.PatreonParticleType.valueOf(split[1]));
                  }
               }

               reader.close();
            } catch (IOException var6) {
               Placebo.LOGGER.error("Exception loading patreon trails data!");
               var6.printStackTrace();
            }
         } catch (Exception var7) {
         }

         Placebo.LOGGER.info("Loaded {} patreon trails.", TRAILS.size());
         if (TRAILS.size() > 0) {
            NeoForge.EVENT_BUS.register(TrailsManager.class);
         }
      }, "Placebo Patreon Trail Loader").start();
   }

   @SubscribeEvent
   public static void clientTick(Post e) {
      PatreonUtils.PatreonParticleType t = null;
      if (Minecraft.getInstance().level != null && !Minecraft.getInstance().isPaused()) {
         for (Player player : Minecraft.getInstance().level.players()) {
            if (!player.isInvisible() && player.tickCount * 3 % 2 == 0 && !DISABLED.contains(player.getUUID()) && (t = TRAILS.get(player.getUUID())) != null) {
               ClientLevel world = (ClientLevel)player.level();
               RandomSource rand = world.random;
               ParticleOptions type = t.type.get();
               world.addParticle(
                  type, player.getX() + rand.nextDouble() * 0.4 - 0.2, player.getY() + 0.1, player.getZ() + rand.nextDouble() * 0.4 - 0.2, 0.0, 0.0, 0.0
               );
            }
         }
      }
   }

   @SubscribeEvent
   public static void keys(Key e) {
      if (e.getAction() == 1 && TOGGLE.matches(e.getKey(), e.getScanCode()) && Minecraft.getInstance().getConnection() != null) {
         PacketDistributor.sendToServer(
            new PatreonDisablePayload(PatreonDisablePayload.CosmeticType.TRAILS, Minecraft.getInstance().player.getUUID()), new CustomPacketPayload[0]
         );
      }
   }
}

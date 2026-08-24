package vazkii.psi.common.core.handler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import vazkii.psi.api.cad.ICADColorizer;
import vazkii.psi.common.Psi;

@EventBusSubscriber(
   modid = "psi"
)
public final class ContributorSpellCircleHandler {
   private static volatile Map<String, int[]> colormap = Collections.emptyMap();
   private static boolean startedLoading = false;

   public static void load(Properties props) {
      Map<String, int[]> m = new HashMap<>();

      for (String key : props.stringPropertyNames()) {
         String value = props.getProperty(key).replace("#", "0x");

         try {
            int[] values = Stream.of(value.split(",")).mapToInt(el -> Integer.parseInt(el.substring(2), 16)).toArray();
            m.put(key, values);
         } catch (StringIndexOutOfBoundsException | NumberFormatException var6) {
            Psi.logger.error("Contributor {} has an invalid hexcode!", key);
         }
      }

      colormap = m;
   }

   public static void firstStart() {
      if (!startedLoading) {
         new ContributorSpellCircleHandler.ThreadContributorListLoader();
         startedLoading = true;
      }
   }

   public static int[] getColors(String name) {
      return colormap.getOrDefault(name, new int[]{-15481345});
   }

   public static boolean isContributor(String name) {
      return colormap.containsKey(name);
   }

   @SubscribeEvent
   public static void craftColorizer(ItemCraftedEvent event) {
      if (isContributor(event.getEntity().getName().getString().toLowerCase(Locale.ROOT)) && event.getCrafting().getItem() instanceof ICADColorizer) {
         ((ICADColorizer)event.getCrafting().getItem()).setContributorName(event.getCrafting(), event.getEntity().getName().getString());
      }
   }

   private static class ThreadContributorListLoader extends Thread {
      public ThreadContributorListLoader() {
         this.setName("Psi Contributor Spell Circle Loader Thread");
         this.setDaemon(true);
         this.setUncaughtExceptionHandler((thread, err) -> Psi.logger.error("Caught off-thread exception from {}: ", thread.getName(), err));
         this.start();
      }

      @Override
      public void run() {
         try {
            URL url = new URI("https://raw.githubusercontent.com/VazkiiMods/Psi/master/contributors.properties").toURL();
            Properties props = new Properties();

            try (InputStreamReader reader = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)) {
               props.load(reader);
               ContributorSpellCircleHandler.load(props);
            }
         } catch (IOException var8) {
            Psi.logger.info("Could not load contributors list. Either you're offline or github is down. Nothing to worry about, carry on~");
         } catch (URISyntaxException var9) {
            throw new RuntimeException(var9);
         }
      }
   }
}

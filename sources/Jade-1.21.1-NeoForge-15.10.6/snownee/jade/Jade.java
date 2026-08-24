package snownee.jade;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.impl.WailaClientRegistration;
import snownee.jade.impl.WailaCommonRegistration;
import snownee.jade.impl.config.PluginConfig;
import snownee.jade.impl.config.WailaConfig;
import snownee.jade.test.ExamplePlugin;
import snownee.jade.util.CommonProxy;
import snownee.jade.util.JsonConfig;

public class Jade {
   public static final String ID = "jade";
   public static final Logger LOGGER = LogUtils.getLogger();
   public static final JsonConfig<WailaConfig> CONFIG = new JsonConfig<>("jade/jade", WailaConfig.CODEC, null);
   public static boolean FROZEN;

   public static void loadComplete() {
      if (!FROZEN) {
         FROZEN = true;
         if (CommonProxy.isDevEnv()) {
            try {
               IWailaPlugin plugin = new ExamplePlugin();
               plugin.register(WailaCommonRegistration.instance());
               if (CommonProxy.isPhysicallyClient()) {
                  plugin.registerClient(WailaClientRegistration.instance());
               }
            } catch (Throwable var1) {
            }
         }

         WailaCommonRegistration.instance().priorities.sort(PluginConfig.INSTANCE.getKeys());
         WailaCommonRegistration.instance().loadComplete();
         if (CommonProxy.isPhysicallyClient()) {
            WailaClientRegistration.instance().loadComplete();
            WailaConfig.ConfigGeneral.init();
         }

         PluginConfig.INSTANCE.reload();
      }
   }
}

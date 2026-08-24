package malte0811.ferritecore.mixin.config;

import java.io.IOException;
import java.util.List;

public interface IPlatformConfigHooks {
   static IPlatformConfigHooks loadHooks() {
      try {
         Class<?> handler = Class.forName("malte0811.ferritecore.mixin.platform.ConfigFileHandler");
         return (IPlatformConfigHooks)handler.getConstructor().newInstance();
      } catch (Exception var1) {
         throw new RuntimeException(var1);
      }
   }

   void readAndUpdateConfig(List<FerriteConfig.Option> var1) throws IOException;

   void collectDisabledOverrides(IPlatformConfigHooks.OverrideCallback var1);

   public interface OverrideCallback {
      void addOverride(String var1, String var2);
   }
}

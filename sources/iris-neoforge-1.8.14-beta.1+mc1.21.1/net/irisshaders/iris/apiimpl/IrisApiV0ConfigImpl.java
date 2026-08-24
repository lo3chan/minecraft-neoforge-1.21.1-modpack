package net.irisshaders.iris.apiimpl;

import java.io.IOException;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.api.v0.IrisApiConfig;
import net.irisshaders.iris.config.IrisConfig;

public class IrisApiV0ConfigImpl implements IrisApiConfig {
   @Override
   public boolean areShadersEnabled() {
      return Iris.getIrisConfig().areShadersEnabled();
   }

   @Override
   public void setShadersEnabledAndApply(boolean enabled) {
      IrisConfig config = Iris.getIrisConfig();
      config.setShadersEnabled(enabled);

      try {
         config.save();
      } catch (IOException var5) {
         Iris.logger.error("Error saving configuration file!", var5);
      }

      try {
         Iris.reload();
      } catch (IOException var4) {
         Iris.logger.error("Error reloading shader pack while applying changes!", var4);
      }
   }
}

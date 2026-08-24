package dev.corgitaco.enhancedcelestials2core.mixin;

import dev.corgitaco.enhancedcelestials2core.config.EC2MixinConfig;
import dev.corgitaco.enhancedcelestials2core.core.EC2Constants;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class EnhancedCelestialsMixinPlugin implements IMixinConfigPlugin {
   public static final String MIXIN_PACKAGE = "dev.corgitaco.enhancedcelestials2core.mixin";
   private final EC2MixinConfig mixinConfig = new EC2MixinConfig();

   public void onLoad(String mixinPackage) {
      this.mixinConfig.onLoad();
   }

   public String getRefMapperConfig() {
      return null;
   }

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      if (mixinClassName.equalsIgnoreCase("dev.corgitaco.enhancedcelestials2core.mixin.TimeCommandMixin")) {
         boolean timeCommandMixin = Boolean.parseBoolean(this.mixinConfig.getProperties().getProperty("TimeCommandMixin"));
         if (timeCommandMixin) {
            EC2Constants.LOGGER.info("TimeCommandMixin is enabled, overwriting vanilla time command behavior.");
         }

         return timeCommandMixin;
      } else {
         return true;
      }
   }

   public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
   }

   public List<String> getMixins() {
      return null;
   }

   public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }

   public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }
}

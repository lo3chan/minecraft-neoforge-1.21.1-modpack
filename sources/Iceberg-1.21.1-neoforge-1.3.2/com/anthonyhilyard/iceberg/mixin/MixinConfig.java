package com.anthonyhilyard.iceberg.mixin;

import com.anthonyhilyard.iceberg.services.Services;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class MixinConfig implements IMixinConfigPlugin {
   private static String packageName = MixinConfig.class.getPackage().getName();

   public void onLoad(String mixinPackage) {
   }

   public String getRefMapperConfig() {
      return null;
   }

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      String subPackage = mixinClassName.substring(packageName.length() + 1);
      int dotIndex = subPackage.indexOf(46);
      if (dotIndex > 0) {
         String modId = subPackage.substring(0, dotIndex);
         return Services.getPlatformHelper().isModLoaded(modId);
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

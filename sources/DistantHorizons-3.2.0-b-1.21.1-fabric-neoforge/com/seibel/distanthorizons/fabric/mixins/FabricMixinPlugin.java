package com.seibel.distanthorizons.fabric.mixins;

import com.seibel.distanthorizons.common.commonMixins.AbstractDhMixinPlugin;
import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class FabricMixinPlugin extends AbstractDhMixinPlugin implements IMixinConfigPlugin {
   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      if (mixinClassName.contains(".mods.")) {
         String cleanedMixinName = mixinClassName.replaceAll("^.*mods.", "").replaceAll("\\..*$", "");
         return FabricLoader.getInstance().isModLoaded(cleanedMixinName);
      } else {
         return this.shouldApplyDhMixin(targetClassName, mixinClassName);
      }
   }

   public void onLoad(String mixinPackage) {
   }

   public String getRefMapperConfig() {
      return null;
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

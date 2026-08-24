package com.aetherteam.aether.mixin;

import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class AetherMixinPlugin implements IMixinConfigPlugin {
   private boolean isOptiFineInstalled = false;

   public void onLoad(String mixinPackage) {
      try {
         Class.forName("optifine.Installer", false, this.getClass().getClassLoader());
         this.isOptiFineInstalled = true;
      } catch (ClassNotFoundException var3) {
      }
   }

   public String getRefMapperConfig() {
      return null;
   }

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      if (this.isOptiFineInstalled) {
         if (mixinClassName.equals("com.aetherteam.aether.mixin.mixins.client.BossHealthOverlayMixin")) {
            return false;
         }

         if (mixinClassName.equals("com.aetherteam.aether.mixin.mixins.client.optifine.BossHealthOverlayMixin")) {
            return true;
         }
      }

      return !mixinClassName.equals("com.aetherteam.aether.mixin.mixins.client.optifine.BossHealthOverlayMixin");
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

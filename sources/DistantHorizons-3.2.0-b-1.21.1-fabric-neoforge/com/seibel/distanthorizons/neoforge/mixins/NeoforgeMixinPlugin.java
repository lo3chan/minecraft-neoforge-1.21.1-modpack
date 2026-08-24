package com.seibel.distanthorizons.neoforge.mixins;

import com.seibel.distanthorizons.common.commonMixins.AbstractDhMixinPlugin;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import java.util.List;
import java.util.Set;
import net.neoforged.fml.ModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class NeoforgeMixinPlugin extends AbstractDhMixinPlugin implements IMixinConfigPlugin {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private boolean firstRun = false;
   private boolean isNeoforgeMixinFile;

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      if (!this.firstRun) {
         try {
            Class<?> cls = Class.forName("net.neoforged.fml.common.Mod");
            this.isNeoforgeMixinFile = true;
         } catch (ClassNotFoundException var4) {
            this.isNeoforgeMixinFile = false;
         }
      }

      if (!this.isNeoforgeMixinFile) {
         return false;
      } else {
         return mixinClassName.contains(".mods.")
            ? ModList.get().isLoaded(mixinClassName.replaceAll("^.*mods.", "").replaceAll("\\..*$", ""))
            : this.shouldApplyDhMixin(targetClassName, mixinClassName);
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

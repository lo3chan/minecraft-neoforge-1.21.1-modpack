package dev.latvian.mods.kubejs;

import java.util.List;
import java.util.Set;
import net.neoforged.fml.loading.LoadingModList;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class KubeJSMixinPlugin implements IMixinConfigPlugin {
   public void onLoad(String mixinPackage) {
   }

   public String getRefMapperConfig() {
      return null;
   }

   public boolean shouldApplyMixin(@NotNull String targetClassName, @NotNull String mixinClassName) {
      if (targetClassName.contains("mezz/modnametooltip")) {
         return LoadingModList.get().getModFileById("modnametooltip") != null;
      } else {
         return targetClassName.contains("me/shedaniel/rei") ? LoadingModList.get().getModFileById("roughlyenoughitems") != null : true;
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

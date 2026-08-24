package fuzs.puzzleslib.neoforge.mixin;

import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class MixinConfigPluginNeoForgeImpl implements IMixinConfigPlugin {
   private static final Collection<String> DEVELOPMENT_MIXINS = Set.of("AbstractPackResourcesNeoForgeMixin", "DatagenModLoaderNeoForgeMixin");

   public void onLoad(String mixinPackage) {
   }

   public String getRefMapperConfig() {
      return null;
   }

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      return ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironment("puzzleslib")
         || !DEVELOPMENT_MIXINS.contains(mixinClassName.replaceAll(".+\\.mixin\\.", ""));
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

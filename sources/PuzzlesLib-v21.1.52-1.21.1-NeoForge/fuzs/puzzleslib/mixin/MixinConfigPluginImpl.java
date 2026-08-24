package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class MixinConfigPluginImpl implements IMixinConfigPlugin {
   private static final Collection<String> DEVELOPMENT_MIXINS = Set.of(
      "DataCommandsMixin",
      "EnchantCommandMixin",
      "client.ClientSuggestionProviderMixin",
      "client.EditBoxMixin",
      "server.DedicatedServerSettingsMixin",
      "server.EulaMixin"
   );

   public void onLoad(String mixinPackage) {
      try {
         Class.forName("fuzs.puzzleslib.api.core.v1.ServiceProviderHelper");
      } catch (ClassNotFoundException var3) {
         throw new RuntimeException(var3);
      }
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

package traben.entity_texture_features.mixin;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

public class Plugin implements IMixinConfigPlugin {
   private static final Logger log = LoggerFactory.getLogger(Plugin.class);

   public void onLoad(String mixinPackage) {
      MixinExtrasBootstrap.init();
   }

   public String getRefMapperConfig() {
      return null;
   }

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      return mixinClassName.endsWith("MixinModelPartSodium")
         ? this.hasClass("me.jellysquid.mods.sodium.client.render.immediate.model.EntityRenderer")
         : !targetClassName.equals("traben.entity_texture_features.mixin.CancelTarget");
   }

   private boolean hasClass(String className) {
      try {
         MixinService.getService().getBytecodeProvider().getClassNode(className);
         return true;
      } catch (IOException | ClassNotFoundException var3) {
         return false;
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

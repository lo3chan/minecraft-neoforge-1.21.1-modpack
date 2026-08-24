package net.bettercombat.mixin;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class BetterCombatMixinPlugin implements IMixinConfigPlugin {
   private Supplier<Boolean> playerAnimatorPresent = () -> {
      boolean result;
      try {
         Class.forName("dev.kosmx.playerAnim.api.layered.IAnimation").getName();
         result = true;
      } catch (ClassNotFoundException var3) {
         result = false;
      }

      boolean finalResult = result;
      this.playerAnimatorPresent = () -> finalResult;
      return result;
   };

   public void onLoad(String mixinPackage) {
   }

   public String getRefMapperConfig() {
      return null;
   }

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      return this.playerAnimatorPresent.get();
   }

   public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
   }

   public List<String> getMixins() {
      return null;
   }

   public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }

   public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }
}

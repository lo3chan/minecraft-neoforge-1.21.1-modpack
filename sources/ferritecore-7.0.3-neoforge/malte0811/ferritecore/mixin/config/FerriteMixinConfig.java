package malte0811.ferritecore.mixin.config;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import malte0811.ferritecore.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

public abstract class FerriteMixinConfig implements IMixinConfigPlugin {
   protected static final Logger LOGGER = LogManager.getLogger("ferritecore-mixin");
   private static final boolean HAS_LITHIUM = hasClass("net.caffeinemc.mods.lithium.common.LithiumMod");
   private String prefix = null;
   private final FerriteConfig.Option enableOption;
   private final FerriteMixinConfig.LithiumSupportState lithiumState;
   private final boolean optIn;

   protected FerriteMixinConfig(FerriteConfig.Option enableOption, FerriteMixinConfig.LithiumSupportState lithiumCompat, boolean optIn) {
      this.enableOption = enableOption;
      this.lithiumState = lithiumCompat;
      this.optIn = optIn;
   }

   protected FerriteMixinConfig(FerriteConfig.Option enableOption) {
      this(enableOption, FerriteMixinConfig.LithiumSupportState.NO_CONFLICT, false);
   }

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      Preconditions.checkState(mixinClassName.startsWith(this.prefix), "Unexpected prefix on " + mixinClassName);
      if (!this.enableOption.isEnabled()) {
         if (!this.optIn) {
            LOGGER.warn("Mixin " + mixinClassName + " is disabled by config");
         }

         return false;
      } else if (!this.lithiumState.shouldApply()) {
         LOGGER.warn("Mixin " + mixinClassName + " is disabled automatically as lithium is installed");
         return false;
      } else {
         if (this.optIn) {
            LOGGER.warn("Opt-in mixin {} is enabled by config", mixinClassName);
         }

         return true;
      }
   }

   public void onLoad(String mixinPackage) {
      this.prefix = mixinPackage + ".";
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
      if (mixinClassName.equals("malte0811.ferritecore.mixin.fastmap.FastMapStateHolderMixin")) {
         this.replaceStateHolderValuesType(targetClass);
      }
   }

   private void replaceStateHolderValuesType(ClassNode targetClass) {
      String oldType = "it/unimi/dsi/fastutil/objects/Reference2ObjectArrayMap";
      String newType = "it/unimi/dsi/fastutil/objects/Reference2ObjectMap";
      String fieldNameToReplace = Constants.PLATFORM_HOOKS.computeStateHolderValuesName();
      FieldNode valuesFieldNode = this.getFieldNode(targetClass, fieldNameToReplace);
      valuesFieldNode.desc = valuesFieldNode.desc
         .replace("it/unimi/dsi/fastutil/objects/Reference2ObjectArrayMap", "it/unimi/dsi/fastutil/objects/Reference2ObjectMap");
      if (valuesFieldNode.signature != null) {
         valuesFieldNode.signature = valuesFieldNode.signature
            .replace("it/unimi/dsi/fastutil/objects/Reference2ObjectArrayMap", "it/unimi/dsi/fastutil/objects/Reference2ObjectMap");
      }

      for (MethodNode method : targetClass.methods) {
         for (AbstractInsnNode insn : method.instructions) {
            if (insn instanceof FieldInsnNode fieldInsn && fieldInsn.name.equals(fieldNameToReplace)) {
               fieldInsn.desc = fieldInsn.desc
                  .replace("it/unimi/dsi/fastutil/objects/Reference2ObjectArrayMap", "it/unimi/dsi/fastutil/objects/Reference2ObjectMap");
            } else if (insn.getOpcode() == 182 && insn instanceof MethodInsnNode call) {
               if (call.owner.contains("it/unimi/dsi/fastutil/objects/Reference2ObjectArrayMap")) {
                  call.owner = call.owner
                     .replace("it/unimi/dsi/fastutil/objects/Reference2ObjectArrayMap", "it/unimi/dsi/fastutil/objects/Reference2ObjectMap");
                  call.setOpcode(185);
                  call.itf = true;
               }
            } else if (insn.getOpcode() == 192 && insn instanceof TypeInsnNode cast) {
               cast.desc = cast.desc.replace("it/unimi/dsi/fastutil/objects/Reference2ObjectArrayMap", "it/unimi/dsi/fastutil/objects/Reference2ObjectMap");
            }
         }
      }
   }

   private FieldNode getFieldNode(ClassNode clazz, String fieldName) {
      for (FieldNode field : clazz.fields) {
         if (field.name.equals(fieldName)) {
            return field;
         }
      }

      String fields = clazz.fields.stream().map(n -> n.name).reduce((s1, s2) -> s1 + ", " + s2).orElse("[None]");
      throw new RuntimeException("Failed to find field with name " + fieldName + " in " + clazz.name + ", available fields are " + fields);
   }

   private static boolean hasClass(String name) {
      try {
         MixinService.getService().getBytecodeProvider().getClassNode(name);
         return true;
      } catch (IOException | ClassNotFoundException var2) {
         return false;
      }
   }

   protected static enum LithiumSupportState {
      NO_CONFLICT,
      INCOMPATIBLE;

      private boolean shouldApply() {
         return switch (this) {
            case NO_CONFLICT -> true;
            case INCOMPATIBLE -> !FerriteMixinConfig.HAS_LITHIUM;
         };
      }
   }
}

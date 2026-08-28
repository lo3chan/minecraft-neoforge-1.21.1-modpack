/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.tree.ClassNode
 *  org.slf4j.Logger
 *  org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
 *  org.spongepowered.asm.mixin.extensibility.IMixinInfo
 */
package net.caffeinemc.caffeineconfig;

import java.util.List;
import java.util.Set;
import net.caffeinemc.caffeineconfig.CaffeineConfig;
import net.caffeinemc.caffeineconfig.Option;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public abstract class AbstractCaffeineConfigMixinPlugin
implements IMixinConfigPlugin {
    private CaffeineConfig config;

    public void onLoad(String mixinPackage) {
        this.config = this.createConfig();
        this.logger().info("Loaded configuration file for {}: {} options available, {} override(s) found", new Object[]{this.config.getModName(), this.config.getOptionCount(), this.config.getOptionOverrideCount()});
    }

    protected abstract CaffeineConfig createConfig();

    protected abstract String mixinPackageRoot();

    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!mixinClassName.startsWith(this.mixinPackageRoot())) {
            throw new IllegalStateException(String.format("Expected mixin '%s' to start with package root '%s'!", mixinClassName, this.mixinPackageRoot()));
        }
        String mixin = mixinClassName.substring(this.mixinPackageRoot().length());
        Option option = this.config.getEffectiveOptionForMixin(mixin);
        if (option == null) {
            throw new IllegalStateException(String.format("No options matched mixin '%s'! Mixins in this config must be under a registered option name", mixin));
        }
        if (option.isOverridden()) {
            Object source = "[unknown]";
            if (option.isUserDefined()) {
                source = "user configuration";
            } else if (option.isModDefined()) {
                source = "mods [" + String.join((CharSequence)", ", option.getDefiningMods()) + "]";
            }
            if (option.isEnabled()) {
                this.logger().warn("Force-enabling mixin '{}' as option '{}' (added by {}) enables it", new Object[]{mixin, option.getName(), source});
            } else {
                this.logger().warn("Force-disabling mixin '{}' as option '{}' (added by {}) disables it and children", new Object[]{mixin, option.getName(), source});
            }
        }
        return option.isEnabled();
    }

    private Logger logger() {
        return this.config.getLogger();
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


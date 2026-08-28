/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.irisshaders.iris.helpers.StringPair
 *  net.irisshaders.iris.shaderpack.preprocessor.JcppProcessor
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.diebuddies.mixins.iris;

import net.diebuddies.compat.Iris;
import net.diebuddies.util.ShaderFixes;
import net.irisshaders.iris.helpers.StringPair;
import net.irisshaders.iris.shaderpack.preprocessor.JcppProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value={JcppProcessor.class})
public class MixinJcppProcessor {
    @Inject(at={@At(value="RETURN")}, method={"glslPreprocessSource"}, remap=false, cancellable=true)
    private static void glslPreprocessSource(String source, Iterable<StringPair> environmentDefines, CallbackInfoReturnable<String> info) {
        String shaderSource = (String)info.getReturnValue();
        if (shaderSource != null && Iris.injectIntoEntityOrShadowShader.get().booleanValue()) {
            shaderSource = ShaderFixes.applyFixes(shaderSource);
            info.setReturnValue((Object)shaderSource);
        }
    }
}


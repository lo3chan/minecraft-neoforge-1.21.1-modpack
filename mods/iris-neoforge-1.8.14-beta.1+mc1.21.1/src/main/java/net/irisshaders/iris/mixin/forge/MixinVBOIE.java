/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.ShaderInstance
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.Shadow
 */
package net.irisshaders.iris.mixin.forge;

import net.irisshaders.iris.Iris;
import net.irisshaders.iris.pipeline.programs.FallbackShader;
import net.irisshaders.iris.pipeline.programs.ShaderAccess;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(targets={"blusunrize/immersiveengineering/client/utils/IEGLShaders"}, remap=false)
public class MixinVBOIE {
    @Shadow
    private static ShaderInstance vboShader;

    @Overwrite
    public static ShaderInstance getVboShader() {
        if (!Iris.isPackInUseQuick()) {
            return vboShader;
        }
        ShaderInstance shader = ShaderAccess.getIEVBOShader();
        if (shader == null || shader instanceof FallbackShader) {
            return vboShader;
        }
        return shader;
    }
}


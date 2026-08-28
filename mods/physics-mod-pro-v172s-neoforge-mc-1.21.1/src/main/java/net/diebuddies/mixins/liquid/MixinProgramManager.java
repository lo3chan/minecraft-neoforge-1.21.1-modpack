/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.shaders.ProgramManager
 *  com.mojang.blaze3d.shaders.Shader
 *  org.lwjgl.opengl.GL32C
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.liquid;

import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.shaders.Shader;
import net.diebuddies.compat.Iris;
import net.diebuddies.opengl.Data;
import net.diebuddies.physics.StarterClient;
import org.lwjgl.opengl.GL32C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ProgramManager.class})
public class MixinProgramManager {
    @Inject(at={@At(value="HEAD")}, method={"linkShader"})
    private static void physicsmod$changeLiquidAttributes(Shader shader, CallbackInfo info) {
        if (StarterClient.iris && Iris.compilingLiquidShadowShader.get().booleanValue()) {
            GL32C.glBindAttribLocation((int)shader.getId(), (int)Data.LIQUID_POS.getAttribute(), (CharSequence)"physics_offset");
            GL32C.glBindAttribLocation((int)shader.getId(), (int)Data.LIQUID_POS_NEW.getAttribute(), (CharSequence)"physics_offsetNew");
            System.out.println("binding wonky attributes");
        }
    }
}


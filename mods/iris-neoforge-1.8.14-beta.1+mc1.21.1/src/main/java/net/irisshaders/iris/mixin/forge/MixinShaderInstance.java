/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceProvider
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin.forge;

import com.mojang.blaze3d.vertex.VertexFormat;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.compat.SkipList;
import net.irisshaders.iris.mixinterface.ShaderInstanceInterface;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ShaderInstance.class})
public abstract class MixinShaderInstance
implements ShaderInstanceInterface {
    @Inject(method={"<init>(Lnet/minecraft/server/packs/resources/ResourceProvider;Lnet/minecraft/resources/ResourceLocation;Lcom/mojang/blaze3d/vertex/VertexFormat;)V"}, require=1, at={@At(value="INVOKE", target="Lnet/minecraft/util/GsonHelper;parse(Ljava/io/Reader;)Lcom/google/gson/JsonObject;")})
    public void iris$setupGeometryShader(ResourceProvider resourceProvider, ResourceLocation shaderLocation, VertexFormat p_173338_, CallbackInfo ci) {
        try {
            this.iris$createExtraShaders(resourceProvider, shaderLocation.getPath());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Inject(method={"<init>(Lnet/minecraft/server/packs/resources/ResourceProvider;Lnet/minecraft/resources/ResourceLocation;Lcom/mojang/blaze3d/vertex/VertexFormat;)V"}, at={@At(value="TAIL")}, require=0)
    private void iriss$storeSkipNeo(ResourceProvider resourceProvider, ResourceLocation string, VertexFormat vertexFormat, CallbackInfo ci) {
        MethodHandle shouldSkip = SkipList.shouldSkipList.computeIfAbsent(this.getClass(), x -> {
            try {
                MethodHandle iris$skipDraw = MethodHandles.lookup().findVirtual((Class<?>)x, "iris$skipDraw", MethodType.methodType(Boolean.TYPE));
                Iris.logger.warn("Class " + x.getName() + " has opted out of being rendered with shaders.");
                return iris$skipDraw;
            }
            catch (IllegalAccessException | NoSuchMethodException e) {
                return SkipList.NONE;
            }
        });
        if (Iris.getIrisConfig().shouldSkip(string)) {
            shouldSkip = SkipList.ALWAYS;
        }
        this.setShouldSkip(shouldSkip);
    }
}


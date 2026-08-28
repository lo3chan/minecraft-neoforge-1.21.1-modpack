/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.atlas.SpriteSource$Output
 *  net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceManager
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyArgs
 *  org.spongepowered.asm.mixin.injection.invoke.arg.Args
 */
package net.irisshaders.iris.mixin.texture.pbr;

import java.util.function.BiConsumer;
import net.irisshaders.iris.pbr.texture.PBRType;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value={DirectoryLister.class})
public class MixinDirectoryLister {
    @ModifyArgs(method={"run(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/client/renderer/texture/atlas/SpriteSource$Output;)V"}, at=@At(value="INVOKE", target="Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V", remap=false, ordinal=0))
    private void iris$modifyForEachAction(Args args, ResourceManager resourceManager, SpriteSource.Output output) {
        BiConsumer action = (BiConsumer)args.get(0);
        BiConsumer<ResourceLocation, Resource> wrappedAction = (location, resource) -> {
            ResourceLocation baseLocation;
            String basePath = PBRType.removeSuffix(location.getPath());
            if (basePath != null && resourceManager.getResource(baseLocation = location.withPath(basePath)).isPresent()) {
                return;
            }
            action.accept(location, resource);
        };
        args.set(0, wrappedAction);
    }
}


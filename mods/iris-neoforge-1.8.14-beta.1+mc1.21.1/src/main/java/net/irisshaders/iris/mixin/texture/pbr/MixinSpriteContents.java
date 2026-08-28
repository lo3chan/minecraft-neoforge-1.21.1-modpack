/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.api.texture.SpriteUtil
 *  net.minecraft.client.renderer.texture.SpriteContents
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  org.spongepowered.asm.mixin.Dynamic
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin.texture.pbr;

import net.caffeinemc.mods.sodium.api.texture.SpriteUtil;
import net.irisshaders.iris.pbr.texture.PBRSpriteHolder;
import net.irisshaders.iris.pbr.texture.SpriteContentsExtension;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={SpriteContents.class})
public class MixinSpriteContents
implements SpriteContentsExtension {
    @Unique
    private PBRSpriteHolder pbrHolder;

    @Inject(method={"close()V"}, at={@At(value="TAIL")}, remap=false)
    private void iris$onTailClose(CallbackInfo ci) {
        if (this.pbrHolder != null) {
            this.pbrHolder.close();
        }
    }

    @Inject(method={"sodium$setActive(Z)V"}, at={@At(value="TAIL")}, remap=false, require=0)
    @Dynamic(value="Added by Sodium")
    private void iris$onTailMarkActive(CallbackInfo ci) {
        PBRSpriteHolder pbrHolder = this.getPBRHolder();
        if (pbrHolder != null) {
            TextureAtlasSprite normalSprite = pbrHolder.getNormalSprite();
            TextureAtlasSprite specularSprite = pbrHolder.getSpecularSprite();
            if (normalSprite != null) {
                SpriteUtil.INSTANCE.markSpriteActive(normalSprite);
            }
            if (specularSprite != null) {
                SpriteUtil.INSTANCE.markSpriteActive(specularSprite);
            }
        }
    }

    @Override
    public PBRSpriteHolder getPBRHolder() {
        return this.pbrHolder;
    }

    @Override
    public PBRSpriteHolder getOrCreatePBRHolder() {
        if (this.pbrHolder == null) {
            this.pbrHolder = new PBRSpriteHolder();
        }
        return this.pbrHolder;
    }
}


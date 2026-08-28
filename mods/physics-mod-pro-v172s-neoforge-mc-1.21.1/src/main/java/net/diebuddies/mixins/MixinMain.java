/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.main.Main
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins;

import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Main.class})
public class MixinMain {
    @Inject(at={@At(value="HEAD")}, method={"main"}, remap=false)
    private static void main(String[] args, CallbackInfo info) {
        System.setProperty("joml.fastmath", "true");
        System.setProperty("joml.sinLookup", "true");
        System.setProperty("iris.prettyPrintShaders", "true");
    }
}


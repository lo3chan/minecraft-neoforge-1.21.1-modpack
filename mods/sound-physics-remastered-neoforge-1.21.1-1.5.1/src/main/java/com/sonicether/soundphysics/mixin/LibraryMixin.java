/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.audio.Library
 *  org.lwjgl.system.MemoryStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyArgs
 *  org.spongepowered.asm.mixin.injection.invoke.arg.Args
 */
package com.sonicether.soundphysics.mixin;

import com.mojang.blaze3d.audio.Library;
import java.nio.IntBuffer;
import java.util.Arrays;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value={Library.class})
public class LibraryMixin {
    @ModifyArgs(method={"init"}, at=@At(value="INVOKE", target="Lorg/lwjgl/openal/ALC10;alcCreateContext(JLjava/nio/IntBuffer;)J"))
    private void modifyContext(Args args) {
        int[] original = this.toArray(((IntBuffer)args.get(1)).duplicate());
        try (MemoryStack stack = MemoryStack.stackPush();){
            IntBuffer buffer = stack.mallocInt(original.length + 3);
            buffer.put(original, 0, original.length - 1);
            buffer.put(131075).put(4).put(0);
            args.set(1, (Object)buffer.put(0).flip());
        }
    }

    @Unique
    private int[] toArray(IntBuffer buffer) {
        if (buffer.hasArray()) {
            if (buffer.arrayOffset() == 0) {
                return buffer.array();
            }
            return Arrays.copyOfRange(buffer.array(), buffer.arrayOffset(), buffer.array().length);
        }
        buffer.rewind();
        int[] arr = new int[buffer.remaining()];
        buffer.get(arr);
        return arr;
    }
}


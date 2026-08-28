/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.server.packs.resources.ResourceProvider
 */
package net.diebuddies.render.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;
import net.diebuddies.render.shader.ShaderResourceProvider;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;

public class TransparentPhysicsShader
extends ShaderInstance {
    public TransparentPhysicsShader() throws IOException {
        super((ResourceProvider)new ShaderResourceProvider(), "transparent_physics", DefaultVertexFormat.NEW_ENTITY);
    }
}


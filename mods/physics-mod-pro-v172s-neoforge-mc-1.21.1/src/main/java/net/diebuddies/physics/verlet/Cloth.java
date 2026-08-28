/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 */
package net.diebuddies.physics.verlet;

import javax.annotation.Nullable;
import net.diebuddies.model.ColladaMesh;
import net.diebuddies.opengl.Texture;
import net.diebuddies.opengl.VAO;
import net.diebuddies.physics.verlet.ClothRules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class Cloth
implements Comparable<Cloth> {
    public final String name;
    public final ColladaMesh mesh;
    private final Texture texture;
    public final ClothRules rules;
    public final VAO vao;
    public final VAO vaoFlatShaded;
    public final ColladaMesh playerMesh;
    public final VAO playerVAO;

    public Cloth(String name, ColladaMesh mesh, @Nullable ColladaMesh playerMesh, Texture texture, ClothRules rules) {
        this.name = name;
        this.mesh = mesh;
        this.texture = texture;
        this.rules = rules;
        this.vao = mesh.createVAO(false);
        this.vaoFlatShaded = mesh.createVAO(true);
        this.playerMesh = playerMesh;
        this.playerVAO = playerMesh != null ? playerMesh.createVAO(true) : null;
    }

    public int getTexture(@Nullable Entity entity) {
        String specialTexture = this.rules.getSpecialTexture();
        if (specialTexture != null) {
            if (specialTexture.equals("minecraft:playerskin")) {
                if (entity != null && entity instanceof AbstractClientPlayer) {
                    AbstractClientPlayer player = (AbstractClientPlayer)entity;
                    return Minecraft.getInstance().getTextureManager().getTexture(player.getSkin().texture()).getId();
                }
                return -1;
            }
            return Minecraft.getInstance().getTextureManager().getTexture(ResourceLocation.parse((String)specialTexture)).getId();
        }
        return this.texture.getID();
    }

    public void destroy() {
        if (this.texture != null) {
            this.texture.destroy();
        }
        if (this.vao != null) {
            this.vao.destroy();
        }
        if (this.vaoFlatShaded != null) {
            this.vaoFlatShaded.destroy();
        }
        if (this.playerVAO != null) {
            this.playerVAO.destroy();
        }
    }

    @Override
    public int compareTo(Cloth o) {
        return this.name.compareTo(o.name);
    }
}


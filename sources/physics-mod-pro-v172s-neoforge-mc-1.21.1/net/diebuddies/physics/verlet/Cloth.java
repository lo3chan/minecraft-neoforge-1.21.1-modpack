package net.diebuddies.physics.verlet;

import javax.annotation.Nullable;
import net.diebuddies.model.ColladaMesh;
import net.diebuddies.opengl.Texture;
import net.diebuddies.opengl.VAO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class Cloth implements Comparable<Cloth> {
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
      if (playerMesh != null) {
         this.playerVAO = playerMesh.createVAO(true);
      } else {
         this.playerVAO = null;
      }
   }

   public int getTexture(@Nullable Entity entity) {
      String specialTexture = this.rules.getSpecialTexture();
      if (specialTexture != null) {
         if (specialTexture.equals("minecraft:playerskin")) {
            return entity != null && entity instanceof AbstractClientPlayer player
               ? Minecraft.getInstance().getTextureManager().getTexture(player.getSkin().texture()).getId()
               : -1;
         } else {
            return Minecraft.getInstance().getTextureManager().getTexture(ResourceLocation.parse(specialTexture)).getId();
         }
      } else {
         return this.texture.getID();
      }
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

   public int compareTo(Cloth o) {
      return this.name.compareTo(o.name);
   }
}

package net.diebuddies.render;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import net.diebuddies.compat.Iris;
import net.diebuddies.compat.Optifine;
import net.diebuddies.opengl.StateTracker;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.render.shader.TransparentPhysicsShader;
import net.diebuddies.util.IRigidBodyHolder;
import net.diebuddies.util.PerformanceTracker;
import net.diebuddies.util.SimplePoolBodyHolder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4fStack;
import org.joml.Vector3d;

public class TransparencyRenderer {
   private MainRenderer mainRenderer;
   private List<IRigidBodyHolder> sortedTransparentBodies = new ObjectArrayList();
   private SimplePoolBodyHolder poolBodyHolder = new SimplePoolBodyHolder(200);
   public static TransparentPhysicsShader transparentPhysicsShader;

   public TransparencyRenderer(MainRenderer mainRenderer) {
      this.mainRenderer = mainRenderer;
   }

   public void render(PhysicsWorld physics, ClientLevel level, Matrix4fStack matrixStackIn, Vec3 view) {
      if (this.sortedTransparentBodies.size() > 0) {
         PerformanceTracker.startNoFlush("transparent_blocks_mobs_particles_rendering");
         this.mainRenderer.lastTextureMatrix = null;
         boolean useCustomShader = true;
         if (StarterClient.iris && Iris.isExtending()) {
            useCustomShader = false;
         }

         if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
            useCustomShader = false;
         }

         if (useCustomShader) {
            if (transparentPhysicsShader == null) {
               try {
                  transparentPhysicsShader = new TransparentPhysicsShader();
               } catch (IOException var17) {
                  var17.printStackTrace();
               }
            }

            RenderSystem.setShader(() -> transparentPhysicsShader);
         } else {
            RenderSystem.setShader(GameRenderer::getRendertypeEntitySolidShader);
            if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
               Optifine.useEntityShader();
            }
         }

         this.mainRenderer.resetColor();
         this.mainRenderer.setupShader(RenderSystem.getShader());
         Collections.sort(this.sortedTransparentBodies);
         Vector3d physicsOffset = physics.getOffset();
         double offsetX = physicsOffset.x - view.x;
         double offsetY = physicsOffset.y - view.y;
         double offsetZ = physicsOffset.z - view.z;
         int size = this.sortedTransparentBodies.size();
         physics.bindForRendering();

         for (int i = 0; i < size; i++) {
            IRigidBody body = this.sortedTransparentBodies.get(i).body;
            PhysicsEntity entity = body.getEntity();
            this.mainRenderer.render(physics, level, matrixStackIn, view, offsetX, offsetY, offsetZ, body, entity, true);
         }

         StateTracker.unbindVertexArray();
         PerformanceTracker.end("transparent_blocks_mobs_particles_rendering");
      }

      this.sortedTransparentBodies.clear();
      this.poolBodyHolder.reset();
   }

   public void addTransparentObject(IRigidBody body, double lengthSquared) {
      this.sortedTransparentBodies.add(this.poolBodyHolder.get(body, lengthSquared));
   }

   public static void destroy() {
      if (transparentPhysicsShader != null) {
         transparentPhysicsShader.close();
      }
   }
}

package traben.entity_model_features.models.parts;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.model.geom.ModelPart.Polygon;
import net.minecraft.client.model.geom.ModelPart.Vertex;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import traben.entity_model_features.EMF;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.mixin.mixins.accessor.CuboidAccessor;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.EMFAttachments;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.jem_objects.EMFBoxData;
import traben.entity_model_features.models.jem_objects.EMFPartData;
import traben.entity_model_features.utils.EMFUtils;

public class EMFModelPartCustom extends EMFModelPart {
   public final String partToBeAttached;
   public final String id;
   public final boolean attach;
   private final float defaultScale;
   @Nullable
   private final List<EMFAttachments> attachments;

   public EMFModelPartCustom(EMFPartData emfPartData, int variant, @Nullable String part, String id, EMFModelPartRoot root) {
      super(getCuboidsFromData(emfPartData), getChildrenFromData(emfPartData, variant, root), root);
      this.attach = emfPartData.attach;
      this.partToBeAttached = part;
      this.id = id;
      this.textureOverride = emfPartData.getCustomTexture();
      List<EMFAttachments> attachments = emfPartData.getAttachments();
      this.attachments = attachments.isEmpty() ? null : attachments;
      this.defaultScale = emfPartData.scale;
      this.xScale = this.defaultScale;
      this.yScale = this.defaultScale;
      this.zScale = this.defaultScale;
      this.x = emfPartData.translate[0];
      this.y = emfPartData.translate[1];
      this.z = emfPartData.translate[2];
      this.xRot = emfPartData.rotate[0];
      this.yRot = emfPartData.rotate[1];
      this.zRot = emfPartData.rotate[2];
      this.setInitialPose(this.storePose());
      if (((EMFConfig)EMF.config().getConfig()).logModelCreationData) {
         EMFUtils.log(" > > EMF custom part made: " + emfPartData.id);
      }
   }

   public void resetPose() {
      super.resetPose();
      this.xScale = this.defaultScale;
      this.yScale = this.defaultScale;
      this.zScale = this.defaultScale;
   }

   private static List<Cube> getCuboidsFromData(EMFPartData emfPartData) {
      return createCuboidsFromBoxDataV3(emfPartData);
   }

   private static Map<String, ModelPart> getChildrenFromData(EMFPartData emfPartData, int variant, EMFModelPartRoot root) {
      Map<String, ModelPart> emfChildren = new HashMap<>();

      for (EMFPartData sub : emfPartData.submodels) {
         String idUnique = EMFUtils.getIdUnique(emfChildren.keySet(), sub.id);
         emfChildren.put(idUnique, new EMFModelPartCustom(sub, variant, null, idUnique, root));
      }

      return emfChildren;
   }

   private static List<Cube> createCuboidsFromBoxDataV3(EMFPartData emfPartData) {
      List<Cube> emfCuboids = new LinkedList<>();
      if (emfPartData.boxes.length > 0) {
         try {
            for (EMFBoxData box : emfPartData.boxes) {
               Cube cube;
               if (box.textureOffset.length == 2) {
                  cube = new EMFModelPartCustom.EMFCube(
                     emfPartData,
                     box.textureOffset[0],
                     box.textureOffset[1],
                     box.coordinates[0],
                     box.coordinates[1],
                     box.coordinates[2],
                     box.coordinates[3],
                     box.coordinates[4],
                     box.coordinates[5],
                     box.sizeAddX,
                     box.sizeAddY,
                     box.sizeAddZ,
                     emfPartData.textureSize[0],
                     emfPartData.textureSize[1],
                     emfPartData.mirrorTexture.contains("u"),
                     emfPartData.mirrorTexture.contains("v")
                  );
               } else {
                  cube = new EMFModelPartCustom.EMFCube(
                     emfPartData,
                     box.uvDown,
                     box.uvUp,
                     box.uvNorth,
                     box.uvSouth,
                     box.uvWest,
                     box.uvEast,
                     box.coordinates[0],
                     box.coordinates[1],
                     box.coordinates[2],
                     box.coordinates[3],
                     box.coordinates[4],
                     box.coordinates[5],
                     box.sizeAddX,
                     box.sizeAddY,
                     box.sizeAddZ,
                     emfPartData.textureSize[0],
                     emfPartData.textureSize[1],
                     false,
                     false
                  );
               }

               emfCuboids.add(cube);
            }
         } catch (Exception var7) {
            EMFUtils.log("cuboid construction broke: " + var7, false);
         }
      }

      return emfCuboids;
   }

   @Override
   public String toString() {
      return "[custom part " + this.id + "], cubes =" + this.cubes.size() + ", children = " + this.children.size();
   }

   @Override
   public String toStringShort() {
      return "[custom part " + this.id.replaceFirst("EMF_", "") + "]";
   }

   @Override
   public void processArmItemOverrides(PoseStack matrices) {
      if (this.attachments != null) {
         for (EMFAttachments attachment : this.attachments) {
            matrices.pushPose();
            this.translateAndRotate(matrices);
            attachment.setAttachment(matrices);
            matrices.popPose();
            EMFEntityRenderState state = EMFAnimationEntityContext.getEmfState();
            if (state != null) {
               if (attachment.right) {
                  state.setRightArmOverride(attachment);
               } else {
                  state.setLeftArmOverride(attachment);
               }
            }
         }
      } else {
         super.processArmItemOverrides(matrices);
      }
   }

   @Override
   public void render(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int k) {
      this.processArmItemOverrides(matrices);
      super.render(matrices, vertices, light, overlay, k);
   }

   @Override
   protected float[] debugBoxColor() {
      return new float[]{1.0F, 1.0F, 1.0F};
   }

   @Override
   void renderWithTextureOverride(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int k) {
      if (this.textureOverride == null || !EMFAnimationEntityContext.isLayerPhase() || !this.getRoot().isMainModel) {
         super.renderWithTextureOverride(matrices, vertices, light, overlay, k);
      }
   }

   static class EMFCube extends Cube {
      EMFCube(
         EMFPartData selfModelData,
         float textureU,
         float textureV,
         float cubeX,
         float cubeY,
         float cubeZ,
         float sizeX,
         float sizeY,
         float sizeZ,
         float extraX,
         float extraY,
         float extraZ,
         float textureWidth,
         float textureHeight,
         boolean mirrorU,
         boolean mirrorV
      ) throws Exception {
         super(
            (int)textureU,
            (int)textureV,
            cubeX,
            cubeY,
            cubeZ,
            sizeX,
            sizeY,
            sizeZ,
            extraX,
            extraY,
            extraZ,
            false,
            textureWidth,
            textureHeight,
            new HashSet<Direction>() {
               {
                  this.addAll(List.of(Direction.values()));
               }
            }
         );
         CuboidAccessor accessor = (CuboidAccessor)this;
         accessor.setMinX(cubeX);
         accessor.setMinY(cubeY);
         accessor.setMinZ(cubeZ);
         accessor.setMaxX(cubeX + sizeX);
         accessor.setMaxY(cubeY + sizeY);
         accessor.setMaxZ(cubeZ + sizeZ);
         float cubeX2 = cubeX + sizeX;
         float cubeY2 = cubeY + sizeY;
         float cubeZ2 = cubeZ + sizeZ;
         cubeX -= extraX;
         cubeY -= extraY;
         cubeZ -= extraZ;
         cubeX2 += extraX;
         cubeY2 += extraY;
         cubeZ2 += extraZ;
         Vertex vertex = new Vertex(cubeX, cubeY, cubeZ, 0.0F, 0.0F);
         Vertex vertex2 = new Vertex(cubeX2, cubeY, cubeZ, 0.0F, 8.0F);
         Vertex vertex3 = new Vertex(cubeX2, cubeY2, cubeZ, 8.0F, 8.0F);
         Vertex vertex4 = new Vertex(cubeX, cubeY2, cubeZ, 8.0F, 0.0F);
         Vertex vertex5 = new Vertex(cubeX, cubeY, cubeZ2, 0.0F, 0.0F);
         Vertex vertex6 = new Vertex(cubeX2, cubeY, cubeZ2, 0.0F, 8.0F);
         Vertex vertex7 = new Vertex(cubeX2, cubeY2, cubeZ2, 8.0F, 8.0F);
         Vertex vertex8 = new Vertex(cubeX, cubeY2, cubeZ2, 8.0F, 0.0F);
         ArrayList<Polygon> sides = new ArrayList<>();
         float j = textureU;
         float k = textureU + sizeZ;
         float l = textureU + sizeZ + sizeX;
         float m = textureU + sizeZ + sizeX + sizeX;
         float n = textureU + sizeZ + sizeX + sizeZ;
         float o = textureU + sizeZ + sizeX + sizeZ + sizeX;
         float p = textureV;
         float q = textureV + sizeZ;
         float r = textureV + sizeZ + sizeY;
         boolean printing = ((EMFConfig)EMF.config().getConfig()).logModelCreationData;

         try {
            sides.add(
               new Polygon(
                  mirrorV ? new Vertex[]{vertex3, vertex4, vertex8, vertex7} : new Vertex[]{vertex6, vertex5, vertex, vertex2},
                  mirrorU ? l : k,
                  mirrorV ? q : p,
                  mirrorU ? k : l,
                  mirrorV ? p : q,
                  textureWidth,
                  textureHeight,
                  false,
                  mirrorV ? Direction.UP : Direction.DOWN
               )
            );
         } catch (Exception var46) {
            if (printing) {
               EMFUtils.log("uv-dwn failed for " + selfModelData.id);
            }

            throw new Exception("uv-dwn failed for " + selfModelData.id);
         }

         try {
            sides.add(
               new Polygon(
                  mirrorV ? new Vertex[]{vertex6, vertex5, vertex, vertex2} : new Vertex[]{vertex3, vertex4, vertex8, vertex7},
                  mirrorU ? m : l,
                  mirrorV ? p : q,
                  mirrorU ? l : m,
                  mirrorV ? q : p,
                  textureWidth,
                  textureHeight,
                  false,
                  mirrorV ? Direction.DOWN : Direction.UP
               )
            );
         } catch (Exception var45) {
            if (printing) {
               EMFUtils.log("uv-up failed for " + selfModelData.id);
            }

            throw new Exception("uv-up failed for " + selfModelData.id);
         }

         try {
            sides.add(
               new Polygon(
                  mirrorU ? new Vertex[]{vertex6, vertex2, vertex3, vertex7} : new Vertex[]{vertex, vertex5, vertex8, vertex4},
                  mirrorU ? k : j,
                  mirrorV ? r : q,
                  mirrorU ? j : k,
                  mirrorV ? q : r,
                  textureWidth,
                  textureHeight,
                  false,
                  mirrorU ? Direction.EAST : Direction.WEST
               )
            );
         } catch (Exception var44) {
            if (printing) {
               EMFUtils.log("uv-west failed for " + selfModelData.id);
            }

            throw new Exception("uv-west failed for " + selfModelData.id);
         }

         try {
            sides.add(
               new Polygon(
                  new Vertex[]{vertex2, vertex, vertex4, vertex3},
                  mirrorU ? l : k,
                  mirrorV ? r : q,
                  mirrorU ? k : l,
                  mirrorV ? q : r,
                  textureWidth,
                  textureHeight,
                  false,
                  Direction.NORTH
               )
            );
         } catch (Exception var43) {
            if (printing) {
               EMFUtils.log("uv-nrth failed for " + selfModelData.id);
            }

            throw new Exception("uv-nrth failed for " + selfModelData.id);
         }

         try {
            sides.add(
               new Polygon(
                  mirrorU ? new Vertex[]{vertex, vertex5, vertex8, vertex4} : new Vertex[]{vertex6, vertex2, vertex3, vertex7},
                  mirrorU ? n : l,
                  mirrorV ? r : q,
                  mirrorU ? l : n,
                  mirrorV ? q : r,
                  textureWidth,
                  textureHeight,
                  false,
                  mirrorU ? Direction.WEST : Direction.EAST
               )
            );
         } catch (Exception var42) {
            if (printing) {
               EMFUtils.log("uv-east failed for " + selfModelData.id);
            }

            throw new Exception("uv-east failed for " + selfModelData.id);
         }

         try {
            sides.add(
               new Polygon(
                  new Vertex[]{vertex5, vertex6, vertex7, vertex8},
                  mirrorU ? o : n,
                  mirrorV ? r : q,
                  mirrorU ? n : o,
                  mirrorV ? q : r,
                  textureWidth,
                  textureHeight,
                  false,
                  Direction.SOUTH
               )
            );
         } catch (Exception var41) {
            if (printing) {
               EMFUtils.log("uv-sth failed for " + selfModelData.id);
            }

            throw new Exception("uv-sth failed for " + selfModelData.id);
         }

         accessor.setPolygons(sides.toArray(new Polygon[0]));
      }

      EMFCube(
         EMFPartData selfModelData,
         float[] uvDown,
         float[] uvUp,
         float[] uvNorth,
         float[] uvSouth,
         float[] uvWest,
         float[] uvEast,
         float cubeX,
         float cubeY,
         float cubeZ,
         float sizeX,
         float sizeY,
         float sizeZ,
         float extraX,
         float extraY,
         float extraZ,
         float textureWidth,
         float textureHeight,
         boolean mirrorU,
         boolean mirrorV
      ) {
         super(0, 0, cubeX, cubeY, cubeZ, sizeX, sizeY, sizeZ, extraX, extraY, extraZ, false, textureWidth, textureHeight, new HashSet<Direction>() {
            {
               this.addAll(List.of(Direction.values()));
            }
         });
         CuboidAccessor accessor = (CuboidAccessor)this;
         accessor.setMinX(cubeX);
         accessor.setMinY(cubeY);
         accessor.setMinZ(cubeZ);
         accessor.setMaxX(cubeX + sizeX);
         accessor.setMaxY(cubeY + sizeY);
         accessor.setMaxZ(cubeZ + sizeZ);
         float cubeX2 = cubeX + sizeX;
         float cubeY2 = cubeY + sizeY;
         float cubeZ2 = cubeZ + sizeZ;
         cubeX -= extraX;
         cubeY -= extraY;
         cubeZ -= extraZ;
         cubeX2 += extraX;
         cubeY2 += extraY;
         cubeZ2 += extraZ;
         Vertex vertex = new Vertex(cubeX, cubeY, cubeZ, 0.0F, 0.0F);
         Vertex vertex2 = new Vertex(cubeX2, cubeY, cubeZ, 0.0F, 8.0F);
         Vertex vertex3 = new Vertex(cubeX2, cubeY2, cubeZ, 8.0F, 8.0F);
         Vertex vertex4 = new Vertex(cubeX, cubeY2, cubeZ, 8.0F, 0.0F);
         Vertex vertex5 = new Vertex(cubeX, cubeY, cubeZ2, 0.0F, 0.0F);
         Vertex vertex6 = new Vertex(cubeX2, cubeY, cubeZ2, 0.0F, 8.0F);
         Vertex vertex7 = new Vertex(cubeX2, cubeY2, cubeZ2, 8.0F, 8.0F);
         Vertex vertex8 = new Vertex(cubeX, cubeY2, cubeZ2, 8.0F, 0.0F);
         ArrayList<Polygon> sides = new ArrayList<>();
         boolean printing = ((EMFConfig)EMF.config().getConfig()).logModelCreationData;

         try {
            sides.add(
               new Polygon(
                  mirrorV ? new Vertex[]{vertex8, vertex7, vertex3, vertex4} : new Vertex[]{vertex, vertex2, vertex6, vertex5},
                  mirrorU ? uvUp[2] : uvUp[0],
                  mirrorV ? uvUp[3] : uvUp[1],
                  mirrorU ? uvUp[0] : uvUp[2],
                  mirrorV ? uvUp[1] : uvUp[3],
                  textureWidth,
                  textureHeight,
                  false,
                  mirrorV ? Direction.UP : Direction.DOWN
               )
            );
         } catch (Exception var41) {
            if (printing) {
               EMFUtils.log("uv-up failed for " + selfModelData.id);
            }
         }

         try {
            sides.add(
               new Polygon(
                  mirrorV ? new Vertex[]{vertex, vertex2, vertex6, vertex5} : new Vertex[]{vertex8, vertex7, vertex3, vertex4},
                  mirrorU ? uvDown[2] : uvDown[0],
                  mirrorV ? uvDown[3] : uvDown[1],
                  mirrorU ? uvDown[0] : uvDown[2],
                  mirrorV ? uvDown[1] : uvDown[3],
                  textureWidth,
                  textureHeight,
                  false,
                  mirrorV ? Direction.DOWN : Direction.UP
               )
            );
         } catch (Exception var40) {
            if (printing) {
               EMFUtils.log("uv-down failed for " + selfModelData.id);
            }
         }

         try {
            sides.add(
               new Polygon(
                  mirrorU ? new Vertex[]{vertex, vertex5, vertex8, vertex4} : new Vertex[]{vertex6, vertex2, vertex3, vertex7},
                  mirrorU ? uvWest[2] : uvWest[0],
                  mirrorV ? uvWest[3] : uvWest[1],
                  mirrorU ? uvWest[0] : uvWest[2],
                  mirrorV ? uvWest[1] : uvWest[3],
                  textureWidth,
                  textureHeight,
                  false,
                  mirrorU ? Direction.WEST : Direction.EAST
               )
            );
         } catch (Exception var39) {
            if (printing) {
               EMFUtils.log("uv-west failed for " + selfModelData.id);
            }
         }

         try {
            sides.add(
               new Polygon(
                  new Vertex[]{vertex2, vertex, vertex4, vertex3},
                  mirrorU ? uvNorth[2] : uvNorth[0],
                  mirrorV ? uvNorth[3] : uvNorth[1],
                  mirrorU ? uvNorth[0] : uvNorth[2],
                  mirrorV ? uvNorth[1] : uvNorth[3],
                  textureWidth,
                  textureHeight,
                  false,
                  Direction.NORTH
               )
            );
         } catch (Exception var38) {
            if (printing) {
               EMFUtils.log("uv-north failed for " + selfModelData.id);
            }
         }

         try {
            sides.add(
               new Polygon(
                  mirrorU ? new Vertex[]{vertex6, vertex2, vertex3, vertex7} : new Vertex[]{vertex, vertex5, vertex8, vertex4},
                  mirrorU ? uvEast[2] : uvEast[0],
                  mirrorV ? uvEast[3] : uvEast[1],
                  mirrorU ? uvEast[0] : uvEast[2],
                  mirrorV ? uvEast[1] : uvEast[3],
                  textureWidth,
                  textureHeight,
                  false,
                  mirrorU ? Direction.EAST : Direction.WEST
               )
            );
         } catch (Exception var37) {
            if (printing) {
               EMFUtils.log("uv-east failed for " + selfModelData.id);
            }
         }

         try {
            sides.add(
               new Polygon(
                  new Vertex[]{vertex5, vertex6, vertex7, vertex8},
                  mirrorU ? uvSouth[2] : uvSouth[0],
                  mirrorV ? uvSouth[3] : uvSouth[1],
                  mirrorU ? uvSouth[0] : uvSouth[2],
                  mirrorV ? uvSouth[1] : uvSouth[3],
                  textureWidth,
                  textureHeight,
                  false,
                  Direction.SOUTH
               )
            );
         } catch (Exception var36) {
            if (printing) {
               EMFUtils.log("uv-south failed for " + selfModelData.id);
            }
         }

         accessor.setPolygons(sides.toArray(new Polygon[0]));
      }

      public void compile(Pose pose, VertexConsumer vertexConsumer, int i, int j, int k) {
         Matrix4f matrix4f = pose.pose();
         Vector3f vector3f = new Vector3f();

         for (Polygon polygon : this.polygons) {
            Vector3f vector3f2 = pose.transformNormal(polygon.normal, vector3f);
            float f = vector3f2.x();
            float g = vector3f2.y();
            float h = vector3f2.z();
            Vertex[] var16 = polygon.vertices;

            for (Vertex vertex : var16) {
               float l = vertex.pos.x() / 16.0F;
               float m = vertex.pos.y() / 16.0F;
               float n = vertex.pos.z() / 16.0F;
               Vector3f vector3f3 = matrix4f.transformPosition(l, m, n, vector3f);
               vertexConsumer.addVertex(vector3f3.x(), vector3f3.y(), vector3f3.z(), k, vertex.u, vertex.v, j, i, f, g, h);
            }
         }
      }
   }
}

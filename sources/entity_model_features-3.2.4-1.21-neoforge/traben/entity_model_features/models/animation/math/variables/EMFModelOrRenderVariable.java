package traben.entity_model_features.models.animation.math.variables;

import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.math.expression_tree.MathValue;
import traben.entity_model_features.models.parts.EMFModelPart;

public enum EMFModelOrRenderVariable {
   TX {
      @Override
      public void setValue(EMFModelPart modelPart, float value) {
         if (modelPart != null) {
            modelPart.x = value;
         }
      }

      @Override
      public float getValue(ModelPart modelPart) {
         return modelPart == null ? 0.0F : modelPart.x;
      }
   },
   TY {
      @Override
      public void setValue(EMFModelPart modelPart, float value) {
         if (modelPart != null) {
            modelPart.y = value;
         }
      }

      @Override
      public float getValue(ModelPart modelPart) {
         return modelPart == null ? 0.0F : modelPart.y;
      }
   },
   TZ {
      @Override
      public void setValue(EMFModelPart modelPart, float value) {
         if (modelPart != null) {
            modelPart.z = value;
         }
      }

      @Override
      public float getValue(ModelPart modelPart) {
         return modelPart == null ? 0.0F : modelPart.z;
      }
   },
   RX {
      @Override
      public void setValue(EMFModelPart modelPart, float value) {
         if (modelPart != null) {
            modelPart.xRot = value;
         }
      }

      @Override
      public float getValue(ModelPart modelPart) {
         return modelPart == null ? 0.0F : modelPart.xRot;
      }
   },
   RY {
      @Override
      public void setValue(EMFModelPart modelPart, float value) {
         if (modelPart != null) {
            modelPart.yRot = value;
         }
      }

      @Override
      public float getValue(ModelPart modelPart) {
         return modelPart == null ? 0.0F : modelPart.yRot;
      }
   },
   RZ {
      @Override
      public void setValue(EMFModelPart modelPart, float value) {
         if (modelPart != null) {
            modelPart.zRot = value;
         }
      }

      @Override
      public float getValue(ModelPart modelPart) {
         return modelPart == null ? 0.0F : modelPart.zRot;
      }
   },
   SX {
      @Override
      public void setValue(EMFModelPart modelPart, float value) {
         if (modelPart != null) {
            modelPart.xScale = value;
         }
      }

      @Override
      public float getValue(ModelPart modelPart) {
         return modelPart == null ? 0.0F : modelPart.xScale;
      }
   },
   SY {
      @Override
      public void setValue(EMFModelPart modelPart, float value) {
         if (modelPart != null) {
            modelPart.yScale = value;
         }
      }

      @Override
      public float getValue(ModelPart modelPart) {
         return modelPart == null ? 0.0F : modelPart.yScale;
      }
   },
   SZ {
      @Override
      public void setValue(EMFModelPart modelPart, float value) {
         if (modelPart != null) {
            modelPart.zScale = value;
         }
      }

      @Override
      public float getValue(ModelPart modelPart) {
         return modelPart == null ? 0.0F : modelPart.zScale;
      }
   },
   VISIBLE {
      @Override
      public void setValue(EMFModelPart modelPart, float value) {
         if (modelPart != null) {
            modelPart.visible = MathValue.toBoolean(value);
         }
      }

      @Override
      public float getValue(ModelPart modelPart) {
         return modelPart == null ? 0.0F : MathValue.fromBoolean(modelPart.visible);
      }

      @Override
      public boolean isBoolean() {
         return true;
      }
   },
   VISIBLE_BOXES {
      @Override
      public void setValue(EMFModelPart modelPart, float value) {
         if (modelPart != null) {
            modelPart.skipDraw = !MathValue.toBoolean(value);
         }
      }

      @Override
      public float getValue(ModelPart modelPart) {
         return modelPart == null ? 0.0F : MathValue.fromBoolean(!modelPart.skipDraw);
      }

      @Override
      public boolean isBoolean() {
         return true;
      }
   },
   RENDER_shadow_size {
      @Override
      public void setValue(EMFModelPart ignored, float value) {
         EMFAnimationEntityContext.setShadowSize(value);
      }

      @Override
      public float getValue(ModelPart ignored) {
         return EMFAnimationEntityContext.getShadowSize();
      }

      @Override
      public boolean isRenderVariable() {
         return true;
      }
   },
   RENDER_SHADOW_OPACITY {
      @Override
      public void setValue(EMFModelPart ignored, float value) {
         EMFAnimationEntityContext.setShadowOpacity(value);
      }

      @Override
      public float getValue(ModelPart ignored) {
         return EMFAnimationEntityContext.getShadowOpacity();
      }

      @Override
      public boolean isRenderVariable() {
         return true;
      }
   },
   RENDER_SHADOW_X {
      @Override
      public void setValue(EMFModelPart ignored, float value) {
         EMFAnimationEntityContext.setShadowX(value);
      }

      @Override
      public float getValue(ModelPart ignored) {
         return EMFAnimationEntityContext.getShadowX();
      }

      @Override
      public boolean isRenderVariable() {
         return true;
      }
   },
   RENDER_SHADOW_Z {
      @Override
      public void setValue(EMFModelPart ignored, float value) {
         EMFAnimationEntityContext.setShadowZ(value);
      }

      @Override
      public float getValue(ModelPart ignored) {
         return EMFAnimationEntityContext.getShadowZ();
      }

      @Override
      public boolean isRenderVariable() {
         return true;
      }
   },
   RENDER_LEASH_X {
      @Override
      public void setValue(EMFModelPart ignored, float value) {
         EMFAnimationEntityContext.setLeashX(value);
      }

      @Override
      public float getValue(ModelPart ignored) {
         return EMFAnimationEntityContext.getLeashX();
      }

      @Override
      public boolean isRenderVariable() {
         return true;
      }
   },
   RENDER_LEASH_Y {
      @Override
      public void setValue(EMFModelPart ignored, float value) {
         EMFAnimationEntityContext.setLeashY(value);
      }

      @Override
      public float getValue(ModelPart ignored) {
         return EMFAnimationEntityContext.getLeashY();
      }

      @Override
      public boolean isRenderVariable() {
         return true;
      }
   },
   RENDER_LEASH_Z {
      @Override
      public void setValue(EMFModelPart ignored, float value) {
         EMFAnimationEntityContext.setLeashZ(value);
      }

      @Override
      public float getValue(ModelPart ignored) {
         return EMFAnimationEntityContext.getLeashZ();
      }

      @Override
      public boolean isRenderVariable() {
         return true;
      }
   };

   @Nullable
   public static EMFModelOrRenderVariable getRenderVariable(String id) {
      if (id == null) {
         return null;
      } else {
         return switch (id) {
            case "render.shadow_size" -> RENDER_shadow_size;
            case "render.shadow_opacity" -> RENDER_SHADOW_OPACITY;
            case "render.shadow_offset_x" -> RENDER_SHADOW_X;
            case "render.shadow_offset_z" -> RENDER_SHADOW_Z;
            case "render.leash_offset_x" -> RENDER_LEASH_X;
            case "render.leash_offset_y" -> RENDER_LEASH_Y;
            case "render.leash_offset_z" -> RENDER_LEASH_Z;
            default -> null;
         };
      }
   }

   @Nullable
   public static EMFModelOrRenderVariable get(String id) {
      if (id == null) {
         return null;
      } else {
         return switch (id) {
            case "tx" -> TX;
            case "ty" -> TY;
            case "tz" -> TZ;
            case "rx" -> RX;
            case "ry" -> RY;
            case "rz" -> RZ;
            case "sx" -> SX;
            case "sy" -> SY;
            case "sz" -> SZ;
            case "visible" -> VISIBLE;
            case "visible_boxes" -> VISIBLE_BOXES;
            default -> null;
         };
      }
   }

   public boolean isRenderVariable() {
      return false;
   }

   public boolean isBoolean() {
      return false;
   }

   public abstract float getValue(ModelPart var1);

   public float getValue() {
      return this.getValue(null);
   }

   public abstract void setValue(EMFModelPart var1, float var2);
}

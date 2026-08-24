package traben.entity_model_features.models.jem_objects;

import java.util.Arrays;
import traben.entity_model_features.EMF;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.utils.EMFUtils;

public class EMFBoxData {
   public float[] textureOffset = new float[0];
   public float[] uvDown = new float[0];
   public float[] uvUp = new float[0];
   public float[] uvFront = new float[0];
   public float[] uvBack = new float[0];
   public float[] uvLeft = new float[0];
   public float[] uvRight = new float[0];
   public float[] uvNorth = new float[0];
   public float[] uvSouth = new float[0];
   public float[] uvWest = new float[0];
   public float[] uvEast = new float[0];
   public float[] coordinates = new float[0];
   public float sizeAdd = 0.0F;
   public float sizeAddX = 0.0F;
   public float sizeAddY = 0.0F;
   public float sizeAddZ = 0.0F;
   public float[] sizesAdd = new float[0];

   public void prepare(boolean invertX, boolean invertY, boolean invertZ) {
      try {
         if (this.sizeAdd != 0.0F && this.sizeAddX == 0.0F && this.sizeAddY == 0.0F && this.sizeAddZ == 0.0F) {
            this.sizeAddX = this.sizeAdd;
            this.sizeAddY = this.sizeAdd;
            this.sizeAddZ = this.sizeAdd;
         }

         if (this.sizesAdd.length == 3) {
            this.sizeAddX = this.sizesAdd[0];
            this.sizeAddY = this.sizesAdd[1];
            this.sizeAddZ = this.sizesAdd[2];
         }

         if (invertX) {
            this.coordinates[0] = -this.coordinates[0] - this.coordinates[3];
         }

         if (invertY) {
            this.coordinates[1] = -this.coordinates[1] - this.coordinates[4];
         }

         if (invertZ) {
            this.coordinates[2] = -this.coordinates[2] - this.coordinates[5];
         }

         boolean offsetValid = this.textureOffset.length == 2;
         if (!offsetValid && this.textureOffset.length != 0) {
            throw new IllegalArgumentException("Invalid textureOffset data: " + Arrays.toString(this.textureOffset));
         } else {
            if (!offsetValid) {
               this.checkAndFixUVLegacyDirections();
               this.validateUV(this.uvDown, "uvDown");
               this.validateUV(this.uvUp, "uvUp");
               this.validateUV(this.uvNorth, "uvNorth");
               this.validateUV(this.uvSouth, "uvSouth");
               this.validateUV(this.uvWest, "uvWest");
               this.validateUV(this.uvEast, "uvEast");
            }
         }
      } catch (Exception var5) {
         throw new IllegalArgumentException("Error preparing box data: " + var5.getMessage(), var5);
      }
   }

   private void validateUV(float[] uv, String name) {
      if (uv.length != 0) {
         if (uv.length != 4) {
            throw new IllegalArgumentException("Invalid UV data for [" + name + "], must have 4 or 0 values: " + Arrays.toString(uv));
         } else {
            if (((EMFConfig)EMF.config().getConfig()).logModelCreationData) {
               if (uv[2] == uv[0]) {
                  EMFUtils.logWarn(
                     "Possibly invalid UV data for ["
                        + name
                        + "], the U width should not be 0, its behaviour is extremely inconsistent between hardware: "
                        + Arrays.toString(uv)
                  );
               }

               if (uv[3] == uv[1]) {
                  EMFUtils.logWarn(
                     "Possibly invalid UV data for ["
                        + name
                        + "], the V height should not be 0, its behaviour is extremely inconsistent between hardware: "
                        + Arrays.toString(uv)
                  );
               }
            }
         }
      }
   }

   public void checkAndFixUVLegacyDirections() {
      if (this.uvFront.length == 4) {
         this.uvNorth = this.uvFront;
      }

      if (this.uvBack.length == 4) {
         this.uvWest = this.uvBack;
      }

      if (this.uvLeft.length == 4) {
         this.uvNorth = this.uvLeft;
      }

      if (this.uvRight.length == 4) {
         this.uvEast = this.uvRight;
      }
   }

   @Override
   public String toString() {
      return "EMF_BoxData{coordinates="
         + Arrays.toString(this.coordinates)
         + ", uvDown="
         + Arrays.toString(this.uvDown)
         + ", uvUp="
         + Arrays.toString(this.uvUp)
         + ", uvNorth="
         + Arrays.toString(this.uvNorth)
         + ", uvSouth="
         + Arrays.toString(this.uvSouth)
         + ", uvWest="
         + Arrays.toString(this.uvWest)
         + ", uvEast="
         + Arrays.toString(this.uvEast)
         + ", textureOffset="
         + Arrays.toString(this.textureOffset)
         + ", sizeAdd="
         + this.sizeAdd
         + "}";
   }
}

package traben.entity_model_features.models.jem_objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.animation.EMFAttachments;
import traben.entity_model_features.utils.EMFUtils;

public class EMFPartData {
   public String texture = "";
   public int[] textureSize = null;
   public String invertAxis = "";
   public float[] translate = null;
   public float[] rotate = null;
   public String mirrorTexture = "";
   public EMFBoxData[] boxes = new EMFBoxData[0];
   public EMFPartData submodel = null;
   public LinkedList<EMFPartData> submodels = new LinkedList<>();
   public String baseId = "";
   public String model = "";
   public String id = "";
   public String part = null;
   public boolean attach = false;
   public float scale = 1.0F;
   @Nullable
   transient String originalPart = null;
   public HashMap<String, float[]> attachments = new HashMap<>();
   public LinkedList<LinkedHashMap<String, String>> animations = null;
   private transient ResourceLocation customTexture = null;

   public ResourceLocation getCustomTexture() {
      return this.customTexture;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         EMFPartData partData = (EMFPartData)o;
         return this.attach == partData.attach
            && Float.compare(partData.scale, this.scale) == 0
            && Objects.equals(this.texture, partData.texture)
            && Arrays.equals(this.textureSize, partData.textureSize)
            && Objects.equals(this.invertAxis, partData.invertAxis)
            && Arrays.equals(this.translate, partData.translate)
            && Arrays.equals(this.rotate, partData.rotate)
            && Objects.equals(this.mirrorTexture, partData.mirrorTexture)
            && Arrays.equals((Object[])this.boxes, (Object[])partData.boxes)
            && Objects.equals(this.submodel, partData.submodel)
            && Objects.equals(this.submodels, partData.submodels)
            && Objects.equals(this.baseId, partData.baseId)
            && Objects.equals(this.model, partData.model)
            && Objects.equals(this.id, partData.id)
            && Objects.equals(this.part, partData.part)
            && Objects.equals(this.animations, partData.animations)
            && Objects.equals(this.customTexture, partData.customTexture);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = Objects.hash(
         this.texture,
         this.invertAxis,
         this.mirrorTexture,
         this.submodel,
         this.submodels,
         this.baseId,
         this.model,
         this.id,
         this.part,
         this.attach,
         this.scale,
         this.customTexture,
         this.animations
      );
      result = 31 * result + Arrays.hashCode(this.textureSize);
      result = 31 * result + Arrays.hashCode(this.translate);
      result = 31 * result + Arrays.hashCode(this.rotate);
      return 31 * result + Arrays.hashCode((Object[])this.boxes);
   }

   private void copyFrom(EMFPartData jpmModel) {
      if (((EMFConfig)EMF.config().getConfig()).logModelCreationData) {
         EMFUtils.log(">> copying from jpm into part: " + this.id);
      }

      if (this.submodels.isEmpty()) {
         this.submodels = jpmModel.submodels;
      }

      if (this.submodel == null) {
         this.submodel = jpmModel.submodel;
      }

      if (this.textureSize == null) {
         this.textureSize = jpmModel.textureSize;
      }

      if (this.texture.isBlank()) {
         this.texture = jpmModel.texture;
      }

      if (this.invertAxis.isBlank()) {
         this.invertAxis = jpmModel.invertAxis;
      }

      if (this.translate == null) {
         this.translate = jpmModel.translate;
      }

      if (this.rotate == null) {
         this.rotate = jpmModel.rotate;
      }

      if (this.mirrorTexture.isBlank()) {
         this.mirrorTexture = jpmModel.mirrorTexture;
      }

      if (this.boxes.length == 0) {
         this.boxes = jpmModel.boxes;
      }

      if (this.scale == 1.0F) {
         this.scale = jpmModel.scale;
      }

      if (this.animations == null || this.animations.isEmpty()) {
         this.animations = jpmModel.animations;
      }

      if (this.baseId.isBlank()) {
         this.baseId = jpmModel.baseId;
      }
   }

   public List<EMFAttachments> getAttachments() {
      ArrayList<EMFAttachments> list = new ArrayList<>();
      boolean invX = this.invertAxis.contains("x");
      boolean invY = this.invertAxis.contains("y");
      boolean invZ = this.invertAxis.contains("z");

      for (Entry<String, float[]> entry : this.attachments.entrySet()) {
         String s = entry.getKey();
         float[] floats = entry.getValue();
         if (floats != null && floats.length == 3) {
            try {
               switch (s) {
                  case "left_handheld_item":
                  case "right_handheld_item":
                     EMFAttachments e = new EMFAttachments(
                        floats[0] * (invX ? -1 : 1), floats[1] * (invY ? -1 : 1), floats[2] * (invZ ? -1 : 1), s.startsWith("right")
                     );
                     list.add(e);
                     break;
                  default:
                     throw new IllegalArgumentException("Unknown attachment point: " + s);
               }
            } catch (IllegalArgumentException var12) {
               EMFUtils.log("Unknown attachment point: " + s);
            }
         }
      }

      return list;
   }

   public void prepare(int[] textureSize, EMFJemData jem) {
      try {
         this.id = "EMF_" + (this.id.isBlank() ? this.hashCode() : this.id);
         if (!this.model.isEmpty()) {
            if (((EMFConfig)EMF.config().getConfig()).logModelCreationData) {
               EMFUtils.log("Loading model part from jpm: " + this.model + " into part: " + this.id);
            }

            ResourceLocation res = jem.validateResourcePathAndExists(this.model, "jpm");
            if (((EMFConfig)EMF.config().getConfig()).logModelCreationData) {
               EMFUtils.log(">> resolved to: " + res);
            }

            if (res != null) {
               Optional.ofNullable(EMFUtils.readModelPart(res)).ifPresent(this::copyFrom);
            }
         }

         if (!this.attachments.isEmpty() && (this.attachments.containsKey("left_handheld_item") || this.attachments.containsKey("right_handheld_item"))) {
            jem.hasAttachments = true;
         }

         if (this.translate == null) {
            this.translate = new float[]{0.0F, 0.0F, 0.0F};
         }

         if (this.rotate == null) {
            this.rotate = new float[]{0.0F, 0.0F, 0.0F};
         }

         if (this.textureSize == null || this.textureSize.length != 2) {
            this.textureSize = textureSize;
         }

         this.customTexture = jem.validateJemTexture(this.texture);
         boolean invX = this.invertAxis.contains("x");
         boolean invY = this.invertAxis.contains("y");
         boolean invZ = this.invertAxis.contains("z");
         this.translate[0] = invX ? -this.translate[0] : this.translate[0];
         this.translate[1] = invY ? -this.translate[1] : this.translate[1];
         this.translate[2] = invZ ? -this.translate[2] : this.translate[2];
         this.rotate[0] = (invX ? -this.rotate[0] : this.rotate[0]) * 0.017453292F;
         this.rotate[1] = (invY ? -this.rotate[1] : this.rotate[1]) * 0.017453292F;
         this.rotate[2] = (invZ ? -this.rotate[2] : this.rotate[2]) * 0.017453292F;

         for (EMFBoxData box : this.boxes) {
            box.prepare(invX, invY, invZ);
         }

         if (this.submodel != null) {
            this.submodel.prepare(this.textureSize, jem);
            if (!this.submodels.contains(this.submodel)) {
               this.submodels.add(this.submodel);
               this.submodel = null;
            }
         }

         for (EMFPartData model : this.submodels) {
            model.prepare(this.textureSize, jem);
         }
      } catch (Exception var10) {
         throw new IllegalArgumentException("Error preparing part data, for part [" + this.id + "]: " + var10.getMessage(), var10);
      }
   }

   @Override
   public String toString() {
      return "modelData{ id='"
         + this.id
         + "', part='"
         + this.part
         + "', rawpart='"
         + this.originalPart
         + "', submodels="
         + this.submodels.size()
         + "', anims="
         + (this.animations == null ? "0" : this.animations.size())
         + "}";
   }
}

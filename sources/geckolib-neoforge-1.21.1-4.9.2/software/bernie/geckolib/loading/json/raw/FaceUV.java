package software.bernie.geckolib.loading.json.raw;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.util.JsonUtil;

public record FaceUV(@Nullable String materialInstance, double[] uv, double[] uvSize, FaceUV.Rotation uvRotation) {
   public FaceUV(@Nullable String materialInstance, double[] uv, double[] uvSize) {
      this(materialInstance, uv, uvSize, FaceUV.Rotation.NONE);
   }

   public static JsonDeserializer<FaceUV> deserializer() throws JsonParseException {
      return (json, type, context) -> {
         JsonObject obj = json.getAsJsonObject();
         String materialInstance = GsonHelper.getAsString(obj, "material_instance", null);
         double[] uv = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "uv", null));
         double[] uvSize = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "uv_size", null));
         FaceUV.Rotation uvRotation = FaceUV.Rotation.fromValue(GsonHelper.getAsInt(obj, "uv_rotation", 0));
         return new FaceUV(materialInstance, uv, uvSize, uvRotation);
      };
   }

   public static enum Rotation {
      NONE,
      CLOCKWISE_90,
      CLOCKWISE_180,
      CLOCKWISE_270;

      public static FaceUV.Rotation fromValue(int value) throws JsonParseException {
         try {
            return values()[value % 360 / 90];
         } catch (Exception var2) {
            GeckoLibConstants.LOGGER.error("Invalid Face UV rotation: " + value);
            return fromValue(Mth.floor(Math.abs(value) / 90.0F) * 90);
         }
      }

      public float[] rotateUvs(float u, float v, float uWidth, float vHeight) {
         return switch (this) {
            case NONE -> new float[]{u, v, uWidth, v, uWidth, vHeight, u, vHeight};
            case CLOCKWISE_90 -> new float[]{uWidth, v, uWidth, vHeight, u, vHeight, u, v};
            case CLOCKWISE_180 -> new float[]{uWidth, vHeight, u, vHeight, u, v, uWidth, v};
            case CLOCKWISE_270 -> new float[]{u, vHeight, u, v, uWidth, v, uWidth, vHeight};
         };
      }
   }
}

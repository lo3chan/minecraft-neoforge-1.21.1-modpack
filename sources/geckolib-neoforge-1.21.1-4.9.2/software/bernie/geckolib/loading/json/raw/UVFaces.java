package software.bernie.geckolib.loading.json.raw;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

public record UVFaces(@Nullable FaceUV north, @Nullable FaceUV south, @Nullable FaceUV east, @Nullable FaceUV west, @Nullable FaceUV up, @Nullable FaceUV down) {
   public static JsonDeserializer<UVFaces> deserializer() {
      return (json, type, context) -> {
         JsonObject obj = json.getAsJsonObject();
         FaceUV north = (FaceUV)GsonHelper.getAsObject(obj, "north", null, context, FaceUV.class);
         FaceUV south = (FaceUV)GsonHelper.getAsObject(obj, "south", null, context, FaceUV.class);
         FaceUV east = (FaceUV)GsonHelper.getAsObject(obj, "east", null, context, FaceUV.class);
         FaceUV west = (FaceUV)GsonHelper.getAsObject(obj, "west", null, context, FaceUV.class);
         FaceUV up = (FaceUV)GsonHelper.getAsObject(obj, "up", null, context, FaceUV.class);
         FaceUV down = (FaceUV)GsonHelper.getAsObject(obj, "down", null, context, FaceUV.class);
         return new UVFaces(north, south, east, west, up, down);
      };
   }

   public FaceUV fromDirection(Direction direction) {
      return switch (direction) {
         case NORTH -> this.north;
         case SOUTH -> this.south;
         case EAST -> this.east;
         case WEST -> this.west;
         case UP -> this.up;
         case DOWN -> this.down;
         default -> throw new MatchException(null, null);
      };
   }
}

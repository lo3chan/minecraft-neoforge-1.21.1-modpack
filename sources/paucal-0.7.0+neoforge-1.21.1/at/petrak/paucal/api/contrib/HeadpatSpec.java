package at.petrak.paucal.api.contrib;

import at.petrak.paucal.xplat.common.msg.MsgHeadpatSoundS2C;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class HeadpatSpec {
   protected final String location;
   protected final HeadpatSpec.Type type;

   protected HeadpatSpec(String location) {
      this.location = location;
      if (location.contains(":") && ResourceLocation.tryParse(location) != null) {
         this.type = HeadpatSpec.Type.VANILLA;
      } else {
         this.type = HeadpatSpec.Type.GITHUB;
      }
   }

   public static List<HeadpatSpec> loadFromJson(JsonElement element) {
      if (element == null) {
         return List.of();
      } else if (GsonHelper.isStringValue(element)) {
         String loc = element.getAsString();
         HeadpatSpec single = new HeadpatSpec(loc);
         return List.of(single);
      } else if (element instanceof JsonArray arr) {
         ArrayList<HeadpatSpec> out = new ArrayList<>();

         for (JsonElement elt : arr) {
            if (!GsonHelper.isStringValue(elt)) {
               throw new RuntimeException("Invalid entry in the headpat spec, expected list of strings");
            }

            out.add(new HeadpatSpec(elt.getAsString()));
         }

         return out;
      } else {
         throw new RuntimeException("Invalid entry in the headpat spec, expected list of strings");
      }
   }

   public MsgHeadpatSoundS2C makePacket(Vec3 pos, float pitch, @Nullable Player patter) {
      Optional<UUID> optUuid = Optional.ofNullable(patter).map(Entity::getUUID);
      return new MsgHeadpatSoundS2C(this.location, this.type == HeadpatSpec.Type.GITHUB, pos, pitch, optUuid);
   }

   public static enum Type {
      VANILLA,
      GITHUB;
   }
}

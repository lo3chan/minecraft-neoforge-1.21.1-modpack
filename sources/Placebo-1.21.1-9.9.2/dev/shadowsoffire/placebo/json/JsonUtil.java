package dev.shadowsoffire.placebo.json;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.apache.logging.log4j.Logger;

public class JsonUtil {
   public static boolean checkAndLogEmpty(JsonElement e, ResourceLocation id, String type, Logger logger) {
      String s = e.toString();
      if (!s.isEmpty() && !"{}".equals(s)) {
         return true;
      } else {
         logger.error("Ignoring {} item with id {} as it is empty.  Please switch to a condition-false json instead of an empty one.", type, id);
         return false;
      }
   }

   public static boolean checkConditions(JsonElement e, ResourceLocation id, String type, Logger logger, ConditionalOps<JsonElement> ops) {
      if (ICondition.conditionsMatched(ops, e.getAsJsonObject())) {
         return true;
      } else {
         logger.trace("Skipping loading {} item with id {} as it's conditions were not met", type, id);
         return false;
      }
   }
}

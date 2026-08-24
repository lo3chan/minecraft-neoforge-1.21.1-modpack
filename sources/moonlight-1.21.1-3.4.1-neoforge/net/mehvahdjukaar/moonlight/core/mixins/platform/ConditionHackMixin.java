package net.mehvahdjukaar.moonlight.core.mixins.platform;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.mehvahdjukaar.moonlight.api.resources.recipe.platform.ResourceConditionsBridge;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.common.conditions.ICondition.IContext;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Deprecated(
   forRemoval = true
)
@Mixin({SimplePreparableReloadListener.class})
public abstract class ConditionHackMixin extends ContextAwareReloadListener {
   @Deprecated(
      forRemoval = true
   )
   @Inject(
      at = {@At("HEAD")},
      method = {"method_18790", "lambda$reload$1"}
   )
   private void applyResourceConditions(ResourceManager resourceManager, ProfilerFiller profiler, Object object, CallbackInfo ci) {
      if (this instanceof SimpleJsonResourceReloadListener) {
         Iterator<Entry<ResourceLocation, JsonElement>> it = ((Map)object).entrySet().iterator();
         IContext ops = this.getContext();

         while (it.hasNext()) {
            Entry<ResourceLocation, JsonElement> entry = it.next();
            JsonElement resourceData = entry.getValue();
            if (resourceData != null && resourceData.isJsonObject()) {
               JsonObject obj = resourceData.getAsJsonObject();
               if (!ResourceConditionsBridge.matchesForgeConditions(obj, ops, "fabric:load_conditions")) {
                  it.remove();
               }
            }
         }
      }
   }
}

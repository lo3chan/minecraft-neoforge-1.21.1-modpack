package net.bettercombat.client.compat;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.Platform;
import net.bettercombat.api.client.AttackRangeExtensions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class PehkuiHelper {
   public static ResourceLocation scaleId = ResourceLocation.fromNamespaceAndPath("pehkui", "entity_reach");
   private static final Method GET_SCALE_DATA;
   private static final Method GET_SCALE;
   private static final Map<ResourceLocation, Object> SCALE_TYPES;

   public static void load() {
   }

   public static float getScale(Entity entity) {
      return getScale(entity, scaleId, 1.0F);
   }

   public static float getScale(Entity entity, ResourceLocation scaleId, float tickDelta) {
      if (GET_SCALE_DATA != null && GET_SCALE != null && SCALE_TYPES != null) {
         try {
            return (Float)GET_SCALE.invoke(GET_SCALE_DATA.invoke(SCALE_TYPES.get(scaleId), entity), tickDelta);
         } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException var4) {
            return 1.0F;
         }
      } else {
         return 1.0F;
      }
   }

   static {
      Method getScaleDataMethod = null;
      Method getScaleMethod = null;
      Map<ResourceLocation, Object> scaleTypes = null;
      if (Platform.isModLoaded("pehkui")) {
         try {
            Class<?> scaleTypeClass = Class.forName("virtuoel.pehkui.api.ScaleType");
            Class<?> scaleDataClass = Class.forName("virtuoel.pehkui.api.ScaleData");
            Class<?> scaleRegistriesClass = Class.forName("virtuoel.pehkui.api.ScaleRegistries");
            Field scaleTypesField = scaleRegistriesClass.getField("SCALE_TYPES");
            getScaleDataMethod = scaleTypeClass.getMethod("getScaleData", Entity.class);
            getScaleMethod = scaleDataClass.getMethod("getScale", float.class);
            scaleTypes = (Map<ResourceLocation, Object>)scaleTypesField.get(null);
            AttackRangeExtensions.register(context -> {
               float multiplier = BetterCombatMod.config.getAttackRangeMultiplierForScale(getScale(context.player()));
               return new AttackRangeExtensions.Modifier(multiplier, AttackRangeExtensions.Operation.MULTIPLY);
            });
         } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException var7) {
            getScaleDataMethod = null;
            getScaleMethod = null;
            scaleTypes = null;
         }
      }

      GET_SCALE_DATA = getScaleDataMethod;
      GET_SCALE = getScaleMethod;
      SCALE_TYPES = scaleTypes;
   }
}

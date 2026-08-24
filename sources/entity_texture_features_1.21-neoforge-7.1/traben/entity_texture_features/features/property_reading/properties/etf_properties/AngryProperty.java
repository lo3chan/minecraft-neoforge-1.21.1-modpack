package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Properties;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.monster.Vindicator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public class AngryProperty extends BooleanProperty {
   protected AngryProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(getGenericBooleanThatCanNull(properties, propertyNum, new String[]{"angry", "isAngry", "is_angry", "aggressive", "is_aggressive"}));
   }

   public static AngryProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new AngryProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Nullable
   @Override
   protected Boolean getValueFromEntity(ETFEntityRenderState state) {
      if (state != null) {
         ETFEntity etfEntity = state.entity();
         if (etfEntity instanceof EnderMan enderman) {
            return enderman.isCreepy();
         }

         if (etfEntity instanceof Blaze blaze) {
            return blaze.isOnFire();
         }

         if (etfEntity instanceof Guardian guardian) {
            return guardian.getActiveAttackTarget() != null;
         }

         if (etfEntity instanceof Vindicator vindicator) {
            return vindicator.isAggressive();
         }

         if (etfEntity instanceof SpellcasterIllager caster) {
            return caster.isCastingSpell();
         }

         if (etfEntity instanceof NeutralMob angry) {
            return angry.isAngry();
         }
      }

      return null;
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"angry", "isAngry", "is_angry", "aggressive", "is_aggressive"};
   }
}

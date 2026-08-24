package traben.entity_texture_features.features.property_reading.properties;

import java.util.Properties;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFLruCache;

public abstract class RandomProperty {
   protected final ETFLruCache.UUIDBoolean entityCachedInitialResult = new ETFLruCache.UUIDBoolean();
   private boolean canUpdate = true;

   @NotNull
   public static String readPropertiesOrThrow(Properties properties, int propertyNum, String... propertyId) throws RandomProperty.RandomPropertyException {
      if (propertyId.length == 0) {
         throw new IllegalArgumentException("[ETF] readPropertiesOrThrow() was given empty property id's");
      } else {
         for (String id : propertyId) {
            if (properties.containsKey(id + "." + propertyNum)) {
               String dataFromProps = properties.getProperty(id + "." + propertyNum).strip();
               if (!dataFromProps.isBlank()) {
                  return dataFromProps;
               }
            }
         }

         throw new RandomProperty.RandomPropertyException("failed to read property [" + propertyId[0] + "]");
      }
   }

   public boolean testEntity(ETFEntityRenderState entity, boolean isUpdate) {
      UUID key = entity.uuid();
      if (isUpdate && !this.canPropertyUpdate() && this.entityCachedInitialResult.containsKey(key)) {
         return (Boolean)this.entityCachedInitialResult.get(key);
      } else {
         try {
            return this.testEntityInternal(entity);
         } catch (Exception var5) {
            return false;
         }
      }
   }

   protected abstract boolean testEntityInternal(ETFEntityRenderState var1);

   public final boolean canPropertyUpdate() {
      return this.canUpdate;
   }

   public void setCanUpdate(boolean canUpdate) {
      this.canUpdate = canUpdate;
   }

   @NotNull
   public abstract String[] getPropertyIds();

   @NotNull
   public String getPropertyId() {
      return this.getPropertyIds()[0];
   }

   protected abstract String getPrintableRuleInfo();

   @Override
   public String toString() {
      return this.getClass().getSimpleName() + "[Property: " + this.getPropertyId() + ", Rule: " + this.getPrintableRuleInfo() + "]";
   }

   public void cacheEntityInitialResult(ETFEntityRenderState entity) {
      try {
         this.entityCachedInitialResult.put(entity.uuid(), this.testEntityInternal(entity));
      } catch (Exception var3) {
         this.entityCachedInitialResult.put(entity.uuid(), false);
      }
   }

   public static class RandomPropertyException extends Exception {
      public RandomPropertyException(String reason) {
         super("[ETF] " + reason);
      }
   }
}

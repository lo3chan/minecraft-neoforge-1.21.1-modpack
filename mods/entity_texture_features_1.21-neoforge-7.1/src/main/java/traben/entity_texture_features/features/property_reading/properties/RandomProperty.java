/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
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
    public static String readPropertiesOrThrow(Properties properties, int propertyNum, String ... propertyId) throws RandomPropertyException {
        if (propertyId.length == 0) {
            throw new IllegalArgumentException("[ETF] readPropertiesOrThrow() was given empty property id's");
        }
        for (String id : propertyId) {
            String dataFromProps;
            if (!properties.containsKey(id + "." + propertyNum) || (dataFromProps = properties.getProperty(id + "." + propertyNum).strip()).isBlank()) continue;
            return dataFromProps;
        }
        throw new RandomPropertyException("failed to read property [" + propertyId[0] + "]");
    }

    public boolean testEntity(ETFEntityRenderState entity, boolean isUpdate) {
        UUID key = entity.uuid();
        if (isUpdate && !this.canPropertyUpdate() && this.entityCachedInitialResult.containsKey(key)) {
            return (Boolean)this.entityCachedInitialResult.get(key);
        }
        try {
            return this.testEntityInternal(entity);
        }
        catch (Exception ignored) {
            return false;
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

    public String toString() {
        return this.getClass().getSimpleName() + "[Property: " + this.getPropertyId() + ", Rule: " + this.getPrintableRuleInfo() + "]";
    }

    public void cacheEntityInitialResult(ETFEntityRenderState entity) {
        try {
            this.entityCachedInitialResult.put(entity.uuid(), this.testEntityInternal(entity));
        }
        catch (Exception ignored) {
            this.entityCachedInitialResult.put(entity.uuid(), false);
        }
    }

    public static class RandomPropertyException
    extends Exception {
        public RandomPropertyException(String reason) {
            super("[ETF] " + reason);
        }
    }
}


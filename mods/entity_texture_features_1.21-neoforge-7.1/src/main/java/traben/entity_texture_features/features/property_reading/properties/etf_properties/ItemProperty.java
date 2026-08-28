/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.Iterator;
import java.util.Properties;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class ItemProperty
extends StringArrayOrRegexProperty {
    protected ItemProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(ItemProperty.readPropertiesOrThrow(properties, propertyNum, "items", "item").replaceAll("(?<=(^| ))minecraft:", ""));
    }

    public static ItemProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new ItemProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return true;
    }

    @Override
    public boolean testEntityInternal(ETFEntityRenderState entity) {
        ItemStack item;
        String itemString;
        if (this.ARRAY.size() == 1 && this.ARRAY.stream().anyMatch(string -> "none".equals(string) || "any".equals(string) || "holding".equals(string) || "wearing".equals(string))) {
            if (this.ARRAY.contains("none")) {
                Iterable<ItemStack> equipped = entity.itemsEquipped();
                for (ItemStack item2 : equipped) {
                    if (item2 == null || item2.isEmpty()) continue;
                    return false;
                }
                return true;
            }
            Iterable<ItemStack> items = this.ARRAY.contains("any") ? entity.itemsEquipped() : (this.ARRAY.contains("holding") ? entity.handItems() : entity.armorItems());
            boolean found = false;
            for (ItemStack item3 : items) {
                if (item3 == null || item3.isEmpty()) continue;
                found = true;
                break;
            }
            return found;
        }
        Iterable<ItemStack> equipped = entity.itemsEquipped();
        boolean found = false;
        Iterator<ItemStack> iterator = equipped.iterator();
        while (iterator.hasNext() && !(found = this.MATCHER.testString(itemString = (item = iterator.next()).getItem().toString().replaceFirst("^minecraft:", "")))) {
        }
        return found;
    }

    @Override
    @Nullable
    public String getValueFromEntity(ETFEntityRenderState etfEntity) {
        return null;
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"items", "item"};
    }
}


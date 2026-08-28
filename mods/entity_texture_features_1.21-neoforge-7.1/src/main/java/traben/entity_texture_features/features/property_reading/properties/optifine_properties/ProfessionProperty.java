/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.npc.VillagerDataHolder
 *  org.jetbrains.annotations.NotNull
 */
package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public class ProfessionProperty
extends StringArrayOrRegexProperty {
    protected ProfessionProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(ProfessionProperty.readPropertiesOrThrow(properties, propertyNum, "professions"));
    }

    public static ProfessionProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new ProfessionProperty(properties, propertyNum);
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    public boolean testEntityInternal(ETFEntityRenderState entity) {
        ETFEntity eTFEntity;
        if (entity != null && (eTFEntity = entity.entity()) instanceof VillagerDataHolder) {
            VillagerDataHolder villagerEntity = (VillagerDataHolder)eTFEntity;
            String entityProfession = villagerEntity.getVillagerData().getProfession().toString().toLowerCase().replace("minecraft:", "");
            int entityProfessionLevel = villagerEntity.getVillagerData().getLevel();
            boolean check = false;
            block0: for (String str : this.ARRAY) {
                if (str == null) continue;
                if ((str = str.toLowerCase().replaceAll("\\s*", "").replace("minecraft:", "")).contains(":")) {
                    String[] data = str.split(":\\d");
                    if (!entityProfession.contains(data[0]) && !data[0].contains(entityProfession)) continue;
                    if (data.length == 2) {
                        String[] levels = data[1].split(",");
                        ArrayList<Integer> levelData = new ArrayList<Integer>();
                        for (String lvls : levels) {
                            if (lvls.contains("-")) {
                                levelData.addAll(Arrays.asList(SimpleIntegerArrayProperty.getIntRange(lvls).getAllWithinRangeAsList()));
                                continue;
                            }
                            levelData.add(Integer.parseInt(lvls.replaceAll("\\D", "")));
                        }
                        for (Integer i : levelData) {
                            if (i != entityProfessionLevel) continue;
                            check = true;
                            continue block0;
                        }
                        continue;
                    }
                    check = true;
                    break;
                }
                if (!entityProfession.contains(str) && !str.contains(entityProfession)) continue;
                check = true;
                break;
            }
            return check;
        }
        return false;
    }

    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return false;
    }

    @Override
    protected String getValueFromEntity(ETFEntityRenderState entity) {
        return null;
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"professions"};
    }
}


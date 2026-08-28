/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.ComponentContents
 *  net.minecraft.network.chat.contents.PlainTextContents$LiteralContents
 *  net.minecraft.world.entity.player.Player
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public class NameProperty
extends StringArrayOrRegexProperty {
    protected NameProperty(String data) throws RandomProperty.RandomPropertyException {
        super(data);
    }

    public static NameProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            String dataFromProperty = NameProperty.readPropertiesOrThrow(properties, propertyNum, "name", "names");
            ArrayList<String> names = new ArrayList<String>();
            if (dataFromProperty.isBlank()) {
                throw new RandomProperty.RandomPropertyException("Name failed");
            }
            if (dataFromProperty.startsWith("regex:") || dataFromProperty.startsWith("pattern:")) {
                names.add(dataFromProperty);
            } else {
                Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(dataFromProperty);
                while (m.find()) {
                    names.add(m.group(1).replace("\"", "").trim());
                }
            }
            StringBuilder builder = new StringBuilder();
            for (String str : names) {
                builder.append(str).append(" ");
            }
            return new NameProperty(builder.toString().trim());
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return false;
    }

    @Override
    @Nullable
    public String getValueFromEntity(ETFEntityRenderState etfEntity) {
        Component entityNameText;
        ETFEntity eTFEntity;
        if (etfEntity != null && (eTFEntity = etfEntity.entity()) instanceof Player) {
            Player player = (Player)eTFEntity;
            return player.getName().getString();
        }
        if (etfEntity != null && etfEntity.hasCustomName() && (entityNameText = etfEntity.customName()) != null) {
            ComponentContents content = entityNameText.getContents();
            if (content instanceof PlainTextContents.LiteralContents) {
                PlainTextContents.LiteralContents literal = (PlainTextContents.LiteralContents)content;
                return literal.text();
            }
            return entityNameText.getString();
        }
        return null;
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"name", "names"};
    }
}


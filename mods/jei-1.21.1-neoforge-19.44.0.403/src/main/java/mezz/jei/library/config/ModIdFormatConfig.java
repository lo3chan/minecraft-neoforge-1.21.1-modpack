/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.config;

import java.util.List;
import java.util.function.Supplier;
import mezz.jei.common.config.file.ConfigValue;
import mezz.jei.common.config.file.IConfigCategoryBuilder;
import mezz.jei.common.config.file.IConfigSchemaBuilder;
import mezz.jei.common.util.function.CachedSupplierTransformer;
import mezz.jei.library.config.IModIdFormatConfig;
import mezz.jei.library.config.ModIdFormatDetectionHelper;
import mezz.jei.library.config.StyledTextHelper;
import mezz.jei.library.config.serializers.ChatFormattingSerializer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ModIdFormatConfig
implements IModIdFormatConfig {
    protected static final List<ChatFormatting> defaultModNameFormat = List.of(ChatFormatting.BLUE, ChatFormatting.ITALIC);
    public static final String MOD_NAME_FORMAT_CODE = "%MODNAME%";
    private final Supplier<Component> modNameFormat;
    @Nullable
    private Component cachedOverride;

    public ModIdFormatConfig(IConfigSchemaBuilder builder) {
        IConfigCategoryBuilder modName = builder.addCategory("modName");
        ConfigValue<List<ChatFormatting>> configValue = modName.addList("modNameFormat", defaultModNameFormat, ChatFormattingSerializer.INSTANCE);
        this.modNameFormat = new CachedSupplierTransformer<List, Component>(configValue, ModIdFormatConfig::toFormatString);
    }

    private static Component toFormatString(List<ChatFormatting> values) {
        if (values.isEmpty()) {
            return Component.empty();
        }
        return Component.literal((String)MOD_NAME_FORMAT_CODE).withStyle((ChatFormatting[])values.toArray(ChatFormatting[]::new));
    }

    private Component getOverride() {
        if (this.cachedOverride == null) {
            this.cachedOverride = ModIdFormatDetectionHelper.detectModNameTooltipFormatting();
        }
        return this.cachedOverride;
    }

    @Override
    public final Component getModNameFormat() {
        Component override = this.getOverride();
        if (!override.getString().isEmpty()) {
            return override;
        }
        return this.modNameFormat.get();
    }

    @Override
    public final boolean isModNameFormatOverrideActive() {
        return !this.getOverride().getString().isEmpty();
    }

    public static Component detectModNameTooltipFormatting(List<Component> tooltip) {
        if (tooltip.size() <= 1) {
            return Component.empty();
        }
        for (int lineNum = 1; lineNum < tooltip.size(); ++lineNum) {
            Component line = tooltip.get(lineNum);
            Component result = ModIdFormatConfig.detectModNameTooltipFormatting(line);
            if (result.getString().isEmpty()) continue;
            return result;
        }
        return Component.empty();
    }

    private static Component detectModNameTooltipFormatting(Component line) {
        return StyledTextHelper.replaceFirst(line, "Minecraft", (Component)Component.literal((String)MOD_NAME_FORMAT_CODE)).orElseGet(Component::empty);
    }

    public static Component replaceModNameFormatCode(Component format, String modName) {
        return StyledTextHelper.replaceFirst(format, MOD_NAME_FORMAT_CODE, (Component)Component.literal((String)modName)).orElseGet(() -> format.copy().append((Component)Component.literal((String)modName)));
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Unmodifiable
 */
package mezz.jei.common.config.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mezz.jei.api.runtime.config.IJeiConfigValueSerializer;
import mezz.jei.common.config.file.ConfigCategory;
import mezz.jei.common.config.file.ConfigValue;
import mezz.jei.common.util.PathUtil;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Unmodifiable;

public final class ConfigSerializer {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern commentRegex = Pattern.compile("\\s*#.*");
    private static final Pattern categoryRegex = Pattern.compile("\\[(?<category>\\w+)]\\s*");
    private static final Pattern keyValueRegex = Pattern.compile("\\s*(?<key>\\w+)\\s*=\\s*(?<value>.*)");
    private static final Map<Path, FileTime> saveTimes = new HashMap<Path, FileTime>();

    private static String getLineErrorString(Path path, int lineNumber, String line, String errorMessage) {
        return "%s\nConfig file: %s\nLine #%s: \"%s\"".formatted(errorMessage, path, lineNumber, line);
    }

    public static void load(Path path, @Unmodifiable List<ConfigCategory> categories) throws IOException {
        FileTime lastModifiedTime = Files.getLastModifiedTime(path, new LinkOption[0]);
        FileTime savedTime = saveTimes.get(path);
        if (savedTime != null && savedTime.compareTo(lastModifiedTime) >= 0) {
            LOGGER.debug("Skipping loading config file, it was just saved by us: {}", (Object)path);
            return;
        }
        LOGGER.debug("Loading config file: {}", (Object)path);
        List<String> lines = Files.readAllLines(path);
        LinkedHashMap<String, ConfigCategory> categoriesMap = new LinkedHashMap<String, ConfigCategory>();
        for (ConfigCategory category : categories) {
            categoriesMap.put(category.getName(), category);
        }
        ConfigCategory category = null;
        for (int i = 0; i < lines.size(); ++i) {
            int lineNumber = i + 1;
            String line = lines.get(i);
            if (line.isBlank() || commentRegex.matcher(line).matches()) continue;
            Matcher categoryMatcher = categoryRegex.matcher(line);
            if (categoryMatcher.matches()) {
                String categoryName = categoryMatcher.group("category");
                category = (ConfigCategory)categoriesMap.get(categoryName);
                if (category != null) continue;
                LOGGER.error(ConfigSerializer.getLineErrorString(path, lineNumber, line, "'[%s]' is not a valid category name.\nValid names are: [%s]\nSkipping all values until the first valid category is declared.".formatted(categoryName, String.join((CharSequence)", ", categoriesMap.keySet()))));
                continue;
            }
            if (category == null) {
                LOGGER.error(ConfigSerializer.getLineErrorString(path, lineNumber, line, "Expected a '[category]' here.\nConfigs must start with a category before defining values.\nSkipping all lines until the first valid category is declared."));
                continue;
            }
            Matcher keyValueMatcher = keyValueRegex.matcher(line);
            if (keyValueMatcher.matches()) {
                String key = keyValueMatcher.group("key").trim();
                String value = keyValueMatcher.group("value").trim();
                Optional<ConfigValue<?>> configValue = category.getConfigValue(key);
                if (configValue.isEmpty()) {
                    LOGGER.error(ConfigSerializer.getLineErrorString(path, lineNumber, line, "'%s' is not a valid config key for config category '%s'.\nValid keys: [%s]\nSkipping this key.".formatted(key, category.getName(), String.join((CharSequence)", ", category.getValueNames()))));
                    continue;
                }
                List<String> errors = configValue.get().setFromSerializedValue(value);
                if (errors.isEmpty()) continue;
                String errorMessage = "Encountered Errors when deserializing value '%s':\n%s".formatted(value, String.join((CharSequence)"\n", errors));
                LOGGER.error(ConfigSerializer.getLineErrorString(path, lineNumber, line, errorMessage));
                continue;
            }
            LOGGER.error(ConfigSerializer.getLineErrorString(path, lineNumber, line, "Encountered an invalid line.\nEvery line in the config must be either:\n * a '[category]'\n * a 'key = value' pair\n * a '#'-prefixed comment"));
        }
    }

    public static void save(Path path, List<ConfigCategory> categories) throws IOException {
        ArrayList serialized = new ArrayList();
        categories.forEach(category -> {
            ConfigSerializer.serializeCategory(serialized, category);
            serialized.add("");
        });
        LOGGER.debug("Saving config file: {}", (Object)path);
        PathUtil.writeUsingTempFile(path, serialized);
        FileTime lastModifiedTime = Files.getLastModifiedTime(path, new LinkOption[0]);
        saveTimes.put(path, lastModifiedTime);
    }

    private static void serializeCategory(List<String> serialized, ConfigCategory category) {
        serialized.add("[%s]".formatted(category.getName()));
        for (ConfigValue<?> value : category.getConfigValues()) {
            ConfigSerializer.serializeConfigValue(serialized, value);
            serialized.add("");
        }
    }

    private static <T> void serializeConfigValue(List<String> serialized, ConfigValue<T> configValue) {
        String name = configValue.getName();
        IJeiConfigValueSerializer<T> serializer = configValue.getSerializer();
        String localizedName = Component.translatable((String)"jei.config.name", (Object[])new Object[]{configValue.getLocalizedName().getString()}).getString();
        ConfigSerializer.addCommentedStrings(serialized, localizedName);
        String description = Component.translatable((String)"jei.config.description", (Object[])new Object[]{configValue.getLocalizedDescription().getString()}).getString();
        ConfigSerializer.addCommentedStrings(serialized, description);
        String validValues = Component.translatable((String)"jei.config.valueValues", (Object[])new Object[]{serializer.getValidValuesDescription()}).getString();
        ConfigSerializer.addCommentedStrings(serialized, validValues);
        T defaultValue = configValue.getDefaultValue();
        String defaultValueSerialized = serializer.serialize(defaultValue);
        String defaultValueString = Component.translatable((String)"jei.config.defaultValue", (Object[])new Object[]{defaultValueSerialized}).getString();
        ConfigSerializer.addCommentedStrings(serialized, defaultValueString);
        T value = configValue.getValue();
        String valueString = serializer.serialize(value);
        serialized.add("\t%s = %s".formatted(name, valueString));
    }

    private static void addCommentedStrings(List<String> serialized, String comment) {
        String[] lines = comment.split("\n");
        if (lines.length == 0) {
            return;
        }
        serialized.add("\t# %s".formatted(lines[0]));
        if (lines.length > 1) {
            for (int i = 1; i < lines.length; ++i) {
                serialized.add("\t# %s".formatted(lines[i]));
            }
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 */
package mezz.jei.library.config.serializers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import mezz.jei.api.runtime.config.IJeiConfigListValueSerializer;
import mezz.jei.api.runtime.config.IJeiConfigValueSerializer;
import mezz.jei.common.config.file.serializers.DeserializeResult;
import net.minecraft.ChatFormatting;

public class ChatFormattingSerializer
implements IJeiConfigListValueSerializer<ChatFormatting> {
    public static final ChatFormattingSerializer INSTANCE = new ChatFormattingSerializer();
    private static final ChatFormattingValueSerializer VALUE_SERIALIZER = new ChatFormattingValueSerializer();
    private static final EnumSet<ChatFormatting> INVALID_VALUES = EnumSet.of(ChatFormatting.RESET);
    private static final EnumSet<ChatFormatting> VALID_VALUES = EnumSet.complementOf(INVALID_VALUES);

    private ChatFormattingSerializer() {
    }

    @Override
    public String serialize(List<ChatFormatting> value) {
        return value.stream().map(VALUE_SERIALIZER::serialize).collect(Collectors.joining(" "));
    }

    @Override
    public DeserializeResult<List<ChatFormatting>> deserialize(String string) {
        if ((string = string.trim()).startsWith("\"") && string.endsWith("\"")) {
            string = string.substring(1, string.length() - 1);
        }
        if (string.isEmpty()) {
            return new DeserializeResult<List<ChatFormatting>>(List.of());
        }
        ArrayList<String> errors = new ArrayList<String>();
        String[] strings = string.split(" ");
        List result = Arrays.stream(strings).mapMulti((s, c) -> {
            IJeiConfigValueSerializer.IDeserializeResult<ChatFormatting> deserializeResult = VALUE_SERIALIZER.deserialize((String)s);
            deserializeResult.getResult().ifPresent((Consumer<ChatFormatting>)c);
            errors.addAll(deserializeResult.getErrors());
        }).toList();
        return new DeserializeResult<List<ChatFormatting>>(result, errors);
    }

    @Override
    public String getValidValuesDescription() {
        ArrayList<ChatFormatting> validColors = new ArrayList<ChatFormatting>();
        ArrayList<ChatFormatting> validFormats = new ArrayList<ChatFormatting>();
        for (ChatFormatting chatFormatting : VALID_VALUES) {
            if (chatFormatting.isColor()) {
                validColors.add(chatFormatting);
                continue;
            }
            if (!chatFormatting.isFormat()) continue;
            validFormats.add(chatFormatting);
        }
        return "A chat formatting string.\nUse these formatting colors:\n%s\nWith these formatting options:\n%s".formatted(this.serialize((List<ChatFormatting>)validColors), this.serialize((List<ChatFormatting>)validFormats));
    }

    @Override
    public boolean isValid(List<ChatFormatting> value) {
        return value.stream().allMatch(VALUE_SERIALIZER::isValid);
    }

    @Override
    public IJeiConfigValueSerializer<ChatFormatting> getListValueSerializer() {
        return VALUE_SERIALIZER;
    }

    @Override
    public Optional<Collection<List<ChatFormatting>>> getAllValidValues() {
        return Optional.empty();
    }

    private static class ChatFormattingValueSerializer
    implements IJeiConfigValueSerializer<ChatFormatting> {
        private ChatFormattingValueSerializer() {
        }

        @Override
        public String serialize(ChatFormatting value) {
            return value.getName();
        }

        @Override
        public IJeiConfigValueSerializer.IDeserializeResult<ChatFormatting> deserialize(String string) {
            ChatFormatting chatFormatting = ChatFormatting.getByName((String)string);
            if (chatFormatting == null) {
                return new DeserializeResult<Object>(null, "No Chat Formatting found for name: '%s'".formatted(string));
            }
            if (INVALID_VALUES.contains(chatFormatting)) {
                return new DeserializeResult<Object>(null, "Chat Formatting '%s' is not valid".formatted(string));
            }
            return new DeserializeResult<ChatFormatting>(chatFormatting);
        }

        @Override
        public boolean isValid(ChatFormatting value) {
            return VALID_VALUES.contains(value);
        }

        @Override
        public Optional<Collection<ChatFormatting>> getAllValidValues() {
            return Optional.of(VALID_VALUES);
        }

        @Override
        public String getValidValuesDescription() {
            String validValuesString = VALID_VALUES.stream().map(this::serialize).collect(Collectors.joining(", "));
            return "A chat formatting string.\nUse any of these formatting values:\n%s".formatted(validValuesString);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.chat.ClickEvent
 *  net.minecraft.network.chat.ClickEvent$Action
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.HoverEvent
 *  net.minecraft.network.chat.HoverEvent$Action
 *  net.minecraft.network.chat.HoverEvent$ItemStackInfo
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.material.Fluid
 */
package mezz.jei.common.chat;

import java.util.Arrays;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.common.Internal;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

public final class JeiChatItemLinks {
    public static final String SHOW_RECIPE_COMMAND = "jei_internal_show";
    public static final String LINK_ARGUMENT = "link";
    private static final String LINK_VERSION = "v1";
    private static final String LINK_VERSION_PREFIX = "v1:";
    private static final String LINK_MARKER_PREFIX = "[JEI:";
    private static final String LINK_MARKER_SUFFIX = "]";
    private static final char LINK_VALUE_SEPARATOR = ';';

    private JeiChatItemLinks() {
    }

    public static String createLinkMarker(ITypedIngredient<?> typedIngredient, IIngredientManager ingredientManager) {
        return JeiChatItemLinks.createLinkMarkerInternal(typedIngredient, ingredientManager);
    }

    public static Component parse(String rawText) {
        return JeiChatItemLinks.parse(rawText, JeiChatItemLinks::getIngredientName);
    }

    public static Component parse(String rawText, Function<IngredientLink, Optional<String>> ingredientNameLookup) {
        int markerStart;
        MutableComponent result = Component.empty();
        int searchStart = 0;
        int lastEnd = 0;
        while ((markerStart = rawText.indexOf(LINK_MARKER_PREFIX, searchStart)) >= 0) {
            Optional<LinkMarker> optionalMarker = JeiChatItemLinks.parseLinkMarker(rawText, markerStart);
            if (optionalMarker.isEmpty()) {
                searchStart = markerStart + LINK_MARKER_PREFIX.length();
                continue;
            }
            LinkMarker marker = optionalMarker.get();
            if (marker.start() > lastEnd) {
                result.append((Component)Component.literal((String)rawText.substring(lastEnd, marker.start())));
            }
            IngredientLink link = marker.link();
            MutableComponent linkComponent = JeiChatItemLinks.createLinkComponent(link, ingredientNameLookup);
            result.append((Component)linkComponent);
            searchStart = marker.end();
            lastEnd = marker.end();
        }
        if (lastEnd < rawText.length()) {
            result.append((Component)Component.literal((String)rawText.substring(lastEnd)));
        }
        return result;
    }

    public static Optional<Component> parseChatMessage(Component message) {
        return JeiChatItemLinks.parseChatMessage(message, JeiChatItemLinks::getIngredientName);
    }

    public static Optional<Component> parseChatMessage(Component message, Function<IngredientLink, Optional<String>> ingredientNameLookup) {
        String rawText = message.getString();
        if (!JeiChatItemLinks.hasLinkMarkers(rawText)) {
            return Optional.empty();
        }
        Component parsedMessage = JeiChatItemLinks.parse(rawText, ingredientNameLookup);
        return Optional.of(parsedMessage);
    }

    public static boolean hasLinkMarkers(String rawText) {
        int searchStart = 0;
        int markerStart;
        while ((markerStart = rawText.indexOf(LINK_MARKER_PREFIX, searchStart)) >= 0) {
            if (JeiChatItemLinks.parseLinkMarker(rawText, markerStart).isPresent()) {
                return true;
            }
            searchStart = markerStart + LINK_MARKER_PREFIX.length();
        }
        return false;
    }

    public static Optional<ITypedIngredient<?>> resolveTypedIngredient(IngredientLink link, IIngredientManager ingredientManager) {
        return ingredientManager.getIngredientTypeForUid(link.ingredientTypeUid()).flatMap(ingredientType -> JeiChatItemLinks.resolveTypedIngredient(ingredientType, link.ingredientUid(), ingredientManager));
    }

    public static String createCommandArgument(IngredientLink link) {
        return LINK_VERSION_PREFIX + link.ingredientTypeUid() + ";" + link.ingredientUid();
    }

    public static Optional<IngredientLink> parseCommandArgument(String linkText) {
        if (!linkText.startsWith(LINK_VERSION_PREFIX)) {
            return Optional.empty();
        }
        int ingredientTypeUidStart = LINK_VERSION_PREFIX.length();
        int separator = linkText.indexOf(59, ingredientTypeUidStart);
        if (separator <= ingredientTypeUidStart) {
            return Optional.empty();
        }
        int ingredientUidStart = separator + 1;
        if (ingredientUidStart >= linkText.length()) {
            return Optional.empty();
        }
        String ingredientTypeUid = linkText.substring(ingredientTypeUidStart, separator);
        if (!JeiChatItemLinks.isValidIngredientTypeUid(ingredientTypeUid)) {
            return Optional.empty();
        }
        String ingredientUid = linkText.substring(ingredientUidStart);
        IngredientLink link = new IngredientLink(ingredientTypeUid, ingredientUid);
        return Optional.of(link);
    }

    public static String createShowRecipeCommand(IngredientLink link) {
        return "jei_internal_show " + JeiChatItemLinks.createCommandArgument(link);
    }

    public static Optional<IngredientLink> parseShowRecipeCommand(String command) {
        String prefix = "jei_internal_show ";
        if (!command.startsWith(prefix)) {
            return Optional.empty();
        }
        String linkText = command.substring(prefix.length());
        return JeiChatItemLinks.parseCommandArgument(linkText);
    }

    private static String createLinkMarker(IngredientLink link) {
        String linkText = JeiChatItemLinks.createCommandArgument(link);
        return LINK_MARKER_PREFIX + linkText + "] ";
    }

    private static <T> String createLinkMarkerInternal(ITypedIngredient<T> typedIngredient, IIngredientManager ingredientManager) {
        IIngredientType<T> ingredientType = typedIngredient.getType();
        IIngredientHelper<T> ingredientHelper = ingredientManager.getIngredientHelper(ingredientType);
        Object uid = ingredientHelper.getUid(typedIngredient, UidContext.Ingredient);
        String ingredientUid = JeiChatItemLinks.getIngredientUidString(uid);
        IngredientLink link = new IngredientLink(ingredientType.getUid(), ingredientUid);
        return JeiChatItemLinks.createLinkMarker(link);
    }

    private static <T> Optional<ITypedIngredient<T>> resolveTypedIngredient(IIngredientType<T> ingredientType, String ingredientUid, IIngredientManager ingredientManager) {
        IIngredientHelper ingredientHelper = ingredientManager.getIngredientHelper(ingredientType);
        return ingredientManager.getAllTypedIngredients(ingredientType).stream().filter(typedIngredient -> {
            Object uid = ingredientHelper.getUid((ITypedIngredient)typedIngredient, UidContext.Ingredient);
            String candidateUid = JeiChatItemLinks.getIngredientUidString(uid);
            return ingredientUid.equals(candidateUid);
        }).findFirst();
    }

    private static Optional<LinkMarker> parseLinkMarker(String rawText, int start) {
        int argumentStart = start + LINK_MARKER_PREFIX.length();
        int markerEnd = rawText.indexOf(LINK_MARKER_SUFFIX, argumentStart);
        if (markerEnd < 0) {
            return Optional.empty();
        }
        String linkText = rawText.substring(argumentStart, markerEnd);
        Optional<IngredientLink> optionalLink = JeiChatItemLinks.parseCommandArgument(linkText);
        if (optionalLink.isEmpty()) {
            return Optional.empty();
        }
        IngredientLink link = optionalLink.get();
        LinkMarker marker = new LinkMarker(start, markerEnd + LINK_MARKER_SUFFIX.length(), link);
        return Optional.of(marker);
    }

    private static boolean isValidIngredientTypeUid(String ingredientTypeUid) {
        for (int i = 0; i < ingredientTypeUid.length(); ++i) {
            char c = ingredientTypeUid.charAt(i);
            if (!Character.isWhitespace(c) && c != '(' && c != ')' && c != '=' && c != '[' && c != ']') continue;
            return false;
        }
        return true;
    }

    private static Optional<String> getIngredientName(IngredientLink link) {
        Optional<IJeiRuntime> optionalRuntime = Internal.getOptionalJeiRuntime();
        if (optionalRuntime.isEmpty()) {
            return Optional.empty();
        }
        IJeiRuntime runtime = optionalRuntime.get();
        IIngredientManager ingredientManager = runtime.getIngredientManager();
        return JeiChatItemLinks.resolveTypedIngredient(link, ingredientManager).map(typedIngredient -> JeiChatItemLinks.getIngredientName(typedIngredient, ingredientManager));
    }

    private static <T> String getIngredientName(ITypedIngredient<T> typedIngredient, IIngredientManager ingredientManager) {
        IIngredientType<T> ingredientType = typedIngredient.getType();
        IIngredientHelper<T> ingredientHelper = ingredientManager.getIngredientHelper(ingredientType);
        return ingredientHelper.getDisplayName(typedIngredient.getIngredient());
    }

    private static String getIngredientUidString(Object uid) {
        if (uid instanceof ResourceLocation) {
            ResourceLocation resourceLocation = (ResourceLocation)uid;
            return resourceLocation.toString();
        }
        if (uid instanceof Item) {
            Item item = (Item)uid;
            return BuiltInRegistries.ITEM.wrapAsHolder((Object)item).getRegisteredName();
        }
        if (uid instanceof Fluid) {
            Fluid fluid = (Fluid)uid;
            return BuiltInRegistries.FLUID.wrapAsHolder((Object)fluid).getRegisteredName();
        }
        if (uid instanceof Iterable) {
            Iterable iterable = (Iterable)uid;
            return JeiChatItemLinks.getIterableUidString(iterable);
        }
        if (uid instanceof Object[]) {
            Object[] array = (Object[])uid;
            return JeiChatItemLinks.getIterableUidString(Arrays.asList(array));
        }
        return String.valueOf(uid);
    }

    private static String getIterableUidString(Iterable<?> iterable) {
        StringJoiner joiner = new StringJoiner(",", "(", ")");
        for (Object value : iterable) {
            String valueString = JeiChatItemLinks.getIngredientUidString(value);
            joiner.add(valueString);
        }
        return joiner.toString();
    }

    private static MutableComponent createLinkComponent(IngredientLink link, Function<IngredientLink, Optional<String>> ingredientNameLookup) {
        Optional<String> optionalIngredientName = ingredientNameLookup.apply(link);
        if (optionalIngredientName.isEmpty()) {
            String ingredientUid = link.ingredientUid();
            return Component.literal((String)("[" + ingredientUid + LINK_MARKER_SUFFIX));
        }
        String ingredientName = optionalIngredientName.get();
        MutableComponent component = Component.literal((String)("[" + ingredientName + LINK_MARKER_SUFFIX));
        HoverEvent hoverEvent = JeiChatItemLinks.createHoverEvent(link, ingredientName);
        return component.withStyle(style -> style.withColor(ChatFormatting.AQUA).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, JeiChatItemLinks.createShowRecipeCommand(link))).withHoverEvent(hoverEvent));
    }

    private static HoverEvent createHoverEvent(IngredientLink link, String ingredientName) {
        return JeiChatItemLinks.createItemHoverEvent(link).orElseGet(() -> JeiChatItemLinks.createTextHoverEvent(ingredientName));
    }

    private static HoverEvent createTextHoverEvent(String ingredientName) {
        MutableComponent hoverText = Component.literal((String)ingredientName);
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, (Object)hoverText);
    }

    private static Optional<HoverEvent> createItemHoverEvent(IngredientLink link) {
        if (!link.ingredientTypeUid().equals(VanillaTypes.ITEM_STACK.getUid())) {
            return Optional.empty();
        }
        Optional<IJeiRuntime> optionalRuntime = Internal.getOptionalJeiRuntime();
        if (optionalRuntime.isEmpty()) {
            return Optional.empty();
        }
        IJeiRuntime runtime = optionalRuntime.get();
        IIngredientManager ingredientManager = runtime.getIngredientManager();
        return JeiChatItemLinks.resolveTypedIngredient(link, ingredientManager).flatMap(ITypedIngredient::getItemStack).filter(stack -> !stack.isEmpty()).map(JeiChatItemLinks::createItemStackHoverEvent);
    }

    private static HoverEvent createItemStackHoverEvent(ItemStack stack) {
        ItemStack displayStack = stack.copy();
        return new HoverEvent(HoverEvent.Action.SHOW_ITEM, (Object)new HoverEvent.ItemStackInfo(displayStack));
    }

    private record LinkMarker(int start, int end, IngredientLink link) {
    }

    public record IngredientLink(String ingredientTypeUid, String ingredientUid) {
    }
}


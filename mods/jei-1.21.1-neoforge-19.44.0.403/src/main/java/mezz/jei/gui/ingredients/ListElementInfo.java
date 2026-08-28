/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.CreativeModeTab$Type
 *  net.minecraft.world.item.CreativeModeTabs
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.TooltipFlag$Default
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.Unmodifiable
 */
package mezz.jei.gui.ingredients;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import mezz.jei.api.helpers.IColorHelper;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.config.IIngredientFilterConfig;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.SafeIngredientUtil;
import mezz.jei.common.util.StringUtil;
import mezz.jei.common.util.Translator;
import mezz.jei.gui.ingredients.DisplayNameUtil;
import mezz.jei.gui.ingredients.IListElement;
import mezz.jei.gui.ingredients.IListElementInfo;
import mezz.jei.gui.ingredients.ListElement;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public class ListElementInfo<V>
implements IListElementInfo<V> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    private static final Pattern MOD_NAME_SEPARATOR_PATTERN = Pattern.compile("(?=[A-Z_-])|\\s+");
    private static int elementCount = 0;
    private final IListElement<V> element;
    private final IModIdHelper modIdHelper;
    private final List<String> names;
    private final List<String> modIds;
    private final List<String> modNames;
    private final ResourceLocation resourceLocation;

    @Nullable
    public static <V> IListElementInfo<V> create(ITypedIngredient<V> value, IIngredientManager ingredientManager, IModIdHelper modIdHelper) {
        int createdIndex = elementCount++;
        ListElement<V> element = new ListElement<V>(value, createdIndex);
        return ListElementInfo.createFromElement(element, ingredientManager, modIdHelper);
    }

    @Nullable
    public static <V> IListElementInfo<V> createFromElement(IListElement<V> element, IIngredientManager ingredientManager, IModIdHelper modIdHelper) {
        try {
            return new ListElementInfo<V>(element, ingredientManager, modIdHelper);
        }
        catch (RuntimeException e) {
            try {
                ITypedIngredient<V> typedIngredient = element.getTypedIngredient();
                IIngredientHelper<V> ingredientHelper = ingredientManager.getIngredientHelper(typedIngredient.getType());
                String ingredientInfo = ingredientHelper.getErrorInfo(typedIngredient.getIngredient());
                LOGGER.warn("Found a broken ingredient {}", (Object)ingredientInfo, (Object)e);
            }
            catch (RuntimeException e2) {
                LOGGER.warn("Found a broken ingredient.", (Throwable)e2);
            }
            return null;
        }
    }

    protected ListElementInfo(IListElement<V> element, IIngredientManager ingredientManager, IModIdHelper modIdHelper) {
        this.element = element;
        this.modIdHelper = modIdHelper;
        ITypedIngredient<V> value = element.getTypedIngredient();
        V ingredient = value.getIngredient();
        IIngredientHelper<V> ingredientHelper = ingredientManager.getIngredientHelper(value.getType());
        this.resourceLocation = ingredientHelper.getResourceLocation(ingredient);
        String displayModId = ingredientHelper.getDisplayModId(ingredient);
        String modId = this.resourceLocation.getNamespace();
        if (modId.equals(displayModId)) {
            this.modIds = List.of(modId);
            this.modNames = List.of(modIdHelper.getModNameForModId(modId));
        } else {
            this.modIds = List.of(modId, displayModId);
            this.modNames = List.of(modIdHelper.getModNameForModId(modId), modIdHelper.getModNameForModId(displayModId));
        }
        String displayNameLowercase = DisplayNameUtil.getLowercaseDisplayNameForSearch(ingredient, ingredientHelper);
        Collection<String> aliases = ingredientManager.getIngredientAliases(value);
        if (aliases.isEmpty()) {
            this.names = List.of(displayNameLowercase);
        } else {
            this.names = new ArrayList<String>(1 + aliases.size());
            this.names.add(displayNameLowercase);
            for (String alias : aliases) {
                String lowercaseAlias = Translator.toLowercaseWithLocale(alias);
                this.names.add(lowercaseAlias);
            }
        }
    }

    @Override
    public List<String> getNames() {
        return this.names;
    }

    @Override
    public String getModNameForSorting() {
        return (String)this.modNames.getFirst();
    }

    @Override
    public Collection<String> getModNames(IIngredientFilterConfig config) {
        HashSet<String> modNames = new HashSet<String>(this.modNames);
        if (config.searchModIds().getValue().booleanValue()) {
            modNames.addAll(this.modIds);
        }
        if (config.searchModAliases().getValue().booleanValue()) {
            for (String modId : this.modIds) {
                Set<String> modAliases = this.modIdHelper.getModAliases(modId);
                modNames.addAll(modAliases);
            }
        }
        if (config.searchShortModNames().getValue().booleanValue()) {
            for (String modName : this.modNames) {
                List<String> shortModNames = ListElementInfo.getShortModNames(modName);
                modNames.addAll(shortModNames);
            }
        }
        HashSet<String> sanitizedModNames = new HashSet<String>();
        for (String modName : modNames) {
            modName = modName.toLowerCase(Locale.ROOT);
            modName = WHITESPACE_PATTERN.matcher(modName).replaceAll("");
            sanitizedModNames.add(modName);
        }
        return sanitizedModNames;
    }

    @Override
    public final @Unmodifiable Set<String> getTooltipStrings(IIngredientFilterConfig config, IIngredientManager ingredientManager) {
        ITypedIngredient<V> value = this.element.getTypedIngredient();
        IIngredientRenderer<V> ingredientRenderer = ingredientManager.getIngredientRenderer(value.getType());
        TooltipFlag.Default tooltipFlag = config.searchAdvancedTooltips().getValue() != false ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
        tooltipFlag = tooltipFlag.asCreative();
        TooltipFlag searchTooltipFlag = Services.PLATFORM.getInputHelper().getSearchTooltipFlag((TooltipFlag)tooltipFlag);
        List<Component> tooltip = SafeIngredientUtil.getPlainTooltipForSearch(ingredientManager, ingredientRenderer, value, searchTooltipFlag);
        Set<String> strings = ListElementInfo.getStrings(tooltip);
        strings.remove(this.names.getFirst());
        strings.remove(((String)this.modNames.getFirst()).toLowerCase(Locale.ENGLISH));
        strings.remove(this.modIds.getFirst());
        strings.remove(this.resourceLocation.getPath());
        return strings;
    }

    public static Set<String> getStrings(@Unmodifiable List<Component> tooltip) {
        HashSet<String> result = new HashSet<String>();
        for (FormattedText formattedText : tooltip) {
            String string = formattedText.getString();
            string = StringUtil.removeChatFormatting(string);
            string = Translator.toLowercaseWithLocale(string);
            ListElementInfo.addSplitStrings(result, string);
        }
        return result;
    }

    private static void addSplitStrings(Set<String> result, String string) {
        String[] strings;
        if ((string = string.trim()).isEmpty()) {
            return;
        }
        for (String splitString : strings = WHITESPACE_PATTERN.split(string)) {
            if (splitString.isEmpty()) continue;
            result.add(splitString);
        }
    }

    private static List<String> getShortModNames(String modName) {
        String[] words = MOD_NAME_SEPARATOR_PATTERN.split(modName);
        if (words.length <= 1) {
            return List.of();
        }
        return List.of(ListElementInfo.combineFirstLetters(words, 1), ListElementInfo.combineFirstLetters(words, 2));
    }

    private static String combineFirstLetters(String[] words, int count) {
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            int end = Math.min(count, word.length());
            sb.append(word, 0, end);
        }
        return sb.toString();
    }

    @Override
    public Collection<String> getTagStrings(IIngredientManager ingredientManager) {
        ITypedIngredient<V> value = this.element.getTypedIngredient();
        IIngredientHelper<V> ingredientHelper = ingredientManager.getIngredientHelper(value.getType());
        return ingredientHelper.getTagStream(value.getIngredient()).map(ResourceLocation::getPath).toList();
    }

    @Override
    public Stream<ResourceLocation> getTagIds(IIngredientManager ingredientManager) {
        ITypedIngredient<V> value = this.element.getTypedIngredient();
        IIngredientHelper<V> ingredientHelper = ingredientManager.getIngredientHelper(value.getType());
        return ingredientHelper.getTagStream(value.getIngredient());
    }

    @Override
    public Iterable<Integer> getColors(IIngredientManager ingredientManager) {
        ITypedIngredient<V> value = this.element.getTypedIngredient();
        IIngredientHelper<V> ingredientHelper = ingredientManager.getIngredientHelper(value.getType());
        V ingredient = value.getIngredient();
        return ingredientHelper.getColors(ingredient);
    }

    @Override
    public @Unmodifiable Collection<String> getColorNames(IIngredientManager ingredientManager, IColorHelper colorHelper) {
        Iterable<Integer> colors = this.getColors(ingredientManager);
        return StreamSupport.stream(colors.spliterator(), false).map(colorHelper::getClosestColorName).map(Translator::toLowercaseWithLocale).distinct().toList();
    }

    @Override
    public @Unmodifiable Collection<String> getCreativeTabsStrings(IIngredientManager ingredientManager) {
        ItemStack itemStack = this.element.getTypedIngredient().getItemStack().orElse(ItemStack.EMPTY);
        if (itemStack.isEmpty()) {
            return List.of();
        }
        HashSet<String> creativeTabStrings = new HashSet<String>();
        for (CreativeModeTab itemGroup : CreativeModeTabs.allTabs()) {
            if (!itemGroup.shouldDisplay() || itemGroup.getType() != CreativeModeTab.Type.CATEGORY || !itemGroup.contains(itemStack)) continue;
            String name = itemGroup.getDisplayName().getString();
            name = StringUtil.removeChatFormatting(name);
            name = Translator.toLowercaseWithLocale(name);
            ListElementInfo.addSplitStrings(creativeTabStrings, name);
        }
        return creativeTabStrings;
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return this.resourceLocation;
    }

    @Override
    public IListElement<V> getElement() {
        return this.element;
    }

    @Override
    public ITypedIngredient<V> getTypedIngredient() {
        return this.element.getTypedIngredient();
    }

    @Override
    public int getCreatedIndex() {
        return this.element.getCreatedIndex();
    }
}


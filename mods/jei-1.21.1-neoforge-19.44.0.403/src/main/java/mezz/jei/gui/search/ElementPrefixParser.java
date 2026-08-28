/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.chars.Char2ObjectMap
 *  it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap
 */
package mezz.jei.gui.search;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.helpers.IColorHelper;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.search.ISearchStorageBuilderFactory;
import mezz.jei.common.config.IIngredientFilterConfig;
import mezz.jei.common.search.LimitedStringStorageBuilder;
import mezz.jei.common.search.PrefixInfo;
import mezz.jei.common.search.SearchMode;
import mezz.jei.gui.ingredients.IListElement;
import mezz.jei.gui.ingredients.IListElementInfo;

public class ElementPrefixParser {
    private final Char2ObjectMap<PrefixInfo<IListElementInfo<?>, IListElement<?>>> map = new Char2ObjectOpenHashMap();
    private final PrefixInfo<IListElementInfo<?>, IListElement<?>> noPrefix;

    public ElementPrefixParser(IIngredientManager ingredientManager, IIngredientFilterConfig config, IColorHelper colorHelper, ISearchStorageBuilderFactory searchStorageBuilderFactory) {
        ISearchStorageBuilderFactory limitedStringStorageBuilderFactory = ElementPrefixParser.createLimitedStringStorageBuilderFactory(searchStorageBuilderFactory);
        this.noPrefix = new PrefixInfo("unprefixed", '\u0000', () -> SearchMode.ENABLED, IListElementInfo::getNames, searchStorageBuilderFactory);
        this.addPrefix(new PrefixInfo("mod_names", '@', config.modNameSearchMode()::getValue, info -> info.getModNames(config), limitedStringStorageBuilderFactory));
        this.addPrefix(new PrefixInfo("tags", '#', config.tagSearchMode()::getValue, e -> e.getTagStrings(ingredientManager), limitedStringStorageBuilderFactory));
        this.addPrefix(new PrefixInfo("tooltips", '$', config.tooltipSearchMode()::getValue, e -> e.getTooltipStrings(config, ingredientManager), searchStorageBuilderFactory));
        this.addPrefix(new PrefixInfo("creative_tabs", '%', config.creativeTabSearchMode()::getValue, e -> e.getCreativeTabsStrings(ingredientManager), limitedStringStorageBuilderFactory));
        this.addPrefix(new PrefixInfo("colors", '^', config.colorSearchMode()::getValue, e -> e.getColorNames(ingredientManager, colorHelper), limitedStringStorageBuilderFactory));
        this.addPrefix(new PrefixInfo("identifiers", '&', config.resourceLocationSearchMode()::getValue, element -> List.of(element.getResourceLocation().toString()), searchStorageBuilderFactory));
    }

    private static ISearchStorageBuilderFactory createLimitedStringStorageBuilderFactory(final ISearchStorageBuilderFactory searchStorageBuilderFactory) {
        return new ISearchStorageBuilderFactory(){

            public <T> LimitedStringStorageBuilder<T> create() {
                return new LimitedStringStorageBuilder(searchStorageBuilderFactory);
            }

            public <T> LimitedStringStorageBuilder<T> create(String id) {
                return new LimitedStringStorageBuilder(searchStorageBuilderFactory, id);
            }
        };
    }

    private void addPrefix(PrefixInfo<IListElementInfo<?>, IListElement<?>> info) {
        this.map.put(info.getPrefix(), info);
    }

    public Collection<PrefixInfo<IListElementInfo<?>, IListElement<?>>> allPrefixInfos() {
        ArrayList values = new ArrayList((Collection<PrefixInfo<IListElementInfo<?>, IListElement<?>>>)this.map.values());
        values.add(this.noPrefix);
        return values;
    }

    public PrefixInfo<IListElementInfo<?>, IListElement<?>> getNoPrefix() {
        return this.noPrefix;
    }

    public Optional<TokenInfo> parseToken(String token) {
        if (token.isEmpty()) {
            return Optional.empty();
        }
        char firstChar = token.charAt(0);
        PrefixInfo prefixInfo = (PrefixInfo)this.map.get(firstChar);
        if (prefixInfo == null || prefixInfo.getMode() == SearchMode.DISABLED) {
            return Optional.of(new TokenInfo(token, this.noPrefix));
        }
        if (token.length() == 1) {
            return Optional.empty();
        }
        return Optional.of(new TokenInfo(token.substring(1), prefixInfo));
    }

    public record TokenInfo(String token, PrefixInfo<IListElementInfo<?>, IListElement<?>> prefixInfo) {
    }
}


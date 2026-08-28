/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  net.caffeinemc.mods.sodium.client.config.structure.Option
 *  net.caffeinemc.mods.sodium.client.config.structure.OptionGroup
 *  net.caffeinemc.mods.sodium.client.config.structure.Page
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import me.flashyreese.mods.reeses_sodium_options.client.gui.option.OptionExtended;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.SearchResultEntry;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.SearchResultOrder;
import net.caffeinemc.mods.sodium.client.config.structure.Option;
import net.caffeinemc.mods.sodium.client.config.structure.OptionGroup;
import net.caffeinemc.mods.sodium.client.config.structure.Page;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

final class PageLayout {
    static final int ROW_HEIGHT = 18;
    private static final int GROUP_PADDING = 4;
    private final List<Row> rows;
    private final int contentHeight;

    private PageLayout(List<Row> rows, int contentHeight) {
        this.rows = List.copyOf(rows);
        this.contentHeight = contentHeight;
    }

    static PageLayout create(Page page, boolean searchActive, List<SearchResultEntry> results, SearchResultOrder resultOrder, boolean hideDisabledOptions, boolean collapsible, Set<ResourceLocation> collapsedGroups) {
        if (searchActive) {
            return PageLayout.createSearchLayout(PageLayout.buildSearchEntries(page, results, resultOrder, hideDisabledOptions));
        }
        return PageLayout.createPageLayout(page, hideDisabledOptions, collapsible, collapsedGroups);
    }

    List<Row> rows() {
        return this.rows;
    }

    int contentHeight() {
        return this.contentHeight;
    }

    private static PageLayout createSearchLayout(List<SearchEntry> searchEntries) {
        ArrayList<Row> rows = new ArrayList<Row>();
        int y = 0;
        OptionGroup lastGroup = null;
        for (SearchEntry entry : searchEntries) {
            OptionGroup group = entry.group();
            if (group != lastGroup) {
                if (lastGroup != null) {
                    y += 4;
                }
                if (PageLayout.hasLabel(group)) {
                    int labelY = y + 4;
                    rows.add(new LabelRow(group, group.name(), labelY, null, false));
                    y = labelY + 18;
                }
                lastGroup = group;
            }
            rows.add(new OptionRow(group, entry.option(), y));
            y += 18;
        }
        return new PageLayout(rows, y += 4);
    }

    private static PageLayout createPageLayout(Page page, boolean hideDisabledOptions, boolean collapsible, Set<ResourceLocation> collapsedGroups) {
        ArrayList<Row> rows = new ArrayList<Row>();
        List<VisibleGroup> groups = PageLayout.visibleGroups((List<OptionGroup>)page.groups(), hideDisabledOptions);
        int y = 0;
        for (int i = 0; i < groups.size(); ++i) {
            boolean collapsed;
            VisibleGroup visibleGroup = groups.get(i);
            OptionGroup group = visibleGroup.group();
            ResourceLocation collapseKey = collapsible ? PageLayout.groupCollapseKey(group) : null;
            boolean bl = collapsed = collapseKey != null && collapsedGroups.contains(collapseKey);
            if (PageLayout.hasLabel(group)) {
                int labelY = y + (i == 0 ? 0 : 4);
                rows.add(new LabelRow(group, group.name(), labelY, collapseKey, collapsed));
                y = labelY + 18;
            }
            if (!collapsed) {
                for (Option option : visibleGroup.options()) {
                    rows.add(new OptionRow(group, option, y));
                    y += 18;
                }
            }
            if (i >= groups.size() - 1) continue;
            y += 4;
        }
        return new PageLayout(rows, y);
    }

    private static List<VisibleGroup> visibleGroups(List<OptionGroup> groups, boolean hideDisabledOptions) {
        if (!hideDisabledOptions) {
            return groups.stream().map(group -> new VisibleGroup((OptionGroup)group, group.options())).toList();
        }
        ArrayList<VisibleGroup> visibleGroups = new ArrayList<VisibleGroup>();
        for (OptionGroup group2 : groups) {
            List<Option> options = group2.options().stream().filter(Option::isEnabled).toList();
            if (options.isEmpty()) continue;
            visibleGroups.add(new VisibleGroup(group2, options));
        }
        return visibleGroups;
    }

    @Nullable
    private static ResourceLocation groupCollapseKey(OptionGroup group) {
        for (Option option : group.options()) {
            if (!(option instanceof OptionExtended)) continue;
            OptionExtended optionExtended = (OptionExtended)option;
            return optionExtended.rso$getId();
        }
        return null;
    }

    private static boolean hasLabel(OptionGroup group) {
        return group.name() != null && !group.name().getString().isEmpty();
    }

    private static List<SearchEntry> buildSearchEntries(Page page, List<SearchResultEntry> results, SearchResultOrder resultOrder, boolean hideDisabledOptions) {
        if (results.isEmpty()) {
            return List.of();
        }
        return switch (resultOrder) {
            default -> throw new MatchException(null, null);
            case SearchResultOrder.PAGE_DISPLAY -> PageLayout.buildSearchEntriesInPageOrder(page, results, hideDisabledOptions);
            case SearchResultOrder.RANKED -> PageLayout.buildSearchEntriesInResultOrder(page, results, hideDisabledOptions);
        };
    }

    private static List<SearchEntry> buildSearchEntriesInPageOrder(Page page, List<SearchResultEntry> results, boolean hideDisabledOptions) {
        Set resultOptions = Collections.newSetFromMap(new IdentityHashMap());
        results.forEach(result -> resultOptions.add(result.option()));
        ArrayList<SearchEntry> entries = new ArrayList<SearchEntry>();
        for (OptionGroup group : page.groups()) {
            for (Option option : group.options()) {
                if (!resultOptions.contains(option) || !PageLayout.shouldShowOption(option, hideDisabledOptions)) continue;
                entries.add(new SearchEntry(group, option));
            }
        }
        return entries;
    }

    private static List<SearchEntry> buildSearchEntriesInResultOrder(Page page, List<SearchResultEntry> results, boolean hideDisabledOptions) {
        IdentityHashMap<Option, SearchEntry> entriesByOption = new IdentityHashMap<Option, SearchEntry>();
        for (OptionGroup group : page.groups()) {
            for (Option option : group.options()) {
                if (!PageLayout.shouldShowOption(option, hideDisabledOptions)) continue;
                entriesByOption.put(option, new SearchEntry(group, option));
            }
        }
        ArrayList<SearchEntry> ordered = new ArrayList<SearchEntry>(results.size());
        for (SearchResultEntry result : results) {
            SearchEntry entry = (SearchEntry)entriesByOption.get(result.option());
            if (entry == null) continue;
            ordered.add(entry);
        }
        return ordered;
    }

    private static boolean shouldShowOption(Option option, boolean hideDisabledOptions) {
        return !hideDisabledOptions || option.isEnabled();
    }

    private record SearchEntry(OptionGroup group, Option option) {
    }

    record LabelRow(OptionGroup group, Component text, int y, @Nullable ResourceLocation collapseKey, boolean collapsed) implements Row
    {
        boolean collapsible() {
            return this.collapseKey != null;
        }
    }

    record OptionRow(OptionGroup group, Option option, int y) implements Row
    {
    }

    private record VisibleGroup(OptionGroup group, List<Option> options) {
    }

    static interface Row {
        public int y();
    }
}


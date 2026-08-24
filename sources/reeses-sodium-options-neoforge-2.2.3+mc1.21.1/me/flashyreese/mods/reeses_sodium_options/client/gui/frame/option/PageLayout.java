package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option;

import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
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
   private final List<PageLayout.Row> rows;
   private final int contentHeight;

   private PageLayout(List<PageLayout.Row> rows, int contentHeight) {
      this.rows = List.copyOf(rows);
      this.contentHeight = contentHeight;
   }

   static PageLayout create(
      Page page,
      boolean searchActive,
      List<SearchResultEntry> results,
      SearchResultOrder resultOrder,
      boolean hideDisabledOptions,
      boolean collapsible,
      Set<ResourceLocation> collapsedGroups
   ) {
      return searchActive
         ? createSearchLayout(buildSearchEntries(page, results, resultOrder, hideDisabledOptions))
         : createPageLayout(page, hideDisabledOptions, collapsible, collapsedGroups);
   }

   List<PageLayout.Row> rows() {
      return this.rows;
   }

   int contentHeight() {
      return this.contentHeight;
   }

   private static PageLayout createSearchLayout(List<PageLayout.SearchEntry> searchEntries) {
      List<PageLayout.Row> rows = new ArrayList<>();
      int y = 0;
      OptionGroup lastGroup = null;

      for (PageLayout.SearchEntry entry : searchEntries) {
         OptionGroup group = entry.group();
         if (group != lastGroup) {
            if (lastGroup != null) {
               y += 4;
            }

            if (hasLabel(group)) {
               int labelY = y + 4;
               rows.add(new PageLayout.LabelRow(group, group.name(), labelY, null, false));
               y = labelY + 18;
            }

            lastGroup = group;
         }

         rows.add(new PageLayout.OptionRow(group, entry.option(), y));
         y += 18;
      }

      y += 4;
      return new PageLayout(rows, y);
   }

   private static PageLayout createPageLayout(Page page, boolean hideDisabledOptions, boolean collapsible, Set<ResourceLocation> collapsedGroups) {
      List<PageLayout.Row> rows = new ArrayList<>();
      List<PageLayout.VisibleGroup> groups = visibleGroups(page.groups(), hideDisabledOptions);
      int y = 0;

      for (int i = 0; i < groups.size(); i++) {
         PageLayout.VisibleGroup visibleGroup = groups.get(i);
         OptionGroup group = visibleGroup.group();
         ResourceLocation collapseKey = collapsible ? groupCollapseKey(group) : null;
         boolean collapsed = collapseKey != null && collapsedGroups.contains(collapseKey);
         if (hasLabel(group)) {
            int labelY = y + (i == 0 ? 0 : 4);
            rows.add(new PageLayout.LabelRow(group, group.name(), labelY, collapseKey, collapsed));
            y = labelY + 18;
         }

         if (!collapsed) {
            for (Option option : visibleGroup.options()) {
               rows.add(new PageLayout.OptionRow(group, option, y));
               y += 18;
            }
         }

         if (i < groups.size() - 1) {
            y += 4;
         }
      }

      return new PageLayout(rows, y);
   }

   private static List<PageLayout.VisibleGroup> visibleGroups(List<OptionGroup> groups, boolean hideDisabledOptions) {
      if (!hideDisabledOptions) {
         return groups.stream().map(groupx -> new PageLayout.VisibleGroup(groupx, groupx.options())).toList();
      } else {
         List<PageLayout.VisibleGroup> visibleGroups = new ArrayList<>();

         for (OptionGroup group : groups) {
            List<Option> options = group.options().stream().filter(Option::isEnabled).toList();
            if (!options.isEmpty()) {
               visibleGroups.add(new PageLayout.VisibleGroup(group, options));
            }
         }

         return visibleGroups;
      }
   }

   @Nullable
   private static ResourceLocation groupCollapseKey(OptionGroup group) {
      for (Option option : group.options()) {
         if (option instanceof OptionExtended optionExtended) {
            return optionExtended.rso$getId();
         }
      }

      return null;
   }

   private static boolean hasLabel(OptionGroup group) {
      return group.name() != null && !group.name().getString().isEmpty();
   }

   private static List<PageLayout.SearchEntry> buildSearchEntries(
      Page page, List<SearchResultEntry> results, SearchResultOrder resultOrder, boolean hideDisabledOptions
   ) {
      if (results.isEmpty()) {
         return List.of();
      } else {
         return switch (resultOrder) {
            case PAGE_DISPLAY -> buildSearchEntriesInPageOrder(page, results, hideDisabledOptions);
            case RANKED -> buildSearchEntriesInResultOrder(page, results, hideDisabledOptions);
         };
      }
   }

   private static List<PageLayout.SearchEntry> buildSearchEntriesInPageOrder(Page page, List<SearchResultEntry> results, boolean hideDisabledOptions) {
      Set<Option> resultOptions = Collections.newSetFromMap(new IdentityHashMap<>());
      results.forEach(result -> resultOptions.add(result.option()));
      List<PageLayout.SearchEntry> entries = new ArrayList<>();
      UnmodifiableIterator var5 = page.groups().iterator();

      while (var5.hasNext()) {
         OptionGroup group = (OptionGroup)var5.next();

         for (Option option : group.options()) {
            if (resultOptions.contains(option) && shouldShowOption(option, hideDisabledOptions)) {
               entries.add(new PageLayout.SearchEntry(group, option));
            }
         }
      }

      return entries;
   }

   private static List<PageLayout.SearchEntry> buildSearchEntriesInResultOrder(Page page, List<SearchResultEntry> results, boolean hideDisabledOptions) {
      Map<Option, PageLayout.SearchEntry> entriesByOption = new IdentityHashMap<>();
      UnmodifiableIterator ordered = page.groups().iterator();

      while (ordered.hasNext()) {
         OptionGroup group = (OptionGroup)ordered.next();

         for (Option option : group.options()) {
            if (shouldShowOption(option, hideDisabledOptions)) {
               entriesByOption.put(option, new PageLayout.SearchEntry(group, option));
            }
         }
      }

      List<PageLayout.SearchEntry> orderedx = new ArrayList<>(results.size());

      for (SearchResultEntry result : results) {
         PageLayout.SearchEntry entry = entriesByOption.get(result.option());
         if (entry != null) {
            orderedx.add(entry);
         }
      }

      return orderedx;
   }

   private static boolean shouldShowOption(Option option, boolean hideDisabledOptions) {
      return !hideDisabledOptions || option.isEnabled();
   }

   record LabelRow(OptionGroup group, Component text, int y, @Nullable ResourceLocation collapseKey, boolean collapsed) implements PageLayout.Row {
      boolean collapsible() {
         return this.collapseKey != null;
      }
   }

   record OptionRow(OptionGroup group, Option option, int y) implements PageLayout.Row {
   }

   interface Row {
      int y();
   }

   private record SearchEntry(OptionGroup group, Option option) {
   }

   private record VisibleGroup(OptionGroup group, List<Option> options) {
   }
}

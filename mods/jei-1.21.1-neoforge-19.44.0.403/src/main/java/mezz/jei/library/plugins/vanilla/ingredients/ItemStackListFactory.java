/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.multiplayer.ClientPacketListener
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.chat.ComponentContents
 *  net.minecraft.network.chat.contents.TranslatableContents
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.flag.FeatureFlagSet
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.CreativeModeTab$ItemDisplayParameters
 *  net.minecraft.world.item.CreativeModeTab$Type
 *  net.minecraft.world.item.CreativeModeTabs
 *  net.minecraft.world.item.ItemStack
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.plugins.vanilla.ingredients;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.common.Internal;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.config.IJeiClientConfigs;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.common.util.RegistryUtil;
import mezz.jei.common.util.StackHelper;
import mezz.jei.library.plugins.vanilla.ingredients.ItemStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class ItemStackListFactory {
    private static final Logger LOGGER = LogManager.getLogger();

    public static List<ItemStack> create(StackHelper stackHelper, ItemStackHelper itemStackHelper) {
        IJeiClientConfigs jeiClientConfigs = Internal.getJeiClientConfigs();
        IClientConfig clientConfig = jeiClientConfigs.getClientConfig();
        boolean showHidden = clientConfig.showHiddenIngredients().getValue();
        ArrayList<ItemStack> itemList = new ArrayList<ItemStack>();
        HashSet<Object> itemUidSet = new HashSet<Object>();
        Minecraft minecraft = Minecraft.getInstance();
        FeatureFlagSet features = Optional.ofNullable(minecraft.player).map(p -> p.connection).map(ClientPacketListener::enabledFeatures).orElse(FeatureFlagSet.of());
        boolean hasOperatorItemsTabPermissions = showHidden || (Boolean)minecraft.options.operatorItemsTab().get() != false || Optional.of(minecraft).map(m -> m.player).map(Player::canUseGameMasterBlocks).orElse(false) != false;
        ClientLevel level = minecraft.level;
        if (level == null) {
            throw new NullPointerException("minecraft.level must be set before JEI fetches ingredients");
        }
        RegistryAccess registryAccess = level.registryAccess();
        CreativeModeTab.ItemDisplayParameters displayParameters = new CreativeModeTab.ItemDisplayParameters(features, hasOperatorItemsTabPermissions, (HolderLookup.Provider)registryAccess);
        for (CreativeModeTab tab : CreativeModeTabs.allTabs()) {
            Collection searchTabDisplayItems;
            Collection displayItems;
            if (tab.getType() != CreativeModeTab.Type.CATEGORY) {
                LOGGER.debug("Skipping creative tab: '{}' because it is type: {}", (Object)tab.getDisplayName().getString(), (Object)tab.getType());
                continue;
            }
            try {
                tab.buildContents(displayParameters);
            }
            catch (LinkageError | RuntimeException e) {
                LOGGER.error("Item Group crashed while building contents.Items from this group will be missing from the JEI ingredient list: {}", (Object)tab.getDisplayName().getString(), (Object)e);
                continue;
            }
            try {
                displayItems = tab.getDisplayItems();
                searchTabDisplayItems = tab.getSearchTabDisplayItems();
            }
            catch (LinkageError | RuntimeException e) {
                LOGGER.error("Item Group crashed while getting search tab display items.Some items from this group will be missing from the JEI ingredient list: {}", (Object)tab.getDisplayName().getString(), (Object)e);
                continue;
            }
            if (displayItems.isEmpty() && searchTabDisplayItems.isEmpty()) {
                Level logLevel = ItemStackListFactory.isKnownEmptyTab(tab) ? Level.DEBUG : Level.WARN;
                LOGGER.log(logLevel, "Item Group has no display items and no search tab display items. Items from this group will be missing from the JEI ingredient list. {}", (Object)tab.getDisplayName().getString());
                continue;
            }
            ItemStackListFactory.addFromTab(displayItems, "displayItems", tab, stackHelper, itemStackHelper, itemList, itemUidSet);
            if (displayItems.equals(searchTabDisplayItems)) continue;
            ItemStackListFactory.addFromTab(searchTabDisplayItems, "searchTabDisplayItems", tab, stackHelper, itemStackHelper, itemList, itemUidSet);
        }
        if (showHidden) {
            ItemStackListFactory.addItemsFromRegistries(stackHelper, itemList, itemUidSet, features);
        }
        return itemList;
    }

    private static boolean isKnownEmptyTab(CreativeModeTab tab) {
        TranslatableContents translatableContents;
        ComponentContents componentContents = tab.getDisplayName().getContents();
        return componentContents instanceof TranslatableContents && (translatableContents = (TranslatableContents)componentContents).getKey().equals("itemGroup.op");
    }

    private static void addFromTab(Collection<ItemStack> tabDisplayItems, String displayType, CreativeModeTab tab, StackHelper stackHelper, ItemStackHelper itemStackHelper, List<ItemStack> itemList, Set<Object> itemUidSet) {
        HashSet<Object> tabUidSet = new HashSet<Object>();
        int added = 0;
        HashSet<Object> duplicateInTab = new HashSet<Object>();
        int duplicateInTabCount = 0;
        for (ItemStack itemStack : tabDisplayItems) {
            String errorInfo;
            if (itemStack.isEmpty()) {
                errorInfo = itemStackHelper.getErrorInfo(itemStack);
                LOGGER.error("Found an empty itemStack in '{}' creative tab's {}: {}", (Object)tab, (Object)displayType, (Object)errorInfo);
                continue;
            }
            if (!itemStackHelper.isValidIngredient(itemStack)) {
                errorInfo = itemStackHelper.getErrorInfo(itemStack);
                LOGGER.error("Ignoring ingredient in '{}' creative tab's {} that is considered invalid: {}", (Object)tab, (Object)displayType, (Object)errorInfo);
                continue;
            }
            if (!itemStackHelper.isIngredientOnServer(itemStack)) {
                errorInfo = itemStackHelper.getErrorInfo(itemStack);
                LOGGER.warn("Ignoring ingredient in '{}' creative tab's {} that isn't on the server: {}", (Object)tab, (Object)displayType, (Object)errorInfo);
                continue;
            }
            Object itemKey = ItemStackListFactory.safeGetUid(stackHelper, itemStack);
            if (itemKey == null) continue;
            if (tabUidSet.contains(itemKey)) {
                duplicateInTab.add(itemKey);
                ++duplicateInTabCount;
            }
            if (!itemUidSet.add(itemKey)) continue;
            tabUidSet.add(itemKey);
            itemList.add(itemStack);
            ++added;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Added {}/{} new items from '{}' creative tab's {}", (Object)StringUtils.leftPad((String)Integer.toString(added), (int)4, (char)' '), (Object)StringUtils.leftPad((String)Integer.toString(tabDisplayItems.size()), (int)4, (char)' '), (Object)tab.getDisplayName().getString(), (Object)displayType);
        }
        if (duplicateInTabCount > 0) {
            Level level = Services.PLATFORM.getModHelper().isInDev() ? Level.WARN : Level.DEBUG;
            LOGGER.log(level, "{} duplicate items were found in '{}' creative tab's: {}\nThis may indicate that these types of item need a subtype interpreter added to JEI:\n{}", (Object)duplicateInTabCount, (Object)tab.getDisplayName().getString(), (Object)displayType, (Object)duplicateInTab.stream().map(Object::toString).collect(Collectors.joining(", ", "[", "]")));
        }
    }

    private static void addItemsFromRegistries(StackHelper stackHelper, List<ItemStack> itemList, Set<Object> itemUidSet, FeatureFlagSet features) {
        Object itemKey;
        List<ItemStack> itemStacks = RegistryUtil.getRegistry(Registries.ITEM).asLookup().filterFeatures(features).listElements().map(ItemStack::new).filter(i -> !i.isEmpty()).toList();
        int added = 0;
        for (ItemStack itemStack : itemStacks) {
            itemKey = ItemStackListFactory.safeGetUid(stackHelper, itemStack);
            if (itemKey == null || !itemUidSet.add(itemKey)) continue;
            itemList.add(itemStack);
            ++added;
        }
        LOGGER.debug("Added {}/{} new items from the item registry (this is run because ShowHiddenItems is set to true in JEI's config)", (Object)added, (Object)itemStacks.size());
        itemStacks = RegistryUtil.getRegistry(Registries.BLOCK).asLookup().filterFeatures(features).listElements().map(Holder.Reference::value).map(ItemStack::new).filter(i -> !i.isEmpty()).toList();
        added = 0;
        for (ItemStack itemStack : itemStacks) {
            itemKey = ItemStackListFactory.safeGetUid(stackHelper, itemStack);
            if (itemKey == null || !itemUidSet.add(itemKey)) continue;
            itemList.add(itemStack);
            ++added;
        }
        LOGGER.debug("Added {}/{} new items from the block registry (this is run because ShowHiddenItems is set to true in JEI's config)", (Object)added, (Object)itemStacks.size());
    }

    @Nullable
    private static Object safeGetUid(StackHelper stackHelper, ItemStack stack) {
        try {
            return stackHelper.getUidForStack(stack, UidContext.Ingredient);
        }
        catch (LinkageError | RuntimeException e) {
            String stackInfo = ErrorUtil.getItemStackInfo(stack);
            LOGGER.error("Couldn't get unique name for itemStack {}", (Object)stackInfo, (Object)e);
            return null;
        }
    }
}


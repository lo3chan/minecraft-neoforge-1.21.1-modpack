/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMaps
 *  it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap
 *  java.lang.MatchException
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.HolderSet$Named
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition
 *  net.minecraft.world.level.block.state.properties.Property
 */
package net.irisshaders.iris.shaderpack.materialmap;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.platform.IrisPlatformHelpers;
import net.irisshaders.iris.shaderpack.materialmap.BlockEntry;
import net.irisshaders.iris.shaderpack.materialmap.BlockRenderType;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import net.irisshaders.iris.shaderpack.materialmap.TagEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockMaterialMapping {
    public static Object2IntMap<BlockState> createBlockStateIdMap(Int2ObjectLinkedOpenHashMap<List<BlockEntry>> blockPropertiesMap, Int2ObjectLinkedOpenHashMap<List<TagEntry>> tagPropertiesMap) {
        Object2IntLinkedOpenHashMap blockStateIds = new Object2IntLinkedOpenHashMap();
        blockStateIds.defaultReturnValue(-1);
        blockPropertiesMap.forEach((arg_0, arg_1) -> BlockMaterialMapping.lambda$createBlockStateIdMap$0((Object2IntMap)blockStateIds, arg_0, arg_1));
        tagPropertiesMap.forEach((arg_0, arg_1) -> BlockMaterialMapping.lambda$createBlockStateIdMap$1((Object2IntMap)blockStateIds, arg_0, arg_1));
        return blockStateIds;
    }

    private static void addTag(TagEntry tagEntry, Object2IntMap<BlockState> idMap, int intId) {
        List<TagKey> compatibleTags = BuiltInRegistries.BLOCK.getTagNames().filter(t -> t.location().getNamespace().equalsIgnoreCase(tagEntry.id().getNamespace()) && t.location().getPath().equalsIgnoreCase(tagEntry.id().getName())).toList();
        if (compatibleTags.isEmpty()) {
            if (IrisPlatformHelpers.getInstance().isDevelopmentEnvironment()) {
                Iris.logger.warn("Failed to find the tag " + String.valueOf(tagEntry.id()));
            }
        } else if (compatibleTags.size() > 1) {
            Iris.logger.fatal("You've broke the system; congrats. More than one tag matched " + String.valueOf(tagEntry.id()));
        } else {
            ((HolderSet.Named)BuiltInRegistries.BLOCK.getTag((TagKey)compatibleTags.getFirst()).get()).forEach(block -> {
                Map<String, String> propertyPredicates = tagEntry.propertyPredicates();
                if (propertyPredicates.isEmpty()) {
                    for (BlockState state : ((Block)block.value()).getStateDefinition().getPossibleStates()) {
                        idMap.putIfAbsent((Object)state, intId);
                    }
                    return;
                }
                LinkedHashMap properties = new LinkedHashMap();
                StateDefinition stateManager = ((Block)block.value()).getStateDefinition();
                propertyPredicates.forEach((key, value) -> {
                    Property property = stateManager.getProperty(key);
                    if (property == null) {
                        Iris.logger.warn("Error while parsing the block ID map entry for tag \"block." + intId + "\":");
                        Iris.logger.warn("- The block " + String.valueOf(((ResourceKey)block.unwrapKey().get()).location()) + " has no property with the name " + key + ", ignoring!");
                        return;
                    }
                    properties.put((Property<?>)property, (String)value);
                });
                for (BlockState state : stateManager.getPossibleStates()) {
                    if (!BlockMaterialMapping.checkState(state, properties)) continue;
                    idMap.putIfAbsent((Object)state, intId);
                }
            });
        }
    }

    public static Map<Block, BlockRenderType> createBlockTypeMap(Map<NamespacedId, BlockRenderType> blockPropertiesMap) {
        if (blockPropertiesMap.isEmpty()) {
            return Object2ObjectMaps.emptyMap();
        }
        Reference2ReferenceOpenHashMap blockTypeIds = new Reference2ReferenceOpenHashMap();
        blockPropertiesMap.forEach((arg_0, arg_1) -> BlockMaterialMapping.lambda$createBlockTypeMap$5((Map)blockTypeIds, arg_0, arg_1));
        return blockTypeIds;
    }

    public static RenderType convertBlockToRenderType(BlockRenderType type) {
        if (type == null) {
            return null;
        }
        return switch (type) {
            default -> throw new MatchException(null, null);
            case BlockRenderType.SOLID -> RenderType.solid();
            case BlockRenderType.CUTOUT -> RenderType.cutout();
            case BlockRenderType.CUTOUT_MIPPED -> RenderType.cutoutMipped();
            case BlockRenderType.TRANSLUCENT -> RenderType.translucent();
        };
    }

    private static void addBlockStates(BlockEntry entry, Object2IntMap<BlockState> idMap, int intId) {
        ResourceLocation resourceLocation;
        NamespacedId id = entry.id();
        try {
            resourceLocation = ResourceLocation.fromNamespaceAndPath((String)id.getNamespace(), (String)id.getName());
        }
        catch (Exception exception) {
            throw new IllegalStateException("Failed to get entry for " + intId, exception);
        }
        Block block = (Block)BuiltInRegistries.BLOCK.get(resourceLocation);
        if (block == Blocks.AIR) {
            return;
        }
        Map<String, String> propertyPredicates = entry.propertyPredicates();
        if (propertyPredicates.isEmpty()) {
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                idMap.putIfAbsent((Object)state, intId);
            }
            return;
        }
        LinkedHashMap properties = new LinkedHashMap();
        StateDefinition stateManager = block.getStateDefinition();
        propertyPredicates.forEach((key, value) -> {
            Property property = stateManager.getProperty(key);
            if (property == null) {
                Iris.logger.warn("Error while parsing the block ID map entry for \"block." + intId + "\":");
                Iris.logger.warn("- The block " + String.valueOf(resourceLocation) + " has no property with the name " + key + ", ignoring!");
                return;
            }
            properties.put((Property<?>)property, (String)value);
        });
        for (BlockState state : stateManager.getPossibleStates()) {
            if (!BlockMaterialMapping.checkState(state, properties)) continue;
            idMap.putIfAbsent((Object)state, intId);
        }
    }

    private static boolean checkState(BlockState state, Map<Property<?>, String> expectedValues) {
        for (Map.Entry<Property<?>, String> condition : expectedValues.entrySet()) {
            String actualValue;
            Property<?> property = condition.getKey();
            String expectedValue = condition.getValue();
            if (expectedValue.equals(actualValue = property.getName(state.getValue(property)))) continue;
            return false;
        }
        return true;
    }

    private static /* synthetic */ void lambda$createBlockTypeMap$5(Map blockTypeIds, NamespacedId id, BlockRenderType blockType) {
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath((String)id.getNamespace(), (String)id.getName());
        Block block = (Block)BuiltInRegistries.BLOCK.get(resourceLocation);
        blockTypeIds.put(block, blockType);
    }

    private static /* synthetic */ void lambda$createBlockStateIdMap$1(Object2IntMap blockStateIds, Integer intId, List entries) {
        for (TagEntry entry : entries) {
            BlockMaterialMapping.addTag(entry, (Object2IntMap<BlockState>)blockStateIds, intId);
        }
    }

    private static /* synthetic */ void lambda$createBlockStateIdMap$0(Object2IntMap blockStateIds, Integer intId, List entries) {
        for (BlockEntry entry : entries) {
            BlockMaterialMapping.addBlockStates(entry, (Object2IntMap<BlockState>)blockStateIds, intId);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.TagParser
 *  net.minecraft.world.item.ItemStack
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Unmodifiable
 */
package mezz.jei.gui.config;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.config.IJeiConfigValueSerializer;
import mezz.jei.common.config.file.serializers.LegacyTypedIngredientSerializer;
import mezz.jei.common.util.LoggedTimer;
import mezz.jei.common.util.ServerConfigPathUtil;
import mezz.jei.gui.bookmarks.BookmarkFactory;
import mezz.jei.gui.bookmarks.IBookmark;
import mezz.jei.gui.bookmarks.IngredientBookmark;
import mezz.jei.gui.bookmarks.RecipeBookmark;
import mezz.jei.gui.config.file.serializers.LegacyRecipeBookmarkSerializer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Unmodifiable;

@Deprecated
public class LegacyBookmarkConfig {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final LoggedTimer TIMER = new LoggedTimer();
    private static final String MARKER_STACK = "T:";
    private static final String MARKER_INGREDIENT = "I:";
    private static final String LEGACY_MARKER_OTHER = "O:";
    private static final String MARKER_RECIPE = "R:";
    private final Path jeiConfigurationDir;

    public static Optional<Path> getPath(Path jeiConfigurationDir) {
        return ServerConfigPathUtil.getWorldPath(jeiConfigurationDir).flatMap(configPath -> {
            try {
                Files.createDirectories(configPath, new FileAttribute[0]);
            }
            catch (IOException e) {
                LOGGER.error("Unable to create bookmark config folder: {}", configPath);
                return Optional.empty();
            }
            Path path = configPath.resolve("bookmarks.ini");
            return Optional.of(path);
        });
    }

    public LegacyBookmarkConfig(Path jeiConfigurationDir) {
        this.jeiConfigurationDir = jeiConfigurationDir;
    }

    public @Unmodifiable List<IBookmark> loadBookmarks(IRecipeManager recipeManager, IFocusFactory focusFactory, IIngredientManager ingredientManager, RegistryAccess registryAccess, BookmarkFactory bookmarkFactory) {
        return LegacyBookmarkConfig.getPath(this.jeiConfigurationDir).map(path -> {
            List<String> lines;
            if (!Files.exists(path, new LinkOption[0])) {
                return List.of();
            }
            try {
                lines = Files.readAllLines(path);
            }
            catch (IOException e) {
                LOGGER.error("Failed to load bookmarks from file {}", path, (Object)e);
                return List.of();
            }
            if (lines.isEmpty()) {
                return List.of();
            }
            TIMER.start(String.format("Converting %s legacy bookmarks", lines.size()));
            ArrayList<IBookmark> bookmarkList = new ArrayList<IBookmark>();
            LegacyTypedIngredientSerializer ingredientSerializer = new LegacyTypedIngredientSerializer(ingredientManager);
            LegacyRecipeBookmarkSerializer legacyRecipeBookmarkSerializer = new LegacyRecipeBookmarkSerializer(recipeManager, focusFactory, ingredientSerializer);
            List<IIngredientType<?>> otherIngredientTypes = ingredientManager.getRegisteredIngredientTypes().stream().filter(i -> !i.equals(VanillaTypes.ITEM_STACK)).toList();
            IIngredientHelper itemStackHelper = ingredientManager.getIngredientHelper(VanillaTypes.ITEM_STACK);
            for (String line : lines) {
                if (line.startsWith(MARKER_STACK)) {
                    String itemStackAsJson = line.substring(MARKER_STACK.length());
                    LegacyBookmarkConfig.loadItemStackBookmark(registryAccess, ingredientManager, itemStackAsJson, bookmarkList, bookmarkFactory);
                    continue;
                }
                if (line.startsWith(MARKER_INGREDIENT)) {
                    String serializedIngredient = line.substring(MARKER_INGREDIENT.length());
                    LegacyBookmarkConfig.loadIngredientBookmark(ingredientSerializer, serializedIngredient, bookmarkList, bookmarkFactory);
                    continue;
                }
                if (line.startsWith(LEGACY_MARKER_OTHER)) {
                    String uid = line.substring(LEGACY_MARKER_OTHER.length());
                    LegacyBookmarkConfig.loadLegacyIngredientBookmark(otherIngredientTypes, ingredientManager, uid, bookmarkList, bookmarkFactory);
                    continue;
                }
                if (line.startsWith(MARKER_RECIPE)) {
                    String serializedRecipe = line.substring(MARKER_RECIPE.length());
                    LegacyBookmarkConfig.loadRecipeBookmark(legacyRecipeBookmarkSerializer, serializedRecipe, bookmarkList);
                    continue;
                }
                LOGGER.error("Failed to load unknown bookmark type:\n{}", (Object)line);
            }
            TIMER.stop();
            return bookmarkList;
        }).orElseGet(List::of);
    }

    private static void loadItemStackBookmark(RegistryAccess registryAccess, IIngredientManager ingredientManager, String itemStackAsJson, List<IBookmark> bookmarkList, BookmarkFactory bookmarkFactory) {
        try {
            CompoundTag itemStackAsNbt = TagParser.parseTag((String)itemStackAsJson);
            ItemStack itemStack = ItemStack.parseOptional((HolderLookup.Provider)registryAccess, (CompoundTag)itemStackAsNbt);
            if (!itemStack.isEmpty()) {
                Optional<ITypedIngredient<ItemStack>> typedIngredient = ingredientManager.createTypedIngredient(VanillaTypes.ITEM_STACK, itemStack, true);
                if (typedIngredient.isEmpty()) {
                    LOGGER.warn("Failed to load bookmarked ItemStack from json string, the item no longer exists:\n{}", (Object)itemStackAsJson);
                } else {
                    IngredientBookmark<ItemStack> bookmark = bookmarkFactory.create(typedIngredient.get());
                    bookmarkList.add(bookmark);
                }
            } else {
                LOGGER.warn("Failed to load bookmarked ItemStack from json string, the item is empty:\n{}", (Object)itemStackAsJson);
            }
        }
        catch (CommandSyntaxException e) {
            LOGGER.error("Failed to load bookmarked ItemStack from json string:\n{}", (Object)itemStackAsJson, (Object)e);
        }
    }

    private static void loadIngredientBookmark(LegacyTypedIngredientSerializer ingredientSerializer, String serializedIngredient, List<IBookmark> bookmarkList, BookmarkFactory bookmarkFactory) {
        IJeiConfigValueSerializer.IDeserializeResult<ITypedIngredient<?>> deserialized = ingredientSerializer.deserialize(serializedIngredient);
        Optional<ITypedIngredient<?>> result = deserialized.getResult();
        if (result.isEmpty()) {
            List<String> errors = deserialized.getErrors();
            LOGGER.warn("Failed to load bookmarked ingredient from string: \n{}\n{}", (Object)serializedIngredient, (Object)String.join((CharSequence)", ", errors));
        } else {
            IngredientBookmark<?> bookmark = bookmarkFactory.create(result.get());
            bookmarkList.add(bookmark);
        }
    }

    private static void loadLegacyIngredientBookmark(Collection<IIngredientType<?>> otherIngredientTypes, IIngredientManager ingredientManager, String uid, List<IBookmark> bookmarkList, BookmarkFactory bookmarkFactory) {
        Optional<ITypedIngredient<?>> typedIngredient = LegacyBookmarkConfig.getLegacyNormalizedIngredientByUid(ingredientManager, otherIngredientTypes, uid);
        if (typedIngredient.isEmpty()) {
            LOGGER.error("Failed to load unknown bookmarked ingredient with uid:\n{}", (Object)uid);
        } else {
            IngredientBookmark<?> bookmark = bookmarkFactory.create(typedIngredient.get());
            bookmarkList.add(bookmark);
        }
    }

    private static void loadRecipeBookmark(LegacyRecipeBookmarkSerializer legacyRecipeBookmarkSerializer, String serializedRecipe, List<IBookmark> bookmarkList) {
        IJeiConfigValueSerializer.IDeserializeResult<RecipeBookmark<?, ?>> deserialized = legacyRecipeBookmarkSerializer.deserialize(serializedRecipe);
        Optional<RecipeBookmark<?, ?>> result = deserialized.getResult();
        if (result.isEmpty()) {
            List<String> errors = deserialized.getErrors();
            LOGGER.warn("Failed to load bookmarked recipe from string: \n{}\n{}", (Object)serializedRecipe, (Object)String.join((CharSequence)", ", errors));
        } else {
            bookmarkList.add(result.get());
        }
    }

    private static Optional<ITypedIngredient<?>> getLegacyNormalizedIngredientByUid(IIngredientManager ingredientManager, Collection<IIngredientType<?>> ingredientTypes, String uid) {
        return ingredientTypes.stream().map(t -> ingredientManager.getTypedIngredientByUid(t, uid)).flatMap(Optional::stream).findFirst();
    }
}


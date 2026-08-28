/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.resources.RegistryOps
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Unmodifiable
 */
package mezz.jei.gui.config;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.config.file.JsonArrayFileHelper;
import mezz.jei.common.util.DeduplicatingRunner;
import mezz.jei.common.util.PathUtil;
import mezz.jei.common.util.ServerConfigPathUtil;
import mezz.jei.gui.bookmarks.BookmarkFactory;
import mezz.jei.gui.bookmarks.BookmarkList;
import mezz.jei.gui.bookmarks.IBookmark;
import mezz.jei.gui.config.IBookmarkConfig;
import mezz.jei.gui.config.LegacyBookmarkConfig;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Unmodifiable;

public class BookmarkJsonConfig
implements IBookmarkConfig {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Duration SAVE_DELAY_TIME = Duration.ofSeconds(5L);
    private static final int VERSION = 2;
    private final LegacyBookmarkConfig legacyBookmarkConfig;
    private final Path jeiConfigurationDir;
    private final DeduplicatingRunner delayedSave = new DeduplicatingRunner(SAVE_DELAY_TIME);

    private static Optional<Path> getPath(Path jeiConfigurationDir) {
        return ServerConfigPathUtil.getWorldPath(jeiConfigurationDir).flatMap(configPath -> {
            try {
                Files.createDirectories(configPath, new FileAttribute[0]);
            }
            catch (IOException e) {
                LOGGER.error("Unable to create bookmark config folder: {}", configPath, (Object)e);
                return Optional.empty();
            }
            Path path = configPath.resolve("bookmarks.json");
            return Optional.of(path);
        });
    }

    public BookmarkJsonConfig(Path jeiConfigurationDir) {
        this.jeiConfigurationDir = jeiConfigurationDir;
        this.legacyBookmarkConfig = new LegacyBookmarkConfig(jeiConfigurationDir);
    }

    private RegistryOps<JsonElement> getRegistryOps(RegistryAccess registryAccess) {
        return registryAccess.createSerializationContext((DynamicOps)JsonOps.INSTANCE);
    }

    @Override
    public boolean saveBookmarks(IRecipeManager recipeManager, IFocusFactory focusFactory, IGuiHelper guiHelper, IIngredientManager ingredientManager, RegistryAccess registryAccess, ICodecHelper codecHelper, List<IBookmark> bookmarks, Codec<IBookmark> bookmarkCodec) {
        List<IBookmark> bookmarksSnapshot = List.copyOf(bookmarks);
        return BookmarkJsonConfig.getPath(this.jeiConfigurationDir).map(path -> {
            this.delayedSave.run(() -> this.save((Path)path, registryAccess, (Collection<IBookmark>)bookmarksSnapshot, bookmarkCodec));
            return true;
        }).orElse(false);
    }

    private boolean save(Path path, RegistryAccess registryAccess, Collection<IBookmark> bookmarks, Codec<IBookmark> bookmarkCodec) {
        RegistryOps<JsonElement> registryOps = this.getRegistryOps(registryAccess);
        try {
            JsonArrayFileHelper.write(path, 2, bookmarks, bookmarkCodec, registryOps, error -> LOGGER.error("Encountered an error when saving the bookmarks config to file {}\n{}", (Object)path, error), (element, exception) -> LOGGER.error("Encountered an exception when saving the bookmarks config to file {}\n{}", (Object)path, element, exception));
            LOGGER.debug("Saved bookmarks config to file: {}", (Object)path);
            return true;
        }
        catch (IOException | RuntimeException e) {
            LOGGER.error("Failed to save bookmarks config to file {}", (Object)path, (Object)e);
            return false;
        }
    }

    @Override
    public void loadBookmarks(IRecipeManager recipeManager, IFocusFactory focusFactory, IGuiHelper guiHelper, IIngredientManager ingredientManager, RegistryAccess registryAccess, BookmarkList bookmarkList, ICodecHelper codecHelper, Codec<IBookmark> bookmarkCodec, BookmarkFactory bookmarkFactory) {
        RegistryOps<JsonElement> registryOps = this.getRegistryOps(registryAccess);
        List<IBookmark> bookmarks = this.loadJsonBookmarks(ingredientManager, recipeManager, registryOps, codecHelper, bookmarkCodec);
        List<IBookmark> legacyIniBookmarks = this.legacyBookmarkConfig.loadBookmarks(recipeManager, focusFactory, ingredientManager, registryAccess, bookmarkFactory);
        List<IBookmark> legacyCompressedBookmarks = this.loadLegacyCompressedJsonBookmarks(ingredientManager, recipeManager, registryAccess, codecHelper, bookmarkCodec);
        ArrayList<IBookmark> legacyBookmarks = new ArrayList<IBookmark>();
        legacyBookmarks.addAll(legacyIniBookmarks);
        legacyBookmarks.addAll(legacyCompressedBookmarks);
        if (!legacyBookmarks.isEmpty()) {
            bookmarks = new ArrayList<IBookmark>(bookmarks);
            bookmarks.addAll(legacyBookmarks);
            BookmarkJsonConfig.getPath(this.jeiConfigurationDir).ifPresent(legacyJsonPath -> {
                if (Files.exists(legacyJsonPath, new LinkOption[0])) {
                    try {
                        Path backupPath = legacyJsonPath.resolveSibling(String.valueOf(legacyJsonPath.getFileName()) + ".bak");
                        PathUtil.moveAtomicReplace(legacyJsonPath, backupPath);
                        LOGGER.info("Backed up legacy json compressed bookmarks config file to '{}'", (Object)backupPath);
                    }
                    catch (IOException e) {
                        LOGGER.error("Failed to back up legacy json compressed bookmarks config file '{}'", legacyJsonPath, (Object)e);
                    }
                }
            });
            boolean savedBookmarks = false;
            Optional<Path> savePath = BookmarkJsonConfig.getPath(this.jeiConfigurationDir);
            if (savePath.isPresent()) {
                savedBookmarks = this.save(savePath.get(), registryAccess, bookmarks, bookmarkCodec);
            }
            if (savedBookmarks) {
                LegacyBookmarkConfig.getPath(this.jeiConfigurationDir).ifPresent(legacyPath -> {
                    if (Files.exists(legacyPath, new LinkOption[0])) {
                        try {
                            Path backupPath = legacyPath.resolveSibling(String.valueOf(legacyPath.getFileName()) + ".bak");
                            PathUtil.moveAtomicReplace(legacyPath, backupPath);
                            LOGGER.info("Backed up legacy bookmarks config file to '{}'", (Object)backupPath);
                        }
                        catch (IOException e) {
                            LOGGER.error("Failed to back up legacy bookmarks config file '{}'", legacyPath, (Object)e);
                        }
                    }
                });
            }
        }
        bookmarkList.setFromConfigFile(bookmarks);
    }

    private @Unmodifiable List<IBookmark> loadJsonBookmarks(IIngredientManager ingredientManager, IRecipeManager recipeManager, RegistryOps<JsonElement> registryOps, ICodecHelper codecHelper, Codec<IBookmark> bookmarkCodec) {
        return BookmarkJsonConfig.getPath(this.jeiConfigurationDir).map(path -> {
            List<Object> bookmarks;
            if (!Files.exists(path, new LinkOption[0])) {
                return List.of();
            }
            try (BufferedReader reader = Files.newBufferedReader(path);){
                bookmarks = JsonArrayFileHelper.read(reader, 2, bookmarkCodec, (DynamicOps<JsonElement>)registryOps, (element, error) -> LOGGER.error("Encountered an error when loading the bookmark config from file {}\n{}\n{}", path, element, error), (element, exception) -> LOGGER.error("Encountered an exception when loading the bookmark config from file {}\n{}", path, element, exception));
                LOGGER.debug("Loaded bookmarks config from file: {}", path);
            }
            catch (IOException | RuntimeException e) {
                LOGGER.error("Failed to load bookmarks from file {}", path, (Object)e);
                bookmarks = new ArrayList();
            }
            return bookmarks;
        }).orElseGet(List::of);
    }

    private @Unmodifiable List<IBookmark> loadLegacyCompressedJsonBookmarks(IIngredientManager ingredientManager, IRecipeManager recipeManager, RegistryAccess registryAccess, ICodecHelper codecHelper, Codec<IBookmark> bookmarkCodec) {
        return BookmarkJsonConfig.getPath(this.jeiConfigurationDir).map(path -> {
            List<Object> bookmarks;
            if (!Files.exists(path, new LinkOption[0])) {
                return List.of();
            }
            RegistryOps compressedOps = registryAccess.createSerializationContext((DynamicOps)JsonOps.COMPRESSED);
            try (BufferedReader reader = Files.newBufferedReader(path);){
                bookmarks = JsonArrayFileHelper.read(reader, null, bookmarkCodec, (DynamicOps<JsonElement>)compressedOps, (element, error) -> {}, (element, exception) -> {});
                LOGGER.debug("Loaded legacy compressed json bookmarks config from file: {}", path);
            }
            catch (IOException | RuntimeException e) {
                LOGGER.error("Failed to load legacy compressed json bookmarks from file {}", path, (Object)e);
                bookmarks = new ArrayList();
            }
            return bookmarks;
        }).orElseGet(List::of);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonIOException
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.MatchException
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.resources.RegistryOps
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package mezz.jei.library.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.runtime.IEditModeConfig;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.codecs.EnumCodec;
import mezz.jei.common.config.file.JsonArrayFileHelper;
import mezz.jei.library.ingredients.IngredientVisibility;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EditModeConfig
implements IEditModeConfig {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int VERSION = 2;
    private final Map<Object, Pair<IEditModeConfig.HideMode, ITypedIngredient<?>>> blacklist = new LinkedHashMap();
    private final ISerializer serializer;
    private final IIngredientManager ingredientManager;
    private WeakReference<IngredientVisibility> ingredientVisibilityRef = new WeakReference<Object>(null);

    public EditModeConfig(ISerializer serializer, IIngredientManager ingredientManager) {
        this.ingredientManager = ingredientManager;
        this.serializer = serializer;
        this.serializer.initialize(this);
        this.serializer.load(this);
    }

    public <V> void addIngredientToConfigBlacklist(ITypedIngredient<V> typedIngredient, IEditModeConfig.HideMode blacklistType, IIngredientHelper<V> ingredientHelper) {
        if (this.addIngredientToConfigBlacklistInternal(typedIngredient, blacklistType, ingredientHelper)) {
            this.serializer.save(this);
            this.notifyListenersOfVisibilityChange(typedIngredient, false);
        }
    }

    private <V> void addIngredientToConfigBlacklistInternal(ITypedIngredient<V> typedIngredient, IEditModeConfig.HideMode blacklistType) {
        IIngredientHelper<V> ingredientHelper = this.ingredientManager.getIngredientHelper(typedIngredient.getType());
        this.addIngredientToConfigBlacklistInternal(typedIngredient, blacklistType, ingredientHelper);
    }

    private <V> boolean addIngredientToConfigBlacklistInternal(ITypedIngredient<V> typedIngredient, IEditModeConfig.HideMode blacklistType, IIngredientHelper<V> ingredientHelper) {
        Object uid;
        Object wildcardUid = EditModeConfig.getIngredientUid(typedIngredient, IEditModeConfig.HideMode.WILDCARD, ingredientHelper);
        if (wildcardUid.equals(uid = EditModeConfig.getIngredientUid(typedIngredient, IEditModeConfig.HideMode.SINGLE, ingredientHelper))) {
            blacklistType = IEditModeConfig.HideMode.WILDCARD;
        }
        if (blacklistType == IEditModeConfig.HideMode.SINGLE) {
            return this.blacklist.put(uid, new Pair((Object)blacklistType, typedIngredient)) == null;
        }
        if (blacklistType == IEditModeConfig.HideMode.WILDCARD) {
            return this.blacklist.put(wildcardUid, new Pair((Object)blacklistType, typedIngredient)) == null;
        }
        return false;
    }

    public <V> boolean isIngredientOnConfigBlacklist(ITypedIngredient<V> typedIngredient, IIngredientHelper<V> ingredientHelper) {
        for (IEditModeConfig.HideMode hideMode : IEditModeConfig.HideMode.values()) {
            if (!this.isIngredientOnConfigBlacklist(typedIngredient, hideMode, ingredientHelper)) continue;
            return true;
        }
        return false;
    }

    private <V> Set<IEditModeConfig.HideMode> getIngredientOnConfigBlacklist(ITypedIngredient<V> ingredient, IIngredientHelper<V> ingredientHelper) {
        Object wildcardUid;
        Object singleUid = EditModeConfig.getIngredientUid(ingredient, IEditModeConfig.HideMode.SINGLE, ingredientHelper);
        if (singleUid.equals(wildcardUid = EditModeConfig.getIngredientUid(ingredient, IEditModeConfig.HideMode.WILDCARD, ingredientHelper))) {
            if (this.blacklist.containsKey(singleUid)) {
                return Set.of(IEditModeConfig.HideMode.SINGLE, IEditModeConfig.HideMode.WILDCARD);
            }
            return Set.of();
        }
        HashSet<IEditModeConfig.HideMode> set = new HashSet<IEditModeConfig.HideMode>();
        if (this.blacklist.containsKey(singleUid)) {
            set.add(IEditModeConfig.HideMode.SINGLE);
        }
        if (this.blacklist.containsKey(wildcardUid)) {
            set.add(IEditModeConfig.HideMode.WILDCARD);
        }
        return Collections.unmodifiableSet(set);
    }

    public <V> boolean isIngredientOnConfigBlacklist(ITypedIngredient<V> typedIngredient, IEditModeConfig.HideMode blacklistType, IIngredientHelper<V> ingredientHelper) {
        Object uid = EditModeConfig.getIngredientUid(typedIngredient, blacklistType, ingredientHelper);
        return this.blacklist.containsKey(uid);
    }

    private static <V> Object getIngredientUid(ITypedIngredient<V> typedIngredient, IEditModeConfig.HideMode blacklistType, IIngredientHelper<V> ingredientHelper) {
        return switch (blacklistType) {
            default -> throw new MatchException(null, null);
            case IEditModeConfig.HideMode.SINGLE -> ingredientHelper.getUid(typedIngredient, UidContext.Ingredient);
            case IEditModeConfig.HideMode.WILDCARD -> ingredientHelper.getGroupingUid(typedIngredient);
        };
    }

    @Override
    public <V> boolean isIngredientHiddenUsingConfigFile(ITypedIngredient<V> ingredient) {
        IIngredientType<V> type = ingredient.getType();
        IIngredientHelper<V> ingredientHelper = this.ingredientManager.getIngredientHelper(type);
        return this.isIngredientOnConfigBlacklist(ingredient, ingredientHelper);
    }

    @Override
    public <V> Set<IEditModeConfig.HideMode> getIngredientHiddenUsingConfigFile(ITypedIngredient<V> ingredient) {
        IIngredientType<V> type = ingredient.getType();
        IIngredientHelper<V> ingredientHelper = this.ingredientManager.getIngredientHelper(type);
        return this.getIngredientOnConfigBlacklist(ingredient, ingredientHelper);
    }

    @Override
    public <V> void hideIngredientUsingConfigFile(ITypedIngredient<V> ingredient, IEditModeConfig.HideMode hideMode) {
        IIngredientType<V> type = ingredient.getType();
        IIngredientHelper<V> ingredientHelper = this.ingredientManager.getIngredientHelper(type);
        this.addIngredientToConfigBlacklist(ingredient, hideMode, ingredientHelper);
    }

    @Override
    public <V> void showIngredientUsingConfigFile(ITypedIngredient<V> ingredient, IEditModeConfig.HideMode hideMode) {
        IIngredientType<V> type = ingredient.getType();
        IIngredientHelper<V> ingredientHelper = this.ingredientManager.getIngredientHelper(type);
        Object blacklistUid = EditModeConfig.getIngredientUid(ingredient, hideMode, ingredientHelper);
        if (this.blacklist.remove(blacklistUid) != null) {
            this.serializer.save(this);
            this.notifyListenersOfVisibilityChange(ingredient, true);
        }
    }

    public void registerListener(IngredientVisibility ingredientVisibility) {
        this.ingredientVisibilityRef = new WeakReference<IngredientVisibility>(ingredientVisibility);
    }

    private <T> void notifyListenersOfVisibilityChange(ITypedIngredient<T> ingredient, boolean visible) {
        IngredientVisibility ingredientVisibility = (IngredientVisibility)this.ingredientVisibilityRef.get();
        if (ingredientVisibility != null) {
            ingredientVisibility.notifyListeners(List.of(ingredient), visible);
        }
    }

    public static interface ISerializer {
        public void initialize(EditModeConfig var1);

        public void save(EditModeConfig var1);

        public void load(EditModeConfig var1);
    }

    public static class FileSerializer
    implements ISerializer {
        private final Path path;
        private final Codec<Pair<IEditModeConfig.HideMode, ITypedIngredient<?>>> codec;
        private final RegistryOps<JsonElement> registryOps;

        public FileSerializer(Path path, RegistryAccess registryAccess, ICodecHelper codecHelper) {
            this.path = path;
            this.codec = RecordCodecBuilder.create(builder -> builder.group((App)EnumCodec.create(IEditModeConfig.HideMode.class).fieldOf("hide_mode").forGetter(Pair::getFirst), (App)codecHelper.getTypedIngredientCodec().codec().fieldOf("ingredient").forGetter(Pair::getSecond)).apply((Applicative)builder, Pair::new));
            this.registryOps = registryAccess.createSerializationContext((DynamicOps)JsonOps.INSTANCE);
        }

        @Override
        public void initialize(EditModeConfig config) {
            if (!Files.exists(this.path, new LinkOption[0])) {
                this.save(config);
            }
        }

        @Override
        public void save(EditModeConfig config) {
            try {
                JsonArrayFileHelper.write(this.path, 2, config.blacklist.values(), this.codec, this.registryOps, error -> LOGGER.error("Encountered an error when saving the blacklist config to file {}\n{}", (Object)this.path, error), (element, exception) -> LOGGER.error("Encountered an exception when saving the blacklist config to file {}\n{}", (Object)this.path, element, exception));
                LOGGER.debug("Saved blacklist config to file: {}", (Object)this.path);
            }
            catch (IOException | RuntimeException e) {
                LOGGER.error("Failed to save blacklist config to file {}", (Object)this.path, (Object)e);
            }
        }

        @Override
        public void load(EditModeConfig config) {
            List<Object> results;
            if (!Files.exists(this.path, new LinkOption[0])) {
                return;
            }
            try (BufferedReader reader = Files.newBufferedReader(this.path);){
                results = JsonArrayFileHelper.read(reader, 2, this.codec, this.registryOps, (element, error) -> LOGGER.error("Encountered an error when loading the blacklist config from file {}\n{}\n{}", (Object)this.path, element, error), (element, exception) -> LOGGER.error("Encountered an exception when loading the blacklist config from file {}\n{}", (Object)this.path, element, exception));
            }
            catch (JsonIOException | JsonSyntaxException | IOException | IllegalArgumentException e) {
                LOGGER.error("Failed to load blacklist from file {}", (Object)this.path, (Object)e);
                results = List.of();
            }
            for (Pair pair : results) {
                config.addIngredientToConfigBlacklistInternal((ITypedIngredient)pair.getSecond(), (IEditModeConfig.HideMode)((Object)pair.getFirst()));
            }
        }
    }
}


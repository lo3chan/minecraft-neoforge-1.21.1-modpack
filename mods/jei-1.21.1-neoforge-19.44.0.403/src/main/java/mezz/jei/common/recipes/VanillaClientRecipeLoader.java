/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.PackType
 *  net.minecraft.server.packs.repository.ServerPacksSource
 *  net.minecraft.server.packs.resources.CloseableResourceManager
 *  net.minecraft.server.packs.resources.MultiPackResourceManager
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package mezz.jei.common.recipes;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class VanillaClientRecipeLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();

    private VanillaClientRecipeLoader() {
    }

    public static List<RecipeHolder<?>> getVanillaRecipes(RegistryAccess registryAccess) {
        HashMap<ResourceLocation, JsonElement> recipeJson = new HashMap<ResourceLocation, JsonElement>();
        try (CloseableResourceManager resourceManager = VanillaClientRecipeLoader.createVanillaServerDataResourceManager();){
            SimpleJsonResourceReloadListener.scanDirectory((ResourceManager)resourceManager, (String)"recipe", (Gson)GSON, recipeJson);
        }
        catch (RuntimeException e) {
            LOGGER.error("Failed to load vanilla recipes from client resources.", (Throwable)e);
            return List.of();
        }
        ArrayList recipeHolders = new ArrayList(recipeJson.size());
        recipeJson.forEach((id, json) -> Recipe.CODEC.parse((DynamicOps)registryAccess.createSerializationContext((DynamicOps)JsonOps.INSTANCE), json).resultOrPartial(message -> LOGGER.error("Failed to parse vanilla recipe {} from client resources: {}", id, message)).ifPresent(recipe -> recipeHolders.add(new RecipeHolder(id, recipe))));
        LOGGER.info("Loaded {} vanilla recipes from client resources.", (Object)recipeHolders.size());
        return recipeHolders;
    }

    private static CloseableResourceManager createVanillaServerDataResourceManager() {
        return new MultiPackResourceManager(PackType.SERVER_DATA, List.of(ServerPacksSource.createVanillaPackSource()));
    }
}


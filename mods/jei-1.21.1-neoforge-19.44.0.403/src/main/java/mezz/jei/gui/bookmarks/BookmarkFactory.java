/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.resources.RegistryOps
 */
package mezz.jei.gui.bookmarks;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.gui.bookmarks.IngredientBookmark;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;

public class BookmarkFactory {
    private final Codec<ITypedIngredient<?>> typedIngredientCodec;
    private final RegistryOps<JsonElement> serializationContext;
    private final IIngredientManager ingredientManager;

    public BookmarkFactory(ICodecHelper codecHelper, RegistryAccess registryAccess, IIngredientManager ingredientManager) {
        this.typedIngredientCodec = codecHelper.getTypedIngredientCodec().codec();
        this.serializationContext = registryAccess.createSerializationContext((DynamicOps)JsonOps.INSTANCE);
        this.ingredientManager = ingredientManager;
    }

    public <T> IngredientBookmark<T> create(ITypedIngredient<T> typedIngredient) {
        typedIngredient = this.ingredientManager.normalizeTypedIngredient(typedIngredient);
        String bookmarkUid = ((JsonElement)this.typedIngredientCodec.encodeStart(this.serializationContext, typedIngredient).result().orElse(JsonNull.INSTANCE)).toString();
        return new IngredientBookmark<T>(typedIngredient, bookmarkUid);
    }
}


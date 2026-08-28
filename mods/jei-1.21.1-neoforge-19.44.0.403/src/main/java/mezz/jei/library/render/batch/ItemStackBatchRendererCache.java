/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.world.item.ItemStack
 */
package mezz.jei.library.render.batch;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.List;
import mezz.jei.api.ingredients.rendering.BatchRenderElement;
import mezz.jei.library.render.ItemStackRenderer;
import mezz.jei.library.render.batch.ItemStackBatchRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;

public class ItemStackBatchRendererCache {
    private final LoadingCache<List<BatchRenderElement<ItemStack>>, ItemStackBatchRenderer> cache = CacheBuilder.newBuilder().maximumSize(6L).build((CacheLoader)new CacheLoader<List<BatchRenderElement<ItemStack>>, ItemStackBatchRenderer>(this){

        public ItemStackBatchRenderer load(List<BatchRenderElement<ItemStack>> elements) {
            Minecraft minecraft = Minecraft.getInstance();
            return new ItemStackBatchRenderer(minecraft, elements);
        }
    });

    public void renderBatch(GuiGraphics guiGraphics, ItemStackRenderer itemStackRenderer, List<BatchRenderElement<ItemStack>> elements) {
        ItemStackBatchRenderer batchData = (ItemStackBatchRenderer)this.cache.getUnchecked(elements);
        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        batchData.render(guiGraphics, minecraft, itemRenderer, itemStackRenderer);
    }
}


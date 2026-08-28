/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 */
package net.diebuddies.physics.settings.blocks;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.gui.EditButton;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.diebuddies.physics.settings.vines.BlockEntry;
import net.diebuddies.physics.vines.BlockFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockSelectionList
extends LegacyObjectSelectionList<BaseEntry> {
    public String filter = "";
    public BlockFilter blockFilter;
    public Consumer<String> editBlock;
    public boolean hasEditButton;
    private Map<BaseEntry, Button> buttons = new Object2ObjectOpenHashMap();

    public BlockSelectionList(Minecraft minecraft, BlockFilter blockFilter, int i, int j, int k, int l, int m, Consumer<String> editBlock) {
        super(minecraft, i, j, k, l, m);
        this.blockFilter = blockFilter;
        this.editBlock = editBlock;
        this.hasEditButton = editBlock != null;
        this.refreshEntries();
    }

    public BlockSelectionList(Minecraft minecraft, BlockFilter blockFilter, int i, int j, int k, int l, int m) {
        this(minecraft, blockFilter, i, j, k, l, m, null);
    }

    public void refreshEntries() {
        this.clearEntries();
        this.buttons = new Object2ObjectOpenHashMap();
        ObjectArrayList ids = new ObjectArrayList();
        for (String id : PhysicsMod.registeredBlocks.values()) {
            ids.add(id);
        }
        Collections.sort(ids);
        BlockEntry first = null;
        for (String id : ids) {
            BlockState state;
            Block block;
            if (!id.toLowerCase().contains(this.filter.toLowerCase()) || (block = PhysicsMod.invRegisteredBlocks.get(id)) == null || (state = block.defaultBlockState()) == null || this.blockFilter != null && !this.blockFilter.isValid(state)) continue;
            BlockEntry entry = new BlockEntry(this, id, PhysicsMod.invRegisteredBlocks.get(id));
            this.addEntry(entry);
            if (first != null) continue;
            first = entry;
        }
        if (first != null) {
            this.ensureVisible(first);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return super.mouseClicked(mouseX, mouseY, mouseButton) | this.listButtons(null, mouseX, mouseY, mouseButton, 0.0f, false);
    }

    public boolean listButtons(GuiGraphics guiGraphics, double mouseX, double mouseY, int mouseButton, float tickDelta, boolean render) {
        if (this.editBlock != null) {
            boolean clicked = false;
            int m = this.getItemCount();
            for (int index = 0; index < m; ++index) {
                int entryY = this.getRowTop(index);
                int p = this.getRowBottomCustom(index);
                if (p < this.y0 || entryY > this.y1) continue;
                int entryHeight = this.itemHeight - 4;
                BaseEntry entry = (BaseEntry)this.getEntry(index);
                Button button = this.buttons.computeIfAbsent(entry, key -> new EditButton(this.getRowRight() + 3, entryY, entryHeight, entryHeight - 1, (Component)Component.literal((String)""), source -> this.editBlock.accept(((BlockEntry)entry).getText())));
                button.setX(this.getRowRight() + 3);
                button.setY(entryY);
                if (!render) {
                    if (!button.mouseClicked(mouseX, mouseY, mouseButton)) continue;
                    return true;
                }
                button.render(guiGraphics, (int)mouseX, (int)mouseY, tickDelta);
            }
            return clicked;
        }
        return false;
    }

    private int getRowBottomCustom(int i) {
        return this.getRowTop(i) + this.itemHeight;
    }

    @Override
    protected int getScrollbarPosition() {
        if (this.hasEditButton) {
            return this.width - 20;
        }
        return super.getScrollbarPosition();
    }

    @Override
    protected void renderList(GuiGraphics guiGraphics, int x, int scrollAmount, int mouseX, int mouseY, float tickDelta) {
        super.renderList(guiGraphics, x, scrollAmount, mouseX, mouseY, tickDelta);
        this.listButtons(guiGraphics, mouseX, mouseY, 0, tickDelta, true);
    }
}


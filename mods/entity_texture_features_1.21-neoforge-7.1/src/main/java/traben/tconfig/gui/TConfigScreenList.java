/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.demonwav.mcdev.annotations.Translatable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 */
package traben.tconfig.gui;

import com.demonwav.mcdev.annotations.Translatable;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import traben.entity_texture_features.ETF;
import traben.tconfig.gui.TConfigEntryListWidget;
import traben.tconfig.gui.TConfigScreen;
import traben.tconfig.gui.entries.TConfigEntry;

public class TConfigScreenList
extends TConfigScreen {
    private final TConfigEntry[] options;
    private final Align align;
    protected boolean fullWidthBackgroundEvenIfSmaller = false;
    private Renderable renderFeature = null;
    private TConfigEntryListWidget list;
    private final EditBox search;

    public TConfigScreenList(@Translatable String title, Screen parent, TConfigEntry[] options, Runnable resetValuesToDefault, Runnable undoChanges, Align align) {
        super(title, parent, true);
        this.options = options;
        this.parent = parent;
        this.resetDefaultValuesRunnable = resetValuesToDefault;
        this.undoChangesRunnable = undoChanges;
        this.align = align;
        if (options.length > 12) {
            this.search = new EditBox(Minecraft.getInstance().font, 4, 0, 120, 20, (Component)Component.literal((String)""));
            this.search.setResponder(this::updateSearch);
            this.search.setHint(ETF.getTextFromTranslation("config.entity_features.search"));
        } else {
            this.search = null;
        }
    }

    public TConfigScreenList(@Translatable String title, Screen parent, TConfigEntry[] options, Runnable resetValuesToDefault, Runnable undoChanges) {
        this(title, parent, options, resetValuesToDefault, undoChanges, Align.CENTER);
    }

    public void setRenderFeature(Renderable renderFeature) {
        this.renderFeature = renderFeature;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (this.renderFeature != null) {
            this.renderFeature.render(context, mouseX, mouseY);
        }
    }

    public void setWidgetBackgroundToFullWidth() {
        this.fullWidthBackgroundEvenIfSmaller = true;
    }

    @Override
    protected void init() {
        super.init();
        if (this.search != null) {
            int y = (int)((double)this.height * 0.15) - 24;
            int width = (int)((double)this.width * 0.2);
            this.search.setY(y);
            this.search.setWidth(width);
            this.addRenderableWidget((GuiEventListener)this.search);
        }
        this.initList(this.options);
    }

    private void updateSearch(String searchText) {
        assert (this.search != null);
        this.removeWidget((GuiEventListener)this.list);
        ArrayList list = new ArrayList();
        if (searchText.isBlank()) {
            this.initList(this.options);
        } else {
            Arrays.stream(this.options).filter(it -> it.getText().getString().contains(searchText)).forEach(list::add);
            this.initList(list.toArray(new TConfigEntry[0]));
        }
    }

    private void initList(TConfigEntry[] subList) {
        int width;
        this.list = (TConfigEntryListWidget)this.addRenderableWidget((GuiEventListener)new TConfigEntryListWidget(width, (int)((double)this.height * 0.7), (int)((double)this.height * 0.15), switch (this.align.ordinal()) {
            case 0 -> {
                width = (int)((double)this.width * 0.3);
                yield (int)((double)this.width * 0.1);
            }
            case 2 -> {
                width = (int)((double)this.width * 0.3);
                yield (int)((double)this.width * 0.6);
            }
            default -> {
                width = this.width;
                yield 0;
            }
        }, 24, subList));
        if (this.fullWidthBackgroundEvenIfSmaller) {
            this.list.setWidgetBackgroundToFullWidth();
        }
    }

    public static interface Renderable {
        public void render(GuiGraphics var1, int var2, int var3);
    }

    public static enum Align {
        LEFT,
        CENTER,
        RIGHT;

    }
}


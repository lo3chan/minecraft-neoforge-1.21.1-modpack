/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.network.chat.Component
 */
package net.diebuddies.physics.settings.gui;

import java.util.function.Consumer;
import net.diebuddies.physics.settings.gui.legacy.LegacyOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class TextOption
extends LegacyOption {
    private String name;
    private String value;
    private Consumer<String> consumer;
    private EditBox search;

    public TextOption(String name, String value, Consumer<String> consumer) {
        super(name);
        this.name = name;
        this.value = value;
        this.consumer = consumer;
    }

    @Override
    public AbstractWidget createButton(Options options, int x, int y, int width) {
        this.search = new EditBox(Minecraft.getInstance().font, x, y, width, 20, (Component)Component.literal((String)this.value));
        if (this.value == null || this.value.isEmpty()) {
            this.search.setSuggestion("<" + this.name + ">");
        } else {
            this.search.setValue(this.value);
        }
        this.search.setResponder(changedText -> {
            if (changedText.isEmpty()) {
                this.search.setSuggestion("<" + this.name + ">");
            } else {
                this.search.setSuggestion("");
            }
            this.consumer.accept((String)changedText);
        });
        return this.search;
    }

    public void setText(String text) {
        if (this.search != null) {
            if (text.isEmpty()) {
                this.search.setSuggestion("<" + this.name + ">");
            } else {
                this.search.setSuggestion("");
            }
            this.search.setValue(text);
        }
    }

    public String getText() {
        if (this.search != null) {
            return this.search.getValue();
        }
        return "";
    }
}


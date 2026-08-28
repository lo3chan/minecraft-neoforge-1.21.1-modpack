/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.FormattedText$StyledContentConsumer
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

public class ExpandNewLineTextAcceptor
implements FormattedText.StyledContentConsumer<Void> {
    private final List<FormattedText> lines = new ArrayList<FormattedText>();
    @Nullable
    private MutableComponent lastComponent;

    public Optional<Void> accept(Style style, String line) {
        String[] descriptionLineExpanded = line.split("\\\\n");
        for (int i = 0; i < descriptionLineExpanded.length; ++i) {
            String s = descriptionLineExpanded[i];
            if (s.isEmpty()) {
                if (i == 0 && this.lastComponent != null) {
                    this.lines.add((FormattedText)this.lastComponent);
                    this.lastComponent = null;
                    continue;
                }
                this.lines.add(Component.EMPTY);
                continue;
            }
            MutableComponent textComponent = Component.literal((String)s);
            textComponent.setStyle(style);
            if (this.lastComponent != null) {
                if (i == 0) {
                    if (!this.lastComponent.getStyle().isEmpty() && !this.lastComponent.getStyle().equals((Object)style)) {
                        this.lastComponent = Component.literal((String)"").append((Component)this.lastComponent);
                    }
                    this.lastComponent.append((Component)textComponent);
                    continue;
                }
                this.lines.add((FormattedText)this.lastComponent);
                this.lastComponent = null;
            }
            if (i == descriptionLineExpanded.length - 1) {
                this.lastComponent = textComponent;
                continue;
            }
            this.lines.add((FormattedText)textComponent);
        }
        return Optional.empty();
    }

    public void addLinesTo(List<FormattedText> descriptionLinesExpanded) {
        descriptionLinesExpanded.addAll(this.lines);
        if (this.lastComponent != null) {
            descriptionLinesExpanded.add((FormattedText)this.lastComponent);
        }
    }
}


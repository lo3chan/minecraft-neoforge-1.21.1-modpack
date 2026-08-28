/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.input.focus;

import mezz.jei.common.util.ReflectionUtil;
import mezz.jei.gui.input.focus.GuiEventListenerFocusHandler;
import mezz.jei.gui.input.focus.IFocusHandler;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public class ScreenFocusHandler
implements IFocusHandler {
    private static final ReflectionUtil reflectionUtil = new ReflectionUtil();
    private final Screen screen;
    @Nullable
    private final IFocusHandler focusedElement;
    @Nullable
    private final GuiEventListener storedInScreenFocus;

    @Nullable
    public static ScreenFocusHandler create(Screen screen) {
        GuiEventListener storedInScreenFocus;
        IFocusHandler focusedElement;
        GuiEventListener focused = screen.getFocused();
        if (focused != null) {
            focusedElement = GuiEventListenerFocusHandler.create(focused);
            storedInScreenFocus = focused;
        } else {
            EditBox editBox = reflectionUtil.getFieldWithClass(screen, EditBox.class).filter(AbstractWidget::isFocused).findFirst().orElse(null);
            if (editBox == null) {
                return null;
            }
            focusedElement = GuiEventListenerFocusHandler.create((GuiEventListener)editBox);
            storedInScreenFocus = null;
        }
        return new ScreenFocusHandler(screen, focusedElement, storedInScreenFocus);
    }

    public ScreenFocusHandler(Screen screen, @Nullable IFocusHandler focusedElement, @Nullable GuiEventListener storedInScreenFocus) {
        this.screen = screen;
        this.focusedElement = focusedElement;
        this.storedInScreenFocus = storedInScreenFocus;
    }

    @Override
    public void unFocus() {
        if (this.focusedElement != null) {
            this.focusedElement.unFocus();
            if (this.storedInScreenFocus != null) {
                this.screen.setFocused(null);
            }
        }
    }

    @Override
    public void focus() {
        if (this.focusedElement != null) {
            this.focusedElement.focus();
            if (this.storedInScreenFocus != null) {
                this.screen.setFocused(this.storedInScreenFocus);
            }
        }
    }
}


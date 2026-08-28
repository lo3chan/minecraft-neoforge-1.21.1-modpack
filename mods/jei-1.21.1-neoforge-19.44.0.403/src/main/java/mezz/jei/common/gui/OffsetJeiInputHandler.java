/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  net.minecraft.client.gui.navigation.ScreenPosition
 *  net.minecraft.client.gui.navigation.ScreenRectangle
 */
package mezz.jei.common.gui;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.function.Supplier;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.common.util.MathUtil;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;

public class OffsetJeiInputHandler
implements IJeiInputHandler {
    private final IJeiInputHandler inputHandler;
    private final Supplier<ScreenPosition> offset;

    public OffsetJeiInputHandler(IJeiInputHandler inputHandler, Supplier<ScreenPosition> offset) {
        this.inputHandler = inputHandler;
        this.offset = offset;
    }

    @Override
    public boolean handleInput(double mouseX, double mouseY, IJeiUserInput input) {
        ScreenPosition screenPosition = this.offset.get();
        double offsetMouseX = mouseX - (double)screenPosition.x();
        double offsetMouseY = mouseY - (double)screenPosition.y();
        ScreenRectangle originalArea = this.inputHandler.getArea();
        if (MathUtil.contains(originalArea, offsetMouseX, offsetMouseY)) {
            ScreenPosition position = originalArea.position();
            double relativeMouseX = offsetMouseX - (double)position.x();
            double relativeMouseY = offsetMouseY - (double)position.y();
            return this.inputHandler.handleInput(relativeMouseX, relativeMouseY, input);
        }
        return false;
    }

    @Override
    public boolean handleMouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
        ScreenPosition screenPosition = this.offset.get();
        double offsetMouseX = mouseX - (double)screenPosition.x();
        double offsetMouseY = mouseY - (double)screenPosition.y();
        ScreenRectangle originalArea = this.inputHandler.getArea();
        if (MathUtil.contains(originalArea, offsetMouseX, offsetMouseY)) {
            ScreenPosition position = originalArea.position();
            double relativeMouseX = offsetMouseX - (double)position.x();
            double relativeMouseY = offsetMouseY - (double)position.y();
            return this.inputHandler.handleMouseScrolled(relativeMouseX, relativeMouseY, scrollDeltaX, scrollDeltaY);
        }
        return false;
    }

    @Override
    public boolean handleMouseDragged(double mouseX, double mouseY, InputConstants.Key mouseKey, double dragX, double dragY) {
        ScreenPosition screenPosition = this.offset.get();
        double offsetMouseX = mouseX - (double)screenPosition.x();
        double offsetMouseY = mouseY - (double)screenPosition.y();
        ScreenRectangle originalArea = this.inputHandler.getArea();
        if (MathUtil.contains(originalArea, offsetMouseX, offsetMouseY)) {
            ScreenPosition position = originalArea.position();
            double relativeMouseX = offsetMouseX - (double)position.x();
            double relativeMouseY = offsetMouseY - (double)position.y();
            return this.inputHandler.handleMouseDragged(relativeMouseX, relativeMouseY, mouseKey, dragX, dragY);
        }
        return false;
    }

    @Override
    public void handleMouseMoved(double mouseX, double mouseY) {
        ScreenPosition screenPosition = this.offset.get();
        double offsetMouseX = mouseX - (double)screenPosition.x();
        double offsetMouseY = mouseY - (double)screenPosition.y();
        ScreenRectangle originalArea = this.inputHandler.getArea();
        if (MathUtil.contains(originalArea, offsetMouseX, offsetMouseY)) {
            ScreenPosition position = originalArea.position();
            double relativeMouseX = offsetMouseX - (double)position.x();
            double relativeMouseY = offsetMouseY - (double)position.y();
            this.inputHandler.handleMouseMoved(relativeMouseX, relativeMouseY);
        }
    }

    @Override
    public ScreenRectangle getArea() {
        ScreenRectangle area = this.inputHandler.getArea();
        return new ScreenRectangle(this.offset.get(), area.width(), area.height());
    }
}


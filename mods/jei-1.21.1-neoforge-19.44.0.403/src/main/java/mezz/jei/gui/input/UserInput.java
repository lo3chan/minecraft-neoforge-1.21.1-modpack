/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.util.StringUtil
 */
package mezz.jei.gui.input;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.Optional;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.runtime.IJeiKeyMapping;
import mezz.jei.common.input.KeyNameUtil;
import mezz.jei.common.platform.IPlatformInputHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.gui.input.IMouseOverable;
import mezz.jei.gui.input.InputType;
import mezz.jei.gui.input.MouseUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.util.StringUtil;

public class UserInput
implements IJeiUserInput {
    private final InputConstants.Key key;
    private final double mouseX;
    private final double mouseY;
    private final int modifiers;
    private final InputType inputType;

    public static UserInput fromVanilla(int keyCode, int scanCode, int modifiers, InputType inputType) {
        InputConstants.Key input = InputConstants.getKey((int)keyCode, (int)scanCode);
        return new UserInput(input, MouseUtil.getX(), MouseUtil.getY(), modifiers, inputType);
    }

    public static Optional<UserInput> fromVanilla(double mouseX, double mouseY, int mouseButton, InputType inputType) {
        if (mouseButton < 0) {
            return Optional.empty();
        }
        InputConstants.Key input = InputConstants.Type.MOUSE.getOrCreate(mouseButton);
        UserInput userInput = new UserInput(input, mouseX, mouseY, 0, inputType);
        return Optional.of(userInput);
    }

    public UserInput(InputConstants.Key key, double mouseX, double mouseY, int modifiers, InputType inputType) {
        this.key = key;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.modifiers = modifiers;
        this.inputType = inputType;
    }

    @Override
    public InputConstants.Key getKey() {
        return this.key;
    }

    public double getMouseX() {
        return this.mouseX;
    }

    public double getMouseY() {
        return this.mouseY;
    }

    public InputType getInputType() {
        return this.inputType;
    }

    @Override
    public int getModifiers() {
        return this.modifiers;
    }

    @Override
    public boolean isSimulate() {
        return this.inputType == InputType.SIMULATE;
    }

    private boolean isKeyboard() {
        return this.key.getType() == InputConstants.Type.KEYSYM;
    }

    public boolean isAllowedChatCharacter() {
        return this.isKeyboard() && StringUtil.isAllowedChatCharacter((char)((char)this.key.getValue()));
    }

    @Override
    public boolean is(IJeiKeyMapping keyMapping) {
        return keyMapping.isActiveAndMatches(this.key);
    }

    @Override
    public boolean is(KeyMapping keyMapping) {
        IPlatformInputHelper inputHelper = Services.PLATFORM.getInputHelper();
        return inputHelper.isActiveAndMatches(keyMapping, this.key);
    }

    public boolean callVanilla(IMouseOverable mouseOverable, MouseClickable mouseClickable) {
        if (this.key.getType() == InputConstants.Type.MOUSE && mouseOverable.isMouseOver(this.mouseX, this.mouseY)) {
            if (this.isSimulate()) {
                return true;
            }
            return mouseClickable.mouseClicked(this.mouseX, this.mouseY, this.key.getValue());
        }
        return false;
    }

    public boolean callVanilla(KeyPressable keyPressable) {
        if (this.key.getType() == InputConstants.Type.KEYSYM) {
            if (this.isSimulate()) {
                return false;
            }
            return keyPressable.keyPressed(this.key.getValue(), 0, this.modifiers);
        }
        return false;
    }

    public boolean callVanilla(IMouseOverable mouseOverable, MouseClickable mouseClickable, KeyPressable keyPressable) {
        return switch (this.key.getType()) {
            case InputConstants.Type.KEYSYM -> this.callVanilla(keyPressable);
            case InputConstants.Type.MOUSE -> this.callVanilla(mouseOverable, mouseClickable);
            default -> false;
        };
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("inputType", (Object)this.inputType).add("key", (Object)KeyNameUtil.getKeyDisplayName(this.key).getString()).add("modifiers", this.modifiers).add("mouse", (Object)String.format("%s, %s", this.mouseX, this.mouseY)).toString();
    }

    @FunctionalInterface
    public static interface MouseClickable {
        public boolean mouseClicked(double var1, double var3, int var5);
    }

    @FunctionalInterface
    public static interface KeyPressable {
        public boolean keyPressed(int var1, int var2, int var3);
    }
}


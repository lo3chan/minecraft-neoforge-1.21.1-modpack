/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.input.keys;

import mezz.jei.common.input.keys.IJeiKeyMappingInternal;
import mezz.jei.common.input.keys.JeiKeyConflictContext;
import mezz.jei.common.input.keys.JeiKeyModifier;

public interface IJeiKeyMappingBuilder {
    public IJeiKeyMappingBuilder setContext(JeiKeyConflictContext var1);

    public IJeiKeyMappingBuilder setModifier(JeiKeyModifier var1);

    public IJeiKeyMappingInternal buildMouseLeft();

    public IJeiKeyMappingInternal buildMouseRight();

    public IJeiKeyMappingInternal buildMouseMiddle();

    public IJeiKeyMappingInternal buildKeyboardKey(int var1);

    public IJeiKeyMappingInternal buildUnbound();
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 */
package mezz.jei.common.input.keys;

import mezz.jei.common.input.keys.JeiKeyConflictContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public enum JeiKeyModifier {
    CONTROL_OR_COMMAND{

        @Override
        public boolean isActive(JeiKeyConflictContext context) {
            return Screen.hasControlDown();
        }

        @Override
        public Component getCombinedName(Component component) {
            if (Minecraft.ON_OSX) {
                return Component.translatable((String)"jei.key.combo.command", (Object[])new Object[]{component});
            }
            return Component.translatable((String)"jei.key.combo.control", (Object[])new Object[]{component});
        }
    }
    ,
    SHIFT{

        @Override
        public boolean isActive(JeiKeyConflictContext context) {
            return Screen.hasShiftDown();
        }

        @Override
        public Component getCombinedName(Component component) {
            return Component.translatable((String)"jei.key.combo.shift", (Object[])new Object[]{component});
        }
    }
    ,
    ALT{

        @Override
        public boolean isActive(JeiKeyConflictContext context) {
            return Screen.hasAltDown();
        }

        @Override
        public Component getCombinedName(Component component) {
            return Component.translatable((String)"jei.key.combo.alt", (Object[])new Object[]{component});
        }
    }
    ,
    NONE{

        @Override
        public boolean isActive(JeiKeyConflictContext context) {
            if (context.conflicts(JeiKeyConflictContext.IN_GAME)) {
                return true;
            }
            return !CONTROL_OR_COMMAND.isActive(context) && !SHIFT.isActive(context) && !ALT.isActive(context);
        }

        @Override
        public Component getCombinedName(Component component) {
            return component;
        }
    };


    public abstract boolean isActive(JeiKeyConflictContext var1);

    public abstract Component getCombinedName(Component var1);
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.client.settings.IKeyConflictContext
 *  net.neoforged.neoforge.client.settings.KeyConflictContext
 */
package mezz.jei.neoforge.input;

import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

public enum JeiForgeKeyConflictContexts implements IKeyConflictContext
{
    JEI_GUI_HOVER{

        public boolean isActive() {
            return KeyConflictContext.GUI.isActive();
        }

        public boolean conflicts(IKeyConflictContext other) {
            return this == other;
        }
    }
    ,
    JEI_GUI_HOVER_CHEAT_MODE{

        public boolean isActive() {
            return KeyConflictContext.GUI.isActive();
        }

        public boolean conflicts(IKeyConflictContext other) {
            return this == other;
        }
    }
    ,
    JEI_GUI_HOVER_CONFIG_BUTTON{

        public boolean isActive() {
            return KeyConflictContext.GUI.isActive();
        }

        public boolean conflicts(IKeyConflictContext other) {
            return this == other;
        }
    }
    ,
    JEI_GUI_HOVER_SEARCH{

        public boolean isActive() {
            return KeyConflictContext.GUI.isActive();
        }

        public boolean conflicts(IKeyConflictContext other) {
            return this == other;
        }
    };

}


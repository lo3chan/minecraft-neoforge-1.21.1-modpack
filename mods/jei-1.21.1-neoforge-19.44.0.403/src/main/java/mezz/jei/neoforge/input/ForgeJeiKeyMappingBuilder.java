/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  java.lang.MatchException
 *  net.minecraft.client.KeyMapping
 *  net.neoforged.neoforge.client.settings.IKeyConflictContext
 *  net.neoforged.neoforge.client.settings.KeyConflictContext
 *  net.neoforged.neoforge.client.settings.KeyModifier
 */
package mezz.jei.neoforge.input;

import com.mojang.blaze3d.platform.InputConstants;
import mezz.jei.common.input.keys.AbstractJeiKeyMappingBuilder;
import mezz.jei.common.input.keys.IJeiKeyMappingBuilder;
import mezz.jei.common.input.keys.IJeiKeyMappingInternal;
import mezz.jei.common.input.keys.JeiKeyConflictContext;
import mezz.jei.common.input.keys.JeiKeyModifier;
import mezz.jei.neoforge.input.JeiForgeKeyConflictContexts;
import mezz.jei.neoforge.input.NeoForgeJeiKeyMapping;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;

public class ForgeJeiKeyMappingBuilder
extends AbstractJeiKeyMappingBuilder {
    private final String category;
    private final String description;
    private IKeyConflictContext keyConflictContext = KeyConflictContext.UNIVERSAL;
    private KeyModifier keyModifier = KeyModifier.NONE;

    public ForgeJeiKeyMappingBuilder(String category, String description) {
        this.category = category;
        this.description = description;
    }

    @Override
    public IJeiKeyMappingBuilder setContext(JeiKeyConflictContext context) {
        this.keyConflictContext = switch (context) {
            default -> throw new MatchException(null, null);
            case JeiKeyConflictContext.UNIVERSAL -> KeyConflictContext.UNIVERSAL;
            case JeiKeyConflictContext.GUI -> KeyConflictContext.GUI;
            case JeiKeyConflictContext.IN_GAME -> KeyConflictContext.IN_GAME;
            case JeiKeyConflictContext.JEI_GUI_HOVER -> JeiForgeKeyConflictContexts.JEI_GUI_HOVER;
            case JeiKeyConflictContext.JEI_GUI_HOVER_CHEAT_MODE -> JeiForgeKeyConflictContexts.JEI_GUI_HOVER_CHEAT_MODE;
            case JeiKeyConflictContext.JEI_GUI_HOVER_CONFIG_BUTTON -> JeiForgeKeyConflictContexts.JEI_GUI_HOVER_CONFIG_BUTTON;
            case JeiKeyConflictContext.JEI_GUI_HOVER_SEARCH -> JeiForgeKeyConflictContexts.JEI_GUI_HOVER_SEARCH;
        };
        return this;
    }

    @Override
    public IJeiKeyMappingBuilder setModifier(JeiKeyModifier modifier) {
        this.keyModifier = switch (modifier) {
            default -> throw new MatchException(null, null);
            case JeiKeyModifier.CONTROL_OR_COMMAND -> KeyModifier.CONTROL;
            case JeiKeyModifier.SHIFT -> KeyModifier.SHIFT;
            case JeiKeyModifier.ALT -> KeyModifier.ALT;
            case JeiKeyModifier.NONE -> KeyModifier.NONE;
        };
        return this;
    }

    @Override
    protected IJeiKeyMappingInternal buildMouse(int mouseButton) {
        KeyMapping keyMapping = new KeyMapping(this.description, this.keyConflictContext, this.keyModifier, InputConstants.Type.MOUSE, mouseButton, this.category);
        return new NeoForgeJeiKeyMapping(keyMapping);
    }

    @Override
    public IJeiKeyMappingInternal buildKeyboardKey(int key) {
        KeyMapping keyMapping = new KeyMapping(this.description, this.keyConflictContext, this.keyModifier, InputConstants.Type.KEYSYM, key, this.category);
        return new NeoForgeJeiKeyMapping(keyMapping);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.neoforge.input;

import mezz.jei.common.input.keys.IJeiKeyMappingBuilder;
import mezz.jei.common.input.keys.IJeiKeyMappingCategoryBuilder;
import mezz.jei.neoforge.input.ForgeJeiKeyMappingBuilder;

public class ForgeJeiKeyMappingCategoryBuilder
implements IJeiKeyMappingCategoryBuilder {
    private final String category;

    public ForgeJeiKeyMappingCategoryBuilder(String category) {
        this.category = category;
    }

    @Override
    public IJeiKeyMappingBuilder createMapping(String description) {
        return new ForgeJeiKeyMappingBuilder(this.category, description);
    }
}


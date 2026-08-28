/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.entity.EntityType
 */
package net.diebuddies.physics.settings.cloth;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import net.diebuddies.config.ConfigCloth;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.cloth.ClothDisplayScreen;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.diebuddies.physics.settings.mobs.MobEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityType;

public class ClothEntitySelectionList
extends LegacyObjectSelectionList<BaseEntry> {
    public ClothEntitySelectionList(Minecraft minecraft, int i, int j, int k, int l, int m) {
        super(minecraft, i, j, k, l, m);
        this.refreshEntries();
    }

    public void refreshEntries() {
        this.clearEntries();
        ObjectArrayList ids = new ObjectArrayList();
        for (EntityType<?> type : PhysicsMod.renderers.keySet()) {
            ids.add(EntityType.getKey(type).toString());
        }
        Collections.sort(ids);
        MobEntry yourself = new MobEntry((LegacyObjectSelectionList)this, "minecraft:player");
        yourself.setUserData("physicsmod:yourself");
        yourself.setText(ClothDisplayScreen.getEntityName("physicsmod:yourself"));
        this.addEntry(yourself);
        MobEntry allPlayers = new MobEntry((LegacyObjectSelectionList)this, "minecraft:player");
        allPlayers.setUserData("minecraft:player");
        allPlayers.setText(ClothDisplayScreen.getEntityName("minecraft:player"));
        this.addEntry(allPlayers);
        for (String id : ConfigCloth.getEntityCustomizations().keySet()) {
            if (!id.startsWith("physicsmod:player:")) continue;
            MobEntry otherPlayer = new MobEntry((LegacyObjectSelectionList)this, "minecraft:player");
            otherPlayer.setUserData(id);
            otherPlayer.setText(ClothDisplayScreen.getEntityName(id));
            this.addEntry(otherPlayer);
        }
        this.ensureVisible(yourself);
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width - 20;
    }
}


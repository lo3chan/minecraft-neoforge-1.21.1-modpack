/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.player.PlayerModelPart
 */
package traben.entity_texture_features.features.player;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import traben.entity_texture_features.utils.ETFEntity;

public interface ETFPlayerEntity
extends ETFEntity {
    public Entity etf$getEntity();

    public boolean etf$isTeammate(Player var1);

    public Inventory etf$getInventory();

    @Deprecated
    public boolean etf$isPartVisible(PlayerModelPart var1);

    public Component etf$getName();

    public String etf$getUuidAsString();
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.world.entity.Entity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 */
package net.irisshaders.batchedentityrendering.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={LevelRenderer.class}, priority=999)
public class MixinLevelRenderer_EntityListSorting {
    @Shadow
    private ClientLevel level;

    @WrapOperation(method={"renderLevel"}, at={@At(value="INVOKE", target="Ljava/lang/Iterable;iterator()Ljava/util/Iterator;")})
    private Iterator<Entity> batchedentityrendering$sortEntityList(Iterable<Entity> instance, Operation<Iterator<Entity>> original) {
        this.level.getProfiler().push("sortEntityList");
        HashMap sortedEntities = new HashMap();
        ArrayList entities = new ArrayList();
        ((Iterator)original.call(new Object[]{instance})).forEachRemaining(entity -> sortedEntities.computeIfAbsent(entity.getType(), entityType -> new ArrayList(32)).add(entity));
        sortedEntities.values().forEach(entities::addAll);
        this.level.getProfiler().pop();
        return entities.iterator();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 */
package net.diebuddies.util;

import java.util.Objects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class EntityLevelPacked {
    public Entity entity;
    public String part;
    public String cloth;
    public Level level;

    public EntityLevelPacked(Entity entity, String part, String cloth, Level level) {
        this.entity = entity;
        this.part = part;
        this.cloth = cloth;
        this.level = level;
    }

    public EntityLevelPacked() {
    }

    public void set(Entity entity, String part, String cloth, Level level) {
        this.entity = entity;
        this.part = part;
        this.cloth = cloth;
        this.level = level;
    }

    public int hashCode() {
        return Objects.hash(this.cloth, this.entity, this.level, this.part);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        EntityLevelPacked other = (EntityLevelPacked)obj;
        return Objects.equals(this.cloth, other.cloth) && Objects.equals(this.entity, other.entity) && Objects.equals(this.level, other.level) && Objects.equals(this.part, other.part);
    }
}


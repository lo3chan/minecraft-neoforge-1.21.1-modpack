/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.opengl;

import java.util.Arrays;
import net.diebuddies.opengl.Data;

public class DataCombined {
    public Data[] values;

    public DataCombined(Data ... values) {
        this.values = values;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + Arrays.hashCode(this.values);
        return result;
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
        DataCombined other = (DataCombined)obj;
        return Arrays.equals(this.values, other.values);
    }
}


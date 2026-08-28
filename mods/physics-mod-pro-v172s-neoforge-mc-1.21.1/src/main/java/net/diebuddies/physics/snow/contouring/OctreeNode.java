/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3i
 */
package net.diebuddies.physics.snow.contouring;

import net.diebuddies.physics.snow.contouring.OctreeDrawInfo;
import org.joml.Vector3i;

public class OctreeNode {
    public OctreeNode[] children;
    public boolean leaf;
    public OctreeDrawInfo drawInfo;
    public Vector3i min;
    public boolean edge;
    public int size;

    public OctreeNode(boolean leaf) {
        if (!leaf) {
            this.children = new OctreeNode[8];
        }
        this.leaf = leaf;
    }

    public OctreeNode() {
        this.children = new OctreeNode[8];
        this.leaf = false;
    }

    public String toString() {
        return this.generateString(0);
    }

    public void reset(boolean leaf) {
        this.children = new OctreeNode[8];
        this.min = null;
        this.size = 0;
        this.drawInfo = null;
        this.leaf = leaf;
        this.edge = false;
    }

    public String generateString(int spaces) {
        int i;
        Object spacesString = "";
        for (int i2 = 0; i2 < spaces; ++i2) {
            spacesString = (String)spacesString + " ";
        }
        int childrenCount = 0;
        for (i = 0; i < this.children.length; ++i) {
            if (this.children[i] == null) continue;
            ++childrenCount;
        }
        spacesString = (String)spacesString + String.valueOf(this.min) + ", size: " + this.size + ", draw info: " + String.valueOf(this.drawInfo) + ", children: " + childrenCount + ", leaf: " + this.leaf + "\n";
        for (i = 0; i < this.children.length; ++i) {
            if (this.children[i] == null) continue;
            spacesString = (String)spacesString + this.children[i].generateString(spaces + 1);
        }
        return spacesString;
    }

    public int getChildrenSize() {
        int childrenSize = 0;
        for (int i = 0; this.children != null && i < this.children.length; ++i) {
            if (this.children[i] == null) continue;
            ++childrenSize;
            childrenSize += this.children[i].getChildrenSize();
        }
        return childrenSize;
    }

    public int getChildrenSize(boolean leaf) {
        int childrenSize = 0;
        for (int i = 0; this.children != null && i < this.children.length; ++i) {
            if (this.children[i] == null) continue;
            if (this.children[i].leaf == leaf) {
                ++childrenSize;
            }
            childrenSize += this.children[i].getChildrenSize(leaf);
        }
        return childrenSize;
    }

    public OctreeNode copy() {
        OctreeNode node = new OctreeNode(this.leaf);
        node.children = this.children;
        node.leaf = this.leaf;
        node.drawInfo = this.drawInfo;
        node.min = this.min;
        node.size = this.size;
        return node;
    }
}


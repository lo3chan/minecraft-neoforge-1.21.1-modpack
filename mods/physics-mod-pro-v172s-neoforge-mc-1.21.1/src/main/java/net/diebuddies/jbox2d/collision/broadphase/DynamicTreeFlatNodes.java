/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.jbox2d.collision.broadphase;

import net.diebuddies.jbox2d.callbacks.DebugDraw;
import net.diebuddies.jbox2d.callbacks.TreeCallback;
import net.diebuddies.jbox2d.callbacks.TreeRayCastCallback;
import net.diebuddies.jbox2d.collision.AABB;
import net.diebuddies.jbox2d.collision.RayCastInput;
import net.diebuddies.jbox2d.collision.broadphase.BroadPhaseStrategy;
import net.diebuddies.jbox2d.common.BufferUtils;
import net.diebuddies.jbox2d.common.Color3f;
import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Settings;
import net.diebuddies.jbox2d.common.Vec2;

public class DynamicTreeFlatNodes
implements BroadPhaseStrategy {
    public static final int MAX_STACK_SIZE = 64;
    public static final int NULL_NODE = -1;
    public static final int INITIAL_BUFFER_LENGTH = 16;
    public int m_root = -1;
    public AABB[] m_aabb;
    public Object[] m_userData;
    protected int[] m_parent;
    protected int[] m_child1;
    protected int[] m_child2;
    protected int[] m_height;
    private int m_nodeCount = 0;
    private int m_nodeCapacity = 16;
    private int m_freeList;
    private final Vec2[] drawVecs = new Vec2[4];
    private int[] nodeStack = new int[20];
    private int nodeStackIndex;
    private final Vec2 r = new Vec2();
    private final AABB aabb = new AABB();
    private final RayCastInput subInput = new RayCastInput();
    private final AABB combinedAABB = new AABB();
    private final Color3f color = new Color3f();
    private final Vec2 textVec = new Vec2();

    public DynamicTreeFlatNodes() {
        this.expandBuffers(0, this.m_nodeCapacity);
        for (int i = 0; i < this.drawVecs.length; ++i) {
            this.drawVecs[i] = new Vec2();
        }
    }

    private void expandBuffers(int oldSize, int newSize) {
        this.m_aabb = BufferUtils.reallocateBuffer(AABB.class, this.m_aabb, oldSize, newSize);
        this.m_userData = BufferUtils.reallocateBuffer(Object.class, this.m_userData, oldSize, newSize);
        this.m_parent = BufferUtils.reallocateBuffer(this.m_parent, oldSize, newSize);
        this.m_child1 = BufferUtils.reallocateBuffer(this.m_child1, oldSize, newSize);
        this.m_child2 = BufferUtils.reallocateBuffer(this.m_child2, oldSize, newSize);
        this.m_height = BufferUtils.reallocateBuffer(this.m_height, oldSize, newSize);
        for (int i = oldSize; i < newSize; ++i) {
            this.m_aabb[i] = new AABB();
            this.m_parent[i] = i == newSize - 1 ? -1 : i + 1;
            this.m_height[i] = -1;
            this.m_child1[i] = -1;
            this.m_child2[i] = -1;
        }
        this.m_freeList = oldSize;
    }

    @Override
    public final int createProxy(AABB aabb, Object userData) {
        int node = this.allocateNode();
        AABB nodeAABB = this.m_aabb[node];
        nodeAABB.lowerBound.x = aabb.lowerBound.x - Settings.aabbExtension;
        nodeAABB.lowerBound.y = aabb.lowerBound.y - Settings.aabbExtension;
        nodeAABB.upperBound.x = aabb.upperBound.x + Settings.aabbExtension;
        nodeAABB.upperBound.y = aabb.upperBound.y + Settings.aabbExtension;
        this.m_userData[node] = userData;
        this.insertLeaf(node);
        return node;
    }

    @Override
    public final void destroyProxy(int proxyId) {
        assert (0 <= proxyId && proxyId < this.m_nodeCapacity);
        assert (this.m_child1[proxyId] == -1);
        this.removeLeaf(proxyId);
        this.freeNode(proxyId);
    }

    @Override
    public final boolean moveProxy(int proxyId, AABB aabb, Vec2 displacement) {
        assert (0 <= proxyId && proxyId < this.m_nodeCapacity);
        int node = proxyId;
        assert (this.m_child1[node] == -1);
        AABB nodeAABB = this.m_aabb[node];
        if (nodeAABB.lowerBound.x <= aabb.lowerBound.x && nodeAABB.lowerBound.y <= aabb.lowerBound.y && aabb.upperBound.x <= nodeAABB.upperBound.x && aabb.upperBound.y <= nodeAABB.upperBound.y) {
            return false;
        }
        this.removeLeaf(node);
        Vec2 lowerBound = nodeAABB.lowerBound;
        Vec2 upperBound = nodeAABB.upperBound;
        lowerBound.x = aabb.lowerBound.x - Settings.aabbExtension;
        lowerBound.y = aabb.lowerBound.y - Settings.aabbExtension;
        upperBound.x = aabb.upperBound.x + Settings.aabbExtension;
        upperBound.y = aabb.upperBound.y + Settings.aabbExtension;
        float dx = displacement.x * Settings.aabbMultiplier;
        float dy = displacement.y * Settings.aabbMultiplier;
        if (dx < 0.0f) {
            lowerBound.x += dx;
        } else {
            upperBound.x += dx;
        }
        if (dy < 0.0f) {
            lowerBound.y += dy;
        } else {
            upperBound.y += dy;
        }
        this.insertLeaf(proxyId);
        return true;
    }

    @Override
    public final Object getUserData(int proxyId) {
        assert (0 <= proxyId && proxyId < this.m_nodeCount);
        return this.m_userData[proxyId];
    }

    @Override
    public final AABB getFatAABB(int proxyId) {
        assert (0 <= proxyId && proxyId < this.m_nodeCount);
        return this.m_aabb[proxyId];
    }

    @Override
    public final void query(TreeCallback callback, AABB aabb) {
        this.nodeStackIndex = 0;
        this.nodeStack[this.nodeStackIndex++] = this.m_root;
        while (this.nodeStackIndex > 0) {
            int node;
            if ((node = this.nodeStack[--this.nodeStackIndex]) == -1 || !AABB.testOverlap(this.m_aabb[node], aabb)) continue;
            int child1 = this.m_child1[node];
            if (child1 == -1) {
                boolean proceed = callback.treeCallback(node);
                if (proceed) continue;
                return;
            }
            if (this.nodeStack.length - this.nodeStackIndex - 2 <= 0) {
                this.nodeStack = BufferUtils.reallocateBuffer(this.nodeStack, this.nodeStack.length, this.nodeStack.length * 2);
            }
            this.nodeStack[this.nodeStackIndex++] = child1;
            this.nodeStack[this.nodeStackIndex++] = this.m_child2[node];
        }
    }

    @Override
    public void raycast(TreeRayCastCallback callback, RayCastInput input) {
        Vec2 p1 = input.p1;
        Vec2 p2 = input.p2;
        float p1x = p1.x;
        float p2x = p2.x;
        float p1y = p1.y;
        float p2y = p2.y;
        this.r.x = p2x - p1x;
        this.r.y = p2y - p1y;
        assert (this.r.x * this.r.x + this.r.y * this.r.y > 0.0f);
        this.r.normalize();
        float rx = this.r.x;
        float ry = this.r.y;
        float vx = -1.0f * ry;
        float vy = 1.0f * rx;
        float absVx = MathUtils.abs(vx);
        float absVy = MathUtils.abs(vy);
        float maxFraction = input.maxFraction;
        AABB segAABB = this.aabb;
        float tempx = (p2x - p1x) * maxFraction + p1x;
        float tempy = (p2y - p1y) * maxFraction + p1y;
        segAABB.lowerBound.x = p1x < tempx ? p1x : tempx;
        segAABB.lowerBound.y = p1y < tempy ? p1y : tempy;
        segAABB.upperBound.x = p1x > tempx ? p1x : tempx;
        segAABB.upperBound.y = p1y > tempy ? p1y : tempy;
        this.nodeStackIndex = 0;
        this.nodeStack[this.nodeStackIndex++] = this.m_root;
        while (this.nodeStackIndex > 0) {
            AABB nodeAABB;
            this.nodeStack[--this.nodeStackIndex] = this.m_root;
            int node = this.nodeStack[--this.nodeStackIndex];
            if (node == -1 || !AABB.testOverlap(nodeAABB = this.m_aabb[node], segAABB)) continue;
            float cx = (nodeAABB.lowerBound.x + nodeAABB.upperBound.x) * 0.5f;
            float cy = (nodeAABB.lowerBound.y + nodeAABB.upperBound.y) * 0.5f;
            float hx = (nodeAABB.upperBound.x - nodeAABB.lowerBound.x) * 0.5f;
            float hy = (nodeAABB.upperBound.y - nodeAABB.lowerBound.y) * 0.5f;
            tempx = p1x - cx;
            tempy = p1y - cy;
            float separation = MathUtils.abs(vx * tempx + vy * tempy) - (absVx * hx + absVy * hy);
            if (separation > 0.0f) continue;
            int child1 = this.m_child1[node];
            if (child1 == -1) {
                this.subInput.p1.x = p1x;
                this.subInput.p1.y = p1y;
                this.subInput.p2.x = p2x;
                this.subInput.p2.y = p2y;
                this.subInput.maxFraction = maxFraction;
                float value = callback.raycastCallback(this.subInput, node);
                if (value == 0.0f) {
                    return;
                }
                if (!(value > 0.0f)) continue;
                maxFraction = value;
                tempx = (p2x - p1x) * maxFraction + p1x;
                tempy = (p2y - p1y) * maxFraction + p1y;
                segAABB.lowerBound.x = p1x < tempx ? p1x : tempx;
                segAABB.lowerBound.y = p1y < tempy ? p1y : tempy;
                segAABB.upperBound.x = p1x > tempx ? p1x : tempx;
                segAABB.upperBound.y = p1y > tempy ? p1y : tempy;
                continue;
            }
            this.nodeStack[this.nodeStackIndex++] = child1;
            this.nodeStack[this.nodeStackIndex++] = this.m_child2[node];
        }
    }

    @Override
    public final int computeHeight() {
        return this.computeHeight(this.m_root);
    }

    private final int computeHeight(int node) {
        assert (0 <= node && node < this.m_nodeCapacity);
        if (this.m_child1[node] == -1) {
            return 0;
        }
        int height1 = this.computeHeight(this.m_child1[node]);
        int height2 = this.computeHeight(this.m_child2[node]);
        return 1 + MathUtils.max(height1, height2);
    }

    public void validate() {
        this.validateStructure(this.m_root);
        this.validateMetrics(this.m_root);
        int freeCount = 0;
        int freeNode = this.m_freeList;
        while (freeNode != -1) {
            assert (0 <= freeNode && freeNode < this.m_nodeCapacity);
            freeNode = this.m_parent[freeNode];
            ++freeCount;
        }
        assert (this.getHeight() == this.computeHeight());
        assert (this.m_nodeCount + freeCount == this.m_nodeCapacity);
    }

    @Override
    public int getHeight() {
        if (this.m_root == -1) {
            return 0;
        }
        return this.m_height[this.m_root];
    }

    @Override
    public int getMaxBalance() {
        int maxBalance = 0;
        for (int i = 0; i < this.m_nodeCapacity; ++i) {
            if (this.m_height[i] <= 1) continue;
            assert (this.m_child1[i] != -1);
            int child1 = this.m_child1[i];
            int child2 = this.m_child2[i];
            int balance = MathUtils.abs(this.m_height[child2] - this.m_height[child1]);
            maxBalance = MathUtils.max(maxBalance, balance);
        }
        return maxBalance;
    }

    @Override
    public float getAreaRatio() {
        if (this.m_root == -1) {
            return 0.0f;
        }
        int root = this.m_root;
        float rootArea = this.m_aabb[root].getPerimeter();
        float totalArea = 0.0f;
        for (int i = 0; i < this.m_nodeCapacity; ++i) {
            if (this.m_height[i] < 0) continue;
            totalArea += this.m_aabb[i].getPerimeter();
        }
        return totalArea / rootArea;
    }

    private final int allocateNode() {
        if (this.m_freeList == -1) {
            assert (this.m_nodeCount == this.m_nodeCapacity);
            this.m_nodeCapacity *= 2;
            this.expandBuffers(this.m_nodeCount, this.m_nodeCapacity);
        }
        assert (this.m_freeList != -1);
        int node = this.m_freeList;
        this.m_freeList = this.m_parent[node];
        this.m_parent[node] = -1;
        this.m_child1[node] = -1;
        this.m_height[node] = 0;
        ++this.m_nodeCount;
        return node;
    }

    private final void freeNode(int node) {
        assert (node != -1);
        assert (0 < this.m_nodeCount);
        this.m_parent[node] = this.m_freeList != -1 ? this.m_freeList : -1;
        this.m_height[node] = -1;
        this.m_freeList = node;
        --this.m_nodeCount;
    }

    private final void insertLeaf(int leaf) {
        if (this.m_root == -1) {
            this.m_root = leaf;
            this.m_parent[this.m_root] = -1;
            return;
        }
        AABB leafAABB = this.m_aabb[leaf];
        int index = this.m_root;
        while (this.m_child1[index] != -1) {
            float cost2;
            float cost1;
            int node = index;
            int child1 = this.m_child1[node];
            int child2 = this.m_child2[node];
            AABB nodeAABB = this.m_aabb[node];
            float area = nodeAABB.getPerimeter();
            this.combinedAABB.combine(nodeAABB, leafAABB);
            float combinedArea = this.combinedAABB.getPerimeter();
            float cost = 2.0f * combinedArea;
            float inheritanceCost = 2.0f * (combinedArea - area);
            AABB child1AABB = this.m_aabb[child1];
            if (this.m_child1[child1] == -1) {
                this.combinedAABB.combine(leafAABB, child1AABB);
                cost1 = this.combinedAABB.getPerimeter() + inheritanceCost;
            } else {
                this.combinedAABB.combine(leafAABB, child1AABB);
                float oldArea = child1AABB.getPerimeter();
                float newArea = this.combinedAABB.getPerimeter();
                cost1 = newArea - oldArea + inheritanceCost;
            }
            AABB child2AABB = this.m_aabb[child2];
            if (this.m_child1[child2] == -1) {
                this.combinedAABB.combine(leafAABB, child2AABB);
                cost2 = this.combinedAABB.getPerimeter() + inheritanceCost;
            } else {
                this.combinedAABB.combine(leafAABB, child2AABB);
                float oldArea = child2AABB.getPerimeter();
                float newArea = this.combinedAABB.getPerimeter();
                cost2 = newArea - oldArea + inheritanceCost;
            }
            if (cost < cost1 && cost < cost2) break;
            if (cost1 < cost2) {
                index = child1;
                continue;
            }
            index = child2;
        }
        int sibling = index;
        int oldParent = this.m_parent[sibling];
        int newParent = this.allocateNode();
        this.m_parent[newParent] = oldParent;
        this.m_userData[newParent] = null;
        this.m_aabb[newParent].combine(leafAABB, this.m_aabb[sibling]);
        this.m_height[newParent] = this.m_height[sibling] + 1;
        if (oldParent != -1) {
            if (this.m_child1[oldParent] == sibling) {
                this.m_child1[oldParent] = newParent;
            } else {
                this.m_child2[oldParent] = newParent;
            }
            this.m_child1[newParent] = sibling;
            this.m_child2[newParent] = leaf;
            this.m_parent[sibling] = newParent;
            this.m_parent[leaf] = newParent;
        } else {
            this.m_child1[newParent] = sibling;
            this.m_child2[newParent] = leaf;
            this.m_parent[sibling] = newParent;
            this.m_parent[leaf] = newParent;
            this.m_root = newParent;
        }
        index = this.m_parent[leaf];
        while (index != -1) {
            index = this.balance(index);
            int child1 = this.m_child1[index];
            int child2 = this.m_child2[index];
            assert (child1 != -1);
            assert (child2 != -1);
            this.m_height[index] = 1 + MathUtils.max(this.m_height[child1], this.m_height[child2]);
            this.m_aabb[index].combine(this.m_aabb[child1], this.m_aabb[child2]);
            index = this.m_parent[index];
        }
    }

    private final void removeLeaf(int leaf) {
        if (leaf == this.m_root) {
            this.m_root = -1;
            return;
        }
        int parent = this.m_parent[leaf];
        int grandParent = this.m_parent[parent];
        int parentChild1 = this.m_child1[parent];
        int parentChild2 = this.m_child2[parent];
        int sibling = parentChild1 == leaf ? parentChild2 : parentChild1;
        if (grandParent != -1) {
            if (this.m_child1[grandParent] == parent) {
                this.m_child1[grandParent] = sibling;
            } else {
                this.m_child2[grandParent] = sibling;
            }
            this.m_parent[sibling] = grandParent;
            this.freeNode(parent);
            int index = grandParent;
            while (index != -1) {
                index = this.balance(index);
                int child1 = this.m_child1[index];
                int child2 = this.m_child2[index];
                this.m_aabb[index].combine(this.m_aabb[child1], this.m_aabb[child2]);
                this.m_height[index] = 1 + MathUtils.max(this.m_height[child1], this.m_height[child2]);
                index = this.m_parent[index];
            }
        } else {
            this.m_root = sibling;
            this.m_parent[sibling] = -1;
            this.freeNode(parent);
        }
    }

    private int balance(int iA) {
        assert (iA != -1);
        int A = iA;
        if (this.m_child1[A] == -1 || this.m_height[A] < 2) {
            return iA;
        }
        int iB = this.m_child1[A];
        int iC = this.m_child2[A];
        assert (0 <= iB && iB < this.m_nodeCapacity);
        assert (0 <= iC && iC < this.m_nodeCapacity);
        int C = iC;
        int B = iB;
        int balance = this.m_height[C] - this.m_height[B];
        if (balance > 1) {
            int iF = this.m_child1[C];
            int iG = this.m_child2[C];
            int F = iF;
            int G = iG;
            assert (0 <= iF && iF < this.m_nodeCapacity);
            assert (0 <= iG && iG < this.m_nodeCapacity);
            this.m_child1[C] = iA;
            int cParent = this.m_parent[C] = this.m_parent[A];
            this.m_parent[A] = iC;
            if (cParent != -1) {
                if (this.m_child1[cParent] == iA) {
                    this.m_child1[cParent] = iC;
                } else {
                    assert (this.m_child2[cParent] == iA);
                    this.m_child2[cParent] = iC;
                }
            } else {
                this.m_root = iC;
            }
            if (this.m_height[F] > this.m_height[G]) {
                this.m_child2[C] = iF;
                this.m_child2[A] = iG;
                this.m_parent[G] = iA;
                this.m_aabb[A].combine(this.m_aabb[B], this.m_aabb[G]);
                this.m_aabb[C].combine(this.m_aabb[A], this.m_aabb[F]);
                this.m_height[A] = 1 + MathUtils.max(this.m_height[B], this.m_height[G]);
                this.m_height[C] = 1 + MathUtils.max(this.m_height[A], this.m_height[F]);
            } else {
                this.m_child2[C] = iG;
                this.m_child2[A] = iF;
                this.m_parent[F] = iA;
                this.m_aabb[A].combine(this.m_aabb[B], this.m_aabb[F]);
                this.m_aabb[C].combine(this.m_aabb[A], this.m_aabb[G]);
                this.m_height[A] = 1 + MathUtils.max(this.m_height[B], this.m_height[F]);
                this.m_height[C] = 1 + MathUtils.max(this.m_height[A], this.m_height[G]);
            }
            return iC;
        }
        if (balance < -1) {
            int iD = this.m_child1[B];
            int iE = this.m_child2[B];
            int D = iD;
            int E = iE;
            assert (0 <= iD && iD < this.m_nodeCapacity);
            assert (0 <= iE && iE < this.m_nodeCapacity);
            this.m_child1[B] = iA;
            int Bparent = this.m_parent[B] = this.m_parent[A];
            this.m_parent[A] = iB;
            if (Bparent != -1) {
                if (this.m_child1[Bparent] == iA) {
                    this.m_child1[Bparent] = iB;
                } else {
                    assert (this.m_child2[Bparent] == iA);
                    this.m_child2[Bparent] = iB;
                }
            } else {
                this.m_root = iB;
            }
            if (this.m_height[D] > this.m_height[E]) {
                this.m_child2[B] = iD;
                this.m_child1[A] = iE;
                this.m_parent[E] = iA;
                this.m_aabb[A].combine(this.m_aabb[C], this.m_aabb[E]);
                this.m_aabb[B].combine(this.m_aabb[A], this.m_aabb[D]);
                this.m_height[A] = 1 + MathUtils.max(this.m_height[C], this.m_height[E]);
                this.m_height[B] = 1 + MathUtils.max(this.m_height[A], this.m_height[D]);
            } else {
                this.m_child2[B] = iE;
                this.m_child1[A] = iD;
                this.m_parent[D] = iA;
                this.m_aabb[A].combine(this.m_aabb[C], this.m_aabb[D]);
                this.m_aabb[B].combine(this.m_aabb[A], this.m_aabb[E]);
                this.m_height[A] = 1 + MathUtils.max(this.m_height[C], this.m_height[D]);
                this.m_height[B] = 1 + MathUtils.max(this.m_height[A], this.m_height[E]);
            }
            return iB;
        }
        return iA;
    }

    private void validateStructure(int node) {
        if (node == -1) {
            return;
        }
        if (node == this.m_root) assert (this.m_parent[node] == -1);
        int child1 = this.m_child1[node];
        int child2 = this.m_child2[node];
        if (child1 == -1) {
            assert (child1 == -1);
            assert (child2 == -1);
            assert (this.m_height[node] == 0);
            return;
        }
        assert (child1 != -1 && 0 <= child1 && child1 < this.m_nodeCapacity);
        assert (child2 != -1 && 0 <= child2 && child2 < this.m_nodeCapacity);
        assert (this.m_parent[child1] == node);
        assert (this.m_parent[child2] == node);
        this.validateStructure(child1);
        this.validateStructure(child2);
    }

    private void validateMetrics(int node) {
        if (node == -1) {
            return;
        }
        int child1 = this.m_child1[node];
        int child2 = this.m_child2[node];
        if (child1 == -1) {
            assert (child1 == -1);
            assert (child2 == -1);
            assert (this.m_height[node] == 0);
            return;
        }
        assert (child1 != -1 && 0 <= child1 && child1 < this.m_nodeCapacity);
        assert (child2 != child1 && 0 <= child2 && child2 < this.m_nodeCapacity);
        int height1 = this.m_height[child1];
        int height2 = this.m_height[child2];
        int height = 1 + MathUtils.max(height1, height2);
        assert (this.m_height[node] == height);
        AABB aabb = new AABB();
        aabb.combine(this.m_aabb[child1], this.m_aabb[child2]);
        assert (aabb.lowerBound.equals(this.m_aabb[node].lowerBound));
        assert (aabb.upperBound.equals(this.m_aabb[node].upperBound));
        this.validateMetrics(child1);
        this.validateMetrics(child2);
    }

    @Override
    public void drawTree(DebugDraw argDraw) {
        if (this.m_root == -1) {
            return;
        }
        int height = this.computeHeight();
        this.drawTree(argDraw, this.m_root, 0, height);
    }

    public void drawTree(DebugDraw argDraw, int node, int spot, int height) {
        AABB a = this.m_aabb[node];
        a.getVertices(this.drawVecs);
        this.color.set(1.0f, (float)(height - spot) * 1.0f / (float)height, (float)(height - spot) * 1.0f / (float)height);
        argDraw.drawPolygon(this.drawVecs, 4, this.color);
        argDraw.getViewportTranform().getWorldToScreen(a.upperBound, this.textVec);
        argDraw.drawString(this.textVec.x, this.textVec.y, node + "-" + (spot + 1) + "/" + height, this.color);
        int c1 = this.m_child1[node];
        int c2 = this.m_child2[node];
        if (c1 != -1) {
            this.drawTree(argDraw, c1, spot + 1, height);
        }
        if (c2 != -1) {
            this.drawTree(argDraw, c2, spot + 1, height);
        }
    }
}


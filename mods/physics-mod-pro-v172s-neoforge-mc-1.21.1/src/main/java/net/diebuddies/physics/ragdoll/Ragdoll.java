/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.state.BlockState
 *  org.joml.Matrix3d
 *  org.joml.Matrix4d
 *  org.joml.Matrix4dc
 *  org.joml.Matrix4f
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.diebuddies.physics.ragdoll;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.Model;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.ragdoll.RagdollJoint;
import net.diebuddies.util.DoublyLinkedList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Ragdoll
implements DoublyLinkedList.NodeStorage<Ragdoll> {
    private static int counter;
    private int hashCode;
    public List<PhysicsEntity> bodies = new ObjectArrayList();
    public List<RagdollJoint> joints = new ObjectArrayList();
    public List<IRigidBody> btBodies = new ObjectArrayList();
    public Vector3d velocity = new Vector3d();
    public Vector3f hitboxScale = new Vector3f(1.0f);
    public boolean kinematic;
    public boolean frozen;
    private DoublyLinkedList.Node<Ragdoll> node;

    public Ragdoll() {
        this.hashCode = counter++;
    }

    public void updatePhysics(PhysicsWorld physics) {
    }

    public boolean blockUpdate(PhysicsWorld physics, BlockPos pos, BlockState state) {
        return false;
    }

    public RagdollJoint addConnection(int from, int connectedTo, boolean fixed, boolean onlyVisual) {
        Matrix4d transFrom;
        Matrix4d transTo;
        Vector3f localPos1 = this.bodies.get((int)from).models.get((int)0).mesh.offset;
        Vector3f localPos2 = this.bodies.get((int)connectedTo).models.get((int)0).mesh.offset;
        Vector3f pivotPoint = this.bodies.get((int)from).pivot;
        Vector3d scaleFrom = this.bodies.get(from).getTransformation().getScale(new Vector3d());
        Vector3d scaleTo = this.bodies.get(connectedTo).getTransformation().getScale(new Vector3d());
        this.bodies.get((int)from).models.get((int)0).onlyVisual = onlyVisual;
        RagdollJoint ragdollJoint = new RagdollJoint(from, connectedTo, new Vector3d((double)(pivotPoint.x - localPos1.x), (double)(pivotPoint.y - localPos1.y), (double)(pivotPoint.z - localPos1.z)).mul((Vector3dc)scaleTo), new Vector3d((double)(pivotPoint.x - localPos2.x), (double)(pivotPoint.y - localPos2.y), (double)(pivotPoint.z - localPos2.z)).mul((Vector3dc)scaleFrom));
        ragdollJoint.fixed = fixed;
        this.joints.add(ragdollJoint);
        if (fixed && !(transTo = this.bodies.get(connectedTo).getTransformation()).equals((Object)(transFrom = this.bodies.get(from).getTransformation()))) {
            Matrix4d transformation = transFrom.mulLocal((Matrix4dc)transTo.invert(new Matrix4d()));
            Matrix3d normalTransformation = transformation.normal(new Matrix3d());
            Matrix4f tmp = new Matrix4f((Matrix4dc)transformation);
            for (Vector3f pos : this.bodies.get((int)from).models.get((int)0).mesh.positions) {
                tmp.transformPosition(pos);
            }
            for (Vector3f normal : this.bodies.get((int)from).models.get((int)0).mesh.normals) {
                normalTransformation.transform(normal);
            }
        }
        return ragdollJoint;
    }

    public RagdollJoint addConnection(int from, int connectedTo, boolean fixed) {
        return this.addConnection(from, connectedTo, fixed, false);
    }

    public void addOverlayConnections(boolean onlyVisual, int bodiesSize, int offset) {
        int size = bodiesSize / 2;
        for (int i = 0; i < size; ++i) {
            this.addConnection(i + size + offset, i, true, onlyVisual);
        }
    }

    public void addOverlayConnections(boolean onlyVisual) {
        this.addOverlayConnections(onlyVisual, this.bodies.size(), 0);
    }

    public void addOverlayConnections() {
        this.addOverlayConnections(false);
    }

    public RagdollJoint addConnection(int from, int connectedTo) {
        return this.addConnection(from, connectedTo, false);
    }

    public List<Node> generateTree() {
        Node parent;
        ObjectArrayList roots = new ObjectArrayList();
        ObjectArrayList notUsedBodies = new ObjectArrayList(this.bodies);
        for (int j = 0; j < this.joints.size(); ++j) {
            RagdollJoint joint = this.joints.get(j);
            if (!notUsedBodies.contains(this.bodies.get(joint.index2))) continue;
            parent = new Node(this, joint.index2, -1);
            roots.add(parent);
            notUsedBodies.remove(this.bodies.get(joint.index2));
            this.searchChildren(parent, (List<PhysicsEntity>)notUsedBodies);
        }
        for (PhysicsEntity body : notUsedBodies) {
            parent = new Node(this, this.bodies.indexOf(body), -1);
            roots.add(parent);
        }
        return roots;
    }

    private void searchChildren(Node parent, List<PhysicsEntity> notUsedBodies) {
        if (notUsedBodies.isEmpty()) {
            return;
        }
        for (int i = 0; i < this.joints.size(); ++i) {
            Node child;
            RagdollJoint joint = this.joints.get(i);
            if (notUsedBodies.contains(this.bodies.get(joint.index2)) && parent.index == joint.index1) {
                child = new Node(this, joint.index2, i);
                parent.children.add(child);
                notUsedBodies.remove(this.bodies.get(joint.index2));
                this.searchChildren(child, notUsedBodies);
                continue;
            }
            if (!notUsedBodies.contains(this.bodies.get(joint.index1)) || parent.index != joint.index2) continue;
            child = new Node(this, joint.index1, i);
            parent.children.add(child);
            notUsedBodies.remove(this.bodies.get(joint.index1));
            this.searchChildren(child, notUsedBodies);
        }
    }

    public void add(PhysicsWorld physics) {
    }

    protected void createChildLinkPrePass(Node parent, Node root) {
        PhysicsEntity particle = this.bodies.get(root.index);
        RagdollJoint rjoint = this.joints.get(root.jointIndex);
        if (rjoint.fixed) {
            PhysicsEntity parentParticle = this.bodies.get(parent.index);
            Model model = particle.models.get(0);
            Mesh mesh = model.mesh;
            Vector3f diff = mesh.offset.sub((Vector3fc)parentParticle.models.get((int)0).mesh.offset);
            for (int i = 0; i < mesh.positions.size(); ++i) {
                mesh.positions.get(i).add((Vector3fc)diff);
            }
            mesh.getRadius(true);
            parentParticle.models.add(model);
        } else {
            for (int i = 0; i < root.children.size(); ++i) {
                this.createChildLinkPrePass(root, root.children.get(i));
            }
        }
    }

    public void remove(PhysicsWorld physicsWorld) {
        for (IRigidBody body : this.btBodies) {
            physicsWorld.removeBody(body);
            if (body.getLastChunk() == null || body.isKinematicOrFrozen()) continue;
            physicsWorld.removeLoadedChunkEntity(body.getLastChunk());
        }
    }

    public boolean isKinematic() {
        return this.kinematic;
    }

    public void setKinematic(boolean kinematic) {
        if (this.kinematic != kinematic) {
            for (IRigidBody body : this.btBodies) {
                body.setKinematic(kinematic);
            }
            this.kinematic = kinematic;
        }
    }

    public void setFrozen(boolean frozen) {
        if (this.frozen != frozen) {
            for (IRigidBody body : this.btBodies) {
                body.setFrozen(frozen);
            }
            this.frozen = frozen;
        }
    }

    public boolean isFrozen() {
        return this.frozen;
    }

    public void destroy() {
        for (IRigidBody body : this.btBodies) {
            body.getEntity().destroy();
        }
    }

    @Override
    public void setNode(DoublyLinkedList.Node<Ragdoll> node) {
        this.node = node;
    }

    @Override
    public DoublyLinkedList.Node<Ragdoll> getNode() {
        return this.node;
    }

    public int hashCode() {
        return this.hashCode;
    }

    public class Node {
        public int index;
        public int jointIndex;
        public List<Node> children = new ObjectArrayList();

        public Node(Ragdoll this$0, int index, int jointIndex) {
            this.index = index;
            this.jointIndex = jointIndex;
        }

        public String toString() {
            return super.toString();
        }

        public String generateString(int indents) {
            int i;
            Object t = "";
            for (i = 0; i < indents; ++i) {
                t = (String)t + ">";
            }
            t = (String)t + this.index + "\n";
            for (i = 0; i < this.children.size(); ++i) {
                t = (String)t + this.children.get(i).generateString(indents + 1);
            }
            return t;
        }
    }
}


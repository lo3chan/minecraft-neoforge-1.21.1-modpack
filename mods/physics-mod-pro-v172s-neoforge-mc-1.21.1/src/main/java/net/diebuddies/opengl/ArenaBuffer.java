/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  org.lwjgl.opengl.GL32C
 */
package net.diebuddies.opengl;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL32C;

public class ArenaBuffer {
    private static final double GROW_MULTIPLICATOR = 1.2;
    private int arenaBuffer = GL32C.glGenBuffers();
    private int stagingBuffer = GL32C.glGenBuffers();
    private int target;
    private int usage;
    private int totalSize;
    private int bytesUsed;
    private MemorySegment root;

    public ArenaBuffer(int size, int target) {
        this.target = target;
        this.usage = 35044;
        this.root = new MemorySegment(this, this, 0, size);
        this.totalSize = size;
        this.bind();
        GL32C.glBufferData((int)target, (long)size, (int)this.usage);
    }

    public ArenaBuffer(int size) {
        this(size, 34962);
    }

    public MemorySegment uploadData(ByteBuffer data) {
        int needed = data.remaining();
        MemorySegment segment = this.uploadToFreeSegment(this.root, data);
        if (segment != null) {
            return segment;
        }
        if (this.totalSize - this.bytesUsed >= needed) {
            this.defragment(Math.max(this.totalSize, (int)((double)(this.bytesUsed + needed) * 1.2)));
        } else {
            this.defragment((int)((double)(this.bytesUsed + needed) * 1.2));
        }
        segment = this.uploadToFreeSegment(this.root, data);
        if (segment != null) {
            return segment;
        }
        throw new RuntimeException("arena buffer couldn't find free memory segment");
    }

    public void defragment() {
        this.defragment(this.totalSize);
    }

    private void defragment(int newSize) {
        int defragmentedArenaBuffer = GL32C.glGenBuffers();
        GL32C.glBindBuffer((int)this.target, (int)defragmentedArenaBuffer);
        GL32C.glBufferData((int)this.target, (long)newSize, (int)this.usage);
        GL32C.glBindBuffer((int)36662, (int)this.arenaBuffer);
        GL32C.glBindBuffer((int)36663, (int)defragmentedArenaBuffer);
        this.totalSize = newSize;
        MemorySegment segment = this.root;
        MemorySegment tail = this.root;
        int offset = 0;
        ObjectArrayList copyCommands = new ObjectArrayList();
        while (segment != null) {
            if (segment.used) {
                copyCommands.add(new CopyCommand(this, segment.offset, offset, segment.size));
                segment.offset = offset;
                offset += segment.size;
                tail = segment;
            } else {
                if (segment.previous != null) {
                    segment.previous.next = segment.next;
                }
                if (segment.next != null) {
                    segment.next.previous = segment.previous;
                }
                if (segment == this.root && segment.next != null) {
                    this.root = segment.next;
                }
            }
            segment = segment.next;
        }
        if (tail.used) {
            tail.next = new MemorySegment(this, this, offset, this.totalSize - this.bytesUsed);
            tail.next.previous = tail;
        } else {
            tail.size = this.totalSize;
        }
        int size = 0;
        int readOffset = 0;
        int writeOffset = 0;
        for (int i = 0; i < copyCommands.size(); ++i) {
            CopyCommand copyCommand = (CopyCommand)copyCommands.get(i);
            if (readOffset != copyCommand.readOffset || writeOffset != copyCommand.writeOffset) {
                GL32C.glCopyBufferSubData((int)36662, (int)36663, (long)(readOffset - size), (long)(writeOffset - size), (long)size);
                readOffset = copyCommand.readOffset;
                writeOffset = copyCommand.writeOffset;
                size = 0;
            }
            readOffset += copyCommand.size;
            writeOffset += copyCommand.size;
            size += copyCommand.size;
        }
        if (copyCommands.size() > 0) {
            GL32C.glCopyBufferSubData((int)36662, (int)36663, (long)(readOffset - size), (long)(writeOffset - size), (long)size);
        }
        GL32C.glDeleteBuffers((int)this.arenaBuffer);
        this.arenaBuffer = defragmentedArenaBuffer;
    }

    private MemorySegment uploadToFreeSegment(MemorySegment segment, ByteBuffer data) {
        int needed = data.remaining();
        while (segment != null) {
            if (segment.size >= needed && !segment.used) {
                this.uploadToSegment(segment, data, needed);
                return segment;
            }
            segment = segment.next;
        }
        return null;
    }

    private void uploadToSegment(MemorySegment segment, ByteBuffer data, int size) {
        this.bytesUsed += size;
        segment.used = true;
        if (segment.size == size) {
            this.transferData(segment.offset, data, size);
            return;
        }
        int newSize = segment.size - size;
        MemorySegment newSegment = new MemorySegment(this, this, segment.offset + size, newSize);
        newSegment.previous = segment;
        newSegment.next = segment.next;
        segment.next = newSegment;
        segment.size = size;
        if (newSegment.next != null) {
            newSegment.next.previous = newSegment;
        }
        this.transferData(segment.offset, data, size);
    }

    private void transferData(int offset, ByteBuffer data, int size) {
        GL32C.glBindBuffer((int)34962, (int)this.stagingBuffer);
        GL32C.glBufferData((int)34962, (ByteBuffer)data, (int)35042);
        GL32C.glBindBuffer((int)36662, (int)this.stagingBuffer);
        GL32C.glBindBuffer((int)36663, (int)this.arenaBuffer);
        GL32C.glCopyBufferSubData((int)36662, (int)36663, (long)0L, (long)offset, (long)size);
    }

    private void freeSegment(MemorySegment segment) {
        if (!segment.used) {
            return;
        }
        this.bytesUsed -= segment.size;
        segment.used = false;
        this.mergeSegmentsIfPossible(segment, segment.next);
        this.mergeSegmentsIfPossible(segment.previous, segment);
    }

    private void mergeSegmentsIfPossible(MemorySegment mergeInto, MemorySegment segment) {
        if (mergeInto != null && segment != null && !segment.used && !mergeInto.used) {
            mergeInto.next = segment.next;
            mergeInto.size += segment.size;
            if (mergeInto.next != null) {
                mergeInto.next.previous = mergeInto;
            }
        }
    }

    public int getTotalSize() {
        return this.totalSize;
    }

    public void bind() {
        GL32C.glBindBuffer((int)this.target, (int)this.arenaBuffer);
    }

    public void destroy() {
        GL32C.glDeleteBuffers((int)this.arenaBuffer);
        GL32C.glDeleteBuffers((int)this.stagingBuffer);
    }

    public class MemorySegment {
        public ArenaBuffer arenaBuffer;
        public int offset;
        public int size;
        public MemorySegment previous;
        public MemorySegment next;
        public boolean used;

        public MemorySegment(ArenaBuffer this$0, ArenaBuffer arenaBuffer, int offset, int size) {
            this.arenaBuffer = arenaBuffer;
            this.offset = offset;
            this.size = size;
        }

        public void free() {
            this.arenaBuffer.freeSegment(this);
        }
    }

    public class CopyCommand {
        public int readOffset;
        public int writeOffset;
        public int size;

        public CopyCommand(ArenaBuffer this$0, int readOffset, int writeOffset, int size) {
            this.readOffset = readOffset;
            this.writeOffset = writeOffset;
            this.size = size;
        }
    }
}


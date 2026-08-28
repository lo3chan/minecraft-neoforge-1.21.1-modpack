/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 */
package net.diebuddies.opengl;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArenaBufferTests {
    private static final double GROW_MULTIPLICATOR = 1.2;
    private int arenaBuffer;
    private int stagingBuffer;
    private int target;
    private int usage;
    private int totalSize;
    private int bytesUsed;
    private MemorySegment root;
    private static List<MemorySegment> list = new ArrayList<MemorySegment>();

    public static void main(String[] args) {
        ArenaBufferTests test = new ArenaBufferTests(0x100000);
        Random r = new Random(0L);
        block0: while (true) {
            if (r.nextDouble() < 0.5) {
                list.add(test.uploadData(r.nextInt(1412, 21904)));
            } else if (list.size() > 0) {
                test.freeSegment(list.remove(r.nextInt(list.size())));
            }
            System.out.println(test.totalSize + " > " + list.size());
            int i = 0;
            while (true) {
                if (i >= list.size()) continue block0;
                MemorySegment segment = list.get(i);
                if (!segment.used) {
                    throw new RuntimeException("should be used");
                }
                if (segment.size < 0) {
                    throw new RuntimeException("invalid size");
                }
                for (int j = 0; j < list.size(); ++j) {
                    MemorySegment offSegment = list.get(j);
                    if (segment == offSegment || (segment.offset < offSegment.offset || segment.offset + segment.size > offSegment.offset + offSegment.size) && (offSegment.offset < segment.offset || offSegment.offset + offSegment.size > segment.offset + segment.size)) continue;
                    System.out.println(segment.offset + " > " + segment.size + " > " + offSegment.offset + " > " + offSegment.size);
                    System.out.println("index: " + i + " > " + j);
                    throw new RuntimeException("overlap");
                }
                ++i;
            }
            break;
        }
    }

    public ArenaBufferTests(int size, int target) {
        this.target = target;
        this.root = new MemorySegment(this, this, 0, size);
        this.totalSize = size;
        this.bind();
    }

    public ArenaBufferTests(int size) {
        this(size, 0);
    }

    public MemorySegment uploadData(int needed) {
        MemorySegment segment = this.uploadToFreeSegment(this.root, needed);
        if (segment != null) {
            return segment;
        }
        if (this.totalSize - this.bytesUsed >= needed) {
            this.defragment(Math.max(this.totalSize, (int)((double)(this.bytesUsed + needed) * 1.2)));
        } else {
            this.defragment((int)((double)(this.bytesUsed + needed) * 1.2));
        }
        segment = this.uploadToFreeSegment(this.root, needed);
        if (segment != null) {
            return segment;
        }
        throw new RuntimeException("arena buffer couldn't find free memory segment");
    }

    public void defragment() {
        this.defragment(this.totalSize);
    }

    private void defragment(int newSize) {
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
                readOffset = copyCommand.readOffset;
                writeOffset = copyCommand.writeOffset;
                size = 0;
            }
            readOffset += copyCommand.size;
            writeOffset += copyCommand.size;
            size += copyCommand.size;
        }
    }

    private void printSegments() {
        MemorySegment t = this.root;
        do {
            System.out.println(t.offset + " > " + t.size + " > " + t.used);
        } while ((t = t.next) != null);
    }

    private MemorySegment uploadToFreeSegment(MemorySegment segment, int needed) {
        while (segment != null) {
            if (segment.size >= needed && !segment.used) {
                this.uploadToSegment(segment, needed);
                return segment;
            }
            segment = segment.next;
        }
        return null;
    }

    private void uploadToSegment(MemorySegment segment, int size) {
        this.bytesUsed += size;
        segment.used = true;
        if (segment.size == size) {
            this.transferData(segment.offset, size);
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
        this.transferData(segment.offset, size);
    }

    private void transferData(int offset, int size) {
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
    }

    public void destroy() {
    }

    public class MemorySegment {
        public ArenaBufferTests arenaBuffer;
        public int offset;
        public int size;
        public MemorySegment previous;
        public MemorySegment next;
        public boolean used;

        public MemorySegment(ArenaBufferTests this$0, ArenaBufferTests arenaBuffer, int offset, int size) {
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

        public CopyCommand(ArenaBufferTests this$0, int readOffset, int writeOffset, int size) {
            this.readOffset = readOffset;
            this.writeOffset = writeOffset;
            this.size = size;
        }
    }
}


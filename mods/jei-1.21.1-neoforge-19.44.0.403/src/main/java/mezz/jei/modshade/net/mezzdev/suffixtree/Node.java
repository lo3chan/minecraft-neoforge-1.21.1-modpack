/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap
 *  it.unimi.dsi.fastutil.chars.Char2ObjectMap
 *  it.unimi.dsi.fastutil.chars.Char2ObjectMaps
 *  it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet
 *  javax.annotation.Nullable
 */
package mezz.jei.modshade.net.mezzdev.suffixtree;

import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMaps;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import mezz.jei.modshade.net.mezzdev.suffixtree.SubString;

class Node<T>
extends SubString {
    private Collection<T> data;
    private Char2ObjectMap<Node<T>> edges = Char2ObjectMaps.emptyMap();
    @Nullable
    private Node<T> suffix = null;

    Node(SubString string) {
        super(string);
        this.data = List.of();
    }

    public void getData(Consumer<Collection<T>> resultsConsumer) {
        if (!this.data.isEmpty()) {
            resultsConsumer.accept(Collections.unmodifiableCollection(this.data));
        }
        for (Node dest : this.edges.values()) {
            dest.getData(resultsConsumer);
        }
    }

    void addRef(T value) {
        if (this.contains(value)) {
            return;
        }
        this.addValue(value);
        Node<T> iter = this.suffix;
        while (iter != null && !iter.contains(value)) {
            iter.addValue(value);
            iter = iter.suffix;
        }
    }

    protected boolean contains(T value) {
        return this.data.contains(value);
    }

    void addEdge(Node<T> edge) {
        char firstChar = edge.charAt(0);
        switch (this.edges.size()) {
            case 0: {
                this.edges = Char2ObjectMaps.singleton((char)firstChar, edge);
                break;
            }
            case 1: {
                Char2ObjectArrayMap newEdges = new Char2ObjectArrayMap(2);
                newEdges.putAll(this.edges);
                newEdges.put(firstChar, edge);
                this.edges = newEdges;
                break;
            }
            case 8: {
                this.edges = new Char2ObjectOpenHashMap(this.edges);
                this.edges.put(firstChar, edge);
                break;
            }
            default: {
                this.edges.put(firstChar, edge);
            }
        }
    }

    @Nullable
    Node<T> getEdge(char ch) {
        return (Node)this.edges.get(ch);
    }

    @Nullable
    Node<T> getEdge(SubString string) {
        if (string.isEmpty()) {
            return null;
        }
        char ch = string.charAt(0);
        return (Node)this.edges.get(ch);
    }

    @Nullable
    Node<T> getSuffix() {
        return this.suffix;
    }

    void setSuffix(Node<T> suffix) {
        this.suffix = suffix;
    }

    protected void addValue(T value) {
        switch (this.data.size()) {
            case 0: {
                this.data = List.of(value);
                break;
            }
            case 1: {
                this.data = List.of(this.data.iterator().next(), value);
                break;
            }
            case 2: {
                ArrayList<T> newData = new ArrayList<T>(4);
                newData.addAll(this.data);
                newData.add(value);
                this.data = newData;
                break;
            }
            case 16: {
                ReferenceOpenHashSet newData = new ReferenceOpenHashSet(17);
                newData.addAll(this.data);
                newData.add(value);
                this.data = newData;
                break;
            }
            default: {
                this.data.add(value);
            }
        }
    }

    @Override
    public String toString() {
        return "Node: edge: " + super.toString() + " size:" + this.data.size() + " Edges: " + String.valueOf(this.edges);
    }

    public IntSummaryStatistics nodeSizeStats() {
        return this.nodeSizes().summaryStatistics();
    }

    private IntStream nodeSizes() {
        return IntStream.concat(IntStream.of(this.data.size()), this.edges.values().stream().flatMapToInt(Node::nodeSizes));
    }

    public String nodeEdgeStats() {
        IntSummaryStatistics edgeCounts = this.nodeEdgeCounts().summaryStatistics();
        IntSummaryStatistics edgeLengths = this.nodeEdgeLengths().summaryStatistics();
        return "Edge counts: " + String.valueOf(edgeCounts) + "\nEdge lengths: " + String.valueOf(edgeLengths);
    }

    private IntStream nodeEdgeCounts() {
        return IntStream.concat(IntStream.of(this.edges.size()), this.edges.values().stream().flatMapToInt(Node::nodeEdgeCounts));
    }

    private IntStream nodeEdgeLengths() {
        return IntStream.concat(this.edges.values().stream().mapToInt(SubString::length), this.edges.values().stream().flatMapToInt(Node::nodeEdgeLengths));
    }

    public void printTree(PrintWriter out, boolean includeSuffixLinks) {
        out.println("digraph {");
        out.println("\trankdir = LR;");
        out.println("\tordering = out;");
        out.println("\tedge [arrowsize=0.4,fontsize=10]");
        out.println("\t" + Node.nodeId(this) + " [label=\"\",style=filled,fillcolor=lightgrey,shape=circle,width=.1,height=.1];");
        out.println("//------leaves------");
        this.printLeaves(out);
        out.println("//------internal nodes------");
        this.printInternalNodes(this, out);
        out.println("//------edges------");
        this.printEdges(out);
        if (includeSuffixLinks) {
            out.println("//------suffix links------");
            this.printSLinks(out);
        }
        out.println("}");
    }

    private void printLeaves(PrintWriter out) {
        if (this.edges.size() == 0) {
            out.println("\t" + Node.nodeId(this) + " [label=\"" + String.valueOf(this.data) + "\",shape=point,style=filled,fillcolor=lightgrey,shape=circle,width=.07,height=.07]");
        } else {
            for (Node edge : this.edges.values()) {
                edge.printLeaves(out);
            }
        }
    }

    private void printInternalNodes(Node<T> root, PrintWriter out) {
        if (this != root && this.edges.size() > 0) {
            out.println("\t" + Node.nodeId(this) + " [label=\"" + String.valueOf(this.data) + "\",style=filled,fillcolor=lightgrey,shape=circle,width=.07,height=.07]");
        }
        for (Node edge : this.edges.values()) {
            edge.printInternalNodes(root, out);
        }
    }

    private void printEdges(PrintWriter out) {
        for (Node child : this.edges.values()) {
            out.println("\t" + Node.nodeId(this) + " -> " + Node.nodeId(child) + " [label=\"" + String.valueOf(child) + "\",weight=10]");
            child.printEdges(out);
        }
    }

    private void printSLinks(PrintWriter out) {
        if (this.suffix != null) {
            out.println("\t" + Node.nodeId(this) + " -> " + Node.nodeId(this.suffix) + " [label=\"\",weight=0,style=dotted]");
        }
        for (Node edge : this.edges.values()) {
            edge.printSLinks(out);
        }
    }

    private static <T> String nodeId(Node<T> node) {
        return "node" + Integer.toHexString(node.hashCode()).toUpperCase();
    }
}


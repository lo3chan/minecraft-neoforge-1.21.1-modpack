/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DoublyLinkedList<T extends NodeStorage<T>>
implements Iterable<T> {
    private Node<T> head = null;
    private Node<T> tail = null;
    private int size = 0;

    public void add(T data) {
        Node<T> newNode = new Node<T>(data);
        data.setNode(newNode);
        if (this.tail == null) {
            this.head = newNode;
            this.tail = newNode;
        } else {
            this.tail.next = newNode;
            newNode.prev = this.tail;
            this.tail = newNode;
        }
        ++this.size;
    }

    public boolean remove(T data) {
        Node node;
        if (data != null && (node = data.getNode()) != null) {
            this.removeNode(node);
            data.setNode(null);
            return true;
        }
        return false;
    }

    private void removeNode(Node<T> node) {
        --this.size;
        if (node == this.head && node == this.tail) {
            this.head = null;
            this.tail = null;
        } else if (node == this.head) {
            this.head = node.next;
            this.head.prev = null;
        } else if (node == this.tail) {
            this.tail = node.prev;
            this.tail.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    public void clear() {
        this.tail = null;
        this.head = null;
        this.size = 0;
    }

    public int size() {
        return this.size;
    }

    @Override
    public Iterator<T> iterator() {
        return new DoublyLinkedListIterator(this.head);
    }

    public static class Node<T> {
        T data;
        Node<T> prev;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    public static interface NodeStorage<T> {
        public void setNode(Node<T> var1);

        public Node<T> getNode();
    }

    private class DoublyLinkedListIterator
    implements Iterator<T> {
        private Node<T> current;

        public DoublyLinkedListIterator(Node<T> head) {
            this.current = head;
        }

        @Override
        public boolean hasNext() {
            return this.current != null;
        }

        @Override
        public T next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            NodeStorage data = (NodeStorage)this.current.data;
            this.current = this.current.next;
            return data;
        }

        @Override
        public void remove() {
            Node nodeToRemove;
            if (this.current == null) {
                nodeToRemove = DoublyLinkedList.this.tail;
            } else {
                if (this.current.prev == null) {
                    throw new IllegalStateException("remove() can only be called after a call to next()");
                }
                nodeToRemove = this.current.prev;
            }
            ((NodeStorage)nodeToRemove.data).setNode(null);
            if (nodeToRemove.prev != null) {
                nodeToRemove.prev.next = this.current;
            } else {
                DoublyLinkedList.this.head = this.current;
            }
            if (this.current != null) {
                this.current.prev = nodeToRemove.prev;
            }
            if (nodeToRemove == DoublyLinkedList.this.tail) {
                DoublyLinkedList.this.tail = nodeToRemove.prev;
            }
            --DoublyLinkedList.this.size;
        }
    }
}


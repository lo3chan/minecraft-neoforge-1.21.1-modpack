/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.jbox2d.dynamics.contacts;

import net.diebuddies.jbox2d.dynamics.Body;
import net.diebuddies.jbox2d.dynamics.contacts.Contact;

public class ContactEdge {
    public Body other = null;
    public Contact contact = null;
    public ContactEdge prev = null;
    public ContactEdge next = null;
}


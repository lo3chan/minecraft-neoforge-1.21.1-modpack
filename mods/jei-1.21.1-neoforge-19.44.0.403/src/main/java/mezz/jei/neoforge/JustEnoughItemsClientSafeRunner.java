/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.neoforge;

import mezz.jei.neoforge.JustEnoughItemsClient;
import mezz.jei.neoforge.events.PermanentEventSubscriptions;
import mezz.jei.neoforge.network.NetworkHandler;

public class JustEnoughItemsClientSafeRunner {
    private final NetworkHandler networkHandler;
    private final PermanentEventSubscriptions subscriptions;

    public JustEnoughItemsClientSafeRunner(NetworkHandler networkHandler, PermanentEventSubscriptions subscriptions) {
        this.networkHandler = networkHandler;
        this.subscriptions = subscriptions;
    }

    public void registerClient() {
        JustEnoughItemsClient jeiClient = new JustEnoughItemsClient(this.networkHandler, this.subscriptions);
        jeiClient.register();
    }
}


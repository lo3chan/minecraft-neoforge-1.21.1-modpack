package fuzs.puzzleslib.api.network.v4.message;

import java.util.function.Consumer;

public abstract class MessageListener<T extends Message.Context<?>> implements Consumer<T> {
}

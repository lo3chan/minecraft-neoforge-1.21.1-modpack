package io.wispforest.owo.client.screens;

import io.wispforest.endec.Endec;
import java.util.function.Consumer;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public record ScreenhandlerMessageData<T>(int id, boolean clientbound, Endec<T> endec, Consumer<T> handler) {
}

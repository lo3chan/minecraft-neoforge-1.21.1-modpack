package io.wispforest.owo.mixin.ui.access;

import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.util.Observable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Internal
@Mixin({TextBoxComponent.class})
public interface TextBoxComponentAccessor {
   @Accessor("textValue")
   Observable<String> owo$textValue();
}

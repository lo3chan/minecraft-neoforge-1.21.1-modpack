package dev.latvian.mods.rhino.util.wrap;

public record TypeWrapper<T>(Class<T> target, TypeWrapperValidator validator, TypeWrapperFactory<T> factory) {
}

package com.aetherteam.aether.item.accessories;

import io.wispforest.accessories.api.slot.SlotTypeReference;

@FunctionalInterface
public interface SlotIdentifierHolder {
   SlotTypeReference getIdentifier();
}

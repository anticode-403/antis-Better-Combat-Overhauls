package me.anticode.abco.api;

import net.bettercombat.api.AttributesContainer;
import org.jetbrains.annotations.Nullable;

public class FakeAttributesContainer {
    private final @Nullable String parent;
    private final @Nullable FakeWeaponAttributes attributes;

    public FakeAttributesContainer(@Nullable String parent, @Nullable FakeWeaponAttributes attributes) {
        this.parent = parent;
        this.attributes = attributes;
    }

    public AttributesContainer convert() {
        return new AttributesContainer(parent, attributes != null ? attributes.convert() : null);
    }
}

package me.anticode.abco.api;

import com.google.gson.annotations.SerializedName;
import me.anticode.abco.BCOverhauls;
import net.bettercombat.api.AttributesContainer;
import org.jetbrains.annotations.Nullable;

public class FakeAttributesContainer {
    @SerializedName("parent")
    private final @Nullable String parent;
    @SerializedName("attributes")
    private final @Nullable FakeWeaponAttributes attributes;

    public FakeAttributesContainer(@Nullable String parent, @Nullable FakeWeaponAttributes attributes) {
        this.parent = parent;
        this.attributes = attributes;
    }

    public AttributesContainer convert() {
        return new AttributesContainer(parent, attributes != null ? attributes.convert() : null);
    }
}

/* Optional breast geometry presets for the NickBound fork. */
package com.wildfire.common.entitydata;

import com.mojang.serialization.Codec;
import com.wildfire.common.WildfireLang;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public enum BreastShape implements StringRepresentable {
    STANDARD("standard", WildfireLang.CUSTOMIZATION_SHAPE_STANDARD),
    ROUNDED("rounded", WildfireLang.CUSTOMIZATION_SHAPE_ROUNDED),
    NATURAL("natural", WildfireLang.CUSTOMIZATION_SHAPE_NATURAL),
    TRIANGULAR("triangular", WildfireLang.CUSTOMIZATION_SHAPE_TRIANGULAR);

    public static final IntFunction<BreastShape> BY_ID = ByIdMap.continuous(BreastShape::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final Codec<BreastShape> CODEC = StringRepresentable.fromEnum(BreastShape::values);
    public static final StreamCodec<ByteBuf, BreastShape> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, BreastShape::ordinal);

    private final String name;
    private final WildfireLang displayName;

    BreastShape(String name, WildfireLang displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    @Override
    public String getSerializedName() { return name; }

    public Component getDisplayName() { return displayName.translate(); }

    public BreastShape next() { return values()[(ordinal() + 1) % values().length]; }
}

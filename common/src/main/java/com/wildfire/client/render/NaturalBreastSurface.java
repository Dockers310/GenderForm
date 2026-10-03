package com.wildfire.client.render;

import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * A softer, slightly fuller lower profile for the Natural shape.
 * The profile is intentionally less inflated than Rounded while still being true 3D geometry.
 */
public final class NaturalBreastSurface {
    private NaturalBreastSurface() {}

    public static Sample sample(float pX, float pY, float pZ, float radiusX, float radiusY, float radiusZ) {
        float length = (float) Math.sqrt(pX * pX + pY * pY + pZ * pZ);
        if (length == 0.0F || radiusX <= 0.0F || radiusY <= 0.0F || radiusZ <= 0.0F) {
            throw new IllegalArgumentException("Surface point and radii must be nonzero");
        }

        float x = pX / length;
        float y = pY / length;
        float z = pZ / length;

        // More fullness toward the lower part and a little less fullness at the top.
        float lowerFullness = 0.93F + 0.12F * (1.0F - y) * 0.5F;

        // A small front bias keeps the profile organic instead of making it a perfect sphere.
        float front = 1.0F + 0.12F * Math.max(0.0F, -z) * (1.0F - 0.20F * Math.abs(y));

        float sx = x * radiusX * lowerFullness;
        float sy = y * radiusY - 0.08F * radiusY * (1.0F - y * y);
        float sz = z * radiusZ * front;

        // Approximate a smooth normal from the local position.
        Vector3f normal = new Vector3f(
                sx / radiusX,
                (sy + 0.08F * radiusY * (1.0F - y * y)) / radiusY,
                sz / radiusZ
        );
        normal.normalize();

        return new Sample(
                sx,
                sy,
                sz,
                normal.x(),
                normal.y(),
                normal.z()
        );
    }

    public record Sample(float x, float y, float z, float nx, float ny, float nz) {}
}

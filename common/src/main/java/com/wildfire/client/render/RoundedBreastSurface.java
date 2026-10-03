package com.wildfire.client.render;

/** Smooth surface function copied from the supplied 26.2 Natural Breast prototype. */
public final class RoundedBreastSurface {
    private RoundedBreastSurface() {}

    public static Sample sample(float pX, float pY, float pZ, float radiusX, float radiusY, float radiusZ) {
        float length = (float)Math.sqrt(pX * pX + pY * pY + pZ * pZ);
        if (length == 0.0F || radiusX <= 0.0F || radiusY <= 0.0F || radiusZ <= 0.0F) {
            throw new IllegalArgumentException("Surface point and radii must be nonzero");
        }
        float x = pX / length, y = pY / length, z = pZ / length;
        float yRadiusFactor = 0.9F + 0.1F * y;
        float zRadiusFactor = 0.86F + 0.14F * y;
        float sx = x * radiusX * yRadiusFactor;
        float sz = z * radiusZ * zRadiusFactor;
        float sy = (y * radiusX * 0.1F * x * sx - radiusZ * 0.14F * z * sz) / radiusY;
        float normalLength = (float)Math.sqrt(sx * sx + sy * sy + sz * sz);
        return new Sample(
            radiusX * x * yRadiusFactor, radiusY * y, radiusZ * z * zRadiusFactor,
            sx / normalLength, sy / normalLength, sz / normalLength);
    }

    public record Sample(float x, float y, float z, float nx, float ny, float nz) {}
}

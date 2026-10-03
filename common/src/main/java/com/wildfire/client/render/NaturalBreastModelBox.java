package com.wildfire.client.render;

import com.google.common.base.Preconditions;
import com.wildfire.api.uvs.UVLayout;
import java.util.Arrays;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/** 6x6 natural 3D surface replacing the regular cube breast box. */
public final class NaturalBreastModelBox extends WildfireModelRenderer.ModelBox {
    private static final int SEGMENTS = 5;

    public NaturalBreastModelBox(int tW, int tH, float x, float y, float z, int dx, int dy, int dz, float delta, UVLayout dynamicUvLayouts) {
        super(tW, tH, x, y, z, dx, dy, dz, delta, 125, dynamicUvLayouts);
    }

    @Override
    protected void initQuads(int tW, int tH, int dx, int dy, int dz, int quads,
                             WildfireModelRenderer.PositionTextureVertex vertex,
                             WildfireModelRenderer.PositionTextureVertex vertex1,
                             WildfireModelRenderer.PositionTextureVertex vertex2,
                             WildfireModelRenderer.PositionTextureVertex vertex3,
                             WildfireModelRenderer.PositionTextureVertex vertex4,
                             WildfireModelRenderer.PositionTextureVertex vertex5,
                             WildfireModelRenderer.PositionTextureVertex vertex6,
                             WildfireModelRenderer.PositionTextureVertex vertex7) {
        super.initQuads(tW, tH, dx, dy, dz, quads, vertex, vertex1, vertex2, vertex3, vertex4, vertex5, vertex6, vertex7);

        var sourceQuads = Arrays.copyOf(this.quads, 5);
        Arrays.fill(this.quads, null);
        int out = 0;

        for (var quad : sourceQuads) {
            if (quad == null) continue;

            for (int sy = 0; sy < SEGMENTS; sy++) {
                for (int sx = 0; sx < SEGMENTS; sx++) {
                    float u0 = sx / (float) SEGMENTS;
                    float u1 = (sx + 1) / (float) SEGMENTS;
                    float v0 = sy / (float) SEGMENTS;
                    float v1 = (sy + 1) / (float) SEGMENTS;
                    float[] us = {u0, u1, u1, u0};
                    float[] vs = {v0, v0, v1, v1};

                    var positions = new WildfireModelRenderer.PositionTextureVertex[4];
                    Vector3fc[] normals = new Vector3fc[4];
                    float cx = (posX1 + posX2) / 2.0F;
                    float cy = (posY1 + posY2) / 2.0F;

                    for (int i = 0; i < 4; i++) {
                        var source = interpolate(quad.vertexPositions, us[i], vs[i]);
                        NaturalBreastSurface.Sample sample = NaturalBreastSurface.sample(
                                (source.x() - cx) / (dx / 2.0F),
                                (source.y() - cy) / (dy / 2.0F),
                                (source.z() - posZ2) / dz,
                                dx / 2.0F, dy / 2.0F, dz);

                        positions[i] = new WildfireModelRenderer.PositionTextureVertex(
                                cx + sample.x(),
                                cy + sample.y(),
                                posZ2 + sample.z(),
                                source.u(),
                                source.v());
                        normals[i] = new Vector3f(sample.nx(), sample.ny(), sample.nz());
                    }

                    this.quads[out++] = new WildfireModelRenderer.TexturedQuad(quad.uvs, positions, normals);
                }
            }
        }

        Preconditions.checkState(out <= this.quads.length, "Too many natural quads");
    }

    private static WildfireModelRenderer.PositionTextureVertex interpolate(
            WildfireModelRenderer.PositionTextureVertex[] v, float u, float w) {
        float a = (1 - u) * (1 - w);
        float b = u * (1 - w);
        float c = u * w;
        float d = (1 - u) * w;
        return new WildfireModelRenderer.PositionTextureVertex(
                a * v[0].x() + b * v[1].x() + c * v[2].x() + d * v[3].x(),
                a * v[0].y() + b * v[1].y() + c * v[2].y() + d * v[3].y(),
                a * v[0].z() + b * v[1].z() + c * v[2].z() + d * v[3].z(),
                a * v[0].u() + b * v[1].u() + c * v[2].u() + d * v[3].u(),
                a * v[0].v() + b * v[1].v() + c * v[2].v() + d * v[3].v());
    }
}

package com.wildfire.client.render;

import com.wildfire.api.uvs.UVLayout;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3f;

/**
 * Wedge-like triangular silhouette: a deliberate retro tribute to the low-poly look of the 1996 Tomb Raider era.
 */
public final class TriangularBreastModelBox extends WildfireModelRenderer.ModelBox {
    public TriangularBreastModelBox(int tW, int tH, float x, float y, float z, int dx, int dy, int dz, float delta, UVLayout dynamicUvLayouts) {
        super(tW, tH, x, y, z, dx, dy, dz, delta, 5, dynamicUvLayouts);
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
        // Build the original five faces first, then replace their vertices with a tapered wedge.
        super.initQuads(tW, tH, dx, dy, dz, quads, vertex, vertex1, vertex2, vertex3, vertex4, vertex5, vertex6, vertex7);

        List<WildfireModelRenderer.TexturedQuad> originals = new ArrayList<>();
        for (WildfireModelRenderer.TexturedQuad quad : this.quads) {
            if (quad != null) originals.add(quad);
        }

        for (int i = 0; i < originals.size(); i++) {
            WildfireModelRenderer.TexturedQuad quad = originals.get(i);
            WildfireModelRenderer.PositionTextureVertex[] positions = quad.vertexPositions.clone();
            var normals = new Vector3f[4];

            for (int v = 0; v < positions.length; v++) {
                WildfireModelRenderer.PositionTextureVertex source = positions[v];
                float y01 = (source.y() - posY1) / Math.max(0.001F, posY2 - posY1);

                // Width shrinks strongly toward the top, producing a triangular/wedge silhouette.
                float widthFactor = 1.0F - 0.78F * y01;
                float centerX = (posX1 + posX2) * 0.5F;
                float newX = centerX + (source.x() - centerX) * widthFactor;

                // The upper front becomes slightly more pronounced than the back.
                float frontAmount = Math.max(0.0F, (posZ2 - source.z()) / Math.max(0.001F, dz));
                float newZ = source.z() - 0.22F * y01 * frontAmount;

                positions[v] = new WildfireModelRenderer.PositionTextureVertex(
                        newX, source.y(), newZ, source.u(), source.v());

                float nx = (newX - centerX) / Math.max(0.001F, dx * 0.5F);
                float ny = (source.y() - (posY1 + posY2) * 0.5F) / Math.max(0.001F, dy * 0.5F);
                float nz = (newZ - (posZ1 + posZ2) * 0.5F) / Math.max(0.001F, dz * 0.5F);
                normals[v] = new Vector3f(nx, ny, nz).normalize();
            }

            this.quads[i] = new WildfireModelRenderer.TexturedQuad(quad.uvs, positions, normals);
        }
    }
}

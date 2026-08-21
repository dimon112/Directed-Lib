package directedlib.world.draw;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.struct.ObjectIntMap;
import arc.util.Eachable;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import directedlib.world.blocks.DirectedCrafter;

public class DrawDirectedOutputs extends DrawBlock {
    public String arrowRegionName = "pointer";
    public TextureRegion arrowRegion;

    public float iconSize = 5f;
    public float arrowSize = 6f;
    public float edgeInset = 3f;
    public boolean drawArrow = true;
    public boolean drawItem = true;

    @Override
    public void load(Block block) {
        arrowRegion = Core.atlas.find(arrowRegionName, Core.atlas.find("pointer", Core.atlas.find("launch-arrow")));
    }

    @Override
    public void draw(Building build) {
        if (!(build.block instanceof DirectedCrafter crafter)) return;

        for (ObjectIntMap.Entry<Item> entry : crafter.itemOutputDirections.entries()) {
            Item item = entry.key;
            int relDir = entry.value;
            if (item == null || relDir < 0) continue;

            int rot = (build.rotation + relDir) % 4;
            float offset = (crafter.size * Vars.tilesize) / 2f - edgeInset;
            float drawX = build.x + Geometry.d4x(rot) * offset;
            float drawY = build.y + Geometry.d4y(rot) * offset;

            if (drawArrow && arrowRegion != null && arrowRegion.found()) {
                Draw.rect(arrowRegion, drawX, drawY, arrowSize, arrowSize, rot * 90f);
            }
            if (drawItem && item.uiIcon != null) {
                Draw.rect(item.uiIcon, drawX, drawY, iconSize, iconSize);
            }
        }
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        if (!(block instanceof DirectedCrafter crafter)) return;

        for (ObjectIntMap.Entry<Item> entry : crafter.itemOutputDirections.entries()) {
            Item item = entry.key;
            int relDir = entry.value;
            if (item == null || relDir < 0) continue;

            int rot = (plan.rotation + relDir) % 4;
            float offset = (crafter.size * Vars.tilesize) / 2f - edgeInset;
            float drawX = plan.drawx() + Geometry.d4x(rot) * offset;
            float drawY = plan.drawy() + Geometry.d4y(rot) * offset;

            if (drawArrow && arrowRegion != null && arrowRegion.found()) {
                Draw.rect(arrowRegion, drawX, drawY, arrowSize, arrowSize, rot * 90f);
            }
            if (drawItem && item.uiIcon != null) {
                Draw.rect(item.uiIcon, drawX, drawY, iconSize, iconSize);
            }
        }
    }
}
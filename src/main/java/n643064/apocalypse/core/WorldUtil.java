package n643064.apocalypse.core;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class WorldUtil
{
    public static float distanceToBLock(Entity entity, BlockPos pos) {
        float f = (float)(entity.getX() - pos.getX());
        float g = (float)(entity.getY() - pos.getY());
        float h = (float)(entity.getZ() - pos.getZ());
        return MathHelper.sqrt(f * f + g * g + h * h);
    }
}

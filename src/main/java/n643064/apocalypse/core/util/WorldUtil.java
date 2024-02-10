package n643064.apocalypse.core.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class WorldUtil
{

    public static float blockPosDistance(Entity entity1, Entity entity2)
    {
        return blockPosDistance(entity1.getBlockPos(), entity2.getBlockPos());
    }

    public static float blockPosDistance(Entity entity, BlockPos pos)
    {
        return blockPosDistance(entity.getBlockPos(), pos);
    }

    public static float blockPosDistance(BlockPos pos1, BlockPos pos2)
    {
        float x = (pos1.getX() - pos2.getX());
        float y = (pos1.getY() - pos2.getY());
        float z = (pos1.getZ() - pos2.getZ());
        return MathHelper.sqrt(x * x + y * y + z * z);
    }
}

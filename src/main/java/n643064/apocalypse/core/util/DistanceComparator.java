package n643064.apocalypse.core.util;

import n643064.apocalypse.core.WorldUtil;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Comparator;

public class DistanceComparator implements Comparator<ZombieEntity>
{
    private final BlockPos origin;
    public DistanceComparator(BlockPos pos)
    {
        this.origin = pos;
    }
    @Override
    public int compare(ZombieEntity o1, ZombieEntity o2)
    {
        int d1 = (int) WorldUtil.blockPosDistance(o1, origin);
        int d2 = (int) WorldUtil.blockPosDistance(o2, origin);
        return Integer.compare(d1, d2);
    }
}

package n643064.apocalypse.core.entity.goal;

import n643064.apocalypse.Apocalypse;
import n643064.apocalypse.core.WorldUtil;
import net.minecraft.block.AirBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ZombieBreakBlockGoal extends Goal
{
    private final ZombieEntity mob;
    private BlockPos target;
    private float targetHardness;
    private float progress;
    public ZombieBreakBlockGoal(ZombieEntity mob)
    {
        this.mob = mob;
    }

    @Override
    public boolean shouldRunEveryTick()
    {
        return true;
    }

    @Override
    public boolean shouldContinue()
    {
        return mob.getNavigation().isIdle() && WorldUtil.distanceToBLock(mob, target) <= 3 && this.progress < this.targetHardness;
    }

    @Override
    public void start()
    {
        System.out.println("Start");
        mob.getDataTracker().set(Apocalypse.IS_DIGGING, true);
    }


    @Override
    public void tick()
    {
        progress += 0.05;
        System.out.println(progress);
        mob.swingHand(Hand.MAIN_HAND);
        if (progress >= targetHardness)
        {
            mob.world.breakBlock(this.target, true);
            System.out.println("Break");
            return;
        }
        mob.world.setBlockBreakingInfo(mob.getId(), target, (int) (progress * 4));

    }

    @Override
    public void stop()
    {
        mob.world.setBlockBreakingInfo(mob.getId(), target, 0);
        progress = 0;
        target = null;
        targetHardness = 0;
        mob.getDataTracker().set(Apocalypse.IS_DIGGING, false);
        mob.getNavigation().recalculatePath();
    }

    @Override
    public boolean canStart()
    {
        if (!mob.getNavigation().isIdle()) return false;
        World world = mob.world;
        Direction direction = mob.getHorizontalFacing();
        BlockPos pos = mob.getBlockPos().add(direction.getVector()).add(0, 1, 0);
        LivingEntity targetEntity = mob.getTarget();
        if (targetEntity == null) return false;
        int yDiff = Math.abs(targetEntity.getBlockY() - mob.getBlockY());
        int mod;
        if (yDiff > 2)
        {
            mod = targetEntity.getBlockY() > mob.getY() ? 1 : -1;
        } else
        {
            mod = -1;
        }
        if (world.getBlockState(pos).getBlock() instanceof AirBlock)
        {
            pos = pos.add(0, mod, 0);
            if (world.getBlockState(pos).getBlock() instanceof AirBlock)
            {
                return false;
            }
            target = pos;

        } else
        {
            target = pos;
        }
        targetHardness = world.getBlockState(target).getBlock().getHardness() * 2;
        return targetHardness < 20;
    }
}

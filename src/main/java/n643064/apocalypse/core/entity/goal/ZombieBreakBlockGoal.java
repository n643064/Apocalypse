package n643064.apocalypse.core.entity.goal;

import me.shedaniel.autoconfig.AutoConfig;
import n643064.apocalypse.Apocalypse;
import n643064.apocalypse.core.util.WorldUtil;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ZombieBreakBlockGoal extends Goal
{
    private final ZombieEntity mob;
    private Apocalypse.ApocalypseConfig.Zombie config;
    private BlockPos target;
    private float targetHardness;
    private float progress;
    private float ratio;
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
        return WorldUtil.blockPosDistance(mob, target) <= 3 && this.progress <= this.targetHardness;
    }

    @Override
    public void start()
    {
        mob.getDataTracker().set(Apocalypse.IS_DIGGING, true);
    }


    @Override
    public boolean canStop()
    {
        return false;
    }

    @Override
    public void tick()
    {
       //Apocalypse.ApocalypseConfig.Zombie config = AutoConfig.getConfigHolder(Apocalypse.ApocalypseConfig.class).get().zombie;
        progress += config.diggingProgressTick;
        mob.swingHand(Hand.MAIN_HAND);
        if (progress >= targetHardness)
        {
            mob.getWorld().breakBlock(this.target, config.dropBrokenBlocks);
            return;
        }
        mob.getWorld().setBlockBreakingInfo(mob.getId(), target, (int) (progress * ratio));

    }

    @Override
    public void stop()
    {
        mob.getWorld().setBlockBreakingInfo(mob.getId(), target, 0);
        progress = 0;
        target = null;
        targetHardness = 0;
        mob.getDataTracker().set(Apocalypse.IS_DIGGING, false);
        mob.getNavigation().recalculatePath();
    }



    @Override
    public boolean canStart()
    {
        config = AutoConfig.getConfigHolder(Apocalypse.ApocalypseConfig.class).get().zombie;
        final World world = mob.getWorld();
        final Direction direction = mob.getHorizontalFacing();
        BlockPos pos = mob.getBlockPos();
        Block b = world.getBlockState(pos).getBlock();

        if (config.instantDoorBreak)
        {
            if (b instanceof DoorBlock && b.getHardness() < config.instantDoorBreakHardness)
            {
                world.breakBlock(pos, config.dropBrokenBlocks);
                return false;
            }
            BlockPos pos2 = pos.add(direction.getVector());
            Block b2 = world.getBlockState(pos2).getBlock();
            if (b2 instanceof DoorBlock && b2.getHardness() < config.instantDoorBreakHardness)
            {
                world.breakBlock(pos2, config.dropBrokenBlocks);
                return false;
            }
        }

        pos = pos.add(direction.getVector()).add(0, 1, 0);
        LivingEntity targetEntity = mob.getTarget();

        if (!mob.getNavigation().isIdle()) return false;
        if (targetEntity == null) return false;
        int yDiff = Math.abs(targetEntity.getBlockY() - mob.getBlockY());
        int mod;
        if (yDiff > 2)
        {
            mod = targetEntity.getBlockY() > mob.getY() ? 1 : -2;
        } else
        {
            mod = -1;
        }
        BlockState state = world.getBlockState(pos);
        b = state.getBlock();

        if (b instanceof AirBlock || b.canMobSpawnInside(state))
        {
            pos = pos.add(0, mod, 0);
            state = world.getBlockState(pos);
            b = state.getBlock();
            if (b instanceof AirBlock || b.canMobSpawnInside(state))
            {
                return false;
            }
        }
        target = pos;
        targetHardness = world.getBlockState(target).getBlock().getHardness() * 2;
        if (targetHardness < 0)
        {
            return false;
        }
        ratio = 10 / targetHardness;
        return targetHardness < config.maximumTargetHardness;
    }
}

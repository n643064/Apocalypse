package n643064.apocalypse.core.entity.goal;

import me.shedaniel.autoconfig.AutoConfig;
import n643064.apocalypse.Apocalypse;
import n643064.apocalypse.core.WorldUtil;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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
        Apocalypse.ApocalypseConfig.Zombie config = AutoConfig.getConfigHolder(Apocalypse.ApocalypseConfig.class).get().zombie;
        progress += config.diggingProgressTick;
        mob.swingHand(Hand.MAIN_HAND);
        if (progress >= targetHardness)
        {
            mob.world.breakBlock(this.target, true);
            return;
        }
        mob.world.setBlockBreakingInfo(mob.getId(), target, (int) (progress * ratio));

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
        Apocalypse.ApocalypseConfig.Zombie config = AutoConfig.getConfigHolder(Apocalypse.ApocalypseConfig.class).get().zombie;

        World world = mob.world;
        Direction direction = mob.getHorizontalFacing();
        BlockPos pos = mob.getBlockPos();
        Block b = world.getBlockState(pos).getBlock();
        if (b instanceof DoorBlock && !b.equals(Blocks.IRON_DOOR))
        {
            world.breakBlock(pos, true);
            return false;
        }
        pos = pos.add(direction.getVector()).add(0, 1, 0);
        LivingEntity targetEntity = mob.getTarget();

        if (!mob.getNavigation().isIdle()) return false;
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
        Block block = world.getBlockState(pos).getBlock();

        if (block instanceof AirBlock || (block instanceof DoorBlock && !block.equals(Blocks.IRON_DOOR)) || block.canMobSpawnInside())
        {
            pos = pos.add(0, mod, 0);
            block = world.getBlockState(pos).getBlock();
            if (block instanceof AirBlock || (block instanceof DoorBlock && !block.equals(Blocks.IRON_DOOR)) || block.canMobSpawnInside())
            {
                return false;
            }

        }
        target = pos;
        targetHardness = world.getBlockState(target).getBlock().getHardness() * 2;
        ratio = 10 / targetHardness;
        return targetHardness < config.maximumTargetHardness;
    }
}

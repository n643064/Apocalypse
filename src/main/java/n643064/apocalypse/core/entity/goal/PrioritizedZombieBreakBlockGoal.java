package n643064.apocalypse.core.entity.goal;

import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.mob.ZombieEntity;

public class PrioritizedZombieBreakBlockGoal extends PrioritizedGoal
{
    public PrioritizedZombieBreakBlockGoal(int priority, ZombieEntity zombie)
    {
        super(priority, new ZombieBreakBlockGoal(zombie));
    }


}

package n643064.apocalypse.core.entity.goal;

import n643064.apocalypse.Apocalypse;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.mob.ZombieEntity;

public class CustomZombieAttackGoal extends ZombieAttackGoal
{
    public CustomZombieAttackGoal(ZombieEntity zombie, double speed, boolean pauseWhenMobIdle)
    {
        super(zombie, speed, pauseWhenMobIdle);
    }

    @Override
    public boolean canStart()
    {
        if (this.mob.getDataTracker().get(Apocalypse.IS_DIGGING))
        {
            return false;
        }
        return super.canStart();
    }
}

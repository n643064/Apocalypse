package n643064.apocalypse.core.entity.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class ZombiePounceAtTargetGoal extends Goal
{
    private final ZombieEntity mob;
    private LivingEntity target;
    private final float velocity;

    public ZombiePounceAtTargetGoal(ZombieEntity mob, float velocity)
    {
        this.mob = mob;
        this.velocity = velocity;
        this.setControls(EnumSet.of(Goal.Control.JUMP, Control.MOVE));
    }

    @Override
    public boolean canStart()
    {
        this.target = this.mob.getTarget();
        if (this.target == null)
        {
            return false;
        } else
        {
            double d = this.mob.squaredDistanceTo(this.target);
            if (!(d < 4.0) && !(d > 16.0))
            {
                if (!this.mob.isOnGround())
                {
                    return false;
                } else
                {
                    return this.mob.getRandom().nextInt(toGoalTicks(10)) == 0;
                }
            } else
            {
                return false;
            }
        }
    }

    @Override
    public boolean shouldContinue()
    {
        return !this.mob.isOnGround();
    }

    @Override
    public void start()
    {
        this.mob.lookAtEntity(this.target, 360, 360);
        Vec3d vec3d = this.mob.getVelocity();
        Vec3d vec3d2 = new Vec3d(this.target.getX() - this.mob.getX(), 0.0, this.target.getZ() - this.mob.getZ());
        if (vec3d2.lengthSquared() > 1.0E-7)
        {
            vec3d2 = vec3d2.normalize().multiply(0.6).add(vec3d.multiply(0.3));
        }

        this.mob.setVelocity(vec3d2.x, this.velocity, vec3d2.z);
    }

}

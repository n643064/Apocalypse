package n643064.apocalypse.mixin;

import n643064.apocalypse.Apocalypse;
import n643064.apocalypse.core.entity.goal.PrioritizedZombieBreakBlockGoal;
import n643064.apocalypse.core.entity.goal.ZombiePounceAtTargetGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;
import java.util.Optional;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends HostileEntity
{

    @Shadow @Final protected abstract void initGoals();

    @Shadow @Final public abstract boolean onKilledOther(ServerWorld world, LivingEntity other);

    protected ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world)
    {
        super(entityType, world);
    }



    @SuppressWarnings("unchecked")
    @Redirect(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/ZombieEntity;initCustomGoals()V"))
    private void initCustomGoals(ZombieEntity instance)
    {
        Apocalypse.ApocalypseConfig.Zombie config = Apocalypse.config.zombie;
        this.goalSelector.add(2, new ZombieAttackGoal(instance, config.attackSpeed, false));
        this.goalSelector.add(6, new MoveThroughVillageGoal(instance, 1.0, false, 4, instance::canBreakDoors));
        this.goalSelector.add(7, new WanderAroundFarGoal(instance, 1.0));
        if (config.revengeEnabled)
        {
            if (config.groupRevengeEnabled)
            {
                this.targetSelector.add(config.revengePriority, new RevengeGoal(this).setGroupRevenge());
            } else
            {
                this.targetSelector.add(config.revengePriority, new RevengeGoal(this));
            }
        }

        if (config.enablePounce)
        {
            this.goalSelector.add(config.pouncePriority, new ZombiePounceAtTargetGoal(instance, config.pounceVelocity));
        }

        for (String s : config.targets)
        {
            String[] strings = s.split(",");
            Class<? extends Entity> clazz;


            if (strings[0].contains("player"))
            {
                clazz = PlayerEntity.class;
            } else {
                Optional<EntityType<?>> optionalEntityType = EntityType.get(strings[0]);
                if (optionalEntityType.isEmpty())
                {
                    continue;
                }
                Entity tmp = optionalEntityType.get().create(getWorld());
                clazz = Objects.requireNonNull(tmp).getClass();
                tmp.remove(RemovalReason.DISCARDED);
                if (!LivingEntity.class.isAssignableFrom(clazz))
                {
                    continue;
                }
            }
            int priority = Integer.parseInt(strings[1]);
            boolean vis = Boolean.parseBoolean(strings[2]);
            boolean nav = Boolean.parseBoolean(strings[3]);
            this.targetSelector.add(priority, new ActiveTargetGoal<>(instance, (Class<LivingEntity>) clazz, vis, nav));
        }

        ((MobNavigation) instance.getNavigation()).setAvoidSunlight(config.avoidSunlight);
        ((MobNavigation) instance.getNavigation()).setCanPathThroughDoors(config.pathThroughDoors);

        this.dataTracker.startTracking(Apocalypse.IS_DIGGING, false);
        if (config.enableDigging)
        {
            this.goalSelector.add(config.blockBreakPriority, new PrioritizedZombieBreakBlockGoal(config.blockBreakPriority, instance));
        }
    }

    /**
     * @author me
     * @reason yes
     */
    @Overwrite
    public boolean burnsInDaylight()
    {
        return Apocalypse.config.zombie.burnsInDaylight;
    }



    /**
     * @author me
     * @reason yes
     */
    @Overwrite
    public boolean canBreakDoors()
    {
        return false;
    }

}

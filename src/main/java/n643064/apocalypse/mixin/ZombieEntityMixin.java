package n643064.apocalypse.mixin;

import n643064.apocalypse.Apocalypse;
import n643064.apocalypse.core.entity.goal.PrioritizedZombieBreakBlockGoal;
import n643064.apocalypse.core.entity.goal.ZombiePounceAtTargetGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static n643064.apocalypse.Apocalypse.ENTITY_LIST;

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
        final Apocalypse.ApocalypseConfig.Zombie config = Apocalypse.config.zombie;
        if (!config.modifyPiglins && instance instanceof ZombifiedPiglinEntity piglin)
        {
            piglin.initCustomGoals();
            return;
        }
        this.goalSelector.add(2, new ZombieAttackGoal(instance, config.attackSpeed, false));
        this.goalSelector.add(6, new MoveThroughVillageGoal(instance, 1.0, false, 4, instance::canBreakDoors));
        this.goalSelector.add(7, new WanderAroundFarGoal(instance, 1.0));
        if (config.revengeEnabled)
        {
            if (config.groupRevengeEnabled)
            {
                if (config.modifyPiglins)
                    this.targetSelector.add(config.revengePriority, new RevengeGoal(this).setGroupRevenge());
                else
                    this.targetSelector.add(config.revengePriority, new RevengeGoal(this).setGroupRevenge(ZombifiedPiglinEntity.class));

            } else
            {
                this.targetSelector.add(config.revengePriority, new RevengeGoal(this));
            }
        }

        if (config.enablePounce)
        {
            this.goalSelector.add(config.pouncePriority, new ZombiePounceAtTargetGoal(instance, config.pounceVelocity));
        }

        if (ENTITY_LIST == null)
            Apocalypse.generateEntityList(getWorld());

        for (Apocalypse.TargetedEntity e : ENTITY_LIST)
        {
            this.targetSelector.add(e.priority(), new ActiveTargetGoal<>(instance, (Class<LivingEntity>) e.clazz(), e.visibility(), e.navigationCheck()));
        }

        ((MobNavigation) instance.getNavigation()).setAvoidSunlight(config.avoidSunlight);
        ((MobNavigation) instance.getNavigation()).setCanPathThroughDoors(config.pathThroughDoors);

        if (config.enableDigging)
        {
            this.goalSelector.add(config.blockBreakPriority, new PrioritizedZombieBreakBlockGoal(config.blockBreakPriority, instance));
        }
    }
    
    @Inject(method = "initDataTracker", at = @At("TAIL"))
    protected void initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(Apocalypse.IS_DIGGING, false);
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
     * @author Wake up. Please.
     * @reason If you're reading this, you've been in a coma for almost 20 years because of a car accident. We're trying a new technique. We don't know where this message will end up in your dream, but we hope we're getting through. Please wake up.
     */
    @Overwrite
    public boolean canBreakDoors()
    {
        return !Apocalypse.config.zombie.enableDigging;
    }

}

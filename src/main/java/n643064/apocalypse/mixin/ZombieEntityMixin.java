package n643064.apocalypse.mixin;

import n643064.apocalypse.Apocalypse;
import n643064.apocalypse.core.entity.goal.CustomZombieAttackGoal;
import n643064.apocalypse.core.entity.goal.ZombieBreakBlockGoal;
import n643064.apocalypse.core.entity.goal.ZombiePounceAtTargetGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends HostileEntity
{
    protected ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world)
    {
        super(entityType, world);
    }


    // TODO: Make zombies persistent, implement wandering hordes

    @Redirect(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/ZombieEntity;initCustomGoals()V"))
    private void initCustomGoals(ZombieEntity instance)
    {

        this.goalSelector.add(2, new ZombieAttackGoal(instance, 1.4, false));
        this.goalSelector.add(6, new MoveThroughVillageGoal(instance, 1.0, false, 4, instance::canBreakDoors));
        this.goalSelector.add(7, new WanderAroundFarGoal(instance, 1.0));
        this.targetSelector.add(1, (new RevengeGoal(this)).setGroupRevenge(ZombifiedPiglinEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(instance, PlayerEntity.class, false, false));
        this.targetSelector.add(3, new ActiveTargetGoal<>(instance, MerchantEntity.class, false, false));
        this.targetSelector.add(3, new ActiveTargetGoal<>(instance, IronGolemEntity.class, false, false));
        this.targetSelector.add(5, new ActiveTargetGoal<>(instance, TurtleEntity.class, 10, false, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));


        this.goalSelector.add(20, new ZombiePounceAtTargetGoal(instance, 0.2f));
        this.goalSelector.add(2, new ZombieBreakBlockGoal(instance));
        ((MobNavigation) instance.getNavigation()).setAvoidSunlight(false);
        ((MobNavigation) instance.getNavigation()).setCanPathThroughDoors(true);

        this.dataTracker.startTracking(Apocalypse.IS_DIGGING, false);


    }


    @Inject(method = "burnsInDaylight", at = @At("HEAD"), cancellable = true)
    private void burnsInDaylight(CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue(false);
    }

    @Redirect(method = "createZombieAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/HostileEntity;createHostileAttributes()Lnet/minecraft/entity/attribute/DefaultAttributeContainer$Builder;"))
    private static DefaultAttributeContainer.Builder createZombieAttributes()
    {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 70.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0)
                .add(EntityAttributes.GENERIC_ARMOR, 3.0)
                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2);
    }

    @Inject(method = "canBreakDoors", at = @At("TAIL"), cancellable = true)
    private void canBreakDoors(CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue(true);
    }


}

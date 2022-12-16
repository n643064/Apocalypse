package n643064.apocalypse.mixin;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import n643064.apocalypse.Apocalypse;
import n643064.apocalypse.core.entity.goal.PrioritizedZombieBreakBlockGoal;
import n643064.apocalypse.core.entity.goal.ZombiePounceAtTargetGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static n643064.apocalypse.Apocalypse.IS_DIGGING;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends HostileEntity
{
    @Shadow @Final protected abstract void initGoals();

    @Shadow @Final public abstract boolean onKilledOther(ServerWorld world, LivingEntity other);

    protected ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world)
    {
        super(entityType, world);
    }

    @Redirect(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/ZombieEntity;initCustomGoals()V"))
    private void initCustomGoals(ZombieEntity instance)
    {
        Apocalypse.ApocalypseConfig.Zombie config = AutoConfig.getConfigHolder(Apocalypse.ApocalypseConfig.class).get().zombie;
        this.goalSelector.add(2, new ZombieAttackGoal(instance, config.attackSpeed, false));
        this.goalSelector.add(6, new MoveThroughVillageGoal(instance, 1.0, false, 4, instance::canBreakDoors));
        this.goalSelector.add(7, new WanderAroundFarGoal(instance, 1.0));
        this.targetSelector.add(1, (new RevengeGoal(this)).setGroupRevenge(ZombifiedPiglinEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(instance, PlayerEntity.class, false, false));
        this.targetSelector.add(3, new ActiveTargetGoal<>(instance, MerchantEntity.class, false, false));
        this.targetSelector.add(3, new ActiveTargetGoal<>(instance, IronGolemEntity.class, false, false));
        this.targetSelector.add(5, new ActiveTargetGoal<>(instance, TurtleEntity.class, 10, false, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));

        this.targetSelector.add(6, new ActiveTargetGoal<>(instance, SheepEntity.class, false, false));
        this.targetSelector.add(6, new ActiveTargetGoal<>(instance, CowEntity.class, false, false));
        this.targetSelector.add(6, new ActiveTargetGoal<>(instance, ChickenEntity.class, false, false));
        this.targetSelector.add(6, new ActiveTargetGoal<>(instance, PigEntity.class, false, false));
        this.targetSelector.add(12, new ActiveTargetGoal<>(instance, PolarBearEntity.class, false, false));
        this.targetSelector.add(12, new ActiveTargetGoal<>(instance, HorseEntity.class, false, false));
        this.targetSelector.add(12, new ActiveTargetGoal<>(instance, MuleEntity.class, false, false));
        this.targetSelector.add(12, new ActiveTargetGoal<>(instance, DonkeyEntity.class, false, false));
        this.targetSelector.add(12, new ActiveTargetGoal<>(instance, WolfEntity.class, false, false));
        this.targetSelector.add(12, new ActiveTargetGoal<>(instance, CatEntity.class, false, false));
        this.targetSelector.add(12, new ActiveTargetGoal<>(instance, DolphinEntity.class, false, false));


        this.goalSelector.add(14, new ZombiePounceAtTargetGoal(instance, config.pounceVelocity));
        this.goalSelector.add(2, new PrioritizedZombieBreakBlockGoal(2, instance));
        ((MobNavigation) instance.getNavigation()).setAvoidSunlight(false);
        ((MobNavigation) instance.getNavigation()).setCanPathThroughDoors(true);

        this.dataTracker.startTracking(IS_DIGGING, false);

    }
    
    @Inject(method = "burnsInDaylight", at = @At("HEAD"), cancellable = true)
    private void burnsInDaylight(CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue(false);
    }

    @Inject(method = "canBreakDoors", at = @At("TAIL"), cancellable = true)
    private void canBreakDoors(CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue(true);
    }




}

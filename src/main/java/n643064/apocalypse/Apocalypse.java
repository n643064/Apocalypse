package n643064.apocalypse;


import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import n643064.apocalypse.core.util.ConfigSavedEvent;
import n643064.apocalypse.core.util.CustomGsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class Apocalypse implements ModInitializer
{

    public static final String MODID = "apocalypse";
    public static TrackedData<Boolean> IS_DIGGING;
    public static ApocalypseConfig config;

    public record TargetedEntity(Class<? extends LivingEntity> clazz, int priority, boolean visibility, boolean navigationCheck) {}

    public static TargetedEntity[] ENTITY_LIST = null;

    @Override
    public void onInitialize()
    {
        AutoConfig.register(ApocalypseConfig.class, CustomGsonConfigSerializer::new);
        IS_DIGGING = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

        ConfigSavedEvent.EVENT.register(() ->
        {
            config = AutoConfig.getConfigHolder(ApocalypseConfig.class).get();
            ENTITY_LIST = null;
            return ActionResult.PASS;
        });

        ConfigSavedEvent.EVENT.invoker().call();
    }

    @SuppressWarnings("unchecked")
    public static void generateEntityList(World world)
    {
        final ArrayList<TargetedEntity> out = new ArrayList<>();
        for (String s : config.zombie.targets)
        {
            final String[] strings = s.split(",");
            final Class<? extends Entity> clazz;

            if (Objects.equals(strings[0], "player") || strings[0].endsWith(":player"))
            {
                clazz = PlayerEntity.class;
            } else {
                Optional<EntityType<?>> optionalEntityType = EntityType.get(strings[0]);
                if (optionalEntityType.isEmpty())
                {
                    continue;
                }
                Entity tmp = optionalEntityType.get().create(world);
                clazz = Objects.requireNonNull(tmp).getClass();
                tmp.remove(Entity.RemovalReason.DISCARDED);
                if (!LivingEntity.class.isAssignableFrom(clazz))
                {
                    continue;
                }
            }
            final int priority = Integer.parseInt(strings[1]);
            final boolean vis = Boolean.parseBoolean(strings[2]);
            final boolean nav = Boolean.parseBoolean(strings[3]);

            out.add(new TargetedEntity((Class<? extends LivingEntity>) clazz, priority, vis, nav));
        }
        ENTITY_LIST = out.toArray(TargetedEntity[]::new);
    }


    @Config(name = Apocalypse.MODID)
    public static class ApocalypseConfig implements ConfigData
    {
        @ConfigEntry.Gui.CollapsibleObject
        public Horde horde = new Horde();

        @ConfigEntry.Gui.CollapsibleObject
        public Zombie zombie = new Zombie();

        public static class Horde
        {
            public boolean enabled = true;
            public int cooldownMin = 300; // 5 minutes
            public int cooldownMax = 600; // 10 minutes
            public int distanceMin = 64; // 4 chunks
            public int distanceMax = 128; // 8 chunks
            public int ZombieAmountMin = 8;
            public int ZombieAmountMax = 16;
            public boolean PersistentZombies = true;
        }
        public static class Zombie
        {
           public float attackSpeed = 1.5f;
           public boolean burnsInDaylight = false;
           public boolean avoidSunlight = false;
           public boolean pathThroughDoors = true;
           public boolean enablePounce = true;
           public float pounceVelocity = 0.2f;
           public int pouncePriority = 14;
           public boolean revengeEnabled = true;
           public boolean groupRevengeEnabled = true;
           public int revengePriority = 1;
           public String[] targets = new String[]{
                   "minecraft:player,2,false,false",
                   "minecraft:villager,2,false,false",
                   "minecraft:wandering_trader,3,false,false",
                   "minecraft:iron_golem,3,false,false",
                   "minecraft:turtle,5,false,false",
                   "minecraft:sheep,6,false,false",
                   "minecraft:cow,6,false,false",
                   "minecraft:pig,6,false,false",
                   "minecraft:polar_bear,12,false,false",
                   "minecraft:horse,8,false,false",
                   "minecraft:mule,8,false,false",
                   "minecraft:donkey,8,false,false",
                   "minecraft:wolf,8,false,false",
                   "minecraft:dolphin,12,false,false"
           };
           public boolean enableDigging = true;
           public int blockBreakPriority = 2;
           public boolean dropBrokenBlocks = true;
           public boolean instantDoorBreak = true;
           public boolean climbEachOther = true;
           public float climbingVelocity = 0.2f;
           public float instantDoorBreakHardness = 5f;
           public float diggingProgressTick = 0.05f;
           public int maximumTargetHardness = 20;
           public boolean modifyPiglins = true;
        }

    }
}

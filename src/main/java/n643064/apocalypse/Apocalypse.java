package n643064.apocalypse;


import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import n643064.apocalypse.core.util.ConfigSavedEvent;
import n643064.apocalypse.core.util.CustomGsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.ActionResult;

public class Apocalypse implements ModInitializer
{
    public static final String MODID = "apocalypse";
    public static TrackedData<Boolean> IS_DIGGING;
    public static ApocalypseConfig config;

    @Override
    public void onInitialize()
    {
        AutoConfig.register(ApocalypseConfig.class, CustomGsonConfigSerializer::new);
        IS_DIGGING = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

        ConfigSavedEvent.EVENT.register(() ->
        {
            config = AutoConfig.getConfigHolder(ApocalypseConfig.class).get();
            return ActionResult.PASS;
        });
        ConfigSavedEvent.EVENT.invoker().call();
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


        }

    }



}

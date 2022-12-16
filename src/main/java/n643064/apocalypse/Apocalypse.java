package n643064.apocalypse;


import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.gui.registry.DefaultGuiRegistryAccess;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.registry.Registry;

public class Apocalypse implements ModInitializer
{
    public static final String MODID = "apocalypse";
    public static MinecraftClient MC;
    public static TrackedData<Boolean> IS_DIGGING;
    @Override
    public void onInitialize()
    {
        AutoConfig.register(ApocalypseConfig.class, GsonConfigSerializer::new);
        IS_DIGGING = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        MC = MinecraftClient.getInstance();
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
            public int distanceMin = 128; // 8 chunks
            public int distanceMax = 256; // 16 chunks
            public int ZombieAmountMin = 8;
            public int ZombieAmountMax = 16;
            public boolean PersistentZombies = true;
            public boolean bypassGameRule = false;
        }
        public static class Zombie
        {
           public float attackSpeed = 1.5f;
           public float pounceVelocity = 0.2f;
        }
    }



}

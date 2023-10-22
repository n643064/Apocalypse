package n643064.apocalypse.client;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.autoconfig.gui.registry.api.GuiProvider;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import n643064.apocalypse.Apocalypse;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.ZombieEntity;

import java.lang.reflect.Field;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ApocalypseClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        GuiRegistry registry = AutoConfig.getGuiRegistry(Apocalypse.ApocalypseConfig.class);
        registry.registerTypeProvider(new NullGuiProvider(), Apocalypse.ApocalypseConfig.Horde.class);
        registry.registerTypeProvider(new NullGuiProvider(), Apocalypse.ApocalypseConfig.Zombie.class);
    }
    class NullGuiProvider implements GuiProvider
    {

        @Override
        public List<AbstractConfigListEntry> get(String s, Field field, Object o, Object o1, GuiRegistryAccess guiRegistryAccess)
        {
            return null;
        }
    }
}

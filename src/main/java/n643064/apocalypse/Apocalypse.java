package n643064.apocalypse;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

public class Apocalypse implements ModInitializer
{
    public static final String MODID = "apocalypse";

    public static TrackedData<Boolean> IS_DIGGING;

    @Override
    public void onInitialize()
    {
        IS_DIGGING = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        /*
        ClientTickEvents.START_CLIENT_TICK.register(client ->
        {
            if (client.player != null)
            {
                client.player.sendMessage(Text.of(client.player.headYaw + " " + client.player.getPitch()));
                client.player.fac
            }
        });

         */
    }
}

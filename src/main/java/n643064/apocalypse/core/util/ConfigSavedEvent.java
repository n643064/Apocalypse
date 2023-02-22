package n643064.apocalypse.core.util;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface ConfigSavedEvent
{
    Event<ConfigSavedEvent> EVENT = EventFactory.createArrayBacked(ConfigSavedEvent.class, (listeners) -> () ->
    {
        for (ConfigSavedEvent l : listeners)
        {
            ActionResult actionResult = l.call();
            if (actionResult != ActionResult.PASS)
            {
                return actionResult;
            }
        }
        return ActionResult.PASS;
    });
    ActionResult call();

}

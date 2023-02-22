package n643064.apocalypse.core.util;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

public class CustomGsonConfigSerializer extends GsonConfigSerializer
{
    @SuppressWarnings("unchecked")
    public CustomGsonConfigSerializer(Config definition, Class configClass)
    {
        super(definition, configClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void serialize(ConfigData config) throws SerializationException
    {
        ConfigSavedEvent.EVENT.invoker().call();
        super.serialize(config);
    }
}

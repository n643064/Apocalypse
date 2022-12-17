package n643064.apocalypse.core;

import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface EntityTypeInterface<T extends Entity>
{
    @Nullable
    public <T extends Entity> Class<T> getType();
}

package n643064.apocalypse.core;

import n643064.apocalypse.Apocalypse;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.spawner.SpecialSpawner;

public class HordeSpawner implements SpecialSpawner
{
    private int cooldown;
    @Override
    public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals)
    {
        final Apocalypse.ApocalypseConfig.Horde horde = Apocalypse.config.horde;
        if (!spawnMonsters || !horde.enabled)
        {
            return 0;
        }
        final Random random = world.random;
        --this.cooldown;
        if (this.cooldown > 0)
        {
            return 0;
        } else
        {

            this.cooldown += (horde.cooldownMin + random.nextInt(horde.cooldownMax)) * 20;

            if (world.getAmbientDarkness() < 5 && world.getDimension().hasSkyLight())
            {
                return 0;
            } else
            {
                for (ServerPlayerEntity player : world.getPlayers())
                {
                    int x = random.nextBetween(horde.distanceMin, horde.distanceMax);
                    if (random.nextBoolean())
                    {
                        x = x * -1;
                    }
                    int z = random.nextBetween(horde.distanceMin, horde.distanceMax);
                    if (random.nextBoolean())
                    {
                        z = z * -1;
                    }
                    BlockPos pos = player.getBlockPos().add(x, 0, z);
                    pos = new BlockPos(pos.getX(), world.getTopY(Heightmap.Type.WORLD_SURFACE, pos.getX(), pos.getZ()), pos.getZ());
                    for (int i = 0; i < random.nextBetween(horde.ZombieAmountMin, horde.ZombieAmountMax); i++)
                    {
                        ZombieEntity e = new ZombieEntity(world);
                        e.setPosition(pos.getX(), pos.getY() + random.nextInt(3), pos.getZ() + random.nextInt(8));
                        if (horde.PersistentZombies)
                        {
                            e.setPersistent();
                        }
                        world.spawnEntity(e);
                    }
                    player.sendMessage(Text.of("A horde has spawned!"));
                }
            }
        }
        
        return 0;
    }

}

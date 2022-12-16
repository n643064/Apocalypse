package n643064.apocalypse.core;

import me.shedaniel.autoconfig.AutoConfig;
import n643064.apocalypse.Apocalypse;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.spawner.Spawner;

public class HordeSpawner implements Spawner
{


    private int cooldown;
    @Override
    public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals)
    {
        Apocalypse.ApocalypseConfig.Horde horde = AutoConfig.getConfigHolder(Apocalypse.ApocalypseConfig.class).getConfig().horde;
        if (!spawnMonsters || !horde.enabled)
        {
            return 0;
        }

        if (!world.getGameRules().get(GameRules.DO_MOB_SPAWNING).get() && !horde.bypassGameRule)
        {
            return 0;
        }
        Random random = world.random;
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
                    pos = new BlockPos(pos.getX(), world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()), pos.getZ());
                    for (int i = 0; i < random.nextBetween(horde.ZombieAmountMin, horde.ZombieAmountMax); i++)
                    {
                        ZombieEntity e = new ZombieEntity(world);
                        e.setPosition(pos.getX(), pos.getY() + random.nextInt(8), pos.getZ() + random.nextInt(8));
                        if (horde.PersistentZombies)
                        {
                            e.setPersistent();
                        }
                        world.spawnEntity(e);
                    }
                    player.sendMessage(Text.of("A horde has spawned! Cooldown: " + this.cooldown));
                }
            }
        }
        
        return 0;
    }

}

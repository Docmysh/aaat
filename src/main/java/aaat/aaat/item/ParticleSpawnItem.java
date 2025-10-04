package test2.vfxtest2.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleSpawnItem extends Item {
    public ParticleSpawnItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            spawnClientParticles(level, player);
            level.playLocalSound(player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.6F,
                    0.8F + level.getRandom().nextFloat() * 0.4F, false);
        }

        player.getCooldowns().addCooldown(this, 20);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnClientParticles(Level level, Player player) {
        Vec3 basePos = player.position().add(0.0D, player.getBbHeight() * 0.5D, 0.0D);
        for (int i = 0; i < 32; i++) {
            double offsetX = (level.getRandom().nextDouble() - 0.5D) * 0.6D;
            double offsetY = level.getRandom().nextDouble() * 0.6D;
            double offsetZ = (level.getRandom().nextDouble() - 0.5D) * 0.6D;
            double speedX = (level.getRandom().nextDouble() - 0.5D) * 0.1D;
            double speedY = level.getRandom().nextDouble() * 0.2D + 0.05D;
            double speedZ = (level.getRandom().nextDouble() - 0.5D) * 0.1D;
            level.addParticle(ParticleTypes.END_ROD,
                    basePos.x + offsetX,
                    basePos.y + offsetY,
                    basePos.z + offsetZ,
                    speedX,
                    speedY,
                    speedZ);
        }
    }
}

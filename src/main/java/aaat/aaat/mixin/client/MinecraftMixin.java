package aaat.aaat.mixin.client;

import aaat.aaat.client.AaatClientHooks;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Listens for window resize events in {@link Minecraft} and delegates to the
 * AAA client hooks. The injection is marked as optional so that the mixin does
 * not prevent the game from launching when the targeted method is renamed by
 * Mojang in future versions.
 */
@Mixin(Minecraft.class)
abstract class MinecraftMixin {

    @Inject(method = "resizeDisplay", at = @At("TAIL"), require = 0)
    private void aaat$resizeCapturedDepthBuffer(CallbackInfo ci) {
        AaatClientHooks.resizeCapturedDepthBuffer();
    }
}

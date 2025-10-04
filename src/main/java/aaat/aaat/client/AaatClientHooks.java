package aaat.aaat.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Centralises client-only hooks used by AAA. {@code AAAParticles} registers the
 * captured depth buffer through these helpers, while {@link Minecraft}'s tick
 * loop invokes {@link #resizeCapturedDepthBuffer()} via a Forge client tick
 * event. This keeps the resize logic independent from method names in Mojang's
 * official mappings, ensuring that the integration remains stable when those
 * names change.
 */
public final class AaatClientHooks {
    private static final AtomicReference<RenderTarget> CAPTURED_DEPTH_BUFFER = new AtomicReference<>();
    private static volatile int cachedWidth = -1;
    private static volatile int cachedHeight = -1;

    private AaatClientHooks() {
    }

    /**
     * Registers the render target that stores the captured depth buffer. When a
     * window resize is detected this target will be resized as well.
     */
    public static void setCapturedDepthBuffer(RenderTarget target) {
        CAPTURED_DEPTH_BUFFER.set(Objects.requireNonNull(target, "target"));
        cachedWidth = target.width;
        cachedHeight = target.height;
    }

    /**
     * Clears the currently registered capture buffer.
     */
    public static void clearCapturedDepthBuffer(RenderTarget target) {
        CAPTURED_DEPTH_BUFFER.compareAndSet(target, null);
    }

    /**
     * Attempts to resize the registered capture buffer so that it matches the
     * current window size.
     */
    public static void resizeCapturedDepthBuffer() {
        RenderTarget renderTarget = CAPTURED_DEPTH_BUFFER.get();
        if (renderTarget == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return;
        }

        Window window = minecraft.getWindow();
        if (window == null) {
            return;
        }

        int width = Math.max(1, window.getWidth());
        int height = Math.max(1, window.getHeight());
        if (width == cachedWidth && height == cachedHeight) {
            return;
        }

        renderTarget.resize(width, height, Minecraft.ON_OSX);
        cachedWidth = width;
        cachedHeight = height;
    }

    /**
     * Returns the currently registered capture buffer, if any. This is exposed
     * for tests and for code that needs to query the buffer directly.
     */
    public static Optional<RenderTarget> getCapturedDepthBuffer() {
        return Optional.ofNullable(CAPTURED_DEPTH_BUFFER.get());
    }
}

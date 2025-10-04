package aaat.aaat.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Centralises client-only hooks used by AAA. The previous implementation
 * injected directly into {@link Minecraft} to resize the captured depth buffer
 * when the window size changed. Mojang renamed the targeted method in 1.20.1,
 * which caused the mixin to crash during game start. Instead of hard failing
 * when the method cannot be found, the mixin now delegates to this utility
 * which performs the resize logic in a safe, optional manner.
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

        renderTarget.resize(width, height);
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

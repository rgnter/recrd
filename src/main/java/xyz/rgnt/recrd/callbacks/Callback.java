package xyz.rgnt.recrd.callbacks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;

/**
 * Provides callback functionality for Logger
 */
public abstract class Callback {

    /**
     * Called on debug log
     * @param formattedMessage Formatted message
     */
    public abstract void onDebug(@NotNull String formattedMessage);
    public abstract void onInfo(@NotNull String formattedMessage);
    public abstract void onError(@NotNull String formattedMessage, @Nullable Throwable exception);

}

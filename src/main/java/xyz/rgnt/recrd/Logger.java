package xyz.rgnt.recrd;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import xyz.rgnt.recrd.callbacks.Callback;
import xyz.rgnt.recrd.timings.TimingsResult;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Logger
 */
public class Logger {

    private final @NotNull List<ChatColor> prefixColors = new ArrayList<ChatColor>() {{
        add(ChatColor.YELLOW);
    }};
    private @NotNull String prefix;

    private final @NotNull List<ChatColor> delimiterColors = new ArrayList<ChatColor>() {{
        add(ChatColor.GRAY);
    }};
    private @NotNull String delimiter = " :: ";

    private final @NotNull List<Callback> callbacks = new ArrayList<>();

    private boolean verbose = true;
    private boolean disableCallbacks = false;

    private long timingsStart = 0;
    private long timingsStop = 0;
    private long timingsResult = 0;

    /**
     * Empty constructor
     */
    private Logger() {
    }

    /**
     * Logger with set prefix and prefix colors
     *
     * @param prefix       Prefix
     * @param prefixColors Prefix colors
     */
    public Logger(@NotNull String prefix, @NotNull List<ChatColor> prefixColors) {
        this.prefixColors.addAll(prefixColors);
        this.prefix = prefix;
    }

    /**
     * Logger with set prefix, prefix colors and delimiter, delimiter colors
     *
     * @param prefix          Prefix
     * @param prefixColors    Prefix colors
     * @param delimiter       Delimiter
     * @param delimiterColors Delimiter colors
     */
    public Logger(@NotNull String prefix, @NotNull List<ChatColor> prefixColors, @NotNull String delimiter, @NotNull List<ChatColor> delimiterColors) {
        this(prefix, prefixColors);
        this.delimiter = delimiter;
        this.delimiterColors.addAll(delimiterColors);
    }

    /**
     * Sends debug message to console
     *
     * @param message Message
     */
    public void debug(@NotNull String message) {
        if (verbose) {
            send(ChatColor.GRAY, message, true);
            if (!disableCallbacks)
                Executors.newSingleThreadExecutor().submit(() -> this.callbacks.forEach(callback -> callback.onDebug(message)));
        }
    }

    /**
     * Sends debug message to console with message format
     *
     * @param message    Message
     * @param formatVars Format
     */
    public void debug(@NotNull String message, Object... formatVars) {
        debug(MessageFormat.format(message, formatVars));
    }


    /**
     * Sends info message to console
     *
     * @param message Message
     */
    public void info(@NotNull String message) {
        send(ChatColor.WHITE, message, true);
        if (!disableCallbacks)
            Executors.newSingleThreadExecutor().submit(() -> this.callbacks.forEach(callback -> callback.onInfo(message)));
    }

    /**
     * Sends info message to console with message format
     *
     * @param message    Message
     * @param formatVars Format
     */
    public void info(@NotNull String message, Object... formatVars) {
        info(MessageFormat.format(message, formatVars));
    }

    /**
     * Sends warn message to console
     *
     * @param message Message
     */
    public void warn(@NotNull String message) {
        send(ChatColor.YELLOW, message, true);
        if (!disableCallbacks)
            Executors.newSingleThreadExecutor().submit(() -> this.callbacks.forEach(callback -> callback.onWarn(message)));
    }

    /**
     * Sends warn message to console with message format
     *
     * @param message    Message
     * @param formatVars Format
     */
    public void warn(@NotNull String message, Object... formatVars) {
        warn(MessageFormat.format(message, formatVars));
    }

    /**
     * Sends error message format to console
     *
     * @param message Message
     */
    public void error(@NotNull String message) {
        send(ChatColor.RED, message, true);
        if (!disableCallbacks)
            Executors.newSingleThreadExecutor().submit(() -> this.callbacks.forEach(callback -> callback.onError(message, null)));
    }

    /**
     * Sends error message to console and traces exception
     *
     * @param message Message
     * @param x       Exception
     */
    public void error(@NotNull String message, @NotNull Throwable x) {
        send(ChatColor.RED, message, true);
        send(ChatColor.RED, x.toString(), false);
        trace(x);
        if (!disableCallbacks)
            Executors.newSingleThreadExecutor().submit(() -> this.callbacks.forEach(callback -> callback.onError(message, x)));
    }

    /**
     * Sends error message to console with message format
     *
     * @param message    Message
     * @param formatVars Format
     */
    public void error(@NotNull String message, Object... formatVars) {
        error(MessageFormat.format(message, formatVars));
    }

    /**
     * Sends error message to console with message format and trace
     *
     * @param message    Message
     * @param x          Exception
     * @param formatVars Format
     */
    public void error(@NotNull String message, @NotNull Throwable x, Object... formatVars) {
        error(MessageFormat.format(message, formatVars), x);
    }

    /**
     * Prints traced Throwable
     *
     * @param x Throwable to trace
     */
    public void trace(@NotNull Throwable x) {
        send(ChatColor.RED, "-", false);
        for (StackTraceElement stackTraceElement : x.getStackTrace()) {
            send(ChatColor.WHITE, "§c| §f" + stackTraceElement, false);
        }
        send(ChatColor.RED, "-", false);
    }

    /**
     * Starts timings.
     */
    public void startTimings() {
        this.timingsStart = System.nanoTime();
    }

    /**
     * Stops timings.
     *
     * @return Nano difference between start and stop.
     */
    public @NotNull TimingsResult stopTimings() {
        this.timingsStop = System.nanoTime();
        this.timingsResult = this.timingsStop - this.timingsStart;
        return new TimingsResult(this.timingsResult);
    }


    /**
     * @param verbose True to enable verbose, False to disable verbose
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * @return Whether verbose is on or off
     */
    public boolean isVerbose() {
        return this.verbose;
    }

    /**
     * @param disableCallbacks True to disable callbacks, false to enable callbacks
     */
    public void setDisableCallbacks(boolean disableCallbacks) {
        this.disableCallbacks = disableCallbacks;
    }

    /**
     * @return Whether callback are disabled or not
     */
    public boolean areCallbacksDisabled() {
        return this.disableCallbacks;
    }

    /**
     * Adds callback
     *
     * @param callback Callback
     */
    public void addCallback(@NotNull Callback callback) {
        this.callbacks.add(callback);
    }

    private void send(ChatColor payloadColor, String payload, boolean usePrefix) {
        Bukkit.getConsoleSender().sendMessage((usePrefix ? applyColors(prefix, prefixColors) + applyColors(delimiter, delimiterColors) : "") + payloadColor + payload);
    }

    private @NotNull String applyColors(@NotNull String message, @NotNull List<ChatColor> colors) {
        return colors.stream().map(ChatColor::toString).collect(Collectors.joining()) + message;
    }


    /**
     * Builder for logger
     */
    public static class Builder {

        private final Logger toBuild = new Logger();

        /**
         * Empty constructor
         */
        private Builder() {
        }

        public static @NotNull Logger.Builder make() {
            return new Builder();
        }


        public @NotNull Logger.Builder withPrefix(@NotNull String prefix) {
            this.toBuild.prefix = prefix;
            return this;
        }

        public @NotNull Logger.Builder prefixColors(@NotNull ChatColor... colors) {
            this.toBuild.prefixColors.addAll(Arrays.asList(colors));
            return this;
        }

        public @NotNull Logger.Builder withDelimiter(@NotNull String delimiter) {
            this.toBuild.delimiter = delimiter;
            return this;
        }

        public @NotNull Logger.Builder delimiterColors(@NotNull ChatColor... colors) {
            this.toBuild.delimiterColors.addAll(Arrays.asList(colors));
            return this;
        }

        public @NotNull Logger build() {
            return this.toBuild;
        }

    }
}

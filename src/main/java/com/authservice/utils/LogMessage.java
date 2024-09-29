package com.authservice.utils;

import org.apache.logging.log4j.Logger;

public final class LogMessage {

    private static final ThreadLocal<String> LOG_MESSAGE = new ThreadLocal<>();

    private LogMessage() {
        // Prevent instantiation
    }

    public static void setLogMessagePrefix(final String logMessagePrefix) {
        if (logMessagePrefix != null && !logMessagePrefix.isEmpty()) {
            LOG_MESSAGE.set(logMessagePrefix + " : ");
        } else {
            LOG_MESSAGE.set("");
        }
    }

    public static void close() {
        LOG_MESSAGE.remove();
    }

    private static String getPrefix() {
        return LOG_MESSAGE.get() != null ? LOG_MESSAGE.get() : "";
    }

    public static void log(final Logger logger, final Object object) {
        logger.info(getPrefix() + object);
    }

    // Overloaded log method for multiple messages using varargs
    public static void log(final Logger logger, final String... messages) {
        StringBuilder sb = new StringBuilder(getPrefix());
        for (String message : messages) {
            sb.append(message).append(" ");
        }
        logger.info(sb.toString().trim());
    }

    public static void warn(final Logger logger, final Object object) {
        logger.warn(getPrefix() + object);
    }

    public static void debug(final Logger logger, final Object object) {
        logger.debug(getPrefix() + object);
    }

    public static void logException(final Logger logger, final Exception e) {
        logger.error(getPrefix(), e);
    }
}

package com.syhpear.client.util;

import org.slf4j.LoggerFactory;

/**
 * Syhpear Client — Logger
 * Thin wrapper around SLF4J with Syhpear prefix.
 */
public class Logger {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger("SyhpearClient");
    private static boolean debugMode = false;

    public static void info(String msg)  { LOG.info("[Syhpear] " + msg); }
    public static void warn(String msg)  { LOG.warn("[Syhpear] " + msg); }
    public static void error(String msg) { LOG.error("[Syhpear] " + msg); }
    public static void debug(String msg) { if (debugMode) LOG.debug("[Syhpear] " + msg); }

    public static void setDebug(boolean d) { debugMode = d; }
    public static boolean isDebug()        { return debugMode; }
}

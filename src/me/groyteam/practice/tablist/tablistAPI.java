package me.groyteam.practice.tablist;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class tablistAPI  {

    public static void tablistAPI(final Player player, String header, String footer) {
        if (header == null) {
            header = "";
        }
        header = tablist.tk(header);
        if (footer == null) {
            footer = "";
        }
        footer = tablist.tk(footer);
        final tablistAPIEvent tablistAPIEvent = new tablistAPIEvent(player, header, footer);
        Bukkit.getPluginManager().callEvent(tablistAPIEvent);
        if (tablistAPIEvent.isCancelled()) {
            return;
        }
        try {
            final Object tabHeader = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + header + "\"}");
            final Object tabFooter = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + footer + "\"}");
            final Constructor<?> titleConstructor = getNMSClass("PacketPlayOutPlayerListHeaderFooter").getConstructor((Class<?>[])new Class[0]);
            final Object packet = titleConstructor.newInstance();
            final Field aField = packet.getClass().getDeclaredField("a");
            aField.setAccessible(true);
            aField.set(packet, tabHeader);
            final Field bField = packet.getClass().getDeclaredField("b");
            bField.setAccessible(true);
            bField.set(packet, tabFooter);
            sendPacket(player, packet);
        }
        catch (IllegalAccessException ex) {}
        catch (IllegalArgumentException ex2) {}
        catch (InstantiationException ex3) {}
        catch (NoSuchFieldException ex4) {}
        catch (NoSuchMethodException ex5) {}
        catch (SecurityException ex6) {}
        catch (InvocationTargetException ex7) {}
    }
    private static Object getNMSPlayer(final Player p) {
        try {
            return p.getClass().getMethod("getHandle", (Class<?>[])new Class[0]).invoke(p);
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException ex3) {
            final Exception ex2;
            final Exception ex = ex3;
            return null;
        }
    }

    public static Class<?> getNMSClass(final String name) {
        final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        }
        catch (ClassNotFoundException ex) {
            return null;
        }
    }

    public static void sendPacket(final Player player, final Object packet) {
        try {
            final Object handle = getNMSPlayer(player);
            final Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        }
        catch (IllegalAccessException ex) {}
        catch (IllegalArgumentException ex2) {}
        catch (NoSuchFieldException ex3) {}
        catch (NoSuchMethodException ex4) {}
        catch (SecurityException ex5) {}
        catch (InvocationTargetException ex6) {}
    }
}


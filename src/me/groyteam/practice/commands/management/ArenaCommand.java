package me.groyteam.practice.commands.management;

import org.bukkit.Location;
import me.groyteam.practice.arena.Arena;
import me.groyteam.practice.runnable.ArenaCommandRunnable;
import me.groyteam.practice.CustomLocation;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import me.groyteam.practice.Practice;
import org.bukkit.command.Command;

public class ArenaCommand extends Command
{
    private static final String NO_ARENA;
    private final Practice plugin;
    
    static {
        NO_ARENA = "§3§lArenaPvP §8» §cEsa arena no existe.";
    }
    
    public ArenaCommand() {
        super("arena");
        this.plugin = Practice.getInstance();
        this.setDescription("Arenas command.");
        this.setUsage("§3§lArenaPvP §8» §fUso: §3/arena §3(subcomando) (args)§f.");
    }
    
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        if (!player.hasPermission("practice.admin")) {
            player.sendMessage("§cComando desconocido.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(this.usageMessage);
            return true;
        }
        final Arena arena = this.plugin.getArenaManager().getArena(args[1]);
        Label_1596: {
            final String lowerCase;
            switch (lowerCase = args[0].toLowerCase()) {
                case "create": {
                    if (arena == null) {
                        this.plugin.getArenaManager().createArena(args[1]);
                        sender.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha creado la arena: " + args[1] + ".");
                        return true;
                    }
                    sender.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cEsa arena ya existe.");
                    return true;
                }
                case "delete": {
                    if (arena != null) {
                        this.plugin.getArenaManager().deleteArena(args[1]);
                        sender.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha eliminado la arena: " + args[1] + ".");
                        return true;
                    }
                    sender.sendMessage(ArenaCommand.NO_ARENA);
                    return true;
                }
                case "enable": {
                    break;
                }
                case "manage": {
                    this.plugin.getArenaManager().openArenaSystemUI(player);
                    return true;
                }
                case "a": {
                    if (arena != null) {
                        final Location location = player.getLocation();
                        if (args.length < 3 || !args[2].equalsIgnoreCase("-e")) {
                            location.setX(location.getBlockX() + 0.5);
                            location.setY(location.getBlockY() + 3.0);
                            location.setZ(location.getBlockZ() + 0.5);
                        }
                        arena.setA(CustomLocation.fromBukkitLocation(location));
                        sender.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha posicionado la posición A la arena: " + args[1] + ".");
                        return true;
                    }
                    sender.sendMessage(ArenaCommand.NO_ARENA);
                    return true;
                }
                case "b": {
                    if (arena != null) {
                        final Location location = player.getLocation();
                        if (args.length < 3 || !args[2].equalsIgnoreCase("-e")) {
                            location.setX(location.getBlockX() + 0.5);
                            location.setY(location.getBlockY() + 3.0);
                            location.setZ(location.getBlockZ() + 0.5);
                        }
                        arena.setB(CustomLocation.fromBukkitLocation(location));
                        sender.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha posicionado la posición B la arena: " + args[1] + ".");
                        return true;
                    }
                    sender.sendMessage(ArenaCommand.NO_ARENA);
                    return true;
                }
                case "max": {
                    if (arena != null) {
                        arena.setMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                        sender.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el maximo de la arena: " + args[1] + ".");
                        return true;
                    }
                    sender.sendMessage(ArenaCommand.NO_ARENA);
                    return true;
                }
                case "min": {
                    if (arena != null) {
                        arena.setMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                        sender.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el minimo de la arena: " + args[1] + ".");
                        return true;
                    }
                    sender.sendMessage(ArenaCommand.NO_ARENA);
                    return true;
                }
                case "help": {
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.RED + "§3§lArenaPvP help arena:");
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.RED + "§2● §3/arena create (nombre): §fCrear arena.");
                    sender.sendMessage(ChatColor.RED + "§2● §3/arena delete (nombre): §fEliminar Arena");
                    sender.sendMessage(ChatColor.RED + "§2● §3/arena a (nombre): §fSetear posición a de la arena.");
                    sender.sendMessage(ChatColor.RED + "§2● §3/arena b (nombre): §fSetear posición b de la arena.");
                    sender.sendMessage(ChatColor.RED + "§2● §3/arena min (nombre): §fSetear el minimo de la arena.");
                    sender.sendMessage(ChatColor.RED + "§2● §3/arena max (nombre): §fSetear el maximo de la arena.");
                    sender.sendMessage(ChatColor.RED + "§2● §3/arena enable (nombre): §fActivar una arena.");
                    sender.sendMessage(ChatColor.RED + "§2● §3/arena disable (nombre): §fDesactivar una arena.");
                    sender.sendMessage(ChatColor.RED + "§2● §3/arena generate (nombre): §fGenerar una arena.");
                    sender.sendMessage(ChatColor.RED + "§2● §3/arena save (nombre): §fSalvar arena.");
                    sender.sendMessage(ChatColor.RED + "§2● §3/arena manage (nombre): §fAbrir menu de arena.");
                    sender.sendMessage("");
                    sender.sendMessage("§3§lPlugin by GroyLandTeam");
                    return true;
                }
                case "save": {
                    this.plugin.getArenaManager().reloadArenas();
                    sender.sendMessage(ChatColor.GREEN + "Se ha recargado todas las arenas.");
                    return true;
                }
                case "disable": {
                    break;
                }
                case "generate": {
                    if (args.length == 3) {
                        final int arenas = Integer.parseInt(args[2]);
                        this.plugin.getServer().getScheduler().runTask(this.plugin, new ArenaCommandRunnable(this.plugin, arena, arenas));
                        this.plugin.getArenaManager().setGeneratingArenaRunnables(this.plugin.getArenaManager().getGeneratingArenaRunnables() + 1);
                        return true;
                    }
                    sender.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §fUso: §3/arena generate (arena) (arenas)§f.");
                    return true;
                }
                default:
                    break Label_1596;
            }
            if (arena != null) {
                arena.setEnabled(!arena.isEnabled());
                sender.sendMessage(arena.isEnabled() ? (ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha activado la arena: " + args[1] + ".") : (ChatColor.RED + "§3§lArenaPvP §8» §cSe ha desactivado: " + args[1] + "."));
                return true;
            }
            sender.sendMessage(ArenaCommand.NO_ARENA);
            return true;
        }
        sender.sendMessage(this.usageMessage);
        return true;
    }
}

package me.groyteam.practice.commands.management;

import me.groyteam.practice.CustomLocation;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import me.groyteam.practice.Practice;
import org.bukkit.command.Command;

public class SpawnsCommand extends Command
{
    private final Practice plugin;
    
    public SpawnsCommand() {
        super("setspawn");
        this.plugin = Practice.getInstance();
        this.setDescription("Setear spawn.");
        this.setUsage(ChatColor.RED + "§3§lArenaPvP §8» §fUso: §3/setspawn (subcomando)§f.");
    }
    
    @Override
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        if (!player.hasPermission("practice.admin")) {
            player.sendMessage(ChatColor.RED + "Comando desconocido.");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(this.usageMessage);
            return true;
        }
        final String lowerCase;
        switch (lowerCase = args[0].toLowerCase()) {
            case "parkourgamelocation": {
                this.plugin.getSpawnManager().setParkourGameLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el spawn para parkour.");
                break;
            }
            case "sumomax": {
                this.plugin.getSpawnManager().setSumoMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el máximo para sumo.");
                break;
            }
            case "sumomin": {
                this.plugin.getSpawnManager().setSumoMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el minimo para sumo.");
                break;
            }
            case "editormax": {
                this.plugin.getSpawnManager().setEditorMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el máximo para el editor.");
                break;
            }
            case "editormin": {
                this.plugin.getSpawnManager().setEditorMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el minimo para el editor.");
                break;
            }
            case "redroversecond": {
                this.plugin.getSpawnManager().setRedroverSecond(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el spawn B para redrover.");
                break;
            }
            case "redrovermax": {
                this.plugin.getSpawnManager().setRedroverMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el máximo para redrover.");
                break;
            }
            case "redrovermin": {
                this.plugin.getSpawnManager().setRedroverMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el minimo para redrover.");
                break;
            }
            case "spawnlocation": {
                this.plugin.getSpawnManager().setSpawnLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el spawn principal.");
                break;
            }
            case "oitcmax": {
                this.plugin.getSpawnManager().setOitcMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el máximo para oitc.");
                break;
            }
            case "oitcmin": {
                this.plugin.getSpawnManager().setOitcMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el minimo para oitc.");
                break;
            }
            case "oitclocation": {
                this.plugin.getSpawnManager().setOitcLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el spawn OITC.");
                break;
            }
            case "parkourlocation": {
                this.plugin.getSpawnManager().setParkourLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el spawn parkour.");
                break;
            }
            case "redroverfirst": {
                this.plugin.getSpawnManager().setRedroverFirst(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el spawn B para redrover.");
                break;
            }
            case "redroverlocation": {
                this.plugin.getSpawnManager().setRedroverLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el spawn redrover.");
                break;
            }
            case "parkourmax": {
                this.plugin.getSpawnManager().setParkourMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el máximo para parkour.");
                break;
            }
            case "parkourmin": {
                this.plugin.getSpawnManager().setParkourMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el minimo para parkour");
                break;
            }
            case "sumolocation": {
                this.plugin.getSpawnManager().setSumoLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el spawn sumo.");
                break;
            }
            case "editorlocation": {
                this.plugin.getSpawnManager().setEditorLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el spawn editor.");
                break;
            }
            case "sumofirst": {
                this.plugin.getSpawnManager().setSumoFirst(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el spawn A para sumo.");
                break;
            }
            case "oitcspawnpoints": {
                this.plugin.getSpawnManager().getOitcSpawnpoints().add(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el spawn-point para oitc: #" + this.plugin.getSpawnManager().getOitcSpawnpoints().size() + ".");
                break;
            }
            case "sumosecond": {
                this.plugin.getSpawnManager().setSumoSecond(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el spawn B para sumo.");
                break;
            }
            case "spawnmax": {
                this.plugin.getSpawnManager().setSpawnMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el spawn-max.");
                break;
            }
            case "spawnmin": {
                this.plugin.getSpawnManager().setSpawnMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el spawn-min.");
                break;
            }
            default:
                break;
        }
        return false;
    }
}

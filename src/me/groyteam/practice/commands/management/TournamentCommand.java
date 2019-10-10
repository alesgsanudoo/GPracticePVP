package me.groyteam.practice.commands.management;

import me.groyteam.practice.kit.Kit;
import me.groyteam.practice.tournament.Tournament;
import org.bukkit.Bukkit;
import me.groyteam.practice.util.Clickable;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import me.groyteam.practice.Practice;
import org.bukkit.command.Command;

public class TournamentCommand extends Command
{
    private final Practice plugin;
    private static final String[] HELP_ADMIN_MESSAGE;
    private static final String[] HELP_REGULAR_MESSAGE;
    
    static {
        HELP_ADMIN_MESSAGE = new String[] {ChatColor.GOLD + "§3§lComandos de Torneos:","§f", ChatColor.RESET + "§2● §3/tournament start: §f" + ChatColor.GRAY + "§fEmpezar un torneo", ChatColor.RESET + "§2● §3/tournament stop: §f" + ChatColor.GRAY + "§fParar un torneo.", ChatColor.RESET + "§2● §3/tournament alert §f" + ChatColor.GRAY + "§fAvisar de un torneo."};
        HELP_REGULAR_MESSAGE = new String[] {ChatColor.GOLD + "§3§lComandos de Torneos:","§f",ChatColor.RESET + "§2● §3/join (id):§f " + ChatColor.GRAY + "§fEntrar en un torneo.", ChatColor.RESET + "§2● §3/leave: §f" + ChatColor.GRAY + "§fSalir del Torneo.", ChatColor.RESET + "§2● §3/status: " + ChatColor.GRAY + "§fEstado de un torneo.", "", "§3§lMade by GroyLandTeam"};
    }
    
    public TournamentCommand() {
        super("tournament");
        this.plugin = Practice.getInstance();
        this.setUsage(ChatColor.RED + "§3§lArenaPvP §8» §fUsage: §3/tournament (args)§f.");
    }
    
    public boolean execute(final CommandSender commandSender, final String s, final String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        final Player player = (Player)commandSender;
        if (args.length == 0) {
            commandSender.sendMessage(player.hasPermission("practice.admin") ? TournamentCommand.HELP_ADMIN_MESSAGE : TournamentCommand.HELP_REGULAR_MESSAGE);
            return true;
        }
        if (!player.hasPermission("practice.admin")) {
            player.sendMessage(ChatColor.RED + "No tienes permisos.");
            return true;
        }
        final String lowerCase;
        switch (lowerCase = args[0].toLowerCase()) {
            case "stop": {
                if (args.length != 2) {
                    commandSender.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §aUso: §3/tournament stop (id)§f.");
                    return false;
                }
                final int id = Integer.parseInt(args[1]);
                final Tournament tournament = this.plugin.getTournamentManager().getTournament(Integer.valueOf(id));
                if (tournament != null) {
                    this.plugin.getTournamentManager().removeTournament(id, true);
                    commandSender.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cHas removido el torneo: " + id + ".");
                    return false;
                }
                commandSender.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cEste torneo no esta.");
                return false;
            }
            case "alert": {
                if (args.length != 2) {
                    commandSender.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §fUso: §3/tournament alert (id)§f.");
                    return false;
                }
                final int id = Integer.parseInt(args[1]);
                final Tournament tournament = this.plugin.getTournamentManager().getTournament(Integer.valueOf(id));
                if (tournament != null) {
                    final String toSend = ChatColor.GOLD.toString() + "(Tournament)" + ChatColor.YELLOW + " " + tournament.getKitName() + " (" + tournament.getTeamSize() + "v" + tournament.getTeamSize() + ")" + " is starting soon. " + ChatColor.GRAY + "[Click to Join]";
                    final Clickable message = new Clickable(toSend, ChatColor.GRAY + "Click to join this tournament.", "/join " + id);
                    Bukkit.getServer().getOnlinePlayers().forEach(message::sendToPlayer);
                    return false;
                }
                return false;
            }
            case "start": {
                if (args.length == 5) {
                    try {
                        final int id = Integer.parseInt(args[1]);
                        final int teamSize = Integer.parseInt(args[3]);
                        final int size = Integer.parseInt(args[4]);
                        final String kitName = args[2];
                        if (size % teamSize != 0) {
                            commandSender.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §fEl número de jugadores y el número de team son invalido, prueba de nuevo.");
                            return true;
                        }
                        if (this.plugin.getTournamentManager().getTournament(Integer.valueOf(id)) != null) {
                            commandSender.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cEse torneo ya existe.");
                            return true;
                        }
                        final Kit kit = this.plugin.getKitManager().getKit(kitName);
                        if (kit == null) {
                            commandSender.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §fEste kit no existe.");
                            return true;
                        }
                        this.plugin.getTournamentManager().createTournament(commandSender, id, teamSize, size, kitName);
                    }
                    catch (NumberFormatException e) {
                        commandSender.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §fUso: §3/tournament start (id) (kit) (team size) (tournament size)§f.");
                    }
                    return false;
                }
                commandSender.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §fUso: §3/tournament start (id) (kit) (team size) (tournament size)§f.");
                return false;
            }
            default:
                break;
        }
        commandSender.sendMessage(player.hasPermission("practice.admin") ? TournamentCommand.HELP_ADMIN_MESSAGE : TournamentCommand.HELP_REGULAR_MESSAGE);
        return false;
    }
}

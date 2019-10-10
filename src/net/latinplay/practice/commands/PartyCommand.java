package net.latinplay.practice.commands;

import net.latinplay.practice.party.Party;
import net.latinplay.practice.player.PlayerData;
import net.latinplay.practice.util.Clickable;
import java.util.function.Predicate;
import java.util.Objects;
import java.util.function.Function;
import java.util.Collection;
import java.util.UUID;
import java.util.ArrayList;
import net.latinplay.practice.util.StringUtil;
import net.latinplay.practice.player.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.Collections;
import org.bukkit.ChatColor;
import net.latinplay.practice.Practice;
import org.bukkit.command.Command;

public class PartyCommand extends Command
{
    private static String NOT_LEADER;
    private static String[] HELP_MESSAGE;
    private Practice plugin;
    
    static {
        NOT_LEADER = ChatColor.RED + "§3§lArenaPvP §8» §cNo eres el lider de la party.";
        HELP_MESSAGE = new String[] { ChatColor.GOLD + "§3§lParty user help:","", ChatColor.RESET + "§2● §3/party create: §f" + ChatColor.GRAY + " §fCrear una party.", ChatColor.RESET + "§2● §3/party leave: " + ChatColor.GRAY + "§f Salir de party actual.", ChatColor.RESET + "§2● §3/party info: " + ChatColor.GRAY + "§fTe enseña la información de la party.", ChatColor.RESET + "§2● §3/party join (nombre): " + ChatColor.GRAY + "§fEntrar a una party.", "", ChatColor.GOLD + "§3§lParty leader help:", ChatColor.RESET + "§2● §3/party open: " + ChatColor.GRAY + "§fHacer party pública.", ChatColor.RESET + "§2● §3/party lock: " + ChatColor.GRAY + "§fHacer la party privada.", ChatColor.RESET + "§2● §3/party setlimit (cantidad): " + ChatColor.GRAY + "§fSetear el maximo de jugadores.", ChatColor.RESET + "§2● §3/party invite (nombre): " + ChatColor.GRAY + "§fInvita a jugadores a tu party.", ChatColor.RESET + "§2● §3/party kick (nombre) " + ChatColor.GRAY + "§fExpulsar a un jugador de tu party.","","§3§lPlugin by GroyLandTeam"};
    }
    
    public PartyCommand() {
        super("party");
        this.plugin = Practice.getInstance();
        this.setDescription("Party Command.");
        this.setUsage(ChatColor.RED + "§3§lArenaPvP §8» §fUso: §3Uso: §3/party (subcommand) (jugador)§f.");
        this.setAliases(Collections.singletonList("p"));
    }
    
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player)sender;
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        String subCommand = (args.length < 1) ? "help" : args[0];
        Label_2893: {
            Label_2353: {
                String lowerCase;
                switch (lowerCase = subCommand.toLowerCase()) {
                    case "accept": {
                        if (party != null) {
                            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §fYa estás en una party.");
                            return true;
                        }
                        if (args.length < 2) {
                            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §fUso: §3/party accept (player)§f.");
                            return true;
                        }
                        if (playerData.getPlayerState() != PlayerState.SPAWN) {
                            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cNo puedes hacer esto mientras estás en otros modos de juego.");
                            return true;
                        }
                        Player target = this.plugin.getServer().getPlayer(args[1]);
                        if (target == null) {
                            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
                            return true;
                        }
                        Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());
                        if (targetParty == null) {
                            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cEse jugador no esta en una party.");
                            return true;
                        }
                        if (targetParty.getMembers().size() >= targetParty.getLimit()) {
                            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cHas llegado al limite de la party.");
                            return true;
                        }
                        if (!this.plugin.getPartyManager().hasPartyInvite(player.getUniqueId(), targetParty.getLeader())) {
                            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §fNo tienes ninguna petición pendiente.");
                            return true;
                        }
                        this.plugin.getPartyManager().joinParty(targetParty.getLeader(), player);
                        return true;
                    }
                    case "create": {
                        if (party != null) {
                            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cYa estás en una party.");
                            return true;
                        }
                        if (playerData.getPlayerState() != PlayerState.SPAWN) {
                            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cNo puedes hacer esto mientras estás en otros modos de juego.");
                            return true;
                        }
                        this.plugin.getPartyManager().createParty(player);
                        return true;
                    }
                    case "invite": {
                        break;
                    }
                    case "inv": {
                        break;
                    }
                    case "info": {
                        if (party == null) {
                            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cNo estás en una party.");
                            return true;
                        }
                        List<UUID> members = new ArrayList<>(party.getMembers());
                        members.remove(party.getLeader());
                        StringBuilder builder = new StringBuilder(ChatColor.RESET + "§2● §eMiembros (" + party.getMembers().size() + "): §f");
                        members.stream().map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull).forEach(member -> builder.append(ChatColor.WHITE).append(member.getName()).append(","));
                        String[] information = { ChatColor.GOLD + "§e§lInformación de la party:","", ChatColor.RESET + "§2● §eLider: §f" + ChatColor.GRAY +"§f" + this.plugin.getServer().getPlayer(party.getLeader()).getName(), ChatColor.WHITE + builder.toString(), ChatColor.RESET + "§2● §eEstado de la Party: " + ChatColor.GRAY +"§f" + (party.isOpen() ? "§aPública" : "§cPrivada")};
                        player.sendMessage(information);
                        return true;
                    }
                    case "join": {
                        if (party != null) {
                            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cYa estás en una party.");
                            return true;
                        }
                        if (args.length < 2) {
                            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §fUso: §3Uso: §3/party join (nombre)§f.");
                            return true;
                        }
                        if (playerData.getPlayerState() != PlayerState.SPAWN) {
                            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cNo puedes hacer esto mientras estás en otros modos de juego.");
                            return true;
                        }
                        Player target = this.plugin.getServer().getPlayer(args[1]);
                        if (target == null) {
                            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
                            return true;
                        }
                        Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());
                        if (targetParty == null || !targetParty.isOpen() || targetParty.getMembers().size() >= targetParty.getLimit()) {
                            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cNo puedes entrar a esta party.");
                            return true;
                        }
                        this.plugin.getPartyManager().joinParty(targetParty.getLeader(), player);
                        return true;
                    }
                    case "kick": {
                        if (party == null) {
                            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cNo estás en una party.");
                            return true;
                        }
                        if (args.length < 2) {
                            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §fUso: §3Uso: §3/party kick (nombre)§f.");
                            return true;
                        }
                        if (party.getLeader() != player.getUniqueId()) {
                            player.sendMessage(PartyCommand.NOT_LEADER);
                            return true;
                        }
                        Player target = this.plugin.getServer().getPlayer(args[1]);
                        if (target == null) {
                            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
                            return true;
                        }
                        Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());
                        if (targetParty == null || targetParty.getLeader() != party.getLeader()) {
                            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cEse jugador no esta en tu party.");
                            return true;
                        }
                        this.plugin.getPartyManager().leaveParty(target);
                        return true;
                    }
                    case "lock": {
                        break Label_2353;
                    }
                    case "open": {
                        break Label_2353;
                    }
                    case "leave": {
                        if (party == null) {
                            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cNo estás en una party.");
                            return true;
                        }
                        if (playerData.getPlayerState() != PlayerState.SPAWN) {
                            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cNo puedes hacer esto mientras estás en otros modos de juego.");
                            return true;
                        }
                        this.plugin.getPartyManager().leaveParty(player);
                        return true;
                    }
                    case "setlimit": {
                        if (party == null) {
                            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cNo estás en una party.");
                            return true;
                        }
                        if (args.length < 2) {
                            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §3/party setlimit (cantidad)§f.");
                            return true;
                        }
                        if (party.getLeader() != player.getUniqueId()) {
                            player.sendMessage(PartyCommand.NOT_LEADER);
                            return true;
                        }
                        try {
                            int limit = Integer.parseInt(args[1]);
                            if (limit < 2 || limit > 50) {
                                player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cEsa cantidad es invalida.");
                            }
                            else {
                                party.setLimit(limit);
                                player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §fHas seteado la cantidad de la party a: §3" + ChatColor.YELLOW + limit + " jugadores§f.");
                            }
                        }
                        catch (NumberFormatException e) {
                            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cEsa cantidad es invalida.");
                        }
                        return true;
                    }
                    default:
                        break Label_2893;
                }
                if (party == null) {
                    player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cNo estás en una party.");
                    return true;
                }
                if (!this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cNo eres el lider de la party.");
                    return true;
                }
                if (this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null) {
                    player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §fActualmente, estás en un torneo.");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §fUso: §3Uso: §3/party invite (nombre)§f.");
                    return true;
                }
                if (party.isOpen()) {
                    player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §fEsta party esta abierte, asi que todo el mundo puede entrar.");
                    return true;
                }
                if (party.getMembers().size() >= party.getLimit()) {
                    player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cLa party esta llena.");
                    return true;
                }
                if (party.getLeader() != player.getUniqueId()) {
                    player.sendMessage(PartyCommand.NOT_LEADER);
                    return true;
                }
                Player target = this.plugin.getServer().getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
                    return true;
                }
                PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
                if (target.getUniqueId() == player.getUniqueId()) {
                    player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cNo puedes invitarte a ti mismo.");
                    return true;
                }
                if (this.plugin.getPartyManager().getParty(target.getUniqueId()) != null) {
                    player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cEse jugador ya esta en una party.");
                    return true;
                }
                if (targetData.getPlayerState() != PlayerState.SPAWN) {
                    player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cEse jugador esta ocupado.");
                    return true;
                }
                if (this.plugin.getPartyManager().hasPartyInvite(target.getUniqueId(), player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cYa has enviado la invitación a este jugador, por favor espera.");
                    return true;
                }
                this.plugin.getPartyManager().createPartyInvite(player.getUniqueId(), target.getUniqueId());
                Clickable partyInvite = new Clickable(ChatColor.GREEN + "§3§lArenaPvP §8» §fEl jugador " + sender.getName() + ChatColor.YELLOW + "§f te ha invitado a su party." + ChatColor.GRAY + " §3[Click to Accept]", ChatColor.GRAY + "§fClick para aceptar.", "/party accept " + sender.getName());
                partyInvite.sendToPlayer(target);
                party.broadcast("§3§lArenaPvP §8» §fEl jugador §3" +target.getName() + "§f ha sido invitado a la party.");
                return true;
            }
            if (party == null) {
                player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cNo estás en una party.");
                return true;
            }
            if (party.getLeader() != player.getUniqueId()) {
                player.sendMessage(PartyCommand.NOT_LEADER);
                return true;
            }
            party.setOpen(!party.isOpen());
            party.broadcast(ChatColor.YELLOW + "§3§lArenaPvP §8» §fHas puesto tu party: " + ChatColor.BOLD + (party.isOpen() ? "§aPública" : "§cLocked"));
            return true;
        }
        player.sendMessage(PartyCommand.HELP_MESSAGE);
        return true;
    }
}

package me.groyteam.practice.commands.management;

import org.bukkit.inventory.ItemStack;
import me.groyteam.practice.arena.Arena;
import me.groyteam.practice.kit.Kit;
import me.groyteam.practice.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import me.groyteam.practice.managers.PlayerManager;
import me.groyteam.practice.Practice;
import org.bukkit.command.Command;

public class KitCommand extends Command
{
    private static final String NO_KIT;
    private static final String NO_ARENA;
    private final Practice plugin;
    private PlayerManager playerManager;
    
    static {
        NO_KIT = ChatColor.RED + "§3§lArenaPvP §8» §cEste kit no existe.";
        NO_ARENA = ChatColor.RED + "§3§lArenaPvP §8» §cEsta arena no existe.";
    }
    
    public KitCommand() {
        super("kit");
        this.plugin = Practice.getInstance();
        this.setDescription("Kit command.");
        this.setUsage(ChatColor.RED + "§3§lArenaPvP §8» §fUso: §3/kit §3(subcomando) (args)§f.");
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
        if (args.length < 2) {
            sender.sendMessage(this.usageMessage);
            return true;
        }
        final Kit kit = this.plugin.getKitManager().getKit(args[1]);
        Label_3100: {
            final String lowerCase;
            switch (lowerCase = args[0].toLowerCase()) {
                case "whitelistarena": {
                    if (args.length < 3) {
                        sender.sendMessage(this.usageMessage);
                        return true;
                    }
                    if (kit == null) {
                        sender.sendMessage(KitCommand.NO_KIT);
                        return true;
                    }
                    final Arena arena = this.plugin.getArenaManager().getArena(args[2]);
                    if (arena != null) {
                        kit.whitelistArena(arena.getName());
                        sender.sendMessage(kit.getArenaWhiteList().contains(arena.getName()) ? (ChatColor.GREEN + "§3§lArenaPvP §8» §aLa especialidad de la arena §3 " + arena.getName() + "§f es el kit kit: §3" + args[1] + ".") : (ChatColor.GREEN + "§3§lArenaPvP §8» §aYa no es la especialidad de la arena §3 " + arena.getName() + "§f es el kit kit: §3" + args[1] + "."));
                        return true;
                    }
                    sender.sendMessage(KitCommand.NO_ARENA);
                    return true;
                }
                case "excludearena": {
                    if (args.length < 3) {
                        sender.sendMessage(this.usageMessage);
                        return true;
                    }
                    if (kit == null) {
                        sender.sendMessage(KitCommand.NO_KIT);
                        return true;
                    }
                    final Arena arena = this.plugin.getArenaManager().getArena(args[2]);
                    if (arena != null) {
                        kit.excludeArena(arena.getName());
                        sender.sendMessage(kit.getExcludedArenas().contains(arena.getName()) ? (ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha excluido de la arena §3 " + arena.getName() + "§f, el kit kit: §3" + args[1] + ".") : (ChatColor.GREEN + "§3§lArenaPvP §8» §aYa  no esta excluido de la arena §3 " + arena.getName() + "§f, el kit kit: §3" + args[1] + "."));
                        return true;
                    }
                    sender.sendMessage(KitCommand.NO_ARENA);
                    return true;
                }
                case "create": {
                    if (kit == null) {
                        this.plugin.getKitManager().createKit(args[1]);
                        sender.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha creado el kit: " + args[1] + ".");
                        return true;
                    }
                    sender.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cEse kit ya existe.");
                    return true;
                }
                case "delete": {
                    if (kit != null) {
                        this.plugin.getKitManager().deleteKit(args[1]);
                        sender.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha eliminado el kit: " + args[1] + ".");
                        return true;
                    }
                    sender.sendMessage(KitCommand.NO_KIT);
                    return true;
                }
                case "enable": {
                    if (kit != null) {
                        kit.setEnabled(!kit.isEnabled());
                        sender.sendMessage(kit.isEnabled() ? (ChatColor.GREEN + "§3§lArenaPvP §8» §aHas activado el kit: " + args[1] + ".") : (ChatColor.GREEN + "§3§lArenaPvP §8» §cHas desactivado el kit: " + args[1] + "."));
                        return true;
                    }
                    break;
                }
                case "getinv": {
                    if (kit != null) {
                        player.getInventory().setContents(kit.getContents());
                        player.getInventory().setArmorContents(kit.getArmor());
                        player.updateInventory();
                        sender.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha recuperado el kit: " + args[1] + ".");
                        return true;
                    }
                    sender.sendMessage(KitCommand.NO_KIT);
                    return true;
                }
                case "ranked": {
                    if (kit != null) {
                        kit.setRanked(!kit.isRanked());
                        sender.sendMessage(kit.isRanked() ? (ChatColor.GREEN + "§3§lArenaPvP §8» §aHas activado para ranked el kit: " + args[1] + ".") : (ChatColor.RED + "§3§lArenaPvP §8» §cHas desactivado para ranked el kit: " + args[1] + "."));
                        return true;
                    }
                    sender.sendMessage(KitCommand.NO_KIT);
                    return true;
                }
                case "setinv": {
                    if (kit == null) {
                        sender.sendMessage(KitCommand.NO_KIT);
                        return true;
                    }
                    if (player.getGameMode() == GameMode.CREATIVE) {
                        sender.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §aNo puedes setear kits en creativo.");
                        return true;
                    }
                    player.updateInventory();
                    kit.setContents(player.getInventory().getContents());
                    kit.setArmor(player.getInventory().getArmorContents());
                    sender.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el contenido para el kit: " + args[1] + ".");
                    return true;
                }
                case "spleef": {
                    if (kit != null) {
                        kit.setSpleef(!kit.isSpleef());
                        sender.sendMessage(kit.isSpleef() ? (ChatColor.GREEN + "§3§lArenaPvP §8» §aHas activado para el modo spleef, el kit: " + args[1] + ".") : (ChatColor.RED + "§3§lArenaPvP §8» §cHas desactivado para el modo spleef, el kit: " + args[1] + "."));
                        return true;
                    }
                    sender.sendMessage(KitCommand.NO_KIT);
                    return true;
                }
                case "parkour": {
                    if (kit != null) {
                        kit.setParkour(!kit.isParkour());
                        sender.sendMessage(kit.isParkour() ? (ChatColor.GREEN + "§3§lArenaPvP §8» §aHas activado para el modo parkour, el kit: " + args[1] + ".") : (ChatColor.RED + "§3§lArenaPvP §8» §cHas desactivado para el modo parkour, el kit: " + args[1] + "."));
                        return true;
                    }
                    sender.sendMessage(KitCommand.NO_KIT);
                    return true;
                }
                case "seteditinv": {
                    if (kit == null) {
                        sender.sendMessage(KitCommand.NO_KIT);
                        return true;
                    }
                    if (player.getGameMode() == GameMode.CREATIVE) {
                        sender.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §aNo puedes setear kits en creativo.");
                        return true;
                    }
                    player.updateInventory();
                    kit.setKitEditContents(player.getInventory().getContents());
                    sender.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha seteado el kit editado: " + args[1] + ".");
                    return true;
                }
                case "help": {
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.RED + "§3§lArenaPvP help arena:");
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.RED + "§2● §3/kit create (nombre): §fCrear kit.");
                    sender.sendMessage(ChatColor.RED + "§2● §3/kit delete (nombre): §fEliminar kit.");
                    sender.sendMessage(ChatColor.RED + "§2● §3/kit enable (nombre): §fActivar un kit.");
                    sender.sendMessage(ChatColor.RED + "§2● §3/kit disable (nombre): §fDesactivar un kit.");
                    sender.sendMessage(ChatColor.RED + "§2● §3/kit combo (nombre): §fSelecionar kit Combo.");
                    sender.sendMessage(ChatColor.RED + "§2● §3/kit build (nombre): §fSelecionar kit Build.");
                    sender.sendMessage(ChatColor.RED + "§2● §3/kit sumo (nombre): §fSelecionar kit Sumo.");
                    sender.sendMessage(ChatColor.RED + "§2● §3/kit spleef (nombre): §fSelecionar kit Spleef.");
                    sender.sendMessage(ChatColor.RED + "§2● §3/kit parkour (nombre): §fSelecionar kit Parkour.");
                    sender.sendMessage(ChatColor.RED + "§2● §3/kit ranked (nombre): §fActivar ranked para este kit.");
                    sender.sendMessage(ChatColor.RED + "§2● §3/kit whitelistarena (nombre): §fAñadir whitelist para este mapa.");
                    sender.sendMessage(ChatColor.RED + "§2● §3/kit icon (nombre): §fSetear icon del kit.");
                    sender.sendMessage(ChatColor.RED + "§2● §3/kit setinv (nombre): §fPoner inv al kit.");
                    sender.sendMessage("");
                    sender.sendMessage("§3§lPlugin by GroyLandTeam");
                    return true;
                }
                case "icon": {
                    if (kit == null) {
                        sender.sendMessage(KitCommand.NO_KIT);
                        return true;
                    }
                    if (player.getItemInHand().getType() != Material.AIR) {
                        final ItemStack icon = ItemUtil.renameItem(player.getItemInHand().clone(), ChatColor.GREEN + kit.getName());
                        kit.setIcon(icon);
                        sender.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha establecido el icono par el kit: " + args[1] + ".");
                        return true;
                    }
                    player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cDebes tener un item en tu mano.");
                    return true;
                }
                case "sumo": {
                    if (kit != null) {
                        kit.setSumo(!kit.isSumo());
                        sender.sendMessage(kit.isSumo() ? (ChatColor.GREEN + "§3§lArenaPvP §8» §aHas activado para el modo sumo, el kit: " + args[1] + ".") : (ChatColor.RED + "§3§lArenaPvP §8» §aHas desactivado para el modo sumo, el kit: " + args[1] + "."));
                        return true;
                    }
                    sender.sendMessage(KitCommand.NO_KIT);
                    return true;
                }
                case "build": {
                    if (kit != null) {
                        kit.setBuild(!kit.isBuild());
                        sender.sendMessage(kit.isBuild() ? (ChatColor.GREEN + "§3§lArenaPvP §8» §aHas activado para el modo build, el kit: " + args[1] + ".") : (ChatColor.RED + "§3§lArenaPvP §8» §aHas desactivado para el modo build, el kit: " + args[1] + "."));
                        return true;
                    }
                    sender.sendMessage(KitCommand.NO_KIT);
                    return true;
                }
                case "combo": {
                    if (kit != null) {
                        kit.setCombo(!kit.isCombo());
                        sender.sendMessage(kit.isCombo() ? (ChatColor.GREEN + "§3§lArenaPvP §8» §aHas activado para el modo combo, el kit: " + args[1] + ".") : (ChatColor.RED + "§3§lArenaPvP §8» §aHas activado para el modo combo, el kit: " + args[1] + "."));
                        return true;
                    }
                    sender.sendMessage(KitCommand.NO_KIT);
                    return true;
                }
                case "excludearenafromallkitsbut": {
                    if (args.length < 2) {
                        sender.sendMessage(this.usageMessage);
                        return true;
                    }
                    if (kit == null) {
                        sender.sendMessage(KitCommand.NO_KIT);
                        return true;
                    }
                    final Arena arena = this.plugin.getArenaManager().getArena(args[2]);
                    if (arena != null) {
                        for (final Kit loopKit : this.plugin.getKitManager().getKits()) {
                            if (!loopKit.equals(kit)) {
                                player.performCommand("kit excludearena " + loopKit.getName() + " " + arena.getName());
                            }
                        }
                        return true;
                    }
                    sender.sendMessage(KitCommand.NO_ARENA);
                    return true;
                }
                case "disable": {
                    break;
                }
                case "geteditinv": {
                    if (kit != null) {
                        player.getInventory().setContents(kit.getKitEditContents());
                        player.updateInventory();
                        sender.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aSe ha recuperado el kit: " + args[1] + ".");
                        return true;
                    }
                    sender.sendMessage(KitCommand.NO_KIT);
                    return true;
                }
                default:
                    break Label_3100;
            }
            sender.sendMessage(KitCommand.NO_KIT);
            return true;
        }
        sender.sendMessage(this.usageMessage);
        return true;
    }
}

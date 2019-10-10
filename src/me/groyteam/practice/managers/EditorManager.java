package me.groyteam.practice.managers;

import org.bukkit.ChatColor;
import me.groyteam.practice.util.PlayerUtil;
import me.groyteam.practice.kit.Kit;
import org.bukkit.entity.Player;
import java.util.HashMap;
import me.groyteam.practice.kit.PlayerKit;
import java.util.UUID;
import java.util.Map;
import me.groyteam.practice.Practice;

public class EditorManager
{
    private final Practice plugin;
    private final Map<UUID, String> editing;
    private final Map<UUID, PlayerKit> renaming;
    
    public EditorManager() {
        this.plugin = Practice.getInstance();
        this.editing = new HashMap<>();
        this.renaming = new HashMap<>();
    }
    
    public void addEditor(final Player player, final Kit kit) {
        this.editing.put(player.getUniqueId(), kit.getName());
        this.plugin.getInventoryManager().addEditingKitInventory(player, kit);
        PlayerUtil.clearPlayer(player);
        player.teleport(this.plugin.getSpawnManager().getEditorLocation().toBukkitLocation());
        player.getInventory().setContents(kit.getContents());
        player.sendMessage(ChatColor.YELLOW + "§3§lArenaPvP §8» §fAhora, estás editando el kit " + ChatColor.GREEN + ChatColor.BOLD + kit.getName() + ChatColor.WHITE + ".");
    }
    
    public void removeEditor(final UUID editor) {
        this.renaming.remove(editor);
        this.editing.remove(editor);
        this.plugin.getInventoryManager().removeEditingKitInventory(editor);
    }
    
    public String getEditingKit(final UUID editor) {
        return this.editing.get(editor);
    }
    
    public void addRenamingKit(final UUID uuid, final PlayerKit playerKit) {
        this.renaming.put(uuid, playerKit);
    }
    
    public void removeRenamingKit(final UUID uuid) {
        this.renaming.remove(uuid);
    }
    
    public PlayerKit getRenamingKit(final UUID uuid) {
        return this.renaming.get(uuid);
    }
}

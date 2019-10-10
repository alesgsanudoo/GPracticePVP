package me.groyteam.practice.managers;

import com.google.common.collect.Lists;
import me.groyteam.practice.Practice;
import me.groyteam.practice.arena.Arena;
import me.groyteam.practice.inventory.InventorySnapshot;
import me.groyteam.practice.kit.Kit;
import me.groyteam.practice.kit.PlayerKit;
import me.groyteam.practice.match.Match;
import me.groyteam.practice.match.MatchTeam;
import me.groyteam.practice.party.Party;
import me.groyteam.practice.player.PlayerData;
import me.groyteam.practice.player.PlayerState;
import me.groyteam.practice.queue.QueueType;
import me.groyteam.practice.util.Clickable;
import me.groyteam.practice.util.ItemUtil;
import me.groyteam.practice.util.StringUtil;
import me.groyteam.practice.util.inventory.InventoryUI;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import org.bukkit.ChatColor;

import java.util.UUID;
import java.util.Map;

public class InventoryManager
{
    private static final String MORE_PLAYERS;
    private final Practice plugin;
    private final InventoryUI unrankedInventory;
    private final InventoryUI rankedInventory;
    private final InventoryUI editorInventory;
    private final InventoryUI duelInventory;
    private final InventoryUI partySplitInventory;
    private final InventoryUI partyFFAInventory;
    private final InventoryUI partyInventory;
    private final Map<String, InventoryUI> duelMapInventories;
    private final Map<String, InventoryUI> partySplitMapInventories;
    private final Map<String, InventoryUI> partyFFAMapInventories;
    private final Map<UUID, InventoryUI> editorInventories;
    private final Map<UUID, InventorySnapshot> snapshots;
    
    static {
        MORE_PLAYERS = ChatColor.RED + "§3§lArenaPvP §8» §fDebe haber al menos 2 jugadores en tu party para hacer esto.";
    }
    
    public InventoryManager() {
        this.plugin = Practice.getInstance();
        this.unrankedInventory = new InventoryUI("Modo UnRanked", true, 2);
        this.rankedInventory = new InventoryUI("Modo Ranked", true, 2);
        this.editorInventory = new InventoryUI("Edita tu kit", true, 2);
        this.duelInventory = new InventoryUI("Enviar un duelo", true, 2);
        this.partySplitInventory = new InventoryUI("Peleas dividas", true, 2);
        this.partyFFAInventory = new InventoryUI("Party FFA", true, 2);
        this.partyInventory = new InventoryUI("Pelear contra otras Parties", true, 6);
        this.duelMapInventories = new HashMap<>();
        this.partySplitMapInventories = new HashMap<>();
        this.partyFFAMapInventories = new HashMap<>();
        this.editorInventories = new HashMap<>();
        this.snapshots = new HashMap<>();
        this.setupInventories();
        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, this::updateInventories, 20L, 20L);
    }
    
    private void setupInventories() {
        final Collection<Kit> kits = this.plugin.getKitManager().getKits();
        for (final Kit kit : kits) {
            if (kit.isEnabled()) {
                this.unrankedInventory.addItem(new InventoryUI.AbstractClickableItem(kit.getIcon()) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        final Player player = (Player)event.getWhoClicked();
                        InventoryManager.this.addToQueue(player, InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()), kit, InventoryManager.this.plugin.getPartyManager().getParty(player.getUniqueId()), QueueType.UNRANKED);
                    }
                });
                if (kit.isRanked()) {
                    this.rankedInventory.addItem(new InventoryUI.AbstractClickableItem(kit.getIcon()) {
                        @Override
                        public void onClick(final InventoryClickEvent event) {
                            final Player player = (Player)event.getWhoClicked();
                            InventoryManager.this.addToQueue(player, InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()), kit, InventoryManager.this.plugin.getPartyManager().getParty(player.getUniqueId()), QueueType.RANKED);
                        }
                    });
                }
                this.editorInventory.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(kit.getIcon().getType(), ChatColor.GREEN + "§l"+ kit.getName(), 1, kit.getIcon().getDurability())) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        final Player player = (Player)event.getWhoClicked();
                        if (kit.getKitEditContents()[0] == null) {
                            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §fEste kit no es editable, prueba con otro.");
                            player.closeInventory();
                            return;
                        }
                        InventoryManager.this.plugin.getEditorManager().addEditor(player, kit);
                        InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()).setPlayerState(PlayerState.EDITING);
                    }
                });
                this.partyInventory.setItem(50, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.SLIME_BALL, ChatColor.GREEN + "§fJugar a §3§lPeleas divididas")) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        final Player player = (Player)event.getWhoClicked();
                        player.closeInventory();
                        player.openInventory(InventoryManager.this.getPartySplitInventory().getCurrentPage());
                    }
                });
                this.partyInventory.setItem(48, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.FISHING_ROD, ChatColor.AQUA + "§fJugar a §3§lParty FFA")) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        final Player player = (Player)event.getWhoClicked();
                        player.closeInventory();
                        player.openInventory(InventoryManager.this.getPartyFFAInventory().getCurrentPage());
                    }
                });
                this.partyInventory.setItem(49, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.BOOK, ChatColor.AQUA + "§fEditar tus §3§lkits")) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        final Player player = (Player) event.getWhoClicked();
                        final PlayerData playerData = InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
                        player.openInventory(InventoryManager.this.getEditorInventory().getCurrentPage());
                    }
                });
                this.duelInventory.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(kit.getIcon().getType(), ChatColor.GREEN + "§l"+ kit.getName(), 1, kit.getIcon().getDurability())) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handleDuelClick((Player)event.getWhoClicked(), kit);
                    }
                });
                this.partySplitInventory.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(kit.getIcon().getType(), ChatColor.GREEN + "§l"+ kit.getName(), 1, kit.getIcon().getDurability())) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handlePartySplitClick((Player)event.getWhoClicked(), kit);
                    }
                });
                this.partyFFAInventory.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(kit.getIcon().getType(), ChatColor.GREEN + "§l"+ kit.getName(), 1, kit.getIcon().getDurability())) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handleFFAClick((Player)event.getWhoClicked(), kit);
                    }
                });
            }
        }
        for (final Kit kit : this.plugin.getKitManager().getKits()) {
            final InventoryUI dlInventory = new InventoryUI("Seleccionar Arena", true, 4);
            final InventoryUI pSplitInventory = new InventoryUI("Seleccionar Arena", true, 4);
            final InventoryUI pFFAInventory = new InventoryUI("Seleccionar Arena", true,  4);
            for (final Arena arena : this.plugin.getArenaManager().getArenas().values()) {
                if (!arena.isEnabled()) {
                    continue;
                }
                if (kit.getExcludedArenas().contains(arena.getName())) {
                    continue;
                }
                if (kit.getArenaWhiteList().size() > 0 && !kit.getArenaWhiteList().contains(arena.getName())) {
                    continue;
                }
                final ItemStack book = ItemUtil.createItem(Material.BOOK, ChatColor.DARK_AQUA + arena.getName());
                dlInventory.addItem(new InventoryUI.AbstractClickableItem(book) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handleDuelMapClick((Player)event.getWhoClicked(), arena, kit);
                    }
                });
                pSplitInventory.addItem(new InventoryUI.AbstractClickableItem(book) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handlePartySplitMapClick((Player)event.getWhoClicked(), arena, kit);
                    }
                });
                pFFAInventory.addItem(new InventoryUI.AbstractClickableItem(book) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handlePartyFFAMapClick((Player)event.getWhoClicked(), arena, kit);
                    }
                });
            }
            this.duelMapInventories.put(kit.getName(), dlInventory);
            this.partySplitMapInventories.put(kit.getName(), pSplitInventory);
            this.partyFFAMapInventories.put(kit.getName(), pFFAInventory);
        }
    }
    
    private void updateInventories() {
        for (int i = 0; i < 18; ++i) {
            final InventoryUI.ClickableItem unrankedItem = this.unrankedInventory.getItem(i);
            if (unrankedItem != null) {
                unrankedItem.setItemStack(this.updateQueueLore(unrankedItem.getItemStack(), QueueType.UNRANKED));
                this.unrankedInventory.setItem(i, unrankedItem);
            }
            final InventoryUI.ClickableItem rankedItem = this.rankedInventory.getItem(i);
            if (rankedItem != null) {
                rankedItem.setItemStack(this.updateQueueLore(rankedItem.getItemStack(), QueueType.RANKED));
                this.rankedInventory.setItem(i, rankedItem);
            }
        }
    }
    
    private ItemStack updateQueueLore(final ItemStack itemStack, final QueueType type) {
        if (itemStack == null) {
            return null;
        }
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            final String ladder = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName());
            final int queueSize = this.plugin.getQueueManager().getQueueSize(ladder, type);
            final int inGameSize = this.plugin.getMatchManager().getFighters(ladder, type);
            return ItemUtil.reloreItem(itemStack, "", ChatColor.DARK_AQUA + "§2● §fEn Juego: " + ChatColor.DARK_AQUA + inGameSize, ChatColor.WHITE + "§2● §fEn Espera: " + ChatColor.DARK_AQUA + queueSize,"" ,ChatColor.AQUA + "Click para jugar.");
        }
        return null;
    }
    
    private void addToQueue(final Player player, final PlayerData playerData, final Kit kit, final Party party, final QueueType queueType) {
        if (kit != null) {
            if (party == null) {
                this.plugin.getQueueManager().addPlayerToQueue(player, playerData, kit.getName(), queueType);
            }
        }
    }
    
    public void addSnapshot(final InventorySnapshot snapshot) {
        this.snapshots.put(snapshot.getSnapshotId(), snapshot);
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> this.removeSnapshot(snapshot.getSnapshotId()), 600L);
    }
    
    public void removeSnapshot(final UUID snapshotId) {
        final InventorySnapshot snapshot = this.snapshots.get(snapshotId);
        if (snapshot != null) {
            this.snapshots.remove(snapshotId);
        }
    }
    
    public InventorySnapshot getSnapshot(final UUID snapshotId) {
        return this.snapshots.get(snapshotId);
    }
    
    public void addParty(final Player player) {
            final ItemStack skull = ItemUtil.createItem(Material.SKULL_ITEM, ChatColor.DARK_AQUA + "§fParty de§3 " + player.getName() + "§f: §f(§3" + ChatColor.GREEN + "1" + ChatColor.GOLD + "§f)");
            this.partyInventory.addPartyItem(new InventoryUI.AbstractClickableItem(skull) {
                @Override
                public void onClick(final InventoryClickEvent inventoryClickEvent) {
                    player.closeInventory();
                    if (inventoryClickEvent.getWhoClicked() instanceof Player) {
                        final Player sender = (Player) inventoryClickEvent.getWhoClicked();
                        sender.performCommand("duel " + player.getName());
                    }
                }
            });
        }
    
    public void updateParty(final Party party) {
        final Player player = this.plugin.getServer().getPlayer(party.getLeader());
        for (int i = 0; i < 36; ++i) {
            final InventoryUI.ClickableItem item = this.partyInventory.getItem(i);
            if (item != null) {
                final ItemStack stack = item.getItemStack();
                if (stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().contains(player.getName())) {
                    final List<String> lores = new ArrayList<>();
                    party.members().forEach(member -> lores.add(ChatColor.RED + "§2● §f" + member.getName()));
                    ItemUtil.reloreItem(stack, (String[])lores.toArray(new String[0]));
                    ItemUtil.renameItem(stack, ChatColor.DARK_AQUA +"§fParty de§3 " + player.getName() + "§f: §f(§3" + ChatColor.GREEN + party.getMembers().size() + ChatColor.GOLD + "§f)");
                    item.setItemStack(stack);
                    break;
                }
            }
        }
    }
    
    public void removeParty(final Party party) {
        final Player player = this.plugin.getServer().getPlayer(party.getLeader());
        for (int i = 0; i < 36; ++i) {
            final InventoryUI.ClickableItem item = this.partyInventory.getItem(i);
            if (item != null) {
                final ItemStack stack = item.getItemStack();
                if (stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().contains(player.getName())) {
                    this.partyInventory.removeItem(i);
                    break;
                }
            }
        }
    }
    
    public void addEditingKitInventory(final Player player, final Kit kit) {
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        final Map<Integer, PlayerKit> kitMap = playerData.getPlayerKits(kit.getName());
        final InventoryUI inventory = new InventoryUI("Gestionar kit", true, 4);
        for (int i = 1; i <= 7; ++i) {
            final ItemStack save = ItemUtil.createItem(Material.CHEST, ChatColor.WHITE + "Guardar kit " + ChatColor.GREEN + kit.getName() + " #" + i);
            final ItemStack load = ItemUtil.createItem(Material.BOOK, ChatColor.WHITE + "Importar kit " + ChatColor.GREEN + kit.getName() + " #" + i);
            final ItemStack rename = ItemUtil.createItem(Material.NAME_TAG, ChatColor.WHITE + "Renombrar kit " + ChatColor.GREEN + kit.getName() + " #" + i);
            final ItemStack delete = ItemUtil.createItem(Material.FLINT, ChatColor.WHITE + "Borrar kit " + ChatColor.GREEN + kit.getName() + " #" + i);
            inventory.setItem(i, new InventoryUI.AbstractClickableItem(save) {
                @Override
                public void onClick(final InventoryClickEvent event) {
                    final int kitIndex = event.getSlot();
                    InventoryManager.this.handleSavingKit(player, playerData, kit, kitMap, kitIndex);
                    inventory.setItem(kitIndex + 1, 2, new InventoryUI.AbstractClickableItem(load) {
                        @Override
                        public void onClick(final InventoryClickEvent event) {
                            InventoryManager.this.handleLoadKit(player, kitIndex, kitMap);
                        }
                    });
                    inventory.setItem(kitIndex + 1, 3, new InventoryUI.AbstractClickableItem(rename) {
                        @Override
                        public void onClick(final InventoryClickEvent event) {
                            InventoryManager.this.handleRenamingKit(player, kitIndex, kitMap);
                        }
                    });
                    inventory.setItem(kitIndex + 1, 4, new InventoryUI.AbstractClickableItem(delete) {
                        @Override
                        public void onClick(final InventoryClickEvent event) {
                            InventoryManager.this.handleDeleteKit(player, kitIndex, kitMap, inventory);
                        }
                    });
                }
            });
            final int kitIndex = i;
            if (kitMap != null && kitMap.containsKey(kitIndex)) {
                inventory.setItem(kitIndex + 1, 2, new InventoryUI.AbstractClickableItem(load) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handleLoadKit(player, kitIndex, kitMap);
                    }
                });
                inventory.setItem(kitIndex + 1, 3, new InventoryUI.AbstractClickableItem(rename) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handleRenamingKit(player, kitIndex, kitMap);
                    }
                });
                inventory.setItem(kitIndex + 1, 4, new InventoryUI.AbstractClickableItem(delete) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handleDeleteKit(player, kitIndex, kitMap, inventory);
                    }
                });
            }
        }
        this.editorInventories.put(player.getUniqueId(), inventory);
    }
    
    public void removeEditingKitInventory(final UUID uuid) {
        final InventoryUI inventoryUI = this.editorInventories.get(uuid);
        if (inventoryUI != null) {
            this.editorInventories.remove(uuid);
        }
    }
    
    public InventoryUI getEditingKitInventory(final UUID uuid) {
        return this.editorInventories.get(uuid);
    }
    
    private void handleSavingKit(final Player player, final PlayerData playerData, final Kit kit, final Map<Integer, PlayerKit> kitMap, final int kitIndex) {
        if (kitMap != null && kitMap.containsKey(kitIndex)) {
            kitMap.get(kitIndex).setContents(player.getInventory().getContents().clone());
            player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §fKit guardado con éxito §3#" + ChatColor.DARK_AQUA + kitIndex + ChatColor.WHITE + ".");
            return;
        }
        final PlayerKit playerKit = new PlayerKit(kit.getName(), kitIndex, player.getInventory().getContents().clone(), kit.getName() + " Kit " + kitIndex);
        playerData.addPlayerKit(kitIndex, playerKit);
        player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §fKit guardado con éxito §3#" + ChatColor.DARK_AQUA + kitIndex + ChatColor.WHITE + ".");
    }
    
    private void handleLoadKit(final Player player, final int kitIndex, final Map<Integer, PlayerKit> kitMap) {
        if (kitMap != null && kitMap.containsKey(kitIndex)) {
            final ItemStack[] contents = kitMap.get(kitIndex).getContents();
            ItemStack[] array;
            for (int length = (array = contents).length, i = 0; i < length; ++i) {
                final ItemStack itemStack = array[i];
                if (itemStack != null && itemStack.getAmount() <= 0) {
                    itemStack.setAmount(1);
                }
            }
            player.getInventory().setContents(contents);
            player.updateInventory();
        }
    }
    
    private void handleRenamingKit(final Player player, final int kitIndex, final Map<Integer, PlayerKit> kitMap) {
        if (kitMap != null && kitMap.containsKey(kitIndex)) {
            this.plugin.getEditorManager().addRenamingKit(player.getUniqueId(), kitMap.get(kitIndex));
            player.closeInventory();
            player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §aIngrese el nombre que deseas que tenga este kit (puedes ingresar los colores del chat).");
        }
    }
    
    private void handleDeleteKit(final Player player, final int kitIndex, final Map<Integer, PlayerKit> kitMap, final InventoryUI inventory) {
        if (kitMap != null && kitMap.containsKey(kitIndex)) {
            this.plugin.getEditorManager().removeRenamingKit(player.getUniqueId());
            kitMap.remove(kitIndex);
            player.sendMessage(ChatColor.GREEN + "§3§lArenaPvP §8» §fKit guardado con éxito §3" + kitIndex + ChatColor.WHITE + ".");
            inventory.setItem(kitIndex + 1, 2, null);
            inventory.setItem(kitIndex + 1, 3, null);
            inventory.setItem(kitIndex + 1, 4, null);
        }
    }
    
    private void handleDuelClick(final Player player, final Kit kit) {
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        final Player selected = this.plugin.getServer().getPlayer(playerData.getDuelSelecting());
        if (selected == null) {
            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, playerData.getDuelSelecting()));
            return;
        }
        final PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(selected.getUniqueId());
        if (targetData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cEse jugador está en partida actualmente.");
            return;
        }
        final Party targetParty = this.plugin.getPartyManager().getParty(selected.getUniqueId());
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        final boolean partyDuel = party != null;
        if (partyDuel && targetParty == null) {
            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cEse jugador no está en ningúna party.");
            return;
        }
        player.closeInventory();
        player.openInventory(this.duelMapInventories.get(kit.getName()).getCurrentPage());
    }
    
    private void handlePartySplitClick(final Player player, final Kit kit) {
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party == null || kit == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            return;
        }
        player.closeInventory();
        if (party.getMembers().size() < 2) {
            player.sendMessage(InventoryManager.MORE_PLAYERS);
        }
        else {
            player.closeInventory();
            player.openInventory(this.partySplitMapInventories.get(kit.getName()).getCurrentPage());
        }
    }
    
    private void handleFFAClick(final Player player, final Kit kit) {
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party == null || kit == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            return;
        }
        player.closeInventory();
        if (party.getMembers().size() < 2) {
            player.sendMessage(InventoryManager.MORE_PLAYERS);
        }
        else {
            player.closeInventory();
            player.openInventory(this.partyFFAMapInventories.get(kit.getName()).getCurrentPage());
        }
    }
    
    private void handleDuelMapClick(final Player player, final Arena arena, final Kit kit) {
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        final Player selected = this.plugin.getServer().getPlayer(playerData.getDuelSelecting());
        if (selected == null) {
            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, playerData.getDuelSelecting()));
            return;
        }
        final PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(selected.getUniqueId());
        if (targetData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cEse jugador está en partida actualmente.");
            return;
        }
        final Party targetParty = this.plugin.getPartyManager().getParty(selected.getUniqueId());
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        final boolean partyDuel = party != null;
        if (partyDuel && targetParty == null) {
            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cEse jugador no está en ningúna party.");
            return;
        }
        if (this.plugin.getMatchManager().getMatchRequest(player.getUniqueId(), selected.getUniqueId()) != null) {
            player.sendMessage(ChatColor.RED + "§3§lArenaPvP §8» §cYa has enviado una petición de duelo a este jugador, por favor espera.");
            return;
        }
        this.sendDuel(player, selected, kit, partyDuel, party, targetParty, arena);
    }
    
    private void handleRedroverMapClick(final Player player, final Arena arena, final Kit kit) {
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            return;
        }
        player.closeInventory();
        if (party.getMembers().size() < 4) {
            player.sendMessage(InventoryManager.MORE_PLAYERS);
        }
        else {
            this.createRedroverMatch(party, arena, kit);
        }
    }
    
    private void handlePartyFFAMapClick(final Player player, final Arena arena, final Kit kit) {
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            return;
        }
        player.closeInventory();
        if (party.getMembers().size() < 2) {
            player.sendMessage(InventoryManager.MORE_PLAYERS);
        }
        else {
            this.createFFAMatch(party, arena, kit);
        }
    }
    
    private void handlePartySplitMapClick(final Player player, final Arena arena, final Kit kit) {
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            return;
        }
        player.closeInventory();
        if (party.getMembers().size() < 2) {
            player.sendMessage(InventoryManager.MORE_PLAYERS);
        }
        else {
            this.createPartySplitMatch(party, arena, kit);
        }
    }
    
    private void sendDuel(final Player player, final Player selected, final Kit kit, final boolean partyDuel, final Party party, final Party targetParty, final Arena arena) {
        this.plugin.getMatchManager().createMatchRequest(player, selected, arena, kit.getName(), partyDuel);
        player.closeInventory();
        final String requestGetString ="§3§lArenaPvP §8» §3" + (partyDuel ? (ChatColor.YELLOW + "§3La party de "): "") + "§3" + player.getName() + (partyDuel ? (" §8(§b" + party.getMembers().size() + "§8)§3") : "") + ChatColor.WHITE + " ha solicitado un duelo con el kit: " + ChatColor.GREEN + ChatColor.BOLD + kit.getName() + ChatColor.WHITE + ". " + ChatColor.AQUA + "[Click para aceptar]";
        final String requestSendString = ChatColor.YELLOW + "§3§lArenaPvP §8» §fHa sido enviada la solicitud de duelo a " + ChatColor.DARK_AQUA + (partyDuel ? (ChatColor.YELLOW + "§3la party de ") : "") + "§3" + selected.getName() + (partyDuel ? (" §8(§b" + party.getMembers().size() + "§8)§3") : "") + ChatColor.WHITE + " con el kit: " + ChatColor.GREEN + ChatColor.BOLD + kit.getName() + ChatColor.WHITE + ".";
        final Clickable requestMessage = new Clickable(requestGetString, ChatColor.WHITE + "Click para aceptar el duelo.", "/accept " + player.getName() + " " + kit.getName());
        if (partyDuel) {
            targetParty.members().forEach(requestMessage::sendToPlayer);
            party.broadcast(requestSendString);
        }
        else {
            requestMessage.sendToPlayer(selected);
            player.sendMessage(requestSendString);
        }
    }
    
    private void createPartySplitMatch(final Party party, final Arena arena, final Kit kit) {
        final MatchTeam[] teams = party.split();
        final Match match = new Match(arena, kit, QueueType.UNRANKED, teams);
        final Player leaderA = this.plugin.getServer().getPlayer(teams[0].getLeader());
        final Player leaderB = this.plugin.getServer().getPlayer(teams[1].getLeader());
        match.broadcast(ChatColor.YELLOW + "§3§lArenaPvP §8» §fComienza una partida de §3§lPeleas divididas §fcon el kit §a§l" + ChatColor.DARK_AQUA + kit.getName() + "§f.");
        this.plugin.getMatchManager().createMatch(match);
    }
    
    private void createFFAMatch(final Party party, final Arena arena, final Kit kit) {
        final MatchTeam team = new MatchTeam(party.getLeader(), Lists.newArrayList((Iterable)party.getMembers()), 0);
        final Match match = new Match(arena, kit, QueueType.UNRANKED, team);
        match.broadcast(ChatColor.YELLOW + "§3§lArenaPvP §8» §fComienza una partida de §3§lParty FFA §fcon el kit §a§l" + ChatColor.DARK_AQUA + kit.getName() + "§f.");
        this.plugin.getMatchManager().createMatch(match);
    }
    
    private void createRedroverMatch(final Party party, final Arena arena, final Kit kit) {
        final MatchTeam[] teams = party.split();
        final Match match = new Match(arena, kit, QueueType.UNRANKED, true, teams);
        final Player leaderA = this.plugin.getServer().getPlayer(teams[0].getLeader());
        final Player leaderB = this.plugin.getServer().getPlayer(teams[1].getLeader());
        match.broadcast(ChatColor.YELLOW + "§3§lArenaPvP §8» §fComienza una partida de §3§lRedrover §fcon el kit §a§l" + ChatColor.DARK_AQUA + kit.getName() + "§f.");
        this.plugin.getMatchManager().createMatch(match);
    }
    
    public InventoryUI getUnrankedInventory() {
        return this.unrankedInventory;
    }
    
    public InventoryUI getRankedInventory() {
        return this.rankedInventory;
    }
    
    public InventoryUI getEditorInventory() {
        return this.editorInventory;
    }
    
    public InventoryUI getDuelInventory() {
        return this.duelInventory;
    }
    
    public InventoryUI getPartySplitInventory() {
        return this.partySplitInventory;
    }
    
    public InventoryUI getPartyFFAInventory() {
        return this.partyFFAInventory;
    }

    
    public InventoryUI getPartyInventory() {
        return this.partyInventory;
    }
}

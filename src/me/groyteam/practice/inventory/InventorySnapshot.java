package me.groyteam.practice.inventory;

import me.groyteam.practice.Practice;
import me.groyteam.practice.match.Match;
import me.groyteam.practice.player.PlayerData;
import me.groyteam.practice.util.ItemUtil;
import me.groyteam.practice.util.MathUtil;
import me.groyteam.practice.util.StringUtil;
import me.groyteam.practice.util.inventory.InventoryUI;
import org.bukkit.enchantments.Enchantment;
import org.json.simple.JSONObject;
import java.util.List;

import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Function;
import java.util.Objects;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffect;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

public class InventorySnapshot
{
    private final InventoryUI inventoryUI;
    private final ItemStack[] originalInventory;
    private final ItemStack[] originalArmor;
    private final UUID snapshotId;
    
    public InventorySnapshot(final Player player, final Match match) {
        this.snapshotId = UUID.randomUUID();
        final ItemStack[] contents = player.getInventory().getContents();
        final ItemStack[] armor = player.getInventory().getArmorContents();
        this.originalInventory = contents;
        this.originalArmor = armor;
        final PlayerData playerData = Practice.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
        final double health = player.getHealth();
        final double food = player.getFoodLevel();
        final List<String> potionEffectStrings = new ArrayList<>();
        for (final PotionEffect potionEffect : player.getActivePotionEffects()) {
            final String romanNumeral = MathUtil.convertToRomanNumeral(potionEffect.getAmplifier() + 1);
            final String effectName = StringUtil.toNiceString(potionEffect.getType().getName().toLowerCase());
            final String duration = MathUtil.convertTicksToMinutes(potionEffect.getDuration());
            potionEffectStrings.add(String.valueOf(ChatColor.YELLOW.toString()) + ChatColor.BOLD + "§2● " + ChatColor.WHITE + effectName + " " + romanNumeral+":" + ChatColor.AQUA + " (" + duration + ")" + "§f.");
        }
        this.inventoryUI = new InventoryUI("Inventario de" + player.getName() + "", true, 6);
        for (int i = 0; i < 9; ++i) {
            this.inventoryUI.setItem(i + 27, new InventoryUI.EmptyClickableItem(contents[i]));
            this.inventoryUI.setItem(i + 18, new InventoryUI.EmptyClickableItem(contents[i + 27]));
            this.inventoryUI.setItem(i + 9, new InventoryUI.EmptyClickableItem(contents[i + 18]));
            this.inventoryUI.setItem(i, new InventoryUI.EmptyClickableItem(contents[i + 9]));
        }
        boolean potionMatch = false;
        boolean soupMatch = false;
        ItemStack[] contents2;
        for (int length = (contents2 = match.getKit().getContents()).length, k = 0; k < length; ++k) {
            final ItemStack item = contents2[k];
            if (item != null) {
                if (item.getType() == Material.MUSHROOM_SOUP) {
                    soupMatch = true;
                    break;
                }
                if (item.getType() == Material.POTION && item.getDurability() == 16421) {
                    potionMatch = true;
                    break;
                }
            }
        }
        if (potionMatch) {
            final int potCount = (int)Arrays.stream(contents).filter(Objects::nonNull).map(ItemStack::getDurability).filter(d -> d == 16421).count();
            this.inventoryUI.setItem(53, new InventoryUI.EmptyClickableItem(ItemUtil.reloreItem(ItemUtil.createItem(Material.POTION, String.valueOf(ChatColor.DARK_AQUA.toString()) + ChatColor.BOLD + "Pociones:"), String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "§2● §b" + ChatColor.WHITE + "Pociones de Curación: " + ChatColor.AQUA + potCount + " pocion" + ((potCount > 1) ? "es." : "."), String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "§2● §b" + ChatColor.WHITE + "Pociones de perdidas: " + ChatColor.AQUA + playerData.getMissedPots() + " pocion" + ((playerData.getMissedPots() > 1) ? "es." : "."))));
        }
        else if (soupMatch) {
            final int soupCount = (int)Arrays.stream(contents).filter(Objects::nonNull).<Object>map((Function<? super ItemStack, ?>)ItemStack::getType).filter(d -> d == Material.MUSHROOM_SOUP).count();
            this.inventoryUI.setItem(47, new InventoryUI.EmptyClickableItem(ItemUtil.createItem(Material.MUSHROOM_SOUP, String.valueOf(ChatColor.DARK_AQUA.toString()) + ChatColor.BOLD + "Sopas restantes: " + ChatColor.WHITE + soupCount, soupCount, (short)16421)));
        }
        final double roundedHealth = Math.round(health / 2.0 * 2.0) / 2.0;
        this.inventoryUI.setItem(46, new InventoryUI.EmptyClickableItem(ItemUtil.createItem(Material.APPLE, String.valueOf(ChatColor.DARK_AQUA.toString()) + ChatColor.BOLD  + "Vida: " + roundedHealth, (int)Math.round(health / 2.0))));
        final double roundedFood = Math.round(health / 2.0 * 2.0) / 2.0;
        this.inventoryUI.setItem(45, new InventoryUI.EmptyClickableItem(ItemUtil.createItem(Material.COOKED_BEEF, String.valueOf(ChatColor.DARK_AQUA.toString()) + ChatColor.BOLD + roundedFood + " Hambre.", (int)Math.round(food / 2.0))));
        this.inventoryUI.setItem(52, new InventoryUI.EmptyClickableItem(ItemUtil.reloreItem(ItemUtil.createItem(Material.BREWING_STAND_ITEM, String.valueOf(ChatColor.DARK_AQUA.toString()) + ChatColor.BOLD + "Efectos de pociones: ", potionEffectStrings.size()), (String[])potionEffectStrings.toArray(new String[0]))));
        this.inventoryUI.setItem(49, new InventoryUI.EmptyClickableItem(ItemUtil.reloreItem(ItemUtil.createItem(Material.CAKE, String.valueOf(ChatColor.DARK_AQUA.toString()) + ChatColor.BOLD + "Estadisticas"), String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "§2● §b" + ChatColor.WHITE + "El mejor combo: " + ChatColor.AQUA + playerData.getLongestCombo() + " Golpe" + ((playerData.getLongestCombo() > 1) ? "s." : "."), String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "§2● §b" + ChatColor.WHITE + "Golpes Totales: " + ChatColor.AQUA + playerData.getHits() + " Golpe" + ((playerData.getHits() > 1) ? "s." : "."))));
        if (!match.isParty()) {
            this.inventoryUI.setItem(48, new InventoryUI.AbstractClickableItem(ItemUtil.reloreItem(ItemUtil.createItem(Material.EMPTY_MAP, ChatColor.DARK_AQUA.toString() + "Ver el otro inventario"), new String[0])) {
                @Override
                public void onClick(final InventoryClickEvent inventoryClickEvent) {
                    final Player clicker = (Player)inventoryClickEvent.getWhoClicked();
                    if (Practice.getInstance().getMatchManager().isRematching(player.getUniqueId())) {
                        clicker.closeInventory();
                        Practice.getInstance().getServer().dispatchCommand(clicker, "inventory " + Practice.getInstance().getMatchManager().getRematcherInventory(player.getUniqueId()));
                    }
                }
            });
        }
        for (int j = 36; j < 40; ++j) {
            this.inventoryUI.setItem(j, new InventoryUI.EmptyClickableItem(armor[39 - j]));
        }
    }
    
    public JSONObject toJson() {
        final JSONObject object = new JSONObject();
        final JSONObject inventoryObject = new JSONObject();
        for (int i = 0; i < this.originalInventory.length; ++i) {
            inventoryObject.put(i, this.encodeItem(this.originalInventory[i]));
        }
        object.put("inventory", inventoryObject);
        final JSONObject armourObject = new JSONObject();
        for (int j = 0; j < this.originalArmor.length; ++j) {
            armourObject.put(j, this.encodeItem(this.originalArmor[j]));
        }
        object.put("armour", armourObject);
        return object;
    }
    
    private JSONObject encodeItem(final ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return null;
        }
        final JSONObject object = new JSONObject();
        object.put("material", itemStack.getType().name());
        object.put("durability", itemStack.getDurability());
        object.put("amount", itemStack.getAmount());
        final JSONObject enchants = new JSONObject();
        for (final Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            enchants.put(enchantment.getName(), itemStack.getEnchantments().get(enchantment));
        }
        object.put("enchants", enchants);
        return object;
    }
    
    public UUID getSnapshotId() {
        return this.snapshotId;
    }
    
    public InventoryUI getInventoryUI() {
        return this.inventoryUI;
    }
    
    public ItemStack[] getOriginalInventory() {
        return this.originalInventory;
    }
    
    public ItemStack[] getOriginalArmor() {
        return this.originalArmor;
    }
}

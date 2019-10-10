package net.latinplay.practice.cache;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.SkullMeta;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.ArrayList;
import org.bukkit.potion.PotionType;
import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import java.util.Map;
import java.util.List;
import org.bukkit.Material;

public class ItemBuilder2
{
    private Material mat;
    private static ItemBuilder2 instance;
    private int amount;
    private short data;
    private String title;
    private List<String> lore;
    private Map<Enchantment, Integer> enchants;
    private Color color;
    private PotionType potion;
    private boolean potionUpgraded;
    private boolean potionExtended;
    private boolean potionSplash;
    private boolean hideFlags;
    private boolean glow;
    private String skull;
    
    public static ItemBuilder2 getInstance() {
        return instance;
    }
    
    public ItemBuilder2(Material material) {
        this(material, 1);
    }
    
    public ItemBuilder2(Material material, int n) {
        this(material, n, (short)0);
    }
    
    public ItemBuilder2(Material material, short n) {
        this(material, 1, n);
    }
    
    public ItemBuilder2(Material mat, int amount, short data) {
        this.title = null;
        this.lore = new ArrayList<>();
        this.enchants = new HashMap<>();
        this.mat = mat;
        if (this.mat == null) {
            this.mat = Material.BEDROCK;
        }
        this.amount = amount;
        this.data = data;
        this.hideFlags = false;
    }
    
    public ItemBuilder2(ItemStack itemStack) {
        this.title = null;
        this.lore = new ArrayList<>();
        this.enchants = new HashMap<>();
        this.mat = itemStack.getType();
        this.amount = itemStack.getAmount();
        this.data = itemStack.getDurability();
        ItemMeta itemMeta = itemStack.getItemMeta();
        this.title = itemMeta.getDisplayName();
        this.lore = (List<String>)itemMeta.getLore();
        if (itemMeta instanceof LeatherArmorMeta) {
            this.color = ((LeatherArmorMeta)itemMeta).getColor();
        }
        if (itemMeta instanceof PotionMeta) {
                Potion fromItemStack = Potion.fromItemStack(itemStack);
                this.potion = fromItemStack.getType();
                this.potionUpgraded = (fromItemStack.getLevel() > 1);
                this.potionSplash = fromItemStack.isSplash();
                this.potionExtended = fromItemStack.hasExtendedDuration();
        }
        this.enchants.putAll(itemStack.getEnchantments());
    }
    
    public ItemBuilder2 setType(Material mat) {
        this.mat = mat;
        return this;
    }
    
    public ItemBuilder2 setData(short data) {
        this.data = data;
        return this;
    }
    
    public ItemBuilder2 setTitle(String s) {
        this.title = ChatColor.translateAlternateColorCodes('&', s);
        return this;
    }
    
    public ItemBuilder2 addLore(String s) {
        this.lore.add(ChatColor.translateAlternateColorCodes('&', s));
        return this;
    }
    
    public ItemBuilder2 addLore(List<String> list) {
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            this.lore.add(ChatColor.translateAlternateColorCodes('&', (String)iterator.next()));
        }
        return this;
    }
    
    public ItemBuilder2 removeLastLoreLine() {
        this.lore.remove(this.lore.size() - 1);
        return this;
    }
    
    public ItemBuilder2 setLore(List<String> list) {
        this.lore.clear();
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            this.lore.add(ChatColor.translateAlternateColorCodes('&', (String)iterator.next()));
        }
        return this;
    }
    
    public ItemBuilder2 addEnchantment(Enchantment enchantment, int n) {
        if (this.enchants.containsKey(enchantment)) {
            this.enchants.remove(enchantment);
        }
        this.enchants.put(enchantment, n);
        return this;
    }
    
    public ItemBuilder2 setColor(Color color) {
        if (this.mat.name().contains("LEATHER_")) {
            this.color = color;
        }
        return this;
    }
    
    public ItemBuilder2 setHideFlags(boolean hideFlags) {
        this.hideFlags = hideFlags;
        return this;
    }
    
    public boolean isHideFlags() {
        return this.hideFlags;
    }
    
    public boolean isGlow() {
        return this.glow;
    }
    
    public void setGlow(boolean glow) {
        this.glow = glow;
    }
    
    public ItemBuilder2 setPotion(String s, Material mat, boolean potionUpgraded, boolean potionExtended) {
        this.mat = mat;
        try {
            if (mat == Material.POTION) {
                this.potionSplash = true;
            }
        }
        catch (NoSuchFieldError noSuchFieldError) {
            this.mat = Material.POTION;
            this.potionSplash = true;
        }
        this.potion = PotionType.valueOf(s);
        this.potionUpgraded = potionUpgraded;
        this.potionExtended = potionExtended;
        return this;
    }
    
    public ItemBuilder2 setAmount(int amount) {
        this.amount = amount;
        return this;
    }
    
    public ItemBuilder2 setSkullOwner(String skull) {
        if (this.mat != Material.SKULL_ITEM) {
            this.mat = Material.SKULL_ITEM;
            this.data = 3;
        }
        this.skull = skull;
        return this;
    }
    
    public ItemStack build() {
        if (this.mat == null) {
            this.mat = Material.AIR;
        }
        ItemStack itemStack = new ItemStack(this.mat, this.amount, this.data);
        Object itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof LeatherArmorMeta && this.color != null) {
            ((LeatherArmorMeta)itemMeta).setColor(this.color);
        }
        if (itemMeta instanceof SkullMeta && this.skull != null) {
            ((SkullMeta)itemMeta).setOwner(this.skull);
        }
        if (itemMeta instanceof PotionMeta && this.potion != null) {
            new Potion(this.potion, this.potionUpgraded ? 2 : 1, this.potionSplash, this.potionExtended).apply(itemStack);
        }
        if (this.title != null) {
            ((ItemMeta)itemMeta).setDisplayName(this.title);
        }
        if (!this.lore.isEmpty()) {
            ((ItemMeta)itemMeta).setLore((List)this.lore);
        }
        if (this.hideFlags) {
            ((ItemMeta)itemMeta).addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS });
        }
        if (this.glow) {
            ((ItemMeta)itemMeta).addEnchant((Enchantment)new Glow(120), 0, true);
        }
        itemStack.setItemMeta((ItemMeta)itemMeta);
        itemStack.addUnsafeEnchantments((Map)this.enchants);
        return itemStack;
    }
    
    @Override
    public ItemBuilder2 clone() throws CloneNotSupportedException {
        ItemBuilder2 itemBuilder = new ItemBuilder2(this.mat, this.amount, this.data);
        itemBuilder.setTitle(this.title);
        itemBuilder.setLore(this.lore);
        for (Map.Entry<Enchantment, Integer> entry : this.enchants.entrySet()) {
            itemBuilder.addEnchantment(entry.getKey(), entry.getValue());
        }
        itemBuilder.setColor(this.color);
        itemBuilder.potion = this.potion;
        itemBuilder.potionExtended = this.potionExtended;
        itemBuilder.potionUpgraded = this.potionUpgraded;
        itemBuilder.potionSplash = this.potionSplash;
        return itemBuilder;
    }
    
    public Material getType() {
        return this.mat;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public List<String> getLore() {
        return this.lore;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public boolean hasEnchantment(Enchantment enchantment) {
        return this.enchants.containsKey(enchantment);
    }
    
    public int getEnchantmentLevel(Enchantment enchantment) {
        return this.enchants.get(enchantment);
    }
    
    public Map<Enchantment, Integer> getAllEnchantments() {
        return this.enchants;
    }
    
    public boolean isItem(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemStack.getType() != this.getType()) {
            return false;
        }
        if (!itemMeta.hasDisplayName() && this.getTitle() != null) {
            return false;
        }
        if (!itemMeta.getDisplayName().equals(this.getTitle())) {
            return false;
        }
        if (!itemMeta.hasLore() && !this.getLore().isEmpty()) {
            return false;
        }
        if (itemMeta.hasLore()) {
            Iterator iterator = itemMeta.getLore().iterator();
            while (iterator.hasNext()) {
                if (!this.getLore().contains(iterator.next())) {
                    return false;
                }
            }
        }
        Iterator<Enchantment> iterator2 = itemStack.getEnchantments().keySet().iterator();
        while (iterator2.hasNext()) {
            if (!this.hasEnchantment(iterator2.next())) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        String s = "" + this.mat.toString();
        if (this.data != 0) {
            s = s + ":" + this.data;
        }
        if (this.amount > 1) {
            s = s + "," + this.amount;
        }
        if (this.title != null) {
            s = s + ",name:" + this.title;
        }
        if (!this.lore.isEmpty()) {
            Iterator<String> iterator = this.lore.iterator();
            while (iterator.hasNext()) {
                s = s + ",lore:" + iterator.next();
            }
        }
        for (Map.Entry<Enchantment, Integer> entry : this.getAllEnchantments().entrySet()) {
            s = s + "," + entry.getKey().getName() + ((entry.getValue() > 1) ? (":" + entry.getValue()) : "");
        }
        if (this.color != null) {
            s = s + ",leather_color:" + this.color.getRed() + "-" + this.color.getGreen() + "-" + this.color.getBlue();
        }
        if (this.potion != null) {
            s = s + ",potion:" + this.potion.toString() + ":" + this.potionUpgraded + ":" + this.potionExtended;
        }
        if (this.glow) {
            s += ",glowing";
        }
        return s;
    }
}

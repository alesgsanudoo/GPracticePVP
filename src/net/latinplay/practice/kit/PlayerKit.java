package net.latinplay.practice.kit;

import net.latinplay.practice.Practice;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerKit
{
    private final String name;
    private final int index;
    private ItemStack[] contents;
    private String displayName;

    public void applyToPlayer(final Player player) {
        ItemStack[] content;
        for (int length = (content = this.contents).length, i = 0; i < length; ++i) {
            final ItemStack itemStack = content[i];
            if (itemStack != null && itemStack.getAmount() <= 0) {
                itemStack.setAmount(1);
            }
        }
        player.getInventory().setContents(this.contents);
        player.getInventory().setArmorContents(Practice.getInstance().getKitManager().getKit(this.name).getArmor());
        player.updateInventory();
    }

    public String getName() {
        return this.name;
    }

    public int getIndex() {
        return this.index;
    }

    public ItemStack[] getContents() {
        return this.contents;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setContents(final ItemStack[] contents) {
        this.contents = contents;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public PlayerKit(final String name, final int index, final ItemStack[] contents, final String displayName) {
        this.name = name;
        this.index = index;
        this.contents = contents;
        this.displayName = displayName;
    }
}

package Commands;

import Listeners.InventoryListener;
import Universal.Items;
import Universal.Kits;
import Universal.PlayerStats;
import Universal.Ui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class OpenConstantMenuCommand implements CommandExecutor {
    Items item = Items.getInstance();
    PlayerStats stat = PlayerStats.INSTANCE;
    Ui ui = Ui.getInstance();
    Kits k = Kits.getInstance();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player p){
            openConstantInv(p);
            InventoryListener.playerInvStatus.put(p.getName(), InventoryListener.InvStatus.CONTESTANT_MENU);
        }
        return true;
    }

    public void openConstantInv(Player p){
        Inventory inv = Bukkit.createInventory(p,9, ChatColor.RED +""+ ChatColor.BOLD + "选择配装(点击图标即可选择配装)");
        int[][]loadOuts = stat.getPlayerLoadOuts(p);
        for(int i = 0;i < loadOuts.length;i ++){
            int classID = loadOuts[i][0];
            ItemStack pClass = item.getClass(classID);
            ItemStack skill = item.getSkill(loadOuts[i][1]);
            ItemStack weapon = item.getWeapon(loadOuts[i][2]);
            ItemStack icon = new ItemStack(pClass.getType());
            ItemMeta meta = icon.getItemMeta();
            ItemMeta classMeta = pClass.getItemMeta();
            ItemMeta skillMeta = skill.getItemMeta();
            ItemMeta weaponMeta = weapon.getItemMeta();
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "选手配置" + (i + 1));
            ArrayList<String>lores = new ArrayList<>();
            lores.add(ChatColor.WHITE + "体型：" + classMeta.getDisplayName());
            lores.add(ChatColor.WHITE + "技能：" + skillMeta.getDisplayName());
            lores.add(ChatColor.WHITE + "武器：" + weaponMeta.getDisplayName());
            for(int j = 3;j < loadOuts[i].length;j ++){
                ItemStack gadget = item.getGadget(loadOuts[i][j]);
                ItemMeta gadgetMeta = gadget.getItemMeta();
                lores.add(ChatColor.WHITE + "道具" + (j - 2) + "：" + gadgetMeta.getDisplayName());
            }
            icon.setItemMeta(meta);
            k.addLore(icon,lores.toArray(new String[]{}));
            inv.setItem(i,icon);
        }
        inv.setItem(8,ui.changeLoadOut());
        p.openInventory(inv);
    }
}

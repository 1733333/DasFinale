package Commands;

import Universal.Kits;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class SummonVaultCommand implements CommandExecutor {
    Kits k = Kits.getInstance();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player p) {
            if (p.isOp()) {
                k.createVault(p.getLocation());
            }
        }else if (commandSender instanceof BlockCommandSender b) {
            try {
                World w = b.getBlock().getWorld();
                switch (strings.length) {
                    case 0:
                        Bukkit.getLogger().info(ChatColor.RED + "请输入值");
                        return true;
                    case 1:
                        Bukkit.getLogger().info(ChatColor.RED + "请输入X值");
                        return true;
                    case 2:
                        Bukkit.getLogger().info(ChatColor.RED + "请输入Y值");
                        return true;
                }
                double x = Double.parseDouble(strings[0]);
                double y = Double.parseDouble(strings[1]);
                double z = Double.parseDouble(strings[2]);
                Location summonLoc = new Location(w, x, y, z);
                k.createVault(summonLoc);
            } catch (Exception e) {
                Bukkit.getLogger().info(ChatColor.RED + "Oops,输入的值好像不是数字");
            }
        }
        return true;
    }
}

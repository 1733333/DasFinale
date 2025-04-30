package Commands;

import Universal.Kits;
import org.bukkit.*;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

public class SummonJarCommand implements CommandExecutor {
    Kits k = Kits.getInstance();
    Entity jar = null;
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(this.jar != null){
            this.jar.remove();
        }
        if(commandSender instanceof Player p){
            try {
                if (!p.isOp()) return true;
                int type = Integer.parseInt(strings[0]);
                this.jar = k.createJar(p.getLocation(),type,true);
            }catch (Exception e){
                p.sendMessage(ChatColor.RED + "Oops,输入的值好像不是数字");
            }
        }else if(commandSender instanceof BlockCommandSender b){
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
                int type = Integer.parseInt(strings[3]);
                Location summonLoc = new Location(w,x,y,z);
                this.jar = k.createJar(summonLoc,type,true);
            }catch (Exception e){
                Bukkit.getLogger().info(ChatColor.RED + "Oops,输入的值好像不是数字");
            }
        }
        return true;
    }
}

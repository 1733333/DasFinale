package Commands;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class SummonDamageTester implements CommandExecutor {
    List<Entity>dummies = new ArrayList<>();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player p){
            try {
                if (!p.isOp()) return true;
                int size = Integer.parseInt(strings[0]);
                if(size < 1){
                    size = 1;
                }else if(size > 3){
                    size = 3;
                }
                summon(p.getLocation(),size);
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
                int size = Integer.parseInt(strings[3]);
                if(size < 1){
                    size = 1;
                }else if(size > 3){
                    size = 3;
                }
                Location summonLoc = new Location(w,x,y,z);
                summon(summonLoc,size);
            }catch (Exception e){
                Bukkit.getLogger().info(ChatColor.RED + "Oops,输入的值好像不是数字");
            }
        }
        return true;
    }

    public void summon(Location location,int size) {
        World w = location.getWorld();
        Snowman test = (Snowman) w.spawnEntity(location, EntityType.SNOWMAN);
        test.setDerp(true);
        test.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(15 + size * 15);
        test.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);
        test.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
        test.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(6);
        test.setHealth(test.getMaxHealth());
        test.setCustomNameVisible(true);
        EntityEquipment equipment = test.getEquipment();
        equipment.clear();
        switch (size) {
            case 1:
                equipment.setHelmet(new ItemStack(Material.LIGHT_BLUE_CONCRETE));
                test.setCustomName(ChatColor.AQUA + "伤害测试假人");
                break;
            case 2:
                equipment.setHelmet(new ItemStack(Material.YELLOW_CONCRETE));
                test.setCustomName(ChatColor.YELLOW + "伤害测试假人");
                break;
            case 3:
                equipment.setHelmet(new ItemStack(Material.RED_CONCRETE));
                test.setCustomName(ChatColor.RED + "伤害测试假人");
                break;
        }
        equipment.setHelmetDropChance(0);
        dummies.add(test);
        if (dummies.size() > 3) {
            Entity e = dummies.get(0);
            e.remove();
            dummies.remove(0);
        }
    }
}

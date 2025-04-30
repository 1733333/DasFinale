package Commands;

import Listeners.GameListeners;
import Universal.GameStatus;
import Universal.Kits;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;

import static Listeners.GameListeners.jumpPointLocation;
import static Listeners.GameListeners.jumpPointVector;

public class SetJumpPointCommand implements CommandExecutor {
    JavaPlugin plugin;
    Kits k = Kits.getInstance();
    Location[] jumpStartPoint = new Location[]{};
    Location[] jumpEndPoint = new Location[]{};
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player p){
            if(p.isOp()){
                recordJumpPoint(p.getWorld());
                setJumpPoint(p.getWorld());
            }
        }
        return true;
    }
    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    public void recordJumpPoint(World w){
        ArrayList<Location> jumpS = new ArrayList<>();
        ArrayList<Location> jumpE = new ArrayList<>();
        Collection<ArmorStand> entities = w.getEntitiesByClass(ArmorStand.class);
        for (ArmorStand a : entities) {
            String name = a.getName();
            Location loc = a.getLocation();
            if (name.contains("滑索开始点")) {
                jumpS.add(loc);
            }
            if (name.contains("滑索结束点")) {
                jumpE.add(loc);
            }
            a.remove();
        }
        jumpStartPoint = jumpS.toArray(new Location[]{});
        jumpEndPoint = jumpE.toArray(new Location[]{});
    }
    public void setJumpPoint(World w){
        Location[]jumpS = jumpStartPoint;
        Location[]jumpE = jumpEndPoint;
        if(jumpS.length != jumpE.length || jumpS.length == 0)return;
        for(int i = 0;i < jumpS.length;i++){
            Location from = jumpS[i].clone();
            Location to = jumpE[i].clone();
            Vector fromV = from.toVector();
            Vector toV = to.toVector();
            Vector jump = toV.subtract(fromV);
            ArmorStand a = (ArmorStand) w.spawnEntity(from, EntityType.ARMOR_STAND);
            ArmorStand a1 = (ArmorStand) w.spawnEntity(to, EntityType.ARMOR_STAND);
            a.setCustomName(ChatColor.AQUA + "滑索A" + (i + 1));
            a1.setCustomName(ChatColor.AQUA + "滑索B" + (i + 1));
            a.setCustomNameVisible(true);
            a1.setCustomNameVisible(true);
            a.setInvulnerable(true);
            a1.setInvulnerable(true);
            a.setGravity(false);
            a1.setGravity(false);
            a.setInvisible(true);
            a1.setInvisible(true);
            jumpPointVector.put(a,jump);
            jumpPointVector.put(a1,jump.clone().multiply(-1));
            jumpPointLocation.put(from,to);
            jumpPointLocation.put(to,from);
            for(int j = 0;j < 2;j ++){
                Block b = w.getBlockAt(a.getLocation().add(0,j,0));
                Block b1 = w.getBlockAt(a1.getLocation().add(0,j,0));
                if(b.getType() == Material.AIR || b.getType() == Material.LIGHT ||k.isBreakable(null,b)){
                    b.setType(Material.BIRCH_FENCE);
                }
                if(b1.getType() == Material.AIR || b1.getType() == Material.LIGHT ||k.isBreakable(null,b1)){
                    b1.setType(Material.BIRCH_FENCE);
                }
            }
            BukkitRunnable particle = new BukkitRunnable() {
                @Override
                public void run() {
                    if(a.isDead()){
                        for(int j = 0;j < 2;j ++){
                            Block b = w.getBlockAt(a.getLocation().add(0,j,0));
                            Block b1 = w.getBlockAt(a1.getLocation().add(0,j,0));
                            if(b.getType() == Material.BIRCH_FENCE){
                                b.setType(Material.AIR);
                            }
                            if(b1.getType() == Material.BIRCH_FENCE){
                                b1.setType(Material.AIR);
                            }
                        }
                        this.cancel();
                        return;
                    }
                    Location pLoc = a.getEyeLocation();
                    for (int i = 0; i < jump.length(); i++) {
                        w.spawnParticle(Particle.END_ROD, pLoc, 0);
                        pLoc.add(jump.clone().normalize());
                    }
                }
            };
            particle.runTaskTimer(plugin,0L,100L);
        }
    }
}

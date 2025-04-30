package Universal;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public enum PlayerStats {
    INSTANCE;
    AchievementList achievementList = AchievementList.INSTANCE;
    HashMap<String, List<Integer>>playerAchievements = new HashMap<>();
    HashMap<String,Integer>playerTeam = new HashMap<>();
    HashMap<String,Integer>playerKills = new HashMap<>();
    HashMap<String,Integer>playerDeaths = new HashMap<>();
    HashMap<String, ArmorStand>playerStatue = new HashMap<>();
    HashMap<String, Location>playerSpawnPoint = new HashMap<>();
    HashMap<ArmorStand, String>statuePlayer = new HashMap<>();
    HashMap<String,int[]>playerCurrentLoadOut = new HashMap<>();
    HashMap<String,int[][]>playerLoadOuts = new HashMap<>();
    HashSet<String>playerCarrying = new HashSet<>();
    HashSet<String>playerGaming = new HashSet<>();
    HashSet<String>spector = new HashSet<>();
    HashSet<String>ready = new HashSet<>();
    HashMap<String,HashMap<Integer,Integer>>playerAmmo = new HashMap<>();

    public void setCarrying(Player p){
        playerCarrying.add(p.getName());
    }
    public void stopCarrying(Player p){
        playerCarrying.remove(p.getName());
    }
    public boolean isCarrying(Player p){
        return playerCarrying.contains(p.getName());
    }
    public void ready(Player p){
        ready.add(p.getName());
    }
    public void cancelReady(Player p){
        ready.remove(p.getName());
    }
    public boolean isReady(Player p){
        return ready.contains(p.getName());
    }
    public void setGaming(Player p){playerGaming.add(p.getName());}
    public void stopGaming(Player p){playerGaming.remove(p.getName());}
    public boolean isGaming(Player p){return playerGaming.contains(p.getName());}
    public void setSpector(Player p){spector.add(p.getName());}
    public void stopSpectating(Player p){spector.remove(p.getName());}
    public boolean isSpector(Player p){return spector.contains(p.getName());}
    public int[] getSelectedLoadOut(Player p){
        return playerCurrentLoadOut.getOrDefault(p.getName(),new int[]{});
    }
    public void setSelectedLoadOut(Player p,int[] loadOut){
        playerCurrentLoadOut.put(p.getName(),loadOut);
    }
    public int getClass(Player p){
        int[] loadOut = getSelectedLoadOut(p);
        if(loadOut.length > 0){
            return loadOut[0];
        }else return -1;
    }
    public void setClass(Player p,int id){
        int[] loadOut = getSelectedLoadOut(p);
        if(loadOut.length > 0){
            loadOut[0] = id;
            setSelectedLoadOut(p,loadOut);
        }
    }
    public int getSkill(Player p){
        int[] loadOut = getSelectedLoadOut(p);
        if(loadOut.length > 0){
            return loadOut[1];
        }else return -1;
    }
    public void setSkill(Player p,int id){
        int[] loadOut = getSelectedLoadOut(p);
        if(loadOut.length > 0){
            loadOut[1] = id;
            setSelectedLoadOut(p,loadOut);
        }
    }
    public int getTeam(Player p){return playerTeam.getOrDefault(p.getName(),-1);}
    public void setTeam(Player p,int team){playerTeam.put(p.getName(),team);}
    public int[][] getPlayerLoadOuts(Player p){
        return playerLoadOuts.getOrDefault(p.getName(),getDefaultLoadOut().clone());}
    public void setPlayerLoadOuts(Player p,int[][]loadOut){
        playerLoadOuts.put(p.getName(),loadOut.clone());
    }
    public int[][] getDefaultLoadOut(){
        return new int[][]{
                {0,0,0,2,5,10},
                {0,1,2,5,10,20},
                {1,2,3,7,11,14},
                {1,7,5,11,16,18},
                {2,4,6,2,12,15},
                {2,5,11,3,19,22}
        };//体型，技能，武器，道具
    }
    public boolean isDead(Player p){return playerStatue.getOrDefault(p.getName(),null) != null;}
    public void setPlayerStatue(Player p,ArmorStand a){
        playerStatue.put(p.getName(),a);
        statuePlayer.put(a,p.getName());
    }
    public ArmorStand getStatue(Player p){
        return playerStatue.getOrDefault(p.getName(),null);
    }
    public String getPlayerByStatue(ArmorStand a){
        return statuePlayer.getOrDefault(a,null);
    }
    public void removePlayerStatue(Player p){
        ArmorStand a = getStatue(p);
        if(a != null){
            a.remove();
            statuePlayer.remove(a);
        }
        playerStatue.remove(p.getName());
    }
    public Location getSpawnPoint(Player p){
        return playerSpawnPoint.getOrDefault(p.getName(),p.getLocation());
    }
    public void setPlayerSpawnPoint(Player p,Location l){playerSpawnPoint.put(p.getName(),l);}
    public void addKillCount(Player p){
        int kill = playerKills.getOrDefault(p.getName(),0);
        playerKills.put(p.getName(),kill + 1);
    }
    public int getPlayerKills(Player p){
        return playerKills.getOrDefault(p.getName(),0);
    }
    public void addDeathCount(Player p){
        int dead = playerDeaths.getOrDefault(p.getName(),0);
        playerDeaths.put(p.getName(),dead + 1);
    }
    public int getPlayerDeaths(Player p){
        return playerDeaths.getOrDefault(p.getName(),0);
    }
    public void clearKD(){
        playerKills = new HashMap<>();
        playerDeaths = new HashMap<>();
    }
    public int getItemAmmo(Player p,int item){
        HashMap<Integer,Integer>itemAmmo = playerAmmo.getOrDefault(p.getName(), new HashMap<>());
        return itemAmmo.getOrDefault(item,-1);
    }
    public void setItemAmmo(Player p,int item,int ammo){
        HashMap<Integer,Integer>itemAmmo = playerAmmo.getOrDefault(p.getName(), new HashMap<>());
        itemAmmo.put(item,ammo);
        playerAmmo.put(p.getName(),itemAmmo);
    }
    public void readPlayerAchievements(Player p,List<Integer>achievements){
        playerAchievements.put(p.getName(),achievements);
    }
    public boolean hasAchievement(Player p, int a){
        List<Integer>achievements = playerAchievements.getOrDefault(p.getName(),new ArrayList<>());
        return achievements.contains(a);
    }
    public void grantAchievement(Player p, int a){
        ItemStack[]stacks = achievementList.achievements.clone();
        if(a < 0 || a > stacks.length)return;
        if(isGaming(p)) {
            List<Integer> achievements = playerAchievements.getOrDefault(p.getName(), new ArrayList<>());
            if(!achievements.contains(a)) {
                ItemStack item = stacks[a];
                p.sendMessage(ChatColor.GREEN + "获得成就：" + item.getItemMeta().getDisplayName());
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,0.7f);
                achievements.add(a);
                playerAchievements.put(p.getName(), achievements);
            }
        }
    }
    public List<Integer>getPlayerAchievements(Player p){
        return playerAchievements.getOrDefault(p.getName(),new ArrayList<>());
    }
}

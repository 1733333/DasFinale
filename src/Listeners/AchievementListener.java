package Listeners;

import Events.GameEndEvent;
import Events.PlayerKillPlayerEvent;
import Universal.GameStatus;
import Universal.PlayerStats;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;

public class AchievementListener implements Listener {
    PlayerStats playerStats = PlayerStats.INSTANCE;
    GameStatus gameStatus = GameStatus.getInstance();
    HashMap<String,Integer>fireCount = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW)
    public void gameEndEvent(GameEndEvent endEvent){
        World w = endEvent.getWorld();
        int win = endEvent.getWinTeam();
        double maxKD = 0;
        double maxDamage = 0;
        Player maxDamagePlayer = null;
        Player mvp = null;
        for(Player p : w.getPlayers()) {
            if (playerStats.isGaming(p)){
                int k = playerStats.getPlayerKills(p);
                int d = playerStats.getPlayerDeaths(p);
                double damage = GameListeners.playerDamage.getOrDefault(p.getName(),0D);
                if(damage > maxDamage){
                    maxDamage = damage;
                    maxDamagePlayer = p;
                }
                if(k > 0) {
                    if(d == 0){
                        d = 1;
                    }
                    double playerKD = (double) k / d;
                    if(playerKD > maxKD){
                        mvp = p;
                        maxKD = playerKD;
                    }
                }
                int team = playerStats.getTeam(p);
                String teamName = gameStatus.getTeamName(team);
                playerStats.grantAchievement(p,0);
                if(team == win){
                    playerStats.grantAchievement(p,4);
                    if(d == 0){
                        playerStats.grantAchievement(p,17);
                    }
                }
                if(gameStatus.getScore(teamName) == 0){
                    playerStats.grantAchievement(p,8);
                }
            }
        }
        if(mvp != null) {
            playerStats.grantAchievement(mvp, 9);
        }
        if(maxDamagePlayer != null){
            playerStats.grantAchievement(maxDamagePlayer,14);
        }
    }
    @EventHandler
    public void playerKillPlayerEvent(PlayerKillPlayerEvent event){
        /*
            type列表
            0:未分类
            1:钱箱淘汰
            2:罐子淘汰
        */
        Player killer = event.getKiller();
        Player dead = event.getDead();
        int type = event.getType();
        playerStats.grantAchievement(killer,1);
        if(killer.getFallDistance() >= 5){
            playerStats.grantAchievement(killer,12);
        }
        if(dead.getFallDistance() >= 5){
            playerStats.grantAchievement(killer,13);
        }
        if(killer.getFireTicks() > 0){
            int count = fireCount.getOrDefault(killer.getName(),0);
            count += 1;
            if(count >= 3){
                playerStats.grantAchievement(killer,24);
                fireCount.remove(killer.getName());
            }else {
                fireCount.put(killer.getName(),count);
            }
        }
        if(type == 0)return;
        switch (type){
            case 1 -> playerStats.grantAchievement(killer,10);
            case 2 -> playerStats.grantAchievement(killer,26);
        }
    }
}

package Listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import ru.xezard.glow.data.glow.Glow;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class OtherStuffListener implements Listener {
    JavaPlugin plugin;
    Random r = new Random();
    HashMap<Entity,BukkitRunnable>dpsMap = new HashMap<>();
    HashMap<Entity,Double> totalTimeMap = new HashMap<>();

    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void interactEvent(PlayerInteractAtEntityEvent entityEvent) {
        Player p = entityEvent.getPlayer();
        Entity entity = entityEvent.getRightClicked();
        String clickedName = entity.getName();
        if(entity instanceof LivingEntity l) {
            if (clickedName.contains("伤害测试假人")) {
                entityEvent.setCancelled(true);
                double health = l.getHealth();
                if (p.isSneaking()) {
                    p.sendMessage(ChatColor.GREEN + "当前生命值：" + ChatColor.RED + String.format("%.2f", health));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void damageEvent(EntityDamageByEntityEvent damageEvent) {
        Entity damaged = damageEvent.getEntity();
        Entity damager = damageEvent.getDamager();
        if(damaged instanceof LivingEntity l) {
            if (l.getName().contains("伤害测试假人")) {
                double damage = damageEvent.getFinalDamage();
                if (damager instanceof Projectile pr) {
                    ProjectileSource source = pr.getShooter();
                    if (source instanceof Player p) {
                        damager = p;
                    }
                }
                    damager.sendMessage(ChatColor.GREEN
                                    + ""
                                    + ChatColor.BOLD
                                    + "造成伤害："
                                    + ChatColor.RED
                                    + ChatColor.BOLD
                                    + String.format("%.2f", damage));
                if (dpsMap.getOrDefault(damaged,null) == null) {
                    Entity finalDamager = damager;
                    BukkitRunnable task = new BukkitRunnable() {
                        @Override
                        public void run() {
                            if(damaged.isDead()){
                                double totalTime = totalTimeMap.getOrDefault(damaged, 0D);
                                double max = ((LivingEntity) damaged).getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                                double dps = max / totalTime;
                                if(finalDamager instanceof Player p1) {
                                    p1.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                            TextComponent.fromLegacyText(
                                                    ChatColor.AQUA
                                                            + ""
                                                            + ChatColor.BOLD + "击杀时间："
                                                            + ChatColor.YELLOW
                                                            + ChatColor.BOLD
                                                            + String.format("%.2f", totalTime)
                                                            + ChatColor.AQUA
                                                            + ChatColor.BOLD + "秒，平均每秒伤害："
                                                            + ChatColor.YELLOW
                                                            + ChatColor.BOLD
                                                            + String.format("%.2f", dps)));
                                }
                                this.cancel();
                                totalTimeMap.remove(damaged);
                                dpsMap.remove(damaged);
                            }else {
                                double totalT = totalTimeMap.getOrDefault(damaged,0D);
                                totalTimeMap.put(damaged,totalT + 0.1);
                            }
                        }
                    };
                    task.runTaskTimer(plugin, 0L, 2L);
                    dpsMap.put(damaged,task);
                }
            }
        }
    }
    @EventHandler
    public void playerChat(AsyncPlayerChatEvent chatEvent){
        if(chatEvent.getMessage().contains("占卜")){
            BukkitRunnable chat = new BukkitRunnable() {
                @Override
                public void run() {
                    String message = "";
                    switch (r.nextInt(5)) {
                        case 0 -> message = "一定可以";
                        case 1 -> message = "很有可能";
                        case 2 -> message = "有一定的可能";
                        case 3 -> message = "不太可能";
                        case 4 -> message = "完全没可能";
                    }
                    Bukkit.broadcastMessage(ChatColor.YELLOW +"金胡萝卜神：" + message);
                }
            };
            chat.runTaskLater(plugin,10L);
        }
    }
    @EventHandler
    public void entitySummonEvent(EntitySpawnEvent spawnEvent){
        Entity e = spawnEvent.getEntity();
        if(e instanceof Skeleton s){
            if(e.getName().contains("桐生一马")){
                summonDragon(s);
            }
        }
    }

    public void summonDragon(LivingEntity e){
        Color c = Color.WHITE;
        EntityEquipment equipment = e.getEquipment();
        ItemStack head = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta headMeta = (LeatherArmorMeta) head.getItemMeta();
        LeatherArmorMeta chestMeta = (LeatherArmorMeta) chest.getItemMeta();
        LeatherArmorMeta legMeta = (LeatherArmorMeta) leg.getItemMeta();
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        headMeta.setColor(c);
        chestMeta.setColor(c);
        legMeta.setColor(c);
        bootsMeta.setColor(c);
        head.setItemMeta(headMeta);
        chest.setItemMeta(chestMeta);
        leg.setItemMeta(legMeta);
        boots.setItemMeta(bootsMeta);
        equipment.setHelmet(head);
        equipment.setChestplate(chest);
        equipment.setLeggings(leg);
        equipment.setBoots(boots);
        e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(150);
        e.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);
        e.setHealth(150);
        World w = e.getWorld();
        BossBar bar = Bukkit.createBossBar(ChatColor.AQUA + "桐生一马|“流氓风格”", BarColor.BLUE, BarStyle.SOLID);
        bar.addFlag(BarFlag.CREATE_FOG);
        Glow g = Glow.builder().color(ChatColor.AQUA).name(e.getName()).build();
        g.addHolders(e);
        for(Player p : w.getPlayers()){
            bar.addPlayer(p);
            g.display(p);
        }
        BukkitRunnable bossTask = new BukkitRunnable() {
            int count = 0;
            double max = e.getMaxHealth();
            @Override
            public void run() {
                if (e.isDead()) {
                    bar.removeAll();
                    this.cancel();
                    return;
                }
                double health = e.getHealth();
                if(health / max > 1){
                    bar.setProgress(1);
                }else if (health / max < 0){
                    bar.setProgress(0);
                }else {
                    bar.setProgress(health / max);
                }
                if(count % 100 == 0 && count > 0){

                    count = 0;
                }
                count +=1;
            }
        };
        bossTask.runTaskTimer(plugin,0L,1L);
    }
}

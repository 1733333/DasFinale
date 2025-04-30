package Listeners;

import Events.StartSkillEvent;
import Events.StopSkillEvent;
import Universal.GameStatus;
import Universal.Items;
import Universal.Kits;
import Universal.PlayerStats;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;

import static Listeners.GameListeners.itemCarriedByPlayerMap;
import static Listeners.GameListeners.playerCarryItemMap;

public class SkillListeners implements Listener {
    JavaPlugin plugin;
    Kits k = Kits.getInstance();
    Items items = Items.getInstance();
    PlayerStats playerStats = PlayerStats.INSTANCE;
    GameStatus gameStatus = GameStatus.getInstance();
    HashMap<String, HashMap<Integer,Double>>playerEnergy = new HashMap<>();
    HashMap<String, HashMap<Integer,BossBar>> playerBars = new HashMap<>();
    HashMap<String, HashMap<Integer,BukkitRunnable>> playerTasks = new HashMap<>();
    HashMap<String,HashSet<Integer>> playerUsingSkill = new HashMap<>();
    public static HashMap<String,Entity>playerTurret = new HashMap<>();
    public static HashMap<Entity,Player>turretPlayer = new HashMap<>();
    HashSet<Entity>projectileHit = new HashSet<>();

    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void projectileHit(ProjectileHitEvent hitEvent){
        Entity hit = hitEvent.getEntity();
        if(hit.getCustomName() != null){
            projectileHit.add(hit);
        }
    }
    @EventHandler
    public void playerInteract(PlayerInteractEvent interactEvent) {
        Action action = interactEvent.getAction();
        Player p = interactEvent.getPlayer();
        ItemStack hand = p.getInventory().getItemInMainHand();
        ItemStack offHand = p.getInventory().getItemInOffHand();
        if (action.equals(Action.RIGHT_CLICK_AIR)
                || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            if (hand.getType() != Material.AIR) {
                ItemMeta meta = hand.getItemMeta();
                interactEvent.setCancelled(true);
                switch (items.getSkillID(meta.getDisplayName())) {
                    case -1 -> interactEvent.setCancelled(false);
                    case 10 -> recycleTurret(p);
                }
            } else if (offHand.getType() != Material.AIR) {
                ItemMeta meta = offHand.getItemMeta();
                if (items.getSkillID(meta.getDisplayName()) != -1) {
                    interactEvent.setCancelled(true);
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerAttack(EntityDamageByEntityEvent damageEvent) {
        Entity damaged = damageEvent.getEntity();
        Entity attacker = damageEvent.getDamager();
        double damage = damageEvent.getFinalDamage();
        Player player = null;
        if (attacker instanceof Player p) {
            player = p;
        }else if(attacker instanceof Projectile pro){
            if(pro.getShooter() instanceof Player p){
                player = p;
            }
        }
        if(player != null){
            if(damaged instanceof Player || damaged instanceof Skeleton) {
                if(player == damaged)return;
                double max = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                double health = player.getHealth();
                if (playerStats.getSkill(player) == 6) {
                    if(health == max){
                        double absorption = health + damage - max;
                        if(absorption * 0.3 + player.getAbsorptionAmount() >= 15){
                            player.setAbsorptionAmount(15);
                        }else {
                            player.setAbsorptionAmount(absorption * 0.3 + player.getAbsorptionAmount());
                        }
                    }else {
                        k.heal(player,damage * 0.3);
                    }
                }
            }
        }
    }
    @EventHandler
    public void playerThrow(PlayerDropItemEvent dropItemEvent) {
        Player p = dropItemEvent.getPlayer();
        if(p.getGameMode() == GameMode.SPECTATOR)return;
        int skill = playerStats.getSkill(p);
        if (skill >= 0) {
            dropItemEvent.setCancelled(true);
            if (playerStats.isCarrying(p)) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(ChatColor.RED + "正在拿着东西，需要丢掉东西才能继续操作"));
                return;
            }
            if(p.hasPotionEffect(PotionEffectType.SLOW_DIGGING)){
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(ChatColor.RED + "被击晕！无法使用技能"));
                return;
            }
            Bukkit.getPluginManager().callEvent(new StartSkillEvent(p,skill));
        }
    }
    @EventHandler
    public void playerStartSkill(StartSkillEvent startSkillEvent){
        Player p = startSkillEvent.getPlayer();
        int skill = startSkillEvent.getSkill();
        switch (skill){
            case 0 -> dodgeDash(p,skill);
            case 1 -> sonarDagger(p,skill);
            case 2 -> healGrenade(p,skill);
            case 4 -> rushAndSlam(p,skill);
            case 5 -> claw(p,skill);
            case 7 -> freezer(p,skill);
            case 9 -> grapplingHook(p,skill);
            case 10 -> turret(p,skill);
            case 11 -> gravityDevice(p,skill);
        }
        switch (skill){
            case 5,9,10,11:return;
        }
        HashSet<Integer>isUsing = playerUsingSkill.getOrDefault(p.getName(),new HashSet<>());
        isUsing.add(skill);
        playerUsingSkill.put(p.getName(),isUsing);
    }
    @EventHandler
    public void playerStopSkill(StopSkillEvent stopSkillEvent){
        Player p = stopSkillEvent.getPlayer();
        int skill = stopSkillEvent.getSkill();
        int time = switch (skill){
            case 11 -> 20;
            default -> 0;
        };
        HashSet<Integer>isUsing = playerUsingSkill.getOrDefault(p.getName(),new HashSet<>());
        switch (skill){
            case 11 :isUsing.remove(skill);
        }
        playerUsingSkill.put(p.getName(),isUsing);
        recoverEnergy(p,skill,time);
    }
    @EventHandler
    public void turretShoot(ProjectileLaunchEvent launchEvent){
        Projectile projectile = launchEvent.getEntity();
        if(projectile.getShooter() instanceof Entity shooter) {
            if (shooter instanceof Snowman s) {
                if(s.getName().contains("炮塔")) {
                    launchEvent.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void turretDie(EntityDeathEvent deathEvent){
        Entity dead = deathEvent.getEntity();
        if(dead instanceof Snowman s){
            if(s.getName().contains("炮塔")){
                Player owner = turretPlayer.getOrDefault(s,null);
                if(owner != null){
                    changeEnergy(owner,10,-1);
                    recoverEnergy(owner,10,30);
                    playerTurret.remove(owner.getName());
                    turretPlayer.remove(dead);
                }
            }
        }
    }
    @EventHandler
    public void turretDamage(EntityDamageEvent damageEvent){
        Entity dead = damageEvent.getEntity();
        if(dead instanceof Snowman s){
            if(s.getName().contains("炮塔")){
                if(damageEvent.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION){
                    damageEvent.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void turretDamageByPlayer(EntityDamageByEntityEvent damageEvent){
        Entity dead = damageEvent.getEntity();
        Entity attacker = damageEvent.getDamager();
        if(dead instanceof Snowman s){
            if(s.getName().contains("炮塔")){
                Player owner = turretPlayer.getOrDefault(s,null);
                if(owner != null) {
                    if (attacker instanceof Player p) {
                        if (gameStatus.isTeamMate(p, owner)) {
                            damageEvent.setCancelled(true);
                        }else {
                            s.setTarget(p);
                        }
                    }
                }
            }
        }
    }

    public double getEnergy(String player, int skillID) {
        HashMap<Integer, Double> defaultMap = new HashMap<>();
        HashMap<Integer, Double> energyMap = this.playerEnergy.getOrDefault(player, defaultMap);
        return energyMap.getOrDefault(skillID, 1.0D);
    }

    public void changeEnergy(Player p, int skillID, double cost) {
        HashMap<Integer, Double> defaultMap = new HashMap<>();
        HashMap<Integer, Double> energyMap = this.playerEnergy.getOrDefault(p.getName(), defaultMap);
        double energy = energyMap.getOrDefault(skillID, 1.0D);
        energy += cost;
        if (energy >= 1.0D) {
            energyMap.put(skillID, 1.0D);
        } else energyMap.put(skillID, Math.max(energy, 0.0D));
        this.playerEnergy.put(p.getName(), energyMap);
    }

    public void recoverEnergy(Player p,int skillID,int time) {
        HashSet<Integer> skillSet = playerUsingSkill.getOrDefault(p.getName(),new HashSet<>());//记录技能是否在使用中
        HashMap<Integer,BossBar>barMap = playerBars.getOrDefault(p.getName(),new HashMap<>());//记录BossBar
        ItemStack skill = this.items.getSkill(skillID);//通过id查找物品
        ItemMeta meta = skill.getItemMeta();
        String name = meta.getDisplayName();
        int body = playerStats.getClass(p);
        BarColor c = switch (body){//根据玩家职业决定bar颜色
            case 0 ->BarColor.BLUE;
            case 1 ->BarColor.YELLOW;
            case 2 ->BarColor.RED;
            default -> BarColor.WHITE;
        };
        BossBar bar = barMap.getOrDefault(skillID,Bukkit.createBossBar(name, c, BarStyle.SOLID));
        if (skillSet.contains(skillID)) {//技能在使用中
            return;
        }
        bar.addPlayer(p);
        barMap.put(skillID,bar);
        playerBars.put(p.getName(),barMap);
        BukkitRunnable charging = new BukkitRunnable() {
            final double step = 1.0D / (time * 20);
            public void run() {
                HashMap<Integer, Double> energyMap = playerEnergy.getOrDefault(p.getName(), new HashMap<>());//玩家能量Map
                double energy = energyMap.getOrDefault(skillID, 0.0D);
                if (energy >= 1.0D) {//能量回复满了
                    energyMap.put(skillID, 1.0D);
                    playerEnergy.put(p.getName(), energyMap);
                    bar.removeAll();
                    skillSet.remove(skillID);
                    barMap.remove(skillID);
                    playerUsingSkill.put(p.getName(), skillSet);
                    playerBars.put(p.getName(),barMap);
                    cancel();
                }else {
                    try{
                        bar.setProgress(energy);
                    }catch (Exception ignored){}
                    energy += this.step;
                    energyMap.put(skillID, energy);
                    playerEnergy.put(p.getName(), energyMap);
                }
            }
        };
        charging.runTaskTimer(plugin, 0L, 1L);
        switch (skillID){
            case 11 -> {}
            default -> {return;}
        }
        HashMap<Integer,BukkitRunnable>taskMap = playerTasks.getOrDefault(p.getName(),new HashMap<>());
        BukkitRunnable task = taskMap.getOrDefault(skillID,null);
        if(task != null){
            task.cancel();
        }
        taskMap.put(skillID,charging);
        playerTasks.put(p.getName(),taskMap);
    }
    public void consumeEnergy(Player p,int skillID,int time) {
        HashMap<Integer, BossBar> barMap = playerBars.getOrDefault(p.getName(), new HashMap<>());//记录BossBar
        ItemStack skill = this.items.getSkill(skillID);
        ItemMeta meta = skill.getItemMeta();
        String name = meta.getDisplayName();
        int body = playerStats.getClass(p);
        BarColor c = switch (body) {
            case 0 -> BarColor.BLUE;
            case 1 -> BarColor.YELLOW;
            case 2 -> BarColor.RED;
            default -> BarColor.WHITE;
        };
        BossBar bar = barMap.getOrDefault(skillID, Bukkit.createBossBar(name, c, BarStyle.SOLID));
        HashMap<Integer, Double> defaultMap = new HashMap<>();
        bar.addPlayer(p);
        barMap.put(skillID, bar);
        playerBars.put(p.getName(), barMap);
        BukkitRunnable consuming = new BukkitRunnable() {
            final double step = 1.0D / (time * 20);

            public void run() {
                HashMap<Integer, Double> energyMap = playerEnergy.getOrDefault(p.getName(), defaultMap);
                double energy = energyMap.getOrDefault(skillID, 1D);
                if (energy <= 0D) {
                    bar.removeAll();
                    energyMap.put(skillID, 0D);
                    playerEnergy.put(p.getName(), energyMap);
                    barMap.remove(skillID);
                    playerBars.put(p.getName(), barMap);
                    Bukkit.getPluginManager().callEvent(new StopSkillEvent(p, skillID));
                    cancel();
                } else {
                    bar.setProgress(energy);
                    energy -= this.step;
                    energyMap.put(skillID, energy);
                    playerEnergy.put(p.getName(), energyMap);
                }
            }
        };
        consuming.runTaskTimer(plugin, 0L, 1L);
        switch (skillID) {
            case 11 -> {
            }
            default -> {
                return;
            }
        }
        HashMap<Integer, BukkitRunnable> taskMap = playerTasks.getOrDefault(p.getName(), new HashMap<>());
        BukkitRunnable task = taskMap.getOrDefault(skillID, null);
        if (task != null) {
            task.cancel();
        }
        taskMap.put(skillID, consuming);
        playerTasks.put(p.getName(), taskMap);
    }
    public void dodgeDash(Player p,int id) {
        World w = p.getWorld();
        double energy = getEnergy(p.getName(), id);
        double consume = 0.34;
        if (energy > consume) {
            changeEnergy(p, id, -consume);
            recoverEnergy(p, id, 21);
            Location eyeLoc = p.getEyeLocation();
            Vector eyeVec = eyeLoc.getDirection();
            Vector dash = new Vector(eyeVec.getX(),0.1,eyeVec.getZ());
            if(p.isOnGround()) {
                p.setVelocity(dash.multiply(2));
            }else {
                p.setVelocity(eyeVec);
            }
            w.playSound(p.getLocation(), Sound.ITEM_TRIDENT_RIPTIDE_1, 1, 1);
            w.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 5));
            dashParticle(p);
        }
    }
    public void dashParticle(Player p){
        World w = p.getWorld();
        Particle.DustOptions dust = new Particle.DustOptions(Color.AQUA,0.75f);
        BukkitRunnable particle = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if(count >= 10){
                    this.cancel();
                }
                w.spawnParticle(Particle.REDSTONE,p.getLocation(),50,0.3,1,0.3,dust);
                count+=1;
            }
        };
        particle.runTaskTimer(plugin,0L,1L);
    }
    public void sonarDagger(Player p,int id){
        World w = p.getWorld();
        double energy = getEnergy(p.getName(), id);
        double consume = 0.5;
        if(energy > consume){
            changeEnergy(p, id, -consume);
            recoverEnergy(p,id,40);
            Location shootLoc = p.getEyeLocation();
            Vector shootVec = shootLoc.getDirection();
            Arrow a = w.spawnArrow(shootLoc,shootVec,2,0);
            a.setDamage(6);
            a.setShooter(p);
            a.setCustomName(p.getName() + "的声纳飞刀");
            a.addCustomEffect(new PotionEffect(PotionEffectType.GLOWING,200,0),false);
            w.playSound(p.getLocation(),Sound.ITEM_TRIDENT_THROW,1,1.5f);
            BukkitRunnable hit = new BukkitRunnable() {
                @Override
                public void run() {
                    if(projectileHit.contains(a)){
                        this.cancel();
                        sonar(p,a.getLocation());
                        projectileHit.remove(a);
                        a.remove();
                    }
                }
            };
            hit.runTaskTimer(plugin,0L,1L);
        }
    }
    public void sonar(Player shooter,Location loc){
        World w = loc.getWorld();
        BukkitRunnable detect = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                Collection<Entity> entities = w.getNearbyEntities(loc,10,10,10);
                w.spawnParticle(Particle.END_ROD,loc,100,0,0,0,0.4);
                w.playSound(loc,Sound.ITEM_TRIDENT_RETURN,2,1.5f);
                boolean detected = false;
                for(Entity e : entities){
                    if(e instanceof LivingEntity l){//队伍判断
                        if(l instanceof ArmorStand)continue;
                        if(l == shooter)continue;
                        if(l instanceof Player p){
                            if(gameStatus.isTeamMate(shooter,p))continue;
                            p.sendTitle(" ","被探测！",0,40,10);
                        }
                        l.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,40,0));
                        detected = true;
                    }
                }
                if(detected){
                    shooter.playSound(shooter.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
                    shooter.sendTitle(" ","探测到敌人！",0,40,10);
                }
                count += 1;
                if(count > 2){
                    this.cancel();
                }
            }
        };
        detect.runTaskTimer(plugin,0L,60L);
    }
    public void healGrenade(Player p,int id){
        World w = p.getWorld();
        double radius = 2.5;
        double energy = getEnergy(p.getName(), id);
        double consume = 0.145;
        ItemStack nade = items.healGrenade().clone();
        Material type = nade.getType();
        if(energy >= consume && p.getCooldown(type) == 0) {
            if(energy <= 0.20){
                int coolDown = 10;
                p.setCooldown(type,coolDown * 20);
                p.sendTitle(" ",ChatColor.YELLOW + "！过热！",10,30,10);
                p.playSound(p.getLocation(),Sound.BLOCK_FIRE_EXTINGUISH,1,1);
            }else {
                p.setCooldown(type,8);
            }
            changeEnergy(p, id, -consume);
            recoverEnergy(p,id,20);
            Location shootLoc = p.getEyeLocation();
            Snowball ball = (Snowball) w.spawnEntity(shootLoc,EntityType.SNOWBALL);
            ball.setItem(new ItemStack(Material.MAGMA_CREAM));
            ball.setVelocity(shootLoc.getDirection());
            w.playSound(p.getLocation(),Sound.ENTITY_EGG_THROW,1,1);
            BukkitRunnable hit = new BukkitRunnable() {
                int count = 0;
                @Override
                public void run() {
                    if(count % 10 == 0){
                        double traceR = 3;
                        for (Entity e : ball.getNearbyEntities(traceR,traceR,traceR)){
                            if(e instanceof Player p1){
                                if(p1.getGameMode() == GameMode.SPECTATOR)continue;
                                if(p1 == p)continue;
                                if(gameStatus.isTeamMate(p,p1)){
                                    Vector p1V = p1.getEyeLocation().toVector();
                                    Vector ballV = ball.getLocation().toVector();
                                    Vector trace = (p1V.subtract(ballV)).normalize();
                                    ball.setVelocity(trace.multiply(1));
                                    break;
                                }
                            }
                        }
                    }
                    Color c = Color.YELLOW.mixColors(Color.ORANGE).mixColors(Color.RED);
                    Particle.DustOptions dust = new Particle.DustOptions(c,1);
                    w.spawnParticle(Particle.REDSTONE,ball.getLocation(),0,dust);
                    if(ball.isDead()){
                        w.playSound(ball.getLocation(),Sound.BLOCK_GLASS_BREAK,1,1);
                        w.playSound(ball.getLocation(),Sound.BLOCK_GLASS_BREAK,1,1);
                        w.spawnParticle(Particle.ITEM_CRACK,ball.getLocation(),100,radius / 2,radius / 2,radius/2,0,nade);
                        w.spawnParticle(Particle.EXPLOSION_LARGE,ball.getLocation(),1);
                        this.cancel();
                        List<Entity>entities = ball.getNearbyEntities(radius,radius,radius);
                        for(Entity e :entities) {
                            if (k.distance(ball.getLocation(), e.getLocation()) > 5) continue;
                            if (e instanceof Player p1) {
                                if (!gameStatus.isTeamMate(p, p1)) continue;
                                if(p1 == p)continue;
                                if (p1.isDead()) continue;
                                k.heal(p1, 5);
                                w.spawnParticle(Particle.HEART, p1.getEyeLocation().add(0,1,0), 1);
                                p.playSound(p.getLocation(),Sound.BLOCK_ENCHANTMENT_TABLE_USE,1,0.8f);
                                p1.playSound(p1.getLocation(),Sound.BLOCK_ENCHANTMENT_TABLE_USE,1,0.8f);
                            }
                        }
                    }
                    count += 1;
                }
            };
            hit.runTaskTimer(plugin,0L,1L);
        }
    }
    public void rushAndSlam(Player p,int id){
        World w = p.getWorld();
        double energy = getEnergy(p.getName(), id);
        double consume = 1;
        if(energy >= consume){
            changeEnergy(p,id,-consume);
            recoverEnergy(p,id,20);
            if(p.isOnGround()){
                w.playSound(p.getLocation(),Sound.ENTITY_VINDICATOR_HURT,2,1);
                rush(p);
            }else {
                slam(p);
            }
        }
    }
    public void rush(Player p){
        World w = p.getWorld();
        BukkitRunnable rushing = new BukkitRunnable() {
            int count = 0;
            int walls = 0;
            boolean hit = false;
            @Override
            public void run() {
                Location eyeLoc = p.getEyeLocation();
                Vector eyeVec = eyeLoc.getDirection();
                Vector front = new Vector(eyeVec.getX(),0,eyeVec.getZ());
                Location breakLoc = eyeLoc.add(front);
                count += 1;
                if(p.getGameMode().equals(GameMode.SPECTATOR)){
                    this.cancel();
                    return;
                }
                if(!p.isOnGround()){
                    this.cancel();
                    slam(p);
                }
                if(count > 8 || walls > 2){
                    this.cancel();
                    return;
                }
                if(k.breakSquareBlock(p,breakLoc,1,1,1,0.5)){
                    walls += 1;
                }
                double radius = 1.5;
                Collection<Entity>entities = w.getNearbyEntities(breakLoc,radius,radius,radius);
                for(Entity e : entities){
                    if(e.getName().contains("传送门")  || e.getName().contains("滑索"))continue;
                    k.knockBack(e,p.getLocation(),3);
                    if(e instanceof LivingEntity l){
                        if(l == p)continue;
                        double damage = 16;
                        if(hit){
                            damage = 7;
                        }
                        l.damage(damage,p);
                        w.playSound(l.getLocation(),Sound.ENTITY_PLAYER_ATTACK_CRIT,1,1);
                        hit = true;
                    }
                }
                if(p.isSprinting()){
                    p.setVelocity(front.multiply(0.5));
                }else {
                    p.setVelocity(front.multiply(1));
                }
            }
        };
        rushing.runTaskTimer(plugin,0L,5L);
    }
    public void slam(Player p){
        World w = p.getWorld();
        Vector down = new Vector(0,-5,0);
        w.playSound(p.getLocation(),Sound.ENTITY_VINDICATOR_DEATH,2,1);
        int y = p.getLocation().getBlockY();
        BukkitRunnable ground = new BukkitRunnable() {
            @Override
            public void run() {
                if(p.getGameMode().equals(GameMode.SPECTATOR)){
                    this.cancel();
                    return;
                }
                if(p.isSneaking()){
                    p.setVelocity(down);
                }
                if(p.isOnGround()){
                    this.cancel();
                    k.breakBallBlock(p,p.getLocation(),3,0.3);
                    w.playSound(p.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,1,1);
                    w.spawnParticle(Particle.EXPLOSION_HUGE,p.getLocation(),1);
                    List<Entity>entities = p.getNearbyEntities(5,5,5);
                    for(Entity e : entities){
                        if(e.getName().contains("传送门")  || e.getName().contains("滑索"))continue;
                        if(k.distance(p.getLocation(),e.getLocation()) > 5)continue;
                        if(e instanceof ItemFrame i)i.remove();
                        if(e instanceof LivingEntity l){
                            if(l == p)continue;
                            int distance = (int) k.distance(l.getEyeLocation(),e.getLocation());
                            double damage = 11 + ((y - p.getLocation().getY()) / 3) * 5;
                            l.damage(damage - (distance - 1) * 3,p);
                            k.knockBack(l,p.getLocation(),3);
                        }
                    }
                }
            }
        };
        ground.runTaskTimer(plugin,0L,3L);
    }
    public void claw(Player p,int id) {
        World w = p.getWorld();
        double energy = getEnergy(p.getName(), id);
        double consume = 1;
        if(energy >= consume) {
            changeEnergy(p, id, -consume);
            w.playSound(p.getLocation(), Sound.BLOCK_CHAIN_PLACE, 1, 1);
            BukkitRunnable claw = new BukkitRunnable() {
                int count = 0;

                @Override
                public void run() {
                    boolean hooked = false;
                    Location shootLoc = p.getEyeLocation();
                    Vector shootVec = shootLoc.getDirection();
                    RayTraceResult result = w.rayTraceEntities(shootLoc.add(shootVec), shootVec, 16);
                    Location pLoc = shootLoc.clone();
                    for (int i = 0; i < 16; i++) {
                        BlockData data = Bukkit.createBlockData(Material.CHAIN);
                        w.spawnParticle(Particle.BLOCK_DUST, pLoc, 10, data);
                        pLoc.add(shootVec.clone());
                    }
                    if (result != null) {
                        Entity e = result.getHitEntity();
                        if (e instanceof LivingEntity l) {
                            if(!k.rayTraceBlock(p.getEyeLocation(),l.getEyeLocation())
                                    ||!k.rayTraceBlock(p.getEyeLocation(),l.getLocation())) {
                                hooked = true;
                            }
                        }
                        if (e.getName().contains("传送门") || e.getName().contains("滑索")) {
                            hooked = false;
                        }
                        if (e instanceof Player p1) {
                            if (gameStatus.isTeamMate(p, p1)) {
                                hooked = false;
                            }
                        }
                        if (hooked) {
                            hooked(p, e);
                        }
                    }
                    if (count > 5 || hooked) {
                        int time = 5;
                        if (hooked) time = 10;
                        recoverEnergy(p, id, time);
                        this.cancel();
                    }
                    count += 1;
                }
            };
            claw.runTaskTimer(plugin, 0L, 1L);
        }
    }
    public void hooked(Player p,Entity e) {
        World w = p.getWorld();
        if (e instanceof LivingEntity l) {
            p.sendTitle(" ", ChatColor.RED + "上钩了！", 0, 10, 10);
            e.setGravity(false);
            l.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 10));
            l.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 9));
            if(!(e instanceof ArmorStand)) {
                l.damage(7, p);
            }
            if (e instanceof Player player) {
                e.setGravity(true);
                player.sendTitle(" ", ChatColor.RED + "被抓住了！", 0, 10, 10);
            }
            BukkitRunnable pull = new BukkitRunnable() {
                public void run() {
                    if(!p.getGameMode().equals(GameMode.SPECTATOR)) {
                        if (e instanceof ArmorStand) {
                            if (e.getName().contains("罐")
                                    || e.getName().contains("钱箱")) {
                                playerCarryItemMap.put(p, e);
                                itemCarriedByPlayerMap.put(e, p);
                                k.carryItem(p, e);
                            }
                        }
                        e.teleport(p.getEyeLocation());
                        w.playSound(p.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 1.0F, 1.0F);
                    }
                    e.setGravity(true);
                }
            };
            pull.runTaskLater(plugin, 10L);
        }
    }
    public void freezer(Player p,int id){
        World w = p.getWorld();
        double energy = getEnergy(p.getName(), id);
        double consume = 0.34;
        if(energy >= consume){
            recoverEnergy(p,id,30);
            Location eyeLoc = p.getEyeLocation();
            Vector eyeVec = eyeLoc.getDirection();
            RayTraceResult result = w.rayTraceBlocks(eyeLoc,eyeVec,6);
            if(result != null){
                changeEnergy(p,id,-consume);
                Block block = result.getHitBlock();
                if(block.getType() != Material.AIR){
                    w.playSound(eyeLoc,Sound.BLOCK_ENCHANTMENT_TABLE_USE,1,1);
                    w.playSound(eyeLoc,Sound.BLOCK_GLASS_BREAK,1,1);
                    Location loc = block.getLocation();
                    int radius = 2;
                    int x = loc.getBlockX();
                    int y = loc.getBlockY();
                    int z = loc.getBlockZ();
                    for (int a = -radius; a <= radius; a++) {
                        for (int b = -radius; b <= radius; b++) {
                            for (int c = -radius; c <= radius; c++) {
                                Location newLoc = new Location(w, x + a, y + b, z + c);
                                Block ice = w.getBlockAt(newLoc);
                                if (k.isBreakable(p,ice)) {
                                    ice.setType(Material.LIGHT_BLUE_STAINED_GLASS);
                                    w.spawnParticle(Particle.ITEM_CRACK, newLoc, 20,new ItemStack(Material.SNOWBALL));
                                }
                            }
                        }
                    }
                    for(Entity e : w.getNearbyEntities(loc,radius,radius,radius)){
                        if(e instanceof Player p1) {
                            if (p1.getGameMode() == GameMode.SPECTATOR
                                    || gameStatus.isTeamMate(p, p1)) {
                                if(e.getFireTicks() > 0) {
                                    e.setFireTicks(0);
                                    w.playSound(e.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
                                }
                                continue;
                            }
                        }
                        if(e instanceof LivingEntity l){
                            l.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,40,9));
                            w.spawnParticle(Particle.ITEM_CRACK, l.getEyeLocation(),20,0.1,0.1,0.1,0
                                    ,new ItemStack(Material.PACKED_ICE));
                            if(l instanceof Player p1){
                                p1.sendTitle(" ",ChatColor.YELLOW + "！被冰冻！",10,20,10);
                                p.sendTitle(" ",ChatColor.YELLOW + "！冻结了一位选手！",10,20,10);
                            }
                        }
                    }
                }
            }
        }
    }
    public void grapplingHook(Player p,int id){
        World w = p.getWorld();
        double energy = getEnergy(p.getName(), id);
        double consume = 1;
        if(energy >= consume){
            changeEnergy(p,id,-consume);
            Location eyeLoc = p.getEyeLocation();
            Vector eyeVec = eyeLoc.getDirection();
            Arrow hook = w.spawnArrow(eyeLoc,eyeVec,3,0);
            hook.setDamage(0);
            hook.setGravity(false);
            hook.setCustomName("抓钩");
            w.playSound(p.getLocation(),Sound.ENTITY_FISHING_BOBBER_THROW,1,1);
            w.playSound(p.getLocation(),Sound.ENTITY_FISHING_BOBBER_THROW,1,1);
            BukkitRunnable hit = new BukkitRunnable() {
                @Override
                public void run() {
                    w.spawnParticle(Particle.CRIT,hook.getLocation(),0);
                    boolean hooked;
                    if(hook.isDead() || hook.getTicksLived() > 10){
                        hooked = hook.getTicksLived() <= 10;
                        if(hooked){
                            recoverEnergy(p,id,8);
                            Location aLoc = hook.getLocation();
                            Vector aVec = aLoc.toVector();
                            BukkitRunnable grapple = new BukkitRunnable() {
                                int count = 0;
                                @Override
                                public void run() {
                                    w.playSound(p.getLocation(),Sound.ENTITY_FISHING_BOBBER_RETRIEVE,1,0.7f);
                                    w.playSound(p.getLocation(),Sound.ENTITY_FISHING_BOBBER_RETRIEVE,1,0.7f);
                                    if(k.distance(aLoc,p.getLocation()) <= 5
                                            || p.isSneaking()
                                            || count > 20
                                            || p.getGameMode() == GameMode.SPECTATOR) {
                                        this.cancel();
                                        if(p.getGameMode() == GameMode.SPECTATOR){
                                            return;
                                        }
                                        Vector pVec = p.getLocation().toVector();
                                        Vector pull = aVec.clone().subtract(pVec.clone());
                                        p.setVelocity(pull.add(new Vector(0, 1, 0)).normalize().multiply(1.5));
                                        return;
                                    }
                                    count += 1;
                                    Vector pVec = p.getLocation().toVector();
                                    Vector pull = aVec.clone().subtract(pVec.clone());
                                    p.setVelocity(pull.clone().normalize().multiply(1));
                                    Location subLoc = p.getLocation().clone();
                                    for(int i = 0;i < pull.length();i ++){
                                        w.spawnParticle(Particle.CRIT,subLoc,0);
                                        subLoc.add(pull.clone().normalize());
                                    }
                                }
                            };
                            grapple.runTaskTimer(plugin,0L,2L);
                        }else {
                            changeEnergy(p,id,consume);
                        }
                        hook.remove();
                        this.cancel();
                    }
                }
            };
            hit.runTaskTimer(plugin,0L,1L);
        }
    }
    public void turret(Player p,int id) {
        if (playerTurret.getOrDefault(p.getName(), null) != null) return;
        double energy = getEnergy(p.getName(), id);
        if (energy == 1) {
            World w = p.getWorld();
            w.playSound(p.getLocation(), Sound.BLOCK_SNOW_BREAK, 1, 1);
            w.playSound(p.getLocation(), Sound.BLOCK_SNOW_BREAK, 1, 1);
            w.playSound(p.getLocation(), Sound.BLOCK_SNOW_BREAK, 1, 1);
            double maxHealth = 20;
            Snowman turret = (Snowman) w.spawnEntity(p.getLocation(), EntityType.SNOWMAN);
            turret.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
            turret.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);
            turret.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
            turret.setHealth(maxHealth);
            turret.setSilent(true);
            turret.setCustomName(ChatColor.YELLOW + p.getName() + "的炮塔");
            turret.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 86400, 0));
            playerTurret.put(p.getName(), turret);
            turretPlayer.put(turret, p);
            BukkitRunnable target = new BukkitRunnable() {
                @Override
                public void run() {
                    if (turret.isDead()) {
                        this.cancel();
                        return;
                    }
                    w.playSound(turret.getEyeLocation(),Sound.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON,1,1);
                    for (Entity e : turret.getNearbyEntities(10, 10, 10)) {
                        if (e instanceof Player p1) {
                            if (gameStatus.isTeamMate(p, p1)) continue;
                            if(p1.getGameMode() == GameMode.SPECTATOR)continue;
                        }
                        if (e instanceof ArmorStand || e instanceof Villager) continue;
                        if (e instanceof LivingEntity l) {
                            if(l.hasPotionEffect(PotionEffectType.INVISIBILITY))continue;
                            Vector pEye = turret.getEyeLocation().toVector();
                            Vector lEye = l.getEyeLocation().toVector();
                            Vector ray = lEye.clone().subtract(pEye);
                            RayTraceResult result = w.rayTraceBlocks(turret.getEyeLocation(),ray,k.distance(turret.getEyeLocation(),((LivingEntity) e).getEyeLocation()));
                            if(result == null || !k.isFullBlock(result.getHitBlock())) {
                                turret.setTarget(l);
                                w.playSound(turret.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
                                break;
                            }
                        }
                    }
                    Entity target = turret.getTarget();
                    if(target != null){
                        if(k.distance(target.getLocation(),turret.getLocation()) > 10){
                            turret.setTarget(null);
                        }
                    }
                }
            };
            BukkitRunnable shoot = new BukkitRunnable() {
                @Override
                public void run() {
                    if(turret.isDead()){
                        this.cancel();
                        return;
                    }
                    if(turret.getTarget() != null){
                        w.playSound(turret.getEyeLocation(),Sound.ENTITY_EGG_THROW,1,1);
                        Vector shootVec = turret.getEyeLocation().getDirection();
                        Snowball ball = (Snowball) w.spawnEntity(turret.getEyeLocation(),EntityType.SNOWBALL);
                        ball.setVelocity(shootVec.multiply(1));
                        BukkitRunnable hit = new BukkitRunnable() {
                            @Override
                            public void run() {
                                w.spawnParticle(Particle.ITEM_CRACK,ball.getLocation(),0,new ItemStack(Material.SNOWBALL));
                                if(ball.isDead()){
                                    this.cancel();
                                    return;
                                }
                                for(Entity e : ball.getNearbyEntities(1,1,1)){
                                    if(e instanceof LivingEntity l){
                                        if(e == turret)continue;
                                        if(e instanceof Player p1){
                                            if(p1.getGameMode() == GameMode.SPECTATOR)continue;
                                            if(p1.isBlocking()){
                                                w.playSound(p1.getLocation(),Sound.ITEM_SHIELD_BLOCK,1,1);
                                                continue;
                                            }
                                            if(gameStatus.isTeamMate(p,p1))continue;
                                        }
                                        Block foot = w.getBlockAt(turret.getLocation().add(0,0.5,0));
                                        if(foot.getType() == Material.AIR || foot.getType() == Material.LIGHT){
                                            FallingBlock block = w.spawnFallingBlock(l.getEyeLocation(),
                                                    Bukkit.createBlockData(Material.POWDER_SNOW));
                                            block.setDropItem(false);
                                        }
                                        l.damage(2.5);
                                        Bukkit.getPluginManager().callEvent(
                                                new EntityDamageByEntityEvent(p,l, EntityDamageEvent.DamageCause.PROJECTILE,2.5)
                                        );
                                        w.playSound(l.getLocation(),Sound.BLOCK_SNOW_BREAK,1,1);
                                        this.cancel();
                                        break;
                                    }
                                }
                            }
                        };
                        hit.runTaskTimer(plugin,0L,1L);
                    }
                }
            };
            target.runTaskTimer(plugin, 0L, 100L);
            shoot.runTaskTimer(plugin,0L,10L);
        }
    }
    public void recycleTurret(Player p){
        World w = p.getWorld();
        Entity turret = playerTurret.getOrDefault(p.getName(),null);
        if(turret != null){
            int distance = (int) k.distance(p.getLocation(),turret.getLocation());
            int time;
            if(distance <= 5){
                time = 2;
            }else if(distance <= 10){
                time = 5;
            }else if(distance <= 20){
                time = 10;
            }else if(distance <= 30){
                time = 20;
            }else time = 30;
            changeEnergy(p,10,-1);
            recoverEnergy(p,10,time);
            playerTurret.remove(p.getName());
            turretPlayer.remove(turret);
            turret.remove();
            w.playSound(turret.getLocation(),Sound.BLOCK_SNOW_BREAK,1,1);
            w.playSound(turret.getLocation(),Sound.BLOCK_SNOW_BREAK,1,1);
            w.playSound(turret.getLocation(),Sound.ENTITY_ARMOR_STAND_BREAK,1,1);
            w.spawnParticle(Particle.BLOCK_CRACK,turret.getLocation(),100,0.5,1.5,0.5,Bukkit.createBlockData(Material.SNOW_BLOCK));
        }
    }
    public void gravityDevice(Player p,int id) {
        World w = p.getWorld();
        double energy = getEnergy(p.getName(), id);
        HashSet<Integer>skillSet = playerUsingSkill.getOrDefault(p.getName(),new HashSet<>());
        if(skillSet.contains(id)){
            w.playSound(p.getLocation(),Sound.BLOCK_STEM_BREAK,1,1);
            Bukkit.getPluginManager().callEvent(new StopSkillEvent(p,id));
            skillSet.remove(id);
        }else {
            if(energy >= 0.3){
                consumeEnergy(p,id,10);
                skillSet.add(id);
                double radius = 5;
                BukkitRunnable gravity = new BukkitRunnable() {
                    int count = 0;
                    @Override
                    public void run() {
                        if(!skillSet.contains(id) || p.getGameMode() == GameMode.SPECTATOR){
                            this.cancel();
                            return;
                        }
                        if(count % 4 == 0) {
                            w.playSound(p.getLocation(), Sound.BLOCK_STEM_PLACE, 1, 1);
                        }
                        count += 1;
                        for(Entity e : p.getNearbyEntities(radius,radius,radius)){
                            if(e instanceof Villager)continue;
                            if(k.distance(e.getLocation(),p.getLocation()) > radius)continue;
                            if(e instanceof Player player){
                                if(player.getGameMode() == GameMode.SPECTATOR)continue;
                                if(gameStatus.isTeamMate(p,player))continue;
                            }
                            if(!(e instanceof LivingEntity) || count % 4 == 0) {
                                if(e instanceof LivingEntity l){
                                    if(p.isSneaking()){
                                        w.playSound(l.getLocation(),Sound.BLOCK_AMETHYST_BLOCK_BREAK,1,1);
                                        l.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,20,6));
                                        l.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,40,0));
                                    }else {
                                        w.playSound(l.getLocation(),Sound.BLOCK_NETHER_BRICKS_BREAK,1,1);
                                        l.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,20,2));
                                        k.breakSquareBlock(p,l.getLocation(),1,1,1,0);
                                    }
                                }
                                Vector down = new Vector(0, -1, 0);
                                if(p.isSneaking()){
                                    down.multiply(-1);
                                }
                                e.setVelocity(down);
                            }
                        }
                        Color c;
                        if(p.isSneaking()){
                            c = Color.WHITE;
                        }else {
                            c = Color.BLACK;
                        }
                        Particle.DustOptions dust = new Particle.DustOptions(c,1f);
                        double padX = p.getLocation().getX();
                        double padY = p.getLocation().getY();
                        double padZ = p.getLocation().getZ();
                        double i = Math.PI;
                        for (int j = 0; j <= 100; j++) {
                            double x = padX + (radius * Math.sin(radius * i + 0.5 * j));
                            double z = padZ + (radius * Math.cos(radius * i + 0.5 * j));
                            Location areaP = new Location(w, x, padY, z);
                            w.spawnParticle(Particle.REDSTONE, areaP, 1,dust);
                        }
                        w.spawnParticle(Particle.REDSTONE,p.getLocation(),100,radius / 2,radius / 2,radius / 2,dust);

                    }
                };
                gravity.runTaskTimer(plugin,0L,5L);
            }
        }
        playerUsingSkill.put(p.getName(),skillSet);
    }
}

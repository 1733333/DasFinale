package Listeners;

import Universal.GameStatus;
import Universal.Items;
import Universal.Kits;
import Universal.PlayerStats;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;

public class WeaponListener implements Listener {
    JavaPlugin plugin;
    Kits k = Kits.getInstance();
    Items items = Items.getInstance();
    PlayerStats playerStats = PlayerStats.INSTANCE;
    GameStatus gameStatus = GameStatus.getInstance();
    HashSet<Player>isBlocking = new HashSet<>();
    HashSet<Player>stabbed = new HashSet<>();
    HashSet<Player>clearCoolDown = new HashSet<>();
    HashMap<Player,BukkitRunnable>playerTask = new HashMap<>();
    Random random = new Random();
    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    String[]weapons = {
        "§f长剑",
        "§f匕首",
        "§f反曲弓",
        "§f防暴盾",
        "§f双刀",
        "§f榴弹弩",
        "§f大锤",
        "§f长矛",
        "§f大口径火铳",
        "§f飞刀",
        "§f羊驼左轮",
        "§f喷火器",
        "§fBug剑",
        "§f故障锏",
        "§f漏洞锚",
        "§fRB-30",
        "§fDB-15",
        "§fPASS-12",
    };

    public int getWeapon(String s) {
        for (int i = 0; i < weapons.length; i++) {
            if (s.equals(weapons[i])) {
                return i;
            }
        }
        return -1;
    }
    @EventHandler
    public void playerAttack(EntityDamageByEntityEvent damageEvent) {
        Entity damaged = damageEvent.getEntity();
        Entity attacker = damageEvent.getDamager();
        double damage = damageEvent.getDamage();
        World w = damaged.getWorld();
        if (attacker instanceof Player p) {
            if (damaged instanceof LivingEntity l) {
                ItemStack hand = p.getInventory().getItemInMainHand();
                if (hand.getType() != Material.AIR) {
                    String tag = k.getLore(hand);
                    int weapon = getWeapon(tag);
                    if (weapon == 1) {
                        if(damageEvent.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK)return;
                        if(damage > 0) stabbed.add(p);
                        boolean jump = false, bStab = false;
                        Vector stabVec = l.getEyeLocation().getDirection();
                        double distance = k.distance(l.getLocation(), p.getLocation());
                        Vector lVec = l.getEyeLocation().toVector();
                        Vector pVec = p.getEyeLocation().toVector();
                        Vector sVec = lVec.clone().subtract(pVec);
                        double angle = k.angle(stabVec, sVec);
                        if (distance <= 4 && angle > 0.5) {
                            bStab = true;
                            damage *= 2;
                            w.playSound(l.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
                            w.playSound(l.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
                            stabbed.remove(p);
                            clearCoolDown.add(p);
                        }
                        if (attacker.getFallDistance() >= 5) {
                            jump = true;
                            damage *= 2;
                            w.playSound(l.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
                            w.playSound(l.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 2);
                            stabbed.remove(p);
                            clearCoolDown.add(p);
                        }
                        if (bStab && jump) {
                            damage *= 2;
                            Bukkit.broadcastMessage(ChatColor.AQUA + p.getName() + "从高处落下，并且背刺了" +
                                    l.getName() + ChatColor.AQUA +  "！WOW！");
                            w.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
                            stabbed.remove(p);
                            clearCoolDown.add(p);
                        }
                        damageEvent.setDamage(damage);
                    }
                }
            }
        }
        if (damaged instanceof Player p) {
            ItemStack hand = p.getInventory().getItemInMainHand();
            if (hand.getType() != Material.AIR) {
                String tag = k.getLore(hand);
                int weapon = getWeapon(tag);
                if (weapon == 4) {
                    if(damage > 6) {
                        if (isBlocking.contains(p)) {
                            if (attacker instanceof Player p1) {
                                if (!isBlocking.contains(p1)) {
                                    p1.damage(damage, p1);
                                }
                            }
                            damageEvent.setDamage(damage / 3);
                            w.playSound(p.getEyeLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
                            p.setCooldown(hand.getType(), 120);
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void shootBow(EntityShootBowEvent shootBowEvent){
        Entity entity = shootBowEvent.getEntity();
        World w = entity.getWorld();
        float force = shootBowEvent.getForce();
        if(entity instanceof Player p) {
            ItemStack hand = p.getInventory().getItemInMainHand();
            if (hand.getType() == Material.AIR) return;
            String tag = k.getLore(hand);
            int weapon = getWeapon(tag);
            Inventory inv = p.getInventory();
            inv.remove(Material.ARROW);
            for(int i = 9;i < inv.getSize();i ++){
                ItemStack item = inv.getItem(i);
                if(item == null || item.getType() == Material.AIR) {
                    inv.setItem(i, new ItemStack(Material.ARROW));
                    break;
                }
            }
            if (weapon == 2) {
                w.playSound(p.getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 1, 1);
                Location shootLoc = p.getEyeLocation();
                Vector shootVec = shootLoc.getDirection();
                Arrow arrow = w.spawnArrow(shootLoc, shootVec, 4 * force, 0);
                arrow.setShooter(p);
                arrow.setDamage(4.5);
                arrow.setKnockbackStrength(0);
                arrow.setColor(Color.WHITE);
                arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
                arrow.setTicksLived(1200);
                shootBowEvent.setCancelled(true);
            }
            if (weapon == 5) {
                grenadeCrossbow(p);
                shootBowEvent.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void playerInteract(PlayerInteractEvent interactEvent) {
        Action action = interactEvent.getAction();
        Player p = interactEvent.getPlayer();
        if(p.getGameMode() == GameMode.SPECTATOR)return;
        ItemStack hand = p.getInventory().getItemInMainHand();
        ItemStack offHand = p.getInventory().getItemInOffHand();
        if (action.equals(Action.RIGHT_CLICK_AIR)
                || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            if (hand.getType() != Material.AIR) {
                String tag = k.getLore(hand);
                if (action == Action.RIGHT_CLICK_BLOCK) {
                    Block block = interactEvent.getClickedBlock();
                    if (block != null) {
                        if (block.getType().name().contains("SIGN")) return;
                    }
                }
                if (playerStats.isCarrying(p)) {
                    interactEvent.setCancelled(true);
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            TextComponent.fromLegacyText(ChatColor.RED + "正在拿着东西，需要丢掉东西才能继续操作"));
                    return;
                }
                switch (getWeapon(tag)) {
                    case 0:
                        thrust(p, hand);
                        interactEvent.setCancelled(true);
                        break;
                    case 1:
                        disappear(p, hand);
                        interactEvent.setCancelled(true);
                        break;
                    case 3:
                        block(p);
                        break;
                    case 4:
                        parry(p, hand);
                        interactEvent.setCancelled(true);
                        break;
                    case 6:
                        swingHammer(p, hand, interactEvent.getClickedBlock());
                        interactEvent.setCancelled(true);
                        break;
                    case 7:
                        if (p.isSneaking()) {
                            sweep(p, hand);
                        } else {
                            stab(p, hand);
                        }
                        interactEvent.setCancelled(true);
                        break;
                    case 8:
                        buss(p, hand);
                        interactEvent.setCancelled(true);
                        break;
                    case 9:
                        throwingKnives(p, hand);
                        interactEvent.setCancelled(true);
                        break;
                    case 10:
                        revolver(p, hand);
                        break;
                    case 11:
                        flameThrower(p, hand);
                        interactEvent.setCancelled(true);
                        break;
                    case 12:
                        whip(p,hand);
                        interactEvent.setCancelled(true);
                        break;
                    case 13:
                        glitchSwing(p,hand);
                        interactEvent.setCancelled(true);
                        break;
                    case 14:
                        smashGround(p,hand);
                        interactEvent.setCancelled(true);
                        break;
                    case 16:
                        dragonBreath(p,hand);
                        interactEvent.setCancelled(true);
                    case 17:
                        slug(p,hand);
                        interactEvent.setCancelled(true);
                }
                switch (tag) {
                    case "§7MasterSword":
                        masterSword(p, hand);
                        interactEvent.setCancelled(true);
                        break;
                    case "§7GhostFlameDagger":
                        majimaDagger(p, hand);
                        interactEvent.setCancelled(true);
                        break;
                    case "§7Katana":
                        katana(p, hand);
                        interactEvent.setCancelled(true);
                        break;
                    case "§f“特制”雪球":
                        snowball(p,hand);
                        interactEvent.setCancelled(true);
                        break;
                    case "§f细雪墙":
                        snowNadeWall(p,hand,15);
                        interactEvent.setCancelled(true);
                        break;
                    case "§f漂浮手里剑Pro":
                        shuriStar(p,hand,15);
                        interactEvent.setCancelled(true);
                        break;
                }
            }
            if (offHand.getType() != Material.AIR) {
                String tag = k.getLore(offHand);
                switch (getWeapon(tag)) {
                    case 3:
                        block(p);
                        break;
                    case 12:
                        interactEvent.setCancelled(true);
                        break;
                }
                switch (tag) {
                    case "§f“特制”雪球":
                        snowball(p,hand);
                        interactEvent.setCancelled(true);
                        break;
                }
            }
        }
    }
    @EventHandler
    public void playerSneak(PlayerToggleSneakEvent sneakEvent){
        Player p = sneakEvent.getPlayer();
        if(p.getGameMode() == GameMode.SPECTATOR)return;
        ItemStack hand = p.getInventory().getItemInMainHand();
        ItemStack offHand = p.getInventory().getItemInOffHand();
        if(hand.getType() != Material.AIR){
            String tag = k.getLore(hand);
            switch (getWeapon(tag)) {
                case 15 -> sniper(p,hand);
            }
        }
        if(offHand.getType() != Material.AIR) {
            String tag = k.getLore(offHand);
            switch (getWeapon(tag)) {
                case 3 -> shieldBash(p, offHand);
            }
        }
    }
    public void thrust(Player p,ItemStack hand){
        World w = p.getWorld();
        if(p.getCooldown(hand.getType()) == 0) {
            p.setCooldown(hand.getType(),100);
            Location eyeLoc = p.getEyeLocation();
            Vector eyeVec = eyeLoc.getDirection();
            Vector dash = new Vector(eyeVec.getX(), 0.1, eyeVec.getZ());
            if(p.isOnGround()){
                p.setVelocity(dash.multiply(2));
            }else {
                p.setVelocity(dash.multiply(1));
            }
            w.playSound(eyeLoc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 0.8f);
            BukkitRunnable stab = new BukkitRunnable() {
                HashSet<LivingEntity>stabbed = new HashSet<>();
                boolean broadcast = false;
                int count = 0;
                @Override
                public void run() {
                    if(count >= 10){
                        this.cancel();
                    }
                    Vector stabVec = p.getEyeLocation().getDirection();
                    List<Entity>entities = p.getNearbyEntities(3,3,3);
                    for(Entity e : entities){
                        if(e.getName().contains("传送门")  || e.getName().contains("滑索"))continue;
                        if(e instanceof ArmorStand || e instanceof Villager)continue;
                        if(e instanceof LivingEntity l){
                            if(stabbed.contains(l))continue;
                            double distance = k.distance(l.getLocation(),p.getLocation());
                            Vector lVec = l.getEyeLocation().toVector();
                            Vector pVec = p.getEyeLocation().toVector();
                            Vector sVec = lVec.clone().subtract(pVec);
                            double angle = k.angle(stabVec,sVec);
                            if(distance <= 3 && angle > 0.85){
                                l.damage(18,p);
                                stabbed.add(l);
                                w.playSound(l.getEyeLocation(),Sound.ENTITY_PLAYER_ATTACK_CRIT,1,1);
                                w.spawnParticle(Particle.CRIT,p.getEyeLocation(),50,0.5,0.5,0.5);
                            }
                        }
                    }
                    if(stabbed.size() >= 3 && !broadcast){
                        Bukkit.broadcastMessage(org.bukkit.ChatColor.AQUA + p.getName() + "用长剑一次性刺穿了多个玩家！WOW！");
                        w.playSound(p.getLocation(),Sound.UI_TOAST_CHALLENGE_COMPLETE,1,1);
                        broadcast = true;
                    }
                    count += 1;
                }
            };
            stab.runTaskTimer(plugin,0L,2L);
        }
    }
    public void disappear(Player p,ItemStack hand){
        stabbed.remove(p);
        World w = p.getWorld();
        RayTraceResult result = w.rayTraceBlocks(p.getEyeLocation(),new Vector(0,1,0),6);
        if(result == null){
            if(p.getCooldown(hand.getType()) == 0) {
                p.setCooldown(hand.getType(), 40);
                Location loc = p.getLocation();
                w.spawnParticle(Particle.EXPLOSION_LARGE,loc,1);
                w.spawnParticle(Particle.SMOKE_LARGE,loc,20,0,0,0,0.1);
                w.playSound(loc,Sound.ENTITY_EVOKER_PREPARE_SUMMON,1,1);
                w.playSound(loc,Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
                p.teleport(loc.add(0,6,0));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,10,0));
                BukkitRunnable land = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(((Entity)p).isOnGround()){
                            BukkitRunnable coolDown = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if(stabbed.contains(p)){
                                        p.setCooldown(hand.getType(), 200);
                                        p.playSound(p.getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
                                        stabbed.remove(p);
                                    }else {
                                        if (clearCoolDown.contains(p)) {
                                            p.setCooldown(hand.getType(), 0);
                                            clearCoolDown.remove(p);
                                        }else p.setCooldown(hand.getType(), 150);
                                    }
                                }
                            };
                            coolDown.runTaskLater(plugin,10L);
                            this.cancel();
                        }
                    }
                };
                land.runTaskTimer(plugin,10L,2L);
            }
        }else {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    TextComponent.fromLegacyText(ChatColor.RED + "头顶住了，无法跳跃"));
        }
    }
    public void shieldBash(Player p, ItemStack hand){
        World w = p.getWorld();
        if(p.getCooldown(hand.getType()) == 0) {
            if(p.isBlocking()) {
                ItemStack mainHand = p.getInventory().getItemInMainHand();
                ItemStack offHand = p.getInventory().getItemInOffHand();
                p.getInventory().setItemInMainHand(offHand);
                p.getInventory().setItemInOffHand(mainHand);
                p.setCooldown(hand.getType(), 100);
                Location eyeLoc = p.getEyeLocation();
                Vector eyeVec = eyeLoc.getDirection();
                Vector dash = new Vector(eyeVec.getX(), 0.1, eyeVec.getZ());
                if (p.isOnGround()) {
                    p.setVelocity(dash.multiply(2));
                } else {
                    p.setVelocity(dash.multiply(1));
                }
                BukkitRunnable rush = new BukkitRunnable() {
                    int count = 0;
                    @Override
                    public void run() {
                        Vector forward = p.getEyeLocation().getDirection();
                        Location loc = p.getEyeLocation().add(new Vector(forward.getX(),0,forward.getZ()));
                        k.breakSquareGlass(loc,1);
                        boolean hit = false;
                        List<Entity> entities = p.getNearbyEntities(2, 2, 2);
                        for (Entity e : entities) {
                            if(k.distance(e.getLocation(),p.getLocation()) > 2)continue;
                            if (e instanceof LivingEntity l) {
                                double distance = k.distance(l.getLocation(), p.getLocation());
                                if (distance <= 2) {
                                    p.setVelocity(new Vector(0,0,0));
                                    l.damage(15, p);
                                    k.knockBack(l,p.getLocation(),2);
                                    w.playSound(l.getEyeLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);
                                    w.spawnParticle(Particle.CRIT, p.getEyeLocation(), 50, 1, 1, 1);
                                    hit = true;
                                    break;
                                }
                            }
                        }
                        if (count >= 10 || hit) {
                            if(mainHand.getType() != Material.AIR) {
                                Item item = w.dropItem(p.getLocation(), mainHand);
                                item.setPickupDelay(0);
                                item.setOwner(p.getUniqueId());
                                item.setInvulnerable(true);
                            }
                            p.getInventory().remove(offHand.getType());
                            p.getInventory().setItemInOffHand(offHand);
                            this.cancel();
                        }
                        count += 1;
                    }
                };
                rush.runTaskTimer(plugin, 2L, 1L);
            }else {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(ChatColor.RED + "需要格挡才能使用盾击"));
            }
        }
    }
    public void block(Player p) {
        BukkitRunnable blocking = new BukkitRunnable() {
            @Override
            public void run() {
                if (p.isHandRaised() || p.isBlocking()) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,20,0));
                }else this.cancel();
            }
        };
        blocking.runTaskTimer(plugin, 0L, 10L);
    }
    public void parry(Player p,ItemStack hand) {
        World w = p.getWorld();
        if(p.getCooldown(hand.getType()) == 0) {
            BukkitRunnable task = playerTask.getOrDefault(p, null);
            isBlocking.add(p);
            if (task == null) {
                w.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,15,0));
                BukkitRunnable blocking = new BukkitRunnable() {
                    int count = 0;

                    @Override
                    public void run() {
                        if (count >= 10) {
                            this.cancel();
                            playerTask.remove(p);
                            isBlocking.remove(p);
                            return;
                        }
                        count += 1;
                        Vector stabVec = p.getEyeLocation().getDirection();
                        double radius = 4;
                        List<Entity> entities = p.getNearbyEntities(radius, radius, radius);
                        for (Entity e : entities) {
                            if (e instanceof Projectile l) {
                                if(k.distance(e.getLocation(),p.getLocation()) > radius)continue;
                                if (l.getShooter() != p) {
                                    if (l.getCustomName() != null) continue;
                                    Vector lVec = l.getLocation().toVector();
                                    Vector pVec = p.getEyeLocation().toVector();
                                    Vector sVec = lVec.clone().subtract(pVec);
                                    double angle = k.angle(stabVec, sVec);
                                    if (angle > 0.93) {
                                        Vector speed = l.getVelocity();
                                        w.playSound(p.getEyeLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);
                                        w.playSound(p.getEyeLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 2.5f - random.nextFloat());
                                        l.setShooter(p);
                                        l.setVelocity(stabVec.multiply(speed.length()));
                                    }
                                }
                            }
                        }
                    }
                };
                blocking.runTaskTimer(plugin, 0L, 1L);
                playerTask.put(p, blocking);
            }
        }
    }
    public void grenadeCrossbow(Player p){
        World w = p.getWorld();
        w.playSound(p.getLocation(), Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, 1, 0.8f);
        w.playSound(p.getLocation(), Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, 1, 0.8f);
        w.playSound(p.getLocation(), Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, 1, 0.8f);
        w.playSound(p.getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 1, 1);
        Location shootLoc = p.getEyeLocation();
        Vector shootVec = shootLoc.getDirection();
        Arrow arrow = w.spawnArrow(shootLoc,shootVec,2,0);
        arrow.setShooter(p);
        arrow.setDamage(8);
        BukkitRunnable hit = new BukkitRunnable() {
            @Override
            public void run() {
                w.spawnParticle(Particle.CRIT,arrow.getLocation(),0);
                if(arrow.isDead() || arrow.isInBlock()){
                    this.cancel();
                    k.explode(p,arrow,16,2,3);
                    w.spawnParticle(Particle.EXPLOSION_HUGE,arrow.getLocation(),1);
                    w.playSound(arrow.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,2,1);
                }
            }
        };
        hit.runTaskTimer(plugin,0L,2L);
    }
    public void swingHammer(Player p, ItemStack hand, Block block){
        World w = p.getWorld();
        if(p.getCooldown(hand.getType()) == 0) {
            p.setCooldown(hand.getType(), 80);
            Location eyeLoc = p.getEyeLocation();
            Vector eyeVec = eyeLoc.getDirection();
            w.playSound(p.getLocation(),Sound.ENTITY_PLAYER_ATTACK_SWEEP,1,0.7f);
            w.spawnParticle(Particle.SWEEP_ATTACK,eyeLoc.add(eyeVec),1);
            Location breakLoc;
            if(block != null){
                breakLoc = block.getLocation();
            }else {
                breakLoc = eyeLoc.clone().add(eyeVec);
            }
            k.breakSquareBlock(p,breakLoc,1,1,1,0);
            Vector stabVec = p.getEyeLocation().getDirection();
            List<Entity>entities = p.getNearbyEntities(4,4,4);
            for(Entity e : entities){
                if(e.getName().contains("传送门")  || e.getName().contains("滑索"))continue;
                if(e instanceof LivingEntity l){
                    double distance = k.distance(l.getLocation(),p.getLocation());
                    double ballDistance = k.distance(eyeLoc.clone().add(stabVec),l.getLocation());
                    Vector lVec = l.getEyeLocation().toVector();
                    Vector pVec = p.getEyeLocation().toVector();
                    Vector sVec = lVec.clone().subtract(pVec);
                    double angle = k.angle(stabVec,sVec);
                    if(distance <= 4 && angle > 0.75 || ballDistance <= 2){
                        l.damage(12,p);
                        w.playSound(l.getEyeLocation(),Sound.ENTITY_PLAYER_ATTACK_CRIT,1,1);
                        k.knockBack(l,p.getEyeLocation(),2);
                    }
                }
            }
        }
    }
    public void stab(Player p,ItemStack hand){
        World w = p.getWorld();
        if(p.getCooldown(hand.getType()) == 0) {
            p.setCooldown(hand.getType(), 50);
            w.playSound(p.getLocation(), Sound.ITEM_TRIDENT_THROW, 1, 1);
            Location pLoc = p.getEyeLocation();
            Vector stabVec = pLoc.getDirection();
            List<Entity>entities = p.getNearbyEntities(6,6,6);
            for (int i = 0; i < 6; i++) {
                w.spawnParticle(Particle.CRIT, pLoc, 10,0,0,0,0.1);
                pLoc.add(stabVec.clone());
            }
            for(Entity e : entities){
                if(e.getName().contains("传送门")  || e.getName().contains("滑索"))continue;
                if(e instanceof LivingEntity l) {
                    double distance = k.distance(l.getLocation(), p.getLocation());
                    double ballDistance = k.distance(pLoc.clone().add(stabVec),l.getLocation());
                    Vector lVec = l.getEyeLocation().toVector();
                    Vector pVec = p.getEyeLocation().toVector();
                    Vector sVec = lVec.clone().subtract(pVec);
                    double angle = k.angle(stabVec, sVec);
                    if (distance <= 6 && angle > 0.90 || ballDistance <= 2) {
                        l.damage(7, p);
                        w.playSound(l.getEyeLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
                    }
                }
            }
        }
    }
    public void sweep(Player p,ItemStack hand){
        World w = p.getWorld();
        if(p.getCooldown(hand.getType()) == 0) {
            p.setCooldown(hand.getType(), 200);
            BukkitRunnable sweep = new BukkitRunnable() {
                int count = 0;
                @Override
                public void run() {
                    if(count > 2 || p.getGameMode() == GameMode.SPECTATOR){
                        this.cancel();
                        return;
                    }
                    w.playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 0.7f + (0.1f * count));
                    double padX = p.getEyeLocation().getX();
                    double padY = p.getEyeLocation().getY();
                    double padZ = p.getEyeLocation().getZ();
                    double i = Math.PI;
                    for (int j = 0; j <= 100; j++) {
                        double x = padX + ((2 + count) * Math.sin((2 + count) * i + 0.5 * j));
                        double z = padZ + ((2 + count) * Math.cos((2 + count) * i + 0.5 * j));
                        Location areaP = new Location(w, x, padY, z);
                        w.spawnParticle(Particle.SWEEP_ATTACK,areaP,1);
                    }
                    List<Entity>entities = p.getNearbyEntities(3 + count,3 + count,3 + count);
                    for(Entity e : entities){
                        if(e.getName().contains("传送门")  || e.getName().contains("滑索"))continue;
                        if(k.distance(e.getLocation(),p.getLocation()) > 3 + count)continue;
                        if(e instanceof LivingEntity l) {
                            l.damage(7 + count, p);
                            w.playSound(l.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
                            if(count > 0) {
                                k.knockBack(l, p.getLocation(), count);
                            }
                        }
                    }
                    count += 1;
                }
            };
            sweep.runTaskTimer(plugin,0L,10L);
        }
    }
    public void buss(Player p,ItemStack hand) {
        World w = p.getWorld();
        Location shootLoc = p.getEyeLocation();
        if (p.getCooldown(hand.getType()) == 0) {
            w.spawnParticle(Particle.SMOKE_NORMAL,shootLoc,50,0.3,0.3,0.3,0.1);
            w.spawnParticle(Particle.FLAME,shootLoc,50,0.3,0.3,0.3,0.1);
            w.spawnParticle(Particle.EXPLOSION_LARGE,shootLoc,1);
            w.playSound(shootLoc,Sound.ENTITY_GENERIC_EXPLODE,2,1);
            Vector stabVec = p.getEyeLocation().getDirection();
            p.setVelocity(stabVec.clone().multiply(-1.2));
            p.setCooldown(hand.getType(), 120);
            for (int i = 0; i < 20; i++) {
                double xSpread = random.nextDouble() - random.nextDouble();
                double zSpread = random.nextDouble() - random.nextDouble();
                Vector spread = new Vector(xSpread, 0, zSpread);
                Vector shootVec = spread.normalize().multiply(0.5);
                shootVec.add(shootLoc.getDirection());
                shootVec.multiply(random.nextDouble());
                Snowball ball = (Snowball) w.spawnEntity(shootLoc, EntityType.SNOWBALL);
                ball.setItem(new ItemStack(Material.COAL_BLOCK));
                ball.setVelocity(shootVec);
                ball.setShooter(p);
            }
            int range = 6;
            List<Entity> entities = p.getNearbyEntities(range, range, range);
            for (Entity e : entities) {
                if (e instanceof LivingEntity l) {
                    double distance = k.distance(l.getLocation(), p.getLocation());
                    double ballDistance = k.distance(p.getEyeLocation().add(stabVec),l.getLocation());
                    Vector lVec = l.getEyeLocation().toVector();
                    Vector pVec = p.getEyeLocation().toVector();
                    Vector sVec = lVec.clone().subtract(pVec);
                    double angle = k.angle(stabVec, sVec);
                    if (distance <= range && angle > 0.78 || ballDistance <= 2) {
                        double amp = 3;
                        if(distance > 2){
                            distance -= 2;
                        }else {
                            amp = 0;
                        }
                        l.damage(22 - (int)distance * amp, p);
                        w.playSound(l.getEyeLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
                        k.knockBack(l,p.getLocation(),1);
                    }
                }
            }
            Location loc = p.getEyeLocation().add(stabVec.multiply(3));
            k.breakBallSnow(loc,2);
        }
    }
    public void throwingKnives(Player p,ItemStack hand){
        World w = p.getWorld();
        if(p.getCooldown(hand.getType()) == 0) {
            p.setCooldown(hand.getType(), 15);
            if(p.isSneaking()){
                for(int i = 0;i < 2;i++) {
                    w.playSound(p.getLocation(), Sound.ITEM_TRIDENT_THROW, 1, 1.2f);
                    Location eyeLoc = p.getEyeLocation();
                    Vector eyeVec = eyeLoc.getDirection();
                    Arrow knife = w.spawnArrow(eyeLoc, eyeVec, 2f, 1);
                    knife.setShooter(p);
                    knife.setDamage(6);
                    knife.setTicksLived(1200);
                    knife.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
                }
            }else {
                BukkitRunnable doubleThrow = new BukkitRunnable() {
                    int count = 0;
                    @Override
                    public void run() {
                        if(count > 1 || p.getGameMode() == GameMode.SPECTATOR){
                            this.cancel();
                            return;
                        }
                        w.playSound(p.getLocation(),Sound.ITEM_TRIDENT_THROW,1,1.4f);
                        Location eyeLoc = p.getEyeLocation();
                        Vector eyeVec = eyeLoc.getDirection();
                        Arrow knife = w.spawnArrow(eyeLoc,eyeVec,1.5f,2);
                        knife.setShooter(p);
                        knife.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
                        knife.setDamage(6);
                        knife.setTicksLived(1200);
                        count += 1;
                    }
                };
                doubleThrow.runTaskTimer(plugin,0L,5L);
            }
        }
    }
    public void revolver(Player p,ItemStack hand){
        World w = p.getWorld();
        ItemMeta handMeta = hand.getItemMeta();
        int itemID = items.getItemID(handMeta.getDisplayName());
        int range = 39;
        if(p.getCooldown(hand.getType()) == 0) {
            int ammo = playerStats.getItemAmmo(p, itemID);
            if (ammo <= 0) {
                ammo = 6;
            }
            if (ammo == 1 || p.isSneaking()) {
                if (ammo == 6) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            TextComponent.fromLegacyText(ChatColor.YELLOW + "弹药已满，无需装填"));
                } else {
                    if(!p.isSneaking())ammo = 0;
                    int reload = 6 - ammo;
                    p.setCooldown(hand.getType(), reload * 16);
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            TextComponent.fromLegacyText(ChatColor.YELLOW + "重新装填中"));
                    playerStats.setItemAmmo(p, itemID, 6);
                    w.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                    BukkitRunnable timer = new BukkitRunnable() {
                        int count = -1;
                        @Override
                        public void run() {
                            if(count >= reload){
                                this.cancel();
                                return;
                            }
                            if(count == -1){
                                w.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
                                w.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
                            }else {
                                w.playSound(p.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1, 1);
                                w.playSound(p.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1, 1);
                            }
                            count += 1;
                        }
                    };
                    timer.runTaskTimer(plugin,(reload - 1) * 8L,8L);
                }
                if (p.isSneaking()) return;
            } else {
                p.setCooldown(hand.getType(), 15);
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(ChatColor.YELLOW + "剩余弹药：" + (ammo - 1)));
            }
            ammo -= 1;
            playerStats.setItemAmmo(p, itemID, ammo);
            Location shootLoc = p.getEyeLocation();
            Vector shootVec = shootLoc.getDirection();
            RayTraceResult result = w.rayTraceEntities(shootLoc.add(shootVec), shootVec, range);
            w.playSound(p.getLocation(),Sound.ITEM_CROSSBOW_SHOOT,1,1);
            w.playSound(p.getLocation(),Sound.ITEM_CROSSBOW_SHOOT,1,1);
            if(random.nextInt(10) == 0){
                w.playSound(p.getLocation(),Sound.ENTITY_LLAMA_AMBIENT,1,1);
            }
            Location pLoc = shootLoc.clone();
            for (int i = 0; i < range; i++) {
                w.spawnParticle(Particle.SPELL_INSTANT, pLoc, 0);
                pLoc.add(shootVec.clone());
            }
            if(result != null){
                Entity e = result.getHitEntity();
                if(e instanceof LivingEntity l){
                    if(e != p) {
                        l.damage(18, p);
                        w.playSound(l.getLocation(), Sound.ITEM_TRIDENT_HIT, 1, 1);
                        p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 0.5f);
                        p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT, 1, 1);
                    }
                }
            }
            Vector speed = p.getVelocity();
            Location playerLoc = p.getLocation();
            playerLoc.setPitch(playerLoc.getPitch() - 5);
            p.teleport(playerLoc);
            p.setVelocity(speed);
            w.spawnParticle(Particle.EXPLOSION_NORMAL,shootLoc.add(shootVec),5,0,0,0,0.2);
            if(ammo > 0) {
                BukkitRunnable click = new BukkitRunnable() {
                    @Override
                    public void run() {
                        w.playSound(p.getLocation(), Sound.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, 1, 1);
                        w.playSound(p.getLocation(), Sound.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, 1, 1);
                    }
                };
                click.runTaskLater(plugin, 10L);
            }
        }
    }
    public void flameThrower(Player p,ItemStack hand){
        World w = p.getWorld();
        ItemMeta handMeta = hand.getItemMeta();
        int itemID = items.getItemID(handMeta.getDisplayName());
        if(p.getCooldown(hand.getType()) == 0) {
            int ammo = playerStats.getItemAmmo(p, itemID);
            if (ammo < 0) {
                ammo = 30;
            }
            if (ammo == 0 || p.isSneaking()) {
                if (ammo == 30) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            TextComponent.fromLegacyText(ChatColor.GOLD + "燃料已满，无需装填"));
                } else {
                    p.setCooldown(hand.getType(), 150);
                    w.playSound(p.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1);
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            TextComponent.fromLegacyText(ChatColor.GOLD + "重新装填中"));
                    playerStats.setItemAmmo(p, itemID, 30);
                }
                if (p.isSneaking()) return;
            } else {
                p.setCooldown(hand.getType(), 12);
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(ChatColor.GOLD + "剩余燃料：" + (ammo - 1)));
                ammo -= 1;
                playerStats.setItemAmmo(p, itemID, ammo);
            }
            Location shootLoc = p.getEyeLocation();
            Vector stabVec = shootLoc.getDirection();
            int range = 6;
            Location pLoc = p.getEyeLocation().add(stabVec).clone();
            BukkitRunnable particle = new BukkitRunnable() {
                int count = 0;
                @Override
                public void run() {
                    boolean hitBlock = false;
                    Block block = w.getBlockAt(pLoc);
                    if(block.getType() != Material.LIGHT
                            && block.getType() != Material.AIR) {
                        hitBlock = true;
                    }
                    if(count >= range || hitBlock){
                        w.spawnParticle(Particle.LAVA,pLoc,10);
                        int x = pLoc.getBlockX();
                        int y = pLoc.getBlockY();
                        int z = pLoc.getBlockZ();
                        for(int a = -1;a <= 1;a++) {
                            for (int b = -1; b <= 1; b++) {
                                for (int c = -1; c <= 1; c++) {
                                    if (a * a + b * b + c * c <= 1) {
                                        Location newLoc = new Location(w, x + a, y + b, z + c);
                                        Block b1 = w.getBlockAt(newLoc);
                                        if (b1.getType() == Material.AIR
                                                || b1.getType() == Material.LIGHT
                                                || b1.getType() == Material.POWDER_SNOW) {
                                            b1.setType(Material.FIRE);
                                        }
                                    }
                                }
                            }
                        }
                        this.cancel();
                        return;
                    }
                    w.spawnParticle(Particle.FLAME,pLoc,10,0.3,0.1,0.3,0.01);
                    pLoc.add(stabVec);
                    count += 1;
                }
            };
            particle.runTaskTimer(plugin,0L,1L);
            List<Entity> entities = p.getNearbyEntities(range, range, range);
            for (Entity e : entities) {
                if(e instanceof Player p1){
                    if(gameStatus.isTeamMate(p,p1))continue;
                    if(p1.getGameMode() == GameMode.SPECTATOR)continue;
                }
                if(e instanceof AreaEffectCloud a){
                    if(a.getName().contains("毒气")){
                        a.remove();
                        w.playSound(a.getLocation(),Sound.ITEM_FIRECHARGE_USE,2,1);
                        w.spawnParticle(Particle.EXPLOSION_HUGE,a.getLocation(),1);
                        w.spawnParticle(Particle.FLAME,a.getLocation(),100,0,0,0,0.2);
                    }
                }
                if (e instanceof LivingEntity l) {
                    double distance = k.distance(l.getLocation(), p.getLocation());
                    double ballDistance = k.distance(p.getEyeLocation().add(stabVec.multiply(0.9)),l.getLocation());
                    Vector lVec = l.getEyeLocation().toVector();
                    Vector pVec = p.getEyeLocation().toVector();
                    Vector sVec = lVec.clone().subtract(pVec);
                    double angle = k.angle(stabVec, sVec);
                    if (distance <= range && angle > 0.95 || ballDistance < 2) {
                        Vector speed = l.getVelocity();
                        l.damage(4, p);
                        l.teleport(l.getLocation());
                        l.setVelocity(speed);
                        int fire = l.getFireTicks();
                        l.setFireTicks(fire + 40);
                        w.playSound(l.getEyeLocation(), Sound.ENTITY_PLAYER_HURT_ON_FIRE, 1, 1);
                    }
                }
            }
            w.playSound(shootLoc,Sound.ITEM_FIRECHARGE_USE,1,1);
        }
    }
    public void flameArrowThrower(Player p,ItemStack hand){
        World w = p.getWorld();
        ItemMeta handMeta = hand.getItemMeta();
        int itemID = items.getItemID(handMeta.getDisplayName());
        double radius = 2.5;
        if(p.getCooldown(hand.getType()) == 0) {
            int ammo = playerStats.getItemAmmo(p, itemID);
            if (ammo < 0) {
                ammo = 25;
            }
            if(ammo == 0 || p.isSneaking()){
                if(ammo == 25){
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            TextComponent.fromLegacyText(ChatColor.GOLD + "燃料已满，无需装填"));
                }else {
                    p.setCooldown(hand.getType(), 150);
                    w.playSound(p.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1);
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            TextComponent.fromLegacyText(ChatColor.GOLD + "重新装填中"));
                    playerStats.setItemAmmo(p,itemID,25);
                }
                if(p.isSneaking())return;
            }else {
                p.setCooldown(hand.getType(),10);
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(ChatColor.GOLD + "剩余燃料：" + (ammo - 1)));
                ammo -= 1;
                playerStats.setItemAmmo(p,itemID,ammo);
            }
            Location eyeLoc = p.getEyeLocation();
            Vector eyeVec = eyeLoc.getDirection();
            Arrow fireball = w.spawnArrow(eyeLoc,eyeVec,1,0);
            fireball.setColor(Color.ORANGE);
            fireball.setDamage(8);
            fireball.setShooter(p);
            fireball.setCustomName("火球");
            fireball.setGravity(false);
            w.playSound(p.getLocation(),Sound.ITEM_FIRECHARGE_USE,1,1);
            BukkitRunnable hit = new BukkitRunnable() {
                @Override
                public void run() {
                    w.spawnParticle(Particle.FLAME,fireball.getLocation(),0);
                    if(fireball.isDead() || fireball.getTicksLived() >= 6){
                        Location loc = fireball.getLocation();
                        int x = loc.getBlockX();
                        int y = loc.getBlockY();
                        int z = loc.getBlockZ();
                        for(int a = -1;a <= 1;a++){
                            for(int b = -1;b <= 1;b++){
                                for(int c = -1;c <=1;c++) {
                                    if (a * a + b * b + c * c <= 1) {
                                        Location newLoc = new Location(w, x + a, y + b, z + c);
                                        Block block = w.getBlockAt(newLoc);
                                        if (block.getType() == Material.AIR || block.getType() == Material.LIGHT) {
                                            block.setType(Material.FIRE);
                                        }
                                    }
                                }
                            }
                        }
                        w.playSound(fireball.getLocation(),Sound.ITEM_FIRECHARGE_USE,1,1);
                        for(Entity e : fireball.getNearbyEntities(radius,radius,radius)){
                            if(k.distance(fireball.getLocation(),e.getLocation()) > radius)continue;
                            if(e instanceof AreaEffectCloud a){
                                if(a.getName().contains("毒气")){
                                    a.remove();
                                    w.playSound(a.getLocation(),Sound.ITEM_FIRECHARGE_USE,2,1);
                                    w.spawnParticle(Particle.EXPLOSION_HUGE,a.getLocation(),1);
                                    w.spawnParticle(Particle.FLAME,a.getLocation(),100,0,0,0,0.2);
                                }
                            }
                            if(e instanceof LivingEntity l){
                                if(e instanceof ItemFrame i)i.remove();
                                if(l instanceof Player p1){
                                    if(p1.getGameMode() == GameMode.SPECTATOR)continue;
                                    if(gameStatus.isTeamMate(p,p1))continue;
                                }
                                Vector speed = l.getVelocity();
                                l.damage(4,p);
                                l.teleport(l.getLocation());
                                l.setVelocity(speed);
                            }
                        }
                        w.spawnParticle(Particle.EXPLOSION_LARGE,fireball.getLocation(),1);
                        w.spawnParticle(Particle.FLAME,fireball.getLocation(),20,0,0,0,0.1);
                        fireball.remove();
                        this.cancel();
                    }
                }
            };
            hit.runTaskTimer(plugin,0L,1L);
        }
    }
    public void wildFireLauncher(Player p, ItemStack hand){
        World w = p.getWorld();
        if(p.getCooldown(hand.getType()) == 0) {
            p.setCooldown(hand.getType(),300);
            w.playSound(p.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1, 1);
            Location eyeLoc = p.getEyeLocation();
            Vector eyeVec = eyeLoc.getDirection();
            Arrow fireball = w.spawnArrow(eyeLoc,eyeVec,1.5f,5);
            fireball.setFireTicks(200);
            fireball.setShooter(p);
            fireball.setDamage(10);
            fireball.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            fireball.setColor(Color.ORANGE);
            fireball.setTicksLived(1200);
            fireball.setCustomName(p.getName() + "的铝热剂榴弹");
            BukkitRunnable hit = new BukkitRunnable() {
                @Override
                public void run() {
                    w.spawnParticle(Particle.FLAME,fireball.getLocation(),0);
                    if (fireball.isDead()) {
                        k.fire(p,fireball,7,2);
                        w.playSound(fireball.getLocation(),Sound.ITEM_FIRECHARGE_USE,1,1);
                        double y = fireball.getVelocity().getY();
                        Vector splash = new Vector(0,-y,0).normalize();
                        for(int i = 0;i < 20;i ++){
                            Arrow fire = w.spawnArrow(fireball.getLocation(),splash,0.35f,100);
                            fire.setShooter(p);
                            fire.setTicksLived(1200);
                            fire.setDamage(5);
                            fire.setFireTicks(200);
                            fire.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
                        }
                        this.cancel();
                    }
                }
            };
            hit.runTaskTimer(plugin, 0L, 1L);
        }
    }
    public void whip(Player p,ItemStack hand){
        World w = p.getWorld();
        if(p.getCooldown(hand.getType()) == 0) {
            p.setCooldown(hand.getType(), 30);
            w.playSound(p.getLocation(), Sound.BLOCK_GRINDSTONE_USE, 1, 1);
            w.playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
            Location pLoc = p.getEyeLocation();
            Vector stabVec = pLoc.getDirection();
            double radius = 10;
            List<Entity>entities = p.getNearbyEntities(radius,radius,radius);
            for(Entity e : entities){
                if(e.getName().contains("传送门")  || e.getName().contains("滑索"))continue;
                if(e instanceof Player p1){
                    if(gameStatus.isTeamMate(p,p1))continue;
                }
                if(e instanceof LivingEntity l){
                    double distance = k.distance(l.getLocation(),p.getLocation());
                    Vector lVec = l.getEyeLocation().toVector();
                    Vector pVec = p.getEyeLocation().toVector();
                    Vector sVec = lVec.clone().subtract(pVec);
                    double angle = k.angle(stabVec,sVec);
                    RayTraceResult result = w.rayTraceBlocks(p.getEyeLocation(),stabVec,6);
                    if(result == null || result.getHitBlock().getType() == Material.AIR) {
                        if (distance <= radius && distance >= 3 && angle > 0.97) {
                            l.damage((int)distance + 3, p);
                            p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 0.5f);
                            w.playSound(p.getLocation(), Sound.BLOCK_GRINDSTONE_USE, 1, 1);
                            Particle.DustOptions dust1 = new Particle.DustOptions(Color.FUCHSIA,1);
                            Particle.DustOptions dust2 = new Particle.DustOptions(Color.BLACK,1);
                            w.spawnParticle(Particle.REDSTONE,l.getLocation(), 30,0.5,1,0.5,0.1,dust1);
                            w.spawnParticle(Particle.REDSTONE,l.getLocation(), 30,0.5,1,0.5,0.1,dust2);
                            w.playSound(l.getEyeLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
                            break;
                        }
                    }
                }
            }
        }
    }
    public void glitchSwing(Player p,ItemStack hand){
        World w = p.getWorld();
        if(p.getCooldown(hand.getType()) == 0) {
            p.setCooldown(hand.getType(), 200);
            w.playSound(p.getLocation(),Sound.ENTITY_PLAYER_ATTACK_SWEEP,1,0.9f);
            //universalSlash(p.getLocation(),4,-1,1,new Color[]{Color.FUCHSIA,Color.BLACK});
            undeadSlash(p,1,-1,1,-1,new Color[]{Color.FUCHSIA,Color.BLACK});
            Vector stabVec = p.getEyeLocation().getDirection();
            List<Entity>entities = p.getNearbyEntities(5,5,5);
            for(Entity e : entities){
                if(e.getName().contains("传送门")  || e.getName().contains("滑索"))continue;
                if(e instanceof ArmorStand || e instanceof Villager) {continue;}
                if(e instanceof Player p1){
                    if(gameStatus.isTeamMate(p,p1))continue;
                }
                if(e instanceof LivingEntity l){
                    double distance = k.distance(l.getLocation(),p.getLocation());
                    double ballDistance = k.distance(p.getEyeLocation().add(stabVec),l.getLocation());
                    Vector lVec = l.getEyeLocation().toVector();
                    Vector pVec = p.getEyeLocation().toVector();
                    Vector sVec = lVec.clone().subtract(pVec);
                    double angle = k.angle(stabVec,sVec);
                    if(distance <= 5 && angle > 0.85 || ballDistance <= 2){
                        l.damage(10,p);
                        l.setVelocity(new Vector(0,0.8,0).add(stabVec.multiply(0.5)));
                        l.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,100,4));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,80,2));
                        w.playSound(l.getEyeLocation(),Sound.ENTITY_PLAYER_ATTACK_CRIT,1,1);
                        w.playSound(l.getEyeLocation(),Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE,1,1);
                    }
                }
            }
        }
    }
    public void smashGround(Player p,ItemStack hand){
        World w = p.getWorld();
        if(p.getCooldown(hand.getType()) == 0) {
            double radius = 2.5;
            p.setCooldown(hand.getType(), 140);
            Location eyeLoc = p.getLocation();
            Vector eyeVec = eyeLoc.getDirection();
            Vector dash = new Vector(eyeVec.getX(),0,eyeVec.getZ());
            p.setVelocity(dash.multiply(0.8));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,15,10));
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,15,0));
            w.playSound(p.getLocation(),Sound.ENTITY_PLAYER_BIG_FALL,1,1);
            w.playSound(p.getLocation(),Sound.ENTITY_PLAYER_BIG_FALL,1,1);
            w.spawnParticle(Particle.ITEM_CRACK,p.getLocation(), 100,1.5,0,1.5,0.1,new ItemStack(Material.DIRT));
            BukkitRunnable slam = new BukkitRunnable() {
                @Override
                public void run() {
                    if(p.getGameMode() == GameMode.SPECTATOR)return;
                    w.playSound(p.getLocation(),Sound.ENTITY_PLAYER_ATTACK_SWEEP,1,0.7f);
                    w.playSound(p.getLocation(),Sound.BLOCK_GRAVEL_BREAK,1,1);
                    Vector stabVec = p.getEyeLocation().getDirection();
                    Vector forward = new Vector(stabVec.getX(),0,stabVec.getZ());
                    Location center = p.getLocation().clone().add(forward.multiply(radius));
                    w.spawnParticle(Particle.ITEM_CRACK,center, 100
                            ,radius/2
                            ,radius/2
                            ,radius/2
                            ,0.1,new ItemStack(Material.DIRT));
                    for(Entity e : w.getNearbyEntities(center,radius,radius,radius)){
                        if(k.distance(center,e.getLocation()) > radius)continue;
                        if(e instanceof Player p1){
                            if(gameStatus.isTeamMate(p,p1))continue;
                            if(p1.getGameMode() == GameMode.SPECTATOR)continue;
                        }
                        if(e instanceof LivingEntity l) {
                            w.spawnParticle(Particle.ITEM_CRACK,l.getLocation(), 50,0.5,1.5,0.5,0.1,new ItemStack(Material.DIRT));
                            l.damage(13, p);
                            k.breakSquareBlock(p, l.getLocation(), 1, 1, 1, 0);
                            w.playSound(l.getEyeLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
                            w.playSound(l.getEyeLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1, 1);
                        }
                    }
                }
            };
            slam.runTaskLater(plugin,15L);
        }
    }
    public void sniper(Player p,ItemStack hand){
        World w = p.getWorld();
        if(p.getCooldown(hand.getType()) == 0) {
            p.setCooldown(hand.getType(), 80);
            double range = 64;
            double radius = 1;
            Location shootLoc = p.getEyeLocation();
            Vector shootVec = shootLoc.getDirection();
            w.playSound(p.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,1,2);
            Location pLoc = shootLoc.clone();
            boolean hit = false;
            for (int i = 0; i < range; i++) {
                if(hit)break;
                if(i >= 20 && i % 10 == 0){
                    radius += 0.5;
                }
                w.spawnParticle(Particle.END_ROD, pLoc, 0);
                for(Entity e : w.getNearbyEntities(pLoc,radius,radius,radius)){
                    if(k.distance(e.getLocation().add(0,1,0),pLoc) > radius)continue;
                    if(e instanceof Player p1){
                        if(gameStatus.isTeamMate(p,p1))continue;
                        if(p1.getGameMode() == GameMode.SPECTATOR)continue;
                    }
                    if(e instanceof LivingEntity l) {
                        double distance = k.distance(p.getLocation(), l.getLocation());//距离
                        double damage = 14;//基础伤害
                        double maxDamage = 41;//最大伤害
                        double startDistance = 8;//伤害开始增加距离
                        double maxDamageRange = 40;//最大伤害距离
                        int damageDistance =(int)Math.max(0,distance - startDistance);
                        double step = (maxDamage - damage) / (maxDamageRange - startDistance);
                        double addedDamage = damage + damageDistance * step;
                        double finalDamage = Math.min(addedDamage,maxDamage);
                        l.damage(finalDamage, p);
                        hit = true;
                        w.playSound(l.getLocation(), Sound.ITEM_TRIDENT_HIT, 1, 1);
                        p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 0.5f);
                        p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT, 1, 1);
                        p.sendTitle(" ",
                                org.bukkit.ChatColor.AQUA + "距离：" + String.format("%.1f", distance), 10, 20, 10);
                        if (l.isDead() && distance >= 48) {
                            Bukkit.broadcastMessage(org.bukkit.ChatColor.AQUA + p.getName() + "百步穿杨！在视距外狙杀了一个目标，WOW！");
                            w.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
                        }
                    }
                }
                pLoc.add(shootVec.clone());
            }
            Vector speed = p.getVelocity();
            Location playerLoc = p.getLocation();
            playerLoc.setPitch(playerLoc.getPitch() - 20);
            p.teleport(playerLoc);
            p.setVelocity(speed);
            BukkitRunnable sound = new BukkitRunnable() {
                int count = 0;
                @Override
                public void run() {
                    if(count > 2){
                        this.cancel();
                        return;
                    }
                    Sound s = switch (count) {
                        case 0 -> Sound.BLOCK_IRON_DOOR_OPEN;
                        case 1 -> Sound.UI_BUTTON_CLICK;
                        case 2 -> Sound.BLOCK_IRON_DOOR_CLOSE;
                        default -> Sound.UI_TOAST_IN;
                    };
                    w.playSound(p.getEyeLocation(),s,1,1.2f);
                    count += 1;
                }
            };
            sound.runTaskTimer(plugin,40L,15L);
            BukkitRunnable recover = new BukkitRunnable() {
                @Override
                public void run() {
                    Vector speed = p.getVelocity();
                    Location playerLoc = p.getLocation();
                    playerLoc.setPitch(playerLoc.getPitch() + 20);
                    p.teleport(playerLoc);
                    p.setVelocity(speed);
                }
            };
            recover.runTaskLater(plugin,4L);
            w.spawnParticle(Particle.EXPLOSION_NORMAL,shootLoc.add(shootVec),10,0,0,0,0.2);
        }
    }
    public void dragonBreath(Player p,ItemStack hand){
        World w = p.getWorld();
        ItemMeta handMeta = hand.getItemMeta();
        int itemID = items.getItemID(handMeta.getDisplayName());
        if(p.getCooldown(hand.getType()) == 0) {
            int ammo = playerStats.getItemAmmo(p, itemID);
            if (ammo < 0) {
                ammo = 2;
            }
            if(ammo == 1) {
                p.setCooldown(hand.getType(), 160);
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(org.bukkit.ChatColor.YELLOW + "重新装填中"));
                playerStats.setItemAmmo(p, itemID, 2);
                BukkitRunnable timer = new BukkitRunnable() {
                    int count = -1;
                    @Override
                    public void run() {
                        if(count > 1){
                            w.playSound(p.getLocation(), Sound.BLOCK_WOODEN_TRAPDOOR_CLOSE, 1, 1);
                            w.playSound(p.getLocation(), Sound.BLOCK_WOODEN_TRAPDOOR_CLOSE, 1, 1);
                            this.cancel();
                            return;
                        }
                        if(count == -1){
                            w.playSound(p.getLocation(), Sound.BLOCK_WOODEN_TRAPDOOR_OPEN, 1, 1);
                            w.playSound(p.getLocation(), Sound.BLOCK_WOODEN_TRAPDOOR_OPEN, 1, 1);
                        }else {
                            w.playSound(p.getLocation(), Sound.ITEM_BOTTLE_FILL_DRAGONBREATH, 1, 1);
                        }
                        count += 1;
                    }
                };
                timer.runTaskTimer(plugin,100L,20L);
            }else {
                p.setCooldown(hand.getType(),30);
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(org.bukkit.ChatColor.YELLOW + "剩余弹药：" + (ammo - 1)));
                ammo -= 1;
                playerStats.setItemAmmo(p,itemID,ammo);
            }
            Location shootLoc = p.getEyeLocation();
            Vector stabVec = shootLoc.getDirection();
            for (int i = 0; i < 10; i++) {
                double xSpread = random.nextDouble() - random.nextDouble();
                double zSpread = random.nextDouble() - random.nextDouble();
                Vector spread = new Vector(xSpread, 0, zSpread);
                Vector shootVec = spread.normalize().multiply(0.2);
                shootVec.add(shootLoc.getDirection());
                shootVec.multiply(random.nextDouble());
                Snowball ball = (Snowball) w.spawnEntity(shootLoc, EntityType.SNOWBALL);
                ball.setItem(new ItemStack(Material.BLAZE_POWDER));
                ball.setVelocity(shootVec);
                ball.setShooter(p);
                ball.setFireTicks(1200);
            }
            int range = 6;
            Location loc = p.getEyeLocation().add(stabVec.multiply(2));
            int x = loc.getBlockX();
            int y = loc.getBlockY();
            int z = loc.getBlockZ();
            for(int a = -1;a <= 1;a++){
                for(int b = -1;b <= 1;b++){
                    for(int c = -1;c <=1;c++) {
                        if (a * a + b * b + c * c <= 1) {
                            Location newLoc = new Location(w, x + a, y + b, z + c);
                            Block block = w.getBlockAt(newLoc);
                            if (block.getType() == Material.AIR
                                    || block.getType() == Material.LIGHT
                                    || block.getType() == Material.POWDER_SNOW) {
                                block.setType(Material.FIRE);
                            }
                        }
                    }
                }
            }
            List<Entity> entities = p.getNearbyEntities(range, range, range);
            for (Entity e : entities) {
                if(e instanceof Player p1){
                    if(gameStatus.isTeamMate(p,p1))continue;
                    if(p1.getGameMode() == GameMode.SPECTATOR)continue;
                }
                if(e instanceof AreaEffectCloud a){
                    if(a.getName().contains("毒气")){
                        a.remove();
                        w.playSound(a.getLocation(),Sound.ITEM_FIRECHARGE_USE,2,1);
                        w.spawnParticle(Particle.EXPLOSION_HUGE,a.getLocation(),1);
                        w.spawnParticle(Particle.FLAME,a.getLocation(),100,0,0,0,0.2);
                    }
                }
                if (e instanceof LivingEntity l) {
                    double distance = k.distance(l.getLocation(), p.getLocation());
                    double ballDistance = k.distance(p.getEyeLocation().add(stabVec.multiply(0.9)),l.getLocation());
                    Vector lVec = l.getEyeLocation().toVector();
                    Vector pVec = p.getEyeLocation().toVector();
                    Vector sVec = lVec.clone().subtract(pVec);
                    double angle = k.angle(stabVec, sVec);
                    if (distance <= range && angle > 0.85 || ballDistance <= 2) {
                        double amp = 3;
                        if(distance > 1){
                            distance -= 1;
                        }else {
                            amp = 0;
                        }
                        l.damage(16 - (int)distance * amp, p);
                        int fire = l.getFireTicks();
                        l.setFireTicks(fire + 60);
                        l.setVelocity(stabVec.setY(0.1));
                        w.playSound(l.getEyeLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
                    }
                }
            }
            p.setVelocity(stabVec.clone().multiply(-0.3));
            w.spawnParticle(Particle.SMOKE_NORMAL,shootLoc,50,0.3,0.3,0.3,0.1);
            w.spawnParticle(Particle.FLAME,shootLoc,50,0.3,0.3,0.3,0.1);
            w.spawnParticle(Particle.EXPLOSION_LARGE,shootLoc,1);
            w.playSound(shootLoc,Sound.ENTITY_GENERIC_EXPLODE,1.5f,2f);
            w.playSound(shootLoc,Sound.ENTITY_ENDER_DRAGON_HURT,1,1);
        }
    }
    public void slug(Player p,ItemStack hand){
        World w = p.getWorld();
        ItemMeta handMeta = hand.getItemMeta();
        int itemID = items.getItemID(handMeta.getDisplayName());
        int maxAmmo = 6;
        if(p.getCooldown(hand.getType()) == 0) {
            int ammo = playerStats.getItemAmmo(p, itemID);
            if (ammo <= 0) {
                ammo = maxAmmo;
            }
            if (ammo == 1 || p.isSneaking()) {
                if (ammo == maxAmmo) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            TextComponent.fromLegacyText(ChatColor.RED + "弹药已满，无需装填"));
                } else {
                    if(!p.isSneaking())ammo = 0;
                    int reload = maxAmmo - ammo;
                    p.setCooldown(hand.getType(), reload * 20);
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            TextComponent.fromLegacyText(ChatColor.RED + "重新装填中"));
                    playerStats.setItemAmmo(p, itemID, maxAmmo);
                    BukkitRunnable timer = new BukkitRunnable() {
                        int count = -1;
                        @Override
                        public void run() {
                            if(count >= reload){
                                w.playSound(p.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1, 1);
                                w.playSound(p.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1, 1);
                                this.cancel();
                                return;
                            }
                            if(count == -1){
                                w.playSound(p.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1, 1);
                                w.playSound(p.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1, 1);
                            }else {
                                w.playSound(p.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1, 1);
                                w.playSound(p.getLocation(), Sound.ENTITY_GLOW_ITEM_FRAME_REMOVE_ITEM, 1, 1);
                            }
                            count += 1;
                        }
                    };
                    timer.runTaskTimer(plugin,(reload - 1) * 10L,10L);
                }
                if (p.isSneaking()) return;
            } else {
                p.setCooldown(hand.getType(), 24);
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(ChatColor.RED + "剩余弹药：" + (ammo - 1)));
                ammo -= 1;
                playerStats.setItemAmmo(p, itemID, ammo);
            }
            Location shootLoc = p.getEyeLocation();
            Vector shootVec = shootLoc.getDirection();
            w.playSound(p.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,1,2);
            w.playSound(p.getLocation(),Sound.ITEM_CROSSBOW_SHOOT,1,1);
            Arrow slug = w.spawnArrow(shootLoc,shootVec,2,0);
            slug.setShooter(p);
            slug.setDamage(6.5);
            slug.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            slug.setGravity(false);
            BukkitRunnable fly = new BukkitRunnable() {
                @Override
                public void run() {
                    w.spawnParticle(Particle.CRIT,slug.getLocation(),0);
                    if(slug.getTicksLived() > 10){
                        slug.setGravity(true);
                    }
                    if(slug.isDead()){
                        k.breakBallBlock(p,slug.getLocation(),1,0);
                        w.spawnParticle(Particle.EXPLOSION_LARGE,slug.getLocation(),1);
                        w.playSound(slug.getLocation(),Sound.ENTITY_IRON_GOLEM_DAMAGE,1,0.5f);
                        w.playSound(slug.getLocation(),Sound.BLOCK_STONE_BREAK,1,1);
                        w.playSound(slug.getLocation(),Sound.BLOCK_NETHER_BRICKS_BREAK,1,1);
                        this.cancel();
                    }
                }
            };
            fly.runTaskTimer(plugin,0L,1L);
            Vector speed = p.getVelocity();
            Location playerLoc = p.getLocation();
            playerLoc.setPitch(playerLoc.getPitch() - 10);
            p.teleport(playerLoc);
            p.setVelocity(speed);
            w.spawnParticle(Particle.EXPLOSION_NORMAL,shootLoc.add(shootVec),5,0,0,0,0.2);
            if(ammo > 0) {
                BukkitRunnable click = new BukkitRunnable() {
                    int count = 0;
                    @Override
                    public void run() {
                        if (count > 1) {
                            this.cancel();
                            return;
                        }
                        if (count == 0) {
                            w.playSound(p.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1, 1);
                            w.playSound(p.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1, 1);
                        } else {
                            w.playSound(p.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1, 1);
                            w.playSound(p.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1, 1);
                        }
                        count += 1;
                    }
                };
                click.runTaskTimer(plugin, 14L,3L);
            }
        }
    }

    public void universalSlash(Location loc, int radius1, double angle1, double angle2,Color[]colors) {
        World w = loc.getWorld();
        Vector upVector = new Vector(0, 1, 0);
        double playerX = loc.getX();
        double playerZ = loc.getZ();
        Vector playerEye1 = (loc.getDirection()).normalize();
        Vector angleVector = ((playerEye1.clone()).crossProduct(upVector)).normalize();
        Vector playerEy = playerEye1.add(angleVector.multiply(0.1));
        Vector playerEye = (playerEy.normalize());
        Vector leftVector = (((playerEye.clone()).crossProduct(upVector)).normalize());
        Vector rightVector = (leftVector.clone()).normalize();
        Vector upRight = ((upVector.clone()).multiply(angle2)).add((rightVector.clone()).multiply(angle1));
        Vector downLeft = ((upRight.clone()).multiply(-1));
        for (double j = 0.065; j <= 3.6; j += 0.035) {
            Color c = colors[random.nextInt(colors.length)];
            Particle.DustOptions dust = new Particle.DustOptions(c,1);
            double x = (Math.sin(j - 1.57)) * (Math.sqrt(radius1) + 1);
            double z = (Math.cos(j - 1.57)) * (Math.sqrt(radius1) + 1);
            Vector left = ((downLeft.clone()).normalize()).multiply(x);
            Vector forward = ((playerEye.clone()).normalize()).multiply(z);
            Vector particleLoc = ((left.clone()).add(forward.clone())).add((upVector.clone()).multiply(1.2));
            Location areaP = new Location(w, playerX, loc.getY(), playerZ);
            areaP = (areaP.add(particleLoc)).add((playerEye1.clone()).multiply(2));
            w.spawnParticle(Particle.REDSTONE, areaP, 0,0,0,0,dust);
        }
    }
    public void universalSlash(Location loc, int radius1, double angle1, double angle2,Particle par) {
        World w = loc.getWorld();
        Vector upVector = new Vector(0, 1, 0);
        double playerX = loc.getX();
        double playerZ = loc.getZ();
        Vector playerEye1 = (loc.getDirection()).normalize();
        Vector angleVector = ((playerEye1.clone()).crossProduct(upVector)).normalize();
        Vector playerEy = playerEye1.add(angleVector.multiply(0.1));
        Vector playerEye = (playerEy.normalize());
        Vector leftVector = (((playerEye.clone()).crossProduct(upVector)).normalize());
        Vector rightVector = (leftVector.clone()).normalize();
        Vector upRight = ((upVector.clone()).multiply(angle2)).add((rightVector.clone()).multiply(angle1));
        Vector downLeft = ((upRight.clone()).multiply(-1));
        for (double j = 0.065; j <= 3.6; j += 0.035) {
            double x = (Math.sin(j - 1.57)) * (Math.sqrt(radius1) + 1);
            double z = (Math.cos(j - 1.57)) * (Math.sqrt(radius1) + 1);
            Vector left = ((downLeft.clone()).normalize()).multiply(x);
            Vector forward = ((playerEye.clone()).normalize()).multiply(z);
            Vector particleLoc = ((left.clone()).add(forward.clone())).add((upVector.clone()).multiply(1.2));
            Location areaP = new Location(w, playerX, loc.getY(), playerZ);
            areaP = (areaP.add(particleLoc)).add((playerEye1.clone()).multiply(2));
            w.spawnParticle(par, areaP, 0,0,0,0);
        }
    }
    public void undeadSlash(Player p, int direction, double angle1, double angle2, int aDirection,Color[]colors) {
        World w = p.getWorld();
        double playerX = p.getLocation().getX();
        double playerZ = p.getLocation().getZ();
        Vector playerE = (p.getEyeLocation().getDirection()).normalize();
        Vector playerEy = new Vector(playerE.getX(), 0, playerE.getZ());
        Vector playerEye = playerEy.normalize();
        Vector upVector = new Vector(0, 1, 0);
        Vector leftVector = (((playerEye.clone()).crossProduct(upVector)).normalize()).multiply(direction);
        Vector rightVector = (leftVector.clone()).normalize();
        Vector upRight = ((upVector.clone()).multiply(angle2)).add((rightVector.clone()).multiply(angle1));
        Vector downLeft = ((upRight.clone()).multiply(-1));
        for (int i = -15; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                Color c = colors[random.nextInt(colors.length)];
                Particle.DustOptions redStone = new Particle.DustOptions(c,1);
                Vector left = ((downLeft.clone()).multiply(i * 0.2));
                Vector forward = ((playerEye.clone()).multiply(j * 0.5));
                Vector particleLoc = ((left.clone()).add(forward.clone())).add((upVector.clone()).multiply(1.2));
                float r = (i * 0.5f) * (i * 0.5f) + (j * 0.5f) * (j * 0.5f);
                BukkitRunnable geneP = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (r >= 13 && r <= 49) {
                            Location area = new Location(w, playerX, p.getLocation().getY(), playerZ);
                            area = (area.add(particleLoc));
                            w.spawnParticle(Particle.REDSTONE, area,0,0,0,0,0,redStone);
                        }
                    }
                };
                if (aDirection * i < -12) {
                    geneP.run();
                }
                if (aDirection * i >= -12 && aDirection * i < -5) {
                    geneP.runTaskLater(plugin, 1L);
                }
                if (aDirection * i >= -5 && aDirection * i < 5) {
                    geneP.runTaskLater(plugin, 2L);
                }
                if (aDirection * i >= 5 && aDirection * i < 12) {
                    geneP.runTaskLater(plugin, 3L);
                }
                if (aDirection * i >= 12) {
                    geneP.runTaskLater(plugin, 4L);
                }
            }
        }
    }
    public void masterSword(Player p,ItemStack hand) {
        if (p.getCooldown(hand.getType()) == 0) {
            p.setCooldown(hand.getType(), 20);
            World w = p.getWorld();
            Vector forwardVec = p.getEyeLocation().getDirection().clone();
            Location forwardLoc = p.getEyeLocation().clone();
            forwardLoc.add(0, -1, 0);
            double angle1 = random.nextDouble() - random.nextDouble();
            double angle2 = random.nextDouble() - random.nextDouble();
            BukkitRunnable slash = new BukkitRunnable() {
                int count = 0;

                @Override
                public void run() {
                    w.playSound(forwardLoc, Sound.BLOCK_AMETHYST_BLOCK_BREAK, 2, 1);
                    if (count >= 10) {
                        this.cancel();
                        return;
                    }
                    universalSlash(forwardLoc, 10, angle1, angle2, Particle.GLOW_SQUID_INK);
                    forwardLoc.add(forwardVec.clone().multiply(2));
                    count += 1;
                }
            };
            slash.runTaskTimer(plugin, 0L, 2L);
        }
    }
    public void majimaDagger(Player p,ItemStack hand){
        World w = p.getWorld();
        if (p.getCooldown(hand.getType()) == 0) {
            p.setCooldown(hand.getType(), 200);
            Location shootLoc = p.getEyeLocation();
            Vector shootVec = shootLoc.getDirection();
            w.playSound(p.getLocation(),Sound.ITEM_TRIDENT_THROW,1,1.1f);
            List<Arrow>arrows = new ArrayList<>();
            for(int i = 0;i < 10;i ++){
                Arrow dagger = w.spawnArrow(shootLoc,shootVec,3,20);
                dagger.setColor(Color.PURPLE);
                dagger.setShooter(p);
                dagger.setDamage(10);
                dagger.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                BukkitRunnable hit = new BukkitRunnable() {
                    @Override
                    public void run() {
                        w.spawnParticle(Particle.DRAGON_BREATH,dagger.getLocation(),0);
                       if(dagger.isInBlock() || dagger.isDead()){
                           this.cancel();
                           arrows.add(dagger);
                           if(arrows.size() >= 10){
                               BukkitRunnable lightning = new BukkitRunnable() {
                                   int count = 0;
                                   @Override
                                   public void run() {
                                       if(count >= arrows.size()){
                                           this.cancel();
                                           return;
                                       }
                                       Arrow a = arrows.get(count);
                                       w.strikeLightning(a.getLocation());
                                       a.remove();
                                       count += 1;
                                   }
                               };
                               lightning.runTaskTimer(plugin,0L,5L);
                           }
                       }
                    }
                };
                hit.runTaskTimer(plugin,0L,1L);
            }
        }
    }
    public void katana(Player p,ItemStack hand) {
        World w = p.getWorld();
        if (p.getCooldown(hand.getType()) == 0) {
            p.setCooldown(hand.getType(), 100);
            Location eyeLoc = p.getEyeLocation();
            Vector eyeVec = eyeLoc.getDirection();
            Vector dash = new Vector(eyeVec.getX(), 0.1, eyeVec.getZ());
            if (p.isOnGround()) {
                p.setVelocity(dash.multiply(2));
            } else {
                p.setVelocity(dash.multiply(1));
            }
            w.playSound(eyeLoc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 0.8f);
            BukkitRunnable stab = new BukkitRunnable() {
                int count = 0;

                @Override
                public void run() {
                    count += 1;
                    if (count >= 10) {
                        this.cancel();
                    }
                    Location pLoc = p.getEyeLocation();
                    Vector stabVec = p.getEyeLocation().getDirection();
                    for (int i = 0; i < 3; i++) {
                        w.spawnParticle(Particle.CRIT, pLoc, 10,0,0,0,0.1);
                        pLoc.add(stabVec.clone());
                    }
                    List<Entity> entities = p.getNearbyEntities(3, 3, 3);
                    for (Entity e : entities) {
                        if (e instanceof LivingEntity l) {
                            double distance = k.distance(l.getLocation(), p.getLocation());
                            Vector lVec = l.getEyeLocation().toVector();
                            Vector pVec = p.getEyeLocation().toVector();
                            Vector sVec = lVec.clone().subtract(pVec);
                            double angle = k.angle(stabVec, sVec);
                            if (distance <= 3 && angle > 0.85) {
                                l.damage(50, p);
                                w.playSound(l.getEyeLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
                                w.spawnParticle(Particle.CRIT, p.getEyeLocation(), 50, 1, 1, 1);
                                p.setVelocity(new Vector(0,0,0));
                                this.cancel();
                                BukkitRunnable stepDragon = new BukkitRunnable() {
                                    int count = 0;
                                    @Override
                                    public void run() {
                                        Location loc = p.getLocation();
                                        switch (count) {
                                            case 0 -> {
                                                w.spawnParticle(Particle.EXPLOSION_LARGE, loc, 1);
                                                w.playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                                                p.setVelocity(new Vector(0, 2, 0));
                                                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20, 0));
                                            }
                                            case 2 -> {
                                                p.setVelocity(new Vector(0, -2, 0));
                                                w.playSound(loc, Sound.BLOCK_ANVIL_PLACE, 1, 2);
                                            }
                                            case 3 -> {
                                                Location pLoc = p.getEyeLocation().add(stabVec);
                                                Particle.DustOptions dust = new Particle.DustOptions(Color.RED,3);
                                                for (int i = 0; i < 6; i++) {
                                                    w.spawnParticle(Particle.REDSTONE, pLoc, 0,0,0,0,0.1,dust);
                                                    pLoc.add(0,1,0);
                                                }
                                                w.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                                                w.spawnParticle(Particle.EXPLOSION_LARGE, loc, 20,5,0,5);
                                                this.cancel();
                                            }
                                        }
                                        count += 1;
                                    }
                                };
                                stepDragon.runTaskTimer(plugin,5L,10L);
                            }
                        }
                    }
                }
            };
            stab.runTaskTimer(plugin, 0L, 2L);
        }
    }
    public void snowball(Player p,ItemStack hand){
        World w = p.getWorld();
        if (p.getCooldown(hand.getType()) == 0) {
            p.setCooldown(hand.getType(), 5);
            w.playSound(p.getEyeLocation(),Sound.ENTITY_EGG_THROW,1,1);
            Vector shootVec = p.getEyeLocation().getDirection();
            Snowball ball = (Snowball) w.spawnEntity(p.getEyeLocation(),EntityType.SNOWBALL);
            ball.setVelocity(shootVec.multiply(1.5));
            ball.setItem(items.snowball());
            BukkitRunnable hit = new BukkitRunnable() {
                @Override
                public void run() {
                    w.spawnParticle(Particle.ITEM_CRACK,ball.getLocation(),0,items.snowball());
                    if(ball.isDead()){
                        w.playSound(ball.getLocation(),Sound.BLOCK_SNOW_BREAK,5,1);
                        this.cancel();
                        return;
                    }
                    for(Entity e : ball.getNearbyEntities(1,1,1)){
                        if(e instanceof LivingEntity l){
                            if(e == p)continue;
                            if(e instanceof Player p1){
                                if(p1.getGameMode() == GameMode.SPECTATOR)continue;
                                if(p1.isBlocking()){
                                    w.playSound(p1.getLocation(),Sound.ITEM_SHIELD_BLOCK,1,1);
                                    continue;
                                }
                                if(gameStatus.isTeamMate(p,p1))continue;
                            }
                            l.damage(6,p);
                            if(random.nextInt(20) == 0){
                                w.playSound(ball.getLocation(),Sound.BLOCK_ANVIL_PLACE,5,1);
                            }
                            ball.remove();
                            this.cancel();
                            break;
                        }
                    }
                }
            };
            hit.runTaskTimer(plugin,0L,1L);
        }
    }
    public void snowNadeWall(Player p, ItemStack hand, int coolDown){
        World w = p.getWorld();
        Material material = hand.getType();
        if (p.getCooldown(material) == 0) {
            p.setCooldown(material, coolDown * 20);
            Location shootLoc = p.getEyeLocation();
            Vector shootVec = shootLoc.getDirection();
            Snowball nade = (Snowball) w.spawnEntity(shootLoc, EntityType.SNOWBALL);
            nade.setVelocity(shootVec.multiply(2));
            nade.setItem(hand);
            nade.setShooter(p);
            nade.setGlowing(true);
            w.playSound(shootLoc, Sound.ENTITY_EGG_THROW, 1, 1);
            w.playSound(shootLoc, Sound.ITEM_BUCKET_EMPTY_POWDER_SNOW, 1, 1);
            w.playSound(shootLoc, Sound.ITEM_BUCKET_EMPTY_POWDER_SNOW, 1, 1);
            Vector forwardVec = p.getEyeLocation().getDirection().setY(0).normalize();
            Vector downVec = new Vector(0, -1, 0).normalize();
            Vector rightVec = forwardVec.clone().crossProduct(downVec);
            BukkitRunnable land = new BukkitRunnable() {
                @Override
                public void run() {
                    w.spawnParticle(Particle.ITEM_CRACK, nade.getLocation(), 0,hand);
                    if (nade.isDead() || k.hitSquareBlock(nade)) {
                        nade.remove();
                        Location loc = nade.getLocation().add(nade.getVelocity().normalize().multiply(-2));
                        k.snow(p, loc.clone(), 2);
                        k.snow(p, loc.clone().add(rightVec.clone().multiply(3)), 2);
                        k.snow(p, loc.clone().add(rightVec.clone().multiply(-3)), 2);
                        this.cancel();
                    }
                }
            };
            land.runTaskTimer(plugin, 5L, 1L);
        }
    }
    public void shuriStar(Player p,ItemStack hand,int coolDown) {
        World w = p.getWorld();
        Material material = hand.getType();
        if (p.getCooldown(material) == 0) {
            p.setCooldown(material, coolDown * 20);
            w.playSound(p.getLocation(),Sound.ITEM_TRIDENT_THROW,1,1.5f);
            for(int i = 0;i <= 36;i ++){
                Location loc = p.getEyeLocation();
                float yaw = loc.getYaw();
                yaw += i * 10;
                loc.setYaw(yaw);
                Location shootLoc = loc;
                Vector shootVec = shootLoc.getDirection();
                shootVec.setY(0);
                Arrow a = w.spawnArrow(shootLoc,shootVec,3,0);
                a.addCustomEffect(new PotionEffect(PotionEffectType.LEVITATION,20,1),false);
                a.addCustomEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,40,0),false);
                a.setDamage(2);
                a.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
                a.setShooter(p);
                a.setCustomName("漂浮手里剑");
            }
        }
    }
}

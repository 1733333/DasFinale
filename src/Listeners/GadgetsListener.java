package Listeners;

import Universal.GameStatus;
import Universal.Items;
import Universal.Kits;
import Universal.PlayerStats;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ru.xezard.glow.data.glow.Glow;

import java.util.*;


public class GadgetsListener implements Listener {
    JavaPlugin plugin;
    Random r = new Random();
    PlayerStats playerStats = PlayerStats.INSTANCE;
    Kits k = Kits.getInstance();
    Items items = Items.getInstance();
    GameStatus gameStatus = GameStatus.getInstance();
    HashMap<String,Entity>playerC4 = new HashMap<>();
    HashMap<String,ArmorStand>playerGateway = new HashMap<>();
    HashMap<Entity,Entity>hitEntity = new HashMap<>();
    HashMap<Entity,ArmorStand>gatewayEntity = new HashMap<>();
    public static HashMap<ArmorStand,ArmorStand>gatewayToGateway = new HashMap<>();
    public static HashSet<Entity>bounced = new HashSet<>();

    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    String[] gadgets = {
            "§f破片手榴弹",
            "§f毒气手榴弹",
            "§f粘胶手榴弹",
            "§f火焰手榴弹",
            "§f烟雾手榴弹",
            "§f致盲手榴弹",
            "§f冲鸡手榴弹",
            "§f爆炸地雷",
            "§f毒气地雷",
            "§f火焰地雷",
            "§f紊乱手榴弹",
            "§f跳板",
            "§fRPG",
            "§f隐身手榴弹",
            "§fAPS",
            "§fC4",
            "§f引力手榴弹",
            "§f电击枪",
            "§f紊乱地雷",
            "§f球形护盾",
            "§f传送门",
            "§fDebug",
            "§fROSS_MK.2",
            "§f漂浮手里剑",
            "§f细雪手榴弹",
            "§f深渊传感器",
    };
    String[]skills = {
            "§f粘胶枪",
            "§f通讯网络",
            "§f东城会徽章",
    };

    public int getGadgets(String s) {
        for (int i = 0; i < gadgets.length; i++) {
            if (s.equals(gadgets[i])) {
                return i;
            }
        }
        return -1;
    }
    public int getSkills(String s) {
        for (int i = 0; i < skills.length; i++) {
            if (s.contains(skills[i])) {
                return i;
            }
        }
        return -1;
    }

    @EventHandler
    public void projectileHit(ProjectileHitEvent hitEvent){
        Entity hit = hitEvent.getEntity();
        k.breakBallGlass(hit.getLocation(),1);
        if(hit.getCustomName() != null){
            hitEvent.getHitEntity();
            hitEntity.put(hit,hitEvent.getHitEntity());
        }
        hit.remove();
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void playerInterAtEntity(PlayerInteractAtEntityEvent interact){
        Player p = interact.getPlayer();
        Entity e = interact.getRightClicked();
        ItemStack hand = p.getInventory().getItemInMainHand();
        if (hand.getType() == Material.AIR) return;
        ItemMeta handMeta = hand.getItemMeta();
        int itemID = items.getGadgetID(handMeta.getDisplayName());
        switch (itemID){
            case 15 -> plantC4(p,hand,null,null,e);
        }
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent interactEvent) {
        Action action = interactEvent.getAction();
        Player p = interactEvent.getPlayer();
        if(p.getGameMode() == GameMode.SPECTATOR)return;
        ItemStack hand = p.getInventory().getItemInMainHand();
        ItemStack offHand = p.getInventory().getItemInOffHand();
        boolean rightClick = action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK);
        if (hand.getType() != Material.AIR) {
            String tag = k.getLore(hand);
            if (rightClick) {
                if (action == Action.RIGHT_CLICK_BLOCK) {
                    Block block = interactEvent.getClickedBlock();
                    if (block != null) {
                        if (block.getType().name().contains("SIGN")) return;
                    }
                }
                if (!tag.equals("")) {
                    if (playerStats.isCarrying(p)) {
                        interactEvent.setCancelled(true);
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                TextComponent.fromLegacyText(ChatColor.RED + "正在拿着东西，需要丢掉东西才能继续操作"));
                        return;
                    }
                }
                switch (getGadgets(tag)) {
                    case 0:
                        interactEvent.setCancelled(true);
                        grenade(p, hand, 15);
                        break;
                    case 1:
                        interactEvent.setCancelled(true);
                        gasGrenade(p, hand, 15);
                        break;
                    case 2:
                        interactEvent.setCancelled(true);
                        gooGrenade(p, hand, 10);
                        break;
                    case 3:
                        interactEvent.setCancelled(true);
                        fireGrenade(p, hand, 15);
                        break;
                    case 4:
                        interactEvent.setCancelled(true);
                        smokeGrenade(p, hand, 10);
                        break;
                    case 5:
                        interactEvent.setCancelled(true);
                        blindGrenade(p, hand, 15);
                        break;
                    case 6:
                        interactEvent.setCancelled(true);
                        impactGrenade(p, hand, 5);
                        break;
                    case 7:
                        if (action == Action.RIGHT_CLICK_BLOCK) {
                            interactEvent.setCancelled(true);
                            Block block = interactEvent.getClickedBlock();
                            if (block != null) {
                                Location bLoc = block.getLocation();
                                BlockFace face = interactEvent.getBlockFace();
                                explosiveMine(p, hand, 20, bLoc, face);
                            }
                        }
                        break;
                    case 8:
                        if (action == Action.RIGHT_CLICK_BLOCK) {
                            interactEvent.setCancelled(true);
                            Block block = interactEvent.getClickedBlock();
                            if (block != null) {
                                Location bLoc = block.getLocation();
                                BlockFace face = interactEvent.getBlockFace();
                                gasMine(p, hand, 20, bLoc, face);
                            }
                        }
                        break;
                    case 9:
                        if (action == Action.RIGHT_CLICK_BLOCK) {
                            interactEvent.setCancelled(true);
                            Block block = interactEvent.getClickedBlock();
                            if (block != null) {
                                Location bLoc = block.getLocation();
                                BlockFace face = interactEvent.getBlockFace();
                                pyroMine(p, hand, 20, bLoc, face);
                            }
                        }
                        break;
                    case 10:
                        interactEvent.setCancelled(true);
                        glitchGrenade(p, hand, 12);
                        break;
                    case 11:
                        if (action == Action.RIGHT_CLICK_BLOCK) {
                            interactEvent.setCancelled(true);
                            Block block = interactEvent.getClickedBlock();
                            if (block != null) {
                                Location bLoc = block.getLocation();
                                BlockFace face = interactEvent.getBlockFace();
                                jumpPad(p, hand, 30, bLoc, face);
                            }
                        }
                        break;
                    case 12:
                        interactEvent.setCancelled(true);
                        rpg(p, hand, 40);
                        break;
                    case 13:
                        interactEvent.setCancelled(true);
                        vanishingBomb(p, hand, 15);
                        break;
                    case 14:
                        interactEvent.setCancelled(true);
                        aps(p, hand, 60);
                        break;
                    case 15:
                        interactEvent.setCancelled(true);
                        if (action == Action.RIGHT_CLICK_BLOCK) {
                            Block block = interactEvent.getClickedBlock();
                            if (block != null) {
                                BlockFace face = interactEvent.getBlockFace();
                                plantC4(p, hand, block, face, null);
                            }
                        } else {
                            plantC4(p, hand, null, null, null);
                        }
                        break;
                    case 16:
                        interactEvent.setCancelled(true);
                        gravityGrenade(p, hand, 10);
                        break;
                    case 17:
                        interactEvent.setCancelled(true);
                        stunGun(p, hand, 12);
                        break;
                    case 18:
                        if (action == Action.RIGHT_CLICK_BLOCK) {
                            interactEvent.setCancelled(true);
                            Block block = interactEvent.getClickedBlock();
                            if (block != null) {
                                Location bLoc = block.getLocation();
                                BlockFace face = interactEvent.getBlockFace();
                                glitchMine(p,hand,20,bLoc,face);
                            }
                        }
                        break;
                    case 19:
                        interactEvent.setCancelled(true);
                        domeShield(p, hand, 30);
                        break;
                    case 20:
                        interactEvent.setCancelled(true);
                        gateway(p,hand,30);
                        break;
                    case 21:
                        interactEvent.setCancelled(true);
                        milk(p,hand);
                        break;
                    case 22:
                        interactEvent.setCancelled(true);
                        ross(p,hand,40);
                        break;
                    case 23:
                        shuriken(p,hand,7);
                        interactEvent.setCancelled(true);
                        break;
                    case 24:
                        interactEvent.setCancelled(true);
                        snowNade(p,hand,15);
                        break;
                    case 25:
                        if (action == Action.RIGHT_CLICK_BLOCK) {
                            Block block = interactEvent.getClickedBlock();
                            if (block != null) {
                                Location bLoc = block.getLocation();
                                BlockFace face = interactEvent.getBlockFace();
                                deepSensor(p,hand,30,bLoc,face);
                            }
                        }
                        interactEvent.setCancelled(true);
                        break;
                }
                switch (getSkills(tag)) {
                    case 0:
                        interactEvent.setCancelled(true);
                        gooBallGun(p, hand);
                        break;
                    case 2:
                        interactEvent.setCancelled(true);
                        hand.setAmount(0);
                        summonDragon(p);
                }
            }
            if(getSkills(tag) == 1) {
                interactEvent.setCancelled(true);
                indicator(p, hand, rightClick);
            }
        } else if (offHand.getType() != Material.AIR) {
            String tag = k.getLore(offHand);
            if (rightClick) {
                if(getGadgets(tag) >= 0){
                    interactEvent.setCancelled(true);
                }
            }
            if(getSkills(tag) == 1) {
                interactEvent.setCancelled(true);
            }
            if(getSkills(tag) == 2) {
                interactEvent.setCancelled(true);
            }
        }
    }

    public void grenade(Player p, ItemStack hand, int coolDown) {
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
            BukkitRunnable hit = new BukkitRunnable() {
                @Override
                public void run() {
                    w.spawnParticle(Particle.CRIT, nade.getLocation(), 0);
                    if (nade.isDead()) {
                        nade.remove();
                        w.spawnParticle(Particle.EXPLOSION_LARGE, nade.getLocation(), 1);
                        BukkitRunnable later = new BukkitRunnable() {
                            @Override
                            public void run() {
                                w.spawnParticle(Particle.EXPLOSION_HUGE, nade.getLocation(), 1);
                                k.explode(p, nade, 18, 1.5, 5);
                                for (int i = 0; i < 20; i++) {
                                    Vector shootVec = new Vector(r.nextDouble() - r.nextDouble(),
                                            r.nextDouble() - r.nextDouble(), r.nextDouble() - r.nextDouble());
                                    Arrow a = w.spawnArrow(nade.getLocation(), shootVec, 1, 50);
                                    a.setDamage(5);
                                    a.setShooter(p);
                                    a.setTicksLived(1200);
                                }
                                k.breakBallBlock(p, nade.getLocation(), 1, 0);
                                w.playSound(nade.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2, 1);
                            }
                        };
                        later.runTaskLater(plugin, 20L);
                        this.cancel();
                    }
                }
            };
            hit.runTaskTimer(plugin, 2L, 1L);
        }
    }
    public void gasGrenade(Player p, ItemStack hand, int coolDown) {
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
            BukkitRunnable land = new BukkitRunnable() {
                @Override
                public void run() {
                    Particle.DustOptions dust = new Particle.DustOptions(Color.LIME, 1);
                    w.spawnParticle(Particle.REDSTONE, nade.getLocation(), 0, dust);
                    if (nade.isDead()) {
                        this.cancel();
                        w.playSound(nade.getLocation(), Sound.BLOCK_LANTERN_BREAK, 2, 1);
                        w.spawnParticle(Particle.EXPLOSION_LARGE,nade.getLocation(),1);
                        k.gas(p, nade, 10,4);
                    }
                }
            };
            land.runTaskTimer(plugin, 0L, 1L);
        }
    }
    public void gooGrenade(Player p, ItemStack hand, int coolDown) {
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
            BukkitRunnable land = new BukkitRunnable() {
                @Override
                public void run() {
                    BlockData data = Bukkit.createBlockData(Material.WHITE_WOOL);
                    w.spawnParticle(Particle.BLOCK_DUST, nade.getLocation(), 5, data);
                    if (nade.isDead() || k.hitBallBlock(nade)) {
                        this.cancel();
                        w.playSound(nade.getLocation(), Sound.BLOCK_SLIME_BLOCK_PLACE, 2, 1);
                        Location loc = nade.getLocation();
                        k.goo(p, loc.add(nade.getVelocity().multiply(-1)), 2);
                        k.goo(p,loc.add(nade.getVelocity().multiply(-2)),2);
                    }
                }
            };
            land.runTaskTimer(plugin, 5L, 1L);
        }
    }
    public void fireGrenade(Player p, ItemStack hand, int coolDown) {
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
            BukkitRunnable land = new BukkitRunnable() {
                @Override
                public void run() {
                    w.spawnParticle(Particle.FLAME, nade.getLocation(), 0);
                    if (nade.isDead()) {
                        this.cancel();
                        w.playSound(nade.getLocation(), Sound.BLOCK_GLASS_BREAK, 2, 1);
                        w.playSound(nade.getLocation(), Sound.BLOCK_GLASS_BREAK, 2, 1);
                        w.spawnParticle(Particle.EXPLOSION_LARGE,nade.getLocation(),1);
                        k.fire(p, nade, 7,2);
                    }
                }
            };
            land.runTaskTimer(plugin, 0L, 1L);
        }
    }
    public void smokeGrenade(Player p, ItemStack hand, int coolDown) {
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
            BukkitRunnable land = new BukkitRunnable() {
                @Override
                public void run() {
                    w.spawnParticle(Particle.CRIT, nade.getLocation(), 0);
                    if (nade.isDead()) {
                        this.cancel();
                        w.playSound(nade.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 2, 1);
                        k.smoke(nade, 20, 4);
                    }
                }
            };
            land.runTaskTimer(plugin, 0L, 1L);
        }
    }
    public void blindGrenade(Player p, ItemStack hand, int coolDown) {
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
            BukkitRunnable land = new BukkitRunnable() {
                @Override
                public void run() {
                    w.spawnParticle(Particle.SMOKE_NORMAL, nade.getLocation(), 0);
                    if (nade.isDead()) {
                        this.cancel();
                        w.playSound(nade.getLocation(), Sound.ENTITY_EVOKER_PREPARE_SUMMON, 2, 1);
                        w.spawnParticle(Particle.SMOKE_LARGE, nade.getLocation(), 100, 0, 0, 0, 0.3);
                        List<Entity> entities = nade.getNearbyEntities(3, 3, 3);
                        Location jarLoc = nade.getLocation();
                        for (Entity e : entities) {
                            if(k.distance(jarLoc,e.getLocation()) > 3)continue;
                            if (e instanceof LivingEntity l) {
                                w.playSound(l.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
                                l.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
                                if (l instanceof Player player) {
                                    player.sendTitle(" ", ChatColor.RED + "！被致盲！", 0, 20, 10);
                                    p.sendTitle(" ",ChatColor.WHITE + "致盲了一位选手！",0,20,10);
                                }
                            }
                        }
                    }
                }
            };
            land.runTaskTimer(plugin, 0L, 1L);
        }
    }
    public void impactGrenade(Player p, ItemStack hand, int coolDown) {
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
            if (r.nextInt(5) == 0) {
                w.playSound(shootLoc, Sound.ENTITY_CHICKEN_AMBIENT, 1, 1);
            }
            BukkitRunnable land = new BukkitRunnable() {
                @Override
                public void run() {
                    w.spawnParticle(Particle.END_ROD, nade.getLocation(), 0);
                    if (nade.isDead()) {
                        Location jarLoc = nade.getLocation();
                        for(Entity e : nade.getNearbyEntities(3,3,3)){
                            if(k.distance(jarLoc,e.getLocation()) > 3)continue;
                            if(e instanceof LivingEntity l){
                                if(l.isOnGround()) {
                                    k.knockBack(l, nade.getLocation(), 2);
                                }else k.knockBack(l, nade.getLocation(), 1);
                            }
                        }
                        k.explode(p, nade, 6, 1, 3);
                        k.breakSquareGlass(nade.getLocation(),1);
                        w.spawnParticle(Particle.EXPLOSION_HUGE,nade.getLocation(),1);
                        w.playSound(nade.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2, 1);
                        if (r.nextInt(5) == 0) {
                            w.playSound(nade.getLocation(), Sound.ENTITY_CHICKEN_DEATH, 2, 1);
                        }
                        this.cancel();
                    }
                }
            };
            land.runTaskTimer(plugin, 0L, 1L);
        }
    }
    public void gravityGrenade(Player p, ItemStack hand, int coolDown) {
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
            BukkitRunnable land = new BukkitRunnable() {
                @Override
                public void run() {
                    w.spawnParticle(Particle.GLOW, nade.getLocation(), 0);
                    if (nade.isDead()) {
                        this.cancel();
                        BukkitRunnable suck = new BukkitRunnable() {
                            int count = 0;
                            @Override
                            public void run() {
                                if(count > 9){
                                    this.cancel();
                                    return;
                                }
                                Location jarLoc = nade.getLocation();
                                for(Entity e : nade.getNearbyEntities(5,5,5)){
                                    if(k.distance(jarLoc,e.getLocation()) > 5)continue;
                                    if(e instanceof Projectile || count % 3 == 0) {
                                        k.knockBack(e, nade.getLocation(), -1.5);
                                    }
                                }
                                if(count % 3 == 0) {
                                    w.spawnParticle(Particle.EXPLOSION_LARGE, nade.getLocation(), 1);
                                    w.spawnParticle(Particle.GLOW, nade.getLocation(), 100, 2.5, 2.5, 2.5, 0.2);
                                    w.playSound(nade.getLocation(), Sound.ENTITY_SHULKER_SHOOT, 2, 1);
                                }
                                count += 1;
                            }
                        };
                        suck.runTaskTimer(plugin,0L,5L);
                    }
                }
            };
            land.runTaskTimer(plugin, 0L, 1L);
        }
    }
    public void explosiveMine(Player p, ItemStack hand, int coolDown, Location loc, BlockFace face) {
        World w = p.getWorld();
        Material material = hand.getType();
        Location placeLoc = loc.add(face.getDirection());
        for(Entity e : w.getNearbyEntities(placeLoc,0.5,0.5,0.5)){
            if(e instanceof ItemFrame){
                if(e.getCustomName() != null)return;
            }
        }
        if (p.getCooldown(material) == 0) {
            ItemFrame frame = (ItemFrame) w.spawnEntity(placeLoc, EntityType.ITEM_FRAME);
            frame.setItem(new ItemStack(Material.RED_CONCRETE));
            p.setCooldown(material, coolDown * 20);
            frame.setFixed(true);
            frame.setVisible(false);
            frame.setCustomName(p.getName() + "的地雷");
            if (frame.setFacingDirection(face, true)) {
                BukkitRunnable trigger = new BukkitRunnable() {
                    int count = 0;
                    @Override
                    public void run() {
                        boolean trigger = false;
                        if(count > 2) {
                            if(count % 2 == 0) {
                                w.playSound(placeLoc, Sound.BLOCK_NOTE_BLOCK_BELL, 0.1f, 1.5f);
                                w.spawnParticle(Particle.NOTE, placeLoc, 1);
                            }
                            List<Entity> entities = frame.getNearbyEntities(3, 3, 3);
                            Location jarLoc = frame.getLocation();
                            for (Entity e : entities) {
                                if(k.distance(jarLoc,e.getLocation()) > 3)continue;
                                if (e instanceof LivingEntity l) {
                                    if (l == p) continue;
                                    if (l instanceof Player player) {
                                        if (gameStatus.isTeamMate(p, player)) continue;
                                        if(player.getGameMode() == GameMode.SPECTATOR)continue;
                                        trigger = true;
                                    }
                                }
                            }
                        }
                        if (frame.isDead() || trigger) {
                            if(count > 2) {
                                k.breakBallBlock(p,frame.getLocation(),2,0);
                                k.explode(p, frame, 14, 1, 4);
                                w.spawnParticle(Particle.EXPLOSION_HUGE, loc, 1);
                                w.playSound(placeLoc, Sound.ENTITY_GENERIC_EXPLODE, 2, 1);
                                w.spawnParticle(Particle.EXPLOSION_HUGE, placeLoc, 1);
                                frame.remove();
                            }
                            this.cancel();
                        }
                        count += 1;
                    }
                };
                trigger.runTaskTimer(plugin, 0L, 20L);
            }
        }
    }
    public void gasMine(Player p, ItemStack hand, int coolDown, Location loc, BlockFace face) {
        World w = p.getWorld();
        Material material = hand.getType();
        Location placeLoc = loc.add(face.getDirection());
        for(Entity e : w.getNearbyEntities(placeLoc,0.5,0.5,0.5)){
            if(e instanceof ItemFrame){
                if(e.getCustomName() != null)return;
            }
        }
        if (p.getCooldown(material) == 0) {
            ItemFrame frame = (ItemFrame) w.spawnEntity(placeLoc, EntityType.ITEM_FRAME);
            frame.setItem(new ItemStack(Material.GREEN_CONCRETE));
            p.setCooldown(material, coolDown * 20);
            frame.setFixed(true);
            frame.setVisible(false);
            frame.setCustomName(p.getName() + "的地雷");
            if (frame.setFacingDirection(face, true)) {
                BukkitRunnable trigger = new BukkitRunnable() {
                    int count = 0;
                    @Override
                    public void run() {
                        boolean trigger = false;
                        if(count > 2) {
                            if(count % 2 == 0) {
                                w.playSound(placeLoc, Sound.BLOCK_NOTE_BLOCK_BELL, 0.1f, 1.5f);
                                w.spawnParticle(Particle.NOTE, placeLoc, 1);
                            }
                            List<Entity> entities = frame.getNearbyEntities(3, 3, 3);
                            Location jarLoc = frame.getLocation();
                            for (Entity e : entities) {
                                if(k.distance(jarLoc,e.getLocation()) > 3)continue;
                                if (e instanceof LivingEntity l) {
                                    if (l == p) continue;
                                    if (l instanceof Player player) {
                                        if (gameStatus.isTeamMate(p, player)) continue;
                                        if(player.getGameMode() == GameMode.SPECTATOR)continue;
                                        trigger = true;
                                    }
                                }
                            }
                        }
                        if(frame.isDead() || trigger) {
                            if(count > 2) {
                                k.gas(p, frame, 10, 5);
                                w.playSound(placeLoc, Sound.BLOCK_LANTERN_BREAK, 2, 1);
                                w.spawnParticle(Particle.EXPLOSION_LARGE, placeLoc, 1);
                                frame.remove();
                            }
                            this.cancel();
                        }
                        count += 1;
                    }
                };
                trigger.runTaskTimer(plugin, 0L, 20L);
            }
        }
    }
    public void pyroMine(Player p, ItemStack hand, int coolDown, Location loc, BlockFace face) {
        World w = p.getWorld();
        Material material = hand.getType();
        Location placeLoc = loc.add(face.getDirection());
        for(Entity e : w.getNearbyEntities(placeLoc,0.5,0.5,0.5)){
            if(e instanceof ItemFrame){
                if(e.getCustomName() != null)return;
            }
        }
        if (p.getCooldown(material) == 0) {
            ItemFrame frame = (ItemFrame) w.spawnEntity(placeLoc, EntityType.ITEM_FRAME);
            frame.setItem(new ItemStack(Material.ORANGE_CONCRETE));
            p.setCooldown(material, coolDown * 20);
            frame.setFixed(true);
            frame.setVisible(false);
            frame.setCustomName(p.getName() + "的地雷");
            if (frame.setFacingDirection(face, true)) {
                BukkitRunnable trigger = new BukkitRunnable() {
                    int count = 0;
                    @Override
                    public void run() {
                        boolean trigger = false;
                        if(count > 2) {
                            if(count % 2 == 0) {
                                w.playSound(placeLoc, Sound.BLOCK_NOTE_BLOCK_BELL, 0.1f, 1.5f);
                                w.spawnParticle(Particle.NOTE, placeLoc, 1);
                            }
                            List<Entity> entities = frame.getNearbyEntities(3, 3, 3);
                            Location jarLoc = frame.getLocation();
                            for (Entity e : entities) {
                                if(k.distance(jarLoc,e.getLocation()) > 3)continue;
                                if (e instanceof LivingEntity l) {
                                    if (l == p) continue;
                                    if (l instanceof Player player) {
                                        if (gameStatus.isTeamMate(p, player)) continue;
                                        if(player.getGameMode() == GameMode.SPECTATOR)continue;
                                        trigger = true;
                                    }
                                }
                            }
                        }
                        if(trigger || frame.isDead()) {
                            if(count > 2) {
                                k.fire(p, frame, 7, 3);
                                w.playSound(placeLoc, Sound.ITEM_FIRECHARGE_USE, 2, 1);
                                w.spawnParticle(Particle.EXPLOSION_LARGE, placeLoc, 1);
                                frame.remove();
                            }
                            this.cancel();
                        }
                        count += 1;
                    }
                };
                trigger.runTaskTimer(plugin, 0L, 20L);
            }
        }
    }
    public void glitchGrenade(Player p, ItemStack hand, int coolDown) {
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
            double radius = 4;
            BukkitRunnable land = new BukkitRunnable() {
                @Override
                public void run() {
                    w.spawnParticle(Particle.CRIT_MAGIC, nade.getLocation(), 0);
                    if (nade.isDead()) {
                        this.cancel();
                        w.playSound(nade.getLocation(), Sound.ENTITY_ARMOR_STAND_BREAK, 2, 1);
                        w.spawnParticle(Particle.CRIT_MAGIC, nade.getLocation(), 100);
                        List<Entity> entities = nade.getNearbyEntities(radius, radius, radius);
                        Location jarLoc = nade.getLocation();
                        for (Entity e : entities) {
                            if(k.distance(jarLoc,e.getLocation()) > radius)continue;
                            if (e instanceof ItemFrame i) {
                                i.teleport(i.getLocation().add(0,50,0));
                                i.remove();
                                w.playSound(i.getLocation(), Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, 2, 1);
                                w.spawnParticle(Particle.EXPLOSION_LARGE,i.getLocation(),1);
                                i.teleport(i.getLocation().add(0,50,0));
                            }
                            if(e instanceof Player player){
                                if(p != player) {
                                    if(player.getGameMode() == GameMode.SPECTATOR)continue;
                                    if (gameStatus.isTeamMate(p, player)) continue;
                                }
                                Inventory inv = player.getInventory();
                                for(ItemStack i : inv.getContents()){
                                    if(i == null)continue;
                                    ItemMeta meta = i.getItemMeta();
                                    int weapon = items.getWeaponID(meta.getDisplayName());
                                    if(weapon >= 0)continue;
                                    int coolDown = player.getCooldown(i.getType());
                                    player.setCooldown(i.getType(),100 + coolDown);
                                }
                                player.sendTitle(" ",ChatColor.AQUA + "！被紊乱！",0,20,10);
                                p.sendTitle(" ",ChatColor.AQUA + "紊乱了一位选手！",0,20,10);
                            }
                            if(e instanceof LivingEntity l){
                                if(l.getName().contains("滑索"))continue;
                                if(l instanceof Snowman s){
                                    s.setTarget(null);
                                }
                                BukkitRunnable task = new BukkitRunnable() {
                                    int count = 0;
                                    @Override
                                    public void run() {
                                        if(count > 2){
                                            this.cancel();
                                            return;
                                        }
                                        count += 1;
                                        Location loc = l.getLocation();
                                        float pitch = loc.getPitch();
                                        float yaw = loc.getYaw();
                                        float radPitch = 30;
                                        float radYaw = 50;
                                        if(r.nextBoolean()){
                                            radPitch *= -1;
                                        }
                                        if(r.nextBoolean()) {
                                            radYaw *= -1;
                                        }
                                        if(r.nextBoolean()) {
                                            loc.setPitch(pitch + radPitch);
                                        }
                                        loc.setYaw(yaw + radYaw);
                                        l.teleport(loc);
                                        w.spawnParticle(Particle.CRIT_MAGIC, nade.getLocation(), 50);
                                    }
                                };
                                task.runTaskTimer(plugin,0L,5L);
                            }
                        }
                    }
                }
            };
            land.runTaskTimer(plugin, 0L, 1L);
        }
    }
    public void jumpPad(Player p, ItemStack hand, int coolDown, Location loc, BlockFace face) {
        World w = p.getWorld();
        Material material = hand.getType();
        Location placeLoc = loc.add(face.getDirection());
        if (p.getCooldown(material) == 0) {
            AreaEffectCloud pad = (AreaEffectCloud) w.spawnEntity(placeLoc, EntityType.AREA_EFFECT_CLOUD);
            p.setCooldown(material, coolDown * 20);
            pad.setRadius(2);
            pad.setDuration(400);
            Particle.DustOptions dust = new Particle.DustOptions(Color.AQUA,1);
            pad.setParticle(Particle.REDSTONE,dust);
            BukkitRunnable trigger = new BukkitRunnable() {
                @Override
                public void run() {
                    if (pad.isDead()) {
                        this.cancel();
                        return;
                    }
                    double padX = pad.getLocation().getX();
                    double padY = pad.getLocation().getY();
                    double padZ = pad.getLocation().getZ();
                    double i = Math.PI;
                    for (int j = 0; j <= 100; j++) {
                        double x = padX + (2 * Math.sin(2 * i + 0.5 * j));
                        double z = padZ + (2 * Math.cos(2 * i + 0.5 * j));
                        Location areaP = new Location(w, x, padY, z);
                        w.spawnParticle(Particle.REDSTONE, areaP, 1,dust);
                    }
                    List<Entity> entities = pad.getNearbyEntities(1, 1, 1);
                    boolean jump = false;
                    Location jarLoc = pad.getLocation();
                    for (Entity e : entities) {
                        if(k.distance(jarLoc,e.getLocation()) > 2)continue;
                        if (e instanceof Projectile || e instanceof LivingEntity) {
                            if (e instanceof Player p) {
                                if (p.getGameMode() == GameMode.SPECTATOR) continue;
                            }
                            if (e instanceof Arrow a) {
                                if (a.isInBlock()) continue;
                            }
                            Vector speed = e.getVelocity();
                            bounced.add(e);
                            speed.setY(1.8);
                            if (e instanceof LivingEntity l) {
                                Location eLoc = l.getEyeLocation();
                                Vector eVec = eLoc.getDirection();
                                Vector dash = new Vector(eVec.getX(), 0, eVec.getZ());
                                speed.add(dash.multiply(0.3));
                            }
                            e.setVelocity(speed);
                            jump = true;
                        }
                    }
                    if(jump){
                        w.playSound(pad.getLocation(), Sound.BLOCK_PISTON_EXTEND, 2, 0.8f);
                    }
                }
            };
            trigger.runTaskTimer(plugin, 0L, 5L);
        }
    }
    public void rpg(Player p,ItemStack hand,int coolDown){
        World w = p.getWorld();
        Material material = hand.getType();
        if (p.getCooldown(material) == 0) {
            p.setCooldown(material, coolDown * 20);
            Location shootLoc = p.getEyeLocation();
            Vector shootVec = shootLoc.getDirection();
            Arrow a = w.spawnArrow(shootLoc,shootVec,2,0);
            a.setGravity(false);
            a.setGlowing(true);
            a.setShooter(p);
            a.setCustomName(p.getName() + "的RPG");
            a.setGravity(false);
            a.setDamage(10);
            w.playSound(p.getEyeLocation(),Sound.ENTITY_WITHER_SHOOT,2,1);
            BukkitRunnable hit = new BukkitRunnable() {
                @Override
                public void run() {
                    w.spawnParticle(Particle.EXPLOSION_LARGE,a.getLocation(),1);
                    if(a.getTicksLived() >= 60){
                        a.remove();
                    }
                    if(a.isInBlock() || a.isDead()){
                        a.remove();
                        k.explode(p,a,20,2,6);
                        w.spawnParticle(Particle.EXPLOSION_HUGE,a.getLocation(),1);
                        k.breakBallBlock(p,a.getLocation(),3,0.3);
                        w.playSound(a.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,2,1);
                        this.cancel();
                    }
                }
            };
            hit.runTaskTimer(plugin,0L,1L);
        }
    }
    public void vanishingBomb(Player p, ItemStack hand, int coolDown) {
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
            BukkitRunnable land = new BukkitRunnable() {
                @Override
                public void run() {
                    w.spawnParticle(Particle.SPELL_MOB, nade.getLocation(), 0,1,0,1);
                    if (nade.isDead()) {
                        this.cancel();
                        w.playSound(nade.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 2, 1);
                        Firework firework = (Firework) w.spawnEntity(nade.getLocation(),EntityType.FIREWORK);
                        FireworkMeta meta = firework.getFireworkMeta();
                        meta.setPower(0);
                        meta.addEffect(FireworkEffect.builder()
                                .withColor(Color.YELLOW)
                                .withColor(Color.WHITE)
                                .flicker(true)
                                .with(FireworkEffect.Type.BALL_LARGE).build());
                        firework.setFireworkMeta(meta);
                        firework.detonate();
                        List<Entity> entities = nade.getNearbyEntities(5, 5, 5);
                        Location jarLoc = nade.getLocation();
                        for (Entity e : entities) {
                            if(k.distance(jarLoc,e.getLocation()) > 5)continue;
                            if(e instanceof Player player){
                                if(gameStatus.isTeamMate(p,player)){
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,24,0));
                                    long delay;
                                    if(player == p){
                                        delay = 155L;
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,160,0,false));
                                    }else {
                                        delay = 195L;
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,200,0,false));
                                    }
                                    k.removeEquipment(player);
                                    player.setArrowsInBody(0);
                                    BukkitRunnable setArmor = new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            if(player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                                k.setPlayerEquipment(player);
                                                player.playSound(player.getLocation(),Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
                                                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,24,0));
                                            }
                                        }
                                    };
                                    setArmor.runTaskLater(plugin,delay);
                                    BukkitRunnable sound = new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            if(!player.hasPotionEffect(PotionEffectType.INVISIBILITY)){
                                                this.cancel();
                                                return;
                                            }
                                            w.playSound(player.getEyeLocation(),Sound.BLOCK_BEACON_AMBIENT,1,1);
                                        }
                                    };
                                    sound.runTaskTimer(plugin,5L,30L);
                                }
                            }
                        }
                    }
                }
            };
            land.runTaskTimer(plugin, 0L, 1L);
        }
    }
    public void aps(Player p,ItemStack hand,int coolDown){
        World w = p.getWorld();
        Material material = hand.getType();
        if (p.getCooldown(material) == 0) {
            p.setCooldown(material, coolDown * 20);
            ArmorStand aps = (ArmorStand) w.spawnEntity(p.getLocation(),EntityType.ARMOR_STAND);
            w.playSound(aps.getLocation(),Sound.BLOCK_BEACON_ACTIVATE,1,1);
            aps.setSmall(true);
            aps.setInvulnerable(true);
            aps.setCustomName(p.getName() + "的APS");
            aps.setCustomNameVisible(true);
            EntityEquipment equipment = aps.getEquipment();
            equipment.setHelmet(new ItemStack(Material.BEACON));
            equipment.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
            equipment.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            equipment.setBoots(new ItemStack(Material.IRON_BOOTS));
            BukkitRunnable defend = new BukkitRunnable() {
                int count = 0;
                @Override
                public void run() {
                    if(aps.isDead() || aps.getTicksLived() >= 600){
                        aps.remove();
                        this.cancel();
                    }
                    int team = playerStats.getTeam(p);
                    Color c = switch (team) {
                        case 0 -> Color.AQUA;
                        case 1 -> Color.ORANGE;
                        case 2 -> Color.FUCHSIA;
                        default -> Color.WHITE;
                    };
                    if(count % 3 == 0) {
                        Particle.DustOptions dust = new Particle.DustOptions(c, 1);
                        double padX = aps.getLocation().getX();
                        double padY = aps.getLocation().getY();
                        double padZ = aps.getLocation().getZ();
                        double i = Math.PI;
                        for (int j = 0; j <= 100; j++) {
                            double x = padX + (5 * Math.sin(5 * i + 0.5 * j));
                            double z = padZ + (5 * Math.cos(5 * i + 0.5 * j));
                            Location areaP = new Location(w, x, padY, z);
                            w.spawnParticle(Particle.REDSTONE, areaP, 1, dust);
                        }
                        w.spawnParticle(Particle.REDSTONE, aps.getLocation(), 100, 2.5, 2.5, 2.5, dust);
                    }
                    count += 1;
                    Location jarLoc = aps.getLocation();
                    for(Entity e : aps.getNearbyEntities(5,5,5)){
                        if(k.distance(jarLoc,e.getLocation()) > 3)continue;
                        if(e instanceof Projectile pro){
                            if(pro.getShooter() instanceof Player p1){
                                if(gameStatus.isTeamMate(p,p1))continue;
                            }
                            Location loc = pro.getLocation();
                            w.playSound(aps.getLocation(),Sound.ENTITY_ARROW_HIT,1,1);
                            w.playSound(aps.getLocation(),Sound.BLOCK_BEACON_DEACTIVATE,1,1);
                            w.spawnParticle(Particle.EXPLOSION_LARGE,pro.getLocation(),1);
                            pro.teleport(loc.add(0,50,0));
                            pro.remove();
                        }
                        if(e instanceof ArmorStand a){
                            if(a.getName().contains("罐")) {
                                if(!a.isOnGround()) {
                                    Location loc = a.getLocation();
                                    w.spawnParticle(Particle.EXPLOSION_LARGE, a.getLocation(), 1);
                                    w.playSound(aps.getLocation(), Sound.ENTITY_ARMOR_STAND_BREAK, 1, 1);
                                    w.playSound(aps.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1, 1);
                                    a.teleport(loc.add(0,50,0));
                                    a.remove();
                                }
                            }
                        }
                    }
                }
            };
            defend.runTaskTimer(plugin,0L,2L);
        }
    }
    public void plantC4(Player p,ItemStack hand,Block block,BlockFace face,Entity entity) {
        World w = p.getWorld();
        Material material = hand.getType();
        if (p.getCooldown(material) == 0) {
            Entity c4 = playerC4.getOrDefault(p.getName(), null);
            if (c4 == null) {//没有安放C4
                if (block != null) {
                    Location bLoc = block.getLocation();
                    Location placeLoc = bLoc.add(face.getDirection());
                    for(Entity e : w.getNearbyEntities(placeLoc,0.5,0.5,0.5)){
                        if(e instanceof ItemFrame){
                            if(e.getCustomName() != null)return;
                        }
                    }
                    p.setCooldown(material, 20);
                    ItemFrame frame = (ItemFrame) w.spawnEntity(placeLoc, EntityType.ITEM_FRAME);
                    frame.setItem(new ItemStack(Material.TNT));
                    frame.setFixed(true);
                    frame.setVisible(false);
                    frame.setCustomName(p.getName() + "的C4");
                    if (frame.setFacingDirection(face, true)) {
                        playerC4.put(p.getName(), frame);
                        p.sendTitle(" ", ChatColor.RED + "C4已安放", 0, 20, 10);
                        w.playSound(frame.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.8f);
                    }
                } else if (entity != null) {
                    if(!k.isC4Planted(entity)) {
                        if (p.isSneaking()) {
                            if (entity instanceof Player) return;
                            if (entity.getName().contains("传送门") || entity.getName().contains("滑索"))return;
                            if (entity instanceof LivingEntity l) {
                                p.setCooldown(material, 50);
                                k.setC4Planted(l);
                                playerC4.put(p.getName(), l);
                                p.sendTitle(" ", ChatColor.RED + "C4已安放", 0, 20, 10);
                                w.playSound(l.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
                                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.8f);
                            }
                        }
                    }
                }
            } else {//安放了C4
                if (c4.isDead()) {
                    p.sendTitle(" ", ChatColor.RED + "C4被摧毁了", 0, 20, 10);
                    p.setCooldown(material, 100);
                } else {
                    w.playSound(c4.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2, 1);
                    k.breakBallBlock(p, c4.getLocation(), 3,0.3);
                    k.explode(p, c4, 20, 3, 5);
                    w.spawnParticle(Particle.EXPLOSION_HUGE,c4.getLocation(),1);
                    if(c4 instanceof LivingEntity l){
                        k.setEntityHelm(l);
                        p.setCooldown(material, 500);
                    }else {
                        p.setCooldown(material, 600);
                    }
                }
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.8f);
                playerC4.remove(p.getName());
            }
        }
    }
    public void stunGun(Player p,ItemStack hand,int coolDown) {
        World w = p.getWorld();
        Material material = hand.getType();
        if (p.getCooldown(material) == 0) {
            p.setCooldown(material, coolDown * 20);
            Location shootLoc = p.getEyeLocation();
            Vector shootVec = shootLoc.getDirection();
            Arrow a = w.spawnArrow(shootLoc, shootVec, 5, 0);
            a.setGravity(false);
            a.setGlowing(true);
            a.setShooter(p);
            a.setCustomName(p.getName() + "的电击枪");
            a.setGravity(false);
            a.setDamage(0);
            a.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            w.playSound(p.getEyeLocation(), Sound.ITEM_CROSSBOW_SHOOT, 2, 1);
            Location pLoc = shootLoc.clone();
            for (int i = 0; i < 10; i++) {
                w.spawnParticle(Particle.ELECTRIC_SPARK, pLoc, 0);
                pLoc.add(shootVec.clone());
            }
            BukkitRunnable hit = new BukkitRunnable() {
                @Override
                public void run() {
                    w.spawnParticle(Particle.ELECTRIC_SPARK, a.getLocation(), 0);
                    if (a.getTicksLived() >= 10 || a.isDead() || a.isInBlock()) {
                        a.remove();
                        this.cancel();
                    }
                    Entity e = hitEntity.getOrDefault(a,null);
                    if(e != null) {
                        if (e instanceof Player p1) {
                            if (gameStatus.isTeamMate(p, p1))return;
                        }
                        if (e instanceof LivingEntity l) {
                            a.remove();
                            l.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 70, 4));
                            l.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 70, 4));
                            w.playSound(l.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1, 1);
                            w.playSound(l.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1, 1);
                            BukkitRunnable particle = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if(!l.hasPotionEffect(PotionEffectType.SLOW) || l.isDead()){
                                        this.cancel();
                                        return;
                                    }
                                    w.spawnParticle(Particle.ELECTRIC_SPARK,l.getLocation(),20,0.5,1.5,0.5,0);
                                }
                            };
                            particle.runTaskTimer(plugin,0L,5L);
                            if (e instanceof Player p1) {
                                p1.sendTitle(" ", ChatColor.RED + "！被眩晕！", 0, 20, 10);
                                p.sendTitle(" ",ChatColor.AQUA + "眩晕了一位选手！",0,20,10);
                                Inventory inv = p1.getInventory();
                                for(ItemStack i : inv.getContents()) {
                                    if (i == null) continue;
                                    ItemMeta meta = i.getItemMeta();
                                    int weapon = items.getWeaponID(meta.getDisplayName());
                                    int skill = items.getSkillID(meta.getDisplayName());
                                    int coolDown = p1.getCooldown(i.getType());
                                    if (skill >= 0) {
                                        p1.setCooldown(i.getType(), 100 + coolDown);
                                    }
                                    if(weapon >= 0){
                                        p1.setCooldown(i.getType(), 30 + coolDown);
                                    }
                                }
                            }
                        }
                        this.cancel();
                    }
                }
            };
            hit.runTaskTimer(plugin, 0L, 1L);
        }
    }
    public void glitchMine(Player p, ItemStack hand, int coolDown, Location loc, BlockFace face) {
        World w = p.getWorld();
        Material material = hand.getType();
        Location placeLoc = loc.add(face.getDirection());
        for(Entity e : w.getNearbyEntities(placeLoc,0.5,0.5,0.5)){
            if(e instanceof ItemFrame){
                if(e.getCustomName() != null)return;
            }
        }
        if (p.getCooldown(material) == 0) {
            ItemFrame frame = (ItemFrame) w.spawnEntity(placeLoc, EntityType.ITEM_FRAME);
            frame.setItem(new ItemStack(Material.LIGHT_BLUE_CONCRETE));
            p.setCooldown(material, coolDown * 20);
            frame.setFixed(true);
            frame.setVisible(false);
            frame.setCustomName(p.getName() + "的地雷");
            if (frame.setFacingDirection(face, true)) {
                BukkitRunnable trigger = new BukkitRunnable() {
                    int count = 0;
                    @Override
                    public void run() {
                        boolean trigger = false;
                        if(count > 2) {
                            if(count % 2 == 0) {
                                w.playSound(placeLoc, Sound.BLOCK_NOTE_BLOCK_BELL, 0.1f, 1.5f);
                                w.spawnParticle(Particle.NOTE, placeLoc, 1);
                            }
                            List<Entity> entities = frame.getNearbyEntities(5, 5, 5);
                            Location jarLoc = frame.getLocation();
                            for (Entity e : entities) {
                                if(k.distance(jarLoc,e.getLocation()) > 5)continue;
                                if (e instanceof LivingEntity l) {
                                    if (l == p) continue;
                                    if (l instanceof Player player) {
                                        if (gameStatus.isTeamMate(p, player)) continue;
                                        if(player.getGameMode() == GameMode.SPECTATOR)continue;
                                        trigger = true;
                                    }
                                }
                            }
                        }
                        if(frame.isDead() || trigger) {
                            if(count > 2) {
                                k.glitch(p,frame,2,5,20);
                            }
                            this.cancel();
                        }
                        count += 1;
                    }
                };
                trigger.runTaskTimer(plugin, 0L, 20L);
            }
        }
    }
    public void domeShield(Player p,ItemStack hand,int coolDown){
        World w = p.getWorld();
        Material material = hand.getType();
        double radius = 3.5;
        if (p.getCooldown(material) == 0) {
            p.setCooldown(material, coolDown * 20);
            ArmorStand dome = (ArmorStand) w.spawnEntity(p.getLocation(),EntityType.ARMOR_STAND);
            dome.setSmall(true);
            dome.setInvulnerable(true);
            dome.setCustomName(p.getName() + "的球形护盾");
            dome.setCustomNameVisible(true);
            EntityEquipment equipment = dome.getEquipment();
            equipment.setHelmet(new ItemStack(Material.SEA_LANTERN));
            equipment.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
            equipment.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
            equipment.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
            w.playSound(p.getEyeLocation(),Sound.BLOCK_CONDUIT_ACTIVATE,1,1);
            Location jarLoc = dome.getLocation();
            for(Entity e : dome.getNearbyEntities(3,3,3)){
                if(k.distance(jarLoc,e.getLocation()) > 3)continue;
                if(e instanceof Player p1){
                    if(gameStatus.isTeamMate(p,p1)){
                        double absorption = p1.getAbsorptionAmount();
                        if(absorption + 6 >= 15){
                            p1.setAbsorptionAmount(15);
                        }else p1.setAbsorptionAmount(6 + absorption);
                    }
                }
            }
            BukkitRunnable defend = new BukkitRunnable() {
                int count = 0;
                @Override
                public void run() {
                    if(dome.isDead() || dome.getTicksLived() >= coolDown * 10){
                        dome.remove();
                        this.cancel();
                    }
                    int team = playerStats.getTeam(p);
                    Color c = switch (team) {
                        case 0 -> Color.AQUA;
                        case 1 -> Color.ORANGE;
                        case 2 -> Color.FUCHSIA;
                        default -> Color.WHITE;
                    };
                    if(count % 5 == 0) {
                        Particle.DustOptions dust = new Particle.DustOptions(c, 0.7f);
                        double padX = dome.getLocation().getX();
                        double padY = dome.getLocation().getY();
                        double padZ = dome.getLocation().getZ();
                        double i = Math.PI;
                        for (int j = 0; j <= 100; j++) {
                            double x = padX + (radius * Math.sin(radius * i + 0.5 * j));
                            double z = padZ + (radius * Math.cos(radius * i + 0.5 * j));
                            Location areaP = new Location(w, x, padY, z);
                            w.spawnParticle(Particle.REDSTONE, areaP, 1, dust);
                        }
                        w.spawnParticle(Particle.REDSTONE, dome.getLocation(), 100, radius / 2, radius / 2, radius / 2, dust);
                    }
                    count += 1;
                    for(Entity e : dome.getNearbyEntities(radius,radius,radius)){
                        if(k.distance(jarLoc,e.getLocation()) > radius)continue;
                        if(e instanceof Projectile pro){
                            if(pro.getShooter() instanceof Player p1){
                                if(gameStatus.isTeamMate(p,p1))continue;
                            }
                            Vector speed = pro.getVelocity();
                            pro.setVelocity(speed.multiply(-0.8));
                            pro.setShooter(p);
                            w.playSound(dome.getLocation(),Sound.BLOCK_CONDUIT_DEACTIVATE,1,1);
                        }
                    }
                }
            };
            defend.runTaskTimer(plugin,0L,1L);
        }
    }
    public void gateway(Player p,ItemStack hand,int coolDown){
        World w = p.getWorld();
        ArmorStand gate1 = playerGateway.getOrDefault(p.getName(),null);
        Location shootLoc = p.getEyeLocation();
        Vector shootVec = shootLoc.getDirection();
        if(p.getCooldown(hand.getType()) == 0) {
            p.setCooldown(hand.getType(), 20);
            if (gate1 != null) {
                if(k.distance(p.getLocation(),gate1.getLocation()) > 40) {
                    gate1.remove();
                    playerGateway.remove(p.getName());
                }
                ArmorStand gate2 = gatewayToGateway.getOrDefault(gate1,null);
                if(gate2 != null){
                    gate1.remove();
                    gate2.remove();
                    gatewayToGateway.remove(gate1);
                    gatewayToGateway.remove(gate2);
                    playerGateway.remove(p.getName());
                }
            }
            Snowball ball = (Snowball) w.spawnEntity(shootLoc, EntityType.SNOWBALL);
            ball.setItem(new ItemStack(Material.ENDER_PEARL));
            ball.setShooter(p);
            ball.setVelocity(shootVec.multiply(2));
            w.playSound(p.getLocation(),Sound.ENTITY_EGG_THROW,1,1);
            BukkitRunnable hit = new BukkitRunnable() {
                @Override
                public void run() {
                    Particle.DustOptions dust = new Particle.DustOptions(Color.PURPLE,1);
                    w.spawnParticle(Particle.REDSTONE,ball.getLocation(),1,dust);
                    if (ball.isDead() || k.hitSquareBlock(ball) || ball.getTicksLived() >= 10) {
                        w.playSound(ball.getLocation(),Sound.BLOCK_LANTERN_BREAK,1,1);
                        ball.remove();
                        this.cancel();
                        ArmorStand gateway = (ArmorStand) w.spawnEntity(ball.getLocation(), EntityType.ARMOR_STAND);
                        EntityEquipment equipment = gateway.getEquipment();
                        gateway.setCustomName(ChatColor.LIGHT_PURPLE + p.getName() + "的传送门");
                        gateway.setGravity(false);
                        gateway.setInvulnerable(true);
                        gateway.setInvisible(true);
                        gateway.setSmall(true);
                        equipment.setHelmet(new ItemStack(Material.CHORUS_PLANT));
                        if (gate1 != null && !gate1.isDead()) {
                            gatewayEntity.clear();
                            w.playSound(gate1.getEyeLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 1);
                            w.playSound(gateway.getEyeLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 1);
                            gatewayToGateway.put(gate1, gateway);
                            gatewayToGateway.put(gateway, gate1);
                            startGateWay(gateway, 0L);
                            startGateWay(gate1, 5L);
                            p.setCooldown(hand.getType(), coolDown * 20);
                        }
                        playerGateway.put(p.getName(), gateway);
                    }
                }
            };
            hit.runTaskTimer(plugin,0L,1L);
        }
    }
    public void startGateWay(ArmorStand a,long delay){
        World w = a.getWorld();
        EntityEquipment equipment = a.getEquipment();
        equipment.setHelmet(new ItemStack(Material.CHORUS_FLOWER));
        BukkitRunnable start = new BukkitRunnable() {
            @Override
            public void run() {
                if(a.isDead() || a.getTicksLived() > 1200){
                    a.remove();
                    this.cancel();
                    return;
                }
                w.spawnParticle(Particle.PORTAL,a.getLocation(),100,1.5,1.5,1.5,0);
                ArmorStand gate = gatewayToGateway.getOrDefault(a,null);
                if(gate != null) {
                    for (Entity e : a.getNearbyEntities(3, 3, 3)) {
                        if(!e.hasGravity())continue;
                        ArmorStand lastGate = gatewayEntity.getOrDefault(e,null);
                        if(lastGate == null || lastGate == a) {
                            if (e instanceof Projectile || e.getName().contains("钱箱") || e.getName().contains("罐")) {
                                Vector spread = new Vector(r.nextDouble(1) - r.nextDouble(1),
                                       0,
                                        r.nextDouble(1) - r.nextDouble(1));
                                Vector speed = e.getVelocity();
                                e.teleport(gate.getEyeLocation().add(spread.normalize().multiply(0.5)));
                                e.setVelocity(speed.multiply(1.1));
                                w.playSound(a.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                                w.playSound(gate.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                                gatewayEntity.put(e, a);
                            }
                        }
                    }
                }
            }
        };
        start.runTaskTimer(plugin,delay,5L);
    }
    public void milk(Player p,ItemStack hand){
        World w = p.getWorld();
        if(p.getCooldown(hand.getType()) == 0) {
            if (p.isSneaking()) {
                p.setFireTicks(0);
                p.setCooldown(hand.getType(), 500);
                w.playSound(p.getLocation(), Sound.ENTITY_GENERIC_DRINK, 1, 1);
                w.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
                w.spawnParticle(Particle.ITEM_CRACK, p.getLocation(), 20, 0.5, 1.5, 0.5, 0, new ItemStack(Material.SNOW_BLOCK));
                p.removePotionEffect(PotionEffectType.SLOW);
                p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                p.removePotionEffect(PotionEffectType.WEAKNESS);
            } else {
                p.setCooldown(hand.getType(), 400);
                Location shootLoc = p.getEyeLocation();
                Vector shootVec = shootLoc.getDirection();
                Snowball ball = (Snowball) w.spawnEntity(shootLoc, EntityType.SNOWBALL);
                ball.setItem(new ItemStack(Material.MILK_BUCKET));
                ball.setShooter(p);
                ball.setVelocity(shootVec.multiply(2));
                w.playSound(p.getLocation(), Sound.ENTITY_EGG_THROW, 1, 1);
                BukkitRunnable hit = new BukkitRunnable() {
                    @Override
                    public void run() {
                        w.spawnParticle(Particle.ITEM_CRACK, ball.getLocation(), 0, new ItemStack(Material.MILK_BUCKET));
                        if (ball.isDead()) {
                            w.playSound(ball.getLocation(),Sound.ITEM_BOTTLE_FILL,1,1);
                            w.spawnParticle(Particle.ITEM_CRACK, ball.getLocation(), 100, 1.5, 1.5, 1.5, 0, new ItemStack(Material.SNOW_BLOCK));
                            this.cancel();
                            boolean hit = false;
                            for (Entity e : ball.getNearbyEntities(5, 5, 5)) {
                                if (k.distance(e.getLocation(), ball.getLocation()) > 5) continue;
                                if (e instanceof Player p1) {
                                    if (p == p1) continue;
                                    if (gameStatus.isTeamMate(p, p1)) {
                                        hit = true;
                                        w.playSound(p1.getLocation(), Sound.ENTITY_GENERIC_DRINK, 1, 1);
                                        w.playSound(p1.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
                                        w.spawnParticle(Particle.ITEM_CRACK, p1.getLocation(), 20, 0.5, 1.5, 0.5, 0, new ItemStack(Material.SNOW_BLOCK));
                                        p1.removePotionEffect(PotionEffectType.SLOW);
                                        p1.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                                        p1.removePotionEffect(PotionEffectType.WEAKNESS);
                                        p1.setFireTicks(0);
                                        p.sendTitle(" ","负面效果被移除了",10,20,10);
                                    }
                                }
                            }
                            if(hit){
                                p.playSound(p.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,0.7f);
                                p.sendTitle(" ","清除了队友的负面效果",10,20,10);
                            }
                        }
                    }
                };
                hit.runTaskTimer(plugin, 0L, 1L);
            }
        }
    }
    public void ross(Player p,ItemStack hand,int coolDown){
        World w = p.getWorld();
        Material material = hand.getType();
        double radius = 2;
        if (p.getCooldown(material) == 0) {
            p.setCooldown(material, coolDown * 20);
            BukkitRunnable shoot = new BukkitRunnable() {
                int count = 0;
                @Override
                public void run() {
                    if(count > 2){
                        Location shootLoc = p.getEyeLocation();
                        Vector shootVec = shootLoc.getDirection();
                        Location pLoc = shootLoc.clone();
                        w.playSound(p.getLocation(),Sound.ENTITY_WITHER_SHOOT,2,1);
                        for (int i = 0; i < 30; i++) {
                            for(Entity e : w.getNearbyEntities(pLoc,radius,radius,radius)){
                                if(e instanceof ItemFrame){
                                    e.remove();
                                }
                                if(e instanceof Snowman s){
                                    s.setHealth(0);
                                }
                                if(e instanceof ArmorStand) {
                                    if(e.getName().contains("滑索"))continue;
                                    if (e.getName().contains("APS") ||
                                            e.getName().contains("球形护盾")) {
                                        e.remove();
                                    }
                                }
                                if(e instanceof Player player){
                                    if(gameStatus.isTeamMate(p,player))continue;
                                    Inventory inv = player.getInventory();
                                    for(ItemStack item : inv.getContents()){
                                        if(item == null)continue;
                                        ItemMeta meta = item.getItemMeta();
                                        int weapon = items.getWeaponID(meta.getDisplayName());
                                        if(weapon >= 0)continue;
                                        player.setCooldown(item.getType(),40);
                                    }
                                    player.sendTitle(" ",ChatColor.AQUA + "！被紊乱！",0,20,10);
                                    p.sendTitle(" ",ChatColor.AQUA + "紊乱了一位选手！",0,20,10);
                                    Location loc = player.getLocation();
                                    loc.setPitch(r.nextInt(180) - 90);
                                    loc.setYaw(r.nextInt(180) - 90);
                                    player.teleport(loc);
                                }
                            }
                            Block b = w.getBlockAt(pLoc);
                            if(b.getType() != Material.LIGHT && b.getType() != Material.AIR) {
                                if (!k.isBreakable(p, b)) {
                                    break;
                                }
                            }
                            w.spawnParticle(Particle.EXPLOSION_LARGE, pLoc, 1);
                            k.breakBallBlock(p,pLoc, (int) radius,0.3);
                            pLoc.add(shootVec.clone());
                        }
                        this.cancel();
                    }else {
                        w.playSound(p.getLocation(),Sound.BLOCK_NOTE_BLOCK_PLING,1,1);
                    }
                    count += 1;
                }
            };
            shoot.runTaskTimer(plugin,0L,15L);
        }
    }
    public void shuriken(Player p,ItemStack hand,int coolDown) {
        World w = p.getWorld();
        Material material = hand.getType();
        if (p.getCooldown(material) == 0) {
            p.setCooldown(material, coolDown * 20);
            List<Location>locs = new ArrayList<>();
            for(int i = -1;i <= 1;i ++){
                Location loc = p.getEyeLocation();
                float yaw = loc.getYaw();
                yaw += i * 5;
                loc.setYaw(yaw);
                locs.add(loc);
            }
            BukkitRunnable shoot = new BukkitRunnable() {
                int count = 0;
                @Override
                public void run() {
                    if(count >= locs.size()){
                        this.cancel();
                        return;
                    }
                    w.playSound(p.getLocation(),Sound.ITEM_TRIDENT_THROW,1,1.4f + count * 0.1f);
                    Location shootLoc = locs.get(count);
                    Vector shootVec = shootLoc.getDirection();
                    Arrow a = w.spawnArrow(shootLoc,shootVec,3,0);
                    a.addCustomEffect(new PotionEffect(PotionEffectType.LEVITATION,20,4),false);
                    a.addCustomEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,60,0),false);
                    a.addCustomEffect(new PotionEffect(PotionEffectType.SLOW,60,0),false);
                    a.setDamage(2);
                    a.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
                    a.setShooter(p);
                    a.setCustomName("漂浮手里剑");
                    count += 1;
                }
            };
            shoot.runTaskTimer(plugin,0L,1L);
        }
    }
    public void snowNade(Player p, ItemStack hand, int coolDown){
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
            BukkitRunnable land = new BukkitRunnable() {
                @Override
                public void run() {
                    w.spawnParticle(Particle.ITEM_CRACK, nade.getLocation(), 0,hand);
                    if (nade.isDead() || k.hitSquareBlock(nade) || nade.getTicksLived() > 15) {
                        Location loc = nade.getLocation().add(nade.getVelocity().normalize().multiply(-2));
                        k.snow(p, loc.clone(), 2);
                        nade.remove();
                        this.cancel();
                    }
                }
            };
            land.runTaskTimer(plugin, 5L, 1L);
        }
    }
    public void deepSensor(Player p,ItemStack hand,int coolDown,Location loc,BlockFace face){
        World w = p.getWorld();
        Material material = hand.getType();
        Location placeLoc = loc.add(face.getDirection());
        for(Entity e : w.getNearbyEntities(placeLoc,0.5,0.5,0.5)){
            if(e instanceof ItemFrame){
                if(e.getCustomName() != null)return;
            }
        }
        if (p.getCooldown(material) == 0) {
            ItemFrame frame = (ItemFrame) w.spawnEntity(placeLoc, EntityType.ITEM_FRAME);
            frame.setItem(new ItemStack(Material.ENDER_EYE));
            p.setCooldown(material, coolDown * 20);
            frame.setFixed(true);
            frame.setVisible(false);
            frame.setCustomName(p.getName() + "的传感器");
            if (frame.setFacingDirection(face, true)) {
                BukkitRunnable trigger = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(frame.getTicksLived() >= 300 || frame.isDead()){
                            frame.remove();
                            this.cancel();
                            w.playSound(frame.getLocation(),Sound.ENTITY_ENDER_EYE_DEATH,1,1);
                            return;
                        }
                        frame.setGlowing(false);
                        boolean trigger = false;
                        w.playSound(frame.getLocation(),Sound.BLOCK_END_PORTAL_FRAME_FILL,1,1);
                        double radius = 6;
                        for(Entity e : frame.getNearbyEntities(radius,radius,radius)){
                            if(k.distance(frame.getLocation(),e.getLocation())>radius)continue;
                            if(e instanceof Player p1){
                                if(gameStatus.isTeamMate(p,p1))continue;
                                if(p1.getGameMode() == GameMode.SPECTATOR)continue;
                            }
                            if(e instanceof LivingEntity l){
                                Location lLoc = l.getLocation();
                                Location lELoc = l.getEyeLocation();
                                Location fLoc = frame.getLocation();
                                if(!k.rayTraceBlock(lLoc,fLoc) || !k.rayTraceBlock(lELoc,fLoc)){
                                    frame.setGlowing(true);
                                    l.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,60,0));
                                    l.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,30,0));
                                    if(l instanceof Player p2){
                                        p2.sendTitle(" ","！被探测！",0,20,10);
                                        Location pLoc = p2.getEyeLocation();
                                        Vector pVec = pLoc.toVector();
                                        Vector subVec = pVec.subtract(fLoc.toVector());
                                        Location subLoc = fLoc.clone();
                                        for(int i = 0;i < subVec.length();i ++){
                                            p2.spawnParticle(Particle.END_ROD,subLoc,0);
                                            subLoc.add(subVec.clone().normalize());
                                        }
                                    }
                                    trigger = true;
                                }
                            }
                        }
                        if(trigger){
                            p.playSound(p.getLocation(),Sound.BLOCK_END_PORTAL_FRAME_FILL,1,1);
                            p.sendTitle(" ","！探测到敌人！",0,20,10);
                        }
                    }
                };
                trigger.runTaskTimer(plugin, 0L, 30L);
            }
        }
    }
    public void gooBallGun(Player p,ItemStack hand){
        World w = p.getWorld();
        ItemMeta handMeta = hand.getItemMeta();
        int itemID = items.getItemID(handMeta.getDisplayName());
        if(p.getCooldown(hand.getType()) == 0) {
            int ammo = playerStats.getItemAmmo(p, itemID);
            if (ammo < 0) {
                ammo = 15;
            }
            if(ammo == 0) {
                p.setCooldown(hand.getType(), 200);
                w.playSound(p.getLocation(), Sound.BLOCK_SMITHING_TABLE_USE, 1, 1);
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(ChatColor.WHITE + "重新装填中"));
                playerStats.setItemAmmo(p, itemID, 15);
            }else {
                p.setCooldown(hand.getType(),5);
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(ChatColor.WHITE + "剩余弹药：" + (ammo - 1)));
                ammo -= 1;
                playerStats.setItemAmmo(p,itemID,ammo);
            }
            Location eyeLoc = p.getEyeLocation();
            Vector eyeVec = eyeLoc.getDirection();
            ItemStack ball = new ItemStack(Material.SNOWBALL);
            Snowball goo = (Snowball) w.spawnEntity(eyeLoc,EntityType.SNOWBALL);
            goo.setItem(new ItemStack(ball));
            goo.setVelocity(eyeVec.multiply(2));
            w.playSound(p.getLocation(),Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM,1,1);
            BukkitRunnable hit = new BukkitRunnable() {
                @Override
                public void run() {
                    w.spawnParticle(Particle.ITEM_CRACK,goo.getLocation(),0,ball);
                    if(k.hitSquareBlock(goo) || goo.isDead()){
                        w.spawnParticle(Particle.ITEM_CRACK, goo.getLocation(), 20, 0, 0, 0, 0.1, ball);
                        for(Entity e : goo.getNearbyEntities(2,2,2)){
                            if(e instanceof LivingEntity l){
                                if(l instanceof Player player){
                                    if(gameStatus.isTeamMate(p,player))continue;
                                }
                                int time = 0;
                                if(l.hasPotionEffect(PotionEffectType.SLOW)){
                                    time = l.getPotionEffect(PotionEffectType.SLOW).getDuration();
                                }
                                l.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,time + 20,2));
                                w.playSound(l.getLocation(),Sound.ENTITY_PLAYER_HURT_FREEZE,1,1);
                                p.playSound(p.getLocation(),Sound.ENTITY_PLAYER_HURT_FREEZE,1,1);
                            }
                        }
                        k.goo(p,goo.getLocation(),1);
                        this.cancel();
                    }
                }
            };
            hit.runTaskTimer(plugin,2L,1L);
        }
    }
    public void indicator(Player p,ItemStack hand,boolean rightClick) {
        World w = p.getWorld();
        Material material = hand.getType();
        if (p.getCooldown(material) == 0) {
            p.setCooldown(material, 100);
            Location pLoc = p.getLocation();
            int x = pLoc.getBlockX();
            int y = pLoc.getBlockY();
            int z = pLoc.getBlockZ();
            int team = playerStats.getTeam(p);
            String hint = "";
            if (rightClick) {
                if (p.isSneaking()) {
                    hint = ChatColor.YELLOW + "" + ChatColor.BOLD + "防守这里";
                } else {
                    hint = ChatColor.GREEN + "" + ChatColor.BOLD + "需要支援";
                }
            } else {
                if (p.isSneaking()) {
                    hint = ChatColor.AQUA + "" + ChatColor.BOLD + "进攻这里";
                } else {
                    hint = ChatColor.RED + "" + ChatColor.BOLD + "发现敌人";
                }
            }
            for (Player p1 : w.getPlayers()) {
                int p1Team = playerStats.getTeam(p1);
                if (p1Team == team) {
                    if(p1 != p) {
                        double distance = k.distance(pLoc, p1.getLocation());
                        String message = ChatColor.GREEN + p.getName()
                                + ChatColor.WHITE+"在"
                                + ChatColor.LIGHT_PURPLE
                                + ChatColor.BOLD + "[" + x + "," + y + "," + z + "]"
                                + ChatColor.WHITE + "距离："
                                + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + String.format("%.2f", distance)
                                + ChatColor.WHITE + "说：" + hint;
                        String title = p.getName() + "说：" + hint;
                        p1.sendMessage(message);
                        p1.sendTitle(" ", title, 10, 40, 10);
                        p1.playSound(p1.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    }else {
                        p.sendMessage("你发送了：" + hint);
                        p1.playSound(p1.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    }
                }
            }
        }
    }
    public void summonDragon(Player p){
        World w = p.getWorld();
        Location loc = p.getLocation();
        BukkitRunnable later = new BukkitRunnable() {
            @Override
            public void run() {
                Skeleton dragon = (Skeleton) w.spawnEntity(loc,EntityType.SKELETON);
                dragon.setCustomName(ChatColor.AQUA + "桐生一马");
                Bukkit.getPluginManager().callEvent(new EntitySpawnEvent(dragon));
            }
        };
        later.runTaskLater(plugin,100L);
    }
}

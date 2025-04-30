package Universal;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.UUID;

public class Items {
    private static Items instance = new Items();
    private Items(){}
    public static Items getInstance() {
        return instance;
    }
    public ItemStack[] getItems() {
        return items.clone();
    }

    ItemStack[]items = {
            //武器
            saber(),
            dagger(),
            longBow(),
            throwingKnives(),
            bugSword(),
            sniper(),
            stick(),
            shield(),
            dualBlade(),
            grenadeCrossbow(),
            revolver(),
            glitchStick(),
            blunderbuss(),
            hammer(),
            spear(),
            flameThrower(),
            errorHook(),
            doubleBarrel(),
            slugShotgun(),
            //道具
            grenade(),
            gasGrenade(),
            gooGrenade(),
            fireGrenade(),
            smokeGrenade(),
            blindGrenade(),
            impactGrenade(),
            explosiveMine(),
            gasMine(),
            pyroMine(),
            glitchGrenade(),
            jumpPad(),
            rpg(),
            vanishingBomb(),
            APS(),
            C4(),
            stunGun(),
            glitchMine(),
            domeShield(),
            gravityGrenade(),
            gateway(),
            debug(),
            ross(),
            shuriken(),
            snowGrenade(),
            deepSensor(),
            //技能
            dodgeDash(),
            sonarDagger(),
            stolHealth(),
            grapplingHook(),
            healGrenade(),
            defib(),
            freezer(),
            turret(),
            rushAndSlam(),
            claw(),
            gooGun(),
            gravityGadget(),
            //其它
            new ItemStack(Material.ARROW),
            indicator(),
    };
    ItemStack[]classes = {
            light(),
            mid(),
            heavy()
    };
    ItemStack[]skills = {
            dodgeDash(),//0
            sonarDagger(),//1
            healGrenade(),//2
            defib(),//3
            rushAndSlam(),//4
            claw(),//5
            stolHealth(),//6
            freezer(),//7
            gooGun(),//8
            grapplingHook(),
            turret(),
            gravityGadget(),
    };
    ItemStack[]weapons = {
            saber(),//0
            dagger(),//1
            longBow(),//2
            shield(),//3
            dualBlade(),//4
            grenadeCrossbow(),//5
            hammer(),//6
            spear(),//7
            blunderbuss(),//8
            throwingKnives(),//9
            revolver(),//10
            flameThrower(),//11
            bugSword(),//12
            glitchStick(),//13
            errorHook(),//14
            sniper(),//15
            doubleBarrel(),//16
            slugShotgun(),//17
    };
    ItemStack[]gadgets = {
            grenade(),//0
            gasGrenade(),//1
            gooGrenade(),//2
            fireGrenade(),//3
            smokeGrenade(),//4
            blindGrenade(),//5
            impactGrenade(),//6
            explosiveMine(),//7
            gasMine(),//8
            pyroMine(),//9
            glitchGrenade(),//10
            jumpPad(),//11
            rpg(),//12
            vanishingBomb(),//13
            APS(),//14
            C4(),//15
            gravityGrenade(),//16
            stunGun(),//17
            glitchMine(),//18
            domeShield(),//19
            gateway(),//20
            debug(),//21
            ross(),//22
            shuriken(),//23
            snowGrenade(),//24
            deepSensor(),//25
    };
    public ItemStack[]devItems = new ItemStack[]{
            masterSword(),
            ghostFlameDagger(),
            katana(),
            snowball(),
            dragonOfDojima(),
            snowWall(),
            shuriStar(),
    };
    public ItemStack getClass(int i){
        if(i > classes.length || i < 0) {
            Bukkit.getLogger().info("体型错误");
            return error();
        }
        return classes[i];
    }
    public ItemStack getSkill(int i){
        if(i >= skills.length || i < 0) {
            Bukkit.getLogger().info("技能错误:" + i);
            return error();
        }
        return skills[i];
    }
    public ItemStack[] getSkillsByClass(int body){
        ItemStack[]itemStacks = skills.clone();
        ArrayList<ItemStack>sorted = new ArrayList<>();
        String[] filter;
        switch (body){
            case 0:
                filter = new String[]{ChatColor.AQUA.toString(),ChatColor.GREEN.toString(),ChatColor.LIGHT_PURPLE.toString(),ChatColor.GRAY.toString()};
                break;
            case 1:
                filter = new String[]{ChatColor.YELLOW.toString(),ChatColor.GOLD.toString(),ChatColor.GREEN.toString(),ChatColor.GRAY.toString()};
                break;
            case 2:
                filter = new String[]{ChatColor.RED.toString(),ChatColor.GOLD.toString(),ChatColor.LIGHT_PURPLE.toString(),ChatColor.GRAY.toString()};
                break;
            default:
                return new ItemStack[]{};
        }
        for(ItemStack i : itemStacks){
            ItemMeta meta = i.getItemMeta();
            String name = meta.getDisplayName();
            for(String s : filter){
                if(name.contains(s)){
                    sorted.add(i);
                    break;
                }
            }
        }
        return sorted.toArray(new ItemStack[]{});
    }
    public ItemStack getWeapon(int i) {
        if (i >= weapons.length || i < 0) {
            Bukkit.getLogger().info("武器错误:" + i);
            return error();
        }
        return weapons[i];
    }
    public ItemStack[] getWeaponsByClass(int body){
        ItemStack[]itemStacks = weapons.clone();
        ArrayList<ItemStack>sorted = new ArrayList<>();
        String[] filter;
        switch (body){
            case 0:
                filter = new String[]{ChatColor.AQUA.toString(),ChatColor.GREEN.toString(),ChatColor.LIGHT_PURPLE.toString(),ChatColor.GRAY.toString()};
                break;
            case 1:
                filter = new String[]{ChatColor.YELLOW.toString(),ChatColor.GOLD.toString(),ChatColor.GREEN.toString(),ChatColor.GRAY.toString()};
                break;
            case 2:
                filter = new String[]{ChatColor.RED.toString(),ChatColor.GOLD.toString(),ChatColor.LIGHT_PURPLE.toString(),ChatColor.GRAY.toString()};
                break;
            default:
                return new ItemStack[]{};
        }
        for(ItemStack i : itemStacks){
            ItemMeta meta = i.getItemMeta();
            String name = meta.getDisplayName();
            for(String s : filter){
                if(name.contains(s)){
                    sorted.add(i);
                    break;
                }
            }
        }
        return sorted.toArray(new ItemStack[]{});
    }
    public ItemStack getGadget(int i) {
        if(i >= gadgets.length || i < 0) {
            Bukkit.getLogger().info("道具错误:" + i);
            return error();
        }
        return gadgets[i];
    }
    public ItemStack[] getGadgetsByClass(int body){
        ItemStack[]itemStacks = gadgets.clone();
        ArrayList<ItemStack>sorted = new ArrayList<>();
        String[] filter;
        switch (body){
            case 0:
                filter = new String[]{ChatColor.AQUA.toString(),ChatColor.GREEN.toString(),ChatColor.LIGHT_PURPLE.toString(),ChatColor.GRAY.toString()};
                break;
            case 1:
                filter = new String[]{ChatColor.YELLOW.toString(),ChatColor.GOLD.toString(),ChatColor.GREEN.toString(),ChatColor.GRAY.toString()};
                break;
            case 2:
                filter = new String[]{ChatColor.RED.toString(),ChatColor.GOLD.toString(),ChatColor.LIGHT_PURPLE.toString(),ChatColor.GRAY.toString()};
                break;
            default:
                return new ItemStack[]{};
        }
        for(ItemStack i : itemStacks){
            ItemMeta meta = i.getItemMeta();
            String name = meta.getDisplayName();
            for(String s : filter){
                if(name.contains(s)){
                    sorted.add(i);
                    break;
                }
            }
        }
        return sorted.toArray(new ItemStack[]{});
    }
    public int getItemID(String s){
        for(int a = 0;a < items.length;a ++){
            ItemStack item = items[a];
            ItemMeta meta = item.getItemMeta();
            String name = meta.getDisplayName();
            if(s.equals(name)){
                return a;
            }
        }
        return -1;
    }
    public int getWeaponID(String s){
        for(int a = 0;a < weapons.length;a ++){
            ItemStack item = weapons[a];
            ItemMeta meta = item.getItemMeta();
            String name = meta.getDisplayName();
            if(s.contains(name)){
                return a;
            }
        }
        return -1;
    }
    public int getGadgetID(String s){
        for(int a = 0;a <gadgets.length;a ++){
            ItemStack item = gadgets[a];
            ItemMeta meta = item.getItemMeta();
            String name = meta.getDisplayName();
            if(s.contains(name)){
                return a;
            }
        }
        return -1;
    }
    public int getSkillID(String s){
        for(int a = 0;a <skills.length;a ++){
            ItemStack item = skills[a];
            ItemMeta meta = item.getItemMeta();
            String name = meta.getDisplayName();
            if(s.contains(name)){
                return a;
            }
        }
        return -1;
    }
    public ItemStack error() {
        ItemStack dash = new ItemStack(Material.BARRIER);
        ItemMeta dashItemMeta = dash.getItemMeta();
        dashItemMeta.setDisplayName(ChatColor.RED + "" + ChatColor.MAGIC + "123456" + ChatColor.RESET + ""
                + ChatColor.RED + "错误物品" + ChatColor.MAGIC + "123456");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "看起来CNS把你的物品给黑掉了");
        lore.add(ChatColor.WHITE + "你能看到这个物品说明你需要联系腐竹了");
        dashItemMeta.setLore(lore);
        dash.setItemMeta(dashItemMeta);
        return dash;
    }
    public ItemStack coin() {
        ItemStack dash = new ItemStack(Material.EMERALD);
        ItemMeta dashItemMeta = dash.getItemMeta();
        dashItemMeta.setDisplayName(ChatColor.GREEN + "“VR”");
        dashItemMeta.addEnchant(Enchantment.LUCK,0,true);
        dashItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "虚拟货币");
        lore.add(ChatColor.WHITE + "仿冒必究");
        dashItemMeta.setLore(lore);
        dash.setItemMeta(dashItemMeta);
        return dash;
    }
    public ItemStack light(){
        ItemStack item = new ItemStack(Material.LIGHT_BLUE_CONCRETE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "轻型");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "轻盈体型");
        lore.add(ChatColor.WHITE + "移动速度最快,生命值最少");
        lore.add(ChatColor.WHITE + "适合游击战术,利用技能和道具干扰敌人");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack mid(){
        ItemStack item = new ItemStack(Material.YELLOW_CONCRETE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "中型");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "中等体型");
        lore.add(ChatColor.WHITE + "中等移动速度,中等生命值");
        lore.add(ChatColor.WHITE + "队伍的中坚力量,利用技能辅助队友");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack heavy(){
        ItemStack item = new ItemStack(Material.RED_CONCRETE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "重型");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "壮硕体型");
        lore.add(ChatColor.WHITE + "移动速度最慢,生命值最高");
        lore.add(ChatColor.WHITE + "不仅输出可观,拆除建筑物的能力也很强");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack dodgeDash() {
        ItemStack dash = new ItemStack(Material.DIAMOND);
        ItemMeta dashItemMeta = dash.getItemMeta();
        dashItemMeta.setDisplayName(ChatColor.AQUA + "闪避冲刺");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "闪避冲刺");
        lore.add(ChatColor.WHITE + "进行几次冲刺");
        lore.add(ChatColor.WHITE + "用来拉近距离或者逃离敌人");
        lore.add(ChatColor.AQUA + "可以充能3次");
        dashItemMeta.setLore(lore);
        dash.setItemMeta(dashItemMeta);
        return dash;
    }
    public ItemStack sonarDagger(){
        ItemStack hook = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta hookItemMeta = hook.getItemMeta();
        hookItemMeta.setDisplayName(ChatColor.AQUA + "声纳飞刀");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "声纳飞刀");
        lore.add(ChatColor.WHITE + "命中敌人或者环境时");
        lore.add(ChatColor.WHITE + "会释放声纳的飞刀");
        lore.add(ChatColor.WHITE + "可以探测敌人");
        lore.add(ChatColor.AQUA + "可以充能2次");
        hookItemMeta.setLore(lore);
        hook.setItemMeta(hookItemMeta);
        return hook;
    }
    public ItemStack stolHealth(){
        ItemStack item = new ItemStack(Material.CONDUIT);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "能量汲取器[被动技能]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "能量汲取器");
        lore.add(ChatColor.WHITE + "攻击敌人可以回复血量");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack healGrenade() {
        ItemStack heal = new ItemStack(Material.MAGMA_CREAM);
        ItemMeta healItemMeta = heal.getItemMeta();
        healItemMeta.setDisplayName(ChatColor.YELLOW + "治疗手雷");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "治疗手雷");
        lore.add(ChatColor.WHITE + "受到冲击后爆炸，回复范围内队友的血量");
        lore.add(ChatColor.WHITE + "扔出后会追踪一定范围内的队友");
        lore.add(ChatColor.WHITE + "如果一次性消耗所有充能会过热");
        lore.add(ChatColor.AQUA + "可以充能8次");
        healItemMeta.setLore(lore);
        heal.setItemMeta(healItemMeta);
        return heal;
    }
    public ItemStack defib() {
        ItemStack defib = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta defibItemMeta = defib.getItemMeta();
        defibItemMeta.setDisplayName(ChatColor.YELLOW + "医疗用针筒[被动技能]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "医疗用针筒");
        lore.add(ChatColor.WHITE + "减少拉起队友所需的时间");
        lore.add(ChatColor.WHITE + "但是拉起的队友血量不会回复到全满");
        lore.add(ChatColor.ITALIC + "萨尼铁塔！");
        defibItemMeta.setLore(lore);
        defib.setItemMeta(defibItemMeta);
        return defib;
    }
    public ItemStack freezer(){
        ItemStack item = new ItemStack(Material.ICE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "液氮装置");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "液氮装置");
        lore.add(ChatColor.WHITE + "把技能范围内的方块全部冰冻");
        lore.add(ChatColor.WHITE + "被冰冻的方块会变得透明、易碎");
        lore.add(ChatColor.WHITE + "范围内的敌人会获得缓慢效果");
        lore.add(ChatColor.WHITE + "可以扑灭人身上的火焰");
        lore.add(ChatColor.AQUA + "可以充能3次");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack rushAndSlam(){
        ItemStack slam = new ItemStack(Material.BRICK);
        ItemMeta slamItemMeta = slam.getItemMeta();
        slamItemMeta.setDisplayName(ChatColor.RED + "冲撞和砸击");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "冲撞和砸击");
        lore.add(ChatColor.WHITE + "向前方冲刺,击退敌人");
        lore.add(ChatColor.WHITE + "在空中时使用砸击,造成范围伤害");
        lore.add(ChatColor.WHITE + "坠落距离越高，砸击伤害越高");
        lore.add(ChatColor.WHITE + "使用砸击时，按住Shift可以加速下落");
        slamItemMeta.setLore(lore);
        slam.setItemMeta(slamItemMeta);
        return slam;
    }
    public ItemStack claw(){
        ItemStack hook = new ItemStack(Material.CHAIN);
        ItemMeta hookItemMeta = hook.getItemMeta();
        hookItemMeta.setDisplayName(ChatColor.RED + "铰链铁爪");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "铰链铁爪");
        lore.add(ChatColor.WHITE + "发射带有铁链的抓钩");
        lore.add(ChatColor.WHITE + "将敌人或物品拉到你的面前");
        hookItemMeta.setLore(lore);
        hook.setItemMeta(hookItemMeta);
        return hook;
    }
    public ItemStack grapplingHook(){
        ItemStack item = new ItemStack(Material.FISHING_ROD);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "抓钩");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "抓钩");
        lore.add(ChatColor.WHITE + "发射一支抓钩，可以将自己拉向抓钩的位置");
        lore.add(ChatColor.WHITE + "按Shift可以脱离抓钩");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack turret(){
        ItemStack item = new ItemStack(Material.CARVED_PUMPKIN);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "护卫炮塔");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "护卫炮塔");
        lore.add(ChatColor.WHITE + "由人工智障操作的护卫炮塔");
        lore.add(ChatColor.WHITE + "可以像罐子一样拿起来");
        lore.add(ChatColor.WHITE + "拿着技能图标按" + ChatColor.AQUA + "鼠标右键"
        + ChatColor.WHITE + "可以回收炮塔");
        lore.add(ChatColor.WHITE + "距离炮塔越近，回收炮塔时的冷却越短");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack gravityGadget(){
        ItemStack item = new ItemStack(Material.ANVIL);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "重力装置");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "可以扰乱重力的装置");
        lore.add(ChatColor.WHITE + "可以随时开启或者关闭");
        lore.add(ChatColor.WHITE + "使用时，范围内的所有实体都会受到周期性的向下的动量");
        lore.add(ChatColor.WHITE + "范围内的敌人会获得缓慢效果，并且脚下的方块会被破坏");
        lore.add(ChatColor.WHITE + "按住" + ChatColor.AQUA + "Shift" +
                ChatColor.WHITE + "可以反转重力场");
        lore.add(ChatColor.WHITE + "反转时，范围内的所有敌人会获得漂浮效果");
        lore.add(ChatColor.WHITE + "并且无法使用技能");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack gooGun(){
        ItemStack item = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "粘胶枪[被动技能]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "粘胶枪");
        lore.add(ChatColor.WHITE + "替换身上的16个玻璃");
        lore.add(ChatColor.WHITE + "发射粘胶球，落到地上会变成粘胶");
        lore.add(ChatColor.WHITE + "命中敌人会造成减速效果");
        lore.add(ChatColor.WHITE + "打空需要等待重新装填");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack grenade(){
        ItemStack item = new ItemStack(Material.CREEPER_SPAWN_EGG);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GRAY + "破片手榴弹");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "破片手榴弹");
        lore.add(ChatColor.WHITE + "扔出去后引爆");
        lore.add(ChatColor.WHITE + "引爆会破坏方块,并且生成飞溅的破片");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack gasGrenade(){
        ItemStack item = new ItemStack(Material.SLIME_SPAWN_EGG);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GRAY + "毒气手榴弹");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "毒气手榴弹");
        lore.add(ChatColor.WHITE + "受到冲击后引爆");
        lore.add(ChatColor.WHITE + "释放毒气");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack gooGrenade(){
        ItemStack item = new ItemStack(Material.PIG_SPAWN_EGG);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GRAY + "粘胶手榴弹");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "粘胶手榴弹");
        lore.add(ChatColor.WHITE + "受到冲击后引爆");
        lore.add(ChatColor.WHITE + "释放一团粘胶");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack fireGrenade(){
        ItemStack item = new ItemStack(Material.BLAZE_SPAWN_EGG);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GRAY + "火焰手榴弹");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "火焰手榴弹");
        lore.add(ChatColor.WHITE + "受到冲击后引爆");
        lore.add(ChatColor.WHITE + "释放火焰");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack smokeGrenade(){
        ItemStack item = new ItemStack(Material.SKELETON_SPAWN_EGG);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GRAY + "烟雾手榴弹");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "烟雾手榴弹");
        lore.add(ChatColor.WHITE + "受到冲击后引爆");
        lore.add(ChatColor.WHITE + "释放阻挡视线的烟雾");
        lore.add(ChatColor.WHITE + "并且可以熄灭身上的火焰");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack blindGrenade(){
        ItemStack item = new ItemStack(Material.ENDERMAN_SPAWN_EGG);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "致盲手榴弹");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "致盲手榴弹");
        lore.add(ChatColor.WHITE + "受到冲击后引爆");
        lore.add(ChatColor.WHITE + "致盲范围内的敌人");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack impactGrenade(){
        ItemStack item = new ItemStack(Material.EGG);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GRAY + "冲鸡手榴弹");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "冲鸡手榴弹");
        lore.add(ChatColor.WHITE + "受到冲击后引爆");
        lore.add(ChatColor.ITALIC + "系统出了点故障，可能导致出现错别字");
        lore.add(ChatColor.ITALIC + "也有可能会影响部分音效");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack gravityGrenade(){
        ItemStack item = new ItemStack(Material.GLOW_SQUID_SPAWN_EGG);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GRAY + "引力手榴弹");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "引力手榴弹");
        lore.add(ChatColor.WHITE + "受到冲击后引爆");
        lore.add(ChatColor.WHITE + "将附近的实体吸引到爆炸中心");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack explosiveMine(){
        ItemStack item = new ItemStack(Material.STONE_PRESSURE_PLATE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "爆炸地雷");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "爆炸地雷");
        lore.add(ChatColor.WHITE + "部署后一段时间激活");
        lore.add(ChatColor.WHITE + "在敌人经过附近时引爆");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack gasMine(){
        ItemStack item = new ItemStack(Material.WARPED_PRESSURE_PLATE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "毒气地雷");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "毒气地雷");
        lore.add(ChatColor.WHITE + "部署后一段时间激活");
        lore.add(ChatColor.WHITE + "在敌人经过附近时释放毒气");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack pyroMine(){
        ItemStack item = new ItemStack(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "火焰地雷");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "火焰地雷");
        lore.add(ChatColor.WHITE + "部署后一段时间激活");
        lore.add(ChatColor.WHITE + "在敌人经过附近时爆炸并燃烧");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack glitchGrenade(){
        ItemStack item = new ItemStack(Material.PHANTOM_SPAWN_EGG);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "紊乱手榴弹");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "紊乱手榴弹");
        lore.add(ChatColor.WHITE + "受到冲击后引爆");
        lore.add(ChatColor.WHITE + "引爆后会扰乱范围内敌人的视角");
        lore.add(ChatColor.WHITE + "并且让其身上的所有道具短暂冷却");
        lore.add(ChatColor.WHITE + "还可以摧毁敌方的地雷，干扰敌方炮塔");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack jumpPad(){
        ItemStack item = new ItemStack(Material.BLACK_BED);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "跳板");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "跳板");
        lore.add(ChatColor.WHITE + "部署之后形成一个小型弹射区域");
        lore.add(ChatColor.WHITE + "供自己和队友使用");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack rpg(){
        ItemStack item = new ItemStack(Material.IRON_HORSE_ARMOR);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "RPG");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "RPG");
        lore.add(ChatColor.WHITE + "肩扛式火箭发射器");
        lore.add(ChatColor.WHITE + "可以用来摧毁建筑物");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack vanishingBomb(){
        ItemStack item = new ItemStack(Material.SHULKER_SPAWN_EGG);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "隐身手榴弹");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "隐身手榴弹");
        lore.add(ChatColor.WHITE + "受到冲击后引爆");
        lore.add(ChatColor.WHITE + "范围内的友方和自己会获得隐身效果");
        lore.add(ChatColor.WHITE + "但是会暂时卸下护甲");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack APS(){
        ItemStack item = new ItemStack(Material.BEACON);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "APS");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "APS");
        lore.add(ChatColor.WHITE + "可以部署的主动防御系统");
        lore.add(ChatColor.WHITE + "清除附近的投射物");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack C4(){
        ItemStack item = new ItemStack(Material.REPEATER);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "C4");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "C4");
        lore.add(ChatColor.WHITE + "可以贴在物体表面的遥控炸药");
        lore.add(ChatColor.WHITE + "按" + ChatColor.AQUA + "鼠标右键" + ChatColor.WHITE + "在方块上贴C4");
        lore.add(ChatColor.WHITE + "按" + ChatColor.AQUA + "Shift + 鼠标右键" + ChatColor.WHITE + "在实体上贴C4");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack stunGun(){
        ItemStack item = new ItemStack(Material.PRISMARINE_SHARD);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "电击枪");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "电击枪");
        lore.add(ChatColor.WHITE + "命中敌人后会使敌人失去行动能力");
        lore.add(ChatColor.WHITE + "并且使其主武器和技能短暂冷却");
        lore.add(ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH +"电击小子");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack glitchMine(){
        ItemStack item = new ItemStack(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "紊乱地雷");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "紊乱地雷");
        lore.add(ChatColor.WHITE + "部署后需要一段时间激活");
        lore.add(ChatColor.WHITE + "触发后生成一片紊乱云");
        lore.add(ChatColor.WHITE + "使范围内敌人的技能和道具短暂冷却");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack domeShield(){
        ItemStack item = new ItemStack(Material.HEART_OF_THE_SEA);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "球形护盾");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "球形护盾");
        lore.add(ChatColor.WHITE + "使用后生成一个球形防护罩");
        lore.add(ChatColor.WHITE + "给周围的队友以及自己伤害吸收效果");
        lore.add(ChatColor.WHITE + "可以反弹远程攻击");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack ross(){
        ItemStack item = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "ROSS_MK.2");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "ROSS_MK.2");
        lore.add(ChatColor.WHITE + "威力强大的电磁炮");
        lore.add(ChatColor.WHITE + "虽然不会造成伤害，但是建筑破坏效果非常好");
        lore.add(ChatColor.WHITE + "并且可以破坏沿途的地雷，炮塔等部署物");
        lore.add(ChatColor.WHITE + "还可以紊乱敌人");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack gateway(){
        ItemStack item = new ItemStack(Material.SEA_PICKLE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "传送门");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "传送门");
        lore.add(ChatColor.WHITE + "在两个地点生成传送门");
        lore.add(ChatColor.WHITE + "对着传送门按" + ChatColor.AQUA + "鼠标右键" + ChatColor.WHITE + "即可在两个传送门之间传送");
        lore.add(ChatColor.WHITE + "投射物和非人类实体会自动使用传送门");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack debug(){
        ItemStack item = new ItemStack(Material.MILK_BUCKET);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "xX_Debug_Xx");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "Debug");
        lore.add(ChatColor.WHITE + "Debug工具");
        lore.add(ChatColor.WHITE + "按" + ChatColor.AQUA + "鼠标右键" + ChatColor.WHITE + "扔出投射物");
        lore.add(ChatColor.WHITE + "命中后会清除范围内友军身上的除隐身之外的所有效果");
        lore.add(ChatColor.WHITE + "不会对自己产生效果");
        lore.add(ChatColor.WHITE + "按" + ChatColor.AQUA + "Shift + 鼠标右键" + ChatColor.WHITE + "对自己使用");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack shuriken(){
        ItemStack item = new ItemStack(Material.PRISMARINE_CRYSTALS);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "漂浮手里剑");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "漂浮手里剑");
        lore.add(ChatColor.WHITE + "向前方从左到右,呈角度依次投掷出3枚手里剑");
        lore.add(ChatColor.WHITE + "命中敌人可以造成漂浮效果");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack snowGrenade(){
        ItemStack item = new ItemStack(Material.POWDER_SNOW_BUCKET);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "细雪手榴弹");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "细雪手榴弹");
        lore.add(ChatColor.WHITE + "受到冲击后引爆");
        lore.add(ChatColor.WHITE + "生成一团细雪");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack deepSensor(){
        ItemStack item = new ItemStack(Material.ENDER_EYE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "深渊传感器");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "深渊传感器");
        lore.add(ChatColor.WHITE + "可以放置在方块表面");
        lore.add(ChatColor.WHITE + "在范围内的敌人会获得发光和失明效果");
        lore.add(ChatColor.WHITE + "当你凝视深渊......");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack saber(){
        ItemStack item = new ItemStack(Material.IRON_SWORD);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "长剑");
        Attribute attribute1 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),-2.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),8, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        itemMeta.addAttributeModifier(attribute1,modifier1);
        itemMeta.addAttributeModifier(attribute2,modifier2);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "长剑");
        lore.add(ChatColor.WHITE + "灵活的近战武器");
        lore.add(ChatColor.WHITE + "按" + ChatColor.AQUA + "鼠标右键" + ChatColor.WHITE + "使用突刺");
        lore.add(ChatColor.GREEN +""+ ChatColor.ITALIC +"由我们虚拟的设计师交到你虚拟的手中");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack dagger(){
        ItemStack item = new ItemStack(Material.NETHERITE_SHOVEL);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "匕首");
        Attribute attribute1 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),-2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),7, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        itemMeta.addAttributeModifier(attribute1,modifier1);
        itemMeta.addAttributeModifier(attribute2,modifier2);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "匕首");
        lore.add(ChatColor.WHITE + "适合隐匿使用的近战武器");
        lore.add(ChatColor.WHITE + "按" + ChatColor.AQUA + "鼠标右键" + ChatColor.WHITE + "使用忍术");
        lore.add(ChatColor.AQUA + "从背后攻击" + ChatColor.WHITE + "或者" +
                ChatColor.AQUA + "从高处落下攻击" + ChatColor.WHITE + "可以造成额外伤害");
        lore.add(ChatColor.GREEN +""+ ChatColor.ITALIC +"由我们虚拟的设计师交到你虚拟的手中");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack longBow(){
        ItemStack item = new ItemStack(Material.BOW);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "反曲弓");
        Attribute attribute1 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),-3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),4, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        itemMeta.addAttributeModifier(attribute1,modifier1);
        itemMeta.addAttributeModifier(attribute2,modifier2);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "反曲弓");
        lore.add(ChatColor.WHITE + "威力不俗的反曲弓");
        lore.add(ChatColor.AQUA + "拉满弓射击" + ChatColor.WHITE + "造成额外伤害");
        lore.add(ChatColor.GREEN +""+ ChatColor.ITALIC +"由我们虚拟的设计师交到你虚拟的手中");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack stick(){
        ItemStack item = new ItemStack(Material.STICK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "警棍");
        Attribute attribute1 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),-2.75, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),7, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        itemMeta.addAttributeModifier(attribute1,modifier1);
        itemMeta.addAttributeModifier(attribute2,modifier2);
        itemMeta.addEnchant(Enchantment.KNOCKBACK,2,true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "警棍");
        lore.add(ChatColor.WHITE + "跟防暴盾配合使用");
        lore.add(ChatColor.GREEN +""+ ChatColor.ITALIC +"由我们虚拟的设计师交到你虚拟的手中");
        lore.add(ChatColor.WHITE + "应该不会有人想用盾直接拍人吧");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack shield(){
        ItemStack item = new ItemStack(Material.SHIELD);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "防暴盾");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "防暴盾");
        lore.add(ChatColor.WHITE + "可以抵挡伤害的近战武器");
        lore.add(ChatColor.WHITE + "在格挡时可以免疫绝大多数攻击");
        lore.add(ChatColor.WHITE + "在副手时，格挡并按" + ChatColor.AQUA + "Shift" + ChatColor.WHITE + "使用盾击");
        lore.add(ChatColor.WHITE + "使用盾击后一段时间内无法格挡");
        lore.add(ChatColor.GREEN +""+ ChatColor.ITALIC +"由我们虚拟的设计师交到你虚拟的手中");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack dualBlade(){
        ItemStack item = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "双刀");
        Attribute attribute1 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),-2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),5, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        itemMeta.addAttributeModifier(attribute1,modifier1);
        itemMeta.addAttributeModifier(attribute2,modifier2);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "双刀");
        lore.add(ChatColor.WHITE + "可以反弹伤害的近战武器");
        lore.add(ChatColor.WHITE + "按住" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + "反弹远程攻击");
        lore.add(ChatColor.WHITE + "在格挡时，会减少受到的伤害");
        lore.add(ChatColor.WHITE + "并将伤害反弹给攻击者");
        lore.add(ChatColor.GREEN +""+ ChatColor.ITALIC +"由我们虚拟的设计师交到你虚拟的手中");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack grenadeCrossbow(){
        ItemStack item = new ItemStack(Material.CROSSBOW);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "榴弹弩");
        Attribute attribute1 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),-3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),4, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        itemMeta.addAttributeModifier(attribute1,modifier1);
        itemMeta.addAttributeModifier(attribute2,modifier2);
        itemMeta.addEnchant(Enchantment.KNOCKBACK,1,true);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "榴弹弩");
        lore.add(ChatColor.WHITE + "射出受到冲击后引爆的榴弹");
        lore.add(ChatColor.WHITE + "不会破坏方块");
        lore.add(ChatColor.GREEN +""+ ChatColor.ITALIC +"由我们虚拟的设计师交到你虚拟的手中");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack hammer(){
        ItemStack item = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta itemMeta = item.getItemMeta();
        Attribute attribute1 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),-3.25, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),10, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        itemMeta.addAttributeModifier(attribute1,modifier1);
        itemMeta.addAttributeModifier(attribute2,modifier2);
        itemMeta.setDisplayName(ChatColor.RED + "大锤");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "大锤");
        lore.add(ChatColor.WHITE + "可以拆除建筑的近战武器");
        lore.add(ChatColor.WHITE + "按" + ChatColor.AQUA + "鼠标右键" + ChatColor.WHITE + "拆除建筑");
        lore.add(ChatColor.GREEN +""+ ChatColor.ITALIC +"由我们虚拟的设计师交到你虚拟的手中");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack spear(){
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "长矛");
        Attribute attribute1 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),-2.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),5.5, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        itemMeta.addAttributeModifier(attribute1,modifier1);
        itemMeta.addAttributeModifier(attribute2,modifier2);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "长矛");
        lore.add(ChatColor.WHITE + "可以大范围攻击的近战武器");
        lore.add(ChatColor.WHITE + "按" + ChatColor.AQUA + "鼠标右键" + ChatColor.WHITE + "使用突刺攻击");
        lore.add(ChatColor.WHITE + "按" + ChatColor.AQUA + "Shift + 鼠标右键" + ChatColor.WHITE + "使用横扫攻击");
        lore.add(ChatColor.GREEN +""+ ChatColor.ITALIC +"由我们虚拟的设计师交到你虚拟的手中");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack blunderbuss(){
        ItemStack item = new ItemStack(Material.IRON_HOE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "大口径火铳");
        Attribute attribute1 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),-3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),4, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        itemMeta.addAttributeModifier(attribute1,modifier1);
        itemMeta.addAttributeModifier(attribute2,modifier2);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "大口径火铳");
        lore.add(ChatColor.WHITE + "射程近,范围大");
        lore.add(ChatColor.WHITE + "装填速度慢");
        lore.add(ChatColor.GREEN +""+ ChatColor.ITALIC +"由我们虚拟的设计师交到你虚拟的手中");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack throwingKnives(){
        ItemStack item = new ItemStack(Material.SHEARS);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "飞刀");
        Attribute attribute1 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),-3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),4, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        itemMeta.addAttributeModifier(attribute1,modifier1);
        itemMeta.addAttributeModifier(attribute2,modifier2);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "飞刀");
        lore.add(ChatColor.WHITE + "无声(大嘘)的投掷武器");
        lore.add(ChatColor.WHITE + "按" + ChatColor.AQUA + "鼠标右键" + ChatColor.WHITE + "快速投掷");
        lore.add(ChatColor.WHITE + "按" + ChatColor.AQUA + "Shift + 鼠标右键" + ChatColor.WHITE + "精准投掷");
        lore.add(ChatColor.GREEN +""+ ChatColor.ITALIC +"由我们虚拟的设计师交到你虚拟的手中");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack revolver(){
        ItemStack item = new ItemStack(Material.GOLDEN_HORSE_ARMOR);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "羊驼左轮");
        Attribute attribute1 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),-3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),4, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        itemMeta.addAttributeModifier(attribute1,modifier1);
        itemMeta.addAttributeModifier(attribute2,modifier2);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "羊驼左轮");
        lore.add(ChatColor.WHITE + "老式左轮手枪");
        lore.add(ChatColor.WHITE + "发射可以穿透墙壁的强力子弹");
        lore.add(ChatColor.WHITE + "按" + ChatColor.AQUA + "鼠标右键" + ChatColor.WHITE + "射击");
        lore.add(ChatColor.WHITE + "按" + ChatColor.AQUA + "Shift + 鼠标右键" + ChatColor.WHITE + "重新装填");
        lore.add(ChatColor.WHITE + "剩余弹量越多，装填速度越快");
        lore.add(ChatColor.GREEN +""+ ChatColor.ITALIC +"由我们虚拟的设计师交到你虚拟的手中");
        lore.add(ChatColor.WHITE +""+ ChatColor.STRIKETHROUGH +"如果全打偏了一定可以让你喊出这把左轮的另一个名字");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack flameThrower(){
        ItemStack item = new ItemStack(Material.FLINT_AND_STEEL);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "喷火器");
        Attribute attribute1 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),-3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),4, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        itemMeta.addAttributeModifier(attribute1,modifier1);
        itemMeta.addAttributeModifier(attribute2,modifier2);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "喷火器");
        lore.add(ChatColor.WHITE + "喷射火焰的远程武器");
        lore.add(ChatColor.WHITE + "按" + ChatColor.AQUA + "鼠标右键" + ChatColor.WHITE + "发射火焰");
        lore.add(ChatColor.WHITE + "按" + ChatColor.AQUA + "Shift + 鼠标右键" + ChatColor.WHITE + "手动重新装填");
        lore.add(ChatColor.GREEN +""+ ChatColor.ITALIC +"由我们虚拟的设计师交到你虚拟的手中");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack bugSword(){
        ItemStack item = new ItemStack(Material.LIGHTNING_ROD);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "Bug剑");
        Attribute attribute1 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),-2.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),7, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        itemMeta.addAttributeModifier(attribute1,modifier1);
        itemMeta.addAttributeModifier(attribute2,modifier2);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "Bug剑");
        lore.add(ChatColor.WHITE + "利用Bug延长攻击距离的武器");
        lore.add(ChatColor.WHITE + "按" + ChatColor.AQUA + "鼠标右键" + ChatColor.WHITE + "远距离攻击");
        lore.add(ChatColor.WHITE + "目标距离越远,远距离攻击的伤害越高");
        lore.add(ChatColor.GREEN +""+ ChatColor.ITALIC +"由我们虚拟的设计师交到你虚拟的手中");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack glitchStick(){
        ItemStack item = new ItemStack(Material.STONE_SWORD);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "故障锏");
        Attribute attribute1 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),-3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),9, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        itemMeta.addAttributeModifier(attribute1,modifier1);
        itemMeta.addAttributeModifier(attribute2,modifier2);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "故障锏");
        lore.add(ChatColor.WHITE + "可以造成故障效果的武器");
        lore.add(ChatColor.WHITE + "按" + ChatColor.AQUA + "鼠标右键" + ChatColor.WHITE + "使用上挑");
        lore.add(ChatColor.WHITE + "自身和被挑飞的目标都会获得挖掘疲劳效果");
        lore.add(ChatColor.GREEN +""+ ChatColor.ITALIC +"由我们虚拟的设计师交到你虚拟的手中");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack errorHook(){
        ItemStack item = new ItemStack(Material.GOLDEN_PICKAXE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "漏洞锚");
        Attribute attribute1 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),-2.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),5, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        itemMeta.addAttributeModifier(attribute1,modifier1);
        itemMeta.addAttributeModifier(attribute2,modifier2);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "漏洞锚");
        lore.add(ChatColor.WHITE + "利用漏洞获得的武器");
        lore.add(ChatColor.WHITE + "按" + ChatColor.AQUA + "鼠标右键" + ChatColor.WHITE + "使用箭步并下砸");
        lore.add(ChatColor.WHITE + "被砸中的目标脚下的方块会被破坏");
        lore.add(ChatColor.GREEN +""+ ChatColor.ITALIC +"由我们虚拟的设计师交到你虚拟的手中");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack sniper(){
        ItemStack item = new ItemStack(Material.SPYGLASS);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "RB-30");
        Attribute attribute1 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),-3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),4, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        itemMeta.addAttributeModifier(attribute1,modifier1);
        itemMeta.addAttributeModifier(attribute2,modifier2);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "RB-30");
        lore.add(ChatColor.WHITE + "滚轮闭锁式步枪，单发装填");
        lore.add(ChatColor.WHITE + "安装了可以放大瞄准倍率的瞄准镜");
        lore.add(ChatColor.WHITE + "发射可以穿透墙壁的强力子弹");
        lore.add(ChatColor.WHITE + "距离越远，造成的伤害越高");
        lore.add(ChatColor.WHITE + "按" + ChatColor.AQUA + "Shift" + ChatColor.WHITE + "射击");
        lore.add(ChatColor.GREEN +""+ ChatColor.ITALIC +"由我们虚拟的设计师交到你虚拟的手中");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack doubleBarrel(){
        ItemStack item = new ItemStack(Material.DRAGON_HEAD);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "DB-15");
        Attribute attribute1 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),-3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),4, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        itemMeta.addAttributeModifier(attribute1,modifier1);
        itemMeta.addAttributeModifier(attribute2,modifier2);
        itemMeta.addEnchant(Enchantment.FIRE_ASPECT,1,true);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "DB-15");
        lore.add(ChatColor.WHITE + "双管霰弹枪");
        lore.add(ChatColor.WHITE + "发射龙息弹，可以点燃对手");
        lore.add(ChatColor.WHITE + "按" + ChatColor.AQUA + "鼠标右键" + ChatColor.WHITE + "射击");
        lore.add(ChatColor.GREEN +""+ ChatColor.ITALIC +"由我们虚拟的设计师交到你虚拟的手中");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack slugShotgun(){
        ItemStack item = new ItemStack(Material.NETHERITE_HOE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "PASS-12");
        Attribute attribute1 = Attribute.GENERIC_ATTACK_SPEED;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        AttributeModifier modifier1 = new AttributeModifier(UUID.randomUUID(),attribute1.name(),-3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(),attribute2.name(),4, AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
        itemMeta.addAttributeModifier(attribute1,modifier1);
        itemMeta.addAttributeModifier(attribute2,modifier2);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "PASS-12");
        lore.add(ChatColor.WHITE + "泵动霰弹枪，发射独头弹");
        lore.add(ChatColor.WHITE + "子弹可以拆除建筑物");
        lore.add(ChatColor.WHITE + "按" + ChatColor.AQUA + "鼠标右键" + ChatColor.WHITE + "射击");
        lore.add(ChatColor.WHITE + "按" + ChatColor.AQUA + "Shift + 鼠标右键" + ChatColor.WHITE + "手动重新装填");
        lore.add(ChatColor.WHITE + "剩余弹量越多，装填速度越快");
        lore.add(ChatColor.GREEN +""+ ChatColor.ITALIC +"由我们虚拟的设计师交到你虚拟的手中");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack masterSword() {
        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta swordItemMeta = sword.getItemMeta();
        swordItemMeta.setDisplayName(ChatColor.AQUA + "大师剑");
        swordItemMeta.addEnchant(Enchantment.SWEEPING_EDGE, 3, true);
        swordItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        Attribute attribute = Attribute.GENERIC_KNOCKBACK_RESISTANCE;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute3 = Attribute.GENERIC_ATTACK_SPEED;
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), attribute.name(), 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(), attribute2.name(), 30, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(), attribute3.name(), -2.4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        swordItemMeta.addAttributeModifier(attribute, modifier);
        swordItemMeta.addAttributeModifier(attribute2, modifier2);
        swordItemMeta.addAttributeModifier(attribute3, modifier3);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "MasterSword");
        lore.add(ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 释放远程剑气");
        lore.add(ChatColor.WHITE + "" + ChatColor.ITALIC + "你能拿到的最好的剑");
        lore.add(ChatColor.WHITE + "" + ChatColor.ITALIC + "虽然剑气对人没有伤害");
        lore.add(ChatColor.WHITE + "" + ChatColor.ITALIC + "但是可以遮蔽视野");
        swordItemMeta.setLore(lore);
        sword.setItemMeta(swordItemMeta);
        return sword;
    }
    public ItemStack ghostFlameDagger() {
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta swordItemMeta = sword.getItemMeta();
        swordItemMeta.setDisplayName(ChatColor.DARK_PURPLE + "鬼炎匕首");
        swordItemMeta.addEnchant(Enchantment.SWEEPING_EDGE, 3, true);
        swordItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        Attribute attribute = Attribute.GENERIC_KNOCKBACK_RESISTANCE;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute3 = Attribute.GENERIC_ATTACK_SPEED;
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), attribute.name(), 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(), attribute2.name(), 50, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(), attribute3.name(), -2.4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        swordItemMeta.addAttributeModifier(attribute, modifier);
        swordItemMeta.addAttributeModifier(attribute2, modifier2);
        swordItemMeta.addAttributeModifier(attribute3, modifier3);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "GhostFlameDagger");
        lore.add(ChatColor.WHITE + "" + ChatColor.ITALIC + "“嶋野的狂犬”所使用的匕首");
        lore.add(ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 使用专属技能“紫电鬼炎刃”");
        swordItemMeta.setLore(lore);
        sword.setItemMeta(swordItemMeta);
        return sword;
    }
    public ItemStack katana() {
        ItemStack sword = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta swordItemMeta = sword.getItemMeta();
        swordItemMeta.setDisplayName(ChatColor.GOLD + "虾头太刀");
        swordItemMeta.addEnchant(Enchantment.SWEEPING_EDGE, 3, true);
        swordItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        Attribute attribute = Attribute.GENERIC_KNOCKBACK_RESISTANCE;
        Attribute attribute2 = Attribute.GENERIC_ATTACK_DAMAGE;
        Attribute attribute3 = Attribute.GENERIC_ATTACK_SPEED;
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), attribute.name(), 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier2 = new AttributeModifier(UUID.randomUUID(), attribute2.name(), 50, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier modifier3 = new AttributeModifier(UUID.randomUUID(), attribute3.name(), -2.4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        swordItemMeta.addAttributeModifier(attribute, modifier);
        swordItemMeta.addAttributeModifier(attribute2, modifier2);
        swordItemMeta.addAttributeModifier(attribute3, modifier3);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Katana");
        lore.add(ChatColor.WHITE + "" + ChatColor.ITALIC + "太刀虾使用的虾头太刀");
        lore.add(ChatColor.WHITE + "" + ChatColor.ITALIC + "人和刃至少得红一个");
        lore.add(ChatColor.WHITE + "按下" + ChatColor.YELLOW + "鼠标右键" + ChatColor.WHITE + " : 使用气刃兜割");
        lore.add(ChatColor.WHITE + "如果气刃兜割命中，则会派生登龙");
        swordItemMeta.setLore(lore);
        sword.setItemMeta(swordItemMeta);
        return sword;
    }
    public ItemStack snowball() {
        ItemStack dash = new ItemStack(Material.SNOWBALL);
        ItemMeta dashItemMeta = dash.getItemMeta();
        dashItemMeta.setDisplayName(ChatColor.AQUA + "“特制”雪球");
        dashItemMeta.addEnchant(Enchantment.LUCK,0,true);
        dashItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "“特制”雪球");
        lore.add(ChatColor.WHITE + "专门用来打雪仗的雪球");
        lore.add(ChatColor.WHITE + "打人非常的疼");
        lore.add(ChatColor.WHITE + "" +ChatColor.STRIKETHROUGH + "谁TM在里面包的石头");
        dashItemMeta.setLore(lore);
        dash.setItemMeta(dashItemMeta);
        return dash;
    }
    public ItemStack indicator() {
        ItemStack dash = new ItemStack(Material.COBWEB);
        ItemMeta dashItemMeta = dash.getItemMeta();
        dashItemMeta.setDisplayName(ChatColor.GREEN + "通讯网络");
        dashItemMeta.addEnchant(Enchantment.LUCK,0,true);
        dashItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "通讯网络");
        lore.add(ChatColor.WHITE + "跟队友进行简单的交流");
        lore.add(ChatColor.WHITE + "并且告诉队友你的位置坐标");
        lore.add(ChatColor.WHITE + "鼠标左键：" + ChatColor.RED + "发现敌人");
        lore.add(ChatColor.WHITE + "鼠标右键：" + ChatColor.GREEN + "需要支援");
        lore.add(ChatColor.WHITE + "Shift + 鼠标左键：" + ChatColor.AQUA + "进攻这里");
        lore.add(ChatColor.WHITE + "Shift + 鼠标右键：" + ChatColor.YELLOW + "防守这里");
        dashItemMeta.setLore(lore);
        dash.setItemMeta(dashItemMeta);
        return dash;
    }
    public ItemStack dragonOfDojima() {
        ItemStack dash = new ItemStack(Material.SKELETON_SKULL);
        ItemMeta dashItemMeta = dash.getItemMeta();
        dashItemMeta.setDisplayName(ChatColor.AQUA + "东城会徽章");
        dashItemMeta.addEnchant(Enchantment.LUCK,0,true);
        dashItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "东城会徽章");
        lore.add(ChatColor.WHITE + "召唤Boss“堂岛之龙”");
        dashItemMeta.setLore(lore);
        dash.setItemMeta(dashItemMeta);
        return dash;
    }
    public ItemStack snowWall(){
        ItemStack item = new ItemStack(Material.POWDER_SNOW_BUCKET);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "细雪墙");
        itemMeta.addEnchant(Enchantment.LUCK,0,true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "细雪墙");
        lore.add(ChatColor.WHITE + "受到冲击后引爆");
        lore.add(ChatColor.WHITE + "生成一大团细雪");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack shuriStar(){
        ItemStack item = new ItemStack(Material.PRISMARINE_CRYSTALS);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "漂浮手里剑Pro");
        itemMeta.addEnchant(Enchantment.LUCK,0,true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "漂浮手里剑Pro");
        lore.add(ChatColor.WHITE + "向前方从左到右,呈角度依次投掷出一圈手里剑");
        lore.add(ChatColor.WHITE + "命中敌人可以造成漂浮效果");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
}

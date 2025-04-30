package Universal;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class BroadCast {
    private static BroadCast instance = new BroadCast();
    private BroadCast(){}
    public static BroadCast getInstance() {
        return instance;
    }
    JavaPlugin plugin;
    Random random = new Random();
    GameStatus gameStatus = GameStatus.getInstance();

    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void broadCastManyMessages(String[]strings,long delay,long timer){
        BukkitRunnable broadCast = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if(count >= strings.length){
                    this.cancel();
                    return;
                }
                Bukkit.broadcastMessage(strings[count]);
                for(Player p : Bukkit.getOnlinePlayers()){
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
                }
                count +=1;
            }
        };
        broadCast.runTaskTimer(plugin,delay,timer);
    }
    public void introducing(int map){
        String[]introducing = {
                ChatColor.YELLOW + "金胡萝卜神：欢迎来到佬年Zone服务器，Das Finale™的比赛现场",
                ChatColor.YELLOW + "金胡萝卜神：我是服务器吉祥物，金胡萝卜神",
                ChatColor.LIGHT_PURPLE + "AlliGator：我是前Das Finale™选手，AlliGator",
        };
        String[] maps = switch (map){
            case 0 -> new String[]{
                    ChatColor.LIGHT_PURPLE + "AlliGator：我们在摩纳哥为您实时解说！",
                    ChatColor.LIGHT_PURPLE + "AlliGator：马上，矫健的选手们将会在小巷和街道之间穿梭，要是拍成电影绝对是个大片！",
            };
            case 1 -> new String[]{
                    ChatColor.LIGHT_PURPLE + "AlliGator：本次比赛我们将载入荒漠竞技场！",
                    ChatColor.LIGHT_PURPLE + "AlliGator：选手们将会在错综复杂，并且有着沙漠风情的环境中一决高下",
            };
            case 2 -> new String[]{
                    ChatColor.LIGHT_PURPLE + "AlliGator：欢迎来到桃花源，观众们！",
                    ChatColor.LIGHT_PURPLE + "AlliGator：在这花团锦簇的古代乡村中，选手们将会体验到美丽的复古风情",
            };
            case 3 -> new String[]{
                    ChatColor.LIGHT_PURPLE + "AlliGator：观众朋友们，欢迎来到数字世界，这里是SYS＄HORIZON！",
                    ChatColor.LIGHT_PURPLE + "AlliGator：选手们将在这个与众不同的虚拟世界中进行对决！",
            };
            default -> new String[]{};
        };
        String[]teams = new String[]{
                ChatColor.LIGHT_PURPLE + "AlliGator：参加本场比赛的有：",
                gameStatus.getTeamName(0),
                gameStatus.getTeamName(1),
                gameStatus.getTeamName(2),
                ChatColor.LIGHT_PURPLE + "AlliGator：比赛现在开始！",
        };
        broadCastManyMessages(introducing,0L,20L);
        broadCastManyMessages(maps,80L,20L);
        broadCastManyMessages(teams,120L,20L);
    }
    public void start(int mode){
        String[]startMessage = new String[]{};
        if(mode == 0){
            switch (random.nextInt(2)) {
                case 0 -> startMessage = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：在快速提现中，选手们的目标很简单，达到目标金额的队伍即可胜利",
                        ChatColor.LIGHT_PURPLE + "AlliGator：没错，让抢钱大作战开始吧",
                };
                case 1 -> startMessage = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：在快速提现中，讲究的是速度和策略，目标只有一个：提现！",
                        ChatColor.LIGHT_PURPLE + "AlliGator：最快拿到足够现金的队伍即可获得胜利",
                };
            }
        }else if(mode == 1){
            switch (random.nextInt(2)) {
                case 0 -> startMessage = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：让我们来存钱吧！所有的选手只有一个争夺目标，那就是金币！",
                        ChatColor.LIGHT_PURPLE + "AlliGator：选手们可以通过淘汰对手或者开启金库来获得金币",
                };
                case 1 -> startMessage = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：正常情况下，被淘汰的选手将会变成一摞金币,但在“存钱至上”中，金币可以捡起来并存到存钱站中",
                        ChatColor.LIGHT_PURPLE + "AlliGator：谁捡到了金币，金币就归谁！率先存到规定金额即可获得胜利",
                };
            }
        }else if(mode == 2){
            switch (random.nextInt(2)) {
                case 0 -> startMessage = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：哦，不，看起来CNS扰乱了我们的比赛，选手们的配装全被打乱了",
                        ChatColor.LIGHT_PURPLE + "AlliGator：但游戏规则没有改变，选手们的目标还是像往常一样——那就是提现！",
                };
                case 1 -> startMessage = new String[]{
                        ChatColor.LIGHT_PURPLE + "AlliGator：CNS为了给竞技场带来一些新乐趣，打乱了选手们的配装",
                        ChatColor.YELLOW + "金胡萝卜神：不过游戏规则还是按照快速提现模式运作，开始抢钱吧！",
                };
            }
        }
        broadCastManyMessages(startMessage,20L,30L);
    }
    public void end(String team){
        String[]end = {
                ChatColor.YELLOW + "金胡萝卜神：" + team + ChatColor.YELLOW + "取得了本场比赛的胜利！本场比赛到此结束",
                ChatColor.LIGHT_PURPLE + "AlliGator：我们代表本场比赛的所有赞助商，感谢大家收看本场比赛",
        };
        broadCastManyMessages(end,20L,30L);
    }
    public void gameShowEventBC(int id){
        String[]message;
        switch (id){
            case 0:
                message = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：在本次事件中，陨石将会从天而降，并落在提现站周围",
                        ChatColor.LIGHT_PURPLE + "AlliGator：也许我们的选手会在陨石坑中发现什么",
                };
                break;
            case 1:
                message = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：在本次事件中，闪电将会击中位置最高的选手",
                        ChatColor.LIGHT_PURPLE + "AlliGator：被闪电击中也不一定是坏事",
                };
                break;
            case 2:
                message = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：在本次事件中，来自下界的生物将会入侵主世界",
                        ChatColor.LIGHT_PURPLE + "AlliGator：对于进攻方和防守方来说都是不小的问题",
                };
                break;
            case 3:
                message = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：在本次事件中，选手被淘汰的位置会发生爆炸",
                        ChatColor.LIGHT_PURPLE + "AlliGator：虽然爆炸没有伤害，但是会很有“吸引力”",
                };
                break;
            case 4:
                message = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：啊哦，在本次事件中，竞技场的重力系统出现了问题",
                        ChatColor.LIGHT_PURPLE + "AlliGator：不知道为什么只有选手受到影响",
                };
                break;
            default:
                if(random.nextBoolean()){
                    message = new String[]{
                            ChatColor.YELLOW + "金胡萝卜神：本次游戏事件到此结束",
                    };
                }else {
                    message = new String[]{
                            ChatColor.LIGHT_PURPLE + "AlliGator：本次游戏事件到此结束",
                    };
                }
        }
        broadCastManyMessages(message,0L,20L);
    }
    public void sponsorShowEventBC(int id){
        String[]message = new String[0];
        String sponsor;
        switch (id){
            case 0:
                sponsor = ChatColor.YELLOW + "OSPUZE";
                message = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：感谢我们的赞助商" + sponsor +
                               ChatColor.YELLOW +"，给选手们提供了速度效果",
                        ChatColor.LIGHT_PURPLE + "AlliGator：" + sponsor +"，扯开拉环，激情畅饮，精彩游戏"
                };
                break;
            case 1:
                sponsor = ChatColor.GOLD + "HOLTOW";
                message = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：感谢我们的赞助商" + sponsor +
                                ChatColor.YELLOW +"，给选手们提供了伤害吸收效果",
                        ChatColor.LIGHT_PURPLE + "AlliGator：" + sponsor +"，有数字保险，虚拟也无忧"
                };
                break;
            case 2:
                sponsor = ChatColor.AQUA + "ISEUL-" +ChatColor.LIGHT_PURPLE + "T";
                message = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：感谢我们的赞助商" + sponsor +
                                ChatColor.YELLOW +"，给选手们提供了跳跃提升效果",
                        ChatColor.LIGHT_PURPLE + "AlliGator：" + sponsor + ChatColor.AQUA +"，超越现实"
                };
                break;
            case 3:
                sponsor = ChatColor.DARK_AQUA + "ENGIMO";
                message = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：我们的赞助商" + sponsor +
                                ChatColor.YELLOW +"觉得赛事可以更刺激一些，给选手们提供了发光效果",
                        ChatColor.LIGHT_PURPLE + "AlliGator：" + sponsor +"，远在天边，近在眼前"
                };
                break;
            case 4:
                sponsor = ChatColor.RED + "DISSUN";
                message = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：感谢我们的赞助商" + sponsor +
                                ChatColor.YELLOW +"，给选手们提供了力量效果",
                        ChatColor.LIGHT_PURPLE + "AlliGator：" + sponsor +"，为你的生活充满能量"
                };
                break;
            case 5:
                sponsor = ChatColor.WHITE + "VAIIYA";
                message = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：感谢我们的赞助商" + sponsor +
                                ChatColor.YELLOW +"，给选手们提供了生命回复效果",
                        ChatColor.LIGHT_PURPLE + "AlliGator：" + sponsor +"，我们，意念合一"
                };
                break;
            case 6:
                sponsor = ChatColor.DARK_RED + "VOLPE";
                message = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：感谢我们的赞助商" + sponsor +
                                ChatColor.YELLOW +"，给选手们提供了抗性提升效果",
                };
                break;
            case 7:
                message = new String[]{
                        ChatColor.RED + ""+ ChatColor.MAGIC +"1234567890",
                        ChatColor.RED + "我们是CNS，我们来改变这场游戏",
                        ChatColor.RED + "选手们的背包物品被打乱了",
                        ChatColor.RED + "游戏愉快",
                        ChatColor.RED + ""+ ChatColor.MAGIC +"1234567890",
                };
                break;
        }
        broadCastManyMessages(message,60L,20L);
    }
    public void teamWipeBC(String team){
        String[]startMessage = new String[]{};
        switch (random.nextInt(4)) {
            case 0 -> startMessage = new String[]{
                    ChatColor.YELLOW + "金胡萝卜神：" + team + ChatColor.YELLOW + "被团灭咯，他们需要再小心一点",
            };
            case 1 -> startMessage = new String[]{
                    ChatColor.YELLOW + "金胡萝卜神：" + team + ChatColor.YELLOW + "无人生还！",
            };
            case 2 -> startMessage = new String[]{
                    ChatColor.LIGHT_PURPLE + "AlliGator：" + team + ChatColor.LIGHT_PURPLE + "团灭了！再接再厉",
            };
            case 3 -> startMessage = new String[]{
                    ChatColor.LIGHT_PURPLE + "AlliGator：" + team + ChatColor.LIGHT_PURPLE + "需要重新准备一下了，团灭！",
            };
        }
        broadCastManyMessages(startMessage,20L,20L);
    }
    public void teamRespawnBC(String team) {
        String[] startMessage;
        if (random.nextBoolean()) {
            startMessage = new String[]{
                    ChatColor.YELLOW + "金胡萝卜神：" + team + ChatColor.YELLOW + "回到了竞技场中",
            };
        } else {
            startMessage = new String[]{
                    ChatColor.LIGHT_PURPLE + "AlliGator：" + team + ChatColor.LIGHT_PURPLE + "卷土重来！",
            };
        }
        broadCastManyMessages(startMessage, 20L, 20L);
    }
    public void openVault(String team){
        String[]startMessage = new String[]{};
        switch (random.nextInt(4)) {
            case 0 -> startMessage = new String[]{
                    ChatColor.YELLOW + "金胡萝卜神：" + team + ChatColor.YELLOW + "正在撬开一个金库",
            };
            case 1 -> startMessage = new String[]{
                    ChatColor.YELLOW + "金胡萝卜神：" + team + ChatColor.YELLOW + "打开了一个金库",
            };
            case 2 -> startMessage = new String[]{
                    ChatColor.LIGHT_PURPLE + "AlliGator：" + team + ChatColor.LIGHT_PURPLE + "正在忙着弄开一个金库",
            };
            case 3 -> startMessage = new String[]{
                    ChatColor.LIGHT_PURPLE + "AlliGator：好像有人在撬开金库，原来是" + team + ChatColor.LIGHT_PURPLE + "！",
            };
        }
        broadCastManyMessages(startMessage,40L,20L);
    }
    public void cashOutStartBC(String team){
        String[]startMessage = new String[]{};
        switch (random.nextInt(4)) {
            case 0 -> startMessage = new String[]{
                    ChatColor.YELLOW + "金胡萝卜神：" + team + ChatColor.YELLOW + "状态火热，开始了一次提现",
            };
            case 1 -> startMessage = new String[]{
                    ChatColor.YELLOW + "金胡萝卜神：" + team + ChatColor.YELLOW + "把钱箱塞进了提现站，提现开始！",
            };
            case 2 -> startMessage = new String[]{
                    ChatColor.LIGHT_PURPLE + "AlliGator：" + team + ChatColor.LIGHT_PURPLE + "开始提现了！",
            };
            case 3 -> startMessage = new String[]{
                    ChatColor.LIGHT_PURPLE + "AlliGator：有队伍开始提现了，是" + team + ChatColor.LIGHT_PURPLE + "！",
            };
        }
        broadCastManyMessages(startMessage,40L,20L);
    }
    public void cashOutCompleteBC(String team){
        String[]startMessage = new String[]{};
        switch (random.nextInt(4)) {
            case 0 -> startMessage = new String[]{
                    ChatColor.YELLOW + "金胡萝卜神：" + team + ChatColor.YELLOW + "拿下了这次提现！",
            };
            case 1 -> startMessage = new String[]{
                    ChatColor.YELLOW + "金胡萝卜神：" + team + ChatColor.YELLOW + "将提现收入了囊中！",
            };
            case 2 -> startMessage = new String[]{
                    ChatColor.LIGHT_PURPLE + "AlliGator：" + team + ChatColor.LIGHT_PURPLE + "冲上头名！",
            };
            case 3 -> startMessage = new String[]{
                    ChatColor.LIGHT_PURPLE + "AlliGator：这次提现进到了" + team + ChatColor.LIGHT_PURPLE + "的口袋中！",
            };
        }
        broadCastManyMessages(startMessage,40L,20L);
    }
    public void bankCoins(String team,boolean isBigMoney){
        String[]startMessage = new String[]{};
        if(isBigMoney) {
            switch (random.nextInt(4)) {
                case 0 -> startMessage = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：标杆级表现！" + team + ChatColor.YELLOW + "的一位成员存入了一大笔金币！",
                };
                case 1 -> startMessage = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：" + team + ChatColor.YELLOW + "的一位成员让他们的队伍富起来了！一定是冲着冠军去的！",
                };
                case 2 -> startMessage = new String[]{
                        ChatColor.LIGHT_PURPLE + "AlliGator：WOW！" + team + ChatColor.LIGHT_PURPLE + "的一位成员存的金币惊掉了存钱站的下巴！",
                };
                case 3 -> startMessage = new String[]{
                        ChatColor.LIGHT_PURPLE + "AlliGator：" + team + ChatColor.LIGHT_PURPLE + "的一位成员存入的金币让存钱站的计数表转了好几圈！太棒了！",
                };
            }
        }else {
            switch (random.nextInt(4)) {
                case 0 -> startMessage = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：" + team + ChatColor.YELLOW + "在存钱站中存入了一笔金币",
                };
                case 1 -> startMessage = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：" + team + ChatColor.YELLOW + "的账务进账了一小笔金币",
                };
                case 2 -> startMessage = new String[]{
                        ChatColor.LIGHT_PURPLE + "AlliGator：" + team + ChatColor.LIGHT_PURPLE + "的一位成员提高了队伍的含金量",
                };
                case 3 -> startMessage = new String[]{
                        ChatColor.LIGHT_PURPLE + "AlliGator：每一枚金币都重要！" + team + ChatColor.LIGHT_PURPLE + "存钱了！",
                };
            }
        }
        broadCastManyMessages(startMessage,40L,20L);
    }
    public void spitCoins(String team){
        String[]startMessage = new String[]{};
        switch (random.nextInt(4)) {
            case 0 -> startMessage = new String[]{
                    ChatColor.YELLOW + "金胡萝卜神：" + team + ChatColor.YELLOW + "的一位成员将金矿搬到了竞技场里",
            };
            case 1 -> startMessage = new String[]{
                    ChatColor.YELLOW + "金胡萝卜神：" + team + ChatColor.YELLOW + "的一位成员丢掉了一大笔钱，其他人致富的机会来了！",
            };
            case 2 -> startMessage = new String[]{
                    ChatColor.LIGHT_PURPLE + "AlliGator：" + team + ChatColor.LIGHT_PURPLE + "的一位成员身上的金币散落了一地，谁找到就归谁！",
            };
            case 3 -> startMessage = new String[]{
                    ChatColor.LIGHT_PURPLE + "AlliGator：Oops，" + team + ChatColor.LIGHT_PURPLE + "的一位成员倒霉咯！身上的金币掉光光",
            };
        }
        broadCastManyMessages(startMessage,40L,20L);
    }
    public void carryManyCoins(String team){
        String[]startMessage = new String[]{};
        switch (random.nextInt(4)) {
            case 0 -> startMessage = new String[]{
                    ChatColor.YELLOW + "金胡萝卜神：" + team + ChatColor.YELLOW + "的一位成员身上装满了金币，但是在“存钱至上”中，玩家可能会随时变得两手空空",
            };
            case 1 -> startMessage = new String[]{
                    ChatColor.YELLOW + "金胡萝卜神：" + team + ChatColor.YELLOW + "的一位成员兜里装满了金币，到底要不要存呢，是个问题",
            };
            case 2 -> startMessage = new String[]{
                    ChatColor.LIGHT_PURPLE + "AlliGator：" + team + ChatColor.LIGHT_PURPLE + "的一位成员简直就是行走的金库！希望这位成员不要把钱弄丢了",
            };
            case 3 -> startMessage = new String[]{
                    ChatColor.LIGHT_PURPLE + "AlliGator：" + team + ChatColor.LIGHT_PURPLE + "的一位成员身上带有一大笔金币！简直是诱人犯罪",
            };
        }
        broadCastManyMessages(startMessage,40L,20L);
    }
    public void stealCashOutBC(String team){
        String[]startMessage = new String[]{};
        switch (random.nextInt(4)) {
            case 0 -> startMessage = new String[]{
                    ChatColor.YELLOW + "金胡萝卜神：" + team + ChatColor.YELLOW + "偷取了一次提现！",
                    ChatColor.LIGHT_PURPLE + "AlliGator：好偷！",
            };
            case 1 -> startMessage = new String[]{
                    ChatColor.YELLOW + "金胡萝卜神：" + team + ChatColor.YELLOW + "偷取了提现！",
                    ChatColor.LIGHT_PURPLE + "AlliGator：干得漂亮！",
            };
            case 2 -> startMessage = new String[]{
                    ChatColor.LIGHT_PURPLE + "AlliGator：" + team + ChatColor.LIGHT_PURPLE + "偷取了一次提现！",
                    ChatColor.YELLOW + "金胡萝卜神：这件事可能在任何地方都是违法的，但在这里不是",
            };
            case 3 -> startMessage = new String[]{
                    ChatColor.LIGHT_PURPLE + "AlliGator：" + team + ChatColor.LIGHT_PURPLE + "精妙出击，偷取了一次提现！",
                    ChatColor.YELLOW + "金胡萝卜神：好孩子不要学哦",
            };
        }
        broadCastManyMessages(startMessage,40L,20L);
    }
    public void destroyBC(){
        String[]startMessage = new String[]{};
        switch (random.nextInt(4)) {
            case 0 -> startMessage = new String[]{
                    ChatColor.YELLOW + "金胡萝卜神：有位选手不喜欢我们竞技场原来的构造",
                    ChatColor.YELLOW + "金胡萝卜神：不知道在他们改造完之前竞技场还需要承受多少攻击"
            };
            case 1 -> startMessage = new String[]{
                    ChatColor.LIGHT_PURPLE + "AlliGator：有位选手想看看我们竞技场的墙壁能承受多少伤害",
                    ChatColor.LIGHT_PURPLE + "AlliGator：继续努力",
            };
        }
        broadCastManyMessages(startMessage,20L,30L);
    }
    public void suddenDeath(int id){
        String[]startMessage = new String[]{
                ChatColor.YELLOW + "金胡萝卜神：游戏时间结束！",
                ChatColor.LIGHT_PURPLE + "AlliGator：但是真正的乐趣才刚刚开始，让我们进入到Sudden Death环节！",
                ChatColor.YELLOW + "金胡萝卜神：我们调整了游戏规则，选手们的装备被清空了，并且取消了自然回血",
                ChatColor.LIGHT_PURPLE + "AlliGator：最后存活的队伍即可获得胜利！让我们看看会有什么特殊事件来炒热气氛吧！",
        };
        broadCastManyMessages(startMessage,0L,20L);
        String[]message = new String[0];
        switch (id){
            case 0:
                message = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：让我们来打雪仗吧！",
                        ChatColor.LIGHT_PURPLE + "AlliGator：所有的选手都会拿到一个“特制”雪球",
                };
                break;
            case 1:
                message = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：小心脚下！现在地板上会涌出岩浆！",
                        ChatColor.LIGHT_PURPLE + "AlliGator：岩浆将会燃烧处于最低位置的选手！看来选手们要去争夺“高处优势”了",
                };
                break;
            case 2:
                message = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：这次选手们不用担心现金不够了，因为每个选手都会获得一个钱箱！",
                        ChatColor.LIGHT_PURPLE + "AlliGator：钱箱将会是选手们的唯一武器！",
                };
                break;
            case 3:
                message = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：Go Go Go！选手们冲锋的脚步停不下来了！",
                        ChatColor.LIGHT_PURPLE + "AlliGator：看来场面将会变得很乱！",
                };
                break;
            case 4:
                message = new String[]{
                        ChatColor.YELLOW + "金胡萝卜神：接下来我们的选手们将会体验到什么叫“一刀一个”",
                        ChatColor.LIGHT_PURPLE + "AlliGator：选手们都会获得“大师剑”，“大师剑”可以发射干扰视线的剑气",
                };
                break;
        }
        broadCastManyMessages(message,100L,20L);
    }
}

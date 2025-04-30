package Events;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StopSkillEvent extends Event implements Cancellable {
    private boolean cancel;
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    Player player;
    int skill;

    public StopSkillEvent(Player p, int s){
        player = p;
        skill = s;
    }

    public Player getPlayer() {
        return player;
    }

    public int getSkill() {
        return skill;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }
    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
    public static HandlerList getHandlerList(){
        return HANDLERS_LIST;
    }
}

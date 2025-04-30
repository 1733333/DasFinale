package Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerKillPlayerEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    Player killer;
    Player dead;
    int type;

    public PlayerKillPlayerEvent(Player killer,Player dead, int type) {
        this.killer = killer;
        this.dead = dead;
        this.type = type;
    }

    public Player getDead() {
        return dead;
    }

    public Player getKiller() {
        return killer;
    }

    public int getType() {
        return type;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
    public static HandlerList getHandlerList(){
        return HANDLERS_LIST;
    }
}

package Events;

import Listeners.WeaponListener;
import Universal.GameStatus;
import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStartEvent extends Event implements Cancellable {
    private boolean cancel;
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    World world;
    int map,gameMode;
    public GameStartEvent(World w,int m,int g){
        world = w;
        this.map = m;
        this.gameMode = g;
    }

    public World getWorld() {
        return world;
    }
    public int getMap() {
        return map;
    }
    public int getGameMode() {
        return gameMode;
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
    public static HandlerList getHandlerList() {return HANDLERS_LIST;}
}

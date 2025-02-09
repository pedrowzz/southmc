package wwz.pedro.vital.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import wwz.pedro.vital.essencial.PrefixType;
import wwz.pedro.vital.essencial.Tag;

public class PlayerUpdateTablistEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Tag tag;
    private final PrefixType prefixType;

    public PlayerUpdateTablistEvent(Player player, Tag tag, PrefixType prefixType) {
        this.player = player;
        this.tag = tag;
        this.prefixType = prefixType;
    }

    public Player getPlayer() {
        return player;
    }

    public Tag getTag() {
        return tag;
    }

    public PrefixType getPrefixType() {
        return prefixType;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

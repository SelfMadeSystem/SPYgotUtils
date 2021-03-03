package uwu.smsgamer.spygotutils.listener.bungee;

import de.exceptionflug.protocolize.api.event.*;
import de.exceptionflug.protocolize.api.handler.PacketAdapter;
import de.exceptionflug.protocolize.api.protocol.*;
import net.md_5.bungee.protocol.packet.Chat;
import uwu.smsgamer.spygotutils.managers.*;

public class BPacketProcessor extends PacketAdapter<Chat> {
    private final boolean b;
    public BPacketProcessor(boolean b) {
        super(b ? Stream.UPSTREAM : Stream.DOWNSTREAM, Chat.class);
        ProtocolAPI.getEventManager().registerListener(this);
        this.b = b;
    }

    @Override
    public void send(PacketSendEvent<Chat> event) {
        Chat packet = event.getPacket();
        System.out.println(b + ":send:" + packet.getPosition() + ":" + packet.getMessage());
        if (!b) return;
        if (packet.getPosition() == 1) { // todo: not json
            AbstractChatFilter.Result result = ChatFilterManager.getInstance().chatFilter.outgoingChat(event.getPlayer(), null, packet.getMessage());
            if (!result.didSomething) return;
            packet.setMessage(result.message);
        }
    }

    @Override
    public void receive(PacketReceiveEvent<Chat> event) {
        Chat packet = event.getPacket();
        System.out.println(b + ":receive:" + packet.getPosition() + ":" + packet.getMessage());
        if (b) return;
        if (packet.getPosition() == 1) { // todo: not json
            AbstractChatFilter.Result result = ChatFilterManager.getInstance().chatFilter.outgoingChat(event.getPlayer(), null, packet.getMessage());
            if (!result.didSomething) return;
            packet.setMessage(result.message);
        }
    }
}

package uwu.smsgamer.spygotutils.listener.bungee;

import de.exceptionflug.protocolize.api.event.*;
import de.exceptionflug.protocolize.api.handler.PacketAdapter;
import de.exceptionflug.protocolize.api.protocol.*;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.chat.*;
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
        if (!b) return;
        Chat packet = event.getPacket();
        if (packet.getPosition() == 1) {
            AbstractChatFilter.Result result = ChatFilterManager.getInstance().chatFilter.outgoingChat(event.getPlayer(), getMessageFromJson(packet.getMessage()), packet.getMessage());
            if (!result.didSomething) return;
            if (result.isJson) packet.setMessage(result.message);
            else packet.setMessage("\"" + result.message + "\"");
        }
    }

    @Override
    public void receive(PacketReceiveEvent<Chat> event) {
        if (b) return;
        Chat packet = event.getPacket();
        if (packet.getPosition() == 1) {
            AbstractChatFilter.Result result = ChatFilterManager.getInstance().chatFilter.outgoingChat(event.getPlayer(), getMessageFromJson(packet.getMessage()), packet.getMessage());
            if (!result.didSomething) return;
            if (result.isJson) packet.setMessage(result.message);
            else packet.setMessage("\"" + result.message + "\""); // this should work
        }
    }

    public static String getMessageFromJson(String json) {
        BaseComponent[] textComponent = ComponentSerializer.parse(json);

        return BaseComponent.toPlainText(textComponent);
    }
}

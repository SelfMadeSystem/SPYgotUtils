package uwu.smsgamer.spygotutils.listener.spigot;

import io.github.retrooper.packetevents.event.PacketListenerDynamic;
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import io.github.retrooper.packetevents.packetwrappers.play.out.chat.WrappedPacketOutChat;
import uwu.smsgamer.spygotutils.managers.*;

public class SPacketProcessor extends PacketListenerDynamic {
    @Override
    public void onPacketPlaySend(PacketPlaySendEvent event) {
        if (event.getPacketId() == PacketType.Play.Server.CHAT) {
            WrappedPacketOutChat packet = new WrappedPacketOutChat(event.getNMSPacket());
            if (packet.getChatPosition().equals(WrappedPacketOutChat.ChatPosition.CHAT)) {
                AbstractChatFilter.Result result = ChatFilterManager.getInstance().chatFilter.outgoingChat(event.getPlayer(), packet.getMessage(), null);
                if (!result.didSomething) return;
                event.setNMSPacket(new NMSPacket(
                  new WrappedPacketOutChat(result.message, event.getPlayer().getUniqueId(), result.isJson).asNMSPacket()));
            }
        }
    }
}

package uwu.smsgamer.spygotutils.listener.spigot;

import io.github.retrooper.packetevents.event.PacketListenerAbstract;
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import io.github.retrooper.packetevents.packetwrappers.play.out.chat.WrappedPacketOutChat;
import org.bukkit.Bukkit;
import uwu.smsgamer.spygotutils.managers.*;

public class SPacketProcessor extends PacketListenerAbstract {
    @Override
    public void onPacketPlaySend(PacketPlaySendEvent event) {
        if (event.getPacketId() == PacketType.Play.Server.CHAT) {
            WrappedPacketOutChat packet = new WrappedPacketOutChat(event.getNMSPacket());
            boolean check = !(Bukkit.getVersion().contains("1.13") || Bukkit.getVersion().contains("1.14") ||
                    Bukkit.getVersion().contains("1.15") || Bukkit.getVersion().contains("1.16"));
            try {
                check = packet.getChatPosition().equals(WrappedPacketOutChat.ChatPosition.CHAT);
            } catch (IllegalArgumentException ignored) {
            }
            if (check) {
                AbstractChatFilter.Result result = ChatFilterManager.getInstance().chatFilter.outgoingChat(event.getPlayer(), packet.getMessage(), null);
                if (!result.didSomething) return;
                event.setNMSPacket(new NMSPacket(
                        new WrappedPacketOutChat(result.message, event.getPlayer().getUniqueId(), result.isJson).asNMSPacket()));
            }
        }
    }
}

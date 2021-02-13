package uwu.smsgamer.spygotutils.listener;

import io.github.retrooper.packetevents.event.*;
import io.github.retrooper.packetevents.event.impl.*;
import io.github.retrooper.packetevents.packettype.PacketType;
import uwu.smsgamer.spygotutils.managers.ChatFilterManager;

public class PacketProcessor extends PacketListenerDynamic {
    @Override
    public void onPacketPlaySend(PacketPlaySendEvent event) {
        if (event.getPacketId() == PacketType.Play.Server.CHAT) {
            ChatFilterManager.getInstance().packetSendEvent(event);
        }
    }
}

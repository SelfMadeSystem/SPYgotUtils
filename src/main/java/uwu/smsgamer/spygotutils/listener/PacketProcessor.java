package uwu.smsgamer.spygotutils.listener;

import io.github.retrooper.packetevents.event.PacketListenerDynamic;
import io.github.retrooper.packetevents.event.impl.*;
import io.github.retrooper.packetevents.packettype.PacketType;
import uwu.smsgamer.spygotutils.managers.ChatFilterManager;

public class PacketProcessor extends PacketListenerDynamic {
    /*@Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        System.out.println(event.getPacketName() + ":" + event.getPacketId());
        switch (event.getPacketId()) {
            case PacketType.Play.Client.CHAT:
                ChatFilterManager.getInstance().chatReceiveEvent(event);
                break;
            case PacketType.Play.Client.TAB_COMPLETE:
                ChatFilterManager.getInstance().tabReceiveEvent(event);
                break;
        }
    }
    */
    @Override
    public void onPacketPlaySend(PacketPlaySendEvent event) {
        if (event.getPacketId() == PacketType.Play.Server.CHAT) {
            ChatFilterManager.getInstance().packetSendEvent(event);
        }
    }
}

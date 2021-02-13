# DON'T DO THIS XD

def onPacketStatusReceive(event):
    print("onPacketStatusReceive" + str(event))


def onPacketStatusSend(event):
    print("onPacketStatusSend" + str(event))


def onPacketLoginReceive(event):
    print("onPacketLoginReceive" + str(event))


def onPacketLoginSend(event):
    print("onPacketLoginSend" + str(event))


def onPacketPlayReceive(event):
    print("onPacketPlayReceive" + str(event))


def onPacketPlaySend(event):
    print("onPacketPlaySend" + str(event))


def onPostPacketPlayReceive(event):
    print("onPostPacketPlayReceive" + str(event))


def onPostPacketPlaySend(event):
    print("onPostPacketPlaySend" + str(event))


def onPostPlayerInject(event):
    print("onPostPlayerInject" + str(event))


def onPlayerInject(event):
    print("onPlayerInject" + str(event))


def onPlayerEject(event):
    print("onPlayerEject" + str(event))


def onPacketEventExternal(event):
    print("onPacketEventExternal" + str(event))


if False:
    packet_listener.packetStatusReceiveFuns.add(onPacketStatusReceive)
    packet_listener.packetStatusSendFuns.add(onPacketStatusSend)
    packet_listener.packetLoginReceiveFuns.add(onPacketLoginReceive)
    packet_listener.packetLoginSendFuns.add(onPacketLoginSend)
    packet_listener.packetPlayReceiveFuns.add(onPacketPlayReceive)
    packet_listener.packetPlaySendFuns.add(onPacketPlaySend)
    packet_listener.postPacketPlayReceiveFuns.add(onPostPacketPlayReceive)
    packet_listener.postPacketPlaySendFuns.add(onPostPacketPlaySend)
    packet_listener.postPlayerInjectFuns.add(onPostPlayerInject)
    packet_listener.playerInjectFuns.add(onPlayerInject)
    packet_listener.playerEjectFuns.add(onPlayerEject)
    packet_listener.packetEventExternalFuns.add(onPacketEventExternal)

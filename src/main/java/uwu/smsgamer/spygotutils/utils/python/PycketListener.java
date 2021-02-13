package uwu.smsgamer.spygotutils.utils.python;

import io.github.retrooper.packetevents.event.*;
import io.github.retrooper.packetevents.event.impl.*;
import org.python.core.*;

import java.util.*;

public class PycketListener extends PacketListenerDynamic {
    public Set<PyFunction> packetStatusReceiveFuns = new HashSet<>();
    public Set<PyFunction> packetStatusSendFuns = new HashSet<>();
    public Set<PyFunction> packetLoginReceiveFuns = new HashSet<>();
    public Set<PyFunction> packetLoginSendFuns = new HashSet<>();
    public Set<PyFunction> packetPlayReceiveFuns = new HashSet<>();
    public Set<PyFunction> packetPlaySendFuns = new HashSet<>();
    public Set<PyFunction> postPacketPlayReceiveFuns = new HashSet<>();
    public Set<PyFunction> postPacketPlaySendFuns = new HashSet<>();
    public Set<PyFunction> postPlayerInjectFuns = new HashSet<>();
    public Set<PyFunction> playerInjectFuns = new HashSet<>();
    public Set<PyFunction> playerEjectFuns = new HashSet<>();
    public Set<PyFunction> packetEventExternalFuns = new HashSet<>();

    private static PycketListener instance;
    public static PycketListener getInstance() {
      if (instance == null) instance = new PycketListener();
      return instance;
    }

    @Override
    public void onPacketStatusReceive(PacketStatusReceiveEvent event) {
        callAll(packetStatusReceiveFuns, event);
    }

    @Override
    public void onPacketStatusSend(PacketStatusSendEvent event) {
        callAll(packetStatusSendFuns, event);
    }

    @Override
    public void onPacketLoginReceive(PacketLoginReceiveEvent event) {
        callAll(packetLoginReceiveFuns, event);
    }

    @Override
    public void onPacketLoginSend(PacketLoginSendEvent event) {
        callAll(packetLoginSendFuns, event);
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        callAll(packetPlayReceiveFuns, event);
    }

    @Override
    public void onPacketPlaySend(PacketPlaySendEvent event) {
        callAll(packetPlaySendFuns, event);
    }

    @Override
    public void onPostPacketPlayReceive(PostPacketPlayReceiveEvent event) {
        callAll(postPacketPlayReceiveFuns, event);
    }

    @Override
    public void onPostPacketPlaySend(PostPacketPlaySendEvent event) {
        callAll(postPacketPlaySendFuns, event);
    }

    @Override
    public void onPostPlayerInject(PostPlayerInjectEvent event) {
        callAll(postPlayerInjectFuns, event);
    }

    @Override
    public void onPlayerInject(PlayerInjectEvent event) {
        callAll(playerInjectFuns, event);
    }

    @Override
    public void onPlayerEject(PlayerEjectEvent event) {
        callAll(playerEjectFuns, event);
    }

    @Override
    public void onPacketEventExternal(PacketEvent event) {
        callAll(packetEventExternalFuns, event);
    }

    public void callAll(Set<PyFunction> functions, PacketEvent event) {
        for (PyFunction function : functions) {
            try {
                function.__call__(Py.java2py(event));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

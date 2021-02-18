from net.md_5.bungee.api.event import ClientConnectEvent
from net.md_5.bungee.event import EventPriority


def test(event):
    """
    :type event: ClientConnectEvent
    """
    print("ClientConnectEvent UwU")
    print(event)


# Events registered on enable, not load.
def on_enable():
    register_event(ClientConnectEvent, EventPriority.NORMAL, test)

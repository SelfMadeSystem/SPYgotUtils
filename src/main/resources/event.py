from org.bukkit.event import EventPriority
from org.bukkit.event.player import AsyncPlayerChatEvent


def test(event):
    """
    :type event: AsyncPlayerChatEvent
    """
    msg = event.getMessage()  # type: str
    if msg.startswith("p!"):
        event.setCancelled(True)
        event.getPlayer().sendMessage("Hewwo fwom jythwon!")


# Events registered on enable, not load.
def on_enable():
    register_event(AsyncPlayerChatEvent, EventPriority.MONITOR, test)

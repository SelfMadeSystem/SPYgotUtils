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


print("============================================== Hello! Jython script loaded!! "
      "==============================================")

register_event(AsyncPlayerChatEvent, EventPriority.MONITOR, test)

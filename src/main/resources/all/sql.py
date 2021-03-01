# from uwu.smsgamer.senapi.utils.sql import MySQLDB
# from uwu.smsgamer.senapi.utils.sql import SenDB
from uwu.smsgamer.senapi.utils.sql import SQLiteDB
from java.io import File


# Check source code to see how it works.
# https://github.com/True-cc/SPYgotUtils/tree/master/src/main/java/uwu/smsgamer/senapi/utils/sql
database = SQLiteDB(str(get_data_folder()) + File.separator + "example.db")
database.connect()

joinedUsers = database.createTable("joinedUsers", "playerUuid UUID, username VARCHAR(64), PRIMARY KEY (playerUuid)")


def on_enable():
    if on_spigot:
        from org.bukkit.event import EventPriority
        from org.bukkit.event.player import PlayerJoinEvent

        def join(event):
            player = event.getPlayer()
            if not joinedUsers.exists("playerUuid=?", player.getUniqueId()):
                print("Set!")
                joinedUsers.add("playerUuid, username", "?,?", [player.getUniqueId(), player.getName()])
            else:
                print("Already Set!")

        register_event(PlayerJoinEvent, EventPriority.MONITOR, join)
    else:
        from net.md_5.bungee.api.event import PostLoginEvent
        from net.md_5.bungee.event import EventPriority

        def join(event):
            player = event.getPlayer()

            if not joinedUsers.exists("playerUuid=?", player.getUniqueId()):
                print("Set!")
                joinedUsers.add("playerUuid, username", "?,?", [player.getUniqueId(), player.getName()])
            else:
                print("Already Set!")

        register_event(PostLoginEvent, EventPriority.NORMAL, join)


def on_disable():
    database.disconnect()

# Script runs on load.


# Happens on plugin enable
def on_enable():  # or  onEnable  or enable
    print("onEnable")


# Happens when you do /spygotutils reload
def on_reload():  # or  onReload  or  reload
    print("onReload")


# Happens on server stop (or server reload or plugman reload)
def on_disable():  # or  onDisable  or  disable
    print("onDisable")


print("onLoad")

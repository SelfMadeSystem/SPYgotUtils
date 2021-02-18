def executor(sender, args):
    sender.sendMessage("Hello. Sender: " + str(sender) + " Args: " + str(args))


# Commands generally get registered on enable, not on load.
def on_enable():
    command = Command("bpycmdtest", ["bpytest"])

    command.set_executor(executor)

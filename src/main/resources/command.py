def executor(sender, label, args):
    sender.sendMessage("Hello. Label: " + label + " Args: " + str(args))


def completer(sender, label, args):
    result = []
    result.extend(args)
    result.append(label)
    return result


command = Command("pycmdtest", "Command test for python script!", "/pycmdtest UwU", ["pytest"])

command.set_executor(executor)
command.set_tab_completer(completer)

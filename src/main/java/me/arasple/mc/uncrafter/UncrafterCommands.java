package me.arasple.mc.uncrafter;

import io.izzel.taboolib.module.command.base.BaseCommand;
import io.izzel.taboolib.module.command.base.BaseMainCommand;
import io.izzel.taboolib.module.locale.TLocale;
import me.arasple.mc.uncrafter.objects.UncrafterMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Arasple
 * @date 2019/8/17 17:12
 */
@BaseCommand(name = "uncrafter", aliases = "ucr")
public class UncrafterCommands extends BaseMainCommand {

    @Override
    public String getCommandTitle() {
        return "&3「&8&m--------------------------------------------------&3」";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            TLocale.sendTo(sender, "COMMANDS.NOT-PLAYER");
            return true;
        }
        if (args.length == 0) {
            displayHelp(sender);
            return true;
        }
        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "open":
                    if (!sender.hasPermission("uncrafter.open")) {
                        TLocale.sendTo(sender, "COMMANDS.NO-PERMISSION", "uncrafter.open");
                        return true;
                    }
                    UncrafterMenu.openFor((Player) sender);
                    break;
                case "get":
                    if (!sender.hasPermission("uncrafter.get")) {
                        TLocale.sendTo(sender, "COMMANDS.NO-PERMISSION", "uncrafter.get");
                        return true;
                    }
                    ((Player) sender).getInventory().addItem(Uncrafter.getUncrafterItem().getItem());
                    break;
                default:
                    displayHelp(sender);
                    break;
            }
            return true;
        }
        return true;
    }

    private void displayHelp(CommandSender sender) {
        sender.sendMessage("§6「§8§m--------------------------------------------------§6」");
        sender.sendMessage();
        sender.sendMessage("§8» §7/§6Uncrafter §eOpen §2打开分解系统GUI");
        sender.sendMessage("§8» §7/§6Uncrafter §eGet §2取得一个移动分解者GUI物品");
        sender.sendMessage();
        sender.sendMessage("§6「§8§m--------------------------------------------------§6」");
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Stream.of("open", "get").filter(x -> args[0].toLowerCase().startsWith(x)).collect(Collectors.toList());
        }
        return null;
    }


}

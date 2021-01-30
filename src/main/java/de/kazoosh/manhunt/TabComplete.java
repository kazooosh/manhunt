package de.kazoosh.manhunt;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter {

    List<String> arguments = new ArrayList<String>();

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 0) {
            arguments.add("hunter");
            arguments.add("runner");
            arguments.add("reset");
            arguments.add("start");
            arguments.add("help");
        }

        List<String> results = new ArrayList<String>();
        if (args.length == 1) {
            for (String a : arguments) {
                if(a.toLowerCase().startsWith(args[0].toLowerCase())) {
                    results.add(a);
                }
            }
            return results;
        } else {
            return null;
        }
    }
}

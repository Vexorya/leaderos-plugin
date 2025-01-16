package net.leaderos.plugin.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.Optional;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import lombok.RequiredArgsConstructor;
import net.leaderos.plugin.Bukkit;
import net.leaderos.plugin.api.LeaderOSAPI;
import net.leaderos.plugin.helpers.ChatUtil;
import net.leaderos.shared.Shared;
import net.leaderos.shared.helpers.MoneyUtil;
import net.leaderos.shared.helpers.Placeholder;
import net.leaderos.shared.helpers.RandomUtil;
import net.leaderos.shared.helpers.UrlUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author poyrazinan, hyperion
 * @since 1.0
 */
@Command("leaderos")
@RequiredArgsConstructor
public class LeaderOSCommand extends BaseCommand {

    /**
     * default command of leaderos
     * @param sender command sender
     */
    @Default
    @Permission("leaderos.help")
    public void defaultCommand(CommandSender sender) {
        for (String message : Bukkit.getInstance().getLangFile().getMessages().getHelp()) {
            ChatUtil.sendMessage(sender, message);
        }
    }

    /**
     * reload command of plugin
     * @param sender commandsender
     */
    @Permission("leaderos.reload")
    @SubCommand("reload")
    public void reloadCommand(CommandSender sender) {
        Bukkit.getInstance().getConfigFile().load(true);
        Bukkit.getInstance().getLangFile().load(true);
        Bukkit.getInstance().getModulesFile().load(true);

        Shared.setLink(UrlUtil.format(Bukkit.getInstance().getConfigFile().getSettings().getUrl()));
        Shared.setApiKey(Bukkit.getInstance().getConfigFile().getSettings().getApiKey());

        LeaderOSAPI.getModuleManager().reloadModules();
        ChatUtil.sendMessage(sender, Bukkit.getInstance().getLangFile().getMessages().getReload());
    }

    /**
     * Removes credit from targeted user
     * @param sender executor
     * @param amount of to set
     */
    @SubCommand(value = "bonus")
    @Permission("leaderos.credit.bonus")
    public void bonusCommand(CommandSender sender, Integer amount) {
        if (amount <= 0) {
            amount = 0;
        }

        int finalAmount = amount;
        org.bukkit.Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getInstance(), () -> {
            boolean success = LeaderOSAPI.getCreditManager().setBonus(finalAmount);
            if (success) ChatUtil.sendMessage(sender, "&aUpdated.");
            else ChatUtil.sendMessage(sender, "&cError.");
        });
    }

    /**
     * Removes credit from targeted user
     * @param sender executor
     * @param amount of to set
     */
    @SubCommand(value = "coupon")
    @Permission("leaderos.credit.coupon")
    public void createCouponCommand(CommandSender sender, String targetPlayer, Integer amount, @Optional String key) {
        if (key == null) {
            key = "VEX-" + RandomUtil.randomString(6);
        }

        String finalKey = key;
        org.bukkit.Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getInstance(), () -> {
            boolean success = LeaderOSAPI.getCreditManager().createCoupon(targetPlayer, finalKey, amount);
            if (success) {
                Player player = Bukkit.getInstance().getServer().getPlayerExact(targetPlayer);
                if (player != null) {
                    ChatUtil.sendMessage(player, ChatUtil.replacePlaceholders(
                            Bukkit.getInstance().getLangFile().getMessages().getCredit().getReceivedCoupon(),
                            new Placeholder("{key}", finalKey),
                            new Placeholder("{amount}", MoneyUtil.format(amount))
                    ));
                }
                ChatUtil.sendMessage(sender, "&aCreated.");
            }
            else ChatUtil.sendMessage(sender, "&cError.");
        });
    }

}
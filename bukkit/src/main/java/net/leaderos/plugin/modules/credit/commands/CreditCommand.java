package net.leaderos.plugin.modules.credit.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import lombok.RequiredArgsConstructor;
import net.leaderos.plugin.Bukkit;
import net.leaderos.plugin.api.LeaderOSAPI;
import net.leaderos.plugin.api.handlers.UpdateCacheEvent;
import net.leaderos.plugin.helpers.ChatUtil;
import net.leaderos.shared.helpers.MoneyUtil;
import net.leaderos.shared.helpers.Placeholder;
import net.leaderos.shared.helpers.RequestUtil;
import net.leaderos.shared.modules.credit.enums.UpdateType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author hyperion, poyrazinan
 * @since 1.0
 */
@RequiredArgsConstructor
@Command(value = "credits", alias = {"credit", "kredi"})
public class CreditCommand extends BaseCommand {

    /**
     * Adds credit to targeted player
     * @param sender executor
     * @param target to deposit
     * @param amount of credit
     */
    @SubCommand(value = "add", alias = "ekle")
    @Permission("leaderos.credit.add")
    public void addCommand(CommandSender sender, String target, Double amount) {
        amount = MoneyUtil.parseDouble(amount);

        if (amount <= 0) {
            ChatUtil.sendMessage(sender, Bukkit.getInstance().getLangFile().getMessages().getCredit().getCannotSendCreditNegative());
            return;
        }

        if (sender instanceof Player && !RequestUtil.canRequest(((Player)sender).getUniqueId())) {
            ChatUtil.sendMessage(sender, Bukkit.getInstance().getLangFile().getMessages().getHaveRequestOngoing());
            return;
        }

        if (sender instanceof Player) {
            RequestUtil.addRequest(((Player)sender).getUniqueId());
        }

        Double finalAmount = amount;
        org.bukkit.Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getInstance(), () -> {
            boolean addCredit = LeaderOSAPI.getCreditManager().add(target, finalAmount);
            if (addCredit) {
                if (org.bukkit.Bukkit.getPlayerExact(target) != null)
                    // Calls UpdateCache event for update player's cache
                    org.bukkit.Bukkit.getScheduler().runTask(Bukkit.getInstance(),
                            () -> org.bukkit.Bukkit.getPluginManager().callEvent(new UpdateCacheEvent(target, finalAmount, UpdateType.ADD)));

                ChatUtil.sendMessage(sender, ChatUtil.replacePlaceholders(
                        Bukkit.getInstance().getLangFile().getMessages().getCredit().getSuccessfullyAddedCredit(),
                        new Placeholder("{amount}", MoneyUtil.format(finalAmount)),
                        new Placeholder("{target}", target)
                ));
            }
            else
                ChatUtil.sendMessage(sender, Bukkit.getInstance().getLangFile().getMessages().getTargetPlayerNotAvailable());

            if (sender instanceof Player) {
                RequestUtil.invalidate(((Player)sender).getUniqueId());
            }
        });
    }

    /**
     * Removes credit from targeted user
     * @param sender executor
     * @param target player
     * @param amount of currency
     */
    @SubCommand(value = "remove", alias = "sil")
    @Permission("leaderos.credit.remove")
    public void removeCommand(CommandSender sender, String target, Double amount) {
        amount = MoneyUtil.parseDouble(amount);

        if (amount <= 0) {
            ChatUtil.sendMessage(sender, Bukkit.getInstance().getLangFile().getMessages().getCredit().getCannotSendCreditNotEnough());
            return;
        }

        if (sender instanceof Player && !RequestUtil.canRequest(((Player)sender).getUniqueId())) {
            ChatUtil.sendMessage(sender, Bukkit.getInstance().getLangFile().getMessages().getHaveRequestOngoing());
            return;
        }

        if (sender instanceof Player) {
            RequestUtil.addRequest(((Player)sender).getUniqueId());
        }

        Double finalAmount = amount;
        org.bukkit.Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getInstance(), () -> {
            boolean removeCredit = LeaderOSAPI.getCreditManager().remove(target, finalAmount);
            if (removeCredit) {
                if (org.bukkit.Bukkit.getPlayerExact(target) != null)
                    // Calls UpdateCache event for update player's cache
                    org.bukkit.Bukkit.getScheduler().runTask(Bukkit.getInstance(),
                            () -> org.bukkit.Bukkit.getPluginManager().callEvent(new UpdateCacheEvent(target, finalAmount, UpdateType.REMOVE)));

                ChatUtil.sendMessage(sender, ChatUtil.replacePlaceholders(
                        Bukkit.getInstance().getLangFile().getMessages().getCredit().getSuccessfullyRemovedCredit(),
                        new Placeholder("{amount}", MoneyUtil.format(finalAmount)),
                        new Placeholder("{target}", target)
                ));
            } else
                ChatUtil.sendMessage(sender, Bukkit.getInstance().getLangFile().getMessages().getTargetPlayerNotAvailable());

            if (sender instanceof Player) {
                RequestUtil.invalidate(((Player)sender).getUniqueId());
            }
        });

    }

    /**
     * Sets credit for target player
     * @param sender executor
     * @param target player
     * @param amount new currency
     */
    @SubCommand(value = "set", alias = "ayarla")
    @Permission("leaderos.credit.set")
    public void setCommand(CommandSender sender, String target, Double amount) {
        amount = MoneyUtil.parseDouble(amount);

        if (sender instanceof Player && !RequestUtil.canRequest(((Player)sender).getUniqueId())) {
            ChatUtil.sendMessage(sender, Bukkit.getInstance().getLangFile().getMessages().getHaveRequestOngoing());
            return;
        }

        if (sender instanceof Player) {
            RequestUtil.addRequest(((Player)sender).getUniqueId());
        }

        Double finalAmount = amount;
        org.bukkit.Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getInstance(), () -> {
            boolean setCredit = LeaderOSAPI.getCreditManager().set(target, finalAmount);
            if (setCredit) {
                if (org.bukkit.Bukkit.getPlayerExact(target) != null)
                    // Calls UpdateCache event for update player's cache
                    org.bukkit.Bukkit.getScheduler().runTask(Bukkit.getInstance(), () -> org.bukkit.Bukkit.getPluginManager().callEvent(new UpdateCacheEvent(target, finalAmount, UpdateType.SET)));

                ChatUtil.sendMessage(sender, ChatUtil.replacePlaceholders(
                        Bukkit.getInstance().getLangFile().getMessages().getCredit().getSuccessfullySetCredit(),
                        new Placeholder("{amount}", MoneyUtil.format(finalAmount)),
                        new Placeholder("{target}", target)
                ));
            }
            else
                ChatUtil.sendMessage(sender, Bukkit.getInstance().getLangFile().getMessages().getTargetPlayerNotAvailable());

            if (sender instanceof Player) {
                RequestUtil.invalidate(((Player)sender).getUniqueId());
            }
        });
    }

    /**
     * Removes credit from targeted user
     * @param sender executor
     * @param amount of to set
     */
    @SubCommand(value = "remove", alias = "sil")
    @Permission("leaderos.credit.remove")
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

}

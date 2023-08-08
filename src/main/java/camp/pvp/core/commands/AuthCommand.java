package camp.pvp.core.commands;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.utils.Colors;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AuthCommand {

    private Core plugin;
    private GoogleAuthenticator gAuth;
    public AuthCommand(Core plugin) {
        this.plugin = plugin;
        this.gAuth = new GoogleAuthenticator();
    }

    @Command(name = "auth",
            aliases = {"2fa"},
            description = "Authorize yourself on the server.",
            inGameOnly = true)
    public void auth(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
        String[] args = commandArgs.getArgs();

        if(args.length > 0) {
            switch(args[0].toLowerCase()) {
                case "setup":
                    if(profile.getAuthKey() != null) {
                        player.sendMessage(ChatColor.RED + "You already have two factor authentication set up.");
                        return;
                    }

                    if(!player.hasPermission("core.staff")) {
                        player.sendMessage(ChatColor.RED + "You do not have permission to set up two factor authentication on this account.");
                        return;
                    }

                    GoogleAuthenticatorKey key = gAuth.createCredentials();
                    profile.setAuthKey(key.getKey());
                    profile.setAuthenticated(false);

                    player.sendMessage(new String[]{
                            Colors.get("&6&lYour Google Authenticator key has been created."),
                            Colors.get("&6Key &c(DO NOT SHARE)&7: &f" + key.getKey()),
                            Colors.get("&aAfter you have added your key to your preferred authenticator app, please type &f/auth <password> &ato authenticate yourself."),
                            Colors.get("&7&oWe recommend using Authy for your authenticator app.")
                    });
                    return;
                case "remove":
                    if(profile.getAuthKey() == null) {
                        player.sendMessage(ChatColor.RED + "Your account does not have two factor authentication enabled.");
                        return;
                    }

                    if(player.hasPermission("core.staff")) {
                        player.sendMessage(ChatColor.RED + "You are required to have two factor authentication set up on your account, so you cannot remove it.");
                        return;
                    }

                    if(profile.isAuthenticated()) {
                        profile.setAuthKey(null);
                        profile.setAuthenticated(true);
                        player.sendMessage(ChatColor.GREEN + "Your two factor authentication has been disabled.");
                    }
                    return;
                default:
                    if(profile.isAuthenticated()) {
                        player.sendMessage(ChatColor.RED + "You are already authenticated.");
                        return;
                    }

                    int password;
                    try {
                        password = Integer.parseInt(args[0]);
                    } catch (NumberFormatException ignored) {
                        player.sendMessage(ChatColor.RED + "You must provide a valid password.");
                        return;
                    }

                    boolean authenticated = gAuth.authorize(profile.getAuthKey(), password);
                    if(authenticated) {
                        profile.setAuthenticated(true);
                        player.sendMessage(ChatColor.GREEN + "You have been authenticated.");
                    } else {
                        player.kickPlayer(ChatColor.RED + "Invalid authentication code.");
                    }
                    return;
            }
        }

        String label = commandArgs.getLabel();
        StringBuilder help = new StringBuilder();
        help.append("&6&l/" + label + " &r&6Help");
        help.append("\n&6/" + label + " <password> &7- &fAuthenticate yourself with your provided password.");
        help.append("\n&6/" + label + " setup &7- &fSetup your authenticator.");
        help.append("\n&6/" + label + " remove &7- &fRemove the authenticator from your account.");

        player.sendMessage(Colors.get(help.toString()));
    }
}

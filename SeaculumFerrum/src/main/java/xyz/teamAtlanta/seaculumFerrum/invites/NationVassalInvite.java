package xyz.teamAtlanta.seaculumFerrum.invites;

import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.inviteobjects.NationAllyNationInvite;
import org.bukkit.command.CommandSender;
import xyz.teamAtlanta.seaculumFerrum.Main;

public class NationVassalInvite extends NationAllyNationInvite {

    public NationVassalInvite(CommandSender directSender, Nation receiver, Nation sender) {
        super(directSender, receiver, sender);
    }

    @Override
    public void accept() throws TownyException {
        Nation receiverNation = getReceiver();
        Nation senderNation = getSender();
        Main.addNewNationVassal(getReceiver(), getSender());

        TownyMessaging.sendPrefixedNationMessage(senderNation, "&b" + receiverNation.getName() + "承诺成为我们的新宗主国，窝们现在要变成苟了！但好处显而易见：窝们已经受到了他們的保护");
        TownyMessaging.sendPrefixedNationMessage(receiverNation, "&b" + senderNation.getName() + "宣誓效忠，成为了像窝們"+ receiverNation.getName() + "这样伟大郭嘉的一名封臣!他们现在是我们的一个附庸");

        receiverNation.deleteReceivedInvite(this);
        senderNation.deleteSentAllyInvite(this);

        receiverNation.save();
        senderNation.save();
    }

    @Override
    public void decline(boolean fromSender) {
        Nation receiverNation = getReceiver();
        Nation senderNation = getSender();

        receiverNation.deleteReceivedInvite(this);
        senderNation.deleteSentAllyInvite(this);

        if (!fromSender) {
            TownyMessaging.sendPrefixedNationMessage(senderNation, "&b" + receiverNation.getName() + "拒绝了我们的效忠邀请!");
        } else {
            TownyMessaging.sendPrefixedNationMessage(receiverNation,"&b" + senderNation.getName() + "的附庸邀请已过期");
        }
    }
}


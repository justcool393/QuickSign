package me.DDoS.Quicksign.command;

import java.util.List;
import me.DDoS.Quicksign.util.QSBlackList;
import me.DDoS.Quicksign.QuickSign;
import me.DDoS.Quicksign.sign.QSSignState;
import me.DDoS.Quicksign.util.QSUtil;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 *
 * @author DDoS
 */
public class QSPasteCommand implements QSCommand {

    private List<Sign> signs;
    private String[] text;
    private boolean colors;
    private QSSignState[] backups;

    public QSPasteCommand(List<Sign> signs, String[] text, boolean colors) {

        this.signs = signs;
        this.text = text;
        this.colors = colors;
        backups = new QSSignState[signs.size()];

    }

    @Override
    public boolean run(Player player) {

        if (QSBlackList.verify(text, player)) {

            QSUtil.tell(player, "You are not allowed to place the provided text.");
            return false;

        }

        if (!colors) {

            QSUtil.tell(player, "You don't have permission for colors. They will not be applied.");

            for (String line : text) {

                line = line.replaceAll("&([0-9[a-fA-F]])", "");

            }

        } else {

            for (String line : text) {

                line = line.replaceAll("&([0-9[a-fA-F]])", "\u00A7$1");

            }

        }

        int i = 0;

        for (Sign sign : signs) {

            backups[i] = new QSSignState(sign);
            sign.setLine(0, text[0]);
            sign.setLine(1, text[1]);
            sign.setLine(2, text[2]);
            sign.setLine(3, text[3]);
            sign.update();
            logChange(player, sign);
            i++;

        }

        QSUtil.tell(player, "Edit successful.");
        return true;

    }

    @Override
    public void undo(Player player) {

        int i = 0;

        for (Sign sign : signs) {

            String[] lines = backups[i].getLines();
            sign.setLine(0, lines[0]);
            sign.setLine(1, lines[1]);
            sign.setLine(2, lines[2]);
            sign.setLine(3, lines[3]);
            sign.update();
            logChange(player, sign);
            i++;

        }

        QSUtil.tell(player, "Undo successful.");

    }

    @Override
    public void redo(Player player) {

        for (Sign sign : signs) {

            sign.setLine(0, text[0]);
            sign.setLine(1, text[1]);
            sign.setLine(2, text[2]);
            sign.setLine(3, text[3]);
            sign.update();
            logChange(player, sign);

        }

        QSUtil.tell(player, "Redo successful.");

    }

    private void logChange(Player player, Sign sign) {

        if (QuickSign.consumer == null) {

            return;

        }

        QuickSign.consumer.queueSignPlace(player.getName(), sign);

    }
}
package me.DDoS.Quicksign.command;

import java.util.List;
import me.DDoS.Quicksign.util.QSBlackList;
import me.DDoS.Quicksign.QuickSign;
import me.DDoS.Quicksign.util.QSUtil;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 *
 * @author DDoS
 */
public class QSReplaceCommand implements QSCommand {

    private List<Sign> signs;
    private int line;
    private String text1;
    private String text2;
    private boolean colors;
    private String[] backups;

    public QSReplaceCommand(List<Sign> signs, int line, String text1, String text2, boolean colors) {

        this.signs = signs;
        this.line = line;
        this.text1 = text1;
        this.text2 = text2;
        this.colors = colors;
        backups = new String[signs.size()];

    }

    @Override
    public boolean run(Player player) {

        if (QSBlackList.verify(text2, player)) {
            
            QSUtil.tell(player, "You are not allowed to place the provided text.");
            return false;
            
        }
        
        if (line < 0 || line > 3) {

            QSUtil.tell(player, "Invalid line.");
            return false;

        }

        if (text2.length() > 15) {

            QSUtil.tell(player, "The provided text is longer than 15 characters. It will be truncated.");
            text2 = text2.substring(0, 16);

        }

        if (!colors) {

            QSUtil.tell(player, "You don't have permission for colors. They will not be applied.");
            text2 = text2.replaceAll("&([0-9[a-fA-F]])", "");

        } else {
        
            text2 = text2.replaceAll("&([0-9[a-fA-F]])", "\u00A7$1");

        }
        
        int i = 0;

        for (Sign sign : signs) {
            
            backups[i] = sign.getLine(line);
            sign.setLine(line, sign.getLine(line).replaceAll(text1, text2));
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
            
            sign.setLine(line, backups[i]);
            sign.update();
            logChange(player, sign);
            i++;

        }

        QSUtil.tell(player, "Undo successful.");

    }

    @Override
    public void redo(Player player) {

        for (Sign sign : signs) {
            
            sign.setLine(line, sign.getLine(line).replaceAll(text1, text2));
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
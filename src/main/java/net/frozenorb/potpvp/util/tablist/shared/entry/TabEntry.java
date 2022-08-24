package net.frozenorb.potpvp.util.tablist.shared.entry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.frozenorb.potpvp.util.tablist.shared.skin.SkinType;

@RequiredArgsConstructor
@Getter
@Setter
public class TabEntry {

    private int x;
    private int y;
    private String text;
    private int ping;

    private String[] skinData = SkinType.DARK_GRAY.getSkinData();

    /**
     * Constructor to make a new tab entry object with provided skin data
     *
     * @param x        the x axis
     * @param y        the y axis
     * @param text     the text to display on the slot
     * @param ping     the displayed latency
     * @param skinData the data to display in the skin slot
     */
    public TabEntry(int x, int y, String text, int ping, String[] skinData) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.ping = ping;
        this.skinData = skinData;
    }

}
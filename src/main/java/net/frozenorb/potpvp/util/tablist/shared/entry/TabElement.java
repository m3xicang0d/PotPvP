package net.frozenorb.potpvp.util.tablist.shared.entry;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.potpvp.util.tablist.shared.skin.SkinType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TabElement {

    private List<TabEntry> entries = new ArrayList<>();

    private String header = "";
    private String footer = "";

    /**
     * Get an entry by location in the tab element
     *
     * @param x the x axis
     * @param y the y axis
     * @return the entry
     */
    public TabEntry getEntry(int x, int y) {
        return this.entries.stream()
                .filter(entry -> entry.getX() == x && entry.getY() == y)
                .findFirst().orElseGet(() -> new TabEntry(x, y, "", 0, SkinType.DARK_GRAY.getSkinData()));
    }

    /**
     * Add a new entry to the element
     *
     * @param index the index to get the axiss from
     * @param text  the text to display on the slot
     */
    public void add(int index, String text) {
        this.add(index, text, 0);
    }

    /**
     * Add a new entry to the element
     *
     * @param x    the x axis
     * @param y    the y axis
     * @param text the text to display on the slot
     */
    public void add(int x, int y, String text) {
        this.add(x, y, text, 0);
    }

    /**
     * Add a new entry to the element
     *
     * @param index the index to get the axiss from
     * @param text  the text to display on the slot
     * @param ping  the ping to display
     */
    public void add(int index, String text, int ping) {
        this.add(index % 4, index / 4, text, ping);
    }

    /**
     * Add a new entry to the element
     *
     * @param x    the x axis
     * @param y    the y axis
     * @param text the text to display on the slot
     * @param ping the ping to display
     */
    public void add(int x, int y, String text, int ping) {
        this.entries.add(new TabEntry(x, y, text, ping, SkinType.DARK_GRAY.getSkinData()));
    }

    /**
     * Add a new entry to the element
     *
     * @param index the index to get the axiss from
     * @param text     the text to display on the slot
     * @param ping     the ping to display
     * @param skinData the data to display in the skin slot
     */
    public void add(int index, String text, int ping, String[] skinData) {
        this.add(index % 4, index / 4, text, ping, skinData);
    }

    /**
     * Add a new entry to the element
     *
     * @param x        the x axis
     * @param y        the y axis
     * @param text     the text to display on the slot
     * @param ping     the ping to display
     * @param skinData the data to display in the skin slot
     */
    public void add(int x, int y, String text, int ping, String[] skinData) {
        this.entries.add(new TabEntry(x, y, text, ping, skinData));
    }

    /**
     * Add a new entry to the element
     *
     * @param index the index to get the axiss from
     * @param text     the text to display on the slot
     * @param ping     the ping to display
     * @param skinType the data to display in the skin slot
     */
    public void add(int index, String text, int ping, SkinType skinType) {
        this.add(index, text, ping, skinType.getSkinData());
    }

    /**
     * Add a new entry to the element
     *
     * @param x        the x axis
     * @param y        the y axis
     * @param text     the text to display on the slot
     * @param ping     the ping to display
     * @param skinType the data to display in the skin slot
     */
    public void add(int x, int y, String text, int ping, SkinType skinType) {
        this.add(x, y, text, ping, skinType.getSkinData());
    }
}
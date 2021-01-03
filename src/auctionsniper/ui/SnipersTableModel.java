package auctionsniper.ui;

import auctionsniper.Column;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel {
    private final static SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);
    private SniperSnapshot snapshot = STARTING_UP;
    private static final String STATUS_JOINING = "Joining";
    private static final String STATUS_BIDDING = "Bidding";
    private static final String STATUS_WINNING = "Winnning";
    private static final String STATUS_LOST = "Lost";
    private static final String STATUS_WON = "Won";
    private static String[] STATUS_TEXT = {STATUS_JOINING,
            STATUS_BIDDING,
            STATUS_WINNING,
            STATUS_LOST,
            STATUS_WON
    };

    public int getRowCount() {
        return 1;
    }

    public int getColumnCount() {
        return Column.values().length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshot);
    }

    public String getColumnName(int column) {
        return Column.at(column).name;
    }

    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }

    public void sniperStateChanged(SniperSnapshot newSnapshot) {
        snapshot = newSnapshot;
        fireTableRowsUpdated(0, 0);
    }
}

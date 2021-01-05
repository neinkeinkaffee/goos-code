package auctionsniper.ui;

import auctionsniper.Column;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.util.Defect;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SnipersTableModel extends AbstractTableModel {
    private final static SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);
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

    private List<SniperSnapshot> snapshots = new ArrayList<>();

    public int getRowCount() {
        return snapshots.size();
    }

    public int getColumnCount() {
        return Column.values().length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
    }

    public String getColumnName(int column) {
        return Column.at(column).name;
    }

    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }

    public void sniperStateChanged(SniperSnapshot newSnapshot) {
        int rowIndex = rowMatching(newSnapshot);
        snapshots.set(rowIndex, newSnapshot);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    private int rowMatching(SniperSnapshot snapshot) {
        for (int i = 0; i < snapshots.size(); i++) {
            if (snapshot.isForSameItemAs(snapshots.get(i))) {
                return i;
            }
        }
        throw new Defect("Cannot find match for " + snapshot);
    }

    public void addSniper(SniperSnapshot snapshot) {
        snapshots.add(snapshot);
        fireTableRowsInserted(0, 0);
    }
}

package auctionsniper.ui;

import auctionsniper.*;
import auctionsniper.util.Defect;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SnipersTableModel extends AbstractTableModel implements SniperCollector {
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
    private List<AuctionSniper> notToBeGcd = new ArrayList<AuctionSniper>();

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

    @Override
    public void addSniper(AuctionSniper sniper) {
        notToBeGcd.add(sniper);
        addSniperSnapshot(sniper.getSnapshot());
        sniper.addSniperListener(new SwingThreadSniperListener(this));
    }

    private void addSniperSnapshot(SniperSnapshot snapshot) {
        snapshots.add(snapshot);
        int row = snapshots.size() - 1;
        fireTableRowsInserted(row, row);
    }
}

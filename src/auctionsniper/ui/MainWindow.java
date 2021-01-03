package auctionsniper.ui;

import auctionsniper.SniperSnapshot;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    private static final String SNIPERS_TABLE_NAME = "Snipers Table";

    private final SnipersTableModel snipers = new SnipersTableModel();

    public MainWindow() {
        super("Auction Sniper");
        setName(MAIN_WINDOW_NAME);
        makeContentPane(makeSnipersTable());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void makeContentPane(JTable snipersTable) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable() {
        final JTable snipersTable = new JTable(snipers);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }

    public void sniperStateChanged(SniperSnapshot state) {
        snipers.sniperStatusChanged(state);
    }
}

package auctionsniper.ui;

import auctionsniper.util.Announcer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame {
    public static final String APPLICATION_TITLE = "Auction Sniper";
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String NEW_ITEM_ID_NAME = "New Item ID";
    public static final String JOIN_BUTTON_NAME = "Join Auction";
    private static final String SNIPERS_TABLE_NAME = "Snipers Table";

    private SnipersTableModel snipers;
    private final Announcer<UserRequestListener> userRequests = Announcer.to(UserRequestListener.class);

    public MainWindow(SnipersTableModel snipers) {
        super(APPLICATION_TITLE);
        this.snipers = snipers;
        setName(MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable(), makeControls());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JPanel makeControls() {
        JPanel controls = new JPanel(new FlowLayout());
        final JTextField itemIdField = new JTextField();
        itemIdField.setColumns(25);
        itemIdField.setName(NEW_ITEM_ID_NAME);
        controls.add(itemIdField);

        JButton joinButton = new JButton("Join Auction");
        joinButton.setName(JOIN_BUTTON_NAME);
        joinButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                userRequests.announce().joinAuction(itemIdField.getText());
            }
        });
        controls.add(joinButton);

        return controls;
    }

    private void fillContentPane(JTable snipersTable, JPanel controls) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(controls, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable() {
        final JTable snipersTable = new JTable(snipers);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }

    public void addUserRequestListener(UserRequestListener userRequestListener) {
        userRequests.addListener(userRequestListener);
    }
}

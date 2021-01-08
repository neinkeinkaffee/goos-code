package test.unit.auctionsniper;

import auctionsniper.*;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.util.Defect;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static auctionsniper.ui.SnipersTableModel.textFor;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(JMock.class)
public class SnipersTableModelTest {
    private final Mockery context = new Mockery();
    private TableModelListener listener = context.mock(TableModelListener.class);
    private Auction auction = context.mock(Auction.class);
    private final SnipersTableModel model = new SnipersTableModel();

    @Before
    public void attachModelListener() {
        model.addTableModelListener(listener);
    }

    @Test
    public void hasEnoughColumns() {
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }

    @Test
    public void setSniperValuesInColumns() {
        AuctionSniper joining = new AuctionSniper(auction, "item id");
        SniperSnapshot bidding = joining.getSnapshot().bidding(555, 666);
        context.checking(new Expectations() {{
            allowing(listener).tableChanged(with(anyInsertionEvent()));
            one(listener).tableChanged((with(aChangeInRow(0))));
        }});

        model.addSniper(joining);
        model.sniperStateChanged(bidding);

        assertRowMatchesSnapshot(bidding);
    }

    @Test
    public void setsUpColumnHeadings() {
        for (Column column: Column.values()) {
            assertEquals(column.name, model.getColumnName(column.ordinal()));
        }
    }

    @Test
    public void notifiesListenersWhenAddingSniper() {
        AuctionSniper joining = new AuctionSniper(auction, "item 123");
        context.checking(new Expectations() {{
            one(listener).tableChanged(with(anInsertionAtRow(0)));
        }});

        assertEquals(0, model.getRowCount());

        model.addSniper(joining);

        assertEquals(1, model.getRowCount());
        assertRowMatchesSnapshot(joining.getSnapshot());
    }

    @Test
    public void holdsSnipersInAdditionOrder() {
        context.checking(new Expectations() {{
            ignoring(listener);
        }});

        model.addSniper(new AuctionSniper(auction, "item 0"));
        model.addSniper(new AuctionSniper(auction, "item 1"));

        assertEquals("item 0", cellValue(0, Column.ITEM_IDENTIFIER));
        assertEquals("item 1", cellValue(1, Column.ITEM_IDENTIFIER));
    }

    @Test(expected=Defect.class)
    public void throwsDefectIfNoExistingSniperFoundForAnUpdate() {
        model.sniperStateChanged(new SniperSnapshot("item 1", 555, 666, SniperState.BIDDING));
    }

    private Matcher<TableModelEvent> anyInsertionEvent() {
        return hasProperty("type", equalTo(TableModelEvent.INSERT));
    }

    private Matcher<TableModelEvent> anInsertionAtRow(int rowIndex) {
        return samePropertyValuesAs(new TableModelEvent(model, rowIndex, rowIndex, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    private Matcher<TableModelEvent> aChangeInRow(int rowIndex) {
        return samePropertyValuesAs(new TableModelEvent(model, rowIndex, rowIndex, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
    }

    private Object cellValue(int rowIndex, Column column) {
        int columnIndex = column.ordinal();
        return model.getValueAt(rowIndex, columnIndex);
    }

    private void assertRowMatchesSnapshot(SniperSnapshot snapshot) {
        assertColumnEquals(Column.ITEM_IDENTIFIER, snapshot.itemId);
        assertColumnEquals(Column.LAST_PRICE, snapshot.lastPrice);
        assertColumnEquals(Column.LAST_BID, snapshot.lastBid);
        assertColumnEquals(Column.SNIPER_STATE, textFor(snapshot.state));
    }

    private void assertColumnEquals(Column column, Object expected) {
        int rowIndex = 0;
        assertEquals(expected, cellValue(rowIndex, column));
    }
}

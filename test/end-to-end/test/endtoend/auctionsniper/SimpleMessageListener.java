package test.endtoend.auctionsniper;

import org.hamcrest.Matcher;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;

public class SimpleMessageListener implements MessageListener {
    private ArrayBlockingQueue<Message> messages = new ArrayBlockingQueue<>(1);

    public void processMessage(Chat chat, Message message) {
        messages.add(message);
    }

    public void receivesAMessage(Matcher<? super String> messageMatcher) throws InterruptedException {
        Message message = messages.poll(5, TimeUnit.SECONDS);
        assertThat("Message", message, hasProperty("body", messageMatcher));
        assertThat(message.getBody(), messageMatcher);
    }
}

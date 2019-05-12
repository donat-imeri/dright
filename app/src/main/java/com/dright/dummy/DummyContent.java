package com.dright.dummy;

import android.util.Log;
import android.widget.ImageView;

import com.dright.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        String item1Content="A dilema is a question, a challenge or a problem that you have to solve. " +
                "You have to choose a way, method or follow a path to do this. To do this you need " +
                "knowledge about the different options you have or knowledge to create these options " +
                "and/or experience from your previous dilemas os problems that you had before. ";
        DummyItem item1=new DummyItem("1", "What is a dilema? " , item1Content, R.drawable.logo);
        addItem(item1);

        String item2Content="To post a dilema you have to go to \"Post a dilema\" tab and do the following: \n" +
                " - Type your dilema description. Make sure to describe clearly what is your problem or what " +
                "kind of help do you need. \n" +
                " - You can add options to your dilema or just let others propose them for you. You can " +
                "choose between image options or just text options\n" +
                " - Pick a priority. Choosing a high priority will appear your dilema before others on" +
                " users list. You will get more responses and faster ones. But you have to spend docents on this.\n" +
                " - Choose the timeout. The timeout is the time that your post will be appearing on other " +
                "users feed. You won't get the final results till this time has passed. \n" +
                " - Post as an anonymous person. We value your privacy so by choosing this option other " +
                "users won't see who posted the dilema. But checking this option will result to your " +
                "dilema not appearing first on your followers list, unless you have prioritised it. ";
        DummyItem item2=new DummyItem("2", "How to post a dilema? " , item2Content, R.drawable.logo);
        addItem(item2);

    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DummyItem createDummyItem(int position) {
        return new DummyItem(String.valueOf(position), "Item " + position, makeDetails(position), 0);
    }

    private static String makeDetails(int position) {

        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 1; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;
        public final int imageView;

        public DummyItem(String id, String content, String details, int imageView) {
            this.id = id;
            this.content = content;
            this.details = details;
            this.imageView = imageView;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}

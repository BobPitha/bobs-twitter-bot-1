package net.pitha.twit;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

        BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);
        BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>(1000);
        Authentication auth = new OAuth1(
                "oCH5M4RnKQ1mjodPCJisYs9UD",
                "RVjLqOkBPmN7qsJBMaKLzk8VUj0dvuN6V1NuTKH8NktnCzgkwg",
                "385664669-Ai7gvIXV3GFrp51cAb68arMi4UUCHMHDQwQRg9zn",
                "XgUHrLODOcpC5SDyazWqCke5A5gs3Rzl2iV8zhE8OXy4W");
        List<Long> followings = Lists.newArrayList(1234L, 566788L);
        List<String> terms = Lists.newArrayList("twitter","api","#Trump","#BlackLivesMatter");
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
        hosebirdEndpoint.trackTerms(terms);

        ClientBuilder builder = new ClientBuilder()
                .name("bobs-twitter-bot-1")
                .hosts(new HttpHosts(Constants.STREAM_HOST))
                .authentication(auth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(queue))
                .eventMessageQueue(eventQueue);
        Client hbClient = builder.build();
        hbClient.connect();
        try {
            while (!hbClient.isDone()) {
                String message = queue.take();
                System.out.println(message);
            }
        }
        catch (InterruptedException iex) {
            System.out.println("**** Exception: " + iex.toString());
        }
    }
}

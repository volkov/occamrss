package svolkov;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class FeedDownloader {

	private static final Logger LOG = Logger.getLogger(FeedDownloader.class);

	private int threads = 10;

	private int maxDownloadTime = 1000;

	private List<URL> feeds = new ArrayList<URL>();

	public FeedDownloader() {
	}

	public FeedDownloader(String fileName) throws MalformedURLException,
			IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(this
				.getClass().getClassLoader()
				.getResourceAsStream(fileName)));
		String line;
		while ((line = reader.readLine()) != null) {
			feeds.add(new URL(line));
		}
		LOG.info("Loaded feeds from " + fileName);
	}

	public List<SyndEntry> getEntries() {
		final List<SyndEntry> result = Collections
				.synchronizedList(new ArrayList<SyndEntry>());
		ExecutorService threadPool = Executors.newFixedThreadPool(getThreads());
		for (URL feed : getFeeds()) {
			final URL finalFeed = feed;
			threadPool.submit(new Runnable() {

				@Override
				public void run() {
					SyndFeedInput input = new SyndFeedInput();
					SyndFeed feed;
					try {
						feed = input.build(new XmlReader(finalFeed));
						for (Object o : feed.getEntries()) {
							SyndEntry entry = (SyndEntry) o;
							result.add(entry);
						}
						LOG.info("Finished adding " + feed.getEntries().size()
								+ " feed from " + finalFeed);
					} catch (Exception e) {
						LOG.error("Problem loading feed " + finalFeed, e);
					}

				}
			});
		}
		threadPool.shutdown();
		try {
			threadPool.awaitTermination(getMaxDownloadTime(), TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			LOG.error("Problem while waiting termination", e);
		}
		LOG.info("Done downloading");
		return result;
	}

	public static void main(String[] args) throws MalformedURLException {
		FeedDownloader downloader = new FeedDownloader();
		downloader.getFeeds().add(new URL("http://lenta.ru/rss"));
		downloader.getFeeds().add(
				new URL("http://www.gazeta.ru/export/rss/first.xml"));
		for (SyndEntry entry : downloader.getEntries()) {
			System.out.println(entry.getTitle());
		}
		System.out.println("done");

	}

	/**
	 * @param feeds
	 *            the feeds to set
	 */
	public void setFeeds(List<URL> feeds) {
		this.feeds = feeds;
	}

	/**
	 * @return the feeds
	 */
	public List<URL> getFeeds() {
		return feeds;
	}

	/**
	 * @param threads
	 *            the threads to set
	 */
	public void setThreads(int threads) {
		this.threads = threads;
	}

	/**
	 * @return the threads
	 */
	public int getThreads() {
		return threads;
	}

	/**
	 * @param maxDownloadTime
	 *            the maxDownloadTime to set
	 */
	public void setMaxDownloadTime(int maxDownloadTime) {
		this.maxDownloadTime = maxDownloadTime;
	}

	/**
	 * @return the maxDownloadTime
	 */
	public int getMaxDownloadTime() {
		return maxDownloadTime;
	}

}

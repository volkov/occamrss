package svolkov;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;

public class FeedProvider {

	private static final Logger LOG = Logger.getLogger(FeedProvider.class);

	private class UpdateDaemon extends Thread {

		public void run() {
			LOG.info("Start update daemon");
			while (true) {
				long timeToUpdate = getTimeToUpdate();
				LOG.info("TTU:" + timeToUpdate);
				if (getTimeToUpdate() <= 0) {
					LOG.info("Trying shedule update...");
					if (update()) {
						LOG.info("Shedule update finished.");
					}
				}
				try {
					Thread.sleep(getTimeToUpdate());
				} catch (InterruptedException e) {
					LOG.error("Error while sleep " + e);
				}
			}
		}

		private long getTimeToUpdate() {
			long timeToUpdate = getLastUpdate().getTime()
					- System.currentTimeMillis() + maxDelay * 1000;
			if (timeToUpdate < 0) {
				return 0;
			}
			return timeToUpdate;
		}

	}

	private Date lastUpdate = new Date(0);

	private int minDelay;

	private int maxDelay;

	private FeedStorage feedStorage;

	private FeedDownloader feedDownloader;

	public void init() {
		if (maxDelay > 0) {
			UpdateDaemon daemon = new UpdateDaemon();
			daemon.setDaemon(true);
			daemon.start();
		}
	}

	public List<SyndEntry> getEntries() {
		update();
		return feedStorage.getEntries();
	}

	public SyndFeed getFeed() {
		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType("rss_2.0");
		feed.setTitle("Occam RSS aggregator");
		feed.setDescription("Last Update:" + getLastUpdate());
		feed.setLink("http://localhost/");
		feed.setEntries(getEntries());
		return feed;
	}

	private boolean update() {
		if (hasToUpdate()) {
			synchronized (feedStorage) {
				if (hasToUpdate()) {
					feedStorage.addEntries(feedDownloader.getEntries());
					setLastUpdate(new Date());
					return true;
				}
				return false;
			}
		}
		return false;
	}

	private boolean hasToUpdate() {
		return System.currentTimeMillis() - getLastUpdate().getTime() > minDelay * 1000;
	}

	/**
	 * @param feedDownloader
	 *            the feedDownloader to set
	 */
	public void setFeedDownloader(FeedDownloader feedDownloader) {
		this.feedDownloader = feedDownloader;
	}

	/**
	 * @return the feedDownloader
	 */
	public FeedDownloader getFeedDownloader() {
		return feedDownloader;
	}

	/**
	 * @param minDelay
	 *            the minDelay to set
	 */
	public void setMinDelay(int minDelay) {
		this.minDelay = minDelay;
	}

	/**
	 * @return the minDelay
	 */
	public int getMinDelay() {
		return minDelay;
	}

	/**
	 * @param feedStorage
	 *            the feedStorage to set
	 */
	public void setFeedStorage(FeedStorage feedStorage) {
		this.feedStorage = feedStorage;
	}

	/**
	 * @return the feedStorage
	 */
	public FeedStorage getFeedStorage() {
		return feedStorage;
	}

	/**
	 * @param maxDelay
	 *            the maxDelay to set
	 */
	public void setMaxDelay(int maxDelay) {
		this.maxDelay = maxDelay;
	}

	/**
	 * @return the maxDelay
	 */
	public int getMaxDelay() {
		return maxDelay;
	}

	/**
	 * @param lastUpdate the lastUpdate to set
	 */
	private void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * @return the lastUpdate
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}

}

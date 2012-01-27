package svolkov;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;

/**
 * Class for managing feed entries. 
 * @author Sergey Volkov
 *
 */
public class FeedProvider {

	private static final Logger LOG = Logger.getLogger(FeedProvider.class);

	/**
	 * Thread for update entries every maxDelay.
	 * @author Sergey Volkov
	 *
	 */
	private class UpdateDaemon extends Thread {
		
		@Override
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

		/**
		 * Returns milliseconds before maxDelay exceeds.
		 * @return
		 */
		private long getTimeToUpdate() {
			long timeToUpdate = getLastUpdate().getTime()
					- System.currentTimeMillis() + maxDelay * 1000;
			if (timeToUpdate < 0) {
				return 0;
			}
			return timeToUpdate;
		}

	}
	
	/**
	 * Stores time when entries were updated last time.
	 */
	private Date lastUpdate = new Date(0);

	/**
	 * Minimum delay between entries updates.
	 */
	private int minDelay;

	/**
	 * Maximum delay between entries updates.
	 */
	private int maxDelay;

	/**
	 * Stores and manage entries.
	 */
	private FeedStorage feedStorage;

	/**
	 * Downloads entries.
	 */
	private FeedDownloader feedDownloader;

	/**
	 * Inits UpdateDaemon if neseccary.
	 */
	public void init() {
		if (maxDelay > 0) {
			UpdateDaemon daemon = new UpdateDaemon();
			daemon.setDaemon(true);
			daemon.start();
		}
	}

	/**
	 * @return updated entry list.
	 */
	public List<SyndEntry> getEntries() {
		update();
		return feedStorage.getEntries();
	}

	/**
	 * @return feed from entry list.
	 */
	public SyndFeed getFeed() {
		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType("rss_2.0");
		feed.setTitle("Occam RSS aggregator");
		feed.setDescription("Last Update:" + getLastUpdate());
		feed.setLink("http://localhost/");
		feed.setEntries(getEntries());
		return feed;
	}
	
	/**
	 * Downloads and adds new entries if minDelay passed.
	 * @return
	 */
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
	
	/**
	 * Checks if minDelay passed.
	 * @return 
	 */
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

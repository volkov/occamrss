package svolkov;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.sun.syndication.feed.synd.SyndEntry;

public class FeedStorage {

	private static final Logger LOG = Logger.getLogger(FeedStorage.class);

	private int size = 100;

	private Map<String, SyndEntry> entryMap = new HashMap<String, SyndEntry>();

	private SortedSet<SyndEntry> sortedEntries = new TreeSet<SyndEntry>();

	public synchronized List<SyndEntry> addEntries(Collection<SyndEntry> entries) {
		LOG.info("Got " + entries.size() + " to update");
		List<SyndEntry> newEntries = new ArrayList<SyndEntry>();
		for (SyndEntry entry : entries) {
			if (!entryMap.containsKey(entry.getLink())) {
				newEntries.add(entry);
				entryMap.put(entry.getLink(), entry);
			}
		}
		LOG.info("Found " + newEntries.size() + " new entries");
		SortedSet<SyndEntry> newSortedEntries = new TreeSet<SyndEntry>(
				new Comparator<SyndEntry>() {
					@Override
					public int compare(SyndEntry o1, SyndEntry o2) {
						return -o1.getPublishedDate().compareTo(
								o2.getPublishedDate());
					}
				});
		newSortedEntries.addAll(entryMap.values());
		newSortedEntries.addAll(newEntries);

		int removed = 0;
		while (newSortedEntries.size() > getSize()) {
			SyndEntry lastEntry = newSortedEntries.last();
			newSortedEntries.remove(lastEntry);
			entryMap.remove(lastEntry);
			removed++;
		}
		LOG.info("Removed " + removed + " old entries");

		sortedEntries = newSortedEntries;
		return newEntries;
	}

	public List<SyndEntry> getEntries() {
		List<SyndEntry> result = new ArrayList<SyndEntry>();
		result.addAll(sortedEntries);
		return result;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

}

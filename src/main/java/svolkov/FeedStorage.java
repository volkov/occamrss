package svolkov;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.sun.syndication.feed.synd.SyndEntry;

/**
 * Thread safe inmemory storage for feed entries.
 * 
 * @author Sergey Volkov
 * 
 */
public class FeedStorage {

	private class EntryComparator implements Comparator<SyndEntry> {
		@Override
		public int compare(SyndEntry o1, SyndEntry o2) {
			return -o1.getPublishedDate().compareTo(o2.getPublishedDate());
		}
	}

	private static final Logger LOG = Logger.getLogger(FeedStorage.class);

	/**
	 * Storage size.
	 */
	private int size = 100;

	/**
	 * Map connects entry url with entry.
	 */
	private Map<String, SyndEntry> entryMap = new HashMap<String, SyndEntry>();

	/**
	 * Sorted entries.
	 */
	private SortedSet<SyndEntry> sortedEntries = new TreeSet<SyndEntry>();
	
	private EntryComparator comparator = new EntryComparator();
	/**
	 * Adds new entries to entryMap and sortedEntries. Removes old entries if
	 * necessary.
	 * 
	 * @param entries
	 * @return - list of really added entries.
	 */
	public synchronized Set<SyndEntry> addEntries(Collection<SyndEntry> entries) {
		LOG.info("Got " + entries.size() + " to update");
		Set<SyndEntry> newEntries = new TreeSet<SyndEntry>(comparator);
		for (SyndEntry entry : entries) {
			if (!entryMap.containsKey(entry.getLink())) {
				newEntries.add(entry);
				entryMap.put(entry.getLink(), entry);
			}
		}
		LOG.info("Found " + newEntries.size() + " new entries");
		SortedSet<SyndEntry> newSortedEntries = new TreeSet<SyndEntry>(comparator);
		newSortedEntries.addAll(entryMap.values());

		int removed = 0;
		while (newSortedEntries.size() > getSize()) {
			SyndEntry lastEntry = newSortedEntries.last();
			newSortedEntries.remove(lastEntry);
			entryMap.remove(lastEntry.getLink());
			removed++;
		}
		LOG.info("Removed " + removed + " old entries");

		sortedEntries = newSortedEntries;
		return newEntries;
	}

	/**
	 * 
	 * @return sorted list of entries.
	 */
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

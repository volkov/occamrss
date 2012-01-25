package svolkov;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.HttpRequestHandler;

import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

public class RssRequestHandler implements HttpRequestHandler {

	private static final Logger LOG = Logger.getLogger(RssRequestHandler.class);

	private FeedProvider feedProvider;

	@Override
	public void handleRequest(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		res.setCharacterEncoding("UTF-8");
		res.setContentType("application/rss+xml");
		Writer writer = res.getWriter();
		SyndFeedOutput output = new SyndFeedOutput();
		try {
			output.output(getFeedProvider().getFeed(), writer);
		} catch (FeedException e) {
			LOG.error("Ooops!", e);
		}
		writer.close();
	}

	/**
	 * @param feedProvider the feedProvider to set
	 */
	public void setFeedProvider(FeedProvider feedProvider) {
		this.feedProvider = feedProvider;
	}

	/**
	 * @return the feedProvider
	 */
	public FeedProvider getFeedProvider() {
		return feedProvider;
	}

}

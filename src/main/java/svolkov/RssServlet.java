package svolkov;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

public class RssServlet extends HttpServlet {
	
	private static final Logger LOG = Logger.getLogger(RssServlet.class);

	private FeedProvider provider = FeedFactory.getFeedProvider();
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		res.setCharacterEncoding("UTF-8");
		res.setContentType("application/rss+xml");
		Writer writer = res.getWriter();
		SyndFeedOutput output = new SyndFeedOutput();
        try {
			output.output(provider.getFeed(),writer);
		} catch (FeedException e) {
			LOG.error("Ooops!", e);
		}
		writer.close();
	}
}

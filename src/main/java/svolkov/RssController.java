package svolkov;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class RssController implements Controller {
	
	private FeedProvider feedProvider;
	
	@Override
	public ModelAndView handleRequest(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		// TODO Auto-generated method stub
		ModelAndView result = new ModelAndView("rssView.jsp");
		result.addObject("entryList", feedProvider.getEntries());
		return result;
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

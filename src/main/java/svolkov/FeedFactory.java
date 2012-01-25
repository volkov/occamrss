package svolkov;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;

public class FeedFactory {

	private static final String CONTEXT_FILE = "applicationContext.xml";

	private static final Logger LOG = Logger.getLogger(FeedFactory.class);

	private static FeedProvider feedProvider;

	static {
		final GenericApplicationContext context = new GenericApplicationContext();
		XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(context);
		xmlReader.loadBeanDefinitions(new ClassPathResource(CONTEXT_FILE));
		context.refresh();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				context.close();
				LOG.info("Context is closed");
			}
		});
		feedProvider = (FeedProvider) context.getBean("FeedProvider");

	}
	
	public static FeedProvider getFeedProvider(){
		return feedProvider;
	}

}

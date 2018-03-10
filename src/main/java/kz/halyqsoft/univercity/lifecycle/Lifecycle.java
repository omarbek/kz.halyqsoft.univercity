package kz.halyqsoft.univercity.lifecycle;

import org.r3a.common.resources.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Author Rakymzhan A. Kenzhegul
 * Created Oct 28, 2015 3:02:00 PM
 */
public final class Lifecycle {

	private volatile boolean started = false;
	private List<Locale> locales;

	private Lifecycle() {
	}
	
	public static Lifecycle getInstance() {
        return LifecycleHolder.INSTANCE;
    }
	
	public void start() throws Exception {
		if (!isStarted()) {
			locales = new ArrayList<>(3);
	        locales.add(new Locale("kk", "KZ"));
	        locales.add(new Locale("ru", "RU"));
	        locales.add(new Locale("en", "US"));

			Resource.INSTANCE.loadApplicationResources("kz.halyqsoft.univercity.resourses.messages", getClass().getClassLoader(), locales);

//			DBLink.INSTANCE.open("UNIVERCITY-PU");
			
			started = true;
		}
	}

	public void shutdown() {
		if (isStarted()) {
//			DBLink.INSTANCE.close();
			started = false;
		}
	}
	
	public boolean isStarted() {
		return started;
	}

	public List<Locale> getAvailableLocales() {
		return locales;
	}
	
	private static class LifecycleHolder {

        private static final Lifecycle INSTANCE = new Lifecycle();
    }
}

package kz.halyqsoft.univercity;

import com.vaadin.annotations.VaadinServletConfiguration;
import org.r3a.common.vaadin.servlet.AbstractCommonServlet;

import javax.servlet.annotation.WebServlet;

/**
 * Author: Rakymzhan A. Kenzhegul
 * Created: 09.03.2018 22:52
 */
@WebServlet(urlPatterns = "/*", name = "UnivercityServlet", asyncSupported = true)
@VaadinServletConfiguration(ui = UnivercityUI.class, productionMode = false)
public class UnivercityServlet extends AbstractCommonServlet {
}

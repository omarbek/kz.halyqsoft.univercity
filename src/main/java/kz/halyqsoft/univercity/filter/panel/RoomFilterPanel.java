package kz.halyqsoft.univercity.filter.panel;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.filter.FRoomFilter;
import org.r3a.common.vaadin.widget.filter2.panel.AbstractFilterPanel;

/**
 * @author Omarbek
 * @created Apr 10, 2017 3:19:50 PM
 */
@SuppressWarnings("serial")
public final class RoomFilterPanel extends AbstractFilterPanel {

	public RoomFilterPanel(FRoomFilter filterBean) {
		super(filterBean);
	}

	@Override
	protected void initWidget() throws Exception {
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSpacing(true);
		hl.setWidthUndefined();
		
		AbstractField af = getFilterComponent("corpus");
		if (af != null) {
			hl.addComponent(af);
		}
		
		af = getFilterComponent("roomType");
		if (af != null) {
			hl.addComponent(af);
		}
		
		af = getFilterComponent("roomNo");
		if (af != null) {
			hl.addComponent(af);
		}
		
		getContent().addComponent(hl);
		getContent().setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
	}
}

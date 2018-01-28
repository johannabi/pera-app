package de.uni_koeln.dh.pera.gui.misc;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LayoutHelper {
		
		private static Logger logger = LoggerFactory.getLogger(LayoutHelper.class);
	
	public static GridLayout getGridLayout() {
		GridLayout layout = getGridLayout(false);
		return layout;
	}	
		
	public static GridLayout getNormalizedLayout() {
		GridLayout layout = getGridLayout(true);
		return layout;
	}
	
	private static GridLayout getGridLayout(boolean reset) {
		GridLayout layout = new GridLayout();	// == new GridLayout(1, true);
		
		if (reset) {
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			layout.verticalSpacing = 0;
		}
		
		logger.info("GridLayout (marginHeight, marginWidth, verticalSpacing): "
				+ layout.marginHeight + ", " + layout.marginWidth + ", " 
				+ layout.verticalSpacing);
		return layout;
	}
	
	public static RowLayout getRowLayout(int spacing) {
		RowLayout layout = new RowLayout();
		layout.spacing = spacing;
		
		logger.info("RowLayout (spacing): " + spacing);
		return layout;
	}
	
	public static GridData getGridData(int width) {
		GridData data = new GridData();
		data.widthHint = width;
		
		logger.info("GridData (width): " + width);
		return data;
	}
	
	public static GridData getGridData(int width, int height) {
		return getGridData(width, height, false, false);
	}
	
	public static GridData getGridData(int width, int height, 
				boolean vCenter, boolean hCenter) {
		GridData data = getAlignment(
				new GridData(width, height), 
				vCenter, hCenter);
		
		return data;
	}
	
	public static GridData getCenteredData() {
		GridData data = getAlignment(
				new GridData(), 
				true, true);
		
		return data;
	}
	
	private static GridData getAlignment(GridData data, boolean vCenter, boolean hCenter) {
		if (vCenter) {
			data.verticalAlignment = SWT.CENTER;
			data.grabExcessVerticalSpace = true;
		}
		
		if (hCenter) {
			data.horizontalAlignment = SWT.CENTER;
			data.grabExcessHorizontalSpace = true;
		}
		
		logger.info("GridData (width / height; vCenter / hCenter): " 
				+ data.widthHint + " / " + data.heightHint + "; " 
				+ vCenter + " / " + hCenter);
		return data;
	}
	
	public static void setMargin(StyledText text, int margin) {
		setMargin(text, margin, 0);
	}
	
	public static void setMargin(StyledText text, int leftRightMargin, int topBottomMargin) {
		text.setMargins(leftRightMargin, topBottomMargin, leftRightMargin, topBottomMargin);
		
		logger.info("Margin (left / top / right / bottom): " 
				+ leftRightMargin + " / " + topBottomMargin + " / " 
				+ leftRightMargin + " / " + topBottomMargin);
	}
	
}

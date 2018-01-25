package de.uni_koeln.dh.pera.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_koeln.dh.pera.gui.misc.LayoutHelper;
import de.uni_koeln.dh.pera.util.Calc;

// main (ui) thread
public class View {
	
		private Logger logger = LoggerFactory.getLogger(getClass());
	
		// TODO if fitting set to final
		private static int H_HMONITOR_PCT = 90, 			// "height: 90% of the height of the monitor"
				W_HEIGHT_PCT = /*75*/80;					// "width: 80% of the height (of the app)"
	
		private Display display = null;
		private Shell shell = null;		
		private Rectangle monitorBounds = null;
		
		private String title = null;
		private boolean initialized = false;
		
	public View(String title) {
		this.display = new Display();
		this.title = title;		
		
		Monitor monitor = display.getPrimaryMonitor();
		this.monitorBounds = monitor.getBounds();
	}
	
	public void init() {		
		logger.info("Initialize shell ('" + title + "')...");
		
		shell = new Shell(display, getStyle());
		shell.setSize(getSizeByHeight(H_HMONITOR_PCT));		
		shell.setLocation(getCenter());		
		shell.setLayout(LayoutHelper.getNormalizedLayout());
//		shell.setBackground(display.getSystemColor(SWT.COLOR_RED));	
		shell.setText(title);

		initialized = true;
	}
	
	public void loadComponents() {
		ClientWrapper client = new ClientWrapper(shell);
		client.wrap();
	}
	
	public void show() {
		logger.info("Open shell");
		shell.open();
		
		// if needed add processing thread here
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) 
				display.sleep();
		}
	}
	
	public void dispose() {
		if (!display.isDisposed())
			display.dispose();
	}
	
	// style
	private int getStyle() {
		// default: SHELL_TRIM = CLOSE | TITLE | MIN | MAX | RESIZE
		// TODO disable macOS-fullscreen?
		return SWT.CLOSE | SWT.TITLE | SWT.MIN;
	}
	
	// size
	private Point getSizeByHeight(int percentage) {
		final int monitorHeight = monitorBounds.height;
		
		float heightF = Calc.getValByPct(monitorHeight, percentage);	
		int width = (int) Calc.getValByPct(heightF, W_HEIGHT_PCT),
				height = (int)heightF;
		
		logger.info("Size (x / y): " + width + " / " + height);
		return new Point(width, height);
	}
	
	// location
	private Point getCenter() {
		// shell.getBounds() == outer area (!= client area)
		Rectangle bounds = shell.getBounds();	 
		
		int x = (monitorBounds.width - bounds.width) / 2, 
				y = (monitorBounds.height - bounds.height) / 2;
		
		logger.info("Location (x / y): " + x + " / " + y);
		return new Point(x, y);
	}	

	public boolean isInitialized() {
		if (!initialized)
			logger.error("No shell initialized.");
		
		return initialized;
	}

}

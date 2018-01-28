package de.uni_koeln.dh.pera.gui.core.img;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.geotools.map.Layer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;

import de.uni_koeln.dh.pera.data.Player;
import de.uni_koeln.dh.pera.data.story.Node;
import de.uni_koeln.dh.pera.gui.core.BaseComposite;
import de.uni_koeln.dh.pera.gui.misc.LayoutHelper;
import de.uni_koeln.dh.pera.util.Calc;

public class ImgComposite extends BaseComposite {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private Map map = null;
	private Label header = null;

	private Button cityButton, routeButton, politButton;

	private Coordinate position = null;

	// private SelectionListener zoomOutSelection = new SelectionListener() {
	// public void widgetSelected(SelectionEvent e) {
	// System.out.println("zoomOut");
	// }
	//
	// public void widgetDefaultSelected(SelectionEvent e) {
	// }
	// };

	// private SelectionListener zoomInSelection = new SelectionListener() {
	// public void widgetSelected(SelectionEvent e) {
	// System.out.println("zoomIn");
	// }
	//
	// public void widgetDefaultSelected(SelectionEvent e) {
	// }
	// };

	// TODO city layer
	private SelectionListener placesSelection = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			boolean selected = ((Button) e.widget).getSelection();
			System.out.println("places: " + selected);
			map.changeVisibility(selected, 2); // layer[2] = orte
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};

	private SelectionListener routeSelection = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			boolean selected = ((Button) e.widget).getSelection();
			System.out.println("route: " + selected);
			map.changeVisibility(selected, 1); // layer[1] = routen
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};

	private Shell mapLegend;

	private SelectionListener territorySelection = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			boolean selected = ((Button) e.widget).getSelection();
			// System.out.println("polit.: " + selected);
			mapLegend.setVisible(selected);
			for (int i = 3; i <= 11; i++) { //layer[3] - layer[11] = hoheitsgebiete
				map.changeVisibility(selected, i);
			}
			// for (Layer layer : map.getPolitLayers())
			// layer.setVisible(selected);
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};

	public ImgComposite(Composite parent) {
		super(parent);
	}

	public ImgComposite(Composite parent, List<Node> nodes, Player player) {
		super(parent, nodes, player);
	}

	public void init(int height) {
		logger.info("Initialize image composite...");
		super.init(height);

		// http://al-teef.info/wp-content/uploads/2017/12/texture-seamless-seamless-city-textures-door-texture-65-best-wood-texture-images-on-pinterest-wood-texture-and-wood.jpg
		setBackgroundImage(new Image(display, "src/main/resources/img/wood.jpg"));
		setInitialized(true);
	}

	public void addMapComponents() {
		logger.info("Add map components...");

		mapLegend = new Shell(display, SWT.TITLE | SWT.MIN);
		mapLegend.setText("Child Shell");
		mapLegend.setSize(200, 200);
		mapLegend.setVisible(false);
		//TODO enable closing

		int height = getHeight();
		int mapHeight = (int) Calc.getValByPct(height, Map.H_HIMGCOMP_PCT);
		int headHeight = (height - mapHeight) / 2;

		setHeader(headHeight);

		map = new Map(this);
		map.init(mapHeight);
		position = new Coordinate(Double.parseDouble(nodes.get(currentID).getLatitude()),
				Double.parseDouble(nodes.get(currentID).getLongitude()));

		map.updatePosition(position);

		setControls();
	}

	private void setHeader(int headHeight) {
		logger.info("Initialize header...");

		header = new Label(this, SWT.CENTER);
		header.setLayoutData(/* LayoutHelper.getCenteredData() */LayoutHelper
				.getAlignment(LayoutHelper.getGridData(parentWidth), true, true));
		header.setForeground(getDefaultFgColor());
		header.setFont(getHeaderFont(headHeight));
		header.setText(getChapter());

	}

	private void setControls() {
		logger.info("Initialize controls...");

		Composite ctrlComp = new Composite(this, SWT.NONE);
		ctrlComp.setLayout(getCtrlLayout());
		ctrlComp.setLayoutData(LayoutHelper.getCenteredData());

		// Button zoomOutButton = new Button(ctrlComp, SWT.PUSH);
		// zoomOutButton.setText("-");
		// zoomOutButton.addSelectionListener(zoomOutSelection);

		// Button zoomInButton = new Button(ctrlComp, SWT.PUSH);
		// zoomInButton.setText("+");
		// zoomInButton.addSelectionListener(zoomInSelection);

		cityButton = new Button(ctrlComp, SWT.TOGGLE);
		cityButton.setText("\u25bc");
		cityButton.setToolTipText("Orte");
		cityButton.addSelectionListener(placesSelection);
		
		routeButton = new Button(ctrlComp, SWT.TOGGLE);
		routeButton.setText("⚓︎");
		routeButton.setSelection(true);
		routeButton.setToolTipText("Reiseroute");
		routeButton.addSelectionListener(routeSelection);
		
		politButton = new Button(ctrlComp, SWT.TOGGLE);
		politButton.setText("♕");
		politButton.setToolTipText("Hoheitsgebiete");
		politButton.addSelectionListener(territorySelection);

	}

	private Font getHeaderFont(int headHeight) {
		Font font = getFont("Palatino Linotype", (headHeight / 3), SWT.BOLD);
		return font;
	}

	private Layout getCtrlLayout() {
		RowLayout layout = LayoutHelper.getRowLayout(parentWidth / 8);
		return layout;
	}

	protected int getWidth() {
		return parentWidth;
	}

	public int getInnerWidth() {
		return map.getWidth();
	}

	public void updateAll() {
		header.setText(getChapter());

		Coordinate newCoordinate = new Coordinate(Double.parseDouble(nodes.get(currentID).getLatitude()),
				Double.parseDouble(nodes.get(currentID).getLongitude()));

		if (!newCoordinate.equals(position)) { // if position changed
			position = newCoordinate;
			map.updatePosition(position);
		}

	}

	private String getChapter() {
		Node node = super.nodes.get(super.currentID);
		return node.getChapter();
	}

}

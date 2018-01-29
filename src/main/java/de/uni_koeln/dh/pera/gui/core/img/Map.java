package de.uni_koeln.dh.pera.gui.core.img;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.data.DataUtilities;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.Hints;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.ChannelSelection;
import org.geotools.styling.ContrastEnhancement;
import org.geotools.styling.Font;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.SelectedChannelType;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.swt.SwtMapPane;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.ContrastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import de.uni_koeln.dh.pera.gui.misc.LayoutHelper;
import de.uni_koeln.dh.pera.util.Calc;

public class Map extends Composite {

	//////////////// ATTRIBUTES/////////////////////////////
	private Logger logger = LoggerFactory.getLogger(getClass());

	protected static final int H_HIMGCOMP_PCT = 75;
	private static final int W_WIMGCOMP_PCT = /* 80 */77;

	private static Color BLACK_AWT = Color.BLACK,
			DARK_RED_AWT = new Color(202, 30, 0),
			CURRENT_POSITION = Color.YELLOW, //TODO STEFAN color star
			LAST_POSITION = Color.RED;
	
	private int STAR_PROPORTION = 40; //TODO STEFAN size star

	private ImgComposite parent = null;

	private GridLayout layout = null;

	/** number of layers (exclusive the layers of player location */
	private int layersWithoutLocation = 0;

	private SwtMapPane mPane = null;
	private Shell legendShell;

	private int width = 0, height = 0;

	private List<Layer> territoryLayers;
	private java.util.Map<String, Integer[]> territoriesMap;
	private Layer routeLayer;
	private Layer placesLayer;

	/// LISTENERS////////////////////
	private Listener labelListener = new Listener() {
		public void handleEvent(Event e) {
			String labelText = ((Label) e.widget).getText();

			for (Layer layer : territoryLayers) {
				if (!layer.getTitle().equals(labelText))
					layer.setVisible(false);
				else
					layer.setVisible(true);
			}
		}
	};

	// TODO fix delay?
	private Listener shellListener = new Listener() {
		public void handleEvent(Event e) {
			setLayerVisibilities(true, territoryLayers, null);
		}
	};

	//////////////////// CONSTRUCTORS////////////////////////
	public Map(ImgComposite parent) {
		super(parent, SWT.NONE);
		this.parent = parent;
	}

	///////////////// METHODS//////////////////////////////
	public void init(int height) {
		this.height = height;

		configComposite();

		// TODO kapseln (stefan)
		setTerritories();
		configMapPane();
		setLegend();

	}

	public int getWidth() {
		return width;
	}
	
	public Layer getRouteLayer() {
		return routeLayer;
	}

	public Layer getPlacesLayer() {
		return placesLayer;
	}

	public void changeVisibility(boolean selected, int layer) {
		Layer currentLayer = mPane.getMapContent().layers().get(layer);

		currentLayer.setVisible(selected);

	}

	public void updatePosition(Coordinate coordinate) {

		Layer newLayer = LayerCreator.createPositionLayer(coordinate, width, 
				CURRENT_POSITION, STAR_PROPORTION);

		int currentSize = mPane.getMapContent().layers().size();

		// replace yellow with red
		if (currentSize > layersWithoutLocation) { // TODO zahl anpassen an polit. layer
			Layer lastLayer = mPane.getMapContent().layers().get(currentSize - 1);
			
			float size = (float) width / STAR_PROPORTION; // TODO STEFAN pct calculation
			Style newStyle = SLD.createPointStyle("Star", LAST_POSITION, LAST_POSITION,
					1.0f, size);

			try {
				FeatureCollection<?, ?> featureCollection = lastLayer.getFeatureSource().getFeatures();
				mPane.getMapContent().layers().remove(currentSize - 1);
				mPane.getMapContent().addLayer(new FeatureLayer(featureCollection, newStyle, lastLayer.getTitle()));
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		mPane.getMapContent().layers().add(newLayer);

	}

	public void setLayerVisibilities(boolean selected, List<Layer> layers, Composite comp) {
		for (Layer layer : layers)
			setLayerVisibility(selected, layer);

		if (comp != null)
			comp.setVisible(selected);
	}

	public List<Layer> getTerritoryLayers() {
		return territoryLayers;
	}

	///////////////////// PROTECTED////////////////////////////
	protected Shell getLegendShell(String tooltip) {
		if (legendShell.getText().equals(""))
			legendShell.setText(tooltip);

		return legendShell;
	}

	////////////////// PRIVATE///////////////////////////////////
	private void configComposite() {
		logger.info("Initialize map composite...");

		setLayout(getCompositeLayout());
		setCompositeLayoutData();
		setBackground(parent.getDefaultBgColor());
	}

	private void setTerritories() {
		territoriesMap = new TreeMap<String, Integer[]>(); // name, rgb

		territoriesMap.put("Aegyptisches Mameluken Sultanat", new Integer[] { 180, 133, 21 }); // brown
		territoriesMap.put("Byzantinische Gebiete", new Integer[] { 227, 25, 77 }); // red
		territoriesMap.put("Genuesische Gebiete", new Integer[] { 195, 54, 176 }); // pink/magenta
		territoriesMap.put("Herzogtum Naxos", new Integer[] { 14, 38, 178 }); // dark blue
		territoriesMap.put("Johanniterorden", new Integer[] { 91, 245, 71 }); // bright green
		territoriesMap.put("Kamariden Emirat", new Integer[] { 31, 120, 180 }); // bright blue
		territoriesMap.put("Königreich Zypern", new Integer[] { 235, 131, 23 }); // orange
		territoriesMap.put("Osmanisches Reich", new Integer[] { 238, 234, 73 }); // yellow
		territoriesMap.put("Venezianische Gebiete", new Integer[] { 36, 112, 31 }); // dark green
	}

	// TODO GeoTools (GIS)
	private void configMapPane() {
		logger.info("Add map pane...");

		MapContent mContent = new MapContent();

		mContent.addLayer(
				LayerCreator.getRasterLayer("src/main/resources/gis/rasterlayer/Textadventrue_neu_Proj.tif", "karte"));

		routeLayer = setShapeLayer("Reiseroute", true);		// parent.getRouteSelection()	
		mContent.addLayer(routeLayer);
		placesLayer = setShapeLayer("Standorte_neu", false);	// parent.getCitySelection()
		mContent.addLayer(placesLayer);
		
		territoryLayers = setShapeLayers("territories");
		mContent.addLayers(territoryLayers);

		layersWithoutLocation = mContent.layers().size();

		mPane = new SwtMapPane(this, SWT.NO_BACKGROUND);
		mPane.setBackground(parent.getDefaultBgColor()); // TODO pane standard color?
		mPane.setRenderer(new StreamingRenderer());
		mPane.setMapContent(mContent);
		mPane.setLayoutData(getMapLayoutData());

		// TODO bug: SwtMapPane/MapMouseListener and macOS because of THIS (Composite)
		int eventType = SWT.MouseDown;
		for (Listener listener : mPane.getListeners(eventType))
			mPane.removeListener(eventType, listener);

	}

	private List<Layer> setShapeLayers(String dirName) {
		List<Layer> layers = new ArrayList<Layer>();

		String subPath = dirName.concat(File.separator);
		boolean visible = false;

		layers.add(setShapeLayer(subPath.concat("AegyptischesMamelukenSultanat"), visible));
		layers.add(setShapeLayer(subPath.concat("Byzantinische Gebiete"), visible));
		layers.add(setShapeLayer(subPath.concat("Herzogtum Naxos"), visible));
		layers.add(setShapeLayer(subPath.concat("Johanniterorden"), visible));
		layers.add(setShapeLayer(subPath.concat("Kamariden Emirat"), visible));
		layers.add(setShapeLayer(subPath.concat("Königreich Zypern"), visible));
		layers.add(setShapeLayer(subPath.concat("OsmanischesReich"), visible));
		layers.add(setShapeLayer(subPath.concat("Venezianische Gebiete"), visible));
		layers.add(setShapeLayer(subPath.concat("Genuesische Gebiete"), visible));

		return layers;
	}

	private GridLayout getCompositeLayout() {
		layout = LayoutHelper.getGridLayout();
		return layout;
	}

	private void setLegend() {
		Display display = Display.getCurrent();
		//TODO enable closing on win10
		legendShell = new Shell(display, SWT.TITLE | SWT.MIN);
		legendShell.setLayout(LayoutHelper.getVerticalFillLayout());
		legendShell.setBackground(parent.getDefaultBgColor());

		for (String name : territoriesMap.keySet()) {
			Integer[] c = territoriesMap.get(name);

			Label label = new Label(legendShell, SWT.NONE);
			label.setText(name);
			// TODO alpha?
			label.setBackground(new org.eclipse.swt.graphics.Color(display, c[0], c[1], c[2]));
			label.addListener(SWT.MouseEnter, labelListener);
		}

		legendShell.addListener(SWT.MouseEnter, shellListener);
		legendShell.setVisible(false);
		legendShell.pack();
	}

	private void setCompositeLayoutData() {
		width = (int) Calc.getValByPct(parent.getWidth(), W_WIMGCOMP_PCT);
		setLayoutData(LayoutHelper.getGridData(width, height, false, true));
	}

	private GridData getMapLayoutData() {
		int width = this.width - (2 * layout.marginWidth), height = this.height - (2 * layout.marginHeight);

		return LayoutHelper.getGridData(width, height);
	}

	protected void setLayerVisibility(boolean selected, Layer layer) {
		layer.setVisible(selected);
	}

	private Layer setShapeLayer(String subPath, boolean visible) {
		Layer layer = null;
		// TODO file reference (deployment)
		File file = new File("src/main/resources/gis/shapelayer", subPath + ".shp");

		try {
			FileDataStore dataStore = FileDataStoreFinder.getDataStore(file);
			SimpleFeatureSource fileSrc = dataStore.getFeatureSource();

			Style style = null;
			String layerName = null;

			if (!subPath.contains(File.separator)) {
				if (subPath.startsWith("Standorte")) {
					float size = (float) width / 80; // TODO pct calculation

					style = SLD.createPointStyle("Circle", BLACK_AWT, DARK_RED_AWT, 1.0f, size, "Standort",
							LayerCreator.getPlacesFont(width));
				} else if (subPath.startsWith("Reiseroute")) {
					float size = (float) width / 400; // TODO pct calculation

					style = SLD.createLineStyle(DARK_RED_AWT, size);
				}
			} else { // territories/
				// TODO optimize workaround?
				String namePrefix = file.getName().substring(0, 8);

				for (String name : territoriesMap.keySet()) {
					if (name.startsWith(namePrefix)) {
						layerName = name;

						Integer[] c = territoriesMap.get(name);
						Color color = new Color(c[0], c[1], c[2]);

						style = SLD.createPolygonStyle(BLACK_AWT, color, 0.5f);

						break;
					}
				}
			}

			layer = new FeatureLayer(fileSrc, style);
			layer.setTitle(layerName);

			if (!visible)
				layer.setVisible(false);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return layer;
	}

}

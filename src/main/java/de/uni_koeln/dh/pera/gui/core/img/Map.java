package de.uni_koeln.dh.pera.gui.core.img;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.data.DataUtilities;
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

	private Logger logger = LoggerFactory.getLogger(getClass());

	protected static final int H_HIMGCOMP_PCT = 75;
	private static final int W_WIMGCOMP_PCT = /* 80 */77;

	private ImgComposite parent = null;

	private GridLayout layout = null;
	private GridCoverage2DReader reader = null;
	private StyleFactory sf = CommonFactoryFinder.getStyleFactory();
	private FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
	
	/** number of layers (exclusive the layers of player location */
	private int layersWithoutLocation = 0;

	private SwtMapPane mPane = null;

	private int width = 0, height = 0;

	public Map(ImgComposite parent) {
		super(parent, SWT.NONE);
		this.parent = parent;
	}

	public void init(int height) {
		this.height = height;

		configComposite();
		configMapPane();

	}
	
	public int getWidth() {
		return width;
	}
	
	public void changeVisibility(boolean selected, int layer) {
		Layer currentLayer = mPane.getMapContent().layers().get(layer);

		currentLayer.setVisible(selected);

	}

	public void updatePosition(Coordinate coordinate) {

		Layer newLayer = createPositionLayer(coordinate);

		int currentSize = mPane.getMapContent().layers().size();

		// replace yellow with red
		if (currentSize > layersWithoutLocation) { // TODO zahl anpassen an polit. layer
			Layer lastLayer = mPane.getMapContent().layers().get(currentSize - 1);
			Style newStyle = SLD.createPointStyle("Star", Color.RED, Color.RED, 1.0f, 10.0f);

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

	private void configComposite() {
		logger.info("Initialize map composite...");

		setLayout(getCompositeLayout());
		setCompositeLayoutData();
		setBackground(parent.getDefaultBgColor());
	}

	// TODO GeoTools (GIS)
	private void configMapPane() {
		logger.info("Add map pane...");

		MapContent mContent = new MapContent();
		try {
			mContent.addLayer(getRasterLayer("src/main/resources/gis/rasterlayer/textadventure.tif", "karte"));
			mContent.addLayer(getShapeLayer("src/main/resources/gis/shapelayer/Reiseroute.shp",
					"routen", new Color(202,30,0), true));
			mContent.addLayer(getShapeLayer("src/main/resources/gis/shapelayer/Standorte_neu.shp", 
					"orte", new Color(202,30,0))); //TODO font
			mContent.addLayer(getShapeLayer
					("src/main/resources/gis/shapelayer/politicallayer/AegyptischesMamelukenSultanat.shp",
							"AegyptischesMamelukenSultanat", new Color(180,133,21)));
			mContent.addLayer(getShapeLayer
					("src/main/resources/gis/shapelayer/politicallayer/Byzantinische Gebiete.shp/",
							"Byzantinische Gebiete", new Color(227,25,77)));
			mContent.addLayer(getShapeLayer
					("src/main/resources/gis/shapelayer/politicallayer/Genuesische Gebiete.shp",
							"Genuesische Gebiete", new Color(14,38,178)));
			mContent.addLayer(getShapeLayer
					("src/main/resources/gis/shapelayer/politicallayer/Herzogtum Naxos.shp",
							"Herzogtum Naxos", new Color(91,245,71)));
			mContent.addLayer(getShapeLayer
					("src/main/resources/gis/shapelayer/politicallayer/Johanniterorden.shp",
							"Johanniterorden", new Color(31,120,180)));
			mContent.addLayer(getShapeLayer
					("src/main/resources/gis/shapelayer/politicallayer/Kamariden Emirat.shp",
							"Kamariden Emirat", new Color(235,131,23)));
			mContent.addLayer(getShapeLayer
					("src/main/resources/gis/shapelayer/politicallayer/Königreich Zypern.shp",
							"Königreich Zypern", new Color(238,234,73)));
			mContent.addLayer(getShapeLayer
					("src/main/resources/gis/shapelayer/politicallayer/OsmanischesReich.shp",
							"Osmanisches Reich", new Color(36,112,31)));
			mContent.addLayer(getShapeLayer
					("src/main/resources/gis/shapelayer/politicallayer/Venezianische Gebiete.shp",
							"Venezianische Gebiete", new Color(195,54,176)));

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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private GridLayout getCompositeLayout() {
		layout = LayoutHelper.getGridLayout();
		return layout;
	}

	private void setCompositeLayoutData() {
		width = (int) Calc.getValByPct(parent.getWidth(), W_WIMGCOMP_PCT);
		setLayoutData(LayoutHelper.getGridData(width, height, false, true));
	}

	private GridData getMapLayoutData() {
		int width = this.width - (2 * layout.marginWidth), height = this.height - (2 * layout.marginHeight);

		return LayoutHelper.getGridData(width, height);
	}

	

	private Layer createPositionLayer(Coordinate coordinate) {
		Layer layer = null;
		try {
			// transform 4326 to 3857
			CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
			CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:3857");

			MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
			JTS.transform(coordinate, coordinate, transform);

			SimpleFeatureType TYPE = DataUtilities.createType("Location", "the_geom:Point:srid=3857");

			GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
			SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);

			Point point = geometryFactory.createPoint(coordinate);
			featureBuilder.add(point);
			SimpleFeature feature = featureBuilder.buildFeature(null);

			DefaultFeatureCollection featureCollection = new DefaultFeatureCollection("position", TYPE);
			featureCollection.add(feature);

			Style style = SLD.createPointStyle("Star", Color.YELLOW, Color.YELLOW, 1.0f, 10.0f);
			layer = new FeatureLayer(featureCollection, style, "position");
		} catch (TransformException e) {
			e.printStackTrace();
		} catch (NoSuchAuthorityCodeException e) {
			e.printStackTrace();
		} catch (FactoryException e) {
			e.printStackTrace();
		} catch (SchemaException e) {
			e.printStackTrace();
		}

		return layer;
	}

	private Layer getRasterLayer(String path, String title) {
		File rasterFile = new File(path);

		AbstractGridFormat format = GridFormatFinder.findFormat(rasterFile);
		Hints hints = new Hints();

		if (format instanceof GeoTiffFormat)
			hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);

		reader = format.getReader(rasterFile, hints);
		Style rasterStyle = createGreyscaleStyle(/* 1 */);

		return new GridReaderLayer(reader, rasterStyle, title);
	}

	private Style createGreyscaleStyle(/* int band */) {
		
		ContrastEnhancement ce = sf.contrastEnhancement(ff.literal(1.0), ContrastMethod.NORMALIZE);
		SelectedChannelType sct = sf.createSelectedChannelType(String.valueOf(/* band */1), ce);

		RasterSymbolizer sym = sf.getDefaultRasterSymbolizer();
		ChannelSelection sel = sf.channelSelection(sct);
		sym.setChannelSelection(sel);

		return SLD.wrapSymbolizers(sym);
	}
	/**
	 * default: layer set insivible
	 * @param path
	 * @param title
	 * @param color
	 * @return
	 * @throws IOException
	 */
	private Layer getShapeLayer(String path, String title, Color color) throws IOException {
		return getShapeLayer(path, title, color, false);
	}

	private Layer getShapeLayer(String path, String title, Color color, boolean visible) throws IOException {
		File file = new File(path);
		ShapefileDataStore shapeFile = new ShapefileDataStore(file.toURI().toURL());
		SimpleFeatureSource featureSource = shapeFile.getFeatureSource();

		try {
			CoordinateReferenceSystem crs = CRS.decode("EPSG:3857");
			shapeFile.forceSchemaCRS(crs);
		} catch (NoSuchAuthorityCodeException e) {
			e.printStackTrace();
		} catch (FactoryException e) {
			e.printStackTrace();
		}

		Style style = null;
		if (title.equals("routen"))
			style = SLD.createLineStyle(color, 1.5f);
		else if (title.equals("orte")) {
			Font font = sf.getDefaultFont();
			font.setSize(ff.literal(15));
			font.setStyle(ff.literal(Font.Style.ITALIC));
			font.setWeight(ff.literal(Font.Weight.BOLD));
			//TODO font color
			style = SLD.createPointStyle("Circle", Color.BLACK, color, 1.0f, 7.0f, "Standort", font);
		} else {
			// TODO transparent
			style = SLD.createPolygonStyle(Color.BLACK, color, /*0.27f*/0.5f);
		}


		FeatureLayer layer = new FeatureLayer(featureSource, style, title);
		layer.setVisible(visible);
		return layer;
	}

	

}

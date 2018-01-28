package de.uni_koeln.dh.pera.gui.core.img;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.geotools.data.DataUtilities;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swt.SwtMapPane;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import de.uni_koeln.dh.pera.gui.misc.LayoutHelper;
import de.uni_koeln.dh.pera.util.Calc;

// TODO GeoTools (GIS)
public class Map extends Composite {

	private Logger logger = LoggerFactory.getLogger(getClass());

	// TODO if fitting set to final
	protected static int H_HIMGCOMP_PCT = 75;
	private static int W_WIMGCOMP_PCT = 80;

	private ImgComposite parent = null;
	private GridLayout layout = null;
	private SwtMapPane mPane = null;

	private int width = 0, height = 0;

	// private List<Layer> layers = null;
	// private SwtMapPane mPane = null;

	public Map(ImgComposite parent) {
		super(parent, SWT.NONE);
		this.parent = parent;
	}

	public void init(int height) {
		this.height = height;

		configComposite();
		configMapPane();

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
			mContent.addLayer(getShapeLayer("src/main/resources/gis/shapelayer/Reiseroute.shp", "routen"));
			mContent.addLayer(getShapeLayer("src/main/resources/gis/shapelayer/Kapitel_neu.shp", "kapitel"));
			mContent.addLayer(getShapeLayer("src/main/resources/gis/shapelayer/Standorte_neu.shp", "standort"));
//			mContent.addLayer(createPositionLayer(38.4, 26.166667));
			List<Layer> layers = mContent.layers();
//			for (Layer layer : layers) {
//				logger.info(layer.getTitle());
//				FeatureIterator i = layer.getFeatureSource().getFeatures().features();
//				while (i.hasNext()) {
//					Feature f = i.next();
//					logger.info(f.toString());
//					logger.info(f.getDefaultGeometryProperty().toString());
//
//				}
//			}
			mPane = new SwtMapPane(this, SWT.NO_BACKGROUND);
			mPane.setBackground(parent.getDefaultBgColor()); // TODO pane standard color?
			mPane.setRenderer(new StreamingRenderer());
			mPane.setMapContent(mContent);
			mPane.setLayoutData(getMapLayoutData());
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

	public void changeVisibility(int layer) {
		Layer currentLayer = mPane.getMapContent().layers().get(layer);
		if (currentLayer.isVisible())
			currentLayer.setVisible(false);
		else
			currentLayer.setVisible(true);
	}
	
	public void updatePosition(Coordinate coordinate) {
		
		Layer newLayer = createPositionLayer(coordinate);
		int currentSize = mPane.getMapContent().layers().size();
		if(currentSize > 3) {
			mPane.getMapContent().layers().remove(currentSize - 1);
		}
		mPane.getMapContent().layers().add(newLayer);
		
	}

	private Layer createPositionLayer(Coordinate coordinate) {
		Layer layer = null;
//		Coordinate coordinate = new Coordinate(latitude, longitude);
		//transform 4326 to 3857
	
		try {
			
			CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
			CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:3857");
			
			MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
			JTS.transform( coordinate, coordinate, transform );
			
			SimpleFeatureType TYPE = DataUtilities.createType("Location", "the_geom:Point:srid=3857");

			GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
			SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);

			Point point = geometryFactory.createPoint(coordinate);
			featureBuilder.add(point);
			SimpleFeature feature = featureBuilder.buildFeature(null);

			DefaultFeatureCollection featureCollection = new DefaultFeatureCollection("position", TYPE);
			featureCollection.add(feature);

			Style style = SLD.createPointStyle("Cross", Color.BLUE, Color.BLUE, 1.0f, 5.0f);
			layer = new FeatureLayer(featureCollection, style,"position");
		} catch (TransformException e) {
			e.printStackTrace();
		} catch (NoSuchAuthorityCodeException e) {
			e.printStackTrace();
		} catch (FactoryException e) {
			e.printStackTrace();
		} catch (SchemaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return layer;
	}

	private Layer getShapeLayer(String path, String title) throws IOException {
		File file = new File(path);
		ShapefileDataStore shapeFile = new ShapefileDataStore(file.toURI().toURL());
		SimpleFeatureSource featureSource = shapeFile.getFeatureSource();
		
		try {
			CoordinateReferenceSystem crs = CRS.decode("EPSG:3857");
			shapeFile.forceSchemaCRS(crs);
		} catch (NoSuchAuthorityCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//		FeatureType schema =  featureSource.getSchema();
//		logger.info(schema.getGeometryDescriptor().toString());


		Style style = null;
		if (title.equals("routen"))
			style = SLD.createLineStyle(new Color(0), 1);
		if (title.equals("standort"))
			style = SLD.createPointStyle("Circle", new Color(0), new Color(0), 1, 2, "Standort", null);
		if (title.equals("kapitel"))
			style = SLD.createPointStyle("Star", Color.RED, Color.BLACK, 1, 10, null, null);

		FeatureLayer layer = new FeatureLayer(featureSource, style, title);
		
		return layer;
	}

	public int getWidth() {
		return width;
	}

	

}

package de.uni_koeln.dh.pera.gui.core.img;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.DataUtilities;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swt.SwtMapPane;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

// TODO GeoTools (GIS)
public class MapPane /*extends SwtMapPane*/ {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
//	private List<Layer> layers = null;
	private SwtMapPane mPane = null;
	
	public void changeVisibility(int layer) {
		Layer currentLayer = mPane.getMapContent().layers().get(layer);
		if(currentLayer.isVisible())
			currentLayer.setVisible(false);
		else
			currentLayer.setVisible(true);
		
//		mPane.redraw();
		
	}
	
	public void addPoint(double latitude, double longitude) throws IOException, SchemaException {
		
		final SimpleFeatureType TYPE = DataUtilities.createType("Location",
                "the_geom:Point:srid=4326," + // <- the geometry attribute: Point type
                "Standort:String," +   // <- a String attribute
                "Reihenfolg:Integer"   // a number attribute
        );
		
		Layer layer = mPane.getMapContent().layers().get(2);

		FeatureCollection<SimpleFeatureType,SimpleFeature> features = (FeatureCollection<SimpleFeatureType, SimpleFeature>) layer
				.getFeatureSource().getFeatures();
		DefaultFeatureCollection collection = new DefaultFeatureCollection(features);
		
		
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
		
		
		Point point = geometryFactory.createPoint(new Coordinate(latitude, longitude));
		
		featureBuilder.add(point);
		featureBuilder.add("aktuell");
		featureBuilder.add(collection.size() + 1);
		SimpleFeature feature = featureBuilder.buildFeature("Standort" + (collection.size()+1));
		
		Collection<Property> props = feature.getProperties();
		for (Property prop : props) {
			logger.info(prop.getName().toString() + ": " + prop.getValue().toString());
		}
		
		
		logger.info(collection.size() + "");
		collection.add(feature);
		logger.info(collection.size() + "");
		logger.info("------------------------------------------");
		Iterator<SimpleFeature> iter = collection.iterator();
		while(iter.hasNext()) {
			SimpleFeature next = iter.next();
			props = next.getProperties();
			for (Property prop : props) {
				logger.info(prop.getName().toString() + ": " + prop.getValue().toString());
			}
			logger.info("---------------");
//			logger.info(next.getID());
//			logger.info(next.getAttribute("Standort").toString());
		}
		
		
		mPane.redraw();
	}
	
	public void setMap(Composite comp) throws Exception {

		
		MapContent mContent = new MapContent();
		mContent.addLayer(getShapeLayer("src/main/resources/gis/Reiseroute.shp", "routen"));
		mContent.addLayer(getShapeLayer("src/main/resources/gis/Kapitel.shp", "kapitel"));
		mContent.addLayer(getShapeLayer("src/main/resources/gis/Standorte.shp","standort"));
//		mContent.addLayer(getRasterLayer("src/main/resources/gis/textadventure_empty_geo.tif"));
		
		List<Layer> layers = mContent.layers();
		for(Layer layer : layers) {
			logger.info(layer.getTitle());
		}
		
		
		mPane = new SwtMapPane(comp, org.eclipse.swt.SWT.BORDER | org.eclipse.swt.SWT.NO_BACKGROUND);
		mPane.setBackground(Display.getCurrent().getSystemColor(org.eclipse.swt.SWT.COLOR_WHITE));
		mPane.setRenderer(new StreamingRenderer());
		mPane.setMapContent(mContent);
		mPane.setLayoutData(comp.getLayoutData());
	}

	private Layer getShapeLayer(String path, String title) throws IOException {
		File file = new File(path);		
		ShapefileDataStore shapeFile = new ShapefileDataStore(file.toURI().toURL());
		//TEST
//		ContentFeatureSource source = shapeFile.getFeatureSource();
//		ContentFeatureCollection collection = source.getFeatures();

		
		
		
		//END
		
		SimpleFeatureSource featureSource = shapeFile.getFeatureSource();
		
//		Font font = 
		
		Style style = null;
		if(title.equals("routen"))
			style = SLD.createLineStyle(new Color(0), 1);
		if(title.equals("standort"))
			style = SLD.createPointStyle("Circle", new Color(0), new Color(0), 1, 2, "Standort", null);
		if(title.equals("kapitel"))
			style = SLD.createPointStyle("Star", Color.RED, Color.BLACK, 1, 10, null, null);

		FeatureLayer layer = new FeatureLayer(featureSource, style, title);
//		FeatureIterator i = layer.getFeatureSource().getFeatures().features();
//		while (i.hasNext()) {
//			Feature f = i.next();
//			logger.info(f.toString());
//			
//		}
		return layer;
	}

	
	
//	private Layer getRasterLayer(String path) throws IOException {
//		File file = new File(path);
//	    
////	    AbstractGridFormat format = GridFormatFinder.findFormat(file);
////	    GridCoverage2DReader reader = format.getReader(file);	    
//
//
//		GeoTiffReader reader = new GeoTiffReader(file, new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
//		GridCoverage2D coverage = reader.read(null);
//
//		
//	    
////	    Style style = SLD.createSimpleStyle(coverage.getSchema());
////	    Style style = createRGBStyle(coverage);
//		Layer rasterLayer = new GridCoverageLayer(coverage, null, "");
//		return rasterLayer;
//	}

	

}

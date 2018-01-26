package de.uni_koeln.dh.pera.gui.core.img;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swt.SwtMapPane;
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
		
		mPane.redraw();
		
	}
	
	public void addPoint(double latitude, double longitude) throws IOException {
		
		Layer layer = mPane.getMapContent().layers().get(1); //get kapitel layer
		FeatureCollection<SimpleFeatureType,SimpleFeature> coll = (FeatureCollection<SimpleFeatureType, SimpleFeature>) layer.getFeatureSource().getFeatures();
		DefaultFeatureCollection collection = new DefaultFeatureCollection(coll);
		
		
		
		SimpleFeatureTypeBuilder typeB = new SimpleFeatureTypeBuilder();
		// set crs
        typeB.setCRS(DefaultGeographicCRS.WGS84);
        // add geometry
        typeB.add("location", Point.class);
        
        
		SimpleFeatureType type = typeB.buildFeatureType();
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(type);
		
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
        com.vividsolutions.jts.geom.Point point = geometryFactory.createPoint(new Coordinate(latitude, longitude));
        featureBuilder.add(point);
        SimpleFeature feature = featureBuilder.buildFeature("FeaturePoint");
//		
        collection.add(feature);
        
//		type
		mPane.redraw();
	}
	
	public void setMap(Composite comp) throws Exception {
		// download: http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/50m/cultural/50m_cultural.zip

		
//		Layer routeLayer = getShapeLayer("src/main/resources/gis/Reiseroute.shp", "routen");
//		Layer chapterLayer = getShapeLayer("src/main/resources/gis/Kapitel.shp", "kapitel"); 
//		Layer positionLayer = getShapeLayer("src/main/resources/gis/positionLayer.shp","standort");
//		Layer rasterLayer = getRasterLayer("src/main/resources/gis/textadventure_empty_geo.tif");
		
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

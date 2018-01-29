package de.uni_koeln.dh.pera.gui.core.img;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.data.DataUtilities;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.Hints;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.Layer;
import org.geotools.referencing.CRS;
import org.geotools.styling.ChannelSelection;
import org.geotools.styling.ContrastEnhancement;
import org.geotools.styling.Font;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.SelectedChannelType;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.ContrastMethod;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class LayerCreator {

	private static GridCoverage2DReader reader = null;
	private static StyleFactory sf = CommonFactoryFinder.getStyleFactory();
	private static FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

	protected static Layer getRasterLayer(String path, String title) {
		File rasterFile = new File(path);
		AbstractGridFormat format = GridFormatFinder.findFormat(rasterFile);
		Hints hints = new Hints();

		if (format instanceof GeoTiffFormat)
			hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);

		reader = format.getReader(rasterFile, hints);
		Style rasterStyle = createGreyscaleStyle(/* 1 */);

		return new GridReaderLayer(reader, rasterStyle, title);
	}

	/**
	 * Create a Style to display the specified band of the GeoTIFF image as a
	 * greyscale layer.
	 * <p>
	 * This method is a helper for createGreyScale() and is also called directly by
	 * the displayLayers() method when the application first starts.
	 *
	 * @param band
	 *            the image band to use for the greyscale display
	 *
	 * @return a new Style instance to render the image in greyscale
	 */
	private static Style createGreyscaleStyle(/* int band */) {

		ContrastEnhancement ce = sf.contrastEnhancement(ff.literal(1.0), ContrastMethod.NORMALIZE);
		SelectedChannelType sct = sf.createSelectedChannelType(String.valueOf(/* band */1), ce);

		RasterSymbolizer sym = sf.getDefaultRasterSymbolizer();
		ChannelSelection sel = sf.channelSelection(sct);
		sym.setChannelSelection(sel);
		return SLD.wrapSymbolizers(sym);
	}

	protected static List<Layer> getShapeLayers(String dirName, HashMap<String, Integer[]> terretoriesMap) {
		List<Layer> layers = new ArrayList<Layer>();

		String subPath = dirName.concat(File.separator);
		boolean visible = false;

		return layers;
	}

	/**
	 * default: layer set invisible
	 * 
	 * @param path
	 * @param title
	 * @param color
	 * @return
	 * @throws IOException
	 */
	protected static Layer getShapeLayer(String path, String title, Color color) {
		return LayerCreator.getShapeLayer(path, title, color, false);
	}

	protected static Layer getShapeLayer(String path, String title, Color color, boolean visible) {

		// try {
		// CoordinateReferenceSystem crs = CRS.decode("EPSG:3857");
		// shapeFile.forceSchemaCRS(crs);
		//
		// } catch (NoSuchAuthorityCodeException e) {
		// e.printStackTrace();
		// } catch (FactoryException e) {
		// e.printStackTrace();
		// }

		File file = new File(path);
		ShapefileDataStore shapeFile;
		try {
			shapeFile = new ShapefileDataStore(file.toURI().toURL());
			SimpleFeatureSource featureSource = shapeFile.getFeatureSource();

			Style style = null;
			if (title.equals("routen"))
				style = SLD.createLineStyle(color, 1.5f);
			else if (title.equals("orte")) {
				Font font = sf.getDefaultFont();
				font.setSize(ff.literal(15));
				font.setStyle(ff.literal(Font.Style.ITALIC));
				font.setWeight(ff.literal(Font.Weight.BOLD));
				// TODO font color
				style = SLD.createPointStyle("Circle", Color.BLACK, color, 1.0f, 7.0f, "Standort", font);
			} else {
				// TODO transparent
				style = SLD.createPolygonStyle(Color.BLACK, color, /* 0.27f */0.5f);
			}

			FeatureLayer layer = new FeatureLayer(featureSource, style, title);
			layer.setVisible(visible);
			return layer;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	protected static Layer createPositionLayer(Coordinate coordinate) {
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

	public static Font getPlacesFont(int width) {
		int size = width / 38;		// TODO pct calculation
		
		Font font = sf.getDefaultFont();
		font.setSize(ff.literal(size));	 
		font.setStyle(ff.literal(Font.Style.ITALIC));
		font.setWeight(ff.literal(Font.Weight.BOLD));

		return font;
	}
	
	
	

}

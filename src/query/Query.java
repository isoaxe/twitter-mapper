package query;

import filters.Filter;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import twitter4j.Status;
import ui.MapMarkerComplex;
import util.Util;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * A query over the twitter stream.
 */
public class Query implements Observer {
    // The map on which to display markers when the query matches
    private final JMapViewer map;
    // Each query has its own "layer" so they can be turned on and off all at once
    private Layer layer;
    // The color of the outside area of the marker
    private final Color color;
    // The string representing the filter for this query
    private final String queryString;
    // The filter parsed from the queryString
    private final Filter filter;
    // The checkBox in the UI corresponding to this query (so we can turn it on and off and delete it)
    private JCheckBox checkBox;
    // A list of all the MapMarkers for this query
    private List<MapMarker> markers;

    // getters
    public Color getColor() {
        return color;
    }
    public String getQueryString() {
        return queryString;
    }
    public Filter getFilter() {
        return filter;
    }
    public Layer getLayer() {
        return layer;
    }
    public JCheckBox getCheckBox() {
        return checkBox;
    }
    public boolean getVisible() { return layer.isVisible(); }

    // setters
    public void setCheckBox(JCheckBox checkBox) {
        this.checkBox = checkBox;
    }
    public void setVisible(boolean visible) {
        layer.setVisible(visible);
    }

    public Query(String queryString, Color color, JMapViewer map) {
        this.queryString = queryString;
        this.filter = Filter.parse(queryString);
        this.color = color;
        this.layer = new Layer(queryString);
        this.map = map;
        this.markers = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Query: " + queryString;
    }

    /**
     * This query is no longer interesting, so terminate it and remove all traces of its existence.
     */
    public void terminate() {
        for (MapMarker marker: markers) {
            map.removeMapMarker(marker);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Status tweet = (Status) arg;
        if (filter.matches(tweet)) {
            Coordinate location = Util.statusCoordinate(tweet);
            String profilePicUrl = tweet.getUser().getProfileImageURL();
            String text = tweet.getText();
            MapMarkerComplex marker = new MapMarkerComplex(layer, location, color, text, profilePicUrl);
            map.addMapMarker(marker);
            markers.add(marker);
        }
    }
}


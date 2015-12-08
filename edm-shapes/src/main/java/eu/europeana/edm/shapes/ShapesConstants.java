/**
 * 
 */
package eu.europeana.edm.shapes;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 8 Dec 2015
 */
public interface ShapesConstants
{
    public static enum ShapesType { INTERNAL, EXTERNAL };

    public static String EDM_EXTERNAL_SHAPES_LOCATION
        = "/etc/edm/shapes/edm-external.ttl";

    public static String EDM_INTERNAL_SHAPES_LOCATION
        = "/etc/edm/shapes/edm-internal.ttl";
}

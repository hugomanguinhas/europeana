/**
 * 
 */
package eu.europeana.edm.shapes;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 15 Dec 2015
 */
public interface SHACLNamespace
{
    public static String SHACL_NS     = "http://www.w3.org/ns/shacl#";
    public static String SHACL_PREFIX = "sh";
    //Classes
    public static String SHACL_RESULT            = SHACL_NS + "ValidationResult";
    public static String SHACL_FOCUS_NODE        = SHACL_NS + "focusNode";
    public static String SHACL_SUBJECT           = SHACL_NS + "subject";
    public static String SHACL_PREDICATE         = SHACL_NS + "predicate";
    public static String SHACL_MESSAGE           = SHACL_NS + "message";
    public static String SHACL_SOURCE_TEMPLATE   = SHACL_NS + "sourceTemplate";
    public static String SHACL_SOURCE_SHAPE      = SHACL_NS + "sourceShape";
    public static String SHACL_SOURCE_CONSTRAINT = SHACL_NS + "sourceConstraint";
    public static String SHACL_SEVERITY          = SHACL_NS + "severity";
}

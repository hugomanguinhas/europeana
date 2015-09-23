package eu.europeana.tf.agreement;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 23 Sep 2015
 */
public class EnrichmentAnnotation
{
    private String               _sample;
    private Object               _source;
    private String               _annotator;
    private String               _comments;
    private AnnotationParameters _params;

    public EnrichmentAnnotation(String sampleID, Object source, String annotator
                              , AnnotationParameters params, String comments)
    {
        _sample    = sampleID;
        _source    = source;
        _annotator = annotator;
        _params    = params;
        _comments  = comments;
    }

    public EnrichmentAnnotation(String sampleID, String ref, String annotator)
    {
        this(sampleID, ref, annotator, null, null);
    }

    public String getSampleID()                 { return _sample; }
    public Object getSource()                   { return _source; }
    public AnnotationParameters getParameters() { return _params; }
}
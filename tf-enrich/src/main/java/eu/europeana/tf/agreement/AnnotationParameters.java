package eu.europeana.tf.agreement;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 23 Sep 2015
 */
public class AnnotationParameters
{
    public static enum Correctness implements RatingCategory
    {
        CORRECT, INCORRECT, UNSURE;

        @Override
        public String getCode() { return String.valueOf(this.name().charAt(0)); }

        @Override
        public String getName() { return this.name(); }
    }

    public static enum NameCompleteness implements RatingCategory
    {
        PARTIAL, FULL, UNSURE, NA;

        @Override
        public String getCode() { return String.valueOf(this.name().charAt(0)); }

        @Override
        public String getName() { return this.name(); }
    }
    
    public static enum ConceptCompleteness implements RatingCategory
    {
        NARROWER, BROADER, FULL, UNSURE, NA;

        @Override
        public String getCode() { return String.valueOf(this.name().charAt(0)); }

        @Override
        public String getName() { return this.name(); }
    }

    private Correctness         _correct;
    private NameCompleteness    _nameComp;
    private ConceptCompleteness _conceptComp;
    private String              _key;

    public AnnotationParameters(Correctness correct
                              , NameCompleteness nameComp
                              , ConceptCompleteness conceptComp)
    {
        _correct     = correct;
        _nameComp    = nameComp;
        _conceptComp = conceptComp;
        _key         = buildKey();
    }

    public AnnotationParameters() {}

    public String getKey() { return _key; }

    public AnnotationParameters parse(String correct, String name
                                    , String concept)
    {
        _correct     = parseCorrectness(correct);
        _nameComp    = parseName(name);
        _conceptComp = parseConcept(concept);
        _key         = buildKey();
        return this;
    }

    public boolean equals(AnnotationParameters p)
    {
        return ((_correct == p._correct) && (_nameComp == p._nameComp)
             && (_conceptComp == p._conceptComp));
    }

    public String toString() { return ("\"" + _key + "\""); }

    public String toFullString()
    {
        return getName(_correct) + "," + getName(_nameComp) + "," + getName(_conceptComp);
    }

    public boolean hasCategory(RatingCategory cat)
    {
        return ( _correct == cat ) || ( _nameComp == cat ) || (_conceptComp == cat);
    }

    public boolean hasCategory(RatingCategory... cats)
    {
        for ( RatingCategory cat : cats )
        {
            if ( !hasCategory(cat) ) { return false; }
        }
        return true;
    }

    public boolean validate()
    {
        if ( _correct == null                       ) { return false; }
        if ( _correct == Correctness.INCORRECT
          || _correct == Correctness.UNSURE         ) { return true;  }
        if ( _conceptComp == ConceptCompleteness.NA ) { return false; }
        return true;
    }

    private String getName(RatingCategory cat) { return (cat == null ? "?" : cat.getName() ); }

    private String buildKey()
    {
        return getKey(_correct) + getKey(_nameComp) + getKey(_conceptComp);
    }

    private String getKey(Enum e)
    {
        return (e == null ? "" : String.valueOf(e.name().charAt(0)));
    }

    private Correctness parseCorrectness(String str)
    {
        if ( str == null || str.isEmpty() ) { 
            System.err.println("Illegal correctness value: " + str);
            return null;
        }

        switch ( str.trim().toUpperCase().charAt(0) )
        {
            case 'C': return Correctness.CORRECT;
            case 'I': return Correctness.INCORRECT;
            case 'U': return Correctness.UNSURE;
        }

        System.err.println("Illegal correctness value: " + str);
        return null;
    }

    private NameCompleteness parseName(String str)
    {
        if ( str == null   ) { return NameCompleteness.NA; }

        str = str.trim();
        if ( str.isEmpty() ) { return NameCompleteness.NA; }

        switch ( str.trim().toUpperCase().charAt(0) )
        {
            case 'P': return NameCompleteness.PARTIAL;
            case 'F': return NameCompleteness.FULL;
            case 'U': return NameCompleteness.UNSURE;
        }

        System.err.println("Illegal concept completeness value: " + str);

        return NameCompleteness.UNSURE;
    }

    private ConceptCompleteness parseConcept(String str)
    {
        return getCategory(str, ConceptCompleteness.values()
                         , ConceptCompleteness.NA, ConceptCompleteness.UNSURE);
    }

    private <C extends RatingCategory> C getCategory(String str, C[] cats
                                                   , C na, C def)
    {
        if ( str == null   ) {
            if ( na != null ) { return na; }
            System.err.println("Empty value: " + str
                             + ", for " + def.getClass().getName());
            return null;
        }

        str = str.trim();
        if ( str.isEmpty() ) { 
            if ( na != null ) { return na; }
            System.err.println("Empty value: " + str
                             + ", for " + def.getClass().getName());
            return null;
        }

        String s = str.toUpperCase().substring(0,1);
        for ( C c : cats )
        {
            if ( c.getCode().equals(s) ) { return c; }
        }
        System.err.println("Illegal value: " + s
                         + ", for " + def.getClass().getName());
        return def;
    }
}
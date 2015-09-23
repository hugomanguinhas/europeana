package eu.europeana.enrich.disamb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import static eu.europeana.edm.EDMNamespace.*;

public class BirthDeathDateDisambiguator implements ResourceDisambiguator
{
    private Property            _birthDate;
    private Property            _deathDate;
    private SameAsDisambiguator _sameAs;

    private static Date               NOW     = new Date();
    private static SimpleDateFormat[] FORMATS = {
        new SimpleDateFormat("yyyyXXX"), new SimpleDateFormat("yyyy-MM-ddXXX")
      , new SimpleDateFormat("yyyy G"), new SimpleDateFormat("yyyy")
      , new SimpleDateFormat("MMM yyyy")
    };

    public BirthDeathDateDisambiguator() {}

    @Override
    public void init(Model m)
    {
        _birthDate = m.getProperty(RDAGR2_DATEOFBIRTH);
        _deathDate = m.getProperty(RDAGR2_DATEOFDEATH);
        _sameAs    = new SameAsDisambiguator();
        _sameAs.init(m);
    }

    @Override
    public int compare(Resource r1, Resource r2)
    {
        if ( (r1 == r2) || r1.getURI().equals(r2.getURI()) ) { return 0; }

        Date b1 = getDate(r1, _birthDate);
        Date d1 = getDate(r1, _deathDate);
        Date b2 = getDate(r2, _birthDate);
        Date d2 = getDate(r2, _deathDate);

        int ret = 0;
        ret = compareDate(getBirthDate(b1, d1), getBirthDate(b2, d2));
        if ( ret != 0 ) { return ret; }

        ret = compareDate(d1, d2);
        if ( ret != 0 ) { return ret; }

        System.err.println("No birth and death: " + r1.getURI() + ";" + r2.getURI());

        return _sameAs.compare(r1, r2);
    }

    private int compareDate(Date d1, Date d2)
    {
        if ( d1 == d2 ) { return 0; }
        if ( d1 != null && d2 != null ) { return d1.compareTo(d2); }
        return (d1 == null ? 1 : -1);
    }

    private Date getBirthDate(Date birth, Date death)
    {
        if ( birth == null                 ) { return death; }
        if ( birth == NOW && death != null ) { return death; }
        return birth;
    }

    private Date getDate(Resource r, Property p)
    {
        StmtIterator iter = r.listProperties(p);
        boolean exists = false;
        while ( iter.hasNext() )
        {
            Date date = parseDate(iter.next().getLiteral().getString());
            if ( date != null ) { return date; }
            exists = true;
        }
        if ( exists ) { return NOW; }
        return null;
    }

    private static Date parseDate(String str)
    {
        str = preprocess(str);
        for ( SimpleDateFormat format : FORMATS )
        {
            try {
                return format.parse(str);
            } catch (ParseException e) {}
        }
        System.err.println("Cannot parse: " + str);
        return null;
    }

    private static String preprocess(String str)
    {
        return str.replaceAll("^ca?[.]?", "").trim();
    }

    private static String format(Date date)
    {
        return new SimpleDateFormat("yyyy-MM-dd G").format(date);
    }
}

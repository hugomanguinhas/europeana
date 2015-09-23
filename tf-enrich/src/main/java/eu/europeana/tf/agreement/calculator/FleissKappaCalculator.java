package eu.europeana.tf.agreement.calculator;

import java.io.File;
import java.text.DecimalFormat;

import eu.europeana.tf.agreement.AgreementRatings;
import eu.europeana.tf.agreement.RatingCategory;
import eu.europeana.utils.CSVWriter;

/*
 * See: https://en.wikipedia.org/wiki/Fleiss%27_kappa
 */
/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 23 Sep 2015
 */
public class FleissKappaCalculator implements AgreementCalculator
{
    private static DecimalFormat FORMAT = new DecimalFormat("0.000");


    private AgreementRatings _ratings;

    private RatingCategory[] _categories;


    public FleissKappaCalculator(RatingCategory... cats)
    {
        _categories = cats;
    }


    @Override
    public synchronized double calculate(AgreementRatings ratings)
    {
        _ratings = ratings;
        try {
            start();
            double kappa = calculateKappa();
            end(kappa);
            return kappa;
        }
        finally {
            _ratings = null;
        }
    }

    public synchronized double calculate(AgreementRatings ratings, File dumpFile)
    {
        try {
            double kappa = calculate(ratings);
            _ratings = ratings;
            printTable(dumpFile, kappa);
            _ratings = null;
            return kappa;
        }
        finally {
            _ratings = null;
        }
    }

    private void printTable(File file, double kappa)
    {
        CSVWriter p = new CSVWriter(file);
        p.start();

        try {
            int N = _ratings.getSubjectsCount();
            //header
            p.print("Subjects\\Categories");
            for ( RatingCategory cat : _categories ) { p.print(cat.getCode()); }
            p.println("#","Pi");

            int    sumR  = 0;
            double sumPi = 0;
            for ( String subject : _ratings.getSubjects() )
            {
                p.print(subject);
                for ( RatingCategory cat : _categories )
                {
                    p.print(_ratings.countRatings(subject, cat));
                }
                int    rating = _ratings.countRatings(subject, _categories);
                double pi     = getPi(subject);
                sumR  += rating;
                sumPi += pi;
                p.println(rating, FORMAT.format(pi));
            }

            p.print("Total");
            for ( RatingCategory cat : _categories ) { p.print(_ratings.countRatings(cat)); }
            p.println(sumR, "Ṗ = " + FORMAT.format(sumPi / N));

            p.print("Pj");
            double sumPj = 0;
            for ( RatingCategory cat : _categories )
            {
                double Pj = getPj(cat);
                sumPj += (Pj * Pj);
                p.print(FORMAT.format(Pj));
            }
            p.println("Ṗe = " + FORMAT.format(sumPj), "K = " + FORMAT.format(kappa));
        }
        finally {
            p.end();
        }
    }

    private void start()
    {
        System.out.print("Calculating Fleiss Kappa with ");
        System.out.print("(N=" + _ratings.getSubjectsCount());
        System.out.print(",n=" + _ratings.getRatingsPerSubject());
        System.out.print(",k=" + getCategoriesCount() + "): ");
    }

    private void end(double ret)
    {
        System.out.println(ret);
    }

    private int getCategoriesCount() { return _categories.length; }

    private double calculateKappa()
    {
        double Pe = getPe();
        return ((getP() - Pe) / ( 1d - Pe));
    }

    /*
     * 
     */
    private double getP()
    {
        return ( getSumPi() / _ratings.getSubjectsCount() );
    }

    /*
     * 
     * Pi: The extent to which raters agree for the i-th subject.
     * Formula: (1 / n*(n-1) ) * ( SUM ( nij * nij ) - n )
     */
    private double getPi(String subject)
    {
        int n = _ratings.getRatingsPerSubject();

        double total = 0;
        for ( RatingCategory cat : _categories )
        {
            int count = _ratings.countRatings(subject, cat);
            total += (count * count );
        }
        return ( ( total - n ) / ( n * ( n - 1 ) ) );
    }

    private double getSumPi()
    {
        double value  = 0;
        for ( String subject : _ratings.getSubjects() ) { value += getPi(subject); }
        return value;
    }

    /*
     * Pe
     */
    private double getPe()
    {
        double total = 0;
        for ( RatingCategory category : _categories )
        {
            double count = getPj(category);
            total += (count * count);
        }
        return total;        
    }

    /*
     * pj: The proportion of all ratings which were assigned to the j-th category:
     */
    private double getPj(RatingCategory cat)
    {
        int n = _ratings.getRatingsPerSubject();
        int N = _ratings.getSubjectsCount();
        
        double total = 0;
        for ( String sbj : _ratings.getSubjects() )
        {
            total += _ratings.countRatings(sbj, cat);
        }
        return ( total / ( n * N ) );
    }
}

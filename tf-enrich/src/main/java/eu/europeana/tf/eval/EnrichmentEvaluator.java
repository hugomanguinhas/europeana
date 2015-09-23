package eu.europeana.tf.eval;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import eu.europeana.tf.agreement.AnnotationParameters;
import eu.europeana.tf.agreement.RatingCategory;
import eu.europeana.tf.agreement.EnrichmentAnnotation;
import eu.europeana.tf.agreement.AnnotationParameters.ConceptCompleteness;
import eu.europeana.tf.agreement.AnnotationParameters.Correctness;
import eu.europeana.tf.agreement.AnnotationParameters.NameCompleteness;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 23 Sep 2015
 */
public class EnrichmentEvaluator
{
    public static EnrichmentEvaluator newRelaxedEvaluator()
    {
        EnrichmentEvaluator evaluator = new EnrichmentEvaluator();
        evaluator.addPrecisionWeight(1 , Correctness.CORRECT);
        evaluator.addPrecisionWeight(-1, Correctness.UNSURE);
        evaluator.addRecallCategory(Correctness.CORRECT);
        return evaluator;
    }

    public static EnrichmentEvaluator newStrictEvaluator()
    {
        EnrichmentEvaluator evaluator = new EnrichmentEvaluator();
        evaluator.addPrecisionWeight(1 , Correctness.CORRECT
                                       , NameCompleteness.FULL
                                       , ConceptCompleteness.FULL);
        evaluator.addPrecisionWeight(1 , Correctness.CORRECT
                                       , NameCompleteness.NA
                                       , ConceptCompleteness.FULL);
        evaluator.addPrecisionWeight(-1, Correctness.UNSURE);
        evaluator.addRecallCategory(Correctness.CORRECT
                                  , NameCompleteness.FULL
                                  , ConceptCompleteness.FULL);
        evaluator.addRecallCategory(Correctness.CORRECT
                                  , NameCompleteness.NA
                                  , ConceptCompleteness.FULL);
        return evaluator;
    }

    private Map<RatingCategory[],Double> _pWeights;
    private Collection<RatingCategory[]> _rCategories;

    public EnrichmentEvaluator()
    {
        _pWeights    = new HashMap();
        _rCategories = new ArrayList();
    }

    public void addPrecisionWeight(double weight, RatingCategory... cats)
    {
        _pWeights.put(cats, weight);
    }

    public void addRecallCategory(RatingCategory... cats)
    {
        _rCategories.add(cats);
    }

    public double calculatePrecision(Collection<EnrichmentAnnotation> col)
    {
        if ( col == null || col.isEmpty() ) { return 0; }

        double sum  = 0;
        int    size = col.size();
        for ( EnrichmentAnnotation ann : col )
        {
            double weight = getWeight(ann);
            if ( weight < 0 ) { size--; continue; }

            sum += weight;
        }
        return (sum / size);
    }

    public double calculateRecall(Collection<EnrichmentAnnotation> sample
                                , Collection<EnrichmentAnnotation> total)
    {
        int posT = calculatePositives(total);
        int posS = calculatePositives(sample);
        return ((double)posS / posT);
    }

    public double calculateFmeasuse(Collection<EnrichmentAnnotation> sample
                                  , Collection<EnrichmentAnnotation> total)
    {
        double precision = calculatePrecision(sample);
        double recall    = calculateRecall(sample, total);
        return ((precision * recall) / ( precision + recall)) * 2;
    }

    private int calculatePositives(Collection<EnrichmentAnnotation> col)
    {
        int count = 0;
        for ( EnrichmentAnnotation ann : col )
        {
            for ( RatingCategory[] cats : _rCategories )
            {
                if ( ann.getParameters().hasCategory(cats) ) { count++; break; }
            }
        }
        return count;
    }

    private double getWeight(EnrichmentAnnotation ann)
    {
        AnnotationParameters params = ann.getParameters();
        for ( RatingCategory[] cats : _pWeights.keySet() )
        {
            if ( !params.hasCategory(cats) ) { continue; }

            return _pWeights.get(cats);
        }
        return 0;
    }
}
package eu.europeana.tf.eval;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Collection;

import eu.europeana.tf.agreement.EnrichmentAnnotation;
import eu.europeana.utils.CSVWriter;
import static eu.europeana.tf.eval.EnrichmentEvaluator.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 23 Sep 2015
 */
public class EvaluationBuilder
{
    private static DecimalFormat FORMAT = new DecimalFormat("0.000");

    private EnrichmentEvaluator _relaxedEval = null;
    private EnrichmentEvaluator _strictEval  = null;

    public EvaluationBuilder()
    {
        this(newRelaxedEvaluator(), newStrictEvaluator());
    }

    public EvaluationBuilder(EnrichmentEvaluator relaxed
                           , EnrichmentEvaluator strict)
    {
        _relaxedEval = relaxed;
        _strictEval  = strict;
    }

    public void build(File sourceDir, File ouput)
    {
        EnrichmentSamplesAssembler assembler = loadAssembler(sourceDir);

        CSVWriter p = new CSVWriter(ouput);
        p.start();

        //header
        p.println("Tools", "Relaxed", "Strict", "Relaxed", "Strict"
                , "Relaxed", "Strict", "Max Recall", "Annotated Enrichments");

        Collection<EnrichmentAnnotation> aRes = assembler.getResults();
        for ( String tool : assembler.getResultClusters() )
        {
            Collection<EnrichmentAnnotation> cRes
                = assembler.getClusteredResults(tool);

            p.print(tool);
            p.print(format(_relaxedEval.calculatePrecision(cRes)));
            p.print(format(_strictEval.calculatePrecision(cRes)));
            p.print(format(_relaxedEval.calculateRecall(cRes, aRes)));
            p.print(format(_strictEval.calculateRecall(cRes, aRes)));
            p.print(format(_relaxedEval.calculateFmeasuse(cRes, aRes)));
            p.print(format(_strictEval.calculateFmeasuse(cRes, aRes)));
            p.print(format(_strictEval.calculateMaxRecall(cRes, aRes)));
            p.print(cRes.size());
            p.println();
        }
        p.println("", "", "", "", "", "", "", "Corpus size", aRes.size());
        p.end();
    }

    private String format(double d) { return FORMAT.format(d); }

    private EnrichmentSamplesAssembler loadAssembler(File sourceDir)
    {
        EnrichmentSamplesAssembler assembler = new EnrichmentSamplesAssembler();
        for ( File file : sourceDir.listFiles() )
        {
            String name = file.getName();
            if ( !name.startsWith("#") || !name.endsWith(".csv") ) { continue; }
            String id   = name.substring(0, name.length()-4);

            assembler.loadAnnotatedSample(id, file);
        }
        assembler.rebuild();
        return assembler;
    }
}

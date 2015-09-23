package eu.europeana.tf;

import static eu.europeana.tf.TaskForceConstants.*;

import java.io.IOException;

import eu.europeana.tf.agreement.AgreementRatings;
import eu.europeana.tf.agreement.AnnotationParameters.Correctness;
import eu.europeana.tf.agreement.calculator.FleissKappaCalculator;

public class RunAgreementEvaluation
{

    public static final void main(String[] args) throws IOException
    {
        AgreementRatings ratings = new AgreementRatings();
        ratings.load(DIR_AGREEMENT);

        //ratings.buildAgreementTable(FILE_AGREE_TABLE);

        FleissKappaCalculator calculator
            = new FleissKappaCalculator(Correctness.CORRECT
                                      , Correctness.INCORRECT, Correctness.UNSURE);
        calculator.calculate(ratings, FILE_AGREE_TABLE);
    }
}

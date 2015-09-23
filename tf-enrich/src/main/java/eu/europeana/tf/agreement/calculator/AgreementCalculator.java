package eu.europeana.tf.agreement.calculator;

import eu.europeana.tf.agreement.AgreementRatings;

/*
 * See: https://en.wikipedia.org/wiki/Inter-rater_reliability
 */
/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 23 Sep 2015
 */
public interface AgreementCalculator
{
    public double calculate(AgreementRatings agreement);
}

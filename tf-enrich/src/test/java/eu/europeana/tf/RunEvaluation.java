package eu.europeana.tf;

import eu.europeana.tf.eval.EvaluationBuilder;

import static eu.europeana.tf.TaskForceConstants.*;

public class RunEvaluation
{
    public static final void main(String[] main)
    {
        EvaluationBuilder builder = new EvaluationBuilder();
        builder.build(DIR_ANNOTATED, FILE_EVAL_TABLE);
    }
}

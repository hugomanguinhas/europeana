package eu.europeana.tf;

import java.io.File;

public interface TaskForceConstants
{
	public static File DIR_TASKFORCE     = new File("D:\\work\\incoming\\taskforce\\work\\");
	public static File DIR_RESULTS       = new File(DIR_TASKFORCE, "results");
	public static File DIR_EVAL          = new File(DIR_TASKFORCE, "evaluation");
	public static File DIR_EVAL_CLUSTERS = new File(DIR_EVAL     , "clusters");
	public static File DIR_GOLD_STANDARD = new File(DIR_TASKFORCE, "goldstandard");
	public static File DIR_AGREEMENT     = new File(DIR_EVAL     , "agreement");
	public static File DIR_ANNOTATED     = new File(DIR_EVAL     , "annotated");

	public static File FILE_AGREE_TABLE   = new File(DIR_AGREEMENT, "agreement_table.csv");
	public static File FILE_EVAL_TABLE    = new File(DIR_ANNOTATED, "evaluation.csv");
	public static File FILE_DATASET       = new File(DIR_TASKFORCE, "dataset\\dataset.xml");

}

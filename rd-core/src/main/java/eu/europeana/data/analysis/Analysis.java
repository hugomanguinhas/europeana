package eu.europeana.data.analysis;

import java.io.File;
import java.io.IOException;

public interface Analysis 
{
    public ObjectStat analyse(File srcList, File src, File dst) throws IOException;
}

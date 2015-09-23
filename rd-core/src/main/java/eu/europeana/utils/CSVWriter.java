package eu.europeana.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class CSVWriter
{
   private File         _file;
   private CSVPrinter   _printer;
   private CSVFormat    _format;
   private List<Object> _cache;

   public CSVWriter(File file, CSVFormat format)
   {
      _file   = file;
      _format = format;
      _cache  = new ArrayList<Object>(20);
   }

   public CSVWriter(File file) { this(file, CSVFormat.EXCEL); }

   public void start()
   {
      try {
         _printer = new CSVPrinter(new PrintStream(_file, "UTF-8"), _format);
      }
      catch (IOException e) {
         System.err.println("Error writting to file: " + _file.getName()
                        + ", reason" + e.getMessage());
      }
   }

   public void end()
   {
      if ( _printer == null ) { return; }

      try {
         try { _printer.flush(); } finally { _printer.close(); _printer = null; }
      }
      catch (IOException e) {
         System.err.println("Error writting to file: " + _file.getName()
                          + ", reason" + e.getMessage());
      }
      finally {
         _cache.clear();
      }
   }

   public void print(Object... objs) { print(Arrays.asList(objs)); }

   public void print(Collection<? extends Object> objs)
   {
      if ( _printer != null ) { _cache.addAll(objs); }
   }

   public void println(Object... objs) { println(Arrays.asList(objs)); }

   public void println(Collection<? extends Object> objs)
   {
      if ( _printer == null ) { return; }

      if ( _cache.isEmpty() ) { doPrint(objs); }
      else                    { _cache.addAll(objs); doPrint(_cache); }
   }

   public void println() { doPrint(_cache); }


   private void doPrint(Collection<? extends Object> objs)
   {
      if ( _printer == null ) { return; }

      try {
         _printer.printRecord(objs);
         _printer.flush();
      }
      catch (IOException e) {
         System.err.println("Error writting to file: " + _file.getName()
                            + ", reason" + e.getMessage());
         _printer = null;
      }
      finally {
         _cache.clear();
      }
   }
}

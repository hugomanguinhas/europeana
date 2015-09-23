package eu.europeana.ld.negotiation.checks;

import java.io.IOException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import eu.europeana.ld.negotiation.impl.NegotiationContext;

public abstract class AbstractCheck {

    protected <O> boolean check(O arg1, O... args2)
    {
       for ( O arg2 : args2 ) 
       {
          if ( arg1.equals(arg2 ) ) { return true; }
       }
       return false;
    }

    /*
    protected boolean checkRedirect(
          NegotiationContext context, String accept, FormatCheck format)
    {
       String defLoc = context.getLocation(format.getID());
       String url    = context.getURL();
       context.printMessage("Checking redirect for <", accept, ">");

       GetMethod method;
       int       iRet;
       try {
          method = newGet(url, accept);
          iRet   = context.getClient().executeMethod(method);
       }
       catch (Exception e) { context.newCannotConnect(url, e); return false; }

      if ( iRet != 303 ) {
         context.printError(
               "Expecting redirect for <", url, ">"
              , " with accept header <", accept, ">");
         return false;
      }

      String contentType = getHeader(method, "Content-Type");
      String[] prefMimes = format.getPreferredMimetypes();
      if ( !check(contentType, prefMimes) ) {
         context.printError(
               "Expecting redirect for <", url, ">"
              , " with content-type header one of ", prefMimes.toString());
         return false;
      }

      String location = getHeader(method, "Location");
      if ( !defLoc.equals(location) ) {
         context.printError(
               "Expecting redirect for <", url, ">"
              , " with location header <", defLoc, ">");
         return false;
      }

      return true;
   }
   */


}

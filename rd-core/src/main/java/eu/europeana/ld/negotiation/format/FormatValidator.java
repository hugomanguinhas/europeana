/**
 * 
 */
package eu.europeana.ld.negotiation.format;

import org.apache.commons.httpclient.HttpMethodBase;

import eu.europeana.ld.negotiation.impl.NegotiationContext;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 11 Aug 2015
 */
public interface FormatValidator
{
    public boolean validate(HttpMethodBase method, NegotiationContext ctx);
}

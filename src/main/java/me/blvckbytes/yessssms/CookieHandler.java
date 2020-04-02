package me.blvckbytes.yessssms;

import me.blvckbytes.yessssms.logging.SLLevel;
import me.blvckbytes.yessssms.logging.SimpleLogger;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.util.HashMap;
import java.util.Map;

public class CookieHandler {

  // Map to keep cookies stored, name -> value
  private Map< String, String > cookies;

  /**
   * Simulates a browser's cookie management
   */
  public CookieHandler() {
    this.cookies = new HashMap<>();
  }

  /**
   * Write and or update cookies to/from the browser session
   * @param response The server's response to extract cookie set-prompts from
   */
  public void extractAndSave( CloseableHttpResponse response ) {
    try {
      // Loop all set headers
      for( Header setter : response.getHeaders( "Set-Cookie" ) ) {
        // Get the first entry, the remaining ones are only timeouts and
        // stuff like this, irrelevant for this application
        String cookie = setter.getValue().split( ";" )[ 0 ].trim();
        String[] cData = cookie.split( "=" );

        // Damaged format received
        if( cData.length != 2 )
          return;

        // Put onto cookie map
        cookies.put( cData[ 0 ], cData[ 1 ] );
      }
    } catch ( Exception e ) {
      SimpleLogger.getInst().log( "Error while trying to extract cookies!", SLLevel.ERROR );
      SimpleLogger.getInst().log( e, SLLevel.ERROR );
    }
  }

  /**
   * Generates the Cookie value for browser requests.
   * Set this in the "Cookies" request header
   * @return Accumulated cookies
   */
  public String generate() {
    StringBuilder builder = new StringBuilder();

    // Append cookies with ; as delimiter
    for( Map.Entry< String, String > entry : this.cookies.entrySet() )
      builder.append( entry.getKey() ).append( "=" ).append( entry.getValue() ).append( "; " );

    return builder.toString();
  }
}

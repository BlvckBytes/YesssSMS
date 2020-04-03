package me.blvckbytes.yessssms;

import me.blvckbytes.yessssms.config.PropConfig;
import me.blvckbytes.yessssms.logging.SLLevel;
import me.blvckbytes.yessssms.logging.SimpleLogger;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class YesssSession {

  // URLs and IDs
  private String loginURL, loginFormID;
  private String smsURL, smsFormID, smsSendURL;
  private String loginData, password;
  private String dashboardURL;

  private CookieHandler cookies;
  private CloseableHttpClient client;
  private Map< String, Map< String, Element > > formCache;

  /**
   * Create a new session with yesss in order to send SMS through
   * their websms service
   * @param loginData Login data, either email or phone number
   * @param password Password to use with login data
   */
  public YesssSession( String loginData, String password ) {
    this.loginData = loginData;
    this.password = password;
    this.formCache = new HashMap<>();

    // Create new cookie handler and client
    this.cookies = new CookieHandler();
    this.client = HttpClients.createDefault();

    // Read needed values from config
    Properties config = PropConfig.getInstance().getProps();
    this.loginURL = config.getProperty( "yesss_loginurl" );
    this.loginFormID = config.getProperty( "yesss_loginformID" );
    this.smsURL = config.getProperty( "yesss_smsurl" );
    this.smsFormID = config.getProperty( "yesss_smsformID" );
    this.smsSendURL = config.getProperty( "yesss_sendsmsurl" );

    // Automatically log in at creation of this session
    login();
  }

  /**
   * Sends an SMS to the given number containing the provided message
   * @param number Phone-Number in the following format (ex.): +4366012345678
   * @param message Message of this sms, needs to be within constraints (max. length, view yesss for further info)
   */
  public void sendSMS( String number, String message ) {
    try {
      // Prepare form data
      List< BasicNameValuePair > paramList = prepareSMSFormData( number, message );

      // Something went wrong while trying to match up form fields to actual data
      if( paramList == null )
        throw new Exception( "Form data could not be prepared, please report this!" );

      // Perform POST to send SMS with needed headers and previously created form data
      HttpPost sendReq = new HttpPost( this.smsSendURL );
      sendReq.addHeader( "Host", "www.yesss.at" );
      sendReq.addHeader( "Cookies", this.cookies.generate() );
      sendReq.addHeader( "Referrer", this.smsSendURL );
      sendReq.setEntity( new UrlEncodedFormEntity( paramList ) );

      // Check if sending worked
      CloseableHttpResponse resp = this.client.execute( sendReq );
      int statCode = resp.getStatusLine().getStatusCode();
      if( statCode != 200 )
        throw new Exception( "Sending SMS was unsuccessful, statuscode was " + statCode );

      resp.close();
    } catch ( Exception e ) {
      SimpleLogger.getInst().log( "Error while trying to send an SMS!", SLLevel.ERROR );
      SimpleLogger.getInst().log( e, SLLevel.ERROR );
    }
  }

  /**
   * Prepares the list of name value pairs needed for a sms send POST request,
   * based on the given number and message
   * @param number Target phone-number
   * @param message Message in SMS
   * @return List of BasicNameValuePair to be used for POST
   */
  private List< BasicNameValuePair > prepareSMSFormData( String number, String message ) {
    try {
      // Note: Somehow it does not respond with the correct page on the first try.
      // Currently, I got no idea why this happes, but this workaround seems to work
      // just fine. If you got a better solution, please inform me about it.
      fetchForm( this.smsURL, this.smsFormID );
      Element smsForm = fetchForm( this.smsURL, this.smsFormID );

      // Not found on this page
      if( smsForm == null )
        throw new Exception( "Unable to find the sms-form by provided id!" );

      // Generate parameter list
      Elements inputs = smsForm.select( ".form-input input, .form-input " );
      List< BasicNameValuePair > paramList = new ArrayList<>();

      // Loop all found form inputs
      for( Element input : inputs ) {
        String name = input.attr( "name" );
        String value = null;

        // Trying to not be 100% hardcoded by using contains, so it's at least a
        // bit furure proof ( if they just change it by a small margin ).

        // The phone book feature is not yet supported by this api...
        if( name.toLowerCase().contains( "telefonbuch" ) )
          value = "-";

          // Netz ( net ) is always "a", which corresponds to ausland ( foreign country )
          // If this is done, the number dictates the net, this saves me some
          // formatting time, since their backend now does it
        else if( name.toLowerCase().contains( "netz" ) )
          value = "a";

          // Nummer ( recipient number ) always needs the + sign replaced by 00
        else if( name.toLowerCase().contains( "nummer" ) )
          value = number.replace( "+", "00" );

          // Nachricht ( message ) is the main sms body, the content
        else if( name.toLowerCase().contains( "nachricht" ) )
          value = message;

        // If this field was assigned a value successfully, set it
        if( value != null )
          paramList.add( new BasicNameValuePair( name, value ) );
      }

      return paramList;
    } catch ( Exception e ) {
      SimpleLogger.getInst().log( "Error while preparing SMS form data!", SLLevel.ERROR );
      SimpleLogger.getInst().log( e, SLLevel.ERROR );
      return null;
    }
  }

  /**
   * Log into the yesss web panel with the given credentials
   */
  private void login() {
    try {
      Element loginForm = fetchForm( this.loginURL, this.loginFormID );

      // Not found on this page
      if( loginForm == null )
        throw new Exception( "Unable to find the login-form by provided id!" );

      Elements inputs = loginForm.select( "input[type=text], input[type=password]" );

      // Structure of login prompt changed
      if( inputs.size() != 2 )
        throw new Exception( "There were more or less than two input-fields, please report this!" );

      // Make request with cookies applied
      HttpPost loginReq = new HttpPost( this.loginURL );
      loginReq.setHeader( "Cookies", this.cookies.generate() );

      // Create name value pairs with login and password, POST names are extracted from inputs
      List< BasicNameValuePair > paramList = new ArrayList<>();
      paramList.add( new BasicNameValuePair( inputs.get( 0 ).attr( "name" ), this.loginData ) );
      paramList.add( new BasicNameValuePair( inputs.get( 1 ).attr( "name" ), this.password ) );

      // Set body with URL encoded parameters
      loginReq.setEntity( new UrlEncodedFormEntity( paramList ) );
      CloseableHttpResponse resp = client.execute( loginReq );
      this.cookies.extractAndSave( resp );

      // If everything worked, there needs to be a redirect to the account manager
      if( resp.getStatusLine().getStatusCode() != 302 )
        throw new Exception( "Unsuccessful login, check your credentials in config.properties (at API jar location)!" );

      // Keep dashboard URL in memory, get from redirect
      this.dashboardURL = resp.getFirstHeader( "Location" ).getValue();
      resp.close();
    } catch ( Exception e ) {
      SimpleLogger.getInst().log( "Error while logging in!", SLLevel.ERROR );
      SimpleLogger.getInst().log( e, SLLevel.ERROR );
    }
  }

  /**
   * Fetches a form with a provided id from the provided page
   * @param url Url of the page to look into
   * @param id HTML id of the target form
   * @return Form element
   */
  private Element fetchForm( String url, String id ) {
    try {

      // Already in cache, no need to request again
      if( formCache.containsKey( url ) && formCache.get( url ).containsKey( id ) )
        return formCache.get( url ).get( id );

      // Make request with needed headers
      HttpGet formReq = new HttpGet( url );
      formReq.addHeader( "Host", "www.yesss.at" );
      formReq.addHeader( "Cookies", this.cookies.generate() );

      // View all further pages as if coming from dashboard
      // ( to not make this bot obvious for the system )
      if( this.dashboardURL != null )
        formReq.addHeader( "Referrer", this.dashboardURL );

      // Parse into document, also save cookies
      CloseableHttpResponse resp = this.client.execute( formReq );
      this.cookies.extractAndSave( resp );
      Document doc = Jsoup.parse( EntityUtils.toString( resp.getEntity() ) );

      // Extract form from provided id
      Element target = doc.selectFirst( "#" + id );

      // Only append to cache if it was successful
      if( target != null ) {
        if( !formCache.containsKey( url ) )
          formCache.put( url, new HashMap<>() );
        formCache.get( url ).put( id, target );
      }

      return target;
    } catch ( Exception e ) {
      SimpleLogger.getInst().log( "Error while getting the HTML login-form!", SLLevel.ERROR );
      SimpleLogger.getInst().log( e, SLLevel.ERROR );
      return null;
    }
  }
}

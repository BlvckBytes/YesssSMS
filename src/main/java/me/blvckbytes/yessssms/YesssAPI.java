package me.blvckbytes.yessssms;

import me.blvckbytes.yessssms.config.PropConfig;
import me.blvckbytes.yessssms.logging.SimpleLogger;

import java.util.Properties;

public class YesssAPI {

  // Instance keeper
  private static YesssSession session;

  /**
   * Initializes a new session if there is none
   * Basically call this in every publicly exposed action
   */
  private static void init() {
    // Already initialized
    if( session != null )
      return;

    // Create new session based on property list credentials
    Properties props = PropConfig.getInstance().getProps();
    session = new YesssSession(
      props.getProperty( "yesss_login" ),
      props.getProperty( "yesss_password" )
    );
  }

  /**
   * Sends an SMS to the given number containing the provided message
   * @param number Phone-Number in the following format (ex.): +4366012345678
   * @param message Message of this sms, needs to be within constraints (max. length, view yesss for further info)
   */
  public static void sendSMS( String number, String message ) {
    init();
    session.sendSMS( number, message );
  }

  /**
   * Set wether or not to use the internal logger. Caution: When the
   * logger gets disabled, you won't be able to see exceptions.
   * @param state Enabled-state of logger
   */
  public static void toggleLogging( boolean state ) {
    SimpleLogger.getInst().setEnabled( state );
  }
}

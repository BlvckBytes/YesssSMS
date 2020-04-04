package me.blvckbytes.yessssms;

import me.blvckbytes.yessssms.logging.SLLevel;
import me.blvckbytes.yessssms.logging.SimpleLogger;

public class Main {

  /**
   * Main entry point of this program, quick method to make use of this API
   * @param args Parameters for API call, first is number, second is message
   */
  public static void main( String[] args ) {
    if( args.length < 2 ) {
      SimpleLogger.getInst().log( "Please provide the arguments <number> and <message>", SLLevel.ERROR );
      return;
    }

    // Build message from parameters
    StringBuilder message = new StringBuilder( args[ 1 ] );
    for( int i = 2; i < args.length; i++ )
      message.append( " " ).append( args[ i ] );

    // Make API call
    YesssAPI.sendSMS( args[ 0 ], message.toString() );
  }
}

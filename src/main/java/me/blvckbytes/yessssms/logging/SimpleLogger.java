package me.blvckbytes.yessssms.logging;

import lombok.Setter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleLogger {

  // To keep the single instance
  private static SimpleLogger inst;

  @Setter
  private boolean enabled;
  private SimpleDateFormat dFormatter;

  /**
   * Creates a new logger and sets up the date formatter with it
   */
  private SimpleLogger() {
    this.dFormatter = new SimpleDateFormat( "dd.MM @ HH:mm" );
    this.enabled = true;
  }

  /**
   * Logs the given message to console, automatically appends
   * the prefix
   * @param input What to log
   * @param level What level to log this entry at
   */
  public void log( String input, SLLevel level ) {
    // Only log when logger is enabled
    if( this.enabled )
      System.out.println( genPrefix( level ) + input );
  }

  /**
   * Logs the given message to console, but no line break is
   * added to the end. Also appends prefix at beginning
   * @param input What to log
   * @param level What level to log this entry at
   */
  public void logInlinePrefixed( String input, SLLevel level ) {
    // Only log when logger is enabled
    if( this.enabled )
      System.out.print( genPrefix( level ) + input );
  }

  /**
   * Logs the given message to console, but no line break is
   * added to the end. No prefix is added
   * @param input What to log
   */
  public void logInlineUnprefixed( String input ) {
    // Only log when logger is enabled
    if( this.enabled )
      System.out.print( input );
  }

  /**
   * Log exceptions in a nice format directly, without having to
   * use #getMessage or something like that
   * @param e Exception to log
   * @param level Level to log it at ( usually error, of course )
   */
  public void log( Exception e, SLLevel level ) {
    StringWriter errors = new StringWriter();
    e.printStackTrace( new PrintWriter( errors ) );
    log( errors.toString(), level );
  }

  /**
   * Generate the prefix which gets appended in front of
   * every log entry, represents current point in time
   * @param level What level this log entry is at
   * @return Generated prefix
   */
  private String genPrefix( SLLevel level ) {
    String date = this.dFormatter.format( new Date( System.currentTimeMillis() ) );
    String name = "YesssSMS";
    return "[" + name + "] [" + date + ", " + level + "]: ";
  }

  /**
   * Singleton getter for this logger
   * @return Instance of class
   */
  public static SimpleLogger getInst() {
    if( inst == null )
      inst = new SimpleLogger();

    return inst;
  }
}

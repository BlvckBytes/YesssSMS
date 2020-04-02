package me.blvckbytes.yessssms.config;

import lombok.Getter;
import me.blvckbytes.yessssms.logging.SLLevel;
import me.blvckbytes.yessssms.logging.SimpleLogger;

public class PathHandler {

  @Getter
  private static String basePath;

  static {
    try {
      // Get path of execution from java's protectiondomain
      basePath = PathHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();

      // Remove file at the end of path, if exists
      if( basePath.endsWith( ".jar" ) || basePath.endsWith( "/" ) )
        basePath = basePath.substring( 0, basePath.lastIndexOf( "/" ) );
    } catch ( Exception e ) {
      SimpleLogger.getInst().log( "Error while fetching base path of this application!", SLLevel.ERROR );
      SimpleLogger.getInst().log( e, SLLevel.ERROR );
    }
  }
}

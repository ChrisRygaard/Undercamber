// CONFIDENTIAL AND PROPRIETARY TO AlpinePeak Systems, LLC
// _________________________________________________________
//
// Copyright (C) 2016-2018 AlpinePeak Systems, LLC
// All Rights Reserved.
//
// NOTICE:  All information contained herein is, and remains, the property of
// AlpinePeak Systems, LLC.  The intellectual and technical concepts contained
// herein are proprietary to AlpinePeak Systems, LLC and may be covered by
// U.S. and Foreign Patents, patents in process, and are protected by trade
// secret or copyright law.  Dissemination of this information or reproduction
// of this material is strictly forbidden unless prior written permission is
// obtained from AlpinePeak Systems, LLC.

package com.undercamber;

/**
 * A convenience enum for discovering what operating system is running.
 */
public enum OperatingSystem
{
   /**
    * Linux
    */
   LINUX( true, false, false, false ),
   /**
    * Macintosh
    */
   MACINTOSH( false, true, false, false ),
   /**
    * Windows
    */
   WINDOWS( false, false, true, false ),
   /**
    * Other
    */
   OTHER( false, false, false, true );

   /**
    * Indicates the operating system
    */
   final public static OperatingSystem CURRENT = getOperatingSystem();

   private boolean _isLinux;
   private boolean _isMacintosh;
   private boolean _isWindows;
   private boolean _isOther;

   OperatingSystem( boolean isLinux,
                    boolean isMacintosh,
                    boolean isWindows,
                    boolean isOther )
   {
      _isLinux = isLinux;
      _isMacintosh = isMacintosh;
      _isWindows = isWindows;
      _isOther = isOther;
   }

   final static OperatingSystem getOperatingSystem()
   {
      if ( java.io.File.separator.equals("\\") )
      {
         return WINDOWS;
      }
      else if ( System.getProperty("os.name").toLowerCase().contains("mac") )
      {
         return MACINTOSH;
      }
      else if ( System.getProperty("os.name").toLowerCase().contains("darwin") )
      {
         return MACINTOSH;
      }
      else if ( System.getProperty("os.name").toLowerCase().contains("linux") )
      {
         return LINUX;
      }
      else
      {
         return OTHER;
      }
   }

   final public boolean isLinux()
   {
      return _isLinux;
   }

   final public boolean isMacintosh()
   {
      return _isMacintosh;
   }

   final public boolean isWindows()
   {
      return _isWindows;
   }

   final public boolean isOther()
   {
      return _isOther;
   }
}

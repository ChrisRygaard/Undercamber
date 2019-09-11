// Copyright 2018 Rygaard Technologies, LLC
//
// Redistribution and use in source and binary forms, with or without modification, are permitted
// provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this list of
//    conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright notice, this list of
//    conditions and the following disclaimer in the documentation and/or other materials provided
//    with the distribution.
//
// 3. Neither the name of the copyright holder nor the names of its contributors may be used to
//    endorse or promote products derived from this software without specific prior written
//    permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
// IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
// FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
// CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE,  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
// OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

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
    * Indicates the operating system used at run-time.
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

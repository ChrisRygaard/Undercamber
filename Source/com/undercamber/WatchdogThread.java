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

final class WatchdogThread
{
   private boolean _continue;
   private int     _sleepDuration;
   private Thread  _thread;

   WatchdogThread( java.io.File resultsDirectory,
                   String       fileName )
   {
      _continue = true;

      _sleepDuration = 10000;

      _thread = new Thread( () -> watchdogThread(resultsDirectory,
                                                 fileName) );

      _thread.start();
   }

   final void stop()
   {
      _sleepDuration = 0;

      _continue = false;

      _thread.interrupt();
   }

   final private void watchdogThread( java.io.File resultsDirectory,
                                      String       fileName )
   {
      java.lang.management.ThreadMXBean threadMXBean;
      java.text.DateFormat              dateFormatter;
      boolean                           fileNotCreated;
      java.io.PrintStream               printStream;
      long                              threadIDs[];
      String                            message;

      threadMXBean = java.lang.management.ManagementFactory.getThreadMXBean();

      dateFormatter = new java.text.SimpleDateFormat( "YYYY-MM-dd-HH-mm-ss.SSS" );

      fileNotCreated = true;

      printStream = null;

      do
      {
         if ( _continue )
         {
            threadIDs = threadMXBean.findDeadlockedThreads();

            if ( threadIDs != null )
            {
               message = getMessage( threadIDs,
                                     threadMXBean,
                                     dateFormatter );

               System.out.println( message );

               if ( fileNotCreated )
               {
                  fileNotCreated = false;

                  printStream = getPrintStream( resultsDirectory,
                                                fileName );
               }

               if ( printStream == null )
               {
                  System.out.println( "Could not open deadlock report file for " + fileName + "." );
               }
               else
               {
                  printStream.println( message );
                  printStream.flush();
               }
            }
         }

         if ( _continue )
         {
            try
            {
               Thread.sleep( _sleepDuration );
            }
            catch ( InterruptedException ignore )
            {
            }
         }
      }
      while ( _continue );

      if ( printStream != null )
      {
         printStream.close();
      }
   }

   final private java.io.PrintStream getPrintStream( java.io.File resultsDirectory,
                                                     String       fileName )
   {
      java.io.File outputFile;

      outputFile = new java.io.File( resultsDirectory, "Deadlock" );
      outputFile.mkdirs();
      outputFile = new java.io.File( outputFile, fileName+".txt" );

      try
      {
         return new java.io.PrintStream( outputFile );
      }
      catch ( java.io.IOException ioException )
      {
         Utilities.printStackTrace( ioException );
         return null;
      }
   }

   final private String getMessage( long                              threadIDs[],
                                    java.lang.management.ThreadMXBean threadMXBean,
                                    java.text.DateFormat              dateFormatter )
   {
      java.lang.management.ThreadInfo threadInfo[];
      StringBuffer                    stringBuffer;

      threadInfo = threadMXBean.getThreadInfo( threadIDs,
                                               true,
                                               true );

      stringBuffer = new StringBuffer();

      stringBuffer.append( "The following threads are deadlocked at " ).append( dateFormatter.format(new java.util.Date()) ).append( ":" );

      for ( java.lang.management.ThreadInfo info : threadInfo )
      {
         stringBuffer.append( System.lineSeparator() ).append( "   " ).append( info.toString() );
      }

      return stringBuffer.toString();
   }
}

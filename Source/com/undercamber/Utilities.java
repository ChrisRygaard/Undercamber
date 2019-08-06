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
 * General-purpose utilities
 */
final public class Utilities
{
   final private static int                               DEBUG_MARGIN_SIZE           = 126;
   final private static String                            PADDING                     = "                                                                                                                                                                  ";
   final         static javafx.scene.input.KeyCombination ESCAPE_KEYBOARD_COMBINATION = new javafx.scene.input.KeyCodeCombination( javafx.scene.input.KeyCode.ESCAPE );
   final         static javafx.scene.input.KeyCombination ENTER_KEYBOARD_COMBINATION  = new javafx.scene.input.KeyCodeCombination( javafx.scene.input.KeyCode.ENTER );

   static final String padToRight( String text,
                                   int    width )
   {
      String result;

      if ( text.length() >= width )
      {
         return text;
      }
      else
      {
         result = text;
         while ( result.length() < width )
         {
            result += PADDING;
         }
         return result.substring( 0, width );
      }
   }

   static final String padToRight( String text,
                                   int    width,
                                   String filler )
   {
      String result;

      if ( text.length() >= width )
      {
         return text;
      }
      else
      {
         result = text;

         while ( result.length() < width )
         {
            result += filler;
         }

         return result.substring( 0, width );
      }
   }

   static final String padToLeft( String text,
                                  int    width )
   {
      String result;

      if ( text.length() >= width )
      {
         return text;
      }
      else
      {
         result = text;

         while ( result.length() < width )
         {
            result = PADDING + result;
         }

         return result.substring( result.length() - width );
      }
   }

   static final String padToLeft( String  text,
                                  int     width,
                                  String  filler )
   {
      String result;

      if ( text.length() >= width )
      {
         return text;
      }
      else
      {
         result = text;

         while ( result.length() < width )
         {
            result = filler + result;
         }

         return result.substring( result.length() - width );
      }
   }

   static final String padToLeft( int number,
                                  int width )
   {
      String result;

      result = Integer.toString( number );

      if ( result.length() >= width )
      {
         return result;
      }
      else
      {
         while ( result.length() < width )
         {
            result = PADDING + result;
         }

         return result.substring( result.length() - width );
      }
   }

   static final String padToLeft( long number,
                                  int  width )
   {
      String result;

      result = Long.toString( number );

      if ( result.length() >= width )
      {
         return result;
      }
      else
      {
         while ( result.length() < width )
         {
            result = PADDING + result;
         }

         return result.substring( result.length() - width );
      }
   }

   final static String formatList( java.util.List<String> list )
   {
      StringBuffer stringBuffer;
      int          index;

      switch ( list.size() )
      {
         case 0:
         {
            return "";
         }
         case 1:
         {
            return list.get( 0 );
         }
         case 2:
         {
            return list.get( 0 ) + " and " + list.get( 1 );
         }
         default:
         {
            stringBuffer = new StringBuffer();

            stringBuffer.append( list.get(0) );

            for ( index=1; index<(list.size()-1); index++ )
            {
               stringBuffer.append( ", " ).append( list.get(index) );
            }

            stringBuffer.append( ", and " ).append( list.get(list.size()-1) );

            return stringBuffer.toString();
         }
      }
   }

   final static javafx.geometry.Rectangle2D ensureInScreen( double windowMinX,
                                                            double windowMinY,
                                                            double windowWidth,
                                                            double windowHeight )
   {
      javafx.geometry.Rectangle2D windowBounds;
      javafx.stage.Screen         bestScreen;

      windowBounds = new javafx.geometry.Rectangle2D( windowMinX,
                                                      windowMinY,
                                                      windowWidth,
                                                      windowHeight );

      for ( javafx.stage.Screen screen : javafx.stage.Screen.getScreens() )
      {
         if ( screen.getVisualBounds().contains(windowBounds) )
         {
            return windowBounds;
         }
      }

      bestScreen = findBestScreen( windowBounds );

      if ( bestScreen == null )
      {
         return centerWindowInPrimaryScreen( windowBounds );
      }
      else
      {
         return fitInScreen( bestScreen,
                             windowBounds );
      }
   }

   final private static javafx.stage.Screen findBestScreen( javafx.geometry.Rectangle2D windowBounds )
   {
      javafx.stage.Screen bestScreen;
      double              bestFraction;
      Double              fraction;

      bestScreen = null;
      bestFraction = -Double.MAX_VALUE;
      for ( javafx.stage.Screen screen : javafx.stage.Screen.getScreens() )
      {
         fraction = getFractionIntersection( screen,
                                             windowBounds.getMinX(),
                                             windowBounds.getMinY(),
                                             windowBounds.getWidth(),
                                             windowBounds.getHeight() );
         if ( fraction != null )
         {
            if ( fraction > bestFraction )
            {
               bestScreen = screen;
               bestFraction = fraction;
            }
         }
      }

      return bestScreen;
   }

   final private static javafx.geometry.Rectangle2D centerWindowInPrimaryScreen( javafx.geometry.Rectangle2D windowBounds )
   {
      javafx.geometry.Rectangle2D screenBounds;
      double                      screenCenterX;
      double                      screenCenterY;
      double                      windowWidth;
      double                      windowHeight;

      screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();

      screenCenterX = ( screenBounds.getMinX() + screenBounds.getMaxX() ) / 2.0;
      screenCenterY = ( screenBounds.getMinY() + screenBounds.getMaxY() ) / 2.0;

      if ( windowBounds.getWidth() > screenBounds.getWidth() )
      {
         windowWidth = screenBounds.getWidth();
      }
      else
      {
         windowWidth = windowBounds.getWidth();
      }

      if ( windowBounds.getHeight() > screenBounds.getHeight() )
      {
         windowHeight = screenBounds.getHeight();
      }
      else
      {
         windowHeight = windowBounds.getHeight();
      }

      return new javafx.geometry.Rectangle2D( screenCenterX - (windowBounds.getWidth()/2.0),
                                              screenCenterY - (windowBounds.getHeight()/2.0),
                                              windowWidth,
                                              windowHeight );
   }

   final private static javafx.geometry.Rectangle2D fitInScreen( javafx.stage.Screen         screen,
                                                                 javafx.geometry.Rectangle2D windowBounds )
   {
      javafx.geometry.Rectangle2D screenBounds;
      double                      windowMinX;
      double                      windowWidth;
      double                      windowMaxX;
      double                      windowMinY;
      double                      windowHeight;
      double                      windowMaxY;

      windowMinX = windowBounds.getMinX();
      windowMinY = windowBounds.getMinY();

      screenBounds = screen.getVisualBounds();

      if ( windowBounds.getWidth() > screenBounds.getWidth() )
      {
         windowWidth = screenBounds.getWidth();
      }
      else
      {
         windowWidth = windowBounds.getWidth();
      }

      if ( windowBounds.getHeight() > screenBounds.getHeight() )
      {
         windowHeight = screenBounds.getHeight();
      }
      else
      {
         windowHeight = windowBounds.getHeight();
      }

      windowMaxX = windowMinX + windowWidth;
      windowMaxY = windowMinY + windowHeight;

      if ( windowMaxX > screenBounds.getMaxX() )
      {
         windowMaxX = screenBounds.getMaxX();
         windowMinX = windowMaxX - windowWidth;
      }

      if ( windowMinX < screenBounds.getMinX() )
      {
         windowMinX = screenBounds.getMinX();
         windowMaxX = windowMinX + windowWidth;
      }

      if ( windowMaxY > screenBounds.getMaxY() )
      {
         windowMaxY = screenBounds.getMaxY();
         windowMinY = windowMaxY - windowHeight;
      }

      if ( windowMinY < screenBounds.getMinY() )
      {
         windowMinY = screenBounds.getMinY();
         windowMaxY = windowMinY + windowHeight;
      }

      return new javafx.geometry.Rectangle2D( windowMinX,
                                              windowMinY,
                                              windowWidth,
                                              windowHeight );
   }

   final private static Double getFractionIntersection( javafx.stage.Screen screen,
                                                        double              windowMinX,
                                                        double              windowMinY,
                                                        double              windowWidth,
                                                        double              windowHeight )
   {
      javafx.geometry.Rectangle2D screenBounds;
      javafx.geometry.Rectangle2D intersection;
      double                      intersectionSize;
      double                      windowSize;

      screenBounds = screen.getVisualBounds();



      intersection = getIntersection( screenBounds,
                                      windowMinX,
                                      windowMinY,
                                      windowWidth,
                                      windowHeight );

      if ( intersection == null )
      {
         return null;
      }

      intersectionSize = intersection.getWidth() * intersection.getHeight();

      windowSize = windowWidth * windowHeight;

      return intersectionSize / windowSize;
   }

   final private static javafx.geometry.Rectangle2D getIntersection( javafx.geometry.Rectangle2D screenBounds,
                                                                     double                      windowMinX,
                                                                     double                      windowMinY,
                                                                     double                      windowWidth,
                                                                     double                      windowHeight )
   {
      double intersectionMinX;
      double intersectionMinY;
      double intersectionMaxX;
      double intersectionMaxY;

      /////////////////////////////////////////////////////////////////////////////////////////////

      if ( screenBounds.getMinX() > windowMinX )
      {
         intersectionMinX = screenBounds.getMinX();
      }
      else
      {
         intersectionMinX = windowMinX;
      }

      if ( screenBounds.getMaxX() < (windowMinX+windowWidth) )
      {
         intersectionMaxX = screenBounds.getMaxX();
      }
      else
      {
         intersectionMaxX = windowMinX + windowWidth;
      }

      /////////////////////////////////////////////////////////////////////////////////////////////

      if ( screenBounds.getMinY() > windowMinY )
      {
         intersectionMinY = screenBounds.getMinY();
      }
      else
      {
         intersectionMinY = windowMinY;
      }

      if ( screenBounds.getMaxY() < (windowMinY+windowHeight) )
      {
         intersectionMaxY = screenBounds.getMaxY();
      }
      else
      {
         intersectionMaxY = windowMinY + windowHeight;
      }

      /////////////////////////////////////////////////////////////////////////////////////////////

      if ( intersectionMaxX <= intersectionMinX )
      {
         return null;
      }

      if ( intersectionMaxY <= intersectionMinY )
      {
         return null;

      }

      /////////////////////////////////////////////////////////////////////////////////////////////

      return new javafx.geometry.Rectangle2D( intersectionMinX,
                                              intersectionMinY,
                                              intersectionMaxX - intersectionMinX,
                                              intersectionMaxY - intersectionMinY );
   }

   /**
    * Get the name of the method that calls this method.
    *
    * @return The method name
    */
   final public static String whatMethodAmI()
   {
      StackTraceElement stackTrace[];

      stackTrace = Thread.currentThread().getStackTrace();

      return stackTrace[2].getClassName() + "." + stackTrace[2].getMethodName() + "()";
   }

   /**
    * Get the method name and line number of the source code line that calls this method.
    *
    * @return The current method name and line number
    */
   final public static String whatLineAmI()
   {
      StackTraceElement stackTrace[];

      stackTrace = Thread.currentThread().getStackTrace();

      return stackTrace[2].getClassName() + "." + stackTrace[2].getMethodName() + "() <line " + stackTrace[2].getLineNumber() + ">";
   }

   /**
    * Get the method name and line number that called the method that calls this method
    *
    * @return The method name and line number
    */
   final public static String whatLineCalledMe()
   {
      StackTraceElement stackTrace[];

      stackTrace = Thread.currentThread().getStackTrace();

      return stackTrace[3].getClassName() + "." + stackTrace[3].getMethodName() + "() <line " + stackTrace[3].getLineNumber() + ">";
   }

   /**
    * Print a message to <tt>System.out</tt>, decorated with the method name and source code line number that calls this method.
    *
    * @param note
    *        The note to be displayed
    */
   final public static void printDebugLine( String note )
   {
      System.out.println( padToRight(" - "+whatLineCalledMe(),DEBUG_MARGIN_SIZE) + ":  " + note );
   }

   final static String escapeForXML( String string )
   {
      return string.replaceAll( "&", "&amp;" ).replaceAll( "<", "&lt;" ).replaceAll( ">", "&gt;" ).replaceAll( "\"", "&quot;" ).replaceAll( "'", "&apos;" );
   }

   final static String isLegalName( String name )
   {
      StringBuffer stringBuffer;
      boolean      errorEncountered;
      int          index;

      stringBuffer = new StringBuffer();
      errorEncountered = stringBuffer.length() < 0;

      if ( name.length() == 0 )
      {
         if ( errorEncountered )
         {
            stringBuffer.append( ", " );
         }
         stringBuffer.append( "names cannot be zero length" );
         errorEncountered = true;
      }
      else
      {
         if ( isIllegalCharacter(name.charAt(0),false) )
         {
            if ( errorEncountered )
            {
               stringBuffer.append( ", " );
            }
            stringBuffer.append( "first character must be alphabetical or underscore" );
            errorEncountered = true;
         }

         for ( index=1; index<name.length(); index++ )
         {
            if ( isIllegalCharacter(name.charAt(index),true) )
            {
               if ( errorEncountered )
               {
                  stringBuffer.append( ", " );
               }
               stringBuffer.append( "names must be alphanumerical or underscore" );
               errorEncountered = true;
            }
         }
      }

      if ( errorEncountered )
      {
         return stringBuffer.toString();
      }
      else
      {
         return null;
      }
   }

   final private static boolean isIllegalCharacter( char    character,
                                                    boolean canBeDigit )
   {
      if ( character == '_' )
      {
         return false;
      }
      else if ( Character.isLetter(character) )
      {
         return false;
      }
      else if ( canBeDigit && (Character.isDigit(character)) )
      {
         return false;
      }
      else
      {
         return true;
      }
   }

   /**
    * Generate a string version of a formatted stack trace
    *
    * @param throwable
    *        The <tt>Throwable</tt> for which the stack trace is to be generated
    *
    * @return The formatted stack trace.
    */
   final public static String getStackTrace( Throwable throwable )
   {
      StringBuffer stringBuffer;

      stringBuffer = new StringBuffer();

      printStackTrace( throwable,
                       "",
                       stringBuffer,
                       false );

      return stringBuffer.toString();
   }

   /**
    * Print (to <tt>System.out</tt>) a formatted stack trace to the line that calls this method.  <p>
    *
    * The output will be contiguous, even in a multi-threaded environment.
    */
   final public static void printStackTrace()
   {
      printStackTrace( (String)null,
                       System.out );
   }

   /**
    * Print (to <tt>System.out</tt>) title and a formatted stack trace to the line that calls this method.  <p>
    *
    * The output will be contiguous, even in a multi-threaded environment.
    *
    * @param title
    *        The title
    */
   final public static void printStackTrace( String title )
   {
      printStackTrace( title,
                       System.out );
   }

   /**
    * Print (to the specified <tt>PrintStream</tt>) a formatted stack trace to the line that calls this method.  <p>
    *
    * The output will be contiguous, even in a multi-threaded environment.
    *
    * @param printStream
    *        The PrintStream to which the stack trace is to be printed
    */
   final public static void printStackTrace( java.io.PrintStream printStream )
   {
      printStackTrace( (String)null,
                       printStream );
   }

   /**
    * Print (to the specified <tt>PrintStream</tt>) a title and a formatted stack trace to the line that calls this method.  <p>
    *
    * The output will be contiguous, even in a multi-threaded environment.
    *
    * @param title
    *        The title
    * @param printStream
    *        The PrintStream to which the stack trace is to be printed
    */
   final public static void printStackTrace( String              title,
                                             java.io.PrintStream printStream )
   {
      StringBuffer      stringBuffer;
      StackTraceElement stackTrace[];
      ColumnWidths      columnWidths;
      int               index;
      int               maximumClassNameLength;
      int               maximumMethodNameLength;
      int               maximumFileNameLength;
      int               maximumLineNumberLength;

      stringBuffer = new StringBuffer();

      if ( title == null )
      {
         stringBuffer.append( "Stack trace:" ).append( System.lineSeparator() );
      }
      else
      {
         stringBuffer.append( "Stack trace (" ).append( title ).append( "):" ).append( System.lineSeparator() );
      }

      stackTrace = Thread.currentThread().getStackTrace();

      columnWidths = new ColumnWidths( 5, 6, 4, 4 );
      for ( index=3; index<stackTrace.length; index++ )
      {
         columnWidths.update( stackTrace[index],
                              "" );
      }

      maximumClassNameLength = columnWidths.getMaximumClassNameLength();
      maximumMethodNameLength = columnWidths.getMaximumMethodNameLength();
      maximumFileNameLength = columnWidths.getMaximumFileNameLength();
      maximumLineNumberLength = columnWidths.getMaximumLineNumberLength();

      stringBuffer.append( "   " ).append( padToRight("Class",maximumClassNameLength) ).append( " " ).append( padToRight("Method",maximumMethodNameLength) ).append( " " ).append( padToRight("File",maximumFileNameLength) ).append( " " ).append( padToLeft("Line",maximumLineNumberLength) ).append( System.lineSeparator() );
      stringBuffer.append( "   " ).append( padToRight("-----",maximumClassNameLength) ).append( " " ).append( padToRight("------",maximumMethodNameLength) ).append( " " ).append( padToRight("----",maximumFileNameLength) ).append( " " ).append( padToLeft("----",maximumLineNumberLength) ).append( System.lineSeparator() );

      for ( index=3; index<stackTrace.length; index++ )
      {
         writeLine( stackTrace[index],
                    "   ",
                    "",
                    maximumClassNameLength,
                    maximumMethodNameLength,
                    maximumFileNameLength,
                    maximumLineNumberLength,
                    stringBuffer );
      }

      stringBuffer.append( "   " ).append( padToRight("",maximumClassNameLength+maximumMethodNameLength+maximumFileNameLength+maximumLineNumberLength+3,"-") ).append( System.lineSeparator() );

      printStream.print( stringBuffer.toString() );
   }

   /**
    * Print (to <tt>System.out</tt>) a formatted stack trace for the provided <tt>Throwable</tt>.  <p>
    *
    * The output will be contiguous, even in a multi-threaded environment.
    *
    * @param throwable
    *        The <tt>Throwable</tt> for which a stack trace is to be printed.
    */
   final public static void printStackTrace( Throwable throwable )
   {
      printStackTrace( throwable,
                       "",
                       System.out,
                       false );
   }

   final static void printStackTrace( Throwable throwable,
                                      String    margin )
   {
      printStackTrace( throwable,
                       margin,
                       System.out,
                       false );
   }

   final static void printStackTrace( Throwable           throwable,
                                      String              margin,
                                      java.io.PrintStream printStream )
   {
      StringBuffer stringBuffer;

      stringBuffer = new StringBuffer();

      printStackTrace( throwable,
                       margin,
                       stringBuffer,
                       false );

      printStream.print( stringBuffer.toString() );
   }

   final static void printStackTrace( Throwable    throwable,
                                      String       margin,
                                      StringBuffer stringBuffer )
   {
      printStackTrace( throwable,
                       margin,
                       stringBuffer,
                       false );
   }

   final static void printStackTrace( Throwable           throwable,
                                      String              margin,
                                      java.io.PrintStream printStream,
                                      boolean             escapeForXML )
   {
      StringBuffer stringBuffer;

      stringBuffer = new StringBuffer();

      printStackTrace( throwable,
                       margin,
                       stringBuffer,
                       escapeForXML );

      printStream.print( stringBuffer.toString() );
   }

   final static void printStackTrace( Throwable    throwable,
                                      String       margin,
                                      StringBuffer stringBuffer,
                                      boolean      escapeForXML )

   {
      ColumnWidths columnWidths;
      int          maximumClassNameLength;
      int          maximumMethodNameLength;
      int          maximumFileNameLength;
      int          maximumLineNumberLength;
      int          index;

      if ( throwable.getMessage() == null )
      {
         stringBuffer.append( margin ).append( "-- " ).append( throwable.getClass().getName() ).append( System.lineSeparator() );
      }
      else if ( throwable.getMessage().length() == 0 )
      {
         stringBuffer.append( margin ).append( "-- " ).append( throwable.getClass().getName() ).append( System.lineSeparator() );
      }
      else
      {
         stringBuffer.append( margin ).append( "-- " ).append( throwable.getClass().getName() ).append( ":  " ).append( throwable.getMessage() ).append( System.lineSeparator() );
      }

      columnWidths = new ColumnWidths( 0, 0, 0, 0 );

      updateColumnWidths( throwable,
                          "",
                          columnWidths );

      maximumClassNameLength = columnWidths.getMaximumClassNameLength();
      maximumMethodNameLength = columnWidths.getMaximumMethodNameLength();
      maximumFileNameLength = columnWidths.getMaximumFileNameLength();
      maximumLineNumberLength = columnWidths.getMaximumLineNumberLength();

      printStackTrace( throwable,
                       margin + "   ",
                       "",
                       maximumClassNameLength,
                       maximumMethodNameLength,
                       maximumFileNameLength,
                       maximumLineNumberLength,
                       stringBuffer );

      stringBuffer.append( margin ).append( padToRight("",maximumClassNameLength+maximumMethodNameLength+maximumFileNameLength+maximumLineNumberLength+6,"-") ).append( System.lineSeparator() );

      if ( escapeForXML )
      {
         index = stringBuffer.indexOf( "&" );
         while ( index != -1 )
         {
            stringBuffer.replace( index, index+1, "&amp;" );
            index = stringBuffer.indexOf( "&", index+4 );
         }

         index = stringBuffer.indexOf( "<" );
         while ( index != -1 )
         {
            stringBuffer.replace( index, index+1, "&lt;" );
            index = stringBuffer.indexOf( "<", index+3 );
         }

         index = stringBuffer.indexOf( ">" );
         while ( index != -1 )
         {
            stringBuffer.replace( index, index+1, "&gt;" );
            index = stringBuffer.indexOf( ">", index+3 );
         }

         index = stringBuffer.indexOf( "\"" );
         while ( index != -1 )
         {
            stringBuffer.replace( index, index+1, "&quot;" );
            index = stringBuffer.indexOf( "\"", index+5 );
         }

         index = stringBuffer.indexOf( "'" );
         while ( index != -1 )
         {
            stringBuffer.replace( index, index+1, "&apos;" );
            index = stringBuffer.indexOf( "'", index+5 );
         }
      }
   }

   final private static void printStackTrace( Throwable    throwable,
                                              String       margin,
                                              String       indentation,
                                              int          maximumClassNameLength,
                                              int          maximumMethodNameLength,
                                              int          maximumFileNameLength,
                                              int          maximumLineNumberLength,
                                              StringBuffer stringBuffer )
   {
      StackTraceElement stackTrace[];
      Throwable         cause;
      Throwable         suppressedExceptions[];

      stackTrace = throwable.getStackTrace();

      if ( stackTrace != null )
      {
         for (StackTraceElement stackTraceElement : stackTrace )
         {
            writeLine( stackTraceElement,
                       margin,
                       indentation,
                       maximumClassNameLength,
                       maximumMethodNameLength,
                       maximumFileNameLength,
                       maximumLineNumberLength,
                       stringBuffer );
         }
      }

      cause = throwable.getCause();
      if ( cause != null )
      {
         if ( cause.getMessage() == null )
         {
            stringBuffer.append( margin ).append( indentation ).append( "- Caused by " ).append( cause.getClass().getName() ).append( System.lineSeparator() );
         }
         else if ( cause.getMessage().length() == 0 )
         {
            stringBuffer.append( margin ).append( indentation ).append( "- Caused by " ).append( cause.getClass().getName() ).append( System.lineSeparator() );
         }
         else
         {
            stringBuffer.append( margin ).append( indentation ).append( "- Caused by " ).append( cause.getClass().getName() ).append( ":  " ).append( cause.getMessage() ).append( System.lineSeparator() );
         }
         printStackTrace( cause,
                          margin,
                          indentation + "   ",
                          maximumClassNameLength,
                          maximumMethodNameLength,
                          maximumFileNameLength,
                          maximumLineNumberLength,
                          stringBuffer );
      }

      suppressedExceptions = throwable.getSuppressed();
      for ( Throwable suppressedException : suppressedExceptions )
      {
         stringBuffer.append( margin ).append( indentation ).append( "         * Suppressed Exception:" ).append( System.lineSeparator() );
         printStackTrace( suppressedException,
                          margin,
                          indentation + "         ",
                          maximumClassNameLength,
                          maximumMethodNameLength,
                          maximumFileNameLength,
                          maximumLineNumberLength,
                          stringBuffer );
      }
   }

   final private static void updateColumnWidths( Throwable    throwable,
                                                 String       indentation,
                                                 ColumnWidths columnWidths )
   {
      StackTraceElement stackTrace[];
      Throwable         cause;
      Throwable         suppressedExceptions[];

      stackTrace = throwable.getStackTrace();
      for ( StackTraceElement stackTraceElement : stackTrace )
      {
         columnWidths.update( stackTraceElement,
                              indentation );
      }

      cause = throwable.getCause();
      if ( cause != null )
      {
         updateColumnWidths( cause,
                             indentation + "   ",
                             columnWidths );
      }

      suppressedExceptions = throwable.getSuppressed();
      for ( Throwable suppressedException : suppressedExceptions )
      {
         updateColumnWidths( suppressedException,
                             indentation + "         ",
                             columnWidths );
      }
   }

   final private static void writeLine( StackTraceElement stackTraceElement,
                                        String            margin,
                                        String            indentation,
                                        int               maximumClassNameLength,
                                        int               maximumMethodNameLength,
                                        int               maximumFileNameLength,
                                        int               maximumLineNumberLength,
                                        StringBuffer      stringBuffer )
   {
      String className;
      String methodName;
      String fileName;
      String lineNumber;

      className = stackTraceElement.getClassName();
      if ( className == null )
      {
         className = indentation + "<unknown>";
      }
      else
      {
         className = indentation + className;
      }

      methodName = stackTraceElement.getMethodName();
      if ( methodName == null )
      {
         methodName = "<unknown>";
      }
      else
      {
         methodName += "()";
      }

      fileName = stackTraceElement.getFileName();
      if ( fileName == null )
      {
         fileName = "<unknown>";
      }

      lineNumber = Integer.toString( stackTraceElement.getLineNumber() );
      if ( lineNumber.equals("-1") )
      {
         lineNumber = "<unknown>";
      }

      stringBuffer.append( margin ).append( padToRight(className,maximumClassNameLength) ).append( " " ).append( padToRight(methodName,maximumMethodNameLength) ).append( " " ).append( padToRight(fileName,maximumFileNameLength) ).append( " " ).append( padToLeft(lineNumber,maximumLineNumberLength) ).append( System.lineSeparator() );
   }

   final private static class ColumnWidths
   {
      private int _maximumClassNameLength;
      private int _maximumMethodNameLength;
      private int _maximumFileNameLength;
      private int _maximumLineNumberLength;

      private ColumnWidths( int minimumClassNameLength,
                            int minimumMethodNameLength,
                            int minimumFileNameLength,
                            int minimumLineNumberLength )
      {
         _maximumClassNameLength = minimumClassNameLength;
         _maximumMethodNameLength = minimumMethodNameLength;
         _maximumFileNameLength = minimumFileNameLength;
         _maximumLineNumberLength = minimumLineNumberLength;
      }

      final private void update( StackTraceElement stackTraceElement,
                                 String            indentation )
      {
         String name;

         name = stackTraceElement.getClassName();
         if ( name == null )
         {
            name = indentation + "<unknown>";
         }
         else
         {
            name = indentation + name;
         }
         if ( name.length() > _maximumClassNameLength )
         {
            _maximumClassNameLength = name.length();
         }

         name = stackTraceElement.getMethodName();
         if ( name == null )
         {
            name = "<unknown>";
         }
         else
         {
            name += "()";
         }
         if ( name.length() > _maximumMethodNameLength )
         {
            _maximumMethodNameLength = name.length();
         }

         name = stackTraceElement.getFileName();
         if ( name == null )
         {
            name = "<unknown>";
         }
         if ( name.length() > _maximumFileNameLength )
         {
            _maximumFileNameLength = name.length();
         }

         name = Integer.toString( stackTraceElement.getLineNumber() );
         if ( name.equals("-1") )
         {
            name = "<unknown>";
         }
         if ( name.length() > _maximumLineNumberLength )
         {
            _maximumLineNumberLength = name.length();
         }
      }

      final private int getMaximumClassNameLength()
      {
         return _maximumClassNameLength;
      }

      final private int getMaximumMethodNameLength()
      {
         return _maximumMethodNameLength;
      }

      final private int getMaximumFileNameLength()
      {
         return _maximumFileNameLength;
      }

      final private int getMaximumLineNumberLength()
      {
         return _maximumLineNumberLength;
      }
   }
}

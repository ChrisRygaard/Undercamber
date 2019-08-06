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
 * A convenience class.  Use this to expand strings with environment variables.  <p>
 *
 * This is similar to parameter expansion in Linux scripts.<p>
 *
 * Example:<br>
 * <ol>
 *    <li>
 *       Assume there is an environment variable named <tt>MY_ENVIRONMENT_VARIABLE</tt>, and its value is <tt>MyString</tt>
 *    </li>
 *    <li>
 *       Assume the string <tt>not${MY_ENVIRONMENT_VARIABLE}AtAll</tt> is passed to the {@link #expandString} method
 *    </li>
 *    <li>
 *       Then the {@link #expandString} will return the string <tt>notMyStringAtAll</tt>.
 *    </li>
 * </ol><br>
 *
 * The characters $, {, and } can be escaped with the \ character.
 */
final public class StringExpander
{
   /**
    * Expand a string.  <p>
    *
    * This is equivalent to<pre>
    *    expandParameter( parameter,
    *                     ValidationLevel.ERROR )</pre>
    *
    * @param parameter
    *        The string to be expanded.
    *
    * @return The expanded string.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion
    */
   final public static String expandString( String parameter )
      throws InternalException,
             UserError
   {
      return expandString( parameter,
                           ValidationLevel.ERROR );
   }

   /**
    * Expand a string.  <p>
    *
    * @param parameter
    *        The string to be expanded.
    *
    * @param validationLevel
    *        Indicates what should occur if an embedded environment variable is not found:
    *        <table summary="argument table">
    *           <tr>
    *              <th>&nbsp;&nbsp;&nbsp;<u>Value</u></th>
    *              <th>&nbsp;&nbsp;&nbsp;<u>Description</u></th>
    *           </tr>
    *           <tr>
    *              <td>&nbsp;&nbsp;&nbsp;<tt>NONE</tt></td>
    *              <td>&nbsp;&nbsp;&nbsp;Quietly replace the embedded environment variable with a zero-length string</td>
    *           </tr>
    *           <tr>
    *              <td>&nbsp;&nbsp;&nbsp;<tt>WARNING</tt></td>
    *              <td>&nbsp;&nbsp;&nbsp;Quietly replace the embedded environment variable with a zero-length string, print a warning to System.out, and proceed</td>
    *           </tr>
    *           <tr>
    *              <td>&nbsp;&nbsp;&nbsp;<tt>ERROR</tt></td>
    *              <td>&nbsp;&nbsp;&nbsp;Throw a UserError exception</td>
    *           </tr>
    *        </table>
    *
    * @return The expanded string.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion
    */
   final public static String expandString( String          parameter,
                                            ValidationLevel validationLevel )
      throws UserError,
             InternalException
   {
      StringBuffer                     stringBuffer;
      java.util.List<DelimiterIndices> delimiterIndiceses;
      int                              previousIndex;
      String                           stringToken;
      String                           cleanedString;
      String                           expandedString;

      stringBuffer = new StringBuffer();

      delimiterIndiceses = getDelimiterIndices( parameter );

      previousIndex = 0;

      for ( DelimiterIndices delimiterIndices : delimiterIndiceses )
      {
         stringToken = parameter.substring( previousIndex, delimiterIndices.getBeginIndex()-2 );
         cleanedString = stringToken.replaceAll( "\\Q\\$\\E", "\\$" ).replaceAll( "\\Q\\{\\E", "{" ).replaceAll( "\\Q\\}\\E", "}" );
         stringBuffer.append( cleanedString );
         stringToken = parameter.substring( delimiterIndices.getBeginIndex(), delimiterIndices.getEndIndex() );
         cleanedString = stringToken.replaceAll( "\\Q\\$\\E", "\\$" ).replaceAll( "\\Q\\{\\E", "{" ).replaceAll( "\\Q\\}\\E", "}" );
         expandedString = System.getenv( cleanedString );
         if ( expandedString == null )
         {
            switch ( validationLevel )
            {
               case WARNING:
               {
                  System.out.println( "Warning:  Could not find environment variable \"" + cleanedString + "\"" );
                  break;
               }
               case ERROR:
               {
                  throw new UserError( "Error:  Could not find environment variable \"" + cleanedString + "\"" );
               }
               default:
               {
               }
            }
         }
         else
         {
            stringBuffer.append( expandedString );
         }
         previousIndex = delimiterIndices.getEndIndex() + 1;
      }

      if ( previousIndex < parameter.length() )
      {
         stringToken = parameter.substring( previousIndex, parameter.length() );
         cleanedString = stringToken.replaceAll( "\\Q\\$\\E", "\\$" ).replaceAll( "\\Q\\{\\E", "{" ).replaceAll( "\\Q\\}\\E", "}" );
         stringBuffer.append( cleanedString );
      }

      return stringBuffer.toString();
   }

   /**
    * Escape a string so it will not be expanded when passed to {@link #expandString(String)} or {@link #expandString(String,ValidationLevel)}.
    *
    * @param text
    *        The string to be expanded
    *
    * @return String
    *         The escaped string
    */
   final public static String escapeString( String text )
   {
      return text.replace( "$", "\\$" ).replace( "{", "\\{" ).replace( "}", "\\}" );
   }

   final private static java.util.List<DelimiterIndices> getDelimiterIndices( String parameter )
      throws UserError,
             InternalException
   {
      java.util.List<DelimiterIndices> delimiterIndices;
      int                              startingIndex;
      ScanState                        scanState;
      int                              index;
      char                             character;

      delimiterIndices = new java.util.ArrayList<DelimiterIndices>();

      startingIndex = -1;

      scanState = ScanState.SCANNING_FOR_DOLLAR_SIGN;

      for ( index=0; index<parameter.length(); index++ )
      {
         character = parameter.charAt( index );
         switch ( scanState )
         {
            case SCANNING_FOR_DOLLAR_SIGN:
            {
               if ( character == '\\' )
               {
                  scanState = ScanState.SKIPPING_AFTER_BACKSLASH_WHEN_SCANNING_FOR_DOLLAR_SIGN;
               }
               else if ( character == '$' )
               {
                  scanState = ScanState.SCANNING_FOR_OPENING_BRACE;
               }
               break;
            }
            case SCANNING_FOR_OPENING_BRACE:
            {
               if ( character == '{' )
               {
                  startingIndex = index+1;
                  scanState = ScanState.SCANNING_FOR_CLOSING_BRACE;
               }
               else
               {
                  scanState = ScanState.SCANNING_FOR_DOLLAR_SIGN;
               }
               break;
            }
            case SKIPPING_AFTER_BACKSLASH_WHEN_SCANNING_FOR_DOLLAR_SIGN:
            {
               scanState = ScanState.SCANNING_FOR_DOLLAR_SIGN;
               break;
            }
            case SCANNING_FOR_CLOSING_BRACE:
            {
               if ( character == '\\' )
               {
                  scanState = ScanState.SKIPPING_AFTER_BACKSLASH_WHEN_SCANNING_FOR_CLOSING_BRACE;
               }
               else if ( character == '}' )
               {
                  delimiterIndices.add( new DelimiterIndices(startingIndex,
                                                             index) );
                  scanState = ScanState.SCANNING_FOR_DOLLAR_SIGN;
               }
               break;
            }
            case SKIPPING_AFTER_BACKSLASH_WHEN_SCANNING_FOR_CLOSING_BRACE:
            {
               scanState = ScanState.SCANNING_FOR_CLOSING_BRACE;
               break;
            }
            default:
            {
               throw new InternalException( "Internal error:  Unrecognized scan state:  " + scanState );
            }
         }
      }

      if ( scanState.isUnclosed() )
      {
         throw new UserError( "Error:  Unclosed brace.  Parameter = <" + parameter + ">" );
      }

      return delimiterIndices;
   }

   private enum ScanState
   {
      SCANNING_FOR_DOLLAR_SIGN                                ( false ),
      SCANNING_FOR_OPENING_BRACE                              ( false ),
      SCANNING_FOR_CLOSING_BRACE                              ( true  ),
      SKIPPING_AFTER_BACKSLASH_WHEN_SCANNING_FOR_DOLLAR_SIGN  ( false ),
      SKIPPING_AFTER_BACKSLASH_WHEN_SCANNING_FOR_CLOSING_BRACE( true  );

      boolean _isUnclosed;

      ScanState( boolean isUnclosed )
      {
         _isUnclosed = isUnclosed;
      }

      final boolean isUnclosed()
      {
         return _isUnclosed;
      }
   }

   final private static class DelimiterIndices
   {
      private int _beginIndex;
      private int _endIndex;

      DelimiterIndices( int beginIndex,
                        int endIndex )
      {
         _beginIndex = beginIndex;
         _endIndex = endIndex;
      }

      final int getBeginIndex()
      {
         return _beginIndex;
      }

      final int getEndIndex()
      {
         return _endIndex;
      }
   }
}

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

final class Message
   extends Exception
{
   final private static long serialVersionUID = 0;

   private String _message[];

   Message( String... message )
   {
      _message = message;
   }

   Message( java.util.List<String> message )
   {
      _message = new String[ message.size() ];

      _message = message.toArray( _message );
   }

   final void printMessage( String              margin,
                            java.io.PrintStream printStream )
   {
      StringBuffer stringBuffer;

      stringBuffer = new StringBuffer();

      printMessage( margin,
                    stringBuffer );

      printStream.print( stringBuffer.toString() );
   }

   final void printMessage( String              margin,
                            java.io.PrintStream printStream,
                            boolean             escapeForXML )
   {
      StringBuffer stringBuffer;

      stringBuffer = new StringBuffer();

      printMessage( margin,
                    stringBuffer );

      if ( escapeForXML )
      {
         printStream.print( stringBuffer.toString().replaceAll("&","&amp;").replaceAll("<","&lt;").replaceAll(">","&gt;").replaceAll("\"","&quot;").replaceAll("'","&apos;") );
      }
      else
      {
         printStream.print( stringBuffer.toString() );
      }
   }

   final void printMessage( String       margin,
                            StringBuffer stringBuffer )
   {
      int index;

      if ( _message.length > 0 )
      {
         stringBuffer.append( margin ).append( "- " ).append( _message[0] ).append( System.lineSeparator() );

         for ( index=1; index<_message.length; index++ )
         {
            stringBuffer.append( margin ).append( "  " ).append( _message[index] ).append( System.lineSeparator() );
         }
      }
   }

   final String toString( String margin )
   {
      StringBuffer stringBuffer;

      stringBuffer = new StringBuffer();

      printMessage( margin,
                    stringBuffer );

      return stringBuffer.toString();
   }
}

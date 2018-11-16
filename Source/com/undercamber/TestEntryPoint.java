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

final class TestEntryPoint
{
   private java.util.List<String> _testSetNames;
   private String                 _className;
   private String                 _methodName;
   private String                 _arguments;

   TestEntryPoint( String  testSetNames,
                   String  className,
                   String  methodName )
   {
      this( testSetNames,
            className,
            methodName,
            null );
   }

   TestEntryPoint( String testSetNames,
                   String className,
                   String methodName,
                   String arguments )
   {
      java.util.StringTokenizer stringTokenizer;

      if ( testSetNames == null )
      {
         _testSetNames = null;
      }
      else
      {
         _testSetNames = new java.util.ArrayList<String>();
         stringTokenizer = new java.util.StringTokenizer( testSetNames, "," );
         while ( stringTokenizer.hasMoreTokens() )
         {
            _testSetNames.add( stringTokenizer.nextToken() );
         }
      }
      _className = className;
      _methodName = methodName;
      _arguments = arguments;
   }

   final boolean matches( TestData testData )
   {
      if ( !(_className.equals(testData.getCallingClassName())) )
      {
         return false;
      }

      if ( !(_methodName.equals(testData.getCallingMethodName())) )
      {
         return false;
      }

      if ( (_arguments==null) != (testData.getArguments()==null) )
      {
         return false;
      }

      if ( _arguments != null )
      {
         if ( !(_arguments.equals(testData.getArguments())) )
         {
            return false;
         }
      }

      return true;
   }

   final public String[] getTestSetNames()
   {
      String testSetNames[];

      if ( _testSetNames == null )
      {
         return null;
      }
      else
      {
         testSetNames = new String[ _testSetNames.size() ];
         testSetNames = _testSetNames.toArray( testSetNames );

         return testSetNames;
      }
   }

   final public String toString()
   {
      StringBuffer stringBuffer;
      int          maximumIndex;
      int          index;

      if ( _testSetNames == null )
      {
         return _className + "." + _methodName + "(" + (_arguments==null?"":_arguments) + ")";
      }
      else
      {
         if ( _testSetNames.size() == 0 )
         {
            return _className + "." + _methodName + "(" + (_arguments==null?"":_arguments) + ") in no test set (huh?)";
         }
         else if ( _testSetNames.size() == 1 )
         {
            return _className + "." + _methodName + "(" + (_arguments==null?"":_arguments) + ") in test set " + _testSetNames.get(0);
         }
         else if ( _testSetNames.size() == 2 )
         {
            return _className + "." + _methodName + "(" + (_arguments==null?"":_arguments) + ") in test sets " + _testSetNames.get(0) + " and " + _testSetNames.get(1);
         }
         else
         {
            stringBuffer = new StringBuffer();

            stringBuffer.append( _className ).append( "." ).append( _methodName ).append( "(" );

            if ( _arguments != null )
            {
               stringBuffer.append( _arguments );
            }

            stringBuffer.append( ") in test sets " ).append( _testSetNames.get(0) );

            maximumIndex = _testSetNames.size() - 1;

            for ( index=1; index<maximumIndex; index++ )
            {
               stringBuffer.append( ", " ).append( _testSetNames.get(index) );
            }

            stringBuffer.append( ", and " ).append( _testSetNames.get(maximumIndex) );

            return stringBuffer.toString();
         }
      }
   }
}

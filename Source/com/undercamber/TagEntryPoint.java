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

final class TagEntryPoint
{
   private java.util.List<String> _testSetNames;
   private java.util.List<String> _tagNames;

   TagEntryPoint( String testSetNames,
                  String tagNames )
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

      _tagNames = new java.util.ArrayList<String>();
      stringTokenizer = new java.util.StringTokenizer( tagNames, "," );
      while ( stringTokenizer.hasMoreTokens() )
      {
         _tagNames.add( stringTokenizer.nextToken() );
      }
   }

   final String[] getTestSetNames()
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

   final String[] getTagNames()
   {
      String tagNames[];

      tagNames = new String[ _tagNames.size() ];
      tagNames = _tagNames.toArray( tagNames );

      return tagNames;
   }

   final void showErrorMessage( Undercamber undercamber )
   {
      StringBuffer           stringBuffer;
      int                    index;
      java.util.Set<String>  availableTagSet;
      TestSet                testSet;
      java.util.List<String> availableTagList;

      stringBuffer = new StringBuffer();

      if ( _tagNames.size() == 1 )
      {
         stringBuffer.append( "Tag " ).append( _tagNames.get(0) );
      }
      else
      {
         stringBuffer.append( "Tags " ).append( Utilities.formatList(_tagNames) );
      }

      stringBuffer.append( " (specified on command line) not found" );

      if ( _testSetNames == null )
      {
         stringBuffer.append( "." );
      }
      else if ( _testSetNames.size() == 1 )
      {
         stringBuffer.append( " in test set " ).append( _testSetNames.get(0) );
      }
      {
         stringBuffer.append( " in test sets " ).append( Utilities.formatList(_testSetNames) ).append( "." );
      }

      availableTagSet = new java.util.HashSet<String>();

      if ( _testSetNames == null )
      {
         availableTagSet.addAll( undercamber.getAllTags() );
      }
      else
      {
         for ( String testSetName : _testSetNames )
         {
            testSet = undercamber.getPass1TestSet( testSetName );

            availableTagSet.addAll( testSet.getTagNames() );
         }
      }

      availableTagList = new java.util.ArrayList<String>();

      availableTagList.addAll( availableTagSet );

      java.util.Collections.sort( availableTagList );

      switch ( availableTagList.size() )
      {
         case 0:
         {
            stringBuffer.append( "  No tags avaialable in these test sets." );
            break;
         }
         case 1:
         {
            stringBuffer.append( "  The only available tag is \"" ).append( availableTagList.get(0) ).append( "." );
            break;
         }
         default:
         {
            stringBuffer.append( "  The available tags are " ).append( Utilities.formatList(availableTagList) ).append( "." );
         }
      }

      System.out.println( stringBuffer.toString() );
   }
}

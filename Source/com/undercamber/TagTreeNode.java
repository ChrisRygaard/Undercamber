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

final class TagTreeNode
   implements java.util.Comparator<TestData>
{
   private Undercamber                 _undercamber;
   private TestData                    _testData;
   private String                      _tagName;
   private boolean                     _isTestRow;
   private boolean                     _isTagRow;
   private String                      _treeText;
   private String                      _dependentsText;
   private String                      _testSetName;
   private java.util.List<TagTreeNode> _children;

   TagTreeNode( TestData    dummyRoot,
                Undercamber undercamber )
   {
      java.util.Map<String,java.util.Set<TestData>> tagMap;
      java.util.List<String>                        tagList;

      _treeText = "";
      _dependentsText = "";
      _tagName = "";
      _testSetName = "";
      _isTagRow = false;
      _isTestRow = false;
      _testData = null;
      _children = new java.util.ArrayList<TagTreeNode>();
      _undercamber = undercamber;

      tagMap = new java.util.HashMap<String,java.util.Set<TestData>>();

      dummyRoot.addToTagMap( tagMap );

      tagList = new java.util.ArrayList<String>();
      tagList.addAll( tagMap.keySet() );
      java.util.Collections.sort( tagList );
      for ( String tagName : tagList )
      {
         _children.add( new TagTreeNode(tagName,
                                        tagMap.get(tagName),
                                        undercamber) );
      }
   }

   private TagTreeNode( String                  tagName,
                        java.util.Set<TestData> testData,
                        Undercamber             undercamber )
   {
      java.util.List<TestData> testDataList;

      _treeText = tagName;
      _testData = null;
      _dependentsText = "";
      _testSetName = "";
      _children = new java.util.ArrayList<TagTreeNode>();
      _isTagRow = true;
      _isTestRow = false;
      _tagName = tagName;
      _undercamber = undercamber;

      testDataList = new java.util.ArrayList<TestData>();

      testDataList.addAll( testData );

      java.util.Collections.sort( testDataList,
                                  this );

      for ( TestData child : testDataList )
      {
         _children.add( new TagTreeNode(child,
                                        tagName,
                                        undercamber) );
      }
   }

   private TagTreeNode( TestData    testData,
                        String      tagName,
                        Undercamber undercamber )
   {
      Tag tag;

      _children = new java.util.ArrayList<TagTreeNode>();
      _isTagRow = false;
      _isTestRow = true;
      _tagName = "";
      _testSetName = testData.getTestSetName();
      _undercamber = undercamber;
      _testData = testData;

      tag = testData.getTag( tagName );

      if ( tag.includeSubtests() )
      {
         _dependentsText = "Include subtests";
      }
      else
      {
         _dependentsText = "Don't include subtests";
      }

      _treeText = testData.getHeading();
   }

   final TagTreeNode[] listChildren()
   {
      TagTreeNode children[];

      children = new TagTreeNode[ _children.size() ];
      children = _children.toArray( children );

      return children;
   }

   final boolean isTagRow()
   {
      return _isTagRow;
   }

   final boolean isTestRow()
   {
      return _isTestRow;
   }

   final String getTreeText()
   {
      return _treeText;
   }

   final String getTestSetName()
   {
      return _testSetName;
   }

   final TestData getTestData()
   {
      return _testData;
   }

   final String getDependentsText()
   {
      return _dependentsText;
   }

   final void run( boolean runSubtests )
   {
      if ( _undercamber != null )
      {
         if ( _isTagRow )
         {
            javafx.application.Platform.runLater( () -> _undercamber.runTag(_tagName) );
         }
         else if ( _isTestRow )
         {
            _undercamber.runTest( _testData,
                                  runSubtests );
         }
      }
   }

   final public int compare( TestData testData1,
                             TestData testData2 )
   {
      return testData1.getID() - testData2.getID();
   }

   final String getTagName()
   {
      return _tagName;
   }
}

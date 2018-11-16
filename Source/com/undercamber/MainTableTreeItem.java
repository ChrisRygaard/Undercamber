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

final class MainTableTreeItem
   extends javafx.scene.control.TreeItem<MainTableItemWrapper>
   implements TestDataListener
{
   private boolean                                                  _isSelectionWindow;
   private MainTableItemWrapper                                     _mainTableItemWrapper;
   private javafx.scene.control.TreeTableView<MainTableItemWrapper> _mainTable;

   MainTableTreeItem( boolean                                                  isSelectionWindow,
                      javafx.scene.control.TreeTableView<MainTableItemWrapper> mainTable,
                      MainTableItemWrapper                                     mainTableItemWrapper )
   {
      super( mainTableItemWrapper );

      TestData testData;

      _isSelectionWindow = isSelectionWindow;
      _mainTableItemWrapper = mainTableItemWrapper;
      _mainTable = mainTable;

      if ( mainTableItemWrapper.isTestData() )
      {
         testData = mainTableItemWrapper.getTestData();

         testData.setMainTableTreeItem( this );

         testData.addListener( this );

         setExpanded( testData.isExpanded(isSelectionWindow) );

         expandedProperty().addListener( (observable,oldValue,newValue) -> testData.setExpanded(isSelectionWindow,
                                                                                                newValue) );
      }
      else
      {
         setExpanded( true );
      }

      for ( MainTableItemWrapper child : mainTableItemWrapper.getChildren() )
      {
         getChildren().add( new MainTableTreeItem(isSelectionWindow,
                                                  mainTable,
                                                  child) );
      }
   }

   void select()
   {
      _mainTableItemWrapper.getTestData().expandAncestors( _isSelectionWindow );
      _mainTable.getSelectionModel().select( this );
   }

   // Methods from WrapperTreeItemListener

   final public void updated( TestData testData )
   {
      setExpanded( testData.isExpanded(_isSelectionWindow) );
   }

   final public void ancestorUpdated( TestData testData,
                                      TestData updatedAncestor )
   {
   }

   final public void descendantUpdated( TestData testData,
                                        TestData updatedDescendant )
   {
   }
}

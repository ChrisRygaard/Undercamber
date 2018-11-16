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

final class DependencyTableCell
   extends javafx.scene.control.TableCell<DependencyTableRowData,DependencyTableRowData>
{
   private Column              _column;
   private javafx.stage.Window _ownerWindow;
   private boolean             _showTestPopupMenu;

   DependencyTableCell( boolean             showTestPopupMenu,
                        Column              column,
                        javafx.stage.Window ownerWindow )
   {
      _column = column;
      _ownerWindow = ownerWindow;
      _showTestPopupMenu = showTestPopupMenu;

      setOnMouseClicked( event -> onMouseClicked(event) );
   }

   final public void updateItem( DependencyTableRowData dependencyTableRowData,
                                 boolean                empty )
   {
      super.updateItem( dependencyTableRowData,
                        empty );

      if ( empty )
      {
         setText( null );
         setGraphic( null );
      }
      else
      {
         if ( dependencyTableRowData == null )
         {
            setText( null );
            setGraphic( null );
         }
         else
         {
            switch ( _column )
            {
               case TEST_SET:
               {
                  setText( dependencyTableRowData.getTestSetName() );
                  break;
               }
               case DEPENDENCY:
               {
                  setText( dependencyTableRowData.getTestHeading() );
                  break;
               }
               case TYPE:
               {
                  setText( dependencyTableRowData.getType() );
                  break;
               }
               default:
               {
                  throw new Error( "Unrecognized column:  " + _column );
               }
            }
         }
      }
   }

   final private void onMouseClicked( javafx.scene.input.MouseEvent event )
   {
      DependencyTableRowData dependencyTableRowData;

      dependencyTableRowData = getItem();

      if ( dependencyTableRowData != null )
      {
         if ( event.getClickCount() == 1 )
         {
            if ( event.getButton() == javafx.scene.input.MouseButton.SECONDARY )
            {
               event.consume();

               createContextMenu( dependencyTableRowData ).show( _ownerWindow,
                                                                 event.getScreenX(),
                                                                 event.getScreenY() );
            }
         }
         else if ( event.getClickCount() == 2 )
         {
            if ( event.getButton() == javafx.scene.input.MouseButton.PRIMARY )
            {
               event.consume();

               dependencyTableRowData.run( true );
            }
         }
      }
   }

   final private javafx.scene.control.ContextMenu createContextMenu( DependencyTableRowData dependencyTableRowData )
   {
      javafx.scene.control.ContextMenu popupMenu;
      javafx.scene.control.MenuItem    menuItem;

      popupMenu = new javafx.scene.control.ContextMenu();

      if ( _showTestPopupMenu )
      {
         menuItem = new javafx.scene.control.MenuItem( "Run this test" );
         menuItem.setOnAction( event -> dependencyTableRowData.run(false) );
         popupMenu.getItems().addAll( menuItem );

         menuItem = new javafx.scene.control.MenuItem( "Run this test and subtests (double-click)" );
         menuItem.setOnAction( event -> dependencyTableRowData.run(true) );
         popupMenu.getItems().addAll( menuItem );

         popupMenu.getItems().add( new javafx.scene.control.SeparatorMenuItem() );

         menuItem = new javafx.scene.control.MenuItem( "Show in main window" );
         menuItem.setOnAction( event -> dependencyTableRowData.getTestData().getMainTableTreeItem().select() );
         popupMenu.getItems().addAll( menuItem );
      }
      else
      {
         menuItem = new javafx.scene.control.MenuItem( "Show in main window" );
         menuItem.setOnAction( event -> dependencyTableRowData.getTestData().getMainTableTreeItem().select() );
         popupMenu.getItems().addAll( menuItem );
      }

      return popupMenu;
   }

   enum Column
   {
      TEST_SET,
      DEPENDENCY,
      TYPE
   }
}

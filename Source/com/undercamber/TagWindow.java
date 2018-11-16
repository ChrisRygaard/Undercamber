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

final class TagWindow
{
   final private static int    CLASS_PERSISTENCE_VERSION = 0;
   final private static String CLASS_PERSISTENCE_BRANCH  = "";

   private javafx.stage.Stage                              _stage;
   private javafx.scene.control.Button                     _closeButton;
   private javafx.scene.control.TreeTableView<TagTreeNode> _treeTableView;
   private TagTreeItem                                     _guiRoot;
   private Undercamber                                     _undercamber;

   TagWindow( TestData            dummyRoot,
              boolean             showTestPopupMenu,
              Undercamber         undercamber,
              javafx.stage.Window ownerWindow )
   {
      javafx.scene.Scene scene;

      _stage = new javafx.stage.Stage();

      _undercamber = undercamber;

      scene = new javafx.scene.Scene( buildContentPane(dummyRoot,
                                                       showTestPopupMenu,
                                                       undercamber) );

      _stage.setScene( scene );

      scene.addEventFilter( javafx.scene.input.KeyEvent.KEY_PRESSED,
                            event -> handleKeyPress(event) );
      _stage.addEventFilter( javafx.scene.input.KeyEvent.KEY_PRESSED,
                             event -> handleKeyPress(event) );

      _stage.setTitle( "Tags" );
      _stage.setScene( scene );
      _stage.initOwner( ownerWindow );

      _stage.setOnCloseRequest( event -> onCloseRequest() );
   }

   final private javafx.scene.layout.Pane buildContentPane( TestData    dummyRoot,
                                                            boolean     showTestPopupMenu,
                                                            Undercamber undercamber )
   {
      TagTreeNode                                                   tagTreeNodeRoot;
      javafx.scene.layout.Pane                                      buttonPane;
      javafx.scene.layout.GridPane                                  pane;
      javafx.scene.layout.ColumnConstraints                         column0Constraints;
      javafx.scene.layout.RowConstraints                            row0Constraints;
      javafx.scene.layout.RowConstraints                            row1Constraints;
      javafx.scene.control.TreeTableColumn<TagTreeNode,TagTreeNode> tableColumn;

      tagTreeNodeRoot = new TagTreeNode( dummyRoot,
                                         undercamber );

      _guiRoot = new TagTreeItem( tagTreeNodeRoot );

      _treeTableView = new javafx.scene.control.TreeTableView<TagTreeNode>();
      _treeTableView.setShowRoot( false );
      _treeTableView.setRoot( _guiRoot );
      _treeTableView.getSelectionModel().setSelectionMode( javafx.scene.control.SelectionMode.SINGLE );

      tableColumn = new javafx.scene.control.TreeTableColumn<TagTreeNode,TagTreeNode>( "Tag" );
      tableColumn.setCellValueFactory( param -> new NoOpObservable<TagTreeNode>(param.getValue().getValue()) );
      tableColumn.setCellFactory( treeTableColumn -> new TagWindowCell(showTestPopupMenu,
                                                                       TagWindowCell.Column.TAG,
                                                                       _stage) );
      tableColumn.setSortable( false );
      _treeTableView.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TreeTableColumn<TagTreeNode,TagTreeNode>( "Test Set" );
      tableColumn.setCellValueFactory( param -> new NoOpObservable<TagTreeNode>(param.getValue().getValue()) );
      tableColumn.setCellFactory( treeTableColumn -> new TagWindowCell(showTestPopupMenu,
                                                                       TagWindowCell.Column.TEST_SET,
                                                                       _stage) );
      tableColumn.setSortable( false );
      _treeTableView.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TreeTableColumn<TagTreeNode,TagTreeNode>( "Subtests" );
      tableColumn.setCellValueFactory( param -> new NoOpObservable<TagTreeNode>(param.getValue().getValue()) );
      tableColumn.setCellFactory( treeTableColumn -> new TagWindowCell(showTestPopupMenu,
                                                                       TagWindowCell.Column.SUBTESTS,
                                                                       _stage) );
      tableColumn.setSortable( false );
      _treeTableView.getColumns().add( tableColumn );

      buttonPane = makeButtonPane();

      pane = new javafx.scene.layout.GridPane();

      column0Constraints = new javafx.scene.layout.ColumnConstraints();
      column0Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      pane.getColumnConstraints().addAll( column0Constraints );

      row0Constraints = new javafx.scene.layout.RowConstraints();
      row0Constraints.setVgrow( javafx.scene.layout.Priority.ALWAYS );
      row1Constraints = new javafx.scene.layout.RowConstraints();
      row1Constraints.setVgrow( javafx.scene.layout.Priority.NEVER );
      pane.getRowConstraints().addAll( row0Constraints,
                                       row1Constraints );

      javafx.scene.layout.GridPane.setConstraints( _treeTableView, 0, 0, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setConstraints( buttonPane,     0, 1, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );

      pane.getChildren().addAll( _treeTableView,
                                 buttonPane );

      return pane;
   }

   final private javafx.scene.layout.Pane makeButtonPane()
   {
      javafx.scene.layout.GridPane          buttonPane;
      javafx.scene.layout.ColumnConstraints column0Constraints;
      javafx.scene.layout.RowConstraints    row0Constraints;

      _closeButton = new javafx.scene.control.Button( "Close" );
      _closeButton.setOnAction( event -> onCloseRequest() );

      buttonPane = new javafx.scene.layout.GridPane();

      column0Constraints = new javafx.scene.layout.ColumnConstraints();
      column0Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      buttonPane.getColumnConstraints().addAll( column0Constraints );

      row0Constraints = new javafx.scene.layout.RowConstraints();
      row0Constraints.setVgrow( javafx.scene.layout.Priority.ALWAYS );
      buttonPane.getRowConstraints().addAll( row0Constraints );

      javafx.scene.layout.GridPane.setConstraints( _closeButton, 0, 0, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );

      buttonPane.getChildren().addAll( _closeButton );

      return buttonPane;
   }

   final void readConfiguration( java.io.DataInputStream dataInputStream )
      throws java.io.IOException
   {
      double                                                              x;
      double                                                              y;
      double                                                              width;
      double                                                              height;
      javafx.geometry.Rectangle2D                                         stageBounds;
      int                                                                 elementCount;
      java.util.List<Double>                                              columnWidths;
      int                                                                 index;
      java.util.List<javafx.scene.control.TreeTableColumn<TagTreeNode,?>> columns;
      int                                                                 loopCount;
      java.util.Set<String>                                               expandedTags;

      if ( dataInputStream.readInt() > CLASS_PERSISTENCE_VERSION )
      {
         throw new java.io.IOException( "The persistent GUI data is from a newer version of Undercamber" );
      }
      if ( !(dataInputStream.readUTF().equals(CLASS_PERSISTENCE_BRANCH)) )
      {
         throw new java.io.IOException( "The persistent GUI data is from an unrecognized branch of Undercamber" );
      }

      x = dataInputStream.readDouble();
      y = dataInputStream.readDouble();
      width = dataInputStream.readDouble();
      height = dataInputStream.readDouble();
      stageBounds = Utilities.ensureInScreen( x,
                                              y,
                                              width,
                                              height );

      _stage.setX( stageBounds.getMinX() );
      _stage.setY( stageBounds.getMinY() );
      _stage.setWidth( stageBounds.getWidth() );
      _stage.setHeight( stageBounds.getHeight() );

      columnWidths = new java.util.ArrayList<Double>();
      elementCount = dataInputStream.readInt();
      for ( index=0; index<elementCount; index++ )
      {
         columnWidths.add( dataInputStream.readDouble() );
      }
      columns = _treeTableView.getColumns();
      if ( columns != null )
      {
         if ( columns.size() > columnWidths.size() )
         {
            loopCount = columnWidths.size();
         }
         else
         {
            loopCount = columns.size();
         }

         for ( index=0; index<loopCount; index++ )
         {
            if ( (columnWidths.get(index)>5) && (columnWidths.get(index)<1000) )
            {
               columns.get( index ).setPrefWidth( columnWidths.get(index) );
            }
         }
      }

      expandedTags = new java.util.HashSet<String>();
      elementCount = dataInputStream.readInt();
      for ( index=0; index<elementCount; index++ )
      {
         expandedTags.add( dataInputStream.readUTF() );
      }

      _guiRoot.expandNodes( expandedTags );

      if ( dataInputStream.readBoolean() )
      {
         _stage.show();
      }
      else
      {
         _stage.hide();
      }
   }

   final void onCloseRequest()
   {
      _stage.setX( _stage.getX() );
      _stage.setY( _stage.getY() );
      _stage.setWidth( _stage.getWidth() );
      _stage.setHeight( _stage.getHeight() );
      _stage.close();
   }

   final private void handleKeyPress( javafx.scene.input.KeyEvent event )
   {
      if ( Utilities.ENTER_KEYBOARD_COMBINATION.match(event) )
      {
         if ( _undercamber != null )
         {
            event.consume();
            _undercamber.runGUISelectedTests();
         }
      }
      else if ( Undercamber.CONTROL_A_KEYBOARD_COMBINATION.match(event) )
      {
         if ( _undercamber != null )
         {
            event.consume();
            _undercamber.runAllTests();
         }
      }
      else if ( Utilities.ESCAPE_KEYBOARD_COMBINATION.match(event) )
      {
         _stage.close();
         event.consume();
      }
   }

   final void writeConfiguration( java.io.DataOutputStream dataOutputStream )
      throws java.io.IOException
   {
      java.util.List<javafx.scene.control.TreeTableColumn<TagTreeNode,?>> columns;
      java.util.Set<String>                                               expandedNodes;

      dataOutputStream.writeInt( CLASS_PERSISTENCE_VERSION );
      dataOutputStream.writeUTF( CLASS_PERSISTENCE_BRANCH );

      dataOutputStream.writeDouble( _stage.getX() );
      dataOutputStream.writeDouble( _stage.getY() );
      dataOutputStream.writeDouble( _stage.getWidth() );
      dataOutputStream.writeDouble( _stage.getHeight() );

      columns = _treeTableView.getColumns();
      dataOutputStream.writeInt( columns.size() );
      for ( javafx.scene.control.TreeTableColumn<TagTreeNode,?> column : columns )
      {
         dataOutputStream.writeDouble( column.getWidth() );
      }

      expandedNodes = new java.util.HashSet<String>();
      _guiRoot.addExpandedTagsToSet( expandedNodes );
      dataOutputStream.writeInt( expandedNodes.size() );
      for ( String tagName : expandedNodes )
      {
         dataOutputStream.writeUTF( tagName );
      }

      dataOutputStream.writeBoolean( _stage.isShowing() );
   }

   final void show( boolean show )
   {
      if ( show )
      {
         _stage.show();
      }
      else
      {
         _stage.hide();
      }
      _stage.requestFocus();
   }
}

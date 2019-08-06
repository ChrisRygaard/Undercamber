// Copyright 2018 AlpinePeak Systems, LLC
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

final class RequirementsWindow
{
   final private static int    CLASS_PERSISTENCE_VERSION = 0;
   final private static String CLASS_PERSISTENCE_BRANCH  = "";

   private javafx.stage.Stage                              _stage;
   private javafx.scene.control.Button                     _closeButton;
   private javafx.scene.control.TableView<RequirementData> _table;

   RequirementsWindow( String                          windowTitle,
                       boolean                         results,
                       java.util.List<RequirementData> requirements,
                       javafx.stage.Window             ownerWindow )
   {
      javafx.scene.Scene scene;

      scene = new javafx.scene.Scene( buildContentPane(results,
                                                       requirements) );

      _stage = new javafx.stage.Stage();

      _stage.setScene( scene );

      scene.addEventFilter( javafx.scene.input.KeyEvent.KEY_PRESSED,
                            event -> handleKeyPress(event) );
      _stage.addEventFilter( javafx.scene.input.KeyEvent.KEY_PRESSED,
                             event -> handleKeyPress(event) );

      _stage.setTitle( windowTitle );
      _stage.setScene( scene );
      _stage.initOwner( ownerWindow );

      _stage.setOnCloseRequest( event -> onCloseRequest() );
   }

   final private javafx.scene.layout.Pane buildContentPane( boolean                         results,
                                                            java.util.List<RequirementData> requirements )
   {
      javafx.scene.control.TableColumn<RequirementData,RequirementData> tableColumn;
      javafx.scene.layout.Pane                                          buttonPane;
      javafx.scene.layout.GridPane                                      pane;
      javafx.scene.layout.ColumnConstraints                             column0Constraints;
      javafx.scene.layout.RowConstraints                                row0Constraints;
      javafx.scene.layout.RowConstraints                                row1Constraints;

      _table = new javafx.scene.control.TableView<RequirementData>();

      tableColumn = new javafx.scene.control.TableColumn<RequirementData,RequirementData>( "ID" );
      tableColumn.setCellValueFactory( cellDataFactory -> new NoOpObservable<RequirementData>(cellDataFactory.getValue()) );
      tableColumn.setCellFactory( column -> new RequirementsTableCell(RequirementsTableCell.CellType.ID) );
      tableColumn.setSortable( false );
      _table.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TableColumn<RequirementData,RequirementData>( "Description" );
      tableColumn.setCellValueFactory( cellDataFactory -> new NoOpObservable<RequirementData>(cellDataFactory.getValue()) );
      tableColumn.setCellFactory( column -> new RequirementsTableCell(RequirementsTableCell.CellType.DESCRIPTION) );
      tableColumn.setSortable( false );
      _table.getColumns().add( tableColumn );

      if ( results )
      {
         tableColumn = new javafx.scene.control.TableColumn<RequirementData,RequirementData>( "State" );
         tableColumn.setCellValueFactory( cellDataFactory -> new NoOpObservable<RequirementData>(cellDataFactory.getValue()) );
         tableColumn.setCellFactory( column -> new RequirementsTableCell(RequirementsTableCell.CellType.COMPLETION_STATE) );
         tableColumn.setSortable( false );
         _table.getColumns().add( tableColumn );

         tableColumn = new javafx.scene.control.TableColumn<RequirementData,RequirementData>( "Result" );
         tableColumn.setCellValueFactory( cellDataFactory -> new NoOpObservable<RequirementData>(cellDataFactory.getValue()) );
         tableColumn.setCellFactory( column -> new RequirementsTableCell(RequirementsTableCell.CellType.RESULT) );
         tableColumn.setSortable( false );
         _table.getColumns().add( tableColumn );
      }

      _table.getItems().addAll( requirements );

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

      javafx.scene.layout.GridPane.setConstraints( _table,     0, 0, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setConstraints( buttonPane, 0, 1, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );

      pane.getChildren().addAll( _table,
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
      java.util.List<javafx.scene.control.TableColumn<RequirementData,?>> columns;
      int                                                                 loopCount;

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
      columns = _table.getColumns();
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

      if ( dataInputStream.readBoolean() )
      {
         _stage.show();
      }
      else
      {
         _stage.hide();
      }
   }

   final private void handleKeyPress( javafx.scene.input.KeyEvent event )
   {
      if ( Utilities.ENTER_KEYBOARD_COMBINATION.match(event) )
      {
         _stage.close();
         event.consume();
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
      java.util.List<javafx.scene.control.TableColumn<RequirementData,?>> columns;

      dataOutputStream.writeInt( CLASS_PERSISTENCE_VERSION );
      dataOutputStream.writeUTF( CLASS_PERSISTENCE_BRANCH );

      dataOutputStream.writeDouble( _stage.getX() );
      dataOutputStream.writeDouble( _stage.getY() );
      dataOutputStream.writeDouble( _stage.getWidth() );
      dataOutputStream.writeDouble( _stage.getHeight() );

      columns = _table.getColumns();
      dataOutputStream.writeInt( columns.size() );
      for ( javafx.scene.control.TableColumn<RequirementData,?> column : columns )
      {
         dataOutputStream.writeDouble( column.getWidth() );
      }

      dataOutputStream.writeBoolean( _stage.isShowing() );
   }

   final void onCloseRequest()
   {
      _stage.setWidth( _stage.getWidth() );
      _stage.setHeight( _stage.getHeight() );
      _stage.setX( _stage.getX() );
      _stage.setY( _stage.getY() );
      _stage.close();
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

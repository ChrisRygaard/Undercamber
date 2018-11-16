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

final class DependencyWindow
{
   private javafx.stage.Stage          _stage;
   private javafx.scene.control.Button _closeButton;
   private Undercamber                 _undercamber;

   DependencyWindow( TestData            testData,
                     boolean             showTestPopupMenu,
                     Undercamber         undercamber,
                     javafx.stage.Window ownerWindow )
   {
      javafx.scene.Scene scene;

      _undercamber = undercamber;

      scene = new javafx.scene.Scene( buildContentPane(testData,
                                                       showTestPopupMenu,
                                                       undercamber,
                                                       ownerWindow) );

      _stage = new javafx.stage.Stage();

      _stage.setScene( scene );

      scene.addEventFilter( javafx.scene.input.KeyEvent.KEY_PRESSED,
                            event -> handleKeyPress(event) );
      _stage.addEventFilter( javafx.scene.input.KeyEvent.KEY_PRESSED,
                             event -> handleKeyPress(event) );

      if ( (testData.getTotalPrerequisiteCount()>0) && (testData.getTotalDependentCount()>0) )
      {
         _stage.setTitle( "Dependencies for " + testData.getHeading() );
      }
      else if ( testData.getTotalPrerequisiteCount() > 0 )
      {
         _stage.setTitle( "Prerequisites for " + testData.getHeading() );
      }
      else if ( testData.getTotalDependentCount() > 0 )
      {
         _stage.setTitle( "Dependents for " + testData.getHeading() );
      }
      _stage.setScene( scene );
      _stage.initOwner( ownerWindow );

      _stage.setOnCloseRequest( event -> onCloseRequest() );

      _stage.show();
   }

   final private javafx.scene.layout.Pane buildContentPane( TestData            testData,
                                                            boolean             showTestPopupMenu,
                                                            Undercamber         undercamber,
                                                            javafx.stage.Window ownerWindow )
   {
      if ( (testData.getTotalPrerequisiteCount()>0) && (testData.getTotalDependentCount()>0) )
      {
         return buildPaneWithPrerequisitesAndDependents( testData,
                                                         showTestPopupMenu,
                                                         undercamber,
                                                         ownerWindow );
      }
      else if ( testData.getTotalPrerequisiteCount() > 0 )
      {
         return buildPrerequisitesPane( testData,
                                        showTestPopupMenu,
                                        undercamber,
                                        ownerWindow );
      }
      else if ( testData.getTotalDependentCount() > 0 )
      {
         return buildDependentsPane( testData,
                                     showTestPopupMenu,
                                     undercamber,
                                     ownerWindow );
      }
      else
      {
         return null;
      }
   }

   final private javafx.scene.layout.Pane buildPaneWithPrerequisitesAndDependents( TestData            testData,
                                                                                   boolean             showTestPopupMenu,
                                                                                   Undercamber         undercamber,
                                                                                   javafx.stage.Window ownerWindow )
   {
      javafx.scene.control.Label                             prerequisitesLabel;
      javafx.scene.control.TableView<DependencyTableRowData> prerequisiteTable;
      javafx.scene.layout.GridPane                           prerequisitesPane;
      javafx.scene.control.Label                             dependentsLabel;
      javafx.scene.control.TableView<DependencyTableRowData> dependentsTable;
      javafx.scene.layout.GridPane                           dependentsPane;
      javafx.scene.layout.ColumnConstraints                  column0Constraints;
      javafx.scene.layout.RowConstraints                     row0Constraints;
      javafx.scene.layout.RowConstraints                     row1Constraints;
      javafx.scene.control.SplitPane                         splitPane;
      javafx.scene.layout.Pane                               buttonPane;
      javafx.scene.layout.GridPane                           pane;

      prerequisitesLabel = new javafx.scene.control.Label( "Prerequisites" );

      prerequisiteTable = makePrerequisiteTable( testData,
                                                 showTestPopupMenu,
                                                 undercamber,
                                                 ownerWindow );

      prerequisitesPane = new javafx.scene.layout.GridPane();

      column0Constraints = new javafx.scene.layout.ColumnConstraints();
      column0Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      prerequisitesPane.getColumnConstraints().addAll( column0Constraints );

      row0Constraints = new javafx.scene.layout.RowConstraints();
      row0Constraints.setVgrow( javafx.scene.layout.Priority.NEVER );
      row1Constraints = new javafx.scene.layout.RowConstraints();
      row1Constraints.setVgrow( javafx.scene.layout.Priority.ALWAYS );
      prerequisitesPane.getRowConstraints().addAll( row0Constraints,
                                                    row1Constraints );

      javafx.scene.layout.GridPane.setConstraints( prerequisitesLabel, 0, 0, 1, 1, javafx.geometry.HPos.LEFT,   javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setConstraints( prerequisiteTable,  0, 1, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );

      prerequisitesPane.getChildren().addAll( prerequisitesLabel,
                                              prerequisiteTable );

      dependentsLabel = new javafx.scene.control.Label( "Dependents" );

      dependentsTable = makeDependentTable( testData,
                                            showTestPopupMenu,
                                            undercamber,
                                            ownerWindow );

      dependentsPane = new javafx.scene.layout.GridPane();

      column0Constraints = new javafx.scene.layout.ColumnConstraints();
      column0Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      dependentsPane.getColumnConstraints().addAll( column0Constraints );

      row0Constraints = new javafx.scene.layout.RowConstraints();
      row0Constraints.setVgrow( javafx.scene.layout.Priority.NEVER );
      row1Constraints = new javafx.scene.layout.RowConstraints();
      row1Constraints.setVgrow( javafx.scene.layout.Priority.ALWAYS );
      dependentsPane.getRowConstraints().addAll( row0Constraints,
                                                 row1Constraints );

      javafx.scene.layout.GridPane.setConstraints( dependentsLabel, 0, 0, 1, 1, javafx.geometry.HPos.LEFT,   javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setConstraints( dependentsTable, 0, 1, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );

      dependentsPane.getChildren().addAll( dependentsLabel,
                                           dependentsTable );

      splitPane = new javafx.scene.control.SplitPane( prerequisitesPane,
                                                      dependentsPane );

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

      javafx.scene.layout.GridPane.setConstraints( splitPane,  0, 0, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setConstraints( buttonPane, 0, 1, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );

      pane.getChildren().addAll( splitPane,
                                 buttonPane );

      return pane;
   }

   final private javafx.scene.layout.Pane buildPrerequisitesPane( TestData            testData,
                                                                  boolean             showTestPopupMenu,
                                                                  Undercamber         undercamber,
                                                                  javafx.stage.Window ownerWindow )
   {
      javafx.scene.control.TableView<DependencyTableRowData> prerequisiteTable;
      javafx.scene.layout.Pane                               buttonPane;
      javafx.scene.layout.GridPane                           pane;
      javafx.scene.layout.ColumnConstraints                  column0Constraints;
      javafx.scene.layout.RowConstraints                     row0Constraints;
      javafx.scene.layout.RowConstraints                     row1Constraints;

      prerequisiteTable = makePrerequisiteTable( testData,
                                                 showTestPopupMenu,
                                                 undercamber,
                                                 ownerWindow );

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

      javafx.scene.layout.GridPane.setConstraints( prerequisiteTable, 0, 0, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setConstraints( buttonPane,        0, 1, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );

      pane.getChildren().addAll( prerequisiteTable,
                                 buttonPane );

      return pane;
   }

   final private javafx.scene.layout.Pane buildDependentsPane( TestData            testData,
                                                               boolean             showTestPopupMenu,
                                                               Undercamber         undercamber,
                                                               javafx.stage.Window ownerWindow )
   {
      javafx.scene.control.TableView<DependencyTableRowData> dependentsTable;
      javafx.scene.layout.Pane                               buttonPane;
      javafx.scene.layout.GridPane                           pane;
      javafx.scene.layout.ColumnConstraints                  column0Constraints;
      javafx.scene.layout.RowConstraints                     row0Constraints;
      javafx.scene.layout.RowConstraints                     row1Constraints;

      dependentsTable = makeDependentTable( testData,
                                            showTestPopupMenu,
                                            undercamber,
                                            ownerWindow );

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

      javafx.scene.layout.GridPane.setConstraints( dependentsTable, 0, 0, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setConstraints( buttonPane,      0, 1, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );

      pane.getChildren().addAll( dependentsTable,
                                 buttonPane );

      return pane;
   }

   final private javafx.scene.control.TableView<DependencyTableRowData> makePrerequisiteTable( TestData            testData,
                                                                                               boolean             showTestPopupMenu,
                                                                                               Undercamber         undercamber,
                                                                                               javafx.stage.Window ownerWindow )
   {
      javafx.scene.control.TableView<DependencyTableRowData>                          table;
      javafx.scene.control.TableColumn<DependencyTableRowData,DependencyTableRowData> tableColumn;
      java.util.Set<DependencyTableRowData>                                           prerequisites;
      java.util.List<DependencyTableRowData>                                          prerequisiteList;

      table = new javafx.scene.control.TableView<DependencyTableRowData>();

      tableColumn = new javafx.scene.control.TableColumn<DependencyTableRowData,DependencyTableRowData>( "Test Set" );
      tableColumn.setCellValueFactory( cellDataFactory -> new NoOpObservable<DependencyTableRowData>(cellDataFactory.getValue()) );
      tableColumn.setCellFactory( column -> new DependencyTableCell(showTestPopupMenu,
                                                                    DependencyTableCell.Column.TEST_SET,
                                                                    ownerWindow) );
      tableColumn.setSortable( false );
      table.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TableColumn<DependencyTableRowData,DependencyTableRowData>( "Prerequisite" );
      tableColumn.setCellValueFactory( cellDataFactory -> new NoOpObservable<DependencyTableRowData>(cellDataFactory.getValue()) );
      tableColumn.setCellFactory( column -> new DependencyTableCell(showTestPopupMenu,
                                                                    DependencyTableCell.Column.DEPENDENCY,
                                                                    ownerWindow) );
      tableColumn.setSortable( false );
      table.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TableColumn<DependencyTableRowData,DependencyTableRowData>( "Type" );
      tableColumn.setCellValueFactory( cellDataFactory -> new NoOpObservable<DependencyTableRowData>(cellDataFactory.getValue()) );
      tableColumn.setCellFactory( column -> new DependencyTableCell(showTestPopupMenu,
                                                                    DependencyTableCell.Column.TYPE,
                                                                    ownerWindow) );
      tableColumn.setSortable( false );
      table.getColumns().add( tableColumn );

      prerequisites = new java.util.HashSet<DependencyTableRowData>();

      for ( TestData prerequisite : testData.listFixedPrerequisites() )
      {
         prerequisites.add( new DependencyTableRowData(undercamber,
                                                       prerequisite,
                                                       Prerequisite.Type.FIXED) );
      }

      for ( TestData prerequisite : testData.listUnsatisfiedConditionalPrerequisites() )
      {
         prerequisites.add( new DependencyTableRowData(undercamber,
                                                       prerequisite,
                                                       Prerequisite.Type.CONDITIONAL_NOT_PREVIOUSLY_SATISFIED) );
      }

      for ( TestData prerequisite : testData.listPreviouslySatisfiedConditionalPrerequisites() )
      {
         prerequisites.add( new DependencyTableRowData(undercamber,
                                                       prerequisite,
                                                       Prerequisite.Type.CONDITIONAL_PREVIOUSLY_SATISFIED) );
      }

      prerequisiteList = new java.util.ArrayList<DependencyTableRowData>();

      prerequisiteList.addAll( prerequisites );

      java.util.Collections.sort( prerequisiteList );

      table.getItems().addAll( prerequisiteList );

      return table;
   }

   final private javafx.scene.control.TableView<DependencyTableRowData> makeDependentTable( TestData            testData,
                                                                                            boolean             showTestPopupMenu,
                                                                                            Undercamber         undercamber,
                                                                                            javafx.stage.Window ownerWindow )
   {
      javafx.scene.control.TableView<DependencyTableRowData>                          table;
      javafx.scene.control.TableColumn<DependencyTableRowData,DependencyTableRowData> tableColumn;
      java.util.Set<DependencyTableRowData>                                           dependents;
      java.util.List<DependencyTableRowData>                                          dependentList;

      table = new javafx.scene.control.TableView<DependencyTableRowData>();

      tableColumn = new javafx.scene.control.TableColumn<DependencyTableRowData,DependencyTableRowData>( "Test Set" );
      tableColumn.setCellValueFactory( cellDataFactory -> new NoOpObservable<DependencyTableRowData>(cellDataFactory.getValue()) );
      tableColumn.setCellFactory( column -> new DependencyTableCell(showTestPopupMenu,
                                                                    DependencyTableCell.Column.TEST_SET,
                                                                    _stage) );
      tableColumn.setSortable( false );
      table.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TableColumn<DependencyTableRowData,DependencyTableRowData>( "Dependent" );
      tableColumn.setCellValueFactory( cellDataFactory -> new NoOpObservable<DependencyTableRowData>(cellDataFactory.getValue()) );
      tableColumn.setCellFactory( column -> new DependencyTableCell(showTestPopupMenu,
                                                                    DependencyTableCell.Column.DEPENDENCY,
                                                                    _stage) );
      tableColumn.setSortable( false );
      table.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TableColumn<DependencyTableRowData,DependencyTableRowData>( "Type" );
      tableColumn.setCellValueFactory( cellDataFactory -> new NoOpObservable<DependencyTableRowData>(cellDataFactory.getValue()) );
      tableColumn.setCellFactory( column -> new DependencyTableCell(showTestPopupMenu,
                                                                    DependencyTableCell.Column.TYPE,
                                                                    _stage) );
      tableColumn.setSortable( false );
      table.getColumns().add( tableColumn );

      dependents = new java.util.HashSet<DependencyTableRowData>();

      for ( TestData dependent : testData.listFixedDependents() )
      {
         dependents.add( new DependencyTableRowData(undercamber,
                                                    dependent,
                                                    Prerequisite.Type.FIXED) );
      }

      for ( TestData dependent : testData.listUnsatisfiedConditionalDependents() )
      {
         dependents.add( new DependencyTableRowData(undercamber,
                                                    dependent,
                                                    Prerequisite.Type.CONDITIONAL_NOT_PREVIOUSLY_SATISFIED) );
      }

      for ( TestData dependent : testData.listSatisfiedConditionalDependents() )
      {
         dependents.add( new DependencyTableRowData(undercamber,
                                                    dependent,
                                                    Prerequisite.Type.CONDITIONAL_PREVIOUSLY_SATISFIED) );
      }

      dependentList = new java.util.ArrayList<DependencyTableRowData>();

      dependentList.addAll( dependents );

      java.util.Collections.sort( dependentList );

      table.getItems().addAll( dependentList );

      return table;
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

   final private void handleKeyPress( javafx.scene.input.KeyEvent event )
   {
      if ( Utilities.ENTER_KEYBOARD_COMBINATION.match(event) )
      {
         if ( _undercamber != null )
         {
            _undercamber.runGUISelectedTests();
            event.consume();
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

   final void onCloseRequest()
   {
      _stage.setWidth( _stage.getWidth() );
      _stage.setHeight( _stage.getHeight() );
      _stage.setX( _stage.getX() );
      _stage.setY( _stage.getY() );
      _stage.close();
   }

   final void show()
   {
      _stage.show();
      _stage.requestFocus();
   }
}

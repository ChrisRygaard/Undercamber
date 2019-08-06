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

final class ResultsWindow
{
   final private static int    CLASS_PERSISTENCE_VERSION = 0;
   final private static String CLASS_PERSISTENCE_BRANCH  = "";

   private Undercamber                                              _undercamber;
   private TestData                                                 _testDataDummyRoot;
   private javafx.stage.Stage                                       _stage;
   private javafx.scene.control.TreeTableView<MainTableItemWrapper> _resultsTree;
   private javafx.scene.control.TableView<RequirementData>          _requirementsTable;
   private javafx.scene.control.TableView<TestData>                 _unsupportiveTestsTable;
   private String                                                   _testSuiteName;
   private TagWindow                                                _tagWindow;

   ResultsWindow( String                          testSuiteName,
                  TestData                        testDataDummyRoot,
                  java.util.List<TestSet>         testSets,
                  java.util.List<RequirementData> requirements,
                  java.util.List<TestData>        unsupportiveTests,
                  java.util.List<String>          commandLineTestParameters,
                  javafx.stage.Window             ownerWindow,
                  Undercamber                     undercamber )
   {
      javafx.scene.Scene scene;

      _undercamber = undercamber;
      _testDataDummyRoot = testDataDummyRoot;

      _stage = new javafx.stage.Stage();

      _tagWindow = new TagWindow( testDataDummyRoot,
                                  false,
                                  null,
                                  _stage );

      _testSuiteName = testSuiteName;

      scene = new javafx.scene.Scene( makeContents(testDataDummyRoot,
                                                   testSets,
                                                   requirements,
                                                   unsupportiveTests,
                                                   commandLineTestParameters,
                                                   _stage) );

      _stage.setScene( scene );

      scene.addEventFilter( javafx.scene.input.KeyEvent.KEY_PRESSED,
                             event -> handleKeyPress(event) );
      _stage.addEventFilter( javafx.scene.input.KeyEvent.KEY_PRESSED,
                             event -> handleKeyPress(event) );

      _stage.setTitle( "Undercamber(TM):  " + testSuiteName );
      _stage.setScene( scene );
      _stage.initOwner( ownerWindow );

      _stage.setOnCloseRequest( event -> onCloseRequest(event) );

      _stage.show();

      readConfiguration();
   }

   final private javafx.scene.layout.Pane makeContents( TestData                        testDataDummyRoot,
                                                        java.util.List<TestSet>         testSets,
                                                        java.util.List<RequirementData> requirements,
                                                        java.util.List<TestData>        unsupportiveTests,
                                                        java.util.List<String>          commandLineTestParameters,
                                                        javafx.stage.Window             ownerWindow )
   {
      javafx.scene.layout.Pane              resultsPane;
      javafx.scene.layout.Pane              unsupportiveTestsPane;
      javafx.scene.control.TabPane          tabPane;
      javafx.scene.layout.Pane              buttonPane;
      javafx.scene.layout.GridPane          contentPane;
      javafx.scene.layout.RowConstraints    row0Constraints;
      javafx.scene.layout.RowConstraints    row1Constraints;
      javafx.scene.layout.ColumnConstraints column0Constraints;

      resultsPane = makeResultsPane( testSets,
                                     commandLineTestParameters,
                                     ownerWindow );

      unsupportiveTestsPane = makeUnsupportiveTestsPane( unsupportiveTests );

      makeRequirementsTable( requirements );

      tabPane = new javafx.scene.control.TabPane();
      tabPane.getTabs().add( new javafx.scene.control.Tab("Results",
                                                          resultsPane) );
      tabPane.getTabs().add( new javafx.scene.control.Tab("Requirements",
                                                          _requirementsTable) );
      tabPane.getTabs().add( new javafx.scene.control.Tab("Unsupportive Tests",
                                                          unsupportiveTestsPane) );

      buttonPane = makeButtonPane();

      contentPane = new javafx.scene.layout.GridPane();

      row0Constraints = new javafx.scene.layout.RowConstraints();
      row0Constraints.setVgrow( javafx.scene.layout.Priority.ALWAYS );
      row1Constraints = new javafx.scene.layout.RowConstraints();
      row1Constraints.setVgrow( javafx.scene.layout.Priority.NEVER );
      contentPane.getRowConstraints().addAll( row0Constraints,
                                              row1Constraints );

      column0Constraints = new javafx.scene.layout.ColumnConstraints();
      column0Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      contentPane.getColumnConstraints().addAll( column0Constraints );

      javafx.scene.layout.GridPane.setConstraints( tabPane,    0, 0, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setConstraints( buttonPane, 0, 1, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );

      contentPane.getChildren().addAll( tabPane,
                                        buttonPane );

      return contentPane;
   }

   final private javafx.scene.layout.Pane makeButtonPane()
   {
      javafx.scene.control.Button           closeButton;
      javafx.scene.control.Button           tagsButton;
      javafx.scene.layout.GridPane          buttonPane;
      javafx.scene.layout.ColumnConstraints column0Constraints;
      javafx.scene.layout.ColumnConstraints column1Constraints;
      javafx.scene.layout.RowConstraints    row0Constraints;

      closeButton = new javafx.scene.control.Button( "Close (Esc)" );
      closeButton.setOnAction( event -> onCloseRequest(event) );

      tagsButton = new javafx.scene.control.Button( "Tags..." );
      tagsButton.setOnAction( event -> _tagWindow.show(true) );

      buttonPane = new javafx.scene.layout.GridPane();

      column0Constraints = new javafx.scene.layout.ColumnConstraints();
      column0Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      column1Constraints = new javafx.scene.layout.ColumnConstraints();
      column1Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      buttonPane.getColumnConstraints().addAll( column0Constraints,
                                                column1Constraints );

      row0Constraints = new javafx.scene.layout.RowConstraints();
      row0Constraints.setVgrow( javafx.scene.layout.Priority.ALWAYS );
      buttonPane.getRowConstraints().addAll( row0Constraints );

      javafx.scene.layout.GridPane.setConstraints( closeButton, 0, 0, 1, 1, javafx.geometry.HPos.RIGHT, javafx.geometry.VPos.CENTER, javafx.scene.layout.Priority.ALWAYS, javafx.scene.layout.Priority.ALWAYS, new javafx.geometry.Insets(5,5,5,5) );
      javafx.scene.layout.GridPane.setConstraints( tagsButton,  1, 0, 1, 1, javafx.geometry.HPos.LEFT,  javafx.geometry.VPos.CENTER, javafx.scene.layout.Priority.ALWAYS, javafx.scene.layout.Priority.ALWAYS, new javafx.geometry.Insets(5,5,5,5) );

      buttonPane.getChildren().addAll( closeButton,
                                       tagsButton );

      return buttonPane;
   }

   final private javafx.scene.layout.Pane makeResultsPane( java.util.List<TestSet> testSets,
                                                           java.util.List<String>  commandLineTestParameters,
                                                           javafx.stage.Window     ownerWindow )
   {
      javafx.scene.layout.GridPane          resultsPane;
      javafx.scene.layout.RowConstraints    row0Constraints;
      javafx.scene.layout.RowConstraints    row1Constraints;
      javafx.scene.layout.ColumnConstraints column0Constraints;
      javafx.scene.layout.Pane              treeButtonPane;

      resultsPane = new javafx.scene.layout.GridPane();

      row0Constraints = new javafx.scene.layout.RowConstraints();
      row0Constraints.setVgrow( javafx.scene.layout.Priority.ALWAYS );
      row1Constraints = new javafx.scene.layout.RowConstraints();
      row1Constraints.setVgrow( javafx.scene.layout.Priority.NEVER );
      resultsPane.getRowConstraints().addAll( row0Constraints,
                                              row1Constraints );

      column0Constraints = new javafx.scene.layout.ColumnConstraints();
      column0Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      resultsPane.getColumnConstraints().addAll( column0Constraints );

      treeButtonPane = makeTreeButtonPane();

      makeResultsTree( testSets,
                       commandLineTestParameters,
                       ownerWindow );

      javafx.scene.layout.GridPane.setConstraints( _resultsTree,   0, 0, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setConstraints( treeButtonPane, 0, 1, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );

      resultsPane.getChildren().addAll( _resultsTree,
                                        treeButtonPane );

      return resultsPane;
   }

   final private javafx.scene.layout.Pane makeTreeButtonPane()
   {
      javafx.scene.control.Button           expandAllButton;
      javafx.scene.control.Button           collapseAllButton;
      javafx.scene.layout.GridPane          buttonPane;
      javafx.scene.layout.ColumnConstraints column0Constraints;
      javafx.scene.layout.ColumnConstraints column1Constraints;
      javafx.scene.layout.RowConstraints    row0Constraints;

      expandAllButton = new javafx.scene.control.Button( "Expand All" );
      expandAllButton.setOnAction( event -> _testDataDummyRoot.setExpandedOnBranch(false,
                                                                                   true) );

      collapseAllButton = new javafx.scene.control.Button( "Collapse All" );
      collapseAllButton.setOnAction( event -> _testDataDummyRoot.setExpandedOnBranch(false,
                                                                                     false) );

      buttonPane = new javafx.scene.layout.GridPane();

      //                                                                                                                                top                                          right                                        bottom                                       left
      buttonPane.setBorder( new javafx.scene.layout.Border(new javafx.scene.layout.BorderStroke(                                        javafx.scene.paint.Color.BLACK,              javafx.scene.paint.Color.BLACK,              new javafx.scene.paint.Color(0,0,0,.5),      javafx.scene.paint.Color.BLACK,
                                                                                                                                        javafx.scene.layout.BorderStrokeStyle.SOLID, javafx.scene.layout.BorderStrokeStyle.SOLID, javafx.scene.layout.BorderStrokeStyle.SOLID, javafx.scene.layout.BorderStrokeStyle.SOLID,
                                                                                                 javafx.scene.layout.CornerRadii.EMPTY,
                                                                                                 new javafx.scene.layout.BorderWidths(  0,                                           0,                                           1,                                           0 ),
                                                                                                 new javafx.geometry.Insets(            0,                                           0,                                           0,                                           0 ) )) );

      column0Constraints = new javafx.scene.layout.ColumnConstraints();
      column0Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      column1Constraints = new javafx.scene.layout.ColumnConstraints();
      column1Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      buttonPane.getColumnConstraints().addAll( column0Constraints,
                                                column1Constraints );

      row0Constraints = new javafx.scene.layout.RowConstraints();
      row0Constraints.setVgrow( javafx.scene.layout.Priority.ALWAYS );
      buttonPane.getRowConstraints().addAll( row0Constraints );

      javafx.scene.layout.GridPane.setConstraints( expandAllButton,   0, 0, 1, 1, javafx.geometry.HPos.RIGHT, javafx.geometry.VPos.CENTER, javafx.scene.layout.Priority.ALWAYS, javafx.scene.layout.Priority.ALWAYS, new javafx.geometry.Insets(5,5,5,5) );
      javafx.scene.layout.GridPane.setConstraints( collapseAllButton, 1, 0, 1, 1, javafx.geometry.HPos.LEFT,  javafx.geometry.VPos.CENTER, javafx.scene.layout.Priority.ALWAYS, javafx.scene.layout.Priority.ALWAYS, new javafx.geometry.Insets(5,5,5,5) );

      buttonPane.getChildren().addAll( expandAllButton,
                                       collapseAllButton );

      return buttonPane;
   }

   final private void makeResultsTree( java.util.List<TestSet> testSets,
                                       java.util.List<String>  commandLineTestParameters,
                                       javafx.stage.Window     ownerWindow )
   {
      MainTableItemWrapper                                                            wrapperDummyRoot;
      MainTableTreeItem                                                               guiRoot;
      javafx.scene.control.TreeTableColumn<MainTableItemWrapper,MainTableItemWrapper> tableColumn;

      wrapperDummyRoot = new MainTableItemWrapper( testSets );

      _resultsTree = new javafx.scene.control.TreeTableView<MainTableItemWrapper>();
      _resultsTree.setShowRoot( false );
      _resultsTree.getSelectionModel().setSelectionMode( javafx.scene.control.SelectionMode.SINGLE );

      guiRoot = new MainTableTreeItem( false,
                                       _resultsTree,
                                       wrapperDummyRoot );
      _resultsTree.setRoot( guiRoot );

      tableColumn = new javafx.scene.control.TreeTableColumn<MainTableItemWrapper,MainTableItemWrapper>( "Method" );
      tableColumn.setCellValueFactory( param -> new NoOpObservable<MainTableItemWrapper>(param.getValue().getValue()) );
      tableColumn.setCellFactory( treeTableColumn -> new ResultsTreeCell(_undercamber,
                                                                         commandLineTestParameters,
                                                                         ownerWindow) );
      tableColumn.setSortable( false );
      _resultsTree.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TreeTableColumn<MainTableItemWrapper,MainTableItemWrapper>( "Errors" );
      tableColumn.setCellValueFactory( param -> new NoOpObservable<MainTableItemWrapper>(param.getValue().getValue()) );
      tableColumn.setCellFactory( treeTableColumn -> new ResultsTableCell(ResultsTableCell.CellType.ERROR_COUNT) );
      tableColumn.setSortable( false );
      _resultsTree.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TreeTableColumn<MainTableItemWrapper,MainTableItemWrapper>( "Child Errors" );
      tableColumn.setCellValueFactory( param -> new NoOpObservable<MainTableItemWrapper>(param.getValue().getValue()) );
      tableColumn.setCellFactory( treeTableColumn -> new ResultsTableCell(ResultsTableCell.CellType.CHILD_ERROR_COUNT) );
      tableColumn.setSortable( false );
      _resultsTree.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TreeTableColumn<MainTableItemWrapper,MainTableItemWrapper>( "Outcome" );
      tableColumn.setCellValueFactory( param -> new NoOpObservable<MainTableItemWrapper>(param.getValue().getValue()) );
      tableColumn.setCellFactory( treeTableColumn -> new ResultsTableCell(ResultsTableCell.CellType.OUTCOME) );
      tableColumn.setSortable( false );
      _resultsTree.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TreeTableColumn<MainTableItemWrapper,MainTableItemWrapper>( "Show Errors" );
      tableColumn.setCellValueFactory( param -> new NoOpObservable<MainTableItemWrapper>(param.getValue().getValue()) );
      tableColumn.setCellFactory( treeTableColumn -> new ShowErrorsCell(ownerWindow) );
      tableColumn.setSortable( false );
      _resultsTree.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TreeTableColumn<MainTableItemWrapper,MainTableItemWrapper>( "Requirements" );
      tableColumn.setCellValueFactory( param -> new NoOpObservable<MainTableItemWrapper>(param.getValue().getValue()) );
      tableColumn.setCellFactory( treeTableColumn -> new ShowRequirementsCell(true,
                                                                              ownerWindow) );
      tableColumn.setSortable( false );
      _resultsTree.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TreeTableColumn<MainTableItemWrapper,MainTableItemWrapper>( "Dependencies" );
      tableColumn.setCellValueFactory( param -> new NoOpObservable<MainTableItemWrapper>(param.getValue().getValue()) );
      tableColumn.setCellFactory( treeTableColumn -> new ShowDependenciesCell(false,
                                                                              null,
                                                                              ownerWindow) );
      tableColumn.setSortable( false );
      _resultsTree.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TreeTableColumn<MainTableItemWrapper,MainTableItemWrapper>( "Details" );
      tableColumn.setCellValueFactory( param -> new NoOpObservable<MainTableItemWrapper>(param.getValue().getValue()) );
      tableColumn.setCellFactory( treeTableColumn -> new ShowDetailsCell(ownerWindow) );
      tableColumn.setSortable( false );
      _resultsTree.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TreeTableColumn<MainTableItemWrapper,MainTableItemWrapper>( "Tags" );
      tableColumn.setCellValueFactory( param -> new NoOpObservable<MainTableItemWrapper>(param.getValue().getValue()) );
      tableColumn.setCellFactory( treeTableColumn -> new TagCell() );
      tableColumn.setSortable( false );
      _resultsTree.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TreeTableColumn<MainTableItemWrapper,MainTableItemWrapper>( "Expand Branch" );
      tableColumn.setCellValueFactory( param -> new NoOpObservable<MainTableItemWrapper>(param.getValue().getValue()) );
      tableColumn.setCellFactory( treeTableColumn -> new ExpandBranchCell(false,
                                                                          true) );
      tableColumn.setSortable( false );
      _resultsTree.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TreeTableColumn<MainTableItemWrapper,MainTableItemWrapper>( "Collapse Branch" );
      tableColumn.setCellValueFactory( param -> new NoOpObservable<MainTableItemWrapper>(param.getValue().getValue()) );
      tableColumn.setCellFactory( treeTableColumn -> new ExpandBranchCell(false,
                                                                          false) );
      tableColumn.setSortable( false );
      _resultsTree.getColumns().add( tableColumn );
   }

   final private void makeRequirementsTable( java.util.List<RequirementData> requirements )
   {
      javafx.scene.control.TableColumn<RequirementData,RequirementData> tableColumn;

      _requirementsTable = new javafx.scene.control.TableView<RequirementData>();

      tableColumn = new javafx.scene.control.TableColumn<RequirementData,RequirementData>( "ID" );
      tableColumn.setCellValueFactory( cellDataFactory -> new NoOpObservable<RequirementData>(cellDataFactory.getValue()) );
      tableColumn.setCellFactory( column -> new RequirementsTableCell(RequirementsTableCell.CellType.ID) );
      tableColumn.setSortable( false );
      _requirementsTable.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TableColumn<RequirementData,RequirementData>( "Description" );
      tableColumn.setCellValueFactory( cellDataFactory -> new NoOpObservable<RequirementData>(cellDataFactory.getValue()) );
      tableColumn.setCellFactory( column -> new RequirementsTableCell(RequirementsTableCell.CellType.DESCRIPTION) );
      tableColumn.setSortable( false );
      _requirementsTable.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TableColumn<RequirementData,RequirementData>( "State" );
      tableColumn.setCellValueFactory( cellDataFactory -> new NoOpObservable<RequirementData>(cellDataFactory.getValue()) );
      tableColumn.setCellFactory( column -> new RequirementsTableCell(RequirementsTableCell.CellType.COMPLETION_STATE) );
      tableColumn.setSortable( false );
      _requirementsTable.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TableColumn<RequirementData,RequirementData>( "Result" );
      tableColumn.setCellValueFactory( cellDataFactory -> new NoOpObservable<RequirementData>(cellDataFactory.getValue()) );
      tableColumn.setCellFactory( column -> new RequirementsTableCell(RequirementsTableCell.CellType.RESULT) );
      tableColumn.setSortable( false );
      _requirementsTable.getColumns().add( tableColumn );

      _requirementsTable.getItems().addAll( requirements );
   }

   final private javafx.scene.layout.Pane makeUnsupportiveTestsPane( java.util.List<TestData> unsupportiveTests )
   {
      javafx.scene.control.TableColumn<TestData,TestData> tableColumn;
      javafx.scene.layout.GridPane                        unsupportiveTestsPane;
      javafx.scene.layout.ColumnConstraints               column0Constraints;
      javafx.scene.layout.RowConstraints                  row0Constraints;
      javafx.scene.layout.RowConstraints                  row1Constraints;
      javafx.scene.text.Text                              label;

      _unsupportiveTestsTable = new javafx.scene.control.TableView<TestData>();

      tableColumn = new javafx.scene.control.TableColumn<TestData,TestData>( "Test" );
      tableColumn.setCellValueFactory( cellDataFactory -> new NoOpObservable<TestData>(cellDataFactory.getValue()) );
      tableColumn.setCellFactory( column -> new UnsupportiveTestCell(UnsupportiveTestCell.ColumnType.TEST) );
      tableColumn.setSortable( false );
      _unsupportiveTestsTable.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TableColumn<TestData,TestData>( "Outcome" );
      tableColumn.setCellValueFactory( cellDataFactory -> new NoOpObservable<TestData>(cellDataFactory.getValue()) );
      tableColumn.setCellFactory( column -> new UnsupportiveTestCell(UnsupportiveTestCell.ColumnType.RESULT) );
      tableColumn.setSortable( false );
      _unsupportiveTestsTable.getColumns().add( tableColumn );

      for ( TestData unsupportiveTest : unsupportiveTests )
      {
         _unsupportiveTestsTable.getItems().add( unsupportiveTest );
      }

      unsupportiveTestsPane = new javafx.scene.layout.GridPane();

      column0Constraints = new javafx.scene.layout.ColumnConstraints();
      column0Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      unsupportiveTestsPane.getColumnConstraints().addAll( column0Constraints );

      row0Constraints = new javafx.scene.layout.RowConstraints();
      row0Constraints.setVgrow( javafx.scene.layout.Priority.NEVER );
      row1Constraints = new javafx.scene.layout.RowConstraints();
      row1Constraints.setVgrow( javafx.scene.layout.Priority.ALWAYS );
      unsupportiveTestsPane.getRowConstraints().addAll( row0Constraints,
                                                        row1Constraints );

      label = new javafx.scene.text.Text( "These tests do not support any requirements validation:" );
      label.setStyle("-fx-font-weight: bold");

      javafx.scene.layout.GridPane.setConstraints( label,                   0, 0, 1, 1, javafx.geometry.HPos.LEFT,   javafx.geometry.VPos.CENTER, javafx.scene.layout.Priority.ALWAYS, javafx.scene.layout.Priority.NEVER, new javafx.geometry.Insets(5,5,5,5) );
      javafx.scene.layout.GridPane.setConstraints( _unsupportiveTestsTable, 0, 1, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );

      unsupportiveTestsPane.getChildren().addAll( label,
                                                  _unsupportiveTestsTable );

      return unsupportiveTestsPane;
   }

   final private void readConfiguration()
   {
      boolean                                                                      tagWindowConfigured;
      java.io.File                                                                 configurationFile;
      double                                                                       x;
      double                                                                       y;
      double                                                                       width;
      double                                                                       height;
      javafx.geometry.Rectangle2D                                                  stageBounds;
      java.util.List<Double>                                                       columnWidths;
      int                                                                          elementCount;
      int                                                                          index;
      java.util.List<javafx.scene.control.TreeTableColumn<MainTableItemWrapper,?>> treeTableColumns;
      int                                                                          loopCount;
      java.util.List<javafx.scene.control.TableColumn<TestData,?>>                 unsupportiveTestsColumns;
      java.util.List<javafx.scene.control.TableColumn<RequirementData,?>>          requirementsColumns;

      tagWindowConfigured = false;

      configurationFile = getConfigurationFile();

      if ( configurationFile.exists() )
      {
         try ( java.io.FileInputStream fileInputStream = new java.io.FileInputStream(configurationFile) )
         {
            try ( java.io.BufferedInputStream bufferedInputStream = new java.io.BufferedInputStream(fileInputStream,262144) )
            {
               try ( java.io.DataInputStream dataInputStream = new java.io.DataInputStream(bufferedInputStream) )
               {
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

                  ////

                  columnWidths = new java.util.ArrayList<Double>();
                  elementCount = dataInputStream.readInt();
                  for ( index=0; index<elementCount; index++ )
                  {
                     columnWidths.add( dataInputStream.readDouble() );
                  }

                  treeTableColumns = _resultsTree.getColumns();
                  if ( treeTableColumns != null )
                  {
                     if ( treeTableColumns.size() > columnWidths.size() )
                     {
                        loopCount = columnWidths.size();
                     }
                     else
                     {
                        loopCount = treeTableColumns.size();
                     }
                     for ( index=0; index<loopCount; index++ )
                     {
                        if ( (columnWidths.get(index)>5) && (columnWidths.get(index)<1000) )
                        {
                           treeTableColumns.get( index ).setPrefWidth( columnWidths.get(index) );
                        }
                     }
                  }

                  ////

                  columnWidths = new java.util.ArrayList<Double>();
                  elementCount = dataInputStream.readInt();
                  for ( index=0; index<elementCount; index++ )
                  {
                     columnWidths.add( dataInputStream.readDouble() );
                  }

                  requirementsColumns = _requirementsTable.getColumns();
                  if ( requirementsColumns != null )
                  {
                     if ( requirementsColumns.size() > columnWidths.size() )
                     {
                        loopCount = columnWidths.size();
                     }
                     else
                     {
                        loopCount = requirementsColumns.size();
                     }
                     for ( index=0; index<loopCount; index++ )
                     {
                        if ( (columnWidths.get(index)>5) && (columnWidths.get(index)<1000) )
                        {
                           requirementsColumns.get( index ).setPrefWidth( columnWidths.get(index) );
                        }
                     }
                  }

                  ////

                  columnWidths = new java.util.ArrayList<Double>();
                  elementCount = dataInputStream.readInt();
                  for ( index=0; index<elementCount; index++ )
                  {
                     columnWidths.add( dataInputStream.readDouble() );
                  }

                  unsupportiveTestsColumns = _unsupportiveTestsTable.getColumns();
                  if ( unsupportiveTestsColumns != null )
                  {
                     if ( unsupportiveTestsColumns.size() > columnWidths.size() )
                     {
                        loopCount = columnWidths.size();
                     }
                     else
                     {
                        loopCount = unsupportiveTestsColumns.size();
                     }
                     for ( index=0; index<loopCount; index++ )
                     {
                        if ( (columnWidths.get(index)>5) && (columnWidths.get(index)<1000) )
                        {
                           unsupportiveTestsColumns.get( index ).setPrefWidth( columnWidths.get(index) );
                        }
                     }
                  }

                  ////

                  _tagWindow.readConfiguration( dataInputStream );
                  tagWindowConfigured = true;
               }
            }
         }
         catch ( Throwable throwable )
         {
            Utilities.printStackTrace( throwable );
         }
      }

      if ( !tagWindowConfigured )
      {
         _tagWindow.show( true );
         _tagWindow.show( false );
      }
   }

   final void onCloseRequest( javafx.event.Event event )
   {
      saveConfiguration();
      _tagWindow.show( false );
      _undercamber.saveAndClose( event );
   }

   final private void handleKeyPress( javafx.scene.input.KeyEvent event )
   {
      if ( Utilities.ESCAPE_KEYBOARD_COMBINATION.match(event) )
      {
         onCloseRequest( event );
      }
      else if ( Utilities.ENTER_KEYBOARD_COMBINATION.match(event) )
      {
         onCloseRequest( event );
      }
   }

   final private void saveConfiguration()
   {
      java.io.File                                                                 configurationFile;
      java.util.List<javafx.scene.control.TreeTableColumn<MainTableItemWrapper,?>> resultsTreeColumns;
      java.util.List<javafx.scene.control.TableColumn<TestData,?>>                 unsupportiveTestsColumns;
      java.util.List<javafx.scene.control.TableColumn<RequirementData,?>>          requirementsColumns;

      configurationFile = getConfigurationFile();

      try ( java.io.FileOutputStream fileOutputStream = new java.io.FileOutputStream(configurationFile) )
      {
         try ( java.io.BufferedOutputStream bufferedOutputStream = new java.io.BufferedOutputStream(fileOutputStream) )
         {
            try ( java.io.DataOutputStream dataOutputStream = new java.io.DataOutputStream(bufferedOutputStream) )
            {
               dataOutputStream.writeInt( CLASS_PERSISTENCE_VERSION );
               dataOutputStream.writeUTF( CLASS_PERSISTENCE_BRANCH );

               dataOutputStream.writeDouble( _stage.getX() );
               dataOutputStream.writeDouble( _stage.getY() );
               dataOutputStream.writeDouble( _stage.getWidth() );
               dataOutputStream.writeDouble( _stage.getHeight() );

               ////

               resultsTreeColumns = _resultsTree.getColumns();
               dataOutputStream.writeInt( resultsTreeColumns.size() );
               for ( javafx.scene.control.TreeTableColumn<MainTableItemWrapper,?> column : resultsTreeColumns )
               {
                  dataOutputStream.writeDouble( column.getWidth() );
               }

               ////

               requirementsColumns = _requirementsTable.getColumns();
               dataOutputStream.writeInt( requirementsColumns.size() );
               for ( javafx.scene.control.TableColumn<RequirementData,?> column : requirementsColumns )
               {
                  dataOutputStream.writeDouble( column.getWidth() );
               }

               ////

               unsupportiveTestsColumns = _unsupportiveTestsTable.getColumns();
               dataOutputStream.writeInt( unsupportiveTestsColumns.size() );
               for ( javafx.scene.control.TableColumn<TestData,?> column : unsupportiveTestsColumns )
               {
                  dataOutputStream.writeDouble( column.getWidth() );
               }

               ////

               _tagWindow.writeConfiguration( dataOutputStream );
            }
         }
      }
      catch ( java.io.IOException ioException )
      {
         ioException.printStackTrace();
      }
   }

   final private java.io.File getConfigurationFile()
   {
      java.io.File configurationFile;

      configurationFile = new java.io.File( System.getProperty("user.home") );
      configurationFile = new java.io.File( configurationFile, ".Undercamber" );
      configurationFile = new java.io.File( configurationFile, _testSuiteName );
      configurationFile = new java.io.File( configurationFile, "ResultsWindowConfiguration.dat" );

      return configurationFile;
   }
}

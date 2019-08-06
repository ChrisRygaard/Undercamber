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

final class SelectionWindow
{
   final private static int                               CLASS_PERSISTENCE_VERSION      = 0;
   final private static String                            CLASS_PERSISTENCE_BRANCH       = "";
   final static         javafx.scene.input.KeyCombination CONTROL_A_KEYBOARD_COMBINATION = new javafx.scene.input.KeyCodeCombination( javafx.scene.input.KeyCode.A,
                                                                                                                                      javafx.scene.input.KeyCombination.ModifierValue.UP,
                                                                                                                                      javafx.scene.input.KeyCombination.ModifierValue.DOWN,
                                                                                                                                      javafx.scene.input.KeyCombination.ModifierValue.UP,
                                                                                                                                      javafx.scene.input.KeyCombination.ModifierValue.UP,
                                                                                                                                      javafx.scene.input.KeyCombination.ModifierValue.UP );

   private javafx.stage.Stage                                       _stage;
   private String                                                   _testSuiteName;
   private javafx.scene.control.Button                              _runSelectedTestsButton;
   private javafx.scene.control.Button                              _cancelButton;
   private javafx.scene.control.Button                              _runAllTestsButton;
   private javafx.scene.control.TreeTableView<MainTableItemWrapper> _selectionTable;
   private TestData                                                 _testDataDummyRoot;
   private TagWindow                                                _tagWindow;
   private RequirementsWindow                                       _requirementsWindow;

   SelectionWindow( String                  testSuiteName,
                    javafx.stage.Stage      stage,
                    java.util.List<TestSet> testSets,
                    TestData                testDataDummyRoot,
                    java.util.List<String>  commandLineTestParameters,
                    Undercamber             undercamber )
   {
      javafx.scene.layout.GridPane    contentPane;
      javafx.scene.Scene              scene;
      java.util.Set<RequirementData>  requirementsSet;
      java.util.List<RequirementData> requirements;

      _stage = stage;
      _testSuiteName = testSuiteName;
      _testDataDummyRoot = testDataDummyRoot;

      _tagWindow = new TagWindow( testDataDummyRoot,
                                  true,
                                  undercamber,
                                  stage );

      requirementsSet = new java.util.HashSet<RequirementData>();
      testDataDummyRoot.addRequirementsToSet( requirementsSet );
      requirements = new java.util.ArrayList<RequirementData>();
      requirements.addAll( requirementsSet );
      java.util.Collections.sort( requirements );
      _requirementsWindow = new RequirementsWindow( "Requirements validated in " + testSuiteName,
                                                    false,
                                                    requirements,
                                                    stage );

      contentPane = makeContentPane( testSets,
                                     commandLineTestParameters,
                                     stage,
                                     undercamber );

      scene = new javafx.scene.Scene( contentPane );

      scene.addEventFilter( javafx.scene.input.KeyEvent.KEY_PRESSED,
                            event -> handleKeyPress(event,
                                                    undercamber) );

      stage.setScene( scene );
      stage.setOnCloseRequest( event -> onCloseRequest(event,
                                                       undercamber) );
      stage.setResizable( true );
      stage.setTitle( "Undercamber(TM):  " + testSuiteName );
      stage.addEventFilter( javafx.scene.input.KeyEvent.KEY_PRESSED,
                            event -> handleKeyPress(event,
                                                    undercamber) );
      stage.show();

      readConfiguration();
   }

   final private javafx.scene.layout.GridPane makeContentPane( java.util.List<TestSet> testSets,
                                                               java.util.List<String>  commandLineTestParameters,
                                                               javafx.stage.Window     ownerWindow,
                                                               Undercamber             undercamber )
   {
      MainTableItemWrapper                                                            wrapperDummyRoot;
      MainTableTreeItem                                                               guiRoot;
      javafx.scene.control.TreeTableColumn<MainTableItemWrapper,MainTableItemWrapper> tableColumn;
      javafx.scene.layout.Pane                                                        allTestsButtonPane;
      javafx.scene.layout.Pane                                                        runButtonPane;
      javafx.scene.layout.GridPane                                                    contentPane;
      javafx.scene.layout.RowConstraints                                              row0Constraints;
      javafx.scene.layout.RowConstraints                                              row1Constraints;
      javafx.scene.layout.RowConstraints                                              row2Constraints;
      javafx.scene.layout.ColumnConstraints                                           column0Constraints;

      wrapperDummyRoot = new MainTableItemWrapper( testSets );

      _selectionTable = new javafx.scene.control.TreeTableView<MainTableItemWrapper>();
      _selectionTable.setShowRoot( false );
      _selectionTable.getSelectionModel().setSelectionMode( javafx.scene.control.SelectionMode.SINGLE );

      guiRoot = new MainTableTreeItem( true,
                                       _selectionTable,
                                       wrapperDummyRoot );
      _selectionTable.setRoot( guiRoot );

      tableColumn = new javafx.scene.control.TreeTableColumn<MainTableItemWrapper,MainTableItemWrapper>( "Method" );
      tableColumn.setCellValueFactory( param -> new NoOpObservable<MainTableItemWrapper>(param.getValue().getValue()) );
      tableColumn.setCellFactory( treeTableColumn -> new SelectionTreeColumnCell(undercamber,
                                                                                 commandLineTestParameters,
                                                                                 ownerWindow) );
      tableColumn.setSortable( false );
      _selectionTable.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TreeTableColumn<MainTableItemWrapper,MainTableItemWrapper>( "Select Branch" );
      tableColumn.setCellValueFactory( param -> new NoOpObservable<MainTableItemWrapper>(param.getValue().getValue()) );
      tableColumn.setCellFactory( treeTableColumn -> new SelectBranchCell(true) );
      tableColumn.setSortable( false );
      _selectionTable.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TreeTableColumn<MainTableItemWrapper,MainTableItemWrapper>( "Deselect Branch" );
      tableColumn.setCellValueFactory( param -> new NoOpObservable<MainTableItemWrapper>(param.getValue().getValue()) );
      tableColumn.setCellFactory( treeTableColumn -> new SelectBranchCell(false) );
      tableColumn.setSortable( false );
      _selectionTable.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TreeTableColumn<MainTableItemWrapper,MainTableItemWrapper>( "Dependencies" );
      tableColumn.setCellValueFactory( param -> new NoOpObservable<MainTableItemWrapper>(param.getValue().getValue()) );
      tableColumn.setCellFactory( treeTableColumn -> new ShowDependenciesCell(true,
                                                                              undercamber,
                                                                              ownerWindow) );
      tableColumn.setSortable( false );
      _selectionTable.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TreeTableColumn<MainTableItemWrapper,MainTableItemWrapper>( "Requirements" );
      tableColumn.setCellValueFactory( param -> new NoOpObservable<MainTableItemWrapper>(param.getValue().getValue()) );
      tableColumn.setCellFactory( treeTableColumn -> new ShowRequirementsCell(false,
                                                                              ownerWindow) );
      tableColumn.setSortable( false );
      _selectionTable.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TreeTableColumn<MainTableItemWrapper,MainTableItemWrapper>( "Tags" );
      tableColumn.setCellValueFactory( param -> new NoOpObservable<MainTableItemWrapper>(param.getValue().getValue()) );
      tableColumn.setCellFactory( treeTableColumn -> new TagCell() );
      tableColumn.setSortable( false );
      _selectionTable.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TreeTableColumn<MainTableItemWrapper,MainTableItemWrapper>( "Expand Branch" );
      tableColumn.setCellValueFactory( param -> new NoOpObservable<MainTableItemWrapper>(param.getValue().getValue()) );
      tableColumn.setCellFactory( treeTableColumn -> new ExpandBranchCell(true,
                                                                          true) );
      tableColumn.setSortable( false );
      _selectionTable.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TreeTableColumn<MainTableItemWrapper,MainTableItemWrapper>( "Collapse Branch" );
      tableColumn.setCellValueFactory( param -> new NoOpObservable<MainTableItemWrapper>(param.getValue().getValue()) );
      tableColumn.setCellFactory( treeTableColumn -> new ExpandBranchCell(true,
                                                                          false) );
      tableColumn.setSortable( false );
      _selectionTable.getColumns().add( tableColumn );

      allTestsButtonPane = makeAllTestButtonPane();

      runButtonPane = makeRunButtonPane( undercamber );

      contentPane = new javafx.scene.layout.GridPane();

      row0Constraints = new javafx.scene.layout.RowConstraints();
      row0Constraints.setVgrow( javafx.scene.layout.Priority.ALWAYS );
      row1Constraints = new javafx.scene.layout.RowConstraints();
      row1Constraints.setVgrow( javafx.scene.layout.Priority.NEVER );
      row2Constraints = new javafx.scene.layout.RowConstraints();
      row2Constraints.setVgrow( javafx.scene.layout.Priority.NEVER );
      contentPane.getRowConstraints().addAll( row0Constraints,
                                              row1Constraints,
                                              row2Constraints );

      column0Constraints = new javafx.scene.layout.ColumnConstraints();
      column0Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      contentPane.getColumnConstraints().addAll( column0Constraints );

      javafx.scene.layout.GridPane.setConstraints( _selectionTable,    0, 0, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setConstraints( allTestsButtonPane, 0, 1, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setConstraints( runButtonPane,      0, 2, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );

      contentPane.getChildren().addAll( _selectionTable,
                                        allTestsButtonPane,
                                        runButtonPane );

      return contentPane;
   }

   final private javafx.scene.layout.Pane makeRunButtonPane( Undercamber undercamber )
   {
      javafx.scene.layout.GridPane          buttonPane;
      javafx.scene.layout.ColumnConstraints column0Constraints;
      javafx.scene.layout.ColumnConstraints column1Constraints;
      javafx.scene.layout.ColumnConstraints column2Constraints;

      _runSelectedTestsButton = new javafx.scene.control.Button( "Run Selected Tests (Enter)" );
      _runSelectedTestsButton.setOnAction( event -> runButtonClicked(false,
                                                                     undercamber) );

      _cancelButton = new javafx.scene.control.Button( "Close Without Running Tests (Esc)" );
      _cancelButton.setOnAction( event -> onCloseRequest(event,
                                                         undercamber) );

      _runAllTestsButton = new javafx.scene.control.Button( "Run All Tests (Ctrl-A)" );
      _runAllTestsButton.setOnAction( event -> runButtonClicked(true,
                                                                undercamber) );

      buttonPane = new javafx.scene.layout.GridPane();
      buttonPane.setHgap( 0 );
      buttonPane.setVgap( 0 );
      buttonPane.setPadding( new javafx.geometry.Insets(1,1,1,1) );

      column0Constraints = new javafx.scene.layout.ColumnConstraints();
      column0Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      column1Constraints = new javafx.scene.layout.ColumnConstraints();
      column1Constraints.setHgrow( javafx.scene.layout.Priority.NEVER );
      column2Constraints = new javafx.scene.layout.ColumnConstraints();
      column2Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      buttonPane.getColumnConstraints().addAll( column0Constraints,
                                                column1Constraints,
                                                column2Constraints );

      javafx.scene.layout.GridPane.setConstraints( _runSelectedTestsButton, 0, 0, 1, 1, javafx.geometry.HPos.RIGHT,  javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setConstraints( _cancelButton,           1, 0, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setConstraints( _runAllTestsButton,      2, 0, 1, 1, javafx.geometry.HPos.LEFT,   javafx.geometry.VPos.CENTER );

      buttonPane.getChildren().addAll( _runSelectedTestsButton,
                                       _cancelButton,
                                       _runAllTestsButton );

      return buttonPane;
   }

   final private javafx.scene.layout.Pane makeAllTestButtonPane()
   {
      javafx.scene.control.Button           selectAllButton;
      javafx.scene.control.Button           deselectAllButton;
      javafx.scene.control.Button           collapseAllButton;
      javafx.scene.control.Button           expandAllButton;
      javafx.scene.control.Button           showTagsButton;
      javafx.scene.control.Button           showRequirementsButton;
      javafx.scene.control.Button           resetButton;
      javafx.scene.layout.GridPane          buttonPane;
      javafx.scene.layout.ColumnConstraints column0Constraints;
      javafx.scene.layout.ColumnConstraints column1Constraints;
      javafx.scene.layout.ColumnConstraints column2Constraints;
      javafx.scene.layout.ColumnConstraints column3Constraints;
      javafx.scene.layout.ColumnConstraints column4Constraints;
      javafx.scene.layout.ColumnConstraints column5Constraints;
      javafx.scene.layout.ColumnConstraints column6Constraints;

      selectAllButton = new javafx.scene.control.Button( "Select All Tests" );
      selectAllButton.setOnAction( event -> _testDataDummyRoot.setRunOnBranch(true,
                                                                              false) );

      deselectAllButton = new javafx.scene.control.Button( "Deselect All Tests" );
      deselectAllButton.setOnAction( event -> _testDataDummyRoot.setRunOnBranch(false,
                                                                                false) );

      expandAllButton = new javafx.scene.control.Button( "Expand All" );
      expandAllButton.setOnAction( event -> _testDataDummyRoot.setExpandedOnBranch(true,
                                                                                   true) );

      collapseAllButton = new javafx.scene.control.Button( "Collapse All" );
      collapseAllButton.setOnAction( event -> _testDataDummyRoot.setExpandedOnBranch(true,
                                                                                     false) );

      showTagsButton = new javafx.scene.control.Button( "Show Tags..." );
      showTagsButton.setOnAction( event -> _tagWindow.show(true) );

      showRequirementsButton = new javafx.scene.control.Button( "Show Requirements..." );
      showRequirementsButton.setOnAction( event -> _requirementsWindow.show(true) );

      resetButton = new javafx.scene.control.Button( "Reset All" );
      resetButton.setOnAction( event -> _testDataDummyRoot.reset(true,
                                                                 false) );

      buttonPane = new javafx.scene.layout.GridPane();
      buttonPane.setHgap( 0 );
      buttonPane.setVgap( 0 );
      buttonPane.setPadding( new javafx.geometry.Insets(1,1,1,1) );

      column0Constraints = new javafx.scene.layout.ColumnConstraints();
      column0Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      column1Constraints = new javafx.scene.layout.ColumnConstraints();
      column1Constraints.setHgrow( javafx.scene.layout.Priority.NEVER );
      column2Constraints = new javafx.scene.layout.ColumnConstraints();
      column2Constraints.setHgrow( javafx.scene.layout.Priority.NEVER );
      column3Constraints = new javafx.scene.layout.ColumnConstraints();
      column3Constraints.setHgrow( javafx.scene.layout.Priority.NEVER );
      column4Constraints = new javafx.scene.layout.ColumnConstraints();
      column4Constraints.setHgrow( javafx.scene.layout.Priority.NEVER );
      column5Constraints = new javafx.scene.layout.ColumnConstraints();
      column5Constraints.setHgrow( javafx.scene.layout.Priority.NEVER );
      column6Constraints = new javafx.scene.layout.ColumnConstraints();
      column6Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      buttonPane.getColumnConstraints().addAll( column0Constraints,
                                                column1Constraints,
                                                column2Constraints,
                                                column3Constraints,
                                                column4Constraints,
                                                column5Constraints,
                                                column6Constraints );

      javafx.scene.layout.GridPane.setConstraints( selectAllButton,        0, 0, 1, 1, javafx.geometry.HPos.RIGHT,  javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setConstraints( deselectAllButton,      1, 0, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setConstraints( expandAllButton,        2, 0, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setConstraints( collapseAllButton,      3, 0, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setConstraints( showTagsButton,         4, 0, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setConstraints( showRequirementsButton, 5, 0, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setConstraints( resetButton,            6, 0, 1, 1, javafx.geometry.HPos.LEFT,   javafx.geometry.VPos.CENTER );

      buttonPane.getChildren().addAll( selectAllButton,
                                       deselectAllButton,
                                       expandAllButton,
                                       collapseAllButton,
                                       showTagsButton,
                                       showRequirementsButton,
                                       resetButton );

      return buttonPane;
   }

   final void closeWindows()
   {
      _tagWindow.show( false );
      _requirementsWindow.show( false );
   }

   final private void readConfiguration()
   {
      boolean                                                                      tagWindowConfigured;
      boolean                                                                      requirementsWindowConfigured;
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

      tagWindowConfigured = false;
      requirementsWindowConfigured = false;

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

                  setColumnWidths( columnWidths );

                  treeTableColumns = _selectionTable.getColumns();
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

                  _tagWindow.readConfiguration( dataInputStream );
                  tagWindowConfigured = true;

                  _requirementsWindow.readConfiguration( dataInputStream );
                  requirementsWindowConfigured = true;
               }
            }
         }
         catch ( java.io.IOException ioException )
         {
            Utilities.printStackTrace( ioException );
         }
      }

      if ( !tagWindowConfigured )
      {
         _tagWindow.show( true );
         _tagWindow.show( false );
      }

      if ( !requirementsWindowConfigured )
      {
         _requirementsWindow.show( true );
         _requirementsWindow.show( false );
      }
   }

   final void setColumnWidths( java.util.List<Double> columnWidths )
   {
      java.util.List<javafx.scene.control.TreeTableColumn<MainTableItemWrapper,?>> columns;
      int                                                                          loopCount;
      int                                                                          index;

      if ( columnWidths == null )
      {
         return;
      }

      columns = _selectionTable.getColumns();

      if ( columns == null )
      {
         return;
      }

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

   final private void runButtonClicked( boolean     runAllTests,
                                        Undercamber undercamber )
   {
      _tagWindow.show( false );

      if ( runAllTests )
      {
         undercamber.runAllTests();
      }
      else
      {
         undercamber.runGUISelectedTests();
      }
   }

   final private void handleKeyPress( javafx.scene.input.KeyEvent event,
                                      Undercamber                 undercamber )
   {
      if ( Utilities.ENTER_KEYBOARD_COMBINATION.match(event) )
      {
         if ( !((event.getTarget()) instanceof javafx.scene.control.TextArea) )
         {
            event.consume();
            undercamber.runGUISelectedTests();
         }
      }
      else if ( CONTROL_A_KEYBOARD_COMBINATION.match(event) )
      {
         event.consume();
         undercamber.runAllTests();
      }
      else if ( Utilities.ESCAPE_KEYBOARD_COMBINATION.match(event) )
      {
         onCloseRequest( event,
                         undercamber );
      }
   }

   final void onCloseRequest( javafx.event.Event event,
                              Undercamber        undercamber )
   {
      saveAndClose( event );
      undercamber.saveAndClose( event );
   }

   final void saveAndClose( javafx.event.Event event )
   {
      saveConfiguration();
      _tagWindow.show( false );
      _stage.close();
   }

   final private void saveConfiguration()
   {
      java.io.File                                                                 configurationFile;
      java.util.List<javafx.scene.control.TreeTableColumn<MainTableItemWrapper,?>> resultsTreeColumns;

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

               resultsTreeColumns = _selectionTable.getColumns();
               dataOutputStream.writeInt( resultsTreeColumns.size() );
               for ( javafx.scene.control.TreeTableColumn<MainTableItemWrapper,?> column : resultsTreeColumns )
               {
                  dataOutputStream.writeDouble( column.getWidth() );
               }

               ////

               _tagWindow.writeConfiguration( dataOutputStream );

               _requirementsWindow.writeConfiguration( dataOutputStream );
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
      configurationFile = new java.io.File( configurationFile, "SelectionWindowConfiguration.dat" );

      return configurationFile;
   }
}

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

final class SelectionTreeColumnCell
   extends javafx.scene.control.TreeTableCell<MainTableItemWrapper,MainTableItemWrapper>
   implements TestDataListener
{
   private Undercamber                      _undercamber;
   private javafx.stage.Window              _ownerWindow;
   private MainTableItemWrapper             _mainTableItemWrapper;
   private javafx.scene.control.CheckBox    _checkBox;
   private javafx.scene.control.Label       _testDataLabel;
   private javafx.scene.control.Label       _testSetLabel;
   private javafx.scene.layout.GridPane     _gridPane;
   private javafx.scene.control.ContextMenu _popupMenu;
   private javafx.scene.control.MenuItem    _runThisTestMenuItem;
   private javafx.scene.control.MenuItem    _runThisTestAndSubtestsMenuItem;
   private javafx.scene.control.MenuItem    _runTestSetMenuItem;
   private javafx.scene.control.MenuItem    _viewTestSetMenuItem;
   private java.util.List<String>           _commandLineTestParameters;

   SelectionTreeColumnCell( Undercamber            undercamber,
                            java.util.List<String> commandLineTestParameters,
                            javafx.stage.Window    ownerWindow )
   {
      javafx.scene.layout.ColumnConstraints column0Constraints;
      javafx.scene.layout.ColumnConstraints column1Constraints;
      javafx.scene.layout.RowConstraints    row0Constraints;

      _ownerWindow = ownerWindow;

      _commandLineTestParameters = new java.util.ArrayList<String>();
      _commandLineTestParameters.addAll( commandLineTestParameters );

      setOnMouseClicked( event -> onMouseClicked(event) );

      _checkBox = new javafx.scene.control.CheckBox();
      _checkBox.selectedProperty().addListener( (observable,oldValue,newValue) -> checkBoxUpdated() );
      _testDataLabel = new javafx.scene.control.Label();
      _testSetLabel = new javafx.scene.control.Label();

      _undercamber = undercamber;

      _gridPane = new javafx.scene.layout.GridPane();

      column0Constraints = new javafx.scene.layout.ColumnConstraints();
      column0Constraints.setHgrow( javafx.scene.layout.Priority.NEVER );
      column1Constraints = new javafx.scene.layout.ColumnConstraints();
      column1Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      _gridPane.getColumnConstraints().addAll( column0Constraints,
                                               column1Constraints );

      row0Constraints = new javafx.scene.layout.RowConstraints();
      row0Constraints.setVgrow( javafx.scene.layout.Priority.ALWAYS );
      _gridPane.getRowConstraints().addAll( row0Constraints );

      javafx.scene.layout.GridPane.setConstraints( _checkBox,      0, 0, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setConstraints( _testDataLabel, 1, 0, 1, 1, javafx.geometry.HPos.LEFT,   javafx.geometry.VPos.CENTER );

      _gridPane.getChildren().addAll( _checkBox,
                                      _testDataLabel );

      setupPopupMenu();

      setContextMenu( _popupMenu );
   }

   final public void updateItem( MainTableItemWrapper mainTableItemWrapper,
                                 boolean              empty )
   {
      super.updateItem( mainTableItemWrapper,
                        empty );

      if ( empty )
      {
         setText( null );
         setGraphic( null );
         setStyle( "" );
      }
      else
      {
         if ( _mainTableItemWrapper != null )
         {
            if ( _mainTableItemWrapper.isTestData() )
            {
               _mainTableItemWrapper.getTestData().removeListenerAllInstances( this );
            }
         }

         _mainTableItemWrapper = mainTableItemWrapper;

         if ( mainTableItemWrapper == null )
         {
            setText( null );
            setGraphic( null );
            setStyle( "" );
         }
         else
         {
            updateGraphic();
            if ( mainTableItemWrapper.isTestData() )
            {
               mainTableItemWrapper.getTestData().addListener( this );
               setGraphic( _gridPane );
               setStyle( "" );
            }
            else
            {
               setGraphic( _testSetLabel );
               setStyle( "-fx-background-color: paleturquoise" );
            }
         }
      }

      updatePopupMenu();
   }

   final private void checkBoxUpdated()
   {
      MainTableItemWrapper mainTableItemWrapper;

      mainTableItemWrapper = getItem();

      if ( mainTableItemWrapper != null )
      {
         if ( mainTableItemWrapper.isTestData() )
         {
            if ( mainTableItemWrapper.getTestData().getRun(false) != _checkBox.isSelected() )
            {
               _mainTableItemWrapper.getTestData().setRun( _checkBox.isSelected(),
                                                           false );
            }
         }
      }
   }

   final private void updateGraphic()
   {
      MainTableItemWrapper mainTableItemWrapper;
      TestData             testData;

      mainTableItemWrapper = getItem();

      if ( mainTableItemWrapper != null )
      {
         if ( mainTableItemWrapper.isTestData() )
         {
            testData = mainTableItemWrapper.getTestData();

            if ( testData.getChildCount() == 0 )
            {
               _testDataLabel.setText( testData.getHeading() );
            }
            else
            {
               switch ( testData.getSubtestSequencingMode() )
               {
                  case CONCURRENT:
                  {
                     _testDataLabel.setText( testData.getHeading() + "  (P)" );
                     break;
                  }
                  case SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR:
                  {
                     _testDataLabel.setText( testData.getHeading() + "  (S/A)" );
                     break;
                  }
                  case SEQUENTIAL_CONTINUE_ON_ERROR:
                  {
                     _testDataLabel.setText( testData.getHeading() + "  (S/C)" );
                     break;
                  }
                  default:
                  {
                     _testDataLabel.setText( testData.getHeading() + "  (?)" );
                     Utilities.printStackTrace( new Exception("Unrecognized subtest sequencing mode;  "+testData.getSubtestSequencingMode()) );
                  }
               }
            }

            if ( testData.getRun(false) != _checkBox.isSelected() )
            {
               _checkBox.setSelected( testData.getRun(false) );
            }

            _testDataLabel.setDisable( testData.hasNonRunningAncestor(false) );
         }
         else
         {
            _testSetLabel.setText( mainTableItemWrapper.getTestSet().getTestSetName() );
            _testSetLabel.setDisable( false );
         }
      }
   }

   final private void onMouseClicked( javafx.scene.input.MouseEvent event )
   {
      if ( event.getClickCount() == 2 )
      {
         if ( event.getButton() == javafx.scene.input.MouseButton.PRIMARY )
         {
            event.consume();
            run( true );
         }
      }
   }

   final private void updatePopupMenu()
   {
      MainTableItemWrapper mainTableItemWrapper;

      mainTableItemWrapper = getItem();

      if ( mainTableItemWrapper != null )
      {
         _popupMenu.getItems().clear();

         if ( mainTableItemWrapper.isTestData() )
         {
            _popupMenu.getItems().add( _runThisTestMenuItem );
            _popupMenu.getItems().add( _runThisTestAndSubtestsMenuItem );
         }
         else
         {
            _popupMenu.getItems().add( _runTestSetMenuItem );
            _popupMenu.getItems().add( _viewTestSetMenuItem );
         }
      }
   }

   final private void setupPopupMenu()
   {
      _popupMenu = new javafx.scene.control.ContextMenu();

      _runThisTestMenuItem = new javafx.scene.control.MenuItem( "Run this test" );
      _runThisTestMenuItem.setOnAction( event -> run(false) );

      _runThisTestAndSubtestsMenuItem = new javafx.scene.control.MenuItem( "Run this test and subtests (double-click)" );
      _runThisTestAndSubtestsMenuItem.setOnAction( event -> run(true) );

      _runTestSetMenuItem = new javafx.scene.control.MenuItem( "Run this test set (double-click)" );
      _runTestSetMenuItem.setOnAction( event -> run(true) );

      _viewTestSetMenuItem = new javafx.scene.control.MenuItem( "View test set environment" );
      _viewTestSetMenuItem.setOnAction( event -> showTestSetWindow() );
   }

   final private void run( boolean runSubtests )
   {
      MainTableItemWrapper mainTableItemWrapper;

      mainTableItemWrapper = getItem();

      if ( mainTableItemWrapper != null )
      {
         if ( mainTableItemWrapper.isTestData() )
         {
            _undercamber.runTest( mainTableItemWrapper.getTestData(),
                                  runSubtests );
         }
         else
         {
            _undercamber.runTest( mainTableItemWrapper.getTestSet().getPass1TestData(),
                                  true );
         }
      }
   }

   final private void showTestSetWindow()
   {
      MainTableItemWrapper mainTableItemWrapper;

      mainTableItemWrapper = getItem();

      if ( mainTableItemWrapper != null )
      {
         if ( !(mainTableItemWrapper.isTestData()) )
         {
            mainTableItemWrapper.getTestSet().getTestSetWindow( _commandLineTestParameters,
                                                                _ownerWindow ).show();
         }
      }
   }

   // Methods from TestDataListener

   final public void updated( TestData testData )
   {
      updateGraphic();
   }

   final public void ancestorUpdated( TestData testData,
                                      TestData updatedAncestor )
   {
      updateGraphic();
   }

   final public void descendantUpdated( TestData testData,
                                        TestData updatedDescendant )
   {
   }
}

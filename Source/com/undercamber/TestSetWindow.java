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

final class TestSetWindow
{
   private javafx.stage.Stage _stage;

   TestSetWindow( TestSet                testSet,
                  java.util.List<String> commandLineTestParameters,
                  javafx.stage.Window    ownerWindow )
   {
      javafx.scene.Scene scene;

      _stage = new javafx.stage.Stage();

      scene = new javafx.scene.Scene( makeContentPane(testSet,
                                                      commandLineTestParameters) );

      _stage.setScene( scene );

      scene.addEventFilter( javafx.scene.input.KeyEvent.KEY_PRESSED,
                            event -> handleKeyPress(event) );
      _stage.addEventFilter( javafx.scene.input.KeyEvent.KEY_PRESSED,
                             event -> handleKeyPress(event) );

      _stage.setTitle( "Test Set " + testSet.getTestSetName() );

      _stage.setOnCloseRequest( event -> onCloseRequest() );

      _stage.initOwner( ownerWindow );

      _stage.show();
   }

   final private javafx.scene.layout.Pane makeContentPane( TestSet                testSet,
                                                           java.util.List<String> commandLineTestParameters )
   {
      javafx.scene.layout.GridPane          gridPane;
      javafx.scene.layout.ColumnConstraints column0Constraints;
      javafx.scene.layout.RowConstraints    row0Constraints;
      javafx.scene.layout.RowConstraints    row1Constraints;
      javafx.scene.control.TabPane          tabPane;
      javafx.scene.layout.Pane              buttonPane;

      gridPane = new javafx.scene.layout.GridPane();

      column0Constraints = new javafx.scene.layout.ColumnConstraints();
      column0Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      gridPane.getColumnConstraints().addAll( column0Constraints );

      row0Constraints = new javafx.scene.layout.RowConstraints();
      row0Constraints.setVgrow( javafx.scene.layout.Priority.ALWAYS );
      row1Constraints = new javafx.scene.layout.RowConstraints();
      row1Constraints = new javafx.scene.layout.RowConstraints();
      gridPane.getRowConstraints().addAll( row0Constraints,
                                           row1Constraints );

      tabPane = makeTabPane( testSet,
                             commandLineTestParameters );
      buttonPane = makeButtonPane();

      javafx.scene.layout.GridPane.setConstraints( tabPane,    0, 0, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setConstraints( buttonPane, 0, 1, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );

      gridPane.getChildren().addAll( tabPane,
                                     buttonPane );

      return gridPane;
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

   final private javafx.scene.control.TabPane makeTabPane( TestSet                testSet,
                                                           java.util.List<String> commandLineTestParameters )
   {
      javafx.scene.control.TabPane tabPane;
      javafx.scene.control.Tab     tab;

      tabPane = new javafx.scene.control.TabPane();

      tab = new javafx.scene.control.Tab( "Details",
                                          makeDetailsPane(testSet) );
      tab.setClosable( false );
      tabPane.getTabs().add( tab );

      tab = new javafx.scene.control.Tab( "Java Parameters",
                                          makeParameterList(new java.util.ArrayList<String>(),
                                                            testSet.getJavaParameters()) );
      tab.setClosable( false );
      tabPane.getTabs().add( tab );

      tab = new javafx.scene.control.Tab( "Test Parameters",
                                          makeParameterList(commandLineTestParameters,
                                                            testSet.getTestParameters()) );
      tab.setClosable( false );
      tabPane.getTabs().add( tab );

      tab = new javafx.scene.control.Tab( "Environment Variables",
                                          makeEnvironmentVariableTable(testSet) );
      tab.setClosable( false );
      tabPane.getTabs().add( tab );

      return tabPane;
   }

   final private javafx.scene.layout.Pane makeDetailsPane( TestSet testSet )
   {
      javafx.scene.layout.GridPane               gridPane;
      javafx.scene.layout.ColumnConstraints      column0Constraints;
      javafx.scene.layout.ColumnConstraints      column1Constraints;
      javafx.scene.layout.RowConstraints         rowConstraints;
      int                                        rowIndex;
      javafx.scene.control.Label                 label;

      gridPane = new javafx.scene.layout.GridPane();
      gridPane.setPadding( new javafx.geometry.Insets(10,10,10,10) );

      column0Constraints = new javafx.scene.layout.ColumnConstraints();
      column0Constraints.setHgrow( javafx.scene.layout.Priority.NEVER );
      column1Constraints = new javafx.scene.layout.ColumnConstraints();
      column1Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      gridPane.getColumnConstraints().addAll( column0Constraints,
                                              column1Constraints );

      rowIndex = 0;

      // testSetName

      rowConstraints = new javafx.scene.layout.RowConstraints();
      rowConstraints.setVgrow( javafx.scene.layout.Priority.NEVER );

      label = new javafx.scene.control.Label( "Name:  " );
      javafx.scene.layout.GridPane.setConstraints( label, 0, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setMargin( label, new javafx.geometry.Insets(5,5,5,5) );
      gridPane.getChildren().add( label );

      label = new javafx.scene.control.Label( testSet.getTestSetName() );
      javafx.scene.layout.GridPane.setConstraints( label, 1, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setMargin( label, new javafx.geometry.Insets(5,5,5,5) );
      gridPane.getChildren().add( label );

      rowIndex++;

      // className

      rowConstraints = new javafx.scene.layout.RowConstraints();
      rowConstraints.setVgrow( javafx.scene.layout.Priority.NEVER );

      label = new javafx.scene.control.Label( "Top Level Class:  " );
      javafx.scene.layout.GridPane.setConstraints( label, 0, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setMargin( label, new javafx.geometry.Insets(5,5,5,5) );
      gridPane.getChildren().add( label );

      label = new javafx.scene.control.Label( testSet.getTopLevelClassName() );
      javafx.scene.layout.GridPane.setConstraints( label, 1, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setMargin( label, new javafx.geometry.Insets(5,5,5,5) );
      gridPane.getChildren().add( label );

      rowIndex++;

      // jvmDirectoryName

      rowConstraints = new javafx.scene.layout.RowConstraints();
      rowConstraints.setVgrow( javafx.scene.layout.Priority.NEVER );

      label = new javafx.scene.control.Label( "JVM Command:  " );
      javafx.scene.layout.GridPane.setConstraints( label, 0, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setMargin( label, new javafx.geometry.Insets(5,5,5,5) );
      gridPane.getChildren().add( label );

      label = new javafx.scene.control.Label( testSet.getJVMCommand() );
      javafx.scene.layout.GridPane.setConstraints( label, 1, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setMargin( label, new javafx.geometry.Insets(5,5,5,5) );
      gridPane.getChildren().add( label );

      rowIndex++;

      // configurationThreadCount

      rowConstraints = new javafx.scene.layout.RowConstraints();
      rowConstraints.setVgrow( javafx.scene.layout.Priority.NEVER );

      label = new javafx.scene.control.Label( "Pass 2 thread count:  " );
      javafx.scene.layout.GridPane.setConstraints( label, 0, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setMargin( label, new javafx.geometry.Insets(5,5,5,5) );
      gridPane.getChildren().add( label );

      label = new javafx.scene.control.Label( Integer.toString(testSet.getPass2ThreadCount()) );
      javafx.scene.layout.GridPane.setConstraints( label, 1, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setMargin( label, new javafx.geometry.Insets(5,5,5,5) );
      gridPane.getChildren().add( label );

      rowIndex++;

      return gridPane;
   }

   final private javafx.scene.layout.Pane makeButtonPane()
   {
      javafx.scene.control.Button           okButton;
      javafx.scene.layout.GridPane          result;
      javafx.scene.layout.ColumnConstraints column0Constraints;

      okButton = new javafx.scene.control.Button( "Close (esc)" );
      okButton.setOnAction( event -> _stage.close() );

      result = new javafx.scene.layout.GridPane();
      result.setHgap( 0 );
      result.setVgap( 0 );
      result.setPadding( new javafx.geometry.Insets(1,1,1,1) );

      column0Constraints = new javafx.scene.layout.ColumnConstraints();
      column0Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      result.getColumnConstraints().addAll( column0Constraints );

      javafx.scene.layout.GridPane.setConstraints( okButton, 0, 0, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );
      result.getChildren().addAll( okButton );

      return result;
   }

   final private javafx.scene.control.ListView<String> makeParameterList( java.util.List<String> commandLineParameters,
                                                                          java.util.List<String> configuredParameters )
   {
      javafx.scene.control.ListView<String> list;

      list = new javafx.scene.control.ListView<String>();

      list.getItems().addAll( commandLineParameters );

      list.getItems().addAll( configuredParameters );

      list.setPlaceholder( new javafx.scene.control.Label("(no parameters)") );

      return list;
   }

   final private javafx.scene.control.TableView<StringPair> makeEnvironmentVariableTable( TestSet testSet )
   {
      javafx.scene.control.TableView<StringPair>              tableView;
      javafx.scene.control.TableColumn<StringPair,StringPair> tableColumn;
      java.util.Map<String,String>                            environmentVariables;
      java.util.List<String>                                  names;

      tableView = new javafx.scene.control.TableView<StringPair>();
      tableView.getSelectionModel().setSelectionMode( javafx.scene.control.SelectionMode.SINGLE );

      tableColumn = new javafx.scene.control.TableColumn<StringPair,StringPair>( "Name" );
      tableColumn.setCellValueFactory( cellDataFactory -> new NoOpObservable<StringPair>(cellDataFactory.getValue()) );
      tableColumn.setCellFactory( column -> new TableCell(true) );
      tableColumn.setSortable( false );
      tableView.getColumns().add( tableColumn );

      tableColumn = new javafx.scene.control.TableColumn<StringPair,StringPair>( "Value" );
      tableColumn.setCellValueFactory( cellDataFactory -> new NoOpObservable<StringPair>(cellDataFactory.getValue()) );
      tableColumn.setCellFactory( column -> new TableCell(false) );
      tableColumn.setSortable( false );
      tableView.getColumns().add( tableColumn );

      environmentVariables = testSet.getEnvironmentVariables();
      names = new java.util.ArrayList<String>();
      if ( environmentVariables == null )
      {
         tableView.setPlaceholder( new javafx.scene.control.Label("(environment variables inherited from operating system)") );
      }
      else
      {
         tableView.setPlaceholder( new javafx.scene.control.Label("(no environment variables)") );
         names.addAll( environmentVariables.keySet() );
         java.util.Collections.sort( names );
         for ( String name : names )
         {
            tableView.getItems().add( new StringPair(name,
                                                     environmentVariables.get(name)) );
         }
      }

      return tableView;
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

   final private static class TableCell
      extends javafx.scene.control.TableCell<StringPair,StringPair>
   {
      private boolean _isNameColumn;

      TableCell( boolean isNameColumn )
      {
         _isNameColumn = isNameColumn;
      }

      final public void updateItem( StringPair stringPair,
                                    boolean    empty )
      {
         super.updateItem( stringPair,
                           empty );

         if ( empty )
         {
            setText( null );
         }
         else
         {
            if ( stringPair == null )
            {
               setText( null );
            }
            else
            {
               setText( _isNameColumn ? (stringPair.getName()) : (stringPair.getValue()) );
            }
         }
      }
   }

   final private static class StringPair
   {
      private String _name;
      private String _value;

      StringPair( String name,
                  String value )
      {
         _name = name;
         _value = value;
      }

      final String getName()
      {
         return _name;
      }

      final String getValue()
      {
         return _value;
      }
   }
}

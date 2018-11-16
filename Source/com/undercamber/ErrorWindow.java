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

final class ErrorWindow
{
   private javafx.stage.Stage           _stage;
   private javafx.scene.control.Button  _closeButton;
   private javafx.scene.layout.GridPane _gridPane;
   private java.util.List<ErrorRow>     _errorRows;

   ErrorWindow( TestData            testData,
                javafx.stage.Window ownerWindow )
   {
      javafx.scene.Scene scene;

      scene = new javafx.scene.Scene( buildContentPane(testData,
                                                       ownerWindow) );

      _stage = new javafx.stage.Stage();

      _stage.setScene( scene );

      scene.addEventFilter( javafx.scene.input.KeyEvent.KEY_PRESSED,
                            event -> handleKeyPress(event) );
      _stage.addEventFilter( javafx.scene.input.KeyEvent.KEY_PRESSED,
                             event -> handleKeyPress(event) );

      _stage.setTitle( "Errors in " + testData.getHeading() );
      _stage.setScene( scene );
      _stage.initOwner( ownerWindow );

      _stage.setOnCloseRequest( event -> onCloseRequest() );

      _stage.show();
   }

   final private javafx.scene.layout.Pane buildContentPane( TestData            testData,
                                                            javafx.stage.Window ownerWindow )
   {
      javafx.scene.layout.GridPane          contentPane;
      javafx.scene.layout.Pane              buttonPane;
      javafx.scene.layout.ColumnConstraints column0Constraints;
      javafx.scene.layout.ColumnConstraints column1Constraints;
      javafx.scene.layout.ColumnConstraints column2Constraints;
      javafx.scene.layout.ColumnConstraints column3Constraints;
      javafx.scene.layout.ColumnConstraints column4Constraints;
      javafx.scene.layout.RowConstraints    row0Constraints;
      javafx.scene.layout.RowConstraints    row1Constraints;

      _gridPane = new javafx.scene.layout.GridPane();

      _gridPane.setHgap( 5 );
      _gridPane.setVgap( 5 );

      column0Constraints = new javafx.scene.layout.ColumnConstraints();
      column0Constraints.setHgrow( javafx.scene.layout.Priority.NEVER );
      column1Constraints = new javafx.scene.layout.ColumnConstraints();
      column1Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      column2Constraints = new javafx.scene.layout.ColumnConstraints();
      column2Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      column3Constraints = new javafx.scene.layout.ColumnConstraints();
      column3Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      column4Constraints = new javafx.scene.layout.ColumnConstraints();
      column4Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      _gridPane.getColumnConstraints().addAll( column0Constraints,
                                               column1Constraints,
                                               column2Constraints,
                                               column3Constraints,
                                               column4Constraints );

      _errorRows = new java.util.ArrayList<ErrorRow>();

      for ( Throwable error : testData.getExceptions() )
      {
         _errorRows.add( new ErrorRow(error,
                                      this) );
      }

      buildTable();

      contentPane = new javafx.scene.layout.GridPane();

      contentPane.setHgap( 5 );
      contentPane.setVgap( 5 );

      column0Constraints = new javafx.scene.layout.ColumnConstraints();
      column0Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      contentPane.getColumnConstraints().addAll( column0Constraints );

      row0Constraints = new javafx.scene.layout.RowConstraints();
      row0Constraints.setVgrow( javafx.scene.layout.Priority.ALWAYS );
      row1Constraints = new javafx.scene.layout.RowConstraints();
      row1Constraints.setVgrow( javafx.scene.layout.Priority.NEVER );
      contentPane.getRowConstraints().addAll( row0Constraints,
                                              row1Constraints );

      buttonPane = makeButtonPane();

      javafx.scene.layout.GridPane.setConstraints( _gridPane,  0, 0, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );
      javafx.scene.layout.GridPane.setConstraints( buttonPane, 0, 1, 1, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );

      contentPane.getChildren().addAll( _gridPane,
                                        buttonPane );

      javafx.scene.layout.BorderPane.setMargin( contentPane, new javafx.geometry.Insets(5,5,5,5) );

      return new javafx.scene.layout.BorderPane( contentPane );
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
      if ( Utilities.ESCAPE_KEYBOARD_COMBINATION.match(event) )
      {
         onCloseRequest();
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

   final private void buildTable()
   {
      int                    rowIndex;
      javafx.scene.text.Text text;

      rowIndex = 0;

      _gridPane.getChildren().clear();

      text = new javafx.scene.text.Text( "   Class" );
      text.setStyle("-fx-font-weight: bold");
      javafx.scene.layout.GridPane.setConstraints( text, 1, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
      _gridPane.getChildren().add( text );

      text = new javafx.scene.text.Text( "Method" );
      text.setStyle("-fx-font-weight: bold");
      javafx.scene.layout.GridPane.setConstraints( text, 2, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
      _gridPane.getChildren().add( text );

      text = new javafx.scene.text.Text( "File" );
      text.setStyle("-fx-font-weight: bold");
      javafx.scene.layout.GridPane.setConstraints( text, 3, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
      _gridPane.getChildren().add( text );

      text = new javafx.scene.text.Text( "Line Number" );
      text.setStyle("-fx-font-weight: bold");
      javafx.scene.layout.GridPane.setConstraints( text, 4, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
      _gridPane.getChildren().add( text );

      rowIndex++;

      for ( ErrorRow errorRow : _errorRows )
      {
         rowIndex = errorRow.addToGridPane( rowIndex,
                                            _gridPane );
      }
   }

   final static class ErrorRow
   {
      private Throwable                  _error;
      private ErrorWindow                _errorWindow;
      private boolean                    _expanded;
      private javafx.scene.control.Label _expandLabel;

      ErrorRow( Throwable   error,
                ErrorWindow errorWindow )
      {
         _error = error;
         _errorWindow = errorWindow;
         _expanded = true;
         _expandLabel = new javafx.scene.control.Label( "+" );

         _expandLabel.setOnMouseClicked( event -> onMouseClicked(event) );
      }

      int addToGridPane( int                          startingRow,
                         javafx.scene.layout.GridPane gridPane )
      {
         return addToGridPane( null,
                               _error,
                               startingRow,
                               "",
                               gridPane );
      }

      int addToGridPane( String                       labelText,
                         Throwable                    error,
                         int                          startingRow,
                         String                       margin,
                         javafx.scene.layout.GridPane gridPane )
      {
         int                        rowIndex;
         javafx.scene.text.Text     text;
         javafx.scene.control.Label label;
         int                        lineNumber;
         String                     data;

         rowIndex = startingRow;

         if ( labelText == null )
         {
            javafx.scene.layout.GridPane.setConstraints( _expandLabel, 0, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
            gridPane.getChildren().add( _expandLabel );
         }
         else
         {
            text = new javafx.scene.text.Text( margin + labelText );
            text.setStyle("-fx-font-weight: bold");
            javafx.scene.layout.GridPane.setConstraints( text, 0, rowIndex, 5, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
            gridPane.getChildren().add( text );
            rowIndex++;
         }

         if ( error.getMessage()==null )
         {
            text = new javafx.scene.text.Text( error.getClass().getName() );
         }
         else
         {
            text = new javafx.scene.text.Text( error.getClass().getName() + ":  " + error.getMessage() );
         }
         text.setStyle("-fx-font-weight: bold");
         javafx.scene.layout.GridPane.setConstraints( text, 1, rowIndex, 4, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
         gridPane.getChildren().add( text );

         if ( _expanded )
         {
            for ( StackTraceElement stackTraceElement : error.getStackTrace() )
            {
               rowIndex++;

               data = stackTraceElement.getClassName();
               label = new javafx.scene.control.Label( margin + "   " + ((data==null)?"<unknown>":data) );
               javafx.scene.layout.GridPane.setConstraints( label, 1, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
               gridPane.getChildren().add( label );

               data = stackTraceElement.getMethodName();
               label = new javafx.scene.control.Label( ((data==null)?"<unknown>":data) );
               javafx.scene.layout.GridPane.setConstraints( label, 2, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
               gridPane.getChildren().add( label );

               data = stackTraceElement.getFileName();
               label = new javafx.scene.control.Label( ((data==null)?"<unknown>":data) );
               javafx.scene.layout.GridPane.setConstraints( label, 3, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
               gridPane.getChildren().add( label );

               lineNumber = stackTraceElement.getLineNumber();
               label = new javafx.scene.control.Label( ((lineNumber<0)?"<unknown>":Integer.toString(lineNumber)) );
               javafx.scene.layout.GridPane.setConstraints( label, 4, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
               gridPane.getChildren().add( label );
            }
         }

         if ( error.getCause() != null )
         {
            rowIndex = addToGridPane( "Caused By:",
                                      error.getCause(),
                                      rowIndex + 1,
                                      margin + "   ",
                                      gridPane );
         }

         for ( Throwable suppressed : error.getSuppressed() )
         {
            rowIndex = addToGridPane( "Suppressed Exception:",
                                      suppressed,
                                      rowIndex + 1,
                                      margin + "   ",
                                      gridPane );
         }

         return rowIndex + 1;
      }

      final private void onMouseClicked( javafx.scene.input.MouseEvent mouseEvent )
      {
         if ( mouseEvent.getClickCount() == 1 )
         {
            if ( mouseEvent.getButton() == javafx.scene.input.MouseButton.PRIMARY )
            {
               _expanded = !_expanded;
               _expandLabel.setText( _expanded ? "-" : "+" );
               _errorWindow.buildTable();
            }
         }
      }
   }
}

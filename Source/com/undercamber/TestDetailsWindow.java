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

final class TestDetailsWindow
{
   private javafx.stage.Stage _stage;

   TestDetailsWindow( TestData            testData,
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

      _stage.setTitle( testData.getHeading() );
      _stage.setScene( scene );
      _stage.initOwner( ownerWindow );

      _stage.setOnCloseRequest( event -> onCloseRequest() );

      _stage.show();
   }

   final private javafx.scene.layout.Pane buildContentPane( TestData            testData,
                                                            javafx.stage.Window ownerWindow )
   {
      javafx.scene.layout.GridPane          contentPane;
      javafx.scene.layout.ColumnConstraints column0Constraints;
      javafx.scene.layout.ColumnConstraints column1Constraints;
      int                                   rowIndex;
      javafx.scene.control.Label            label;
      javafx.scene.control.Button           exceptionButton;
      int                                   index;
      Tag                                   tags[];
      TestData                              children[];
      java.util.List<Requirement>           requirements;

      contentPane = new javafx.scene.layout.GridPane();

      contentPane.setHgap( 5 );
      contentPane.setVgap( 5 );

      column0Constraints = new javafx.scene.layout.ColumnConstraints();
      column0Constraints.setHgrow( javafx.scene.layout.Priority.NEVER );
      column1Constraints = new javafx.scene.layout.ColumnConstraints();
      column1Constraints.setHgrow( javafx.scene.layout.Priority.ALWAYS );
      contentPane.getColumnConstraints().addAll( column0Constraints,
                                                 column1Constraints );

      rowIndex = 0;  //////////////////////////////////////////////////////////////////////////////

      label = new javafx.scene.control.Label( testData.getHeading() );
      javafx.scene.layout.GridPane.setConstraints( label, 0, rowIndex, 2, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
      contentPane.getChildren().add( label );

      rowIndex++;  ////////////////////////////////////////////////////////////////////////////////

      label = new javafx.scene.control.Label( "Status:  " );
      javafx.scene.layout.GridPane.setConstraints( label, 0, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
      contentPane.getChildren().add( label );

      label = new javafx.scene.control.Label( testData.getTestState().name() );
      javafx.scene.layout.GridPane.setConstraints( label, 1, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
      contentPane.getChildren().add( label );

      rowIndex++;  ////////////////////////////////////////////////////////////////////////////////

      if ( testData.getChildCount() > 0 )
      {
         label = new javafx.scene.control.Label( "Subtest sequencing mode:  " );
         javafx.scene.layout.GridPane.setConstraints( label, 0, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
         contentPane.getChildren().add( label );

         label = new javafx.scene.control.Label( testData.getSubtestSequencingMode().name() );
         javafx.scene.layout.GridPane.setConstraints( label, 1, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
         contentPane.getChildren().add( label );

         rowIndex++;  /////////////////////////////////////////////////////////////////////////////

         label = new javafx.scene.control.Label( "Subtest continuation mode:  " );
         javafx.scene.layout.GridPane.setConstraints( label, 0, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
         contentPane.getChildren().add( label );

         label = new javafx.scene.control.Label( testData.getSubtestContinuationMode().name() );
         javafx.scene.layout.GridPane.setConstraints( label, 1, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
         contentPane.getChildren().add( label );

         rowIndex++;  /////////////////////////////////////////////////////////////////////////////

         children = testData.getChildren();

         label = new javafx.scene.control.Label( "Subtests:  " );
         javafx.scene.layout.GridPane.setConstraints( label, 0, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
         contentPane.getChildren().add( label );

         label = new javafx.scene.control.Label( children[0].getHeading() + " (" + children[0].getTestState().name() + ")" );
         javafx.scene.layout.GridPane.setConstraints( label, 1, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
         contentPane.getChildren().add( label );

         rowIndex++;  /////////////////////////////////////////////////////////////////////////////

         for ( index=1; index<children.length; index++ )
         {
            label = new javafx.scene.control.Label( children[index].getHeading() + " (" + children[index].getTestState().name() + ")" );
            javafx.scene.layout.GridPane.setConstraints( label, 1, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
            contentPane.getChildren().add( label );

            rowIndex++;  //////////////////////////////////////////////////////////////////////////
         }
      }

      requirements = testData.getRequirements();
      java.util.Collections.sort( requirements );

      if ( requirements.size() > 0 )
      {
         label = new javafx.scene.control.Label( "Requirements:  " );
         javafx.scene.layout.GridPane.setConstraints( label, 0, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
         contentPane.getChildren().add( label );

         label = new javafx.scene.control.Label( requirements.get(0).getDescription() + ":  " + requirements.get(0).getResultsText() );
         javafx.scene.layout.GridPane.setConstraints( label, 1, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
         contentPane.getChildren().add( label );

         rowIndex++;  /////////////////////////////////////////////////////////////////////////////

         for ( index=1; index<requirements.size(); index++ )
         {
            label = new javafx.scene.control.Label( requirements.get(index).getDescription() + ":  " + requirements.get(index).getResultsText() );
            javafx.scene.layout.GridPane.setConstraints( label, 1, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
            contentPane.getChildren().add( label );

            rowIndex++;  //////////////////////////////////////////////////////////////////////////
         }
      }

      rowIndex = addDependenciesToGridPane( rowIndex,
                                            "Fixed prerequisites",
                                            testData.listFixedPrerequisites(),
                                            contentPane );

      rowIndex = addDependenciesToGridPane( rowIndex,
                                            "Unsatisfied conditional prerequisites",
                                            testData.listUnsatisfiedConditionalPrerequisites(),
                                            contentPane );

      rowIndex = addDependenciesToGridPane( rowIndex,
                                            "Previously satisfied conditional prerequisites",
                                            testData.listPreviouslySatisfiedConditionalPrerequisites(),
                                            contentPane );

      rowIndex = addDependenciesToGridPane( rowIndex,
                                            "Fixed dependents",
                                            testData.listFixedDependents(),
                                            contentPane );

      rowIndex = addDependenciesToGridPane( rowIndex,
                                            "Unsatisfied conditional dependents",
                                            testData.listUnsatisfiedConditionalDependents(),
                                            contentPane );

      rowIndex = addDependenciesToGridPane( rowIndex,
                                            "Previously satisfied conditional dependents",
                                            testData.listSatisfiedConditionalDependents(),
                                            contentPane );

      tags = testData.listLocalTags();
      if ( tags.length > 0 )
      {
         label = new javafx.scene.control.Label( "Tags:  " );
         javafx.scene.layout.GridPane.setConstraints( label, 0, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
         contentPane.getChildren().add( label );

         label = new javafx.scene.control.Label( tags[0].getName() + (tags[0].includeSubtests()?" (include subtests)":" (don't include subtests)") );
         javafx.scene.layout.GridPane.setConstraints( label, 1, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
         contentPane.getChildren().add( label );

         rowIndex++;  /////////////////////////////////////////////////////////////////////////////

         for ( index=1; index<tags.length; index++ )
         {
            label = new javafx.scene.control.Label( tags[index].getName() + (tags[index].includeSubtests()?" (include subtests)":" (don't include subtests)") );
            javafx.scene.layout.GridPane.setConstraints( label, 1, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
            contentPane.getChildren().add( label );

            rowIndex++;  //////////////////////////////////////////////////////////////////////////
         }
      }

      label = new javafx.scene.control.Label( "ID:  " );
      javafx.scene.layout.GridPane.setConstraints( label, 0, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
      contentPane.getChildren().add( label );

      label = new javafx.scene.control.Label( Integer.toString(testData.getID()) );
      javafx.scene.layout.GridPane.setConstraints( label, 1, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
      contentPane.getChildren().add( label );

      rowIndex++;  ////////////////////////////////////////////////////////////////////////////////

      if ( testData.getLocalExceptionCount() > 0 )
      {
         exceptionButton = new javafx.scene.control.Button( "Show Exceptions..." );
         exceptionButton.setOnAction( event -> testData.getErrorWindow(ownerWindow).show() );
         javafx.scene.layout.GridPane.setConstraints( exceptionButton, 0, rowIndex, 2, 1, javafx.geometry.HPos.CENTER, javafx.geometry.VPos.CENTER );
         contentPane.getChildren().add( exceptionButton );

         rowIndex++;  /////////////////////////////////////////////////////////////////////////////
      }

      javafx.scene.layout.BorderPane.setMargin( contentPane, new javafx.geometry.Insets(5,5,5,5) );

      return new javafx.scene.layout.BorderPane( contentPane );
   }

   final private int addDependenciesToGridPane( int                          startingRowIndex,
                                                String                       labelText,
                                                TestData                     dependencies[],
                                                javafx.scene.layout.GridPane contentPane )
   {
      int                        rowIndex;
      javafx.scene.control.Label label;
      int                        index;

      rowIndex = startingRowIndex;

      if ( dependencies.length > 0 )
      {
         label = new javafx.scene.control.Label( labelText + ":  " );
         javafx.scene.layout.GridPane.setConstraints( label, 0, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
         contentPane.getChildren().add( label );

         label = new javafx.scene.control.Label( dependencies[0].getHeading() + " (" + dependencies[0].getTestState().name() + ")" );
         javafx.scene.layout.GridPane.setConstraints( label, 1, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
         contentPane.getChildren().add( label );

         rowIndex++;  /////////////////////////////////////////////////////////////////////////////

         for ( index=1; index<dependencies.length; index++ )
         {
            label = new javafx.scene.control.Label( dependencies[index].getHeading() + " (" + dependencies[index].getTestState().name() + ")" );
            javafx.scene.layout.GridPane.setConstraints( label, 1, rowIndex, 1, 1, javafx.geometry.HPos.LEFT, javafx.geometry.VPos.CENTER );
            contentPane.getChildren().add( label );

            rowIndex++;  //////////////////////////////////////////////////////////////////////////
         }
      }

      return rowIndex;
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
}

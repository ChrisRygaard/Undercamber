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

final class ResultsTreeCell
   extends javafx.scene.control.TreeTableCell<MainTableItemWrapper,MainTableItemWrapper>
   implements TestDataListener
{
   private MainTableItemWrapper       _mainTableItemWrapper;
   private javafx.scene.control.Label _label;
   private javafx.stage.Window        _ownerWindow;
   private java.util.List<String>     _commandLineTestParameters;

   ResultsTreeCell( Undercamber            undercamber,
                    java.util.List<String> commandLineTestParameters,
                    javafx.stage.Window    ownerWindow )
   {
      _label = new javafx.scene.control.Label();
      _ownerWindow = ownerWindow;
      _commandLineTestParameters = commandLineTestParameters;

      setupPopupMenu( undercamber );
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
               setGraphic( _label );
               setStyle( "" );
            }
            else
            {
               setGraphic( _label );
               setStyle( "-fx-background-color: paleturquoise" );
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
               _label.setText( testData.getHeading() );
            }
            else
            {
               switch ( testData.getSubtestSequencingMode() )
               {
                  case CONCURRENT:
                  {
                     _label.setText( testData.getHeading() + "  (P)" );
                     break;
                  }
                  case SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR:
                  {
                     _label.setText( testData.getHeading() + "  (S/A)" );
                     break;
                  }
                  case SEQUENTIAL_CONTINUE_ON_ERROR:
                  {
                     _label.setText( testData.getHeading() + "  (S/C)" );
                     break;
                  }
                  default:
                  {
                     _label.setText( testData.getHeading() + "  (?)" );
                     Utilities.printStackTrace( new Exception("Unrecognized subtest sequencing mode;  "+testData.getSubtestSequencingMode()) );
                  }
               }
            }

            if ( testData.getTestState().ran() )
            {
               _label.setDisable( false );
               if ( testData.getTestState().successful() )
               {
                  _label.setTextFill( javafx.scene.paint.Color.DARKGREEN );
               }
               else
               {
                  _label.setTextFill( javafx.scene.paint.Color.DARKRED );
               }
            }
            else
            {
               _label.setDisable( true );
               _label.setTextFill( javafx.scene.paint.Color.BLACK );
            }

            setStyle( "" );
         }
         else
         {
            _label.setText( mainTableItemWrapper.getTestSet().getTestSetName() );
            setStyle( "-fx-background-color: paleturquoise" );
         }
      }
   }

   final private void setupPopupMenu( Undercamber undercamber )
   {
      javafx.scene.control.ContextMenu popupMenu;
      javafx.scene.control.MenuItem    viewTestSetMenuItem;

      popupMenu = new javafx.scene.control.ContextMenu();

      viewTestSetMenuItem = new javafx.scene.control.MenuItem( "View test set environment" );
      viewTestSetMenuItem.setOnAction( event -> showTestSetWindow(undercamber) );

      popupMenu.getItems().addAll( viewTestSetMenuItem );

      setContextMenu( popupMenu );
   }

   final private void showTestSetWindow( Undercamber undercamber )
   {
      MainTableItemWrapper mainTableItemWrapper;

      mainTableItemWrapper = getItem();

      if ( mainTableItemWrapper != null )
      {
         if ( !(mainTableItemWrapper.isTestData()) )
         {
            mainTableItemWrapper.getTestSet().getTestSetWindow( undercamber,
                                                                _commandLineTestParameters,
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

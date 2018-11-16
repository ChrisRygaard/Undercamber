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

final class ShowDependenciesCell
   extends javafx.scene.control.TreeTableCell<MainTableItemWrapper,MainTableItemWrapper>
{
   private Undercamber                 _undercamber;
   private boolean                     _showTestPopupMenu;
   private javafx.stage.Window         _ownerWindow;
   private javafx.scene.control.Button _button;

   ShowDependenciesCell( boolean             showTestPopupMenu,
                         Undercamber         undercamber,
                         javafx.stage.Window ownerWindow )
   {
      _ownerWindow = ownerWindow;
      _undercamber = undercamber;
      _showTestPopupMenu = showTestPopupMenu;

      _button = new javafx.scene.control.Button( "Show Dependencies" );
      _button.setOnAction( event -> onAction() );
   }

   final public void updateItem( MainTableItemWrapper mainTableItemWrapper,
                                 boolean              empty )
   {
      TestData testData;

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
         if ( mainTableItemWrapper == null )
         {
            setText( null );
            setGraphic( null );
            setStyle( "" );
         }
         else
         {
            updateButton();
            if ( mainTableItemWrapper.isTestData() )
            {
               testData = mainTableItemWrapper.getTestData();

               if ( (testData.getTotalPrerequisiteCount()>0) || (testData.getTotalDependentCount()>0) )
               {
                  setGraphic( _button );
               }
               else
               {
                  setGraphic( null );
               }

               setStyle( "" );
            }
            else
            {
               setGraphic( null );

               setStyle( "-fx-background-color: paleturquoise" );
            }
         }
      }
   }

   final private void updateButton()
   {
      MainTableItemWrapper mainTableItemWrapper;
      TestData             testData;

      mainTableItemWrapper = getItem();

      if ( mainTableItemWrapper != null )
      {
         if ( mainTableItemWrapper.isTestData() )
         {
            testData = mainTableItemWrapper.getTestData();

            if ( (testData.getTotalPrerequisiteCount()>0) && (testData.getTotalDependentCount()>0) )
            {
               _button.setText( "Show Dependencies" );
            }
            else if ( testData.getTotalPrerequisiteCount() > 0 )
            {
               _button.setText( "Show Prerequisites" );
            }
            else if ( testData.getTotalDependentCount() > 0 )
            {
               _button.setText( "Show Dependents" );
            }
         }
      }
   }

   final void onAction()
   {
      MainTableItemWrapper mainTableItemWrapper;

      mainTableItemWrapper = getItem();

      if ( mainTableItemWrapper != null )
      {
         if ( mainTableItemWrapper.isTestData() )
         {
            mainTableItemWrapper.getTestData().getDependencyWindow( _showTestPopupMenu,
                                                                    _undercamber,
                                                                    _ownerWindow ).show();
         }
      }
   }
}

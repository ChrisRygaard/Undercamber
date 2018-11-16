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

final class ResultsTableCell
   extends javafx.scene.control.TreeTableCell<MainTableItemWrapper,MainTableItemWrapper>
{
   private CellType                   _cellType;
   private javafx.scene.control.Label _label;

   ResultsTableCell( CellType cellType )
   {
      _label = new javafx.scene.control.Label();
      _cellType = cellType;
   }

   final public void updateItem( MainTableItemWrapper mainTableItemWrapper,
                                 boolean              empty )
   {
      TestData testData;

      super.updateItem( mainTableItemWrapper,
                        empty );

      int errorCount;

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
            setText( null );

            if ( mainTableItemWrapper.isTestData() )
            {
               testData = mainTableItemWrapper.getTestData();

               switch ( _cellType )
               {
                  case ERROR_COUNT:
                  {
                     errorCount = testData.getLocalExceptionCount();
                     _label.setText( (errorCount==0) ? "" : Integer.toString(errorCount) );
                     break;
                  }
                  case CHILD_ERROR_COUNT:
                  {
                     errorCount = testData.getChildExceptionCount();
                     _label.setText( (errorCount==0) ? "" : Integer.toString(errorCount) );
                     break;
                  }
                  case OUTCOME:
                  {
                     _label.setText( testData.getTestState().name() );
                     break;
                  }
                  default:
                  {
                     throw new Error( "Unrecognized cell type:  " + _cellType );
                  }
               }

               setStyle( "" );
            }
            else
            {
               _label.setText( null );

               setStyle( "-fx-background-color: paleturquoise" );
            }

            setGraphic( _label );
         }
      }
   }

   enum  CellType
   {
      ERROR_COUNT,
      CHILD_ERROR_COUNT,
      OUTCOME
   }
}

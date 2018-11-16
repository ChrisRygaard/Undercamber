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

final class UnsupportiveTestCell
   extends javafx.scene.control.TableCell<TestData,TestData>
{
   private ColumnType                 _columnType;
   private javafx.scene.control.Label _label;

   UnsupportiveTestCell( ColumnType columnType )
   {
      _columnType = columnType;
      _label = new javafx.scene.control.Label();
   }

   final public void updateItem( TestData testData,
                                 boolean  empty )
   {
      super.updateItem( testData,
                        empty );

      if ( empty )
      {
         setText( null );
         setGraphic( null );
      }
      else
      {
         if ( testData == null )
         {
            setText( null );
            setGraphic( null );
         }
         else
         {
            setText( null );
            switch ( _columnType )
            {
               case TEST:
               {
                  _label.setText( testData.getHeading() );
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
                  break;
               }
               case RESULT:
               {
                  _label.setText( testData.getTestState().name() );
                  break;
               }
               case ID:
               {
                  _label.setText( Integer.toString(testData.getID()) );
                  break;
               }
               default:
               {
                  throw new Error( "Internal error:  Unrecognized column type:  " + _columnType );
               }
            }
            setGraphic( _label );
         }
      }
   }

   enum ColumnType
   {
      TEST,
      RESULT,
      ID
   }
}

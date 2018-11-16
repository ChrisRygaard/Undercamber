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

final class RequirementsTableCell
   extends javafx.scene.control.TableCell<Requirement,Requirement>
{
   private CellType _cellType;

   RequirementsTableCell( CellType cellType )
   {
      _cellType = cellType;
   }

   final public void updateItem( Requirement requirement,
                                 boolean     empty )
   {
      super.updateItem( requirement,
                        empty );

      if ( empty )
      {
         setText( null );
         setGraphic( null );
      }
      else
      {
         if ( requirement == null )
         {
            setText( null );
            setGraphic( null );
         }
         else
         {
            switch ( _cellType )
            {
               case ID:
               {
                  setText( requirement.getRequirementID() );
                  break;
               }
               case COMPLETION_STATE:
               {
                  setText( requirement.getCompletionState().name() );
                  break;
               }
               case DESCRIPTION:
               {
                  setText( requirement.getDescription() );
                  break;
               }
               case RESULT:
               {
                  setText( requirement.getResultsText() );
                  break;
               }
               default:
               {
                  throw new Error( "Internal error:  Unrecognized cell type:  " + _cellType );
               }
            }
            setGraphic( null );
         }
      }
   }

   enum CellType
   {
      ID,
      COMPLETION_STATE,
      DESCRIPTION,
      RESULT
   }
}

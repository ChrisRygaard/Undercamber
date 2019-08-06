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

final class RequirementData
   implements Comparable<RequirementData>
{
   private Requirement _requirement;
   private String      _requirementID;
   private String      _description;
   private String      _resultsText;
   private TestState   _completionState;

   RequirementData( Requirement requirement )
   {
      _requirement = requirement;
      setData();
   }

   RequirementData( java.io.DataInput dataInput )
      throws java.io.IOException
   {
      if ( dataInput.readBoolean() )
      {
         _requirementID = dataInput.readUTF();
      }

      if ( dataInput.readBoolean() )
      {
         _description = dataInput.readUTF();
      }

      if ( dataInput.readBoolean() )
      {
         _resultsText = dataInput.readUTF();
      }

      if ( dataInput.readBoolean() )
      {
         _completionState = TestState.values()[ dataInput.readInt() ];
      }
   }

   final String getRequirementID()
   {
      return _requirementID;
   }

   final String getDescription()
   {
      return _description;
   }

   final String getResultsText()
   {
      return _resultsText;
   }

   final TestState getCompletionState()
   {
      return _completionState;
   }

   final void writeXML( java.io.PrintStream printStream,
                        String              margin )
   {
      printStream.println( margin + "<requirement>" );

      if ( _requirementID == null )
      {
         printStream.println( margin + "   <id>(null)</id>" );
      }
      else
      {
         printStream.println( margin + "   <id>" + Utilities.escapeForXML(_requirementID) + "</id>" );
      }

      if ( _description == null )
      {
         printStream.println( margin + "   <description>(null)</description>" );
      }
      else
      {
         printStream.println( margin + "   <description>" + Utilities.escapeForXML(_description) + "</description>" );
      }

      if ( _completionState == null )
      {
         printStream.println( margin + "   <completionState>(not set)</completionState>" );
      }
      else
      {
         printStream.println( margin + "   <completionState>" + Utilities.escapeForXML(_completionState.toString()) + "</completionState>" );
      }

      if ( _resultsText == null )
      {
         printStream.println( margin + "   <result>(not set)</result>" );
      }
      else
      {
         printStream.println( margin + "   <result>" + Utilities.escapeForXML(_resultsText) + "</result>" );
      }

      printStream.println( margin + "</requirement>" );
   }

   private void setData()
   {
      if ( _requirement != null )
      {
         _requirementID = _requirement.getRequirementID();
         _description = _requirement.getDescription();
         _resultsText = _requirement.getResultsText();
         _completionState = _requirement.getCompletionState();
      }
   }

   final public boolean equals( Object thatObject )
   {
      RequirementData that;

      if ( thatObject instanceof RequirementData )
      {
         that = (RequirementData)thatObject;

         return that._requirementID.equals( _requirementID );
      }
      else
      {
         return false;
      }
   }

   final public int hashCode()
   {
      return _requirementID.hashCode();
   }

   final public int compareTo( RequirementData that )
   {
      return _requirementID.compareTo( that._requirementID );
   }

   final void write( java.io.DataOutput dataOutput )
      throws java.io.IOException
   {
      setData();

      if ( _requirementID == null )
      {
         dataOutput.writeBoolean( false );
      }
      else
      {
         dataOutput.writeBoolean( true );
         dataOutput.writeUTF( _requirementID );
      }

      if ( _description == null )
      {
         dataOutput.writeBoolean( false );
      }
      else
      {
         dataOutput.writeBoolean( true );
         dataOutput.writeUTF( _description );
      }

      if ( _resultsText == null )
      {
         dataOutput.writeBoolean( false );
      }
      else
      {
         dataOutput.writeBoolean( true );
         dataOutput.writeUTF( _resultsText );
      }

      if ( _completionState == null )
      {
         dataOutput.writeBoolean( false );
      }
      else
      {
         dataOutput.writeBoolean( true );
         dataOutput.writeInt( _completionState.ordinal() );
      }
   }
}

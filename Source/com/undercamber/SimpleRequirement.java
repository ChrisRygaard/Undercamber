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

/**
 * A bare-bones implementation of {@link Requirement}
 */
final public class SimpleRequirement
   implements Requirement
{
   private String    _requirementID;
   private String    _description;
   private TestState _testState;

   /**
    * Constructor
    *
    * @param requirementID
    *        The requirement's ID
    * @param description
    *        A description of the requirement
    */
   public SimpleRequirement( String requirementID,
                             String description )
   {
      _requirementID = requirementID;
      _description = description;
      _testState = TestState.NOT_RUN;
   }

   /**
    * This just returns the ID passed into the constructor
    * @return The requirement's ID
    */
   final public String getRequirementID()
   {
      return _requirementID;
   }

   final public String getResultsText()
   {
      if ( _testState.successful() )
      {
         return "Passed";
      }
      else
      {
         return "Failed";
      }
   }

   /**
    * This just returns the description passed into the constructor
    *
    * @return The description
    */
   final public String getDescription()
   {
      return _description;
   }

   /**
    * This just returns the value for
    * <tt>testCompletionData.getTestCompletionStatus()</tt> from
    * the call to {@link #testComplete}
    *
    * @return The completion status
    */
   final public TestState getCompletionState()
   {
      return _testState;
   }

   /**
    * This just records the value of
    * <tt>testCompletionData.getTestCompletionStatus()</tt>
    *
    * @param testData
    *        The {@link TestData} indicating the status of the
    *        associated test.
    */
   final public void testComplete( TestData testData )
   {
      _testState = testData.getTestState();
   }
}

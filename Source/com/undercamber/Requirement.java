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
 * Interface for a Requirement
 *
 * @see SimpleRequirement
 */
public interface Requirement
   extends Comparable<Requirement>
{
   /**
    * Get the requirement's unique ID.  <br>
    * <br>
    * Two different instances of this class with the same RequirementID are
    * considered the same requirement.
    *
    * @return The requirement's ID
    */
   String getRequirementID();

   /**
    * Called by Undercamber after the associated test is run.  <br>
    * <br>
    * The Requirement objects passed to
    * <tt>TestManager.initialize(...)</tt> during the discovery
    * pass are retained, and the test results from the test pass
    * are passed to them.  The Requirement objects passed to
    * <tt>TestManager.initialize(...)</tt> during the second pass
    * are discarded, and this method is never called on them.
    *
    * @param testData
    *        The object describing the test status
    * @see TestManager#initialize( SubtestSequencingMode, String, SubtestContinuationMode, Prerequisite[], Tag[], Requirement... )
    */
   void testComplete( TestData testData );

   /**
    * Get the test completion status.  <br>
    * <br>
    * This will typically be the <tt>TestCompletionStatus</tt> from
    * the <tt>TestCompletionData</tt> passed into {@link
    * #testComplete}.  It can be other values, e.g. when a single
    * requirement is verified by multiple tests.
    *
    * @return The completion status
    */
   TestState getCompletionState();

   /**
    * Get a description of the requirement.
    *
    * @return String The description
    */
   String getDescription();

   /**
    * Get the text describing the validation results.
    *
    * @return The results text
    */
   String getResultsText();

   /**
    * Imposes ordering.  <br>
    * <br>
    * The default is the lexical ordering based on the output from
    * {@link getRequirementID}
    *
    * @return The integer indicating ordering
    */
   default int compareTo( Requirement that )
   {
      return getRequirementID().compareTo( that.getRequirementID() );
   }
}

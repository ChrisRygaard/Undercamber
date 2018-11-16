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
 * Indicates the stage in a test's lifecycle.
 */
public enum TestState
{
   //                                 ran    initialized skipped skippedByParentError skippedByPrerequisiteError skippedBySiblingError, complete success displayInShortReport canAcceptConfiguration
   /**
    * The test was not run
    */
   NOT_RUN                          ( false, false,      false,  false,               false,                     false,                 false,   false,  false,               true  ),
   /**
    * The test never called {@link TestManager#initialize}
    */
   UNINITIALIZED                    ( true,  false,      false,  false,               false,                     false,                 false,   false,  false,               true  ),
   /**
    * The test has called {@link TestManager#initialize}, and is
    * awaiting execution.
    */
   INITIALIZED                      ( true,  true,       false,  false,               false,                     false,                 false,   false,  false,               true  ),
   /**
    * The test has completed execution, and is awaiting completion
    * of its subtests.
    */
   RUNNING_SUBTESTS                 ( true,  true,       false,  false,               false,                     false,                 false,   false,  false,               false ),
   /**
    * The test was not run because an ancestor failed
    */
   SKIPPED_DUE_TO_PARENT_ERROR      ( false, true,       true,   true,                false,                     false,                 false,   false,  false,               false ),
   /**
    * The test was not run because a prerequisite failed
    */
   SKIPPED_DUE_TO_PREREQUISITE_ERROR( false, true,       true,   false,               true,                      false,                 false,   false,  false,               false ),
   /**
    * The test was skipped because the subtest mode was {@link
    * SubtestSequencingMode#SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR} and
    * a prior sibling failed
    */
   SKIPPED_DUE_TO_SIBLING_ERROR     ( false, true,       true,   false,               false,                     true,                  false,   false,  false,               false ),
   /**
    * The test was skipped because the user did not select it.
    */
   SKIPPED_BY_USER                  ( false, true,       true,   false,               false,                     false,                 true,    false,  false,               false ),
   /**
    * The test ran to completion without errors
    */
   COMPLETE_SUCCEEDED               ( true,  true,       false,  false,               false,                     false,                 true,    true,   true,                false ),
   /**
    * The test ran to completion with errors
    */
   COMPLETE_FAILED                  ( true,  true,       false,  false,               false,                     false,                 true,    false,  true,                false );

   private boolean _ran;
   private boolean _initialized;
   private boolean _skipped;
   private boolean _skippedByParentError;
   private boolean _skippedByPrerequisiteError;
   private boolean _skippedBySiblingError;
   private boolean _complete;
   private boolean _success;
   private boolean _displayInShortReport;
   private boolean _canAcceptConfiguration;

   private TestState( boolean ran,
                      boolean initialized,
                      boolean skipped,
                      boolean skippedByParentError,
                      boolean skippedByPrerequisiteError,
                      boolean skippedBySiblingError,
                      boolean complete,
                      boolean success,
                      boolean displayInShortReport,
                      boolean canAcceptConfiguration )
   {
      _ran = ran;
      _initialized = initialized;
      _skipped = skipped;
      _skippedByParentError = skippedByParentError;
      _skippedByPrerequisiteError = skippedByPrerequisiteError;
      _skippedBySiblingError = skippedBySiblingError;
      _complete = complete;
      _success = success;
      _displayInShortReport = displayInShortReport;
      _canAcceptConfiguration = canAcceptConfiguration;
   }

   /**
    * Did it run at all?
    *
    * @return
    * <table summary="return value table">
    *    <tr>
    *       <td>&nbsp;&nbsp;&nbsp;true:</td>
    *       <td>it ran</td>
    *    </tr>
    *    <tr>
    *       <td>&nbsp;&nbsp;&nbsp;false:</td>
    *       <td>it did not run</td>
    *    </tr>
    * </table>
    */
   final public boolean ran()
   {
      return _ran;
   }

   /**
    * Was it initialized?
    *
    * @return
    * <table summary="return value table">
    *    <tr>
    *       <td>&nbsp;&nbsp;&nbsp;true:</td>
    *       <td>it was initialized</td>
    *    </tr>
    *    <tr>
    *       <td>&nbsp;&nbsp;&nbsp;false:</td>
    *       <td>it was not initialized</td>
    *    </tr>
    * </table>
    */
   final public boolean initialized()
   {
      return _initialized;
   }

   /**
    * Was it skipped for some reason?
    *
    * @return
    * <table summary="return value table">
    *    <tr>
    *       <td>&nbsp;&nbsp;&nbsp;true:</td>
    *       <td>it was skipped</td>
    *    </tr>
    *    <tr>
    *       <td>&nbsp;&nbsp;&nbsp;false:</td>
    *       <td>it was not skipped</td>
    *    </tr>
    * </table>
    */
   final public boolean skipped()
   {
      return _skipped;
   }

   /**
    * Was it skipped due to a parent error?
    *
    * @return
    * <table summary="return value table">
    *    <tr>
    *       <td>&nbsp;&nbsp;&nbsp;true:</td>
    *       <td>it was skipped due to an error in an ancestor</td>
    *    </tr>
    *    <tr>
    *       <td>&nbsp;&nbsp;&nbsp;false:</td>
    *       <td>it was not skipped because of a failed ancestor</td>
    *    </tr>
    * </table>
    */
   final public boolean skippedByParentError()
   {
      return _skippedByParentError;
   }

   /**
    * Was it skipped because a prerequisite failed?
    *
    * @return
    * <table summary="return value table">
    *    <tr>
    *       <td>&nbsp;&nbsp;&nbsp;true:</td>
    *       <td>it was skipped due to an error in a
    *       prerequisite</td>
    *    </tr>
    *    <tr>
    *       <td>&nbsp;&nbsp;&nbsp;false:</td>
    *       <td>it was not skipped because of a failed
    *       prerequisite</td>
    *    </tr>
    * </table>
    */
   final public boolean skippedByPrerequisiteError()
   {
      return _skippedByPrerequisiteError;
   }

   /**
    * Was it skipped due to a sibling error?
    *
    * @return
    * <table summary="return value table">
    *    <tr>
    *       <td>&nbsp;&nbsp;&nbsp;true:</td>
    *       <td>it was skipped because the parent's {link SubtestSequencingMode} was
    *       {link SubtestSequencingMode#SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR} and a prior
    *       sibling failed.</td>
    *    </tr>
    *    <tr>
    *       <td>&nbsp;&nbsp;&nbsp;false:</td>
    *       <td>it was not skipped due to a sibling error</td>
    *    </tr>
    * </table>
    */
   final public boolean skippedBySiblingError()
   {
      return _skippedBySiblingError;
   }

   /**
    * Did it run to completion?
    *
    * @return
    * <table summary="return value table">
    *    <tr>
    *       <td>&nbsp;&nbsp;&nbsp;true:</td>
    *       <td>it ran to completion, possibly with errors</td>
    *    </tr>
    *    <tr>
    *       <td>&nbsp;&nbsp;&nbsp;false:</td>
    *       <td>it did not run to completion</td>
    *    </tr>
    * </table>
    */
   final public boolean complete()
   {
      return _complete;
   }

   /**
    * Was it run to completion without errors?
    *
    * @return
    * <table summary="return value table">
    *    <tr>
    *       <td>&nbsp;&nbsp;&nbsp;true:</td>
    *       <td>success</td>
    *    </tr>
    *    <tr>
    *       <td>&nbsp;&nbsp;&nbsp;false:</td>
    *       <td>failure</td>
    *    </tr>
    * </table>
    */
   final public boolean successful()
   {
      return _success;
   }

   final boolean displayInShortReport()
   {
      return _displayInShortReport;
   }

   final boolean canAcceptConfiguration()
   {
      return _canAcceptConfiguration;
   }
}

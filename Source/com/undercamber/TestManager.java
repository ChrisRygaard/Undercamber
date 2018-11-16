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
 * The primary interface between Undercamber and one test.  <p>
 *
 * Each test must implement {@link TestUnit}, and each test is
 * passed a unique TestManager.
 *
 * Each test must call an overload of
 * {@link #initialize(SubtestSequencingMode,String,SubtestContinuationMode,Prerequisite[],Tag[],Requirement...)}
 * exactly once.
 */
final public class TestManager
{
   final static Object MONITOR_HOLDER = new Object();

   final private static java.lang.management.ThreadMXBean THREAD_MX_BEAN = java.lang.management.ManagementFactory.getThreadMXBean();
   final private static boolean                           TRACK_CPU_TIME = THREAD_MX_BEAN.isThreadCpuTimeSupported();

   private TestUnit _testUnit;
   private TestData _pass1TestData;
   private TestData _testData;
   private String   _margin;
   private TestSet  _testSet;
   private int      _childCompletionCount;

   TestManager( TestData parentTestData,  // Called only when running tests.  During discovery and during execution.
                TestUnit testUnit,
                TestSet  testSet,
                TestData pass1TestData,
                String   margin )
   {
      _testUnit = testUnit;
      _pass1TestData = pass1TestData;
      _margin = margin;
      _testSet = testSet;
      _testData = new TestData( this,
                                parentTestData,
                                testSet );
      _testData.transferConfigurationFrom( pass1TestData );
      _childCompletionCount = 0;
   }

   /**
    * A convenience method.  Equivalent to
    * <pre> initialize( SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR,
    *             null,
    *             SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
    *             new Prerequisite[0],
    *             new Tag[0],
    *             requirements );</pre>
    *
    * @param requirements
    *        The requirements that this test will either verify or
    *        help verify.
    *
    * @return Indicates whether this test should run its validation checks.
    *         <table summary="return value table">
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;true:</td>
    *               <td>Validation checks should be run</td>
    *            </tr>
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;false:</td>
    *               <td>validation checks should not be run</td>
    *            </tr>
    *         </table>
    *
    * @throws UserError
    *         If there is a configuration error
    *
    * @see #initialize( SubtestSequencingMode, String, SubtestContinuationMode, Prerequisite[], Tag[], Requirement... )
    */
   final public boolean initialize( Requirement... requirements )
      throws UserError
   {
      return setup( SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR,
                    null,
                    SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
                    new Prerequisite[0],
                    new Tag[0],
                    requirements );
   }

   /**
    * A convenience method.  Equivalent to
    * <pre> initialize( subtestSequencingMode,
    *             null,
    *             SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
    *             new Prerequisite[0],
    *             new Tag[0],
    *             requirements );</pre>
    *
    * @param subtestSequencingMode
    *        Indicates how subtests should be sequenced.  Has no
    *        effect if the test has no subtests
    * @param requirements
    *        The requirements that this test will either verify or
    *        help verify.
    *
    * @return Indicates whether this test should run its validation checks.
    *         <table summary="return value table">
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;true:</td>
    *               <td>Validation checks should be run</td>
    *            </tr>
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;false:</td>
    *               <td>validation checks should not be run</td>
    *            </tr>
    *         </table>
    *
    * @throws UserError
    *         If there is a configuration error
    *
    * @see #initialize( SubtestSequencingMode, String, SubtestContinuationMode, Prerequisite[], Tag[], Requirement... )
    */
   final public boolean initialize( SubtestSequencingMode subtestSequencingMode,
                                    Requirement...        requirements )
      throws UserError
   {
      return setup( subtestSequencingMode,
                    null,
                    SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
                    new Prerequisite[0],
                    new Tag[0],
                    requirements );
   }

   /**
    * A convenience method.  Equivalent to
    * <pre> initialize( subtestSequencingMode,
    *             null,
    *             SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
    *             prerequisites,
    *             new Tag[0],
    *             requirements );</pre>
    *
    * @param subtestSequencingMode
    *        Indicates how subtests should be sequenced.  Has no
    *        effect if the test has no subtests
    * @param prerequisites
    *        The prerequisites to this test
    * @param requirements
    *        The requirements that this test will either verify or
    *        help verify.
    *
    * @return Indicates whether this test should run its validation checks.
    *         <table summary="return value table">
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;true:</td>
    *               <td>Validation checks should be run</td>
    *            </tr>
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;false:</td>
    *               <td>validation checks should not be run</td>
    *            </tr>
    *         </table>
    *
    * @throws UserError
    *         If there is a configuration error
    *
    * @see #initialize( SubtestSequencingMode, String, SubtestContinuationMode, Prerequisite[], Tag[], Requirement... )
    */
   final public boolean initialize( SubtestSequencingMode subtestSequencingMode,
                                    Prerequisite          prerequisites[],
                                    Requirement...        requirements )
      throws UserError
   {
      return setup( subtestSequencingMode,
                    null,
                    SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
                    prerequisites,
                    new Tag[0],
                    requirements );
   }

   /**
    * A convenience method.  Equivalent to
    * <pre> initialize( SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR,
    *             null,
    *             SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
    *             prerequisites,
    *             new Tag[0],
    *             requirements );</pre>
    *
    * @param prerequisites
    *        The prerequisites to this test
    * @param requirements
    *        The requirements that this test will either verify or
    *        help verify.
    *
    * @return Indicates whether this test should run its validation checks.
    *         <table summary="return value table">
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;true:</td>
    *               <td>Validation checks should be run</td>
    *            </tr>
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;false:</td>
    *               <td>validation checks should not be run</td>
    *            </tr>
    *         </table>
    *
    * @throws UserError
    *         If there is a configuration error
    *
    * @see #initialize( SubtestSequencingMode, String, SubtestContinuationMode, Prerequisite[], Tag[], Requirement... )
    */
   final public boolean initialize( Prerequisite   prerequisites[],
                                    Requirement... requirements )
      throws UserError
   {
      return setup( SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR,
                    null,
                    SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
                    prerequisites,
                    new Tag[0],
                    requirements );
   }

   /**
    * A convenience method.  Equivalent to
    * <pre> initialize( subtestSequencingMode,
    *             null,
    *             SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
    *             new Prerequisite[0],
    *             tags,
    *             requirements );</pre>
    *
    * @param subtestSequencingMode
    *        Indicates how subtests should be sequenced.  Has no
    *        effect if the test has no subtests
    * @param tags
    *        The tags on this test.  Tags are used to group tests
    *        so that a group can be specified on the Undercamber command
    *        line.
    * @param requirements
    *        The requirements that this test will either verify or
    *        help verify.
    *
    * @return Indicates whether this test should run its validation checks.
    *         <table summary="return value table">
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;true:</td>
    *               <td>Validation checks should be run</td>
    *            </tr>
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;false:</td>
    *               <td>validation checks should not be run</td>
    *            </tr>
    *         </table>
    *
    * @throws UserError
    *         If there is a configuration error
    *
    * @see #initialize( SubtestSequencingMode, String, SubtestContinuationMode, Prerequisite[], Tag[], Requirement... )
    */
   final public boolean initialize( SubtestSequencingMode subtestSequencingMode,
                                    Tag                   tags[],
                                    Requirement...        requirements )
      throws UserError
   {
      return setup( subtestSequencingMode,
                    null,
                    SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
                    new Prerequisite[0],
                    tags,
                    requirements );
   }

   /**
    * A convenience method.  Equivalent to
    * <pre> initialize( SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR,
    *             null,
    *             SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
    *             new Prerequisite[0],
    *             tags,
    *             requirements );</pre>
    *
    * @param tags
    *        The tags on this test.  Tags are used to group tests
    *        so that a group can be specified on the Undercamber command
    *        line.
    * @param requirements
    *        The requirements that this test will either verify or
    *        help verify.
    *
    * @return Indicates whether this test should run its validation checks.
    *         <table summary="return value table">
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;true:</td>
    *               <td>Validation checks should be run</td>
    *            </tr>
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;false:</td>
    *               <td>validation checks should not be run</td>
    *            </tr>
    *         </table>
    *
    * @throws UserError
    *         If there is a configuration error
    *
    * @see #initialize( SubtestSequencingMode, String, SubtestContinuationMode, Prerequisite[], Tag[], Requirement... )
    */
   final public boolean initialize( Tag            tags[],
                                    Requirement... requirements )
      throws UserError
   {
      return setup( SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR,
                    null,
                    SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
                    new Prerequisite[0],
                    tags,
                    requirements );
   }

   /**
    * A convenience method.  Equivalent to
    * <pre> initialize( SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR,
    *             null,
    *             SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
    *             prerequisites,
    *             tags,
    *             requirements );</pre>
    *
    * @param prerequisites
    *        The prerequisites to this test
    * @param tags
    *        The tags on this test.  Tags are used to group tests
    *        so that a group can be specified on the Undercamber command
    *        line.
    * @param requirements
    *        The requirements that this test will either verify or
    *        help verify.
    *
    * @return Indicates whether this test should run its validation checks.
    *         <table summary="return value table">
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;true:</td>
    *               <td>Validation checks should be run</td>
    *            </tr>
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;false:</td>
    *               <td>validation checks should not be run</td>
    *            </tr>
    *         </table>
    *
    * @throws UserError
    *         If there is a configuration error
    *
    * @see #initialize( SubtestSequencingMode, String, SubtestContinuationMode, Prerequisite[], Tag[], Requirement... )
    */
   final public boolean initialize( Prerequisite   prerequisites[],
                                    Tag            tags[],
                                    Requirement... requirements )
      throws UserError
   {
      return setup( SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR,
                    null,
                    SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
                    prerequisites,
                    tags,
                    requirements );
   }

   /**
    * A convenience method.  Equivalent to
    * <pre> initialize( SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR,
    *             argument,
    *             SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
    *             new Prerequisite[0],
    *             new Tag[0],
    *             requirements );</pre>
    *
    * @param argument
    *        A string used to provide uniqueness.  <br>
    *        Undercamber does not have a way to discover the arguments of
    *        the test methods, so different tests whose signature
    *        differs only in the argument list are
    *        indistinguishable to Undercamber.  This argument can provide
    *        uniqueness.<br>
    *        <br>
    *        Also, different calls to the same test will have
    *        identical signatures, and this argument can be used to
    *        provide uniqueness.<br>
    *        <br>
    *        Not all tests that are indistinguishable to Undercamber need
    *        to be made unique via this parameter.  This is needed
    *        only for:
    *        <ul>
    *           <li>Uniquely specifying prerequisites</li>
    *           <li>Uniquely specifying tests on the Undercamber
    *           command line</li>
    *        </ul>
    * @param requirements
    *        The requirements that this test will either verify or
    *        help verify.
    *
    * @return Indicates whether this test should run its validation checks.
    *         <table summary="return value table">
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;true:</td>
    *               <td>Validation checks should be run</td>
    *            </tr>
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;false:</td>
    *               <td>validation checks should not be run</td>
    *            </tr>
    *         </table>
    *
    * @throws UserError
    *         If there is a configuration error
    *
    * @see #initialize( SubtestSequencingMode, String, SubtestContinuationMode, Prerequisite[], Tag[], Requirement... )
    */
   final public boolean initialize( String         argument,
                                    Requirement... requirements )
      throws UserError
   {
      return setup( SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR,
                    argument,
                    SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
                    new Prerequisite[0],
                    new Tag[0],
                    requirements );
   }

   /**
    * A convenience method.  Equivalent to
    * <pre> initialize( SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR,
    *             argument,
    *             SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
    *             prerequisites,
    *             new Tag[0],
    *             requirements );</pre>
    *
    * @param argument
    *        A string used to provide uniqueness.  <br>
    *        Undercamber does not have a way to discover the arguments of
    *        the test methods, so different tests whose signature
    *        differs only in the argument list are
    *        indistinguishable to Undercamber.  This argument can provide
    *        uniqueness.<br>
    *        <br>
    *        Also, different calls to the same test will have
    *        identical signatures, and this argument can be used to
    *        provide uniqueness.<br>
    *        <br>
    *        Not all tests that are indistinguishable to Undercamber need
    *        to be made unique via this parameter.  This is needed
    *        only for:
    *        <ul>
    *           <li>Uniquely specifying prerequisites</li>
    *           <li>Uniquely specifying tests on the Undercamber
    *           command line</li>
    *        </ul>
    * @param prerequisites
    *        The prerequisites to this test
    * @param requirements
    *        The requirements that this test will either verify or
    *        help verify.
    *
    * @return Indicates whether this test should run its validation checks.
    *         <table summary="return value table">
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;true:</td>
    *               <td>Validation checks should be run</td>
    *            </tr>
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;false:</td>
    *               <td>validation checks should not be run</td>
    *            </tr>
    *         </table>
    *
    * @throws UserError
    *         If there is a configuration error
    *
    * @see #initialize( SubtestSequencingMode, String, SubtestContinuationMode, Prerequisite[], Tag[], Requirement... )
    */
   final public boolean initialize( String         argument,
                                    Prerequisite   prerequisites[],
                                    Requirement... requirements )
      throws UserError
   {
      return setup( SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR,
                    argument,
                    SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
                    prerequisites,
                    new Tag[0],
                    requirements );
   }

   /**
    * A convenience method.  Equivalent to
    * <pre> initialize( SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR,
    *             null,
    *             SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
    *             new Prerequisite[0],
    *             new Tag[0],
    *             new Requirement[0] );</pre>
    *
    * @return Indicates whether this test should run its validation checks.
    *         <table summary="return value table">
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;true:</td>
    *               <td>Validation checks should be run</td>
    *            </tr>
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;false:</td>
    *               <td>validation checks should not be run</td>
    *            </tr>
    *         </table>
    *
    * @throws UserError
    *         If there is a configuration error
    *
    * @see #initialize( SubtestSequencingMode, String, SubtestContinuationMode, Prerequisite[], Tag[], Requirement... )
    */
   final public boolean initialize()
      throws UserError
   {
      return setup( SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR,
                    null,
                    SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
                    new Prerequisite[0],
                    new Tag[0],
                    new Requirement[0] );
   }

   /**
    * A convenience method.  Equivalent to
    * <pre> initialize( subtestSequencingMode,
    *             null,
    *             SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
    *             new Prerequisite[0],
    *             new Tag[0],
    *             new Requirement[0] );</pre>
    *
    * @param subtestSequencingMode
    *        Indicates how subtests should be sequenced.  Has no
    *        effect if the test has no subtests
    *
    * @return Indicates whether this test should run its validation checks.
    *         <table summary="return value table">
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;true:</td>
    *               <td>Validation checks should be run</td>
    *            </tr>
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;false:</td>
    *               <td>validation checks should not be run</td>
    *            </tr>
    *         </table>
    *
    * @throws UserError
    *         If there is a configuration error
    *
    * @see #initialize( SubtestSequencingMode, String, SubtestContinuationMode, Prerequisite[], Tag[], Requirement... )
    */
   final public boolean initialize( SubtestSequencingMode subtestSequencingMode )
      throws UserError
   {
      return setup( subtestSequencingMode,
                    null,
                    SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
                    new Prerequisite[0],
                    new Tag[0],
                    new Requirement[0] );
   }

   /**
    * A convenience method.  Equivalent to
    * <pre> initialize( subtestSequencingMode,
    *             null,
    *             SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
    *             prerequisites,
    *             new Tag[0],
    *             new Requirement[0] );</pre>
    *
    * @param subtestSequencingMode
    *        Indicates how subtests should be sequenced.  Has no
    *        effect if the test has no subtests
    * @param prerequisites
    *        The prerequisites to this test
    *
    * @return Indicates whether this test should run its validation checks.
    *         <table summary="return value table">
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;true:</td>
    *               <td>Validation checks should be run</td>
    *            </tr>
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;false:</td>
    *               <td>validation checks should not be run</td>
    *            </tr>
    *         </table>
    *
    * @throws UserError
    *         If there is a configuration error
    *
    * @see #initialize( SubtestSequencingMode, String, SubtestContinuationMode, Prerequisite[], Tag[], Requirement... )
    */
   final public boolean initialize( SubtestSequencingMode subtestSequencingMode,
                                    Prerequisite...       prerequisites )
      throws UserError
   {
      return setup( subtestSequencingMode,
                    null,
                    SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
                    prerequisites,
                    new Tag[0],
                    new Requirement[0] );
   }

   /**
    * A convenience method.  Equivalent to
    * <pre> initialize( SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR,
    *             null,
    *             SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
    *             prerequisites,
    *             new Tag[0],
    *             new Requirement[0] );</pre>
    *
    * @param prerequisites
    *        The prerequisites to this test
    *
    * @return Indicates whether this test should run its validation checks.
    *         <table summary="return value table">
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;true:</td>
    *               <td>Validation checks should be run</td>
    *            </tr>
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;false:</td>
    *               <td>validation checks should not be run</td>
    *            </tr>
    *         </table>
    *
    * @throws UserError
    *         If there is a configuration error
    *
    * @see #initialize( SubtestSequencingMode, String, SubtestContinuationMode, Prerequisite[], Tag[], Requirement... )
    */
   final public boolean initialize( Prerequisite... prerequisites )
      throws UserError
   {
      return setup( SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR,
                    null,
                    SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
                    prerequisites,
                    new Tag[0],
                    new Requirement[0] );
   }

   /**
    * A convenience method.  Equivalent to
    * <pre> initialize( subtestSequencingMode,
    *             null,
    *             SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
    *             new Prerequisite[0],
    *             tags,
    *             new Requirement[0] );</pre>
    *
    * @param subtestSequencingMode
    *        Indicates how subtests should be sequenced.  Has no
    *        effect if the test has no subtests
    * @param tags
    *        The tags on this test.  Tags are used to group tests
    *        so that a group can be specified on the Undercamber command
    *        line.
    *
    * @return Indicates whether this test should run its validation checks.
    *         <table summary="return value table">
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;true:</td>
    *               <td>Validation checks should be run</td>
    *            </tr>
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;false:</td>
    *               <td>validation checks should not be run</td>
    *            </tr>
    *         </table>
    *
    * @throws UserError
    *         If there is a configuration error
    *
    * @see #initialize( SubtestSequencingMode, String, SubtestContinuationMode, Prerequisite[], Tag[], Requirement... )
    */
   final public boolean initialize( SubtestSequencingMode subtestSequencingMode,
                                    Tag...                tags )
      throws UserError
   {
      return setup( subtestSequencingMode,
                    null,
                    SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
                    new Prerequisite[0],
                    tags,
                    new Requirement[0] );
   }

   /**
    * A convenience method.  Equivalent to
    * <pre> initialize( subtestSequencingMode,
    *             null,
    *             SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
    *             new Prerequisite[0],
    *             tags,
    *             new Requirement[0] );</pre>
    *
    * @param subtestSequencingMode
    *        Indicates how subtests should be sequenced.  Has no
    *        effect if the test has no subtests
    * @param prerequisites
    *        The prerequisites to this test
    * @param tags
    *        The tags on this test.  Tags are used to group tests
    *        so that a group can be specified on the Undercamber command
    *        line.
    *
    * @return Indicates whether this test should run its validation checks.
    *         <table summary="return value table">
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;true:</td>
    *               <td>Validation checks should be run</td>
    *            </tr>
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;false:</td>
    *               <td>validation checks should not be run</td>
    *            </tr>
    *         </table>
    *
    * @throws UserError
    *         If there is a configuration error
    *
    * @see #initialize( SubtestSequencingMode, String, SubtestContinuationMode, Prerequisite[], Tag[], Requirement... )
    */
   final public boolean initialize( SubtestSequencingMode subtestSequencingMode,
                                    Prerequisite          prerequisites[],
                                    Tag...                tags )
      throws UserError
   {
      return setup( subtestSequencingMode,
                    null,
                    SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
                    prerequisites,
                    tags,
                    new Requirement[0] );
   }

   /**
    * A convenience method.  Equivalent to
    * <pre> initialize( SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR,
    *             null,
    *             SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
    *             new Prerequisite[0],
    *             tags,
    *             new Requirement[0] );</pre>
    *
    * @param tags
    *        The tags on this test.  Tags are used to group tests
    *        so that a group can be specified on the Undercamber command
    *        line.
    *
    * @return Indicates whether this test should run its validation checks.
    *         <table summary="return value table">
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;true:</td>
    *               <td>Validation checks should be run</td>
    *            </tr>
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;false:</td>
    *               <td>validation checks should not be run</td>
    *            </tr>
    *         </table>
    *
    * @throws UserError
    *         If there is a configuration error
    *
    * @see #initialize( SubtestSequencingMode, String, SubtestContinuationMode, Prerequisite[], Tag[], Requirement... )
    */
   final public boolean initialize( Tag... tags )
      throws UserError
   {
      return setup( SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR,
                    null,
                    SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
                    new Prerequisite[0],
                    tags,
                    new Requirement[0] );
   }

   /**
    * A convenience method.  Equivalent to
    * <pre> initialize( SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR,
    *             null,
    *             SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
    *             prerequisites,
    *             tags,
    *             new Requirement[0] );</pre>
    *
    * @param prerequisites
    *        The prerequisites to this test
    * @param tags
    *        The tags on this test.  Tags are used to group tests
    *        so that a group can be specified on the Undercamber command
    *        line.
    *
    * @return Indicates whether this test should run its validation checks.
    *         <table summary="return value table">
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;true:</td>
    *               <td>Validation checks should be run</td>
    *            </tr>
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;false:</td>
    *               <td>validation checks should not be run</td>
    *            </tr>
    *         </table>
    *
    * @throws UserError
    *         If there is a configuration error
    *
    * @see #initialize( SubtestSequencingMode, String, SubtestContinuationMode, Prerequisite[], Tag[], Requirement... )
    */
   final public boolean initialize( Prerequisite prerequisites[],
                                    Tag...       tags )
      throws UserError
   {
      return setup( SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR,
                    null,
                    SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
                    prerequisites,
                    tags,
                    new Requirement[0] );
   }

   /**
    * A convenience method.  Equivalent to
    * <pre> initialize( SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR,
    *             argument,
    *             SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
    *             new Prerequisite[0],
    *             new Tag[0],
    *             new Requirement[0] );</pre>
    *
    * @param argument
    *        A string used to provide uniqueness.  <br>
    *        Undercamber does not have a way to discover the arguments of
    *        the test methods, so different tests whose signature
    *        differs only in the argument list are
    *        indistinguishable to Undercamber.  This argument can provide
    *        uniqueness.<br>
    *        <br>
    *        Also, different calls to the same test will have
    *        identical signatures, and this argument can be used to
    *        provide uniqueness.<br>
    *        <br>
    *        Not all tests that are indistinguishable to Undercamber need
    *        to be made unique via this parameter.  This is needed
    *        only for:
    *        <ul>
    *           <li>Uniquely specifying prerequisites</li>
    *           <li>Uniquely specifying tests on the Undercamber
    *           command line</li>
    *        </ul>
    *
    * @return Indicates whether this test should run its validation checks.
    *         <table summary="return value table">
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;true:</td>
    *               <td>Validation checks should be run</td>
    *            </tr>
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;false:</td>
    *               <td>validation checks should not be run</td>
    *            </tr>
    *         </table>
    *
    * @throws UserError
    *         If there is a configuration error
    *
    * @see #initialize( SubtestSequencingMode, String, SubtestContinuationMode, Prerequisite[], Tag[], Requirement... )
    */
   final public boolean initialize( String argument )
      throws UserError
   {
      return setup( SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR,
                    argument,
                    SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
                    new Prerequisite[0],
                    new Tag[0],
                    new Requirement[0] );
   }

   /**
    * A convenience method.  Equivalent to
    * <pre> initialize( SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR,
    *             argument,
    *             SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
    *             prerequisites,
    *             new Tag[0],
    *             new Requirement[0] );</pre>
    *
    * @param argument
    *        A string used to provide uniqueness.  <br>
    *        Undercamber does not have a way to discover the arguments of
    *        the test methods, so different tests whose signature
    *        differs only in the argument list are
    *        indistinguishable to Undercamber.  This argument can provide
    *        uniqueness.<br>
    *        <br>
    *        Also, different calls to the same test will have
    *        identical signatures, and this argument can be used to
    *        provide uniqueness.<br>
    *        <br>
    *        Not all tests that are indistinguishable to Undercamber need
    *        to be made unique via this parameter.  This is needed
    *        only for:
    *        <ul>
    *           <li>Uniquely specifying prerequisites</li>
    *           <li>Uniquely specifying tests on the Undercamber
    *           command line</li>
    *        </ul>
    * @param prerequisites
    *        The prerequisites to this test
    *
    * @return Indicates whether this test should run its validation checks.
    *         <table summary="return value table">
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;true:</td>
    *               <td>Validation checks should be run</td>
    *            </tr>
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;false:</td>
    *               <td>validation checks should not be run</td>
    *            </tr>
    *         </table>
    *
    * @throws UserError
    *         If there is a configuration error
    *
    * @see #initialize( SubtestSequencingMode, String, SubtestContinuationMode, Prerequisite[], Tag[], Requirement... )
    */
   final public boolean initialize( String          argument,
                                    Prerequisite... prerequisites )
      throws UserError
   {
      return setup( SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR,
                    argument,
                    SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL,
                    prerequisites,
                    new Tag[0],
                    new Requirement[0] );
   }

   /**
    * Initialize the test manager.  <br>
    * <br>
    * Each test is given a unique TestManager object, and the test
    * should call one of the overloaded versions of this method
    * just once.
    *
    * @param subtestSequencingMode
    *        Indicates how subtests should be sequenced.  Has no
    *        effect if the test has no subtests
    * @param argument
    *        A string used to provide uniqueness.  <br>
    *        Undercamber does not have a way to discover the arguments of
    *        the test methods, so different tests whose signature
    *        differs only in the argument list are
    *        indistinguishable to Undercamber.  This argument can provide
    *        uniqueness.<br>
    *        <br>
    *        Also, different calls to the same test will have
    *        identical signatures, and this argument can be used to
    *        provide uniqueness.<br>
    *        <br>
    *        Not all tests that are indistinguishable to Undercamber need
    *        to be made unique via this parameter.  This is needed
    *        only for:
    *        <ul>
    *           <li>Uniquely specifying prerequisites</li>
    *           <li>Uniquely specifying tests on the Undercamber
    *           command line</li>
    *        </ul>
    * @param subtestContinuationMode
    *        Indicates how the subtests should be handled if this
    *        test fails.
    * @param prerequisites
    *        The prerequisites to this test
    * @param tags
    *        The tags on this test.  Tags are used to group tests
    *        so that a group can be specified on the Undercamber command
    *        line.
    * @param requirements
    *        The requirements that this test will either verify or
    *        help verify.
    *
    * @return Indicates whether this test should run its validation
    *         checks. During the first pass (the discovery pass),
    *         this will be false. During the second pass (the
    *         validation pass), this will be true.
    *         <table summary="return value table">
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;true:</td>
    *               <td>Validation checks should be run</td>
    *            </tr>
    *            <tr>
    *               <td>&nbsp;&nbsp;&nbsp;false:</td>
    *               <td>validation checks should not be run</td>
    *            </tr>
    *         </table>
    *
    * @throws UserError
    *         If there is a configuration error
    */
   final public boolean initialize( SubtestSequencingMode   subtestSequencingMode,
                                    String                  argument,
                                    SubtestContinuationMode subtestContinuationMode,
                                    Prerequisite            prerequisites[],
                                    Tag                     tags[],
                                    Requirement...          requirements )
      throws UserError
   {
      return setup( subtestSequencingMode,
                    argument,
                    subtestContinuationMode,
                    prerequisites,
                    tags,
                    requirements );
   }

   final private boolean setup(  SubtestSequencingMode   subtestSequencingMode,
                                 String                  argument,
                                 SubtestContinuationMode subtestContinuationMode,
                                 Prerequisite            prerequisites[],
                                 Tag                     tags[],
                                 Requirement             requirements[] )
      throws UserError
   {
      if ( _testData.getTestState().initialized() )
      {
         throw new UserError( "Error:  Must call TestManager.initialize(...) first.  State = " + _testData.getTestState() + "; expected state = " + TestState.UNINITIALIZED + "." );
      }

      if ( subtestSequencingMode == null )
      {
         subtestSequencingMode = SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR;
      }

      if ( subtestContinuationMode == null )
      {
         subtestContinuationMode = SubtestContinuationMode.SKIP_SUBTESTS_IF_LOCAL_TESTS_FAIL;
      }

      if ( prerequisites == null )
      {
         prerequisites = new Prerequisite[ 0 ];
      }

      if ( tags == null )
      {
         tags = new Tag[ 0 ];
      }

      if ( requirements == null )
      {
         requirements = new Requirement[ 0 ];
      }

      _testData.initialize( Thread.currentThread().getStackTrace()[3],
                            subtestSequencingMode,
                            subtestContinuationMode,
                            argument,
                            prerequisites,
                            tags,
                            requirements );

      return _testSet.getExecutionMode().verify();
   }

   /**
    * A convenience method.  This just returns the arguments as an
    * array.  This is useful for building arguments to the
    * <tt>initialize(...)</tt> function.
    *
    * @param prerequisites
    *        The Prerequisites to be stuffed into an array
    *
    * @return The array of prerequisites
    */
   final public static Prerequisite[] toArray( Prerequisite... prerequisites )
   {
      return prerequisites;
   }

   /**
    * A convenience method.  This just returns the arguments as an
    * array.  This is useful for building arguments to the
    * <tt>initialize(...)</tt> function.
    *
    * @param tags
    *        The Tags to be stuffed into an array
    *
    * @return The array of Tags
    */
   final public static Tag[] toArray( Tag... tags )
   {
      return tags;
   }

   /**
    * A convenience method.  This just returns the arguments as an
    * array.  This is useful for building arguments to the
    * <tt>initialize(...)</tt> function.
    *
    * @param requirements
    *        The Requirements to be stuffed into an array
    *
    * @return The array of requirements
    */
   final public static Requirement[] toArray( Requirement... requirements )
   {
      return requirements;
   }

   final TestData getPass1TestData()
   {
      return _pass1TestData;
   }

   final void childCallback( TestManager       child,
                             int               headingColumnWidth,
                             UserError         userError,
                             InternalException internalError )
   {
      if ( (internalError==null) && (userError==null) )
      {
         if ( _testSet.getExecutionMode().isDiscovery() )
         {
            concurrentChildCallback( headingColumnWidth );
         }
         else
         {
            switch ( _testData.getSubtestSequencingMode() )
            {
               case CONCURRENT:
               {
                  concurrentChildCallback( headingColumnWidth );
                  break;
               }
               case SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR:
               {
                  sequentialChildCallbackWithAbort( child,
                                                    headingColumnWidth );
                  break;
               }
               case SEQUENTIAL_CONTINUE_ON_ERROR:
               {
                  sequentialChildCallbackWithoutAbort( child,
                                                       headingColumnWidth );
                  break;
               }
               default:
               {
                  addException( new InternalException("Internal error:  unrecognized SubtestSequencingMode:  "+_testData.getSubtestSequencingMode()) );
               }
            }
         }
      }
      else
      {
         finish( headingColumnWidth,
                 userError,
                 internalError );
      }
   }

   final private void concurrentChildCallback( int headingColumnWidth )
   {
      boolean done;

      synchronized( MONITOR_HOLDER )
      {
         _childCompletionCount++;

         done = _childCompletionCount >= getChildCount();
      }

      if ( done )
      {
         finish( headingColumnWidth,
                 null,
                 null );
      }
   }

   final private void sequentialChildCallbackWithAbort( TestManager child,
                                                        int         headingColumnWidth )
   {
      int         childIndex;
      int         childCount;
      boolean     done;
      int         index;
      TestManager nextChild;

      childIndex = child._testData.getSiblingIndex();

      childCount = getChildCount();

      if ( child._testData.hasExceptionInBranch() )
      {
         done = true;

         for ( index=childIndex+1; index<childCount; index++ )
         {
            _testData.getChild( index ).recordState( TestState.SKIPPED_DUE_TO_SIBLING_ERROR );
         }
      }
      else if ( childIndex >= (childCount-1) )
      {
         done = true;
      }
      else
      {
         done = false;
      }

      if ( done )
      {
         finish( headingColumnWidth,
                 null,
                 null );
      }
      else
      {
         nextChild = getChild( childIndex + 1 );
         synchronized( MONITOR_HOLDER )
         {
            nextChild.submitTest( headingColumnWidth );
         }
      }
   }

   final private void sequentialChildCallbackWithoutAbort( TestManager finishedChild,
                                                           int         headingColumnWidth )
   {
      int         childIndex;
      int         childCount;
      TestManager nextChild;

      childIndex = finishedChild.getSiblingIndex();

      childCount = getChildCount();

      if ( childIndex >= (childCount-1) )
      {
         finish( headingColumnWidth,
                 null,
                 null );
      }
      else
      {
         nextChild = getChild( childIndex + 1 );
         synchronized( MONITOR_HOLDER )
         {
            nextChild.submitTest( headingColumnWidth );
         }
      }
   }

   final void testThread( int headingColumnWidth )
   {
      long              cpuStartTime;
      long              cpuStopTime;
      long              threadID;
      long              startTime;
      long              stopTime;
      UserError         userError;
      InternalException internalError;

      try
      {
         if ( shouldVerify() )
         {
            cpuStartTime = -1L;
            cpuStopTime = -1L;

            threadID = Thread.currentThread().getId();
            startTime = System.nanoTime();
            if ( TRACK_CPU_TIME )
            {
               cpuStartTime = THREAD_MX_BEAN.getThreadCpuTime( threadID );
            }

            try
            {
               _testUnit.runTest( this );
            }
            catch ( Throwable throwable )
            {
               addException( throwable );
            }

            if ( TRACK_CPU_TIME )
            {
               cpuStopTime = THREAD_MX_BEAN.getThreadCpuTime( threadID );
            }
            stopTime = System.nanoTime();

            if ( _testSet.getExecutionMode().displayProgress() )
            {
               if ( _testData.getLocalExceptionCount() == 0 )
               {
                  System.out.println( Utilities.padToRight(_margin+_testData.getHeading(),headingColumnWidth) + " passed" );
               }
               else
               {
                  System.out.println( Utilities.padToRight(_margin+_testData.getHeading(),headingColumnWidth) + " failed" );
               }
            }

            if ( _testData.getTestState() == null )
            {
               userError = new UserError( "Error:  Test uninitialized when running " + _testData.getHeading() + ".  Be sure to call testManager.initialize(...) in the test unit." );
               internalError = null;
            }
            else if ( _testData.getTestState() != TestState.INITIALIZED )
            {
               userError = null;
               internalError = new InternalException( "Internal error:  Invalid state when running " + _testData.getHeading() + ".  State = "+_testData.getTestState() + "; expected state = " + TestState.INITIALIZED + "." );
            }
            else
            {
               userError = null;
               internalError = null;
            }

            _testData.setTimingData( startTime,
                                     stopTime,
                                     cpuStartTime,
                                     cpuStopTime );

            if ( getChildCount() == 0 )
            {
               finish( headingColumnWidth,
                       userError,
                       internalError );
            }
            else if ( shouldRunSubtests() )
            {
               launchChildTests( headingColumnWidth );
            }
            else
            {
               finish( headingColumnWidth,
                       userError,
                       internalError );
            }
         }
         else
         {
            finish( headingColumnWidth,
                    null,
                    null );
         }
      }
      catch ( Throwable throwable )
      {
         _testSet.failureCallback( throwable );
      }
   }

   final void finish( int               headingColumnWidth,
                      UserError         userError,
                      InternalException internalError )
   {
      TestManager parent;

      if ( _testData.getLocalExceptionCount() > 0 )
      {
         _testData.setState( TestState.COMPLETE_FAILED );
      }
      else if ( userError != null )
      {
         System.out.println( userError.getMessage() );
         _testData.setState( TestState.COMPLETE_FAILED );
      }
      else if ( internalError != null )
      {
         Utilities.printStackTrace( internalError );
         _testData.setState( TestState.COMPLETE_FAILED );
      }
      else if ( _testData.getTestState() == null )
      {
         _testData.setState( TestState.COMPLETE_FAILED );
      }
      else if ( !(_testData.getTestState().skipped()) )
      {
         _testData.setState( TestState.COMPLETE_SUCCEEDED );
      }

      if ( _testSet.getExecutionMode().recordStatus() )
      {
         _testData.recordState();
      }

      parent = getParent();

      if ( parent == null )
      {
         _testSet.completionCallback();
      }
      else
      {
         parent.childCallback( this,
                               headingColumnWidth,
                               userError,
                               internalError );
      }
   }

   final private void launchChildTests( int headingColumnWidth )
   {
      _testData.setState( TestState.RUNNING_SUBTESTS );

      if ( _testSet.getExecutionMode().isDiscovery() )
      {
         for ( TestManager child : listChildren() )
         {
            child.submitTest( headingColumnWidth );
         }
      }
      else
      {
         if ( _testData.getSubtestSequencingMode().isSequential() )
         {
            getChild( 0 ).submitTest( headingColumnWidth );
         }
         else
         {
            for ( TestManager child : listChildren() )
            {
               child.submitTest( headingColumnWidth );
            }
         }
      }
   }

   final private boolean shouldRunSubtests()
   {
      if ( _testData.getSubtestContinuationMode().skipSubtestsIfLocalTestsFail() )
      {
         if ( _testData.getLocalExceptionCount() > 0 )
         {
            return false;
         }
      }

      return true;
   }

   final private boolean shouldVerify()
   {
      if ( _testSet.getExecutionMode().useConfigurationTestDataRunFlag() )
      {
         if ( _pass1TestData.getRun(_testSet.useAlternateRun()) )
         {
            if ( _pass1TestData.prerequisitesSucceeded() )
            {
               return true;
            }
            else
            {
               _testData.recordState( TestState.SKIPPED_DUE_TO_PREREQUISITE_ERROR );
               recordChildrenStates( TestState.SKIPPED_DUE_TO_PREREQUISITE_ERROR );
               return false;
            }
         }
         else
         {
            _testData.recordState( TestState.SKIPPED_BY_USER );
            recordChildrenStates( TestState.SKIPPED_BY_USER );
            return false;
         }
      }
      else
      {
         if ( _testSet.getExecutionMode().checkPrerequisites() )
         {
            if ( _pass1TestData.prerequisitesSucceeded() )
            {
               return true;
            }
            else
            {
               _testData.recordState( TestState.SKIPPED_DUE_TO_PREREQUISITE_ERROR );
               recordChildrenStates( TestState.SKIPPED_DUE_TO_PREREQUISITE_ERROR );
               return false;
            }
         }
         else
         {
            return true;
         }
      }
   }

   final private void recordChildrenStates( TestState childState )
   {
      for ( TestData child : _testData.getChildren() )
      {
         child.recordStateOnBranch( childState );
      }
   }

   final void submitTest( int headingColumnWidth )
   {
      synchronized ( MONITOR_HOLDER )
      {
         _testSet.submitConcurrentTest( this,
                                        headingColumnWidth );
      }
   }

   final int getSiblingIndex()
   {
      return _testData.getSiblingIndex();
   }

   final private TestManager getChild( int index )
   {
      return _testData.getChild( index ).getTestManager();
   }

   final private int getChildCount()
   {
      return _testData.getChildCount();
   }

   final private TestManager[] listChildren()
   {
      TestData    childTestData[];
      TestManager childTestManagers[];
      int         index;

      childTestData = _testData.getChildren();

      childTestManagers = new TestManager[ childTestData.length ];

      for ( index=0; index<childTestData.length; index++ )
      {
         childTestManagers[ index ] = childTestData[ index ].getTestManager();
      }

      return childTestManagers;
   }

   final TestData getTestData()
   {
      return _testData;
   }

   /**
    * For adding a prerequisite.  <br>
    * <br> Prerequisites can be added while the test is running.
    * Prerequisites are collected and recorded during the discovery
    * phase.  Prerequisites supplied to Undercamber during the execution
    * phase are ignored.
    *
    * @param prerequisite
    *        The prerequisite to add to the test.
    *
    * @throws UserError
    *         If the prerequisite cannot be found
    */
   final public void addPrerequisite( Prerequisite prerequisite )
      throws UserError
   {
      if ( _testSet.getExecutionMode().canAcceptPrerequisites() )
      {
         synchronized ( MONITOR_HOLDER )
         {
            _testData.addPrerequisite( prerequisite );
         }
      }
      else
      {
         throw new UserError( "Error:  Can add prerequisites only during discovery phase.  Mode = " + _testSet.getExecutionMode() + ", expected mode = " + ExecutionMode.DISCOVERY );
      }
   }

   /**
    * Add a subtest.  <br>
    * <br>
    * The subtests are executed AFTER the parent test is executed.
    * The subtests are executed in the same order as the calls to
    * this method.
    *
    * @param testUnit
    *        The subtest to be added.
    *
    * @throws UserError
    *         If this method is called when tests are not running
    */
   final public void addSubtest( TestUnit testUnit )
      throws UserError
   {
      if ( _testData.getTestState() == null )
      {
         synchronized ( MONITOR_HOLDER )
         {
            _testData.addChild( testUnit,
                                _margin + "   " );
         }
      }
      else if ( _testData.getTestState() == TestState.INITIALIZED )
      {
         _testData.addChild( testUnit,
                             _margin + "   " );
      }
      else
      {
         throw new UserError( "Error:  Invalid state when calling " + Utilities.whatMethodAmI() + ".  State = " + _testData.getTestState() + "; expected state = " + TestState.INITIALIZED + " or null." );
      }
   }

   /**
    * Add an exception.  If a test adds any exceptions. Undercamber will
    * consider the test a failure.  If a test throws any
    * exceptions, Undercamber will consider the test a failure.
    *
    * @param throwable
    *        The exception to be added.
    */
   final public void addException( Throwable throwable )
   {
      StringBuffer stringBuffer;

      synchronized ( MONITOR_HOLDER )
      {
         _testData.addException( throwable );
      }

      stringBuffer = new StringBuffer();

      stringBuffer.append( _margin ).append( "   Error in " ).append( _testData.getHeading() ).append( ":" ).append( System.lineSeparator() );;
      Utilities.printStackTrace( throwable,
                                 _margin + "      ",
                                 stringBuffer );
      System.out.print( stringBuffer.toString() );
   }

   /**
    * Add exceptions.  If a test adds any exceptions. Undercamber will
    * consider the test a failure.  If a test throws any
    * exceptions, Undercamber will consider the test a failure.
    *
    * @param throwables
    *        The exception to be added.
    */
   final public void addException( java.util.List<Throwable> throwables )
   {
      StringBuffer stringBuffer;

      synchronized ( MONITOR_HOLDER )
      {
         _testData.addExceptions( throwables );
      }

      stringBuffer = new StringBuffer();

      for ( Throwable throwable : throwables )
      {
         stringBuffer.append( _margin ).append( "   Error in " ).append( _testData.getHeading() ).append( ":" ).append( System.lineSeparator() );;
         Utilities.printStackTrace( throwable,
                                    _margin + "      ",
                                    stringBuffer );
      }

      System.out.print( stringBuffer.toString() );
   }

   /**
    * Add exceptions.  If a test adds any exceptions. Undercamber will
    * consider the test a failure.  If a test throws any
    * exceptions, Undercamber will consider the test a failure.
    *
    * @param throwables
    *        The exception to be added.
    */
   final public void addException( Throwable... throwables )
   {
      StringBuffer stringBuffer;

      synchronized( MONITOR_HOLDER )
      {
         _testData.addExceptions( throwables );
      }

      stringBuffer = new StringBuffer();

      for ( Throwable throwable : throwables )
      {
         stringBuffer.append( _margin ).append( "   Error in " ).append( _testData.getHeading() ).append( ":" ).append( System.lineSeparator() );;
         Utilities.printStackTrace( throwable,
                                    _margin + "      ",
                                    stringBuffer );
      }

      System.out.print( stringBuffer.toString() );
   }

   /**
    * Add an multi-line informational message.
    *
    * @param message
    *        The message.  Each string is one line of the message
    */
   final public void addMessage( String... message )
   {
      StringBuffer stringBuffer;
      int          index;

      synchronized ( MONITOR_HOLDER )
      {
         _testData.addException( new Message(message) );
      }

      if ( message.length > 0 )
      {
         stringBuffer = new StringBuffer();

         stringBuffer.append( _margin ).append( "      - " ).append( message[0] ).append( System.lineSeparator() );

         for ( index=1; index<message.length; index++ )
         {
            stringBuffer.append( _margin ).append( "        " ).append( message[index] ).append( System.lineSeparator() );
         }

         System.out.print( stringBuffer.toString() );
      }
   }

   /**
    * Add an multi-line informational message.
    *
    * @param message
    *        The message.  Each string is one line of the message
    */
   final public void addMessage( java.util.List<String> message )
   {
      StringBuffer stringBuffer;
      int          index;

      synchronized ( MONITOR_HOLDER )
      {
         _testData.addException( new Message(message) );
      }

      if ( message.size() > 0 )
      {
         stringBuffer = new StringBuffer();

         stringBuffer.append( _margin ).append( "      - " ).append( message.get(0) ).append( System.lineSeparator() );

         for ( index=1; index<message.size(); index++ )
         {
            stringBuffer.append( _margin ).append( "        " ).append( message.get(index) ).append( System.lineSeparator() );
         }

         System.out.print( stringBuffer.toString() );
      }
   }

   /**
    * Add a one-line informational message
    *
    * @param message
    *        The message.
    */
   final public void addMessage( String message )
   {
      synchronized ( MONITOR_HOLDER )
      {
         _testData.addException( new Message(message) );
      }

      System.out.println( _margin + "      - " + message );
   }

   /**
    * Add a callback to be notified when tests are complete.  <p>
    *
    * Callbacks are notified after all tests are complete, but before any calls to {@link Requirement#testComplete}.<p>
    *
    * {@link CompletionCallback} objects passed to TestManager during the first pass (the discovery pass)
    * are retained, and these objects are notified of test completion.  {@link CompletionCallback} objects
    * passed during the second pass are ignored.
    *
    * @param completionCallback
    *        The callback to be notified after all tests are complete.
    *
    * @throws UserError
    *         If this method is called when the tests are not being
    *         run.
    */
   final public void addCompletionCallback( CompletionCallback completionCallback )
      throws UserError
   {
      if ( _testData.getTestState().canAcceptConfiguration() )
      {
         synchronized( MONITOR_HOLDER )
         {
            _testSet.addCompletionCallback( completionCallback );
         }
      }
      else
      {
         throw new UserError( "Error:  Can add completion callbacks only inside the test unit.  State = " + _testData.getTestState() + "." );
      }
   }

   final private TestManager getParent()
   {
      TestData parentTestData;

      parentTestData = _testData.getParent();

      if ( parentTestData == null )
      {
         return null;
      }
      else
      {
         return parentTestData.getTestManager();
      }
   }

   /**
    * Is this the first pass?
    *
    * @return
    * <table summary="return value table">
    *    <tr>
    *       <td>&nbsp;&nbsp;&nbsp;true:</td>
    *       <td>it is the first (discovery) pass</td>
    *    </tr>
    *    <tr>
    *       <td>&nbsp;&nbsp;&nbsp;false:</td>
    *       <td>it is the second (validation) pass</td>
    *    </tr>
    * </table>
    */
   final public boolean isDiscoveryPass()
   {
      return _testSet.getExecutionMode().isDiscovery();
   }

   /**
    * Get a one-line description of the test.  The description is
    * constructed from the test's class, the test's method, and the
    * argument, if any
    *
    * @return The description
    */
   final public String getHeading()
   {
      return _testData.getHeading();
   }

   /**
    * Get the SubtestSequencingMode.  This indicates how Undercamber will
    * sequence the subtests.
    *
    * @return The SubtestSequencingMode
    */
   final public SubtestSequencingMode getSubtestSequencingMode()
   {
      return _testData.getSubtestSequencingMode();
   }

   /**
    * Get the Prerequisites for this test.
    *
    * @return The Prerequisites
    */
   final public Prerequisite[] getPrerequisites()
   {
      return _testData.getPrerequisites();
   }

   /**
    * Get the command used to invoke the JVM.  This does not
    * include the parameters passed to the JVM.
    *
    * @return The JVM command
    */
   final public String getJVMCommand()
   {
      return _testSet.getJVMCommand();
   }

   /**
    * Get the test set name.  Test sets are specified in the Undercamber
    * configuration file.  Each test set is run in a different
    * process.
    *
    * @return The name of this test's test set.
    */
   final public String getTestSetName()
   {
      return _testSet.getTestSetName();
   }

   /**
    * Get the thread count.  Note that this might be different
    * between the first pass (the discovery pass) and the second
    * pass (the validation pass).
    *
    * @return The capacity of the thread pool.
    */
   final public int getThreadCount()
   {
      return _testSet.getThreadCount();
   }

   /**
    * Get the thread count used in the first pass (the discovery
    * pass).
    *
    * @return The capacity of the thread pool during the first
    *         pass.
    */
   final public int getPass1ThreadCount()
   {
      return _testSet.getPass1ThreadCount();
   }

   /**
    * Get the thread count used in the second pass (the validation
    * pass).
    *
    * @return The capacity of the thread pool during the second
    *         pass.
    */
   final public int getPass2ThreadCount()
   {
      return _testSet.getThreadCount();
   }

   /**
    * Get the name of the overall test suite.
    *
    * @return The name of the test suite.
    */
   final String getTestSuiteName()
   {
      return _testSet.getTestSuiteName();
   }

   /**
    * Get the SubtestContinuationMode.  This indicates how Undercamber
    * should handle this test's subtests if this test fails.
    *
    * @return The SubtestContinuationMode
    */
   final public SubtestContinuationMode getSubtestContinuationMode()
   {
      return _testData.getSubtestContinuationMode();
   }

   /**
    * Get the test parameters for this test suite.  Parameters are
    * specified in the Undercamber configuration file and on the Undercamber
    * command line
    *
    * @return The parameters
    */
   final public java.util.List<String> getParameters()
   {
      return _testSet.getTestParameters();
   }

   /**
    * Get the parameter following the specified parameter.  This is
    * a convenience method to help parse the test parameters.  For
    * example, if the parameters include a flag/value pair, this
    * method can provide the value, given the flag.
    *
    * @param parameter
    *        The name of the specified parameter
    *
    * @return The value of the parameter immediately following the
    *         specified parameter
    *
    * @throws UserError
    *         If the specified parameter does not exist, or if
    *         the specified parameter is the last in the list of
    *         parameters.
    */
   final public String getFollowingParameterAsString( String parameter )
      throws UserError
   {
      return _testSet.getFollowingParameter( parameter );
   }

   /**
    * Get the parameter following the specified parameter,
    * interpreted as an integer. This is a convenience method to
    * help parse the test parameters. For example, if the
    * parameters include a flag/value pair, this method can provide
    * the value, given the flag.
    *
    * @param parameter
    *        The name of the specified parameter
    *
    * @return The value of the parameter immediately following the
    *         specified parameter
    *
    * @throws UserError
    *         If the specified parameter does not exist, or if
    *         the specified parameter is the last in the list of
    *         parameters, or if the parameter is not an integer.
    */
   final public int getFollowingParameterAsInteger( String parameter )
      throws UserError
   {
      try
      {
         return Integer.parseInt( _testSet.getFollowingParameter(parameter) );
      }
      catch ( NumberFormatException numberFormatException )
      {
         throw new UserError( "Error:  Invalid integer format:  \"" + _testSet.getFollowingParameter(parameter) + "\"" );
      }
   }

   /**
    * Get the parameter following the specified
    * parameter,interpreted as a long. This is a convenience method
    * to help parse the test parameters. For example, if the
    * parameters include a flag/value pair, this method can provide
    * the value, given the flag.
    *
    * @param parameter
    *        The name of the specified parameter
    *
    * @return The value of the parameter immediately following the
    *         specified parameter
    *
    * @throws UserError
    *         If the specified parameter does not exist, or if
    *         the specified parameter is the last in the list of
    *         parameters, or if the parameter is not a long.
    */
   final public long getFollowingParameterAsLong( String parameter )
      throws UserError
   {
      try
      {
         return Long.parseLong( _testSet.getFollowingParameter(parameter) );
      }
      catch ( NumberFormatException numberFormatException )
      {
         throw new UserError( "Error:  Invalid long format:  \"" + _testSet.getFollowingParameter(parameter) + "\"" );
      }
   }

   /**
    * Get the parameter following the specified parameter,
    * interpreted as a double. This is a convenience method to
    * help parse the test parameters. For example, if the
    * parameters include a flag/value pair, this method can provide
    * the value, given the flag.
    *
    * @param parameter
    *        The name of the specified parameter
    *
    * @return The value of the parameter immediately following the
    *         specified parameter
    *
    * @throws UserError
    *         If the specified parameter does not exist, or if
    *         the specified parameter is the last in the list of
    *         parameters, or if the parameter is not a double.
    */
   final public double getFollowingParameterAsDouble( String parameter )
      throws UserError
   {
      try
      {
         return Double.parseDouble( _testSet.getFollowingParameter(parameter) );
      }
      catch ( NumberFormatException numberFormatException )
      {
         throw new UserError( "Error:  Invalid double format:  \"" + _testSet.getFollowingParameter(parameter) + "\"" );
      }
   }

   /**
    * Get the parameter following the specified parameter,
    * interpreted as a float. This is a convenience method to
    * help parse the test parameters. For example, if the
    * parameters include a flag/value pair, this method can provide
    * the value, given the flag.
    *
    * @param parameter
    *        The name of the specified parameter
    *
    * @return The value of the parameter immediately following the
    *         specified parameter
    *
    * @throws UserError
    *         If the specified parameter does not exist, or if
    *         the specified parameter is the last in the list of
    *         parameters, or if the parameter is not a float.
    */
   final public float getFollowingParameterAsFloat( String parameter )
      throws UserError
   {
      try
      {
         return Float.parseFloat( _testSet.getFollowingParameter(parameter) );
      }
      catch ( NumberFormatException numberFormatException )
      {
         throw new UserError( "Error:  Invalid float format:  \"" + _testSet.getFollowingParameter(parameter) + "\"" );
      }
   }

   /**
    * Determine whether a parameter is present.  This is a convenience method to
    * help determine if a simple flag is present on the command line.
    *
    * @param parameter
    *        The parameter name
    *
    * @return
    * <table summary="return value table">
    *    <tr>
    *       <td>&nbsp;&nbsp;&nbsp;true:</td>
    *       <td>The specified parameter is present</td>
    *    </tr>
    *    <tr>
    *       <td>&nbsp;&nbsp;&nbsp;false:</td>
    *       <td>The specified parameter is not present</td>
    *    </tr>
    * </table>
    */
   final public boolean containsParameter( String parameter )
   {
      return _testSet.containsParameter( parameter );
   }

   /**
    * Get the directory created by Undercamber specifically as a work
    * space for the tests.  Tests are not required to use this
    * directory; this is provided as a convenience.  The directory
    * is guaranted to be empty.
    *
    * @return The work directory
    */
   final public java.io.File getUserWorkingDirectory()
   {
      return new java.io.File( _testSet.getLocalResultsDirectory(), "work" );
   }
}

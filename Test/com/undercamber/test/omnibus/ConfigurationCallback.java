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

package com.undercamber.test.omnibus;

final public class ConfigurationCallback
   implements com.undercamber.ConfigurationCallback
{
   final public void configure( com.undercamber.Configurator configurator )
      throws Throwable
   {
      com.undercamber.TestSetBuilder testSetBuilder;
      com.undercamber.Path           path;

      configurator.setSuiteName( "UndercamberTest" );
      configurator.setResultsRootDirectoryName( "${UNDERCAMBER_TEST_RESULTS_DIRECTORY}" );

      testSetBuilder = configurator.getEmptyTestSetBuilder();

      path = new com.undercamber.Path();
      path.addEntries( "${UNDERCAMBER_PROJECT_ROOT}/Source",
                       "${UNDERCAMBER_PROJECT_ROOT}/Test" );
      testSetBuilder.appendJavaParameterPair( "-cp",
                                              path.toString() );

      testSetBuilder.setJVMDirectoryName( "${JAVA_HOME}" );

      // Test Set 1

      testSetBuilder.setTestSetName( "APITest" );
      testSetBuilder.setClassName( "com.undercamber.test.omnibus.apitests.APITests1" );

      testSetBuilder.createTestSet();

      // Test Set 2

      testSetBuilder.setTestSetName( "GUITest" );
      testSetBuilder.setClassName( "com.undercamber.test.omnibus.guitests.GUITests1" );

      testSetBuilder.createTestSet();

      // Test Set 3

      testSetBuilder.setTestSetName( "Prerequisite" );
      testSetBuilder.setClassName( "com.undercamber.test.omnibus.prerequisites.PrerequisiteExcerciser1" );

      testSetBuilder.createTestSet();

      // Test Set 4

      testSetBuilder.setTestSetName( "Constructor" );
      testSetBuilder.setClassName( "com.undercamber.test.omnibus.constructor.Constructor1" );

      testSetBuilder.createTestSet();
   }
}
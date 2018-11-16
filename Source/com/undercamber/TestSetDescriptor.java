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

final class TestSetDescriptor
{
   private Undercamber                  _undercamber;
   private String                       _className;
   private String                       _testSetName;
   private String                       _testSuiteName;
   private String                       _jvmDirectoryName;
   private Integer                      _configurationThreadCount;
   private java.util.List<String>       _testParameters;
   private java.util.List<String>       _javaParameters;
   private java.util.Map<String,String> _environmentVariables;
   private TestSet                      _testSet;

   TestSetDescriptor( Undercamber                  undercamber,
                      Class<? extends TestUnit>    type,
                      String                       testSetName,
                      String                       testSuiteName,
                      String                       jvmDirectoryName,
                      Integer                      configurationThreadCount,
                      java.util.List<String>       testParameters,
                      java.util.List<String>       javaParameters,
                      java.util.Map<String,String> environmentVariables )
   {
      _testSet = null;

      _undercamber = undercamber;
      _testSetName = testSetName;
      _testSuiteName = testSuiteName;

      _className = type.getName();

      _jvmDirectoryName = jvmDirectoryName;

      _configurationThreadCount = configurationThreadCount;

      _testParameters = testParameters;

      _javaParameters = javaParameters;

      _environmentVariables = environmentVariables;
   }

   final String getTestSetName()
   {
      return _testSetName;
   }

   final void setTestSuiteName( String testSuiteName )
   {
      _testSuiteName = testSuiteName;
   }

   final String getClassName()
   {
      return _className;
   }

   final void checkJVMCommand()
      throws UserError
   {
      getJVMCommand();
   }

   final String getJVMCommand()
      throws UserError
   {
      java.io.File jvm;

      if ( _jvmDirectoryName == null )
      {
         return "java";
      }

      jvm = new java.io.File( _jvmDirectoryName );
      jvm = new java.io.File( jvm, "bin" );

      if ( java.io.File.separator.equals("\\") )
      {
         jvm = new java.io.File( jvm, "java.exe" );
         if ( jvm.exists() )
         {
            return jvm.getPath();
         }
      }
      else
      {
         jvm = new java.io.File( jvm, "java" );
         if ( jvm.exists() )
         {
            return jvm.getPath();
         }
      }

      throw new UserError( "Could not find JVM in " + _jvmDirectoryName );
   }

   final Integer getConfigurationThreadCount()
   {
      return _configurationThreadCount;
   }

   final TestSet getTestSet( java.util.List<String>               commandLineParameters,
                             int                                  index,
                             int                                  threadCount,
                             java.util.concurrent.ExecutorService executorService )
      throws UserError
   {
      java.util.List<String> testParameters;

      testParameters = new java.util.ArrayList<String>();
      testParameters.addAll( commandLineParameters );
      if ( _testParameters != null )
      {
         testParameters.addAll( _testParameters );
      }

      if ( _testSet == null )
      {
         _testSet = new TestSet( _undercamber,
                                 index,
                                 _className,
                                 _testSetName,
                                 _testSuiteName,
                                 getJVMCommand(),
                                 threadCount,
                                 testParameters,
                                 _javaParameters,
                                 _environmentVariables,
                                 executorService );
      }

      return _testSet;
   }
}

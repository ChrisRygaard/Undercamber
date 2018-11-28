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

final class TestSet
{
   final private static int    CLASS_PERSISTENCE_VERSION = 0;
   final private static String CLASS_PERSISTENCE_BRANCH  = "";

   private String                               _testUnitClassName;
   private String                               _testSetName;
   private String                               _testSuiteName;
   private String                               _jvmCommand;
   private int                                  _threadCount;
   private java.util.List<String>               _testParameters;
   private java.util.List<String>               _javaParameters;
   private java.util.Map<String,String>         _environmentVariables;
   private ExecutionMode                        _executionMode;
   private WatchdogThread                       _watchdogThread;
   private java.util.List<CompletionCallback>   _completionCallbacks;
   private int                                  _configuredIndex;
   private java.util.concurrent.ExecutorService _executorService;
   private TestUnit                             _testUnit;
   private TestManager                          _rootTestManager;
   private TestData                             _pass1TestData;
   private java.io.File                         _resultsDirectory;
   private StatusFile                           _statusFile;
   private Undercamber                          _undercamber;
   private TestSetWindow                        _testSetWindow;

   TestSet( Undercamber                          undercamber,
            int                                  configuredIndex,
            String                               testUnitClassName,
            String                               testSetName,
            String                               testSuiteName,
            String                               jvmCommand,
            int                                  threadCount,
            java.util.List<String>               testParameters,
            java.util.List<String>               javaParameters,
            java.util.Map<String,String>         environmentVariables,
            java.util.concurrent.ExecutorService executorService )
   {
      if ( testUnitClassName == null )
      {
         throw new NullPointerException( "Internal error:  Class name for test unit cannot be null" );
      }
      if ( testSetName == null )
      {
         throw new NullPointerException( "Internal error:  Test name cannot be null" );
      }
      _undercamber = undercamber;
      _configuredIndex = configuredIndex;
      _testUnitClassName = testUnitClassName;
      _testSetName = testSetName;
      _testSuiteName = testSuiteName;
      _jvmCommand = jvmCommand;
      _threadCount = threadCount;
      _completionCallbacks = new java.util.ArrayList<CompletionCallback>();

      _testParameters = new java.util.ArrayList<String>();
      if ( testParameters != null )
      {
         _testParameters.addAll( testParameters );
      }

      _javaParameters = new java.util.ArrayList<String>();
      if ( javaParameters != null )
      {
         _javaParameters.addAll( javaParameters );
      }

      if ( environmentVariables == null )
      {
         _environmentVariables = null;
      }
      else
      {
         _environmentVariables = new java.util.HashMap<String,String>();
         for ( String name : environmentVariables.keySet() )
         {
            _environmentVariables.put( name, environmentVariables.get(name) );
         }
      }

      _executorService = executorService;
   }

   TestSet( java.io.File           executiveFile,
            java.io.File           resultsDirectory,
            ExecutionMode          executionMode,
            int                    headingColumnWidth,
            int                    threadCount,
            java.util.List<String> commandLineTestParameters )
      throws java.io.IOException
   {
      int    elementCount;
      int    index;
      String name;
      String value;

      _undercamber = null;
      _completionCallbacks = new java.util.ArrayList<CompletionCallback>();

      _resultsDirectory = resultsDirectory;
      _threadCount = threadCount;
      _statusFile = new StatusFile( getLocalResultsDirectory() );
      _watchdogThread = new WatchdogThread( resultsDirectory,
                                            _testSetName );

      try ( java.io.FileInputStream fileInputStream = new java.io.FileInputStream(executiveFile) )
      {
         try ( java.io.BufferedInputStream bufferedInputStream = new java.io.BufferedInputStream(fileInputStream,262144) )
         {
            try ( java.io.DataInputStream dataInputStream = new java.io.DataInputStream(bufferedInputStream) )
            {
               _configuredIndex = dataInputStream.readInt();

               _testUnitClassName = dataInputStream.readUTF();

               _testSetName = dataInputStream.readUTF();

               _testSuiteName = dataInputStream.readUTF();

               if ( dataInputStream.readBoolean() )
               {
                  _jvmCommand = dataInputStream.readUTF();
               }
               else
               {
                  _jvmCommand = null;
               }

               _testParameters = new java.util.ArrayList<String>();
               _testParameters.addAll( commandLineTestParameters );
               if ( dataInputStream.readBoolean() )
               {
                  elementCount = dataInputStream.readInt();
                  for ( index=0; index<elementCount; index++ )
                  {
                     _testParameters.add( dataInputStream.readUTF() );
                  }
               }

               if ( dataInputStream.readBoolean() )
               {
                  _javaParameters = new java.util.ArrayList<String>();
                  elementCount = dataInputStream.readInt();
                  for ( index=0; index<elementCount; index++ )
                  {
                     _javaParameters.add( dataInputStream.readUTF() );
                  }
               }
               else
               {
                  _javaParameters = null;
               }

               if ( dataInputStream.readBoolean() )
               {
                  _environmentVariables = new java.util.HashMap<String,String>();
                  elementCount = dataInputStream.readInt();
                  for ( index=0; index<elementCount; index++ )
                  {
                     name = dataInputStream.readUTF();
                     if ( dataInputStream.readBoolean() )
                     {
                        value = dataInputStream.readUTF();
                     }
                     else
                     {
                        value = null;
                     }
                     _environmentVariables.put( name, value );
                  }
               }
               else
               {
                  _environmentVariables = null;
               }
            }
         }
      }

      _executorService = java.util.concurrent.Executors.newFixedThreadPool( _threadCount );
   }

   final java.io.File writeExecutiveFile()
      throws java.io.IOException
   {
      java.io.File executiveFile;
      String       value;

      executiveFile = getExecutiveFile();

      try ( java.io.FileOutputStream fileOutputStream = new java.io.FileOutputStream(executiveFile) )
      {
         try ( java.io.BufferedOutputStream bufferedOutputStream = new java.io.BufferedOutputStream(fileOutputStream,262144) )
         {
            try ( java.io.DataOutputStream dataOutputStream = new java.io.DataOutputStream(bufferedOutputStream) )
            {
               dataOutputStream.writeInt( _configuredIndex );

               dataOutputStream.writeUTF( _testUnitClassName );

               dataOutputStream.writeUTF( _testSetName );

               dataOutputStream.writeUTF( _testSuiteName );

               dataOutputStream.writeBoolean( _jvmCommand != null );
               if ( _jvmCommand != null )
               {
                  dataOutputStream.writeUTF( _jvmCommand );
               }

               dataOutputStream.writeBoolean( _testParameters != null );
               if ( _testParameters != null )
               {
                  dataOutputStream.writeInt( _testParameters.size() );
                  for ( String testParameter : _testParameters )
                  {
                     dataOutputStream.writeUTF( testParameter );
                  }
               }

               dataOutputStream.writeBoolean( _javaParameters != null );
               if ( _javaParameters != null )
               {
                  dataOutputStream.writeInt( _javaParameters.size() );
                  for ( String javaParameter : _javaParameters )
                  {
                     dataOutputStream.writeUTF( javaParameter );
                  }
               }

               dataOutputStream.writeBoolean( _environmentVariables != null );
               if ( _environmentVariables != null )
               {
                  dataOutputStream.writeInt( _environmentVariables.size() );
                  for ( String name : _environmentVariables.keySet() )
                  {
                     dataOutputStream.writeUTF( name );
                     value = _environmentVariables.get( name );
                     dataOutputStream.writeBoolean( value != null );
                     if ( value != null )
                     {
                        dataOutputStream.writeUTF( value );
                     }
                  }
               }
            }
         }
      }

      return executiveFile;
   }

   final private TestUnit createAndGetTestUnit()
      throws UserError,
             InternalException
   {
      Class<?>                         testUnitClass;
      java.lang.reflect.Constructor<?> constructor;
      Object                           testUnitObject;

      if ( _testUnit == null )
      {
         try
         {
            testUnitClass = Class.forName( _testUnitClassName );

            if ( TestUnit.class.isAssignableFrom(testUnitClass) )
            {
               constructor = testUnitClass.getConstructor();
               testUnitObject = constructor.newInstance();
               _testUnit = (TestUnit)testUnitObject;
            }
            else
            {
               throw new UserError( "Error:  Class " + _testUnitClassName + " does not implement interface TestUnit." );
            }
         }
         catch ( ClassNotFoundException classNotFoundException )
         {
            throw new UserError( "Error:  Could not find class " + _testUnitClassName );
         }
         catch ( NoSuchMethodException noSuchMethodException )
         {
            throw new UserError( "Error:  Could not find no-argument constructor for class " + _testUnitClassName );
         }
         catch ( InstantiationException | IllegalAccessException | java.lang.reflect.InvocationTargetException callException )
         {
            throw new InternalException( "Internal Error:  Difficulties calling no-argument constructor for class " + _testUnitClassName );
         }
      }

      return _testUnit;
   }

   final String getTopLevelClassName()
   {
      return _testUnitClassName;
   }

   final void initialize( java.io.File resultsDirectory )
   {
      _resultsDirectory = resultsDirectory;
   }

   final void setStatusFile( StatusFile statusFile )
   {
      _statusFile = statusFile;
   }

   final StatusFile getStatusFile()
   {
      return _statusFile;
   }

   final int getConfiguredIndex()
   {
      return _configuredIndex;
   }

   final String getJVMCommand()
   {
      return _jvmCommand;
   }

   final int getThreadCount()
   {
      return _threadCount;
   }

   final int getPass1ThreadCount()
   {
      return _undercamber.getPass1ThreadCount();
   }

   final int getPass2ThreadCount()
   {
      return _undercamber.getThreadCount( _configuredIndex );
   }

   final void runPass1()
      throws UserError,
             InternalException
   {
      _executionMode = ExecutionMode.PASS_1_DISCOVERY;

      _rootTestManager = new TestManager( null,
                                          createAndGetTestUnit(),
                                          this,
                                          null,
                                          "" );

      _rootTestManager.submitTest( 100 );
   }

   final private java.io.File getTestConfigurationFile()
   {
      java.io.File configurationFile;

      configurationFile = new java.io.File( System.getProperty("user.home") );
      configurationFile = new java.io.File( configurationFile, ".Undercamber" );
      configurationFile = new java.io.File( configurationFile, _testSuiteName );
      configurationFile = new java.io.File( configurationFile, "tests" );
      configurationFile = new java.io.File( configurationFile, _testSetName+".dat" );

      return configurationFile;
   }

   final String getTestSetName()
   {
      return _testSetName;
   }

   final String getTestSuiteName()
   {
      return _testSuiteName;
   }

   final String getConfigurationFileName()
   {
      return _testSetName + ".dat";
   }

   final java.util.List<String> getTestParameters()
   {
      java.util.List<String> testParameters;

      testParameters = new java.util.ArrayList<String>();
      testParameters.addAll( _testParameters );

      return testParameters;
   }

   final boolean containsParameter( String parameter )
   {
      return _testParameters.contains( parameter );
   }

   final String getFollowingParameter( String parameter )
      throws UserError
   {
      int index;

      index = _testParameters.indexOf( parameter );

      if ( index == -1 )
      {
         throw new UserError( "Error:  Could not find test parameter \"" + parameter + "\" in test set " + _testSetName );
      }

      index++;

      if ( index < _testParameters.size() )
      {
         return _testParameters.get( index );
      }
      else
      {
         throw new UserError( "Error:  No test parameter following \"" + parameter + "\"" );
      }
   }

   final java.util.Map<String,String> getEnvironmentVariables()
   {
      java.util.Map<String,String> environmentVariables;

      if ( _environmentVariables == null )
      {
         return null;
      }
      else
      {
         environmentVariables = new java.util.HashMap<String,String>();

         for ( String name : _environmentVariables.keySet() )
         {
            environmentVariables.put( name, _environmentVariables.get(name) );
         }

         return environmentVariables;
      }
   }

   final java.util.List<String> getJavaParameters()
   {
      java.util.List<String> javaParameters;

      javaParameters = new java.util.ArrayList<String>();
      javaParameters.addAll( _javaParameters );

      return javaParameters;
   }

   final TestSetWindow getTestSetWindow( java.util.List<String> commandLineTestParameters,
                                         javafx.stage.Window    ownerWindow )
   {
      if ( _testSetWindow == null )
      {
         _testSetWindow = new TestSetWindow( this,
                                             commandLineTestParameters,
                                             ownerWindow );
      }

      return _testSetWindow;
   }

   final void submitConcurrentTest( TestManager testManager,
                                    int         headingColumnWidth )
   {
      _executorService.submit( () -> testManager.testThread(headingColumnWidth) );
   }

   final void appendToSequence( SequenceList sequenceList )
   {
      _pass1TestData.appendToSequence( sequenceList );
   }

   final void initializePrerequisites( boolean useAlternateRunFlag )
      throws UserError
   {
      _pass1TestData.initializePrerequisites( useAlternateRunFlag );
   }

   final void addCompletionCallback( CompletionCallback completionCallback )
   {
      _completionCallbacks.add( completionCallback );
   }

   final void postRunCallback( java.util.List<TestData> testDataRoots )
   {
      java.util.List<TestData> roots;

      roots = new java.util.ArrayList<TestData>();

      for ( CompletionCallback completionCallback : _completionCallbacks )
      {
         roots.clear();
         roots.addAll( testDataRoots );
         completionCallback.testComplete( roots );
      }
   }

   final void savePass1ConfigurationData()
   {
      java.io.File testConfigurationFile;

      testConfigurationFile = getTestConfigurationFile();

      testConfigurationFile.getParentFile().mkdirs();

      writeTestData( testConfigurationFile,
                     _pass1TestData );
   }

   final java.util.Set<String> getTagNames()
   {
      java.util.Set<String> tagNames;

      tagNames = new java.util.HashSet<String>();

      _pass1TestData.addTagNamesToSet( tagNames );

      return tagNames;
   }

   final private TestData readTestData( java.io.File persistenceFile )
   {
      if ( persistenceFile.exists() )
      {
         try ( java.io.FileInputStream fileInputStream = new java.io.FileInputStream(persistenceFile) )
         {
            try ( java.io.BufferedInputStream bufferedInputStream = new java.io.BufferedInputStream(fileInputStream,262144) )
            {
               try ( java.io.DataInputStream dataInputStream = new java.io.DataInputStream(bufferedInputStream) )
               {
                  if ( dataInputStream.readInt() > Undercamber.PERSISTENCE_VERSION )
                  {
                     throw new java.io.IOException( "The database is from a newer version of Undercamber" );
                  }
                  if ( !(dataInputStream.readUTF().equals(Undercamber.PERSISTENCE_BRANCH)) )
                  {
                     throw new java.io.IOException( "The database is from an unrecognized branch of Undercamber" );
                  }

                  if ( dataInputStream.readInt() > CLASS_PERSISTENCE_VERSION )
                  {
                     throw new java.io.IOException( "The database is from a newer version of Undercamber" );
                  }
                  if ( !(dataInputStream.readUTF().equals(CLASS_PERSISTENCE_BRANCH)) )
                  {
                     throw new java.io.IOException( "The database is from an unrecognized branch of Undercamber" );
                  }

                  return new TestData( dataInputStream,
                                       null,
                                       this );
               }
            }
         }
         catch ( Throwable throwable )
         {
            Utilities.printStackTrace( throwable );
            return null;
         }
      }
      else
      {
         return null;
      }
   }

   final private void writeTestData( java.io.File persistenceFile,
                                     TestData     testData )
   {
      if ( persistenceFile.getParentFile() != null )
      {
         persistenceFile.getParentFile().mkdirs();
      }

      try ( java.io.FileOutputStream fileOutputStream = new java.io.FileOutputStream(persistenceFile) )
      {
         try ( java.io.BufferedOutputStream bufferedOutputStream = new java.io.BufferedOutputStream(fileOutputStream,262144) )
         {
            try ( java.io.DataOutputStream dataOutputStream = new java.io.DataOutputStream(bufferedOutputStream) )
            {
               dataOutputStream.writeInt( Undercamber.PERSISTENCE_VERSION );
               dataOutputStream.writeUTF( Undercamber.PERSISTENCE_BRANCH );

               dataOutputStream.writeInt( CLASS_PERSISTENCE_VERSION );
               dataOutputStream.writeUTF( CLASS_PERSISTENCE_BRANCH );

               testData.write( dataOutputStream );
            }
         }
      }
      catch ( Throwable throwable )
      {
         Utilities.printStackTrace( throwable );
      }
   }

   final boolean shouldRun( boolean useAlternateRunFlag )
   {
      return _pass1TestData.getRun( useAlternateRunFlag );
   }

   final void runPass2( int headingColumnWidth )
      throws UserError,
             InternalException
   {
      TestData configurationTestData;

      configurationTestData = readTestData( getTestConfigurationFile() );

      _executionMode = ExecutionMode.PASS_2_VERIFICATION;

      System.out.println( Utilities.padToRight(_testSetName + " ",
                                               headingColumnWidth+10,
                                               "................................") );

      _rootTestManager = new TestManager( null,
                                          createAndGetTestUnit(),
                                          this,
                                          configurationTestData,
                                          "   " );

      _rootTestManager.submitTest( headingColumnWidth + 3 );
   }

   final ExecutionMode getExecutionMode()
   {
      return _executionMode;
   }

   final boolean useAlternateRun()
   {
      return _executionMode.useAlternateRunFlag();
   }

   final void completionCallback()
   {
      TestData previousRunTestData;

      if ( _executionMode.isDiscovery() )
      {
         _pass1TestData = _rootTestManager.getTestData();

         previousRunTestData = readTestData( getTestConfigurationFile() );
         if ( previousRunTestData != null )
         {
            _pass1TestData.transferConfigurationFromPreviousRun( previousRunTestData );
         }

         savePass1ConfigurationData();

         _undercamber.pass1Callback();
      }
      else
      {
         writeTestData( getBinaryResultsFile(),
                        _rootTestManager.getTestData() );

         shutdown();
      }
   }

   final void failureCallback( Throwable throwable )
   {
      _undercamber.failureCallback( throwable );
   }

   final int computeHeadingColumnWidth( int maximumHeadingColumnWidth )
   {
      return _pass1TestData.getHeadingColumnWidth( maximumHeadingColumnWidth,
                                                   "" );
   }

   final TestData getPass1TestData()
   {
      return _pass1TestData;
   }

   final TestData getTestDataFromPersistence()
   {
      return readTestData( getBinaryResultsFile() );
   }

   final private java.io.File getExecutiveFile()
   {
      java.io.File executiveFile;

      executiveFile = new java.io.File( getLocalResultsDirectory(), "UndercamberWorkingDirectory" );
      executiveFile = new java.io.File( executiveFile, "executive" );
      executiveFile.mkdirs();
      executiveFile = new java.io.File( executiveFile, _testSetName+".dat" );

      return executiveFile;
   }

   final private java.io.File getBinaryResultsFile()
   {
      java.io.File binaryResultsFile;

      binaryResultsFile = new java.io.File( getLocalResultsDirectory(), "UndercamberWorkingDirectory" );
      binaryResultsFile = new java.io.File( binaryResultsFile, "tests" );
      binaryResultsFile = new java.io.File( binaryResultsFile, _testSetName+".dat" );

      return binaryResultsFile;
   }

   final java.io.File getLocalResultsDirectory()
   {
      return _resultsDirectory;
   }

   final void shutdown()
   {
      _executorService.shutdown();
      if ( _watchdogThread != null )
      {
         _watchdogThread.stop();
      }
   }

   final void writeToXML( Undercamber         undercamber,
                          String              margin,
                          java.io.PrintStream printStream )
   {
      printStream.println( margin + "<testSet>" );
      printStream.println( margin + "   <name>" + _testSetName + "</name>" );
      printStream.println( margin + "   <pass2ThreadCount>" + undercamber.getThreadCount(_configuredIndex) + "</pass2ThreadCount>" );
      printStream.println( margin + "   <jvmCommand>" + _jvmCommand + "</jvmCommand>" );

      printStream.println( margin + "   <javaParameters>" );
      for ( String javaParameter : _javaParameters )
      {
         printStream.println( margin + "      <parameter value=\"" + javaParameter + "\"/>" );
      }
      printStream.println( margin + "   </javaParameters>" );

      printStream.println( margin + "   <testParameters>" );
      for ( String testParameter : _testParameters )
      {
         printStream.println( margin + "      <parameter value=\"" + testParameter + "\"/>" );
      }
      printStream.println( margin + "   </testParameters>" );

      _pass1TestData.writeXMLReport( margin + "   ",
                                     printStream );

      printStream.println( margin + "</testSet>" );
   }

   final private static void showUsageMessage()
   {
      //                                                                              0                   1                      2              3            4...
      System.out.println( "Usage:  " + TestSet.class.getName() + " <executive file name> <results directory> <heading column width> <thread count> {<parameter>...}" );
   }

   final public static void main( String arguments[] )
   {
      java.io.File           executiveFile;
      java.io.File           resultsDirectory;
      ExecutionMode          executionMode;
      int                    headingColumnWidth;
      int                    threadCount;
      java.util.List<String> commandLineTestParameters;
      int                    index;
      TestSet                testSet;
      boolean                cleanup;

      if ( arguments.length < 4 )
      {
         showUsageMessage();
      }
      else
      {
         try
         {
            executiveFile = new java.io.File( arguments[0] );

            resultsDirectory = new java.io.File( arguments[1] );

            headingColumnWidth = Integer.parseInt( arguments[2] );

            threadCount = Integer.parseInt( arguments[3] );

            commandLineTestParameters = new java.util.ArrayList<String>();
            for ( index=4; index<arguments.length; index++ )
            {
               commandLineTestParameters.add( arguments[index] );
            }

            testSet = null;
            cleanup = false;

            try
            {
               testSet = new TestSet( executiveFile,
                                      resultsDirectory,
                                      ExecutionMode.PASS_2_VERIFICATION,
                                      headingColumnWidth,
                                      threadCount,
                                      commandLineTestParameters );

               testSet.runPass2( headingColumnWidth );
            }
            catch ( Throwable throwable )
            {
               Utilities.printStackTrace( throwable );
               cleanup = true;
            }
            finally
            {
               if ( cleanup )
               {
                  if ( testSet != null )
                  {
                     testSet.shutdown();
                  }
               }
            }
         }
         catch ( NumberFormatException NumberFormatException )
         {
            showUsageMessage();
         }
         catch ( Throwable throwable )
         {
            Utilities.printStackTrace( throwable );
         }
      }
   }
}

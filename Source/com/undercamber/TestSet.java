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
   private java.util.List<String>               _jvmParameters;
   private java.util.Map<String,String>         _environmentVariables;
   private ExecutionMode                        _executionMode;
   private WatchdogThread                       _watchdogThread;
   private java.util.List<CompletionCallback>   _completionCallbacks;
   private int                                  _configuredIndex;
   private java.util.concurrent.ExecutorService _executorService;
   private TestUnit                             _testUnit;
   private TestManager                          _rootTestManager;
   private TestData                             _testData;
   private java.io.File                         _resultsDirectory;
   private java.io.File                         _pass1StatusFile;
   private StatusFile                           _statusFile;
   private TestSetWindow                        _testSetWindow;
   private java.util.List<String>               _testParameters;
   private java.util.List<String>               _configurationTestParameters;
   private java.util.List<String>               _commandLineTestParameters;

   TestSet( java.util.concurrent.ExecutorService executorService,
            java.util.Map<String,String>         environmentVariables,
            String                               jvmCommand,
            java.util.List<String>               jvmParameters,
            String                               testSuiteName,
            int                                  configuredIndex,
            String                               testSetName,
            String                               testUnitClassName,
            int                                  threadCount,
            java.util.List<String>               commandLineTestParameters,
            java.util.List<String>               configurationTestParameters )
   {
      if ( testUnitClassName == null )
      {
         throw new NullPointerException( "Internal error:  Class name for test unit cannot be null" );
      }

      if ( testSetName == null )
      {
         throw new NullPointerException( "Internal error:  Test name cannot be null" );
      }

      _completionCallbacks = new java.util.ArrayList<CompletionCallback>();
      _executionMode = ExecutionMode.PASS_1_DISCOVERY;
      _testParameters = new java.util.ArrayList<String>();
      _jvmParameters = new java.util.ArrayList<String>();

      _configuredIndex = configuredIndex;
      _testUnitClassName = testUnitClassName;
      _testSetName = testSetName;
      _testSuiteName = testSuiteName;
      _jvmCommand = jvmCommand;
      _threadCount = threadCount;
      _commandLineTestParameters = commandLineTestParameters;
      _configurationTestParameters = configurationTestParameters;
      _executorService = executorService;

      _testParameters.addAll( commandLineTestParameters );
      if ( configurationTestParameters != null )
      {
         _testParameters.addAll( configurationTestParameters );
      }

      if ( jvmParameters != null )
      {
         _jvmParameters.addAll( jvmParameters );
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

   }

   private TestSet( java.util.Map<String,String> environmentVariables,
                    String                       jvmCommand,
                    java.util.List<String>       jvmParameters,
                    ExecutionMode                executionMode,
                    java.io.File                 resultsDirectory,
                    String                       testSuiteName,
                    int                          configuredIndex,
                    String                       testSetName,
                    String                       testUnitClassName,
                    int                          threadCount,
                    int                          headingColumnWidth,
                    java.util.List<String>       commandLineTestParameters,
                    java.util.List<String>       configurationTestParameters )
      throws java.io.IOException,
             UserError,
             InternalException
   {
      int      elementCount;
      int      index;
      String   name;
      String   value;
      TestData configurationTestData;

      _completionCallbacks = new java.util.ArrayList<CompletionCallback>();

      _environmentVariables = environmentVariables;
      _jvmCommand = jvmCommand;
      _jvmParameters = jvmParameters;
      _executionMode = executionMode;
      _resultsDirectory = resultsDirectory;
      _testSuiteName = testSuiteName;
      _configuredIndex = configuredIndex;
      _testSetName = testSetName;
      _testUnitClassName = testUnitClassName;
      _threadCount = threadCount;
      _commandLineTestParameters = commandLineTestParameters;
      _configurationTestParameters = configurationTestParameters;
      if ( executionMode.verify() )
      {
         _statusFile = new StatusFile( getLocalResultsDirectory() );
      }
      _watchdogThread = new WatchdogThread( resultsDirectory,
                                            _testSetName );
      _testParameters = new java.util.ArrayList<String>();
      _testParameters.addAll( commandLineTestParameters );
      _testParameters.addAll( configurationTestParameters );

      _executorService = java.util.concurrent.Executors.newFixedThreadPool( threadCount );

      switch ( executionMode )
      {
         case PASS_1_DISCOVERY:
         {
            _rootTestManager = new TestManager( null,
                                                createAndGetTestUnit(),
                                                this,
                                                null,
                                                "" );
            break;
         }
         case PASS_2_VERIFICATION:
         {
            configurationTestData = readTestData( getTestConfigurationFile() );

            System.out.println( Utilities.padToRight(_testSetName + " ",
                                                     headingColumnWidth+10,
                                                     ".................................................................................................") );

            _rootTestManager = new TestManager( null,
                                                createAndGetTestUnit(),
                                                this,
                                                configurationTestData,
                                                "   " );
            break;
         }
         default:
         {
            throw new InternalException( "Unrecognized ExecutionMode:  " + executionMode );
         }
      }

      _rootTestManager.submitTest( headingColumnWidth + 3 );
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

   final java.util.List<String> getConfigurationTestParameters()
   {
      java.util.List<String> configurationTestParameters;

      configurationTestParameters = new java.util.ArrayList<String>();
      configurationTestParameters.addAll( _configurationTestParameters );

      return configurationTestParameters;
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

   final java.util.List<String> getJVMParameters()
   {
      java.util.List<String> jvmParameters;

      jvmParameters = new java.util.ArrayList<String>();
      jvmParameters.addAll( _jvmParameters );

      return jvmParameters;
   }

   final TestSetWindow getTestSetWindow( Undercamber            undercamber,
                                         java.util.List<String> commandLineTestParameters,
                                         javafx.stage.Window    ownerWindow )
   {
      if ( _testSetWindow == null )
      {
         _testSetWindow = new TestSetWindow( this,
                                             undercamber,
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
      _testData.appendToSequence( sequenceList );
   }

   final void initializePrerequisites( boolean useAlternateRunFlag )
      throws UserError
   {
      _testData.initializePrerequisites( useAlternateRunFlag );
   }

   final void addCompletionCallback( CompletionCallback completionCallback )
   {
      _completionCallbacks.add( completionCallback );
   }

   final void callCompletionListeners()
   {
      for ( CompletionCallback completionCallback : _completionCallbacks )
      {
         completionCallback.testComplete( _testData );
      }
   }

   final void savePass1ConfigurationData()
   {
      java.io.File testConfigurationFile;

      testConfigurationFile = getTestConfigurationFile();

      testConfigurationFile.getParentFile().mkdirs();

      writeTestData( testConfigurationFile,
                     _testData );
   }

   final java.util.Set<String> getTagNames()
   {
      java.util.Set<String> tagNames;

      tagNames = new java.util.HashSet<String>();

      _testData.addTagNamesToSet( tagNames );

      return tagNames;
   }

   final void readPass1TestData()
      throws java.io.IOException
   {
      java.io.File testDataFile;

      testDataFile = getTestConfigurationFile();

      _testData = readTestResults( getTestConfigurationFile() );
   }

   final TestData readTestData( java.io.File persistenceFile )
   {
      if ( persistenceFile.exists() )
      {
         try
         {
            return readTestResults( persistenceFile );
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

   final private TestData readTestResults( java.io.File persistenceFile )
      throws java.io.IOException
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
      return _testData.getRun( useAlternateRunFlag );
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
      boolean      failed;
      TestData     previousRunTestData;
      java.io.File testConfigurationFile;

      failed = true;

      try
      {
         if ( _executionMode.isDiscovery() )
         {
            _testData = _rootTestManager.getTestData();

            testConfigurationFile = getTestConfigurationFile();

            previousRunTestData = readTestData( testConfigurationFile );
            if ( previousRunTestData != null )
            {
               _testData.transferConfigurationFromPreviousRun( previousRunTestData );
            }

            testConfigurationFile.getParentFile().mkdirs();

            writeTestData( testConfigurationFile,
                           _testData );

            failed = writePass1StatusFile( true );

            shutdown();
         }
         else
         {
            _testData = _rootTestManager.getTestData();

            _testData.requirementsCallback();

            writeTestData( getBinaryResultsFile(),
                           _rootTestManager.getTestData() );

            callCompletionListeners();

            shutdown();
         }
      }
      finally
      {
         if ( failed )
         {
            shutdown();
         }
      }
   }

   final void failureCallback( Throwable throwable )
   {
      writePass1StatusFile( false );

      Utilities.printStackTrace( throwable );

      shutdown();
   }

   final private boolean writePass1StatusFile( boolean okay )
   {
      try
      {
         try ( java.io.FileOutputStream fileOutputStream = new java.io.FileOutputStream(getPass1StatusFile()) )
         {
            try ( java.io.DataOutputStream dataOutputStream = new java.io.DataOutputStream(fileOutputStream) )
            {
               dataOutputStream.writeBoolean( okay );
               return false;
            }
         }
      }
      catch ( java.io.IOException ioException )
      {
         ioException.printStackTrace();
         return true;
      }
   }

   final boolean readPass1StatusFile()
   {
      try
      {
         try ( java.io.FileInputStream fileInputStream = new java.io.FileInputStream(getPass1StatusFile()) )
         {
            try ( java.io.DataInputStream dataInputStream = new java.io.DataInputStream(fileInputStream) )
            {
               return dataInputStream.readBoolean();
            }
         }
      }
      catch ( java.io.IOException ioException )
      {
         ioException.printStackTrace();
         return false;
      }
   }

   final int computeHeadingColumnWidth( int maximumHeadingColumnWidth )
   {
      return _testData.getHeadingColumnWidth( maximumHeadingColumnWidth,
                                              "" );
   }

   final TestData getTestData()
   {
      return _testData;
   }

   final TestData getTestDataFromPersistence()
   {
      return readTestData( getBinaryResultsFile() );
   }

   final java.io.File getTestConfigurationFile()
   {
      java.io.File configurationFile;

      configurationFile = new java.io.File( System.getProperty("user.home") );
      configurationFile = new java.io.File( configurationFile, ".Undercamber" );
      configurationFile = new java.io.File( configurationFile, _testSuiteName );
      configurationFile = new java.io.File( configurationFile, "tests" );
      configurationFile = new java.io.File( configurationFile, _testSetName+".dat" );

      return configurationFile;
   }

   final java.io.File getPass1StatusFile()
   {
      java.io.File pass1StatusFile;

      pass1StatusFile = new java.io.File( getLocalResultsDirectory(), "UndercamberWorkingDirectory" );
      pass1StatusFile = new java.io.File( pass1StatusFile, "executive" );
      pass1StatusFile.mkdirs();
      pass1StatusFile = new java.io.File( pass1StatusFile, _testSetName+".status" );

      return pass1StatusFile;
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
      printStream.println( margin + "   <pass2ThreadCount>" + undercamber.getPass2ThreadCount(_configuredIndex) + "</pass2ThreadCount>" );
      printStream.println( margin + "   <jvmCommand>" + _jvmCommand + "</jvmCommand>" );

      printStream.println( margin + "   <javaParameters>" );
      for ( String javaParameter : _jvmParameters )
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

      _testData.writeXMLReport( margin + "   ",
                                printStream );

      printStream.println( margin + "</testSet>" );
   }

   final private static void showUsageMessage()
   {
      System.out.println(   "Arguments:  ( 0) <environment variable count>" );
      System.out.println(   "            ( 1) <JVM command>" );
      System.out.println(   "            ( 2) <JVM parameter count>" );
      System.out.println(   "            ( 3) <execution mode>" );
      System.out.println(   "            ( 4) <results directory>" );
      System.out.println(   "            ( 5) <heading column width>" );
      System.out.println(   "            ( 6) <test suite name>" );
      System.out.println(   "            ( 7) <configured index>" );
      System.out.println(   "            ( 8) <test set name>" );
      System.out.println(   "            ( 9) <test unit class name>" );
      System.out.println(   "            (10) <command line test parameter count>" );
      System.out.println(   "            (11) <thread count>" );
      System.out.println(   "                 {<environment variable name, environment variable value>...}" );
      System.out.println(   "                 {<JVM parameters>...}" );
      System.out.println(   "                 {<command line test parameter>...}" );
      System.out.println(   "                 {<configuration test parameter>...}" );
   }

   final public static void main( String arguments[] )
   {
      int                          environmentVariableCount;
      java.util.Map<String,String> environmentVariables;
      String                       jvmCommand;
      int                          jvmParameterCount;
      ExecutionMode                executionMode;
      java.io.File                 resultsDirectory;
      int                          headingColumnWidth;
      String                       testSuiteName;
      int                          configuredIndex;
      String                       testSetName;
      String                       testUnitClassName;
      int                          commandLineTestParameterCount;
      int                          threadCount;
      int                          argumentIndex;
      int                          index;
      java.util.List<String>       jvmParameters;
      java.util.List<String>       commandLineTestParameters;
      java.util.List<String>       configurationTestParameters;
      TestSet                      testSet;
      boolean                      cleanup;

      if ( arguments.length < 11 )
      {
         showUsageMessage();
      }
      else
      {
         try
         {
            environmentVariableCount = Integer.parseInt( arguments[0] );

            if ( environmentVariableCount == -1 )
            {
               environmentVariables = null;
            }
            else
            {
               environmentVariables = new java.util.HashMap<String,String>();
            }

            jvmCommand = arguments[ 1 ];

            jvmParameterCount = Integer.parseInt( arguments[2] );

            executionMode = ExecutionMode.values()[ Integer.parseInt(arguments[3]) ];

            resultsDirectory = new java.io.File( arguments[4] );

            headingColumnWidth = Integer.parseInt( arguments[5] );

            testSuiteName = arguments[ 6 ];

            configuredIndex = Integer.parseInt( arguments[7] );

            testSetName = arguments[ 8 ];

            testUnitClassName = arguments[ 9 ];

            commandLineTestParameterCount = Integer.parseInt( arguments[10] );

            threadCount = Integer.parseInt( arguments[11] );

            argumentIndex = 11;

            if ( environmentVariables != null )
            {
               for ( index=0; index<environmentVariableCount; index++ )
               {
                  environmentVariables.put( arguments[argumentIndex], arguments[argumentIndex+1] );
                  argumentIndex += 2;
               }
            }

            jvmParameters = new java.util.ArrayList<String>();
            for ( index=0; index<jvmParameterCount; index++ )
            {
               jvmParameters.add( arguments[argumentIndex] );
               argumentIndex++;
            }

            commandLineTestParameters = new java.util.ArrayList<String>();
            for ( index=0; index<commandLineTestParameterCount; index++ )
            {
               commandLineTestParameters.add( arguments[argumentIndex] );
               argumentIndex++;
            }

            configurationTestParameters = new java.util.ArrayList<String>();
            for ( index=argumentIndex; index<arguments.length; index++ )
            {
               configurationTestParameters.add( arguments[index] );
            }

            testSet = null;
            cleanup = false;

            try
            {
               testSet = new TestSet( environmentVariables,
                                      jvmCommand,
                                      jvmParameters,
                                      executionMode,
                                      resultsDirectory,
                                      testSuiteName,
                                      configuredIndex,
                                      testSetName,
                                      testUnitClassName,
                                      threadCount,
                                      headingColumnWidth,
                                      commandLineTestParameters,
                                      configurationTestParameters );
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

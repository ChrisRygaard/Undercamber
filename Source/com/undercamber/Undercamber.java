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
 * Main class
 */
final public class Undercamber
   extends javafx.application.Application
{
   final static String                            DEADLOCK_FILE_NAME             = "UndercamberMain";
   final static int                               PERSISTENCE_VERSION            = 0;
   final static String                            PERSISTENCE_BRANCH             = "";
   final static javafx.scene.input.KeyCombination CONTROL_A_KEYBOARD_COMBINATION = new javafx.scene.input.KeyCodeCombination( javafx.scene.input.KeyCode.A,
                                                                                                                              javafx.scene.input.KeyCombination.ModifierValue.UP,
                                                                                                                              javafx.scene.input.KeyCombination.ModifierValue.DOWN,
                                                                                                                              javafx.scene.input.KeyCombination.ModifierValue.UP,
                                                                                                                              javafx.scene.input.KeyCombination.ModifierValue.UP,
                                                                                                                              javafx.scene.input.KeyCombination.ModifierValue.UP );

   private javafx.stage.Stage                   _primaryStage;
   private SequenceList                         _sequenceList;
   private Configurator                         _configurator;
   private ArgumentParser                       _argumentParser;
   private String                               _resultsSubdirectoryName;
   private StatusFile                           _statusFile;
   private TestData                             _dummyRoot;
   private java.util.List<TestSetDescriptor>    _testSetDescriptors;
   private java.util.List<TestSet>              _pass1TestSets;
   private int                                  _pass1ThreadCount;
   private WatchdogThread                       _watchdogThread;
   private long                                 _discoveryStartTime;
   private int                                  _pass1CallbackCounter;
   private Process                              _testProcess;
   private java.util.concurrent.ExecutorService _executorService;
   private SelectionWindow                      _selectionWindow;
   private Integer                              _environmentVariableThreadCount;

   /**
    * JavaFX entry point
    */
   final public void start( javafx.stage.Stage primaryStage )
   {
      java.io.File          resultsDirectory;
      ConfigurationCallback configurationCallback;
      int                   testSetIndex;

      _pass1CallbackCounter = 0;
      _primaryStage = primaryStage;

      javafx.application.Platform.setImplicitExit( false );

      try
      {
         _environmentVariableThreadCount = getEnvironmentVariableThreadCount();
      }
      catch ( UserError userError )
      {
         Utilities.printStackTrace( userError );
         shutdown();
         return;
      }

      _argumentParser = new ArgumentParser( getParameters().getRaw() );
      if ( !(_argumentParser.success()) )
      {
         shutdown();
         return;
      }

      try
      {
         configurationCallback = getConfigurationCallback( _argumentParser );
      }
      catch ( Exception exception )
      {
         Utilities.printStackTrace( exception );
         shutdown();
         return;
      }

      try
      {
         _configurator = new Configurator( this,
                                           configurationCallback,
                                           _argumentParser );

         configurationCallback.configure( _configurator );

         _configurator.close();

         _pass1ThreadCount = getThreadCount( null );

         _executorService = java.util.concurrent.Executors.newFixedThreadPool( _pass1ThreadCount );

         _testSetDescriptors = new java.util.ArrayList<TestSetDescriptor>();
         _testSetDescriptors.addAll( _configurator.getTestSetDescriptors() );

         testSetIndex = 0;
         _pass1TestSets = new java.util.ArrayList<TestSet>();
         for ( TestSetDescriptor testSetDescriptor : _testSetDescriptors )
         {
            _pass1TestSets.add( testSetDescriptor.getTestSet(_argumentParser.getTestParameters(),
                                                             testSetIndex,
                                                             _pass1ThreadCount,
                                                             _executorService) );
            testSetIndex++;
         }

         _resultsSubdirectoryName = _argumentParser.getResultsSubdirectoryName();

         resultsDirectory = getResultsDirectory();

         _watchdogThread = new WatchdogThread( resultsDirectory,
                                               DEADLOCK_FILE_NAME );

         ( new java.io.File(resultsDirectory,"work") ).mkdirs();

         for ( TestSet testSet : _pass1TestSets )
         {
            testSet.initialize( resultsDirectory );
         }

         runPass1();
      }
      catch ( Throwable throwable )
      {
         Utilities.printStackTrace( throwable );
         shutdown();
      }
   }

   final private ConfigurationCallback getConfigurationCallback( ArgumentParser argumentParser )
      throws UserError,
             InstantiationException,
             IllegalAccessException,
             java.lang.reflect.InvocationTargetException
   {
      String   className;
      Class<?> type;
      Object   configurationCallbackObject;

      className = argumentParser.getConfiguratorClassName();

      if ( className == null )
      {
         throw new UserError( "ConfigurationCallback class name not specified on command line" );
      }

      try
      {
         type = Class.forName( className );
      }
      catch ( ClassNotFoundException classNotFoundException )
      {
         showClassPath();
         Utilities.printStackTrace( classNotFoundException );
         throw new UserError( "Class \"" + className + "\" not found.  Specified on command line." );
      }

      try
      {
         configurationCallbackObject = type.getDeclaredConstructor().newInstance();
      }
      catch ( NoSuchMethodException noSuchMethodException )
      {
       throw new UserError( "Could not find no-argument constructor for class \"" + className + "\".  Specified on command line" );
      }

      if ( configurationCallbackObject instanceof ConfigurationCallback )
      {
         return (ConfigurationCallback)configurationCallbackObject;
      }
      else
      {
         throw new UserError( "Class " + className + " does not implement com.undercamber.ConfigurationCallback.  Specified on command line" );
      }
   }

   final private void showClassPath()
   {
      String                    classPath;
      java.util.StringTokenizer stringTokenizer;

      classPath = System.getProperty( "java.class.path" );

      stringTokenizer = new java.util.StringTokenizer( classPath,
                                                       java.io.File.pathSeparator );

      System.out.println( "Class path:" );
      while ( stringTokenizer.hasMoreTokens() )
      {
         System.out.println( "   " + stringTokenizer.nextToken() );
      }
   }

   final java.io.File getResultsDirectory()
      throws InternalException,
             UserError
   {
      return new java.io.File( getResultsRootDirectory(), _resultsSubdirectoryName );
   }

   final private java.io.File getResultsRootDirectory()
      throws UserError,
             InternalException
   {
      java.io.File resultsRootDirectory;

      resultsRootDirectory = new java.io.File( getResultsRootDirectoryString() );

      resultsRootDirectory.mkdirs();

      if ( !(resultsRootDirectory.isDirectory()) )
      {
         throw new InternalException( "Internal error:  Could not create results root directory <" + resultsRootDirectory.getPath() + ">" );
      }

      return resultsRootDirectory;
   }

   final private String getResultsRootDirectoryString()
      throws UserError
   {
      String resultsRootDirectoryName;

      resultsRootDirectoryName = _argumentParser.getResultsRootDirectoryName();
      if ( resultsRootDirectoryName != null )
      {
         return resultsRootDirectoryName;
      }

      resultsRootDirectoryName = _configurator.getResultsRootDirectoryName();
      if ( resultsRootDirectoryName != null )
      {
         return resultsRootDirectoryName;
      }

      resultsRootDirectoryName = System.getenv( "UNDERCAMBER_ROOT" );
      if ( resultsRootDirectoryName != null )
      {
         return resultsRootDirectoryName;
      }

      throw new UserError( "Error:  Results root directory not specified" );
   }

   final Integer getEnvironmentVariableThreadCount()
      throws UserError
   {
      String text;
      int    threadCount;

      text = System.getenv( "UNDERCAMBER_THREAD_COUNT" );

      if ( text == null )
      {
         return null;
      }
      else
      {
         try
         {
            threadCount = Integer.parseInt( text );

            if ( threadCount < 1 )
            {
               throw new UserError( "Error:  Invalid thread count in environment variable \"UNDERCAMBER_THREAD_COUNT\":  " + threadCount + ".  Must be at least 1" );
            }

            return threadCount;
         }
         catch ( NumberFormatException numberFormatException )
         {
            throw new UserError( "Error:  Invalid thread count in environment variable \"UNDERCAMBER_THREAD_COUNT\":  " + text );
         }
      }
   }

   final int getPass1ThreadCount()
   {
      return _pass1ThreadCount;
   }

   final int getThreadCount( Integer configuredIndex )
   {
      Integer threadCount;

      threadCount = _argumentParser.getThreadCount();
      if ( threadCount != null )
      {
         return threadCount;
      }

      if ( configuredIndex != null )
      {
         threadCount = _testSetDescriptors.get( configuredIndex ).getConfigurationThreadCount();
         if ( threadCount != null )
         {
            return threadCount;
         }
      }

      threadCount = _configurator.getPass1ThreadCount();
      if ( threadCount != null )
      {
         return threadCount;
      }

      if ( _environmentVariableThreadCount != null )
      {
         return _environmentVariableThreadCount;
      }

      return Runtime.getRuntime().availableProcessors();
   }

   final private boolean showResultsGUI()
      throws UserError
   {
      Boolean showResultsWindow;
      String  environmentText;

      showResultsWindow = _argumentParser.showResultsWindow();
      if ( showResultsWindow != null )
      {
         return showResultsWindow;
      }

      showResultsWindow = _configurator.getShowResultsWindow();
      if ( showResultsWindow != null )
      {
         return showResultsWindow;
      }

      environmentText = System.getenv( "UNDERCAMBER_SHOW_RESULTS_WINDOW" );
      if ( environmentText != null )
      {
         if ( environmentText.equals("true") )
         {
            return true;
         }
         else if ( environmentText.equals("false") )
         {
            return false;
         }
         else
         {
            throw new UserError( "Error:  Invalid value for environment variable UNDERCAMBER_SHOW_RESULTS_WINDOW:  <" + environmentText + ">.  Should be either \"true\" or \"false\"." );
         }
      }

      return true;
   }

   final private void runPass1()
   {
      System.out.println( "Discovering test structures..." );

      _discoveryStartTime = System.currentTimeMillis();

      try
      {
         for ( TestSet testSet : _pass1TestSets )
         {
            testSet.runPass1();
         }
      }
      catch ( Throwable throwable )
      {
         Utilities.printStackTrace( throwable );
         shutdown();
      }
   }

   final void failureCallback( Throwable throwable )
   {
      Utilities.printStackTrace( throwable );
      shutdown();
   }

   final void pass1Callback()
   {
      boolean proceed;
      boolean showGUI;

      synchronized ( TestManager.MONITOR_HOLDER )
      {
         _pass1CallbackCounter++;

         proceed = _pass1CallbackCounter >= _pass1TestSets.size();
      }

      if ( proceed )
      {
         showGUI = _argumentParser.showGUI();

         _dummyRoot = new TestData( _pass1TestSets );

         _sequenceList = new SequenceList();

         _dummyRoot.appendToSequence( _sequenceList );

         try
         {
            _sequenceList.initializeDependencies();

            for ( TestSet testSet : _pass1TestSets )
            {
               testSet.initializePrerequisites( !showGUI );
            }
         }
         catch ( Throwable throwable )
         {
            Utilities.printStackTrace( throwable );
            shutdown();
            return;
         }

         _dummyRoot.setRunOnBranch( false,
                                    true );

         System.out.println( "Finished discovering test structure for " + (_sequenceList.size()-1) + " tests in " + (System.currentTimeMillis()-_discoveryStartTime) + " ms." );

         if ( showGUI )
         {
            javafx.application.Platform.runLater( () -> selectionWindowThread() );
         }
         else
         {
            runPass2FromCommandLine();
         }
      }
   }

   final private void selectionWindowThread()
   {
      _selectionWindow = new SelectionWindow( _configurator.getSuiteName(),
                                              _primaryStage,
                                              _pass1TestSets,
                                              _dummyRoot,
                                              _argumentParser.getTestParameters(),
                                              this );
   }

   final private void runPass2FromCommandLine()
   {
      java.util.Set<String>          badTestSetNames;
      java.util.Set<TagEntryPoint>   badTagEntryPoints;
      java.util.List<TestEntryPoint> badTestEntryPoints;
      boolean                        foundEntryPoint;
      java.util.Set<TestData>        entryPoints;
      TestSet                        testSet;
      boolean                        foundTag;
      boolean                        found;
      boolean                        okay;

      if ( _argumentParser.useGUIFlags() )
      {
         runGUISelectedTests();
      }
      else
      {
         try
         {
            if ( _argumentParser.runAllTests() )
            {
               _dummyRoot.setRunOnBranch( true,
                                          true );
            }

            badTestSetNames = new java.util.HashSet<String>();
            badTagEntryPoints = new java.util.HashSet<TagEntryPoint>();
            badTestEntryPoints = new java.util.ArrayList<TestEntryPoint>();

            for ( TestEntryPoint testEntryPoint : _argumentParser.getEntryPoints() )
            {
               foundEntryPoint = false;

               if ( testEntryPoint.getTestSetNames() == null )
               {
                  entryPoints = _dummyRoot.findEntryPoints( testEntryPoint );
                  for ( TestData entryPoint : entryPoints )
                  {
                     foundEntryPoint = true;
                     entryPoint.setRunOnBranch( true,
                                                true );
                  }
               }
               else
               {
                  for ( String testSetName : testEntryPoint.getTestSetNames() )
                  {
                     testSet = getPass1TestSet( testSetName );
                     if ( testSet == null )
                     {
                        badTestSetNames.add( testSetName );
                     }
                     else
                     {
                        entryPoints = testSet.getPass1TestData().findEntryPoints( testEntryPoint );
                        for ( TestData entryPoint : entryPoints )
                        {
                           foundEntryPoint = true;
                           entryPoint.setRunOnBranch( true,
                                                      true );
                        }
                     }
                  }
               }

               if ( !foundEntryPoint )
               {
                  badTestEntryPoints.add( testEntryPoint );
               }
            }

            for ( TagEntryPoint tagEntryPoint : _argumentParser.getTagEntryPoints() )
            {
               for ( String tagName : tagEntryPoint.getTagNames() )
               {
                  if ( tagEntryPoint.getTestSetNames() == null )
                  {
                     foundTag = _dummyRoot.setAlternateRunFlagOnBranch( tagName );
                  }
                  else
                  {
                     foundTag = false;

                     for ( String testSetName : tagEntryPoint.getTestSetNames() )
                     {
                        testSet = getPass1TestSet( testSetName );
                        if ( testSet == null )
                        {
                           badTestSetNames.add( testSetName );
                        }
                        else
                        {
                           found = testSet.getPass1TestData().setAlternateRunFlagOnBranch( tagName );
                           if ( found )
                           {
                              foundTag = true;
                           }
                        }
                     }
                  }

                  if ( !foundTag )
                  {
                     badTagEntryPoints.add( tagEntryPoint );
                  }
               }
            }

            for ( String testSetName : _argumentParser.getTestSetNames() )
            {
               testSet = getPass1TestSet( testSetName );

               if ( testSet == null )
               {
                  badTestSetNames.add( testSetName );
               }
               else
               {
                  testSet.getPass1TestData().setRunOnBranch( true,
                                                             true );
               }
            }

            okay = check( badTestEntryPoints,
                          badTestSetNames,
                          badTagEntryPoints );

            if ( okay )
            {
               runPass2();
            }
            else
            {
               shutdown();
            }
         }
         catch ( Throwable throwable )
         {
            Utilities.printStackTrace( throwable );
            shutdown();
         }
      }
   }

   final boolean check( java.util.List<TestEntryPoint> badTestEntryPoints,
                        java.util.Set<String>          badTestSetNames,
                        java.util.Set<TagEntryPoint>   badTagEntryPoints )
   {
      boolean                okay;
      java.util.List<String> stringList;

      okay = true;

      if ( badTestEntryPoints.size() > 0 )
      {
         okay = false;

         if ( badTestEntryPoints.size() == 1 )
         {
            System.out.println( "Test " + badTestEntryPoints.get(0) + " (specified on command line) not found" );
         }
         else
         {
            stringList = new java.util.ArrayList<String>();

            for ( TestEntryPoint testEntryPoint : badTestEntryPoints )
            {
               stringList.add( testEntryPoint.toString() );
            }

            System.out.println( "Tests " + Utilities.formatList(stringList) + " (specified on command line) not found" );
         }
      }

      if ( badTestSetNames.size() > 0 )
      {
         okay = false;

         stringList = new java.util.ArrayList<String>();
         stringList.addAll( badTestSetNames );
         java.util.Collections.sort( stringList );

         if ( stringList.size() == 1 )
         {
            System.out.println( "Test set " + stringList.get(0) + " (specified on command line) not found" );
         }
         else
         {
            System.out.println( "Test sets " + Utilities.formatList(stringList) + " (specified on command line) not found" );
         }
      }

      if ( badTagEntryPoints.size() > 0 )
      {
         okay = false;

         for ( TagEntryPoint tagEntryPoint : badTagEntryPoints )
         {
            tagEntryPoint.showErrorMessage( this );
         }
      }

      return okay;
   }

   final void runTag( String tagName )
   {
      _dummyRoot.setAlternateRunFlagOnBranch( tagName );

      runPass2();
   }

   final void runAllTests()
   {
      _dummyRoot.setRunOnBranch( true,
                                 true );

      runPass2();
   }

   final void runGUISelectedTests()
   {
      _dummyRoot.transferGUIRunFlags();

      runPass2();
   }

   final void runTest( TestData testData,
                       boolean  runSubtests )
   {
      if ( runSubtests )
      {
         testData.setRunOnBranch( true,
                                  true );
      }
      else
      {
         testData.setRun( true,
                          true );
      }

      runPass2();
   }

   final private void runPass2()
   {
      int headingColumnWidth;

      javafx.application.Platform.runLater( () -> _primaryStage.hide() );

      savePass1Configurations();

      headingColumnWidth = computeHeadingColumnWidth();

      try
      {
         _statusFile = new StatusFile( getResultsDirectory(),
                                       _sequenceList.size() );
         for ( TestSet testSet : _pass1TestSets )
         {
            testSet.setStatusFile( _statusFile );
         }

         if ( _selectionWindow != null )
         {
            javafx.application.Platform.runLater( () -> _selectionWindow.saveAndClose(null) );
         }

         ( new Thread(()->pass2Thread(headingColumnWidth)) ).start();
      }
      catch ( Exception exception )
      {
         Utilities.printStackTrace( exception );
         shutdown();
      }
   }

   final private void pass2Thread( int headingColumnWidth )
   {
      java.io.File                 executiveFile;
      boolean                      skippedResultsScreen;
      Timer                        timer;
      java.util.List<String>       processArguments;
      boolean                      runThisProcess;
      int                          configuredIndex;
      ProcessBuilder               processBuilder;
      java.util.Map<String,String> userEnvironmentVariables;
      java.util.Map<String,String> processEnvironmentVariables;
      String                       elapsedTimeString;

      Runtime.getRuntime().addShutdownHook( new Thread(()->shutdownHook()) );

      skippedResultsScreen = true;
      try
      {
         timer = new Timer();

         processArguments = new java.util.ArrayList<String>();

         try
         {
            _dummyRoot.setStateOnBranch( TestState.NOT_RUN );

            for ( TestSet testSet : _pass1TestSets )
            {
               if ( testSet.shouldRun(true) )
               {
                  runThisProcess = true;
               }
               else
               {
                  runThisProcess = false;
               }

               if ( runThisProcess )
               {
                  executiveFile = testSet.writeExecutiveFile();

                  configuredIndex = testSet.getConfiguredIndex();

                  processArguments.clear();
                  processArguments.add( testSet.getJVMCommand() );
                  processArguments.addAll( testSet.getJavaParameters() );
                  processArguments.add( TestSet.class.getName() );
                  processArguments.add( executiveFile.getPath() );                                 // 0
                  processArguments.add( getResultsDirectory().getPath() );                         // 1
                  processArguments.add( Integer.toString(ExecutionMode.PASS_2_EXECUTION.ordinal()) );               // 2
                  processArguments.add( Integer.toString(headingColumnWidth) );                    // 3
                  processArguments.add( Integer.toString(getThreadCount(configuredIndex)) );       // 4
                  processArguments.addAll( _argumentParser.getTestParameters() );                  // 5...

                  processBuilder = new ProcessBuilder( processArguments );
                  processBuilder.inheritIO();

                  userEnvironmentVariables = testSet.getEnvironmentVariables();
                  if ( userEnvironmentVariables != null )
                  {
                     processEnvironmentVariables = processBuilder.environment();
                     processEnvironmentVariables.clear();
                     for ( String name : userEnvironmentVariables.keySet() )
                     {
                        processEnvironmentVariables.put( name, userEnvironmentVariables.get(name) );
                     }
                  }

                  try
                  {
                     _testProcess = processBuilder.start();

                     _testProcess.waitFor( 36524,
                                           java.util.concurrent.TimeUnit.DAYS );
                  }
                  catch ( Throwable throwable )
                  {
                     Utilities.printStackTrace( throwable );
                  }
               }
            }
         }
         catch ( Throwable throwable )
         {
            Utilities.printStackTrace( throwable );
         }

         elapsedTimeString = timer.getElapsedTimeString();

         skippedResultsScreen = !showResultsGUI();

         new Finisher( _configurator.getSuiteName(),
                       _pass1ThreadCount,
                       _pass1TestSets,
                       getResultsDirectory(),
                       headingColumnWidth,
                       _sequenceList.getTestDataMap(),
                       elapsedTimeString,
                       showResultsGUI(),
                       _argumentParser.getTestParameters(),
                       _primaryStage,
                       this );
      }
      catch ( Throwable throwable )
      {
         Utilities.printStackTrace( throwable );
      }
      finally
      {
         if ( skippedResultsScreen )
         {
            shutdown();
         }
      }
   }

   final private void listTags()
   {
      for ( String tag : getAllTags() )
      {
         System.out.println( "   - " + tag );
      }
   }

   final private java.util.Map<String,java.util.Set<TestData>> getTagMap()
   {
      java.util.Map<String,java.util.Set<TestData>> tagMap;

      tagMap = new java.util.HashMap<String,java.util.Set<TestData>>();

      _dummyRoot.addToTagMap( tagMap );

      return tagMap;
   }

   final java.util.List<String> getAllTags()
   {
      java.util.Map<String,java.util.Set<TestData>> tagMap;
      java.util.List<String>                        tagNames;

      tagMap = getTagMap();

      tagNames = new java.util.ArrayList<String>();

      tagNames.addAll( tagMap.keySet() );

      java.util.Collections.sort( tagNames );

      return tagNames;
   }

   final TestSet getPass1TestSet( String name )
   {
      for ( TestSet testSet : _pass1TestSets )
      {
         if ( testSet.getTestSetName().equals(name) )
         {
            return testSet;
         }
      }

      return null;
   }

   final private int computeHeadingColumnWidth()
   {
      int headingColumnWidth;

      headingColumnWidth = 0;

      for ( TestSet testSet : _pass1TestSets )
      {
         headingColumnWidth = testSet.computeHeadingColumnWidth( headingColumnWidth );
      }

      return headingColumnWidth;
   }

   final private void shutdownHook()
   {
      if ( _testProcess != null )
      {
         try
         {
            _testProcess.destroyForcibly();
         }
         catch ( Throwable throwable )
         {
            throwable.printStackTrace( System.out );
         }
      }
   }

   final void saveAndClose( javafx.event.Event event )
   {
      savePass1Configurations();

      shutdown();

      if ( event != null )
      {
         event.consume();
      }
   }

   final private void savePass1Configurations()
   {
      for ( TestSet testSet : _pass1TestSets )
      {
         testSet.savePass1ConfigurationData();
      }
   }

   final private void shutdown()
   {
      if ( _watchdogThread != null )
      {
         _watchdogThread.stop();
      }

      if ( _executorService != null )
      {
         _executorService.shutdownNow();
      }

      if ( _pass1TestSets != null )
      {
         for ( TestSet testSet : _pass1TestSets )
         {
            testSet.shutdown();
         }
      }

      javafx.application.Platform.exit();
   }

   /**
    * Entry point.
    *
    * @param arguments
    *        Command-line arguments<p>
    *
    *        Usage:  <tt>Undercamber [flag parameters]...</tt><br>
    *        <br>
    *        <table summary     = "Command line arguments"
    *               border      = "1"
    *               style       = "border-collapse:collapse"
    *               cellpadding = "5" >
    *           <tr>
    *              <th>Flag</th>
    *              <th>Parameter1</th>
    *              <th>Parameter2</th>
    *              <th>Parameter3</th>
    *              <th>Parameter4</th>
    *              <th>Description</th>
    *           </tr>
    *           <tr>
    *              <td>-a</td>
    *              <td></td>
    *              <td></td>
    *              <td></td>
    *              <td></td>
    *              <td>automatically run all tests</td>
    *           </tr>
    *           <tr>
    *              <td>-tag1</td>
    *              <td><i>tagNames</i></td>
    *              <td></td>
    *              <td></td>
    *              <td></td>
    *              <td>automatically run tests with specified tags</td>
    *           </tr>
    *           <tr>
    *              <td>-tag2</td>
    *              <td><i>tagNames</i></td>
    *              <td><i>testSetNames</i></td>
    *              <td></td>
    *              <td></td>
    *              <td>automatically run tests with specified tag.  Limit search for tags to specified testSets.</td>
    *           </tr>
    *           <tr>
    *              <td>-test1</td>
    *              <td><i>className</i></td>
    *              <td><i>methodName</i></td>
    *              <td></td>
    *              <td></td>
    *              <td>automatically run specified test and subtests.  No arguments.</td>
    *           </tr>
    *           <tr>
    *              <td>-test2</td>
    *              <td><i>className</i></td>
    *              <td><i>methodName</i></td>
    *              <td><i>arguments</i></td>
    *              <td></td>
    *              <td>automatically run specified test and subtests, with arguments.</td>
    *           </tr>
    *           <tr>
    *              <td>-test3</td>
    *              <td><i>testSetList</i></td>
    *              <td><i>className</i></td>
    *              <td><i>methodName</i></td>
    *              <td></td>
    *              <td>automatically run specified test and subtests.  Limit search for test to specified testSets.  No arguments.</td>
    *           </tr>
    *           <tr>
    *              <td>-test4</td>
    *              <td><i>testSetList</i></td>
    *              <td><i>className</i></td>
    *              <td><i>methodName</i></td>
    *              <td><i>arguments</i></td>
    *              <td>automatically run specified test and subtests.  Limit search for test to specified testSets.  With arguments.</td>
    *           </tr>
    *           <tr>
    *              <td>-rootDirectory</td>
    *              <td><i>directoryName</i></td>
    *              <td></td>
    *              <td></td>
    *              <td></td>
    *              <td>output root directory name</td>
    *           </tr>
    *           <tr>
    *              <td>-subdirectory</td>
    *              <td><i>name</i></td>
    *              <td></td>
    *              <td></td>
    *              <td></td>
    *              <td>output subdirectory name,  Default built from current time and date)</td>
    *           </tr>
    *           <tr>
    *              <td>-config</td>
    *              <td><i>className</i></td>
    *              <td></td>
    *              <td></td>
    *              <td></td>
    *              <td>name of configuration class</td>
    *           </tr>
    *           <tr>
    *              <td>-threadCount</td>
    *              <td><i>threadCount</i></td>
    *              <td></td>
    *              <td></td>
    *              <td></td>
    *              <td>concurrent thread count</td>
    *           </tr>
    *           <tr>
    *              <td>-p</td>
    *              <td><i>parameter</i></td>
    *              <td></td>
    *              <td></td>
    *              <td></td>
    *              <td>parameter passed to test processes</td>
    *           </tr>
    *           <tr>
    *              <td>-pp</td>
    *              <td><i>parameter1</i></td>
    *              <td><i>parameter2</i></td>
    *              <td></td>
    *              <td></td>
    *              <td>parameter pair passed to test processes</td>
    *           </tr>
    *           <tr>
    *              <td>-g</td>
    *              <td></td>
    *              <td></td>
    *              <td></td>
    *              <td></td>
    *              <td>automatically run tests selected in the GUI (from previous run)</td>
    *           </tr>
    *           <tr>
    *              <td>-set</td>
    *              <td><i>testSetName</i></td>
    *              <td></td>
    *              <td></td>
    *              <td></td>
    *              <td>automatically run all tests in the specified test set</td>
    *           </tr>
    *           <tr>
    *              <td>-resultWindow</td>
    *              <td><tt>true</tt> or <tt>false</tt></td>
    *              <td></td>
    *              <td></td>
    *              <td></td>
    *              <td>show or hide the results screen</td>
    *           </tr>
    *        </table>
    */
   final public static void main( String arguments[] )
   {
      launch( arguments );
   }
}

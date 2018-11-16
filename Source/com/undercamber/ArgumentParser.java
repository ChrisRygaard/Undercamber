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

final class ArgumentParser
{
   private boolean                        _success;
   private State                          _state;
   private boolean                        _runAllTests;
   private String                         _resultsRootDirectoryName;
   private String                         _resultsSubdirectoryName;
   private String                         _configuratorClassName;
   private Integer                        _threadCount;
   private java.util.List<String>         _testParameters;
   private java.util.List<TestEntryPoint> _entryPoints;
   private java.util.List<TagEntryPoint>  _tagEntryPoints;
   private java.util.List<String>         _testSetNames;
   private boolean                        _useGUIFlags;
   private Boolean                        _showResultsWindow;

   ArgumentParser( java.util.List<String> arguments )
   {
      String               tagTestSets;
      String               tags;
      String               entryPointTestSets;
      String               entryPointClassName;
      String               entryPointMethodName;
      String               entryPointArguments;
      java.text.DateFormat dateFormatter;

      tagTestSets = null;
      entryPointTestSets = null;
      entryPointClassName = null;
      entryPointMethodName = null;
      entryPointArguments = null;

      _runAllTests = false;
      _useGUIFlags = false;
      _resultsRootDirectoryName = null;
      _resultsSubdirectoryName = null;
      _configuratorClassName = null;
      _state = State.BETWEEN_PARAMETERS;
      _testParameters = new java.util.ArrayList<String>();
      _entryPoints = new java.util.ArrayList<TestEntryPoint>();
      _tagEntryPoints = new java.util.ArrayList<TagEntryPoint>();
      _testSetNames = new java.util.ArrayList<String>();
      _success = true;
      _showResultsWindow = null;

      for ( String argument : arguments )
      {
         switch ( _state )
         {
            case BETWEEN_PARAMETERS:
            {
               _state = getNextState( argument );
               if ( _state == null )
               {
                  return;
               }
               break;
            }
            case PARSING_OUTPUT_DIRECTORY:
            {
               _resultsRootDirectoryName = argument;
               _state = State.BETWEEN_PARAMETERS;
               break;
            }
            case PARSING_OUTPUT_SUBDIRECTORY_NAME:
            {
               _resultsSubdirectoryName = argument;
               _state = State.BETWEEN_PARAMETERS;
               break;
            }
            case PARSING_CONFIGURATOR_CLASS_NAME:
            {
               _configuratorClassName = argument;
               _state = State.BETWEEN_PARAMETERS;
               break;
            }
            case PARSING_THREAD_COUNT:
            {
               _threadCount = parseThreadCount( argument );
               if ( _threadCount == null )
               {
                  return;
               }
               _state = State.BETWEEN_PARAMETERS;
               break;
            }
            case PARSING_TEST_PARAMETER:
            {
               _testParameters.add( argument );
               _state = State.BETWEEN_PARAMETERS;
               break;
            }
            case PARSING_TEST_PARAMETER_PAIR_1:
            {
               _testParameters.add( argument );
               _state = State.PARSING_TEST_PARAMETER_PAIR_2;
               break;
            }
            case PARSING_TEST_PARAMETER_PAIR_2:
            {
               _testParameters.add( argument );
               _state = State.BETWEEN_PARAMETERS;
               break;
            }
            case PARSING_TAG_1:
            {
               tags = argument;
               _tagEntryPoints.add( new TagEntryPoint(null,
                                                      tags));
               _state = State.BETWEEN_PARAMETERS;
               break;
            }
            case PARSING_TAG_2_SETS:
            {
               tagTestSets = argument;
               _state = State.PARSING_TAG_2_NAMES;
               break;
            }
            case PARSING_TAG_2_NAMES:
            {
               tags = argument;
               _tagEntryPoints.add( new TagEntryPoint(tagTestSets,
                                                      tags) );
               _state = State.BETWEEN_PARAMETERS;
               break;
            }
            case PARSING_TEST_SET:
            {
               _testSetNames.add( argument );
               _state = State.BETWEEN_PARAMETERS;
               break;
            }
            case PARSING_TEST_1_CLASS_NAME:
            {
               entryPointClassName = argument;
               _state = State.PARSING_TEST_1_METHOD_NAME;
               break;
            }
            case PARSING_TEST_1_METHOD_NAME:
            {
               entryPointMethodName = argument;
               _entryPoints.add( new TestEntryPoint(null,
                                                    entryPointClassName,
                                                    entryPointMethodName) );
               _state = State.BETWEEN_PARAMETERS;
               break;
            }
            case PARSING_TEST_2_CLASS_NAME:
            {
               entryPointClassName = argument;
               _state = State.PARSING_TEST_2_METHOD_NAME;
               break;
            }
            case PARSING_TEST_2_METHOD_NAME:
            {
               entryPointMethodName = argument;
               _state = State.PARSING_TEST_2_ARGUMENTS;
               break;
            }
            case PARSING_TEST_2_ARGUMENTS:
            {
               entryPointArguments = argument;
               _entryPoints.add( new TestEntryPoint(null,
                                                    entryPointClassName,
                                                    entryPointMethodName,
                                                    entryPointArguments) );
               _state = State.BETWEEN_PARAMETERS;
               break;
            }
            case PARSING_TEST_3_SETS:
            {
               entryPointTestSets = argument;
               break;
            }
            case PARSING_TEST_3_CLASS_NAME:
            {
               entryPointClassName = argument;
               _state = State.PARSING_TEST_3_METHOD_NAME;
               break;
            }
            case PARSING_TEST_3_METHOD_NAME:
            {
               entryPointMethodName = argument;
               _entryPoints.add( new TestEntryPoint(entryPointTestSets,
                                                    entryPointClassName,
                                                    null) );
               _state = State.BETWEEN_PARAMETERS;
               break;
            }
            case PARSING_TEST_4_SETS:
            {
               entryPointTestSets = argument;
               _state = State.PARSING_TEST_4_CLASS_NAME;
               break;
            }
            case PARSING_TEST_4_CLASS_NAME:
            {
               entryPointClassName = argument;
               _state = State.PARSING_TEST_4_METHOD_NAME;
               break;
            }
            case PARSING_TEST_4_METHOD_NAME:
            {
               entryPointMethodName = argument;
               _state = State.PARSING_TEST_4_ARGUMENTS;
               break;
            }
            case PARSING_TEST_4_ARGUMENTS:
            {
               entryPointArguments = argument;
               _entryPoints.add( new TestEntryPoint( entryPointTestSets,
                                                     entryPointClassName,
                                                     entryPointMethodName,
                                                     entryPointArguments) );
               _state = State.BETWEEN_PARAMETERS;
               break;
            }
            case PARSING_RESULTS_SCREEN:
            {
               if ( argument.equals("true") )
               {
                  _showResultsWindow = true;
               }
               else if ( argument.equals("false") )
               {
                  _showResultsWindow = false;
               }
               else
               {
                  System.out.println( "Could not parse command-line flag for -resultsScreen:  \"" + argument + "\".  Should be either \"true\" or \"false\"." );
                  showUsage();
                  _success = false;
                  return;
               }
               _state = State.BETWEEN_PARAMETERS;
               break;
            }
            default:
            {
               throw new IllegalArgumentException( "Internal error:  Unrecognized state:  " + _state );
            }
         }
      }

      if ( _state != State.BETWEEN_PARAMETERS )
      {
         System.out.println( "Missing command line parameter for " + _state.getDescription() );
         showUsage();
         _success = false;
         return;
      }

      if ( _resultsSubdirectoryName == null )
      {
         dateFormatter = new java.text.SimpleDateFormat( "YYYY-MM-dd-HH-mm-ss.SSS" );
         _resultsSubdirectoryName = dateFormatter.format( new java.util.Date() );
      }

      if ( getConfiguratorClassName() == null )
      {
         System.out.println( "missing configuration file name" );
         showUsage();
         _success = false;
         return;
      }
   }

   final boolean success()
   {
      return _success;
   }

   final private State getNextState( String argument )
   {
      if ( argument.equals("-help") )
      {
         showUsage();
         return State.BETWEEN_PARAMETERS;
      }
      else if ( argument.equals("-a") )
      {
         _runAllTests = true;
         return State.BETWEEN_PARAMETERS;
      }
      else if ( argument.equals("-g") )
      {
         _useGUIFlags = true;
         return State.BETWEEN_PARAMETERS;
      }
      else if ( argument.equals("-rootDirectory") )
      {
         return State.PARSING_OUTPUT_DIRECTORY;
      }
      else if ( argument.equals("-subdirectory") )
      {
         return State.PARSING_OUTPUT_SUBDIRECTORY_NAME;
      }
      else if ( argument.equals("-config") )
      {
         return State.PARSING_CONFIGURATOR_CLASS_NAME;
      }
      else if ( argument.equals("-threadCount") )
      {
         return State.PARSING_THREAD_COUNT;
      }
      else if ( argument.equals("-p") )
      {
         return State.PARSING_TEST_PARAMETER;
      }
      else if ( argument.equals("-pp") )
      {
         return State.PARSING_TEST_PARAMETER_PAIR_1;
      }
      else if ( argument.equals("-tag1") )
      {
         return State.PARSING_TAG_1;
      }
      else if ( argument.equals("-tag2") )
      {
         return State.PARSING_TAG_2_SETS;
      }
      else if ( argument.equals("-test1") )
      {
         return State.PARSING_TEST_1_CLASS_NAME;
      }
      else if ( argument.equals("-test2") )
      {
         return State.PARSING_TEST_2_CLASS_NAME;
      }
      else if ( argument.equals("-test3") )
      {
         return State.PARSING_TEST_3_SETS;
      }
      else if ( argument.equals("-test4") )
      {
         return State.PARSING_TEST_4_SETS;
      }
      else if ( argument.equals("-set") )
      {
         return State.PARSING_TEST_SET;
      }
      else if ( argument.equals("-resultWindow") )
      {
         return State.PARSING_RESULTS_SCREEN;
      }
      else
      {
         System.out.println( "Unrecognized command line flag:  " + argument );
         showUsage();
         _success = false;
         return null;
      }
   }

   final Integer parseThreadCount( String  text )
   {
      int result;

      try
      {
         result = Integer.parseInt( text );
      }
      catch ( NumberFormatException numberFormatException )
      {
         System.out.println( "Invalid thread count:  <" + text + ">" );
         showUsage();
         _success = false;
         return null;
      }

      if ( result < 1 )
      {
         System.out.println( "Thread count must be 1 or larger" );
         showUsage();
         _success = false;
         return null;
      }

      return result;
   }

   final boolean showGUI()
   {
      if ( _tagEntryPoints.size() > 0 )
      {
         return false;
      }

      if ( _entryPoints.size() > 0 )
      {
         return false;
      }

      if ( _runAllTests )
      {
         return false;
      }

      if ( _useGUIFlags )
      {
         return false;
      }

      if ( _testSetNames.size() > 0 )
      {
         return false;
      }

      return true;
   }

   final boolean runAllTests()
   {
      return _runAllTests;
   }

   final boolean useGUIFlags()
   {
      return _useGUIFlags;
   }

   final java.util.List<TestEntryPoint> getEntryPoints()
   {
      java.util.List<TestEntryPoint> entryPoints;

      entryPoints = new java.util.ArrayList<TestEntryPoint>();

      entryPoints.addAll( _entryPoints );

      return entryPoints;
   }

   final java.util.List<TagEntryPoint> getTagEntryPoints()
   {
      java.util.List<TagEntryPoint> tags;

      tags = new java.util.ArrayList<TagEntryPoint>();
      tags.addAll( _tagEntryPoints );

      return tags;
   }

   final java.util.List<String> getTestSetNames()
   {
      java.util.List<String> testSetNames;

      testSetNames = new java.util.ArrayList<String>();
      testSetNames.addAll( _testSetNames );

      return testSetNames;
   }

   final String getResultsSubdirectoryName()
   {
      return _resultsSubdirectoryName;
   }

   final String getResultsRootDirectoryName()
   {
      return _resultsRootDirectoryName;
   }

   final String getConfiguratorClassName()
   {
      return _configuratorClassName;
   }

   final Integer getThreadCount()
   {
      return _threadCount;
   }

   final Boolean showResultsWindow()
   {
      return _showResultsWindow;
   }

   final java.util.List<String> getTestParameters()
   {
      java.util.List<String> testParameters;

      testParameters = new java.util.ArrayList<String>();

      testParameters.addAll( _testParameters );

      return testParameters;
   }

   final String getFollowingParameter( String parameter )
      throws UserError
   {
      int index;

      index = _testParameters.indexOf( parameter );

      if ( index == -1 )
      {
         throw new UserError( "Error:  Could not find test parameter \"" + parameter + "\"" );
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

   final boolean containsParameter( String parameter )
   {
      return _testParameters.contains( parameter );
   }

   private enum State
   {
      BETWEEN_PARAMETERS              ( "parsing"                                        ),

      PARSING_OUTPUT_SUBDIRECTORY_NAME( "outputSubdirectoryName"                         ),

      PARSING_OUTPUT_DIRECTORY        ( "outputDirectory"                                ),

      PARSING_CONFIGURATOR_CLASS_NAME ( "parsing configurator class name"                ),

      PARSING_THREAD_COUNT            ( "threadCount"                                    ),

      PARSING_TEST_PARAMETER          ( "parameter"                                      ),

      PARSING_TEST_PARAMETER_PAIR_1   ( "parameterPair1"                                 ),
      PARSING_TEST_PARAMETER_PAIR_2   ( "parameterPair2"                                 ),

      PARSING_TEST_SET                ( "parsing test set"                               ),

      PARSING_TAG_1                   ( "parsing tag"                                    ),

      PARSING_TAG_2_SETS              ( "parsing tag testSets"                           ),
      PARSING_TAG_2_NAMES             ( "parsing tag name"                               ),

      PARSING_TEST_1_CLASS_NAME       ( "parsing class name for test without arguments"  ),
      PARSING_TEST_1_METHOD_NAME      ( "parsing method name for test without arguments" ),

      PARSING_TEST_2_CLASS_NAME       ( "parsing class name for test with arguments"     ),
      PARSING_TEST_2_METHOD_NAME      ( "parsing method name for test with arguments"    ),
      PARSING_TEST_2_ARGUMENTS        ( "parsing arguments for test with arguments"      ),

      PARSING_TEST_3_SETS             ( "parsing test sets for test without arguments"   ),
      PARSING_TEST_3_CLASS_NAME       ( "parsing class name for test without arguments"  ),
      PARSING_TEST_3_METHOD_NAME      ( "parsing method name for test without arguments" ),

      PARSING_TEST_4_SETS             ( "paring test sets for test with arguments"       ),
      PARSING_TEST_4_CLASS_NAME       ( "parsing class name for test with arguments"     ),
      PARSING_TEST_4_METHOD_NAME      ( "parsing method name for test with arguments"    ),
      PARSING_TEST_4_ARGUMENTS        ( "parsing arguments for test with arguments"      ),

      PARSING_RESULTS_SCREEN          ( "parsing results screen flag"                    );

      String _description;

      State( String description )
      {
         _description = description;
      }

      final String getDescription()
      {
         return _description;
      }
   }

   final static void showUsage()
   {
      System.out.println( "Usage:  " + Undercamber.class.getName() + " [flag parameter...]..." );
      System.out.println( "   Flag            Parameters                                Description" );
      System.out.println( "   ----            ----------                                -----------" );
      System.out.println( "   -a              (none)                                    run all tests." );
      System.out.println( "   -tag1           tagNames                                  run tests with specified tags." );
      System.out.println( "   -tag2           tagNames testSetNames                     run tests with specified tags.  Limit search to specified test sets." );
      System.out.println( "   -test1          className methodName                      run specified test (no arguments)." );
      System.out.println( "   -test2          className methodName arguments            run specified test with arguments." );
      System.out.println( "   -test3          testSets className methodName             run specified test.  Limit search to specified test sets.  No arguments." );
      System.out.println( "   -test4          testSets className methodName arguments   run specified test with arguments.  Limit search to specified tests." );
      System.out.println( "   -set            testSetName                               run specified test set." );
      System.out.println( "   -rootDirectory  rootOutputDirectory                       output root directory name." );
      System.out.println( "   -subdirectory   outputSubdirectoryName                    output subdirectory name,  Default built from current time and date)." );
      System.out.println( "   -config         configurationClassName                    configurator class name." );
      System.out.println( "   -threadCount    threadCount                               concurrent thread count." );
      System.out.println( "   -p              parameter                                 parameter passed to test processes." );
      System.out.println( "   -pp             flag parameter                            parameter pair passed to test processes." );
      System.out.println( "   -g              (none)                                    automatically run tests selected in the GUI (from previous run)." );
      System.out.println( "   -resultWindow   [true|false]                              display the results window." );
      System.out.println( "   -help           (none)                                    show this message and continue." );
   }
}

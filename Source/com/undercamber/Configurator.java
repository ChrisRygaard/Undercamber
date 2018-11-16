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
 * This class configures a test system in Undercamber.  <p>
 *
 * The strings passed into this class are automatically expanded using environment variables.  To have a string expanded, use the %{...} notation, similar to parameter expansion in Linux.
 *
 * @see ConfigurationCallback
 */
final public class Configurator
{
   private Undercamber                       _undercamber;
   private String                            _suiteName;
   private Integer                           _pass1ThreadCount;
   private Boolean                           _showResultsWindow;
   private String                            _resultsRootDirectoryName;
   private java.util.List<TestSetDescriptor> _testSetDescriptors;
   private String                            _configurationCallbackClassName;
   private ArgumentParser                    _argumentParser;
   private boolean                           _closed;

   Configurator( Undercamber           undercamber,
                 ConfigurationCallback configurationCallback,
                 ArgumentParser        argumentParser )
      throws javax.xml.parsers.ParserConfigurationException,
             org.xml.sax.SAXException,
             java.io.IOException,
             UserError,
             InternalException
   {
      _undercamber = undercamber;

      _argumentParser = argumentParser;

      _configurationCallbackClassName = configurationCallback.getClass().getName();

      _testSetDescriptors = new java.util.ArrayList<TestSetDescriptor>();

      _suiteName = null;

      _pass1ThreadCount = null;

      _resultsRootDirectoryName = null;

      _showResultsWindow = null;

      _closed = false;
   }

   /**
    * Set the name of the overall suite.  <p>
    *
    * This is equivalent to<pre>
    *    setSuiteName( suiteName,
    *                  ValidationLevel.ERROR );</pre>
    *
    * @param suiteName
    *        The name
    *
    * @throws UserError
    *         If the name is not valid, or if the string is improperly formatted for expansion.
    *
    * @return This Configurator, for chaining
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public Configurator setSuiteName( String suiteName )
      throws UserError,
             InternalException
   {
      return setSuiteName( suiteName,
                           ValidationLevel.ERROR );
   }

   /**
    * Set the name of the overall suite.  <p>
    *
    * @param suiteName
    *        The name.
    *
    * @param validationLevel
    *        The validation level to use when expanding the string.
    *
    * @throws UserError
    *         If the name is not valid, or if the string is improperly formatted for expansion.
    *
    * @return This Configurator, for chaining
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public Configurator setSuiteName( String          suiteName,
                                           ValidationLevel validationLevel )
      throws UserError,
             InternalException
   {
      String errorMessage;

      if ( _closed )
      {
         throw new IllegalAccessError( "Do not alter the configuration after the configuration phase" );
      }

      if ( suiteName == null )
      {
         throw new NullPointerException( "Test suite name cannot be null.  Specified in class " + _configurationCallbackClassName );
      }

      errorMessage = Utilities.isLegalName( suiteName );
      if ( errorMessage != null )
      {
         throw new UserError( errorMessage );
      }

      _suiteName = StringExpander.expandString( suiteName,
                                                validationLevel );

      for ( TestSetDescriptor testSetDescriptor : _testSetDescriptors )
      {
         testSetDescriptor.setTestSuiteName( suiteName );
      }

      return this;
   }

   /**
    * Get the name of the overall test suite.
    *
    * @return The name.
    */
   final public String getSuiteName()
   {
      return _suiteName;
   }

   /**
    * Set the name of the root results directory.  <p>
    *
    * This is equivalent to<pre>
    *    setResultsRootDirectoryName( directoryName,
    *                                 ValidationLevel.ERROR );</pre>
    *
    * Unlike {@link #setResultsRootDirectory}, this method uses string expansion.
    *
    * @param directoryName
    *        The name of the directory.
    *
    * @return This Configurator, for chaining
    *
    * @throws UserError
    *         If the string is improperly formatted for expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public Configurator setResultsRootDirectoryName( String directoryName )
      throws UserError,
             InternalException
   {
      return setResultsRootDirectoryName( directoryName,
                                          ValidationLevel.ERROR );
   }

   /**
    * Set the name of the root results directory.  <p>
    *
    * Unlike {@link #setResultsRootDirectory}, this method uses string expansion.
    *
    * @param directoryName
    *        The name of the directory.
    *
    * @param validationLevel
    *        The validation level to use when expanding the name.
    *
    * @return This Configurator, for chaining
    *
    * @throws UserError
    *         If the string is improperly formatted for expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public Configurator setResultsRootDirectoryName( String          directoryName,
                                                          ValidationLevel validationLevel )
      throws UserError,
             InternalException
   {
      if ( _closed )
      {
         throw new IllegalAccessError( "Do not alter the configuration after the configuration phase" );
      }

      if ( directoryName == null )
      {
         throw new NullPointerException( "Result root directory name cannot be null.  Specified in class " + _configurationCallbackClassName );
      }

      _resultsRootDirectoryName = StringExpander.expandString( directoryName,
                                                               validationLevel );

      return this;
   }

   /**
    * Set the root results directory.
    *
    * Unlike {@link #setResultsRootDirectoryName}, this method does not use string expansion.
    *
    * @return This Configurator, for chaining
    *
    * @param directory
    *        The directory.
    */
   final public Configurator setResultsRootDirectory( java.io.File directory )
   {
      if ( _closed )
      {
         throw new IllegalAccessError( "Do not alter the configuration after the configuration phase" );
      }

      if ( directory == null )
      {
         throw new NullPointerException( "Results root directory cannot be null.  Specified in class " + _configurationCallbackClassName );
      }

      _resultsRootDirectoryName = directory.getPath();

      return this;
   }

   /**
    * Get the name of the root results directory.
    *
    * @return The name
    */
   final public String getResultsRootDirectoryName()
   {
      return _resultsRootDirectoryName;
   }

   /**
    * Set the number of threads to be used for the first pass (the discovery pass).
    *
    * @return This Configurator, for chaining
    *
    * @param threadCount
    *        The number of threads.  If this is null, Undercamber will use a default value.
    */
   final public Configurator setPass1ThreadCount( Integer threadCount )
   {
      if ( _closed )
      {
         throw new IllegalAccessError( "Do not alter the configuration after the configuration phase" );
      }

      _pass1ThreadCount = threadCount;

      return this;
   }

   /**
    * Get the number of threads used for the first pass (the discovery pass).
    *
    * @return The thread count.  If this is null, Undercamber will use a default value.
    */
   final public Integer getPass1ThreadCount()
   {
      return _pass1ThreadCount;
   }

   /**
    * Set the flag indicating whether the results window should be shown.
    *
    * @return This Configurator, for chaining
    *
    * @param showResultsWindow
    *        The flag.  If this is null, Undercamber will use a default value.
    */
   final public Configurator setShowResultsWindow( Boolean showResultsWindow )
   {
      if ( _closed )
      {
         throw new IllegalAccessError( "Do not alter the configuration after the configuration phase" );
      }

      _showResultsWindow = showResultsWindow;

      return this;
   }

   /**
    * Should the results window be shown?
    *
    * @return Indicates whether the results window should be shown.  If this is null, Undercamber will use a default value.
    */
   final public Boolean getShowResultsWindow()
   {
      return _showResultsWindow;
   }

   /**
    * Get a new, unpopulated TestSetBuilder
    *
    * @return The TestSetBuilder
    */
   final public TestSetBuilder getEmptyTestSetBuilder()
   {
      if ( _closed )
      {
         throw new IllegalAccessError( "Do not alter the configuration after the configuration phase" );
      }

      return new TestSetBuilder( _undercamber,
                                 _suiteName,
                                 this );
   }

   final void addTestSetDecriptor( TestSetDescriptor testSetDescriptor )
   {
      _testSetDescriptors.add( testSetDescriptor );
   }

   final private void verifyTestSets()
      throws UserError
   {
      java.util.Set<String> names;
      java.util.Set<String> duplicateNames;

      names = new java.util.HashSet<String>();
      duplicateNames = new java.util.HashSet<String>();

      for ( TestSetDescriptor testSetDescriptor : _testSetDescriptors )
      {
         if ( names.contains(testSetDescriptor.getTestSetName()) )
         {
            duplicateNames.add( testSetDescriptor.getTestSetName() );
         }

         names.add( testSetDescriptor.getTestSetName() );
      }

      if ( duplicateNames.size() > 0 )
      {
         throw new UserError( getDuplicateNameMessage(duplicateNames) );
      }

      for ( TestSetDescriptor testSetDescriptor : _testSetDescriptors )
      {
         if ( testSetDescriptor.getClassName() == null )
         {
            throw new UserError( "Test set \"" + testSetDescriptor.getTestSetName() + "\" does not specify a top-level class name.  Specified in class " + _configurationCallbackClassName );
         }

         testSetDescriptor.checkJVMCommand();

         if ( testSetDescriptor.getTestSetName().equals(Undercamber.DEADLOCK_FILE_NAME) )
         {
            throw new UserError( "\"" + Undercamber.DEADLOCK_FILE_NAME + "\" is a reserved name; it cannot be used as the name of a test set.  Specified in class " + _configurationCallbackClassName );
         }
      }
   }

   final private String getDuplicateNameMessage( java.util.Set<String> duplicateNames )
   {
      StringBuffer           stringBuffer;
      java.util.List<String> duplicateNameList;
      int                    index;

      stringBuffer = new StringBuffer();

      duplicateNameList = new java.util.ArrayList<String>();
      duplicateNameList.addAll( duplicateNames );
      java.util.Collections.sort( duplicateNameList );

      if ( duplicateNames.size() == 1 )
      {
         return "Error:  The test set name \"" + duplicateNameList.get(0) + "\" is not unique.  Specified in class " + _configurationCallbackClassName;
      }
      else if ( duplicateNames.size() == 2 )
      {
         return "Error:  The test set names \"" + duplicateNameList.get(0) + "\" and \"" + duplicateNameList.get(1) + "\" are not unique.  Specified in class " + _configurationCallbackClassName;
      }
      else
      {
         stringBuffer = new StringBuffer( "Error:  The test set names \"" ).append( duplicateNameList.get(0) ).append( "\"" );

         for ( index=1; index<duplicateNameList.size(); index++ )
         {
            stringBuffer.append( ", " );

            if ( index == (duplicateNames.size()-1) )
            {
               stringBuffer.append( "and " );
            }

            stringBuffer.append( "\"" ).append( duplicateNameList.get(index) ).append( "\"" );
         }

         stringBuffer.append( " are not unique.  Specified in class " + _configurationCallbackClassName );

         return stringBuffer.toString();
      }
   }

   final java.util.List<TestSetDescriptor> getTestSetDescriptors()
      throws UserError
   {
      java.util.List<TestSetDescriptor> testSetDescriptors;

      verifyTestSets();

      testSetDescriptors = new java.util.ArrayList<TestSetDescriptor>();

      testSetDescriptors.addAll( _testSetDescriptors );

      return testSetDescriptors;
   }

   final TestSetDescriptor getTestSetDescriptor( String name )
   {
      for ( TestSetDescriptor testSetDescriptor : _testSetDescriptors )
      {
         if ( testSetDescriptor.getTestSetName().equals(name) )
         {
            return testSetDescriptor;
         }
      }

      return null;
   }

   final TestSetDescriptor getTestSetDescriptor( int index )
   {
      return _testSetDescriptors.get( index );
   }

   /**
    * Get the number of test sets set up so far.
    *
    * @return The number of test sets.
    */
   final public int getTestSetCount()
   {
      return _testSetDescriptors.size();
   }

   /**
    * Get the test parameters specified on the command line.
    *
    * @return The parameters
    */
   final public java.util.List<String> getCommandLineParameters()
   {
      return _argumentParser.getTestParameters();
   }

   /**
    * Get the command-line parameter parameter following the specified parameter.  This is a convenience method to help parse the test parameters.  For example, if the command-line parameters include a
    * flag/value pair, this method can provide the value, given the flag.
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
   final public String getFollowingCommandLineParameterAsString( String parameter )
      throws UserError
   {
      return _argumentParser.getFollowingParameter( parameter );
   }

   /**
    * Get the command-line parameter following the specified parameter, interpreted as an integer. This is a convenience method to help parse the test parameters. For example, if the command-line
    * parameters include a flag/value pair, this method can provide the value, given the flag.
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
   final public int getFollowingCommandLineParameterAsInteger( String parameter )
      throws UserError
   {
      try
      {
         return Integer.parseInt( _argumentParser.getFollowingParameter(parameter) );
      }
      catch ( NumberFormatException numberFormatException )
      {
         throw new UserError( "Error:  Invalid integer format:  \"" + _argumentParser.getFollowingParameter(parameter) + "\"" );
      }
   }

   /**
    * Get the command-line parameter following the specified parameter,interpreted as a long. This is a convenience method to help parse the test parameters. For example, if the command-line parameters
    * include a flag/value pair, this method can provide the value, given the flag.
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
   final public long getFollowingCommandLineParameterAsLong( String parameter )
      throws UserError
   {
      try
      {
         return Long.parseLong( _argumentParser.getFollowingParameter(parameter) );
      }
      catch ( NumberFormatException numberFormatException )
      {
         throw new UserError( "Error:  Invalid long format:  \"" + _argumentParser.getFollowingParameter(parameter) + "\"" );
      }
   }

   /**
    * Get the command-line parameter following the specified parameter, interpreted as a double. This is a convenience method to help parse the test parameters. For example, if the command-line
    * parameters include a flag/value pair, this method can provide the value, given the flag.
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
   final public double getFollowingCommandLineParameterAsDouble( String parameter )
      throws UserError
   {
      try
      {
         return Double.parseDouble( _argumentParser.getFollowingParameter(parameter) );
      }
      catch ( NumberFormatException numberFormatException )
      {
         throw new UserError( "Error:  Invalid double format:  \"" + _argumentParser.getFollowingParameter(parameter) + "\"" );
      }
   }

   /**
    * Get the command-line parameter following the specified parameter, interpreted as a float. This is a convenience method to help parse the test parameters. For example, if the command-line parameters
    * include a flag/value pair, this method can provide the value, given the flag.
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
   final public float getFollowingCommandLineParameterAsFloat( String parameter )
      throws UserError
   {
      try
      {
         return Float.parseFloat( _argumentParser.getFollowingParameter(parameter) );
      }
      catch ( NumberFormatException numberFormatException )
      {
         throw new UserError( "Error:  Invalid float format:  \"" + _argumentParser.getFollowingParameter(parameter) + "\"" );
      }
   }

   /**
    * Determine whether a command-line parameter is present.  This is a convenience method to help determine if a simple flag is present on the command line.
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
   final public boolean containsCommandLineParameter( String parameter )
   {
      return _argumentParser.containsParameter( parameter );
   }

   final void close()
   {
      _closed = true;
   }

   final boolean isClosed()
   {
      return _closed;
   }
}

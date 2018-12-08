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
 * Configure a test set.  <p>
 *
 * This class configures the environment that is used during the second pass.
 *
 * @see Configurator#getEmptyTestSetBuilder
 */
final public class TestSetBuilder
{
   private Class<? extends TestUnit>    _class;
   private String                       _testSetName;
   private String                       _testSuiteName;
   private String                       _jvmDirectoryName;
   private Integer                      _threadCount;
   private java.util.List<String>       _testParameters;
   private java.util.List<String>       _javaParameters;
   private java.util.Map<String,String> _environmentVariables;
   private Undercamber                  _undercamber;
   private Configurator                 _configurator;

   TestSetBuilder( Undercamber  undercamber,
                   String       testSuiteName,
                   Configurator configurator )
   {
      _undercamber = undercamber;
      _testSuiteName = testSuiteName;
      _configurator = configurator;

      _testSetName = null;
      _class = null;
      _jvmDirectoryName = null;
      _threadCount = null;
      _testParameters = new java.util.ArrayList<String>();
      _javaParameters = new java.util.ArrayList<String>();
      _environmentVariables = null;
   }

   /**
    * Create a copy
    *
    * @param original
    *        The original from which the copy is made.
    */
   public TestSetBuilder( TestSetBuilder original )
   {
      if ( original._configurator.isClosed() )
      {
         throw new IllegalAccessError( "Do not alter the configuration after the configuration phase" );
      }

      _undercamber = original._undercamber;
      _testSuiteName = original._testSuiteName;
      _configurator = original._configurator;

      _testSetName = original._testSetName;
      _class = original._class;
      _jvmDirectoryName = original._jvmDirectoryName;
      _threadCount = original._threadCount;
      _testParameters = new java.util.ArrayList<String>();
      _testParameters.addAll( original._testParameters );
      _javaParameters = new java.util.ArrayList<String>();
      _javaParameters.addAll( original._javaParameters );
      if ( original._environmentVariables == null )
      {
         _environmentVariables = null;
      }
      else
      {
         _environmentVariables = new java.util.HashMap<String,String>();
         for ( String name : original._environmentVariables.keySet() )
         {
            _environmentVariables.put( name,
                                       original._environmentVariables.get(name) );
         }
      }
   }

   /**
    * Set the name of the test set.  <p>
    *
    * This is equivalent to<pre>
    *    setTestSetName( testSetName,
    *                    ValidationLevel.ERROR );</pre>
    *
    * @param testSetName
    *        The name.
    *
    * @throws UserError
    *         If the name is not valid, or if the string is improperly formatted for expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void setTestSetName( String testSetName )
      throws UserError,
             InternalException
   {
      setTestSetName( testSetName,
                      ValidationLevel.ERROR );
   }

   /**
    * Set the name of the test set.
    *
    * @param testSetName
    *        The name.
    *
    * @param validationLevel
    *        The validation level to use when expanding the string.
    *
    * @throws UserError
    *         If the name is not valid, or if the string is improperly formatted for expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void setTestSetName( String          testSetName,
                                     ValidationLevel validationLevel )
      throws UserError,
             InternalException
   {
      String errorMessage;

      errorMessage = Utilities.isLegalName( testSetName );
      if ( errorMessage != null )
      {
         throw new UserError( errorMessage );
      }

      _testSetName = StringExpander.expandString( testSetName,
                                                  validationLevel );
   }

   /**
    * Get the test set name
    *
    * @return The name
    */
   final public String getTestSetName()
   {
      return _testSetName;
   }

   /**
    * Set the name of the top-level test class.  <p>
    *
    * The specified class must implement {@link TestUnit}.
    *
    * @param type
    *        The top-level class
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void setClass( Class<? extends TestUnit> type )
      throws UserError,
             InternalException
   {
      _class = type;
   }

   /**
    * Get the name of the top-level test class.
    *
    * @return The top-level class
    */
   final public Class<? extends TestUnit> getTopLevelClass()
   {
      return _class;
   }

   /**
    * Set the name of the JVM directory to be used in the second pass.  <p>
    *
    * Undercamber will look for the Java executable in the <tt>bin</tt> subdirectory below the specified directory.<p>
    *
    * Unlike {@link #setJVMDirectory}, this method uses string expansion.<p>
    *
    * This is equivalent to<pre>
    *    setJVMDirectoryName( jvmDirectoryName,
    *                         ValidationLevel.ERROR )</pre>
    *
    * @param jvmDirectoryName
    *        The directory name
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void setJVMDirectoryName( String jvmDirectoryName )
      throws UserError,
             InternalException
   {
      setJVMDirectoryName( jvmDirectoryName,
                           ValidationLevel.ERROR );
   }

   /**
    * Set the name of the JVM directory to be used in the second pass.  <p>
    *
    * Undercamber will look for the Java executable in the <tt>bin</tt> subdirectory below the specified directory.<p>
    *
    * Unlike {@link #setJVMDirectory}, this method uses string expansion.<p>
    *
    * @param jvmDirectoryName
    *        The directory name
    *
    * @param validationLevel
    *        The validation level to use for string expansion
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void setJVMDirectoryName( String          jvmDirectoryName,
                                          ValidationLevel validationLevel )
      throws UserError,
             InternalException
   {
      _jvmDirectoryName = StringExpander.expandString( jvmDirectoryName,
                                                       validationLevel );
   }

   /**
    * Set the JVM directory.  <p>
    *
    * Undercamber will look for the Java executable in the <pre>bin</pre> subdirectory below the specified directory.<p>
    *
    * Unlike {@link #setJVMDirectoryName}, this method does not use string expansion.<p>
    *
    * @param jvmDirectory
    *        The JVM directory
    */
   final public void setJVMDirectory( java.io.File jvmDirectory )
   {
      _jvmDirectoryName = jvmDirectory.getPath();
   }

   /**
    * Get the name of the JVM directory.  <p>
    *
    * Undercamber will look for the Java executable in the <pre>bin</pre> subdirectory below the specified directory.<p>
    *
    * @return The directory name
    */
   final public String getJVMDirectoryName()
   {
      return _jvmDirectoryName;
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

   /**
    * Set the thread count to be used during the second pass.
    *
    * @param threadCount
    *        The thread count.  If this is null, Undercamber will use a default value.
    */
   final public void setPass2ThreadCount( Integer threadCount )
   {
      _threadCount = threadCount;
   }

   /**
    * Get the thread count used during the second pass.
    *
    * @return The thread count.  If this is null, Undercamber will use a default value.
    */
   final public Integer getPass2ThreadCount()
   {
      return _threadCount;
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////

   /**
    * Set the test parameters in this test set.  <p>
    *
    * This method does not use string expansion.
    *
    * @param testParameters
    *        The set of parameters to be used for the test set.  The internal list is replace by the provided list, so updates to the list outside of this class will be reflected in the list.
    */
   final public void setTestParameters( java.util.List<String> testParameters )
   {
      if ( testParameters == null )
      {
         throw new NullPointerException( "Test parameters cannot be null" );
      }

      _testParameters = testParameters;
   }

   /**
    * Remove all test parameters.
    */
   final public void clearTestParameters()
   {
      _testParameters.clear();
   }

   /**
    * Get the list of test parameters.  <p>
    *
    * This returns the internal list, so updates to the list outside of this class will be reflected in the list.
    *
    * @return The list of parameters.
    */
   final public java.util.List<String> getTestParameters()
   {
      return _testParameters;
   }

   /**
    * Append a parameter to the list of test parameters.  <p>
    *
    * This is equivalent to<pre>
    *    appendTestParameter( testParameter,
    *                         ValidationLevel.ERROR )</pre>
    *
    * @param testParameter
    *        The parameter to append.
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void appendTestParameter( String testParameter )
      throws UserError,
             InternalException
   {
      appendTestParameter( testParameter,
                           ValidationLevel.ERROR );
   }

   /**
    * Append a parameter to the list of test parameters.
    *
    * @param testParameter
    *        The parameter to append.
    *
    * @param validationLevel
    *        The validation level to use when expanding the parameter.
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void appendTestParameter( String          testParameter,
                                          ValidationLevel validationLevel )
      throws UserError,
             InternalException
   {
      _testParameters.add( StringExpander.expandString(testParameter,
                                                       validationLevel) );
   }

   /**
    * Append two entries to the list of test parameters.  <p>
    *
    * This is equivalent to<pre>
    *    appendTestParameterPair( flag,
    *                             parameter,
    *                             ValidationLevel.ERROR )</pre>
    *
    * @param flag
    *        The first parameter to append
    *
    * @param parameter
    *        The second parameter to append
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void appendTestParameterPair( String flag,
                                              String parameter )
      throws UserError,
             InternalException
   {
      appendTestParameterPair( flag,
                               parameter,
                               ValidationLevel.ERROR );
   }

   /**
    * Append two entries to the list of test parameters.  <p>
    *
    * @param flag
    *        The first parameter to append
    *
    * @param parameter
    *        The second parameter to append
    *
    * @param validationLevel
    *        The validation level to use when expanding the strings.
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void appendTestParameterPair( String          flag,
                                              String          parameter,
                                              ValidationLevel validationLevel )
      throws UserError,
             InternalException
   {
      _testParameters.add( StringExpander.expandString(flag,
                                                       validationLevel) );
      _testParameters.add( StringExpander.expandString(parameter,
                                                       validationLevel) );
   }

   /**
    * Append multiple entries to the list of test parameters.  <p>
    *
    * @param validationLevel
    *        The validation level to use when expanding the strings.
    *
    * @param parameters
    *        The parameters to append
    *
    * @throws UserError
    *         If a string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the strings.
    */
   final public void appendTestParameters( ValidationLevel validationLevel,
                                           String...       parameters )
      throws UserError,
             InternalException
   {
      for ( String parameter : parameters )
      {
         _testParameters.add( StringExpander.expandString(parameter,
                                                          validationLevel) );
      }
   }

   /**
    * Append multiple entries to the list of test parameters.  <p>
    *
    * @param parameters
    *        The parameters to append
    *
    * @throws UserError
    *         If a string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the strings.
    */
   final public void appendTestParameters( String... parameters )
      throws UserError,
             InternalException
   {
      for ( String parameter : parameters )
      {
         _testParameters.add( StringExpander.expandString(parameter,
                                                          ValidationLevel.ERROR) );
      }
   }

   /**
    * Prepend a parameter to the list of test parameters.  <p>
    *
    * This is equivalent to<pre>
    *    prependTestParameter( testParameter,
    *                          ValidationLevel.ERROR )</pre>
    *
    * @param testParameter
    *        The parameter to prepend.
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void prependTestParameter( String testParameter )
      throws UserError,
             InternalException
   {
      prependTestParameter( testParameter,
                            ValidationLevel.ERROR );
   }

   /**
    * Prepend a parameter to the list of test parameters.
    *
    * @param testParameter
    *        The parameter to prepend.
    *
    * @param validationLevel
    *        The validation level to use when expanding the parameter.
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void prependTestParameter( String          testParameter,
                                           ValidationLevel validationLevel )
      throws UserError,
             InternalException
   {
      _testParameters.add( 0, StringExpander.expandString(testParameter,
                                                          validationLevel) );
   }

   /**
    * Prepend two entries to the list of test parameters.  <p>
    *
    * This is equivalent to<pre>
    *    prependTestParameterPair( flag,
    *                              parameter,
    *                              ValidationLevel.ERROR )</pre>
    *
    * @param flag
    *        The first parameter to append
    *
    * @param parameter
    *        The second parameter to append
    *
    * @throws UserError
    *         If the strings are not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void prependTestParameterPair( String flag,
                                               String parameter )
      throws UserError,
             InternalException
   {
      prependTestParameterPair( flag,
                                parameter,
                                ValidationLevel.ERROR );
   }

   /**
    * Prepend two entries to the list of test parameters.  <p>
    *
    * @param flag
    *        The first parameter to preppend
    *
    * @param validationLevel
    *        The validation level to use when expanding the strings.
    *
    * @param parameter
    *        The second parameter to prepend
    *
    * @throws UserError
    *         If the strings are not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void prependTestParameterPair( String          flag,
                                               String          parameter,
                                               ValidationLevel validationLevel )
      throws UserError,
             InternalException
   {
      if ( _testParameters == null )
      {
         _testParameters = new java.util.ArrayList<String>();
      }

      _testParameters.add( 0, StringExpander.expandString(parameter,
                                                          validationLevel) );

      _testParameters.add( 0, StringExpander.expandString(flag,
                                                          validationLevel) );
   }

   /**
    * Prepend multiple entries to the list of test parameters.  <p>
    *
    * @param validationLevel
    *        The validation level to use when expanding the strings.
    *
    * @param parameters
    *        The parameters to prepend
    *
    * @throws UserError
    *         If a string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the strings.
    */
   final public void prependTestParameters( ValidationLevel validationLevel,
                                            String...       parameters )
      throws UserError,
             InternalException
   {
      int index;

      for ( index=(parameters.length-1); index>=0; index-- )
      {
         _testParameters.add( 0, StringExpander.expandString(parameters[index],
                                                             validationLevel) );
      }
   }

   /**
    * Prepend multiple entries to the list of test parameters.  <p>
    *
    * @param parameters
    *        The parameters to prepend
    *
    * @throws UserError
    *         If a string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the strings.
    */
   final public void prependTestParameters( String... parameters )
      throws UserError,
             InternalException
   {
      int index;

      for ( index=(parameters.length-1); index>=0; index-- )
      {
         _testParameters.add( 0, StringExpander.expandString(parameters[index],
                                                             ValidationLevel.ERROR) );
      }
   }

   /**
    * Append two entries to the list of test parameters.  <p>
    *
    * This is equivalent to<pre>
    *    appendTestParameterPair( flag,
    *                             path,
    *                             ValidationLevel.ERROR )</pre>
    *
    * @param flag
    *        The first parameter to append
    *
    * @param path
    *        The second parameter to append.  No string expansion is applied to this.
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void appendTestParameterPair( String flag,
                                              Path   path )
      throws UserError,
             InternalException
   {
      appendTestParameterPair( flag,
                               path,
                               ValidationLevel.ERROR );
   }

   /**
    * Append two entries to the list of test parameters.  <p>
    *
    * @param flag
    *        The first parameter to append
    *
    * @param validationLevel
    *        The validation level to use when expanding the strings.
    *
    * @param path
    *        The second parameter to append.  No string expansion is applied to this.
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void appendTestParameterPair( String          flag,
                                              Path            path,
                                              ValidationLevel validationLevel )
      throws UserError,
             InternalException
   {
      _testParameters.add( StringExpander.expandString(flag,
                                                       validationLevel) );
      _testParameters.add( path.toString() );
   }

   /**
    * Prepend two entries to the list of test parameters.  <p>
    *
    * This is equivalent to<pre>
    *    prependTestParameterPair( flag,
    *                              path,
    *                              ValidationLevel.ERROR )</pre>
    *
    * @param flag
    *        The first parameter to append
    *
    * @param path
    *        The second parameter to append.  No string expansion is applied to this.
    *
    * @throws UserError
    *         If the strings are not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void prependTestParameterPair( String flag,
                                               Path   path )
      throws UserError,
             InternalException
   {
      prependTestParameterPair( flag,
                                path,
                                ValidationLevel.ERROR );
   }

   /**
    * Prepend two entries to the list of test parameters.  <p>
    *
    * @param flag
    *        The first parameter to preppend
    *
    * @param validationLevel
    *        The validation level to use when expanding the strings.
    *
    * @param path
    *        The second parameter to append.  No string expansion is applied to this.
    *
    * @throws UserError
    *         If the strings are not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void prependTestParameterPair( String          flag,
                                               Path            path,
                                               ValidationLevel validationLevel )
      throws UserError,
             InternalException
   {
      if ( _testParameters == null )
      {
         _testParameters = new java.util.ArrayList<String>();
      }

      _testParameters.add( 0, path.toString() );

      _testParameters.add( 0, StringExpander.expandString(flag,
                                                          validationLevel) );
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////

   /**
    * Set the java parameters in this test set.  <p>
    *
    * This method does not use string expansion.
    *
    * @param javaParameters
    *        The set of parameters to be used for the test set.  The internal list is replaced by the provided list, so updates to the list outside of this class will be reflected in the list.
    */
   final public void setJavaParameters( java.util.List<String> javaParameters )
   {
      if ( javaParameters == null )
      {
         throw new NullPointerException( "Java parameters cannot be null" );
      }

      _javaParameters = javaParameters;
   }

   /**
    * Remove all java parameters.
    */
   final public void clearJavaParameters()
   {
      _javaParameters.clear();
   }

   /**
    * Get the list of java parameters.  <p>
    *
    * This returns the internal list, so updates to the list outside of this class will be reflected in the list.
    *
    * @return The list of parameters.
    */
   final public java.util.List<String> getJavaParameters()
   {
      return _javaParameters;
   }

   /**
    * Append a parameter to the list of java parameters.  <p>
    *
    * This is equivalent to<pre>
    *    appendJavaParameter( javaParameter,
    *                         ValidationLevel.ERROR )</pre>
    *
    * @param javaParameter
    *        The parameter to append.
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void appendJavaParameter( String javaParameter )
      throws UserError,
             InternalException
   {
      appendJavaParameter( javaParameter,
                           ValidationLevel.ERROR );
   }

   /**
    * Append a parameter to the list of java parameters.
    *
    * @param javaParameter
    *        The parameter to append.
    *
    * @param validationLevel
    *        The validation level to use when expanding the parameter.
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void appendJavaParameter( String          javaParameter,
                                          ValidationLevel validationLevel )
      throws UserError,
             InternalException
   {
      _javaParameters.add( StringExpander.expandString(javaParameter,
                                                       validationLevel) );
   }

   /**
    * Append multiple Java parameters
    *
    * @param javaParameters
    *        The parameters to add
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void appendJavaParameters( String... javaParameters )
      throws UserError,
             InternalException
   {
      for ( String javaParameter : javaParameters )
      {
         _javaParameters.add( StringExpander.expandString(javaParameter,
                                                          ValidationLevel.ERROR) );
      }
   }

   /**
    * Apped multiple Java parameters
    *
    * @param validationLevel
    *        The validation level to use when expanding the parameters
    *
    * @param javaParameters
    *        The parameters to append
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void appendJavaParameters( ValidationLevel validationLevel,
                                           String...       javaParameters )
      throws UserError,
             InternalException
   {
      for ( String javaParameter : javaParameters )
      {
         _javaParameters.add( StringExpander.expandString(javaParameter,
                                                          validationLevel) );
      }
   }

   /**
    * Append two entries to the list of java parameters.  <p>
    *
    * This is equivalent to<pre>
    *    appendJavaParameterPair( flag,
    *                             parameter,
    *                             ValidationLevel.ERROR )</pre>
    *
    * @param flag
    *        The first parameter to append
    *
    * @param parameter
    *        The second parameter to append
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void appendJavaParameterPair( String flag,
                                              String parameter )
      throws UserError,
             InternalException
   {
      appendJavaParameterPair( flag,
                               parameter,
                               ValidationLevel.ERROR );
   }

   /**
    * Append two entries to the list of java parameters.  <p>
    *
    * @param flag
    *        The first parameter to append
    *
    * @param validationLevel
    *        The validation level to use when expanding the strings.
    *
    * @param parameter
    *        The second parameter to append
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void appendJavaParameterPair( String          flag,
                                              String          parameter,
                                              ValidationLevel validationLevel )
      throws UserError,
             InternalException
   {
      _javaParameters.add( StringExpander.expandString(flag,
                                                       validationLevel) );
      _javaParameters.add( StringExpander.expandString(parameter,
                                                       validationLevel) );
   }

   /**
    * Prepend a parameter to the list of java parameters.  <p>
    *
    * This is equivalent to<pre>
    *    prependJavaParameter( javaParameter,
    *                          ValidationLevel.ERROR )</pre>
    *
    * @param javaParameter
    *        The parameter to prepend.
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void prependJavaParameter( String javaParameter )
      throws UserError,
             InternalException
   {
      prependJavaParameter( javaParameter,
                            ValidationLevel.ERROR );
   }

   /**
    * Prepend a parameter to the list of java parameters.
    *
    * @param javaParameter
    *        The parameter to prepend.
    *
    * @param validationLevel
    *        The validation level to use when expanding the parameter.
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void prependJavaParameter( String          javaParameter,
                                           ValidationLevel validationLevel )
      throws UserError,
             InternalException
   {
      _javaParameters.add( 0, StringExpander.expandString(javaParameter,
                                                          validationLevel) );
   }

   /**
    * Prepend two entries to the list of java parameters.  <p>
    *
    * This is equivalent to<pre>
    *    prependJavaParameterPair( flag,
    *                              parameter,
    *                              ValidationLevel.ERROR )</pre>
    *
    * @param flag
    *        The first parameter to append
    *
    * @param parameter
    *        The second parameter to append
    *
    * @throws UserError
    *         If the strings are not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void prependJavaParameterPair( String flag,
                                               String parameter )
      throws UserError,
             InternalException
   {
      prependJavaParameterPair( flag,
                                parameter,
                                ValidationLevel.ERROR );
   }

   /**
    * Prepend two entries to the list of java parameters.  <p>
    *
    * @param flag
    *        The first parameter to preppend
    *
    * @param validationLevel
    *        The validation level to use when expanding the strings.
    *
    * @param parameter
    *        The second parameter to prepend
    *
    * @throws UserError
    *         If the strings are not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void prependJavaParameterPair( String          flag,
                                               String          parameter,
                                               ValidationLevel validationLevel )
      throws UserError,
             InternalException
   {
      if ( _javaParameters == null )
      {
         _javaParameters = new java.util.ArrayList<String>();
      }

      _javaParameters.add( 0, StringExpander.expandString(parameter,
                                                          validationLevel) );

      _javaParameters.add( 0, StringExpander.expandString(flag,
                                                          validationLevel) );
   }

   /**
    * Prepend multiple Java parameters
    *
    * @param parameters
    *        The parameters to prepend
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void prependJavaParamters( String... parameters )
      throws UserError,
             InternalException
   {
      int index;

      for ( index=(parameters.length-1); index>=0; index-- )
      {
         _javaParameters.add( 0, StringExpander.expandString(parameters[index],
                                                             ValidationLevel.ERROR) );
      }
   }

   /**
    * Prepend multiple Java parameters
    *
    * @param validationLevel
    *        The validation level to use when expanding the strings.
    *
    * @param parameters
    *        The parameters to prepend
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void prependJavaParameters( ValidationLevel validationLevel,
                                            String...       parameters )
      throws UserError,
             InternalException
   {
      int index;

      for ( index=(parameters.length-1); index>=0; index-- )
      {
         _javaParameters.add( 0, StringExpander.expandString(parameters[index],
                                                             validationLevel) );
      }
   }

   /**
    * Prepend multiple Java parameters
    *
    * @param parameters
    *        The parameters to prepend
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void prependJavaParameters( String...       parameters )
      throws UserError,
             InternalException
   {
      int index;

      for ( index=(parameters.length-1); index>=0; index-- )
      {
         _javaParameters.add( 0, StringExpander.expandString(parameters[index],
                                                             ValidationLevel.ERROR) );
      }
   }

   /**
    * Append two entries to the list of java parameters.  <p>
    *
    * This is equivalent to<pre>
    *    appendJavaParameterPair( flag,
    *                             path,
    *                             ValidationLevel.ERROR )</pre>
    *
    * @param flag
    *        The first parameter to append
    *
    * @param path
    *        The second parameter to append.  No string expansion is applied to this.
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void appendJavaParameterPair( String flag,
                                              Path   path )
      throws UserError,
             InternalException
   {
      appendJavaParameterPair( flag,
                               path,
                               ValidationLevel.ERROR );
   }

   /**
    * Append two entries to the list of java parameters.  <p>
    *
    * @param flag
    *        The first parameter to append
    *
    * @param validationLevel
    *        The validation level to use when expanding the strings.
    *
    * @param path
    *        The second parameter to append.  No string expansion is applied to this.
    *
    * @throws UserError
    *         If the string is not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void appendJavaParameterPair( String          flag,
                                              Path            path,
                                              ValidationLevel validationLevel )
      throws UserError,
             InternalException
   {
      _javaParameters.add( StringExpander.expandString(flag,
                                                       validationLevel) );

      _javaParameters.add( path.toString() );
   }

   /**
    * Prepend two entries to the list of java parameters.  <p>
    *
    * This is equivalent to<pre>
    *    prependJavaParameterPair( flag,
    *                              path,
    *                              ValidationLevel.ERROR )</pre>
    *
    * @param flag
    *        The first parameter to append
    *
    * @param path
    *        The second parameter to append.  No string expansion is applied to this.
    *
    * @throws UserError
    *         If the strings are not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void prependJavaParameterPair( String flag,
                                               Path   path )
      throws UserError,
             InternalException
   {
      prependJavaParameterPair( flag,
                                path,
                                ValidationLevel.ERROR );
   }

   /**
    * Prepend two entries to the list of java parameters.  <p>
    *
    * @param flag
    *        The first parameter to preppend
    *
    * @param validationLevel
    *        The validation level to use when expanding the strings.
    *
    * @param path
    *        The second parameter to append.  No string expansion is applied to this.
    *
    * @throws UserError
    *         If the strings are not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void prependJavaParameterPair( String          flag,
                                               Path            path,
                                               ValidationLevel validationLevel )
      throws UserError,
             InternalException
   {
      if ( _javaParameters == null )
      {
         _javaParameters = new java.util.ArrayList<String>();
      }

      _javaParameters.add( 0, path.toString() );

      _javaParameters.add( 0, StringExpander.expandString(flag,
                                                          validationLevel) );
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////

   /**
    * Set the environment variables.  <p>
    *
    * The internal Map is replaced by the provided map, so subsequent modifications to the map outside of this class will be reflected in the internal map.<p>
    *
    * If environmentVariables is null, Undercamber will use the environment variables inherited from the operating system.<p>
    *
    * This method does not perform any string expansion.
    *
    * @param environmentVariables
    *        The environment variables
    */
   final public void setEnvironmentVariables( java.util.Map<String,String> environmentVariables )
   {
      _environmentVariables = environmentVariables;
   }

   /**
    * Remove all environment variables.  This implies the test set will have no environment variables.  Contrast this with {@link #deleteEnvironmentVariables}, which implies defaults.
    */
   final public void clearEnvironmentVariables()
   {
      if ( _environmentVariables == null )
      {
         _environmentVariables = new java.util.HashMap<String,String>();
      }

      _environmentVariables.clear();
   }

   /**
    * Set the internal representation to null, implying defaults.  Contrast this with {@link #clearEnvironmentVariables}, which implies no environment variables.
    */
   final public void deleteEnvironmentVariables()
   {
      _environmentVariables = null;
   }

   /**
    * Get the environment variables.  <p>
    *
    * This method returns the internal map, so subsequent modification to the map outside of this class will be reflected in the internal map.
    *
    * @return The environment variables.  If this is <tt>null</tt>, it implies Undercamber will use the environement variables inherited from the operating system.
    */
   final public java.util.Map<String,String> getEnvironmentVariables()
   {
      return _environmentVariables;
   }

   /**
    * Add an environment variable.  <p>
    *
    * This is equivalent to<pre>
    *    addEnvironmentVariable( name,
    *                            value,
    *                            ValidationLevel.ERROR );</pre>
    *
    * @param name
    *        The name of the environment variable
    *
    * @param value
    *        The value of the environment variable
    *
    * @throws UserError
    *         If the strings are not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void addEnvironmentVariable( String name,
                                             String value )
      throws UserError,
             InternalException
   {
      addEnvironmentVariable( name,
                              value,
                              ValidationLevel.ERROR );
   }

   /**
    * Add an environment variable.  <p>
    *
    * If the internal representation is <tt>null</tt> (implying defaults), a new internal Map will be created.
    *
    * @param name
    *        The name of the environment variable
    * @param value
    *        The value of the environment variable
    * @param validationLevel
    *        The validation level to use when expanding the strings.
    *
    * @throws UserError
    *         If the strings are not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void addEnvironmentVariable( String          name,
                                             String          value,
                                             ValidationLevel validationLevel )
      throws UserError,
             InternalException
   {
      String expandedName;
      String expandedValue;

      expandedName = StringExpander.expandString( name );
      expandedValue = StringExpander.expandString( value );

      if ( _environmentVariables == null )
      {
         _environmentVariables = new java.util.HashMap<String,String>();
      }

      _environmentVariables.put( expandedName,
                                 expandedValue );
   }

   /**
    * Add an environment variable.  <p>
    *
    * This is equivalent to<pre>
    *    addEnvironmentVariable( name,
    *                            path,
    *                            ValidationLevel.ERROR );</pre>
    *
    * @param name
    *        The name of the environment variable
    * @param path
    *        The value of the environment variable.  There is no string expansion on this argument.
    *
    * @throws UserError
    *         If the strings are not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void addEnvironmentVariable( String name,
                                             Path   path )
      throws UserError,
             InternalException
   {
      addEnvironmentVariable( name,
                              path,
                              ValidationLevel.ERROR );
   }

   /**
    * Add an environment variable.  <p>
    *
    * @param name
    *        The name of the environment variable
    * @param path
    *        The value of the environment variable.  There is no string expansion on this argument.
    * @param validationLevel
    *        The validation level to use when expanding the strings.
    *
    * @throws UserError
    *         If the strings are not properly formatted for string expansion.
    *
    * @throws InternalException
    *         If there is an internal problem expanding the string.
    */
   final public void addEnvironmentVariable( String          name,
                                             Path            path,
                                             ValidationLevel validationLevel )
      throws UserError,
             InternalException
   {
      String expandedName;

      expandedName = StringExpander.expandString( name );

      if ( _environmentVariables == null )
      {
         _environmentVariables = new java.util.HashMap<String,String>();
      }

      _environmentVariables.put( expandedName,
                                 path.toString() );
   }

   /**
    * A convenience method to copy an environment variable from the system environment to the environment used in the second pass.
    *
    * @param environmentVariable
    *        The name of the environment variable to copy into the second-pass environment.
    *
    * @throws UserError
    *         If the environment variable does not exist.
    */
   final public void copyEnvironmentVariable( String environmentVariable )
      throws UserError
   {
      String value;

      value = System.getenv( environmentVariable );

      if ( value == null )
      {
         throw new UserError( "Environment variable \"" + environmentVariable + "\" not found." );
      }
      else
      {
         if ( _environmentVariables == null )
         {
            _environmentVariables = new java.util.HashMap<String,String>();
         }

         _environmentVariables.put( environmentVariable,
                                    value );
      }
   }

   /**
    * A convenience method to copy an environment variable from the system environment to the environment used in the second pass.  <p>
    *
    * This method does nothing if the specified environment variable does not exist.
    *
    * @param environmentVariable
    *        The name of the environment variable to copy into the second-pass environment.
    */
   final public void copyEnvironmentVariableIfPresent( String environmentVariable )
   {
      String value;

      value = System.getenv( environmentVariable );

      if ( value != null )
      {
         if ( _environmentVariables == null )
         {
            _environmentVariables = new java.util.HashMap<String,String>();
         }

         _environmentVariables.put( environmentVariable,
                                    value );
      }
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////

   /**
    * Create a test set and add it to the Undercamber system.  <p>
    *
    * The newly created test set will use copies of the internal data in this <tt>TestSetBuilder</tt>, so subsequent updates to this TestSetBuilder object will not be reflected in the test set created by
    * this call.
    */
   final public void createTestSet()
   {
      java.util.List<String>       testParameters;
      java.util.List<String>       javaParameters;
      java.util.Map<String,String> environmentVariables;

      if ( _configurator.isClosed() )
      {
         throw new IllegalAccessError( "Do not alter the configuration after the configuration phase" );
      }

      testParameters = new java.util.ArrayList<String>();
      testParameters.addAll( _testParameters );

      javaParameters = new java.util.ArrayList<String>();
      javaParameters.addAll( _javaParameters );

      if ( _environmentVariables == null )
      {
         environmentVariables = null;
      }
      else
      {
         environmentVariables = new java.util.HashMap<String,String>();
         for ( String name : _environmentVariables.keySet() )
         {
            environmentVariables.put( name,
                                      _environmentVariables.get(name) );
         }
      }

      _configurator.addTestSetDecriptor( new TestSetDescriptor(_undercamber,
                                                               _class,
                                                               _testSetName,
                                                               _testSuiteName,
                                                               _jvmDirectoryName,
                                                               _threadCount,
                                                               testParameters,
                                                               javaParameters,
                                                               environmentVariables) );
   }
}

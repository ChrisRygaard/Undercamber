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
 * This indicates to Undercamber what tests must run and pass before
 * another test can run.
 *
 * @see TestManager#initialize( SubtestSequencingMode, String, SubtestContinuationMode, Prerequisite[], Tag[], Requirement... )
 */
final public class Prerequisite
{
   final private static int    CLASS_PERSISTENCE_VERSION = 0;
   final private static String CLASS_PERSISTENCE_BRANCH  = "";

   private java.util.List<String>        _testSetNames;
   private String                        _className;
   private String                        _methodName;
   private String                        _arguments;
   private IncludeSubtests               _includeSubtests;
   private Type                          _type;
   private PrerequisiteMatchMultiplicity _prerequisiteMatchMultiplicity;
   private boolean                       _satisfied;

   /**
    * Equivalent to<pre>
    *    Prerequisite( className,
    *                  methodName,
    *                  null,
    *                  IncludeSubtests.DONT_INCLUDE_SUBTESTS,
    *                  Type.FIXED,
    *                  PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
    *                  null )</pre>
    *
    * @param className
    *        Name of the prerequisite class
    * @param methodName
    *        Name of the prerequisite method
    * @see #Prerequisite(String,String,String,IncludeSubtests,Type,PrerequisiteMatchMultiplicity,String...)
    */
   public Prerequisite( String className,
                        String methodName )
   {
      setup( className,
             methodName,
             null,
             IncludeSubtests.DONT_INCLUDE_SUBTESTS,
             Type.FIXED,
             PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
             null );
   }

   /**
    * Equivalent to<pre>
    *    Prerequisite( className,
    *                  methodName,
    *                  arguments,
    *                  IncludeSubtests.DONT_INCLUDE_SUBTESTS,
    *                  Type.FIXED,
    *                  PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
    *                  null )</pre>
    *
    * @param className
    *        Name of the prerequisite class
    * @param methodName
    *        Name of the prerequisite method
    * @param arguments
    *        Arguments.  If the prerequisite has no argument, this
    *        should be null
    * @see #Prerequisite(String,String,String,IncludeSubtests,Type,PrerequisiteMatchMultiplicity,String...)
    */
   public Prerequisite( String className,
                        String methodName,
                        String arguments )
   {
      setup( className,
             methodName,
             arguments,
             IncludeSubtests.DONT_INCLUDE_SUBTESTS,
             Type.FIXED,
             PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
             null );
   }

   /**
    * Equivalent to<pre>
    *    Prerequisite( className,
    *                  methodName,
    *                  arguments,
    *                  IncludeSubtests.DONT_INCLUDE_SUBTESTS,
    *                  Type.FIXED,
    *                  PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
    *                  testSetNames )</pre>
    *
    * @param className
    *        Name of the prerequisite class
    * @param methodName
    *        Name of the prerequisite method
    * @param arguments
    *        Arguments.  If the prerequisite has no argument, this
    *        should be null
    * @param testSetNames
    *        Limit the scope of the search for prerequisites to the
    *        specified test sets.  If this is null, search all test
    *        sets.  A null test set name indicates the current test
    *        set.
    * @see #Prerequisite(String,String,String,IncludeSubtests,Type,PrerequisiteMatchMultiplicity,String...)
    */
   public Prerequisite( String    className,
                        String    methodName,
                        String    arguments,
                        String... testSetNames )
   {
      setup( className,
             methodName,
             arguments,
             IncludeSubtests.DONT_INCLUDE_SUBTESTS,
             Type.FIXED,
             PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
             testSetNames );
   }

   /**
    * Equivalent to<pre>
    *    Prerequisite( className,
    *                  methodName,
    *                  null,
    *                  includeSubtests,
    *                  Type.FIXED,
    *                  PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
    *                  null )</pre>
    *
    * @param className
    *        Name of the prerequisite class
    * @param methodName
    *        Name of the prerequisite method
    * @param includeSubtests
    *        Indicates whether the prerequisite's subtests should
    *        also be considered prerequisites
    * @see #Prerequisite(String,String,String,IncludeSubtests,Type,PrerequisiteMatchMultiplicity,String...)
    */
   public Prerequisite( String          className,
                        String          methodName,
                        IncludeSubtests includeSubtests )
   {
      setup( className,
             methodName,
             null,
             includeSubtests,
             Type.FIXED,
             PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
             null );
   }

   /**
    * Equivalent to<pre>
    *    Prerequisite( className,
    *                  methodName,
    *                  null,
    *                  includeSubtests,
    *                  Type.FIXED,
    *                  PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
    *                  null )</pre>
    *
    * @param className
    *        Name of the prerequisite class
    * @param methodName
    *        Name of the prerequisite method
    * @param includeSubtests
    *        Indicates whether the prerequisite's subtests should
    *        also be considered prerequisites
    * @param testSetNames
    *        Limit the scope of the search for prerequisites to the
    *        specified test sets.  If this is null, search all test
    *        sets.  A null test set name indicates the current test
    *        set.
    * @see #Prerequisite(String,String,String,IncludeSubtests,Type,PrerequisiteMatchMultiplicity,String...)
    */
   public Prerequisite( String          className,
                        String          methodName,
                        IncludeSubtests includeSubtests,
                        String...       testSetNames )
   {
      setup( className,
             methodName,
             null,
             includeSubtests,
             Type.FIXED,
             PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
             testSetNames );
   }

   /**
    * Equivalent to<pre>
    *    Prerequisite( className,
    *                  methodName,
    *                  null,
    *                  IncludeSubtests.DONT_INCLUDE_SUBTESTS,
    *                  Type.FIXED,
    *                  prerequisiteMatchMultiplicity,
    *                  null )</pre>
    *
    * @param className
    *        Name of the prerequisite class
    * @param methodName
    *        Name of the prerequisite method
    * @param prerequisiteMatchMultiplicity
    *        Indicates the multiplicity
    * @see #Prerequisite(String,String,String,IncludeSubtests,Type,PrerequisiteMatchMultiplicity,String...)
    */
   public Prerequisite( String                        className,
                        String                        methodName,
                        PrerequisiteMatchMultiplicity prerequisiteMatchMultiplicity )
   {
      setup( className,
             methodName,
             null,
             IncludeSubtests.DONT_INCLUDE_SUBTESTS,
             Type.FIXED,
             prerequisiteMatchMultiplicity,
             null );
   }

   /**
    * Equivalent to<pre>
    *    Prerequisite( className,
    *                  methodName,
    *                  null,
    *                  includeSubtests,
    *                  Type.FIXED,
    *                  prerequisiteMatchMultiplicity,
    *                  null )</pre>
    *
    * @param className
    *        Name of the prerequisite class
    * @param methodName
    *        Name of the prerequisite method
    * @param includeSubtests
    *        Indicates whether the prerequisite's subtests should
    *        also be considered prerequisites
    * @param prerequisiteMatchMultiplicity
    *        Indicates the multiplicity
    * @see #Prerequisite(String,String,String,IncludeSubtests,Type,PrerequisiteMatchMultiplicity,String...)
    */
   public Prerequisite( String                        className,
                        String                        methodName,
                        IncludeSubtests               includeSubtests,
                        PrerequisiteMatchMultiplicity prerequisiteMatchMultiplicity )
   {
      setup( className,
             methodName,
             null,
             includeSubtests,
             Type.FIXED,
             prerequisiteMatchMultiplicity,
             null );
   }

   /**
    * Equivalent to<pre>
    *    Prerequisite( className,
    *                  methodName,
    *                  arguments,
    *                  IncludeSubtests.DONT_INCLUDE_SUBTESTS,
    *                  Type.FIXED,
    *                  prerequisiteMatchMultiplicity,
    *                  null )</pre>
    *
    * @param className
    *        Name of the prerequisite class
    * @param methodName
    *        Name of the prerequisite method
    * @param arguments
    *        Arguments.  If the prerequisite has no argument, this
    *        should be null
    * @param prerequisiteMatchMultiplicity
    *        Indicates the multiplicity
    * @see #Prerequisite(String,String,String,IncludeSubtests,Type,PrerequisiteMatchMultiplicity,String...)
    */
   public Prerequisite( String                        className,
                        String                        methodName,
                        String                        arguments,
                        PrerequisiteMatchMultiplicity prerequisiteMatchMultiplicity )
   {
      setup( className,
             methodName,
             arguments,
             IncludeSubtests.DONT_INCLUDE_SUBTESTS,
             Type.FIXED,
             prerequisiteMatchMultiplicity,
             null );
   }

   /**
    * Equivalent to<pre>
    *    Prerequisite( className,
    *                  methodName,
    *                  arguments,
    *                  includeSubtests,
    *                  Type.FIXED,
    *                  PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
    *                  null )</pre>
    *
    * @param className
    *        Name of the prerequisite class
    * @param methodName
    *        Name of the prerequisite method
    * @param arguments
    *        Arguments.  If the prerequisite has no argument, this
    *        should be null
    * @param includeSubtests
    *        Indicates whether the prerequisite's subtests should
    *        also be considered prerequisites
    * @see #Prerequisite(String,String,String,IncludeSubtests,Type,PrerequisiteMatchMultiplicity,String...)
    */
   public Prerequisite( String          className,
                        String          methodName,
                        String          arguments,
                        IncludeSubtests includeSubtests )
   {
      setup( className,
             methodName,
             arguments,
             includeSubtests,
             Type.FIXED,
             PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
             null );
   }

   /**
    * Equivalent to<pre>
    *    Prerequisite( className,
    *                  methodName,
    *                  arguments,
    *                  includeSubtests,
    *                  Type.FIXED,
    *                  PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
    *                  testSetNames )</pre>
    *
    * @param className
    *        Name of the prerequisite class
    * @param methodName
    *        Name of the prerequisite method
    * @param arguments
    *        Arguments.  If the prerequisite has no argument, this
    *        should be null
    * @param includeSubtests
    *        Indicates whether the prerequisite's subtests should
    *        also be considered prerequisites
    * @param testSetNames
    *        Limit the scope of the search for prerequisites to the
    *        specified test sets.  If this is null, search all test
    *        sets.  A null test set name indicates the current test
    *        set.
    * @see #Prerequisite(String,String,String,IncludeSubtests,Type,PrerequisiteMatchMultiplicity,String...)
    */
   public Prerequisite( String          className,
                        String          methodName,
                        String          arguments,
                        IncludeSubtests includeSubtests,
                        String...       testSetNames )
   {
      setup( className,
             methodName,
             arguments,
             includeSubtests,
             Type.FIXED,
             PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
             testSetNames );
   }

   /**
    * Equivalent to<pre>
    *    Prerequisite( className,
    *                  methodName,
    *                  arguments,
    *                  includeSubtests,
    *                  Type.FIXED,
    *                  prerequisiteMatchMultiplicity,
    *                  null )</pre>
    *
    * @param className
    *        Name of the prerequisite class
    * @param methodName
    *        Name of the prerequisite method
    * @param arguments
    *        Arguments.  If the prerequisite has no argument, this
    *        should be null
    * @param includeSubtests
    *        Indicates whether the prerequisite's subtests should
    *        also be considered prerequisites
    * @param prerequisiteMatchMultiplicity
    *        Indicates the multiplicity
    * @see #Prerequisite(String,String,String,IncludeSubtests,Type,PrerequisiteMatchMultiplicity,String...)
    */
   public Prerequisite( String                        className,
                        String                        methodName,
                        String                        arguments,
                        IncludeSubtests               includeSubtests,
                        PrerequisiteMatchMultiplicity prerequisiteMatchMultiplicity )
   {
      setup( className,
             methodName,
             arguments,
             includeSubtests,
             Type.FIXED,
             prerequisiteMatchMultiplicity,
             null );
   }

   /**
    * Equivalent to<pre>
    *    Prerequisite( className,
    *                  methodName,
    *                  null,
    *                  IncludeSubtests.DONT_INCLUDE_SUBTESTS,
    *                  type,
    *                  PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
    *                  null )</pre>
    *
    * @param className
    *        Name of the prerequisite class
    * @param methodName
    *        Name of the prerequisite method
    * @param type
    *        Indicates the prerequisite type
    * @see #Prerequisite(String,String,String,IncludeSubtests,Type,PrerequisiteMatchMultiplicity,String...)
    */
   public Prerequisite( String className,
                        String methodName,
                        Type   type )
   {
      setup( className,
             methodName,
             null,
             IncludeSubtests.DONT_INCLUDE_SUBTESTS,
             type,
             PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
             null );
   }

   /**
    * Equivalent to<pre>
    *    Prerequisite( className,
    *                  methodName,
    *                  null,
    *                  IncludeSubtests.DONT_INCLUDE_SUBTESTS,
    *                  type,
    *                  PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
    *                  testSetNames )</pre>
    *
    * @param className
    *        Name of the prerequisite class
    * @param methodName
    *        Name of the prerequisite method
    * @param type
    *        Indicates the prerequisite type
    * @param testSetNames
    *        Limit the scope of the search for prerequisites to the
    *        specified test sets.  If this is null, search all test
    *        sets.  A null test set name indicates the current test
    *        set.
    * @see #Prerequisite(String,String,String,IncludeSubtests,Type,PrerequisiteMatchMultiplicity,String...)
    */
   public Prerequisite( String    className,
                        String    methodName,
                        Type      type,
                        String... testSetNames )
   {
      setup( className,
             methodName,
             null,
             IncludeSubtests.DONT_INCLUDE_SUBTESTS,
             type,
             PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
             testSetNames );
   }

   /**
    * Equivalent to<pre>
    *    Prerequisite( className,
    *                  methodName,
    *                  arguments,
    *                  IncludeSubtests.DONT_INCLUDE_SUBTESTS,
    *                  type,
    *                  PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
    *                  null )</pre>
    *
    * @param className
    *        Name of the prerequisite class
    * @param methodName
    *        Name of the prerequisite method
    * @param arguments
    *        Arguments.  If the prerequisite has no argument, this
    *        should be null
    * @param type
    *        Indicates the prerequisite type
    * @see #Prerequisite(String,String,String,IncludeSubtests,Type,PrerequisiteMatchMultiplicity,String...)
    */
   public Prerequisite( String className,
                        String methodName,
                        String arguments,
                        Type   type )
   {
      setup( className,
             methodName,
             arguments,
             IncludeSubtests.DONT_INCLUDE_SUBTESTS,
             type,
             PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
             null );
   }

   /**
    * Equivalent to<pre>
    *    Prerequisite( className,
    *                  methodName,
    *                  arguments,
    *                  IncludeSubtests.DONT_INCLUDE_SUBTESTS,
    *                  type,
    *                  PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
    *                  testSetNames )</pre>
    *
    * @param className
    *        Name of the prerequisite class
    * @param methodName
    *        Name of the prerequisite method
    * @param arguments
    *        Arguments.  If the prerequisite has no argument, this
    *        should be null
    * @param type
    *        Indicates the prerequisite type
    * @param testSetNames
    *        Limit the scope of the search for prerequisites to the
    *        specified test sets.  If this is null, search all test
    *        sets.  A null test set name indicates the current test
    *        set.
    * @see #Prerequisite(String,String,String,IncludeSubtests,Type,PrerequisiteMatchMultiplicity,String...)
    */
   public Prerequisite( String    className,
                        String    methodName,
                        String    arguments,
                        Type      type,
                        String... testSetNames )
   {
      setup( className,
             methodName,
             arguments,
             IncludeSubtests.DONT_INCLUDE_SUBTESTS,
             type,
             PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
             testSetNames );
   }

   /**
    * Equivalent to<pre>
    *    Prerequisite( className,
    *                  methodName,
    *                  null,
    *                  includeSubtests,
    *                  type,
    *                  PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
    *                  null )</pre>
    *
    * @param className
    *        Name of the prerequisite class
    * @param methodName
    *        Name of the prerequisite method
    * @param includeSubtests
    *        Indicates whether the prerequisite's subtests should
    *        also be considered prerequisites
    * @param type
    *        Indicates the prerequisite type
    * @see #Prerequisite(String,String,String,IncludeSubtests,Type,PrerequisiteMatchMultiplicity,String...)
    */
   public Prerequisite( String          className,
                        String          methodName,
                        IncludeSubtests includeSubtests,
                        Type            type )
   {
      setup( className,
             methodName,
             null,
             includeSubtests,
             type,
             PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
             null );
   }

   /**
    * Equivalent to<pre>
    *    Prerequisite( className,
    *                  methodName,
    *                  null,
    *                  includeSubtests,
    *                  type,
    *                  PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
    *                  testSetNames )</pre>
    *
    * @param className
    *        Name of the prerequisite class
    * @param methodName
    *        Name of the prerequisite method
    * @param includeSubtests
    *        Indicates whether the prerequisite's subtests should
    *        also be considered prerequisites
    * @param type
    *        Indicates the prerequisite type
    * @param testSetNames
    *        Limit the scope of the search for prerequisites to the
    *        specified test sets.  If this is null, search all test
    *        sets.  A null test set name indicates the current test
    *        set.
    * @see #Prerequisite(String,String,String,IncludeSubtests,Type,PrerequisiteMatchMultiplicity,String...)
    */
   public Prerequisite( String          className,
                        String          methodName,
                        IncludeSubtests includeSubtests,
                        Type            type,
                        String...       testSetNames )
   {
      setup( className,
             methodName,
             null,
             includeSubtests,
             type,
             PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
             testSetNames );
   }

   /**
    * Equivalent to<pre>
    *    Prerequisite( className,
    *                  methodName,
    *                  null,
    *                  IncludeSubtests.DONT_INCLUDE_SUBTESTS,
    *                  type,
    *                  prerequisiteMatchMultiplicity,
    *                  null )</pre>
    *
    * @param className
    *        Name of the prerequisite class
    * @param methodName
    *        Name of the prerequisite method
    * @param type
    *        Indicates the prerequisite type
    * @param prerequisiteMatchMultiplicity
    *        Indicates the multiplicity
    * @see #Prerequisite(String,String,String,IncludeSubtests,Type,PrerequisiteMatchMultiplicity,String...)
    */
   public Prerequisite( String                        className,
                        String                        methodName,
                        Type                          type,
                        PrerequisiteMatchMultiplicity prerequisiteMatchMultiplicity )
   {
      setup( className,
             methodName,
             null,
             IncludeSubtests.DONT_INCLUDE_SUBTESTS,
             type,
             prerequisiteMatchMultiplicity,
             null );
   }

   /**
    * Equivalent to<pre>
    *    Prerequisite( className,
    *                  methodName,
    *                  null,
    *                  includeSubtests,
    *                  type,
    *                  prerequisiteMatchMultiplicity,
    *                  null )</pre>
    *
    * @param className
    *        Name of the prerequisite class
    * @param methodName
    *        Name of the prerequisite method
    * @param includeSubtests
    *        Indicates whether the prerequisite's subtests should
    *        also be considered prerequisites
    * @param type
    *        Indicates the prerequisite type
    * @param prerequisiteMatchMultiplicity
    *        Indicates the multiplicity
    * @see #Prerequisite(String,String,String,IncludeSubtests,Type,PrerequisiteMatchMultiplicity,String...)
    */
   public Prerequisite( String                        className,
                        String                        methodName,
                        IncludeSubtests               includeSubtests,
                        Type                          type,
                        PrerequisiteMatchMultiplicity prerequisiteMatchMultiplicity )
   {
      setup( className,
             methodName,
             null,
             includeSubtests,
             type,
             prerequisiteMatchMultiplicity,
             null );
   }

   /**
    * Equivalent to<pre>
    *    Prerequisite( className,
    *                  methodName,
    *                  arguments,
    *                  IncludeSubtests.DONT_INCLUDE_SUBTESTS,
    *                  type,
    *                  prerequisiteMatchMultiplicity,
    *                  null )</pre>
    *
    * @param className
    *        Name of the prerequisite class
    * @param methodName
    *        Name of the prerequisite method
    * @param arguments
    *        Arguments.  If the prerequisite has no argument, this
    *        should be null
    * @param type
    *        Indicates the prerequisite type
    * @param prerequisiteMatchMultiplicity
    *        Indicates the multiplicity
    * @see #Prerequisite(String,String,String,IncludeSubtests,Type,PrerequisiteMatchMultiplicity,String...)
    */
   public Prerequisite( String                        className,
                        String                        methodName,
                        String                        arguments,
                        Type                          type,
                        PrerequisiteMatchMultiplicity prerequisiteMatchMultiplicity )
   {
      setup( className,
             methodName,
             arguments,
             IncludeSubtests.DONT_INCLUDE_SUBTESTS,
             type,
             prerequisiteMatchMultiplicity,
             null );
   }

   /**
    * Equivalent to<pre>
    *    Prerequisite( className,
    *                  methodName,
    *                  arguments,
    *                  includeSubtests,
    *                  type,
    *                  PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
    *                  null )</pre>
    *
    * @param className
    *        Name of the prerequisite class
    * @param methodName
    *        Name of the prerequisite method
    * @param arguments
    *        Arguments.  If the prerequisite has no argument, this
    *        should be null
    * @param includeSubtests
    *        Indicates whether the prerequisite's subtests should
    *        also be considered prerequisites
    * @param type
    *        Indicates the prerequisite type
    * @see #Prerequisite(String,String,String,IncludeSubtests,Type,PrerequisiteMatchMultiplicity,String...)
    */
   public Prerequisite( String          className,
                        String          methodName,
                        String          arguments,
                        IncludeSubtests includeSubtests,
                        Type            type )
   {
      setup( className,
             methodName,
             arguments,
             includeSubtests,
             type,
             PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
             null );
   }

   /**
    * Equivalent to<pre>
    *    Prerequisite( className,
    *                  methodName,
    *                  arguments,
    *                  includeSubtests,
    *                  type,
    *                  PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
    *                  testSetNames )</pre>
    *
    * @param className
    *        Name of the prerequisite class
    * @param methodName
    *        Name of the prerequisite method
    * @param arguments
    *        Arguments.  If the prerequisite has no argument, this
    *        should be null
    * @param includeSubtests
    *        Indicates whether the prerequisite's subtests should
    *        also be considered prerequisites
    * @param type
    *        Indicates the prerequisite type
    * @param testSetNames
    *        Limit the scope of the search for prerequisites to the
    *        specified test sets.  If this is null, search all test
    *        sets.  A null test set name indicates the current test
    *        set.
    * @see #Prerequisite(String,String,String,IncludeSubtests,Type,PrerequisiteMatchMultiplicity,String...)
    */
   public Prerequisite( String          className,
                        String          methodName,
                        String          arguments,
                        IncludeSubtests includeSubtests,
                        Type            type,
                        String...       testSetNames )
   {
      setup( className,
             methodName,
             arguments,
             includeSubtests,
             type,
             PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
             testSetNames );
   }

   /**
    * Equivalent to<pre>
    *    Prerequisite( className,
    *                  methodName,
    *                  arguments,
    *                  includeSubtests,
    *                  type,
    *                  PrerequisiteMatchMultiplicity.INCLUDE_FIRST_MATCHING_METHOD,
    *                  null )</pre>
    *
    * @param className
    *        Name of the prerequisite class
    * @param methodName
    *        Name of the prerequisite method
    * @param arguments
    *        Arguments.  If the prerequisite has no argument, this
    *        should be null
    * @param includeSubtests
    *        Indicates whether the prerequisite's subtests should
    *        also be considered prerequisites
    * @param type
    *        Indicates the prerequisite type
    * @param prerequisiteMatchMultiplicity
    *        Indicates the multiplicity
    * @see #Prerequisite(String,String,String,IncludeSubtests,Type,PrerequisiteMatchMultiplicity,String...)
    */
   public Prerequisite( String                        className,
                        String                        methodName,
                        String                        arguments,
                        IncludeSubtests               includeSubtests,
                        Type                          type,
                        PrerequisiteMatchMultiplicity prerequisiteMatchMultiplicity )
   {
      setup( className,
             methodName,
             arguments,
             includeSubtests,
             type,
             prerequisiteMatchMultiplicity,
             null );
   }

   /**
    * @param className
    *        Name of the prerequisite class
    * @param methodName
    *        Name of the prerequisite method
    * @param arguments
    *        Arguments.  If the prerequisite has no argument, this
    *        should be null
    * @param includeSubtests
    *        Indicates whether the prerequisite's subtests should
    *        also be considered prerequisites
    * @param type
    *        Indicates the prerequisite type
    * @param prerequisiteMatchMultiplicity
    *        Indicates the multiplicity
    * @param testSetNames
    *        Limit the scope of the search for prerequisites to the
    *        specified test sets.  If this is null, search all test
    *        sets.  A null test set name indicates the current test
    *        set.
    */
   public Prerequisite( String                        className,
                        String                        methodName,
                        String                        arguments,
                        IncludeSubtests               includeSubtests,
                        Type                          type,
                        PrerequisiteMatchMultiplicity prerequisiteMatchMultiplicity,
                        String...                     testSetNames )
   {
      setup( className,
             methodName,
             arguments,
             includeSubtests,
             type,
             prerequisiteMatchMultiplicity,
             testSetNames );
   }

   final private void setup( String                        className,
                             String                        methodName,
                             String                        arguments,
                             IncludeSubtests               includeSubtests,
                             Type                          type,
                             PrerequisiteMatchMultiplicity prerequisiteMatchMultiplicity,
                             String                        testSetNames[] )
   {
      _className = className;
      _methodName = methodName;
      _arguments = arguments;
      _includeSubtests = includeSubtests;
      _type = type;
      _prerequisiteMatchMultiplicity = prerequisiteMatchMultiplicity;
      _satisfied = type._previouslySatisified;
      if ( testSetNames == null )
      {
         _testSetNames = null;
      }
      else
      {
         _testSetNames = new java.util.ArrayList<String>();
         for ( String testSetName : testSetNames )
         {
            _testSetNames.add( testSetName );
         }
      }
   }

   Prerequisite( java.io.DataInputStream dataInputStream )
      throws java.io.IOException
   {
      int numberOfElements;
      int index;

      if ( dataInputStream.readInt() > CLASS_PERSISTENCE_VERSION )
      {
         throw new java.io.IOException( "The database is from a newer version of Undercamber" );
      }
      if ( !(dataInputStream.readUTF().equals(CLASS_PERSISTENCE_BRANCH)) )
      {
         throw new java.io.IOException( "The database is from an unrecognized branch of Undercamber" );
      }

      if ( dataInputStream.readBoolean() )
      {
         _className = dataInputStream.readUTF();
      }
      else
      {
         _className = null;
      }

      if ( dataInputStream.readBoolean() )
      {
         _methodName = dataInputStream.readUTF();
      }
      else
      {
         _methodName = null;
      }

      if ( dataInputStream.readBoolean() )
      {
         _arguments = dataInputStream.readUTF();
      }
      else
      {
         _arguments = null;
      }

      _prerequisiteMatchMultiplicity = PrerequisiteMatchMultiplicity.values()[ dataInputStream.readInt() ];

      if ( dataInputStream.readBoolean() )
      {
         _testSetNames = new java.util.ArrayList<String>();
         numberOfElements = dataInputStream.readInt();
         for ( index=0; index<numberOfElements; index++ )
         {
            if ( dataInputStream.readBoolean() )
            {
               _testSetNames.add( dataInputStream.readUTF() );
            }
            else
            {
               _testSetNames.add( null );
            }
         }
      }
      else
      {
         _testSetNames = null;
      }
   }

   final void setSatisfied()
   {
      _satisfied = true;
   }

   final boolean isSatisfied()
   {
      return _satisfied;
   }

   final boolean matches( TestData candidatePrerequisiteTestData,
                          TestSet  currentTestSet )
   {
      String prerequisiteTestSetName;

      if ( !(_className.equals(candidatePrerequisiteTestData.getCallingClassName())) )
      {
         return false;
      }

      if ( !(_methodName.equals(candidatePrerequisiteTestData.getCallingMethodName())) )
      {
         return false;
      }

      if ( (_arguments==null) != (candidatePrerequisiteTestData.getArguments()==null) )
      {
         return false;
      }

      if ( _arguments != null )
      {
         if ( !(_arguments.equals(candidatePrerequisiteTestData.getArguments())) )
         {
            return false;
         }
      }

      if ( _testSetNames == null )
      {
         return true;
      }
      else
      {
         prerequisiteTestSetName = candidatePrerequisiteTestData.getTestSet().getTestSetName();

         for ( String testSetName : _testSetNames )
         {
            if ( testSetName == null )
            {
               if ( candidatePrerequisiteTestData.getTestSet() == currentTestSet )
               {
                  return true;
               }
            }
            else
            {
               if ( prerequisiteTestSetName.equals(testSetName) )
               {
                  return true;
               }
            }
         }

         return false;
      }
   }

   final Type getType()
   {
      return _type;
   }

   final String[] getTestSetNames()
   {
      String testSetNames[];

      testSetNames = new String[ _testSetNames.size() ];
      testSetNames = _testSetNames.toArray( testSetNames );

      return testSetNames;
   }

   final boolean includeSubtests()
   {
      return _includeSubtests.includeSubtests();
   }

   final boolean requiresAllPreviousMatchingMethods()
   {
      return _prerequisiteMatchMultiplicity.requiresAllPreviousMatchingMethods();
   }

   final void writeToXML( String              margin,
                          java.io.PrintStream printStream )
   {
      printStream.println( margin + "<prerequisiteDeclaration>" );
      printStream.println( margin + "   <className>" + Utilities.escapeForXML(_className) + "</className>" );
      printStream.println( margin + "   <methodName>" + Utilities.escapeForXML(_methodName) + "</methodName>" );
      if ( _arguments != null )
      {
         printStream.println( margin + "   <argument>" + Utilities.escapeForXML(_arguments) + "</argument>" );
      }
      printStream.println( margin + "   <includeSubtests>" + Utilities.escapeForXML(_includeSubtests.name()) + "</includeSubtests>" );
      printStream.println( margin + "   <type>" + Utilities.escapeForXML(_type.name()) + "</type>" );
      printStream.println( margin + "   <multiplicity>" + Utilities.escapeForXML(_prerequisiteMatchMultiplicity.name()) + "</multiplicity>" );
      printStream.println( margin + "   <testSets>" );
      if ( _testSetNames != null )
      {
         for ( String testSetName : _testSetNames )
         {
            if ( testSetName == null )
            {
               printStream.println( margin + "      <testSet></testSet>" );
            }
            else
            {
               printStream.println( margin + "      <testSet>" + Utilities.escapeForXML(testSetName) + "</testSet>" );
            }
         }
      }
      printStream.println( margin + "   </testSets>" );
      printStream.println( margin + "</prerequisiteDeclaration>" );
   }

   final void write( java.io.DataOutputStream dataOutputStream )
      throws java.io.IOException
   {
      dataOutputStream.writeInt( CLASS_PERSISTENCE_VERSION );
      dataOutputStream.writeUTF( CLASS_PERSISTENCE_BRANCH );

      dataOutputStream.writeBoolean( _className != null );
      if ( _className != null )
      {
         dataOutputStream.writeUTF( _className );
      }

      dataOutputStream.writeBoolean( _methodName != null );
      if ( _methodName != null )
      {
         dataOutputStream.writeUTF( _methodName );
      }

      dataOutputStream.writeBoolean( _arguments != null );
      if ( _arguments != null )
      {
         dataOutputStream.writeUTF( _arguments );
      }

      dataOutputStream.writeInt( _prerequisiteMatchMultiplicity.ordinal() );

      dataOutputStream.writeBoolean( _testSetNames != null );
      if ( _testSetNames != null )
      {
         dataOutputStream.writeInt( _testSetNames.size() );
         for ( String testSetName : _testSetNames )
         {
            dataOutputStream.writeBoolean( testSetName != null );
            if ( testSetName != null )
            {
               dataOutputStream.writeUTF( testSetName );
            }
         }
      }
   }

   final public String toString()
   {
      String       testSetName0;
      String       testSetName1;
      StringBuffer stringBuffer;
      int          maximumIndex;
      int          index;
      String       testSetName;

      if ( _testSetNames == null )
      {
         return _className + "." + _methodName + "(" + (_arguments==null?"":_arguments) + ")";
      }
      else
      {
         if ( _testSetNames.size() == 0 )
         {
            return _className + "." + _methodName + "(" + (_arguments==null?"":_arguments) + ") in no test sets (huh?)";
         }
         else if ( _testSetNames.size() == 1 )
         {
            testSetName0 = _testSetNames.get(0);
            if ( testSetName0 == null )
            {
               testSetName0 = "<current>";
            }
            return _className + "." + _methodName + "(" + (_arguments==null?"":_arguments) + ") in test set " + testSetName0;
         }
         else if ( _testSetNames.size() == 2 )
         {
            testSetName0 = _testSetNames.get(0);
            if ( testSetName0 == null )
            {
               testSetName0 = "<current>";
            }
            testSetName1 = _testSetNames.get(1);
            if ( testSetName1 == null )
            {
               testSetName1 = "<current>";
            }
            return _className + "." + _methodName + "(" + (_arguments==null?"":_arguments) + ") in test sets " + testSetName0 + " and " + testSetName1;
         }
         else
         {
            stringBuffer = new StringBuffer();
            testSetName0 = _testSetNames.get(0);
            if ( testSetName0 == null )
            {
               testSetName0 = "<current>";
            }
            stringBuffer.append( _className ).append( "." ).append( _methodName ).append( "(" ).append( _arguments == null ? "" : _arguments ).append( ") in test sets " + testSetName0 );
            maximumIndex = _testSetNames.size() - 1;
            for ( index=1; index<maximumIndex; index++ )
            {
               testSetName = _testSetNames.get( index );
               if ( testSetName == null )
               {
                  testSetName = "<current>";
               }
               stringBuffer.append( ", " ).append( testSetName );
            }
            testSetName = _testSetNames.get( maximumIndex );
            if ( testSetName == null )
            {
               testSetName = "<current>";
            }
            stringBuffer.append( ", and " ).append( testSetName );
            return stringBuffer.toString();
         }
      }
   }

   /**
    * Indicates whether the Prerequisite's subtests should also be
    * considered prerequisites
    */
   public static enum IncludeSubtests
   {
      /**
       * The Prerequisite's subtests should also be considered
       * prerequisites
       */
      INCLUDE_SUBTESTS      ( true  ),
      /**
       * The Prerequisite's subtests should not be considered
       * prerequisites
       */
      DONT_INCLUDE_SUBTESTS ( false );

      private boolean _includeSubtests;

      IncludeSubtests( boolean includeSubtests )
      {
         _includeSubtests = includeSubtests;
      }

      final boolean includeSubtests()
      {
         return _includeSubtests;
      }
   }

   /**
    * Indicates how to handle identical prerequisites
    */
   public static enum PrerequisiteMatchMultiplicity
   {
      /**
       * All matching tests should be considered prerequisites
       */
      INCLUDE_ALL_MATCHING_METHODS  ( true  ),
      /**
       * Only one test should be considered a prerequisite
       */
      INCLUDE_FIRST_MATCHING_METHOD ( false );

      private boolean _requiresAllPreviousMatchingMethods;

      PrerequisiteMatchMultiplicity( boolean requiresAllPreviousMatchingMethods )
      {
         _requiresAllPreviousMatchingMethods = requiresAllPreviousMatchingMethods;
      }

      boolean requiresAllPreviousMatchingMethods()
      {
         return _requiresAllPreviousMatchingMethods;
      }
   }

   /**
    * Indicates to Undercamber the type of the prerequisite.<br>
    * <br>
    * This conditional types are needed for report generation when
    * determining which tests do not contribute to a requirement.
    */
   public static enum Type
   {
      /**
       * Indicates a prerequisite that must be run under all
       * circumstances.
       */
      FIXED                                ( false, "Always run" ),
      /**
       * Indicates a prerequisite that may or may not be run in the
       * general case, but should be run because the needed setup has
       * not been performed prior to running the test.
       */
      CONDITIONAL_NOT_PREVIOUSLY_SATISFIED ( false, "Conditional; not yet satisfied" ),
      /**
       * Indicates a prerequisite that may or may not be run in the
       * general case, but should be not run because the needed setup
       * has been performed prior to running the test.
       */
      CONDITIONAL_PREVIOUSLY_SATISFIED     ( true,  "Conditional; previously satisfied" );

      boolean _previouslySatisified;
      String  _displayText;

      Type( boolean previouslySatisifed,
            String  displayText )
      {
         _previouslySatisified = previouslySatisifed;
         _displayText = displayText;
      }

      boolean previouslySatisified()
      {
         return _previouslySatisified;
      }

      String getDisplayText()
      {
         return _displayText;
      }

      /**
       * A convenience method to retrieve the appropriate conditional
       * type.
       *
       * @param prerequisitesShouldBeRun
       *        <table summary="argument table">
       *           <tr>
       *              <td>&nbsp;&nbsp;&nbsp;true:</td>
       *              <td>&nbsp;&nbsp;&nbsp;The needed setup has not
       *              been performed prior to running the tests</td>
       *           </tr>
       *           <tr>
       *              <td>&nbsp;&nbsp;&nbsp;false:</td>
       *              <td>&nbsp;&nbsp;&nbsp;The needed setup has been
       *              performed prior to running the tests</td>
       *           </tr>
       *        </table>
       *
       * @return <table summary="return value table">
       *            <tr>
       *               <th>&nbsp;&nbsp;&nbsp;<u>If prerequisitesShouldBeRun is this</u></th>
       *               <th>&nbsp;&nbsp;&nbsp;<u>Return this</u></th>
       *            </tr>
       *            <tr>
       *               <td>&nbsp;&nbsp;&nbsp;true</td>
       *               <td>&nbsp;&nbsp;&nbsp;CONDITIONAL_NOT_PREVIOUSLY_SATISFIED</td>
       *            </tr>
       *            <tr>
       *               <td>&nbsp;&nbsp;&nbsp;false</td>
       *               <td>&nbsp;&nbsp;&nbsp;CONDITIONAL_PREVIOUSLY_SATISFIED</td>
       *            </tr>
       *         </table>
       */
      public static Type getConditionalType( boolean prerequisitesShouldBeRun )
      {
         if ( prerequisitesShouldBeRun )
         {
            return CONDITIONAL_NOT_PREVIOUSLY_SATISFIED;
         }
         else
         {
            return CONDITIONAL_PREVIOUSLY_SATISFIED;
         }
      }
   }
}

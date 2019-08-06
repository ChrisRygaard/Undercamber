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
 * Indicates the results of a single test
 *
 * @see Requirement#testComplete
 */
final public class TestData
   implements Comparable<TestData>
{
   final private static int    CLASS_PERSISTENCE_VERSION = 0;
   final private static String CLASS_PERSISTENCE_BRANCH  = "";

   private TestData                         _parent;
   private TestManager                      _testManager;
   private StackTraceElement                _callingStackTraceElement;
   private String                           _arguments;
   private SubtestSequencingMode            _subtestSequencingMode;
   private SubtestContinuationMode          _subtestContinuationMode;
   private TestState                        _testState;
   private boolean                          _guiRunFlag;
   private boolean                          _alternateRunFlag;
   private int                              _sequenceIndex;
   private java.util.List<Throwable>        _exceptions;
   private java.util.List<TestData>         _children;
   private java.util.List<TestDataListener> _listeners;
   private boolean                          _selectionExpanded;
   private boolean                          _resultsExpanded;
   private java.util.List<Prerequisite>     _prerequisites;
   private java.util.Set<TestData>          _fixedPrerequisites;
   private java.util.Set<TestData>          _unsatisfiedConditionalPrerequisites;
   private java.util.Set<TestData>          _satisfiedConditionalPrerequisites;
   private java.util.List<Integer>          _fixedPrerequisiteIndices;
   private java.util.List<Integer>          _unsatisfiedConditionalPrerequisiteIndices;
   private java.util.List<Integer>          _satisfiedConditionalPrerequisiteIndices;
   private java.util.Set<TestData>          _fixedDependents;
   private java.util.Set<TestData>          _unsatisfiedConditionalDependents;
   private java.util.Set<TestData>          _satisfiedConditionalDependents;
   private java.util.List<Integer>          _fixedDependentIndices;
   private java.util.List<Integer>          _unsatisfiedConditionalDependentIndices;
   private java.util.List<Integer>          _satisfiedConditionalDependentIndices;
   private TestSet                          _testSet;
   private Tag                              _tags[];
   private java.util.List<Requirement>      _requirements;
   private java.util.List<RequirementData>  _requirementsData;
   private java.util.List<RequirementData>  _referencedRequirementsData;
   private long                             _startTime;
   private long                             _stopTime;
   private long                             _cpuStartTime;
   private long                             _cpuStopTime;
   private DependencyWindow                 _dependencyWindow;
   private ErrorWindow                      _errorWindow;
   private TestDetailsWindow                _testDetailsWindow;
   private RequirementsWindow               _requirementsWindow;
   private RequirementsWindow               _requirementsResultsWindow;
   private MainTableTreeItem                _mainTableTreeItem;

   TestData( java.util.List<TestSet> testSets )
   {
      _testManager = null;
      _parent = null;
      _exceptions = new java.util.ArrayList<Throwable>();
      _listeners = new java.util.ArrayList<TestDataListener>();
      _requirements = new java.util.ArrayList<Requirement>();
      _requirementsData = new java.util.ArrayList<RequirementData>();
      _callingStackTraceElement = null;
      _arguments = null;
      _testState = TestState.UNINITIALIZED;
      _children = new java.util.ArrayList<TestData>();
      for ( TestSet testSet : testSets )
      {
         _children.add( testSet.getTestData() );
      }
      _guiRunFlag = false;
      _alternateRunFlag = true;
      _selectionExpanded = true;
      _resultsExpanded = true;
      _subtestSequencingMode = SubtestSequencingMode.SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR;
      _sequenceIndex = 0;
      _prerequisites = new java.util.ArrayList<Prerequisite>();
      _fixedPrerequisites = new java.util.HashSet<TestData>();
      _unsatisfiedConditionalPrerequisites = new java.util.HashSet<TestData>();
      _satisfiedConditionalPrerequisites = new java.util.HashSet<TestData>();
      _fixedDependents = new java.util.HashSet<TestData>();
      _unsatisfiedConditionalDependents = new java.util.HashSet<TestData>();
      _satisfiedConditionalDependents = new java.util.HashSet<TestData>();
      _fixedDependents = new java.util.HashSet<TestData>();
      _dependencyWindow = null;
      _tags = new Tag[ 0 ];
      _startTime = -1L;
      _stopTime = -1L;
      _cpuStartTime = -1L;
      _cpuStopTime = -1L;
   }

   TestData( TestManager testManager,
             TestData    parent,
             TestSet     testSet )
   {
      _testManager = testManager;
      _parent = parent;
      _exceptions = new java.util.ArrayList<Throwable>();
      _listeners = new java.util.ArrayList<TestDataListener>();
      _requirements = new java.util.ArrayList<Requirement>();
      _requirementsData = new java.util.ArrayList<RequirementData>();
      _callingStackTraceElement = null;
      _testState = TestState.UNINITIALIZED;
      _arguments = null;
      _children = new java.util.ArrayList<TestData>();
      _guiRunFlag = false;
      _alternateRunFlag = false;
      _selectionExpanded = true;
      _resultsExpanded = true;
      _sequenceIndex = -1;
      _testSet = testSet;
      _fixedDependents = new java.util.HashSet<TestData>();
      _unsatisfiedConditionalDependents = new java.util.HashSet<TestData>();
      _satisfiedConditionalDependents = new java.util.HashSet<TestData>();
      _fixedPrerequisites = new java.util.HashSet<TestData>();
      _unsatisfiedConditionalPrerequisites = new java.util.HashSet<TestData>();
      _satisfiedConditionalPrerequisites = new java.util.HashSet<TestData>();
      _dependencyWindow = null;
      _startTime = -1L;
      _stopTime = -1L;
      _cpuStartTime = -1L;
      _cpuStopTime = -1L;
   }

   TestData( java.io.DataInputStream dataInputStream,
             TestData                parent,
             TestSet                 testSet )
      throws java.io.IOException
   {
      int  arraySize;
      byte serializedObject[];
      int  elementCount;
      int  index;

      if ( dataInputStream.readInt() > CLASS_PERSISTENCE_VERSION )
      {
         throw new java.io.IOException( "The database is from a newer version of Undercamber" );
      }
      if ( !(dataInputStream.readUTF().equals(CLASS_PERSISTENCE_BRANCH)) )
      {
         throw new java.io.IOException( "The database is from an unrecognized branch of Undercamber" );
      }

      _parent = parent;
      _testManager = null;
      _testSet = testSet;
      _fixedDependents = new java.util.HashSet<TestData>();
      _listeners = new java.util.ArrayList<TestDataListener>();
      _unsatisfiedConditionalDependents = new java.util.HashSet<TestData>();
      _satisfiedConditionalDependents = new java.util.HashSet<TestData>();
      _fixedPrerequisites = new java.util.HashSet<TestData>();
      _unsatisfiedConditionalPrerequisites = new java.util.HashSet<TestData>();
      _satisfiedConditionalPrerequisites = new java.util.HashSet<TestData>();
      _requirements = new java.util.ArrayList<Requirement>();
      _requirementsData = new java.util.ArrayList<RequirementData>();
      _dependencyWindow = null;

      arraySize = dataInputStream.readInt();
      serializedObject = new byte[ arraySize ];
      dataInputStream.readFully( serializedObject );
      try ( java.io.ByteArrayInputStream byteArrayInputStream = new java.io.ByteArrayInputStream(serializedObject) )
      {
         try ( java.io.ObjectInputStream objectInputStream = new java.io.ObjectInputStream(byteArrayInputStream) )
         {
            try
            {
               _callingStackTraceElement = (StackTraceElement)( objectInputStream.readObject() );
            }
            catch ( ClassNotFoundException classNotFoundException )
            {
               throw new java.io.IOException( "Could not read calling StackTraceElement",
                                              classNotFoundException );
            }
         }
      }

      if ( dataInputStream.readBoolean() )
      {
         _arguments = dataInputStream.readUTF();
      }
      else
      {
         _arguments = null;
      }

      if ( dataInputStream.readBoolean() )
      {
         elementCount = dataInputStream.readInt();
         _prerequisites = new java.util.ArrayList<Prerequisite>( elementCount );
         for ( index=0; index<elementCount; index++ )
         {
            _prerequisites.add( new Prerequisite(dataInputStream) );
         }
      }

      if ( dataInputStream.readBoolean() )
      {
         elementCount = dataInputStream.readInt();
         _fixedPrerequisiteIndices = new java.util.ArrayList<Integer>( elementCount );
         for ( index=0; index<elementCount; index++ )
         {
            _fixedPrerequisiteIndices.add( dataInputStream.readInt() );
         }
      }

      if ( dataInputStream.readBoolean() )
      {
         elementCount = dataInputStream.readInt();
         _unsatisfiedConditionalPrerequisiteIndices = new java.util.ArrayList<Integer>( elementCount );
         for ( index=0; index<elementCount; index++ )
         {
            _unsatisfiedConditionalPrerequisiteIndices.add( dataInputStream.readInt() );
         }
      }

      if ( dataInputStream.readBoolean() )
      {
         elementCount = dataInputStream.readInt();
         _satisfiedConditionalPrerequisiteIndices = new java.util.ArrayList<Integer>( elementCount );
         for ( index=0; index<elementCount; index++ )
         {
            _satisfiedConditionalPrerequisiteIndices.add( dataInputStream.readInt() );
         }
      }

      if ( dataInputStream.readBoolean() )
      {
         elementCount = dataInputStream.readInt();
         _fixedDependentIndices = new java.util.ArrayList<Integer>( elementCount );
         for ( index=0; index<elementCount; index++ )
         {
            _fixedDependentIndices.add( dataInputStream.readInt() );
         }
      }

      if ( dataInputStream.readBoolean() )
      {
         elementCount = dataInputStream.readInt();
         _unsatisfiedConditionalDependentIndices = new java.util.ArrayList<Integer>( elementCount );
         for ( index=0; index<elementCount; index++ )
         {
            _unsatisfiedConditionalDependentIndices.add( dataInputStream.readInt() );
         }
      }

      if ( dataInputStream.readBoolean() )
      {
         elementCount = dataInputStream.readInt();
         _satisfiedConditionalDependentIndices = new java.util.ArrayList<Integer>( elementCount );
         for ( index=0; index<elementCount; index++ )
         {
            _satisfiedConditionalDependentIndices.add( dataInputStream.readInt() );
         }
      }

      if ( dataInputStream.readBoolean() )
      {
         _subtestSequencingMode = SubtestSequencingMode.values()[ dataInputStream.readInt() ];
      }
      else
      {
         _subtestSequencingMode = null;
      }

      if ( dataInputStream.readBoolean() )
      {
         _subtestContinuationMode = SubtestContinuationMode.values()[ dataInputStream.readInt() ];
      }
      else
      {
         _subtestContinuationMode = null;
      }

      _testState = TestState.values()[ dataInputStream.readInt() ];

      if ( dataInputStream.readBoolean() )
      {
         _tags = new Tag[ dataInputStream.readInt() ];
         for ( index=0; index<_tags.length; index++ )
         {
            _tags[ index ] = new Tag( dataInputStream );
         }
      }
      else
      {
         _tags = null;
      }

      _guiRunFlag = dataInputStream.readBoolean();
      _alternateRunFlag = dataInputStream.readBoolean();
      _sequenceIndex = dataInputStream.readInt();
      _selectionExpanded = dataInputStream.readBoolean();
      _resultsExpanded = dataInputStream.readBoolean();

      _exceptions = new java.util.ArrayList<Throwable>();
      elementCount = dataInputStream.readInt();
      for ( index=0; index<elementCount; index++ )
      {
         arraySize = dataInputStream.readInt();
         serializedObject = new byte[ arraySize ];
         dataInputStream.readFully( serializedObject );
         try ( java.io.ByteArrayInputStream byteArrayInputStream = new java.io.ByteArrayInputStream(serializedObject) )
         {
            try ( java.io.ObjectInputStream objectInputStream = new java.io.ObjectInputStream(byteArrayInputStream) )
            {
               try
               {
                  _exceptions.add( (Throwable)(objectInputStream.readObject()) );
               }
               catch ( ClassNotFoundException classNotFoundException )
               {
                  throw new java.io.IOException( "Could not read exception",
                                                 classNotFoundException );
               }
            }
         }
      }

      _startTime = dataInputStream.readLong();
      _stopTime = dataInputStream.readLong();
      _cpuStartTime = dataInputStream.readLong();
      _cpuStopTime = dataInputStream.readLong();

      elementCount = dataInputStream.readInt();
      for ( index=0; index<elementCount; index++ )
      {
         _requirementsData.add( new RequirementData(dataInputStream) );
      }

      _children = new java.util.ArrayList<TestData>();
      elementCount = dataInputStream.readInt();
      for ( index=0; index<elementCount; index++ )
      {
         _children.add( new TestData(dataInputStream,
                                     this,
                                     _testSet) );
      }
   }

   final void initialize( StackTraceElement       callingStackTraceElement,
                          SubtestSequencingMode   subtestSequencingMode,
                          SubtestContinuationMode subtestContinuationMode,
                          String                  arguments,
                          Prerequisite            prerequisites[],
                          Tag                     tags[],
                          Requirement             requirements[] )
   {
      _testState = TestState.INITIALIZED;

      _callingStackTraceElement = callingStackTraceElement;

      _subtestSequencingMode = subtestSequencingMode;

      _subtestContinuationMode = subtestContinuationMode;

      _arguments = arguments;

      _tags = new Tag[ tags.length ];
      System.arraycopy( tags, 0, _tags, 0, tags.length );

      _prerequisites = new java.util.ArrayList<>();
      if ( prerequisites != null )
      {
         for ( Prerequisite prerequisite : prerequisites )
         {
            _prerequisites.add( prerequisite );
         }
      }

      for ( Requirement requirement : requirements )
      {
         _requirements.add( requirement );
         _requirementsData.add( new RequirementData(requirement) );
      }

      notifyListeners();
   }

   final void transferConfigurationFromPreviousRun( TestData that )
   {
      int      index;
      TestData thatChild;
      TestData thisChild;

      _guiRunFlag = that._guiRunFlag;
      _alternateRunFlag = that._alternateRunFlag;
      _selectionExpanded = that._selectionExpanded;
      _resultsExpanded = that._resultsExpanded;
      _sequenceIndex = that._sequenceIndex;

      if ( childrenMatch(that) )
      {
         for ( index=0; index<that._children.size(); index++ )
         {
            thatChild = that._children.get( index );
            thisChild = _children.get( index );
            thisChild.transferConfigurationFromPreviousRun( thatChild );
         }
      }
      else
      {
         for ( index=0; index<that._children.size(); index++ )
         {
            thatChild = that._children.get( index );
            thisChild = getChild( thatChild._callingStackTraceElement,
                                  thatChild._arguments );
            if ( thisChild != null )
            {
               thisChild.transferConfigurationFromPreviousRun( thatChild );
            }
         }
      }
   }

   final void transferConfigurationFrom( TestData that )
   {
      if ( that != null )
      {
         _sequenceIndex = that._sequenceIndex;

         _fixedPrerequisiteIndices = new java.util.ArrayList<Integer>();
         _fixedPrerequisiteIndices.addAll( that._fixedPrerequisiteIndices );

         _satisfiedConditionalPrerequisiteIndices = new java.util.ArrayList<Integer>();
         _satisfiedConditionalPrerequisiteIndices.addAll( that._satisfiedConditionalPrerequisiteIndices );

         _unsatisfiedConditionalPrerequisiteIndices = new java.util.ArrayList<Integer>();
         _unsatisfiedConditionalPrerequisiteIndices.addAll( that._unsatisfiedConditionalPrerequisiteIndices );

         _fixedDependentIndices = new java.util.ArrayList<Integer>();
         _fixedDependentIndices.addAll( that._fixedDependentIndices );

         _satisfiedConditionalPrerequisiteIndices = new java.util.ArrayList<Integer>();
         _satisfiedConditionalPrerequisiteIndices.addAll( that._satisfiedConditionalPrerequisiteIndices );

         _unsatisfiedConditionalDependentIndices = new java.util.ArrayList<Integer>();
         _unsatisfiedConditionalDependentIndices.addAll( that._unsatisfiedConditionalDependentIndices );

         if ( that._tags == null )
         {
            _tags = null;
         }
         else
         {
            _tags = new Tag[ that._tags.length ];
            System.arraycopy( that._tags, 0, _tags, 0, that._tags.length );
         }
      }
   }

   final void transferResultsFrom( TestData that )
   {
      int      index;
      TestData thatChild;
      TestData thisChild;

      _testState = that._testState;
      _exceptions.clear();
      _exceptions.addAll( that._exceptions );
      _startTime = that._startTime;
      _stopTime = that._stopTime;
      _cpuStartTime = that._cpuStartTime;
      _cpuStopTime = that._cpuStopTime;

      if ( that.getChildCount() == getChildCount() )
      {
         for ( index=0; index<that._children.size(); index++ )
         {
            thatChild = that._children.get( index );
            thisChild = _children.get( index );
            thisChild.transferResultsFrom( thatChild );
         }
      }
      else
      {
         for ( index=0; index<that._children.size(); index++ )
         {
            thatChild = that._children.get( index );
            thisChild = getChild( thatChild._callingStackTraceElement,
                                  thatChild._arguments );
            if ( thisChild != null )
            {
               thisChild.transferResultsFrom( thatChild );
            }
         }
      }
   }

   final void setTimingData( long startTime,
                             long stopTime,
                             long cpuStartTime,
                             long cpuStopTime )
   {
      _startTime = startTime;
      _stopTime = stopTime;
      _cpuStartTime = cpuStartTime;
      _cpuStopTime = cpuStopTime;
   }

   /**
    * Get the parent
    *
    * @return The TestData object for this test's parent test.
    */
   final public TestData getParent()
   {
      return _parent;
   }

   final TestManager getTestManager()
   {
      return _testManager;
   }

   final TestSet getTestSet()
   {
      return _testSet;
   }

   final int getSiblingIndex()
   {
      if ( _parent == null )
      {
         return 0;
      }
      else
      {
         return _parent._children.indexOf( this );
      }
   }

   final private boolean childrenMatch( TestData that )
   {
      int      index;
      TestData thisChild;
      TestData thatChild;

      if ( that._children.size() != _children.size() )
      {
         return false;
      }
      else
      {
         for ( index=0; index<_children.size(); index++ )
         {
            thisChild = _children.get( index );
            thatChild = that._children.get( index );

            if ( !(thisChild._callingStackTraceElement.equals(thatChild._callingStackTraceElement)) )
            {
               return false;
            }

            if ( (thisChild._arguments==null) != (thatChild._arguments==null) )
            {
               return false;
            }

            if ( thisChild._arguments != null )
            {
               if ( !(thisChild._arguments.equals(thatChild._arguments)) )
               {
                  return false;
               }
            }
         }

         return true;
      }
   }

   final private TestData getChild( StackTraceElement stackTraceElement,
                                    String            arguments )
   {
      for ( TestData child : _children )
      {
         if ( child._callingStackTraceElement.equals(stackTraceElement) )
         {
            if ( (child._arguments==null) == (arguments==null) )
            {
               if ( child._arguments != null )
               {
                  if ( child._arguments.equals( arguments ) )
                  {
                     return child;
                  }
               }
               else
               {
                  return child;
               }
            }
         }
      }

      for ( TestData child : _children )
      {
         if ( child == null )
         {
            System.out.println( "-------------------------------------------------------------------------------------------------------------------- child is null" );
            Utilities.printStackTrace();
            return null;
         }
         if ( child._callingStackTraceElement == null )
         {
            System.out.println( "-------------------------------------------------------------------------------------------------------------------- child._callingStackTraceElement is null" );
            Utilities.printStackTrace();
            return null;
         }
         if ( stackTraceElement == null )
         {
            System.out.println( "-------------------------------------------------------------------------------------------------------------------- stackTraceElement is null" );
            Utilities.printStackTrace();
            return null;
         }
         if ( child._callingStackTraceElement.getClassName().equals(stackTraceElement.getClassName()) )
         {
            if ( child._callingStackTraceElement.getMethodName().equals(stackTraceElement.getMethodName()) )
            {
               if ( ((child._arguments)==null) == (arguments==null) )
               {
                  if ( arguments == null )
                  {
                     return child;
                  }
                  else
                  {
                     if ( child._arguments.equals(arguments) )
                     {
                        return child;
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   final TestData getChild( int index )
   {
      return _children.get( index );
   }

   final void appendToSequence( SequenceList sequenceList )
   {
      _sequenceIndex = sequenceList.size();

      sequenceList.append( this );

      for ( TestData child : _children )
      {
         child.appendToSequence( sequenceList );
      }
   }

   /**
    * Get the test ID.  <br>
    * <br>
    * Within a single run, each test is assigned a unique integer
    * ID.  This ID might change from one Undercamber run to the next.
    *
    * @return The ID
    */
   final public int getID()
   {
      return _sequenceIndex;
   }

   final void addDependent( TestData          dependent,
                            Prerequisite.Type type )
      throws InternalException
   {
      switch ( type )
      {
         case FIXED:
         {
            _fixedDependents.add( dependent );
            break;
         }
         case CONDITIONAL_NOT_PREVIOUSLY_SATISFIED:
         {
            _unsatisfiedConditionalDependents.add( dependent );
            break;
         }
         case CONDITIONAL_PREVIOUSLY_SATISFIED:
         {
            _satisfiedConditionalDependents.add( dependent );
            break;
         }
         default:
         {
            throw new InternalException( "Internal error:  unrecognized prerequisite type:  " + type );
         }
      }
   }

   final void addPrerequisite( TestData          prerequisite,
                               Prerequisite.Type type )
      throws InternalException
   {
      switch ( type )
      {
         case FIXED:
         {
            _fixedPrerequisites.add( prerequisite );
            break;
         }
         case CONDITIONAL_NOT_PREVIOUSLY_SATISFIED:
         {
            _unsatisfiedConditionalPrerequisites.add( prerequisite );
            break;
         }
         case CONDITIONAL_PREVIOUSLY_SATISFIED:
         {
            _satisfiedConditionalPrerequisites.add( prerequisite );
            break;
         }
         default:
         {
            throw new InternalException( "Internal error:  unrecognized prerequisite type:  " + type );
         }
      }
   }

   final void initializePrerequisites( boolean useAlternateRunFlag )
      throws UserError
   {
      TestData closestCommonAncestor;

      _fixedPrerequisiteIndices = new java.util.ArrayList<Integer>();
      for ( TestData prerequisite : _fixedPrerequisites )
      {
         _fixedPrerequisiteIndices.add( prerequisite._sequenceIndex );
         closestCommonAncestor = findClosestCommonAncestor( prerequisite );

         if ( closestCommonAncestor != null )
         {
            if ( closestCommonAncestor._subtestSequencingMode.isParallel() )
            {
               throw new UserError( "Error:  Race condition:  " + getHeading() + " and its prerequisite (" + prerequisite.getHeading() + ") run concurrently.  The closest common ancestor ("  + closestCommonAncestor.getHeading() + ") runs its children concurrently" );
            }
         }
      }

      _unsatisfiedConditionalPrerequisiteIndices = new java.util.ArrayList<Integer>();
      for ( TestData prerequisite : _unsatisfiedConditionalPrerequisites )
      {
         _unsatisfiedConditionalPrerequisiteIndices.add( prerequisite._sequenceIndex );
         closestCommonAncestor = findClosestCommonAncestor( prerequisite );

         if ( closestCommonAncestor != null )
         {
            if ( closestCommonAncestor._subtestSequencingMode.isParallel() )
            {
               throw new UserError( "Error:  Race condition:  " + getHeading() + " and its unsatisfied conditional prerequisite (" + prerequisite.getHeading() + ") run concurrently.  The closest common ancestor ("  + closestCommonAncestor.getHeading() + ") runs its children concurrently" );
            }
         }
      }

      _satisfiedConditionalPrerequisiteIndices = new java.util.ArrayList<Integer>();
      for ( TestData prerequisite : _unsatisfiedConditionalPrerequisites )
      {
         _satisfiedConditionalPrerequisiteIndices.add( prerequisite._sequenceIndex );
         closestCommonAncestor = findClosestCommonAncestor( prerequisite );

         if ( closestCommonAncestor != null )
         {
            if ( closestCommonAncestor._subtestSequencingMode.isParallel() )
            {
               throw new UserError( "Error:  Race condition:  " + getHeading() + " and its unsatisfied conditional prerequisite (" + prerequisite.getHeading() + ") run concurrently.  The closest common ancestor ("  + closestCommonAncestor.getHeading() + ") runs its children concurrently" );
            }
         }
      }

      _fixedDependentIndices = new java.util.ArrayList<Integer>();
      for ( TestData prerequisite : _fixedDependents )
      {
         _fixedDependentIndices.add( prerequisite._sequenceIndex );
         closestCommonAncestor = findClosestCommonAncestor( prerequisite );

         if ( closestCommonAncestor != null )
         {
            if ( closestCommonAncestor._subtestSequencingMode.isParallel() )
            {
               throw new UserError( "Error:  Race condition:  " + getHeading() + " and its prerequisite (" + prerequisite.getHeading() + ") run concurrently.  The closest common ancestor ("  + closestCommonAncestor.getHeading() + ") runs its children concurrently" );
            }
         }
      }

      _unsatisfiedConditionalDependentIndices = new java.util.ArrayList<Integer>();
      for ( TestData prerequisite : _unsatisfiedConditionalDependents )
      {
         _unsatisfiedConditionalDependentIndices.add( prerequisite._sequenceIndex );
         closestCommonAncestor = findClosestCommonAncestor( prerequisite );

         if ( closestCommonAncestor != null )
         {
            if ( closestCommonAncestor._subtestSequencingMode.isParallel() )
            {
               throw new UserError( "Error:  Race condition:  " + getHeading() + " and its unsatisfied conditional prerequisite (" + prerequisite.getHeading() + ") run concurrently.  The closest common ancestor ("  + closestCommonAncestor.getHeading() + ") runs its children concurrently" );
            }
         }
      }

      _satisfiedConditionalDependentIndices = new java.util.ArrayList<Integer>();
      for ( TestData prerequisite : _unsatisfiedConditionalDependents )
      {
         _satisfiedConditionalDependentIndices.add( prerequisite._sequenceIndex );
         closestCommonAncestor = findClosestCommonAncestor( prerequisite );

         if ( closestCommonAncestor != null )
         {
            if ( closestCommonAncestor._subtestSequencingMode.isParallel() )
            {
               throw new UserError( "Error:  Race condition:  " + getHeading() + " and its unsatisfied conditional prerequisite (" + prerequisite.getHeading() + ") run concurrently.  The closest common ancestor ("  + closestCommonAncestor.getHeading() + ") runs its children concurrently" );
            }
         }
      }

      for ( TestData prerequisite : _satisfiedConditionalPrerequisites )
      {
         closestCommonAncestor = findClosestCommonAncestor( prerequisite );

         if ( closestCommonAncestor != null )
         {
            if ( closestCommonAncestor._subtestSequencingMode.isParallel() )
            {
               throw new UserError( "Error:  Race condition:  " + getHeading() + " and its satisfied conditional prerequisite (" + prerequisite.getHeading() + ") run concurrently.  The closest common ancestor ("  + closestCommonAncestor.getHeading() + ") runs its children concurrently" );
            }
         }
      }

      if ( getRun(useAlternateRunFlag) )
      {
         enableUnsatisfiedPrerequisites( useAlternateRunFlag );
      }

      for ( TestData child : _children )
      {
         child.initializePrerequisites( useAlternateRunFlag );
      }
   }

   final private TestData findClosestCommonAncestor( TestData that )
   {
      java.util.List<TestData> thisAncestorList;
      java.util.List<TestData> thatAncestorList;

      thisAncestorList = new java.util.ArrayList<TestData>();
      updateAncestorList( thisAncestorList );

      thatAncestorList = new java.util.ArrayList<TestData>();
      that.updateAncestorList( thatAncestorList );

      for ( TestData thisAncestor : thisAncestorList )
      {
         if ( thatAncestorList.contains(thisAncestor) )
         {
            return thisAncestor;
         }
      }

      return null;
   }

   final private void updateAncestorList( java.util.List<TestData> ancestorList )
   {
      if ( _parent != null )
      {
         _parent.updateAncestorList( ancestorList );
      }

      ancestorList.add( this );
   }

   final boolean isAncestorOf( TestData testData )
   {
      java.util.List<TestData> ancestorList;

      ancestorList = new java.util.ArrayList<TestData>();

      testData.updateAncestorList( ancestorList );

      return ancestorList.contains( this );
   }

   final void addPrerequisite( Prerequisite prerequisite )
      throws UserError
   {
      if ( _testState.canAcceptConfiguration() )
      {
         _prerequisites.add( prerequisite );
      }
      else
      {
         throw new UserError( "Error:  Can add prerequisites only inside the test unit.  State = " + _testState + "." );
      }
   }

   /**
    * Get the prerequisites for this test.
    *
    * @return The prerequisites
    */
   final public Prerequisite[] getPrerequisites()
   {
      Prerequisite prerequisites[];

      prerequisites = new Prerequisite[ _prerequisites.size() ];
      prerequisites = _prerequisites.toArray( prerequisites );

      return prerequisites;
   }

   final TestData[] listUnsatisfiedPrerequisites()
   {
      java.util.Set<TestData> prerequisiteSet;
      TestData                prerequisites[];

      prerequisiteSet = new java.util.HashSet<TestData>();

      prerequisiteSet.addAll( _fixedPrerequisites );
      prerequisiteSet.addAll( _unsatisfiedConditionalPrerequisites );

      prerequisites = new TestData[ prerequisiteSet.size() ];
      prerequisites = prerequisiteSet.toArray( prerequisites );

      return prerequisites;
   }

   final TestData[] listFixedPrerequisites()
   {
      TestData prerequisites[];

      prerequisites = new TestData[ _fixedPrerequisites.size() ];

      prerequisites = _fixedPrerequisites.toArray( prerequisites );

      return prerequisites;
   }

   final TestData[] listUnsatisfiedConditionalPrerequisites()
   {
      TestData prerequisites[];

      prerequisites = new TestData[ _unsatisfiedConditionalPrerequisites.size() ];

      prerequisites = _unsatisfiedConditionalPrerequisites.toArray( prerequisites );

      return prerequisites;
   }

   final TestData[] listPreviouslySatisfiedConditionalPrerequisites()
   {
      TestData prerequisites[];

      prerequisites = new TestData[ _satisfiedConditionalPrerequisites.size() ];

      prerequisites = _satisfiedConditionalPrerequisites.toArray( prerequisites );

      return prerequisites;
   }

   final TestData[] listUnsatisfiedDependents()
   {
      java.util.Set<TestData> dependentSet;
      TestData                dependents[];

      dependentSet = new java.util.HashSet<TestData>();

      dependentSet.addAll( _fixedDependents );
      dependentSet.addAll( _unsatisfiedConditionalDependents );

      dependents = new TestData[ dependentSet.size() ];
      dependents = dependentSet.toArray( dependents );

      return dependents;
   }

   final TestData[] listFixedDependents()
   {
      TestData dependents[];

      dependents = new TestData[ _fixedDependents.size() ];
      dependents = _fixedDependents.toArray( dependents );

      return dependents;
   }

   final TestData[] listUnsatisfiedConditionalDependents()
   {
      TestData dependents[];

      dependents = new TestData[ _unsatisfiedConditionalDependents.size() ];
      dependents = _unsatisfiedConditionalDependents.toArray( dependents );

      return dependents;
   }

   final TestData[] listSatisfiedConditionalDependents()
   {
      TestData dependents[];

      dependents = new TestData[ _satisfiedConditionalDependents.size() ];
      dependents = _satisfiedConditionalDependents.toArray( dependents );

      return dependents;
   }

   final int getTotalPrerequisiteCount()
   {
      return _fixedPrerequisites.size() + _unsatisfiedConditionalPrerequisites.size() + _satisfiedConditionalPrerequisites.size();
   }

   final int getTotalDependentCount()
   {
      return _fixedDependents.size() + _unsatisfiedConditionalDependents.size() + _satisfiedConditionalDependents.size();
   }

   final boolean prerequisitesSucceeded()
   {
      if ( _fixedPrerequisiteIndices==null && _unsatisfiedConditionalPrerequisiteIndices==null )
      {
         return true;
      }
      else
      {
         for ( int prerequisiteIndex : _fixedPrerequisiteIndices )
         {
            try
            {
               if ( !(_testSet.getStatusFile().get(prerequisiteIndex).successful()) )
               {
                  return false;
               }
            }
            catch ( java.io.IOException ioException )
            {
               _exceptions.add( new InternalException("Internal Error:  Could not retrieve test status",ioException) );
               Utilities.printStackTrace( ioException );
               return false;
            }
         }
         for ( int prerequisiteIndex : _unsatisfiedConditionalPrerequisiteIndices )
         {
            try
            {
               if ( !(_testSet.getStatusFile().get(prerequisiteIndex).successful()) )
               {
                  return false;
               }
            }
            catch ( java.io.IOException ioException )
            {
               _exceptions.add( new InternalException("Internal Error:  Could not retrieve status",ioException) );
               Utilities.printStackTrace( ioException );
               return false;
            }
         }

         return true;
      }
   }

   final java.util.Set<TestData> findEntryPoints( TestEntryPoint entryPoint )
   {
      java.util.Set<TestData> testDataEntryPointSet;

      testDataEntryPointSet = new java.util.HashSet<TestData>();

      findEntryPoints( entryPoint,
                       testDataEntryPointSet );

      return testDataEntryPointSet;
   }

   final boolean findEntryPoints( TestEntryPoint          entryPoint,
                                  java.util.Set<TestData> entryPoints )
   {
      boolean found;

      found = false;

      if ( _testManager != null )
      {
         if ( entryPoint.matches(this) )
         {
            found = true;
            entryPoints.add( this );
         }
      }

      for ( TestData child : _children )
      {
         if ( child.findEntryPoints(entryPoint,entryPoints) )
         {
            found = true;
         }
      }

      return found;
   }

   /**
    * Get the subtest continuation mode
    *
    * @return The subtest continuation mode
    */
   final public SubtestContinuationMode getSubtestContinuationMode()
   {
      return _subtestContinuationMode;
   }

   final TestData getRoot()
   {
      if ( _parent == null )
      {
         return this;
      }
      else
      {
         return _parent.getRoot();
      }
   }

   final void addChild( TestUnit testUnit,
                        String   margin )
   {
      TestManager childTestManager;
      TestData    child;

      childTestManager = new TestManager( this,
                                          testUnit,
                                          _testSet,
                                          getChildPass1Data(_children.size()),
                                          margin );

      child = childTestManager.getTestData();

      _children.add( child );

      notifyListeners();
   }

   final private TestData getChildPass1Data( int index )
   {
      TestData thisConfigurationData;
      TestData childrenConfigurationData[];

      thisConfigurationData = _testManager.getPass1TestData();

      if ( thisConfigurationData == null )
      {
         return null;
      }
      else
      {
         childrenConfigurationData = thisConfigurationData.getChildren();
         if ( index >= childrenConfigurationData.length )
         {
            return null;
         }
         else
         {
            return childrenConfigurationData[ index ];
         }
      }
   }

   /**
    * Get the TestData objects for this test's subtests.  <br>
    * <br>
    * <b>Note:</b>  The children specified in {@link
    * TestManager#addSubtest} might or might not be present,
    * depending on whether or not they were run.
    *
    * @return The TestData objects for the subtests immediately
    *         below this test.
    */
   final public TestData[] getChildren()
   {
      TestData children[];

      children = new TestData[ _children.size() ];
      children = _children.toArray( children );

      return children;
   }

   final int getChildCount()
   {
      return _children.size();
   }

   final boolean hasGrandchildren()
   {
      for ( TestData child : _children )
      {
         if ( child._children.size() > 0 )
         {
            return true;
         }
      }

      return false;
   }

   final int getNumberOfChildrenToRun( boolean useAlternateRunFlag )
   {
      int count;

      count = 0;

      for ( TestData child : _children )
      {
         if ( child.getRun(useAlternateRunFlag) )
         {
            count++;
         }
      }

      return count;
   }

   /**
    * Get the subtest sequencing mode for the test
    *
    * @return The mode
    */
   final public SubtestSequencingMode getSubtestSequencingMode()
   {
      return _subtestSequencingMode;
   }

   final String getCallingClassName()
   {
      return _callingStackTraceElement.getClassName();
   }

   final String getCallingMethodName()
   {
      return _callingStackTraceElement.getMethodName();
   }

   final String getArguments()
   {
      return _arguments;
   }

   /**
    * Get a descriptive string for the test.  <br>
    * <br>
    * The string is constructed from the test's class, the test's
    * method, and the test's arguments (if any).
    *
    * @return Brief description.
    */
   final public String getHeading()
   {
      if ( _callingStackTraceElement == null )
      {
         return "Test Framework Root";
      }
      else
      {
         return getCallingClassName() + "." + getCallingMethodName() + "(" + (_arguments == null ? "" : _arguments) + ")";
      }
   }

   final int getHeadingColumnWidth( int    maximumHeadingColumnWidth,
                                    String margin )
   {
      String text;

      if ( _testState.skipped() )
      {
         return maximumHeadingColumnWidth;
      }

      text = margin + getHeading();

      if ( text.length() > maximumHeadingColumnWidth )
      {
         maximumHeadingColumnWidth = text.length();
      }

      for ( TestData child : _children )
      {
         maximumHeadingColumnWidth = child.getHeadingColumnWidth( maximumHeadingColumnWidth,
                                                                  margin + "   " );
      }

      return maximumHeadingColumnWidth;
   }

   final DependencyWindow getDependencyWindow( boolean             showTestPopupMenu,
                                               Undercamber         undercamber,
                                               javafx.stage.Window ownerWindow )
   {
      if ( _dependencyWindow == null )
      {
         _dependencyWindow = new DependencyWindow( this,
                                                   showTestPopupMenu,
                                                   undercamber,
                                                   ownerWindow );
      }

      return _dependencyWindow;
   }

   final ErrorWindow getErrorWindow( javafx.stage.Window ownerWindow )
   {
      if ( _errorWindow == null )
      {
         _errorWindow = new ErrorWindow( this,
                                         ownerWindow );
      }

      return _errorWindow;
   }

   final TestDetailsWindow getTestDetailsWindow( javafx.stage.Window ownerWindow )
   {
      if ( _testDetailsWindow == null )
      {
         _testDetailsWindow = new TestDetailsWindow( this,
                                                     ownerWindow );
      }

      return _testDetailsWindow;
   }

   final RequirementsWindow getRequirementsWindow( boolean             results,
                                                   javafx.stage.Window ownerWindow )
   {
      java.util.List<RequirementData> sortedRequirements;

      if ( results )
      {
         if ( _requirementsResultsWindow == null )
         {
            sortedRequirements = new java.util.ArrayList<RequirementData>();
            sortedRequirements.addAll( _requirementsData );
            java.util.Collections.sort( sortedRequirements );
            _requirementsResultsWindow = new RequirementsWindow( "Requirements verified by " + getHeading(),
                                                                 true,
                                                                 sortedRequirements,
                                                                 ownerWindow );
         }
         return _requirementsResultsWindow;
      }
      else
      {
         if ( _requirementsWindow == null )
         {
            sortedRequirements = new java.util.ArrayList<RequirementData>();
            sortedRequirements.addAll( _requirementsData );
            java.util.Collections.sort( sortedRequirements );
            _requirementsWindow = new RequirementsWindow( "Requirements verified by " + getHeading(),
                                                          false,
                                                          sortedRequirements,
                                                          ownerWindow );
         }
         return _requirementsWindow;
      }
   }

   final void setState( TestState testState )
   {
      _testState = testState;

      notifyListeners();
   }

   final void setStateOnBranch( TestState testState )
   {
      _testState = testState;

      notifyListeners();

      for ( TestData child : _children )
      {
         child.setStateOnBranch( testState );
      }
   }

   final void recordStateOnBranch( TestState state )
   {
      recordState( state );

      for ( TestData child : _children )
      {
         child.recordStateOnBranch( state );
      }
   }

   final void recordState()
   {
      try
      {
         _testSet.getStatusFile().set( _sequenceIndex,
                                       _testState );
      }
      catch ( java.io.IOException ioException )
      {
         Utilities.printStackTrace( ioException );
      }
   }

   final void recordState( TestState testState )
   {
      _testState = testState;

      recordState();
   }

   final void addException( Throwable throwable )
   {
      _exceptions.add( throwable );

      notifyListeners();
   }

   final void addExceptions( java.util.List<Throwable> throwables )
   {
      _exceptions.addAll( throwables );

      notifyListeners();
   }

   final void addExceptions( Throwable... throwables )
   {
      for ( Throwable throwable : throwables )
      {
         _exceptions.add( throwable );
      }

      notifyListeners();
   }

   final boolean hasExceptionInBranch()
   {
      for ( Throwable exception : _exceptions )
      {
         if ( !(exception instanceof Message) )
         {
            return true;
         }
      }

      for ( TestData child : _children )
      {
         if ( child.hasExceptionInBranch() )
         {
            return true;
         }
      }

      return false;
   }

   /**
    * Get the number of exceptions captured from the test.  <br>
    * <br>
    * Undercamber considers the test passed if the test runs to
    * completion and if the test has no exceptions.
    *
    * @return The number of exceptions
    */
   final public int getLocalExceptionCount()
   {
      int exceptionCount;

      exceptionCount = 0;

      for ( Throwable exception : _exceptions )
      {
         if ( !(exception instanceof Message) )
         {
            exceptionCount++;
         }
      }

      return exceptionCount;
   }

   final int getChildExceptionCount()
   {
      int exceptionCount;

      exceptionCount = 0;

      for ( TestData child : _children )
      {
         exceptionCount += child.getExceptionCountOnBranch();
      }

      return exceptionCount;
   }

   final int getExceptionCountOnBranch()
   {
      int exceptionCount;

      if ( _testState.ran() )
      {
         exceptionCount = getLocalExceptionCount();

         if ( !(isComplete()) )
         {
            exceptionCount++;
         }

         for ( TestData child : _children )
         {
            exceptionCount += child.getExceptionCountOnBranch();
         }

         return exceptionCount;
      }
      else
      {
         return 0;
      }
   }

   /**
    * Get the exceptions captured for this test.
    *
    * @return The exceptions
    */
   final public Throwable[] getExceptions()
   {
      return _exceptions.toArray( new Throwable[0] );
   }

   final void setRun( boolean run,
                      boolean useAlternateRunFlag )
   {
      int thisIndex;
      int index;
      int nextIndex;
      int siblingCount;

      if ( getRun(useAlternateRunFlag) != run )
      {
         if ( useAlternateRunFlag )
         {
            _alternateRunFlag = run;
         }
         else
         {
            _guiRunFlag = run;
         }

         if ( run )
         {
            enableUnsatisfiedPrerequisites( useAlternateRunFlag );

            if ( _parent != null )
            {
               _parent.setRun( run,
                               useAlternateRunFlag );

               if ( _parent._subtestSequencingMode.abortOnError() )
               {
                  thisIndex = getSiblingIndex();
                  for ( index=0; index<thisIndex; index++ )
                  {
                     _parent._children.get( index ).setRun( true,
                                                            useAlternateRunFlag );
                  }
               }
            }
         }
         else
         {
            if ( _parent != null )
            {
               if ( _parent._subtestSequencingMode.abortOnError() )
               {
                  nextIndex = getSiblingIndex() + 1;
                  siblingCount = _parent._children.size();
                  for ( index=nextIndex; index<siblingCount; index++ )
                  {
                     _parent._children.get( index ).setRun( false,
                                                            useAlternateRunFlag );
                  }
               }
            }

            disableUnsatisfiedDependentsOnBranch( useAlternateRunFlag );
         }

         notifyListeners();
      }
   }

   final private void enableUnsatisfiedPrerequisites( boolean useAlternateRunFlag )
   {
      int thisIndex;
      int index;

      for ( TestData preresuisite : _fixedPrerequisites  )
      {
         preresuisite.setRun( true,
                              useAlternateRunFlag );
      }

      for ( TestData preresuisite : _unsatisfiedConditionalPrerequisites  )
      {
         preresuisite.setRun( true,
                              useAlternateRunFlag );
      }

      if ( _parent != null )
      {
         if ( _parent._subtestSequencingMode.abortOnError() )
         {
            thisIndex = getSiblingIndex();
            for ( index=0; index<thisIndex; index++ )
            {
               _parent._children.get( index ).setRun( true,
                                                      useAlternateRunFlag );
            }
         }
      }

      if ( _parent != null )
      {
         _parent.setRun( true,
                         useAlternateRunFlag );
      }
   }

   final private void disableUnsatisfiedDependentsOnBranch( boolean useAlternateRunFlag )
   {
      for ( TestData dependent : _fixedDependents )
      {
         dependent.setRun( false,
                           useAlternateRunFlag );
      }

      for ( TestData dependent : _unsatisfiedConditionalDependents )
      {
         dependent.setRun( false,
                           useAlternateRunFlag );
      }

      for ( TestData child : _children )
      {
         child.disableUnsatisfiedDependentsOnBranch( useAlternateRunFlag );
      }
   }

   final void enableRunAndEnableUnsatisfiedDependents( boolean useAlternateRunFlag )
   {
      if ( !getRun(useAlternateRunFlag) )
      {
         setRun( true,
                 useAlternateRunFlag );
      }

      for ( TestData dependent : _fixedDependents )
      {
         dependent.enableRunAndEnableUnsatisfiedDependents( useAlternateRunFlag );
      }

      for ( TestData dependent : _unsatisfiedConditionalDependents )
      {
         dependent.enableRunAndEnableUnsatisfiedDependents( useAlternateRunFlag );
      }
   }

   final void forcePrerequisitesOnBranch( boolean useAlternateRunFlag )
   {
      forcePrerequisites( useAlternateRunFlag );

      for ( TestData child : _children )
      {
         child.forcePrerequisitesOnBranch( useAlternateRunFlag );
      }
   }

   final void forcePrerequisites( boolean useAlternateRunFlag )
   {
      int thisIndex;
      int index;

      if ( getRun(useAlternateRunFlag) )
      {
         for ( TestData preresuisite : _fixedPrerequisites  )
         {
            preresuisite.setRun( true,
                                 useAlternateRunFlag );
         }

         for ( TestData preresuisite : _unsatisfiedConditionalPrerequisites  )
         {
            preresuisite.setRun( true,
                                 useAlternateRunFlag );
         }

         for ( TestData preresuisite : _satisfiedConditionalPrerequisites  )
         {
            preresuisite.setRun( true,
                                 useAlternateRunFlag );
         }

         if ( _parent != null )
         {
            if ( _parent._subtestSequencingMode.abortOnError() )
            {
               thisIndex = getSiblingIndex();
               for ( index=0; index<thisIndex; index++ )
               {
                  _parent._children.get( index ).setRun( true,
                                                         useAlternateRunFlag );
               }
            }
         }

         if ( _parent != null )
         {
            _parent.setRun( true,
                            useAlternateRunFlag );
         }
      }
   }

   final void transferGUIRunFlags()
   {
      setRunOnBranch( false,
                      true );

      transferGUIRunFlagsOnBranch();
   }

   final private void transferGUIRunFlagsOnBranch()
   {
      if ( _guiRunFlag )
      {
         setRun( true,
                 true );
      }

      for ( TestData child : _children )
      {
         child.transferGUIRunFlagsOnBranch();
      }
   }

   final void setRunOnBranch( boolean run,
                              boolean useAlternateRunFlag )
   {
      setRun( run,
              useAlternateRunFlag );

      for ( TestData child : _children )
      {
         child.setRunOnBranch( run,
                               useAlternateRunFlag );
      }
   }

   final Tag getTag( String tagName )
   {
      for ( Tag tag : _tags )
      {
         if ( tag.getName().equals(tagName) )
         {
            return tag;
         }
      }

      return null;
   }

   final boolean setAlternateRunFlagOnBranch( String tagName )
   {
      Tag     tag;
      boolean found;
      boolean foundInChild;

      found = false;

      tag = getTag( tagName );

      if ( tag != null )
      {
         found = true;

         if ( tag.includeSubtests() )
         {
            setRunOnBranch( true,
                            true );
         }
         else
         {
            setRun( true,
                    true );
         }
      }

      for ( TestData child : _children )
      {
         foundInChild = child.setAlternateRunFlagOnBranch( tagName );
         if ( foundInChild )
         {
            found = true;
         }
      }

      return found;
   }

   final Tag[] listLocalTags()
   {
      Tag tags[];

      tags = new Tag[ _tags.length ];

      System.arraycopy( _tags, 0, tags, 0, _tags.length );

      return tags;
   }

   final void addToTagMap( java.util.Map<String,java.util.Set<TestData>> map )
   {
      java.util.Set<TestData> testDataSet;

      for ( Tag tag : _tags )
      {
         testDataSet = map.get( tag.getName() );

         if ( testDataSet == null )
         {
            testDataSet = new java.util.HashSet<TestData>();
            map.put( tag.getName(), testDataSet );
         }

         testDataSet.add( this );
      }

      for ( TestData child : _children )
      {
         child.addToTagMap( map );
      }
   }

   final void addTagNamesToSet( java.util.Set<String> tagNames )
   {
      for ( Tag tag : _tags )
      {
         tagNames.add( tag.getName() );
      }

      for ( TestData child : _children )
      {
         child.addTagNamesToSet( tagNames );
      }
   }

   final boolean getRun( boolean useAlternateRunFlag )
   {
      if ( useAlternateRunFlag )
      {
         return _alternateRunFlag;
      }
      else
      {
         return _guiRunFlag;
      }
   }

   final boolean hasNonRunningAncestor( boolean useAlternateRunFlag )
   {
      if ( !getRun(useAlternateRunFlag) )
      {
         return true;
      }

      if ( _parent == null )
      {
         return false;
      }

      return _parent.hasNonRunningAncestor( useAlternateRunFlag );
   }

   final private boolean isComplete()
   {
      return _testState.complete();
   }

   final void setExpanded( boolean selection,
                           boolean expanded )
   {
      if ( selection )
      {
         if ( expanded != _selectionExpanded )
         {
            _selectionExpanded = expanded;
            notifyListeners();
         }
      }
      else
      {
         if ( expanded != _resultsExpanded )
         {
            _resultsExpanded = expanded;
            notifyListeners();
         }
      }
   }

   final void setExpandedOnBranch( boolean selection,
                                   boolean expanded )
   {
      setExpanded( selection,
                   expanded );

      for ( TestData child : _children )
      {
         child.setExpandedOnBranch( selection,
                                    expanded );
      }
   }

   final boolean isExpanded( boolean selection )
   {
      if ( selection )
      {
         return _selectionExpanded;
      }
      else
      {
         return _resultsExpanded;
      }
   }

   final void expandAncestors( boolean selection )
   {
      if ( _parent != null )
      {
         _parent.expandToRoot( selection );
      }
   }

   final private void expandToRoot( boolean selection )
   {
      if ( _parent != null )
      {
         _parent.expandToRoot( selection );
      }

      setExpanded( selection,
                   true );
   }

   final void setMainTableTreeItem( MainTableTreeItem mainTableTreeItem )
   {
      _mainTableTreeItem = mainTableTreeItem;
   }

   final MainTableTreeItem getMainTableTreeItem()
   {
      return _mainTableTreeItem;
   }

   final void reset( boolean selection,
                     boolean useAlternateRunFlag )
   {
      setRun( false,
              useAlternateRunFlag );

      setExpanded( selection,
                   false );

      for ( TestData child : _children )
      {
         child.reset( selection,
                      useAlternateRunFlag );
      }
   }

   final void addListener( TestDataListener listener )
   {
      _listeners.add( listener );
   }

   final void removeListener( TestDataListener listener )
   {
      _listeners.remove( listener );
   }

   final void removeListenerAllInstances( TestDataListener listener )
   {
      while ( _listeners.contains(listener) )
      {
         _listeners.remove( listener );
      }
   }

   final private void notifyListeners()
   {
      for ( TestDataListener listener : _listeners )
      {
         listener.updated( this );
      }

      if ( _parent != null )
      {
         _parent.notifyAncestors( this );
      }

      for ( TestData child : _children )
      {
         child.notifyDescendants( this );
      }
   }

   final private void notifyDescendants( TestData updatedAncestor )
   {
      for ( TestDataListener listener : _listeners )
      {
         listener.ancestorUpdated( this,
                                   updatedAncestor );
      }

      for ( TestData child : _children )
      {
         child.notifyDescendants( updatedAncestor );
      }
   }

   final private void notifyAncestors( TestData updatedDescendant )
   {
      for ( TestDataListener listener : _listeners )
      {
         listener.descendantUpdated( this,
                                     updatedDescendant );
      }

      if ( _parent != null )
      {
         _parent.notifyAncestors( updatedDescendant );
      }
   }

   /**
    * Get the requirements validated by this test.
    *
    * @return The requirements
    */
   final public java.util.List<Requirement> getRequirements()
   {
      java.util.List<Requirement> requirements;

      requirements = new java.util.ArrayList<Requirement>();

      requirements.addAll( _requirements );

      return requirements;
   }

   final void addRequirementsToSet( java.util.Set<RequirementData> requirementsSet )
   {
      requirementsSet.addAll( _requirementsData );

      for ( TestData child : _children )
      {
         child.addRequirementsToSet( requirementsSet );
      }
   }

   final boolean hasRequirementsData()
   {
      return _requirementsData.size() > 0;
   }

   /**
    * Get the name of the TestSet, as specified in the Undercamber
    * configuration file.  <br>
    * <br>
    * Each TestSet is run in a different process.
    *
    * @return The name of the TestSet
    */
   final public String getTestSetName()
   {
      return _testSet.getTestSetName();
   }

   /**
    * Get the name of the overall test suite.
    *
    * @return The test suite name
    */
   final public String getTestSuiteName()
   {
      return _testSet.getTestSuiteName();
   }

   /**
    * Get the parameters passed to the test.
    *
    * @return The parameters, in original order
    */
   final public java.util.List<String> getTestParameters()
   {
      return _testSet.getTestParameters();
   }

   /**
    * Get the test directory provided by Undercamber.
    *
    * @return The test directory
    */
   final public java.io.File getUserWorkingDirectory()
   {
      return new java.io.File( _testSet.getLocalResultsDirectory(), "work" );
   }

   final void setupReferencedRequirementsOnBranch( int                     testCount,
                                                   java.util.Set<TestData> unsupportiveTests )
   {
      _referencedRequirementsData = new java.util.ArrayList<RequirementData>();

      addReferencedRequirements( _referencedRequirementsData,
                                 new boolean[testCount] );

      if ( _referencedRequirementsData.size() == 0 )
      {
         unsupportiveTests.add( this );
      }

      for ( TestData child : _children )
      {
         child.setupReferencedRequirementsOnBranch( testCount,
                                                    unsupportiveTests );
      }
   }

   final private void addReferencedRequirements( java.util.List<RequirementData> referencedRequirementsData,
                                                 boolean                         visitedNodeFlags[] )
   {
      java.util.List<TestData> siblings;
      int                      siblingCount;
      int                      siblingIndex;
      int                      index;

      if ( visitedNodeFlags[_sequenceIndex] )
      {
         return;
      }
      visitedNodeFlags[ _sequenceIndex ] = true;

      referencedRequirementsData.addAll( _requirementsData );

      for ( TestData testData : _fixedDependents )
      {
         testData.addReferencedRequirements( referencedRequirementsData,
                                             visitedNodeFlags );
      }

      for ( TestData testData : _unsatisfiedConditionalDependents )
      {
         testData.addReferencedRequirements( referencedRequirementsData,
                                             visitedNodeFlags );
      }

      for ( TestData testData : _satisfiedConditionalDependents )
      {
         testData.addReferencedRequirements( referencedRequirementsData,
                                             visitedNodeFlags );
      }

      for ( TestData child : _children )
      {
         child.addReferencedRequirements( referencedRequirementsData,
                                          visitedNodeFlags );
      }

      if ( _parent != null )
      {
         if ( _parent._subtestSequencingMode.abortOnError() )
         {
            siblings = _parent._children;
            siblingCount = siblings.size();
            siblingIndex = siblings.indexOf( this );
            for ( index=siblingIndex+1; index<siblingCount; index++ )
            {
               siblings.get( index ).addReferencedRequirements( referencedRequirementsData,
                                                                visitedNodeFlags );
            }
         }
      }
   }

   final void addBranchToMap( TestData map[] )
   {
      map[ _sequenceIndex ] = this;

      for ( TestData child : _children )
      {
         child.addBranchToMap( map );
      }
   }

   /**
    * Get the completion status
    *
    * @return The completion status
    */
   final public TestState getTestState()
   {
      return _testState;
   }

   final void requirementsCallback()
   {
      for ( Requirement requirement : _requirements )
      {
         requirement.testComplete( this );
      }

      for ( TestData child : _children )
      {
         child.requirementsCallback();
      }
   }

   final void printTextReport( boolean             printAll,
                               String              margin,
                               int                 headingColumnWidth,
                               java.io.PrintStream printStream )
   {
      StringBuffer stringBuffer;
      int          localExceptionCount;
      int          childExceptionCount;
      boolean      useComma;

      if ( printAll || (_testState.displayInShortReport()) )
      {
         stringBuffer = new StringBuffer();

         localExceptionCount = getLocalExceptionCount();

         childExceptionCount = getChildExceptionCount();

         stringBuffer.append( Utilities.padToRight(margin+getHeading(),headingColumnWidth) ).append( " " ).append( Utilities.padToLeft( localExceptionCount, Finisher.LOCAL_EXCEPTION_COUNT_COLUMN_WIDTH ) ).append( " " ).append( Utilities.padToLeft(childExceptionCount,Finisher.CHILD_EXCEPTION_COUNT_COLUMN_WIDTH) );

         useComma = false;
         if ( localExceptionCount > 0 )
         {
            if ( useComma )
            {
               stringBuffer.append( "; " );
            }
            else
            {
               stringBuffer.append( "  " );
            }
            stringBuffer.append( "failed" );
            useComma = true;
         }

         if ( !(_testState.successful()) )
         {
            if ( _testState != TestState.COMPLETE_FAILED )
            {
               if ( useComma )
               {
                  stringBuffer.append( "; " );
               }
               else
               {
                  stringBuffer.append( "  " );
               }
               stringBuffer.append( "state = " ).append( _testState.name() );
               useComma = true;
            }
         }

         if ( childExceptionCount > 0 )
         {
            if ( useComma )
            {
               stringBuffer.append( "; " );
            }
            else
            {
               stringBuffer.append( "  " );
            }
            stringBuffer.append( "descendents failed" );
            useComma = true;
         }

         printStream.println( stringBuffer.toString() );

         if ( _exceptions.size() > 0 )
         {
            printStream.println( margin + "   Exceptions:" );
            for ( Throwable error : _exceptions )
            {
               if ( error instanceof Message )
               {
                  ((Message)error).printMessage( margin + "      ",
                                                 printStream );
               }
               else
               {
                  Utilities.printStackTrace( error,
                                             margin + "      ",
                                             printStream );
               }
            }
         }

         for ( TestData child : _children )
         {
            child.printTextReport( printAll,
                                   margin + "   ",
                                   headingColumnWidth,
                                   printStream );
         }
      }
   }

   final void writeXMLReport( String              margin,
                              java.io.PrintStream printStream )
   {
      printStream.println( margin + "<test>" );
      printStream.println( margin + "   <testID>" + _sequenceIndex + "</testID>" );
      printStream.println( margin + "   <name>" + Utilities.escapeForXML(getHeading()) + "</name>" );
      printStream.println( margin + "   <result>" + _testState.name() + "</result>" );
      printStream.println( margin + "   <errorCount>" + getLocalExceptionCount() + "</errorCount>" );
      printStream.println( margin + "   <subtestSequencingMode>" + _subtestSequencingMode + "</subtestSequencingMode>" );
      printStream.println( margin + "   <subtestContinuationMode>" + _subtestContinuationMode + "</subtestContinuationMode>" );

      printStream.println( margin + "   <errorsAndMessages>" );
      for ( Throwable throwable : _exceptions )
      {
         if ( throwable instanceof Message )
         {
            printStream.print( margin + "      <message>" );
            ((Message)throwable).printMessage( "",
                                               printStream,
                                               true );
            printStream.print( "</message>" );
         }
         else
         {
            printStream.println( margin + "      <exception>" );
            Utilities.printStackTrace( throwable,
                                       margin + "         ",
                                       printStream,
                                       true );
            printStream.println( margin + "      </exception>" );
         }
      }
      printStream.println( margin + "   </errorsAndMessages>" );

      printStream.println( margin + "   <validatedRequirements>");
      for ( RequirementData requirementData : _requirementsData )
      {
         requirementData.writeXML( printStream,
                                   margin + "   " );
      }
      printStream.println( margin + "   </validatedRequirements>");

      printStream.println( margin + "   <supportedRequirements>" );
      for ( RequirementData requirementData : _referencedRequirementsData )
      {
         requirementData.writeXML( printStream,
                                   margin + "   " );
      }
      printStream.println( margin + "   </supportedRequirements>" );

      printStream.println( margin + "   <prerequisiteDeclarations>" );
      for ( Prerequisite prerequisite : _prerequisites )
      {
         prerequisite.writeToXML( margin + "      ",
                                  printStream );
      }
      printStream.println( margin + "   </prerequisiteDeclarations>" );

      printStream.println( margin + "   <prerequisites>" );
      for ( TestData prerequisite : _fixedPrerequisites )
      {
         printStream.println( margin + "      <testID>" + prerequisite.getID() + "</testID>" );
      }
      for ( TestData prerequisite : _satisfiedConditionalPrerequisites )
      {
         printStream.println( margin + "      <testID>" + prerequisite.getID() + "</testID>" );
      }
      for ( TestData prerequisite : _unsatisfiedConditionalPrerequisites )
      {
         printStream.println( margin + "      <testID>" + prerequisite.getID() + "</testID>" );
      }
      printStream.println( margin + "   </prerequisites>" );

      printStream.println( margin + "   <dependents>" );
      for ( TestData dependent : _fixedDependents )
      {
         printStream.println( margin + "      <testID>" + dependent.getID() + "</testID>" );
      }
      for ( TestData dependent : _satisfiedConditionalDependents )
      {
         printStream.println( margin + "      <testID>" + dependent.getID() + "</testID>" );
      }
      for ( TestData dependent : _unsatisfiedConditionalDependents )
      {
         printStream.println( margin + "      <testID>" + dependent.getID() + "</testID>" );
      }
      printStream.println( margin + "   </dependents>" );

      printStream.println( margin + "   <tags>" );
      for ( Tag tag : _tags )
      {
         printStream.println( margin + "      <tag>" );
         printStream.println( margin + "         <name>" + Utilities.escapeForXML(tag.getName()) + "</name>" );
         printStream.println( margin + "         <includeSubtests>" + tag.includeSubtests() + "</includeSubtests>" );
         printStream.println( margin + "      </tag>" );
      }
      printStream.println( margin + "   </tags>" );

      printStream.println( margin + "   <subtests>" );
      for ( TestData child : _children )
      {
         child.writeXMLReport( margin + "      ",
                               printStream );
      }
      printStream.println( margin + "   </subtests>" );

      printStream.println( margin + "</test>" );
   }

   final void write( java.io.DataOutputStream dataOutputStream )
      throws java.io.IOException
   {
      byte serializedObject[];

      dataOutputStream.writeInt( CLASS_PERSISTENCE_VERSION );
      dataOutputStream.writeUTF( CLASS_PERSISTENCE_BRANCH );

      try ( java.io.ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream() )
      {
         try ( java.io.ObjectOutputStream objectOutputStream = new java.io.ObjectOutputStream( byteArrayOutputStream ) )
         {
            objectOutputStream.writeObject( _callingStackTraceElement );
         }

         serializedObject = byteArrayOutputStream.toByteArray();
         dataOutputStream.writeInt( serializedObject.length );
         dataOutputStream.write( serializedObject );
      }

      dataOutputStream.writeBoolean( _arguments != null );
      if ( _arguments != null )
      {
         dataOutputStream.writeUTF( _arguments );
      }

      dataOutputStream.writeBoolean( _prerequisites != null );
      if ( _prerequisites != null )
      {
         dataOutputStream.writeInt( _prerequisites.size() );
         for ( Prerequisite prerequisite : _prerequisites )
         {
            prerequisite.write( dataOutputStream );
         }
      }

      dataOutputStream.writeBoolean( _fixedPrerequisiteIndices != null );
      if ( _fixedPrerequisiteIndices != null )
      {
         dataOutputStream.writeInt( _fixedPrerequisiteIndices.size() );
         for ( int prerequisiteIndex : _fixedPrerequisiteIndices )
         {
            dataOutputStream.writeInt( prerequisiteIndex );
         }
      }

      dataOutputStream.writeBoolean( _unsatisfiedConditionalPrerequisiteIndices != null );
      if ( _unsatisfiedConditionalPrerequisiteIndices != null )
      {
         dataOutputStream.writeInt( _unsatisfiedConditionalPrerequisiteIndices.size() );
         for ( int prerequisiteIndex : _unsatisfiedConditionalPrerequisiteIndices )
         {
            dataOutputStream.writeInt( prerequisiteIndex );
         }
      }

      dataOutputStream.writeBoolean( _satisfiedConditionalPrerequisiteIndices != null );
      if ( _satisfiedConditionalPrerequisiteIndices != null )
      {
         dataOutputStream.writeInt( _satisfiedConditionalPrerequisiteIndices.size() );
         for ( int prerequisiteIndex : _satisfiedConditionalPrerequisiteIndices )
         {
            dataOutputStream.writeInt( prerequisiteIndex );
         }
      }

      dataOutputStream.writeBoolean( _fixedDependentIndices != null );
      if ( _fixedDependentIndices != null )
      {
         dataOutputStream.writeInt( _fixedDependentIndices.size() );
         for ( int prerequisiteIndex : _fixedDependentIndices )
         {
            dataOutputStream.writeInt( prerequisiteIndex );
         }
      }

      dataOutputStream.writeBoolean( _unsatisfiedConditionalDependentIndices != null );
      if ( _unsatisfiedConditionalDependentIndices != null )
      {
         dataOutputStream.writeInt( _unsatisfiedConditionalDependentIndices.size() );
         for ( int prerequisiteIndex : _unsatisfiedConditionalDependentIndices )
         {
            dataOutputStream.writeInt( prerequisiteIndex );
         }
      }

      dataOutputStream.writeBoolean( _satisfiedConditionalDependentIndices != null );
      if ( _satisfiedConditionalDependentIndices != null )
      {
         dataOutputStream.writeInt( _satisfiedConditionalDependentIndices.size() );
         for ( int prerequisiteIndex : _satisfiedConditionalDependentIndices )
         {
            dataOutputStream.writeInt( prerequisiteIndex );
         }
      }

      dataOutputStream.writeBoolean( _subtestSequencingMode != null );
      if ( _subtestSequencingMode != null )
      {
         dataOutputStream.writeInt( _subtestSequencingMode.ordinal() );
      }

      dataOutputStream.writeBoolean( _subtestContinuationMode != null );
      if ( _subtestContinuationMode != null )
      {
         dataOutputStream.writeInt( _subtestContinuationMode.ordinal() );
      }

      dataOutputStream.writeInt( _testState.ordinal() );

      dataOutputStream.writeBoolean( _tags != null );
      if ( _tags != null )
      {
         dataOutputStream.writeInt( _tags.length );
         for ( Tag tag : _tags )
         {
            tag.write( dataOutputStream );
         }
      }

      dataOutputStream.writeBoolean( _guiRunFlag );
      dataOutputStream.writeBoolean( _alternateRunFlag );
      dataOutputStream.writeInt( _sequenceIndex );
      dataOutputStream.writeBoolean( _selectionExpanded );
      dataOutputStream.writeBoolean( _resultsExpanded );

      dataOutputStream.writeInt( _exceptions.size() );
      for ( Throwable throwable : _exceptions )
      {
         try ( java.io.ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream() )
         {
            try ( java.io.ObjectOutputStream objectOutputStream = new java.io.ObjectOutputStream( byteArrayOutputStream ) )
            {
               objectOutputStream.writeObject( throwable );
            }

            serializedObject = byteArrayOutputStream.toByteArray();
            dataOutputStream.writeInt( serializedObject.length );
            dataOutputStream.write( serializedObject );
         }
      }

      dataOutputStream.writeLong( _startTime );
      dataOutputStream.writeLong( _stopTime );
      dataOutputStream.writeLong( _cpuStartTime );
      dataOutputStream.writeLong( _cpuStopTime );

      dataOutputStream.writeInt( _requirementsData.size() );
      for ( RequirementData requirementData : _requirementsData )
      {
         requirementData.write( dataOutputStream );
      }

      dataOutputStream.writeInt( _children.size() );
      for ( TestData child : _children )
      {
         child.write( dataOutputStream );
      }
   }

   /**
    * Imposes ordering
    *
    * @param that
    *        The TestData object for comparison
    *
    * @return The integer indicating order
    */
   final public int compareTo( TestData that )
   {
      return _sequenceIndex - that._sequenceIndex;
   }
}

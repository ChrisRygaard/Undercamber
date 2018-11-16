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

final class SequenceList
{
   java.util.List<TestData> _list;

   SequenceList()
   {
      _list = new java.util.ArrayList<TestData>();
   }

   final void append( TestData testData )
   {
      _list.add( testData );
   }

   final void initializeDependencies()
      throws UserError,
             InternalException
   {
      int      testCount;
      int      scanIndex;
      TestData dependentTestData;
      TestSet  dependentTestSet;
      int      searchIndex;
      TestData candidatePrerequisiteTestData;
      TestData prerequisiteTestData;

      testCount = _list.size();

      for ( scanIndex=1; scanIndex<testCount; scanIndex++ )  // start at 1 -> skip dummy root
      {
         dependentTestData = _list.get( scanIndex );

         dependentTestSet = dependentTestData.getTestSet();

         for ( Prerequisite prerequisite : dependentTestData.getPrerequisites() )
         {
            for ( searchIndex=scanIndex-1; searchIndex>0; searchIndex-- )  // Greater than 0 -> skip dummy root
            {
               candidatePrerequisiteTestData = _list.get( searchIndex );

               if ( prerequisite.matches(candidatePrerequisiteTestData,
                                         dependentTestSet) )
               {
                  if ( !(candidatePrerequisiteTestData.isAncestorOf(dependentTestData)) )
                  {
                     prerequisiteTestData = candidatePrerequisiteTestData;

                     if ( prerequisite.includeSubtests() )
                     {
                        addPrerequisiteBranchToSet( dependentTestData,
                                                    prerequisiteTestData,
                                                    prerequisite.getType() );
                     }
                     else
                     {
                        dependentTestData.addPrerequisite( prerequisiteTestData,
                                                           prerequisite.getType() );
                        prerequisiteTestData.addDependent( dependentTestData,
                                                           prerequisite.getType() );
                     }

                     if ( !(prerequisite.isSatisfied()) )
                     {
                        prerequisite.setSatisfied();

                        if ( !(prerequisite.requiresAllPreviousMatchingMethods()) )
                        {
                           break;
                        }
                     }
                  }
               }
            }

            if ( !(prerequisite.isSatisfied()) )
            {
               throw new UserError( "Error:  Could not find prerequisite " + prerequisite + " for test " + dependentTestData.getHeading() + " in test set " + dependentTestData.getTestSetName() );
            }
         }
      }
   }

   final private void addPrerequisiteBranchToSet( TestData          dependentTestData,
                                                  TestData          prerequisiteTestData,
                                                  Prerequisite.Type type )
      throws InternalException
   {
      prerequisiteTestData.addDependent( dependentTestData,
                                         type );

      dependentTestData.addPrerequisite( prerequisiteTestData,
                                         type );

      for ( TestData child : prerequisiteTestData.getChildren() )
      {
         addPrerequisiteBranchToSet( dependentTestData,
                                     child,
                                     type );
      }
   }

   final TestData[] getTestDataMap()
   {
      return _list.toArray( new TestData[0] );
   }

   final int size()
   {
      return _list.size();
   }
}

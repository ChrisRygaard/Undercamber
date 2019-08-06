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

final class Finisher
   implements java.util.Comparator<TestData>
{
   final static int LOCAL_EXCEPTION_COUNT_COLUMN_WIDTH = 16;
   final static int CHILD_EXCEPTION_COUNT_COLUMN_WIDTH = 16;

   Finisher( String                  suiteName,
             int                     pass1ThreadCount,
             java.util.List<TestSet> pass1TestSets,
             java.io.File            resultsDirectory,
             int                     headingColumnWidth,
             TestData                pass1TestMap[],
             String                  elapsedTimeString,
             boolean                 displayResultsWindow,
             java.util.List<String>  commandLineTestParameters,
             javafx.stage.Window     ownerWindow,
             Undercamber             undercamber )
   {
      java.util.List<TestData>        testDataRoots;
      java.util.Set<TestData>         unsupportiveTestSet;
      TestData                        testResults;
      java.util.List<TestData>        unsupportiveTests;
      java.util.Set<RequirementData>  requirementsSet;
      java.util.List<RequirementData> requirementsList;
      java.io.File                    xmlFile;
      java.io.File                    xsdFile;

      unsupportiveTestSet = new java.util.HashSet<TestData>();

      for ( TestSet testSet : pass1TestSets )
      {
         testSet.getTestData().setupReferencedRequirementsOnBranch( pass1TestMap.length,
                                                                    unsupportiveTestSet );
         if ( testSet.shouldRun(true) )
         {
            testResults = testSet.getTestDataFromPersistence();
            testSet.getTestData().transferResultsFrom( testResults );
         }
      }

      unsupportiveTests = new java.util.ArrayList<TestData>();
      unsupportiveTests.addAll( unsupportiveTestSet );
      java.util.Collections.sort( unsupportiveTests );

      testDataRoots = new java.util.ArrayList<TestData>();
      for ( TestSet testSet : pass1TestSets )
      {
         testDataRoots.add( testSet.getTestData() );
      }

      requirementsSet = new java.util.HashSet<RequirementData>();
      for ( TestSet testSet : pass1TestSets )
      {
         testSet.getTestData().addRequirementsToSet( requirementsSet );
      }
      requirementsList = new java.util.ArrayList<RequirementData>();
      requirementsList.addAll( requirementsSet );
      java.util.Collections.sort( requirementsList );

      printTextReport( pass1TestSets,
                       resultsDirectory,
                       headingColumnWidth,
                       elapsedTimeString );

      printAnalysis( unsupportiveTests,
                     resultsDirectory );

      xmlFile = writeXMLFile( suiteName,
                              pass1ThreadCount,
                              pass1TestSets,
                              requirementsList,
                              unsupportiveTests,
                              pass1TestMap.length,
                              resultsDirectory,
                              undercamber );

      xsdFile = writeXSDFile( resultsDirectory );

      checkXMLSyntax( xmlFile,
                      xsdFile );

      System.out.println();
      System.out.println( "Results saved to " + resultsDirectory.getPath() );

      if ( displayResultsWindow )
      {
         javafx.application.Platform.runLater( () -> new ResultsWindow(suiteName,
                                                                       new TestData(pass1TestSets),
                                                                       pass1TestSets,
                                                                       requirementsList,
                                                                       unsupportiveTests,
                                                                       commandLineTestParameters,
                                                                       ownerWindow,
                                                                       undercamber) );
      }
   }

   final private void printTextReport( java.util.List<TestSet> pass1TestSets,
                                       java.io.File            resultsDirectory,
                                       int                     headingColumnWidth,
                                       String                  elapsedTimeString )
   {
      java.util.List<String>  testSetNames;
      java.util.List<String>  headings;
      java.util.List<Integer> errorCounts;
      java.util.List<Boolean> ranFlags;
      java.io.File            reportFile;

      testSetNames = new java.util.ArrayList<String>();
      headings = new java.util.ArrayList<String>();
      errorCounts = new java.util.ArrayList<Integer>();
      ranFlags = new java.util.ArrayList<Boolean>();

      reportFile = new java.io.File( resultsDirectory, "FinalReport.txt" );
      try ( java.io.FileOutputStream fileOutputStream = new java.io.FileOutputStream(reportFile) )
      {
         try ( java.io.BufferedOutputStream bufferedOutputStream = new java.io.BufferedOutputStream(fileOutputStream) )
         {
            try ( java.io.PrintStream printStream = new java.io.PrintStream(bufferedOutputStream) )
            {
               printStream.println( Utilities.padToRight("Test",headingColumnWidth) + " " + Utilities.padToLeft("Local Exceptions",LOCAL_EXCEPTION_COUNT_COLUMN_WIDTH) + " " + Utilities.padToLeft("Child Exceptions",CHILD_EXCEPTION_COUNT_COLUMN_WIDTH) );
               printStream.println( Utilities.padToRight("----",headingColumnWidth) + " " + Utilities.padToLeft("----------------",LOCAL_EXCEPTION_COUNT_COLUMN_WIDTH) + " " + Utilities.padToLeft("----------------",CHILD_EXCEPTION_COUNT_COLUMN_WIDTH) );

               for ( TestSet pass1TestSet : pass1TestSets )
               {
                  pass1TestSet.getTestData().printTextReport( false,
                                                              "",
                                                              headingColumnWidth,
                                                              printStream );
                  if ( pass1TestSet.getTestData().getTestState().displayInShortReport() )
                  {
                     testSetNames.add( pass1TestSet.getTestSetName() );
                     headings.add( pass1TestSet.getTestData().getHeading() );
                     errorCounts.add( pass1TestSet.getTestData().getExceptionCountOnBranch() );
                     ranFlags.add( true );
                  }
               }

               printSummary( testSetNames,
                             headings,
                             errorCounts,
                             ranFlags,
                             elapsedTimeString,
                             printStream,
                             true );
            }
         }
      }
      catch ( java.io.IOException ioException )
      {
         Utilities.printStackTrace( ioException );
      }

      reportFile = new java.io.File( resultsDirectory, "Summary.txt" );
      try ( java.io.FileOutputStream fileOutputStream = new java.io.FileOutputStream(reportFile) )
      {
         try ( java.io.BufferedOutputStream bufferedOutputStream = new java.io.BufferedOutputStream(fileOutputStream) )
         {
            try ( java.io.PrintStream printStream = new java.io.PrintStream(bufferedOutputStream) )
            {
               printSummary( testSetNames,
                             headings,
                             errorCounts,
                             ranFlags,
                             elapsedTimeString,
                             printStream,
                             false );
            }
         }
      }
      catch ( java.io.IOException ioException )
      {
         Utilities.printStackTrace( ioException );
      }

      printSummary( testSetNames,
                    headings,
                    errorCounts,
                    ranFlags,
                    elapsedTimeString,
                    System.out,
                    true );

      testSetNames.clear();
      headings.clear();
      errorCounts.clear();
      ranFlags.clear();

      reportFile = new java.io.File( resultsDirectory, "FullReport.txt" );
      try ( java.io.FileOutputStream fileOutputStream = new java.io.FileOutputStream(reportFile) )
      {
         try ( java.io.BufferedOutputStream bufferedOutputStream = new java.io.BufferedOutputStream(fileOutputStream) )
         {
            try ( java.io.PrintStream printStream = new java.io.PrintStream(bufferedOutputStream) )
            {
               printStream.println( Utilities.padToRight("Test",headingColumnWidth) + " " + Utilities.padToLeft("Local Exceptions",LOCAL_EXCEPTION_COUNT_COLUMN_WIDTH) + " " + Utilities.padToLeft("Child Exceptions",CHILD_EXCEPTION_COUNT_COLUMN_WIDTH) );
               printStream.println( Utilities.padToRight("----",headingColumnWidth) + " " + Utilities.padToLeft("----------------",LOCAL_EXCEPTION_COUNT_COLUMN_WIDTH) + " " + Utilities.padToLeft("----------------",CHILD_EXCEPTION_COUNT_COLUMN_WIDTH) );

               for ( TestSet pass1TestSet : pass1TestSets )
               {
                  pass1TestSet.getTestData().printTextReport( true,
                                                              "",
                                                              headingColumnWidth,
                                                              printStream );
                  testSetNames.add( pass1TestSet.getTestSetName() );
                  headings.add( pass1TestSet.getTestData().getHeading() );
                  errorCounts.add( pass1TestSet.getTestData().getExceptionCountOnBranch() );
                  ranFlags.add( pass1TestSet.getTestData().getTestState().ran() );
               }

               printSummary( testSetNames,
                             headings,
                             errorCounts,
                             ranFlags,
                             elapsedTimeString,
                             printStream,
                             true );
            }
         }
      }
      catch ( java.io.IOException ioException )
      {
         Utilities.printStackTrace( ioException );
      }
   }

   final private java.io.File writeXMLFile( String                          suiteName,
                                            int                             pass1ThreadCount,
                                            java.util.List<TestSet>         pass1TestSets,
                                            java.util.List<RequirementData> requirementsList,
                                            java.util.List<TestData>        unsupportiveTests,
                                            int                             testCount,
                                            java.io.File                    resultsDirectory,
                                            Undercamber                     undercamber )
   {
      java.io.File xmlFile;
      int          index;

      xmlFile = new java.io.File( resultsDirectory, "TestReport.xml" );

      try ( java.io.FileOutputStream fileOutputStream = new java.io.FileOutputStream(xmlFile) )
      {
         try ( java.io.BufferedOutputStream bufferedOutputStream = new java.io.BufferedOutputStream(fileOutputStream,262144) )
         {
            try ( java.io.PrintStream printStream = new java.io.PrintStream(bufferedOutputStream) )
            {
               printStream.println( "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" );
               printStream.println();
               printStream.println( "<UndercamberTestReport>" );
               printStream.println( "   <name>" + suiteName + "</name>" );
               printStream.println( "   <pass1ThreadCount>" + pass1ThreadCount + "</pass1ThreadCount>" );

               for ( index=0; index<pass1TestSets.size(); index++ )
               {
                  pass1TestSets.get(index ).writeToXML( undercamber,
                                                        "   ",
                                                        printStream );
               }

               printStream.println( "   <requirements>" );
               for ( RequirementData requirement : requirementsList )
               {
                  requirement.writeXML( printStream,
                                        "   " );
               }
               printStream.println( "   </requirements>" );

               printStream.println( "   <tags>" );
               writeTagsToXML( "      ",
                               printStream,
                               pass1TestSets );
               printStream.println( "   </tags>" );

               printStream.println( "   <unsupportiveTests>" );
               for ( TestData unsupportiveTest : unsupportiveTests )
               {
                  printStream.println( "      <testID>" + unsupportiveTest.getID() + "</testID>" );
               }
               printStream.println( "   </unsupportiveTests>" );

               printStream.println( "</UndercamberTestReport>" );
            }
         }
      }
      catch ( java.io.IOException ioException )
      {
         Utilities.printStackTrace( ioException );
         return null;
      }

      return xmlFile;
   }

   final static void checkXMLSyntax( java.io.File xmlFile,
                                     java.io.File xsdFile )
   {
      //javax.xml.validation.Schema        schema;
      //javax.xml.validation.SchemaFactory factory;
      //javax.xml.validation.Validator     validator;
      //
      //System.out.println( "Checking syntax of " + xmlFile.getPath() + " *****************************************************************************************************" );
      //
      //try
      //{
      //   factory = javax.xml.validation.SchemaFactory.newInstance( javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI );
      //
      //   schema = factory.newSchema( xsdFile );
      //
      //   validator = schema.newValidator();
      //
      //   validator.validate( new javax.xml.transform.stream.StreamSource(xmlFile) );
      //}
      //catch ( Throwable throwable )
      //{
      //   throwable.printStackTrace();
      //}
   }

   final private static java.io.File writeXSDFile( java.io.File resultsDirectory )
   {
      java.io.File xsdFile;
      String       line;

      xsdFile = new java.io.File( resultsDirectory, "TestReport.xsd" );

      try ( java.io.InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/undercamber/TestReport.xsd") )
      {
         try ( java.io.BufferedInputStream bufferedInputStream = new java.io.BufferedInputStream(inputStream,262144) )
         {
            try ( java.io.InputStreamReader inputStreamReader = new java.io.InputStreamReader(bufferedInputStream) )
            {
               try ( java.io.LineNumberReader lineNumberReader = new java.io.LineNumberReader(inputStreamReader) )
               {
                  try ( java.io.FileOutputStream fileOutputStream = new java.io.FileOutputStream(xsdFile) )
                  {
                     try ( java.io.BufferedOutputStream bufferedOutputStream = new java.io.BufferedOutputStream(fileOutputStream,262144) )
                     {
                        try ( java.io.PrintStream printStream = new java.io.PrintStream(bufferedOutputStream) )
                        {
                           line = lineNumberReader.readLine();

                           while ( line != null )
                           {
                              printStream.println( line );

                              line = lineNumberReader.readLine();
                           }
                        }
                     }
                  }
               }
            }
         }
      }
      catch ( java.io.IOException ioException )
      {
         Utilities.printStackTrace( ioException );
         return null;
      }

      return xsdFile;
   }

   final private void writeTagsToXML( String                  margin,
                                      java.io.PrintStream     printStream,
                                      java.util.List<TestSet> configurationTestSets )
      throws java.io.IOException
   {
      java.util.Map<String,java.util.Set<TestData>> tagMap;
      java.util.List<String>                        tagList;

      tagMap = new java.util.HashMap<String,java.util.Set<TestData>>();

      for ( TestSet testSet : configurationTestSets )
      {
         testSet.getTestData().addToTagMap( tagMap );
      }

      tagList = new java.util.ArrayList<String>();
      tagList.addAll( tagMap.keySet() );
      java.util.Collections.sort( tagList );
      for ( String tagName : tagList )
      {
         printStream.println( margin + "<tag name=\"" + Utilities.escapeForXML(tagName) + "\">" );

         writeTagsToXML( tagName,
                         margin + "   ",
                         printStream,
                         tagMap.get(tagName) );

         printStream.println( margin + "</tag>" );
      }
   }

   final private void writeTagsToXML( String                  tagName,
                                      String                  margin,
                                      java.io.PrintStream     printStream,
                                      java.util.Set<TestData> testDataSet )
      throws java.io.IOException
   {
      java.util.List<TestData> testDataList;

      testDataList = new java.util.ArrayList<TestData>();

      testDataList.addAll( testDataSet );

      java.util.Collections.sort( testDataList,
                                  this );

      for ( TestData testData : testDataList )
      {
         printStream.println( margin + "<taggedTest>" );
         printStream.println( margin + "   <testID>" + testData.getID() + "</testID>" );
         printStream.println( margin + "   <includeSubtests>" + testData.getTag(tagName).includeSubtests() + "</includeSubtests>" );
         printStream.println( margin + "</taggedTest>" );
      }
   }

   final private void printSummary( java.util.List<String>  testSetNames,
                                    java.util.List<String>  headings,
                                    java.util.List<Integer> errorCounts,
                                    java.util.List<Boolean> ranFlags,
                                    String                  elapsedTimeString,
                                    java.io.PrintStream     printStream,
                                    boolean                 includeDivider )
   {
      int testSetColumnWidth;
      int headingColumnWidth;
      int errorColumnWidth;
      int index;

      testSetColumnWidth = ( "Test Set" ).length();
      headingColumnWidth = ( "Top Level Test" ).length();
      errorColumnWidth = 6;

      for ( index=0; index<testSetNames.size(); index++ )
      {
         if ( testSetNames.get(index).length() > testSetColumnWidth )
         {
            testSetColumnWidth = testSetNames.get(index).length();
         }
         if ( headings.get(index).length() > headingColumnWidth )
         {
            headingColumnWidth = headings.get(index).length();
         }
         if ( errorCounts.get(index).toString().length() > errorColumnWidth )
         {
            errorColumnWidth = errorCounts.get(index).toString().length();
         }
      }

      if ( includeDivider )
      {
         printStream.println();
         printStream.println( Utilities.padToRight("", testSetColumnWidth,"=") + "=" + Utilities.padToLeft("",headingColumnWidth,"=") + "=" + Utilities.padToLeft("",errorColumnWidth,"=") );
         printStream.println();
      }

      printStream.println( Utilities.padToRight("Test Set",testSetColumnWidth) + " " + Utilities.padToRight("Top Level Test",headingColumnWidth) + " " + Utilities.padToLeft("Errors",errorColumnWidth) );
      printStream.println( Utilities.padToRight("--------",testSetColumnWidth) + " " + Utilities.padToRight("--------------",headingColumnWidth) + " " + Utilities.padToLeft("------",errorColumnWidth) );

      for ( index=0; index<testSetNames.size(); index++ )
      {
         if ( errorCounts.get(index) > 0 )
         {
            printStream.println( Utilities.padToRight(testSetNames.get(index),testSetColumnWidth) + " " + Utilities.padToRight(headings.get(index),headingColumnWidth) + " " + Utilities.padToLeft(errorCounts.get(index).toString(),errorColumnWidth) + "  failed" + (ranFlags.get(index)?"":" (not run)") );
         }
         else
         {
            printStream.println( Utilities.padToRight(testSetNames.get(index),testSetColumnWidth) + " " + Utilities.padToRight(headings.get(index),headingColumnWidth) + " " + Utilities.padToLeft(0,errorColumnWidth) + (ranFlags.get(index)?"":"  (not run)") );
         }
      }

      printStream.println();

      printStream.println( "Elapsed time = " + elapsedTimeString );
   }

   final private void printAnalysis( java.util.List<TestData> unsupportiveTests,
                                     java.io.File             resultsDirectory )
   {
      java.io.File reportFile;

      reportFile = new java.io.File( resultsDirectory, "Analysis.txt" );
      try ( java.io.FileOutputStream fileOutputStream = new java.io.FileOutputStream(reportFile) )
      {
         try ( java.io.BufferedOutputStream bufferedOutputStream = new java.io.BufferedOutputStream(fileOutputStream,262144) )
         {
            try ( java.io.PrintStream printStream = new java.io.PrintStream(bufferedOutputStream) )
            {
               printStream.println( "Tests that do not support any requirements:" );
               printStream.println();
               printStream.println( "         ID  Test" );
               printStream.println( "   --------  ----" );
               for ( TestData testData : unsupportiveTests )
               {
                  printStream.println( "   " + Utilities.padToLeft(testData.getID(),8) + "  " + testData.getHeading() );
               }
            }
         }
      }
      catch ( java.io.IOException ioException )
      {
         Utilities.printStackTrace( ioException );
      }
   }

   final public int compare( TestData testData1,
                             TestData testData2 )
   {
      return testData1.getID() - testData2.getID();
   }
}

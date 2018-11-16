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

package com.mydomain.test;

public class DivisionChecker
{
   DivisionChecker( com.undercamber.TestManager testManager,
                    java.io.File                parentDirectory )
      throws Throwable
   {
      boolean      verify;
      java.io.File localDirectory;
      MyCalculator calculator;

      verify = testManager.initialize( new com.undercamber.Tag("Division") );

      if ( verify )
      {
         localDirectory = new java.io.File( parentDirectory, "DivisionChecker" );
         localDirectory.mkdirs();
         calculator = new MyCalculator( new java.io.File(localDirectory,"CalculatorOutput.txt") );
      }
      else
      {
         calculator = null;
      }

      testManager.addSubtest( tm -> checkDivision(tm,calculator,2,2,1) );
      testManager.addSubtest( tm -> checkDivision(tm,calculator,18,6,3) );
      testManager.addSubtest( tm -> checkDivideByZero(tm,calculator) );
   }

   private void checkDivision( com.undercamber.TestManager testManager,
                               MyCalculator                calculator,
                               double                      numerator,
                               double                      denominator,
                               double                      expectedResult )
      throws Throwable
   {
      boolean verify;
      double  result;

      verify = testManager.initialize( Double.toString(numerator) + "," + denominator + "," + expectedResult );

      if ( verify )
      {
         result = calculator.divide( numerator, denominator );
         if ( result != expectedResult )
         {
            testManager.addException( new Exception("Incorrect result.  Expected = "+expectedResult+".  Found = "+result) );
         }
      }
   }

   private void checkDivideByZero( com.undercamber.TestManager testManager,
                                   MyCalculator                calculator )
      throws Throwable
   {
      boolean verify;

      verify = testManager.initialize();

      if ( verify )
      {
         try
         {
            calculator.divide( 1, 0 );
            testManager.addException( new Exception("Did not get expected divide-by-zero exception") );
         }
         catch ( RuntimeException expected )
         {
         }
      }
   }
}

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

import com.undercamber.*;
import java.io.*;

public class AdditionChecker
{
   AdditionChecker( TestManager  testManager,
                    java.io.File parentDirectory )
      throws Throwable
   {
      boolean      verify;
      java.io.File localDirectory;
      MyCalculator calculator;

      verify = testManager.initialize( new Tag("Addition") );

      if ( verify )
      {
         localDirectory = new File( parentDirectory, "DivisionChecker" );

         localDirectory.mkdirs();

         calculator = new MyCalculator( new java.io.File(localDirectory,"CalculatorOutput.txt") );
      }
      else
      {
         calculator = null;
      }

      testManager.addSubtest( tm -> checkAddition(tm,calculator,2,2,4) );
      testManager.addSubtest( tm -> checkAddition(tm,calculator,3,6,9) );
   }

   private void checkAddition( TestManager  testManager,
                               MyCalculator calculator,
                               double       addend1,
                               double       addend2,
                               double       expectedResult )
      throws Throwable
   {
      boolean verify;
      double  result;

      verify = testManager.initialize( Double.toString(addend1) + "," + addend2 + "," + expectedResult );

      if ( verify )
      {
         result = calculator.add( addend1, addend2 );

         if ( result != expectedResult )
         {
            testManager.addException( new Exception("Incorrect result.  Expected = "+expectedResult+".  Found = "+result) );
         }
      }
   }
}


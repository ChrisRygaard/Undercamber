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

import java.io.*;

public class MyCalculator
{
   java.io.PrintStream _printStream;

   MyCalculator( File outputFile )
      throws IOException
   {
      _printStream = new PrintStream( outputFile );
   }

   public double add( double addend1,
                      double addend2 )
      throws IOException
   {
      double sum;

      sum = addend1 + addend2;

      _printStream.println( Double.toString(addend1) + " + " + addend2 + " = " + sum );

      return sum;
   }

   public double divide( double numerator,
                         double denominator )
   {
      double dividend;

      if ( denominator == 0.0 )
      {
         throw new RuntimeException( "Divide by zero" );
      }

      dividend = numerator / denominator;

      _printStream.println( Double.toString(numerator) + " / " + denominator + " = " + dividend );

      return dividend;
   }

   public void close()
      throws IOException
   {
      _printStream.close();
   }
}

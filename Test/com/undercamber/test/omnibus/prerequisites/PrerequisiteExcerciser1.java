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

package com.undercamber.test.omnibus.prerequisites;

import com.undercamber.*;

final public class PrerequisiteExcerciser1
   implements TestUnit
{
   final public void runTest( TestManager testManager )
      throws Throwable
   {
      boolean verify;

      verify = testManager.initialize( new Tag("All") );

      if ( verify )
      {
         // Put tests here
      }

      testManager.addSubtest( tm -> delay(tm,1000) );
      testManager.addSubtest( tm -> delay(tm,2000) );
      testManager.addSubtest( new PrerequisiteExcerciser11() );
      testManager.addSubtest( tm -> delay(tm,3000) );
   }

   final private void delay( TestManager testManager,
                             long        duration )
      throws Throwable
   {
      boolean verify;
      boolean forceError;

      if ( duration > 1000 )
      {
         verify = testManager.initialize( Long.toString(duration),
                                          TestManager.toArray(new Prerequisite("com.undercamber.test.omnibus.prerequisites.PrerequisiteExcerciser1","delay","1000")) );
         forceError = false;
      }
      else
      {
         verify = testManager.initialize( Long.toString(duration) );
         forceError = true;
      }

      if ( verify )
      {
         try
         {
            Thread.sleep( duration );
         }
         catch ( InterruptedException interruptedException )
         {
            interruptedException.printStackTrace();
         }

         if ( forceError )
         {
            testManager.addException( new Exception("Prerequisite error",
                                                    new NullPointerException("Text")) );
         }
      }
   }
}

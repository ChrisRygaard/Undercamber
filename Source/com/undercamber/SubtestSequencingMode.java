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
 * Indicates how subtests should be sequenced
 */
public enum SubtestSequencingMode
{
   /**
    * Run subtests concurrently.
    */
   CONCURRENT                        ( true,  false, false, true  ),
   /**
    * Run subtests sequentially.  If a subtest fails, do not run
    * the remaining subtests.
    */
   SEQUENTIAL_ABORT_SEQUENCE_ON_ERROR( false, true,  true,  false ),
   /**
    * Run subtests sequentially.  If a subtest fails, continue
    * running the remaining subtests.
    */
   SEQUENTIAL_CONTINUE_ON_ERROR      ( false, true,  false, true  );

   private boolean _isParallel;
   private boolean _isSequential;
   private boolean _abortOnError;
   private boolean _continueOnError;

   private SubtestSequencingMode( boolean isParallel,
                                  boolean isSequential,
                                  boolean abortOnError,
                                  boolean continueOnError )
   {
      _isParallel = isParallel;
      _isSequential = isSequential;
      _abortOnError = abortOnError;
      _continueOnError = continueOnError;
   }

   final boolean isParallel()
   {
      return _isParallel;
   }

   final boolean isSequential()
   {
      return _isSequential;
   }

   final boolean abortOnError()
   {
      return _abortOnError;
   }

   final boolean continueOnError()
   {
      return _continueOnError;
   }
}

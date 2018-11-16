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

class Timer
{
   private long _startTime;

   Timer()
   {
      _startTime = System.currentTimeMillis();
   }

   final private static String getMillisecondsString( long milliseconds )
   {
      if ( milliseconds < 10 )
      {
         return "00" + milliseconds;
      }
      else if ( milliseconds < 100 )
      {
         return "0" + milliseconds;
      }
      else
      {
         return Long.toString( milliseconds );
      }
   }

   final private static String getSecondsString( long seconds )
   {
      if ( seconds < 10 )
      {
         return "0" + seconds;
      }
      else
      {
         return Long.toString( seconds );
      }
   }

   final private static String getMinutesString( long minutes )
   {
      if ( minutes < 10 )
      {
         return "0" + minutes;
      }
      else
      {
         return Long.toString( minutes );
      }
   }

   final String getElapsedTimeString()
   {
      long deltaTime_ms;
      long milliseconds;
      long totalSeconds;
      long seconds;
      long totalMinutes;
      long minutes;
      long hours;

      deltaTime_ms = System.currentTimeMillis() - _startTime;

      milliseconds = deltaTime_ms % 1000;

      totalSeconds = ( deltaTime_ms - milliseconds ) / 1000;

      seconds = totalSeconds % 60;

      totalMinutes = ( totalSeconds - seconds ) / 60;

      minutes = totalMinutes % 60;

      hours = ( totalMinutes - minutes ) / 60;

      return Long.toString(hours) + ":" + getMinutesString(minutes) + ":" + getSecondsString(seconds) + "." + getMillisecondsString(milliseconds);
   }
}

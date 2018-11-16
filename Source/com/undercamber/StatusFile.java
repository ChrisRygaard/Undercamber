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

final class StatusFile
{
   final private static int RECORD_SIZE = Integer.BYTES;

   private java.io.RandomAccessFile _randomAccessFile;

   StatusFile( java.io.File resultsDirectory,
               int          size )
      throws java.io.IOException
   {
      java.io.File file;

      file = new java.io.File( resultsDirectory, "UndercamberWorkingDirectory" );
      file.mkdirs();
      file = new java.io.File( file, "FinalStatus.dat" );

      initializeFile( file,
                      size );

      _randomAccessFile = new java.io.RandomAccessFile( file, "rws" );
   }

   StatusFile( java.io.File resultsDirectory )
      throws java.io.IOException
   {
      java.io.File file;

      file = new java.io.File( resultsDirectory, "UndercamberWorkingDirectory" );
      file = new java.io.File( file, "FinalStatus.dat" );
      _randomAccessFile = new java.io.RandomAccessFile( file, "rws" );
   }

   final private void initializeFile( java.io.File file,
                                      int          size )
      throws java.io.IOException
   {
      byte dataArray[];

      dataArray = new byte[ size * RECORD_SIZE ];

      java.util.Arrays.fill( dataArray, (byte)0 );

      try ( java.io.FileOutputStream fileOutputStream = new java.io.FileOutputStream(file) )
      {
         fileOutputStream.write( dataArray );
      }
   }

   final void set( int       sequenceIndex,
                   TestState state )
      throws java.io.IOException
   {
      synchronized ( TestManager.MONITOR_HOLDER )
      {
         _randomAccessFile.seek( sequenceIndex * RECORD_SIZE );

         _randomAccessFile.writeInt( state.ordinal() );
      }
   }

   final TestState get( int sequenceIndex )
      throws java.io.IOException
   {
      synchronized ( TestManager.MONITOR_HOLDER )
      {
         _randomAccessFile.seek( sequenceIndex * RECORD_SIZE );

         return TestState.values()[ _randomAccessFile.readInt() ];
      }
   }
}

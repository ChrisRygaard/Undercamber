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
 * Used to create pre-defined groups of tests.  <br>
 * <br>
 * Tests can be tagged in the call to {@link TestManager#initialize( SubtestSequencingMode, String, SubtestContinuationMode, Prerequisite[], Tag[], Requirement... )}
 * using this class.  All of the tests with the same tag name
 * are grouped together.<br>
 * <br>
 * When running Undercamber, the groups to be run can be specified by
 * passing thier tag name in the command line, using the
 * <tt>-tag1</tt> or <tt>-tag2</tt> argument.
 */
final public class Tag
{
   final private static int    CLASS_PERSISTENCE_VERSION = 0;
   final private static String CLASS_PERSISTENCE_BRANCH  = "";

   private String  _name;
   private boolean _includeSubtests;

   /**
    * Create a new tag.  <br>
    * <br>
    * This is equivalent to <pre>
    *    Tag( name,
    *         true )
    * </pre>
    *
    * @param name
    *        The tag name.  A tag name cannot be zero length.  A
    *        tag name must start with a letter or an underscore.  A
    *        tag name can contain letters, digits, and underscores.
    *
    * @throws UserError
    *         If the name is not legal.
    */
   public Tag( String name )
      throws UserError
   {
      this( name,
            true );
   }

   /**
    * Create a new tag.
    *
    * @param name
    *        The tag name.  A tag name cannot be zero length.  A
    *        tag name must start with a letter or an underscore.  A
    *        tag name can contain letters, digits, and underscores.
    *
    * @param includeSubtests
    *        <ul>
    *           <li><b>true</b>:  The subtests should be
    *           included in the group</li>
    *           <li><b>false</b>:  The subtests should not be
    *           included in the group</li>
    *        </ul>
    *
    * @throws UserError
    *         If the name is not legal.
    */
   public Tag( String  name,
               boolean includeSubtests )
      throws UserError
   {
      String errorMessage;

      errorMessage = Utilities.isLegalName( name );
      if ( errorMessage != null )
      {
         throw new UserError( "Illegal tag name:  \"" + name + "\":  " + errorMessage );
      }
      _name = name;
      _includeSubtests = includeSubtests;
   }

   Tag( java.io.DataInputStream dataInputStream )
      throws java.io.IOException
   {
      if ( dataInputStream.readInt() > CLASS_PERSISTENCE_VERSION )
      {
         throw new java.io.IOException( "The database is from a newer version of Undercamber" );
      }
      if ( !(dataInputStream.readUTF().equals(CLASS_PERSISTENCE_BRANCH)) )
      {
         throw new java.io.IOException( "The database is from an unrecognized branch of Undercamber" );
      }

      _name = dataInputStream.readUTF();
      _includeSubtests = dataInputStream.readBoolean();
   }

   final String getName()
   {
      return _name;
   }

   final boolean includeSubtests()
   {
      return _includeSubtests;
   }

   final void write( java.io.DataOutputStream dataOutputStream )
      throws java.io.IOException
   {
      dataOutputStream.writeInt( CLASS_PERSISTENCE_VERSION );
      dataOutputStream.writeUTF( CLASS_PERSISTENCE_BRANCH );

      dataOutputStream.writeUTF( _name );
      dataOutputStream.writeBoolean( _includeSubtests );
   }
}

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
 * A convenience class to build path-like strings in a
 * platform-independent way.  <p>
 *
 * Entries have string expansion enabled, as describe in {@link StringExpander}.<p>
 *
 * By default, entries in this list can optionally have simple
 * validation performed.<br>
 */
final public class Path
{
   private java.util.List<String> _entries;

   /**
    * Creates an empty path.
    */
   public Path()
   {
      _entries = new java.util.ArrayList<String>();
   }

   /**
    * Creates a path initialized with the specified entries.
    *
    * @param entries
    *        The initial entries
    *
    * @throws UserError
    *         If one of the entries is not properly formatted for string expansion, or one of the entries does not point to a location on disk.
    *
    * @throws InternalException
    *         If there is internal difficulty expanding the strings.
    */
   public Path( String... entries )
      throws UserError,
             InternalException
   {
      _entries = new java.util.ArrayList<String>();

      addEntries( entries );
   }

   /**
    * Create a copy.  <p>
    *
    * Changes to the original have no effect on the copy, and vice versa.<p>
    *
    * No string expansion is performed.
    *
    * @param original
    *        The original from which to make a copy.
    */
   public Path( Path original )
   {
      _entries = new java.util.ArrayList<String>();

      _entries.addAll( original._entries );
   }

   /**
    * Add entries to the path.  <p>
    *
    * This is equivalent to<pre>
    *    addEntries( ValidationLevel.ERROR,
    *                entries );</pre>
    *
    * @param entries
    *        The entries to append to the path.
    *
    * @throws UserError
    *         If one of the entries is not properly formatted for string expansion, or one of the entries does not point to a location on disk.
    *
    * @throws InternalException
    *         If there is internal difficulty expanding the strings.
    */
   final public void addEntries( String... entries )
      throws UserError,
             InternalException
   {
      addEntries( ValidationLevel.ERROR,
                  entries );
   }

   /**
    * Add entries to the path.  <p>
    *
    * @param validationLevel
    *        The validation level to use when validating the entries.
    *
    * @param entries
    *        The entries to append to the path.
    *
    * @throws UserError
    *         If one of the entries is not properly formatted for string expansion, or one of the entries does not point to a location on disk.
    *
    * @throws InternalException
    *         If there is internal difficulty expanding the strings.
    */
   final public void addEntries( ValidationLevel validationLevel,
                                 String...       entries )
      throws UserError,
             InternalException
   {
      java.io.File file;

      for ( String entry : entries )
      {
         entry = StringExpander.expandString( entry,
                                              validationLevel );

         if ( validationLevel.needsCheck() )
         {
            file = new java.io.File( entry );
            if ( !(file.exists()) )
            {
               if ( validationLevel.throwException() )
               {
                  throw new UserError( "File " + entry + " not found" );
               }
               else
               {
                  System.out.println( "File " + entry + " not found" );
               }
            }
         }

         _entries.add( entry );
      }
   }

   /**
    * Add entries to the path.  <p>
    *
    * This is equivalent to<pre>
    *    addEntries( entries,
    *                ValidationLevel.ERROR );</pre>
    *
    * @param entries
    *        The entries to append to the path.
    *
    * @throws UserError
    *         If one of the entries is not properly formatted for string expansion, or one of the entries does not point to a location on disk.
    *
    * @throws InternalException
    *         If there is internal difficulty expanding the strings.
    */
   final public void addEntries( java.util.List<String> entries )
      throws UserError,
             InternalException
   {
      addEntries( entries,
                  ValidationLevel.ERROR );
   }

   /**
    * Add entries to the path.  <p>
    *
    * @param validationLevel
    *        The validation level to use when validating the entries.
    *
    * @param entries
    *        The entries to append to the path.
    *
    * @throws UserError
    *         If one of the entries is not properly formatted for string expansion, or one of the entries does not point to a location on disk.
    *
    * @throws InternalException
    *         If there is internal difficulty expanding the strings.
    */
   final public void addEntries( java.util.List<String> entries,
                                 ValidationLevel        validationLevel )
      throws UserError,
             InternalException
   {
      java.io.File file;

      for ( String entry : entries )
      {
         entry = StringExpander.expandString( entry,
                                              validationLevel );

         if ( validationLevel.needsCheck() )
         {
            file = new java.io.File( entry );
            if ( !(file.exists()) )
            {
               if ( validationLevel.throwException() )
               {
                  throw new UserError( "File " + entry + " not found" );
               }
               else
               {
                  System.out.println( "File " + entry + " not found" );
               }
            }
         }

         _entries.add( entry );
      }
   }

   /**
    * Remove all entries.
    */
   final public void clear()
   {
      _entries.clear();
   }

   /**
    * List the entries.  <p>
    *
    * This returns a copy of the internal list.
    *
    * @return The entries.
    */
   final public java.util.List<String> getEntries()
   {
      java.util.List<String> entries;

      entries = new java.util.ArrayList<String>();

      entries.addAll( _entries );

      return entries;
   }

   /**
    * Remove an entry
    *
    * @param index
    *        The index of the entry to remove
    */
   final public void remove( int index )
   {
      _entries.remove( index );
   }

   /**
    * Convert this path to a platform-specific path.
    *
    * @return The path.
    */
   final public String toString()
   {
      StringBuffer stringBuffer;
      int          index;

      if ( _entries.size() == 0 )
      {
         return "";
      }
      else
      {
         stringBuffer = new StringBuffer();

         stringBuffer.append( _entries.get(0) );

         for ( index=1; index<_entries.size(); index++ )
         {
            stringBuffer.append( java.io.File.pathSeparator ).append( _entries.get(index) );
         }

         return stringBuffer.toString();
      }
   }
}

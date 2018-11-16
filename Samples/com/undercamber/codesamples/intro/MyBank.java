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

package com.undercamber.codesamples.intro;

public class MyBank
{
   private java.util.Map<String,Integer> _accounts;

   public MyBank()
   {
      _accounts = new java.util.HashMap<String,Integer>();
   }

   public void createAccount( String name )
   {
      if ( _accounts.get(name) != null )
      {
         throw new IllegalArgumentException( "Account \"" + name + "\" already exists" );
      }

      _accounts.put( name, 0 );
   }

   public int deposit( String name,
                       int    amount )
   {
      Integer currentBalance;

      currentBalance = _accounts.get( name );

      if ( currentBalance == null )
      {
         throw new IllegalArgumentException( "Account \"" + name + "\" not found" );
      }

      if ( (currentBalance+amount) < 0 )
      {
         throw new IllegalArgumentException( "Overdraft" );
      }

      _accounts.put( name, (currentBalance+amount) );

      return currentBalance + amount;
   }
}

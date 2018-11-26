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

enum ExecutionMode
{
   //                isDiscovery, verify useConfigurationTestDataRunFlag checkPrerequisites displayProgress recordStatus canAcceptPrerequisites useAlternateRunFlag
   PASS_1_DISCOVERY( true,        false, false,                          false,             false,          false,       true,                  false ),
   PASS_2_EXECUTION( false,       true,  true,                           true,              true,           true,        false,                 true  );

   private boolean _isDiscovery;
   private boolean _verify;
   private boolean _useConfigurationTestDataRunFlag;
   private boolean _checkPrerequisites;
   private boolean _displayProgress;
   private boolean _recordStatus;
   private boolean _canAcceptPrerequisites;
   private boolean _useAlternateRunFlag;

   ExecutionMode( boolean isDiscovery,
                  boolean verify,
                  boolean useConfigurationTestDataRunFlag,
                  boolean checkPrerequisites,
                  boolean displayProgress,
                  boolean recordStatus,
                  boolean canAcceptPrerequisites,
                  boolean useAlternateRunFlag )
   {
      _isDiscovery = isDiscovery;
      _verify = verify;
      _useConfigurationTestDataRunFlag = useConfigurationTestDataRunFlag;
      _checkPrerequisites = checkPrerequisites;
      _displayProgress = displayProgress;
      _recordStatus = recordStatus;
      _canAcceptPrerequisites = canAcceptPrerequisites;
      _useAlternateRunFlag = useAlternateRunFlag;
   }

   final boolean isDiscovery()
   {
      return _isDiscovery;
   }

   final boolean verify()
   {
      return _verify;
   }

   final boolean useConfigurationTestDataRunFlag()
   {
      return _useConfigurationTestDataRunFlag;
   }

   final boolean checkPrerequisites()
   {
      return _checkPrerequisites;
   }

   final boolean displayProgress()
   {
      return _displayProgress;
   }

   final boolean recordStatus()
   {
      return _recordStatus;
   }

   final boolean canAcceptPrerequisites()
   {
      return _canAcceptPrerequisites;
   }

   final boolean useAlternateRunFlag()
   {
      return _useAlternateRunFlag;
   }
}

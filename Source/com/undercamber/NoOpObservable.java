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

final class NoOpObservable<ObservedClass>
   implements javafx.beans.value.ObservableValue<ObservedClass>
{
   private ObservedClass                                                            _observedObject;
   private java.util.List<javafx.beans.value.ChangeListener<? super ObservedClass>> _changeListeners;
   private java.util.List<javafx.beans.InvalidationListener>                        _invalidationListeners;

   NoOpObservable( ObservedClass observedObject )
   {
      _observedObject = observedObject;

      _changeListeners = new java.util.ArrayList<javafx.beans.value.ChangeListener<? super ObservedClass>>();
      _invalidationListeners = new java.util.ArrayList<javafx.beans.InvalidationListener>();
   }

   final public ObservedClass getValue()
   {
      return _observedObject;
   }

   final public void addListener( javafx.beans.value.ChangeListener<? super ObservedClass> listener )
   {
      _changeListeners.add( listener );
   }

   final public void removeListener( javafx.beans.value.ChangeListener<? super ObservedClass> listener )
   {
      _changeListeners.remove( listener );
   }

   final public void addListener( javafx.beans.InvalidationListener listener )
   {
      _invalidationListeners.add( listener );
   }

   final public void removeListener( javafx.beans.InvalidationListener listener )
   {
      _invalidationListeners.remove( listener );
   }
}

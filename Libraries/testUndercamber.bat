@rem Copyright 2018 Rygaard Technologies, LLC
@rem
@rem Redistribution and use in source and binary forms, with or without modification, are permitted
@rem provided that the following conditions are met:
@rem
@rem 1. Redistributions of source code must retain the above copyright notice, this list of
@rem    conditions and the following disclaimer.
@rem
@rem 2. Redistributions in binary form must reproduce the above copyright notice, this list of
@rem    conditions and the following disclaimer in the documentation and/or other materials provided
@rem    with the distribution.
@rem
@rem 3. Neither the name of the copyright holder nor the names of its contributors may be used to
@rem    endorse or promote products derived from this software without specific prior written
@rem    permission.
@rem
@rem THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
@rem IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
@rem FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
@rem CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
@rem CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
@rem SERVICES; LOSS OF USE,  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
@rem THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
@rem OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
@rem POSSIBILITY OF SUCH DAMAGE.

echo off

call ant -noclasspath -Dbasedir=%UNDERCAMBER_PROJECT_ROOT% -buildfile "%UNDERCAMBER_PROJECT_ROOT%\build.xml" All

"%JAVA_HOME%\bin\java" -cp %UNDERCAMBER_PROJECT_ROOT%\Source;%UNDERCAMBER_PROJECT_ROOT%\Test com.undercamber.Undercamber -config com.undercamber.test.omnibus.ConfigurationCallback %1 %2 %3 %4 %5 %6 %7 %8 %9

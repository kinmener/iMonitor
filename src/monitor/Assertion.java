/* Copyright 2007 Theodore S. Norvell. All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

   1. Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
   2. Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
   3. Neither the source nor binary form shall be included in any product used by
      or intended for use by any military organization. 

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THEODORE
NORVELL BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package monitor;

import java.lang.AssertionError ;

/** Assertions that may be checked from time to time.
 * 
 * @author Theodore S. Norvell
 * @version 1.0 */
public abstract class Assertion {

    private static final String defaultMessage = "Assertion Failure" ;
    
    protected String message = defaultMessage ;

    /** This method says whether the assertion is true. */
    public abstract boolean isTrue() ;

    public abstract String getGlobalState() ; 

    /** Throw an AssertionError if the assertion is not true. */
    public void check() {
        check( isTrue(), message ) ; }

    /** Throw an AssertionError if the parameter is not true. */
    public static void check(boolean b ) {
        check( b, defaultMessage ) ; }

    /** Throw an AssertionError if the boolean parameter is not true. */
    public static void check(boolean b, String message) {
        if( ! b )
        	throw new AssertionError(message) ; }
}
/*
 * ========THE SOLMIX PROJECT=====================================
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */
package com.solmix.eventservice.deliver;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 
 * @author solomon
 * @version 110035 2011-10-2
 */

public class Rendezvous extends CyclicBarrier {
	/** Flag for timeout handling. */
	private volatile boolean timedout = false;

	/**
	 * Create a Barrier for the indicated number of parties, and the default Rotator function to run at each barrier
	 * point.
	 */
	public Rendezvous() {
		super(2);
	}

	/**
	 * see {@link CyclicBarrier#barrier()}
	 */
	public void waitForRendezvous() {
		if(timedout) {
			// if we have timed out, we return immediately
			return;
		}
		try {
			this.await();
		} catch(BrokenBarrierException ignore1) {
		} catch(InterruptedException ignore2) {
		}
	}

	/**
	 * see {@link CyclicBarrier#attemptBarrier(long)}
	 */
	public void waitAttemptForRendezvous(final long timeout) throws TimeoutException {
		try {
			this.await(timeout, TimeUnit.MILLISECONDS);
			// rest timeout.
			this.reset();
		} catch(BrokenBarrierException ignore1) {
		} catch(TimeoutException te) {
			timedout = true;
			throw te;
		} catch(InterruptedException ignore2) {
		}
	}
}
